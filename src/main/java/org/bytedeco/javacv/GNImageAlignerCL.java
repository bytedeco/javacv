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

import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImageFormat;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opencl.gl.CLGLImage2d;
import java.util.Arrays;
import javax.media.opengl.GL2;
import org.bytedeco.javacv.ImageTransformer.Parameters;
import org.bytedeco.javacv.ImageTransformerCL.InputData;
import org.bytedeco.javacv.ImageTransformerCL.OutputData;

import static org.bytedeco.javacpp.opencv_core.*;

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
        super(transformer, initialParameters);
        setSettings(settings);
        context = transformer.getContext();

        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

        this.template    = new IplImage[maxLevel+1];
        this.target      = new IplImage[maxLevel+1];
        this.transformed = new IplImage[maxLevel+1];
        this.residual    = new IplImage[maxLevel+1];
        this.mask        = new IplImage[maxLevel+1];

        this.templateCL    = new CLImage2d[maxLevel+1];
        this.targetCL      = new CLImage2d[maxLevel+1];
        this.transformedCL = new CLImage2d[maxLevel+1];
        this.residualCL    = new CLImage2d[maxLevel+1];
        this.maskCL        = new CLGLImage2d[maxLevel+1];
        this.maskrb        = new int[maxLevel+1];
        this.maskfb        = new int[maxLevel+1];
        int w = template0 != null ? template0.width  : target0.width;
        int h = template0 != null ? template0.height : target0.height;
        CLGLContext c = context.getCLGLContext();
//        GLContext glContext = c.getGLContext();
//        glContext.makeCurrent();
        GL2 gl = context.getGL2();
        gl.glGenRenderbuffers(maxLevel+1, maskrb, 0);
        gl.glGenFramebuffers(maxLevel+1, maskfb, 0);
        CLImageFormat f = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
        for (int i = minLevel; i <= maxLevel; i++) {
            templateCL   [i] = i == minLevel && template0 != null ? template0 : c.createImage2d(w, h, f);
            targetCL     [i] = i == minLevel && target0   != null ? target0   : c.createImage2d(w, h, f);
            transformedCL[i] = c.createImage2d(w, h, f);
            residualCL   [i] = c.createImage2d(w, h, f);
            gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, maskrb[i]);
            gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, maskfb[i]);
            gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_LUMINANCE8, w, h);
            gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, maskrb[i]);
            assert gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER) == GL2.GL_FRAMEBUFFER_COMPLETE;
            maskCL[i] = c.createFromGLRenderbuffer(maskrb[i]);
            System.out.println(maskCL[i] + " " + maskCL[i].getElementSize() + " " + maskCL[i].getFormat());
            w /= 2;
            h /= 2;
        }
//        glContext.release();

        this.inputData = new InputData();
        this.outputData = new OutputData(false);
        this.templateChanged = new boolean[maxLevel+1];
        Arrays.fill(templateChanged, true);

        setConstrained(settings.constrained);
        setTemplateImageCL(template0, roiPts);
        setTargetImageCL(target0);
    }
    public void release() {
        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

        if (templateCL != null && targetCL != null && transformedCL != null &&
                residualCL != null && maskCL != null) {
            for (int i = minLevel; i <= maxLevel; i++) {
                if (i > minLevel) templateCL[i].release();
                if (i > minLevel) targetCL  [i].release();
                transformedCL[i].release();
                residualCL   [i].release();
                maskCL       [i].release();
            }
            templateCL = targetCL = transformedCL = residualCL = maskCL = null;
        }

        // NVIDIA drivers crash if we don't delete those before terminating
        context.getGLContext().makeCurrent();
        GL2 gl = context.getGL2();
        if (maskfb != null) {
            gl.glDeleteFramebuffers(maxLevel+1, maskfb, 0);
            maskfb = null;
        }
        if (maskrb != null) {
            gl.glDeleteRenderbuffers(maxLevel+1, maskrb, 0);
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
    private CLImage2d[] imagesCL = new CLImage2d[5];
    private InputData inputData;
    private OutputData outputData;
    private boolean[] templateChanged;

    @Override public IplImage getTemplateImage() {
        return getTemplateImage(true);
    }
    public IplImage getTemplateImage(boolean blocking) {
        if (templateChanged[pyramidLevel]) {
            templateChanged[pyramidLevel] = false;
            return template[pyramidLevel] = context.readImage(getTemplateImageCL(), template[pyramidLevel], blocking);
        } else {
            return template[pyramidLevel];
        }
    }
    @Override public void setTemplateImage(IplImage template0, double[] roiPts) {
        context.writeImage(templateCL[settings.pyramidLevelMin], template0, false);
        setTemplateImageCL(templateCL[settings.pyramidLevelMin], roiPts);
    }

    @Override public IplImage getTargetImage() {
        return getTargetImage(true);
    }
    public IplImage getTargetImage(boolean blocking) {
        return target[pyramidLevel] = context.readImage(getTargetImageCL(), target[pyramidLevel], blocking);
    }
    @Override public void setTargetImage(IplImage target0) {
        context.writeImage(targetCL[settings.pyramidLevelMin], target0, false);
        setTargetImageCL(targetCL[settings.pyramidLevelMin]);
    }

    @Override public IplImage getTransformedImage() {
        return getTransformedImage(true);
    }
    public IplImage getTransformedImage(boolean blocking) {
        return transformed[pyramidLevel] = context.readImage(getTransformedImageCL(), transformed[pyramidLevel], blocking);
    }

    @Override public IplImage getResidualImage() {
        return getResidualImage(true);
    }
    public IplImage getResidualImage(boolean blocking) {
        return residual[pyramidLevel] = context.readImage(getResidualImageCL(), residual[pyramidLevel], blocking);
    }

    @Override public IplImage getMaskImage() {
        return getMaskImage(true);
    }
    public IplImage getMaskImage(boolean blocking) {
        context.acquireGLObject(maskCL[pyramidLevel]);
        mask[pyramidLevel] = context.readImage(getMaskImageCL(), mask[pyramidLevel], blocking);
        context.releaseGLObject(maskCL[pyramidLevel]);
        return mask[pyramidLevel];
    }

    @Override public double getRMSE() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return RMSE;
    }

    @Override public int getPixelCount() {
        if (residualUpdateNeeded) {
            doRoi();
            doResidual();
        }
        return outputData.dstCount;
    }

    @Override public int getOutlierCount() {
        return outputData.dstCountOutlier;
    }

    @Override public CvRect getRoi() {
        if (residualUpdateNeeded) {
            doRoi();
        }
        return roi.x(inputData.roiX).y(inputData.roiY).
                width(inputData.roiWidth).height(inputData.roiHeight);
    }

    @Override public IplImage[] getImages() {
        return getImages(true);
    }
    public IplImage[] getImages(boolean blocking) {
        images[0] = getTemplateImage(false);
        images[1] = getTargetImage(false);
        images[2] = getTransformedImage(false);
        images[3] = getResidualImage(false);
        images[4] = getMaskImage(blocking);
        return images;
    }

    public CLImage2d getTemplateImageCL() {
        return templateCL[pyramidLevel];
    }
    public void setTemplateImageCL(CLImage2d template0, double[] roiPts) {
        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

        if (roiPts == null && template0 != null) {
            int w = template0.width  << minLevel;
            int h = template0.height << minLevel;
            this.srcRoiPts.put(0.0, 0.0,  w, 0.0,  w, h,  0, h);
        } else {
            this.srcRoiPts.put(roiPts);
        }

        if (template0 == null) {
            return;
        }

//        if (templateCL == null || templateCL.length != settings.pyramidLevels) {
//            templateCL = new CLImage2d[settings.pyramidLevels];
//        }
        templateCL[minLevel] = template0;
        for (int i = minLevel+1; i <= maxLevel; i++) {
//            if (templateCL[i] == null) {
//                int w = templateCL[i-1].width/2;
//                int h = templateCL[i-1].height/2;
//                CLImageFormat format = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
//                templateCL[i] = JavaCVCL.getCLContext().createImage2d(w, h, format);
//            }
            context.pyrDown(templateCL[i-1], templateCL[i]);
        }
        setPyramidLevel(maxLevel);
        Arrays.fill(templateChanged, true);
    }

    public CLImage2d getTargetImageCL() {
        return targetCL[pyramidLevel];
    }
    public void setTargetImageCL(CLImage2d target0) {
        final int minLevel = settings.pyramidLevelMin;
        final int maxLevel = settings.pyramidLevelMax;

//        if (targetCL == null || targetCL.length != settings.pyramidLevels) {
//            targetCL = new CLImage2d[settings.pyramidLevels];
//        }
        targetCL[minLevel] = target0;
        for (int i = minLevel+1; i <= maxLevel; i++) {
//            if (targetCL[i] == null) {
//                int w = targetCL[i-1].width/2;
//                int h = targetCL[i-1].height/2;
//                CLImageFormat format = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
//                targetCL[i] = JavaCVCL.getCLContext().createImage2d(w, h, format);
//            }
            context.pyrDown(targetCL[i-1], targetCL[i]);
        }
        setPyramidLevel(maxLevel);
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

    public CLImage2d[] getImagesCL() {
        imagesCL[0] = templateCL   [pyramidLevel];
        imagesCL[1] = targetCL     [pyramidLevel];
        imagesCL[2] = transformedCL[pyramidLevel];
        imagesCL[3] = residualCL   [pyramidLevel];
        imagesCL[4] = maskCL       [pyramidLevel];
        return imagesCL;
    }

    @Override protected void doHessianGradient(final double[] scale) {
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

        inputData.zeroThreshold    = settings.thresholdsZero   [Math.min(settings.thresholdsZero   .length-1, pyramidLevel)];
        inputData.outlierThreshold = settings.thresholdsOutlier[Math.min(settings.thresholdsOutlier.length-1, pyramidLevel)];
        if (settings.thresholdsMulRMSE) {
            inputData.zeroThreshold    *= RMSE;
            inputData.outlierThreshold *= RMSE;
        }
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

    @Override protected void doRoi() {
        transformer.transform(srcRoiPts, dstRoiPts, parameters, false);
        double[] pts = dstRoiPts.get();
        for (int i = 0; i < pts.length; i++) {
            pts[i] /= (1<<pyramidLevel);
        }
        roi.x(0).y(0).width(maskCL[pyramidLevel].width).height(maskCL[pyramidLevel].height);
        // Add +3 all around because cvWarpPerspective() needs it for interpolation,
        // and there seems to be something funny with memory alignment and
        // ROIs, so let's align our ROI to a 16 byte boundary just in case..
        JavaCV.boundingRect(pts, roi, 3, 3, 16, 1);
//System.out.println(roi);
        inputData.roiX = roi.x();
        inputData.roiY = roi.y();
        inputData.roiWidth  = roi.width();
        inputData.roiHeight = roi.height();

//        GLContext glContext = context.getGLContext();
//        glContext.makeCurrent();
        GL2 gl = context.getGL2();
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, maskfb[pyramidLevel]);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        context.getGLU().gluOrtho2D(0.0f, maskCL[pyramidLevel].width, 0.0f, maskCL[pyramidLevel].height);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glViewport(0, 0, maskCL[pyramidLevel].width, maskCL[pyramidLevel].height);

        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glColor4f(1, 1, 1, 1);
        gl.glBegin(GL2.GL_POLYGON);
// XXX: Remove "extra" pixels here...
            gl.glVertex2d(pts[0],   pts[1]);
            gl.glVertex2d(pts[2]+1, pts[3]);
            gl.glVertex2d(pts[4]+1, pts[5]+1);
            gl.glVertex2d(pts[6],   pts[7]+1);
        gl.glEnd();
//        gl.glFinish();
//        glContext.release();
    }

    @Override protected void doResidual() {
        parameters.getConstraintError();

//        inputData.zeroThreshold = 0;
//        inputData.outlierThreshold = 0;
        inputData.zeroThreshold    = settings.thresholdsZero   [Math.min(settings.thresholdsZero   .length-1, pyramidLevel)];
        inputData.outlierThreshold = settings.thresholdsOutlier[Math.min(settings.thresholdsOutlier.length-1, pyramidLevel)];
        if (settings.thresholdsMulRMSE) {
            inputData.zeroThreshold    *= RMSE;
            inputData.outlierThreshold *= RMSE;
        }
        inputData.pyramidLevel = pyramidLevel;
        context.acquireGLObject(maskCL[pyramidLevel]);
        ((ImageTransformerCL)transformer).transform(templateCL[pyramidLevel], targetCL[pyramidLevel], null,
                transformedCL[pyramidLevel], residualCL[pyramidLevel], maskCL[pyramidLevel],
                parametersArray, null, inputData, outputData);
        context.releaseGLObject(maskCL[pyramidLevel]);

        outputData.readBuffer(context);
        double dstDstDot = outputData.dstDstDot.get(0);
        int dstCount = outputData.dstCount;
        RMSE = dstCount < n ? Double.NaN : Math.sqrt(dstDstDot/dstCount);
//        if (Double.isNaN(RMSE)) {
//System.out.println("dstCount " + outputData.dstCount + " RMSE " + RMSE + " " + pyramidLevel);
//        }
        residualUpdateNeeded = false;
    }
}
