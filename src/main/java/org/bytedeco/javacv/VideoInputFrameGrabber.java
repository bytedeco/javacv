/*
 * Copyright (C) 2011-2013 Samuel Audet
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
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.videoinput.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.videoinput.global.videoInputLib.*;

/**
 *
 * @author Samuel Audet
 */
public class VideoInputFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();

        int count = videoInput.listDevices();
        String[] descriptions = new String[count];
        for (int i = 0; i < descriptions.length; i++) {
            descriptions[i] = videoInput.getDeviceName(i).getString();
        }
        return descriptions;
    }

    public static VideoInputFrameGrabber createDefault(File deviceFile)   throws Exception { throw new Exception(VideoInputFrameGrabber.class + " does not support device files."); }
    public static VideoInputFrameGrabber createDefault(String devicePath) throws Exception { throw new Exception(VideoInputFrameGrabber.class + " does not support device paths."); }
    public static VideoInputFrameGrabber createDefault(int deviceNumber)  throws Exception { return new VideoInputFrameGrabber(deviceNumber); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.videoinput.global.videoInputLib.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + VideoInputFrameGrabber.class, t);
            }
        }
    }

    public VideoInputFrameGrabber(int deviceNumber) {
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
    private videoInput myVideoInput = null;
    private IplImage bgrImage = null, grayImage = null;
    private BytePointer bgrImageData = null;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    @Override public int getImageWidth() {
        return myVideoInput == null ? super.getImageWidth() : myVideoInput.getWidth(deviceNumber);
    }

    @Override public int getImageHeight() {
        return myVideoInput == null ? super.getImageHeight() : myVideoInput.getHeight(deviceNumber);
    }

    public void start() throws Exception {
        start(-1);
    }
    public void start(int connection) throws Exception {
        myVideoInput = new videoInput();
        if (frameRate > 0) {
            myVideoInput.setIdealFramerate(deviceNumber, (int)frameRate);
        }
        if (!myVideoInput.setupDevice(deviceNumber, imageWidth  > 0 ? imageWidth  : 640,
                                                    imageHeight > 0 ? imageHeight : 480, connection)) {
            myVideoInput = null;
            throw new Exception("videoInput.setupDevice() Error: Could not setup device.");
        }
        if (format != null && format.length() > 0) {
            int f = format.equals("VI_NTSC_M")   ? VI_NTSC_M   :
                    format.equals("VI_PAL_B")    ? VI_PAL_B    :
                    format.equals("VI_PAL_D")    ? VI_PAL_D    :
                    format.equals("VI_PAL_G")    ? VI_PAL_G    :
                    format.equals("VI_PAL_H")    ? VI_PAL_H    :
                    format.equals("VI_PAL_I")    ? VI_PAL_I    :
                    format.equals("VI_PAL_M")    ? VI_PAL_M    :
                    format.equals("VI_PAL_N")    ? VI_PAL_N    :
                    format.equals("VI_PAL_NC")   ? VI_PAL_NC   :
                    format.equals("VI_SECAM_B")  ? VI_SECAM_B  :
                    format.equals("VI_SECAM_D")  ? VI_SECAM_D  :
                    format.equals("VI_SECAM_G")  ? VI_SECAM_G  :
                    format.equals("VI_SECAM_H")  ? VI_SECAM_H  :
                    format.equals("VI_SECAM_K")  ? VI_SECAM_K  :
                    format.equals("VI_SECAM_K1") ? VI_SECAM_K1 :
                    format.equals("VI_SECAM_L")  ? VI_SECAM_L  :
                    format.equals("VI_NTSC_M_J") ? VI_NTSC_M_J :
                    format.equals("VI_NTSC_433") ? VI_NTSC_433 : -1;
            if (f >= 0 && !myVideoInput.setFormat(deviceNumber, f)) {
                throw new Exception("videoInput.setFormat() Error: Could not set format " + format + ".");
            }
        }
    }

    public void stop() throws Exception {
        if (myVideoInput != null) {
            myVideoInput.stopDevice(deviceNumber);
            myVideoInput = null;
        }
    }

    public void trigger() throws Exception {
        if (myVideoInput == null) {
            throw new Exception("videoInput is null. (Has start() been called?)");
        }
        int w = myVideoInput.getWidth(deviceNumber), h = myVideoInput.getHeight(deviceNumber);
        if (bgrImage == null || bgrImage.width() != w || bgrImage.height() != h) {
            bgrImage = IplImage.create(w, h, IPL_DEPTH_8U, 3);
            bgrImageData = bgrImage.imageData();
        }

        for (int i = 0; i < numBuffers+1; i++) {
            myVideoInput.getPixels(deviceNumber, bgrImageData, false, true);
        }
    }

    public Frame grab() throws Exception {
        if (myVideoInput == null) {
            throw new Exception("videoInput is null. (Has start() been called?)");
        }
        int w = myVideoInput.getWidth(deviceNumber), h = myVideoInput.getHeight(deviceNumber);
        if (bgrImage == null || bgrImage.width() != w || bgrImage.height() != h) {
            bgrImage = IplImage.create(w, h, IPL_DEPTH_8U, 3);
            bgrImageData = bgrImage.imageData();
        }

        if (!myVideoInput.getPixels(deviceNumber, bgrImageData, false, true)) {
            throw new Exception("videoInput.getPixels() Error: Could not get pixels.");
        }
        timestamp = System.nanoTime()/1000;

        if (imageMode == ImageMode.GRAY) {
            if (grayImage == null) {
                grayImage = IplImage.create(w, h, IPL_DEPTH_8U, 1);
            }
            cvCvtColor(bgrImage, grayImage, CV_BGR2GRAY);
            return converter.convert(grayImage);
        } else {
            return converter.convert(bgrImage);
        }
    }
}
