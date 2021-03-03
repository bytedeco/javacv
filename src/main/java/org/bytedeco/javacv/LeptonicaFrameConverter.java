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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;

import org.bytedeco.leptonica.*;
import static org.bytedeco.leptonica.global.lept.*;

/**
 * A utility class to map data between {@link Frame} and {@link PIX},
 * which is the image data structure used by Tesseract.
 * Currently supports only plain PIX images, not FPIX or DPIX ones.
 *
 * @author Samuel Audet
 */
public class LeptonicaFrameConverter extends FrameConverter<PIX> {
    static { Loader.load(org.bytedeco.leptonica.global.lept.class); }

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
            return (PIX)frame.opaque;
        } else if (!isEqual(frame, pix)) {
            Pointer data;
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                if (pixData == null || pixData.capacity() < frame.imageHeight * frame.imageStride) {
                    if (pixData != null) {
                        pixData.releaseReference();
                    }
                    pixData = new BytePointer(frame.imageHeight * frame.imageStride).retainReference();
                }
                data = pixData;
                pixBuffer = data.asByteBuffer().order(ByteOrder.BIG_ENDIAN);
            } else {
                data = new Pointer(frame.image[0].position(0));
            }
            if (pix != null) {
                pix.releaseReference();
            }
            pix = PIX.create(frame.imageWidth, frame.imageHeight, frame.imageChannels * 8, data)
                     .wpl(frame.imageStride / 4 * Math.abs(frame.imageDepth) / 8).retainReference();
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
                if (frameData == null || frameData.capacity() < frame.imageHeight * frame.imageStride) {
                    if (frameData != null) {
                        frameData.releaseReference();
                    }
                    frameData = new BytePointer(frame.imageHeight * frame.imageStride).retainReference();
                }
                frameBuffer = frameData.asByteBuffer().order(ByteOrder.LITTLE_ENDIAN);
                frame.opaque = frameData;
                frame.image = new Buffer[] { frameBuffer };
            } else {
                if (tempPix != null) {
                    if (this.pix != null) {
                        this.pix.releaseReference();
                    }
                    this.pix = pix = pix.clone();
                }
                frame.opaque = pix;
                frame.image = new Buffer[] { pix.createBuffer() };
            }
        }

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ((ByteBuffer)frameBuffer.position(0)).asIntBuffer()
                    .put(pix.createBuffer().order(ByteOrder.BIG_ENDIAN).asIntBuffer());
        }

        if (tempPix != null) {
            pixDestroy(tempPix);
        }
        return frame;
    }

    @Override public void close() {
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
