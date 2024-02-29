/*
 * Copyright (C) 2009-2016 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bytedeco.javacv;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class JavaCV {

    public static final double
            SQRT2 = 1.41421356237309504880,
            FLT_EPSILON = 1.19209290e-7F,
            DBL_EPSILON = 2.2204460492503131e-16;

    /** returns the distance^2 between the line (x1, y1) (x2, y2) and the point (x3, y3) */
    public static double distanceToLine(double x1, double y1, double x2, double y2, double x3, double y3) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double d2 = dx*dx + dy*dy;
        double u = ((x3 - x1)*dx + (y3 - y1)*dy) / d2;

        double x = x1 + u*dx;
        double y = y1 + u*dy;

        dx = x - x3;
        dy = y - y3;
        return dx*dx + dy*dy;
    }

    /**
     * returns the largest rectangle of given aspect ratio and angle,
     * bounded by the contour and sharing the same centroid
     */
    private static ThreadLocal<CvMoments> moments = CvMoments.createThreadLocal();
    public static CvBox2D boundedRect(CvMat contour, CvBox2D box) {
        int contourLength = contour.length();
        CvMoments m = moments.get();
        cvMoments(contour, m, 0);
        double inv_m00 = 1 / m.m00();
        double centerX = m.m10() * inv_m00;
        double centerY = m.m01() * inv_m00;

        float[] pts = new float[8];
        CvPoint2D32f center = box.center();
        CvSize2D32f size = box.size();
        center.put(centerX, centerY);
        cvBoxPoints(box, pts);

        float scale = Float.POSITIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            double x1 = centerX,  y1 = centerY,
                   x2 = pts[2*i], y2 = pts[2*i + 1];
            for (int j = 0; j < contourLength; j++) {
                int k = (j + 1) % contourLength;
                double x3 = contour.get(2*j), y3 = contour.get(2*j + 1),
                       x4 = contour.get(2*k), y4 = contour.get(2*k + 1);
                double d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
                double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/d,
                       ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/d;
                if (ub >= 0 && ub <= 1 && ua >= 0 && ua < scale) {
                    scale = (float)ua;
                }
            }
        }
        size.width(scale*size.width()).height(scale*size.height());
        return box;
    }

    /**
     * Similar to cvBoundingRect(), but can also pad the output with some extra
     * pixels, useful to use as ROI for operations with interpolation. Further
     * aligns the region to specified boundaries, for easier vectorization and
     * subsampling, and also uses on input the rect argument as a maximum boundary.
     */
    public static CvRect boundingRect(double[] contour, CvRect rect,
            int padX, int padY, int alignX, int alignY) {
        double minX = contour[0];
        double minY = contour[1];
        double maxX = contour[0];
        double maxY = contour[1];
        for (int i = 1; i < contour.length/2; i++) {
            double x = contour[2*i  ];
            double y = contour[2*i+1];
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        int x = (int)Math.floor(Math.max(rect.x(), minX-padX)/alignX)*alignX;
        int y = (int)Math.floor(Math.max(rect.y(), minY-padY)/alignY)*alignY;
        int width  = (int)Math.ceil(Math.min(rect.width(),  maxX+padX)/alignX)*alignX - x;
        int height = (int)Math.ceil(Math.min(rect.height(), maxY+padY)/alignY)*alignY - y;

        return rect.x(x).y(y).width(Math.max(0, width)).height(Math.max(0, height));
    }

    private static ThreadLocal<CvMat>
            A8x8 = CvMat.createThreadLocal(8, 8),
            b8x1 = CvMat.createThreadLocal(8, 1),
            x8x1 = CvMat.createThreadLocal(8, 1);
    /**
     * this is basically cvGetPerspectiveTransform() using CV_LU instead of
     * CV_SVD, because the latter gives inaccurate results...
     * Consider using {@link opencv_imgproc#getPerspectiveTransform} instead.
     */
    public static CvMat getPerspectiveTransform(double[] src, double[] dst, CvMat map_matrix) {
        // creating and releasing matrices via NIO here in this function
        // can easily become a bottleneck, so we use ThreadLocal references
        CvMat A = A8x8.get();
        CvMat b = b8x1.get();
        CvMat x = x8x1.get();

        for(int i = 0; i < 4; ++i ) {
            A.put(i*8+0, src[i*2]);   A.put((i+4)*8+3, src[i*2]);
            A.put(i*8+1, src[i*2+1]); A.put((i+4)*8+4, src[i*2+1]);
            A.put(i*8+2, 1);          A.put((i+4)*8+5, 1);
            A.put(i*8+3, 0);          A.put(i*8+4, 0); A.put(i*8+5, 0);
            A.put((i+4)*8+0, 0);  A.put((i+4)*8+1, 0); A.put((i+4)*8+2, 0);

            A.put(i*8+6,     -src[i*2]  *dst[i*2]);
            A.put(i*8+7,     -src[i*2+1]*dst[i*2]);
            A.put((i+4)*8+6, -src[i*2]  *dst[i*2+1]);
            A.put((i+4)*8+7, -src[i*2+1]*dst[i*2+1]);

            b.put(i,   dst[i*2]);
            b.put(i+4, dst[i*2+1]);
        }
        cvSolve(A, b, x, CV_LU);
        map_matrix.put(x.get());
        map_matrix.put(8, 1);

        return map_matrix;
    }

    /** Consider using {@link opencv_core#perspectiveTransform} instead. */
    public static void perspectiveTransform(double[] src, double[] dst, CvMat map_matrix) {
        double[] mat = map_matrix.get();
        for (int j = 0; j < src.length; j += 2) {
            double x = src[j], y = src[j + 1];
            double w = x*mat[6] + y*mat[7] + mat[8];

            if (Math.abs(w) > FLT_EPSILON) {
                w = 1.0/w;
                dst[j] = (x*mat[0] + y*mat[1] + mat[2])*w;
                dst[j+1] = (x*mat[3] + y*mat[4] + mat[5])*w;
            } else {
                dst[j] = dst[j+1] = 0;
            }
        }
    }

    private static ThreadLocal<CvMat>
            A3x3 = CvMat.createThreadLocal(3, 3), b3x1 = CvMat.createThreadLocal(3, 1);
    public static CvMat getPlaneParameters(double[] src, double[] dst,
            CvMat invSrcK, CvMat dstK, CvMat R, CvMat t, CvMat n) {
        CvMat A = A3x3.get(), b = b3x1.get();

        double[] x = new double[6], y = new double[6];
        perspectiveTransform(src, x, invSrcK);
        cvInvert(dstK, A);
        perspectiveTransform(dst, y, A);

        for (int i = 0; i < 3; i++) {
            A.put(i, 0, (t.get(2)*y[i*2] - t.get(0))*x[i*2  ]);
            A.put(i, 1, (t.get(2)*y[i*2] - t.get(0))*x[i*2+1]);
            A.put(i, 2,  t.get(2)*y[i*2] - t.get(0));

            b.put(i, (R.get(2, 0)*x[i*2] + R.get(2, 1)*x[i*2+1] + R.get(2, 2))*y[i*2] -
                     (R.get(0, 0)*x[i*2] + R.get(0, 1)*x[i*2+1] + R.get(0, 2)));
        }
        cvSolve(A, b, n, CV_LU);

        return n;
    }

    private static ThreadLocal<CvMat>
            n3x1 = CvMat.createThreadLocal(3, 1);
    public static CvMat getPerspectiveTransform(double[] src, double[] dst, 
            CvMat invSrcK, CvMat dstK, CvMat R, CvMat t, CvMat H) {
        CvMat n = n3x1.get();
        getPlaneParameters(src, dst, invSrcK, dstK, R, t, n);

        // H = R - t*n^T
        cvGEMM(t, n, -1,  R, 1,  H, CV_GEMM_B_T);
        // H = dstK * H * srcK^-1
        cvMatMul(dstK, H, H);
        cvMatMul(H, invSrcK, H);

        return H;
    }

    private static ThreadLocal<CvMat>
            H3x3 = CvMat.createThreadLocal(3, 3);
    public static void perspectiveTransform(double[] src, double[] dst,
            CvMat invSrcK, CvMat dstK, CvMat R, CvMat t, CvMat n, boolean invert) {
        CvMat H = H3x3.get();

        // H = R - t*n^T
        cvGEMM(t, n, -1,  R, 1,  H, CV_GEMM_B_T);

        // H = dstK * H * srcK^-1
        cvMatMul(dstK, H, H);
        cvMatMul(H, invSrcK, H);
        if (invert) {
            cvInvert(H, H);
        }
        perspectiveTransform(src, dst, H);
    }

    private static ThreadLocal<CvMat>
            M3x2 = CvMat.createThreadLocal(3, 2), S2x2 = CvMat.createThreadLocal(2, 2),
            U3x2 = CvMat.createThreadLocal(3, 2), V2x2 = CvMat.createThreadLocal(2, 2);
    /**
     * Algorithms for Plane-Based Pose Estimation, Peter Sturm
     * This assumes plane parameters n == z axis.
     */
    public static void HtoRt(CvMat H, CvMat R, CvMat t) {
        CvMat M = M3x2.get(), S = S2x2.get(),
              U = U3x2.get(), V = V2x2.get();
        M.put(H.get(0), H.get(1),
              H.get(3), H.get(4),
              H.get(6), H.get(7));
        cvSVD(M, S, U, V, CV_SVD_V_T);

        double lambda = S.get(3);
        t.put(H.get(2)/lambda, H.get(5)/lambda, H.get(8)/lambda);

        cvMatMul(U, V, M);
        R.put(M.get(0), M.get(1), M.get(2)*M.get(5) - M.get(3)*M.get(4),
              M.get(2), M.get(3), M.get(1)*M.get(4) - M.get(0)*M.get(5),
              M.get(4), M.get(5), M.get(0)*M.get(3) - M.get(1)*M.get(2));
    }

    private static ThreadLocal<CvMat>
            R13x3 = CvMat.createThreadLocal(3, 3), R23x3 = CvMat.createThreadLocal(3, 3),
            t13x1 = CvMat.createThreadLocal(3, 1), t23x1 = CvMat.createThreadLocal(3, 1),
            n13x1 = CvMat.createThreadLocal(3, 1), n23x1 = CvMat.createThreadLocal(3, 1),
            H13x3 = CvMat.createThreadLocal(3, 3), H23x3 = CvMat.createThreadLocal(3, 3);
    public static double HnToRt(CvMat H, CvMat n, CvMat R, CvMat t) {
        CvMat S = S3x3.get(), U = U3x3.get(), V = V3x3.get();
        cvSVD(H, S, U, V, 0);

        CvMat R1 = R13x3.get(),  R2 = R23x3.get(),
              t1 = t13x1.get(),  t2 = t23x1.get(),
              n1 = n13x1.get(),  n2 = n23x1.get(),
              H1 = H13x3.get(),  H2 = H23x3.get();
        double zeta = homogToRt(S, U, V, R1, t1, n1, R2, t2, n2);

        // H = (R^-1 * H)/s2
        cvGEMM(R1, H, 1/S.get(4),  null, 0,  H1, CV_GEMM_A_T);
        cvGEMM(R2, H, 1/S.get(4),  null, 0,  H2, CV_GEMM_A_T);

        // H = H - I
        H1.put(0, H1.get(0)-1); H1.put(4, H1.get(4)-1); H1.put(8, H1.get(8)-1);
        H2.put(0, H2.get(0)-1); H2.put(4, H2.get(4)-1); H2.put(8, H2.get(8)-1);

        // Now H should ~= -tn^T, so extract "average" t
        double d   =    Math.abs   (n.get(0)) + Math.abs   (n.get(1)) + Math.abs   (n.get(2));
        double s[] = { -Math.signum(n.get(0)), -Math.signum(n.get(1)), -Math.signum(n.get(2)) };
        t1.put(0.0, 0.0, 0.0);
        t2.put(0.0, 0.0, 0.0);
        for (int i = 0; i < 3; i++) {
            t1.put(0, t1.get(0) + s[i]*H1.get(i)  /d);
            t1.put(1, t1.get(1) + s[i]*H1.get(i+3)/d);
            t1.put(2, t1.get(2) + s[i]*H1.get(i+6)/d);

            t2.put(0, t2.get(0) + s[i]*H2.get(i)  /d);
            t2.put(1, t2.get(1) + s[i]*H2.get(i+3)/d);
            t2.put(2, t2.get(2) + s[i]*H2.get(i+6)/d);
        }

        // H = H + tn^T
        cvGEMM(t1, n, 1,  H1, 1,  H1, CV_GEMM_B_T);
        cvGEMM(t2, n, 1,  H2, 1,  H2, CV_GEMM_B_T);

        // take what's left as the error of the model,
        // this either indicates inaccurate camera matrix K or normal vector n
        double err1 = cvNorm(H1);
        double err2 = cvNorm(H2);

        double err;
        if (err1 < err2) {
            if (R != null) {
                R.put(R1);
            }
            if (t != null) {
                t.put(t1);
            }
            err = err1;
        } else {
            if (R != null) {
                R.put(R2);
            }
            if (t != null) {
                t.put(t2);
            }
            err = err2;
        }

        return err;
    }

    private static ThreadLocal<CvMat>
            S3x3 = CvMat.createThreadLocal(3, 3),
            U3x3 = CvMat.createThreadLocal(3, 3),
            V3x3 = CvMat.createThreadLocal(3, 3);
    /**
     * Ported to Java/OpenCV from
     * Bill Triggs. Autocalibration from Planar Scenes. In 5th European Conference
     * on Computer Vision (ECCV ’98), volume I, pages 89–105. Springer-Verlag, 1998.
     */
    public static double homogToRt(CvMat H,
            CvMat R1, CvMat t1, CvMat n1,
            CvMat R2, CvMat t2, CvMat n2) {
        CvMat S = S3x3.get(), U = U3x3.get(), V = V3x3.get();
        cvSVD(H, S, U, V, 0);
        double zeta = homogToRt(S, U, V, R1, t1, n1, R2, t2, n2);
        return zeta;
    }
    public static double homogToRt(CvMat S, CvMat U, CvMat V,
            CvMat R1, CvMat t1, CvMat n1,
            CvMat R2, CvMat t2, CvMat n2) {
        double s1 = S.get(0)/S.get(4);
        double s3 = S.get(8)/S.get(4);
        double zeta = s1-s3;
        double a1 = Math.sqrt(1 - s3*s3);
        double b1 = Math.sqrt(s1*s1 - 1);
        double[] ab = unitize(a1, b1);
        double[] cd = unitize(1+s1*s3, a1*b1);
        double[] ef = unitize(-ab[1]/s1, -ab[0]/s3);

        R1.put(cd[0],0,cd[1], 0,1,0, -cd[1],0,cd[0]);
        cvGEMM(U , R1, 1,  null, 0,  R1, 0);
        cvGEMM(R1, V,  1,  null, 0,  R1, CV_GEMM_B_T);

        R2.put(cd[0],0,-cd[1], 0,1,0, cd[1],0,cd[0]);
        cvGEMM(U , R2, 1,  null, 0,  R2, 0);
        cvGEMM(R2, V,  1,  null, 0,  R2, CV_GEMM_B_T);

        double[] v1 = { V.get(0), V.get(3), V.get(6) };
        double[] v3 = { V.get(2), V.get(5), V.get(8) };
        double sign1 = 1, sign2 = 1;
        for (int i = 2; i >= 0; i--) {
            n1.put(i, sign1*(ab[1]*v1[i] - ab[0]*v3[i]));
            n2.put(i, sign2*(ab[1]*v1[i] + ab[0]*v3[i]));
            t1.put(i, sign1*(ef[0]*v1[i] + ef[1]*v3[i]));
            t2.put(i, sign2*(ef[0]*v1[i] - ef[1]*v3[i]));
            if (i == 2) {
                if (n1.get(2) < 0) {
                    n1.put(2, -n1.get(2));
                    t1.put(2, -t1.get(2));
                    sign1 = -1;
                }
                if (n2.get(2) < 0) {
                    n2.put(2, -n2.get(2));
                    t2.put(2, -t2.get(2));
                    sign2 = -1;
                }
            }
        }

        return zeta;
    }

    public static double[] unitize(double a, double b) {
        double norm = Math.sqrt(a*a + b*b);
        if (norm > FLT_EPSILON) {
            a = a / norm;
            b = b / norm;
        }
        return new double[] { a, b };
    }

    /** more sophisticated than cvAdaptiveThreshold() */
    public static void adaptiveThreshold(IplImage srcImage, final IplImage sumImage,
            final IplImage sqSumImage, final IplImage dstImage, final boolean invert,
            final int windowMax, final int windowMin, final double varMultiplier, final double k) {
        final int w = srcImage.width();
        final int h = srcImage.height();
        final int srcChannels = srcImage.nChannels();
        final int srcDepth = srcImage.depth();
        final int dstDepth = dstImage.depth();

        if (srcChannels > 1 && dstDepth == IPL_DEPTH_8U) {
            cvCvtColor(srcImage, dstImage, srcChannels == 4 ? CV_RGBA2GRAY : CV_BGR2GRAY);
            srcImage = dstImage;
        }

        final ByteBuffer srcBuf = srcImage.getByteBuffer();
        final ByteBuffer dstBuf = dstImage.getByteBuffer();
        final DoubleBuffer sumBuf = sumImage.getDoubleBuffer();
        final DoubleBuffer sqSumBuf = sqSumImage.getDoubleBuffer();
        final int srcStep = srcImage.widthStep();
        final int dstStep = dstImage.widthStep();
        final int sumStep = sumImage.widthStep();
        final int sqSumStep = sqSumImage.widthStep();

        // compute integral images
        cvIntegral(srcImage, sumImage, sqSumImage, null);

        // try to detect a reasonable maximum and minimum intensity
        // for thresholds instead of simply 0 and 255...
        double totalMean = sumBuf.get((h-1)*sumStep/8 + (w-1)) -
                           sumBuf.get((h-1)*sumStep/8) -
                           sumBuf.get(w-1) + sumBuf.get(0);
        totalMean /= w*h;
        double totalSqMean = sqSumBuf.get((h-1)*sqSumStep/8 + (w-1)) -
                             sqSumBuf.get((h-1)*sqSumStep/8) -
                             sqSumBuf.get(w-1) + sqSumBuf.get(0);
        totalSqMean /= w*h;
        double totalVar = totalSqMean - totalMean*totalMean;
//double totalDev = Math.sqrt(totalVar);
//System.out.println(totalDev);
        final double targetVar = totalVar*varMultiplier;

        //for (int y = 0; y < h; y++) {
        Parallel.loop(0, h, new Parallel.Looper() {
        public void loop(int from, int to, int looperID) {
            for (int y = from; y < to; y++) {
                for (int x = 0; x < w; x++) {
                    double var = 0, mean = 0, sqMean = 0;
                    int upperLimit = windowMax;
                    int lowerLimit = windowMin;
                    int window = upperLimit; // start with windowMax
                    while (upperLimit - lowerLimit > 2) {
                        int x1 = Math.max(x-window/2, 0);
                        int x2 = Math.min(x+window/2+1, w);

                        int y1 = Math.max(y-window/2, 0);
                        int y2 = Math.min(y+window/2+1, h);

                        mean = sumBuf.get(y2*sumStep/8 + x2) -
                               sumBuf.get(y2*sumStep/8 + x1) -
                               sumBuf.get(y1*sumStep/8 + x2) +
                               sumBuf.get(y1*sumStep/8 + x1);
                        mean /= window*window;
                        sqMean = sqSumBuf.get(y2*sqSumStep/8 + x2) -
                                           sqSumBuf.get(y2*sqSumStep/8 + x1) -
                                           sqSumBuf.get(y1*sqSumStep/8 + x2) +
                                           sqSumBuf.get(y1*sqSumStep/8 + x1);
                        sqMean /= window*window;
                        var = sqMean - mean*mean;

                        // if we're at maximum window size, but variance is
                        // too low anyway, let's break out immediately
                        if (window == upperLimit && var < targetVar) {
                            break;
                        }

                        // otherwise, start binary search
                        if (var > targetVar) {
                            upperLimit = window;
                        } else {
                            lowerLimit = window;
                        }

                        window = lowerLimit   + (upperLimit-lowerLimit)/2;
                        window = (window/2)*2 + 1;
                    }

                    double value = 0;
                    if (srcDepth == IPL_DEPTH_8U) {
                        value = srcBuf.get(y*srcStep       +   x) & 0xFF;
                    } else if (srcDepth == IPL_DEPTH_32F) {
                        value = srcBuf.getFloat(y*srcStep  + 4*x);
                    } else if (srcDepth == IPL_DEPTH_64F) {
                        value = srcBuf.getDouble(y*srcStep + 8*x);
                    } else {
                        // cvIntegral() does not support other image types,
                        // so we should not be able to get here.
                        assert false;
                    }
                    if (invert) {
                        //double threshold = 255 - (255 - mean) * (1 + 0.1*(Math.sqrt(var)/128 - 1));
                        double threshold = 255 - (255 - mean) * k;
                        dstBuf.put(y*dstStep + x, (value < threshold ? (byte)0xFF : (byte)0x00));
                    } else {
                        //double threshold = mean * (1 + k*(Math.sqrt(var)/128 - 1));
                        double threshold = mean * k;
                        dstBuf.put(y*dstStep + x, (value > threshold ? (byte)0xFF : (byte)0x00));
                    }
                }
            }
        }});
    }

    /** similar to hysteresis thresholding as used by the Canny edge detector */
    public static void hysteresisThreshold(IplImage srcImage, IplImage dstImage,
            double highThresh, double lowThresh, double maxValue) {
        int highThreshold = (int)Math.round(highThresh);
        int lowThreshold  = (int)Math.round(lowThresh);
        byte lowValue  = 0;
        byte medValue  = (byte)Math.round(maxValue/2);
        byte highValue = (byte)Math.round(maxValue);

        int height = srcImage.height();
        int width  = srcImage.width();

        ByteBuffer srcData = srcImage.getByteBuffer();
        ByteBuffer dstData = dstImage.getByteBuffer();
        int srcStep = srcImage.widthStep();
        int dstStep = dstImage.widthStep();
        int srcIndex = 0;
        int dstIndex = 0;

        //
        // first pass forward
        //

        // first line
        int i = 0;
        int in = srcData.get(srcIndex+i)&0xFF;
        if (in >= highThreshold) {
            dstData.put(dstIndex+i, highValue);
        } else if (in < lowThreshold) {
            dstData.put(dstIndex+i, lowValue);
        } else {
            dstData.put(dstIndex+i, medValue);
        }

        for (i = 1; i < width-1; i++) {
            in = srcData.get(srcIndex+i)&0xFF;
            if (in >= highThreshold) {
                dstData.put(dstIndex+i, highValue);
            } else if (in < lowThreshold) {
                dstData.put(dstIndex+i, lowValue);
            } else {
                byte prev = dstData.get(dstIndex+i-1);
                if (prev == highValue) {
                    dstData.put(dstIndex+i, highValue);
                } else {
                    dstData.put(dstIndex+i, medValue);
                }
            }
        }

        i = width-1;
        in = srcData.get(srcIndex+i)&0xFF;
        if (in >= highThreshold) {
            dstData.put(dstIndex+i, highValue);
        } else if (in < lowThreshold) {
            dstData.put(dstIndex+i, lowValue);
        } else {
            byte prev = dstData.get(dstIndex+i-1);
            if (prev == highValue) {
                dstData.put(dstIndex+i, highValue);
            } else {
                dstData.put(dstIndex+i, medValue);
            }
        }

        height--;

        // other lines
        while (height-- > 0) {
            srcIndex += srcStep;
            dstIndex += dstStep;

            // first column
            i = 0;
            in = srcData.get(srcIndex+i)&0xFF;
            if (in >= highThreshold) {
                dstData.put(dstIndex+i, highValue);
            } else if (in < lowThreshold) {
                dstData.put(dstIndex+i, lowValue);
            } else {
                byte prev1 = dstData.get(dstIndex+i-dstStep);
                byte prev2 = dstData.get(dstIndex+i-dstStep+1);
                if (prev1 == highValue || prev2 == highValue) {
                    dstData.put(dstIndex+i, highValue);
                } else {
                    dstData.put(dstIndex+i, medValue);
                }
            }

            // other columns
            for (i = 1; i < width-1; i++) {
                in = srcData.get(srcIndex+i)&0xFF;
                if (in >= highThreshold) {
                    dstData.put(dstIndex+i, highValue);
                } else if (in < lowThreshold) {
                    dstData.put(dstIndex+i, lowValue);
                } else {
                    byte prev1 = dstData.get(dstIndex+i-1);
                    byte prev2 = dstData.get(dstIndex+i-dstStep-1);
                    byte prev3 = dstData.get(dstIndex+i-dstStep);
                    byte prev4 = dstData.get(dstIndex+i-dstStep+1);

                    if (prev1 == highValue || prev2 == highValue ||
                        prev3 == highValue || prev4 == highValue) {
                        dstData.put(dstIndex+i, highValue);
                    } else {
                        dstData.put(dstIndex+i, medValue);
                    }
                }
            }

            // last column
            i = width-1;
            in = srcData.get(srcIndex+i)&0xFF;
            if (in >= highThreshold) {
                dstData.put(dstIndex+i, highValue);
            } else if (in < lowThreshold) {
                dstData.put(dstIndex+i, lowValue);
            } else {
                byte prev1 = dstData.get(dstIndex+i-1);
                byte prev2 = dstData.get(dstIndex+i-dstStep-1);
                byte prev3 = dstData.get(dstIndex+i-dstStep);

                if (prev1 == highValue || prev2 == highValue ||
                    prev3 == highValue) {
                    dstData.put(dstIndex+i, highValue);
                } else {
                    dstData.put(dstIndex+i, medValue);
                }
            }
        }

        height = srcImage.height();
        width  = srcImage.width();
        dstIndex = (height-1)*dstStep;

        //
        // second pass backward
        //

        // first (actually last) line
        i = width-1;
        if (dstData.get(dstIndex+i) == medValue) {
            dstData.put(dstIndex+i, lowValue);
        }

        for (i = width-2; i > 0 ; i--) {
            if (dstData.get(dstIndex+i) == medValue) {
                if (dstData.get(dstIndex+i+1) == highValue) {
                    dstData.put(dstIndex+i, highValue);
                } else {
                    dstData.put(dstIndex+i, lowValue);
                }
            }
        }

        i = 0;
        if (dstData.get(dstIndex+i) == medValue) {
            if (dstData.get(dstIndex+i+1) == highValue) {
                dstData.put(dstIndex+i, highValue);
            } else {
                dstData.put(dstIndex+i, lowValue);
            }
        }

        height--;

        // other lines
        while (height-- > 0) {
            dstIndex -= dstStep;

            // first column
            i = width-1;
            if (dstData.get(dstIndex+i) == medValue) {
                if (dstData.get(dstIndex+i+dstStep)   == highValue ||
                    dstData.get(dstIndex+i+dstStep-1) == highValue) {
                    dstData.put(dstIndex+i, highValue);
                } else {
                    dstData.put(dstIndex+i, lowValue);
                }
            }

            // other columns
            for (i = width-2; i > 0 ; i--) {
                if (dstData.get(dstIndex+i) == medValue) {
                    if (dstData.get(dstIndex+i+1)         == highValue ||
                        dstData.get(dstIndex+i+dstStep+1) == highValue ||
                        dstData.get(dstIndex+i+dstStep)   == highValue ||
                        dstData.get(dstIndex+i+dstStep-1) == highValue) {
                        dstData.put(dstIndex+i, highValue);
                    } else {
                        dstData.put(dstIndex+i, lowValue);
                    }
                }
            }

            // last column
            i = 0;
            if (dstData.get(dstIndex+i) == medValue) {
                if (dstData.get(dstIndex+i+1)         == highValue ||
                    dstData.get(dstIndex+i+dstStep+1) == highValue ||
                    dstData.get(dstIndex+i+dstStep)   == highValue) {
                    dstData.put(dstIndex+i, highValue);
                } else {
                    dstData.put(dstIndex+i, lowValue);
                }
            }
        }
    }

    /** Clamps image intensities between min and max. */
    public static void clamp(IplImage src, IplImage dst, double min, double max) {
        switch (src.depth()) {
            case IPL_DEPTH_8U: {
                ByteBuffer sb = src.getByteBuffer();
                ByteBuffer db = dst.getByteBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (byte)Math.max(Math.min(sb.get(i) & 0xFF,max),min));
                }
                break;
            }
            case IPL_DEPTH_16U: {
                ShortBuffer sb = src.getShortBuffer();
                ShortBuffer db = dst.getShortBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (short)Math.max(Math.min(sb.get(i) & 0xFFFF,max),min));
                }
                break;
            }
            case IPL_DEPTH_32F: {
                FloatBuffer sb = src.getFloatBuffer();
                FloatBuffer db = dst.getFloatBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (float)Math.max(Math.min(sb.get(i),max),min));
                }
                break;
            }
            case IPL_DEPTH_8S: {
                ByteBuffer sb = src.getByteBuffer();
                ByteBuffer db = dst.getByteBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (byte)Math.max(Math.min(sb.get(i),max),min));
                }
                break;
            }
            case IPL_DEPTH_16S: {
                ShortBuffer sb = src.getShortBuffer();
                ShortBuffer db = dst.getShortBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (short)Math.max(Math.min(sb.get(i),max),min));
                }
                break;
            }
            case IPL_DEPTH_32S: {
                IntBuffer sb = src.getIntBuffer();
                IntBuffer db = dst.getIntBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (int)Math.max(Math.min(sb.get(i),max),min));
                }
                break;
            }
            case IPL_DEPTH_64F: {
                DoubleBuffer sb = src.getDoubleBuffer();
                DoubleBuffer db = dst.getDoubleBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, Math.max(Math.min(sb.get(i),max),min));
                }
                break;
            }
            default: assert(false);
        }
    }

    /** vector norm 2 */
    public static double norm(double[] v) {
        return norm(v, 2.0);
    }
    /** vector norm p */
    public static double norm(double[] v, double p) {
        double norm = 0;
        if (p == 1.0) {
            for (double e : v) {
                norm += Math.abs(e);
            }
        } else if (p == 2.0) {
            for (double e : v) {
                norm += e*e;
            }
            norm = Math.sqrt(norm);
        } else if (p == Double.POSITIVE_INFINITY) {
            for (double e : v) {
                e = Math.abs(e);
                if (e > norm) {
                    norm = e;
                }
            }
        } else if (p == Double.NEGATIVE_INFINITY) {
            norm = Double.MAX_VALUE;
            for (double e : v) {
                e = Math.abs(e);
                if (e < norm) {
                    norm = e;
                }
            }
        } else {
            for (double e : v) {
                norm += Math.pow(Math.abs(e), p);
            }
            norm = Math.pow(norm, 1/p);
        }
        return norm;
    }

    /** induced norm 2 */
    public static double norm(CvMat A) {
        return norm(A, 2.0);
    }
    /** induced norm p */
    public static double norm(CvMat A, double p) {
        return norm(A, p, null);
    }
    /** induced norm p */
    public static double norm(CvMat A, double p, CvMat W) {
        double norm = -1;

        if (p == 1.0) {
            int cols = A.cols(), rows = A.rows();
            for (int j = 0; j < cols; j++) {
                double n = 0;
                for (int i = 0; i < rows; i++) {
                    n += Math.abs(A.get(i, j));
                }
                norm = Math.max(n, norm);
            }
        } else if (p == 2.0) {
            int size = Math.min(A.rows(), A.cols());
            if (W == null || W.rows() != size || W.cols() != 1) {
                W = CvMat.create(size, 1);
            }
            cvSVD(A, W, null, null, 0);
            norm = W.get(0); // largest singular value
        } else if (p == Double.POSITIVE_INFINITY) {
            int rows = A.rows(), cols = A.cols();
            for (int i = 0; i < rows; i++) {
                double n = 0;
                for (int j = 0; j < cols; j++) {
                    n += Math.abs(A.get(i, j));
                }
                norm = Math.max(n, norm);
            }
        } else {
            assert(false);
        }
        return norm;
    }

    public static double cond(CvMat A) {
        return cond(A, 2.0);
    }
    public static double cond(CvMat A, double p) {
        return cond(A, p, null);
    }
    public static double cond(CvMat A, double p, CvMat W) {
        double cond = -1;

        if (p == 2.0) {
            int size = Math.min(A.rows(), A.cols());
            if (W == null || W.rows() != size || W.cols() != 1) {
                W = CvMat.create(size, 1);
            }
            cvSVD(A, W, null, null, 0);
            cond = W.get(0)/W.get(W.length()-1); // largest/smallest singular value
        } else {
            // should put something faster here if we're really serious
            // about using something other than the 2-norm
            int rows = A.rows(), cols = A.cols();
            if (W == null || W.rows() != rows || W.cols() != cols) {
                W = CvMat.create(rows, cols);
            }
            CvMat Ainv = W;
            cvInvert(A, Ainv);
            cond = norm(A, p)*norm(Ainv, p);
        }
        return cond;
    }

    public static double median(double[] doubles) {
        double[] sorted = doubles.clone();
        Arrays.sort(sorted);
        if (doubles.length%2 == 0) {
            return (sorted[doubles.length/2 - 1] + sorted[doubles.length/2])/2;
        } else {
            return sorted[doubles.length/2];
        }
    }
    public static <T extends Object> T median(T[] objects) {
        T[] sorted = objects.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length/2];
    }

    public static void fractalTriangleWave(double[] line, int i, int j, double a) {
        fractalTriangleWave(line, i, j, a, -1);
    }
    public static void fractalTriangleWave(double[] line, int i, int j, double a, int roughness) {
        int m = (j-i)/2+i;
        if (i == j || i == m) {
            return;
        }
        line[m] = (line[i]+line[j])/2 + a;
        if (roughness > 0 && line.length > roughness*(j-i)) {
            fractalTriangleWave(line, i, m, 0, roughness);
            fractalTriangleWave(line, m, j, 0, roughness);
        } else {
            fractalTriangleWave(line, i, m,  a/SQRT2, roughness);
            fractalTriangleWave(line, m, j, -a/SQRT2, roughness);
        }
    }

    public static void fractalTriangleWave(IplImage image, CvMat H) {
        fractalTriangleWave(image, H, -1);
    }
    public static void fractalTriangleWave(IplImage image, CvMat H, int roughness) {
        assert (image.depth() == IPL_DEPTH_32F);
        double[] line = new double[image.width()];
        fractalTriangleWave(line, 0,             line.length/2,  1, roughness);
        fractalTriangleWave(line, line.length/2, line.length-1, -1, roughness);

        double[] minMax = { Double.MAX_VALUE, Double.MIN_VALUE };
        int height   = image.height();
        int width    = image.width();
        int channels = image.nChannels();
        int step     = image.widthStep();
        int start = 0;
        if (image.roi() != null) {
            height = image.roi().height();
            width  = image.roi().width();
            start  = image.roi().yOffset()*step/4 + image.roi().xOffset()*channels;
        }
        FloatBuffer fb = image.getFloatBuffer(start);
        double[] h = H == null ? null : H.get();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < channels; z++) {
                    double sum = 0.0;
                    if (h == null) {
                        sum += line[x];
                    } else {
                        double x2 = (h[0]*x + h[1]*y + h[2])/(h[6]*x + h[7]*y + h[8]);
                        while (x2 < 0) {
                            x2 += line.length;
                        }
                        int xi2   = (int)x2;
                        double xn = x2 - xi2;
                        sum += line[ xi2   %line.length]*(1-xn) +
                               line[(xi2+1)%line.length]*xn;
                    }
                    minMax[0] = Math.min(minMax[0], sum);
                    minMax[1] = Math.max(minMax[1], sum);
                    fb.put(y*step/4 + x*channels + z, (float)sum);
                }
            }
        }

        cvConvertScale(image, image, 1/(minMax[1]-minMax[0]),
                -minMax[0]/(minMax[1]-minMax[0]));
    }

    public static void main(String[] args) {
        String version = JavaCV.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "unknown";
        }
        System.out.println(
            "JavaCV version " + version + "\n" +
            "Copyright (C) 2009-2018 Samuel Audet <samuel.audet@gmail.com>\n" +
            "Project site: https://github.com/bytedeco/javacv");
        System.exit(0);
    }

}
