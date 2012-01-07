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

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLEventList;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLKernel;
import java.nio.FloatBuffer;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveColorTransformerCL extends ProjectiveColorTransformer implements ImageTransformerCL {
    public ProjectiveColorTransformerCL(JavaCVCL context, CvMat K1, CvMat K2, CvMat R, CvMat t,
            CvMat n, double[] referencePoints1, double[] referencePoints2,
            CvMat X, int numGains, int numBiases) {
        super(K1, K2, R, t, n, referencePoints1, referencePoints2, X, numGains, numBiases);
        final int dotSize = createParameters().size();
        this.context = context;
        this.HBuffer = context.getCLContext().createFloatBuffer(dotSize*9,  CLBuffer.Mem.READ_ONLY);
        this.XBuffer = context.getCLContext().createFloatBuffer(dotSize*16, CLBuffer.Mem.READ_ONLY);
        CLKernel[] kernels = context.buildKernels(
                JavaCVCL.fastCompilerOptions + " -DDOT_SIZE=" + dotSize,
                "ImageTransformer.cl:ProjectiveColorTransformer.cl",
                "transformOne", "transformSub", "transformDot", "reduceOutputData");
        oneKernel    = kernels[0];
        subKernel    = kernels[1];
        dotKernel    = kernels[2];
        reduceKernel = kernels[3];
    }

    protected final JavaCVCL context;
    protected final CLBuffer<FloatBuffer> HBuffer, XBuffer;
    private final CLKernel oneKernel, subKernel, dotKernel, reduceKernel;

    public JavaCVCL getContext() {
        return context;
    }

    protected void prepareHomographies(CLBuffer HBuffer, int pyramidLevel,
            ImageTransformer.Parameters[] parameters, boolean[] inverses) {
        FloatBuffer floatH = (FloatBuffer)HBuffer.getBuffer().rewind();
        CvMat H = H3x3.get();
        for (int i = 0; i < parameters.length; i++) {
            prepareHomography(H, pyramidLevel, (ProjectiveColorTransformer.Parameters)parameters[i],
                    inverses == null ? false : inverses[i]);
            for (int j = 0; j < 9; j++) {
                floatH.put((float)H.get(j));
            }
        }
        floatH.rewind();
    }

    protected void prepareColorTransforms(CLBuffer XBuffer, int pyramidLevel,
            ImageTransformer.Parameters[] parameters, boolean[] inverses) {
        FloatBuffer floatX = (FloatBuffer)XBuffer.getBuffer().rewind();
        CvMat X2 = X24x4.get();
        for (int i = 0; i < parameters.length; i++) {
            prepareColorTransform(X2, pyramidLevel, (ProjectiveColorTransformer.Parameters)parameters[i],
                    inverses == null ? false : inverses[i]);
            for (int j = 0; j < 16; j++) {
                floatX.put((float)X2.get(j));
            }
        }
        floatX.rewind();
    }

    @Override public void transform(CLImage2d srcImg, CLImage2d subImg, CLImage2d srcDotImg,
            CLImage2d transImg, CLImage2d dstImg, CLImage2d maskImg,
            ImageTransformer.Parameters[] parameters, boolean[] inverses,
            InputData inputData, OutputData outputData) {
        prepareHomographies(HBuffer, inputData.pyramidLevel, parameters, inverses);
        prepareColorTransforms(XBuffer, inputData.pyramidLevel, parameters, inverses);

        final int dotSize = parameters[0].size();
        final int localSize = parameters.length > 1 ? parameters.length : (inputData.roiWidth > 32 ? 64 : 32);
        final int globalSize = JavaCVCL.alignCeil(inputData.roiWidth, localSize);
        final int reduceSize = globalSize/localSize;

        // allocate buffers if necessary
        CLBuffer inputBuffer = inputData.getBuffer(context);
        CLBuffer outputBuffer = outputData.getBuffer(context, dotSize, reduceSize);

        CLEventList list = new CLEventList(1);

        // setup kernel
        context.writeBuffer(HBuffer, false); // upload H
        context.writeBuffer(XBuffer, false); // upload X
        if (inputData.autoWrite) {
            inputData.writeBuffer(context);
        }
        CLKernel kernel = null;
        if (subImg == null) {
            assert parameters.length == 1;
            kernel = oneKernel.putArg(srcImg).putArg(dstImg == null ? transImg : dstImg).putArg(maskImg)
                    .putArg(HBuffer).putArg(XBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
        } else if (srcDotImg == null) {
            assert parameters.length == 1;
            kernel = subKernel.putArg(srcImg).putArg(subImg).putArg(transImg).putArg(dstImg).putArg(maskImg)
                    .putArg(HBuffer).putArg(XBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
        } else {
            assert parameters.length == dotSize;
            kernel = dotKernel.putArg(srcImg).putArg(subImg).putArg(srcDotImg).putArg(maskImg)
                    .putArg(HBuffer).putArg(XBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
        }
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
