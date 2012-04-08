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

import com.jogamp.common.os.Platform;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLEventList;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLImageFormat;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import java.nio.FloatBuffer;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class ProCamTransformerCL extends ProCamTransformer implements ImageTransformerCL {
    public ProCamTransformerCL(JavaCVCL context, double[] referencePoints,
            CameraDevice camera, ProjectorDevice projector) {
        this(context, referencePoints, camera, projector, null);
    }
    public ProCamTransformerCL(JavaCVCL context, double[] referencePoints,
            CameraDevice camera, ProjectorDevice projector, CvMat n) {
        super(referencePoints, camera, projector, n);
        final int dotSize = createParameters().size();
        this.context  = context;
        this.nullSize = Platform.is32Bit() ? 4 : 8;
        this.H1Buffer = surfaceTransformer == null ? null :
                        context.getCLContext().createFloatBuffer(dotSize*9,  CLBuffer.Mem.READ_ONLY);
        this.H2Buffer = context.getCLContext().createFloatBuffer(dotSize*9,  CLBuffer.Mem.READ_ONLY);
        this.XBuffer  = context.getCLContext().createFloatBuffer(dotSize*16, CLBuffer.Mem.READ_ONLY);
        if (getClass() == ProCamTransformerCL.class) {
            CLKernel[] kernels = context.buildKernels(
                    JavaCVCL.fastCompilerOptions + " -cl-nv-maxrregcount=32 -DDOT_SIZE=" + dotSize,
                    //JavaCVCL.fastCompilerOptions + " -DDOT_SIZE=" + dotSize,
                    "ImageTransformer.cl:ProCamTransformer.cl",
                    "transformOne", "transformSub", "transformDot", "reduceOutputData");
            oneKernel    = kernels[0];
            subKernel    = kernels[1];
            dotKernel    = kernels[2];
            reduceKernel = kernels[3];
        }
    }

    private static final ThreadLocal<CvMat>
            H13x3 = CvMat.createThreadLocal(3, 3),
            H23x3 = CvMat.createThreadLocal(3, 3),
            X4x4  = CvMat.createThreadLocal(4, 4);

    protected final JavaCVCL context;
    protected final int nullSize;
    protected final CLBuffer<FloatBuffer> H1Buffer, H2Buffer, XBuffer;
    protected CLImage2d[] projectorImageCL = null, surfaceImageCL = null;
    private CLKernel oneKernel, subKernel, dotKernel, reduceKernel;

    public JavaCVCL getContext() {
        return context;
    }

    public ProjectiveColorTransformerCL getSurfaceTransformerCL() {
        return (ProjectiveColorTransformerCL)surfaceTransformer;
    }
    public ProjectiveColorTransformerCL getProjectorTransformerCL() {
        return (ProjectiveColorTransformerCL)projectorTransformer;
    }

    public CLImage2d getProjectorImageCL(int pyramidLevel) {
        return projectorImageCL[pyramidLevel];
    }
    public void setProjectorImageCL(CLImage2d projectorImage0, int minPyramidLevel, int maxPyramidLevel) {
        if (projectorImageCL == null || projectorImageCL.length != maxPyramidLevel+1) {
            projectorImageCL = new CLImage2d[maxPyramidLevel+1];
        }
        projectorImageCL[minPyramidLevel] = projectorImage0;
        for (int i = minPyramidLevel+1; i <= maxPyramidLevel; i++) {
            if (projectorImageCL[i] == null) {
                int w = projectorImageCL[i-1].width/2;
                int h = projectorImageCL[i-1].height/2;
                CLImageFormat format = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
                projectorImageCL[i] = context.getCLContext().createImage2d(w, h, format);
            }
            context.pyrDown(projectorImageCL[i-1], projectorImageCL[i]);
        }
    }
    public CLImage2d getSurfaceImageCL(int pyramidLevel) {
        return surfaceImageCL[pyramidLevel];
    }
    public void setSurfaceImageCL(CLImage2d surfaceImage0, int pyramidLevels) {
        if (surfaceImageCL == null || surfaceImageCL.length != pyramidLevels) {
            surfaceImageCL = new CLImage2d[pyramidLevels];
        }
        surfaceImageCL[0] = surfaceImage0;
        for (int i = 1; i < pyramidLevels; i++) {
            if (surfaceImageCL[i] == null) {
                int w = surfaceImageCL[i-1].width/2;
                int h = surfaceImageCL[i-1].height/2;
                CLImageFormat format = new CLImageFormat(CLImageFormat.ChannelOrder.RGBA, CLImageFormat.ChannelType.FLOAT);
                surfaceImageCL[i] = context.getCLContext().createImage2d(w, h, format);
            }
            context.pyrDown(surfaceImageCL[i-1], surfaceImageCL[i]);
        }
    }

    protected void prepareTransforms(CLBuffer H1Buffer, CLBuffer H2Buffer, CLBuffer XBuffer,
            int pyramidLevel, ImageTransformer.Parameters[] parameters) {
        FloatBuffer floatH1 = surfaceTransformer == null ? null : (FloatBuffer)H1Buffer.getBuffer().rewind();
        FloatBuffer floatH2 = (FloatBuffer)H2Buffer.getBuffer().rewind();
        FloatBuffer floatX  = (FloatBuffer) XBuffer.getBuffer().rewind();
        CvMat H1 = H13x3.get();
        CvMat H2 = H23x3.get();
        CvMat X  = X4x4.get();
        for (int i = 0; i < parameters.length; i++) {
            prepareTransforms(surfaceTransformer == null ? null : H1,
                    H2, X, pyramidLevel, (ProCamTransformer.Parameters)parameters[i]);
            for (int j = 0; j < 9; j++) {
                if (surfaceTransformer != null) {
                    floatH1.put((float)H1.get(j));
                }
                floatH2.put((float)H2.get(j));
            }
            for (int j = 0; j < 16; j++) {
                floatX.put((float)X.get(j));
            }
        }
        if (surfaceTransformer != null) {
            floatH1.rewind();
        }
        floatH2.rewind();
        floatX.rewind();
    }

    @Override public void transform(CLImage2d srcImg, CLImage2d subImg, CLImage2d srcDotImg,
            CLImage2d transImg, CLImage2d dstImg, CLImage2d maskImg,
            ImageTransformer.Parameters[] parameters, boolean[] inverses,
            InputData inputData, OutputData outputData) {
        if (inverses != null) {
            for (int i = 0; i < inverses.length; i++) {
                if (inverses[i]) {
                    throw new UnsupportedOperationException("Inverse transform not supported.");
                }
            }
        }

        prepareTransforms(H1Buffer, H2Buffer, XBuffer, inputData.pyramidLevel, parameters);

        final int dotSize = parameters[0].size();
        final int localSize = parameters.length > 1 ? parameters.length : (inputData.roiWidth > 32 ? 64 : 32);
        final int globalSize = JavaCVCL.alignCeil(inputData.roiWidth, localSize);
        final int reduceSize = globalSize/localSize;

        // allocate buffers if necessary
        CLBuffer inputBuffer = inputData.getBuffer(context);
        CLBuffer outputBuffer = outputData.getBuffer(context, dotSize, reduceSize);

        CLEventList list = new CLEventList(1);

        // setup kernel
        if (surfaceTransformer != null) {
            context.writeBuffer(H1Buffer, false); // upload H1
        }
        context.writeBuffer(H2Buffer, false); // upload H2
        context.writeBuffer(XBuffer, false); // upload X
        if (inputData.autoWrite) {
            inputData.writeBuffer(context);
        }
        CLImage2d srcImg2 = projectorImageCL[inputData.pyramidLevel];
        CLKernel kernel = null;
        if (subImg == null) {
            assert parameters.length == 1;
            kernel = oneKernel.putArg(srcImg2).putArg(srcImg).putArg(dstImg == null ? transImg : dstImg).putArg(maskImg).putArg(H2Buffer);
        } else if (srcDotImg == null) {
            assert parameters.length == 1;
            kernel = subKernel.putArg(srcImg2).putArg(srcImg).putArg(subImg).putArg(transImg).putArg(dstImg).putArg(maskImg).putArg(H2Buffer);
        } else {
            assert parameters.length == dotSize;
            kernel = dotKernel.putArg(srcImg2).putArg(srcImg).putArg(subImg).putArg(srcDotImg).putArg(maskImg).putArg(H2Buffer);
//System.out.println(kernel.getWorkGroupSize(context.getCLCommandQueue().getDevice()));
        }
        if (H1Buffer != null) { kernel.putArg(H1Buffer); } else { kernel.putNullArg(nullSize); }
        kernel.putArg(XBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
        context.executeKernel(kernel, inputData.roiX, 0, 0,
                globalSize, 1, parameters.length,
                localSize, 1, parameters.length, list); // execute program
        if (reduceSize > 1) {
            reduceKernel.putArg(outputBuffer).rewind();
            context.executeKernel(reduceKernel, 0, reduceSize, reduceSize);
        }
        if (outputData.autoRead) {
            outputData.readBuffer(context);
        }

//        CLEvent event = list.getEvent(0);
//        System.out.println((event.getProfilingInfo(CLEvent.ProfilingCommand.END) -
//                            event.getProfilingInfo(CLEvent.ProfilingCommand.START))/1000000.0);

//        long res = q.getDevice().getProfilingTimerResolution();
//        System.out.println(res);
    }
}
