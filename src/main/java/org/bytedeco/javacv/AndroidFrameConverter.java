/*
 * Copyright (C) 2015 Samuel Audet
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

import android.graphics.Bitmap;
import android.hardware.Camera;
import java.nio.ByteBuffer;

/**
 * A utility class to copy data between {@link Frame} and {@link Bitmap}.
 * Since {@link Bitmap} does not expose its internal buffer, we cannot share
 * allocated memory with {@link Frame}.
 * <p>
 * This class is not optimized for speed. For best performance, convert first
 * your data to and from RGBA with optimized functions from FFmpeg or OpenCV.
 *
 * @author Samuel Audet
 */
public class AndroidFrameConverter extends FrameConverter<Bitmap> {
    Bitmap bitmap;
    ByteBuffer buffer;
    byte[] row;

    /**
     * Convert YUV 4:2:0 SP (NV21) data to BGR, as received, for example,
     * via {@link Camera.PreviewCallback#onPreviewFrame(byte[],Camera)}.
     */
    public Frame convert(byte[] data, int width, int height) {
        if (frame == null || frame.imageWidth != width
                || frame.imageHeight != height || frame.imageChannels != 3) {
            frame = new Frame(width, height, Frame.DEPTH_UBYTE, 3);
        }
        ByteBuffer out = (ByteBuffer)frame.image[0];
        int stride = frame.imageStride;

        // ported from https://android.googlesource.com/platform/development/+/master/tools/yuv420sp2rgb/yuv420sp2rgb.c
        int offset = height * width;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int Y = data[i * width + j] & 0xFF;
                int V = data[offset + (i/2) * width + 2 * (j/2)    ] & 0xFF;
                int U = data[offset + (i/2) * width + 2 * (j/2) + 1] & 0xFF;

                // Yuv Convert
                Y -= 16;
                U -= 128;
                V -= 128;

                if (Y < 0)
                    Y = 0;

                // R = (int)(1.164 * Y + 2.018 * U);
                // G = (int)(1.164 * Y - 0.813 * V - 0.391 * U);
                // B = (int)(1.164 * Y + 1.596 * V);

                int B = (int)(1192 * Y + 2066 * U);
                int G = (int)(1192 * Y - 833 * V - 400 * U);
                int R = (int)(1192 * Y + 1634 * V);

                R = Math.min(262143, Math.max(0, R));
                G = Math.min(262143, Math.max(0, G));
                B = Math.min(262143, Math.max(0, B));

                R >>= 10; R &= 0xff;
                G >>= 10; G &= 0xff;
                B >>= 10; B &= 0xff;

                out.put(i * stride + 3 * j,     (byte)B);
                out.put(i * stride + 3 * j + 1, (byte)G);
                out.put(i * stride + 3 * j + 2, (byte)R);
            }
        }
        return frame;
    }

    @Override public Frame convert(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int channels = 0;
        switch (bitmap.getConfig()) {
            case ALPHA_8:   channels = 1; break;
            case RGB_565:
            case ARGB_4444: channels = 2; break;
            case ARGB_8888: channels = 4; break;
            default: assert false;
        }

        if (frame == null || frame.imageWidth != bitmap.getWidth()
                || frame.imageHeight != bitmap.getHeight() || frame.imageChannels != channels) {
            frame = new Frame(bitmap.getWidth(), bitmap.getHeight(), Frame.DEPTH_UBYTE, channels);
        }

        // assume matching strides
        bitmap.copyPixelsToBuffer(frame.image[0].position(0));

        return frame;
    }

    @Override public Bitmap convert(Frame frame) {
        if (frame == null || frame.image == null) {
            return null;
        }

        Bitmap.Config config = null;
        switch (frame.imageChannels) {
            case 2: config = Bitmap.Config.RGB_565; break;
            case 1:
            case 3:
            case 4: config = Bitmap.Config.ARGB_8888; break;
            default: assert false;
        }

        if (bitmap == null || bitmap.getWidth() != frame.imageWidth
                || bitmap.getHeight() != frame.imageHeight || bitmap.getConfig() != config) {
            bitmap = Bitmap.createBitmap(frame.imageWidth, frame.imageHeight, config);
        }

        // assume frame.imageDepth == Frame.DEPTH_UBYTE
        ByteBuffer in = (ByteBuffer)frame.image[0];
        int width = frame.imageWidth;
        int height = frame.imageHeight;
        int stride = frame.imageStride;
        int rowBytes = bitmap.getRowBytes();
        int rgba;
        if (frame.imageChannels == 1) {
            if (buffer == null || buffer.capacity() < height * rowBytes) {
                buffer = ByteBuffer.allocate(height * rowBytes);
            }
            if (row == null || row.length != stride)
                row = new byte[stride];
            for (int y = 0; y < height; y++) {
                in.position(y * stride);
                in.get(row);
                for (int x = 0; x < width; x++) {
                    // GRAY -> RGBA
                    byte B = row[x];
                    rgba = ( B & 0xff) << 24 |
                            (B & 0xff) << 16 |
                            (B & 0xff) <<  8 | 0xff;
                    buffer.putInt(y * rowBytes + 4 * x, rgba);
                }
            }
            bitmap.copyPixelsFromBuffer(buffer.position(0));
        } else if (frame.imageChannels == 3) {
            if (buffer == null || buffer.capacity() < height * rowBytes) {
                buffer = ByteBuffer.allocate(height * rowBytes);
            }
            if (in.remaining() > 0) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        // BGR -> RGBA
                        if (x < width - 1 || y < height - 1) {
                            rgba = in.getInt(y * stride + 3 * x);
                        } else {
                            int r = in.get(y * stride + 3 * x)     & 0xff;
                            int g = in.get(y * stride + 3 * x + 1) & 0xff;
                            int b = in.get(y * stride + 3 * x + 2) & 0xff;
                            rgba = (r << 24) | (g << 16) | (b << 8);
                        }
                        buffer.putInt(y * rowBytes + 4 * x, (rgba << 8) | 0xff);
                    }
                }
            }
            bitmap.copyPixelsFromBuffer(buffer.position(0));
        } else {
            // assume matching strides
            bitmap.copyPixelsFromBuffer(in.position(0));
        }
        return bitmap;
    }
}
