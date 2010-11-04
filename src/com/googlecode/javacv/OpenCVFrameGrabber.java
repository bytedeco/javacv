/*
 * Copyright (C) 2009,2010 Samuel Audet
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

import java.io.File;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;
import static com.googlecode.javacv.jna.highgui.*;
import com.googlecode.javacv.jna.highgui;

/**
 *
 * @author Samuel Audet
 */
public class OpenCVFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();
        throw new UnsupportedOperationException("Device enumeration not support by OpenCV.");
    }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                String s = highgui.libname;
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception(t);
                }
            }
        }
    }

    public OpenCVFrameGrabber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
    public OpenCVFrameGrabber(File file) {
        this(file.getAbsolutePath());
    }
    public OpenCVFrameGrabber(String filename) {
        this.filename = filename;
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
    private String filename = null;
    private CvCapture capture = null;
    private IplImage return_image = null;

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    public void start() throws Exception {
        if (filename != null && filename.length() > 0) {
            capture = cvCreateFileCapture(filename);
            if (capture == null) {
                throw new Exception("cvCreateFileCapture() Error: Could not create camera capture.");
            }
        } else {
            capture = cvCreateCameraCapture(deviceNumber);
            if (capture == null) {
                throw new Exception("cvCreateCameraCapture() Error: Could not create camera capture.");
            }
        }
        if (imageWidth > 0) {
            if (cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, imageWidth) == 0) {
                cvSetCaptureProperty(capture, CV_CAP_PROP_MODE, imageWidth); // ??
            }
        }
        if (imageHeight > 0) {
            if (cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, imageHeight) == 0) {
                cvSetCaptureProperty(capture, CV_CAP_PROP_MODE, imageHeight); // ??
            }
        }
        if (frameRate > 0) {
            cvSetCaptureProperty(capture, CV_CAP_PROP_FPS, frameRate);
        }
        if (bpp > 0) {
            cvSetCaptureProperty(capture, CV_CAP_PROP_FORMAT, bpp); // ??
        }
        if (highgui.is10or11) {
            cvSetCaptureProperty(capture, highgui.v10or11.CV_CAP_PROP_CONVERT_RGB,
                    colorMode == ColorMode.BGR ? 1 : 0);
        } else {
            cvSetCaptureProperty(capture, highgui.v20.CV_CAP_PROP_CONVERT_RGB,
                    colorMode == ColorMode.BGR ? 1 : 0);
        }
        if (!triggerMode) {
            trigger();
        }
    }

    public void stop() throws Exception {
        if (capture != null) {
            cvReleaseCapture(capture.pointerByReference());
            capture = null;
        }
    }

    public void trigger() throws Exception {
        for (int i = 0; i < triggerFlushSize; i++) {
            cvQueryFrame(capture);
        }
        int err = cvGrabFrame(capture);
        if (err == 0) {
            throw new Exception("cvGrabFrame() Error: Could not grab frame.");
        }
    }

    public IplImage grab() throws Exception {
        IplImage image = cvRetrieveFrame(capture);
        if (image == null) {
            throw new Exception("cvRetrieveFrame() Error: Could not retrieve frame.");
        }
        if (!triggerMode) {
            trigger();
        }

        if (colorMode == ColorMode.GRAY && image.nChannels > 1) {
            if (return_image == null) {
                return_image = IplImage.create(image.width, image.height, image.depth, 1);
            }
            cvCvtColor(image, return_image, CV_BGR2GRAY);
        } else if (colorMode == ColorMode.BGR && image.nChannels == 1) {
            if (return_image == null) {
                return_image = IplImage.create(image.width, image.height, image.depth, 3);
            }
            cvCvtColor(image, return_image, CV_GRAY2BGR);
        } else {
            return_image = image;
        }

        return_image.setTimestamp(Math.round(cvGetCaptureProperty(capture, CV_CAP_PROP_POS_MSEC)*1000));
        return return_image;
    }

}
