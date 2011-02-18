/*
 * Copyright (C) 2009,2010,2011 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.javacv;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import com.googlecode.javacv.Parallel.Looper;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class JavaCV {

    public static final double
            SQRT2 = 1.41421356237309504880,
            FLT_EPSILON = 1.19209290e-7F,
            DBL_EPSILON = 2.2204460492503131e-16;

    // this is basically cvGetPerspectiveTransform() using CV_LU instead of
    // CV_SVD, because the latter gives inaccurate results...
    public static CvMat getPerspectiveTransform(double[] src, double[] dst, CvMat map_matrix) {
        // creating and releasing matrices via NIO here in this function...
        // this can easily become a bottleneck
        CvMat A = CvMat.take(8, 8);
        CvMat b = CvMat.take(8, 1);
        CvMat x = CvMat.take(8, 1);

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

        A.pool();
        b.pool();
        x.pool();

        return map_matrix;
    }

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

    public static CvMat getPlaneParameters(double[] src, double[] dst,
            CvMat invSrcK, CvMat dstK, CvMat R, CvMat t, CvMat n) {
        CvMat A = CvMat.take(3, 3), b = CvMat.take(3, 1);

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

        A.pool(); b.pool();

        return n;
    }

    public static CvMat getPerspectiveTransform(double[] src, double[] dst, 
            CvMat invSrcK, CvMat dstK, CvMat R, CvMat t, CvMat H) {
        CvMat n = CvMat.take(3, 1);
        getPlaneParameters(src, dst, invSrcK, dstK, R, t, n);

        // H = R - t*n^T
        cvGEMM(t, n, -1,  R, 1,  H, CV_GEMM_B_T);
        // H = dstK * H * srcK^-1
        cvMatMul(dstK, H, H);
        cvMatMul(H, invSrcK, H);

        n.pool();

        return H;
    }

    public static void perspectiveTransform(double[] src, double[] dst,
            CvMat invSrcK, CvMat dstK, CvMat R, CvMat t, CvMat n, boolean invert) {
        CvMat H = CvMat.take(3, 3);

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

    // Algorithms for Plane-Based Pose Estimation, Peter Sturm
    // This assumes plane parameters n == z axis.
    public static void HtoRt(CvMat H, CvMat R, CvMat t) {
        CvMat M = CvMat.take(3, 2), S = CvMat.take(2, 2),
              U = CvMat.take(3, 2), V = CvMat.take(2, 2);
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

        V.pool(); U.pool();
        S.pool(); M.pool();
    }

    public static double HnToRt(CvMat H, CvMat n, CvMat R, CvMat t) {
        CvMat R1 = CvMat.take(3, 3),  R2 = CvMat.take(3, 3),
              t1 = CvMat.take(3, 1),  t2 = CvMat.take(3, 1),
              n1 = CvMat.take(3, 1),  n2 = CvMat.take(3, 1),
              H1 = CvMat.take(3, 3),  H2 = CvMat.take(3, 3);
        CvMat S = CvMat.take(3, 3), U = CvMat.take(3, 3), V = CvMat.take(3, 3);
        cvSVD(H, S, U, V, 0);
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

        V.pool(); U.pool(); S.pool();

        H2.pool(); H1.pool();
        n2.pool(); n1.pool();
        t2.pool(); t1.pool();
        R2.pool(); R1.pool();

        return err;
    }

    // Ported to Java/OpenCV from
    // Bill Triggs. Autocalibration from Planar Scenes. In 5th European Conference
    // on Computer Vision (ECCV ’98), volume I, pages 89–105. Springer-Verlag, 1998.
    public static double homogToRt(CvMat H,
            CvMat R1, CvMat t1, CvMat n1,
            CvMat R2, CvMat t2, CvMat n2) {
        CvMat S = CvMat.take(3, 3), U = CvMat.take(3, 3), V = CvMat.take(3, 3);
        cvSVD(H, S, U, V, 0);
        double zeta = homogToRt(S, U, V, R1, t1, n1, R2, t2, n2);
        V.pool(); U.pool(); S.pool();
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

    public static void adaptiveBinarization(final IplImage src, final IplImage sumimage, 
            final IplImage sqsumimage, final IplImage dst, final boolean invert,
            final int minwindow, final int maxwindow, final double varmultiplier, final double k) {
        final int w = src.width();
        final int h = src.height();
        final int srcdepth = src.depth();
//        final IplImage graysrc;
//        if (src.nChannels() > 1) {
//            cvCvtColor(src, dst, CV_BGR2GRAY);
//            graysrc = dst;
//        } else {
//            graysrc = src;
//        }

        // compute integral images
        cvIntegral(src, sumimage, sqsumimage, null);
        final DoubleBuffer sumbuf = sumimage.getByteBuffer().asDoubleBuffer();
        final DoubleBuffer sqsumbuf = sqsumimage.getByteBuffer().asDoubleBuffer();
        final int sumstep = sumimage.widthStep();
        final int sqsumstep = sqsumimage.widthStep();
        final ByteBuffer srcbuf = src.getByteBuffer();
        final ByteBuffer dstbuf = dst.getByteBuffer();
        final int srcstep = src.widthStep();
        final int dststep = dst.widthStep();

        // try to detect a reasonable maximum and minimum intensity
        // for thresholds instead of simply 0 and 255...
        double totalmean = sumbuf.get((h-1)*sumstep/8 + (w-1)) -
                           sumbuf.get((h-1)*sumstep/8) -
                           sumbuf.get(w-1) + sumbuf.get(0);
        totalmean /= w*h;
        double totalsqmean = sqsumbuf.get((h-1)*sqsumstep/8 + (w-1)) -
                             sqsumbuf.get((h-1)*sqsumstep/8) -
                             sqsumbuf.get(w-1) + sqsumbuf.get(0);
        totalsqmean /= w*h;
        double totalvar = totalsqmean - totalmean*totalmean;
//double totaldev = Math.sqrt(totalvar);
//System.out.println(totaldev);
        final double targetvar = totalvar*varmultiplier;

        //for (int y = 0; y < h; y++) {
        Parallel.loop(0, h, new Looper() {
        public void loop(int from, int to, int looperID) {
            for (int y = from; y < to; y++) {
                for (int x = 0; x < w; x++) {
                    double var = 0, mean = 0, sqmean = 0;
                    int upperlimit = maxwindow;
                    int lowerlimit = minwindow;
                    int window = upperlimit; // start with maxwindow
                    while (upperlimit - lowerlimit > 2) {
                        int x1 = Math.max(x-window/2, 0);
                        int x2 = Math.min(x+window/2+1, w);

                        int y1 = Math.max(y-window/2, 0);
                        int y2 = Math.min(y+window/2+1, h);

                        mean = sumbuf.get(y2*sumstep/8 + x2) -
                               sumbuf.get(y2*sumstep/8 + x1) -
                               sumbuf.get(y1*sumstep/8 + x2) +
                               sumbuf.get(y1*sumstep/8 + x1);
                        mean /= window*window;
                        sqmean = sqsumbuf.get(y2*sqsumstep/8 + x2) -
                                           sqsumbuf.get(y2*sqsumstep/8 + x1) -
                                           sqsumbuf.get(y1*sqsumstep/8 + x2) +
                                           sqsumbuf.get(y1*sqsumstep/8 + x1);
                        sqmean /= window*window;
                        var = sqmean - mean*mean;

                        // if we're at maximum window size, but variance is
                        // too low anyway, let's break out immediately
                        if (window == upperlimit && var < targetvar) {
                            break;
                        }

                        // otherwise, start binary search
                        if (var > targetvar) {
                            upperlimit = window;
                        } else {
                            lowerlimit = window;
                        }

                        window = lowerlimit   + (upperlimit-lowerlimit)/2;
                        window = (window/2)*2 + 1;
                    }

                    double value = 0;
                    if (srcdepth == IPL_DEPTH_8U) {
                        value = srcbuf.get(y*srcstep       + x) & 0xFF;
                    } else if (srcdepth == IPL_DEPTH_32F) {
                        value = srcbuf.getFloat(y*srcstep  + 4*x);
                    } else if (srcdepth == IPL_DEPTH_64F) {
                        value = srcbuf.getDouble(y*srcstep + 8*x);
                    } else {
                        //cvIntegral() does not support other image types, so we
                        //should not be able to get here...
                        assert(false);
                    }
                    if (invert) {
                        //double threshold = 255 - (255 - mean) * (1 + 0.1*(Math.sqrt(var)/128 - 1));
                        double threshold = 255 - (255 - mean) * k;
                        dstbuf.put(y*dststep + x, (value < threshold ? (byte)0xFF : (byte)0x00));
                    } else {
                        //double threshold = mean * (1 + k*(Math.sqrt(var)/128 - 1));
                        double threshold = mean * k;
                        dstbuf.put(y*dststep + x, (value > threshold ? (byte)0xFF : (byte)0x00));
                    }
                }
            }
        }});
    }

    // clamps image intensities between min and max...
    public static void minMaxS(IplImage src, double min, double max, IplImage dst) {

        switch (src.depth()) {
            case IPL_DEPTH_8U: {
                ByteBuffer sb = src.getByteBuffer();
                ByteBuffer db = dst.getByteBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (byte)Math.max(Math.min(sb.get(i) & 0xFF,max),min));
                }
                break; }
            case IPL_DEPTH_16U: {
                ShortBuffer sb = src.getByteBuffer().asShortBuffer();
                ShortBuffer db = dst.getByteBuffer().asShortBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (short)Math.max(Math.min(sb.get(i) & 0xFFFF,max),min));
                }
                break; }
            case IPL_DEPTH_32F: {
                FloatBuffer sb = src.getByteBuffer().asFloatBuffer();
                FloatBuffer db = dst.getByteBuffer().asFloatBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (float)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case IPL_DEPTH_8S: {
                ByteBuffer sb = src.getByteBuffer();
                ByteBuffer db = dst.getByteBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (byte)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case IPL_DEPTH_16S: {
                ShortBuffer sb = src.getByteBuffer().asShortBuffer();
                ShortBuffer db = dst.getByteBuffer().asShortBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (short)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case IPL_DEPTH_32S: {
                IntBuffer sb = src.getByteBuffer().asIntBuffer();
                IntBuffer db = dst.getByteBuffer().asIntBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, (int)Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            case IPL_DEPTH_64F: {
                DoubleBuffer sb = src.getByteBuffer().asDoubleBuffer();
                DoubleBuffer db = dst.getByteBuffer().asDoubleBuffer();
                for (int i = 0; i < sb.capacity(); i++) {
                    db.put(i, Math.max(Math.min(sb.get(i),max),min));
                }
                break; }
            default: assert(false);
        }

    }

    // vector norm
    public static double norm(double[] v) {
        return norm(v, 2.0);
    }
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

    // induced norm
    public static double norm(CvMat A) {
        return norm(A, 2.0);
    }
    public static double norm(CvMat A, double p) {
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
            CvMat W = CvMat.take(Math.min(A.rows(), A.cols()), 1);
            cvSVD(A, W, null, null, 0);
            norm = W.get(0); // largest singular value
            W.pool();
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

    public static double cond(CvMat A, double p) {
        double cond = -1;

        if (p == 2.0) {
            CvMat W = CvMat.take(Math.min(A.rows(), A.cols()), 1);
            cvSVD(A, W, null, null, 0);
            cond = W.get(0)/W.get(W.length()-1); // largest/smallest singular value
            W.pool();
        } else {
            // should put something faster here if we're really serious
            // about using something other than the 2-norm
            CvMat Ainv = CvMat.take(A.rows(), A.cols());
            cvInvert(A, Ainv);
            cond = norm(A, p)*norm(Ainv, p);
            Ainv.pool();
        }
        return cond;
    }

    public static double randn(CvRNG state, double sigma) {
        return randn(state, cvRealScalar(sigma));
    }
    public static double randn(CvRNG state, CvScalar sigma) {
        CvMat values = CvMat.take(1, 1);
        cvRandArr(state, values, CV_RAND_NORMAL, CvScalar.ZERO, sigma);
        double res = values.get(0);
        values.pool();
        return res;
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
        String timestamp = JavaCV.class.getPackage().getImplementationVersion();
        if (timestamp == null) {
            timestamp = "unknown";
        }
        System.out.println(
            "JavaCV build timestamp " + timestamp + "\n" +
            "Copyright (C) 2009,2010,2011 Samuel Audet <samuel.audet@gmail.com>\n" +
            "Project site: http://code.google.com/p/javacv/\n\n" +

            "Licensed under the GNU General Public License version 2 (GPLv2) with Classpath exception.\n" +
            "Please refer to LICENSE.txt or http://www.gnu.org/licenses/ for details.");
        System.exit(0);
    }

}
