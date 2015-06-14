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

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacpp.FlyCapture2.Error;
import static org.bytedeco.javacpp.FlyCapture2.*;

/**
 *
 * @author Jeremy Laviole
 */
public class FlyCapture2FrameGrabber extends FrameGrabber {

    public static String[] getDeviceDescriptions() throws FrameGrabber.Exception {
        tryLoad();

        BusManager busMgr = new BusManager();

        int[] numCameras = new int[1];
        busMgr.GetNumOfCameras(numCameras);

        String[] descriptions = new String[numCameras[0]];

        for (int i = 0; i < numCameras[0]; i++) {
            PGRGuid guid = new PGRGuid();
            Error error = busMgr.GetCameraFromIndex(i, guid);
            if (error.notEquals(PGRERROR_OK)) {
                PrintError(error);
                System.exit(-1);
            }

            Camera cam = new Camera();
            // Connect to a camera
            error = cam.Connect(guid);
            if (error.notEquals(PGRERROR_OK)) {
                PrintError(error);
            }

            // Get the camera information
            CameraInfo camInfo = new CameraInfo();
            error = cam.GetCameraInfo(camInfo);
            if (error.notEquals(PGRERROR_OK)) {
                PrintError(error);
            }
            descriptions[i] = CameraInfo(camInfo);
        }

        return descriptions;
    }

    static void PrintError(Error error) {
        error.PrintErrorTrace();
    }

    static String CameraInfo(CameraInfo pCamInfo) {
        return "\n*** CAMERA INFORMATION ***\n"
                + "Serial number - " + pCamInfo.serialNumber() + "\n"
                + "Camera model - " + pCamInfo.modelName().getString() + "\n"
                + "Camera vendor - " + pCamInfo.vendorName().getString() + "\n"
                + "Sensor - " + pCamInfo.sensorInfo().getString() + "\n"
                + "Resolution - " + pCamInfo.sensorResolution().getString() + "\n"
                + "Firmware version - " + pCamInfo.firmwareVersion().getString() + "\n"
                + "Firmware build time - " + pCamInfo.firmwareBuildTime().getString() + "\n";
    }

    public static FlyCaptureFrameGrabber createDefault(File deviceFile) throws FrameGrabber.Exception {
        return null;
    }

    public static FlyCaptureFrameGrabber createDefault(String devicePath) throws FrameGrabber.Exception {
        return null;
    }

    public static FlyCaptureFrameGrabber createDefault(int deviceNumber) throws FrameGrabber.Exception {
        return new FlyCaptureFrameGrabber(deviceNumber);
    }

    private static FrameGrabber.Exception loadingException = null;

    public static void tryLoad() throws FrameGrabber.Exception {
        if (loadingException != null) {
            loadingException.printStackTrace();
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.javacpp.FlyCapture2.class);
            } catch (Throwable t) {
                throw loadingException = new FrameGrabber.Exception("Failed to load " + FlyCapture2FrameGrabber.class, t);
            }
        }
    }

    public FlyCapture2FrameGrabber(int deviceNumber) throws FrameGrabber.Exception {
        int[] numCameras = new int[1];
        busMgr.GetNumOfCameras(numCameras);

        // Get the camera
        PGRGuid guid = new PGRGuid();
        Error error = busMgr.GetCameraFromIndex(deviceNumber, guid);
        if (error.notEquals(PGRERROR_OK)) {
            PrintError(error);
            System.exit(-1);
        }

        camera = new Camera();

        // Connect to a camera
        error = camera.Connect(guid);
        if (error.notEquals(PGRERROR_OK)) {
            PrintError(error);
        }

        // Get the camera information
        cameraInfo = new CameraInfo();
        error = camera.GetCameraInfo(cameraInfo);
        if (error.notEquals(PGRERROR_OK)) {
            PrintError(error);
        }

    }

    public void release() throws FrameGrabber.Exception {
        if (camera != null) {
            stop();
            camera.Disconnect();
            camera = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public static final int INITIALIZE = 0x000,
            TRIGGER_INQ = 0x530,
            IS_CAMERA_POWER = 0x400,
            CAMERA_POWER = 0x610,
            SOFTWARE_TRIGGER = 0x62C,
            SOFT_ASYNC_TRIGGER = 0x102C,
            IMAGE_DATA_FORMAT = 0x1048;

    private BusManager busMgr = new BusManager();
    private Camera camera;
    private CameraInfo cameraInfo;
    private Image raw_image = new Image();
    private Image conv_image = new Image();
    private IplImage temp_image, return_image = null;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();
    private final int[] regOut = new int[1];
    private final float[] outFloat = new float[1];
    private final float[] gammaOut = new float[1];

    @Override
    public double getGamma() {
        return Float.isNaN(gammaOut[0]) || Float.isInfinite(gammaOut[0]) || gammaOut[0] == 0.0f ? 2.2 : gammaOut[0];
    }

    @Override
    public int getImageWidth() {
        return return_image == null ? super.getImageWidth() : return_image.width();
    }

    @Override
    public int getImageHeight() {
        return return_image == null ? super.getImageHeight() : return_image.height();
    }

    @Override
    public double getFrameRate() {  // TODO: check this. 
//        if (context == null || context.isNull()) {
        return super.getFrameRate();
//        } else {
//            flycaptureGetCameraAbsProperty(context, FRAME_RATE, outFloat);
//            return outFloat[0];
//        }
    }

    @Override
    public void setImageMode(FrameGrabber.ImageMode imageMode) {
        if (imageMode != this.imageMode) {
            temp_image = null;
            return_image = null;
        }
        super.setImageMode(imageMode);
    }
    static final int VIDEOMODE_ANY = -1;

    public void start() throws FrameGrabber.Exception {
        int f = FRAMERATE_30;  // TODO: Default 30 ? 
        if (frameRate <= 0) {
            f = FRAMERATE_30;
        } else if (frameRate <= 1.876) {
            f = FRAMERATE_1_875;
        } else if (frameRate <= 3.76) {
            f = FRAMERATE_3_75;
        } else if (frameRate <= 7.51) {
            f = FRAMERATE_7_5;
        } else if (frameRate <= 15.01) {
            f = FRAMERATE_15;
        } else if (frameRate <= 30.01) {
            f = FRAMERATE_30;
        } else if (frameRate <= 60.01) {
            f = FRAMERATE_60;
        } else if (frameRate <= 120.01) {
            f = FRAMERATE_120;
        } else if (frameRate <= 240.01) {
            f = FRAMERATE_240;
        }

        int c = VIDEOMODE_ANY;
        if (imageMode == FrameGrabber.ImageMode.COLOR || imageMode == FrameGrabber.ImageMode.RAW) {
            if (imageWidth <= 0 || imageHeight <= 0) {
                c = VIDEOMODE_ANY;
            } else if (imageWidth <= 640 && imageHeight <= 480) {
                c = VIDEOMODE_640x480RGB;
            } else if (imageWidth <= 800 && imageHeight <= 600) {
                c = VIDEOMODE_800x600RGB;
            } else if (imageWidth <= 1024 && imageHeight <= 768) {
                c = VIDEOMODE_1024x768RGB;
            } else if (imageWidth <= 1280 && imageHeight <= 960) {
                c = VIDEOMODE_1280x960RGB;
            } else if (imageWidth <= 1600 && imageHeight <= 1200) {
                c = VIDEOMODE_1600x1200RGB;
            }
        } else if (imageMode == FrameGrabber.ImageMode.GRAY) {
            if (imageWidth <= 0 || imageHeight <= 0) {
                c = VIDEOMODE_ANY;
            } else if (imageWidth <= 640 && imageHeight <= 480) {
                c = bpp > 8 ? VIDEOMODE_640x480Y16 : VIDEOMODE_640x480Y8;
            } else if (imageWidth <= 800 && imageHeight <= 600) {
                c = bpp > 8 ? VIDEOMODE_800x600Y16 : VIDEOMODE_800x600Y8;
            } else if (imageWidth <= 1024 && imageHeight <= 768) {
                c = bpp > 8 ? VIDEOMODE_1024x768Y16 : VIDEOMODE_1024x768Y8;
            } else if (imageWidth <= 1280 && imageHeight <= 960) {
                c = bpp > 8 ? VIDEOMODE_1280x960Y16 : VIDEOMODE_1280x960Y8;
            } else if (imageWidth <= 1600 && imageHeight <= 1200) {
                c = bpp > 8 ? VIDEOMODE_1600x1200Y16 : VIDEOMODE_1600x1200Y8;
            }
        }

        Error error = camera.StartCapture();
        if (error.notEquals(PGRERROR_OK)) {
            PrintError(error);
        }

    }

    public void stop() throws FrameGrabber.Exception {
        Error error = camera.StopCapture();
        if (error.notEquals(PGRERROR_OK)) {
            throw new FrameGrabber.Exception("flycapture camera StopCapture() Error " + error);
        }
        temp_image = null;
        return_image = null;
        timestamp = 0;
        frameNumber = 0;
    }

    /**
     * Not tested.
     *
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     */
    public void trigger() throws FrameGrabber.Exception {
        // waitForTriggerReady();
        Error error = camera.FireSoftwareTrigger();
        if (error.notEquals(PGRERROR_OK)) {
            throw new FrameGrabber.Exception("flycaptureSetCameraRegister() Error " + error);
        }

    }

    private int getNumChannels(int pixelFormat) {
        switch (pixelFormat) {
            case PIXEL_FORMAT_BGR:
            case PIXEL_FORMAT_RGB8:
            case PIXEL_FORMAT_RGB16:
            case PIXEL_FORMAT_S_RGB16:
                return 3;

            case PIXEL_FORMAT_MONO8:
            case PIXEL_FORMAT_MONO16:
            case PIXEL_FORMAT_RAW8:
            case PIXEL_FORMAT_RAW16:
            case PIXEL_FORMAT_S_MONO16:
                return 1;

            case PIXEL_FORMAT_BGRU:
                return 4;

            case PIXEL_FORMAT_411YUV8:
            case PIXEL_FORMAT_422YUV8:
            case PIXEL_FORMAT_444YUV8:
            default:
                return -1;
        }
    }

    private int getDepth(int pixelFormat) {
        switch (pixelFormat) {
            case PIXEL_FORMAT_BGR:
            case PIXEL_FORMAT_RGB8:
            case PIXEL_FORMAT_MONO8:
            case PIXEL_FORMAT_RAW8:
            case PIXEL_FORMAT_BGRU:
                return IPL_DEPTH_8U;

            case PIXEL_FORMAT_MONO16:
            case PIXEL_FORMAT_RAW16:
            case PIXEL_FORMAT_RGB16:
                return IPL_DEPTH_16U;

            case PIXEL_FORMAT_S_MONO16:
            case PIXEL_FORMAT_S_RGB16:
                return IPL_DEPTH_16S;

            case PIXEL_FORMAT_411YUV8:
            case PIXEL_FORMAT_422YUV8:
            case PIXEL_FORMAT_444YUV8:
            default:
                return IPL_DEPTH_8U;
        }
    }

    private void setPixelFormat(Image image, int pixelFormat) {
        image.SetDimensions(image.GetRows(),
                image.GetCols(),
                image.GetStride(),
                pixelFormat,
                image.GetBayerTileFormat());
    }

    private void setStride(Image image, int stride) {
        image.SetDimensions(image.GetRows(),
                image.GetCols(),
                stride,
                image.GetPixelFormat(),
                image.GetBayerTileFormat());
    }

    public Frame grab() throws FrameGrabber.Exception {
        Error error = camera.RetrieveBuffer(raw_image);
        if (error.notEquals(PGRERROR_OK)) {
            throw new FrameGrabber.Exception("flycaptureGrabImage2() Error " + error + " (Has start() been called?)");
        }
        int w = raw_image.GetCols();
        int h = raw_image.GetRows();
        int format = raw_image.GetPixelFormat();
        int depth = getDepth(format);
        int stride = raw_image.GetStride();
        int size = h * stride;
        int numChannels = getNumChannels(format);
        error = camera.ReadRegister(IMAGE_DATA_FORMAT, regOut);
        if (error.notEquals(PGRERROR_OK)) {
            throw new FrameGrabber.Exception("flycaptureGetCameraRegister() Error " + error);
        }
        ByteOrder frameEndian = (regOut[0] & 0x1) != 0
                ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        boolean alreadySwapped = false;

// TODO: check bayer 
//        boolean colorbayer = raw_image.bStippled();
        boolean colorbayer = false;

        boolean colorrgb = format == PIXEL_FORMAT_RGB8 || format == PIXEL_FORMAT_RGB16
                || format == PIXEL_FORMAT_BGR || format == PIXEL_FORMAT_BGRU;
        boolean coloryuv = format == PIXEL_FORMAT_411YUV8 || format == PIXEL_FORMAT_422YUV8
                || format == PIXEL_FORMAT_444YUV8;
        BytePointer imageData = raw_image.GetData();

        if ((depth == IPL_DEPTH_8U || frameEndian.equals(ByteOrder.nativeOrder()))
                && (imageMode == FrameGrabber.ImageMode.RAW || (imageMode == FrameGrabber.ImageMode.COLOR && numChannels == 3)
                || (imageMode == FrameGrabber.ImageMode.GRAY && numChannels == 1 && !colorbayer))) {
            if (return_image == null) {
                return_image = IplImage.createHeader(w, h, depth, numChannels);
            }
            return_image.widthStep(stride);
            return_image.imageSize(size);
            return_image.imageData(imageData);
        } else {
            if (return_image == null) {
                return_image = IplImage.create(w, h, depth, imageMode == FrameGrabber.ImageMode.COLOR ? 3 : 1);
            }
            if (temp_image == null) {
                if (imageMode == FrameGrabber.ImageMode.COLOR
                        && (numChannels > 1 || depth > 8) && !coloryuv && !colorbayer) {
                    temp_image = IplImage.create(w, h, depth, numChannels);
                } else if (imageMode == FrameGrabber.ImageMode.GRAY && colorbayer) {
                    temp_image = IplImage.create(w, h, depth, 3);
                } else if (imageMode == FrameGrabber.ImageMode.GRAY && colorrgb) {
                    temp_image = IplImage.createHeader(w, h, depth, 3);
                } else if (imageMode == FrameGrabber.ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer) {
                    temp_image = IplImage.createHeader(w, h, depth, 1);
                } else {
                    temp_image = return_image;
                }
            }

            setStride(conv_image, temp_image.widthStep());
            conv_image.SetData(temp_image.imageData(), temp_image.width() * temp_image.height() * temp_image.depth());

            if (depth == IPL_DEPTH_8U) {

                setPixelFormat(conv_image, imageMode == FrameGrabber.ImageMode.RAW ? PIXEL_FORMAT_RAW8
                        : temp_image.nChannels() == 1 ? PIXEL_FORMAT_MONO8 : PIXEL_FORMAT_BGR);
            } else {
                setPixelFormat(conv_image, imageMode == FrameGrabber.ImageMode.RAW ? PIXEL_FORMAT_RAW16
                        : temp_image.nChannels() == 1 ? PIXEL_FORMAT_MONO16 : PIXEL_FORMAT_RGB16);
            }
            if (depth != IPL_DEPTH_8U && conv_image.GetPixelFormat() == format && conv_image.GetStride() == stride) {
                // we just need a copy to swap bytes..
                ShortBuffer in = raw_image.GetData().asByteBuffer().order(frameEndian).asShortBuffer();
                ShortBuffer out = temp_image.getByteBuffer().order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
                alreadySwapped = true;
            } else if ((imageMode == FrameGrabber.ImageMode.GRAY && colorrgb)
                    || (imageMode == FrameGrabber.ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer)) {
                temp_image.widthStep(stride);
                temp_image.imageSize(size);
                temp_image.imageData(imageData);
            } else if (!colorrgb && (colorbayer || coloryuv || numChannels > 1)) {

                error = raw_image.Convert(conv_image);
//                error = flycaptureConvertImage(context, raw_image, conv_image);
                if (error.notEquals(PGRERROR_OK)) {
                    throw new FrameGrabber.Exception("flycaptureConvertImage() Error " + error);
                }
            }
            if (!alreadySwapped && depth != IPL_DEPTH_8U
                    && !frameEndian.equals(ByteOrder.nativeOrder())) {

                // ack, the camera's endianness doesn't correspond to our machine ...
                // swap bytes of 16-bit images
                ByteBuffer bb = temp_image.getByteBuffer();
                ShortBuffer in = bb.order(frameEndian).asShortBuffer();
                ShortBuffer out = bb.order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
            }
            if (imageMode == FrameGrabber.ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer) {
                cvCvtColor(temp_image, return_image, CV_GRAY2BGR);
            } else if (imageMode == FrameGrabber.ImageMode.GRAY && (colorbayer || colorrgb)) {
                cvCvtColor(temp_image, return_image, CV_BGR2GRAY);
            }
        }

        int bayerFormat = cameraInfo.bayerTileFormat();
        switch (bayerFormat) {
            case BGGR:
                sensorPattern = SENSOR_PATTERN_BGGR;
                break;
            case GBRG:
                sensorPattern = SENSOR_PATTERN_GBRG;
                break;
            case GRBG:
                sensorPattern = SENSOR_PATTERN_GRBG;
                break;
            case RGGB:
                sensorPattern = SENSOR_PATTERN_RGGB;
                break;
            default:
                sensorPattern = -1L;
        }

        TimeStamp timeStamp = raw_image.GetTimeStamp();
        timestamp = timeStamp.seconds() * 1000000L + timeStamp.microSeconds();
        return converter.convert(return_image);
    }
}
