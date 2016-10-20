/*
 * Copyright (C) 2014 Jeremy Laviole, Samuel Audet
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
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.RealSense;
import org.bytedeco.javacpp.RealSense.context;
import org.bytedeco.javacpp.RealSense.device;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 *
 * @author Jeremy Laviole
 */
public class RealSenseFrameGrabber extends FrameGrabber {

    public static String[] getDeviceDescriptions() throws FrameGrabber.Exception {
        tryLoad();
        String[] desc = new String[1];
        desc[0] = "No description yet.";
        return desc;
    }

    public static int DEFAULT_DEPTH_WIDTH = 640;
    public static int DEFAULT_DEPTH_HEIGHT = 480;
    public static int DEFAULT_COLOR_WIDTH = 640;
    public static int DEFAULT_COLOR_HEIGHT = 480;

    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private int depthImageWidth = DEFAULT_DEPTH_WIDTH;
    private int depthImageHeight = DEFAULT_DEPTH_HEIGHT;
    private int depthFrameRate = 60;

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public static RealSenseFrameGrabber createDefault(int deviceNumber) throws FrameGrabber.Exception {
        return new RealSenseFrameGrabber(deviceNumber);
    }
    
    public static RealSenseFrameGrabber createDefault(File deviceFile)   throws Exception { throw new Exception(RealSenseFrameGrabber.class + " does not support File devices."); }
    public static RealSenseFrameGrabber createDefault(String devicePath) throws Exception { throw new Exception(RealSenseFrameGrabber.class + " does not support path."); }

    private static FrameGrabber.Exception loadingException = null;

    public static void tryLoad() throws FrameGrabber.Exception {
        if (loadingException != null) {
            loadingException.printStackTrace();
            throw loadingException;
        } else {
            try {
                if(context != null){
                    return;
                }
                Loader.load(org.bytedeco.javacpp.RealSense.class);
                
                // Context is shared accross cameras. 
                context = new context();
                System.out.println("RealSense devices found: " + context.get_device_count());
            } catch (Throwable t) {
                throw loadingException = new FrameGrabber.Exception("Failed to load " + RealSenseFrameGrabber.class, t);
            }
        }
    }

    private static context context;
    private final device device;
    private boolean depth = false; // default to "video"
    private boolean colorEnabled = false;
    private boolean depthEnabled = false;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    public RealSenseFrameGrabber(int deviceNumber) {
        if (context == null || context.get_device_count() <= deviceNumber) {
            System.out.println("FATAL error: Realsense camera: " + deviceNumber + " not connected/found");
            System.exit(-1);
        }
        device = context.get_device(deviceNumber);
    }
    
    public static void main(String[] args) {
        context context = new context();
        System.out.println("Devices found: " + context.get_device_count());
        device device = context.get_device(0);
        System.out.println("Using device 0, an " + device.get_name());
        System.out.println(" Serial number: " + device.get_serial());
    }

    public void enableColorStream() {
        if (!colorEnabled) {
            if (imageWidth == 0) {
                imageWidth = DEFAULT_COLOR_WIDTH;
            }
            if (imageHeight == 0) {
                imageHeight = DEFAULT_COLOR_HEIGHT;
            }
            device.enable_stream(RealSense.color, imageWidth, imageHeight, RealSense.rgb8, (int) frameRate);
            colorEnabled = true;
        }
    }

    public void disableColorStream() {
        if (colorEnabled) {
            device.disable_stream(RealSense.color);
            colorEnabled = false;
        }
    }

    public void enableDepthStream() {
        if (!depthEnabled) {
            device.enable_stream(RealSense.depth, depthImageWidth, depthImageHeight, RealSense.z16, depthFrameRate);
            depthEnabled = true;
        }
    }

    public void disableDepthStream() {
        if (depthEnabled) {
            device.disable_stream(RealSense.depth);
            depthEnabled = false;
        }
    }

    public void release() throws FrameGrabber.Exception {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    /**
     * Warning can lead to unsafe situations.
     *
     * @return
     */
    public device getRealSenseDevice() {
        return this.device;
    }

    public float getDepthScale() {
        return device.get_depth_scale();
    }

    @Override
    public double getFrameRate() {  // TODO: check this. 
        return super.getFrameRate();
    }

    @Override
    public void start() throws FrameGrabber.Exception {
        device.start();
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void stop() throws FrameGrabber.Exception {
        device.stop();
        frameNumber = 0;
    }
    private Pointer rawDepthImageData = new Pointer((Pointer) null),
            rawVideoImageData = new Pointer((Pointer) null);
    private IplImage rawDepthImage = null, rawVideoImage = null, returnImage = null;

    public IplImage grabDepth() throws Exception {

        if (!depthEnabled) {
            System.out.println("Depth stream not enabled, impossible to get the image.");
            return null;
        }
        rawDepthImageData = device.get_frame_data(RealSense.depth);
//        ShortBuffer bb = data.position(0).limit(640 * 480 * 2).asByteBuffer().asShortBuffer();

        int iplDepth = IPL_DEPTH_16U, channels = 1;

        int w = this.getDepthImageWidth(), h = this.getDepthImageHeight(); // how to get the resolution ??
        if (rawDepthImage == null || rawDepthImage.width() != w || rawDepthImage.height() != h) {
            rawDepthImage = IplImage.createHeader(w, h, iplDepth, channels);
        }
        cvSetData(rawDepthImage, rawDepthImageData, w * channels * iplDepth / 8);

        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer bb = rawDepthImage.getByteBuffer();
            ShortBuffer in = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

        return rawDepthImage;
    }

    public IplImage grabVideo() {

        if (!colorEnabled) {
            System.out.println("Color stream not enabled, impossible to get the image.");
            return null;
        }

//        int fmt = videoFormat < 0 ? bpp : videoFormat; // default bpp == 0 == FREENECT_VIDEO_RGB
        int iplDepth = IPL_DEPTH_8U, channels = 3;

        rawVideoImageData = device.get_frame_data(RealSense.color);
        int w = this.getImageWidth(), h = this.getImageHeight(); // how to get the resolution ??
        if (rawVideoImage == null || rawVideoImage.width() != w || rawVideoImage.height() != h) {
            rawVideoImage = IplImage.createHeader(w, h, iplDepth, channels);
        }
        cvSetData(rawVideoImage, rawVideoImageData, w * channels * iplDepth / 8);

        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer bb = rawVideoImage.getByteBuffer();
            ShortBuffer in = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

//        if (channels == 3) {
//            cvCvtColor(rawVideoImage, rawVideoImage, CV_RGB2BGR);
//        }
        return rawVideoImage;
    }

    /**
     *
     * @return null always, use grabColor and grabDepth instead.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     */
    public Frame grab() throws Exception {
        device.wait_for_frames();

        if (colorEnabled) {
            IplImage image = grabVideo();
            return converter.convert(image);
        }
        return null;
    }

    @Override
    public void trigger() throws Exception {
        device.wait_for_frames();
    }

    public int getDepthImageWidth() {
        return depthImageWidth;
    }

    public void setDepthImageWidth(int depthImageWidth) {
        this.depthImageWidth = depthImageWidth;
    }

    public int getDepthImageHeight() {
        return depthImageHeight;
    }

    public void setDepthImageHeight(int depthImageHeight) {
        this.depthImageHeight = depthImageHeight;
    }

    public int getDepthFrameRate() {
        return depthFrameRate;
    }

    public void setDepthFrameRate(int frameRate) {
        this.depthFrameRate = frameRate;
    }

}
