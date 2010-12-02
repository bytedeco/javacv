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

package com.googlecode.javacv;

import java.util.Arrays;
import com.googlecode.javacv.ImageTransformer.Data;
import com.googlecode.javacv.ImageTransformer.Parameters;
import com.googlecode.javacv.Parallel.Looper;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;

/**
 *
 * @author Samuel Audet
 */
public class GNImageAligner implements ImageAligner {
    public GNImageAligner(ImageTransformer transformer, Parameters initialParameters,
            IplImage template0, double[] roiPts, IplImage target0) {
        this(transformer, initialParameters, template0, roiPts, target0, new Settings());
    }
    public GNImageAligner(ImageTransformer transformer, Parameters initialParameters,
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

        this.subroi    = new CvRect.ByValue[settings.numThreads];
        for (int i = 0; i < subroi.length; i++) {
            this.subroi[i] = new CvRect.ByValue();
        }
        this.transformer = transformer;
        this.hessianGradientTransformerData = new Data[settings.numThreads][n];
        for (int i = 0; i < hessianGradientTransformerData.length; i++) {
            for (int j = 0; j < hessianGradientTransformerData[i].length; j++) {
                hessianGradientTransformerData[i][j] = new Data(template[pyramidLevel],
                        warped[pyramidLevel], residual[pyramidLevel], null, null, n);
            }
        }
        this.residualTransformerData = new Data[settings.numThreads][1];
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

        subspaceParameters = parameters.getSubspace();
        if (subspaceParameters != null) {
            tempSubspaceParameters = new double[settings.numThreads][];
            for (int i = 0; i < tempSubspaceParameters.length; i++) {
                tempSubspaceParameters[i] = subspaceParameters.clone();
            }
//        for (double d : subspaceParameters) {
//            System.out.print(d + " ");
//        }
//        System.out.println();
        }

        setConstrained(settings.constrained);
        setTemplateImage(template0, roiPts);
        setTargetImage(target0);
    }

    public static class Settings extends ImageAligner.Settings implements Cloneable {
        public Settings() { }
        public Settings(Settings s) {
            super(s);
            stepScale       = s.stepScale;
            lineSearch      = s.lineSearch;
            zeroThresholds  = s.zeroThresholds;
            deltaMin        = s.deltaMin;
            deltaMax        = s.deltaMax;
            displacementMax = s.displacementMax;
            subspaceTermK   = s.subspaceTermK;
            numThreads      = s.numThreads;
        }

        double stepScale        = 0.1;
        double[] lineSearch     = {1.0, 0.25};
        double[] zeroThresholds = {1.0, 0.75, 0.5, 0.25, 0.0};
        double deltaMin         = 10;
        double deltaMax         = 300;
        double displacementMax  = 0.15;
        double subspaceTermK    = 0.1;
        int numThreads = Parallel.numCores;

        public double getStepScale() {
            return stepScale;
        }
        public void setStepScale(double stepScale) {
            this.stepScale = stepScale;
        }

        public double[] getLineSearch() {
            return lineSearch;
        }
        public void setLineSearch(double[] lineSearch) {
            this.lineSearch = lineSearch;
        }

        public double[] getZeroThresholds() {
            return zeroThresholds;
        }
        public void setZeroThresholds(double[] zeroThresholds) {
            this.zeroThresholds = zeroThresholds;
        }

        public double getDeltaMin() {
            return deltaMin;
        }
        public void setDeltaMin(double deltaMin) {
            this.deltaMin = deltaMin;
        }

        public double getDeltaMax() {
            return deltaMax;
        }
        public void setDeltaMax(double deltaMax) {
            this.deltaMax = deltaMax;
        }

        public double getDisplacementMax() {
            return displacementMax;
        }
        public void setDisplacementMax(double displacementMax) {
            this.displacementMax = displacementMax;
        }

        public double getSubspaceTermK() {
            return subspaceTermK;
        }
        public void setSubspaceTermK(double subspaceTermK) {
            this.subspaceTermK = subspaceTermK;
        }

        public int getNumThreads() {
            return numThreads;
        }
        public void setNumThreads(int numThreads) {
            this.numThreads = numThreads;
        }

        @Override public Settings clone() {
            return new Settings(this);
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(ImageAligner.Settings settings) {
        this.settings = (Settings)settings;
    }

    private IplImage[] template, target, warped, residual, roiMask;
    private CvMat srcRoiPts, dstRoiPts;
    private CvPoint[] dstRoiPtsArray;
    private CvRect.ByValue roi, subroi[];
    private ImageTransformer transformer;
    private Data[][] hessianGradientTransformerData, residualTransformerData;
    private Parameters parameters, parametersArray[], tempParameters[], priorParameters;
    private CvMat hessian, regularizedHessian, gradient, update, prior;
    private double[] constraintGrad, subspaceResidual, subspaceJacobian[], updateScale;
    private boolean[] subspaceCorrelated;
    private int pyramidLevel;
    private double RMSE;
    private boolean residualUpdateNeeded = true;
    private int lastLinePosition = 0;
    private int trials = 0;

    public double[] subspaceParameters, tempSubspaceParameters[];

    public IplImage getTemplateImage() {
        return template[pyramidLevel];
    }
    public void setTemplateImage(IplImage template0, double[] roiPts) {
        if (roiPts == null) {
            this.srcRoiPts.put(0.0, 0.0,  template0.width, 0.0,
                    template0.width, template0.height,  0.0, template0.height);
        } else {
            this.srcRoiPts.put(roiPts);
        }

        if (template0.depth == IPL_DEPTH_32F) {
            template[0] = template0;
        } else {
            cvConvertScale(template0, template[0], 1.0/template0.getMaxIntensity(), 0);
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
            subroi[0].x      = Math.max(0, (int)Math.floor((double)roi.x/align)*align);
            subroi[0].y      = Math.max(0, (int)Math.floor((double)roi.y/align)*align);
            subroi[0].width  = Math.min(target0.width,  (int)Math.ceil((double)roi.width /align)*align);
            subroi[0].height = Math.min(target0.height, (int)Math.ceil((double)roi.height/align)*align);
            cvSetImageROI(target0,   subroi[0]);
            cvSetImageROI(target[0], subroi[0]);
        } else {
            cvResetImageROI(target0);
            cvResetImageROI(target[0]);
        }

        if (target0.depth != IPL_DEPTH_32F) {
            cvConvertScale(target0, target[0], 1.0/target0.getMaxIntensity(), 0);
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
        trials = 0;
    }

    public boolean isConstrained()  {
        return settings.constrained;
    }
    public void setConstrained(boolean constrained) {
        if (settings.constrained == constrained && hessian != null &&
                regularizedHessian != null && gradient != null && update != null) {
            return;
        }
        settings.constrained = constrained;
        int n = parameters.size();
        int m = constrained ? n+1 : n;
        if (subspaceParameters != null && settings.subspaceTermK != 0.0) {
            m += subspaceParameters.length;
        }
        hessian       = CvMat.create(m, m);
        regularizedHessian = CvMat.create(m, m);
        gradient      = CvMat.create(m, 1);
        update        = CvMat.create(m, 1);
        updateScale   = new double[m];
        prior         = CvMat.create(n, 1);

        constraintGrad = new double[n];
        subspaceResidual = new double[n];
        subspaceJacobian = new double[m][n];
        subspaceCorrelated = new boolean[n];
    }

    public Parameters getParameters() {
        return parameters;
    }
    public void setParameters(Parameters parameters) {
        this.parameters.set(parameters);
        subspaceParameters = parameters.getSubspace();
        if (subspaceParameters != null && settings.subspaceTermK != 0.0) {
            for (int i = 0; i < tempSubspaceParameters.length; i++) {
                tempSubspaceParameters[i] = subspaceParameters.clone();
            }
        }
        residualUpdateNeeded = true;
    }

    public Parameters getPriorParameters() {
        return priorParameters;
    }
    public void setPriorParameters(Parameters priorParameters) {
        this.priorParameters.set(priorParameters);
    }

    public IplImage getTransformedImage() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
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

    public CvRect getROI() {
        if (residualUpdateNeeded) {
            doRoi();
        }
        return roi;
    }
    public int getLastLinePosition() {
        return lastLinePosition;
    }

    public boolean iterate(double[] delta) {
        boolean converged = false;
        final int n = parameters.size();
        final double prevRMSE = getRMSE();
        final double[] prevParameters = parameters.get();
        final double[] prevSubspaceParameters = subspaceParameters == null ? null : subspaceParameters.clone();

        if (trials == 0 && parameters.preoptimize()) {
            setParameters(parameters);
            doResidual();
        }
        final double[] resetParameters = parameters.get();
        final double[] resetSubspaceParameters = subspaceParameters == null ? null : subspaceParameters.clone();

        doHessianGradient(updateScale);

        // add Tikhonov regularization
        for (int i = 0; i < hessian.rows; i++) {
            for (int j = 0; j < hessian.cols; j++) {
                double h = hessian.get(i, j);
                double g = 0;
                if (settings.gammaTgamma != null && i < settings.gammaTgamma.rows && j < settings.gammaTgamma.cols) {
                    g = settings.gammaTgamma.get(i, j);
                }
                double a = 0;
                if (i == j && i < n) {
                    a = settings.tikhonovAlpha;
                }
                regularizedHessian.put(i, j, h + g + a);
            }
        }

        lastLinePosition = 0;

        // solve for optimal parameter update
        cvSolve(regularizedHessian, gradient, update, CV_SVD);
        for (int i = 0; i < n; i++) {
            parameters.set(i, parameters.get(i) + settings.lineSearch[0]*update.get(i)*updateScale[i]);
        }
        for (int i = n; i < update.getLength(); i++) {
            subspaceParameters[i-n] += settings.lineSearch[0]*update.get(i)*updateScale[i];
        }
        residualUpdateNeeded = true;

        for (int j = 1; j < settings.lineSearch.length && getRMSE() > prevRMSE; j++) {
            RMSE = prevRMSE;
            parameters.set(resetParameters);
            if (subspaceParameters != null) {
                System.arraycopy(resetSubspaceParameters, 0, subspaceParameters, 0, subspaceParameters.length);
            }
            lastLinePosition = j;
            for (int i = 0; i < n; i++) {
                parameters.set(i, parameters.get(i) + settings.lineSearch[j]*update.get(i)*updateScale[i]);
            }
            for (int i = n; i < update.getLength(); i++) {
                subspaceParameters[i-n] += settings.lineSearch[j]*update.get(i)*updateScale[i];
            }
            residualUpdateNeeded = true;
        }

        double deltaNorm = 0;
        if (delta != null) {
            for (int i = 0; i < delta.length && i < updateScale.length; i++) {
                delta[i] = settings.lineSearch[lastLinePosition]*update.get(i)*updateScale[i];
            }
            deltaNorm = JavaCV.norm(Arrays.copyOf(delta, n));
        }

        boolean invalid = getRMSE() > prevRMSE || deltaNorm > settings.deltaMax ||
                          Double.isNaN(RMSE) || Double.isInfinite(RMSE);
        if (invalid) {
            RMSE = prevRMSE;
            parameters.set(prevParameters);
            if (subspaceParameters != null) {
                System.arraycopy(prevSubspaceParameters, 0, subspaceParameters, 0, subspaceParameters.length);
            }
            residualUpdateNeeded = true;
        }
        if (invalid && deltaNorm > settings.deltaMin && ++trials < 2) {
            return false;
        } else if (invalid || deltaNorm < settings.deltaMin) {
            trials = 0;
            if (pyramidLevel > 0) {
                setPyramidLevel(pyramidLevel-1);
            } else {
                converged = true;
            }
        } else {
            trials = 0;
        }

//long residualTime = System.currentTimeMillis();

//accTime += (residualTime-start);
//System.out.println("gradientHessianTime = "+ (gradientHessianTime-start) +
//                 "  solveTime = " + (solveTime-gradientHessianTime) +
//                 "  residualTime = " + (residualTime-solveTime) +
//                 "  totalTime = " + (residualTime-start) +
//                 "  accTime = " + accTime);

        return converged;
    }

    private void doHessianGradient(final double[] scale) {
        final int n = parameters.size();
        final double constraintError = parameters.getConstraintError();
        final double stepScale = settings.stepScale;

        cvSetZero(gradient);
        cvSetZero(hessian);

        Parallel.loop(0, n, new Looper() {
        public void loop(int from, int to, int looperID) {
//        for (int i = 0; i < n; i++) {
        for (int i = from; i < to; i++) {
            tempParameters[i].set(parameters);
//            if (i >= 8 && i <= 10) {
//            if (trials > 0) {
//                tempParameters[i].addDelta(i, 0);
//            } else {
                tempParameters[i].set(i, tempParameters[i].get(i) + /*(1<<pyramidLevel)**/stepScale);
//            }
            scale[i] = tempParameters[i].get(i) - parameters.get(i);
            constraintGrad[i] = tempParameters[i].getConstraintError() - constraintError;
        }}});

        Parallel.loop(0, hessianGradientTransformerData.length, settings.numThreads, new Looper() {
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

        double dstCount = 0;
        for (Data[] data : hessianGradientTransformerData) {
            dstCount += data[0].dstCount;
            for (int i = 0; i < n; i++) {
                Data d = (Data)data[i];
                gradient.put(i, gradient.get(i) - d.srcDstDot);
                for (int j = 0; j < n; j++) {
                    hessian.put(i, j, hessian.get(i, j) + d.dstDstDot.get(j));
                }
            }
        }

        // if we have a gamma of an alpha, compute the prior for regularization, but
        // if prioParameters == null, our prior is zero motion, so no need to compute it
        if ((settings.gammaTgamma != null || settings.tikhonovAlpha != 0) &&
                prior != null && priorParameters != null) {
            for (int i = 0; i < n; i++) {
                prior.put(i, parameters.get(i) - priorParameters.get(i));
            }
            cvMatMul(hessian, prior, prior);

            // compute gradient
            for (int i = 0; i < n; i++) {
                gradient.put(i, gradient.get(i) + prior.get(i));
            }
        }
//System.out.println(prior);

        if (settings.constrained) {
            // to get a well-conditionned matrix, compute what
            // looks like an appropriate scale for the constraint
            double constraintGradSum = 0;
            for (double d : constraintGrad) {
                constraintGradSum += d;
            }
            scale[n] = n*constraintGradSum;

            for (int i = 0; i < n; i++) {
                double c = constraintGrad[i]*scale[n];
                hessian.put(i, n, c);
                hessian.put(n, i, c);
            }
            gradient.put(n, -constraintError*scale[n]);
        }

        //double K = 0.000000001*dstCount;
//        double K = 0.0001*dstCount/n;
//        K = K*K;
        if (subspaceParameters != null && subspaceParameters.length > 0 &&
                settings.subspaceTermK != 0.0) {
            final int m = subspaceParameters.length;
//            double[][] subspaceHessian  = new double[n+m][n+m];
//            double[] subspaceGradient   = new double[n+m];

            Arrays.fill(subspaceCorrelated, false);
            tempParameters[0].set(parameters);
            tempParameters[0].setSubspace(subspaceParameters);
            Parallel.loop(0, n+m, new Looper() {
            public void loop(int from, int to, int looperID) {
//            int looperID = 0;
//            for (int i = 0; i < n+m; i++) {
            for (int i = from; i < to; i++) {
                if (i < n) {
                    Arrays.fill(subspaceJacobian[i], 0);
                    subspaceJacobian[i][i] = scale[i];
                } else {
                    System.arraycopy(subspaceParameters, 0, tempSubspaceParameters[looperID], 0, m);
//                    if (i < 18) {
//                        tempSubspaceParameters[i-n] += 0.0001;//stepScale;
//                    } else {
//                        tempSubspaceParameters[i-n] += 1.0;//stepScale;
//                    }
                    tempSubspaceParameters[looperID][i-n] += stepScale;
                    tempParameters[i-n+1].set(parameters);
                    tempParameters[i-n+1].setSubspace(tempSubspaceParameters[looperID]);
                    scale[i] = tempSubspaceParameters[looperID][i-n] - subspaceParameters[i-n];
                    for (int j = 0; j < n; j++) {
                        subspaceJacobian[i][j] = tempParameters[0].get(j) - tempParameters[i-n+1].get(j);
                        subspaceCorrelated[j] |= subspaceJacobian[i][j] != 0; // this may not work in parallel...
                    }
                }
            }}});

            double K = 0;
            int subspaceCorrelatedCount = 0;
//            double subspaceRMSE = 0;
            for (int i = 0; i < n; i++) {
                subspaceResidual[i] = parameters.get(i) - tempParameters[0].get(i);
//                subspaceRMSE += subspaceResidual[i]*subspaceResidual[i];

                if (subspaceCorrelated[i]) {
                    K += hessian.get(i, i);
                    subspaceCorrelatedCount++;
                }
            }
//            subspaceRMSE = Math.sqrt(subspaceRMSE/n);
//System.out.println((float)RMSE + " " + (float)subspaceRMSE);
            final double KK = settings.subspaceTermK * Math.sqrt(K)/subspaceCorrelatedCount /
                    (stepScale*n);
//System.out.println(K + "  " + KK);

            Parallel.loop(0, n+m, new Looper() {
            public void loop(int from, int to, int looperID) {
//            int looperID = 0;
//            for (int i = 0; i < n+m; i++) {
            for (int i = from; i < to; i++) {
                if (i < n && !subspaceCorrelated[i]) {
                    continue;
                }

                for (int j = i; j < n+m; j++) {
                    if (j < n && !subspaceCorrelated[j]) {
                        continue;
                    }
                    double h = 0;
                    for (int k = 0; k < n; k++) {
                        h += subspaceJacobian[i][k]*subspaceJacobian[j][k];
                    }
//                    subspaceHessian[i][j] = h;
                    h = hessian.get(i, j) + KK*h;
                    hessian.put(i, j, h);
                    hessian.put(j, i, h);
                }

                double g = 0;
                for (int k = 0; k < n; k++) {
                    g -= subspaceJacobian[i][k]*subspaceResidual[k];
                }
//                subspaceGradient[i] = g;
                g = gradient.get(i) + KK*g;
                gradient.put(i, g);
            }}});
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
        roi.x      = Math.max(0, (int)Math.floor(minX/16)*16);
        roi.y      = Math.max(0, (int)Math.floor(minY));
        roi.width  = Math.min(roiMask[pyramidLevel].width,  (int)Math.ceil(maxX/16)*16) - roi.x;
        roi.height = Math.min(roiMask[pyramidLevel].height, (int)Math.ceil(maxY))       - roi.y;
//        roi.x      = 0;
//        roi.y      = 0;
//        roi.width  = roiMask[pyramidLevel].width;
//        roi.height = roiMask[pyramidLevel].height;

//System.out.println(roi);
    }

    private void doResidual() {
        parameters.getConstraintError();
//        cvSetZero(warped  [pyramidLevel]);
//        cvSetZero(residual[pyramidLevel]);
        Parallel.loop(0, residualTransformerData.length, settings.numThreads, new Looper() {
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
        if (dstCount < parameters.size()) {
            RMSE = Double.NaN;
        } else {
            RMSE = Math.sqrt(dstDstDot/dstCount);
//            RMSE = dstDstDot/dstCount;
        }
//        if (Double.isNaN(RMSE)) {
//System.out.println("dstCount " + dstCount + " RMSE " + RMSE + " " + pyramidLevel);
//        }
        residualUpdateNeeded = false;
    }

}
