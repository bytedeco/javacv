/*
 * Copyright (C) 2015-2021 Samuel Audet
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
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.EnumSet;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.LongPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.Indexable;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacpp.indexer.LongIndexer;
import org.bytedeco.javacpp.indexer.ShortIndexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.indexer.UShortIndexer;

/**
 * A class to manage the data of audio and video frames. It it used by
 * {@link CanvasFrame}, {@link FrameGrabber}, {@link FrameRecorder}, and their
 * subclasses. We can also make the link with other APIs, such as Android,
 * Java 2D, FFmpeg, and OpenCV, via a {@link FrameConverter}.
 *
 * @author Samuel Audet
 */
public class Frame implements AutoCloseable, Indexable {
    /** A flag set by a FrameGrabber or a FrameRecorder to indicate a key frame. */
    public boolean keyFrame;

    /** The type of the image frame ('I', 'P', 'B', etc). */
    public char pictType;

    /** Constants to be used for {@link #imageDepth}. */
    public static final int
            DEPTH_BYTE   =  -8,
            DEPTH_UBYTE  =   8,
            DEPTH_SHORT  = -16,
            DEPTH_USHORT =  16,
            DEPTH_INT    = -32,
            DEPTH_LONG   = -64,
            DEPTH_FLOAT  =  32,
            DEPTH_DOUBLE =  64;

    /** Constants defining data type in the frame. */
    public static enum Type {
        VIDEO,
        AUDIO,
        DATA,
        SUBTITLE,
        ATTACHMENT
    }

    /** Information associated with the {@link #image} field. */
    public int imageWidth, imageHeight, imageDepth, imageChannels, imageStride;

    /**
     * Buffers to hold image pixels from multiple channels for a video frame.
     * Most of the software supports packed data only, but an array is provided
     * to allow users to store images in a planar format as well.
     */
    public Buffer[] image;

    /** Information associated with the {@link #samples} field. */
    public int sampleRate, audioChannels;

    /** Buffers to hold audio samples from multiple channels for an audio frame. */
    public Buffer[] samples;

    /** Buffer to hold a data stream associated with a frame. */
    public ByteBuffer data;

    /** Stream number the audio|video|other data is associated with. */
    public int streamIndex;

    /** The type of the stream. */
    public Type type;

    /** The underlying data object, for example, Pointer, AVFrame, IplImage, or Mat. */
    public Object opaque;

    /** Timestamp of the frame creation in microseconds. */
    public long timestamp;

    /** Returns {@code Math.abs(depth) / 8}. */
    public static int pixelSize(int depth) {
        return Math.abs(depth) / 8;
    }

    /** Empty constructor. */
    public Frame() { }

    /** Allocates a new packed image frame in native memory where rows are 8-byte aligned. */
    public Frame(int width, int height, int depth, int channels) {
        this(width, height, depth, channels, ((width * channels * pixelSize(depth) + 7) & ~7) / pixelSize(depth));
    }
    public Frame(int width, int height, int depth, int channels, int imageStride) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.imageDepth = depth;
        this.imageChannels = channels;
        this.imageStride = imageStride;
        this.pictType = '\0';
        this.image = new Buffer[1];
        this.data = null;
        this.streamIndex = -1;
        this.type = null;

        Pointer pointer = new BytePointer(imageHeight * imageStride * pixelSize(depth));
        ByteBuffer buffer = pointer.asByteBuffer();
        switch (imageDepth) {
            case DEPTH_BYTE:
            case DEPTH_UBYTE:  image[0] = buffer;                  break;
            case DEPTH_SHORT:
            case DEPTH_USHORT: image[0] = buffer.asShortBuffer();  break;
            case DEPTH_INT:    image[0] = buffer.asIntBuffer();    break;
            case DEPTH_LONG:   image[0] = buffer.asLongBuffer();   break;
            case DEPTH_FLOAT:  image[0] = buffer.asFloatBuffer();  break;
            case DEPTH_DOUBLE: image[0] = buffer.asDoubleBuffer(); break;
            default: throw new UnsupportedOperationException("Unsupported depth value: " + imageDepth);
        }
        opaque = new Pointer[] {pointer.retainReference()};
    }

    /** Returns {@code createIndexer(true, 0)}. */
    public <I extends Indexer> I createIndexer() {
        return (I)createIndexer(true, 0);
    }
    @Override public <I extends Indexer> I createIndexer(boolean direct) {
        return (I)createIndexer(direct, 0);
    }
    /** Returns an {@link Indexer} for the <i>i</i>th image plane. */
    public <I extends Indexer> I createIndexer(boolean direct, int i) {
        long[] sizes = {imageHeight, imageWidth, imageChannels};
        long[] strides = {imageStride, imageChannels, 1};
        Buffer buffer = image[i];
        Object array = buffer.hasArray() ? buffer.array() : null;
        switch (imageDepth) {
            case DEPTH_UBYTE:
                return array != null ? (I)UByteIndexer.create((byte[])array, sizes, strides).indexable(this)
                            : direct ? (I)UByteIndexer.create((ByteBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)UByteIndexer.create(new BytePointer((ByteBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_BYTE:
                return array != null ? (I)ByteIndexer.create((byte[])array, sizes, strides).indexable(this)
                            : direct ? (I)ByteIndexer.create((ByteBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)ByteIndexer.create(new BytePointer((ByteBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_USHORT:
                return array != null ? (I)UShortIndexer.create((short[])array, sizes, strides).indexable(this)
                            : direct ? (I)UShortIndexer.create((ShortBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)UShortIndexer.create(new ShortPointer((ShortBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_SHORT:
                return array != null ? (I)ShortIndexer.create((short[])array, sizes, strides).indexable(this)
                            : direct ? (I)ShortIndexer.create((ShortBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)ShortIndexer.create(new ShortPointer((ShortBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_INT:
                return array != null ? (I)IntIndexer.create((int[])array, sizes, strides).indexable(this)
                            : direct ? (I)IntIndexer.create((IntBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)IntIndexer.create(new IntPointer((IntBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_LONG:
                return array != null ? (I)LongIndexer.create((long[])array, sizes, strides).indexable(this)
                            : direct ? (I)LongIndexer.create((LongBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)LongIndexer.create(new LongPointer((LongBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_FLOAT:
                return array != null ? (I)FloatIndexer.create((float[])array, sizes, strides).indexable(this)
                            : direct ? (I)FloatIndexer.create((FloatBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)FloatIndexer.create(new FloatPointer((FloatBuffer)buffer), sizes, strides, false).indexable(this);
            case DEPTH_DOUBLE:
                return array != null ? (I)DoubleIndexer.create((double[])array, sizes, strides).indexable(this)
                            : direct ? (I)DoubleIndexer.create((DoubleBuffer)buffer, sizes, strides).indexable(this)
                                     : (I)DoubleIndexer.create(new DoublePointer((DoubleBuffer)buffer), sizes, strides, false).indexable(this);
            default: assert false;
        }
        return null;
    }

    /**Care must be taken if this method is to be used in conjunction with movie recordings.
     *  Cloning a frame containing a full HD picture (alpha channel included) would take 1920 x 1080 * 4 = 8.294.400 Bytes.
     *  Expect a heap overflow exception when using this method without cleaning up.
     *
     * @return A deep copy of this frame.
     * @see {@link #cloneBufferArray}
     *
     * Extension proposed by Dragos Dutu
     * */
    @Override
    public Frame clone() {
        Frame newFrame = new Frame();

        // Video part
        newFrame.imageWidth = imageWidth;
        newFrame.imageHeight = imageHeight;
        newFrame.imageDepth = imageDepth;
        newFrame.imageChannels = imageChannels;
        newFrame.imageStride = imageStride;
        newFrame.keyFrame = keyFrame;
        newFrame.pictType = pictType;
        newFrame.streamIndex = streamIndex;
        newFrame.type = type;
        newFrame.opaque = new Pointer[3];
        if (image != null) {
            newFrame.image = new Buffer[image.length];
            ((Pointer[])newFrame.opaque)[0] = cloneBufferArray(image, newFrame.image);
        }

        // Audio part
        newFrame.audioChannels = audioChannels;
        newFrame.sampleRate = sampleRate;
        if (samples != null) {
            newFrame.samples = new Buffer[samples.length];
            ((Pointer[])newFrame.opaque)[1] = cloneBufferArray(samples, newFrame.samples);
        }

        // Other data streams
        if (data != null) {
            ByteBuffer[] dst = new ByteBuffer[1];
            ((Pointer[])newFrame.opaque)[2] = cloneBufferArray(new ByteBuffer[]{data}, dst);
            newFrame.data = dst[0];
        }

        // Add timestamp
        newFrame.timestamp = timestamp;

        return newFrame;
    }

    /**
     * This private method takes a buffer array as input and returns a deep copy.
     * It is assumed that all buffers in the input array are of the same subclass.
     *
     * @param srcBuffers - Buffer array to be cloned
     * @param clonedBuffers - Buffer array to fill with clones
     * @return Opaque object to store
     *
     *  @author Extension proposed by Dragos Dutu
     */
    private static Pointer cloneBufferArray(Buffer[] srcBuffers, Buffer[] clonedBuffers) {
        Pointer opaque = null;

        if (srcBuffers != null && srcBuffers.length > 0) {
            int totalCapacity = 0;
            for (int i = 0; i < srcBuffers.length; i++) {
                srcBuffers[i].rewind();
                totalCapacity += srcBuffers[i].capacity();
            }

            /*
             * In order to optimize the transfer we need a type check.
             *
             * Most CPUs support hardware memory transfer for different data
             * types, so it's faster to copy more bytes at once rather
             * than one byte per iteration as in case of ByteBuffer.
             *
             * For example, Intel CPUs support MOVSB (byte transfer), MOVSW
             * (word transfer), MOVSD (double word transfer), MOVSS (32 bit
             * scalar single precision floating point), MOVSQ (quad word
             * transfer) and so on...
             *
             * Type checking may be improved by changing the order in
             * which a buffer is checked against. If it's likely that the
             * expected buffer is of type "ShortBuffer", then it should be
             * checked at first place.
             *
             */

            if (srcBuffers[0] instanceof ByteBuffer) {
                BytePointer pointer = new BytePointer(totalCapacity);
                for (int i = 0; i < srcBuffers.length; i++) {
                    clonedBuffers[i] = pointer.limit(pointer.position() + srcBuffers[i].limit())
                            .asBuffer().put((ByteBuffer)srcBuffers[i]);
                    pointer.position(pointer.limit());
                }
                opaque = pointer;
            } else if (srcBuffers[0] instanceof ShortBuffer) {
                ShortPointer pointer = new ShortPointer(totalCapacity);
                for (int i = 0; i < srcBuffers.length; i++) {
                    clonedBuffers[i] = pointer.limit(pointer.position() + srcBuffers[i].limit())
                            .asBuffer().put((ShortBuffer)srcBuffers[i]);
                    pointer.position(pointer.limit());
                }
                opaque = pointer;
            } else if (srcBuffers[0] instanceof IntBuffer) {
                IntPointer pointer = new IntPointer(totalCapacity);
                for (int i = 0; i < srcBuffers.length; i++) {
                    clonedBuffers[i] = pointer.limit(pointer.position() + srcBuffers[i].limit())
                            .asBuffer().put((IntBuffer)srcBuffers[i]);
                    pointer.position(pointer.limit());
                }
                opaque = pointer;
            } else if (srcBuffers[0] instanceof LongBuffer) {
                LongPointer pointer = new LongPointer(totalCapacity);
                for (int i = 0; i < srcBuffers.length; i++) {
                    clonedBuffers[i] = pointer.limit(pointer.position() + srcBuffers[i].limit())
                            .asBuffer().put((LongBuffer)srcBuffers[i]);
                    pointer.position(pointer.limit());
                }
                opaque = pointer;
            } else if (srcBuffers[0] instanceof FloatBuffer) {
                FloatPointer pointer = new FloatPointer(totalCapacity);
                for (int i = 0; i < srcBuffers.length; i++) {
                    clonedBuffers[i] = pointer.limit(pointer.position() + srcBuffers[i].limit())
                            .asBuffer().put((FloatBuffer)srcBuffers[i]);
                    pointer.position(pointer.limit());
                }
                opaque = pointer;
            } else if (srcBuffers[0] instanceof DoubleBuffer) {
                DoublePointer pointer = new DoublePointer(totalCapacity);
                for (int i = 0; i < srcBuffers.length; i++) {
                    clonedBuffers[i] = pointer.limit(pointer.position() + srcBuffers[i].limit())
                            .asBuffer().put((DoubleBuffer)srcBuffers[i]);
                    pointer.position(pointer.limit());
                }
                opaque = pointer;
            }

            for (int i = 0; i < srcBuffers.length; i++) {
                srcBuffers[i].rewind();
                clonedBuffers[i].rewind();
            }
        }

        if (opaque != null) {
            opaque.retainReference();
        }
        return opaque;
    }

    /** Returns types of data containing in the frame */
    public EnumSet<Type> getTypes() {
        EnumSet<Type> type = EnumSet.noneOf(Type.class);
        if (image != null) type.add(Type.VIDEO);
        if (samples != null) type.add(Type.AUDIO);
        if (data != null) type.add(Type.DATA);
        return type;
    }

    @Override public void close() {
        if (opaque instanceof Pointer[]) {
            for (Pointer p : (Pointer[])opaque) {
                if (p != null) {
                    p.releaseReference();
                    p = null;
                }
            }
            opaque = null;
        }
    }
}
