/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.cvkernels.*;

/**
 *
 * @author Samuel Audet
 */
public class ProCamTransformer implements ImageTransformer {
    public ProCamTransformer(double[] referencePoints,
            CameraDevice camera, ProjectorDevice projector) {
        this(referencePoints, camera, projector, null);
    }
    public ProCamTransformer(double[] referencePoints,
            CameraDevice camera, ProjectorDevice projector, CvMat n) {
        this.camera = camera;
        this.projector = projector;

        this.surfaceTransformer = new ProjectiveColorTransformer(
                camera.cameraMatrix, camera.cameraMatrix, null, null, n,
                referencePoints, null, null, 3, 0);
        this.projectorTransformer = new ProjectiveColorTransformer(
                camera.cameraMatrix, projector.cameraMatrix,
                projector.R, projector.T, null,
                new double[] { 0, 0,  camera.imageWidth/2, camera.imageHeight,  camera.imageWidth, 0 },
                new double[] { 0, 0,  projector.imageWidth/2, projector.imageHeight,  projector.imageWidth, 0 },
                projector.colorMixingMatrix, 1, 3);

        if (n != null) {
            frontoParallelH = camera.getFrontoParallelH(referencePoints, n, CvMat.create(3, 3));
            invFrontoParallelH = frontoParallelH.clone();
            cvInvert(frontoParallelH, invFrontoParallelH);
        }
    }

    protected CameraDevice camera = null;
    protected ProjectorDevice projector = null;
    protected ProjectiveColorTransformer surfaceTransformer = null;
    protected ProjectiveColorTransformer projectorTransformer = null;
    protected IplImage[] projectorImage = null, surfaceImage = null;
    protected CvScalar fillColor = cvScalar(0.0, 0.0, 0.0, 1.0);
    protected CvRect roi = new CvRect();
    protected CvMat frontoParallelH = null, invFrontoParallelH = null;

    protected KernelData kernelData = null;
    protected CvMat[] H1 = null;
    protected CvMat[] H2 = null;
    protected CvMat[] X = null;

    public CvScalar getFillColor() {
        return fillColor;
    }
    public void setFillColor(CvScalar fillColor) {
        this.fillColor = fillColor;
    }

    public ProjectiveColorTransformer getSurfaceTransformer() {
        return surfaceTransformer;
    }
    public ProjectiveColorTransformer getProjectorTransformer() {
        return projectorTransformer;
    }

    public IplImage getProjectorImage(int pyramidLevel) {
        return projectorImage[pyramidLevel];
    }
    public void setProjectorImage(IplImage projectorImage0, int minLevel, int maxLevel) {
        setProjectorImage(projectorImage0, minLevel, maxLevel, true);
    }
    public void setProjectorImage(IplImage projectorImage0, int minLevel, int maxLevel, boolean convertToFloat) {
        if (projectorImage == null || projectorImage.length != maxLevel+1) {
            projectorImage = new IplImage[maxLevel+1];
        }

        if (projectorImage0.depth() == IPL_DEPTH_32F || !convertToFloat) {
            projectorImage[minLevel] = projectorImage0;
        } else {
            if (projectorImage[minLevel] == null) {
                projectorImage[minLevel] = IplImage.create(projectorImage0.width(), projectorImage0.height(),
                        IPL_DEPTH_32F, projectorImage0.nChannels(), projectorImage0.origin());
            }
            IplROI ir = projectorImage0.roi();
            if (ir != null) {
                int align = 1<<(maxLevel+1);
                roi.x(Math.max(0, (int)Math.floor((double)ir.xOffset()/align)*align));
                roi.y(Math.max(0, (int)Math.floor((double)ir.yOffset()/align)*align));
                roi.width (Math.min(projectorImage0.width(),  (int)Math.ceil((double)ir.width() /align)*align));
                roi.height(Math.min(projectorImage0.height(), (int)Math.ceil((double)ir.height()/align)*align));
                cvSetImageROI(projectorImage0, roi);
                cvSetImageROI(projectorImage[minLevel], roi);
            } else {
                cvResetImageROI(projectorImage0);
                cvResetImageROI(projectorImage[minLevel]);
            }
            cvConvertScale(projectorImage0, projectorImage[minLevel], 1.0/255.0, 0);
        }

//        CvScalar.ByValue average = cvAvg(projectorImage[0], null);
//        cvSubS(projectorImage[0], average, projectorImage[0], null);

        for (int i = minLevel+1; i <= maxLevel; i++) {
            int w = projectorImage[i-1].width()/2;
            int h = projectorImage[i-1].height()/2;
            int d = projectorImage[i-1].depth();
            int c = projectorImage[i-1].nChannels();
            int o = projectorImage[i-1].origin();
            if (projectorImage[i] == null) {
                projectorImage[i] = IplImage.create(w, h, d, c, o);
            }

            IplROI ir = projectorImage[i-1].roi();
            if (ir != null) {
                roi.x(ir.xOffset()/2); roi.width (ir.width() /2);
                roi.y(ir.yOffset()/2); roi.height(ir.height()/2);
                cvSetImageROI(projectorImage[i], roi);
            } else {
                cvResetImageROI(projectorImage[i]);
            }
            cvPyrDown(projectorImage[i-1], projectorImage[i], CV_GAUSSIAN_5x5);
            cvResetImageROI(projectorImage[i-1]);
        }
    }
    public IplImage getSurfaceImage(int pyramidLevel) {
        return surfaceImage[pyramidLevel];
    }
    public void setSurfaceImage(IplImage surfaceImage0, int pyramidLevels) {
        if (surfaceImage == null || surfaceImage.length != pyramidLevels) {
            surfaceImage = new IplImage[pyramidLevels];
        }
        surfaceImage[0] = surfaceImage0;
        cvResetImageROI(surfaceImage0);
        for (int i = 1; i < pyramidLevels; i++) {
            int w = surfaceImage[i-1].width()/2;
            int h = surfaceImage[i-1].height()/2;
            int d = surfaceImage[i-1].depth();
            int c = surfaceImage[i-1].nChannels();
            int o = surfaceImage[i-1].origin();
            if (surfaceImage[i] == null) {
                surfaceImage[i] = IplImage.create(w, h, d, c, o);
            } else {
                cvResetImageROI(surfaceImage[i]);
            }
            cvPyrDown(surfaceImage[i-1], surfaceImage[i], CV_GAUSSIAN_5x5);
        }
    }

    protected void prepareTransforms(CvMat H1, CvMat H2, CvMat X, int pyramidLevel, Parameters p) {
        ProjectiveColorTransformer.Parameters cameraParameters    = p.getSurfaceParameters();
        ProjectiveColorTransformer.Parameters projectorParameters = p.getProjectorParameters();

        cvInvert(cameraParameters.getH(), H1);
        cvInvert(projectorParameters.getH(), H2);

        // adjust the scale of the transformation based on the pyramid level
        if (pyramidLevel > 0) {
            int scale = 1<<pyramidLevel;
            H1.put(2, H1.get(2)/scale);
            H1.put(5, H1.get(5)/scale);
            H1.put(6, H1.get(6)*scale);
            H1.put(7, H1.get(7)*scale);

            H2.put(2, H2.get(2)/scale);
            H2.put(5, H2.get(5)/scale);
            H2.put(6, H2.get(6)*scale);
            H2.put(7, H2.get(7)*scale);
        }

        double[] x = projector.colorMixingMatrix.get();
        double[] a = projectorParameters.getColorParameters();
        double a2 = a[0];
        X.put(a2*x[0], a2*x[1], a2*x[2], a[1],
              a2*x[3], a2*x[4], a2*x[5], a[2],
              a2*x[6], a2*x[7], a2*x[8], a[3],
              0, 0, 0, 1);
    }

    public void transform(final IplImage srcImage, final IplImage dstImage, final CvRect roi,
            final int pyramidLevel, final ImageTransformer.Parameters parameters, final boolean inverse) {
        if (inverse) {
            throw new UnsupportedOperationException("Inverse transform not supported.");
        }
        final Parameters p = ((Parameters)parameters);
        final ProjectiveTransformer.Parameters cameraParameters    = p.getSurfaceParameters();
        final ProjectiveTransformer.Parameters projectorParameters = p.getProjectorParameters();

        if (p.tempImage == null || p.tempImage.length <= pyramidLevel) {
            p.tempImage = new IplImage[pyramidLevel+1];
        }
        p.tempImage[pyramidLevel] = IplImage.createIfNotCompatible(p.tempImage[pyramidLevel], dstImage);
        if (roi == null) {
            cvResetImageROI(p.tempImage[pyramidLevel]);
        } else {
            cvSetImageROI(p.tempImage[pyramidLevel], roi);
        }

//        Parallel.run(new Runnable() { public void run() {
            // warp the template image
            surfaceTransformer.transform(srcImage, p.tempImage[pyramidLevel], roi, pyramidLevel, cameraParameters, false);
//        }}, new Runnable() { public void run() {
            // warp the projector image
            projectorTransformer.transform(projectorImage[pyramidLevel], dstImage, roi, pyramidLevel, projectorParameters, false);
//        }});

        // multiply projector image with template image
        cvMul(dstImage, p.tempImage[pyramidLevel], dstImage, 1/dstImage.highValue());
    }

    public void transform(CvMat srcPts, CvMat dstPts, ImageTransformer.Parameters parameters, boolean inverse) {
        surfaceTransformer.transform(srcPts, dstPts, ((Parameters)parameters).surfaceParameters, inverse);
    }

    public void transform(Data[] data, CvRect roi, ImageTransformer.Parameters[] parameters, boolean[] inverses) {
        assert data.length == parameters.length;
        if (kernelData == null || kernelData.capacity() < data.length) {
            kernelData = new KernelData(data.length);
        }
        if (H1 == null || H1.length < data.length) {
            H1 = new CvMat[data.length];
            for (int i = 0; i < H1.length; i++) {
                H1[i] = CvMat.create(3, 3);
            }
        }
        if (H2 == null || H2.length < data.length) {
            H2 = new CvMat[data.length];
            for (int i = 0; i < H2.length; i++) {
                H2[i] = CvMat.create(3, 3);
            }
        }
        if (X == null || X.length < data.length) {
            X = new CvMat[data.length];
            for (int i = 0; i < X.length; i++) {
                X[i] = CvMat.create(4, 4);
            }
        }

        for (int i = 0; i < data.length; i++) {
            kernelData.position(i);

            kernelData.srcImg2(data[i].srcImg);
            kernelData.srcImg(projectorImage[data[i].pyramidLevel]);
            kernelData.subImg(data[i].subImg);
            kernelData.srcDotImg(data[i].srcDotImg);
            kernelData.mask(data[i].mask);
            kernelData.zeroThreshold(data[i].zeroThreshold);
            kernelData.outlierThreshold(data[i].outlierThreshold);

            prepareTransforms(H1[i], H2[i], X[i], data[i].pyramidLevel, (Parameters)parameters[i]);

            kernelData.H1(H2[i]);
            kernelData.H2(H1[i]);
            kernelData.X (X [i]);

            kernelData.transImg(data[i].transImg);
            kernelData.dstImg(data[i].dstImg);
            kernelData.dstDstDot(data[i].dstDstDot);
        }

        int fullCapacity = kernelData.capacity();
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

//        if (data[0].dstCountZero > 0) {
//            System.err.println(data[0].dstCountZero + " out of " + data[0].dstCount
//                    + " are zero = " + 100*data[0].dstCountZero/data[0].dstCount + "%");
//        }
    }

    public Parameters createParameters() {
        return new Parameters();
    }

    public class Parameters implements ImageTransformer.Parameters {
        protected Parameters() {
            reset(false);
        }
        protected Parameters(ProjectiveColorTransformer.Parameters surfaceParameters,
                             ProjectiveColorTransformer.Parameters projectorParameters) {
            reset(surfaceParameters, projectorParameters);
        }

        private ProjectiveColorTransformer.Parameters surfaceParameters = null;
        private ProjectiveColorTransformer.Parameters projectorParameters = null;
        private IplImage[] tempImage = null;
        private CvMat H = CvMat.create(3, 3), R = CvMat.create(3, 3),
                      n = CvMat.create(3, 1), t = CvMat.create(3, 1);

        public ProjectiveColorTransformer.Parameters getSurfaceParameters() {
            return surfaceParameters;
        }
        public ProjectiveColorTransformer.Parameters getProjectorParameters() {
            return projectorParameters;
        }

        private int getSizeForSurface() {
            return surfaceParameters.size() - surfaceTransformer.getNumGains() - surfaceTransformer.getNumBiases();
        }
        private int getSizeForProjector() {
            return projectorParameters.size();
        }
        public int size() {
            return getSizeForSurface() + getSizeForProjector();
        }
        public double[] get() {
            double[] p = new double[size()];
            for (int i = 0; i < p.length; i++) {
                p[i] = get(i);
            }
            return p;
        }
        public double get(int i) {
            if (i < getSizeForSurface()) {
                return surfaceParameters.get(i);
            } else {
                return projectorParameters.get(i-getSizeForSurface());
            }
        }
        public void set(double ... p) {
            for (int i = 0; i < p.length; i++) {
                set(i, p[i]);
            }
        }
        public void set(int i, double p) {
            if (i < getSizeForSurface()) {
                surfaceParameters.set(i, p);
            } else {
                projectorParameters.set(i-getSizeForSurface(), p);
            }
        }
        public void set(ImageTransformer.Parameters p) {
            Parameters pcp = (Parameters)p;
            surfaceParameters.set(pcp.getSurfaceParameters());
            projectorParameters.set(pcp.getProjectorParameters());

            surfaceParameters.resetColor(false);
        }
        public void reset(boolean asIdentity) {
            reset(null, null);
        }
        public void reset(ProjectiveColorTransformer.Parameters surfaceParameters,
                          ProjectiveColorTransformer.Parameters projectorParameters) {
            if (surfaceParameters == null) {
                surfaceParameters = surfaceTransformer.createParameters();
            }
            if (projectorParameters == null) {
                projectorParameters = projectorTransformer.createParameters();
            }
            this.surfaceParameters   = surfaceParameters;
            this.projectorParameters = projectorParameters;

            setSubspace(getSubspace());
        }
//        public boolean addDelta(int i) {
//            return addDelta(i, 1);
//        }
//        public boolean addDelta(int i, double scale) {
//            // gradient varies linearly with intensity, so
//            // the increment value is not very important, but
//            // referenceCameraImage is good only for the value 1,
//            // so let's use that
//            if (i < getSizeForSurface()) {
//                surfaceParameters.addDelta(i, scale);
//                projectorParameters.setUpdateNeeded(true);
//            } else {
//                projectorParameters.addDelta(i-getSizeForSurface(), scale);
//            }
//
//            return false;
//        }
        public double getConstraintError() {
            double error = surfaceParameters.getConstraintError();
            projectorParameters.update();
            return error;
        }
        public void compose(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            throw new UnsupportedOperationException("Compose operation not supported.");
        }

        public boolean preoptimize() {
            double[] p = setSubspaceInternal(getSubspaceInternal());
            if (p != null) {
                set(8, p[8]);
                set(9, p[9]);
                set(10, p[10]);
                return true;
            }
            return false;
        }
        public void setSubspace(double ... p) {
            double[] dst = setSubspaceInternal(p);
            if (dst != null) {
                set(dst);
            }
        }
        public double[] getSubspace() {
            return getSubspaceInternal();
        }

        private double[] setSubspaceInternal(double ... p) {
            if (invFrontoParallelH == null) {
                return null;
            }
            double[] dst = new double[8+3];
            t.put(p[0], p[1], p[2]);
            cvRodrigues2(t, R, null);
            t.put(p[3], p[4], p[5]);

            // compute new H
            H.put(R.get(0), R.get(1), t.get(0),
                  R.get(3), R.get(4), t.get(1),
                  R.get(6), R.get(7), t.get(2));
            cvMatMul(H, invFrontoParallelH, H);
            cvMatMul(surfaceTransformer.getK2(), H,    H);
            cvMatMul(H, surfaceTransformer.getInvK1(), H);

            // compute new n, rotation from the z-axis
            cvGEMM(R, t, 1,  null, 0,  t, CV_GEMM_A_T);
            double scale = 1/t.get(2);
            n.put(0.0, 0.0, 1.0);
            cvGEMM(R, n, scale, null, 0, n, 0);

            // compute and set new three points
            double[] src = projectorTransformer.getReferencePoints2();
            JavaCV.perspectiveTransform(src, dst,
                    projectorTransformer.getInvK1(),projectorTransformer.getK2(),
                    projectorTransformer.getR(), projectorTransformer.getT(), n, true);
            dst[8]  = dst[0];
            dst[9]  = dst[2];
            dst[10] = dst[4];

            // compute and set new four points
            JavaCV.perspectiveTransform(surfaceTransformer.getReferencePoints1(), dst, H);

            return dst;
        }

        private double[] getSubspaceInternal() {
            if (frontoParallelH == null) {
                return null;
            }
            cvMatMul(surfaceTransformer.getK1(),    frontoParallelH, H);
            cvMatMul(surfaceParameters .getH(),     H, H);
            cvMatMul(surfaceTransformer.getInvK2(), H, H);

            JavaCV.HtoRt(H, R, t);
            cvRodrigues2(R, n, null);
            double[] p = { n.get(0), n.get(1), n.get(2),
                           t.get(0), t.get(1), t.get(2) };
            return p;
        }

        public CvMat getSrcN() {
            double[] src = projectorTransformer.getReferencePoints2();
            double[] dst = projectorTransformer.getReferencePoints1().clone();
            dst[0] = projectorParameters.get(0);
            dst[2] = projectorParameters.get(1);
            dst[4] = projectorParameters.get(2);

            // get plane parameters n, but since we model the target to be
            // the camera, we have to inverse everything before calling
            // getPlaneParameters() and reframe the n it returns
            cvTranspose(projectorTransformer.getR(), R);
            cvGEMM(R, projectorTransformer.getT(), -1, null, 0, t, 0);
            JavaCV.getPlaneParameters(src, dst, projectorTransformer.getInvK2(),
                    projectorTransformer.getK1(), R, t, n);
            double d = 1 + cvDotProduct(n, projectorTransformer.getT());
            cvGEMM(R, n, 1/d,  null, 0,  n, 0);

            // remove projective effect of the current n,
            // leaving only the effect of the "source" n
            camera.getFrontoParallelH(surfaceParameters.get(), n, R);
            cvInvert(surfaceParameters.getH(), H);
            cvMatMul(H, surfaceTransformer.getK2(), H);
            cvMatMul(H, R, H);
            cvMatMul(surfaceTransformer.getInvK1(), H, H);

            JavaCV.HtoRt(H, R, t);

            // compute "source" n, rotation from the z-axis
            cvGEMM(R, t, 1,  null, 0,  t, CV_GEMM_A_T);
            double scale = 1/t.get(2);
            n.put(0.0, 0.0, 1.0);
            cvGEMM(R, n, scale, null, 0, n, 0);

            return n;
        }

        @Override public Parameters clone() {
            Parameters p = new Parameters();
            p.surfaceParameters   = surfaceParameters.clone();
            p.projectorParameters = projectorParameters.clone();
            return p;
        }

        @Override public String toString() {
            return surfaceParameters.toString() + projectorParameters.toString();
        }
    }
}
