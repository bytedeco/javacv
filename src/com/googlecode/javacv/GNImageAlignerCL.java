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

import com.googlecode.javacv.ImageTransformerCL.InputData;
import com.googlecode.javacv.ImageTransformerCL.OutputData;
import com.googlecode.javacv.ImageTransformer.Parameters;
import com.googlecode.javacv.Parallel.Looper;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImageFormat;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opencl.gl.CLGLImage2d;
import java.util.Arrays;
import javax.media.opengl.GL2;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class GNImageAlignerCL extends GNImageAligner implements ImageAlignerCL {
    public GNImageAlignerCL(ImageTransformerCL transformer, Parameters initialParameters,
            CLImage2d template0, double[] roiPts, CLImage2d target0) {
        this(transformer, initialParameters, template0, roiPts, target0, new GNImageAligner.Settings());
    }
    public GNImageAlignerCL(ImageTransformerCL transformer, Parameters initialParameters,
            CLImage2d template0, double[] roiPts, CLImage2d target0, GNImageAligner.Settings settings) {
        setSettings(settings);
        context = transformer.getContext();

        int n = initialParameters.size();

        this.templateCL    = new CLImage2d[settings.maxPyramidLevel+1];
        this.targetCL      = new CLImage2d[settings.maxPyramidLevel+1];
        this.transformedCL = new CLImage2d[settings.maxPyramidLevel+1];
        this.residualCL    = new CLImage2d[settings.maxPyramidLevel+1];
        this.maskCL        = new CLGLImage2d[settings.maxPyramidLevel+1];
        this.maskrb        = new int[settings.maxPyramidLevel+1];
        this.maskfb        = new int[settings.maxPyramidLevel+1];
        int w = template0.width;
        int h = template0.height;
        CLGLContext c = context.getCLGLContext();
//        GLContext glContext = c.getGLContext();
//        glContext.makeCurrent();
        GL2 gl = context.getGL().getGL2();
        gl.glGenRenderbuffers(settings.maxPyramidLevel+1, maskrb, 0);
        gl.glGenFramebuffers(settings.maxPyramidLevel+1, maskfb, 0);
        CLImageFormat f = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
        for (int i = settings.minPyramidLevel; i <= settings.maxPyramidLevel; i++) {
            templateCL   [i] = i == settings.minPyramidLevel ? template0 : c.createImage2d(w, h, f, CLMemory.Mem.READ_WRITE);
            targetCL     [i] = i == settings.minPyramidLevel ? target0   : c.createImage2d(w, h, f, CLMemory.Mem.READ_WRITE);
            transformedCL[i] = c.createImage2d(w, h, f,  CLMemory.Mem.READ_WRITE);
            residualCL   [i] = c.createImage2d(w, h, f,  CLMemory.Mem.READ_WRITE);
            gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, maskrb[i]);
            gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, maskfb[i]);
            gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_INTENSITY8UI, w, h);
            gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, maskrb[i]);
            assert gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER) == GL2.GL_FRAMEBUFFER_COMPLETE;
            maskCL[i] = c.createFromGLRenderbuffer(maskrb[i], CLMemory.Mem.READ_WRITE);
            System.out.println(maskCL[i] + " " + maskCL[i].getElementSize() + " " + maskCL[i].getFormat());
            w /= 2;
            h /= 2;
        }
//        glContext.release();

        this.srcRoiPts = CvMat.create(4, 1, CV_64F, 2);
        this.dstRoiPts = CvMat.create(4, 1, CV_64F, 2);
        this.transformer = transformer;
        this.inputData = new InputData();
        this.outputData = new OutputData(false);

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
        setTemplateImageCL(template0, roiPts);
        setTargetImageCL(target0);
    }
    public void release() {
        if (templateCL != null && targetCL != null && transformedCL != null &&
                residualCL != null && maskCL != null) {
            for (int i = settings.minPyramidLevel; i <= settings.maxPyramidLevel; i++) {
                if (i > settings.minPyramidLevel) templateCL[i].release();
                if (i > settings.minPyramidLevel) targetCL  [i].release();
                transformedCL[i].release();
                residualCL   [i].release();
                maskCL       [i].release();
            }
            templateCL = targetCL = transformedCL = residualCL = maskCL = null;
        }

        // NVIDIA drivers crash if we don't delete those before terminating
        context.getGLContext().makeCurrent();
        GL2 gl = context.getGL().getGL2();
        if (maskfb != null) {
            gl.glDeleteFramebuffers(settings.maxPyramidLevel+1, maskfb, 0);
            maskfb = null;
        }
        if (maskrb != null) {
            gl.glDeleteRenderbuffers(settings.maxPyramidLevel+1, maskrb, 0);
            maskrb = null;
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private final JavaCVCL context;
    private CLImage2d[] templateCL, targetCL, transformedCL, residualCL;
    private CLGLImage2d[] maskCL;
    private int[] maskrb, maskfb;
    private InputData inputData;
    private OutputData outputData;

    public CLImage2d getTemplateImageCL() {
        return templateCL[pyramidLevel];
    }
    public void setTemplateImageCL(CLImage2d template0, double[] roiPts) {
        if (roiPts == null) {
            int w = template0.width  << settings.minPyramidLevel;
            int h = template0.height << settings.minPyramidLevel;
            this.srcRoiPts.put(0.0, 0.0,  w, 0.0,  w, h,  0, h);
        } else {
            this.srcRoiPts.put(roiPts);
        }

//        if (templateCL == null || templateCL.length != settings.pyramidLevels) {
//            templateCL = new CLImage2d[settings.pyramidLevels];
//        }
        templateCL[settings.minPyramidLevel] = template0;
        for (int i = settings.minPyramidLevel+1; i <= settings.maxPyramidLevel; i++) {
//            if (templateCL[i] == null) {
//                int w = templateCL[i-1].width/2;
//                int h = templateCL[i-1].height/2;
//                CLImageFormat format = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
//                templateCL[i] = JavaCVCL.getCLContext().createImage2d(w, h, format, CLMemory.Mem.READ_WRITE);
//            }
            context.pyrDown(templateCL[i-1], templateCL[i]);
        }
        setPyramidLevel(settings.maxPyramidLevel);
    }

    public CLImage2d getTargetImageCL() {
        return targetCL[pyramidLevel];
    }
    public void setTargetImageCL(CLImage2d target0) {
//        if (targetCL == null || targetCL.length != settings.pyramidLevels) {
//            targetCL = new CLImage2d[settings.pyramidLevels];
//        }
        targetCL[settings.minPyramidLevel] = target0;
        for (int i = settings.minPyramidLevel+1; i <= settings.maxPyramidLevel; i++) {
//            if (targetCL[i] == null) {
//                int w = targetCL[i-1].width/2;
//                int h = targetCL[i-1].height/2;
//                CLImageFormat format = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
//                targetCL[i] = JavaCVCL.getCLContext().createImage2d(w, h, format, CLMemory.Mem.READ_WRITE);
//            }
            context.pyrDown(targetCL[i-1], targetCL[i]);
        }
        setPyramidLevel(settings.maxPyramidLevel);
    }

    public CLImage2d getTransformedImageCL() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return transformedCL[pyramidLevel];
    }
    public CLImage2d getResidualImageCL() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return residualCL[pyramidLevel];
    }
    public CLImage2d getMaskImageCL() {
        return maskCL[pyramidLevel];
    }

    @Override public double getRMSE() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return RMSE;
    }

    @Override public int getPixelCount() {
        return outputData.dstCount;
    }

    @Override public CvRect getRoi() {
        if (residualUpdateNeeded) {
            doRoi();
        }
        return roi.x(inputData.roiX).y(inputData.roiY).
                width(inputData.roiWidth).height(inputData.roiHeight);
    }

    @Override protected void doHessianGradient(final double[] scale) {
        final int n = parameters.size();
        final double constraintError = parameters.getConstraintError();
        final double stepScale = settings.stepScale;

        cvSetZero(gradient);
        cvSetZero(hessian);

        Parallel.loop(0, n, settings.numThreads, new Looper() {
        public void loop(int from, int to, int looperID) {
//        for (int i = 0; i < n; i++) {
        for (int i = from; i < to; i++) {
            tempParameters[i].set(parameters);
            tempParameters[i].set(i, tempParameters[i].get(i) + /*(1<<pyramidLevel)**/stepScale);
            scale[i] = tempParameters[i].get(i) - parameters.get(i);
            constraintGrad[i] = tempParameters[i].getConstraintError() - constraintError;
        }}});

        inputData.zeroThreshold    = /*RMSE**/settings.zeroThresholds   [Math.min(settings.zeroThresholds   .length-1, pyramidLevel)];
        inputData.outlierThreshold = /*RMSE**/settings.outlierThresholds[Math.min(settings.outlierThresholds.length-1, pyramidLevel)];
        inputData.pyramidLevel = pyramidLevel;
        context.acquireGLObject(maskCL[pyramidLevel]);
        ((ImageTransformerCL)transformer).transform(templateCL[pyramidLevel], transformedCL[pyramidLevel],
                residualCL[pyramidLevel], null, null, maskCL[pyramidLevel],
                tempParameters, null, inputData, outputData);
        context.releaseGLObject(maskCL[pyramidLevel]);

        doRegularization(updateScale);

        outputData.readBuffer(context);
        for (int i = 0; i < n; i++) {
            gradient.put(i, gradient.get(i) - outputData.srcDstDot.get(i));
            for (int j = 0; j < n; j++) {
                hessian.put(i, j, hessian.get(i, j) + outputData.dstDstDot.get(i*n + j));
            }
        }
    }

    @Override protected void doRoi(double extraPadding) {
        transformer.transform(srcRoiPts, dstRoiPts, parameters, false);
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE,
               minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        for (int i = 0; i < dstRoiPts.length(); i++) {
            double x = dstRoiPts.get(2*i  )/(1<<pyramidLevel);
            double y = dstRoiPts.get(2*i+1)/(1<<pyramidLevel);
            dstRoiPts.put(2*i  , x);
            dstRoiPts.put(2*i+1, y);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
//        GLContext glContext = context.getGLContext();
//        glContext.makeCurrent();
        GL2 gl = context.getGL().getGL2();
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, maskfb[pyramidLevel]);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        context.getGLU().gluOrtho2D(0.0f, maskCL[pyramidLevel].width, 0.0f, maskCL[pyramidLevel].height);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glViewport(0, 0, maskCL[pyramidLevel].width, maskCL[pyramidLevel].height);

        gl.glClearColorIui(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glColor4ui(255, 255, 255, 255);
        gl.glBegin(GL2.GL_POLYGON);
// XXX: Remove "extra" pixels here...
            gl.glVertex2d(dstRoiPts.get(0), dstRoiPts.get(1));
            gl.glVertex2d(dstRoiPts.get(2)+1, dstRoiPts.get(3));
            gl.glVertex2d(dstRoiPts.get(4)+1, dstRoiPts.get(5)+1);
            gl.glVertex2d(dstRoiPts.get(6), dstRoiPts.get(7)+1);
        gl.glEnd();
//        gl.glFinish();
//        glContext.release();

        // add +3 all around because cvWarpPerspective() needs it apparently
        minX = Math.max(0, minX-3-(maxX-minX)*extraPadding);
        minY = Math.max(0, minY-3-(maxY-minY)*extraPadding);
        maxX = Math.min(maskCL[pyramidLevel].width,  maxX+3+(maxX-minX)*extraPadding);
        maxY = Math.min(maskCL[pyramidLevel].height, maxY+3+(maxY-minY)*extraPadding);

        // there seems to be something funny with memory alignment and
        // ROIs, so let's align our ROI to a 16 byte boundary just in case..
        inputData.roiX = Math.max(0, (int)Math.floor(minX/16)*16);
        inputData.roiY = Math.max(0, (int)Math.floor(minY));
        inputData.roiWidth  = Math.min(maskCL[pyramidLevel].width,  (int)Math.ceil(maxX/16)*16) - inputData.roiX;
        inputData.roiHeight = Math.min(maskCL[pyramidLevel].height, (int)Math.ceil(maxY))       - inputData.roiY;
//        inputData.roiX      = 0;
//        inputData.roiY      = 0;
//        inputData.roiWidth  = mask[pyramidLevel].width;
//        inputData.roiHeight = mask[pyramidLevel].height;

//System.out.println(roi);
    }

    @Override protected void doResidual() {
        parameters.getConstraintError();

        inputData.zeroThreshold = 0;
        inputData.outlierThreshold = 0;
        inputData.pyramidLevel = pyramidLevel;
        context.acquireGLObject(maskCL[pyramidLevel]);
        ((ImageTransformerCL)transformer).transform(templateCL[pyramidLevel], targetCL[pyramidLevel], null,
                transformedCL[pyramidLevel], residualCL[pyramidLevel], maskCL[pyramidLevel],
                parametersArray, null, inputData, outputData);
        context.releaseGLObject(maskCL[pyramidLevel]);

        outputData.readBuffer(context);
        if (outputData.dstCount < parameters.size()) {
            RMSE = Double.NaN;
        } else {
            RMSE = Math.sqrt(outputData.dstDstDot.get(0)/outputData.dstCount);
//            RMSE = outputData.dstDstDot.get(0)/outputData.dstCount;
        }
//        if (Double.isNaN(RMSE)) {
//System.out.println("dstCount " + outputData.dstCount + " RMSE " + RMSE + " " + pyramidLevel);
//        }
        residualUpdateNeeded = false;
    }
}
