/*
 * Copyright (C) 2011 Samuel Audet
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

package com.googlecode.javacv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.Loader;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.freenect.*;

/**
 *
 * @author Samuel Audet
 */
public class OpenKinectFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();

        freenect_context ctx = new freenect_context(null);
        int err = freenect_init(ctx, null);
        if (err < 0) {
            throw new Exception("freenect_init() Error " + err + ": Failed to init context.");
        }

        int count = freenect_num_devices(ctx);
        if (count < 0) {
            throw new Exception("freenect_num_devices() Error " + err + ": Failed to get number of devices.");
        }
        String[] descriptions = new String[count];
        for (int i = 0; i < descriptions.length; i++) {
            descriptions[i] = "Kinect #" + i;
        }

        err = freenect_shutdown(ctx);
        if (err < 0) {
            throw new Exception("freenect_shutdown() Error " + err + ": Failed to shutdown context.");
        }

        return descriptions;
    }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(com.googlecode.javacv.cpp.freenect.class);
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception(t);
                }
            }
        }
    }

    public OpenKinectFrameGrabber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() {
        try {
            release();
        } catch (Exception ex) { }
    }

    private int deviceNumber = 0;
    private boolean depth = false; // default to "video"
    private IplImage rawImage = null, grayImage = null;
    private BytePointer rawImageData = new BytePointer((Pointer)null);
    private int[] timestamp = { 0 };

    @Override public double getGamma() {
        // I guess a default gamma of 2.2 is reasonable...
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    public void start() throws Exception {
        depth = "depth".equalsIgnoreCase(format);
    }

    public void stop() throws Exception {
        freenect_sync_stop();
    }

    public void trigger() throws Exception {
        int fmt = bpp; // default bpp == 0 == FREENECT_DEPTH_11BIT == FREENECT_VIDEO_RGB
        for (int i = 0; i < triggerFlushSize; i++) {
            if (depth) {
                int err = freenect_sync_get_depth(rawImageData, timestamp, deviceNumber, fmt);
                if (err != 0) {
                    throw new Exception("freenect_sync_get_depth() Error " + err + ": Failed to get video synchronously.");
                }
            } else {
                int err = freenect_sync_get_video(rawImageData, timestamp, deviceNumber, fmt);
                if (err != 0) {
                    throw new Exception("freenect_sync_get_video() Error " + err + ": Failed to get video synchronously.");
                }
            }
        }
    }

    public IplImage grab() throws Exception {
        int fmt = bpp; // default bpp == 0 == FREENECT_DEPTH_11BIT == FREENECT_VIDEO_RGB
        int iplDepth = IPL_DEPTH_8U, channels = 3;
        if (depth) {
            int err = freenect_sync_get_depth(rawImageData, timestamp, deviceNumber, fmt);
            if (err != 0) {
                throw new Exception("freenect_sync_get_depth() Error " + err + ": Failed to get depth synchronously.");
            }
            switch (fmt) {
                case FREENECT_DEPTH_11BIT:
                case FREENECT_DEPTH_10BIT: iplDepth = IPL_DEPTH_16U; channels = 1; break;
                case FREENECT_DEPTH_11BIT_PACKED:
                case FREENECT_DEPTH_10BIT_PACKED:
                default: assert false;
            }
        } else {
            int err = freenect_sync_get_video(rawImageData, timestamp, deviceNumber, fmt);
            if (err != 0) {
                throw new Exception("freenect_sync_get_video() Error " + err + ": Failed to get video synchronously.");
            }
            switch (fmt) {
                case FREENECT_VIDEO_RGB:      iplDepth = IPL_DEPTH_8U; channels = 3; break;
                case FREENECT_VIDEO_BAYER:
                case FREENECT_VIDEO_IR_8BIT:  iplDepth = IPL_DEPTH_8U; channels = 1; break;
                case FREENECT_VIDEO_IR_10BIT: iplDepth = IPL_DEPTH_16U; channels = 1; break;
                case FREENECT_VIDEO_YUV_RGB:  iplDepth = IPL_DEPTH_8U; channels = 3; break;
                case FREENECT_VIDEO_YUV_RAW:  iplDepth = IPL_DEPTH_8U; channels = 2; break;
                case FREENECT_VIDEO_IR_10BIT_PACKED:
                default: assert false;
            }
        }
        int w = 640, h = 480; // how to get the resolution ??
        if (rawImage == null || rawImage.width() != w || rawImage.height() != h) {
            rawImage = IplImage.createHeader(w, h, iplDepth, channels);
        }
        cvSetData(rawImage, rawImageData, w*channels*iplDepth/8);

        if (iplDepth > 8 && ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer  bb  = rawImage.getByteBuffer();
            ShortBuffer in  = bb.order(ByteOrder.BIG_ENDIAN   ).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

        if (colorMode == ColorMode.GRAY && channels == 3) {
            if (grayImage == null) {
                grayImage = IplImage.create(w, h, iplDepth, 1);
            }
            cvCvtColor(rawImage, grayImage, CV_RGB2GRAY);
            grayImage.timestamp = timestamp[0];
            return grayImage;
        } else if (colorMode == ColorMode.BGR && channels == 3) {
            cvCvtColor(rawImage, rawImage, CV_RGB2BGR);
            rawImage.timestamp = timestamp[0];
            return rawImage;
        } else {
            rawImage.timestamp = timestamp[0];
            return rawImage;
        }
    }
}
