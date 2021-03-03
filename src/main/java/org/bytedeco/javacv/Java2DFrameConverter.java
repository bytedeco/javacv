/*
 * Copyright (C) 2015-2019 Samuel Audet
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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * A utility class to copy data between {@link Frame} and {@link BufferedImage}.
 * Since {@link BufferedImage} does not support NIO buffers, we cannot share
 * allocated memory with {@link Frame}.
 *
 * @author Samuel Audet
 */
public class Java2DFrameConverter extends FrameConverter<BufferedImage> {

    @Override public Frame convert(BufferedImage img) {
        return getFrame(img);
    }

    @Override public BufferedImage convert(Frame frame) {
        return getBufferedImage(frame);
    }

    /**
     * @param source
     * @return null if source is null
     */
    public static BufferedImage cloneBufferedImage(BufferedImage source) {
        if (source == null) {
            return null;
        }
        int type = source.getType();
        if (type == BufferedImage.TYPE_CUSTOM) {
            return new BufferedImage(
                    source.getColorModel(),
                    source.copyData(null),
                    source.isAlphaPremultiplied(),
                    null
            );
        } else {
            BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), type);
            Graphics g = copy.getGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return copy;
        }
    }

    public static final byte[]
            gamma22    = new byte[256],
            gamma22inv = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            gamma22[i]    = (byte)Math.round(Math.pow(i/255.0,   2.2)*255.0);
            gamma22inv[i] = (byte)Math.round(Math.pow(i/255.0, 1/2.2)*255.0);
        }
    }
    public static int decodeGamma22(int value) {
        return gamma22[value & 0xFF] & 0xFF;
    }
    public static int encodeGamma22(int value) {
        return gamma22inv[value & 0xFF] & 0xFF;
    }
    public static void flipCopyWithGamma(ByteBuffer srcBuf, int srcBufferIndex, int srcStep,
                                         ByteBuffer dstBuf, int dstBufferIndex, int dstStep,
                                         boolean signed, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBufferIndex, dstLine = dstBufferIndex;
        byte[] buffer = new byte[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBufferIndex = srcBuf.capacity() - srcLine - srcStep;
            } else {
                srcBufferIndex = srcLine;
            }
            dstBufferIndex = dstLine;
            w = Math.min(Math.min(w, srcBuf.capacity() - srcBufferIndex), dstBuf.capacity() - dstBufferIndex);
            if (signed) {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get(srcBufferIndex++);
                            byte out;
                            if (gamma == 1.0) {
                                out = (byte)in;
                            } else {
                                out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(dstBufferIndex++, buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get(srcBufferIndex++);
                        byte out;
                        if (gamma == 1.0) {
                            out = (byte)in;
                        } else {
                            out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                        }
                        dstBuf.put(dstBufferIndex++, out);
                    }
                }
            } else {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            byte out;
                            int in = srcBuf.get(srcBufferIndex++) & 0xFF;
                            if (gamma == 1.0) {
                                out = (byte)in;
                            } else if (gamma == 2.2) {
                                out = gamma22[in];
                            } else if (gamma == 1/2.2) {
                                out = gamma22inv[in];
                            } else {
                                out = (byte)Math.round(Math.pow((double)in/0xFF, gamma)*0xFF);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(dstBufferIndex++, buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        byte out;
                        int in = srcBuf.get(srcBufferIndex++) & 0xFF;
                        if (gamma == 1.0) {
                            out = (byte)in;
                        } else if (gamma == 2.2) {
                            out = gamma22[in];
                        } else if (gamma == 1/2.2) {
                            out = gamma22inv[in];
                        } else {
                            out = (byte)Math.round(Math.pow((double)in/0xFF, gamma)*0xFF);
                        }
                        dstBuf.put(dstBufferIndex++, out);
                    }
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(ShortBuffer srcBuf, int srcBufferIndex, int srcStep,
                                         ShortBuffer dstBuf, int dstBufferIndex, int dstStep,
                                         boolean signed, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBufferIndex, dstLine = dstBufferIndex;
        short[] buffer = new short[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBufferIndex = srcBuf.capacity() - srcLine - srcStep;
            } else {
                srcBufferIndex = srcLine;
            }
            dstBufferIndex = dstLine;
            w = Math.min(Math.min(w, srcBuf.capacity() - srcBufferIndex), dstBuf.capacity() - dstBufferIndex);
            if (signed) {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get(srcBufferIndex++);
                            short out;
                            if (gamma == 1.0) {
                                out = (short)in;
                            } else {
                                out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(dstBufferIndex++, buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get(srcBufferIndex++);
                        short out;
                        if (gamma == 1.0) {
                            out = (short)in;
                        } else {
                            out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                        }
                        dstBuf.put(dstBufferIndex++, out);
                    }
                }
            } else {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get(srcBufferIndex++);
                            short out;
                            if (gamma == 1.0) {
                                out = (short)in;
                            } else {
                                out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(dstBufferIndex++, buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get(srcBufferIndex++) & 0xFFFF;
                        short out;
                        if (gamma == 1.0) {
                            out = (short)in;
                        } else {
                            out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                        }
                        dstBuf.put(dstBufferIndex++, out);
                    }
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(IntBuffer srcBuf, int srcBufferIndex, int srcStep,
                                         IntBuffer dstBuf, int dstBufferIndex, int dstStep,
                                         double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBufferIndex, dstLine = dstBufferIndex;
        int[] buffer = new int[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBufferIndex = srcBuf.capacity() - srcLine - srcStep;
            } else {
                srcBufferIndex = srcLine;
            }
            dstBufferIndex = dstLine;
            w = Math.min(Math.min(w, srcBuf.capacity() - srcBufferIndex), dstBuf.capacity() - dstBufferIndex);
            if (channels > 1) {
                for (int x = 0; x < w; x+=channels) {
                    for (int z = 0; z < channels; z++) {
                        int in = srcBuf.get(srcBufferIndex++);
                        int out;
                        if (gamma == 1.0) {
                            out = (int)in;
                        } else {
                            out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                        }
                        buffer[z] = out;
                    }
                    for (int z = channels-1; z >= 0; z--) {
                        dstBuf.put(dstBufferIndex++, buffer[z]);
                    }
                }
            } else {
                for (int x = 0; x < w; x++) {
                    int in = srcBuf.get(srcBufferIndex++);
                    int out;
                    if (gamma == 1.0) {
                        out = in;
                    } else {
                        out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                    }
                    dstBuf.put(dstBufferIndex++, out);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(FloatBuffer srcBuf, int srcBufferIndex, int srcStep,
                                         FloatBuffer dstBuf, int dstBufferIndex, int dstStep,
                                         double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBufferIndex, dstLine = dstBufferIndex;
        float[] buffer = new float[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBufferIndex = srcBuf.capacity() - srcLine - srcStep;
            } else {
                srcBufferIndex = srcLine;
            }
            dstBufferIndex = dstLine;
            w = Math.min(Math.min(w, srcBuf.capacity() - srcBufferIndex), dstBuf.capacity() - dstBufferIndex);
            if (channels > 1) {
                for (int x = 0; x < w; x+=channels) {
                    for (int z = 0; z < channels; z++) {
                        float in = srcBuf.get(srcBufferIndex++);
                        float out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = (float)Math.pow(in, gamma);
                        }
                        buffer[z] = out;
                    }
                    for (int z = channels-1; z >= 0; z--) {
                        dstBuf.put(dstBufferIndex++, buffer[z]);
                    }
                }
            } else {
                for (int x = 0; x < w; x++) {
                    float in = srcBuf.get(srcBufferIndex++);
                    float out;
                    if (gamma == 1.0) {
                        out = in;
                    } else {
                        out = (float)Math.pow(in, gamma);
                    }
                    dstBuf.put(dstBufferIndex++, out);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(DoubleBuffer srcBuf, int srcBufferIndex, int srcStep,
                                         DoubleBuffer dstBuf, int dstBufferIndex, int dstStep,
                                         double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBufferIndex, dstLine = dstBufferIndex;
        double[] buffer = new double[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBufferIndex = srcBuf.capacity() - srcLine - srcStep;
            } else {
                srcBufferIndex = srcLine;
            }
            dstBufferIndex = dstLine;
            w = Math.min(Math.min(w, srcBuf.capacity() - srcBufferIndex), dstBuf.capacity() - dstBufferIndex);
            if (channels > 1) {
                for (int x = 0; x < w; x+=channels) {
                    for (int z = 0; z < channels; z++) {
                        double in = srcBuf.get(srcBufferIndex++);
                        double out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = Math.pow(in, gamma);
                        }
                        buffer[z] = out;
                    }
                    for (int z = channels-1; z >= 0; z--) {
                        dstBuf.put(dstBufferIndex++, buffer[z]);
                    }
                }
            } else {
                for (int x = 0; x < w; x++) {
                    double in = srcBuf.get(srcBufferIndex++);
                    double out;
                    if (gamma == 1.0) {
                        out = in;
                    } else {
                        out = Math.pow(in, gamma);
                    }
                    dstBuf.put(dstBufferIndex++, out);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }

    public static void applyGamma(Frame frame, double gamma) {
        applyGamma(frame.image[0], frame.imageDepth, frame.imageStride, gamma);
    }
    public static void applyGamma(Buffer buffer, int depth, int stride, double gamma) {
        if (gamma == 1.0) {
            return;
        }
        switch (depth) {
            case Frame.DEPTH_UBYTE:
                flipCopyWithGamma(((ByteBuffer)buffer).asReadOnlyBuffer(), 0, stride, (ByteBuffer)buffer, 0, stride, false, gamma, false, 0);
                break;
            case Frame.DEPTH_BYTE:
                flipCopyWithGamma(((ByteBuffer)buffer).asReadOnlyBuffer(), 0, stride, (ByteBuffer)buffer, 0, stride, true, gamma, false, 0);
                break;
            case Frame.DEPTH_USHORT:
                flipCopyWithGamma(((ShortBuffer)buffer).asReadOnlyBuffer(), 0, stride, (ShortBuffer)buffer, 0, stride, false, gamma, false, 0);
                break;
            case Frame.DEPTH_SHORT:
                flipCopyWithGamma(((ShortBuffer)buffer).asReadOnlyBuffer(), 0, stride, (ShortBuffer)buffer, 0, stride, true, gamma, false, 0);
                break;
            case Frame.DEPTH_INT:
                flipCopyWithGamma(((IntBuffer)buffer).asReadOnlyBuffer(), 0, stride, (IntBuffer)buffer, 0, stride, gamma, false, 0);
                break;
            case Frame.DEPTH_FLOAT:
                flipCopyWithGamma(((FloatBuffer)buffer).asReadOnlyBuffer(), 0, stride, (FloatBuffer)buffer, 0, stride, gamma, false, 0);
                break;
            case Frame.DEPTH_DOUBLE:
                flipCopyWithGamma(((DoubleBuffer)buffer).asReadOnlyBuffer(), 0, stride, (DoubleBuffer)buffer, 0, stride, gamma, false, 0);
                break;
            default:
                assert false;
        }
    }

    public static void copy(Frame frame, BufferedImage bufferedImage) {
        copy(frame, bufferedImage, 1.0);
    }
    public static void copy(Frame frame, BufferedImage bufferedImage, double gamma) {
        copy(frame, bufferedImage, gamma, false, null);
    }
    public static void copy(Frame frame, BufferedImage bufferedImage, double gamma, boolean flipChannels, Rectangle roi) {
        Buffer in = frame.image[0];
        int bufferIndex = roi == null ? 0 : roi.y*frame.imageStride + roi.x*frame.imageChannels;
        SampleModel sm = bufferedImage.getSampleModel();
        Raster r       = bufferedImage.getRaster();
        DataBuffer out = r.getDataBuffer();
        int x = -r.getSampleModelTranslateX();
        int y = -r.getSampleModelTranslateY();
        int step = sm.getWidth()*sm.getNumBands();
        int channels = sm.getNumBands();
        if (sm instanceof ComponentSampleModel) {
            step = ((ComponentSampleModel)sm).getScanlineStride();
            channels = ((ComponentSampleModel)sm).getPixelStride();
        } else if (sm instanceof SinglePixelPackedSampleModel) {
            step = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
            channels = 1;
        } else if (sm instanceof MultiPixelPackedSampleModel) {
            step = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
            channels = ((MultiPixelPackedSampleModel)sm).getPixelBitStride()/8; // ??
        }
        int start = y*step + x*channels;

        if (out instanceof DataBufferByte) {
            byte[] a = ((DataBufferByte)out).getData();
            flipCopyWithGamma((ByteBuffer)in, bufferIndex, frame.imageStride, ByteBuffer.wrap(a), start, step, false, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferDouble) {
            double[] a = ((DataBufferDouble)out).getData();
            flipCopyWithGamma((DoubleBuffer)in, bufferIndex, frame.imageStride, DoubleBuffer.wrap(a), start, step, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferFloat) {
            float[] a = ((DataBufferFloat)out).getData();
            flipCopyWithGamma((FloatBuffer)in, bufferIndex, frame.imageStride, FloatBuffer.wrap(a), start, step, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferInt) {
            int[] a = ((DataBufferInt)out).getData();
            int stride = frame.imageStride;
            if (in instanceof ByteBuffer) {
                in = ((ByteBuffer)in).order(flipChannels ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN).asIntBuffer();
                stride /= 4;
            }
            flipCopyWithGamma((IntBuffer)in, bufferIndex, stride, IntBuffer.wrap(a), start, step, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferShort) {
            short[] a = ((DataBufferShort)out).getData();
            flipCopyWithGamma((ShortBuffer)in, bufferIndex, frame.imageStride, ShortBuffer.wrap(a), start, step, true, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferUShort) {
            short[] a = ((DataBufferUShort)out).getData();
            flipCopyWithGamma((ShortBuffer)in, bufferIndex, frame.imageStride, ShortBuffer.wrap(a), start, step, false, gamma, false, flipChannels ? channels : 0);
        } else {
            assert false;
        }
    }

    public static void copy(BufferedImage image, Frame frame) {
        copy(image, frame, 1.0);
    }
    public static void copy(BufferedImage image, Frame frame, double gamma) {
        copy(image, frame, gamma, false, null);
    }
    public static void copy(BufferedImage image, Frame frame, double gamma, boolean flipChannels, Rectangle roi) {
        Buffer out = frame.image[0];
        int bufferIndex = roi == null ? 0 : roi.y*frame.imageStride + roi.x*frame.imageChannels;
        SampleModel sm = image.getSampleModel();
        Raster r       = image.getRaster();
        DataBuffer in  = r.getDataBuffer();
        int x = -r.getSampleModelTranslateX();
        int y = -r.getSampleModelTranslateY();
        int step = sm.getWidth()*sm.getNumBands();
        int channels = sm.getNumBands();
        if (sm instanceof ComponentSampleModel) {
            step = ((ComponentSampleModel)sm).getScanlineStride();
            channels = ((ComponentSampleModel)sm).getPixelStride();
        } else if (sm instanceof SinglePixelPackedSampleModel) {
            step = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
            channels = 1;
        } else if (sm instanceof MultiPixelPackedSampleModel) {
            step = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
            channels = ((MultiPixelPackedSampleModel)sm).getPixelBitStride()/8; // ??
        }
        int start = y*step + x*channels;

        if (in instanceof DataBufferByte) {
            byte[] a = ((DataBufferByte)in).getData();
            flipCopyWithGamma(ByteBuffer.wrap(a), start, step, (ByteBuffer)out, bufferIndex, frame.imageStride, false, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferDouble) {
            double[] a = ((DataBufferDouble)in).getData();
            flipCopyWithGamma(DoubleBuffer.wrap(a), start, step, (DoubleBuffer)out, bufferIndex, frame.imageStride, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferFloat) {
            float[] a = ((DataBufferFloat)in).getData();
            flipCopyWithGamma(FloatBuffer.wrap(a), start, step, (FloatBuffer)out, bufferIndex, frame.imageStride, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferInt) {
            int[] a = ((DataBufferInt)in).getData();
            int stride = frame.imageStride;
            if (out instanceof ByteBuffer) {
                out = ((ByteBuffer)out).order(flipChannels ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN).asIntBuffer();
                stride /= 4;
            }
            flipCopyWithGamma(IntBuffer.wrap(a), start, step, (IntBuffer)out, bufferIndex, stride, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferShort) {
            short[] a = ((DataBufferShort)in).getData();
            flipCopyWithGamma(ShortBuffer.wrap(a), start, step, (ShortBuffer)out, bufferIndex, frame.imageStride, true, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferUShort) {
            short[] a = ((DataBufferUShort)in).getData();
            flipCopyWithGamma(ShortBuffer.wrap(a), start, step, (ShortBuffer)out, bufferIndex, frame.imageStride, false, gamma, false, flipChannels ? channels : 0);
        } else {
            assert false;
        }
    }

    protected BufferedImage bufferedImage = null;
    public static int getBufferedImageType(Frame frame) {
        // precanned BufferedImage types are confusing... in practice though,
        // they all use the sRGB color model when blitting:
        //     http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5051418
        // and we should use them because they are *A LOT* faster with Java 2D.
        // workaround: do gamma correction ourselves ("gamma" parameter)
        //             since we'll never use getRGB() and setRGB(), right?
        int type = BufferedImage.TYPE_CUSTOM;
        if (frame.imageChannels == 1) {
            if (frame.imageDepth == Frame.DEPTH_UBYTE || frame.imageDepth == Frame.DEPTH_BYTE) {
                type = BufferedImage.TYPE_BYTE_GRAY;
            } else if (frame.imageDepth == Frame.DEPTH_USHORT) {
                type = BufferedImage.TYPE_USHORT_GRAY;
            }
        } else if (frame.imageChannels == 3) {
            if (frame.imageDepth == Frame.DEPTH_UBYTE || frame.imageDepth == Frame.DEPTH_BYTE) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
        } else if (frame.imageChannels == 4) {
            // The channels end up reversed of what we need for OpenCL.
            // We work around this in copyTo() and copyFrom() by
            // inversing the channels to let us use RGBA in our IplImage.
            if (frame.imageDepth == Frame.DEPTH_UBYTE || frame.imageDepth == Frame.DEPTH_BYTE) {
                type = BufferedImage.TYPE_4BYTE_ABGR;
            }
        }
        return type;
    }
    public BufferedImage getBufferedImage(Frame frame) {
        return getBufferedImage(frame, 1.0);
    }
    public BufferedImage getBufferedImage(Frame frame, double gamma) {
        return getBufferedImage(frame, gamma, false, null);
    }
    public BufferedImage getBufferedImage(Frame frame, double gamma, boolean flipChannels, ColorSpace cs) {
        if (frame == null || frame.image == null) {
            return null;
        }
        int type = getBufferedImageType(frame);

        if (bufferedImage == null || bufferedImage.getWidth() != frame.imageWidth
                || bufferedImage.getHeight() != frame.imageHeight || bufferedImage.getType() != type) {
            bufferedImage = type == BufferedImage.TYPE_CUSTOM || cs != null ? null
                    : new BufferedImage(frame.imageWidth, frame.imageHeight, type);
        }

        if (bufferedImage == null) {
            boolean alpha = false;
            int[] offsets = null;
            if (frame.imageChannels == 1) {
                alpha = false;
                if (cs == null) {
                    cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                }
                offsets = new int[] {0};
            } else if (frame.imageChannels == 3) {
                alpha = false;
                if (cs == null) {
                    cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                }
                // raster in "BGR" order like OpenCV..
                offsets = new int[] {2, 1, 0};
            } else if (frame.imageChannels == 4) {
                alpha = true;
                if (cs == null) {
                    cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                }
                // raster in "RGBA" order for OpenCL.. alpha needs to be last
                offsets = new int[] {0, 1, 2, 3};
            } else {
                assert false;
            }

            ColorModel cm = null;
            WritableRaster wr = null;
            if (frame.imageDepth == Frame.DEPTH_UBYTE || frame.imageDepth == Frame.DEPTH_BYTE) {
                cm = new ComponentColorModel(cs, alpha,
                        false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
                wr = Raster.createWritableRaster(new ComponentSampleModel(
                        DataBuffer.TYPE_BYTE, frame.imageWidth, frame.imageHeight, frame.imageChannels, frame.imageStride,
                        offsets), null);
            } else if (frame.imageDepth == Frame.DEPTH_USHORT) {
                cm = new ComponentColorModel(cs, alpha,
                        false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
                wr = Raster.createWritableRaster(new ComponentSampleModel(
                        DataBuffer.TYPE_USHORT, frame.imageWidth, frame.imageHeight, frame.imageChannels, frame.imageStride,
                        offsets), null);
            } else if (frame.imageDepth == Frame.DEPTH_SHORT) {
                cm = new ComponentColorModel(cs, alpha,
                        false, Transparency.OPAQUE, DataBuffer.TYPE_SHORT);
                wr = Raster.createWritableRaster(new ComponentSampleModel(
                        DataBuffer.TYPE_SHORT, frame.imageWidth, frame.imageHeight, frame.imageChannels, frame.imageStride,
                        offsets), null);
            } else if (frame.imageDepth == Frame.DEPTH_INT) {
                cm = new ComponentColorModel(cs, alpha,
                        false, Transparency.OPAQUE, DataBuffer.TYPE_INT);
                wr = Raster.createWritableRaster(new ComponentSampleModel(
                        DataBuffer.TYPE_INT, frame.imageWidth, frame.imageHeight, frame.imageChannels, frame.imageStride,
                        offsets), null);
            } else if (frame.imageDepth == Frame.DEPTH_FLOAT) {
                cm = new ComponentColorModel(cs, alpha,
                        false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
                wr = Raster.createWritableRaster(new ComponentSampleModel(
                        DataBuffer.TYPE_FLOAT, frame.imageWidth, frame.imageHeight, frame.imageChannels, frame.imageStride,
                        offsets), null);
            } else if (frame.imageDepth == Frame.DEPTH_DOUBLE) {
                cm = new ComponentColorModel(cs, alpha,
                        false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
                wr = Raster.createWritableRaster(new ComponentSampleModel(
                        DataBuffer.TYPE_DOUBLE, frame.imageWidth, frame.imageHeight, frame.imageChannels, frame.imageStride,
                        offsets), null);
            } else {
                assert false;
            }

            bufferedImage = new BufferedImage(cm, wr, false, null);
        }

        if (bufferedImage != null) {
            copy(frame, bufferedImage, gamma, flipChannels, null);
        }

        return bufferedImage;
    }

    /**
     * Returns a Frame based on a BufferedImage.
     */
    public Frame getFrame(BufferedImage image) {
        return getFrame(image, 1.0);
    }
    /**
     * Returns a Frame based on a BufferedImage, and given gamma.
     */
    public Frame getFrame(BufferedImage image, double gamma) {
        return getFrame(image, gamma, false);
    }
    /**
     * Returns a Frame based on a BufferedImage, given gamma, and inverted channels flag.
     */
    public Frame getFrame(BufferedImage image, double gamma, boolean flipChannels) {
        if (image == null) {
            return null;
        }
        SampleModel sm = image.getSampleModel();
        int depth = 0, numChannels = sm.getNumBands();
        switch (image.getType()) {
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
            case BufferedImage.TYPE_INT_BGR:
                depth = Frame.DEPTH_UBYTE;
                numChannels = 4;
                break;
        }
        if (depth == 0 || numChannels == 0) {
            switch (sm.getDataType()) {
                case DataBuffer.TYPE_BYTE:   depth = Frame.DEPTH_UBYTE;  break;
                case DataBuffer.TYPE_USHORT: depth = Frame.DEPTH_USHORT; break;
                case DataBuffer.TYPE_SHORT:  depth = Frame.DEPTH_SHORT;  break;
                case DataBuffer.TYPE_INT:    depth = Frame.DEPTH_INT;    break;
                case DataBuffer.TYPE_FLOAT:  depth = Frame.DEPTH_FLOAT;  break;
                case DataBuffer.TYPE_DOUBLE: depth = Frame.DEPTH_DOUBLE; break;
                default: assert false;
            }
        }
        if (frame == null || frame.imageWidth != image.getWidth() || frame.imageHeight != image.getHeight()
                || frame.imageDepth != depth || frame.imageChannels != numChannels) {
            if (frame != null) {
                frame.close();
            }
            frame = new Frame(image.getWidth(), image.getHeight(), depth, numChannels);
        }
        copy(image, frame, gamma, flipChannels, null);
        return frame;
    }
}
