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

import java.util.Arrays;
import com.googlecode.javacv.ImageTransformer.Data;
import com.googlecode.javacv.ImageTransformer.Parameters;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

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
        this(transformer, initialParameters);
        setSettings(settings);

        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

        this.template    = new IplImage[maxLevel+1];
        this.target      = new IplImage[maxLevel+1];
        this.transformed = new IplImage[maxLevel+1];
        this.residual    = new IplImage[maxLevel+1];
        this.mask        = new IplImage[maxLevel+1];
        int w = template0.width();
        int h = template0.height();
        int c = template0.nChannels();
        int o = template0.origin();
        for (int i = minLevel; i <= maxLevel; i++) {
            if (i == minLevel && template0.depth() == IPL_DEPTH_32F) {
                template[i] = template0;
            } else {
                template[i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            }
            if (i == minLevel && target0.depth() == IPL_DEPTH_32F) {
                target[i] = target0;
            } else {
                target[i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            }
            transformed[i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            residual   [i] = IplImage.create(w, h, IPL_DEPTH_32F, c, o);
            mask       [i] = IplImage.create(w, h, IPL_DEPTH_8U,  1, o);
            w /= 2;
            h /= 2;
        }

        this.hessianGradientTransformerData = new Data[n];
        for (int i = 0; i < n; i++) {
            hessianGradientTransformerData[i] = new Data(template[pyramidLevel],
                    transformed[pyramidLevel], residual[pyramidLevel], mask[pyramidLevel],
                    0, 0, pyramidLevel, null, null, n);
        }
        this.residualTransformerData = new Data[] { new Data(template[pyramidLevel],
                    target[pyramidLevel], null, mask[pyramidLevel],
                    0, 0, pyramidLevel, transformed[pyramidLevel], residual[pyramidLevel], 1) };

        setConstrained(settings.constrained);
        setTemplateImage(template0, roiPts);
        setTargetImage(target0);
    }
    protected GNImageAligner(ImageTransformer transformer, Parameters initialParameters) {
        this.n = initialParameters.size();

        this.srcRoiPts = CvMat.create(4, 1, CV_64F, 2);
        this.dstRoiPts = CvMat.create(4, 1, CV_64F, 2);
        this.dstRoiPtsArray = new CvPoint(4);
        this.roi     = new CvRect();
        this.temproi = new CvRect();
        this.transformer = transformer;

        this.parameters      = initialParameters.clone();
        this.parametersArray = new Parameters[] { parameters };
        this.tempParameters  = new Parameters[n];
        for (int i = 0; i < tempParameters.length; i++) {
            this.tempParameters[i] = initialParameters.clone();
        }

        subspaceParameters = parameters.getSubspace();
        if (subspaceParameters != null) {
            tempSubspaceParameters = new double[Parallel.getNumThreads()][];
            for (int i = 0; i < tempSubspaceParameters.length; i++) {
                tempSubspaceParameters[i] = subspaceParameters.clone();
            }
//        for (double d : subspaceParameters) {
//            System.out.print(d + " ");
//        }
//        System.out.println();
        }
    }

    public static class Settings extends ImageAligner.Settings implements Cloneable {
        public Settings() { }
        public Settings(Settings s) {
            super(s);
            stepSize        = s.stepSize;
            lineSearch      = s.lineSearch;
            deltaMin        = s.deltaMin;
            deltaMax        = s.deltaMax;
            displacementMax = s.displacementMax;
            alphaSubspace   = s.alphaSubspace;
            alphaTikhonov   = s.alphaTikhonov;
            gammaTgamma     = s.gammaTgamma;
            constrained     = s.constrained;
        }

        double stepSize        = 0.1;
        double[] lineSearch    = {1.0, 0.25};
        double deltaMin        = 10;
        double deltaMax        = 300;
        double displacementMax = 0.2;
        double alphaSubspace   = 0.1;
        double alphaTikhonov   = 0;
        CvMat gammaTgamma      = null;
        boolean constrained    = false;

        public double getStepSize() {
            return stepSize;
        }
        public void setStepSize(double stepSize) {
            this.stepSize = stepSize;
        }

        public double[] getLineSearch() {
            return lineSearch;
        }
        public void setLineSearch(double[] lineSearch) {
            this.lineSearch = lineSearch;
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

        public double getAlphaSubspace() {
            return alphaSubspace;
        }
        public void setAlphaSubspace(double alphaSubspace) {
            this.alphaSubspace = alphaSubspace;
        }

        public double getAlphaTikhonov() {
            return alphaTikhonov;
        }
        public void setAlphaTikhonov(double alphaTikhonov) {
            this.alphaTikhonov = alphaTikhonov;
        }

        public CvMat getGammaTgamma() {
            return gammaTgamma;
        }
        public void setGammaTgamma(CvMat gammaTgamma) {
            this.gammaTgamma = gammaTgamma;
        }

//        public boolean isConstrained() {
//            return constrained;
//        }
//        public void setConstrained(boolean constrained) {
//            this.constrained = constrained;
//        }

        @Override public Settings clone() {
            return new Settings(this);
        }
    }

    protected Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(ImageAligner.Settings settings) {
        this.settings = (Settings)settings;
    }

    protected final int n;
    protected IplImage[] template, target, transformed, residual, mask;
    protected IplImage[] images = new IplImage[5];
    protected CvMat srcRoiPts, dstRoiPts;
    protected CvPoint dstRoiPtsArray;
    protected CvRect roi, temproi;
    protected ImageTransformer transformer;
    protected Data[] hessianGradientTransformerData, residualTransformerData;
    protected Parameters parameters, parametersArray[], tempParameters[], priorParameters;
    protected CvMat hessian, gradient, update, prior;
    protected double[] constraintGrad, subspaceResidual, subspaceJacobian[], updateScale;
    protected boolean[] subspaceCorrelated;
    protected int pyramidLevel;
    protected double RMSE;
    protected boolean residualUpdateNeeded = true;
    protected int lastLinePosition = 0;
    protected int trials = 0;
//    protected double prevOutlierRatio = 0;

    protected double[] subspaceParameters, tempSubspaceParameters[];

    public IplImage getTemplateImage() {
        return template[pyramidLevel];
    }
    public void setTemplateImage(IplImage template0, double[] roiPts) {
        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

        if (roiPts == null) {
            int w = template0.width()  << minLevel;
            int h = template0.height() << minLevel;
            this.srcRoiPts.put(0.0, 0.0,  w, 0.0,  w, h,  0, h);
        } else {
            this.srcRoiPts.put(roiPts);
        }

        if (template0.depth() == IPL_DEPTH_32F) {
            template[minLevel] = template0;
        } else {
            cvConvertScale(template0, template[minLevel], 1.0/template0.highValue(), 0);
        }

        for (int i = minLevel+1; i <= maxLevel; i++) {
            cvPyrDown(template[i-1], template[i], CV_GAUSSIAN_5x5);
        }
        setPyramidLevel(maxLevel);
    }

    public IplImage getTargetImage() {
        return target[pyramidLevel];
    }
    public void setTargetImage(IplImage target0) {
        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

        if (target0.depth() == IPL_DEPTH_32F) {
            target[minLevel] = target0;
        }

        if (settings.displacementMax > 0) {
            transformer.transform(srcRoiPts, dstRoiPts, parameters, false);
            double[] pts = dstRoiPts.get();
            for (int i = 0; i < pts.length; i++) {
                pts[i] /= (1<<minLevel);
            }
            int width  = target[minLevel].width();
            int height = target[minLevel].height();
            temproi.x(0).y(0).width(width).height(height);
            int padX = (int)Math.round(settings.displacementMax*width);
            int padY = (int)Math.round(settings.displacementMax*height);
            int align = 1<<(maxLevel+1);
            // add +3 all around because cvPyrDown() needs it for smoothing
            JavaCV.boundingRect(pts, temproi, padX+3, padY+3, align, align);
            cvSetImageROI(target0, temproi);
            cvSetImageROI(target[minLevel], temproi);
        } else {
            cvResetImageROI(target0);
            cvResetImageROI(target[minLevel]);
        }

        if (target0.depth() != IPL_DEPTH_32F) {
            cvConvertScale(target0, target[minLevel], 1.0/target0.highValue(), 0);
            cvResetImageROI(target0);
        }

        for (int i = minLevel+1; i <= maxLevel; i++) {
            IplROI ir = target[i-1].roi();
            if (ir != null) {
                temproi.x(ir.xOffset()/2); temproi.width (ir.width() /2);
                temproi.y(ir.yOffset()/2); temproi.height(ir.height()/2);
                cvSetImageROI(target[i], temproi);
            } else {
                cvResetImageROI(target[i]);
            }
//            if (i == 1) {
//                cvResize(target[i-1], target[i], CV_INTER_NN);
//            } else {
//                cvPyrDown(target[i-1], target[i], CV_GAUSSIAN_5x5);
//            }
            cvPyrDown(target[i-1], target[i], CV_GAUSSIAN_5x5);
        }

        setPyramidLevel(maxLevel);
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
                gradient != null && update != null) {
            return;
        }
        settings.constrained = constrained;
        int m = constrained ? n+1 : n;
        if (subspaceParameters != null && settings.alphaSubspace != 0.0) {
            m += subspaceParameters.length;
        }
        hessian       = CvMat.create(m, m);
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
        if (subspaceParameters != null && settings.alphaSubspace != 0.0) {
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

    public double[] getTransformedRoiPts() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return dstRoiPts.get();
    }

    public IplImage getTransformedImage() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return transformed[pyramidLevel];
    }
    public IplImage getResidualImage() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return residual[pyramidLevel];
    }
    public IplImage getMaskImage() {
        return mask[pyramidLevel];
    }

    public double getRMSE() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return RMSE;
    }

    public int getPixelCount() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return residualTransformerData[0].dstCount;
    }

    public int getOutlierCount() {
        return hessianGradientTransformerData[0].dstCountOutlier;
    }

    public CvRect getRoi() {
        if (residualUpdateNeeded) {
            doRoi();
        }
        return roi;
    }
    public int getLastLinePosition() {
        return lastLinePosition;
    }

    public IplImage[] getImages() {
        images[0] = getTemplateImage();
        images[1] = getTargetImage();
        images[2] = getTransformedImage();
        images[3] = getResidualImage();
        images[4] = getMaskImage();
        return images;
    }

    public boolean iterate(double[] delta) {
        boolean converged = false;
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

        lastLinePosition = 0;

        // solve for optimal parameter update
        cvSolve(hessian, gradient, update, CV_SVD);
        for (int i = 0; i < n; i++) {
            parameters.set(i, parameters.get(i) + settings.lineSearch[0]*update.get(i)*updateScale[i]);
        }
        for (int i = n; i < update.length(); i++) {
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
            for (int i = n; i < update.length(); i++) {
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
            if (pyramidLevel > settings.pyramidLevelMin) {
                setPyramidLevel(pyramidLevel-1);
            } else {
                converged = true;
            }
        } else {
            trials = 0;
        }
        return converged;
    }

    protected void doHessianGradient(final double[] scale) {
        final double constraintError = parameters.getConstraintError();
        final double stepSize = settings.stepSize;

        cvSetZero(gradient);
        cvSetZero(hessian);

        Parallel.loop(0, n, new Parallel.Looper() {
        public void loop(int from, int to, int looperID) {
//        for (int i = 0; i < n; i++) {
        for (int i = from; i < to; i++) {
            tempParameters[i].set(parameters);
            tempParameters[i].set(i, tempParameters[i].get(i) + /*(1<<pyramidLevel)**/stepSize);
            scale[i] = tempParameters[i].get(i) - parameters.get(i);
            constraintGrad[i] = tempParameters[i].getConstraintError() - constraintError;
        }}});

//        final double adjustedRMSE = (1-prevOutlierRatio)*RMSE;
        for (int i = 0; i < n; i++) {
            Data d = hessianGradientTransformerData[i];
            d.srcImg    = template   [pyramidLevel];
            d.subImg    = transformed[pyramidLevel];
            d.srcDotImg = residual   [pyramidLevel];
            d.transImg  = null;
            d.dstImg    = null;
            d.mask      = mask       [pyramidLevel];
            d.zeroThreshold    = /*adjustedRMSE**/settings.thresholdsZero   [Math.min(settings.thresholdsZero   .length-1, pyramidLevel)];
            d.outlierThreshold = /*adjustedRMSE**/settings.thresholdsOutlier[Math.min(settings.thresholdsOutlier.length-1, pyramidLevel)];
            if (settings.thresholdsMulRMSE) {
                d.zeroThreshold    *= RMSE; //adjustedRMSE;
                d.outlierThreshold *= RMSE; //adjustedRMSE;
            }
            d.pyramidLevel = pyramidLevel;
        }
        transformer.transform(hessianGradientTransformerData, roi, tempParameters, null);

//        double dstCount = hessianGradientTransformerData[0].dstCount;
//        double dstCountZero = hessianGradientTransformerData[0].dstCountZero;
//        double dstCountOutlier = hessianGradientTransformerData[0].dstCountOutlier;
        for (int i = 0; i < n; i++) {
            Data d = (Data)hessianGradientTransformerData[i];
            gradient.put(i, gradient.get(i) - d.srcDstDot);
            for (int j = 0; j < n; j++) {
                hessian.put(i, j, hessian.get(i, j) + d.dstDstDot.get(j));
            }
        }
//        prevOutlierRatio = dstCountOutlier/dstCount;
//System.out.println(dstCountZero/dstCount + " " + dstCountOutlier/dstCount);

        doRegularization(updateScale);
    }

    protected void doRegularization(final double[] scale) {
        final double constraintError = parameters.getConstraintError();
        final double stepSize = settings.stepSize;

        // if we have a gamma or an alpha, compute the prior for regularization, but
        // if prioParameters == null, our prior is zero motion, so no need to compute it
        if ((settings.gammaTgamma != null || settings.alphaTikhonov != 0) &&
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

        if (subspaceParameters != null && subspaceParameters.length > 0 &&
                settings.alphaSubspace != 0.0) {
            final int m = subspaceParameters.length;
//            double[][] subspaceHessian  = new double[n+m][n+m];
//            double[] subspaceGradient   = new double[n+m];

            Arrays.fill(subspaceCorrelated, false);
            tempParameters[0].set(parameters);
            tempParameters[0].setSubspace(subspaceParameters);
            Parallel.loop(0, n+m, tempSubspaceParameters.length, new Parallel.Looper() {
            public void loop(int from, int to, int looperID) {
//            int looperID = 0;
//            for (int i = 0; i < n+m; i++) {
            for (int i = from; i < to; i++) {
                if (i < n) {
                    Arrays.fill(subspaceJacobian[i], 0);
                    subspaceJacobian[i][i] = scale[i];
                } else {
                    System.arraycopy(subspaceParameters, 0, tempSubspaceParameters[looperID], 0, m);
                    tempSubspaceParameters[looperID][i-n] += stepSize;
                    tempParameters[i-n+1].set(parameters);
                    tempParameters[i-n+1].setSubspace(tempSubspaceParameters[looperID]);
                    scale[i] = tempSubspaceParameters[looperID][i-n] - subspaceParameters[i-n];
                    for (int j = 0; j < n; j++) {
                        subspaceJacobian[i][j] = tempParameters[0].get(j) - tempParameters[i-n+1].get(j);
                        subspaceCorrelated[j] |= subspaceJacobian[i][j] != 0; // this may not work in parallel...
                    }
                }
            }}});

            int subspaceCorrelatedCount = 0;
//            double subspaceRMSE = 0;
            for (int i = 0; i < n; i++) {
                subspaceResidual[i] = parameters.get(i) - tempParameters[0].get(i);
//                subspaceRMSE += subspaceResidual[i]*subspaceResidual[i];

                if (subspaceCorrelated[i]) {
                    subspaceCorrelatedCount++;
                }
            }
//            subspaceRMSE = Math.sqrt(subspaceRMSE/n);
//System.out.println((float)RMSE + " " + (float)subspaceRMSE);
            final double K = settings.alphaSubspace*settings.alphaSubspace * RMSE*RMSE/
                    subspaceCorrelatedCount;//(subspaceRMSE*subspaceRMSE);

            Parallel.loop(0, n+m, new Parallel.Looper() {
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
                    h = hessian.get(i, j) + K*h;
                    hessian.put(i, j, h);
                    hessian.put(j, i, h);
                }

                double g = 0;
                for (int k = 0; k < n; k++) {
                    g -= subspaceJacobian[i][k]*subspaceResidual[k];
                }
//                subspaceGradient[i] = g;
                g = gradient.get(i) + K*g;
                gradient.put(i, g);
            }}});
        }

        // add Tikhonov regularization
        int rows = hessian.rows(), cols = hessian.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double h = hessian.get(i, j);
                double g = 0;
                if (settings.gammaTgamma != null && i < settings.gammaTgamma.rows() && j < settings.gammaTgamma.cols()) {
                    g = settings.gammaTgamma.get(i, j);
                }
                double a = 0;
                if (i == j && i < n) {
                    a = settings.alphaTikhonov * settings.alphaTikhonov;
                }
                hessian.put(i, j, h + g + a);
            }
        }
    }

    protected void doRoi() {
        transformer.transform(srcRoiPts, dstRoiPts, parameters, false);
        double[] pts = dstRoiPts.get();
        for (int i = 0; i < pts.length; i++) {
            pts[i] /= (1<<pyramidLevel);
        }
        roi.x(0).y(0).width(mask[pyramidLevel].width()).height(mask[pyramidLevel].height());
        // Add +3 all around because cvWarpPerspective() needs it for interpolation,
        // and there seems to be something funny with memory alignment and
        // ROIs, so let's align our ROI to a 16 byte boundary just in case..
        JavaCV.boundingRect(pts, roi, 3, 3, 16, 1);
//System.out.println(roi);

        cvSetZero(mask[pyramidLevel]);
        dstRoiPtsArray.put((byte)16, pts);
        cvFillConvexPoly(mask[pyramidLevel], dstRoiPtsArray, 4, CvScalar.WHITE, 8, 16);
    }

    protected void doResidual() {
        parameters.getConstraintError();
//        cvSetZero(transformed[pyramidLevel]);
//        cvSetZero(residual   [pyramidLevel]);
        Data d = residualTransformerData[0];
        d.srcImg    = template   [pyramidLevel];
        d.subImg    = target     [pyramidLevel];
        d.srcDotImg = null;
        d.transImg  = transformed[pyramidLevel];
        d.dstImg    = residual   [pyramidLevel];
        d.mask      = mask       [pyramidLevel];
//        d.zeroThreshold    = 0;
//        d.outlierThreshold = 0;
        d.zeroThreshold    = settings.thresholdsZero   [Math.min(settings.thresholdsZero   .length-1, pyramidLevel)];
        d.outlierThreshold = settings.thresholdsOutlier[Math.min(settings.thresholdsOutlier.length-1, pyramidLevel)];
        if (settings.thresholdsMulRMSE) {
            d.zeroThreshold    *= RMSE;
            d.outlierThreshold *= RMSE;
        }
        d.pyramidLevel = pyramidLevel;

        transformer.transform(residualTransformerData, roi, parametersArray, null);

        double dstDstDot = residualTransformerData[0].dstDstDot.get(0);
        int dstCount = residualTransformerData[0].dstCount;
        RMSE = dstCount < n ? Double.NaN : Math.sqrt(dstDstDot/dstCount);
//        if (Double.isNaN(RMSE)) {
//System.out.println("dstCount " + dstCount + " RMSE " + RMSE + " " + pyramidLevel);
//        }
        residualUpdateNeeded = false;
    }
}
