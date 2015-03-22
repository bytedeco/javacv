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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A class to manage the data of audio and video frames. It it used by
 * {@link CanvasFrame}, {@link FrameGrabber}, {@link FrameRecorder}, and their
 * subclasses. We can also make the link with other APIs, such as Android,
 * Java 2D, FFmpeg, and OpenCV, via a {@link FrameConverter}.
 *
 * @author Samuel Audet
 */
public class Frame {
    /** A flag set by a FrameGrabber or a FrameRecorder to indicate a key frame. */
    public boolean keyFrame;

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

    /** The underlying data object, for example, AVFrame, IplImage, or Mat. */
    public Object opaque;

    public Frame() { }

    /** Allocates a new packed image frame in native memory where rows are 8-byte aligned. */
    public Frame(int width, int height, int depth, int channels) {
        int pixelSize = Math.abs(depth) / 8;
        this.imageWidth = width;
        this.imageHeight = height;
        this.imageDepth = depth;
        this.imageChannels = channels;
        this.imageStride = ((imageWidth * imageChannels * pixelSize + 7) & ~7) / pixelSize; // 8-byte aligned
        this.image = new Buffer[1];

        ByteBuffer buffer = ByteBuffer.allocateDirect(imageHeight * imageStride * pixelSize).order(ByteOrder.nativeOrder());
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
    }
}
