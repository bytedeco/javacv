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

package name.audet.samuel.javacv;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static name.audet.samuel.javacv.jna.PGRFlyCapture.*;
import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

/**
 *
 * @author Samuel Audet
 */
public class FlyCaptureFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();

        IntByReference count = new IntByReference();
        int error = flycaptureBusCameraCount(count);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureBusCameraCount() Error " + error);
        }

        FlyCaptureInfoEx[] info = FlyCaptureInfoEx.createArray(count.getValue());
        error = flycaptureBusEnumerateCamerasEx(info, count);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureBusEnumerateCamerasEx() Error " + error);
        }

        String[] descriptions = new String[count.getValue()];
        for (int i = 0; i < descriptions.length; i++) {
            descriptions[i] = Native.toString(info[i].pszVendorName) + " " +
                              Native.toString(info[i].pszModelName) + " " +
                              info[i].SerialNumber;
        }
        return descriptions;
    }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                String s =  name.audet.samuel.javacv.jna.PGRFlyCapture.libname;
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception(t);
                }
            }
        }
    }

    public FlyCaptureFrameGrabber(int deviceNumber) throws Exception {
        FlyCaptureContext.PointerByReference c = new FlyCaptureContext.PointerByReference();
        int error = flycaptureCreateContext(c);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureCreateContext() Error " + error);
        }
        context = c.getStructure();
        error = flycaptureInitializePlus(context, new NativeLong(deviceNumber),
                new NativeLong(numBuffers), (Pointer)null);
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
    @Override
    protected void finalize() {
        try {
            release();
        } catch (Exception ex) {
            Logger.getLogger(FlyCaptureFrameGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final NativeLong
            INITIALIZE         = new NativeLong(0x000),
            TRIGGER_INQ        = new NativeLong(0x530),
            IS_CAMERA_POWER    = new NativeLong(0x400),
            CAMERA_POWER       = new NativeLong(0x610),
            SOFTWARE_TRIGGER   = new NativeLong(0x62C),
            SOFT_ASYNC_TRIGGER = new NativeLong(0x102C),
            IMAGE_DATA_FORMAT  = new NativeLong(0x1048);

    private FlyCaptureContext context = null;
    private FlyCaptureImage raw_image = new FlyCaptureImage();
    private FlyCaptureImage conv_image = new FlyCaptureImage();
    private IplImage temp_image, return_image = null;

    private NativeLongByReference regVal = new NativeLongByReference();
    private NativeLong setRegVal = new NativeLong();

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
        if (colorMode == ColorMode.BGR || colorMode == ColorMode.RAW) {
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
        } else if (colorMode == ColorMode.GRAY) {
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
        IntByReference iPolarity = new IntByReference();
        IntByReference iSource   = new IntByReference();
        IntByReference iRawValue = new IntByReference();
        IntByReference iMode     = new IntByReference();
        int error = flycaptureGetTrigger(context,
                null, iPolarity, iSource, iRawValue, iMode, null);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGetTrigger() Error " + error);
        }
        error = flycaptureSetTrigger(context,
                triggerMode, iPolarity.getValue(), 7, 0, 0);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetTrigger() Error " + error);
        }
        if (triggerMode) {
            waitForTriggerReady();
        }

        // try to match the endianness to our platform
        error = flycaptureGetCameraRegister(context,
                IMAGE_DATA_FORMAT, regVal);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGetCameraRegister() Error " + error);
        }
        if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
            setRegVal.setValue(regVal.getValue().intValue() | 0x1);
        } else {
            setRegVal.setValue(regVal.getValue().intValue() & ~0x1);
        }
        error = flycaptureSetCameraRegister(context,
                IMAGE_DATA_FORMAT, setRegVal);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetCameraRegister() Error " + error);
        }

        error = flycaptureSetBusSpeed(context,
                FLYCAPTURE_S_FASTEST, FLYCAPTURE_S_FASTEST);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetBusSpeed() Error " + error);
        }
        error = flycaptureStart(context, c, f);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureStart() Error " + error);
        }
        error = flycaptureSetGrabTimeoutEx(context, new NativeLong(timeout));
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureSetGrabTimeoutEx() Error " + error);
        }
    }

    private void waitForTriggerReady() throws Exception {
        // wait for trigger to be ready...
        long time = System.currentTimeMillis();
        do {
            int error = flycaptureGetCameraRegister(context,
                    SOFTWARE_TRIGGER, regVal);
            if (error != FLYCAPTURE_OK) {
                throw new Exception("flycaptureGetCameraRegister() Error " + error);
            }
            if (System.currentTimeMillis() - time > timeout) {
                break;
                //throw new Exception("waitForTriggerReady() Error: Timeout occured.");
            }
        } while((regVal.getValue().intValue() >>> 31) != 0);
    }

    public void stop() throws Exception {
        int error = flycaptureStop(context);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureStop() Error " + error);
        }
        temp_image    = null;
        return_image  = null;
    }

    public void trigger() throws Exception {
        waitForTriggerReady();
        setRegVal.setValue(0x80000000);
        int error = flycaptureSetCameraRegister(context,
                SOFT_ASYNC_TRIGGER, setRegVal);
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
            throw new Exception("flycaptureGrabImage2() Error " + error);
        }

        int w = raw_image.iCols;
        int h = raw_image.iRows;
        int depth = getDepth(raw_image.pixelFormat);
        int stride = raw_image.iRowInc;
        int size = raw_image.iRows*raw_image.iRowInc;
        int numChannels = getNumChannels(raw_image.pixelFormat);
        error = flycaptureGetCameraRegister(context,
                IMAGE_DATA_FORMAT, regVal);
        if (error != FLYCAPTURE_OK) {
            throw new Exception("flycaptureGetCameraRegister() Error " + error);
        }
        ByteOrder frameEndian = (regVal.getValue().intValue() & 0x1) != 0 ?
                ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        boolean alreadySwapped = false;
        boolean colorbayer = raw_image.bStippled;

        if ((depth == IPL_DEPTH_8U || frameEndian.equals(ByteOrder.nativeOrder())) &&
                (colorMode == ColorMode.RAW || (colorMode == ColorMode.BGR && numChannels == 3 &&
                                                raw_image.pixelFormat == FLYCAPTURE_BGR) ||
                (colorMode == ColorMode.GRAY && numChannels == 1 && !colorbayer))) {
            if (return_image == null) {
                return_image = IplImage.createHeader(w, h, depth, numChannels);
            }
            return_image.widthStep = stride;
            return_image.imageSize = size;
            return_image.imageData = raw_image.pData;
        } else {
            if (return_image == null) {
                return_image = IplImage.create(w, h, depth, colorMode == ColorMode.BGR ? 3 : 1);
            }
            if (temp_image == null) {
                if (colorMode == ColorMode.GRAY && colorbayer) {
                    temp_image = IplImage.create(w, h, depth, 3);
                } else {
                    temp_image = return_image;
                }
            }
            conv_image.iRowInc = temp_image.widthStep;
            conv_image.pData = temp_image.imageData;
            if (depth == IPL_DEPTH_8U) {
                conv_image.pixelFormat = colorMode == ColorMode.RAW ? FLYCAPTURE_RAW8 :
                                         temp_image.nChannels == 1  ? FLYCAPTURE_MONO8 : FLYCAPTURE_BGR;
            } else {
                conv_image.pixelFormat = colorMode == ColorMode.RAW ? FLYCAPTURE_RAW16 :
                                         temp_image.nChannels == 1  ? FLYCAPTURE_MONO16 : FLYCAPTURE_RGB16;
            }

            if (raw_image.pixelFormat == conv_image.pixelFormat &&
                    raw_image.iRowInc == conv_image.iRowInc) {
                // we just need a copy to swap bytes..
                ShortBuffer in  = raw_image.getByteBuffer().order(frameEndian).asShortBuffer();
                ShortBuffer out = temp_image.getByteBuffer().order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
                alreadySwapped = true;
            } else {
                error = flycaptureConvertImage(context, raw_image, conv_image);
                if (error != FLYCAPTURE_OK) {
                    throw new Exception("flycaptureConvertImage() Error " + error);
                }
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

        if (colorMode == ColorMode.GRAY && colorbayer) {
            cvCvtColor(temp_image, return_image, CV_BGR2GRAY);
        }

        return_image.setTimestamp(raw_image.timeStamp.ulSeconds.longValue()*1000000 +
                raw_image.timeStamp.ulMicroSeconds.longValue());
        return return_image;
    }

}
