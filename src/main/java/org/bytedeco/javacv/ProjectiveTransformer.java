/*
 * Copyright (C) 2009-2012 Samuel Audet
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

import org.bytedeco.opencv.opencv_calib3d.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.javacv.cvkernels.*;
import static org.bytedeco.opencv.global.opencv_calib3d.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveTransformer implements ImageTransformer {
    public ProjectiveTransformer() {
        this(null, null, null, null, null, new double[0], null);
    }
    public ProjectiveTransformer(double[] referencePoints) {
        this(null, null, null, null, null, referencePoints, null);
    }
    public ProjectiveTransformer(ProjectiveDevice d1, ProjectiveDevice d2, CvMat n,
            double[] referencePoints1, double[] referencePoints2) {
        // assuming d1 has identity values, use d2's stuff directly
        this(d1.cameraMatrix, d2.cameraMatrix, d2.R, d2.T, n, referencePoints1, referencePoints2);
    }
    public ProjectiveTransformer(CvMat K1, CvMat K2, CvMat R, CvMat t, CvMat n,
            double[] referencePoints1, double[] referencePoints2) {
        this.K1    = K1 == null ? null : K1.clone();
        this.K2    = K2 == null ? null : K2.clone();
        this.invK1 = K1 == null ? null : K1.clone();
        this.invK2 = K2 == null ? null : K2.clone();
        if (K1 != null) {
            cvInvert(K1, invK1);
        }
        if (K2 != null) {
            cvInvert(K2, invK2);
        }
        this.R = R == null ? null : R.clone();
        this.t = t == null ? null : t.clone();
        this.n = n == null ? null : n.clone();

        this.referencePoints1 = referencePoints1 == null ? null : referencePoints1.clone();
        this.referencePoints2 = referencePoints2 == null ? null : referencePoints2.clone();
    }

    protected static ThreadLocal<CvMat>
            H3x3   = CvMat.createThreadLocal(3, 3),
            pts4x1 = CvMat.createThreadLocal(4, 1, CV_64F, 2);

    protected CvMat K1 = null, K2 = null, invK1 = null, invK2 = null, R = null, t = null, n = null;
    protected double[] referencePoints1 = null, referencePoints2 = null;
    protected CvScalar fillColor = cvScalar(0.0, 0.0, 0.0, 1.0);

    protected KernelData kernelData = null;
    protected CvMat[] H = null;

    public CvScalar getFillColor() {
        return fillColor;
    }
    public void setFillColor(CvScalar fillColor) {
        this.fillColor = fillColor;
    }

    public double[] getReferencePoints1() {
        return referencePoints1;
    }
    public double[] getReferencePoints2() {
        return referencePoints2;
    }
    public CvMat getK1() {
        return K1;
    }
    public CvMat getK2() {
        return K2;
    }
    public CvMat getInvK1() {
        return invK1;
    }
    public CvMat getInvK2() {
        return invK2;
    }
    public CvMat getR() {
        return R;
    }
    public CvMat getT() {
        return t;
    }
    public CvMat getN() {
        return n;
    }

    protected void prepareHomography(CvMat H, int pyramidLevel, Parameters p, boolean inverse) {
        if (K2 != null && invK1 != null && R != null && t != null && p.fakeIdentity) {
            // no identity available for plane parameter...
            // fakeIdentity needs to be implemented..
            cvSetIdentity(H);
            return;
        }

        if (inverse) {
            H.put(p.getH());
        } else {
            cvInvert(p.getH(), H);
        }

        // adjust the scale of the transformation based on the pyramid level
        if (pyramidLevel > 0) {
            int scale = 1<<pyramidLevel;
            H.put(2, H.get(2)/scale);
            H.put(5, H.get(5)/scale);
            H.put(6, H.get(6)*scale);
            H.put(7, H.get(7)*scale);
        }
    }

    public void transform(IplImage srcImage, IplImage dstImage, CvRect roi, int pyramidLevel,
            ImageTransformer.Parameters parameters, boolean inverse) {
        Parameters p = ((Parameters)parameters);
        if (K2 != null && invK1 != null && R != null && t != null && p.fakeIdentity) {
            // no identity available for plane parameter...
            // fakeIdentity needs to be implemented..
            if (srcImage != dstImage) {
                cvCopy(srcImage, dstImage);
            }
            return;
        }

        CvMat H = H3x3.get();
        prepareHomography(H, pyramidLevel, p, true);

        // use ROI not as a sub-image, but as the region we want to fill in
        // the destination image... so here we compensate for the translation
        // caused by the ROI inside cvWarpPerspective()
        if (roi != null && (roi.x() != 0 || roi.y() != 0)) {
            int x = roi.x(), y = roi.y();
            if (inverse) {
                H.put(2, H.get(0)*x + H.get(1)*y + H.get(2));
                H.put(5, H.get(3)*x + H.get(4)*y + H.get(5));
                H.put(8, H.get(6)*x + H.get(7)*y + H.get(8));
            } else {
                H.put(0, H.get(0) - x*H.get(6));
                H.put(1, H.get(1) - x*H.get(7));
                H.put(2, H.get(2) - x*H.get(8));
                H.put(3, H.get(3) - y*H.get(6));
                H.put(4, H.get(4) - y*H.get(7));
                H.put(5, H.get(5) - y*H.get(8));
            }
        }

        dstImage.origin(srcImage.origin()); // cvWarpPerspective doesn't use it..

        if (roi == null) {
            cvResetImageROI(dstImage);
        } else {
            cvSetImageROI(dstImage, roi);
        }
        cvWarpPerspective(srcImage, dstImage, H, CV_INTER_LINEAR |
                CV_WARP_FILL_OUTLIERS | (inverse ? CV_WARP_INVERSE_MAP : 0),
                getFillColor());
    }

    public void transform(CvMat srcPts, CvMat dstPts, ImageTransformer.Parameters parameters, boolean inverse) {
        Parameters p = ((Parameters)parameters);
        CvMat H;
        if (inverse) {
            H = H3x3.get();
            cvInvert(p.getH(), H);
        } else {
            H = p.getH();
        }
        cvPerspectiveTransform(srcPts, dstPts, H);
    }

    public void transform(Data[] data, CvRect roi, ImageTransformer.Parameters[] parameters, boolean[] inverses) {
        assert data.length == parameters.length;
        if (kernelData == null || kernelData.capacity() < data.length) {
            kernelData = new KernelData(data.length);
        }
        if (H == null || H.length < data.length) {
            H = new CvMat[data.length];
            for (int i = 0; i < H.length; i++) {
                H[i] = CvMat.create(3, 3);
            }
        }

        for (int i = 0; i < data.length; i++) {
            kernelData.position(i);

            kernelData.srcImg(data[i].srcImg);
            kernelData.srcImg2(null);
            kernelData.subImg(data[i].subImg);
            kernelData.srcDotImg(data[i].srcDotImg);
            kernelData.mask(data[i].mask);
            kernelData.zeroThreshold(data[i].zeroThreshold);
            kernelData.outlierThreshold(data[i].outlierThreshold);

            prepareHomography(H[i], data[i].pyramidLevel, (Parameters)parameters[i],
                    inverses == null ? false : inverses[i]);

            kernelData.H1(H[i]);
            kernelData.H2(null);
            kernelData.X (null);

            kernelData.transImg(data[i].transImg);
            kernelData.dstImg(data[i].dstImg);
            kernelData.dstDstDot(data[i].dstDstDot);
        }

        long fullCapacity = kernelData.capacity();
        kernelData.capacity(data.length);
        multiWarpColorTransform(kernelData, roi, getFillColor());
        kernelData.capacity(fullCapacity);

        for (int i = 0; i < data.length; i++) {
            kernelData.position(i);
            data[i].dstCount        = kernelData.dstCount();
            data[i].dstCountZero    = kernelData.dstCountZero();
            data[i].dstCountOutlier = kernelData.dstCountOutlier();
            data[i].srcDstDot       = kernelData.srcDstDot();
        }
    }

    public Parameters createParameters() {
        return new Parameters();
    }

    public class Parameters implements ImageTransformer.Parameters {
        protected Parameters() {
            reset(false);
        }

        protected double[] projectiveParameters = null;
        private CvMat H = CvMat.create(3, 3), n2 = null, R2 = null, t2 = null;
        private double constraintError = 0;
        private boolean updateNeeded = true;
        protected boolean fakeIdentity = false;

        public boolean isUpdateNeeded() {
            return updateNeeded;
        }
        public void setUpdateNeeded(boolean updateNeeded) {
            this.updateNeeded = updateNeeded;
        }

        public int size() {
            return projectiveParameters.length;
        }
        public double[] get() {
            double[] p = new double[size()];
            for (int i = 0; i < p.length; i++) {
                p[i] = get(i);
            }
            return p;
        }
        public double get(int i) {
            return projectiveParameters[i];
        }
        public void set(double ... p) {
            for (int i = 0; i < p.length; i++) {
                set(i, p[i]);
            }
        }
        public void set(int i, double p) {
            if (projectiveParameters[i] != p) {
                projectiveParameters[i]  = p;
                setUpdateNeeded(true);
            }
        }
        public void set(ImageTransformer.Parameters p) {
            set(p.get());
            fakeIdentity = ((Parameters)p).fakeIdentity;
        }
        public void reset(boolean asIdentity) {
            setUpdateNeeded(true);
            if (referencePoints1 != null && (referencePoints1.length == 0 || referencePoints1.length == 8)) {
                if (referencePoints1.length == 0) {
//                    if (K2 != null && invK1 != null && n == null) {
//                        projectiveParameters = new double[] { 1, 0, 0,  0, 1, 0,  0, 0 /*, 1*/,  0, 0, 0};
//                    } else {
                        projectiveParameters = new double[] { 1, 0, 0,  0, 1, 0,  0, 0 /*, 1*/ };
//                    }
                } else {
//                    if (K2 != null && invK1 != null && n == null) {
//                        projectiveParameters = Arrays.copyOf(referencePoints, 11);
//                    } else {
                        projectiveParameters = referencePoints1.clone();
//                    }
                }
            } else if (K2 != null && invK1 != null) {
                if (R != null && t != null) {
                    // no identity available for this one, so...
                    //projectiveParameters = new double[] { 0, 0, 0 };
                    projectiveParameters = new double[] { 
                        referencePoints1[0], referencePoints1[2], referencePoints1[4] };
                } else if (n != null) {
                    projectiveParameters = new double[] { 0, 0, 0,  0, 0, 0 };
                } else {
                    projectiveParameters = new double[] { 0, 0, 0,  0, 0, 0,  0, 0, 0};
                }
            }
        }

//        public boolean addDelta(int i) {
//            return addDelta(i, 1);
//        }
//
//        public boolean addDelta(int i, double scale) {
//            if (referencePoints != null && i < 8) {
//                // add one pixel..
//                projectiveParameters[i] += scale;
//            } else if (K2 != null && invK1 != null) {
//                if (i < 3 || i >= 8) {
//                    projectiveParameters[i] += scale;
//                } else {
//                    // translation vector
//
//                    // assuming a reference plane at [0, 0, 1],
//                    // this is about 1% of image resolution?
//                    projectiveParameters[i] += 0.01 * scale;
//                }
//            }
//            setUpdateNeeded(true);
//            return false;
//        }
        public double getConstraintError() {
            update();
            return constraintError;
        }
        public void set(CvMat setH, boolean inverse) {
            if (projectiveParameters.length == 8 && referencePoints1 != null) {
                if (inverse) {
                    cvInvert(setH, H);
                } else if (setH != H) {
                    cvCopy(setH, H);
                }
                if (referencePoints1.length == 0) {
                    // direct homography parameterization
                    for (int i = 0; i < 8; i++) {
                        projectiveParameters[i] = H.get(i)/H.get(8);
                    }
                } else {
                    // 4 point parametrization
                    CvMat pts = pts4x1.get().put(referencePoints1);
                    cvPerspectiveTransform(pts, pts, H);
                    pts.get(projectiveParameters);
                }
                setUpdateNeeded(true);
            } else {
                throw new UnsupportedOperationException("Set homography operation not supported.");
            }
        }
        public void compose(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            Parameters pp1 = (Parameters)p1, pp2 = (Parameters)p2;
            if (K2 != null && invK1 != null && R != null && t != null && pp1.fakeIdentity) {
                // no identity available for plane parameter...
                // fakeIdentity needs to be implemented..
                return;
            }

            compose(pp1.getH(), inverse1, pp2.getH(), inverse2);
        }
        public void compose(CvMat H1, boolean inverse1, CvMat H2, boolean inverse2) {
            if (inverse1 && inverse2) {
                cvMatMul(H2, H1, H);
                cvInvert(H, H);
            } else if (inverse1) {
                cvInvert(H1, H);
                cvMatMul(H, H2, H);
            } else if (inverse2) {
                cvInvert(H2, H);
                cvMatMul(H1, H, H);
            } else {
                cvMatMul(H1, H2, H);
            }
            set(H, false);
        }

        public CvMat getH() {
            update();
            return H;
        }
        public CvMat getN() {
            update();
            return n2;
        }
        public CvMat getR() {
            update();
            return R2;
        }
        public CvMat getT() {
            update();
            return t2;
        }

        protected void update() {
            if (!isUpdateNeeded()) {
                return;
            }

            if (referencePoints1 != null && (referencePoints1.length == 0 || referencePoints1.length == 8)) {
                if (referencePoints1.length == 0) {
                    // direct homography parameterization
                    H.put(0, projectiveParameters, 0, 8);
                    H.put(8, 1);
                } else {
                    // 4 point parameterization
                    JavaCV.getPerspectiveTransform(referencePoints1, projectiveParameters, H);
                }
//                if (K1 != null && invK2 != null && n != null) {
//                    CvMat Hprime = CvMat.take(3, 3);
//                    cvCopy(H, Hprime);
//                    if (R2 == null) {
//                        R2 = CvMat.create(3, 3);
//                    }
//                    if (t2 == null) {
//                        t2 = CvMat.create(3, 1);
//                    }
//
//                    cvMatMul(invK2,  Hprime, Hprime);
//                    cvMatMul(Hprime, K1,     Hprime);
//
//                    // get 3D rotation and translation, with given n
//                    constraintError = JavaCV.HnToRt(Hprime, n, R2, t2);
//                    //System.out.println(constraintError);
//                    Hprime.pool();
//                }
            } else if (K2 != null && invK1 != null) {
                if (R != null && t != null) {
                    // 3D plane motion, with given R and t
//                    if (n2 == null) {
//                        n2 = CvMat.create(3, 1);
//                    }
                    double[] src = referencePoints2; 
                    double[] dst = { projectiveParameters[0], referencePoints1[1],
                                     projectiveParameters[1], referencePoints1[3],
                                     projectiveParameters[2], referencePoints1[5] };
                    if (R2 == null) {
                        R2 = CvMat.create(3, 3);
                    }
                    if (t2 == null) {
                        t2 = CvMat.create(3, 1);
                    }
                    cvTranspose(R, R2);
                    cvGEMM(R2, t, -1, null, 0, t2, 0);
                    JavaCV.getPerspectiveTransform(src, dst, invK2, K1, R2, t2, H);
//cvConvertScale(H, H, 1/H.get(8), 0);
//System.out.println(H);

//                    n2.put(projectiveParameters);
//                    // H = R-t*n^T
//                    cvGEMM(t, n2, -1,  R, 1,  H, CV_GEMM_B_T);
                } else {
                    // 3D rotation and translation, with given n
                    if (n != null) {
                        n2 = n; // take n from transformer
                    } else {
                        if (n2 == null) {
                            n2 = CvMat.create(3, 1);
                        }
                        n2.put(0, projectiveParameters, 8, 3); // take n from parameters
                    }

                    // put rotation angle and translation in matrices
                    if (R2 == null) {
                        R2 = CvMat.create(3, 3);
                    }
                    if (t2 == null) {
                        t2 = CvMat.create(3, 1);
                    }
                    t2.put(0, projectiveParameters, 0, 3);
                    Rodrigues(cvarrToMat(t2), cvarrToMat(R2), null);
                    t2.put(0, projectiveParameters, 3, 3);

                    // H = R-tn^T
                    cvGEMM(t2, n2, -1,  R2, 1,  H, CV_GEMM_B_T);
                }
//                // H = K2 * H * K1^-1
//                cvMatMul(K2,    H, H);
//                cvMatMul(H, invK1, H);
            }

            setUpdateNeeded(false);
        }

//        public void project() {
//            CvMat t2 = getT(), n2 = getN(), R2 = getR();
//            cvSetIdentity(H);
//            cvGEMM(t2, n2, -1,  H, 1,  H, CV_GEMM_B_T);
//            cvMatMul(R2,    H,  H);
//            cvMatMul(K2,    H,  H);
//            cvMatMul(H, invK1,  H);
//
//            CvMat pts = CvMat.take(4, 1, CV_64F, 2);
//            pts.put(referencePoints);
//            cvPerspectiveTransform(pts, pts, H);
//            pts.get(projectiveParameters);
//            pts.pool();
//        }

        public boolean preoptimize() {
            return false;
        }
        public double[] getSubspace() {
            return null;
        }
        public void setSubspace(double ... p) {
        }

        @Override public Parameters clone() {
            Parameters p = new Parameters();
            p.set(this);
            return p;
        }

        @Override public String toString() {
            String s = "[";
            double[] p = get();
            for (int i = 0; i < p.length; i++) {
                s += (float)p[i];
                if (i < p.length-1) {
                    s+= ", ";
                }
            }
            s += "]";
            return s;
        }
    }
}
