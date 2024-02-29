/*
 * Copyright (C) 2011-2012 Samuel Audet
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

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLEventList;
import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.CLKernel;
import java.nio.FloatBuffer;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveTransformerCL extends ProjectiveTransformer implements ImageTransformerCL {
    public ProjectiveTransformerCL(JavaCVCL context) {
        this(context, null, null, null, null, null, new double[0], null);
    }
    public ProjectiveTransformerCL(JavaCVCL context, double[] referencePoints) {
        this(context, null, null, null, null, null, referencePoints, null);
    }
    public ProjectiveTransformerCL(JavaCVCL context, ProjectiveDevice d1, ProjectiveDevice d2, CvMat n,
            double[] referencePoints1, double[] referencePoints2) {
        // assuming d1 has identity values, use d2's stuff directly
        this(context, d1.cameraMatrix, d2.cameraMatrix, d2.R, d2.T, n, referencePoints1, referencePoints2);
    }
    public ProjectiveTransformerCL(JavaCVCL context, CvMat K1, CvMat K2, CvMat R, CvMat t, CvMat n,
            double[] referencePoints1, double[] referencePoints2) {
        super(K1, K2, R, t, n, referencePoints1, referencePoints2);
        final int dotSize = createParameters().size();
        this.context = context;
        this.HBuffer = context.getCLContext().createFloatBuffer(dotSize*9,  CLBuffer.Mem.READ_ONLY);
        if (getClass() == ProjectiveTransformerCL.class) {
            CLKernel[] kernels = context.buildKernels(
                    JavaCVCL.fastCompilerOptions + " -DDOT_SIZE=" + dotSize,
                    "ImageTransformer.cl:ProjectiveTransformer.cl",
                    "transformOne", "transformSub", "transformDot", "reduceOutputData");
            this.oneKernel    = kernels[0];
            this.subKernel    = kernels[1];
            this.dotKernel    = kernels[2];
            this.reduceKernel = kernels[3];
        }
    }

    protected final JavaCVCL context;
    protected final CLBuffer<FloatBuffer> HBuffer;
    private CLKernel oneKernel, subKernel, dotKernel, reduceKernel;

    public JavaCVCL getContext() {
        return context;
    }

    protected void prepareHomographies(CLBuffer HBuffer, int pyramidLevel, 
            ImageTransformer.Parameters[] parameters, boolean[] inverses) {
        FloatBuffer floatH = (FloatBuffer)HBuffer.getBuffer().rewind();
        CvMat H = H3x3.get();
        for (int i = 0; i < parameters.length; i++) {
            prepareHomography(H, pyramidLevel, (ProjectiveTransformer.Parameters)parameters[i],
                    inverses == null ? false : inverses[i]);
            for (int j = 0; j < 9; j++) {
                floatH.put((float)H.get(j));
            }
        }
        floatH.rewind();
    }

    public void transform(CLImage2d srcImg, CLImage2d subImg, CLImage2d srcDotImg,
            CLImage2d transImg, CLImage2d dstImg, CLImage2d maskImg,
            ImageTransformer.Parameters[] parameters, boolean[] inverses,
            InputData inputData, OutputData outputData) {
        prepareHomographies(HBuffer, inputData.pyramidLevel, parameters, inverses);

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
        if (inputData.autoWrite) {
            inputData.writeBuffer(context);
        }
        CLKernel kernel = null;
        if (subImg == null) {
            assert parameters.length == 1;
            kernel = oneKernel.putArg(srcImg).putArg(dstImg == null ? transImg : dstImg).putArg(maskImg)
                    .putArg(HBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
        } else if (srcDotImg == null) {
            assert parameters.length == 1;
            kernel = subKernel.putArg(srcImg).putArg(subImg).putArg(transImg).putArg(dstImg).putArg(maskImg)
                    .putArg(HBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
        } else {
            assert parameters.length == dotSize;
            kernel = dotKernel.putArg(srcImg).putArg(subImg).putArg(srcDotImg).putArg(maskImg)
                    .putArg(HBuffer).putArg(inputBuffer).putArg(outputBuffer).rewind();
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
//        System.out.println(kernel + " " + (event.getProfilingInfo(CLEvent.ProfilingCommand.END) -
//                            event.getProfilingInfo(CLEvent.ProfilingCommand.START))/1000000.0);

//        long res = q.getDevice().getProfilingTimerResolution();
//        System.out.println(res);
    }
}
