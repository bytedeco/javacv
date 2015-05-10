/*
 * Copyright (C) 2015 Samuel Audet
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

    public static BufferedImage cloneBufferedImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        BufferedImage bi = bufferedImage;
        int type = bi.getType();
        if (type == BufferedImage.TYPE_CUSTOM) {
            return new BufferedImage(bi.getColorModel(),
                    bi.copyData(null), bi.isAlphaPremultiplied(), null);
        } else {
            return new BufferedImage(bi.getWidth(), bi.getHeight(), type);
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
    public static void flipCopyWithGamma(ByteBuffer srcBuf, int srcStep,
            ByteBuffer dstBuf, int dstStep, boolean signed, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBuf.position(), dstLine = dstBuf.position();
        byte[] buffer = new byte[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
            } else {
                srcBuf.position(srcLine);
            }
            dstBuf.position(dstLine);
            w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
            if (signed) {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get();
                            byte out;
                            if (gamma == 1.0) {
                                out = (byte)in;
                            } else {
                                out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get();
                        byte out;
                        if (gamma == 1.0) {
                            out = (byte)in;
                        } else {
                            out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                        }
                        dstBuf.put(out);
                    }
                }
            } else {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            byte out;
                            int in = srcBuf.get() & 0xFF;
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
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        byte out;
                        int in = srcBuf.get() & 0xFF;
                        if (gamma == 1.0) {
                            out = (byte)in;
                        } else if (gamma == 2.2) {
                            out = gamma22[in];
                        } else if (gamma == 1/2.2) {
                            out = gamma22inv[in];
                        } else {
                            out = (byte)Math.round(Math.pow((double)in/0xFF, gamma)*0xFF);
                        }
                        dstBuf.put(out);
                    }
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(ShortBuffer srcBuf, int srcStep,
            ShortBuffer dstBuf, int dstStep, boolean signed, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBuf.position(), dstLine = dstBuf.position();
        short[] buffer = new short[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
            } else {
                srcBuf.position(srcLine);
            }
            dstBuf.position(dstLine);
            w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
            if (signed) {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get();
                            short out;
                            if (gamma == 1.0) {
                                out = (short)in;
                            } else {
                                out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get();
                        short out;
                        if (gamma == 1.0) {
                            out = (short)in;
                        } else {
                            out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                        }
                        dstBuf.put(out);
                    }
                }
            } else {
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get();
                            short out;
                            if (gamma == 1.0) {
                                out = (short)in;
                            } else {
                                out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get() & 0xFFFF;
                        short out;
                        if (gamma == 1.0) {
                            out = (short)in;
                        } else {
                            out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                        }
                        dstBuf.put(out);
                    }
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(IntBuffer srcBuf, int srcStep,
            IntBuffer dstBuf, int dstStep, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBuf.position(), dstLine = dstBuf.position();
        int[] buffer = new int[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
            } else {
                srcBuf.position(srcLine);
            }
            dstBuf.position(dstLine);
            w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
            if (channels > 1) {
                for (int x = 0; x < w; x+=channels) {
                    for (int z = 0; z < channels; z++) {
                        int in = srcBuf.get();
                        int out;
                        if (gamma == 1.0) {
                            out = (int)in;
                        } else {
                            out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                        }
                        buffer[z] = out;
                    }
                    for (int z = channels-1; z >= 0; z--) {
                        dstBuf.put(buffer[z]);
                    }
                }
            } else {
                for (int x = 0; x < w; x++) {
                    int in = srcBuf.get();
                    int out;
                    if (gamma == 1.0) {
                        out = in;
                    } else {
                        out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                    }
                    dstBuf.put(out);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(FloatBuffer srcBuf, int srcStep,
            FloatBuffer dstBuf, int dstStep, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBuf.position(), dstLine = dstBuf.position();
        float[] buffer = new float[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
            } else {
                srcBuf.position(srcLine);
            }
            dstBuf.position(dstLine);
            w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
            if (channels > 1) {
                for (int x = 0; x < w; x+=channels) {
                    for (int z = 0; z < channels; z++) {
                        float in = srcBuf.get();
                        float out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = (float)Math.pow(in, gamma);
                        }
                        buffer[z] = out;
                    }
                    for (int z = channels-1; z >= 0; z--) {
                        dstBuf.put(buffer[z]);
                    }
                }
            } else {
                for (int x = 0; x < w; x++) {
                    float in = srcBuf.get();
                    float out;
                    if (gamma == 1.0) {
                        out = in;
                    } else {
                        out = (float)Math.pow(in, gamma);
                    }
                    dstBuf.put(out);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
    public static void flipCopyWithGamma(DoubleBuffer srcBuf, int srcStep,
            DoubleBuffer dstBuf, int dstStep, double gamma, boolean flip, int channels) {
        assert srcBuf != dstBuf;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBuf.position(), dstLine = dstBuf.position();
        double[] buffer = new double[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            if (flip) {
                srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
            } else {
                srcBuf.position(srcLine);
            }
            dstBuf.position(dstLine);
            w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
            if (channels > 1) {
                for (int x = 0; x < w; x+=channels) {
                    for (int z = 0; z < channels; z++) {
                        double in = srcBuf.get();
                        double out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = Math.pow(in, gamma);
                        }
                        buffer[z] = out;
                    }
                    for (int z = channels-1; z >= 0; z--) {
                        dstBuf.put(buffer[z]);
                    }
                }
            } else {
                for (int x = 0; x < w; x++) {
                    double in = srcBuf.get();
                    double out;
                    if (gamma == 1.0) {
                        out = in;
                    } else {
                        out = Math.pow(in, gamma);
                    }
                    dstBuf.put(out);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }

    public static void applyGamma(Frame frame, double gamma) {
        applyGamma(frame.image[0].position(0), frame.imageDepth, frame.imageStride, gamma);
    }
    public static void applyGamma(Buffer buffer, int depth, int stride, double gamma) {
        if (gamma == 1.0) {
            return;
        }
        switch (depth) {
            case Frame.DEPTH_UBYTE:
                flipCopyWithGamma(((ByteBuffer)buffer).asReadOnlyBuffer(), stride, (ByteBuffer)buffer, stride, false, gamma, false, 0);
                break;
            case Frame.DEPTH_BYTE:
                flipCopyWithGamma(((ByteBuffer)buffer).asReadOnlyBuffer(), stride, (ByteBuffer)buffer, stride, true, gamma, false, 0);
                break;
            case Frame.DEPTH_USHORT:
                flipCopyWithGamma(((ShortBuffer)buffer).asReadOnlyBuffer(), stride, (ShortBuffer)buffer, stride, false, gamma, false, 0);
                break;
            case Frame.DEPTH_SHORT:
                flipCopyWithGamma(((ShortBuffer)buffer).asReadOnlyBuffer(), stride, (ShortBuffer)buffer, stride, true, gamma, false, 0);
                break;
            case Frame.DEPTH_INT:
                flipCopyWithGamma(((IntBuffer)buffer).asReadOnlyBuffer(), stride, (IntBuffer)buffer, stride, gamma, false, 0);
                break;
            case Frame.DEPTH_FLOAT:
                flipCopyWithGamma(((FloatBuffer)buffer).asReadOnlyBuffer(), stride, (FloatBuffer)buffer, stride, gamma, false, 0);
                break;
            case Frame.DEPTH_DOUBLE:
                flipCopyWithGamma(((DoubleBuffer)buffer).asReadOnlyBuffer(), stride, (DoubleBuffer)buffer, stride, gamma, false, 0);
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
        Buffer in = frame.image[0].position(roi == null ? 0 : roi.y*frame.imageStride + roi.x*frame.imageChannels);
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
            flipCopyWithGamma((ByteBuffer)in, frame.imageStride, ByteBuffer.wrap(a, start, a.length - start), step, false, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferDouble) {
            double[] a = ((DataBufferDouble)out).getData();
            flipCopyWithGamma((DoubleBuffer)in, frame.imageStride, DoubleBuffer.wrap(a, start, a.length - start), step, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferFloat) {
            float[] a = ((DataBufferFloat)out).getData();
            flipCopyWithGamma((FloatBuffer)in, frame.imageStride, FloatBuffer.wrap(a, start, a.length - start), step, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferInt) {
            int[] a = ((DataBufferInt)out).getData();
            flipCopyWithGamma((IntBuffer)in, frame.imageStride, IntBuffer.wrap(a, start, a.length - start), step, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferShort) {
            short[] a = ((DataBufferShort)out).getData();
            flipCopyWithGamma((ShortBuffer)in, frame.imageStride, ShortBuffer.wrap(a, start, a.length - start), step, true, gamma, false, flipChannels ? channels : 0);
        } else if (out instanceof DataBufferUShort) {
            short[] a = ((DataBufferUShort)out).getData();
            flipCopyWithGamma((ShortBuffer)in, frame.imageStride, ShortBuffer.wrap(a, start, a.length - start), step, false, gamma, false, flipChannels ? channels : 0);
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
        Buffer out = frame.image[0].position(roi == null ? 0 : roi.y*frame.imageStride + roi.x*frame.imageChannels);
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
            flipCopyWithGamma(ByteBuffer.wrap(a, start, a.length - start), step, (ByteBuffer)out, frame.imageStride, false, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferDouble) {
            double[] a = ((DataBufferDouble)in).getData();
            flipCopyWithGamma(DoubleBuffer.wrap(a, start, a.length - start), step, (DoubleBuffer)out, frame.imageStride, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferFloat) {
            float[] a = ((DataBufferFloat)in).getData();
            flipCopyWithGamma(FloatBuffer.wrap(a, start, a.length - start), step, (FloatBuffer)out, frame.imageStride, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferInt) {
            int[] a = ((DataBufferInt)in).getData();
            int stride = frame.imageStride;
            if (out instanceof ByteBuffer) {
                out = ((ByteBuffer)out).asIntBuffer();
                stride /= 4;
            }
            flipCopyWithGamma(IntBuffer.wrap(a, start, a.length - start), step, (IntBuffer)out, stride, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferShort) {
            short[] a = ((DataBufferShort)in).getData();
            flipCopyWithGamma(ShortBuffer.wrap(a, start, a.length - start), step, (ShortBuffer)out, frame.imageStride, true, gamma, false, flipChannels ? channels : 0);
        } else if (in instanceof DataBufferUShort) {
            short[] a = ((DataBufferUShort)in).getData();
            flipCopyWithGamma(ShortBuffer.wrap(a, start, a.length - start), step, (ShortBuffer)out, frame.imageStride, false, gamma, false, flipChannels ? channels : 0);
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
        if (frame == null) {
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
            frame = new Frame(image.getWidth(), image.getHeight(), depth, numChannels);
        }
        copy(image, frame, gamma, flipChannels, null);
        return frame;
    }
}
