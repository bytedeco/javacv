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
import java.nio.ByteOrder;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.freenect2;
import org.bytedeco.javacpp.freenect2.CpuPacketPipeline;
import org.bytedeco.javacpp.freenect2.FrameMap;
import org.bytedeco.javacpp.freenect2.Freenect2;
import org.bytedeco.javacpp.freenect2.Freenect2Device;
import org.bytedeco.javacpp.freenect2.PacketPipeline;
import org.bytedeco.javacpp.freenect2.SyncMultiFrameListener;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 *
 * @author Jeremy Laviole
 */
public class OpenKinect2FrameGrabber extends FrameGrabber {

    public static String[] getDeviceDescriptions() throws FrameGrabber.Exception {
        tryLoad();
        String[] desc = new String[freenect2Context.enumerateDevices()];
        for (int i = 0; i < desc.length; i++) {
            desc[i] = freenect2Context.getDeviceSerialNumber(i).getString();
        }
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

    private int IRImageWidth = DEFAULT_DEPTH_WIDTH;
    private int IRImageHeight = DEFAULT_DEPTH_HEIGHT;
    private int IRFrameRate = 60;

    private SyncMultiFrameListener frameListener;

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public static OpenKinect2FrameGrabber createDefault(int deviceNumber) throws FrameGrabber.Exception {
        return new OpenKinect2FrameGrabber(deviceNumber);
    }

    public static OpenKinect2FrameGrabber createDefault(File deviceFile) throws Exception {
        throw new Exception(OpenKinect2FrameGrabber.class + " does not support File devices.");
    }

    public static OpenKinect2FrameGrabber createDefault(String devicePath) throws Exception {
        throw new Exception(OpenKinect2FrameGrabber.class + " does not support path.");
    }

    private static FrameGrabber.Exception loadingException = null;
    private static Freenect2 freenect2Context = null;

    public static void tryLoad() throws FrameGrabber.Exception {
        if (loadingException != null) {
            loadingException.printStackTrace();
            throw loadingException;
        } else {
            try {
                if (freenect2Context != null) {
                    return;
                }
                Loader.load(org.bytedeco.javacpp.freenect2.class);

                // Context is shared accross cameras. 
                freenect2Context = new Freenect2();
            } catch (Throwable t) {
                throw loadingException = new FrameGrabber.Exception("Failed to load " + OpenKinect2FrameGrabber.class, t);
            }
        }
    }

    private boolean colorEnabled = false;
    private boolean depthEnabled = false;
    private boolean IREnabled = false;

    private int deviceNumber = 0;
    private String serial = null;
    private Freenect2Device device = null;

    private int frameTypes = 0;

    public OpenKinect2FrameGrabber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public void enableColorStream() {
        if (!colorEnabled) {
            frameTypes |= freenect2.Frame.Color;
            colorEnabled = true;
        }
    }

    public void enableDepthStream() {
        if (!depthEnabled) {
            frameTypes |= freenect2.Frame.Depth;
            depthEnabled = true;
        }
    }

    public void enableIRStream() {
        if (!IREnabled) {
            frameTypes |= freenect2.Frame.Ir;
            IREnabled = true;
        }
    }

    public void release() throws FrameGrabber.Exception {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    @Override
    public void start() throws FrameGrabber.Exception {
        if (freenect2Context == null) {
            try {
                OpenKinect2FrameGrabber.tryLoad();
            } catch (Exception e) {
                System.out.println("Exception in the TryLoad !" + e);
                e.printStackTrace();
            }
        }
        if (freenect2Context == null) {
            throw new Exception("FATAL error: OpenKinect2 camera: driver could not load.");
        }
        if (freenect2Context.enumerateDevices() == 0) {
            throw new Exception("FATAL error: OpenKinect2: no device connected!");
        }
        device = null;
        PacketPipeline pipeline = null;

        pipeline = new CpuPacketPipeline();
//        pipeline = new libfreenect2::OpenGLPacketPipeline();
//        pipeline = new libfreenect2::OpenCLPacketPipeline(deviceId);
//        pipeline = new libfreenect2::CudaPacketPipeline(deviceId);
        serial = freenect2Context.getDeviceSerialNumber(deviceNumber).getString();
        device = freenect2Context.openDevice(serial, pipeline);

        frameListener = new freenect2.SyncMultiFrameListener(frameTypes);

        if (colorEnabled) {
            device.setColorFrameListener(frameListener);
        }
        if (depthEnabled || IREnabled) {
            device.setIrAndDepthFrameListener(frameListener);
        }
        rawVideoImage = IplImage.createHeader(1920, 1080, IPL_DEPTH_8U, 4);
        device.start();

        System.out.println("OpenKinect2 device started.");
        System.out.println("Serial: " + device.getSerialNumber().getString());
        System.out.println("Firmware: " + device.getFirmwareVersion().getString());
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

//    private Pointer rawVideoImageData;
    private IplImage rawVideoImage = null;
    private IplImage videoImageRGBA = null;
    private boolean hasFirstGoodColorImage = false;

    private BytePointer videoBuffer = null;

    protected void grabVideo() {
        int iplDepth = IPL_DEPTH_8U;
        freenect2.Frame rgb = frames.get(freenect2.Frame.Color);
        int channels = (int) rgb.bytes_per_pixel();
        int deviceWidth = (int) rgb.width();
        int deviceHeight = (int) rgb.height();

        BytePointer rawVideoImageData = rgb.data();
        if (rawVideoImage == null) {
            rawVideoImage = IplImage.createHeader(deviceWidth, deviceHeight, iplDepth, channels);
        }

        cvSetData(rawVideoImage, rawVideoImageData, deviceWidth * channels * iplDepth / 8);

        if (videoImageRGBA == null) {
            videoImageRGBA = rawVideoImage.clone();
        }
        cvCvtColor(rawVideoImage, videoImageRGBA, COLOR_BGRA2RGBA);
    }

    private IplImage rawIRImage = null;

    protected void grabIR() {
        /**
         * 512x424 float. Range is [0.0, 65535.0].
         */

        freenect2.Frame IRImage = frames.get(freenect2.Frame.Ir);

        int channels = 1;
        int iplDepth = IPL_DEPTH_32F;
        int bpp = (int) IRImage.bytes_per_pixel();
        int deviceWidth = (int) IRImage.width();
        int deviceHeight = (int) IRImage.height();

        Pointer rawIRData = IRImage.data();
        if (rawIRImage == null) {
            rawIRImage = IplImage.createHeader(deviceWidth, deviceHeight, iplDepth, channels);
        }

        cvSetData(rawIRImage, rawIRData, deviceWidth * channels * iplDepth / 8);

    }

    private IplImage rawDepthImage = null;

    protected void grabDepth() {

        /**
         * 512x424 float also ?.
         */
        freenect2.Frame depthImage = frames.get(freenect2.Frame.Depth);
        int channels = 1;
        int iplDepth = IPL_DEPTH_32F;
        int bpp = (int) depthImage.bytes_per_pixel();
        int deviceWidth = (int) depthImage.width();
        int deviceHeight = (int) depthImage.height();

        Pointer rawDepthData = depthImage.data();
        if (rawDepthImage == null) {
            rawDepthImage = IplImage.createHeader(deviceWidth, deviceHeight, iplDepth, channels);
        }
        cvSetData(rawDepthImage, rawDepthData, deviceWidth * channels * iplDepth / 8);
    }

    private FrameMap frames = new FrameMap();

    /**
     *
     * @return null grabs all images, get them with grabColor, grabDepth, and
     * grabIR instead.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     */
    public Frame grab() throws Exception {

        if (!frameListener.waitForNewFrame(frames, 10 * 1000)) // 10 seconds
        {
            System.out.println("Openkinect2: timeout!");
            // TODO: throw exception
        }
        frameNumber++;
        if (colorEnabled) {
            grabVideo();
        }
        if (IREnabled) {
            grabIR();
        }
        if (depthEnabled) {
            grabDepth();
        }

        frameListener.release(frames);
        return null;
    }

    public IplImage getVideoImage() {
        return videoImageRGBA;
//            return rawVideoImage;
    }

    public IplImage getIRImage() {
        return rawIRImage;
    }

    public IplImage getDepthImage() {
        return rawDepthImage;
    }

    @Override
    public void trigger() throws Exception {
//        device.wait_for_frames();
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

    public int getIRImageWidth() {
        return IRImageWidth;
    }

    public void setIRImageWidth(int IRImageWidth) {
        this.IRImageWidth = IRImageWidth;
    }

    public int getIRImageHeight() {
        return IRImageHeight;
    }

    public void setIRImageHeight(int IRImageHeight) {
        this.IRImageHeight = IRImageHeight;
    }

    public int getDepthFrameRate() {
        return depthFrameRate;
    }

    public void setDepthFrameRate(int frameRate) {
        this.depthFrameRate = frameRate;

    }
}
