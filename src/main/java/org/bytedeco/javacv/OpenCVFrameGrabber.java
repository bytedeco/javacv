/*
 * Copyright (C) 2009-2019 Samuel Audet
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
import java.util.Map;
import java.util.Map.Entry;
import org.bytedeco.javacpp.Loader;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_videoio.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_videoio.*;

/**
 *
 * @author Samuel Audet
 * @author Lloyd (github.com/lloydmeta)
 */
public class OpenCVFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();
        throw new UnsupportedOperationException("Device enumeration not support by OpenCV.");
    }

    public static OpenCVFrameGrabber createDefault(File deviceFile)   throws Exception { return new OpenCVFrameGrabber(deviceFile); }
    public static OpenCVFrameGrabber createDefault(String devicePath) throws Exception { return new OpenCVFrameGrabber(devicePath); }
    public static OpenCVFrameGrabber createDefault(int deviceNumber)  throws Exception { return new OpenCVFrameGrabber(deviceNumber); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.opencv.global.opencv_highgui.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + OpenCVFrameGrabber.class, t);
            }
        }
    }

    public OpenCVFrameGrabber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
    public OpenCVFrameGrabber(File file) {
        this(file.getAbsolutePath());
    }
    public OpenCVFrameGrabber(File file, int apiPreference) {
      this(file.getAbsolutePath(), apiPreference);
    }
    public OpenCVFrameGrabber(String filename) {
        this.filename = filename;
    }
    public OpenCVFrameGrabber(String filename, int apiPreference) {
      this.filename = filename;
      this.apiPreference = apiPreference;
    }

    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private int deviceNumber = 0;
    private String filename = null;
    private int apiPreference = 0;
    private VideoCapture capture = null;
    private Mat returnMatrix = null;
    private final OpenCVFrameConverter converter = new OpenCVFrameConverter.ToMat();
    private final Mat mat = new Mat();

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    @Override public String getFormat() {
        if (capture == null) {
            return super.getFormat();
        } else {
            int fourcc = (int)capture.get(CAP_PROP_FOURCC);
            return "" + (char)( fourcc        & 0xFF) +
                        (char)((fourcc >>  8) & 0xFF) +
                        (char)((fourcc >> 16) & 0xFF) +
                        (char)((fourcc >> 24) & 0xFF);
        }
    }

    @Override public int getImageWidth() {
        if (returnMatrix != null) {
            return returnMatrix.cols();
        } else {
            return capture == null ? super.getImageWidth() : (int)capture.get(CAP_PROP_FRAME_WIDTH);
        }
    }

    @Override public int getImageHeight() {
        if (returnMatrix != null) {
            return returnMatrix.rows();
        } else {
            return capture == null ? super.getImageHeight() : (int)capture.get(CAP_PROP_FRAME_HEIGHT);
        }
    }

    @Override public int getPixelFormat() {
        return capture == null ? super.getPixelFormat() : (int)capture.get(CAP_PROP_CONVERT_RGB);
    }

    @Override public double getFrameRate() {
        return capture == null ? super.getFrameRate() : (int)capture.get(CAP_PROP_FPS);
    }

    @Override public void setImageMode(ImageMode imageMode) {
        if (imageMode != this.imageMode) {
            returnMatrix = null;
        }
        super.setImageMode(imageMode);
    }

    @Override public int getFrameNumber() {
        return capture == null ? super.getFrameNumber() :
                (int)capture.get(CAP_PROP_POS_FRAMES);
    }
    @Override public void setFrameNumber(int frameNumber) throws Exception {
        if (capture == null) {
            super.setFrameNumber(frameNumber);
        } else {
            if (!capture.set(CAP_PROP_POS_FRAMES, frameNumber)) {
                throw new Exception("set() Error: Could not set CAP_PROP_POS_FRAMES to " + frameNumber + ".");
            }
        }
    }

    @Override public long getTimestamp() {
        return capture == null ? super.getTimestamp() :
                Math.round(capture.get(CAP_PROP_POS_MSEC)*1000);
    }
    @Override public void setTimestamp(long timestamp) throws Exception {
        if (capture == null) {
            super.setTimestamp(timestamp);
        } else {
            if (!capture.set(CAP_PROP_POS_MSEC, timestamp/1000.0)) {
                throw new Exception("set() Error: Could not set CAP_PROP_POS_MSEC to " + timestamp/1000.0 + ".");
            }
        }
    }

    @Override public int getLengthInFrames() {
        return capture == null ? super.getLengthInFrames() :
                (int)capture.get(CAP_PROP_FRAME_COUNT);
    }
    @Override public long getLengthInTime() {
        return Math.round(getLengthInFrames() * 1000000L / getFrameRate());
    }

    public double getOption(int propId) {
        if (capture != null) {
            return capture.get(propId);
        }
        return Double.parseDouble(options.get(Integer.toString(propId)));
    }
    
    /**
     *
     * @param propId Property ID, look at opencv_videoio for possible values
     * @param value
     */
    public void setOption(int propId, double value) {
        options.put(Integer.toString(propId), Double.toString(value));
        if (capture != null) {
            capture.set(propId, value);
        }
    }

    public void start() throws Exception {
        if (filename != null && filename.length() > 0) {
            if (apiPreference > 0) {
                capture = new VideoCapture(filename, apiPreference);
            } else {
                capture = new VideoCapture(filename);
            }
        } else {
            capture = new VideoCapture(deviceNumber);
        }

        if (format != null && format.length() >= 4) {
            format = format.toUpperCase();
            byte cc0 = (byte)format.charAt(0);
            byte cc1 = (byte)format.charAt(1);
            byte cc2 = (byte)format.charAt(2);
            byte cc3 = (byte)format.charAt(3);
            capture.set(CAP_PROP_FOURCC, VideoWriter.fourcc(cc0, cc1, cc2, cc3));
        }

        if (imageWidth > 0) {
            if (!capture.set(CAP_PROP_FRAME_WIDTH, imageWidth)) {
                capture.set(CAP_PROP_FRAME_WIDTH, imageWidth);
            }
        }
        if (imageHeight > 0) {
            if (!capture.set(CAP_PROP_FRAME_HEIGHT, imageHeight)) {
                capture.set(CAP_PROP_FRAME_HEIGHT, imageHeight);
            }
        }
        if (frameRate > 0) {
            capture.set(CAP_PROP_FPS, frameRate);
        }
        if (bpp > 0) {
            capture.set(CAP_PROP_FORMAT, bpp); // ??
        }
        if (imageMode == ImageMode.RAW) {
            capture.set(CAP_PROP_CONVERT_RGB, 0);
        }

        for (Entry<String, String> e : options.entrySet()) {
            capture.set(Integer.parseInt(e.getKey()), Double.parseDouble(e.getValue()));
        }

        Mat mat = new Mat();

        try {
            // Before retrieve() starts returning something else then null
            // QTKit sometimes requires some "warm-up" time for some reason...
            // The first frame on Linux is sometimes null as well,
            // so it's probably a good idea to run this for all platforms... ?
            int count = 0;
            while (count++ < 100 && !capture.read(mat)) {
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            // reset interrupt to be nice
            Thread.currentThread().interrupt();
        }
        if (!capture.read(mat)) {
            throw new Exception("read() Error: Could not read frame in start().");
        }

        if (!triggerMode) {
            if (!capture.grab()) {
                throw new Exception("grab() Error: Could not grab frame. (Has start() been called?)");
            }
        }
    }

    public void stop() throws Exception {
        if (capture != null) {
            capture.release();
            capture = null;
        }
    }

    public void trigger() throws Exception {
        Mat mat = new Mat();
        for (int i = 0; i < numBuffers+1; i++) {
            capture.read(mat);
        }
        if (!capture.grab()) {
            throw new Exception("grab() Error: Could not grab frame. (Has start() been called?)");
        }
    }

    public Frame grab() throws Exception {
        if (!capture.retrieve(mat)) {
            throw new Exception("retrieve() Error: Could not retrieve frame. (Has start() been called?)");
        }
        if (!triggerMode) {
            if (!capture.grab()) {
                throw new Exception("grab() Error: Could not grab frame. (Has start() been called?)");
            }
        }

        if (imageMode == ImageMode.GRAY && mat.channels() > 1) {
            if (returnMatrix == null) {
                returnMatrix = new Mat(mat.rows(), mat.cols(), mat.depth(), 1);
            }

            cvtColor(mat, returnMatrix, CV_BGR2GRAY);
        } else if (imageMode == ImageMode.COLOR && mat.channels() == 1) {
            if (returnMatrix == null) {
                returnMatrix = new Mat(mat.rows(), mat.cols(), mat.depth(), 3);
            }
            cvtColor(mat, returnMatrix, CV_GRAY2BGR);
        } else {
            returnMatrix = mat;
        }
        return converter.convert(returnMatrix);
    }
}
