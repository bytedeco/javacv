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

package org.bytedeco.javacv;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLImage2d;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author Samuel Audet
 */
public interface ImageTransformerCL extends ImageTransformer {

    public class InputData {
        public InputData() { this(true); }
        public InputData(boolean autoWrite) { this.autoWrite = autoWrite; }

        public int pyramidLevel = 0;
        public int roiX = 0, roiY = 0, roiWidth = 0, roiHeight = 0;
        public double zeroThreshold = 0, outlierThreshold = 0;

        CLBuffer<ByteBuffer> buffer = null;
        boolean autoWrite = true;

        CLBuffer<ByteBuffer> getBuffer(JavaCVCL context) {
            int structSize = 4*4;
            if (buffer == null || buffer.getCLSize() < structSize) {
                if (buffer != null) buffer.release();
                buffer = context.getCLContext().createByteBuffer(structSize, CLBuffer.Mem.READ_ONLY);
            }
            return buffer;
        }

        public CLBuffer<ByteBuffer> writeBuffer(JavaCVCL context) {
            getBuffer(context);
            ByteBuffer byteBuffer = (ByteBuffer)buffer.getBuffer().rewind();
            byteBuffer.putInt(roiY).putInt(roiHeight).putFloat((float)zeroThreshold)
                    .putFloat((float)outlierThreshold).rewind();
            context.writeBuffer(buffer, false); // upload input data
            return buffer;
        }
    }

    public class OutputData {
        public OutputData() { this(true); }
        public OutputData(boolean autoRead) { this.autoRead = autoRead; }

        public int dstCount = 0, dstCountZero = 0, dstCountOutlier = 0;
        public FloatBuffer srcDstDot = null, dstDstDot = null;

        CLBuffer<ByteBuffer> buffer = null;
        boolean autoRead = true;

        CLBuffer<ByteBuffer> getBuffer(JavaCVCL context, int dotSize, int reduceSize) {
            int structSize = 4*(4 + dotSize + dotSize*dotSize);
            if (buffer == null || buffer.getCLSize() < structSize*reduceSize) {
                if (buffer != null) buffer.release();
                buffer = context.getCLContext().createByteBuffer(structSize*reduceSize);
                ByteBuffer byteBuffer = buffer.getBuffer();
                byteBuffer.position(4*4);             srcDstDot = byteBuffer.asFloatBuffer();
                byteBuffer.position(4*(4 + dotSize)); dstDstDot = byteBuffer.asFloatBuffer();
                byteBuffer.rewind();
            }
            return buffer;
        }

        public CLBuffer<ByteBuffer> readBuffer(JavaCVCL context) {
            //getBuffer(context, dotSize, reduceSize);
            context.readBuffer(buffer, true); // read results back (blocking read)
            ByteBuffer byteBuffer = buffer.getBuffer();
            dstCount        = byteBuffer.getInt(4);
            dstCountZero    = byteBuffer.getInt(8);
            dstCountOutlier = byteBuffer.getInt(12);
            return buffer;
        }
    }

    JavaCVCL getContext();

    void transform(CLImage2d srcImg, CLImage2d subImg, CLImage2d srcDotImg, CLImage2d transImg, CLImage2d dstImg,
            CLImage2d mask, ImageTransformer.Parameters[] parameters, boolean[] inverses, InputData inputData, OutputData outputData);
}
