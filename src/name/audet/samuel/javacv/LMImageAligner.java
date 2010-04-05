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

import name.audet.samuel.javacv.ImageTransformer.Data;
import name.audet.samuel.javacv.ImageTransformer.Parameters;
import name.audet.samuel.javacv.Parallel.Looper;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

/**
 *
 * @author Samuel Audet
 */
public class LMImageAligner implements ImageAligner {
    public LMImageAligner(ImageTransformer transformer, Parameters initialParameters,
            IplImage template0, double[] roiPts, IplImage target0) {
        this(transformer, initialParameters, template0, roiPts, target0, new Settings());
    }
    public LMImageAligner(ImageTransformer transformer, Parameters initialParameters, 
            IplImage template0, double[] roiPts, IplImage target0, Settings settings) {
        setSettings(settings);

        int n = initialParameters.size();

        this.template = new IplImage[settings.pyramidLevels];
        this.target   = new IplImage[settings.pyramidLevels];
        this.warped   = new IplImage[settings.pyramidLevels];
        this.residual = new IplImage[settings.pyramidLevels];
        this.roiMask  = new IplImage[settings.pyramidLevels];
        int w = template0.width;
        int h = template0.height;
        int c = template0.nChannels;
        int o = template0.origin;
        for (int i = 0; i < settings.pyramidLevels; i++) {
            if (i == 0 && template0.depth == IPL_DEPTH_32F) {
                template[0] = template0;
            } else {
                template[i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            }
            if (i == 0 && target0.depth == IPL_DEPTH_32F) {
                target[0] = target0;
            } else {
                target[i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            }
            warped  [i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            residual[i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            roiMask [i] = IplImage.create(w, h, IPL_DEPTH_8U,  1, o);
            w /= 2;
            h /= 2;
        }
        this.srcRoiPts = CvMat.create(4, 1, CV_64F, 2);
        this.dstRoiPts = CvMat.create(4, 1, CV_64F, 2);
        this.dstRoiPtsArray = CvPoint.createArray(4);
        this.roi       = new CvRect.ByValue();

        int numThreads = Parallel.numCores;
        this.subroi    = new CvRect.ByValue[numThreads];
        for (int i = 0; i < subroi.length; i++) {
            this.subroi[i] = new CvRect.ByValue();
        }
        this.transformer = transformer;
        this.hessianGradientTransformerData = new Data[numThreads][n];
        for (int i = 0; i < hessianGradientTransformerData.length; i++) {
            for (int j = 0; j < hessianGradientTransformerData[i].length; j++) {
                hessianGradientTransformerData[i][j] = new Data(template[pyramidLevel],
                        warped[pyramidLevel], residual[pyramidLevel], null, null, n);
            }
        }
        this.residualTransformerData = new Data[numThreads][1];
        for (int i = 0; i < residualTransformerData.length; i++) {
            residualTransformerData[i][0] = new Data(template[pyramidLevel],
                    target[pyramidLevel], null, warped[pyramidLevel], residual[pyramidLevel], 1);
        }

        this.parameters      = initialParameters.clone();
        this.parametersArray = new Parameters[] { parameters };
        this.tempParameters  = new Parameters[n];
        for (int i = 0; i < tempParameters.length; i++) {
            this.tempParameters[i] = initialParameters.clone();
        }

        setConstrained(settings.constrained);
        setTemplateImage(template0, roiPts);
        setTargetImage(target0);
    }

    public static class Settings extends ImageAligner.Settings implements Cloneable {
        public Settings() { }
        public Settings(Settings s) {
            super(s);
            zeroThresholds   = s.zeroThresholds;
            LMLambdas        = s.LMLambdas;
            errorDecreaseMin = s.errorDecreaseMin;
            displacementMax  = s.displacementMax;
        }

        double[] zeroThresholds = {1.0, 0.75, 0.5, 0.25, 0.0};
        double[] LMLambdas      = {0.0, 1.0};
        double errorDecreaseMin = 0.02;
        double displacementMax  = 0.15;

        public double[] getZeroThresholds() {
            return zeroThresholds;
        }
        public void setZeroThresholds(double[] zeroThresholds) {
            this.zeroThresholds = zeroThresholds;
        }

        public double[] getLMLambdas() {
            return LMLambdas;
        }
        public void setLMLambdas(double[] LMLambdas) {
            this.LMLambdas = LMLambdas;
        }

        public double getErrorDecreaseMin() {
            return errorDecreaseMin;
        }
        public void setErrorDecreaseMin(double errorDecreaseMin) {
            this.errorDecreaseMin = errorDecreaseMin;
        }

        public double getDisplacementMax() {
            return displacementMax;
        }
        public void setDisplacementMax(double displacementMax) {
            this.displacementMax = displacementMax;
        }

        @Override public Settings clone() {
            return new Settings(this);
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    private IplImage[] template, target, warped, residual, roiMask;
    private CvMat srcRoiPts, dstRoiPts;
    private CvPoint[] dstRoiPtsArray;
    private CvRect.ByValue roi, subroi[];
    private ImageTransformer transformer;
    private Data[][] hessianGradientTransformerData, residualTransformerData;
    private Parameters parameters, parametersArray[], tempParameters[];
    private CvMat hessian, dampedHessian, gradient, update, prior;
    private double[] updateScale;
    private int pyramidLevel, lmLambdaIndex = 0;
    private double RMSE;
    private boolean residualUpdateNeeded = true;

    public IplImage getTemplateImage() {
        return template[pyramidLevel];
    }
    public void setTemplateImage(IplImage template0, double[] roiPts) {
        this.srcRoiPts.put(roiPts);

        if (template0.depth == IPL_DEPTH_32F) {
            template[0] = template0;
        } else {
            cvConvertScale(template0, template[0], 1.0/255.0, 0);
        }

        for (int i = 1; i < template.length; i++) {
            cvPyrDown(template[i-1], template[i], CV_GAUSSIAN_5x5);
        }
        setPyramidLevel(template.length-1);
    }

    public IplImage getTargetImage() {
        return target[pyramidLevel];
    }
    public void setTargetImage(IplImage target0) {
        if (target0.depth == IPL_DEPTH_32F) {
            target[0] = target0;
        }

        if (settings.displacementMax > 0) {
            setPyramidLevel(0);
            doRoi(settings.displacementMax);
            int align = 1<<target.length;
            subroi[0].x      = (int)Math.floor((double)roi.x     /align)*align;
            subroi[0].y      = (int)Math.floor((double)roi.y     /align)*align;
            subroi[0].width  = (int)Math.ceil ((double)roi.width /align)*align;
            subroi[0].height = (int)Math.ceil ((double)roi.height/align)*align;
            cvSetImageROI(target0,   subroi[0]);
            cvSetImageROI(target[0], subroi[0]);
        } else {
            cvResetImageROI(target0);
            cvResetImageROI(target[0]);
        }

        if (target0.depth != IPL_DEPTH_32F) {
            cvConvertScale(target0, target[0], 1.0/255.0, 0);
            cvResetImageROI(target0);
        }

        for (int i = 1; i < target.length; i++) {
            IplROI ir = target[i-1].roi;
            if (ir != null) {
                subroi[0].x = ir.xOffset/2; subroi[0].width  = ir.width /2;
                subroi[0].y = ir.yOffset/2; subroi[0].height = ir.height/2;
                cvSetImageROI(target[i], subroi[0]);
            } else {
                cvResetImageROI(target[i]);
            }
            cvPyrDown(target[i-1], target[i], CV_GAUSSIAN_5x5);
        }

        setPyramidLevel(target.length-1);
    }

    public int getPyramidLevel() {
        return pyramidLevel;
    }
    public void setPyramidLevel(int pyramidLevel) {
        this.pyramidLevel = pyramidLevel;
        residualUpdateNeeded = true;
    }

    public boolean isConstrained()  {
        return settings.constrained;
    }
    public void setConstrained(boolean constrained) {
        if (settings.constrained == constrained && hessian != null &&
                dampedHessian != null && gradient != null && update != null) {
            return;
        }
        settings.constrained = constrained;
        int n = parameters.size();
        int m = constrained ? n+1 : n;
        hessian       = CvMat.create(m, m);
        dampedHessian = CvMat.create(m, m);
        gradient      = CvMat.create(m, 1);
        update        = CvMat.create(m, 1);
        updateScale   = new double[m];
    }

    public int getLmLambdaIndex() {
        return lmLambdaIndex;
    }
    public void setLmLambdaIndex(int lmLambdaIndex) {
        this.lmLambdaIndex = lmLambdaIndex;
    }

    public Parameters getParameters() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return parameters;
    }
    public void setParameters(Parameters parameters) {
        this.parameters.set(parameters);
        residualUpdateNeeded = true;
    }

    public IplImage getWarpedImage() {
        return warped[pyramidLevel];
    }
    public IplImage getResidualImage() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return residual[pyramidLevel];
    }
    public IplImage getRoiMaskImage() {
        return roiMask[pyramidLevel];
    }

    public double getRMSE() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return RMSE;
    }

    public int getPixelCount() {
        int dstCount = 0;
        for (Data[] data : residualTransformerData) {
            dstCount += data[0].dstCount;
        }
        return dstCount;
    }

long accTime = 0;
    public boolean iterate(double[] delta) {
        boolean converged = false;
        final int n = parameters.size();
        final double prevRMSE = getRMSE();
        final double[] prevParameters = parameters.get();

//long start = System.currentTimeMillis();
        if (lmLambdaIndex == 0) {
            doHessianGradient(updateScale);
        }

//System.err.println((float)(hessian.get(9,9) + hessian.get(10,10) + hessian.get(11,11)) /
//                    (hessian.get(8,9) + hessian.get(8,10) + hessian.get(8,11)));
//System.out.println("condition number: " + JavaCV.cond(hessian, 2));
//long gradientHessianTime = System.currentTimeMillis();

        // add Levenberg-Marquardt damping factor + Tikonov regularization gammaTgamma
        for (int i = 0; i < hessian.rows; i++) {
            for (int j = 0; j < hessian.cols; j++) {
                double h = hessian.get(i, j);
                if (i == j) {
                    h *= 1+settings.LMLambdas[lmLambdaIndex];
                }
                double g = 0; 
                if (settings.gammaTgamma != null && i < settings.gammaTgamma.rows && j < settings.gammaTgamma.cols) {
                    g = settings.gammaTgamma.get(i, j);
                }
                double a = 0;
                if (i == j && i < n) {
                    a = settings.tikhonovAlpha;
                }
                dampedHessian.put(i, j, h + g + a);
            }
        }

        // solve for optimal parameter update
        cvSolve(dampedHessian, gradient, update, CV_SVD);
        for (int i = 0; i < n; i++) {
            parameters.set(i, parameters.get(i) + update.get(i)*updateScale[i]);
        }
        residualUpdateNeeded = true;

//long solveTime = System.currentTimeMillis();

        double newRMSE = getRMSE();
        if (RMSE > prevRMSE) {
            RMSE = prevRMSE;
            parameters.set(prevParameters);
            residualUpdateNeeded = false;
        } else {
            RMSE = newRMSE;
        }

        if (lmLambdaIndex < settings.LMLambdas.length-1 && newRMSE > prevRMSE) {
            lmLambdaIndex++;
        } else {
            lmLambdaIndex = 0;
            if (newRMSE > prevRMSE*(1.0-settings.errorDecreaseMin)) {
                if (pyramidLevel > 0) {
                    setPyramidLevel(pyramidLevel-1);
                } else {
                    converged = true;
                }
            }
        }

//long residualTime = System.currentTimeMillis();

//accTime += (residualTime-start);
//System.out.println("gradientHessianTime = "+ (gradientHessianTime-start) +
//                 "  solveTime = " + (solveTime-gradientHessianTime) +
//                 "  residualTime = " + (residualTime-solveTime) +
//                 "  totalTime = " + (residualTime-start) +
//                 "  accTime = " + accTime);

        if (delta != null) {
            for (int i = 0; i < delta.length; i++) {
                delta[i] = update.get(i)*updateScale[i];
            }
        }

        return converged;
    }


    private void doHessianGradient(final double[] scale) {
        final int n = parameters.size();
        final double  constraintError = parameters.getConstraintError();
        final double[] constraintGrad = new double[n];

        cvSetZero(gradient);
        cvSetZero(hessian);

        Parallel.loop(0, n, new Looper() {
        public void loop(int from, int to, int looperID) {
//        for (int i = 0; i < n; i++) {
        for (int i = from; i < to; i++) {
            tempParameters[i].set(parameters);
            tempParameters[i].addDelta(i);
            scale[i] = tempParameters[i].get(i) - parameters.get(i);
            constraintGrad[i] = tempParameters[i].getConstraintError() - constraintError;
        }}});

        Parallel.loop(0, hessianGradientTransformerData.length, new Looper() {
        public void loop(int from, int to, int looperID) {
            for (int i = 0; i < n; i++) {
                Data d = hessianGradientTransformerData[looperID][i];
                d.srcImg    = template[pyramidLevel];
                d.transImg  = d.dstImg = null;
                d.subImg    = warped  [pyramidLevel];
                d.srcDotImg = residual[pyramidLevel];
            }

            int y1 = roi.y +  looperID   *roi.height/hessianGradientTransformerData.length;
            int y2 = roi.y + (looperID+1)*roi.height/hessianGradientTransformerData.length;
            subroi[looperID].x      = roi.x;
            subroi[looperID].width  = roi.width;
            subroi[looperID].y      = y1;
            subroi[looperID].height = y2-y1;

            transformer.transform(hessianGradientTransformerData[looperID],
                    roiMask[pyramidLevel], subroi[looperID],
                    RMSE*settings.zeroThresholds[Math.min(settings.zeroThresholds.length-1, pyramidLevel)],
                    pyramidLevel, tempParameters);
        }});

        for (Data[] data : hessianGradientTransformerData) {
            for (int i = 0; i < n; i++) {
                Data d = (Data)data[i];
                gradient.put(i, gradient.get(i) - d.srcDstDot);
                for (int j = 0; j < n; j++) {
                    hessian.put(i, j, hessian.get(i, j) + d.dstDstDot.get(j));
                }
            }
        }

        // if we have a gamma, compute the prior for regularization
// since our prior is zero motion, no need to compute it
//        if (gamma != null && prior != null) {
//            for (int i = 0; i < n; i++) {
//                prior.put(i, initialParameters.get(i) - currentParameters.get(i));
//            }
//            cvMatMul(hessian, prior, prior);
//
//            // compute gradient
//            for (int i = 0; i < n; i++) {
//                gradient.put(i, gradient.get(i) - /* deltax * */ prior.get(i));
//            }
//        }
//System.out.println(prior);

        if (settings.constrained) {
            // to get a well-conditionned matrix, compute what
            // looks like an appropriate scale for the constraint
            double constraintGradSum = 0;
            for (double d : constraintGrad) {
                constraintGradSum += d;
            }
            scale[n] = n/constraintGradSum;

            for (int i = 0; i < n; i++) {
                double c = constraintGrad[i]*scale[n];
                hessian.put(i, n, c);
                hessian.put(n, i, c);
            }
            gradient.put(n, -constraintError*scale[n]);
        }
    }

    private void doRoi() {
        doRoi(0);
    }
    private void doRoi(double extraPadding) {
        transformer.transform(srcRoiPts, dstRoiPts, parameters, false);
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE,
               minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        for (int i = 0; i < dstRoiPts.getLength(); i++) {
            double x = dstRoiPts.get(2*i  )/(1<<pyramidLevel);
            double y = dstRoiPts.get(2*i+1)/(1<<pyramidLevel);
            dstRoiPts.put(2*i  , x);
            dstRoiPts.put(2*i+1, y);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        cvSetZero(roiMask[pyramidLevel]);
        CvPoint.fillArray(dstRoiPtsArray, (byte)16, dstRoiPts.get());
        cvFillConvexPoly(roiMask[pyramidLevel], dstRoiPtsArray, 4, CvScalar.WHITE, 8, 16);
        // add +3 all around because cvWarpPerspective() needs it apparently
        minX = Math.max(0, minX-3-(maxX-minX)*extraPadding);
        minY = Math.max(0, minY-3-(maxY-minY)*extraPadding);
        maxX = Math.min(roiMask[pyramidLevel].width,  maxX+3+(maxX-minX)*extraPadding);
        maxY = Math.min(roiMask[pyramidLevel].height, maxY+3+(maxY-minY)*extraPadding);

        // there seems to be something funny with memory alignment and
        // ROIs, so let's align our ROI to a 16 byte boundary just in case..
        roi.x      = (int)Math.floor(minX/16)*16;
        roi.y      = (int)Math.floor(minY);
        roi.width  = (int)Math.ceil (maxX/16)*16 - roi.x;
        roi.height = (int)Math.ceil (maxY)       - roi.y;
//        roi.x      = 0;
//        roi.y      = 0;
//        roi.width  = roiMask[pyramidLevel].width;
//        roi.height = roiMask[pyramidLevel].height;

//System.out.println(roi);
    }

    private void doResidual() {
        parameters.getConstraintError();
        Parallel.loop(0, residualTransformerData.length, new Looper() {
        public void loop(int from, int to, int looperID) {
            Data d = residualTransformerData[looperID][0];
            d.srcImg    = template[pyramidLevel];
            d.subImg    = target  [pyramidLevel];
            d.srcDotImg = null;
            d.transImg  = warped  [pyramidLevel];
            d.dstImg    = residual[pyramidLevel];

            int y1 = roi.y +  looperID   *roi.height/residualTransformerData.length;
            int y2 = roi.y + (looperID+1)*roi.height/residualTransformerData.length;
            subroi[looperID].x      = roi.x;
            subroi[looperID].y      = y1;
            subroi[looperID].width  = roi.width;
            subroi[looperID].height = y2-y1;

            transformer.transform(residualTransformerData[looperID], roiMask[pyramidLevel],
                    subroi[looperID], 0, pyramidLevel, parametersArray);
        }});

        double dstDstDot = 0, dstCount = 0;
        for (Data[] data : residualTransformerData) {
            dstDstDot += data[0].dstDstDot.get(0);
            dstCount  += data[0].dstCount;
        }
        RMSE = Math.sqrt(dstDstDot/dstCount);
//System.out.println("dstCount " + dstCount + " RMSE " + RMSE);

        residualUpdateNeeded = false;
    }

}
