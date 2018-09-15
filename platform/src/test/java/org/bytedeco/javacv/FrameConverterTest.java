/*
 * Copyright (C) 2015-2016 Samuel Audet
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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.lept.PIX;
import org.junit.Test;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.junit.Assert.*;

/**
 * Test cases for FrameConverter classes. Also uses other classes from JavaCV.
 *
 * @author Samuel Audet
 */
public class FrameConverterTest {

    @Test public void testAndroidFrameConverter() {
        System.out.println("AndroidFrameConverter");

        AndroidFrameConverter converter = new AndroidFrameConverter();

        int width = 512;
        int height = 1024;
        byte[] yuvData = new byte[3 * width * height / 2];
        for (int i = 0; i < yuvData.length; i++) {
            yuvData[i] = (byte)i;
        }
        Mat yuvImage = new Mat(3 * height / 2, width, CV_8UC1, new BytePointer(yuvData));
        Mat bgrImage = new Mat(height, width, CV_8UC3);
        cvtColor(yuvImage, bgrImage, CV_YUV2BGR_NV21);
        Frame bgrFrame = converter.convert(yuvData, width, height);

        UByteIndexer bgrImageIdx = bgrImage.createIndexer();
        UByteIndexer bgrFrameIdx = bgrFrame.createIndexer();
        assertEquals(bgrImageIdx.rows(), bgrFrameIdx.rows());
        assertEquals(bgrImageIdx.cols(), bgrFrameIdx.cols());
        assertEquals(bgrImageIdx.channels(), bgrFrameIdx.channels());
        for (int i = 0; i < bgrImageIdx.rows(); i++) {
            for (int j = 0; j < bgrImageIdx.cols(); j++) {
                for (int k = 0; k < bgrImageIdx.channels(); k++) {
                    assertEquals((float)bgrImageIdx.get(i, j, k), (float)bgrFrameIdx.get(i, j, k), 1.0f);
                }
            }
        }
        bgrImageIdx.release();
        bgrFrameIdx.release();

        Frame grayFrame = new Frame(1024 + 1, 768, Frame.DEPTH_UBYTE, 1);
        Frame colorFrame = new Frame(640 + 1, 480, Frame.DEPTH_UBYTE, 3);

        UByteIndexer grayFrameIdx = grayFrame.createIndexer();
        for (int i = 0; i < grayFrameIdx.rows(); i++) {
            for (int j = 0; j < grayFrameIdx.cols(); j++) {
                grayFrameIdx.put(i, j, i + j);
            }
        }

        UByteIndexer colorFrameIdx = colorFrame.createIndexer();
        for (int i = 0; i < colorFrameIdx.rows(); i++) {
            for (int j = 0; j < colorFrameIdx.cols(); j++) {
                for (int k = 0; k < colorFrameIdx.channels(); k++) {
                    colorFrameIdx.put(i, j, k, i + j + k);
                }
            }
        }

        width = grayFrame.imageWidth;
        height = grayFrame.imageHeight;
        int stride = grayFrame.imageStride;
        int rowBytes = width * 4;
        ByteBuffer in = (ByteBuffer)grayFrame.image[0];
        ByteBuffer buffer = converter.gray2rgba(in, width, height, stride, rowBytes);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // GRAY -> RGBA
                byte B = in.get(y * stride + x);
                assertEquals(buffer.get(y * rowBytes + 4 * x    ), B);
                assertEquals(buffer.get(y * rowBytes + 4 * x + 1), B);
                assertEquals(buffer.get(y * rowBytes + 4 * x + 2), B);
                assertEquals(buffer.get(y * rowBytes + 4 * x + 3), (byte)0xFF);
            }
        }

        width = colorFrame.imageWidth;
        height = colorFrame.imageHeight;
        stride = colorFrame.imageStride;
        rowBytes = width * 4;
        in = (ByteBuffer)colorFrame.image[0];
        buffer = converter.bgr2rgba(in, width, height, stride, rowBytes);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // BGR -> RGBA
                byte B = in.get(y * stride + 3 * x    );
                byte G = in.get(y * stride + 3 * x + 1);
                byte R = in.get(y * stride + 3 * x + 2);
                assertEquals(buffer.get(y * rowBytes + 4 * x    ), R);
                assertEquals(buffer.get(y * rowBytes + 4 * x + 1), G);
                assertEquals(buffer.get(y * rowBytes + 4 * x + 2), B);
                assertEquals(buffer.get(y * rowBytes + 4 * x + 3), (byte)0xFF);
            }
        }

        colorFrameIdx.release();
        grayFrameIdx.release();
    }

    @Test public void testJava2DFrameConverter() {
        System.out.println("Java2DFrameConverter");

        int[] depths = {Frame.DEPTH_UBYTE, Frame.DEPTH_SHORT, Frame.DEPTH_FLOAT};
        int[] channels = {1, 3, 4};
        for (int i = 0; i < depths.length; i++) {
            for (int j = 0; j < channels.length; j++) {
                Frame frame = new Frame(640 + 1, 480, depths[i], channels[j]);
                Java2DFrameConverter converter = new Java2DFrameConverter();

                Indexer frameIdx = frame.createIndexer();
                for (int y = 0; y < frameIdx.rows(); y++) {
                    for (int x = 0; x < frameIdx.cols(); x++) {
                        for (int z = 0; z < frameIdx.channels(); z++) {
                            frameIdx.putDouble(new long[] {y, x, z}, y + x + z);
                        }
                    }
                }

                BufferedImage image = converter.convert(frame);
                converter.frame = null;
                Frame frame2 = converter.convert(image);

                Indexer frame2Idx = frame2.createIndexer();
                for (int y = 0; y < frameIdx.rows(); y++) {
                    for (int x = 0; x < frameIdx.cols(); x++) {
                        for (int z = 0; z < frameIdx.channels(); z++) {
                            double value = frameIdx.getDouble(y, x, z);
                            assertEquals(value, frame2Idx.getDouble(y, x, z), 0);
                        }
                    }
                }

                try {
                    frame2Idx.getDouble(frameIdx.rows() + 1, frameIdx.cols() + 1);
                    fail("IndexOutOfBoundsException should have been thrown.");
                } catch (IndexOutOfBoundsException e) { }

                frameIdx.release();
                frame2Idx.release();
            }
        }

        int[] types = {BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB,
                       BufferedImage.TYPE_INT_ARGB_PRE, BufferedImage.TYPE_INT_BGR};
        for (int i = 0; i < types.length; i++) {
            BufferedImage image = new BufferedImage(640 + 1, 480, types[i]);
            Java2DFrameConverter converter = new Java2DFrameConverter();

            WritableRaster raster = image.getRaster();
            int[] array = ((DataBufferInt)raster.getDataBuffer()).getData();
            for (int j = 0; j < array.length; j++) {
                array[j] = j;
            }

            Frame frame = converter.convert(image);
            converter.bufferedImage = null;
            BufferedImage image2 = converter.convert(frame);

            WritableRaster raster2 = image2.getRaster();
            byte[] array2 = ((DataBufferByte)raster2.getDataBuffer()).getData();
            for (int j = 0; j < array.length; j++) {
                int n = ((array2[4 * j    ] & 0xFF) << 24) | ((array2[4 * j + 1] & 0xFF) << 16)
                      | ((array2[4 * j + 2] & 0xFF) << 8)  |  (array2[4 * j + 3] & 0xFF);
                assertEquals(array[j], n);
            }
        }
    }

    @Test public void testOpenCVFrameConverter() {
        System.out.println("OpenCVFrameConverter");
        Loader.load(org.bytedeco.javacpp.opencv_java.class);

        for (int depth = 8; depth <= 64; depth *= 2) {
            assertEquals(depth, OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getIplImageDepth(depth)));
            assertEquals(depth, OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getMatDepth(depth)));
            if (depth < 64) {
                assertEquals(-depth, OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getIplImageDepth(-depth)));
                assertEquals(-depth, OpenCVFrameConverter.getFrameDepth(OpenCVFrameConverter.getMatDepth(-depth)));
            }
        }

        Frame frame = new Frame(640 + 1, 480, Frame.DEPTH_UBYTE, 3);
        OpenCVFrameConverter.ToIplImage converter1 = new OpenCVFrameConverter.ToIplImage();
        OpenCVFrameConverter.ToMat converter2 = new OpenCVFrameConverter.ToMat();
        OpenCVFrameConverter.ToOrgOpenCvCoreMat converter3 = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();

        UByteIndexer frameIdx = frame.createIndexer();
        for (int i = 0; i < frameIdx.rows(); i++) {
            for (int j = 0; j < frameIdx.cols(); j++) {
                for (int k = 0; k < frameIdx.channels(); k++) {
                    frameIdx.put(i, j, k, i + j + k);
                }
            }
        }

        IplImage image = converter1.convert(frame);
        Mat mat = converter2.convert(frame);
        final org.opencv.core.Mat cvmat = converter3.convert(frame);

        converter1.frame = null;
        converter2.frame = null;
        converter3.frame = null;
        Frame frame1 = converter1.convert(image);
        Frame frame2 = converter2.convert(mat);
        Frame frame3 = converter3.convert(cvmat);
        assertEquals(frame2.opaque, mat);
        assertEquals(frame3.opaque, cvmat);

        Mat mat2 = new Mat(mat.rows(), mat.cols(), mat.type(), mat.data(), mat.step());
        org.opencv.core.Mat cvmat2 = new org.opencv.core.Mat(cvmat.rows(), cvmat.cols(), cvmat.type(),
                new BytePointer() { { address = cvmat.dataAddr(); } }.capacity(cvmat.rows() * cvmat.cols() * cvmat.elemSize()).asByteBuffer());
        assertNotEquals(mat, mat2);
        assertNotEquals(cvmat, cvmat2);

        frame2 = converter2.convert(mat2);
        frame3 = converter3.convert(cvmat2);
        assertEquals(frame2.opaque, mat2);
        assertEquals(frame3.opaque, cvmat2);

        // official Java API does not support memory aligned strides...
        assertEquals(cvmat2.cols() * cvmat2.channels(), frame3.imageStride);
        frame3.imageStride = frame2.imageStride;

        UByteIndexer frame1Idx = frame1.createIndexer();
        UByteIndexer frame2Idx = frame2.createIndexer();
        UByteIndexer frame3Idx = frame3.createIndexer();
        for (int i = 0; i < frameIdx.rows(); i++) {
            for (int j = 0; j < frameIdx.cols(); j++) {
                for (int k = 0; k < frameIdx.channels(); k++) {
                    int b = frameIdx.get(i, j, k);
                    assertEquals(b, frame1Idx.get(i, j, k));
                    assertEquals(b, frame2Idx.get(i, j, k));
                    if (i < frameIdx.rows() - 2) {
                        // ... so also cannot access most of the 2 last rows
                        assertEquals(b, frame3Idx.get(i, j, k));
                    }
                }
            }
        }

        try {
            frame1Idx.get(frameIdx.rows() + 1, frameIdx.cols() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        try {
            frame2Idx.get(frameIdx.rows() + 1, frameIdx.cols() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        try {
            frame3Idx.get(frameIdx.rows() + 1, frameIdx.cols() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        frameIdx.release();
        frame1Idx.release();
        frame2Idx.release();
        frame3Idx.release();
    }

    @Test public void testLeptonicaFrameConverter() {
        System.out.println("LeptonicaFrameConverter");

        Frame frame = new Frame(640 + 1, 480, Frame.DEPTH_UBYTE, 3);
        LeptonicaFrameConverter converter = new LeptonicaFrameConverter();

        UByteIndexer frameIdx = frame.createIndexer();
        for (int i = 0; i < frameIdx.rows(); i++) {
            for (int j = 0; j < frameIdx.cols(); j++) {
                for (int k = 0; k < frameIdx.channels(); k++) {
                    frameIdx.put(i, j, k, i + j + k);
                }
            }
        }

        PIX pix = converter.convert(frame);

        converter.frame = null;
        Frame frame1 = converter.convert(pix);
        assertEquals(frame1.opaque, pix);

        PIX pix2 = PIX.createHeader(pix.w(), pix.h(), pix.d()).data(pix.data()).wpl(pix.wpl());
        assertNotEquals(pix, pix2);

        Frame frame2 = converter.convert(pix2);
        assertEquals(frame2.opaque, pix2);

        IntBuffer frameBuf = ((ByteBuffer)frame.image[0].position(0)).asIntBuffer();
        IntBuffer frame1Buf = ((ByteBuffer)frame1.image[0].position(0)).asIntBuffer();
        IntBuffer frame2Buf = ((ByteBuffer)frame2.image[0].position(0)).asIntBuffer();
        IntBuffer pixBuf = pix.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        IntBuffer pix2Buf = pix2.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        for (int i = 0; i < frameBuf.capacity(); i++) {
            int j = frameBuf.get(i);
            assertEquals(j, frame1Buf.get(i));
            assertEquals(j, frame2Buf.get(i));
            assertEquals(j, pixBuf.get(i));
            assertEquals(j, pix2Buf.get(i));
        }

        try {
            frame1Buf.get(frameBuf.capacity() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        try {
            frame2Buf.get(frameBuf.capacity() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        try {
            pixBuf.get(frameBuf.capacity() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        try {
            pix2Buf.get(frameBuf.capacity() + 1);
            fail("IndexOutOfBoundsException should have been thrown.");
        } catch (IndexOutOfBoundsException e) { }

        pix2.deallocate();
        pix.deallocate();
    }
}
