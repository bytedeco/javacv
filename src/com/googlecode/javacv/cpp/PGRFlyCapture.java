/*
 * Copyright (C) 2009,2010,2011 Samuel Audet
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
 *
 *
 * With permission from Point Grey Research, Inc. this file was derived from:
 *   $Id: PGRFlyCapture.h,v 1.1 2009/04/30 17:29:30 soowei Exp $
 *   $Id: PGRFlyCapturePlus.h,v 1.1 2009/04/30 17:29:30 soowei Exp $
 *   $Id: PGRFlyCaptureMessaging.h,v 1.1 2009/04/30 17:29:30 soowei Exp $
 * which are covered by the following copyright notice:
 *
 * Copyright © 2001-2006 Point Grey Research, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Point
 * Grey Research, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Point Grey Research Inc.
 *
 * PGR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. PGR SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 *
 * ****** IMPORTANT: Some functions are commented out to cover the
 * ****** common API from the FlyCapture SDK version 1.7 to 2.1.
 */

package com.googlecode.javacv.cpp;

import java.nio.ByteBuffer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(value="windows", link="PGRFlyCapture", preload="FlyCapture2",
        include={"<windows.h>", "<PGRFlyCapture.h>", "<PGRFlyCapturePlus.h>","<PGRFlyCaptureMessaging.h>"},
        includepath={"C:/Program Files/Point Grey Research/PGR FlyCapture/include/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/include/FC1/"}),
    @Platform(value="windows-x86",
        linkpath   ={"C:/Program Files/Point Grey Research/PGR FlyCapture/lib/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/lib/FC1/" },
        preloadpath={"C:/Program Files/Point Grey Research/PGR FlyCapture/bin/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/bin/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/bin/FC1/"}),
    @Platform(value="windows-x86_64",
        linkpath   ={"C:/Program Files/Point Grey Research/PGR FlyCapture/lib64/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/lib64/FC1/" },
        preloadpath={"C:/Program Files/Point Grey Research/PGR FlyCapture/bin64/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/bin64/",
                     "C:/Program Files/Point Grey Research/FlyCapture2/bin64/FC1/"}) })
public class PGRFlyCapture {
    static { load(); }

    //=============================================================================
    //
    // PGRFlyCapture.h
    //
    //   Defines the API to the PGR FlyCapture library.
    //
    //  We welcome your bug reports, suggestions, and comments:
    //  www.ptgrey.com/support/contact
    //
    //=============================================================================

    //
    // Description:
    //   Context pointer for the PGRFlyCapture library.
    //
    @Opaque public static class FlyCaptureContext extends Pointer {
        static { load(); }
        public FlyCaptureContext() { }
        public FlyCaptureContext(Pointer p) { super(p); }
    }

    //
    // Description:
    //  The error codes returned by the functions in this library.
    //
    //enum FlyCaptureError
    public static final int
            // Function completed successfully.
            FLYCAPTURE_OK = 0,
            // General failure.
            FLYCAPTURE_FAILED = 1,
            // Invalid argument passed.
            FLYCAPTURE_INVALID_ARGUMENT = 2,
            // Invalid context passed.
            FLYCAPTURE_INVALID_CONTEXT = 3,
            // Function not implemented.
            FLYCAPTURE_NOT_IMPLEMENTED = 4,
            // Device already initialized.
            FLYCAPTURE_ALREADY_INITIALIZED = 5,
            // Grabbing has already been started.
            FLYCAPTURE_ALREADY_STARTED = 6,
            // Callback is not registered
            FLYCAPTURE_CALLBACK_NOT_REGISTERED = 7,
            // Callback is already registered
            FLYCAPTURE_CALLBACK_ALREADY_REGISTERED = 8,
            // Problem controlling camera.
            FLYCAPTURE_CAMERACONTROL_PROBLEM = 9,
            // Failed to open file.
            FLYCAPTURE_COULD_NOT_OPEN_FILE = 10,
            // Failed to open a device handle.
            FLYCAPTURE_COULD_NOT_OPEN_DEVICE_HANDLE = 11,
            // Memory allocation error
            FLYCAPTURE_MEMORY_ALLOC_ERROR = 12,
            // flycaptureGrabImage() not called.
            FLYCAPTURE_NO_IMAGE = 13,
            // Device not initialized.
            FLYCAPTURE_NOT_INITIALIZED = 14,
            // flycaptureStart() not called.
            FLYCAPTURE_NOT_STARTED = 15,
            // Request would exceed maximum bandwidth.
            FLYCAPTURE_MAX_BANDWIDTH_EXCEEDED = 16,
            // Attached camera is not a PGR camera.
            FLYCAPTURE_NON_PGR_CAMERA = 17,
            // Invalid video mode or framerate passed or retrieved.
            FLYCAPTURE_INVALID_MODE = 18,
            // Unknown error.
            FLYCAPTURE_ERROR_UNKNOWN = 19,
            // Invalid custom size.
            FLYCAPTURE_INVALID_CUSTOM_SIZE = 20,
            // Operation timed out.
            FLYCAPTURE_TIMEOUT = 21,
            // Too many image buffers are locked by the user.
            FLYCAPTURE_TOO_MANY_LOCKED_BUFFERS = 22,
            // There is a version mismatch between one of the interacting modules:
            // pgrflycapture.dll, pgrflycapturegui.dll, and the camera driver.
            FLYCAPTURE_VERSION_MISMATCH = 23,
            // The camera responded that it is currently busy.
            FLYCAPTURE_DEVICE_BUSY = 24,
            // Function has been deprecated.  Please see documentation.
            FLYCAPTURE_DEPRECATED = 25,
            // Supplied User Buffer is too small.
            FLYCAPTURE_BUFFER_SIZE_TOO_SMALL = 26;

    //
    // Description:
    //  An enumeration of the different camera properties that can be set via the
    //  API.
    //
    // Remarks:
    //  A lot of these properties are included only for completeness and future
    //  expandability, and will have no effect on a PGR camera.
    //
    //enum FlyCaptureProperty
    public static final int
            // The brightness property of the camera.
            FLYCAPTURE_BRIGHTNESS = 0,
            // The auto exposure property of the camera.
            FLYCAPTURE_AUTO_EXPOSURE = 1,
            // The sharpness property of the camera.
            FLYCAPTURE_SHARPNESS = 2,
            // The hardware white balance property of the camera.
            FLYCAPTURE_WHITE_BALANCE = 3,
            // The hue property of the camera.
            FLYCAPTURE_HUE = 4,
            // The saturation property of the camera.
            FLYCAPTURE_SATURATION = 5,
            // The gamma property of the camera.
            FLYCAPTURE_GAMMA = 6,
            // The iris property of the camera.
            FLYCAPTURE_IRIS = 7,
            // The focus property of the camera.
            FLYCAPTURE_FOCUS = 8,
            // The zoom property of the camera.
            FLYCAPTURE_ZOOM = 9,
            // The pan property of the camera.
            FLYCAPTURE_PAN = 10,
            // The tilt property of the camera.
            FLYCAPTURE_TILT = 11,
            // The shutter property of the camera.
            FLYCAPTURE_SHUTTER = 12,
            // The gain  property of the camera.
            FLYCAPTURE_GAIN = 13,
            // The trigger delay property of the camera.
            FLYCAPTURE_TRIGGER_DELAY = 14,
            // The frame rate property of the camera.
            FLYCAPTURE_FRAME_RATE = 15,
            //
            // Software white balance property. Use this to manipulate the
            // values for software whitebalance.  This is only applicable to cameras
            // that do not do onboard color processing.  On these cameras, hardware
            // white balance is disabled.
            //
            FLYCAPTURE_SOFTWARE_WHITEBALANCE = 16,
            // The temperature property of the camera
            FLYCAPTURE_TEMPERATURE = 17;

    //
    // Description:
    //  The type used to store the serial number uniquely identifying a FlyCapture
    //  camera.
    //
    // typedef unsigned long FlyCaptureCameraSerialNumber

    //
    // Description:
    //  An enumeration of the different type of bus events.
    //
    //enum FlyCaptureBusEvent
    public static final int
           // A message returned from the bus callback mechanism indicating a bus reset.
           FLYCAPTURE_MESSAGE_BUS_RESET = 0x02,
           // A message returned from the bus callback mechanism indicating a device has
           // arrived on the bus.
           FLYCAPTURE_MESSAGE_DEVICE_ARRIVAL = 0x03,
           // A message returned from the bus callback mechanism indicating a device has
           // been removed from the bus.
           FLYCAPTURE_MESSAGE_DEVICE_REMOVAL = 0x04;
    //
    // Function prototype for the bus callback mechanism.  pParam contains the
    // parameter passed in when registering the callback.  iMessage is one of the
    // above FLYCAPTURE_MESSAGE_* #defines and ulParam is a message-defined
    // parameter.
    //
    // See also: flycaptureModifyCallback()
    //
    public static class FlyCaptureCallback extends FunctionPointer {
        static { load(); }
        public    FlyCaptureCallback(Pointer p) { super(p); }
        protected FlyCaptureCallback() { allocate(); }
        protected final native void allocate();
        public native void call(Pointer pParam, int iMessage, @Cast("unsigned long") int ulParam);
    }

    //
    // A value indicating an infinite wait.  This macro is used primarily used with
    // the flycaptureSetGrabTimeoutEx() in order to indicate the software should
    // wait indefinitely for the camera to produce an image.
    //
    public static final int FLYCAPTURE_INFINITE = 0xFFFFFFFF;

    //
    // Description:
    //   Enum describing different framerates.
    //
    //enum FlyCaptureFrameRate
    public static final int
            // 1.875 fps. (Frames per second)
            FLYCAPTURE_FRAMERATE_1_875 = 0,
            // 3.75 fps.
            FLYCAPTURE_FRAMERATE_3_75 = 1,
            // 7.5 fps.
            FLYCAPTURE_FRAMERATE_7_5 = 2,
            // 15 fps.
            FLYCAPTURE_FRAMERATE_15 = 3,
            // 30 fps.
            FLYCAPTURE_FRAMERATE_30 = 4,
            // Deprecated.  Please use Custom image.
            FLYCAPTURE_FRAMERATE_UNUSED = 5,
            // 60 fps.
            FLYCAPTURE_FRAMERATE_60 = 6,
            // 120 fps.
            FLYCAPTURE_FRAMERATE_120 = 7,
            // 240 fps.
            FLYCAPTURE_FRAMERATE_240 = 8,
            // Number of possible camera frame rates.
            FLYCAPTURE_NUM_FRAMERATES = 9,
            // Custom frame rate.  Used with custom image size functionality.
            FLYCAPTURE_FRAMERATE_CUSTOM = 10,
            // Hook for "any usable frame rate."
            FLYCAPTURE_FRAMERATE_ANY = 11;

    //
    // Description:
    //   Enum describing different video modes.
    //
    // Remarks:
    //   The explicit numbering is to provide downward compatibility for this enum.
    //
    //enum FlyCaptureVideoMode
    public static final int
            // 160x120 YUV444.
            FLYCAPTURE_VIDEOMODE_160x120YUV444     = 0,
            // 320x240 YUV422.
            FLYCAPTURE_VIDEOMODE_320x240YUV422     = 1,
            // 640x480 YUV411.
            FLYCAPTURE_VIDEOMODE_640x480YUV411     = 2,
            // 640x480 YUV422.
            FLYCAPTURE_VIDEOMODE_640x480YUV422     = 3,
            // 640x480 24-bit RGB.
            FLYCAPTURE_VIDEOMODE_640x480RGB        = 4,
            // 640x480 8-bit greyscale or bayer tiled color image.
            FLYCAPTURE_VIDEOMODE_640x480Y8         = 5,
            // 640x480 16-bit greyscale or bayer tiled color image.
            FLYCAPTURE_VIDEOMODE_640x480Y16        = 6,
            // 800x600 YUV422.
            FLYCAPTURE_VIDEOMODE_800x600YUV422     = 17,
            // 800x600 RGB.
            FLYCAPTURE_VIDEOMODE_800x600RGB        = 18,
            // 800x600 8-bit greyscale or bayer tiled color image.
            FLYCAPTURE_VIDEOMODE_800x600Y8         = 7,
            // 800x600 16-bit greyscale or bayer tiled color image.
            FLYCAPTURE_VIDEOMODE_800x600Y16        = 19,
            // 1024x768 YUV422.
            FLYCAPTURE_VIDEOMODE_1024x768YUV422    = 20,
            // 1024x768 RGB.
            FLYCAPTURE_VIDEOMODE_1024x768RGB       = 21,
            // 1024x768 8-bit greyscale or bayer tiled color image.
            FLYCAPTURE_VIDEOMODE_1024x768Y8        = 8,
            // 1024x768 16-bit greyscale or bayer tiled color image.
            FLYCAPTURE_VIDEOMODE_1024x768Y16       = 9,
            // 1280x960 YUV422.
            FLYCAPTURE_VIDEOMODE_1280x960YUV422    = 22,
            // 1280x960 RGB.
            FLYCAPTURE_VIDEOMODE_1280x960RGB       = 23,
            // 1280x960 8-bit greyscale or bayer titled color image.
            FLYCAPTURE_VIDEOMODE_1280x960Y8        = 10,
            // 1280x960 16-bit greyscale or bayer titled color image.
            FLYCAPTURE_VIDEOMODE_1280x960Y16       = 24,
            // 1600x1200 YUV422.
            FLYCAPTURE_VIDEOMODE_1600x1200YUV422   = 50,
            // 1600x1200 RGB.
            FLYCAPTURE_VIDEOMODE_1600x1200RGB      = 51,
            // 1600x1200 8-bit greyscale or bayer titled color image.
            FLYCAPTURE_VIDEOMODE_1600x1200Y8       = 11,
            // 1600x1200 16-bit greyscale or bayer titled color image.
            FLYCAPTURE_VIDEOMODE_1600x1200Y16      = 52,

            // Custom video mode.  Used with custom image size functionality.
            FLYCAPTURE_VIDEOMODE_CUSTOM            = 15,
            // Hook for "any usable video mode."
            FLYCAPTURE_VIDEOMODE_ANY               = 16,

            // Number of possible video modes.
            FLYCAPTURE_NUM_VIDEOMODES              = 23;

    //
    // Description:
    //  An enumeration used to describe the different camera models that can be
    //  accessed through this SDK.
    //
    //enum FlyCaptureCameraModel
    public static final int
            FLYCAPTURE_FIREFLY = 0,
            FLYCAPTURE_DRAGONFLY = 1,
            FLYCAPTURE_AIM = 2,
            FLYCAPTURE_SCORPION = 3,
            FLYCAPTURE_TYPHOON = 4,
            FLYCAPTURE_FLEA = 5,
            FLYCAPTURE_DRAGONFLY_EXPRESS = 6,
            FLYCAPTURE_FLEA2 = 7,
            FLYCAPTURE_FIREFLY_MV = 8,
            FLYCAPTURE_DRAGONFLY2 = 9,
            FLYCAPTURE_BUMBLEBEE = 10,
            FLYCAPTURE_BUMBLEBEE2 = 11,
            FLYCAPTURE_BUMBLEBEEXB3 = 12,
            FLYCAPTURE_GRASSHOPPER = 13,
            FLYCAPTURE_CHAMELEON = 14,
            FLYCAPTURE_UNKNOWN = -1;

    //
    // Description:
    //  An enumeration used to describe the different camera color configurations.
    //
    //enum FlyCaptureCameraType
    public static final int
            // black and white system.
            FLYCAPTURE_BLACK_AND_WHITE = 0,
            // color system.
            FLYCAPTURE_COLOR = 1;

    //
    // Description:
    //  An enumeration used to describe the bus speed
    //
    //enum FlyCaptureBusSpeed
    public static final int
           // 100Mbits/sec.
           FLYCAPTURE_S100 = 0,
           // 200Mbits/sec.
           FLYCAPTURE_S200 = 1,
           // 400Mbits/sec.
           FLYCAPTURE_S400 = 2,
           // 480Mbits/sec. USB
           FLYCAPTURE_S480 = 3,
           // 800Mbits/sec.
           FLYCAPTURE_S800 = 4,
           // 1600Mbits/sec.
           FLYCAPTURE_S1600 = 5,
           // 3200Mbits/sec.
           FLYCAPTURE_S3200 = 6,
           // The fastest speed available.
           FLYCAPTURE_S_FASTEST = 7,
           // Any speed that is available.
           FLYCAPTURE_ANY = 8,
           FLYCAPTURE_SPEED_UNKNOWN = -1;

    //
    // Description:
    //  This structure stores a variety of different pieces of information
    //  associated with a particular camera.  It is used with the
    //  flycaptureBusEnumerateCamerasEx() method.  This structure has replaced
    //  FlyCaptureInfo.
    //
    public static class FlyCaptureInfoEx extends Pointer {
        static { load(); }
        public FlyCaptureInfoEx() { allocate(); }
        public FlyCaptureInfoEx(int size) { allocateArray(size); }
        public FlyCaptureInfoEx(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureInfoEx position(int position) {
            return (FlyCaptureInfoEx)super.position(position);
        }

        // Camera serial number.
        @Cast("FlyCaptureCameraSerialNumber")
        public native int    SerialNumber();      public native FlyCaptureInfoEx SerialNumber(int SerialNumber);
        // Type of imager (color or b&w).
        @Cast("FlyCaptureCameraType")
        public native int    CameraType();        public native FlyCaptureInfoEx CameraType(int CameraType);
        // Camera model.
        @Cast("FlyCaptureCameraModel")
        public native int    CameraModel();       public native FlyCaptureInfoEx CameraModel(int CameraModel);
        // Camera model string.  Null terminated.
        //char  pszModelName[ 512 ];
        public native String pszModelName();      public native FlyCaptureInfoEx pszModelName(String pszModelName);
        // Vendor name string.  Null terminated.
        // char  pszVendorName[ 512 ];
        public native String pszVendorName();     public native FlyCaptureInfoEx pszVendorName(String pszVendorName);
        // Sensor info string.  Null terminated.
        // char  pszSensorInfo[ 512 ];
        public native String pszSensorInfo();     public native FlyCaptureInfoEx pszSensorInfo(String pszSensorInfo);
        // 1394 DCAM compliance level.  DCAM version is this value / 100. eg, 1.31.
        public native int    iDCAMVer();          public native FlyCaptureInfoEx iDCAMVer(int iDCAMVer);
        // Low-level 1394 node number for this device.
        public native int    iNodeNum();          public native FlyCaptureInfoEx iNodeNum(int iNodeNum);
        // Low-level 1394 bus number for this device.
        public native int    iBusNum();           public native FlyCaptureInfoEx iBusNum(int iBusNum);
        // Camera max bus speed
        @Cast("FlyCaptureBusSpeed")
        public native int    CameraMaxBusSpeed(); public native FlyCaptureInfoEx CameraMaxBusSpeed(int CameraMaxBusSpeed);
//        // Flag indicating that the camera is already initialized
//        public native int    iInitialized();      public native FlyCaptureInfoEx iInitialized(int iInitialized);

        // Reserved for future data.
        @Cast("unsigned long") // ulReserved[ 116 ] / ulReserved[ 115 ]
        public native int ulReserved(int i);  public native FlyCaptureInfoEx ulReserved(int i, int ulReserved);
    }

    //
    // Description:
    //  This structure stores some extra driver info not stored on FlyCaptureInfoEx
    //
//    public static class FlyCaptureDriverInfo extends Pointer {
//        static { load(); }
//        public FlyCaptureDriverInfo() { allocate(); }
//        public FlyCaptureDriverInfo(int size) { allocateArray(size); }
//        public FlyCaptureDriverInfo(Pointer p) { super(p); }
//        private native void allocate();
//        private native void allocateArray(int size);
//
//        @Override public FlyCaptureDriverInfo position(int position) {
//            return (FlyCaptureDriverInfo)super.position(position);
//        }
//
//        // Null-terminated driver name for attached camera.
//        // char pszDriverName[ 512 ];
//        public native String pszDriverName(); public native FlyCaptureDriverInfo pszDriverName(String pszDriverName);
//        //  Null-terminated driver Driver version
//        // char pszVersion [ 512 ];
//        public native String pszVersion();    public native FlyCaptureDriverInfo pszVersion(String pszVersion);
//    }

    //
    // Description:
    //   An enumeration used to describe the different color processing
    //   methods.
    //
    // Remarks:
    //   This is only relevant for cameras that do not do onboard color
    //   processing, such as the Dragonfly.  The FLYCAPTURE_RIGOROUS
    //   method is very slow and will not keep up with high frame rates.
    //
    //enum FlyCaptureColorMethod
    public static final int
            // Disable color processing.
            FLYCAPTURE_DISABLE = 0,
            // Edge sensing de-mosaicing.  This is the most accurate method
            // that can still keep up with the camera's frame rate.
            FLYCAPTURE_EDGE_SENSING = 1,
            // Nearest neighbor de-mosaicing.  This algorithm is significantly
            // faster than edge sensing, at the cost of accuracy.
            // Please note The Nearest Neighbor method has been remapped internally to
            // Nearest Neighbor Fast due to observed artifacts with the original method.
            FLYCAPTURE_NEAREST_NEIGHBOR = 2,
            // Faster, less accurate nearest neighbor de-mosaicing.
            FLYCAPTURE_NEAREST_NEIGHBOR_FAST =3,
            // Rigorous de-mosaicing.  This provides the best quality color
            // reproduction.  This method is so processor intensive that it
            // might not keep up with the camera's frame rate.  Best used for
            // offline processing where accurate color reproduction is required.
            FLYCAPTURE_RIGOROUS = 4,
            // High quality linear interpolation. This algorithm provides similar
            // results to Rigorous, but is up to 30 times faster.
            FLYCAPTURE_HQLINEAR = 5;

    //
    // Description:
    //   An enumeration used to indicate the Bayer tile format of the stippled
    //   images passed into a destippling function.
    //
    // Remarks:
    //   This is only relevant for cameras that do not do onboard color
    //   processing, such as the Dragonfly.  The four letters of the enum
    //   value correspond to the "top left" 2x2 section of the stippled image.
    //   For example, the first line of a BGGR image image will be
    //   BGBGBG..., and the second line will be GRGRGR....
    //
    //enum FlyCaptureStippledFormat
    public static final int
            // Indicates a BGGR image.
            FLYCAPTURE_STIPPLEDFORMAT_BGGR = 0,
            // Indicates a GBRG image.
            FLYCAPTURE_STIPPLEDFORMAT_GBRG = 1,
            // Indicates a GRBG image.
            FLYCAPTURE_STIPPLEDFORMAT_GRBG = 2,
            // Indicates a RGGB image.
            FLYCAPTURE_STIPPLEDFORMAT_RGGB = 3,
            // Indicates the default stipple format for the Dragonfly or Firefly.
            FLYCAPTURE_STIPPLEDFORMAT_DEFAULT = 4;

    //
    // Description:
    //   An enumeration used to indicate the pixel format of an image.  This
    //   enumeration is used as a member of FlyCaptureImage and as a parameter
    //   to FlyCaptureStartCustomImage().
    //
    //enum FlyCapturePixelFormat
    public static final int
            // 8 bit of mono.
            FLYCAPTURE_MONO8     = 0x00000001,
            // YUV 4:1:1.
            FLYCAPTURE_411YUV8   = 0x00000002,
            // YUV 4:2:2.
            FLYCAPTURE_422YUV8   = 0x00000004,
            // YUV 4:4:4.
            FLYCAPTURE_444YUV8   = 0x00000008,
            // R, G and B are the same and equal 8 bits.
            FLYCAPTURE_RGB8      = 0x00000010,
            // 16 bit mono.
            FLYCAPTURE_MONO16    = 0x00000020,
            // RR, G and B are the same and equal 16 bits.
            FLYCAPTURE_RGB16     = 0x00000040,
            // 16 bit signed mono .
            FLYCAPTURE_S_MONO16  = 0x00000080,
            // RR, G and B are the same and equal 16 bits signed
            FLYCAPTURE_S_RGB16   = 0x00000100,
            // 8 bit raw data output from sensor.
            FLYCAPTURE_RAW8      = 0x00000200,
            // 16 bit raw data output from  sensor.
            FLYCAPTURE_RAW16     = 0x00000400,
            // 24 bit BGR
            FLYCAPTURE_BGR       = 0x10000001,
            // 32 bit BGRU
            FLYCAPTURE_BGRU      = 0x10000002;

    //
    // Description:
    //   Enumerates the image file formats that flycaptureSaveImage() can write to.
    //
    //enum FlyCaptureImageFileFormat
    public static final int
            // Single channel (8 or 16 bit) greyscale portable grey map.
            FLYCAPTURE_FILEFORMAT_PGM = 0,
            // 3 channel RGB portable pixel map.
            FLYCAPTURE_FILEFORMAT_PPM = 1,
            // 3 or 4 channel RGB windows bitmap.
            FLYCAPTURE_FILEFORMAT_BMP = 2,
            // JPEG format.
            FLYCAPTURE_FILEFORMAT_JPG = 3,
            // Portable Network Graphics format.  Not implemented.
            FLYCAPTURE_FILEFORMAT_PNG = 4,
            // Raw data output.
            FLYCAPTURE_FILEFORMAT_RAW = 5;


    //
    // Description:
    //  This structure defines the format by which time is represented in the
    //  PGRFlycapture SDK.  The ulSeconds and ulMicroSeconds values represent the
    //  absolute system time when the image was captured.  The ulCycleSeconds
    //  and ulCycleCount are higher-precision values that have either been
    //  propagated up from the 1394 bus or extracted from the image itself.  The
    //  data will be extracted from the image if image timestamping is enabled and
    //  directly (and less accurately) from the 1394 bus otherwise.
    //
    //  The ulCycleSeconds value will wrap around after 128 seconds.  The ulCycleCount
    //  represents the 1/8000 second component. Use these two values when synchronizing
    //  grabs between two computers sharing a common 1394 bus that may not have
    //  precisely synchronized system timers.
    //
    public static class FlyCaptureTimestamp extends Pointer {
        static { load(); }
        public FlyCaptureTimestamp() { allocate(); }
        public FlyCaptureTimestamp(int size) { allocateArray(size); }
        public FlyCaptureTimestamp(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureTimestamp position(int position) {
            return (FlyCaptureTimestamp)super.position(position);
        }

        // The number of seconds since the epoch.
        @Cast("unsigned long") public native int ulSeconds();      public native FlyCaptureTimestamp ulSeconds(int ulSeconds);
        // The microseconds component.
        @Cast("unsigned long") public native int ulMicroSeconds(); public native FlyCaptureTimestamp ulMicroSeconds(int ulMicroSeconds);
        // The cycle time seconds.  0-127.
        @Cast("unsigned long") public native int ulCycleSeconds(); public native FlyCaptureTimestamp ulCycleSeconds(int ulCycleSeconds);
        // The cycle time count.  0-7999. (1/8000ths of a second.)
        @Cast("unsigned long") public native int ulCycleCount();   public native FlyCaptureTimestamp ulCycleCount(int ulCycleCount);
        // The cycle offset.  0-3071 (1/3072ths of a cycle count.)
        @Cast("unsigned long") public native int ulCycleOffset();  public native FlyCaptureTimestamp ulCycleOffset(int ulCycleOffset);
    }

    //
    // Description:
    //  This structure is used to pass image information into and out of the
    //  API.
    //
    // Remarks:
    //  The size of the image buffer is iRowInc * iRows, and depends on the
    //  pixel format.
    //
    public static class FlyCaptureImage extends Pointer {
        static { load(); }
        public FlyCaptureImage() { allocate(); }
        public FlyCaptureImage(int size) { allocateArray(size); }
        public FlyCaptureImage(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureImage position(int position) {
            return (FlyCaptureImage)super.position(position);
        }

        // Rows, in pixels, of the image.
        public native int iRows();                     public native FlyCaptureImage iRows(int iRows);
        // Columns, in pixels, of the image.
        public native int iCols();                     public native FlyCaptureImage iCols(int iCols);
        // Row increment.  The number of bytes per row.
        public native int iRowInc();                   public native FlyCaptureImage iRowInc(int iRowInc);
        // Video mode that this image was captured with.  This member is only
        // populated when the image is returned from a grab call.
        @Cast("FlyCaptureVideoMode")
        public native int videoMode();                 public native FlyCaptureImage videoMode(int videoMode);
        // Timestamp of this image.
        @ByRef
        public native FlyCaptureTimestamp timeStamp(); public native FlyCaptureImage timeStamp(FlyCaptureTimestamp timeStamp);
        // Pointer to the actual image data.
        @Cast("unsigned char*")
        public native BytePointer pData();             public native FlyCaptureImage pData(BytePointer pData);
        //
        // If the returned image is Y8, Y16, RAW8 or RAW16, this flag indicates
        // whether it is a greyscale or stippled (bayer tiled) image.  In all
        // other modes, this flag has no meaning.
        //
        @Cast("bool")
        public native boolean bStippled();             public native FlyCaptureImage bStippled(boolean bStippled);
        // The pixel format of this image.
        @Cast("FlyCapturePixelFormat")
        public native int  pixelFormat();              public native FlyCaptureImage pixelFormat(int pixelFormat);

        // This field is always 1 for single lens cameras.  This field is
        // used to indicate the number of images contained in the structure
        // when dealing with multi-imager systems such as the Bumblebee2
        // or XB3
        public native int iNumImages();                public native FlyCaptureImage iNumImages(int iNumImages);

        // Reserved for future use.
        @Cast("unsigned long") // ulReserved[ 5 ]
        public native int ulReserved(int i);           public native FlyCaptureImage ulReserved(int i, int ulReserved);

        public ByteBuffer getByteBuffer() {
            return pData().capacity(iRowInc()*iRows()).asByteBuffer();
        }
    }


    //=============================================================================
    // 1394 Bus Functions
    //=============================================================================
    // Group = 1394 Bus Functions

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureBusCameraCount()
    //
    // Description:
    //   This function returns the number of 1394 cameras attached to the machine.
    //
    // Arguments:
    //   puiCount - The number of cameras on the bus.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureBusCameraCount(
            @Cast("unsigned int*") int[] puiCount);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureBusEnumerateCamerasEx()
    //
    // Description:
    //   This function enumerates all of the cameras found on the machine.
    //   It fills an array of FlyCaptureInfoEx structures with all of the
    //   pertinent information from the attached cameras. The index of a given
    //   FlyCaptureInfoEx structure in the array parInfo is the device number.
    //
    // Arguments:
    //   arInfo  - An array of FlyCaptureInfoEx structures, at least as
    //             large as the number of cameras on the bus.
    //   puiSize - The size of the array passed in.  The number of cameras
    //             detected is passed back in this argument also.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureBusCameraCount()
    //
    public static native @Cast("FlyCaptureError") int flycaptureBusEnumerateCamerasEx(
            FlyCaptureInfoEx arInfo, @Cast("unsigned int*") int[] puiSize);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureModifyCallback()
    //
    // Description:
    //   This function registers or deregisters a bus callback function.
    //   When the state of the bus changes, the registered callback
    //   function will be called with a FLYCAPTURE_MESSAGE_X parameter indicating
    //   the type of event.  Please see the FlyCap example for more information on
    //   how to use callback functionality.
    //
    // Arguments:
    //   context     - The FlyCapture context to access.
    //   pfnCallback - A pointer to an externally defined callback function.
    //   pParam      - A user-specified parameter to be passed back to the callback
    //                 function.  Can be NULL.
    //   bAdd        - True if the callback is to be added to the list of callbacks,
    //                 false if the callback is to be removed.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureModifyCallback(FlyCaptureContext context,
            FlyCaptureCallback pfnCallback, Pointer pParam, @Cast("bool") boolean bAdd);

    //=============================================================================
    // Construction/Destruction Functions
    //=============================================================================
    // Group = Construction/Destruction Functions

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureCreateContext()
    //
    // Description:
    //   This function creates a FlyCaptureContext and allocates all of the memory
    //   that it requires.  The purpose of the FlyCaptureContext is to act as a
    //   handle to one of the cameras attached to the system. This call must be
    //   made before any other calls involving the context will work.
    //
    // Arguments:
    //   pContext - A pointer to the FlyCaptureContext to be created.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureCreateContext(
            @Cast("FlyCaptureContext*") @ByPtrPtr FlyCaptureContext pContext);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureDestroyContext()
    //
    // Description:
    //   Destroys the given FlyCaptureContext.  In order to prevent memory leaks
    //   from occurring, this function must be called when the user is finished
    //   with the FlyCaptureContext.
    //
    // Arguments:
    //   context - The FlyCaptureContext to be destroyed.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureDestroyContext(FlyCaptureContext context);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureInitialize()
    //
    // Description:
    //   This function initializes one of the cameras on the bus and associates it
    //   with the provided FlyCaptureContext. This call must be made after a
    //   flycaptureCreateContext() command and prior to a flycaptureStart() command
    //   in order for images to be grabbed.  Users can also use the
    //   flycaptureInitializeFromSerialNumber() command to initialize a context
    //   with a specific serial number.
    //
    // Arguments:
    //   context  - The FlyCaptureContext to be associated with the camera being
    //              initialized.
    //   ulDevice - The device index of the FlyCapture camera to be initialized
    //              (as indicated by flycaptureBusEnumerateCamerasEx()).
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  If there is only one device on the bus, its index is generally 0.
    //
    // See Also:
    //   flycaptureInitializeFromSerialNumber(), flycaptureCreateContext(),
    //   flycaptureStart(), flycaptureBusEnumerateCamerasEx()
    //
    public static native @Cast("FlyCaptureError") int flycaptureInitialize(FlyCaptureContext context,
            @Cast("unsigned long") int ulDevice);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureInitializeFromSerialNumber()
    //
    // Description:
    //   Similar to the flycaptureInitialize() command, this function initializes
    //   one of the cameras on the bus and associates it with the given
    //   FlyCaptureContext.  This function differs from its counterpart in that it
    //   takes a serial number rather than a bus index.
    //
    // Arguments:
    //   context      - The FlyCaptureContext to be associated with the camera
    //                  being initialized.
    //   serialNumber - The serial number of the FlyCapture camera system to be
    //                  initialized.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureInitialize(), flycaptureCreateContext(), flycaptureStart()
    //
    public static native @Cast("FlyCaptureError") int flycaptureInitializeFromSerialNumber(FlyCaptureContext context,
            @Cast("FlyCaptureCameraSerialNumber") long serialNumber);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureGetCameraInfo()
    //
    // Description:
    //   Retrieves information about the camera.
    //
    // Arguments:
    //   context - The FlyCaptureContext associated with the camera.
    //   pInfo   - Receives the camera information.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraInfo(FlyCaptureContext context,
            FlyCaptureInfoEx pInfo);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureGetCameraInfo()
    //
    // Description:
    //   Retrieves information about the camera.
    //
    // Arguments:
    //   context - The FlyCaptureContext associated with the camera.
    //   pInfo   - Receives the camera information.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
//    public static native @Cast("FlyCaptureError") int flycaptureGetDriverInfo(FlyCaptureContext context,
//            FlyCaptureDriverInfo pInfo);

    //-----------------------------------------------------------------------------
    // Name: flycaptureGetBusSpeed()
    //
    // Description:
    //   This function gets the current asynchronous and isochronous bus speeds.
    //   Asynchronous data transmission is primarily register reads and writes.
    //   Isochronous data transmission is reserved for image transmission.
    //
    // Arguments:
    //   context        - The FlyCaptureContext associated with the camera to be
    //                    queried.
    //   pAsyncBusSpeed - The current asynchronous bus speed.
    //   pIsochBusSpeed - The current isochronous bus speed.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureSetBusSpeed()
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetBusSpeed(FlyCaptureContext context,
            @Cast("FlyCaptureBusSpeed*") int[] pAsyncBusSpeed,
            @Cast("FlyCaptureBusSpeed*") int[] pIsochBusSpeed);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetBusSpeed()
    //
    // Description:
    //   This function sets the asynchronous and isochronous transmit and receive
    //   bus speeds.
    //
    // Arguments:
    //   context        - The FlyCaptureContext associated with the camera to be queried.
    //   asyncBusSpeed  - The desired asynchronous data communication speed.
    //   isochBusSpeed  - The desired isochronous data communication speed.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   If only one of asyncBusSpeed or isochBusSpeed is required, set the other
    //   parameter to FLYCAPTURE_ANY.
    //
    // See Also:
    //   flycaptureGetBusSpeed()
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetBusSpeed(FlyCaptureContext context,
            @Cast("FlyCaptureBusSpeed") int asyncBusSpeed,
            @Cast("FlyCaptureBusSpeed") int isochBusSpeed);


    //=============================================================================
    // General Functions
    //=============================================================================
    // Group = General Functions

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureGetLibraryVersion()
    //
    // Description:
    //  This function returns the version of the library defined at the top
    //  of this header file (PGRFLYCAPTURE_VERSION), which is in the format
    //  100*(major version)+(minor version).
    //
    // Returns:
    //  An integer indicating the current version of the library.
    //
    public static native int flycaptureGetLibraryVersion();

    //-----------------------------------------------------------------------------
    //
    // Name: FlyCaptureErrorToString()
    //
    // Description:
    //    This function returns a description of the provided FlyCaptureError.
    //
    // Arguments:
    //   error - The FlyCapture error to be parsed.
    //
    // Returns:
    //   A null-terminated character string that describes the FlyCapture error.
    //
    public static native String flycaptureErrorToString(@Cast("FlyCaptureError") int error);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureRegisterToString()
    //
    // Description:
    //    This function returns a description of the provided register number.
    //
    // Arguments:
    //   ulRegister - The register to be translated.
    //
    // Returns:
    //   A null-terminated character string that describes the register.
    //
    public static native String flycaptureRegisterToString(@Cast("unsigned long") int ulRegister);


    //=============================================================================
    // Control Functions
    //=============================================================================
    // Group = Control Functions

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureCheckVideoMode()
    //
    // Description:
    //   This function allows the user to check if a given mode is supported by the
    //   camera.
    //
    // Arguments:
    //   context     - An initialized FlyCaptureContext.
    //   videoMode   - The video mode to check.
    //   frameRate   - The frame rate to check.
    //   pbSupported - A pointer to a bool that will store whether or not the mode
    //                 is supported.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureCheckVideoMode(FlyCaptureContext context,
            @Cast("FlyCaptureVideoMode") int videoMode, @Cast("FlyCaptureFrameRate") int frameRate,
            @Cast("bool*") boolean[] pbSupported);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureGetCurrentVideoMode()
    //
    // Description:
    //   This function allows the user to request the camera's current video mode
    //   and frame rate.
    //
    // Arguments:
    //   context    - An initialized FlyCaptureContext.
    //   pVideoMode - A pointer to a video mode to be filled in.
    //   pFrameRate - A pointer to a frame rate to be filled in.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCurrentVideoMode(FlyCaptureContext context,
            @Cast("FlyCaptureVideoMode*") int[] pVideoMode,
            @Cast("FlyCaptureFrameRate*") int[] pFrameRate);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCurrentCustomImage()
    //
    // Description:
    //   This function allows the user to request the current Format 7 settings
    //   on the camera, provided that the camera is in Format 7.
    //
    // Arguments:
    //   context            - The FlyCaptureContext to start grabbing.
    //   puiMode            - The mode currently active (0-7).
    //   puiImagePosLeft    - Maximum horizontal pixels.
    //   puiImagePosTop     - Maximum vertical pixels.
    //   puiWidth           - Indicates the horizontal "step size" of the custom
    //                        image.
    //   puiHeight          - Indicates the vertical "step size" of the custom
    //                        image.
    //   puiPacketSizeBytes - Packet size in bytes.
    //   pPixelFormat       - Current pixel format.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCurrentCustomImage(FlyCaptureContext context,
            @Cast("unsigned int*") int[] puiMode,        @Cast("unsigned int*") int[] puiImagePosLeft,
            @Cast("unsigned int*") int[] puiImagePosTop, @Cast("unsigned int*") int[] puiWidth,
            @Cast("unsigned int*") int[] puiHeight,      @Cast("unsigned int*") int[] puiPacketSizeBytes,
            float[] pfSpeed, @Cast("FlyCapturePixelFormat*") int[] pPixelFormat);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureGetColorProcessingMethod()
    //
    // Description:
    //   This function allows users to check the current color processing method.
    //
    // Arguments:
    //   context - The FlyCapture context to access.
    //   pMethod - A pointer to a FlyCaptureColorMethod that will store the current
    //             color processing method.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //  flycaptureSetColorProcessingMethod()
    //
    //
    // Remarks:
    //  This function is only applicable when using the SDK and driver with cameras
    //  that do not do on board color processing. See the definition of
    //  FlyCaptureColorMethod for detailed descriptions of the available modes.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetColorProcessingMethod(FlyCaptureContext context,
            @Cast("FlyCaptureColorMethod*") int[] pMethod);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureSetColorProcessingMethod()
    //
    // Description:
    //   This function allows users to select the method used for color processing.
    //
    // Arguments:
    //   context - The FlyCapture context to access.
    //   method  - A variable of type FlyCaptureColorMethod indicating the color
    //             processing method to be used.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureGetColorProcessingMethod()
    //
    // Remarks:
    //  The Nearest Neighbor method has been remapped internally to Nearest
    //  Neighbor Fast due to observed artifacts with the original method.
    //  This function is only applicable when using the SDK and driver with cameras
    //  that do not do on board color processing. See the definition of
    //  FlyCaptureColorMethod for detailed descriptions of the available modes.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetColorProcessingMethod(FlyCaptureContext context,
            @Cast("FlyCaptureColorMethod") int method);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureGetColorTileFormat()
    //
    // Description:
    //   This function allows users to check the current color tile destippling
    //   format.
    //
    // Arguments:
    //   context - The FlyCapture context to access.
    //   pformat - A pointer to a FlyCaptureStippledFormat that will store the current
    //             color tile format.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   The color tile format indicates the format of the stippled image the camera
    //   returns.  This function is only applicable to cameras that do not do
    //   onboard color processing.
    //
    // See Also:
    //   flycaptureSetColorTileFormat()
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetColorTileFormat(FlyCaptureContext context,
            @Cast("FlyCaptureStippledFormat*") int[] pformat);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureSetColorTileFormat()
    //
    // Description:
    //   This function sets the color tile destippling format.
    //
    // Arguments:
    //   context - The FlyCapture context to access.
    //   format  - The FlyCaptureStippledFormat to set.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  The color tile format indicates the format of the stippled image the camera
    //  returns.  This function is only applicable to cameras that do not do
    //  onboard color processing.
    //
    // See Also:
    //   flycaptureGetColorTileFormat()
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetColorTileFormat(FlyCaptureContext context,
            @Cast("FlyCaptureStippledFormat") int format);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureStart()
    //
    // Description:
    //   This function starts the image grabbing process.  It should be called
    //   after flycaptureCreateContext() and flycaptureInitialize().
    //
    // Arguments:
    //   context   - The FlyCaptureContext to start grabbing.
    //   videoMode - The video mode to start the camera in.
    //   frameRate - The frame rate to start the camera at.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   It is during this and other related start calls where driver level image
    //   buffer allocation occurs.
    //
    // See Also:
    //   flycaptureCreateContext(), flycaptureInitialize(),
    //   flycaptureInitializeFromSerialNumber(), flycaptureStartCustomImage(),
    //   flycaptureStop()
    //
    public static native @Cast("FlyCaptureError") int flycaptureStart(FlyCaptureContext context,
            @Cast("FlyCaptureVideoMode") int videoMode,
            @Cast("FlyCaptureFrameRate") int frameRate);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureQueryCustomImage()
    //
    // Description:
    //   This function queries the options available for the advanced Custom Image
    //   or DCAM Format 7 functionality.
    //
    // Arguments:
    //   context                 - The FlyCaptureContext to start grabbing.
    //   uiMode                  - The mode to query (0-7).
    //   pbAvailable             - Indicates the availability of this mode.
    //   puiMaxImagePixelsWidth  - Maximum horizontal pixels.
    //   puiMaxImagePixelsHeight - Maximum vertical pixels.
    //   puiPixelUnitHorz        - Indicates the horizontal "step size" of the custom
    //                             image.
    //   puiPixelUnitVert        - Indicates the vertical "step size" of the custom
    //                             image.
    //   puiPixelFormats         - A bit field indicating the supported pixel formats
    //                             of this mode.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureStartCustomImage()
    //
    public static native @Cast("FlyCaptureError") int flycaptureQueryCustomImage(FlyCaptureContext context,
            int uiMode, @Cast("bool*") boolean[] pbAvailable,     @Cast("unsigned int*") int[] puiMaxImagePixelsWidth,
            @Cast("unsigned int*") int[] puiMaxImagePixelsHeight, @Cast("unsigned int*") int[] puiPixelUnitHorz,
            @Cast("unsigned int*") int[] puiPixelUnitVert,        @Cast("unsigned int*") int[] puiPixelFormats);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureQueryCustomImageEx()
    //
    // Description:
    //   This function queries the options available for the advanced Custom Image
    //   or DCAM Format 7functionality.  This function differs from
    //   flycaptureStartCustomImage() in that it allows the user to retrieve the
    //   offset unit size as well (which may be different than the image unit size).
    //
    // Arguments:
    //   context - The FlyCaptureContext to start grabbing.
    //   uiMode - The mode to query (0-7).
    //   pbAvailable - Indicates the availability of this mode.
    //   puiMaxImagePixelsWidth - Maximum horizonal pixels.
    //   puiMaxImagePixelsHeight - Maximum vertical pixels.
    //   puiPixelUnitHorz - Indicates the horizontal "step size" of the custom
    //                      image.
    //   puiPixelUnitVert - Indicates the vertical "step size" of the custom
    //                      image.
    //   puiOffsetUnitHorz - Indicates the horizontal "step size" of the offset
    //                      in the custom image.
    //   puiOffsetUnitVert - Indicates the vertical "step size" of the offset in
    //                      the custom image.
    //   puiPixelFormats  - A bit field indicating the supported pixel formats of
    //                      this mode.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureStartCustomImage()
    //
    //
    public static native @Cast("FlyCaptureError") int flycaptureQueryCustomImageEx(FlyCaptureContext context,
            int uiMode, @Cast("bool*") boolean[] pbAvailable,     @Cast("unsigned int*") int[] puiMaxImagePixelsWidth,
            @Cast("unsigned int*") int[] puiMaxImagePixelsHeight, @Cast("unsigned int*") int[] puiPixelUnitHorz,
            @Cast("unsigned int*") int[] puiPixelUnitVert,        @Cast("unsigned int*") int[] puiOffsetUnitHorz,
            @Cast("unsigned int*") int[] puiOffsetUnitVert,       @Cast("unsigned int*") int[] puiPixelFormats);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureStartCustomImage()
    //
    // Description:
    //   This function starts the image grabbing process with "custom
    //   image" (DCAM Format 7) functionality, which allows the user to select a
    //   custom image size and/or region of interest.
    //
    // Arguments:
    //   context         - The FlyCaptureContext to start grabbing.
    //   uiMode          - The camera-specific mode.  (0-7).
    //   uiImagePosLeft  - The left position of the (sub)image.
    //   uiImagePosTop   - Top top position of the (sub)image.
    //   uiWidth         - The width of the (sub)image.
    //   uiHeight        - The height of the (sub)image.
    //   fBandwidth      - A number between 1.0 and 100.0 which represents the
    //                     percentage of the camera's maximum bandwidth to use for
    //                     transmission.
    //   format          - The pixel format to be used.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   It is during this and other related start calls where driver level image
    //   buffer allocation occurs.
    //
    // See Also:
    //   flycaptureStartCustomImagePacket(), flycaptureQueryCustomImage()
    //
    public static native @Cast("FlyCaptureError") int flycaptureStartCustomImage(FlyCaptureContext context,
            int uiMode, int uiImagePosLeft, int uiImagePosTop, int uiWidth,
            int uiHeight, float fBandwidth, @Cast("FlyCapturePixelFormat") int format);

    //-----------------------------------------------------------------------------
    //
    // Name:
    //  flycaptureStop()
    //
    // Description:
    //   This function halts all image grabbing for the specified FlyCaptureContext.
    //
    // Arguments:
    //   context - The FlyCaptureContext to stop.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This function invalidates all buffers returned by flycaptureLockNext()
    //   and flycaptureLockLatest().
    //
    public static native @Cast("FlyCaptureError") int flycaptureStop(FlyCaptureContext context);


    //=============================================================================
    // Image Related Functions
    //=============================================================================
    // Group = Image Related Functions

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetGrabTimeoutEx()
    //
    // Description:
    //   This function allows the user to set the timeout value for
    //   flycaptureGrabImage*(), flycaptureLockLatest() and flycaptureLockNext().
    //   This is not normally necessary but can be useful in specific applications.
    //   For example, setting uiTimeout to be 0 will result in non-blocking
    //   grab call.
    //
    // Arguments:
    //   context   - The FlyCaptureContext associated with the camera to be queried.
    //   ulTimeout - The timeout value, in milliseconds.  A value of
    //               FLYCAPTURE_INFINITE indicates an infinite wait.  A value of
    //               zero indicates a nonblocking grab call.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  The default grab timeout value is "infinite."  It is not normally necessary
    //  to set this value.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetGrabTimeoutEx(FlyCaptureContext context,
            @Cast("unsigned long") int ulTimeout);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGrabImage()
    //
    // Description:
    //   This function grabs the newest image from the FlyCapture camera system and
    //   passes the image buffer and information to the user.
    //
    // Arguments:
    //   context       - The FlyCapture context to lock the image in.
    //   ppImageBuffer - Pointer to the returned image buffer pointer.
    //   piRows        - Pointer to the returned rows.
    //   piCols        - Pointer to the returned columns.
    //   piRowInc      - Pointer to the returned row increment (number of bytes per row.)
    //   pVideoMode    - Pointer to the returned video mode.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  This function will block until a new image is available.  You can
    //  optionally set the timeout value for the wait using the
    //  flycaptureSetGrabTimeoutEx() function (by default the wait time is
    //  infinite.) Setting the timeout value should normally not be necessary.
    //
    // See Also:
    //  flycaptureStart(), flycaptureGrabImage2(), flycaptureSetGrabTimeoutEx()
    //
    public static int flycaptureGrabImage(FlyCaptureContext context, BytePointer[] ppImageBuffer,
            int[] piRows, int[] piCols, int[] piRowInc, int[] pVideoMode) {
        return flycaptureGrabImage(context, new PointerPointer(ppImageBuffer),
                piRows, piCols, piRowInc, pVideoMode);
    }
    public static native @Cast("FlyCaptureError") int flycaptureGrabImage(FlyCaptureContext context,
            @Cast("unsigned char**") PointerPointer ppImageBuffer, int[] piRows, int[] piCols,
            int[] piRowInc, @Cast("FlyCaptureVideoMode*") int[] pVideoMode);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGrabImage2()
    //
    // Description:
    //   This function is identical to flycaptureGrabImage() except that it returns
    //   a FlyCaptureImage structure.
    //
    // Arguments:
    //   context - The FlyCapture context to lock the image in.
    //   pimage  - A pointer to a FlyCaptureImage structure that will contain the
    //             image information.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  See remarks for flycaptureGrabImage().
    //
    // See Also:
    //  flycaptureStart(), flycaptureGrabImage(), flycaptureSetGrabTimeoutEx()
    //
    public static native @Cast("FlyCaptureError") int flycaptureGrabImage2(FlyCaptureContext context,
            FlyCaptureImage pimage);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSaveImage()
    //
    // Description:
    //   Writes the specified image buffer to disk.
    //
    // Arguments:
    //   context   - The FlyCapture context to access.
    //   pImage    - The image to save.  This can be populated by the user, by only
    //               filling out the pData, size, and pixel format information, or
    //               can be the structure returned by flycaptureConvertImage().
    //   pszPath   - The name of the file to write to.
    //   format    - The file format to write.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSaveImage(FlyCaptureContext context,
            FlyCaptureImage pImage, String pszPath, @Cast("FlyCaptureImageFileFormat") int format);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetJPEGCompressionQuality()
    //
    // Description:
    //   Sets the JPEG compression quality to the specified value.
    //
    // Arguments:
    //   context   - The FlyCapture context to access.
    //   iQuality  - The JPEG compression quality to use when saving JPEG images
    //               with flycaptureSaveImage()
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetJPEGCompressionQuality(
            FlyCaptureContext context, int iQuality);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureConvertImage()
    //
    // Description:
    //   Convert an arbitrary image format to another format.
    //
    // Arguments:
    //   context      - The FlyCapture context to access.
    //   pimageSrc    - The source image to convert
    //   pimageDest   - The destination image to convert.  The pData member must be
    //                  initialized to an output buffer of sufficient size, and
    //                  the pixelFormat member indicates the desired output format.
    //                  Only BGR and BGRU are currently supported.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This function replaces flycaptureConvertToBGR24(),
    //   flycaptureStippledToBGR24(), and flycaptureStippledToBGRU32().
    //
    public static native @Cast("FlyCaptureError") int flycaptureConvertImage(FlyCaptureContext context,
            FlyCaptureImage pimageSrc, FlyCaptureImage pimageDest);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureInplaceRGB24toBGR24()
    //
    // Description:
    //   Changes the input image buffer from 24-bit RGB to windows-displayable
    //   24-bit BGR.
    //
    // Arguments:
    //   pImageBuffer - Pointer to the image contents.
    //   iImagePixels - Size of the image, in pixels.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureInplaceRGB24toBGR24(
            @Cast("unsigned char*") BytePointer pImageBuffer, int iImagePixels);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureInplaceWhiteBalance()
    //
    // Description:
    //   This function performs an inplace software based white balance on the
    //   provided image.
    //
    // Arguments:
    //   context   - The FlyCapture context.
    //   pData     - The BGR24 image data.
    //   iRows     - Image rows.
    //   iCols     - Image columns.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  The image must be in BGR24 format.  I.e., the output from one of the above
    //  functions.  This function has no effect on cameras that are detected to
    //  have hardware whitebalance.
    //
    public static native @Cast("FlyCaptureError") int flycaptureInplaceWhiteBalance(FlyCaptureContext context,
            @Cast("unsigned char*") BytePointer pData, int iRows, int iCols);


    //=============================================================================
    // Camera Property Functions
    //=============================================================================
    // Group = Camera Property Functions

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraPropertyRange()
    //
    // Description:
    //   Allows the user to examine the default, minimum, maximum, and auto
    //   characteristics for the given property.
    //
    // Arguments:
    //   context        - The FlyCapture context to extract the properties from.
    //   cameraProperty - A FlyCaptureProperty indicating the property to
    //                    examine.
    //   pbPresent      - A pointer to a bool that will contain whether or not
    //                    camera property is present.
    //   plMin          - A pointer to a long that will contain the minimum
    //                    property value.
    //   plMax          - A pointer to a long that will contain the maximum
    //                    property value.
    //   plDefault      - A pointer to a long that will contain the default
    //                    property value.
    //   pbAuto         - A pointer to a bool that will contain whether or not
    //                    the Auto setting is available for this property.
    //   pbManual       - A pointer to a bool that will contain whether or not
    //                    this property may be manually adjusted.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Pass NULL for any pointer argument to ignore that argument.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraPropertyRange(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool*") boolean[] pbPresent,
            @Cast("long*") int[] plMin, @Cast("long*") int[] plMax, @Cast("long*") int[] plDefault,
            @Cast("bool*") boolean[] pbAuto, @Cast("bool*") boolean[] pbManual);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraProperty()
    //
    // Description:
    //   Allows the user to query the current value of the given property.
    //
    // Arguments:
    //   context        - The FlyCapture context to extract the properties from.
    //   cameraProperty - A FlyCaptureProperty indicating the property to
    //                    query.
    //   plValueA       - A pointer to storage space for the "A", or first value
    //                    associated with this property.
    //   plValueB       - A pointer to storage space for the "B", or second value
    //                    associated with this property.
    //   pbAuto         - A pointer to a bool that will store the current Auto
    //                    value of the property.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Pass NULL for any pointer argument to ignore that argument.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraProperty(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("long*") int[] plValueA,
            @Cast("long*") int[] plValueB, @Cast("bool*") boolean[] pbAuto);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraProperty()
    //
    // Description:
    //   Allows the user to set the given property.
    //
    // Arguments:
    //   context        - The FlyCaptureContext to set the properties in.
    //   cameraProperty - A FlyCaptureProperty indicating the property to set.
    //   lValueA        - A long containing the "A", or first new value of the
    //                    property.
    //   lValueB        - A long containing the "B", or second new value of the
    //                    property.
    //   bAuto          - A boolean containing the new 'auto' state of the property.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Calling this function with either of FLYCAPTURE_SOFTWARE_WHITEBALANCE
    //   as the cameraProperty parameter and 'true' for the bAuto parameter will
    //   invoke a single shot auto white balance method.  The assumption is that
    //   flycaptureGrabImage() has been called previously with a white object
    //   centered in the field of view.  This will only work if the camera is a
    //   color camera and in RGB mode.  The Red and Blue whitebalance parameters
    //   only affect cameras that do offboard color calculation such as the
    //   Dragonfly.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraProperty(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("long") int lValueA,
            @Cast("long") int lValueB, @Cast("bool") boolean bAuto);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraPropertyBroadcast()
    //
    // Description:
    //   Allows the user to set the given property for all cameras on the bus.
    //
    // Arguments:
    //   context        - The FlyCaptureContext to set the properties in.
    //   cameraProperty - A FlyCaptureProperty indicating the property to set.
    //   lValueA        - A long containing the "A", or first new value of the
    //                    property.
    //   lValueB        - A long containing the "B", or second new value of the
    //                    property.
    //   bAuto          - A boolean containing the new 'auto' state of the property.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This function will set the given property for all the cameras on the
    //   1394 bus.  If you are using multiple busses (ie, more than one 1394 card)
    //   you must call this function for each bus, on a context representing a
    //   camera on that bus.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraPropertyBroadcast(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("long") int lValueA,
            @Cast("long") int lValueB, boolean bAuto);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraPropertyRangeEx()
    //
    // Description:
    //   Replaces flycaptureGetCameraPropertyRange() and provides better access to
    //   camera features.
    //
    // Arguments:
    //   context        - The FlyCapture context to extract the properties from.
    //   cameraProperty - A FlyCaptureProperty indicating the property to
    //                    examine.
    //   pbPresent      - Indicates the presence of this property on the camera.
    //   pbOnePush      - Indicates the availability of the one push feature.
    //   pbReadOut      - Indicates the ability to read out the value of this property.
    //   pbOnOff        - Indicates the ability to turn this property on and off.
    //   pbAuto         - Indicates the availability of auto mode for this property.
    //   pbManual       - Indicates the ability to manually control this property.
    //   piMin          - The minimum value of the property is returned in this
    //                    argument.
    //   piMax          - The maximum value of the property is returned in this
    //                    argument.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Pass NULL for any pointer argument to ignore that argument.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraPropertyRangeEx(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool*") boolean[] pbPresent,
            @Cast("bool*") boolean[] pbOnePush, @Cast("bool*") boolean[] pbReadOut,
            @Cast("bool*") boolean[] pbOnOff,   @Cast("bool*") boolean[] pbAuto,
            @Cast("bool*") boolean[] pbManual, int[] piMin, int[] piMax);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraPropertyEx()
    //
    // Description:
    //   Replaces flycaptureGetCameraProperty() and provides better access to
    //   camera features.
    //
    // Arguments:
    //   context        - The FlyCapture context to extract the properties from.
    //   cameraProperty - A FlyCaptureProperty indicating the property to
    //                    query.
    //   pbOnePush      - The value of the one push bit.
    //   pbOnOff        - The value of the On/Off bit.
    //   pbAuto         - The value of the Auto bit.
    //   piValueA       - The current value of this property.
    //   piValueB       - The current secondary value of this property. (only
    //                    used for the two whitebalance values.)
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Pass NULL for any pointer argument to ignore that argument.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraPropertyEx(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool*") boolean[] pbOnePush,
            @Cast("bool*") boolean[] pbOnOff, @Cast("bool*") boolean[] pbAuto, int[] piValueA, int[] piValueB);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraPropertyEx()
    //
    // Description:
    //   Replaces flycaptureSetCameraPropertyEx() and provides better access to
    //   camera features.
    //
    // Arguments:
    //   context        - The FlyCaptureContext to set the properties in.
    //   cameraProperty - A FlyCaptureProperty indicating the property to set.
    //   bOnePush       - Set the one push bit.
    //   bOnOff         - Set the on/off bit.
    //   bAuto          - Set the auto bit.
    //   iValueA        - The value to set.
    //   iValueB        - The secondary value to set.  (only used for the two
    //                    whitebalance values.)
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraPropertyEx(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool") boolean bOnePush,
            @Cast("bool") boolean bOnOff, @Cast("bool") boolean bAuto, int iValueA, int iValueB);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraPropertyBroadcastEx()
    //
    // Description:
    //   Replaces flycaptureSetCameraPropertyBroadcast() and provides better access
    //   to camera features.
    //
    // Arguments:
    //   context        - The FlyCaptureContext to set the properties in.
    //   cameraProperty - A FlyCaptureProperty indicating the property to set.
    //   bOnePush       - Set the one push bit.
    //   bOnOff         - Set the on/off bit.
    //   bAuto          - Set the auto bit.
    //   iValueA        - The value to set.
    //   iValueB        - The secondary value to set.  (only used for the two
    //                    whitebalance values.)
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This function will set the given property for all the cameras on the
    //   1394 bus.  If you are using multiple busses (ie, more than one 1394 card)
    //   you must call this function for each bus, on a context representing a
    //   camera on that bus.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraPropertyBroadcastEx(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool") boolean bOnePush,
            @Cast("bool") boolean bOnOff, @Cast("bool") boolean bAuto, int iValueA, int iValueB);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraAbsPropertyRange()
    //
    // Description:
    //   Allows the user to determine the presence and range of the absolute value
    //   registers for the camera
    //
    // Arguments:
    //   context - The Flycapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   pbPresent - Whether or not this register has absolute value support.
    //   pfMin - The minimum value that this register can handle.
    //   pfMax - The maximum value that this register can handle.
    //   ppszUnits - A string indicating the units of the register.
    //   ppszUnitAbbr - An abbreviation of the units
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraAbsPropertyRange(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool*") boolean[] pbPresent, float[] pfMin, float[] pfMax,
            @Cast("const char**") @ByPtrPtr BytePointer ppszUnits,
            @Cast("const char**") @ByPtrPtr BytePointer ppszUnitAbbr);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraAbsProperty()
    //
    // Description:
    //   Allows the user to get the current absolute value for a given parameter
    //   from the camera if it is supported.
    //
    // Arguments:
    //   context - The FlyCapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   pfValue - A pointer to a float that will contain the result.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the operation.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraAbsProperty(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, float[] pfValue);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCameraAbsPropertyEx()
    //
    // Description:
    //   Allows the user to get the current absolute value for a given parameter
    //   from the camera if it is supported.  This function also allows the user
    //   to query the states of the one push, on/off, and auto controls in the
    //   property's standard register.
    //
    // Arguments:
    //   context        - The FlyCapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   pbOnePush      - A valid pointer to a bool that will store the one push state
    //   pbOnOff        - A valid pointer to a bool that will store the on/off state.
    //   pbAuto         - A valid pointer to a bool that will store the auto state
    //   pfValue        - A pointer to a float that will contain the result.
    //
    //
    // Remarks:
    //   The data returned by this function is extracted by a series of two register
    //   reads.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the operation.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraAbsPropertyEx(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool*") boolean[] pbOnePush,
            @Cast("bool*") boolean[] pbOnOff, @Cast("bool*") boolean[] pbAuto, float[] pfValue);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraAbsProperty()
    //
    // Description:
    //   Allows the user to set the absolute value of the given parameter if the
    //   mode is supported.
    //
    // Arguments:
    //   context        - The FlyCapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   fValue         - A float containing the new value of the parameter.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the operation.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraAbsProperty(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, float fValue);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraAbsPropertyEx()
    //
    // Description:
    //   Allows the user to set the absolute value of the given parameter if the
    //   mode is supported.  This function also allows the user to specify the
    //   one push, on/off, and auto settings of the same property.
    //
    // Arguments:
    //   context        - The FlyCapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   bOnePush       - A bool indicating if one push should be enabled.
    //   bOnOff         - A bool indicating if the property should be on or off.
    //   bAuto          - A bool indicating if the property should be automatically
    //                    controlled by the camera.
    //   fValue         - A float containing the new value of the parameter.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the operation.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraAbsPropertyEx(FlyCaptureContext context,
            @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool") boolean bOnePush,
            @Cast("bool") boolean bOnOff, @Cast("bool") boolean bAuto, float fValue);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraAbsPropertyBroadcastEx()
    //
    // Description:
    //   Allows the user to set the absolute value of the given parameter if the
    //   mode is supported.  This function also allows the user to specify the
    //   one push, on/off, and auto settings of the same property.
    //
    // Arguments:
    //   context        - The FlyCapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   bOnePush       - A bool indicating if one push should be enabled.
    //   bOnOff         - A bool indicating if the property should be on or off.
    //   bAuto          - A bool indicating if the property should be automatically
    //                    controlled by the camera.
    //   fValue         - A float containing the new value of the parameter.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the operation.
    //
    // Remarks:
    //   This function will set the given property for all the cameras on the
    //   1394 bus that are associated with the context passed.  If multiple busses
    //   (i.e. more than one 1394 card) exist, a call to this function must be made
    //   for each bus using a context representing a camera on that bus.
    //
    public static native @Cast("FlyCaptureError") int  flycaptureSetCameraAbsPropertyBroadcastEx(
            FlyCaptureContext context, @Cast("FlyCaptureProperty") int cameraProperty, @Cast("bool") boolean bOnePush,
            @Cast("bool") boolean bOnOff, @Cast("bool") boolean bAuto, float fValue);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetCameraAbsPropertyBroadcast()
    //
    // Description:
    //   Allows the user to set the absolute value of the given parameter to all
    //   cameras on the current bus.
    //
    // Arguments:
    //   context - The FlyCapture context to query.
    //   cameraProperty - A FlyCaptureProperty indicating which property to query.
    //   fValue - A float containing the new value of the parameter.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the operation.
    //
    // Remarks:
    //   This function will set the given property for all the cameras on the
    //   1394 bus that are associated with the context passed.  If multiple busses
    //   (i.e. more than one 1394 card) exist, a call to this function must be made
    //   for each bus using a context representing a camera on that bus.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraAbsPropertyBroadcast(
            FlyCaptureContext context, @Cast("FlyCaptureProperty") int cameraProperty, float fValue);

    //-----------------------------------------------------------------------------
    // Name:  flycaptureGetCameraRegister()
    //
    // Description:
    //   This function allows the user to get any of camera's registers.
    //
    // Arguments:
    //   context    - The FlyCaptureContext associated with the camera to be queried.
    //   ulRegister - The 32 bit register location to query.
    //   pulValue   - The 32 bit value currently stored in the register.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   The ulRegister value is actually an offset applied to a base address.
    //   Typically this base addess is 0xFFFFF0F00000 but it is not constant.
    //   Refer to the "Unit Dependent Directory" section of the DCAM spec for more
    //   information.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraRegister(FlyCaptureContext context,
            @Cast("unsigned long") int ulRegister, @Cast("unsigned long*") int[] pulValue);

    //-----------------------------------------------------------------------------
    // Name:  flycaptureSetCameraRegister()
    //
    // Description:
    //   This function allows the user to set any of the camera's registers.
    //
    // Arguments:
    //   context    - The FlyCaptureContext associated with the camera to be queried.
    //   ulRegister - The 32 bit register location to set.
    //   ulValue    - The 32 bit value to store in the register.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   The ulRegister value is actually an offset applied to a base address.
    //   Typically this base addess is 0xFFFFF0F00000 but it is not constant.
    //   Refer to the "Unit Dependent Directory" section of the DCAM spec for more
    //   information.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraRegister(FlyCaptureContext context,
            @Cast("unsigned long") int ulRegister, @Cast("unsigned long") int ulValue);

    //-----------------------------------------------------------------------------
    // Name:  flycaptureSetCameraRegisterBroadcast()
    //
    // Description:
    //   This function allows the user to set any register for all cameras on
    //   the bus.
    //
    // Arguments:
    //   context    - The FlyCaptureContext associated with the camera to be queried.
    //   ulRegister - The 32 bit register location to set.
    //   ulValue    - The 32 bit value to store in the register.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   The ulRegister value is actually an offset applied to a base address.
    //   Typically this base addess is 0xFFFFF0F00000 but it is not constant.
    //   Refer to the "Unit Dependent Directory" section of the DCAM spec for more
    //   information.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraRegisterBroadcast(FlyCaptureContext context,
            @Cast("unsigned long") int ulRegister, @Cast("unsigned long") int ulValue);

    //-----------------------------------------------------------------------------
    // Name:  flycaptureGetMemoryChannel()
    //
    // Description:
    //   This function will query the camera to see what the currently set memory
    //   channel is and/or what the maximum valid channel is.  At least one pointer
    //   must be valid.
    //
    // Arguments:
    //   context - The FlyCaptureContext associated with the camera
    //   puiCurrentChannel - NULL or a valid pointer to an unsigned int to store
    //                       the current channel in.
    //   puiNumChannels - NULL or a valid pointer to an unsigned int to store the
    //                    maximum valid memory channel. Zero indicates no user
    //                    channels.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Refer to your camera's technical reference for the registers affected by
    //   the memory channels. Use flycaptureGetMemoryChannel() to check the current
    //   and/or maximum number of channels available.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetMemoryChannel(FlyCaptureContext context,
            @Cast("unsigned int*") int[] puiCurrentChannel, @Cast("unsigned int*") int[] puiNumChannels);

    //-----------------------------------------------------------------------------
    // Name:  flycaptureSaveToMemoryChannel()
    //
    // Description:
    //   This function will save a group of the current camera registers to the
    //   specified memory channel on the camera.
    //
    // Arguments:
    //   context    - The FlyCaptureContext associated with the camera
    //   ulChannel  - The channel to store the values in
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Refer to your camera's technical reference for the registers affected by
    //   the memory channels. Use flycaptureGetMemoryChannel() to check the current
    //   and/or maximum number of channels available.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSaveToMemoryChannel(FlyCaptureContext context,
            @Cast("unsigned long") int ulChannel);

    //-----------------------------------------------------------------------------
    // Name:  flycaptureRestoreFromMemoryChannel()
    //
    // Description:
    //   This function will restore a group of register settings from the specified
    //   memory channel on the camera.  This will make the specified channel the
    //   current channel.
    //
    // Arguments:
    //   context    - The FlyCaptureContext associated with the camera
    //   ulChannel  - The channel to change to
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Refer to your camera's technical reference for the registers affected by
    //   the memory channels. Use flycaptureGetMemoryChannel() to check the current
    //   and/or maximum number of channels available.
    //
    public static native @Cast("FlyCaptureError") int flycaptureRestoreFromMemoryChannel(FlyCaptureContext context,
            @Cast("unsigned long") int ulChannel);

    //-----------------------------------------------------------------------------
    // Name: flycaptureGetCameraTrigger()
    //
    // Description:
    //   Deprecated.  Please use flycaptureGetTrigger().
    //
    // Returns:
    //   FLYCAPTURE_DEPRECATED.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCameraTrigger(FlyCaptureContext context,
            @Cast("unsigned int*") int[] puiPresence, @Cast("unsigned int*") int[] puiOnOff,
            @Cast("unsigned int*") int[] puiPolarity, @Cast("unsigned int*") int[] puiTriggerMode);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetCameraTrigger()
    //
    // Description:
    //    Deprecated.  Please use flycaptureSetTrigger().
    //
    // Returns:
    //   FLYCAPTURE_DEPRECATED.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraTrigger(FlyCaptureContext context,
            int uiOnOff, int uiPolarity, int uiTriggerMode);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetCameraTriggerBroadcast()
    //
    // Description:
    //   Deprecated.  Please use flycaptureSetTriggerBroadcast().
    //
    // Returns:
    //   FLYCAPTURE_DEPRECATED.
    //
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetCameraTriggerBroadcast(FlyCaptureContext context,
            byte ucOnOff, byte ucPolarity, byte ucTriggerMode);

    //-----------------------------------------------------------------------------
    // Name: flycaptureQueryTrigger()
    //
    // Description:
    //   This function allows the user to query the trigger functionality of the
    //   camera.
    //
    // Arguments:
    //   context         - The context associated with the camera to be queried.
    //   pbPresent       - Whether or not the camera has trigger functionality.
    //   pbReadOut       - Whether or not the user can read values in the trigger
    //                     functionality.
    //   pbOnOff         - Whether or not the functionality can be turned on or
    //                     off.
    //   pbPolarity      - Whether or not the polarity can be changed.
    //   pbValueRead     - Whether or not the raw trigger input can be read.
    //   puiSourceMask   - A bit field indicating which trigger sources are available.
    //   pbSoftwareTrigger  - Whether or not software triggering is available.
    //   puiModeMask     - A bit field indicating which trigger modes are available.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   Polarity and trigger sources are camera dependant.
    //
    public static native @Cast("FlyCaptureError") int  flycaptureQueryTrigger(FlyCaptureContext context,
            @Cast("bool*") boolean[] pbPresent, @Cast("bool*") boolean[] pbReadOut,
            @Cast("bool*") boolean[] pbOnOff,   @Cast("bool*") boolean[] pbPolarity,
            @Cast("bool*") boolean[] pbValueRead,       @Cast("unsigned int*") int[] puiSourceMask,
            @Cast("bool*") boolean[] pbSoftwareTrigger, @Cast("unsigned int*") int[] puiModeMask);

    //-----------------------------------------------------------------------------
    // Name: flycaptureGetTrigger()
    //
    // Description:
    //   This function allows the user to query the state of the camera's trigger
    //   functionality.  This function replaces the deprecated
    //   flycaptureGetCameraTrigger() function.
    //
    // Arguments:
    //   context      - The context associated with the camera to be queried.
    //   pbOnOff      - The On/Off state is returned in this parameter.
    //   piPolarity   - The polarity value is returned in this parameter.
    //   piSource     - The source value is returned in this parameter.
    //   piRawValue   - The raw signal value is returned in this parameter.
    //   piMode       - The trigger mode is returned in this parameter.
    //   piParameter  - The parameter for the trigger function
    //                  is returned in this parameter.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int  flycaptureGetTrigger(FlyCaptureContext context,
            @Cast("bool*") boolean[] pbOnOff, int[] piPolarity, int[] piSource, 
            int[] piRawValue,  int[] piMode,  int[] piParameter);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetTrigger()
    //
    // Description:
    //   This function allows the user to set the state of the camera's
    //   trigger functionality.  THis function replaces the deprecated
    //   flycaptureSetCameraTrigger() function.
    //
    // Arguments:
    //   context      - The context associated with the camera to be queried.
    //   bOnOff       - Turn the trigger on or off.
    //   iPolarity    - The polarity of the trigger. 1 or 0.
    //   iSource      - The new trigger source.  Corresponds to the source mask.
    //   iMode        - The new trigger mode.  Corresponds to the mode mask.
    //   iParameter   - The (optional) parameter to the trigger function, if required.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //  If you have set a grab timeout using flycaptureSetGrabTimeoutEx(), this
    //  timeout will be used in asynchronous trigger mode as well:
    //  flycaptureGrabImage*() will return with the image when you either trigger
    //  the camera, or the timeout value expires.
    //
    public static native @Cast("FlyCaptureError") int  flycaptureSetTrigger(FlyCaptureContext context,
            @Cast("bool") boolean bOnOff, int iPolarity, int iSource, int iMode, int iParameter);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetTriggerBroadcast()
    //
    // Description:
    //   This function duplicates the functionality of flycaptureSetTrigger, except
    //   it broadcasts changes to all cameras on the bus.
    //
    // Arguments:
    //   context      - The context associated with the camera to be queried.
    //   bOnOff       - Turn the trigger on or off.
    //   iPolarity    - The polarity of the trigger. 1 or 0.
    //   iSource      - The new trigger source.  Corresponds to the source mask.
    //   iMode        - The new trigger mode.  Corresponds to the mode mask.
    //   iParameter   - The (optional) parameter to the trigger function, if required.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetTriggerBroadcast(FlyCaptureContext context,
            @Cast("bool") boolean bOnOff, int iPolarity, int iSource, int iMode, int iParameter);

    //-----------------------------------------------------------------------------
    // Name: flycaptureGetStrobe()
    //
    // Description:
    //   This function allows the user to query the state of one of the camera's
    //   strobe sources.  Only for use with cameras which support DCAM v1.31 compliant
    //   strobes.
    //
    // Arguments:
    //   context                - The context associated with the camera to be queried.
    //   iSource                - The strobe source to be queried.
    //   pbOnOff                - The current on/off status is returned in this paramaeter.
    //   pbPolarityActiveLow    - The current polarity of the strobe is returned. 1 or 0.
    //   piDelay                - The current delay is returned in this parameter.
    //   piDuration             - The current duration is returned in this parameter.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetStrobe(FlyCaptureContext context,
            int iSource, @Cast("bool*") boolean[] pbOnOff, @Cast("bool*") boolean[] pbPolarityActiveLow,
            int[] piDelay, int[] piDuration);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetStrobe()
    //
    // Description:
    //   This function allows the user to set the state of one of the camera's
    //   strobe sources.  Only for use with cameras which support DCAM v1.31 compliant
    //   strobes.
    //
    // Arguments:
    //   context                - The context associated with the camera to be queried.
    //   iSource                - The strobe source to be set.
    //   bOnOff                 - Describes whether to turn the strobe on or off.
    //   bPolarityActiveLow     - The polarity of the strobe. 1 or 0.
    //   iDelay                 - The delay of the strobe.
    //   iDuration              - The duration of the strobe.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetStrobe(FlyCaptureContext context,
            int iSource, @Cast("bool") boolean bOnOff, @Cast("bool") boolean bPolarityActiveLow,
            int iDelay, int iDuration);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetStrobeBroadcast()
    //
    // Description:
    //   This function duplicates the functionality of flycaptureSetStrobe() but
    //   broadcasts the settings to all cameras on the bus.  Only for use with
    //   cameras which support DCAM v1.31 compliant strobes.
    //
    // Arguments:
    //   context                - The context associated with the camera to be queried.
    //   iSource                - The strobe source to be set.
    //   bOnOff                 - Describes whether to turn the strobe on or off.
    //   bPolarityActiveLow     - The polarity of the strobe. 1 or 0.
    //   iDelay                 - The delay of the strobe.
    //   iDuration              - The duration of the strobe.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetStrobeBroadcast(FlyCaptureContext context,
            int iSource, @Cast("bool") boolean bOnOff, @Cast("bool") boolean bPolarityActiveLow,
            int iDelay, int iDuration);

    //-----------------------------------------------------------------------------
    // Name: flycaptureQueryStrobe()
    //
    // Description:
    //   This function queries the abilities and available settings for a particular
    //   strobe source.  Only for use with cameras which support DCAM v1.31 compliant
    //   strobes.
    //
    // Arguments:
    //   context      - The context associated with the camera to be queried.
    //   iSource      - The strobe source to be queried.
    //   pbAvailable  - NULL or a parameter which indicates if strobe is supported
    //   pbReadOut    - Describes whether the source allows reading of the current value.
    //   pbOnOff      - Describes whether the source can be turned on or off.
    //   pbPolarity   - Describes whether the source's polarity can be changed.
    //   piMinValue   - This parameter holds the minimum value of the delay and duration.
    //   piMaxValue   - This parameter holds the maximum value of the delay and duration.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureQueryStrobe(FlyCaptureContext context,
            int iSource, @Cast("bool*") boolean[] pbAvailable, @Cast("bool*") boolean[] pbReadOut,
            @Cast("bool*") boolean[] pbOnOff, @Cast("bool*") boolean[] pbPolarity,
            int[] piMinValue, int[] piMaxValue);

    //-----------------------------------------------------------------------------
    // Name: flycaptureQueryLookUpTable()
    //
    // Description:
    //   This function queries the availability and state of the camera's look up
    //   table.
    //
    // Arguments:
    //   context        - The context associated with the camera to be queried.
    //   pbAvailable    - NULL or a parameter which indicates if the LUT is supported
    //   puiNumChannels - NULL or a parameter which indicates the number of
    //                    available channels.  NOTE some cameras will return
    //                    available, but zero channels.  Typically, these cameras
    //                    will have a single channel and not support turning the
    //                    LUT off.
    //   pbOn           - NULL or a parameter which indicates whether the LUT is currently on
    //   puiBitDepth    - NULL or a parameter which indicates the bit depth of the
    //                    LUT (this will be the number of bits in the output values).
    //   puiNumEntries  - NULL or a parameter which indicates the number of entries
    //                    in the table (this will be the number of input values).
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   At least one parameter besides the context must be valid or an error will
    //   be returned.
    //
    public static native @Cast("FlyCaptureError") int flycaptureQueryLookUpTable(FlyCaptureContext context,
            @Cast("bool*") boolean[] pbAvailable, @Cast("unsigned int*") int[] puiNumChannels,
            @Cast("bool*") boolean[] pbOn,        @Cast("unsigned int*") int[] puiBitDepth,
            @Cast("unsigned int*") int[] puiNumEntries);

    //-----------------------------------------------------------------------------
    // Name: flycaptureEnableLookUpTable()
    //
    // Description:
    //   This function turns the look up table on or off.
    //
    // Arguments:
    //   context - The context associated with the camera.
    //   bOn - true to enable, false to disable.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   The look up table on some cameras can not be turned off.  These cameras
    //   return FLYCAPTURE_NOT_IMPLEMENTED if bOn is false.  See the description of
    //   flycaptureQueryLookUpTable() for help on identifying these cameras.
    //
    public static native @Cast("FlyCaptureError") int flycaptureEnableLookUpTable(FlyCaptureContext context,
            @Cast("bool") boolean bOn);

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetLookUpTableChannel()
    //
    // Description:
    //   This function will set the specified look up table on the camera.
    //
    // Arguments:
    //   context - The context associated with the camera.
    //   uiChannel - The channel to set
    //   puiArray - a valid array of "numberOfEntries" unsigned ints with values
    //              less than 2^( "bitDepth" )
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetLookUpTableChannel(FlyCaptureContext context,
            int uiChannel, @Cast("unsigned int*") int[] puiArray);

    //-----------------------------------------------------------------------------
    // Name: flycaptureGetLookUpTableChannel()
    //
    // Description:
    //   This function will retrieve the specified look up table on the camera.
    //
    // Arguments:
    //   context   - The context associated with the camera.
    //   uiChannel - The channel to retrieve
    //   puiArray  - a valid array of "numberOfEntries" unsigned ints
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetLookUpTableChannel(FlyCaptureContext context,
            int uiChannel, @Cast("unsigned int*") int[] puiArray);



    //=============================================================================
    //
    // PGRFlyCapturePlus.h
    //
    //   Defines advanced functionality of the FlyCapture SDK.  Please note that
    //   these functions are for advanced users only and that regular users need
    //   not care about this file.
    //
    //  We welcome your bug reports, suggestions, and comments:
    //  www.ptgrey.com/support/contact
    //
    //=============================================================================

    //
    // Description:
    //   A wrapper for FlyCaptureImage that provides access to advanced
    //   functionality.
    //
    public static class FlyCaptureImagePlus extends Pointer {
        static { load(); }
        public FlyCaptureImagePlus() { allocate(); }
        public FlyCaptureImagePlus(int size) { allocateArray(size); }
        public FlyCaptureImagePlus(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureImagePlus position(int position) {
            return (FlyCaptureImagePlus)super.position(position);
        }

        //
        // The FlyCaptureImage that this FlyCaptureImagePlus structure is wrapping.
        // Please see documentation in pgrflycapture.h.
        //
        @ByRef
        public native FlyCaptureImage image();           public native FlyCaptureImagePlus image(FlyCaptureImage image);

        //
        // The sequence number of the image.  This number is generated in the
        // driver and sequential images should have a difference of one.  If
        // the difference is greater than one, it indicates the number of missed
        // images since the last lock image call.
        //
        public native int             uiSeqNum();        public native FlyCaptureImagePlus uiSeqNum(int uiSeqNum);

        //
        // The internal buffer index that the image buffer contained in the
        // FlyCaptureImage corresponds to.  For functions that lock the image,
        // this number must be passed back to the "unlock" function.  If
        // flycaptureInitializePlus() was called, this number corresponds to the
        // position of the buffer in the buffer array passed in.
        //
        public native int             uiBufferIndex();   public native FlyCaptureImagePlus uiBufferIndex(int uiBufferIndex);

        //
        // The sequence number of the image.  This number is generated in the
        // driver and sequential images should have a difference of one.  If
        // the difference is greater than one, it indicates the number of missed
        // images since the last lock image call.
        //
        public native int             uiBufSeqNum();     public native FlyCaptureImagePlus uiBufSeqNum(int uiBufSeqNum);

        //
        // Reserved for future use.
        //
        @Cast("unsigned long") // ulReserved[ 7 ]
        public native int             ulReserved(int i); public native FlyCaptureImagePlus ulReserved(int i, int ulReserved);
    }

    //
    // Description:
    //   This structure is used for partial image notification functionality.
    //   Please see the release notes and API documentation for details.
    //
    // See Also:
    //   flycaptureInitializeNotify(), flycaptureLockNextEvent(),
    //   flycaptureWaitForImageEvent(), flycaptureUnlockEvent().
    //   flycaptureGetPacketSize(), flycaptureGetCustomImageMaxPacketSize()
    //
    public static class FlyCaptureImageEvent extends Pointer {
        static { load(); }
        public FlyCaptureImageEvent() { allocate(); }
        public FlyCaptureImageEvent(int size) { allocateArray(size); }
        public FlyCaptureImageEvent(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureImageEvent position(int position) {
            return (FlyCaptureImageEvent)super.position(position);
        }

        //
        // A pointer to the start of the location inside the image buffer that
        // this event corresponds to.  This is only valid on structures coming back
        // from flycaptureLockNextEvent() and points to a buffer allocated
        // internally (in the case of flycaptureInitialize*(), or passed in by the
        // user (in the case of flycaptureInitializePlus()).
        //
        @Cast("unsigned char*")
        public native BytePointer pBuffer();         public native FlyCaptureImageEvent pBuffer(BytePointer pBuffer);

        //
        // The size of the image portion that this event corresponds to.  This must
        // be specified for structures being passed in to
        // flycaptureInitializeNotify().  This is the only member that needs to be
        // specified.  The sizes passed in to flycaptureInitializeNotify() must
        // add up to the total image size and must be whole multiples of the packet
        // size.  Appropriate packet sizes can be determined using
        // flycaptureGetPacketSize() and flycaptureGetCustomImagePacketInfo().
        //
        public native int         uiSizeBytes();     public native FlyCaptureImageEvent uiSizeBytes(int uiSizeBytes);

        //
        // Sequence number for this image event.  Populated by
        // flycaptureWaitForImageEvent().  Sequence numbers should be contiguous if
        // no image buffers are being dropped.  If they are not, then the user level
        // grab thread is not keeping up with the images the camera is sending, and
        // this is a fatal error.
        //
        public native int         uiSeqNum();        public native FlyCaptureImageEvent uiSeqNum(int uiSeqNum);

        // The internal buffer index of this image portion.  This can be ignored.
        public native int         uiBufferIndex();   public native FlyCaptureImageEvent uiBufferIndex(int uiBufferIndex);

        // Internal bookkeeping.  This can be ignored.
        public native Pointer     pInternal();       public native FlyCaptureImageEvent pInternal(Pointer pInternal);

        // Reserved for future use.
        @Cast("unsigned long") // ulReserved[ 8 ];
        public native int         ulReserved(int i); public native FlyCaptureImageEvent ulReserved(int i, int ulReserved);
    }

    //
    // Description:
    //   The rate at which data is transmitted over the 1394 bus is determined in
    //   part by the size of the datapackets.  This structure describes a camera's
    //   capabilities for a given video mode.
    //
    // See Also:
    //   flycaptureGetPacketInfo()
    //
    public static class FlyCapturePacketInfo extends Pointer {
        static { load(); }
        public FlyCapturePacketInfo() { allocate(); }
        public FlyCapturePacketInfo(int size) { allocateArray(size); }
        public FlyCapturePacketInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCapturePacketInfo position(int position) {
            return (FlyCapturePacketInfo)super.position(position);
        }

        // Minimum packet size, in bytes.
        public native int uiMinSizeBytes();      public native FlyCapturePacketInfo uiMinSizeBytes(int uiMinSizeBytes);
        // Maximum packet size, in bytes.  Note that this ignores the OS-enforced
        // bandwidth restrictions.  The realized max packet size will be 80% of
        // this reported value.
        public native int uiMaxSizeBytes();      public native FlyCapturePacketInfo uiMaxSizeBytes(int uiMaxSizeBytes);
        // Maximum packet size, in bytes, when using events.  The bandwidth note
        // for uiMaxSizeBytes applies here too.
        public native int uiMaxSizeEventBytes(); public native FlyCapturePacketInfo uiMaxSizeEventBytes(int uiMaxSizeEventBytes);
        // Reserved for future use.
        @Cast("unsigned long") // ulReserved[ 8 ];
        public native int ulReserved(int i);     public native FlyCapturePacketInfo ulReserved(int i, int ulReserved);
    }

    //
    // Description:
    //  Available image filters.  These are bit values for a bitmask that will be
    //  set with flycaptureSetImageFilers() and retrieved with
    //  flycaptureGetImageFilters().  Currently there is only one available filter.
    //
    public static final int
            // Disable all image filters.
            FLYCAPTURE_IMAGE_FILTER_NONE                 = 0x00000000,
            //
            // Crosstalk filter for colour Scorpion cameras with the
            // Symmagery VCA1281 sensor.  This filter will be automatically
            // enabled for cameras with this sensor.  This filter is applied
            // during flycaptureStippledToBGR*() calls.
            //
            FLYCAPTURE_IMAGE_FILTER_SCORPION_CROSSTALK   = 0x00000001,
            // Enable all image filters.
            FLYCAPTURE_IMAGE_FILTER_ALL                  = 0xFFFFFFFF;


    //=============================================================================
    // Construction/Destruction Functions
    //=============================================================================
    // Group = Construction/Destruction

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureInitializePlus()
    //
    // Description:
    //   Identical behaviour to flycaptureInitialize(), except that the user has
    //   the option of specifying the number of buffers to use, and optionally
    //   allocate those buffers outside the library.
    //
    // Arguments:
    //   context      - The context associated with the camera to be accessed.
    //   ulBusIndex   - The zero-based device index of the camera to be initialized.
    //   ulNumBuffers - The number of buffers to expect or allocate.  For lock next
    //                  mode, the minimum number of buffers is 2.  For lock latest
    //                  mode, the minimum number of buffers is 4.  The maximum
    //                  number of buffers is only limited by system memory.
    //   arpBuffers   - An array of pointers to buffers.  If this argument is NULL
    //                  the library will allocate and free the buffers internally,
    //                  otherwise the caller is responsible for allocation and
    //                  deallocation.  No boundary checking is done on these
    //                  images, if you are supplying your own buffers, they must
    //                  be large enough to hold the largest image you are
    //                  expecting.
    //
    //                  When allocating your own buffers, you must take padding into
    //                  account.  The maximum amount of padding required is 1 packet,
    //                  which can be up to 4096 bytes for 1394a and 8192 bytes for 1394b.
    //                  Adding this padding to the image size will ensure the buffer
    //                  is large enough to accomodate the image.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   If you wish to use the camera serial number to initialize, or you don't
    //   care about the number of buffers being allocated, use either of the
    //   other initialize methods in pgrflycapture.h.
    //
    // See Also:
    //   flycaptureInitialize()
    //
    public static native @Cast("FlyCaptureError") int flycaptureInitializePlus(FlyCaptureContext context,
            @Cast("unsigned long") int ulBusIndex, @Cast("unsigned long") int ulNumBuffers,
            @Cast("unsigned char**") BytePointer arpBuffers);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureInitializeFromSerialNumberPlus()
    //
    // Description:
    //   Identical behaviour to flycaptureInitializeFromSerialNumber(), except that the user has
    //   the option of specifying the number of buffers to use, and optionally
    //   allocate those buffers outside the library.
    //
    // Arguments:
    //   context      - The context associated with the camera to be accessed.
    //   serialNumber - The serial number of the FlyCapture camera system to be initialized.
    //   ulNumBuffers - The number of buffers to expect or allocate.  For lock next
    //                  mode, the minimum number of buffers is 2.  For lock latest
    //                  mode, the minimum number of buffers is 4.  The maximum
    //                  number of buffers is only limited by system memory.
    //   arpBuffers   - An array of pointers to buffers.  If this argument is NULL
    //                  the library will allocate and free the buffers internally,
    //                  otherwise the caller is responsible for allocation and
    //                  deallocation.  No boundary checking is done on these
    //                  images, if you are supplying your own buffers, they must
    //                  be large enough to hold the largest image you are
    //                  expecting.
    //
    //                  When allocating your own buffers, you must take padding into
    //                  account.  The maximum amount of padding required is 1 packet,
    //                  which can be up to 4096 bytes for 1394a and 8192 bytes for 1394b.
    //                  Adding this padding to the image size will ensure the buffer
    //                  is large enough to accomodate the image.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   If you don't care about the number of buffers being allocated, use either of the
    //   other initialize methods in pgrflycapture.h.
    //
    // See Also:
    //   flycaptureInitialize(), flycaptureInitializeFromSerialNumber(), flycaptureInitializePlus()
    //
//    public static native @Cast("FlyCaptureError") int flycaptureInitializeFromSerialNumberPlus(FlyCaptureContext context,
//            FlyCaptureCameraSerialNumber serialNumber, @Cast("unsigned long") int ulNumBuffers, Pointer arpBuffers);

    //=============================================================================
    // Control Functions
    //=============================================================================
    // Group = Control Functions

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureInitializeNotify()
    //
    // Description:
    //   Initializes partial image notification mode.
    //   Partial image notification allows the grabbing program to be notified
    //   several times during a single image grab.  Processing on an image can
    //   then begin even before the entire image has been acquired.
    //   This function must be called after a camera initialization function like
    //   flycaptureInitialize() or flycaptureInitializePlus(), and before a start
    //   function like flycaptureStartLockNext().  flycaptureLockNextEvent(),
    //   flycaptureWaitForImageEvent() and flycaptureUnlockEvent() are the only
    //   image acquisition functions that can be used when in partial image
    //   notification mode.  Please see the ImageEventEx example for more
    //   information.
    //
    // Arguments:
    //   context     - The context associated with the camera to be accessed.
    //   ulNumEvents - The number of desired image events per image.  The maximum
    //                 number of events is camera-dependant.
    //   arpEvents   - An array of uiNumEvents event structures.  The uiSizeBytes
    //                 member must be filled, which indicates which portion of the
    //                 image each event is for.  The image portions need not be
    //                 equal sized.  No other members need to be filled.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This will not work unless you have a hotfix to the 1394 subsystem from
    //   Microsoft. Please see PGR knowledge base article 153,
    //   http://www.ptgrey.com/support/kb/details.asp?id=153, for more information.
    //   Partial image notification is not available for "lock latest"
    //   functionality.  This is PGR bug 2126.
    //
    // See Also:
    //   flycaptureGetPacketInfo(), flycaptureGetCustomImagePacketInfo()
    //
//    public static native @Cast("FlyCaptureError") int flycaptureInitializeNotify(FlyCaptureContext context,
//            @Cast("unsigned long") long ulNumEvents, FlyCaptureImageEvent arpEvents);


    //=============================================================================
    // Control Functions
    //=============================================================================
    // Group = Control Functions

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureStartCustomImagePacket()
    //
    // Description:
    //   This function is identical to flycaptureStartCustomImage() except it
    //   takes in a packet size rather than a float bandwidth parameter.
    //
    // Arguments:
    //   context            - The FlyCaptureContext to start grabbing.
    //   ulMode             - The camera-specific mode.  (0-7).
    //   ulImagePosLeft     - The left position of the (sub)image.
    //   ulImagePosTop      - The top position of the (sub)image.
    //   ulWidth            - The width of the (sub)image.
    //   ulHeight           - The height of the (sub)image.
    //   ulPacketSizeBytes  - The number of packets to send per isochronous period.
    //                        A larger packet size will result in faster image
    //                        transmission and increased bandwidth requirements.
    //                        This number should be a multiple of 4 and fit within
    //                        the values defined by
    //                        flycaptureGetCustomImageMaxPacketSize().
    //   format             - The pixel format to be used.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureStartCustomImage(), flycaptureQueryCustomImage(),
    //   flycaptureGetCustomImagePacketInfo()
    //
    public static native @Cast("FlyCaptureError") int flycaptureStartCustomImagePacket(FlyCaptureContext context,
            @Cast("unsigned long") int ulMode,        @Cast("unsigned long") int ulImagePosLeft,
            @Cast("unsigned long") int ulImagePosTop, @Cast("unsigned long") int ulWidth,
            @Cast("unsigned long") int ulHeight,      @Cast("unsigned long") int ulPacketSizeBytes,
            @Cast("FlyCapturePixelFormat") int format);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureStartLockNext()
    //
    // Description:
    //   Starts the camera streaming and initializes the library for "lock next"
    //   functionality.  This function needs to used instead of flycaptureStart()
    //   for the following "lock next" functions.
    //
    // Arguments:
    //   context   - The context associated with the camera to be started.
    //   videoMode - The video mode to start the camera in.
    //   frameRate - The frame rate to start the camera at.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   For "lock latest" functionality, use flycaptureStart() and the
    //   flycaptureLockLatest().
    //
    public static native @Cast("FlyCaptureError") int flycaptureStartLockNext(FlyCaptureContext context,
            @Cast("FlyCaptureVideoMode") int videoMode, @Cast("FlyCaptureFrameRate") int frameRate);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureStartLockNextCustomImage()
    //
    // Description:
    //   This function is identical to flycaptureStartLockNext(), except that it
    //   will start the camera in custom image mode.  See
    //   flycaptureStartCustomImage().
    //
    // Arguments:
    //   context         - The context associated with the camera to be started.
    //   ulMode          - The camera-specific mode.  (0-7).
    //   ulImagePosLeft  - The left position of the (sub)image.
    //   ulImagePosTop   - The top position of the (sub)image.
    //   ulWidth         - The width of the (sub)image.
    //   ulHeight        - The height of the (sub)image.
    //   fBandwidth      - The bandwidth to assign to this camera.  100.0 indicates
    //                     full bandwidth.
    //   format          - The pixel format to be used.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   For "lock latest" functionality, use flycaptureStart() and the
    //   flycaptureLockLatest().
    //
    // See Also:
    //   flycaptureQueryCustomImage(), flycaptureStartCustomImage().
    //
    public static native @Cast("FlyCaptureError") int flycaptureStartLockNextCustomImage(FlyCaptureContext context,
            @Cast("unsigned long") int ulMode,        @Cast("unsigned long") int ulImagePosLeft,
            @Cast("unsigned long") int ulImagePosTop, @Cast("unsigned long") int ulWidth,
            @Cast("unsigned long") int ulHeight, float fBandwidth, @Cast("FlyCapturePixelFormat") int format);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureStartLockNextCustomImagePacket()
    //
    // Description:
    //   This function is identical to flycaptureStartLockNextCustomImage(),
    //   except that it takes a packet size in bytes, instead of a floating point
    //   bandwidth estimation.
    //
    // Arguments:
    //   context         - The context associated with the camera to be started.
    //   ulMode          - The camera-specific mode.  (0-7).
    //   ulImagePosLeft  - The left position of the (sub)image.
    //   ulImagePosTop   - The top position of the (sub)image.
    //   ulWidth         - The width of the (sub)image.
    //   ulHeight        - The height of the (sub)image.
    //   ulPacketSizeBytes  - The number of packets to send per isochronous period.
    //                        A larger packet size will result in faster image
    //                        transmission and increased bandwidth requirements.
    //                        This number should be a multiple of 4 and fit within
    //                        the values defined by
    //                        flycaptureGetCustomImageMaxPacketSize().
    //   format          - The pixel format to be used.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureQueryCustomImage(), flycaptureStartCustomImage().
    //
    public static native @Cast("FlyCaptureError") int flycaptureStartLockNextCustomImagePacket(FlyCaptureContext context,
            @Cast("unsigned long") long ulMode,        @Cast("unsigned long") long ulImagePosLeft,
            @Cast("unsigned long") long ulImagePosTop, @Cast("unsigned long") long ulWidth,
            @Cast("unsigned long") long ulHeight,      @Cast("unsigned long") long ulPacketSizeBytes,
            @Cast("FlyCapturePixelFormat") int format);


    //=============================================================================
    // Image Related Functions
    //=============================================================================
    // Group = Image Related Functions

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSyncForLockNext()
    //
    // Description:
    //  Takes in an array of contexts attached to multiple cameras that
    //  are already synchronized in hardware and assures that the
    //  next time lockNext() is called for all contexts, the images locked will
    //  correspond to one another.  Note that this function only needs to be called
    //  once after the contexts have been started.  The contexts should be started
    //  in the same order that they are listed in arContexts before this function
    //  is called.
    //
    // Arguments:
    //   arContexts - An array of contexts attached to the cameras to synchronize.
    //   ulContexts - The number of contexts in arContext.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This function operates by skipping the appropriate number of images
    //   in contexts that were started "after" the reference context (position 0
    //   in the array).  If this function fails it does not necessarily mean the
    //   cameras are out of sync.  This is still experimental.  Note also that
    //   this function will turn on image timestamping.  Please contact PGR
    //   support for more information.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSyncForLockNext(FlyCaptureContext arContexts,
            @Cast("unsigned long") int ulContexts);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureLockNext()
    //
    // Description:
    //   Lock the "next" image that has not been seen.  Provided that the previous
    //   image processing time is not greater than the time taken for the camera
    //   to transmit images to the available unlocked buffers, this function can
    //   be called repeatedly to guarantee that each image will be seen.  If the
    //   camera has not finished transmitting the next image, this function will
    //   block.  Users can verify image sequentiality by comparing sequence
    //   numbers of sequential images.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   pimage    - The returned FlyCaptureImagePlus.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   flycaptureUnlock() must be called using the buffer index returned in
    //   pimage when processing on this image has been completed.  The camera must
    //   have been started using flycaptureStartLockNext() for this function to
    //   succeed.
    //
    public static native @Cast("FlyCaptureError") int flycaptureLockNext(FlyCaptureContext context,
            FlyCaptureImagePlus pimage);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureLockLatest()
    //
    // Description:
    //   Lock the "latest" image that has not been seen.  If there is an unseen
    //   image waiting, this function will return immediately with that image,
    //   otherwise it will block until the next image has been received.  The
    //   difference in the sequence numbers of images returned by consecutive calls
    //   to this function indicates the number of missed images between calls.
    //
    // Arguments:
    //   context - The context associated with the camera to be accessed.
    //   pimage  - The returned FlyCaptureImagePlus.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   flycaptureUnlock() must be called using the buffer index returned in
    //   pimage when processing on this image has been completed.  This function
    //   behaves identically to flycaptureGrabImage(), except it doesn't implicitly
    //   unlock the previously seen image first.  The camera must have been
    //   started using flycaptureStart() in order for this function to succeed.
    //
    public static native @Cast("FlyCaptureError") int flycaptureLockLatest(FlyCaptureContext context,
            FlyCaptureImagePlus pimage);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureUnlock()
    //
    // Description:
    //   Returns a buffer into the pool to be filled by the camera driver.  This
    //   must be called for each image locked using the above lock functions after
    //   processing on that image has been completed.
    //
    // Arguments:
    //   context       - The context associated with the camera to be accessed.
    //   ulBufferIndex - The buffer to unlock.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureUnlock(FlyCaptureContext context,
            @Cast("unsigned long") int ulBufferIndex);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureUnlockAll()
    //
    // Description:
    //   Unlocks all locked images.  This is equivalent to maintaining a list of
    //   locked buffers and calling flycaptureUnlock() for each.
    //
    // Arguments:
    //   context - The context associated with the camera to be accessed.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureUnlockAll(FlyCaptureContext context);


    //=============================================================================
    // Image Related Functions
    //=============================================================================
    // Group = Image Related Functions

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetImageFilters()
    //
    // Description:
    //   Retrieves the currently active filters.  The returned number is a
    //   bitmap corresponding to the FLYCAPTURE_IMAGE_FILTER_* values.
    //
    // Arguments:
    //   context    - The context associated with the camera to be accessed.
    //   puiFilters - The filter bitmap is returned in this value.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetImageFilters(FlyCaptureContext context,
            @Cast("unsigned int*") int[] puiFilters);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetImageFilters()
    //
    // Description:
    //   Sets the active filters.  The returned number is a bitmap corresponding to
    //   the FLYCAPTURE_IMAGE_FILTER_* values.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   uiFilters - The filters to set.  Use FLYCAPTURE_IMAGE_FILTER_NONE to
    //               disable image filtering.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetImageFilters(FlyCaptureContext context,
            int uiFilters);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetImageTimestamping()
    //
    // Description:
    //   Retrieves the status of camera-generated image timestamping.
    //
    // Arguments:
    //   context - The context associated with the camera to be accessed.
    //   pbOn    - Whether or not the camera is producing image timestamps.
    //
    // Returns:
    //   FLYCAPTURE_OK - If the time stamping status was read correctly.
    //   FLYCAPTURE_NOT_IMPLEMENTED - If the camera does not support image
    //                                timestamping.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetImageTimestamping(FlyCaptureContext context,
            @Cast("bool*") boolean[] pbOn);


    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureSetImageTimestamping()
    //
    // Description:
    //   Sets image timestamping.  If image timestamping is on, the first 4 bytes
    //   of the image will contain camera-generated timestamp information, and
    //   the cycle seconds, count, and offset returned in FlyCaptureTimestamp
    //   will use the data.
    //
    // Arguments:
    //   context - The context associated with the camera to be accessed.
    //   bOn     - On or off flag for image timestamping.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetImageTimestamping(FlyCaptureContext context,
            @Cast("bool") boolean bOn);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureParseImageTimestamp()
    //
    // Description:
    //   Parses the first 4 bytes of an image generated with image timestamping on
    //   to retrieve 1394 timestamp information.
    //
    // Arguments:
    //   context    - The context associated with the camera to be accessed.
    //   pData      - The image data to be parsed.
    //   puiSeconds - The seconds component of the 1394 timestamp.
    //   puiCount   - The count component of the 1394 timestamp.
    //   puiOffset  - The offset component of the 1394 timestamp.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureParseImageTimestamp(FlyCaptureContext context,
            @Cast("unsigned char*") BytePointer pData, @Cast("unsigned int*") int[] puiSeconds, 
            @Cast("unsigned int*") int[] puiCount,     @Cast("unsigned int*") int[] puiOffset);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureLockNextEvent()
    //
    // Description:
    //   When in partial image notification mode (flycaptureInitializeNotify()),
    //   this function will fill an array of FlyCaptureImageEvent structures
    //   corresponding to the requested events for each received image.  This
    //   function will not block.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   pimage    - The returned FlyCaptureImage corresponding to the image that
    //               the events are for.
    //   arpEvents - An array of event structures that will be filled by this
    //               function.  The number of events in this array must be
    //               the number passed in to flycaptureInitializeNotify().  This
    //               array can contain the same events that were passed into
    //               flycaptureInitializeNotify(), or it can be a new array if you
    //               wish to retain ownership of the image buffer.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This will not work unless you have a hotfix to the 1394 subsystem from
    //   Microsoft. Please see PGR knowledge base article 153,
    //   http://www.ptgrey.com/support/kb/details.asp?id=153, for more information.
    //
//    public static native @Cast("FlyCaptureError") int flycaptureLockNextEvent(FlyCaptureContext context,
//            FlyCaptureImage pimage, FlyCaptureImageEvent arpEvents);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureWaitForImageEvent()
    //
    // Description:
    //   This function waits for a single partial image image event, as defined by
    //   the sizes specified by flycaptureInitializeNotify().  If the event has
    //   already been triggered (the image part has already been received) this
    //   function will return immediately.  It is not necessary to call this
    //   function for all the events in an image.  The events from a single image
    //   will be triggered in order.  To verify that no images have been missed,
    //   call this function on all the events of all the images received and verify
    //   the sequence numbers are contiguous.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   pevent    - The event structure corresponding to the part of the image
    //               to wait for.  This should be one of the structures filled in
    //               by flycaptureLockNextEvent().  At this point, the sequence
    //               number of the event is filled.
    //   ulTimeout - The time, in milliseconds, to wait for the image event to be
    //               received.  Can be FLYCAPTURE_INFINITE.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This will not work unless you have a hotfix to the 1394 subsystem from
    //   Microsoft. Please see PGR knowledge base article 153,
    //   http://www.ptgrey.com/support/kb/details.asp?id=153, for more information.
    //
    public static native @Cast("FlyCaptureError") int flycaptureWaitForImageEvent(FlyCaptureContext context,
            FlyCaptureImageEvent pevent, @Cast("unsigned long") int ulTimeout);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureUnlockEvent()
    //
    // Description:
    //   This function will release ownership of the buffers in the set of event
    //   structures.  It has the same functionality as flycaptureUnlock(), except
    //   that it unlocks the buffers in the correct order.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   arpEvents - An array of event structures that will be unlocked by this
    //               function.  The number of events in this array must be
    //               the number passed in to flycaptureInitializeNotify().  This
    //               array should be the same one filled by
    //               flycaptureLockNextEvent().
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // Remarks:
    //   This will not work unless you have a hotfix to the 1394 subsystem from
    //   Microsoft. Please see PGR knowledge base article 153,
    //   http://www.ptgrey.com/support/kb/details.asp?id=153, for more information.
    //
//    public static native @Cast("FlyCaptureError") int flycaptureUnlockEvent(FlyCaptureContext context,
//            FlyCaptureImageEvent arpEvents);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetPacketInfo()
    //
    // Description:
    //   Returns the isochronous packet size for the indicated video mode and
    //   frame rate.  This number is useful when deciding the amount of data for
    //   each image event notification.  The size of each image event has to be a
    //   multiple of the packet size.  It is also useful for determining the amount
    //   of bandwidth required to run a camera at a given mode and frame rate.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   videoMode - Required video mode.
    //   frameRate - Required frame rate.
    //   pinfo     - Returned packet size information.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureGetCustomImagePacketInfo()
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetPacketInfo(FlyCaptureContext context,
            @Cast("FlyCaptureVideoMode") int videoMode, @Cast("FlyCaptureFrameRate") int frameRate,
            FlyCapturePacketInfo pinfo);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureGetCustomImagePacketInfo()
    //
    // Description:
    //   Returns isochronous packet size information for the indicated custom image
    //   mode and image size.  The maximum packet size is useful for determining a
    //   minimum image event notification size.  This function is very similar to
    //   flycaptureGetPacketSize() but should be used when dealing with custom image
    //   modes.
    //
    // Arguments:
    //   context   - The context associated with the camera to be accessed.
    //   ulMode    - The camera-specific mode.  (0-7).
    //   ulWidth   - The width of the (sub)image.
    //   ulHeight  - The height of the (sub)image.
    //   format    - The pixel format to be used.
    //   pinfo     - Returned packet size information.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    // See Also:
    //   flycaptureGetPacketInfo(), flycaptureStartCustomImagePacket()
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetCustomImagePacketInfo(FlyCaptureContext context,
            @Cast("unsigned long") int ulMode, @Cast("unsigned long") int ulWidth, @Cast("unsigned long") int ulHeight,
            @Cast("FlyCapturePixelFormat") int format, FlyCapturePacketInfo pinfo);

    //=============================================================================
    // Camera Property Functions
    //=============================================================================
    // Group = Camera Property Functions

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureReadRegisterBlock()
    //
    // Description:
    //   Provides block-read (asynchronous) access to the entire register space of
    //   the camera.
    //
    // Arguments:
    //   context    - The context associated with the camera to be accessed.
    //   usAddrHigh - The top 16 bits of the 48-bit absolute address to read.
    //   ulAddrLow  - The bottom 32 bits of the 48-bit absolute addresss to read.
    //   pulBuffer  - The buffer that will receive the data.  Must be of size
    //                ulLength.
    //   ulLength   - The length, in quadlets, of the block to read.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureReadRegisterBlock(FlyCaptureContext context,
            short usAddrHigh, @Cast("unsigned long") int ulAddrLow, @Cast("unsigned long*") int[] pulBuffer,
            @Cast("unsigned long") int ulLength);

    //-----------------------------------------------------------------------------
    //
    // Name:  flycaptureWriteRegisterBlock()
    //
    // Description:
    //   Provides block-write (asynchronous) access to the entire register space of
    //   the camera.
    //
    // Arguments:
    //   context    - The context associated with the camera to be accessed.
    //   usAddrHigh - The top 16 bits of the 48-bit absolute address to write.
    //   ulAddrLow  - The bottom 32 bits of the 48-bit absolute addresss to write.
    //   pulBuffer  - The buffer that contains the data to be written.
    //   ulLength   - The length, in quadlets, of the block to write.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureWriteRegisterBlock(FlyCaptureContext context,
            short usAddrHigh, @Cast("unsigned long") int ulAddrLow, @Cast("unsigned long*") int[] pulBuffer,
            @Cast("unsigned long") int ulLength);



    //=============================================================================
    //
    // PGRFlyCaptureMessaging.h
    //
    //   Defines the API to the PGR FlyCapture Messaging library.
    //
    //  We welcome your bug reports, suggestions, and comments:
    //  www.ptgrey.com/support/contact
    //
    //=============================================================================

    //
    // This is used as the serial number when initializing or receiving
    // bus messages.
    //
    public static final int FLYCAPTURE_BUS_MESSAGE   = 999999999;

    //
    // Description:
    //  This structure is used in messages as either the timestamp of
    //  an image or a bus event time.
    //
    public static class FlyCaptureSystemTime extends Pointer {
        static { load(); }
        public FlyCaptureSystemTime() { allocate(); }
        public FlyCaptureSystemTime(int size) { allocateArray(size); }
        public FlyCaptureSystemTime(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureSystemTime position(int position) {
            return (FlyCaptureSystemTime)super.position(position);
        }

        public native short usHour();         public native FlyCaptureSystemTime usHour(short usHour);
        public native short usMinute();       public native FlyCaptureSystemTime usMinute(short usMinute);
        public native short usSecond();       public native FlyCaptureSystemTime usSecond(short usSecond);
        public native short usMilliseconds(); public native FlyCaptureSystemTime usMilliseconds(short usMilliseconds);
    }

    //
    // Description:
    //  Enumerates the message types that can be received.
    //
    //enum FlyCaptureMessageType
    public static final int
            // The bus was reset.
            FLYCAPTURE_BUS_RESET = 0,
            // A device was connected.
            FLYCAPTURE_DEVICE_ARRIVAL = 1,
            // A device was disconnected.
            FLYCAPTURE_DEVICE_REMOVAL = 2,
            // A 1394b bus has experienced an error.
            FLYCAPTURE_BUS_ERROR = 3,
            // An image has been grabbed.
            FLYCAPTURE_GRABBED_IMAGE = 4,
            // A register has been read.
            FLYCAPTURE_REGISTER_READ = 5,
            // A block of registers has been read.
            FLYCAPTURE_REGISTER_READ_BLOCK = 6,
            // A register has been written to.
            FLYCAPTURE_REGISTER_WRITE = 7,
            // A block of registers has been written to.
            FLYCAPTURE_REGISTER_WRITE_BLOCK = 8;

    //
    // Description:
    //  This structure is used to receive messages for specific cameras or
    //  bus events.  The information received is message dependent.
    //
    public static class FlyCaptureMessage extends Pointer {
        static { load(); }
        public FlyCaptureMessage() { allocate(); }
        public FlyCaptureMessage(int size) { allocateArray(size); }
        public FlyCaptureMessage(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FlyCaptureMessage position(int position) {
            return (FlyCaptureMessage)super.position(position);
        }

        // The type of message being received.
        @Cast("FlyCaptureMessageType")
        public native int msgType();       public native FlyCaptureMessage msgType(int msgType);


        // The message specific details.

        // The bus number of the device.
        @Name("Msg.Reset.iBusNumber")
        public native int                  Msg_Reset_iBusNumber();
        public native FlyCaptureMessage    Msg_Reset_iBusNumber(int Msg_Reset_iBusNumber);
        // The timestamp of the bus event.
        @Name("Msg.Reset.stTimeStamp")     @ByRef
        public native FlyCaptureSystemTime Msg_Reset_stTimeStamp();
        public native FlyCaptureMessage    Msg_Reset_stTimeStamp(FlyCaptureSystemTime Msg_Reset_stTimeStamp);

        // The full name of the device including model.
        // char                   szDevice[128];
        @Name("Msg.Arrival.szDevice")
        public native String               Msg_Arrival_szDevice();
        public native FlyCaptureMessage    Msg_Arrival_szDevice(String Msg_Arrival_szDevice);
        // The serial number of the device.
        @Name("Msg.Arrival.ulSerialNumber") @Cast("unsigned long")
        public native int                  Msg_Arrival_ulSerialNumber();
        public native FlyCaptureMessage    Msg_Arrival_ulSerialNumber(int Msg_Arrival_ulSerialNumber);
        // The bus number of the device.
        @Name("Msg.Arrival.iBusNumber")
        public native int                  Msg_Arrival_iBusNumber();
        public native FlyCaptureMessage    Msg_Arrival_iBusNumber(int Msg_Arrival_iBusNumber);
        // The node number of the device.
        @Name("Msg.Arrival.iNodeNumber")
        public native int                  Msg_Arrival_iNodeNumber();
        public native FlyCaptureMessage    Msg_Arrival_iNodeNumber(int Msg_Arrival_iNodeNumber);
        // The timestamp of the bus event.
        @Name("Msg.Arrival.stTimeStamp")   @ByRef
        public native FlyCaptureSystemTime Msg_Arrival_stTimeStamp();
        public native FlyCaptureMessage    Msg_Arrival_stTimeStamp(FlyCaptureSystemTime Msg_Arrival_stTimeStamp);

        // The full name of the device including model.
        // char                   szDevice[128];
        @Name("Msg.Removal.szDevice")
        public native String               Msg_Removal_szDevice();
        public native FlyCaptureMessage    Msg_Removal_szDevice(String Msg_Removal_szDevice);
        // The serial number of the device.
        @Name("Msg.Removal.ulSerialNumber") @Cast("unsigned long")
        public native int                  Msg_Removal_ulSerialNumber();
        public native FlyCaptureMessage    Msg_Removal_ulSerialNumber(int Msg_Removal_ulSerialNumber);
        // The bus number of the device.
        @Name("Msg.Removal.iBusNumber")
        public native int                  Msg_Removal_iBusNumber();
        public native FlyCaptureMessage    Msg_Removal_iBusNumber(int Msg_Removal_iBusNumber);
        // The node number of the device.
        @Name("Msg.Removal.iNodeNumber")
        public native int                  Msg_Removal_iNodeNumber();
        public native FlyCaptureMessage    Msg_Removal_iNodeNumber(int Msg_Removal_iNodeNumber);
        // The timestamp of the bus event.
        @Name("Msg.Removal.stTimeStamp")   @ByRef
        public native FlyCaptureSystemTime Msg_Removal_stTimeStamp();
        public native FlyCaptureMessage    Msg_Removal_stTimeStamp(FlyCaptureSystemTime Msg_Removal_stTimeStamp);

        // The full name of the device including model.
        // char                   szDevice[128];
        @Name("Msg.BusError.szDevice")
        public native String               Msg_BusError_szDevice();
        public native FlyCaptureMessage    Msg_BusError_szDevice(String Msg_BusError_szDevice);
        // The serial number of the device.
        @Name("Msg.BusError.ulSerialNumber") @Cast("unsigned long")
        public native int                  Msg_BusError_ulSerialNumber();
        public native FlyCaptureMessage    Msg_BusError_ulSerialNumber(int Msg_BusError_ulSerialNumber);
        // The bus number of the device.
        @Name("Msg.BusError.iBusNumber")
        public native int                  Msg_BusError_iBusNumber();
        public native FlyCaptureMessage    Msg_BusError_iBusNumber(int Msg_BusError_iBusNumber);
        // The node number of the device.
        @Name("Msg.BusError.iNodeNumber")
        public native int                  Msg_BusError_iNodeNumber();
        public native FlyCaptureMessage    Msg_BusError_iNodeNumber(int Msg_BusError_iNodeNumber);
        // The timestamp of the bus error.
        @Name("Msg.BusError.stTimeStamp")  @ByRef
        public native FlyCaptureSystemTime Msg_BusError_stTimeStamp();
        public native FlyCaptureMessage    Msg_BusError_stTimeStamp(FlyCaptureSystemTime Msg_BusError_stTimeStamp);
        // The bus error code.
        @Name("Msg.BusError.ulErrorCode")  @Cast("unsigned long")
        public native int                  Msg_BusError_ulErrorCode();
        public native FlyCaptureMessage    Msg_BusError_ulErrorCode(int Msg_BusError_ulErrorCode);

        // The full name of the device including model.
        // char                   szDevice[128];
        @Name("Msg.Image.szDevice")
        public native String               Msg_Image_szDevice();
        public native FlyCaptureMessage    Msg_Image_szDevice(String Msg_Image_szDevice);
        // The serial number of the device.
        @Name("Msg.Image.ulSerialNumber")  @Cast("unsigned long")
        public native int                  Msg_Image_ulSerialNumber();
        public native FlyCaptureMessage    Msg_Image_ulSerialNumber(int Msg_Image_ulSerialNumber);
        // The bus number of the device.
        @Name("Msg.Image.iBusNumber")
        public native int                  Msg_Image_iBusNumber();
        public native FlyCaptureMessage    Msg_Image_iBusNumber(int Msg_Image_iBusNumber);
        // The node number of the device.
        @Name("Msg.Image.iNodeNumber")
        public native int                  Msg_Image_iNodeNumber();
        public native FlyCaptureMessage    Msg_Image_iNodeNumber(int Msg_Image_iNodeNumber);
        // The sequence number of the grabbed image.
        @Name("Msg.Image.ulSequence")      @Cast("unsigned long")
        public native int                  Msg_Image_ulSequence();
        public native FlyCaptureMessage    Msg_Image_ulSequence(int Msg_Image_ulSequence);
        // The size (in bytes) of the grabbed image.
        @Name("Msg.Image.ulBytes")         @Cast("unsigned long")
        public native int                  Msg_Image_ulBytes();
        public native FlyCaptureMessage    Msg_Image_ulBytes(int Msg_Image_ulBytes);
        // The timestamp of the grabbed image.
        @Name("Msg.Image.stTimeStamp")     @ByRef
        public native FlyCaptureSystemTime Msg_Image_stTimeStamp();
        public native FlyCaptureMessage    Msg_Image_stTimeStamp(FlyCaptureSystemTime Msg_Image_stTimeStamp);

        // The full name of the device including model.
        // char                   szDevice[128];
        @Name("Msg.Register.szDevice")
        public native String               Msg_Register_szDevice();
        public native FlyCaptureMessage    Msg_Register_szDevice(String Msg_Register_szDevice);
        // The serial number of the device.
        @Name("Msg.Register.ulSerialNumber") @Cast("unsigned long")
        public native int                  Msg_Register_ulSerialNumber();
        public native FlyCaptureMessage    Msg_Register_ulSerialNumber(int Msg_Register_ulSerialNumber);
        // The bus number of the device.
        @Name("Msg.Register.iBusNumber")
        public native int                  Msg_Register_iBusNumber();
        public native FlyCaptureMessage    Msg_Register_iBusNumber(int Msg_Register_iBusNumber);
        // The node number of the device.
        @Name("Msg.Register.iNodeNumber")
        public native int                  Msg_Register_iNodeNumber();
        public native FlyCaptureMessage    Msg_Register_iNodeNumber(int Msg_Register_iNodeNumber);
        // The register being read/written to.
        @Name("Msg.Register.ulRegister")   @Cast("unsigned long")
        public native int                  Msg_Register_ulRegister();
        public native FlyCaptureMessage    Msg_Register_ulRegister(int Msg_Register_ulRegister);
        // The value being read/written.
        @Name("Msg.Register.ulValue")      @Cast("unsigned long")
        public native int                  Msg_Register_ulValue();
        public native FlyCaptureMessage    Msg_Register_ulValue(int Msg_Register_ulValue);
        // The error received after a read/write.
        // char                   szError[16];
        @Name("Msg.Register.szError")
        public native String               Msg_Register_szError();
        public native FlyCaptureMessage    Msg_Register_szError(String Msg_Register_szError);

        // The full name of the device including model.
        // char                   szDevice[128];
        @Name("Msg.RegisterBlock.szDevice")
        public native String               Msg_RegisterBlock_szDevice();
        public native FlyCaptureMessage    Msg_RegisterBlock_szDevice(String Msg_RegisterBlock_szDevice);
        // The serial number of the device.
        @Name("Msg.RegisterBlock.ulSerialNumber") @Cast("unsigned long")
        public native int                  Msg_RegisterBlock_ulSerialNumber();
        public native FlyCaptureMessage    Msg_RegisterBlock_ulSerialNumber(int Msg_RegisterBlock_ulSerialNumber);
        // The bus number of the device.
        @Name("Msg.RegisterBlock.iBusNumber")
        public native int                  Msg_RegisterBlock_iBusNumber();
        public native FlyCaptureMessage    Msg_RegisterBlock_iBusNumber(int Msg_RegisterBlock_iBusNumber);
        // The node number of the device.
        @Name("Msg.RegisterBlock.iNodeNumber")
        public native int                  Msg_RegisterBlock_iNodeNumber();
        public native FlyCaptureMessage    Msg_RegisterBlock_iNodeNumber(int Msg_RegisterBlock_iNodeNumber);
        // The register being read/written to.
        @Name("Msg.RegisterBlock.ulRegister") @Cast("unsigned long")
        public native int                  Msg_RegisterBlock_ulRegister();
        public native FlyCaptureMessage    Msg_RegisterBlock_ulRegister(int Msg_RegisterBlock_ulRegister);
        // The number of register quadlets read/written to.
        @Name("Msg.RegisterBlock.ulNumberOfQuadlets") @Cast("unsigned long")
        public native int                  Msg_RegisterBlock_ulNumberOfQuadlets();
        public native FlyCaptureMessage    Msg_RegisterBlock_ulNumberOfQuadlets(int Msg_RegisterBlock_ulNumberOfQuadlets);
        // The error received after a read/write.
        // char                   szError[16];
        @Name("Msg.RegisterBlock.szError")
        public native String               Msg_RegisterBlock_szError();
        public native FlyCaptureMessage    Msg_RegisterBlock_szError(String Msg_RegisterBlock_szError);

        // Reserved for future use.
        @Cast("unsigned long") // ulReserved[64];
        public native int ulReserved(int i); public native FlyCaptureMessage ulReserved(int i, int ulReserved);
    }

    //=============================================================================
    // Messaging Functions
    //=============================================================================
    // Group = Messaging

    //-----------------------------------------------------------------------------
    // Name: flycaptureSetMessageLoggingStatus()
    //
    // Description:
    //   This function turns message logging on and off.
    //
    // Arguments:
    //   context  - The FlyCaptureContext associated with the camera.
    //   bEnable  - TRUE turns message logging on, FALSE turns it off.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureSetMessageLoggingStatus(FlyCaptureContext context,
            @Cast("bool") boolean bEnable);

    //-----------------------------------------------------------------------------
    // Name: flycaptureGetMessageLoggingStatus()
    //
    // Description:
    //   This function returns the status of message logging.
    //
    // Arguments:
    //   context   - The FlyCaptureContext associated with the camera.
    //   pbEnabled - TRUE if message logging is on, FALSE if it is off.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureGetMessageLoggingStatus(FlyCaptureContext context,
            @Cast("bool*") boolean[] pbEnabled);

    //-----------------------------------------------------------------------------
    // Name: flycaptureInitializeMessaging()
    //
    // Description:
    //   This function initializes messaging for a specific camera or all buses.
    //
    // Arguments:
    //   context         - The FlyCaptureContext associated with the camera.
    //   ulSerialNumber  - The serial number of the camera of which to
    //                     initialize messaging for.  Use FLYCAPTURE_BUS_MESSAGE as
    //                     the serial number to initialize bus event messages.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureInitializeMessaging(FlyCaptureContext context,
            @Cast("unsigned long") int ulSerialNumber);

    //-----------------------------------------------------------------------------
    // Name: flycaptureCloseMessaging()
    //
    // Description:
    //   This function closes messaging for a specific camera or all buses.
    //
    // Arguments:
    //   context         - The FlyCaptureContext associated with the camera.
    //   ulSerialNumber  - The serial number of the camera of which to
    //                     close messaging for. Use FLYCAPTURE_BUS_MESSAGE as the
    //                     serial number to close bus event messaging.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureCloseMessaging(FlyCaptureContext context,
            @Cast("unsigned long") int ulSerialNumber);

    //-----------------------------------------------------------------------------
    // Name: flycaptureReceiveMessage()
    //
    // Description:
    //   This function is used to receive messages from cameras or bus events.
    //
    // Arguments:
    //   context         - The FlyCaptureContext associated with the camera.
    //   ulSerialNumber  - The serial number of the camera of which to
    //                     close messaging for.  Use FLYCAPTURE_BUS_MESSAGE as the
    //                     serial number to receive bus event messages.
    //   pMessage        - A pointer to the message which will be filled
    //                     when a message is received.
    //   polRead         - A pointer to an overlapped I/O structure.  The event
    //                     handle of this structure is set when a message
    //                     is received.
    //
    // Returns:
    //   A FlyCaptureError indicating the success or failure of the function.
    //
    public static native @Cast("FlyCaptureError") int flycaptureReceiveMessage(FlyCaptureContext context,
            @Cast("unsigned long") int ulSerialNumber, FlyCaptureMessage pMessage,
            @Cast("OVERLAPPED*") Pointer polRead);

    //-----------------------------------------------------------------------------
    //
    // Name: flycaptureBusErrorToString()
    //
    // FlyCaptureMessage.BusError.ulErrorCode
    // Description:
    //   This function provides the user with a mechanism for decoding the error
    //   code member returned as part of a FlycaptureMessage FLYCAPTURE_BUS_ERROR.
    //   It returns a string containing a description of the provided error.
    //
    // Arguments:
    //   ulErrorCode - The error code to be translated.
    //
    // Returns:
    //   A string containing a human readable interpretation of the error code.
    //
    public static native String flycaptureBusErrorToString(@Cast("unsigned long") int ulErrorCode);
}
