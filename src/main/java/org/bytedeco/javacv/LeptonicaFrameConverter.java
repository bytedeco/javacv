/*
 * Copyright (C) 2018-2021 Samuel Audet
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

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.leptonica.PIX;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.bytedeco.leptonica.global.leptonica.*;

/**
 * A utility class to map data between {@link Frame} and {@link PIX},
 * which is the image data structure used by Tesseract.
 * Currently supports only plain PIX images, not FPIX or DPIX ones.
 *
 * @author Samuel Audet
 */
public class LeptonicaFrameConverter extends FrameConverter<PIX> {
    static {
        Loader.load(org.bytedeco.leptonica.global.leptonica.class);
    }

    PIX pix;
    BytePointer frameData, pixData;
    ByteBuffer frameBuffer, pixBuffer;

    static boolean isEqual(Frame frame, PIX pix) {
        return pix != null && frame != null && frame.image != null && frame.image.length > 0
                && frame.imageWidth == pix.w() && frame.imageHeight == pix.h()
                && frame.imageChannels == pix.d() / 8 && frame.imageDepth == Frame.DEPTH_UBYTE
                && (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)
                || new Pointer(frame.image[0]).address() == pix.data().address())
                && frame.imageStride * Math.abs(frame.imageDepth) / 8 == pix.wpl() * 4;
    }

    public PIX convert(Frame frame) {
        if (frame == null || frame.image == null) {
            return null;
        } else if (frame.opaque instanceof PIX) {
            return (PIX) frame.opaque;
        } else if (!isEqual(frame, pix)) {
            //I simply lack a machine to test this.
            if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
                System.err.println("This converter does not support running on big-endian machines");
                return null;
            }
            //PIX data should be packed as tightly as possible, see https://github.com/DanBloomberg/leptonica/blob/0d4477653691a8cb4f63fa751d43574c757ccc9f/src/pix.h#L133
            //For anything not greyscale or RGB @ 8 bit per pixel, this involves more bit-shift logic than I'm willing to write (and I lack test cases)
            if (frame.imageChannels != 3 && frame.imageChannels != 1) {
                System.out.println(String.format("Image has %d channels, converter only supports 3 (RGB) or 1 (grayscale) for input", frame.imageChannels));
                return null;
            }
            if (frame.imageDepth != 8 || !(frame.image[0] instanceof ByteBuffer)) {
                System.out.println(String.format("Image has bit depth %d, converter only supports 8 (1 byte/px) for input", frame.imageDepth));
                return null;
            }
            // Leptonica frame scan lines must be padded to 32 bit / 4 bytes of stride (line) length, otherwise one gets nasty scan effects
            // See http://www.leptonica.org/library-notes.html#PIX
            int srcChannelDepthBytes = frame.imageDepth / 8;
            int srcBytesPerPixel = srcChannelDepthBytes * frame.imageChannels;

            // Leptonica counts RGB images as 24 bits per pixel, while the data actually is 32 bit per pixel
            int destBytesPerPixel = srcBytesPerPixel;
            if (frame.imageChannels == 3) {
                // RGB pixels are stored as RGBA, so they take up 4 bytes!
                // See https://github.com/DanBloomberg/leptonica/blob/master/src/pix.h#L157
                destBytesPerPixel = 4;
            }
            int currentStrideLength = frame.imageWidth * destBytesPerPixel;
            int targetStridePad = 4 - (currentStrideLength % 4);
            if (targetStridePad == 4)
                targetStridePad = 0;
            int targetStrideLength = (currentStrideLength) + targetStridePad;
            ByteBuffer src = ((ByteBuffer) frame.image[0]).order(ByteOrder.LITTLE_ENDIAN);
            int newSize = targetStrideLength * frame.imageHeight;
            ByteBuffer dst = ByteBuffer.allocate(newSize).order(ByteOrder.LITTLE_ENDIAN);
            /*
            System.out.println(String.format(
                    "src: %d bytes total, %d channels @ %d bytes per pixel, stride length %d",
                    frame.image[0].capacity(),
                    frame.imageChannels,
                    srcBytesPerPixel,
                    currentStrideLength
            ));
            System.out.println(String.format(
                    "dst: %d bytes total, stride length %d, stride pad %d",
                    newSize,
                    targetStrideLength,
                    targetStridePad
            ));
             */
            //The source bytes will be RGB, which means it will have to be copied byte-by-byte to match Leptonica RGBA
            //todo: use qword copy ops?
            byte[] rowData = new byte[targetStrideLength];
            for (int row = 0; row < frame.imageHeight; row++) {
                for (int col = 0; col < frame.imageWidth; col++) {
                    int srcIndex = (frame.imageStride * row) + (col * frame.imageChannels);
                    if (frame.imageChannels == 1) {
                        byte v = src.get(srcIndex);
                        rowData[col] = v;
                        //System.out.println(String.format("row %03d col %03d idx src %03d val %02x", row, col, srcIndex,v));
                    } else if (frame.imageChannels == 3) {
                        int dstIndex = col * destBytesPerPixel;
                        byte[] pixelData = new byte[3];
                        src.position(srcIndex);
                        src.get(pixelData, 0, pixelData.length);
                        // Convert BGR (OpenCV's standard ordering) to RGB (Leptonica)
                        // See https://learnopencv.com/why-does-opencv-use-bgr-color-format/ and https://github.com/DanBloomberg/leptonica/blob/master/src/pix.h#L157
                        rowData[dstIndex] = pixelData[2]; //dst: r
                        rowData[dstIndex + 1] = pixelData[1]; //dst: g
                        rowData[dstIndex + 2] = pixelData[0]; // dst: b
                        rowData[dstIndex + 3] = 0;
                        //System.out.println(String.format("row %03d col %03d idx src %03d dst %03d val r %02x g %02x b %02x", row, col, srcIndex,dstIndex, pixelData[2], pixelData[1], pixelData[1]));
                    }
                }
                //And since pixel data in source is little-endian, but Leptonica is big-endian on 32-bit level, now invert accordingly...
                ByteBuffer rowBuffer = ByteBuffer.wrap(rowData);
                IntBuffer inverted = rowBuffer.order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                //System.out.println(Arrays.toString(rowBuffer.array()));
                dst.position(row * targetStrideLength).asIntBuffer().put(inverted);
            }
            pix = PIX.create(frame.imageWidth, frame.imageHeight, destBytesPerPixel * 8, new BytePointer(dst.position(0)));
        }

        return pix;
    }

    public Frame convert(PIX pix) {
        if (pix == null) {
            return null;
        }

        PIX tempPix = null;
        if (pix.colormap() != null) {
            tempPix = pix = pixRemoveColormap(pix, REMOVE_CMAP_TO_FULL_COLOR);
        } else if (pix.d() < 8) {
            switch (pix.d()) {
                case 1:
                    tempPix = pix = pixConvert1To8(null, pix, (byte) 0, (byte) 255);
                    break;
                case 2:
                    tempPix = pix = pixConvert2To8(pix, (byte) 0, (byte) 85, (byte) 170, (byte) 255, 0);
                    break;
                case 4:
                    tempPix = pix = pixConvert4To8(pix, 0);
                    break;
                default:
                    assert false;
            }
        }

        if (!isEqual(frame, pix)) {
            frame = new Frame();
            frame.imageWidth = pix.w();
            frame.imageHeight = pix.h();
            frame.imageDepth = Frame.DEPTH_UBYTE;
            frame.imageChannels = pix.d() / 8;
            frame.imageStride = pix.wpl() * 4;
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                if (frameData == null || frameData.capacity() < frame.imageHeight * frame.imageStride) {
                    if (frameData != null) {
                        frameData.releaseReference();
                    }
                    frameData = new BytePointer(frame.imageHeight * frame.imageStride).retainReference();
                }
                frameBuffer = frameData.asByteBuffer().order(ByteOrder.LITTLE_ENDIAN);
                frame.opaque = frameData;
                frame.image = new Buffer[]{frameBuffer};
            } else {
                if (tempPix != null) {
                    if (this.pix != null) {
                        this.pix.releaseReference();
                    }
                    this.pix = pix = pix.clone();
                }
                frame.opaque = pix;
                frame.image = new Buffer[]{pix.createBuffer()};
            }
        }

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ((ByteBuffer) frameBuffer.position(0)).asIntBuffer()
                    .put(pix.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer());
        }

        if (tempPix != null) {
            pixDestroy(tempPix);
        }
        return frame;
    }

    @Override
    public void close() {
        super.close();
        if (pix != null) {
            pix.releaseReference();
            pix = null;
        }
        if (pixData != null) {
            pixData.releaseReference();
            pixData = null;
        }
        if (frameData != null) {
            frameData.releaseReference();
            frameData = null;
        }
    }
}
