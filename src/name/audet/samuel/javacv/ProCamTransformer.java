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

import static name.audet.samuel.javacv.jna.cvkernels.*;
import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

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

        this.surfaceTransformer = new SurfaceTransformer(camera, n, referencePoints);
        this.projectorTransformer = new ProjectorTransformer(camera, projector);
    }

    private CameraDevice camera = null;
    private ProjectorDevice projector = null;
    private SurfaceTransformer surfaceTransformer = null;
    private ProjectorTransformer projectorTransformer = null;
    private IplImage[] projectorImage = null, surfaceImage = null;
    private CvScalar.ByValue fillColor = cvScalar(0.0, 0.0, 0.0, 1.0);
    private CvRect.ByValue roi = new CvRect.ByValue();

    public CvScalar.ByValue getFillColor() {
        return fillColor;
    }
    public void setFillColor(CvScalar fillColor) {
        this.fillColor = fillColor.byValue();
    }

    public SurfaceTransformer getSurfaceTransformer() {
        return surfaceTransformer;
    }
    public ProjectorTransformer getProjectorTransformer() {
        return projectorTransformer;
    }

    public IplImage getProjectorImage(int pyramidLevel) {
        return projectorImage[pyramidLevel];
    }
    public void setProjectorImage(IplImage projectorImage0, int pyramidLevels) {
        if (projectorImage == null || projectorImage.length != pyramidLevels) {
            projectorImage = new IplImage[pyramidLevels];
        }

        if (projectorImage0.depth == IPL_DEPTH_32F) {
            projectorImage[0] = projectorImage0;
        } else {
            if (projectorImage[0] == null) {
                projectorImage[0] = IplImage.create(projectorImage0.width, projectorImage0.height,
                        IPL_DEPTH_32F, projectorImage0.nChannels, projectorImage0.origin);
            }
            IplROI ir = projectorImage0.roi;
            if (ir != null) {
                int align  = 1<<projectorImage.length;
                roi.x      = (int)Math.floor((double)ir.xOffset/align)*align;
                roi.y      = (int)Math.floor((double)ir.yOffset/align)*align;
                roi.width  = (int)Math.ceil ((double)ir.width  /align)*align;
                roi.height = (int)Math.ceil ((double)ir.height /align)*align;
                cvSetImageROI(projectorImage0,   roi);
                cvSetImageROI(projectorImage[0], roi);
            } else {
                cvResetImageROI(projectorImage0);
                cvResetImageROI(projectorImage[0]);
            }
            cvConvertScale(projectorImage0, projectorImage[0], 1.0/255.0, 0);
        }

//        CvScalar.ByValue average = cvAvg(projectorImage[0], null);
//        cvSubS(projectorImage[0], average, projectorImage[0], null);

        for (int i = 1; i < pyramidLevels; i++) {
            int w = projectorImage[i-1].width/2;
            int h = projectorImage[i-1].height/2;
            int d = projectorImage[i-1].depth;
            int c = projectorImage[i-1].nChannels;
            int o = projectorImage[i-1].origin;
            if (projectorImage[i] == null) {
                projectorImage[i] = IplImage.create(w, h, d, c, o);
            }

            IplROI ir = projectorImage[i-1].roi;
            if (ir != null) {
                roi.x = ir.xOffset/2; roi.width  = ir.width /2;
                roi.y = ir.yOffset/2; roi.height = ir.height/2;
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
            int w = surfaceImage[i-1].width/2;
            int h = surfaceImage[i-1].height/2;
            int d = surfaceImage[i-1].depth;
            int c = surfaceImage[i-1].nChannels;
            int o = surfaceImage[i-1].origin;
            if (surfaceImage[i] == null) {
                surfaceImage[i] = IplImage.create(w, h, d, c, o);
            } else {
                cvResetImageROI(surfaceImage[i]);
            }
            cvPyrDown(surfaceImage[i-1], surfaceImage[i], CV_GAUSSIAN_5x5);
        }
    }

    protected void prepareHomographyTransform(CvMat H1, CvMat H2, CvMat X, int pyramidLevel, Parameters p) {
        ProjectiveTransformer.Parameters cameraParameters    = p.getSurfaceParameters();
        ProjectiveTransformer.Parameters projectorParameters = p.getProjectorParameters();

        cvInvert(cameraParameters.getH(), H1);
        cvCopy(projectorParameters.getH(), H2);

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
        double[] a = projectorParameters.get();
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

        if (p.warpedTemplateImage == null || p.warpedTemplateImage.length <= pyramidLevel) {
            p.warpedTemplateImage = new IplImage[pyramidLevel+1];
        }
        p.warpedTemplateImage[pyramidLevel] = IplImage.createIfNotCompatible(p.warpedTemplateImage[pyramidLevel], dstImage);
        if (roi == null) {
            cvResetImageROI(p.warpedTemplateImage[pyramidLevel]);
        } else {
            cvSetImageROI(p.warpedTemplateImage[pyramidLevel], roi.byValue());
        }

//        Parallel.run(new Runnable() { public void run() {
            // warp the template image
            surfaceTransformer.transform(srcImage, p.warpedTemplateImage[pyramidLevel], roi, pyramidLevel, cameraParameters, false);
//        }}, new Runnable() { public void run() {
            // warp the projector image
            projectorTransformer.transform(projectorImage[pyramidLevel], dstImage, roi, pyramidLevel, projectorParameters, false);
//        }});

        // multiply projector image with template image
        cvMul(dstImage, p.warpedTemplateImage[pyramidLevel], dstImage, 1/dstImage.getMaxIntensity());
    }

    public void transform(CvMat srcPts, CvMat dstPts, ImageTransformer.Parameters parameters, boolean inverse) {
        surfaceTransformer.transform(srcPts, dstPts, ((Parameters)parameters).surfaceParameters, inverse);
    }

    public void transform(Data[] data, IplImage mask, CvRect roi, double zeroThreshold,
            int pyramidLevel, ImageTransformer.Parameters[] parameters) {
        assert (data.length == parameters.length);
        boolean allOK = true;
        for (int i = 0; i < data.length; i++) {
            Data d = data[i];
            if (d.inverse) {
                throw new UnsupportedOperationException("Inverse transform not supported.");
            }
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
                CvMat[] H1, H2, X;
            }
            Cache cache = data[0].cache instanceof Cache ?
                   (Cache)data[0].cache : new Cache();
            if (cache.kernelData == null || cache.kernelData.length != data.length ||
                        cache.H1 == null ||         cache.H1.length != data.length ||
                        cache.H2 == null ||         cache.H2.length != data.length ||
                        cache.X  == null ||         cache.X .length != data.length) {
                data[0].cache = cache;
                cache.kernelData = (MultiWarpColorTransformData[])
                        new MultiWarpColorTransformData().toArray(data.length);
                cache.H1 = new CvMat[data.length];
                cache.H2 = new CvMat[data.length];
                cache.X  = new CvMat[data.length];
            }
            MultiWarpColorTransformData[] kd = cache.kernelData;

            for (int i = 0; i < data.length; i++) {
                Data d = data[i];
                kd[i].srcImg    = d.srcImg    == null ? null : d.srcImg   .getPointer();
                kd[i].srcImg2   = projectorImage[pyramidLevel].getPointer();
                kd[i].subImg    = d.subImg    == null ? null : d.subImg   .getPointer();
                kd[i].srcDotImg = d.srcDotImg == null ? null : d.srcDotImg.getPointer();

                CvMat H1 = cache.H1[i] == null ? cache.H1[i] = CvMat.create(3, 3) : cache.H1[i];
                CvMat H2 = cache.H2[i] == null ? cache.H2[i] = CvMat.create(3, 3) : cache.H2[i];
                CvMat X  = cache.X[i]  == null ? cache.X [i] = CvMat.create(4, 4) : cache.X [i];

                prepareHomographyTransform(H1, H2, X, pyramidLevel, (Parameters)parameters[i]);

                kd[i].H1 = H1.getPointer();
                kd[i].H2 = H2.getPointer();
                kd[i].X  = X .getPointer();

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

//            if (data[0].dstCountZero > 0) {
//                System.err.println(data[0].dstCountZero + " out of " + data[0].dstCount
//                        + " are zero = " + 100*data[0].dstCountZero/data[0].dstCount + "%");
//            }
        }
    }

    public Parameters createParameters() {
        return new Parameters();
    }

    public class Parameters implements ImageTransformer.Parameters {
        protected Parameters() {
            reset(false);
        }
        protected Parameters(SurfaceTransformer  .Parameters surfaceParameters,
                             ProjectorTransformer.Parameters projectorParameters) {
            reset(surfaceParameters, projectorParameters);
        }

        private SurfaceTransformer.Parameters surfaceParameters = null;
        private ProjectorTransformer.Parameters projectorParameters = null;
        private IplImage[] warpedTemplateImage = null;

        public SurfaceTransformer.Parameters getSurfaceParameters() {
            return surfaceParameters;
        }
        public ProjectorTransformer.Parameters getProjectorParameters() {
            return projectorParameters;
        }

        private int getSizeForSurface() {
            return surfaceParameters.size() - surfaceTransformer.getNumGains() - surfaceTransformer.getNumBiases();
        }
        private int getSizeForProjector() {
            return projectorTransformer.getNumGains() + projectorTransformer.getNumBiases();
        }
        private int getProjectorOffset() {
            return projectorParameters.size() - size();
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
                return projectorParameters.get(getProjectorOffset()+i);
            }
        }
        public void set(double ... p) {
            for (int i = 0; i < p.length; i++) {
                set(i, p[i]);
            }
        }
        public void set(int i, double p) {
            if (i < getSizeForSurface()) {
                if (surfaceParameters.get(i) != p) {
                    surfaceParameters.set(i, p);
                    projectorParameters.setUpdateNeeded(true);
                }
            } else {
                projectorParameters.set(getProjectorOffset()+i, p);
            }
        }
        public void set(ImageTransformer.Parameters p) {
            Parameters pcp = null;
            if (p instanceof Parameters) {
                pcp = (Parameters)p;
            } else if (p instanceof HasProCamParameters) {
                pcp = ((HasProCamParameters)p).getProCamParameters();
            }
            surfaceParameters.superSet(pcp.getSurfaceParameters());
            projectorParameters.superSet(pcp.getProjectorParameters());
            projectorParameters.setUpdateNeeded(surfaceParameters.isUpdateNeeded());

            if (!(p instanceof SurfaceTransformer.Parameters)) {
                surfaceParameters.resetGainBias(false);
            }
        }
        public void reset(boolean asIdentity) {
            reset(null, null);
        }
        public void reset(SurfaceTransformer  .Parameters surfaceParameters,
                          ProjectorTransformer.Parameters projectorParameters) {
            if (surfaceParameters == null) {
                surfaceParameters = surfaceTransformer.new Parameters(this);
            }
            if (projectorParameters == null) {
                projectorParameters = projectorTransformer.new Parameters(this);
            }
            this.surfaceParameters   = surfaceParameters;
            this.projectorParameters = projectorParameters;
        }
        public boolean addDelta(int i) {
            // gradient varies linearly with intensity, so
            // the increment value is not very important, but
            // referenceCameraImage is good only for the value 1,
            // so let's use that
            if (i < getSizeForSurface()) {
                surfaceParameters.addDelta(i);
                projectorParameters.setUpdateNeeded(true);
            } else {
                projectorParameters.addDelta(getProjectorOffset()+i);
            }

            return false;
        }
        public double getConstraintError() {
            double error = surfaceParameters.getConstraintError();
            projectorParameters.update();
            return error;
        }
        public void compose(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            throw new UnsupportedOperationException("Compose operation not supported.");
        }

        public CvMat getN2() {
            return projectorTransformer.getN();
        }

        @Override public Parameters clone() {
            Parameters p = new Parameters();
            p.surfaceParameters   = surfaceTransformer  .new Parameters(p);
            p.projectorParameters = projectorTransformer.new Parameters(p);
            p.surfaceParameters  .superSet(surfaceParameters);
            p.projectorParameters.superSet(projectorParameters);
            return p;
        }

        @Override public String toString() {
            if (true) {
                return surfaceParameters.toString() + projectorParameters.toString();
            } else {
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

    public interface HasProCamParameters {
        ProCamTransformer.Parameters getProCamParameters();
    }

    public class SurfaceTransformer extends ProjectiveGainBiasTransformer {
        protected SurfaceTransformer(CameraDevice camera, CvMat n, double[] referencePoints) {
            super(camera.cameraMatrix, camera.cameraMatrix, null, null,
                    n, referencePoints, null, 3, 0);
        }

        @Override public Parameters createParameters() {
            return new Parameters();
        }

        public class Parameters extends ProjectiveGainBiasTransformer.Parameters implements HasProCamParameters {
            protected Parameters(ProCamTransformer.Parameters proCamParameters) {
                this.proCamParameters = proCamParameters;
                reset(false);
            }
            protected Parameters() {
                this.proCamParameters = new ProCamTransformer.Parameters(this, null);
                reset(false);
            }

            private ProCamTransformer.Parameters proCamParameters = null;
            public ProCamTransformer.Parameters getProCamParameters() {
                return proCamParameters;
            }

            private void superSet(ImageTransformer.Parameters p) {
                super.set(p);
            }
            @Override public void set(ImageTransformer.Parameters p) {
                proCamParameters.set(p);
            }
            @Override public Parameters clone() {
                ProCamTransformer.Parameters p = proCamParameters.clone();
                return p.surfaceParameters;
            }
        }
    }


    public class ProjectorTransformer extends ProjectiveGainBiasTransformer {
        // assuming the camera has identity values, use the projector's stuff directly
        // this ImageTransformer has the projector as source device, so we need to inverse
        // the rotation and the translation..
        protected ProjectorTransformer(CameraDevice camera, ProjectorDevice projector) {
            super(camera.cameraMatrix, projector.cameraMatrix, projector.R,
                    projector.T, null, null, projector.colorMixingMatrix, 1, 3);
        }

        @Override public void transform(IplImage srcImage, IplImage dstImage,
                CvRect roi, int pyramidLevel, ImageTransformer.Parameters parameters, boolean inverse) {
            super.transform(srcImage, dstImage, roi, pyramidLevel, parameters, !inverse);
            super.transformGainBias(dstImage, dstImage, roi, pyramidLevel, parameters, inverse);
        }

        @Override public Parameters createParameters() {
            return new Parameters();
        }

        public class Parameters extends ProjectiveGainBiasTransformer.Parameters implements HasProCamParameters {
            protected Parameters(ProCamTransformer.Parameters proCamParameters) {
                this.proCamParameters = proCamParameters;
                reset(false);
            }
            protected Parameters() {
                this.proCamParameters = new ProCamTransformer.Parameters(null, this);
                reset(false);
            }

            private ProCamTransformer.Parameters proCamParameters = null;
            public ProCamTransformer.Parameters getProCamParameters() {
                return proCamParameters;
            }

            @Override public int size() {
                return getNumGains() + getNumBiases();
            }
            @Override public double get(int i) {
                return super.get(super.size()-size()+i);
            }
            @Override public void set(int i, double p) {
                super.set(super.size()-size()+i, p);
            }
            private void superSet(ImageTransformer.Parameters p) {
                super.set(p);
            }
            @Override public void set(ImageTransformer.Parameters p) {
                proCamParameters.set(p);
            }
            @Override public void reset(boolean asIdentity) {
                super.reset(asIdentity);
                if (proCamParameters != null && proCamParameters.surfaceParameters != null) {
                    proCamParameters.surfaceParameters.reset(asIdentity);
                    setUpdateNeeded(true);
                }
            }
            @Override public boolean addDelta(int i) {
                return super.addDelta(super.size()-size()+i);
            }

            private CvMat n = CvMat.create(3, 1);
            @Override protected void update() {
                if (!isUpdateNeeded()) {
                    return;
                }

                CvMat n1 = proCamParameters.surfaceParameters.getN();
                CvMat R  = proCamParameters.surfaceParameters.getR();
                CvMat t  = proCamParameters.surfaceParameters.getT();

                // transform the initial normal n1 using motion derived from the homography
                // n^T = n1^T [R^T | -t]
                // n   = (R*n1) / (1 - t^T*n1)
                double d = 1 - cvDotProduct(t, n1);
                cvGEMM(R, n1, 1/d, null, 0, n, 0);
                n.get(projectiveParameters);

                super.update();
                setUpdateNeeded(false);
            }

            @Override public Parameters clone() {
                ProCamTransformer.Parameters p = proCamParameters.clone();
                return p.projectorParameters;
            }
        }
    }


}
