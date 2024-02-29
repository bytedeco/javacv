/*
 * Copyright (C) 2011-2012 Samuel Audet
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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.Loader;

import org.bytedeco.libfreenect.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.libfreenect.global.freenect.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

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

    public static OpenKinectFrameGrabber createDefault(File deviceFile)   throws Exception { throw new Exception(OpenKinectFrameGrabber.class + " does not support device files."); }
    public static OpenKinectFrameGrabber createDefault(String devicePath) throws Exception { throw new Exception(OpenKinectFrameGrabber.class + " does not support device paths."); }
    public static OpenKinectFrameGrabber createDefault(int deviceNumber)  throws Exception { return new OpenKinectFrameGrabber(deviceNumber); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.libfreenect.global.freenect.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + OpenKinectFrameGrabber.class, t);
            }
        }
    }

    public OpenKinectFrameGrabber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private int deviceNumber = 0;
    private boolean depth = false; // default to "video"
    private BytePointer rawDepthImageData = new BytePointer((Pointer)null),
                        rawVideoImageData = new BytePointer((Pointer)null),
                        rawIRImageData = new BytePointer((Pointer)null);
    private IplImage rawDepthImage = null, rawVideoImage = null, rawIRImage = null, returnImage = null;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();
    private int[] timestamp = { 0 };
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private int depthFormat = -1;
    private int videoFormat = -1;

    public ByteOrder getByteOrder() {
        return byteOrder;
    }
    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public int getDepthFormat() {
        return depthFormat;
    }
    public void setDepthFormat(int depthFormat) {
        this.depthFormat = depthFormat;
    }

    public int getVideoFormat() {
        return videoFormat;
    }
    public void setVideoFormat(int videoFormat) {
        this.videoFormat = videoFormat;
    }

    @Override public double getGamma() {
        // I guess a default gamma of 2.2 is reasonable...
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    @Override public void setImageMode(ImageMode imageMode) {
        if (imageMode != this.imageMode) {
            returnImage = null;
        }
        super.setImageMode(imageMode);
    }

    public void start() throws Exception {
        depth = "depth".equalsIgnoreCase(format);
    }

    public void stop() throws Exception {
        freenect_sync_stop();
    }

    public void trigger() throws Exception {
        for (int i = 0; i < numBuffers+1; i++) {
            if (depth) {
                int fmt = depthFormat < 0 ? bpp : depthFormat; // default bpp == 0 == FREENECT_DEPTH_11BIT
                int err = freenect_sync_get_depth(rawDepthImageData, timestamp, deviceNumber, fmt);
                if (err != 0) {
                    throw new Exception("freenect_sync_get_depth() Error " + err + ": Failed to get depth synchronously.");
                }
            } else {
                int fmt = videoFormat < 0 ? bpp : videoFormat; // default bpp == 0 == FREENECT_VIDEO_RGB
                int err = freenect_sync_get_video(rawVideoImageData, timestamp, deviceNumber, fmt);
                if (err != 0) {
                    throw new Exception("freenect_sync_get_video() Error " + err + ": Failed to get video synchronously.");
                }
            }
        }
    }

    public IplImage grabDepth() throws Exception {
        int fmt = depthFormat < 0 ? bpp : depthFormat; // default bpp == 0 == FREENECT_DEPTH_11BIT
        int iplDepth = IPL_DEPTH_16U, channels = 1;
        switch (fmt) {
            case FREENECT_DEPTH_11BIT:
            case FREENECT_DEPTH_REGISTERED:
            case FREENECT_DEPTH_MM:
            case FREENECT_DEPTH_10BIT: iplDepth = IPL_DEPTH_16U; channels = 1; break;
            case FREENECT_DEPTH_11BIT_PACKED:
            case FREENECT_DEPTH_10BIT_PACKED:
            default: assert false;
        }

        int err = freenect_sync_get_depth(rawDepthImageData, timestamp, deviceNumber, fmt);
        if (err != 0) {
            throw new Exception("freenect_sync_get_depth() Error " + err + ": Failed to get depth synchronously.");
        }

        int w = 640, h = 480; // how to get the resolution ??
        if (rawDepthImage == null || rawDepthImage.width() != w || rawDepthImage.height() != h) {
            rawDepthImage = IplImage.createHeader(w, h, iplDepth, channels);
        }
        cvSetData(rawDepthImage, rawDepthImageData, w*channels*iplDepth/8);

        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer  bb  = rawDepthImage.getByteBuffer();
            ShortBuffer in  = bb.order(ByteOrder.BIG_ENDIAN   ).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

        super.timestamp = timestamp[0];
        return rawDepthImage;
    }

    public IplImage grabVideo() throws Exception {
        int fmt = videoFormat < 0 ? bpp : videoFormat; // default bpp == 0 == FREENECT_VIDEO_RGB
        int iplDepth = IPL_DEPTH_8U, channels = 3;
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

        int err = freenect_sync_get_video(rawVideoImageData, timestamp, deviceNumber, fmt);
        if (err != 0) {
            throw new Exception("freenect_sync_get_video() Error " + err + ": Failed to get video synchronously.");
        }

        int w = 640, h = 480; // how to get the resolution ??
        if (rawVideoImage == null || rawVideoImage.width() != w || rawVideoImage.height() != h) {
            rawVideoImage = IplImage.createHeader(w, h, iplDepth, channels);
        }
        cvSetData(rawVideoImage, rawVideoImageData, w*channels*iplDepth/8);

        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer  bb  = rawVideoImage.getByteBuffer();
            ShortBuffer in  = bb.order(ByteOrder.BIG_ENDIAN   ).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

        if (channels == 3) {
            cvCvtColor(rawVideoImage, rawVideoImage, CV_RGB2BGR);
        }
        super.timestamp = timestamp[0];
        return rawVideoImage;
    }
    
    public IplImage grabIR() throws Exception {
        int iplDepth = IPL_DEPTH_8U, channels = 1;

        int err = freenect_sync_get_video(rawIRImageData, timestamp, deviceNumber, FREENECT_VIDEO_IR_8BIT);
        if (err != 0) {
            throw new Exception("freenect_sync_get_video() Error " + err + ": Failed to get video synchronously.");
        }

        int w = 640, h = 480; // how to get the resolution ??
        if (rawIRImage == null || rawIRImage.width() != w || rawIRImage.height() != h) {
            rawIRImage = IplImage.createHeader(w, h, iplDepth, channels);
        }
        cvSetData(rawIRImage, rawIRImageData, w*channels*iplDepth/8);

        super.timestamp = timestamp[0];
        return rawIRImage;
    }

    public Frame grab() throws Exception {
        IplImage image = depth ? grabDepth() :  grabVideo();
        int w = image.width();
        int h = image.height();
        int iplDepth = image.depth();
        int channels = image.nChannels();

        if (imageMode == ImageMode.COLOR && channels == 1) {
            if (returnImage == null) {
                returnImage = IplImage.create(w, h, iplDepth, 3);
            }
            cvCvtColor(image, returnImage, CV_GRAY2BGR);
            return converter.convert(returnImage);
        } else if (imageMode == ImageMode.GRAY && channels == 3) {
            if (returnImage == null) {
                returnImage = IplImage.create(w, h, iplDepth, 1);
            }
            cvCvtColor(image, returnImage, CV_BGR2GRAY);
            return converter.convert(returnImage);
        } else {
            return converter.convert(image);
        }
    }
}
