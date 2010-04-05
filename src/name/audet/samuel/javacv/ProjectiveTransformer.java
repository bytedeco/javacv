/*
 * Copyright (C) 2009,2010 Samuel Audet
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

package name.audet.samuel.javacv;

import java.util.Arrays;

import static name.audet.samuel.javacv.jna.cvkernels.*;
import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveTransformer implements ImageTransformer {
    public ProjectiveTransformer(CvMat K1, CvMat K2, CvMat R, CvMat t, CvMat n, double[] referencePoints) {
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

        this.referencePoints = referencePoints == null ? null : referencePoints.clone();
    }
    public ProjectiveTransformer(ProjectiveDevice d1, ProjectiveDevice d2, CvMat n, double[] referencePoints) {
        // assuming d1 has identity values, use d2's stuff directly
        this(d1.cameraMatrix, d2.cameraMatrix, d2.R, d2.T, n, referencePoints);
    }
    public ProjectiveTransformer() {
        this(null, null, null, null, null, new double[0]);
    }
    public ProjectiveTransformer(double[] referencePoints) {
        this(null, null, null, null, null, referencePoints);
    }

    private CvMat K1 = null, K2 = null, invK1 = null, invK2 = null, R = null, t = null, n = null;
    private double[] referencePoints = null;
    private CvScalar.ByValue fillColor = cvScalar(0.0, 0.0, 0.0, 1.0);

    public CvScalar.ByValue getFillColor() {
        return fillColor;
    }
    public void setFillColor(CvScalar fillColor) {
        this.fillColor = fillColor.byValue();
    }

    public double[] getReferencePoints() {
        return referencePoints;
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

        final CvMat H = CvMat.take(3, 3);
        prepareHomography(H, pyramidLevel, p, true);

        // use ROI not as a sub-image, but as the region we want to fill in
        // the destination image... so here we compensate for the translation
        // caused by the ROI inside cvWarpPerspective()
        if (roi != null && (roi.x != 0 || roi.y != 0)) {
            if (inverse) {
                H.put(2, H.get(0)*roi.x + H.get(1)*roi.y + H.get(2));
                H.put(5, H.get(3)*roi.x + H.get(4)*roi.y + H.get(5));
                H.put(8, H.get(6)*roi.x + H.get(7)*roi.y + H.get(8));
            } else {
                H.put(0, H.get(0) - roi.x*H.get(6));
                H.put(1, H.get(1) - roi.x*H.get(7));
                H.put(2, H.get(2) - roi.x*H.get(8));
                H.put(3, H.get(3) - roi.y*H.get(6));
                H.put(4, H.get(4) - roi.y*H.get(7));
                H.put(5, H.get(5) - roi.y*H.get(8));
            }
        }

        dstImage.origin = srcImage.origin; // cvWarpPerspective doesn't use it..

        if (roi == null) {
            cvResetImageROI(dstImage);
        } else {
            cvSetImageROI(dstImage, roi.byValue());
        }
        cvWarpPerspective(srcImage, dstImage, H, CV_INTER_LINEAR |
                CV_WARP_FILL_OUTLIERS | (inverse ? CV_WARP_INVERSE_MAP : 0),
                getFillColor());

        H.pool();
    }

    public void transform(CvMat srcPts, CvMat dstPts, ImageTransformer.Parameters parameters, boolean inverse) {
        Parameters p = ((Parameters)parameters);
        CvMat H;
        if (inverse) {
            H = CvMat.take(3, 3);
            cvInvert(p.getH(), H);
        } else {
            H = p.getH();
        }
        cvPerspectiveTransform(srcPts, dstPts, H);
        if (inverse) {
            H.pool();
        }
    }

    public void transform(Data[] data, IplImage mask, CvRect roi, double zeroThreshold,
            int pyramidLevel, ImageTransformer.Parameters[] parameters) {
        assert (data.length == parameters.length);
        boolean allOK = true;
        for (int i = 0; i < data.length; i++) {
            Data d = data[i];
            if (d.srcImg != null) {
                if ((d.transImg != null || d.dstImg != null) &&
                     d.subImg   == null && d.srcDotImg == null && d.dstDstDot == null) {
                        transform(d.srcImg, d.transImg == null ? d.dstImg : d.transImg,
                                roi, pyramidLevel, parameters[i], d.inverse);
                } else {
                    allOK = false;
                }
            }
        }
        if (!allOK) {
            class Cache {
                MultiWarpColorTransformData[] kernelData;
                CvMat[] H;
            }
            Cache cache = data[0].cache instanceof Cache ?
                   (Cache)data[0].cache : new Cache();
            if (cache.kernelData == null || cache.kernelData.length != data.length ||
                         cache.H == null ||          cache.H.length != data.length) {
                data[0].cache = cache;
                cache.kernelData = (MultiWarpColorTransformData[])
                        new MultiWarpColorTransformData().toArray(data.length);
                cache.H = new CvMat[data.length];
            }
            MultiWarpColorTransformData[] kd = cache.kernelData;

            for (int i = 0; i < kd.length; i++) {
                Data d = data[i];
                kd[i].srcImg    = d.srcImg    == null ? null : d.srcImg   .getPointer();
                kd[i].srcImg2   = null;
                kd[i].subImg    = d.subImg    == null ? null : d.subImg   .getPointer();
                kd[i].srcDotImg = d.srcDotImg == null ? null : d.srcDotImg.getPointer();

                CvMat H = cache.H[i] == null ? cache.H[i] = CvMat.create(3, 3) : cache.H[i];

                prepareHomography(H, pyramidLevel, (Parameters)parameters[i], d.inverse);

                kd[i].H1 = H.getPointer();
                kd[i].H2 = null;
                kd[i].X  = null;

                kd[i].transImg  = d.transImg  == null ? null : d.transImg .getPointer();
                kd[i].dstImg    = d.dstImg    == null ? null : d.dstImg   .getPointer();
                kd[i].dstDstDot = d.dstDstDot;
            }

            multiWarpColorTransform(kd, mask, roi, zeroThreshold, getFillColor());

            for (int i = 0; i < data.length; i++) {
                data[i].dstCount     = kd[i].dstCount;
                data[i].dstCountZero = kd[i].dstCountZero;
                data[i].srcDstDot    = kd[i].srcDstDot;
            }
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
        protected IplImage[] tempImage = null;
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
            if (referencePoints != null) {
                if (referencePoints.length == 0) {
                    if (K2 != null && invK1 != null && n == null) {
                        projectiveParameters = new double[] { 1, 0, 0,  0, 1, 0,  0, 0 /*, 1*/,  0, 0, 0};
                    } else {
                        projectiveParameters = new double[] { 1, 0, 0,  0, 1, 0,  0, 0 /*, 1*/ };
                    }
                } else {
                    if (K2 != null && invK1 != null && n == null) {
                        projectiveParameters = Arrays.copyOf(referencePoints, 11);
                    } else {
                        projectiveParameters = referencePoints.clone();
                    }
                }
            } else if (K2 != null && invK1 != null) {
                if (R != null && t != null) {
                    // no identity available for this one, so...
                    projectiveParameters = new double[] { 0, 0, 0 };
                } else if (n != null) {
                    projectiveParameters = new double[] { 0, 0, 0,  0, 0, 0 };
                }
            }
        }

        public boolean addDelta(int i) {
            return addDelta(i, 1/*(1<<pyramidLevel)*/);
        }
        public boolean addDelta(int i, int scale) {
            if (referencePoints != null && i < 8) {
                // add one pixel..
                projectiveParameters[i] += scale;
            } else if (K2 != null && invK1 != null) {
                if (i < 3 || i >= 8) {
                    // normal vector n or rotation vector
                    double norm = 0;
                    int start = i < 3 ? 0 : 8;
                    for (int j = start; j < start+3; j++) {
                        norm += projectiveParameters[j]*projectiveParameters[j];
                    }
                    if (norm > Float.MIN_VALUE) {
                        projectiveParameters[i]+=Math.sqrt(norm)*0.01 * scale;
                    } else {
                        projectiveParameters[i]+=Math.PI/180 * scale; // 1 degree sounds OK ?
                    }
                } else {
                    // translation vector

                    // assuming a reference plane at [0, 0, 1],
                    // this is about 1% of image resolution?
                    projectiveParameters[i] += 0.01 * scale;
                }
            }
            setUpdateNeeded(true);
            return false;
        }
        public double getConstraintError() {
            update();
            return constraintError;
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

            if (projectiveParameters.length == 8 && referencePoints != null) {

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

                if (referencePoints.length == 0) {
                    // direct homography parameterization
                    for (int i = 0; i < 8; i++) {
                        projectiveParameters[i] = H.get(i)/H.get(8);
                    }
                } else {
                    // 4 point parametrization
                    CvMat pts = CvMat.take(4, 1, CV_64F, 2);
                    pts.put(referencePoints);
                    cvPerspectiveTransform(pts, pts, H);
                    pts.get(projectiveParameters);
                    pts.pool();
                }
                setUpdateNeeded(true);
            } else {
                throw new UnsupportedOperationException("Compose operation not supported.");
            }
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

            if (referencePoints != null) {
                if (referencePoints.length == 0) {
                    // direct homography parameterization
                    H.put(0, projectiveParameters, 0, 8);
                    H.put(8, 1);
                } else {
                    // 4 point parameterization
                    JavaCV.getPerspectiveTransform(referencePoints, projectiveParameters, H);
                }
                if (K1 != null && invK2 != null) {
                    if (n != null) {
                        n2 = n; // take n from warper
                    } else {
                        if (n2 == null) {
                            n2 = CvMat.create(3, 1);
                        }
                        n2.put(0, projectiveParameters, 8, 3); // take n from parameters
                    }

                    CvMat Hprime = CvMat.take(3, 3);
                    cvCopy(H, Hprime);
                    if (R2 == null) {
                        R2 = CvMat.create(3, 3);
                    }
                    if (t2 == null) {
                        t2 = CvMat.create(3, 1);
                    }

                    cvMatMul(invK2,  Hprime, Hprime);
                    cvMatMul(Hprime, K1,     Hprime);

                    // get 3D rotation and translation, with given n
                    constraintError = JavaCV.HnToRt(Hprime, n2, R2, t2);
                    //System.out.println(constraintError);
if (false) {
                    if (referencePoints != null && referencePoints.length > 0) {
                        CvMat pts = CvMat.take(4, 1, CV_64F, 2);
                        CvMat r = CvMat.take(3, 1);
                        CvMat t3 = CvMat.take(3, 1);
                        cvRodrigues2(R2, r, null);
                        cvMatMul(R2, t2, t3);
                        double step = 1;
                        constraintError = Double.POSITIVE_INFINITY;

                        done:
                        while (Math.abs(step) >= 0.00001) {
                            for (int s = 0; s < 2; s++) {
                                for (int ri = -1; ri < 3; ri++) {
                                    for (int ti = -1; ti < 3; ti++) {
                                        if (ri >= 0) {
                                            r.put(ri, r.get(ri) + step);
                                            cvRodrigues2(r, R2, null);
                                        }
                                        if (ti >= 0)
                                            t3.put(ti, t3.get(ti) + step);
                                        // H = K2(R-t*n^T)K1^-1
                                        cvGEMM(t3, n2, -1,  R2, 1,  Hprime, CV_GEMM_B_T);
                                        cvMatMul(K2,        Hprime, Hprime);
                                        cvMatMul(Hprime,    invK1,  Hprime);

                                        pts.put(referencePoints);
                                        cvPerspectiveTransform(pts, pts, Hprime);
                                        double newError = 0;
                                        for (int i = 0; i < 8; i++) {
                                            double d = projectiveParameters[i] - pts.get(i);
                                            newError += d*d;
                                        }
                                        newError = Math.sqrt(newError/4);
                                        if (newError < constraintError) {
                                            //System.out.println("LOWER CONSTRAINT FOUND: " + constraintError + " -> " + newError);
                                            constraintError = newError;
                                            //break done;
                                        } else {
                                            if (ri >= 0) {
                                                r.put(ri, r.get(ri) - step);
                                                cvRodrigues2(r, R2, null);
                                            }
                                            if (ti >= 0)
                                                t3.put(ti, t3.get(ti) - step);
                                        }
                                    }
                                }
                                step = -step;
                            }
                            step = step/2;
                        }
                        cvGEMM(R2, t3, 1,  null, 0,  t2, CV_GEMM_A_T);
                        pts.pool();
                        r.pool();
                        t3.pool();
                    }
}
                    Hprime.pool();
                }

            } else if (K2 != null && invK1 != null) {
                if (R != null && t != null) {
                    // 3D plane motion, with given R and t
                    if (n2 == null) {
                        n2 = CvMat.create(3, 1);
                    }
                    n2.put(projectiveParameters);
                    // H = R-t*n^T
                    cvGEMM(t, n2, -1,  R, 1,  H, CV_GEMM_B_T);
                } else if (n != null) {
                    // 3D rotation and translation, with given n

                    // put rotation angle and translation in matrices
                    if (R2 == null) {
                        R2 = CvMat.create(3, 3);
                    }
                    if (t2 == null) {
                        t2 = CvMat.create(3, 1);
                    }
                    t2.put(0, projectiveParameters, 0, 3);
                    cvRodrigues2(t2, R2, null);
                    t2.put(0, projectiveParameters, 3, 3);

                    // H = R-tn^T
                    cvGEMM(t2, n, -1,  R2, 1,  H, CV_GEMM_B_T);
                }
                // H = K2 * H * K1^-1
                cvMatMul(K2,    H, H);
                cvMatMul(H, invK1, H);
            }

            setUpdateNeeded(false);
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
                s += p[i];
                if (i < p.length-1) {
                    s+= ", ";
                }
            }
            s += "]";
            return s;
        }

    }

}

