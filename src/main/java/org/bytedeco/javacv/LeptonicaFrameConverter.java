/*
 * Copyright (C) 2018 Samuel Audet
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;

import static org.bytedeco.javacpp.lept.*;

/**
 * A utility class to map data between {@link Frame} and {@link PIX},
 * which is the image data structure used by Tesseract.
 * Currently supports only plain PIX images, not FPIX or DPIX ones.
 *
 * @author Samuel Audet
 */
public class LeptonicaFrameConverter extends FrameConverter<PIX> {
    PIX pix;
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
            return (PIX)frame.opaque;
        } else if (!isEqual(frame, pix)) {
            pix = PIX.createHeader(frame.imageWidth, frame.imageHeight, frame.imageChannels * 8)
                     .wpl(frame.imageStride / 4 * Math.abs(frame.imageDepth) / 8);
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                pixBuffer = ByteBuffer.allocateDirect(frame.imageHeight * frame.imageStride).order(ByteOrder.BIG_ENDIAN);
                pix.data(new IntPointer(new Pointer(pixBuffer)));
            } else {
                pix.data(new IntPointer(new Pointer(frame.image[0].position(0))));
            }
        }

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ((ByteBuffer)pixBuffer.position(0)).asIntBuffer()
                    .put(((ByteBuffer)frame.image[0].position(0)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer());
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
                    tempPix = pix = pixConvert1To8(null, pix, (byte)0, (byte)255);
                    break;
                case 2:
                    tempPix = pix = pixConvert2To8(pix, (byte)0, (byte)85, (byte)170, (byte)255, 0);
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
                frameBuffer = ByteBuffer.allocateDirect(frame.imageHeight * frame.imageStride).order(ByteOrder.LITTLE_ENDIAN);
                frame.image = new Buffer[] { frameBuffer };
            } else {
                frame.image = new Buffer[] { pix.createBuffer() };
            }
        }

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ((ByteBuffer)frameBuffer.position(0)).asIntBuffer()
                    .put(pix.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer());
        }

        if (tempPix != null) {
            frame.opaque = pix.clone();
            pixDestroy(tempPix);
        } else {
            frame.opaque = pix;
        }
        return frame;
    }
}
