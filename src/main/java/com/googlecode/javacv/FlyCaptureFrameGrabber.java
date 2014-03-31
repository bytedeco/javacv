/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Loader;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.PGRFlyCapture.*;

/**
 *
 * @author Samuel Audet
 */
public class FlyCaptureFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();

        int[] count = new int[1];
        int error = flycaptureBusCameraCount(count);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureBusCameraCount() Error " + error);
        }
        int c = count[0];
        String[] descriptions = new String[c];

        if (c > 0) {
            FlyCaptureInfoEx info = new FlyCaptureInfoEx(c);
            error = flycaptureBusEnumerateCamerasEx(info, count);
            if (error != FLYCAPTURE_OK) {
                throw new Exception("flycaptureBusEnumerateCamerasEx() Error " + error);
            }

            for (int i = 0; i < descriptions.length; i++) {
                info.position(i);
                descriptions[i] = info.pszVendorName() + " " +
                        info.pszModelName() + " " + info.SerialNumber();
            }
        }

        return descriptions;
    }

    public static FlyCaptureFrameGrabber createDefault(File deviceFile)   throws Exception { return null; }
    public static FlyCaptureFrameGrabber createDefault(String devicePath) throws Exception { return null; }
    public static FlyCaptureFrameGrabber createDefault(int deviceNumber)  throws Exception { return new FlyCaptureFrameGrabber(deviceNumber); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(com.googlecode.javacv.cpp.PGRFlyCapture.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + FlyCaptureFrameGrabber.class, t);
            }
        }
    }

    public FlyCaptureFrameGrabber(int deviceNumber) throws Exception {
        int error = flycaptureCreateContext(context);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureCreateContext() Error " + error);
        }
        error = flycaptureInitializePlus(context, deviceNumber, numBuffers, (BytePointer)null);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureInitialize() Error " + error);
        }
    }
    public void release() throws Exception {
        if (context != null) {
            stop();
            int error = flycaptureDestroyContext(context);
            context = null;
            if (error != FLYCAPTURE_OK) {
                throw new Exception("flycaptureDestroyContext() Error " + error);
            }
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public static final int
            INITIALIZE         = 0x000,
            TRIGGER_INQ        = 0x530,
            IS_CAMERA_POWER    = 0x400,
            CAMERA_POWER       = 0x610,
            SOFTWARE_TRIGGER   = 0x62C,
            SOFT_ASYNC_TRIGGER = 0x102C,
            IMAGE_DATA_FORMAT  = 0x1048;

    private FlyCaptureContext context = new FlyCaptureContext(null);
    private FlyCaptureImage raw_image = new FlyCaptureImage();
    private FlyCaptureImage conv_image = new FlyCaptureImage();
    private IplImage temp_image, return_image = null;
    private final int[] regOut = new int[1];
    private final float[] outFloat = new float[1];
    private final float[] gammaOut = new float[1];

    @Override public double getGamma() {
        return Float.isNaN(gammaOut[0]) || Float.isInfinite(gammaOut[0]) || gammaOut[0] == 0.0f ? 2.2 : gammaOut[0];
    }

    @Override public int getImageWidth() {
        return return_image == null ? super.getImageWidth() : return_image.width();
    }

    @Override public int getImageHeight() {
        return return_image == null ? super.getImageHeight() : return_image.height();
    }

    @Override public double getFrameRate() {
        if (context == null || context.isNull()) {
            return super.getFrameRate();
        } else {
            flycaptureGetCameraAbsProperty(context, FLYCAPTURE_FRAME_RATE, outFloat);
            return outFloat[0];
        }
    }

    @Override public void setImageMode(ImageMode imageMode) {
        if (imageMode != this.imageMode) {
            temp_image = null;
            return_image = null;
        }
        super.setImageMode(imageMode);
    }

    public void start() throws Exception {
        int f = FLYCAPTURE_FRAMERATE_ANY;
        if (frameRate <= 0) {
            f = FLYCAPTURE_FRAMERATE_ANY;
        } else if (frameRate <= 1.876) {
            f = FLYCAPTURE_FRAMERATE_1_875;
        } else if (frameRate <= 3.76) {
            f = FLYCAPTURE_FRAMERATE_3_75;
        } else if (frameRate <= 7.51) {
            f = FLYCAPTURE_FRAMERATE_7_5;
        } else if (frameRate <= 15.01) {
            f = FLYCAPTURE_FRAMERATE_15;
        } else if (frameRate <= 30.01) {
            f = FLYCAPTURE_FRAMERATE_30;
        } else if (frameRate <= 60.01) {
            f = FLYCAPTURE_FRAMERATE_60;
        } else if (frameRate <= 120.01) {
            f = FLYCAPTURE_FRAMERATE_120;
        } else if (frameRate <= 240.01) {
            f = FLYCAPTURE_FRAMERATE_240;
        }

        int c = FLYCAPTURE_VIDEOMODE_ANY;
        if (imageMode == ImageMode.COLOR || imageMode == ImageMode.RAW) {
            if (imageWidth <= 0 || imageHeight <= 0) {
                c = FLYCAPTURE_VIDEOMODE_ANY;
            } else if (imageWidth <= 640 && imageHeight <= 480) {
                c = FLYCAPTURE_VIDEOMODE_640x480RGB;
            } else if (imageWidth <= 800 && imageHeight <= 600) {
                c = FLYCAPTURE_VIDEOMODE_800x600RGB;
            } else if (imageWidth <= 1024 && imageHeight <= 768) {
                c = FLYCAPTURE_VIDEOMODE_1024x768RGB;
            } else if (imageWidth <= 1280 && imageHeight <= 960) {
                c = FLYCAPTURE_VIDEOMODE_1280x960RGB;
            } else if (imageWidth <= 1600 && imageHeight <= 1200) {
                c = FLYCAPTURE_VIDEOMODE_1600x1200RGB;
            }
        } else if (imageMode == ImageMode.GRAY) {
            if (imageWidth <= 0 || imageHeight <= 0) {
                c = FLYCAPTURE_VIDEOMODE_ANY;
            } else if (imageWidth <= 640 && imageHeight <= 480) {
                c = bpp > 8 ? FLYCAPTURE_VIDEOMODE_640x480Y16 : FLYCAPTURE_VIDEOMODE_640x480Y8;
            } else if (imageWidth <= 800 && imageHeight <= 600) {
                c = bpp > 8 ? FLYCAPTURE_VIDEOMODE_800x600Y16 : FLYCAPTURE_VIDEOMODE_800x600Y8;
            } else if (imageWidth <= 1024 && imageHeight <= 768) {
                c = bpp > 8 ? FLYCAPTURE_VIDEOMODE_1024x768Y16 : FLYCAPTURE_VIDEOMODE_1024x768Y8;
            } else if (imageWidth <= 1280 && imageHeight <= 960) {
                c = bpp > 8 ? FLYCAPTURE_VIDEOMODE_1280x960Y16 : FLYCAPTURE_VIDEOMODE_1280x960Y8;
            } else if (imageWidth <= 1600 && imageHeight <= 1200) {
                c = bpp > 8 ? FLYCAPTURE_VIDEOMODE_1600x1200Y16 : FLYCAPTURE_VIDEOMODE_1600x1200Y8;
            }
        }

        // set or reset trigger mode
        int[] iPolarity = new int[1];
        int[] iSource   = new int[1];
        int[] iRawValue = new int[1];
        int[] iMode     = new int[1];
        int error = flycaptureGetTrigger(context, null, iPolarity, iSource, iRawValue, iMode, null);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGetTrigger() Error " + error);
        }
        error = flycaptureSetTrigger(context, triggerMode, iPolarity[0], 7, 14, 0);
        if (error != FLYCAPTURE_OK) {
            // try with trigger mode 0 instead
            error = flycaptureSetTrigger(context, true, iPolarity[0], 7, 0, 0);
        }
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetTrigger() Error " + error);
        }
        if (triggerMode) {
            waitForTriggerReady();
        }

        // try to match the endianness to our platform
        error = flycaptureGetCameraRegister(context, IMAGE_DATA_FORMAT, regOut);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGetCameraRegister() Error " + error);
        }
        int reg;
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
            reg = regOut[0] | 0x1;
        } else {
            reg = regOut[0] & ~0x1;
        }
        error = flycaptureSetCameraRegister(context, IMAGE_DATA_FORMAT, reg);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetCameraRegister() Error " + error);
        }

        error = flycaptureSetBusSpeed(context, FLYCAPTURE_S_FASTEST, FLYCAPTURE_S_FASTEST);
        if (error != FLYCAPTURE_OK) {
            error = flycaptureSetBusSpeed(context,
                    FLYCAPTURE_ANY, FLYCAPTURE_ANY);
            if (error != FLYCAPTURE_OK) {
                throw new Exception("flycaptureSetBusSpeed() Error " + error);
            }
        }

        if (gamma != 0.0) {
            error = flycaptureSetCameraAbsProperty(context, FLYCAPTURE_GAMMA, (float)gamma);
            if (error != FLYCAPTURE_OK) {
                throw new Exception("flycaptureSetCameraAbsProperty() Error " + error + ": Could not set gamma.");
            }
        }
        error = flycaptureGetCameraAbsProperty(context, FLYCAPTURE_GAMMA, gammaOut);
        if (error != FLYCAPTURE_OK) {
            gammaOut[0] = 2.2f;
        }

        error = flycaptureStart(context, c, f);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureStart() Error " + error);
        }
        error = flycaptureSetGrabTimeoutEx(context, timeout);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetGrabTimeoutEx() Error " + error);
        }
    }

    private void waitForTriggerReady() throws Exception {
        // wait for trigger to be ready...
        long time = System.currentTimeMillis();
        do {
            int error = flycaptureGetCameraRegister(context, SOFTWARE_TRIGGER, regOut);
            if (error != FLYCAPTURE_OK) {
                throw new Exception("flycaptureGetCameraRegister() Error " + error);
            }
            if (System.currentTimeMillis() - time > timeout) {
                break;
                //throw new Exception("waitForTriggerReady() Error: Timeout occured.");
            }
        } while((regOut[0] >>> 31) != 0);
    }

    public void stop() throws Exception {
        int error = flycaptureStop(context);
        if (error != FLYCAPTURE_OK && error != FLYCAPTURE_FAILED) {
            throw new Exception("flycaptureStop() Error " + error);
        }
        temp_image    = null;
        return_image  = null;
        timestamp   = 0;
        frameNumber = 0;
    }

    public void trigger() throws Exception {
        waitForTriggerReady();
        int error = flycaptureSetCameraRegister(context, SOFT_ASYNC_TRIGGER, 0x80000000);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetCameraRegister() Error " + error);
        }
    }

    private int getNumChannels(int pixelFormat) {
        switch (pixelFormat) {
            case FLYCAPTURE_BGR:
            case FLYCAPTURE_RGB8:
            case FLYCAPTURE_RGB16:
            case FLYCAPTURE_S_RGB16:
                return 3;

            case FLYCAPTURE_MONO8:
            case FLYCAPTURE_MONO16:
            case FLYCAPTURE_RAW8:
            case FLYCAPTURE_RAW16:
            case FLYCAPTURE_S_MONO16:
                return 1;

            case FLYCAPTURE_BGRU:
                return 4;

            case FLYCAPTURE_411YUV8:
            case FLYCAPTURE_422YUV8:
            case FLYCAPTURE_444YUV8:
            default:
                return -1;
        }
    }
    private int getDepth(int pixelFormat) {
        switch (pixelFormat) {
            case FLYCAPTURE_BGR:
            case FLYCAPTURE_RGB8:
            case FLYCAPTURE_MONO8:
            case FLYCAPTURE_RAW8:
            case FLYCAPTURE_BGRU:
                return IPL_DEPTH_8U;

            case FLYCAPTURE_MONO16:
            case FLYCAPTURE_RAW16:
            case FLYCAPTURE_RGB16:
                return IPL_DEPTH_16U;

            case FLYCAPTURE_S_MONO16:
            case FLYCAPTURE_S_RGB16:
                return IPL_DEPTH_16S;

            case FLYCAPTURE_411YUV8:
            case FLYCAPTURE_422YUV8:
            case FLYCAPTURE_444YUV8:
            default:
                return IPL_DEPTH_8U;
        }
    }

    public IplImage grab() throws Exception {
        int error = flycaptureGrabImage2(context, raw_image);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGrabImage2() Error " + error + " (Has start() been called?)");
        }

        int w = raw_image.iCols();
        int h = raw_image.iRows();
        int format = raw_image.pixelFormat();
        int depth = getDepth(format);
        int stride = raw_image.iRowInc();
        int size = h*stride;
        int numChannels = getNumChannels(format);
        error = flycaptureGetCameraRegister(context, IMAGE_DATA_FORMAT, regOut);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGetCameraRegister() Error " + error);
        }
        ByteOrder frameEndian = (regOut[0] & 0x1) != 0 ?
                ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        boolean alreadySwapped = false;
        boolean colorbayer = raw_image.bStippled();
        boolean colorrgb = format == FLYCAPTURE_RGB8 || format == FLYCAPTURE_RGB16 ||
                           format == FLYCAPTURE_BGR  || format == FLYCAPTURE_BGRU;
        boolean coloryuv = format == FLYCAPTURE_411YUV8 || format == FLYCAPTURE_422YUV8 ||
                           format == FLYCAPTURE_444YUV8;
        BytePointer imageData = raw_image.pData();

        if ((depth == IPL_DEPTH_8U || frameEndian.equals(ByteOrder.nativeOrder())) &&
                (imageMode == ImageMode.RAW || (imageMode == ImageMode.COLOR && numChannels == 3) ||
                (imageMode == ImageMode.GRAY && numChannels == 1 && !colorbayer))) {
            if (return_image == null) {
                return_image = IplImage.createHeader(w, h, depth, numChannels);
            }
            return_image.widthStep(stride);
            return_image.imageSize(size);
            return_image.imageData(imageData);
        } else {
            if (return_image == null) {
                return_image = IplImage.create(w, h, depth, imageMode == ImageMode.COLOR ? 3 : 1);
            }
            if (temp_image == null) {
                if (imageMode == ImageMode.COLOR &&
                        (numChannels > 1 || depth > 8) && !coloryuv && !colorbayer) {
                    temp_image = IplImage.create(w, h, depth, numChannels);
                } else if (imageMode == ImageMode.GRAY && colorbayer) {
                    temp_image = IplImage.create(w, h, depth, 3);
                } else if (imageMode == ImageMode.GRAY && colorrgb) {
                    temp_image = IplImage.createHeader(w, h, depth, 3);
                } else if (imageMode == ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer) {
                    temp_image = IplImage.createHeader(w, h, depth, 1);
                } else {
                    temp_image = return_image;
                }
            }
            conv_image.iRowInc(temp_image.widthStep());
            conv_image.pData(temp_image.imageData());
            if (depth == IPL_DEPTH_8U) {
                conv_image.pixelFormat(imageMode == ImageMode.RAW ? FLYCAPTURE_RAW8 :
                                       temp_image.nChannels() == 1  ? FLYCAPTURE_MONO8 : FLYCAPTURE_BGR);
            } else {
                conv_image.pixelFormat(imageMode == ImageMode.RAW ? FLYCAPTURE_RAW16 :
                                       temp_image.nChannels() == 1  ? FLYCAPTURE_MONO16 : FLYCAPTURE_RGB16);
            }

            if (depth != IPL_DEPTH_8U && conv_image.pixelFormat() == format && conv_image.iRowInc() == stride) {
                // we just need a copy to swap bytes..
                ShortBuffer in  = raw_image.getByteBuffer().order(frameEndian).asShortBuffer();
                ShortBuffer out = temp_image.getByteBuffer().order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
                alreadySwapped = true;
            } else if ((imageMode == ImageMode.GRAY && colorrgb) ||
                    (imageMode == ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer)) {
                temp_image.widthStep(stride);
                temp_image.imageSize(size);
                temp_image.imageData(imageData);
            } else if (!colorrgb && (colorbayer || coloryuv || numChannels > 1)) {
                error = flycaptureConvertImage(context, raw_image, conv_image);
                if (error != FLYCAPTURE_OK) {
                    throw new Exception("flycaptureConvertImage() Error " + error);
                }
            }

            if (!alreadySwapped && depth != IPL_DEPTH_8U &&
                    !frameEndian.equals(ByteOrder.nativeOrder())) {
                // ack, the camera's endianness doesn't correspond to our machine ...
                // swap bytes of 16-bit images
                ByteBuffer  bb  = temp_image.getByteBuffer();
                ShortBuffer in  = bb.order(frameEndian).asShortBuffer();
                ShortBuffer out = bb.order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
            }

            if (imageMode == ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer) {
                cvCvtColor(temp_image, return_image, CV_GRAY2BGR);
            } else if (imageMode == ImageMode.GRAY && (colorbayer || colorrgb)) {
                cvCvtColor(temp_image, return_image, CV_BGR2GRAY);
            }
        }

        error = flycaptureGetColorTileFormat(context, regOut);
        if (error != FLYCAPTURE_OK) {
            sensorPattern = -1L;
        } else switch (regOut[0]) {
            case FLYCAPTURE_STIPPLEDFORMAT_BGGR: sensorPattern = SENSOR_PATTERN_BGGR; break;
            case FLYCAPTURE_STIPPLEDFORMAT_GBRG: sensorPattern = SENSOR_PATTERN_GBRG; break;
            case FLYCAPTURE_STIPPLEDFORMAT_GRBG: sensorPattern = SENSOR_PATTERN_GRBG; break;
            case FLYCAPTURE_STIPPLEDFORMAT_RGGB: sensorPattern = SENSOR_PATTERN_RGGB; break;
            default: sensorPattern = -1L;
        }

        FlyCaptureTimestamp timeStamp = raw_image.timeStamp();
        timestamp = timeStamp.ulSeconds() * 1000000L + timeStamp.ulMicroSeconds();
        return return_image;
    }
}
