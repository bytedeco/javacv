/*
 * Copyright (C) 2011,2012,2013 Samuel Audet
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
 * This file is based on information found in highgui_c.h of OpenCV 2.4.8,
 * which are covered by the following copyright notice:
 *
 *                          License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000, Intel Corporation, all rights reserved.
 * Third party copyrights are property of their respective owners.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * Redistribution's of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistribution's in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * The name of the copyright holders may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as is" and
 * any express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the Intel Corporation or contributors be liable for any direct,
 * indirect, incidental, special, exemplary, or consequential damages
 * (including, but not limited to, procurement of substitute goods or services;
 * loss of use, data, or profits; or business interruption) however caused
 * and on any theory of liability, whether in contract, strict liability,
 * or tort (including negligence or otherwise) arising in any way out of
 * the use of this software, even if advised of the possibility of such damage.
 *
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties(inherit=opencv_imgproc.class, value={
    @Platform(include="<opencv2/highgui/highgui_c.h>", link="opencv_highgui@.2.4"),
    @Platform(value="windows", link="opencv_highgui248", preload={"opencv_ffmpeg248", "opencv_ffmpeg248_64"}) })
public class opencv_highgui {
    static { load(); }

    public static final int
            CV_FONT_LIGHT          = 25,
            CV_FONT_NORMAL         = 50,
            CV_FONT_DEMIBOLD       = 63,
            CV_FONT_BOLD           = 75,
            CV_FONT_BLACK          = 87,

            CV_STYLE_NORMAL        = 0,
            CV_STYLE_ITALIC        = 1,
            CV_STYLE_OBLIQUE       = 2;

    @Platform("linux")
    public static native @ByVal CvFont cvFontQt(String nameFont, int pointSize/*=-1*/,
            @ByVal CvScalar color/*=cvScalarAll(0)*/, int weight/*=CV_FONT_NORMAL*/,
            int style/*=CV_STYLE_NORMAL*/, int spacing/*=0*/);

    @Platform("linux")
    public static native void cvAddText(CvArr img, String text, @ByVal CvPoint org, CvFont arg2);

    @Platform("linux")
    public static native void cvDisplayOverlay(String name, String text, int delayms/*=0*/);
    @Platform("linux")
    public static native void cvDisplayStatusBar(String name, String text, int delayms/*=0*/);

    @Platform("linux")
    public static native void cvSaveWindowParameters(String name);
    @Platform("linux")
    public static native void cvLoadWindowParameters(String name);
    @Platform("linux")
    public static class Pt2Func extends FunctionPointer {
        static { load(); }
        public    Pt2Func(Pointer p) { super(p); }
        protected Pt2Func() { allocate(); }
        private native void allocate();
        public native int call(int argc, @Cast("char**") PointerPointer argv);
    }
    @Platform("linux")
    public static native int cvStartLoop(Pt2Func pt2Func, int argc, @Cast("char**") PointerPointer argv);
    @Platform("linux")
    public static native void cvStopLoop();

    @Platform("linux")
    public static class CvButtonCallback extends FunctionPointer {
        static { load(); }
        public    CvButtonCallback(Pointer p) { super(p); }
        protected CvButtonCallback() { allocate(); }
        private native void allocate();
        public native void call(int state, Pointer userdata);
    }
    public static int CV_PUSH_BUTTON = 0, CV_CHECKBOX = 1, CV_RADIOBOX = 2;
    @Platform("linux")
    public static native int cvCreateButton(String button_name/*=null*/, CvButtonCallback on_change/*=null*/,
            Pointer userdata/*=null*/, int button_type/*=CV_PUSH_BUTTON*/, int initial_button_state/*=0*/);


    public static native int cvInitSystem(int argc, @Cast("char**") PointerPointer argv);
    public static native int cvStartWindowThread();

    public static final int
        CV_WND_PROP_FULLSCREEN  = 0,
        CV_WND_PROP_AUTOSIZE    = 1,
        CV_WND_PROP_ASPECTRATIO = 2,
        CV_WND_PROP_OPENGL      = 3,

        CV_WINDOW_NORMAL        = 0x00000000,
        CV_WINDOW_AUTOSIZE      = 0x00000001,
        CV_WINDOW_OPENGL        = 0x00001000,

        CV_GUI_EXPANDED         = 0x00000000,
        CV_GUI_NORMAL           = 0x00000010,

        CV_WINDOW_FULLSCREEN    = 1,
        CV_WINDOW_FREERATIO     = 0x00000100,
        CV_WINDOW_KEEPRATIO     = 0x00000000;

    public static int cvNamedWindow(String name) { return cvNamedWindow(name, CV_WINDOW_AUTOSIZE); }
    public static native int cvNamedWindow(String name, int flags/*=CV_WINDOW_AUTOSIZE*/);

    public static native void cvSetWindowProperty(String name, int prop_id, double prop_value);
    public static native double cvGetWindowProperty(String name, int prop_id);

    public static native void cvShowImage(String name, CvArr image);

    public static native void cvResizeWindow(String name, int width, int height);
    public static native void cvMoveWindow(String name, int x, int y);

    public static native void cvDestroyWindow(String name);
    public static native void cvDestroyAllWindows();

    public static native Pointer cvGetWindowHandle(String name);
    public static native String cvGetWindowName(Pointer window_handle);


    public static class CvTrackbarCallback extends FunctionPointer {
        static { load(); }
        public    CvTrackbarCallback(Pointer p) { super(p); }
        protected CvTrackbarCallback() { allocate(); }
        private native void allocate();
        public native void call(int pos);
    }
    public static native int cvCreateTrackbar(String trackbar_name, String window_name,
            int[] value, int count, CvTrackbarCallback on_change/*=null*/);

    public static class CvTrackbarCallback2 extends FunctionPointer {
        static { load(); }
        public    CvTrackbarCallback2(Pointer p) { super(p); }
        protected CvTrackbarCallback2() { allocate(); }
        private native void allocate();
        public native void call(int pos, Pointer userdata);
    }
    public static native int cvCreateTrackbar2(String trackbar_name, String window_name,
            int[] value, int count, CvTrackbarCallback2 on_change, Pointer userdata/*=null*/);

    public static native int cvGetTrackbarPos(String trackbar_name, String window_name);
    public static native void cvSetTrackbarPos(String trackbar_name, String window_name, int pos);


    public static final int
            CV_EVENT_MOUSEMOVE     = 0,
            CV_EVENT_LBUTTONDOWN   = 1,
            CV_EVENT_RBUTTONDOWN   = 2,
            CV_EVENT_MBUTTONDOWN   = 3,
            CV_EVENT_LBUTTONUP     = 4,
            CV_EVENT_RBUTTONUP     = 5,
            CV_EVENT_MBUTTONUP     = 6,
            CV_EVENT_LBUTTONDBLCLK = 7,
            CV_EVENT_RBUTTONDBLCLK = 8,
            CV_EVENT_MBUTTONDBLCLK = 9,

            CV_EVENT_FLAG_LBUTTON  = 1,
            CV_EVENT_FLAG_RBUTTON  = 2,
            CV_EVENT_FLAG_MBUTTON  = 4,
            CV_EVENT_FLAG_CTRLKEY  = 8,
            CV_EVENT_FLAG_SHIFTKEY = 16,
            CV_EVENT_FLAG_ALTKEY   = 32;

    public static class CvMouseCallback extends FunctionPointer {
        static { load(); }
        public    CvMouseCallback(Pointer p) { super(p); }
        protected CvMouseCallback() { allocate(); }
        private native void allocate();
        public native void call(int event, int x, int y, int flags, Pointer param);
    }
    public static native void cvSetMouseCallback(String window_name,
            CvMouseCallback on_mouse, Pointer param/*=null*/);


    public static final int
            CV_LOAD_IMAGE_UNCHANGED  = -1,
            CV_LOAD_IMAGE_GRAYSCALE  = 0,
            CV_LOAD_IMAGE_COLOR      = 1,
            CV_LOAD_IMAGE_ANYDEPTH   = 2,
            CV_LOAD_IMAGE_ANYCOLOR   = 4;

    public static IplImage cvLoadImage(String filename) { return cvLoadImage(filename, CV_LOAD_IMAGE_COLOR); }
    public static native IplImage cvLoadImage(String filename, int iscolor/*=CV_LOAD_IMAGE_COLOR*/);
    public static CvMat cvLoadImageM(String filename)   { return cvLoadImageM(filename, CV_LOAD_IMAGE_COLOR); }
    public static native CvMat cvLoadImageM(String filename, int iscolor/*=CV_LOAD_IMAGE_COLOR*/);

    public static IplImage cvLoadImageBGRA(String filename) {
        IplImage imageBGR = cvLoadImage(filename, CV_LOAD_IMAGE_COLOR);
        if (imageBGR == null) {
            return null;
        } else {
            IplImage imageBGRA = cvCreateImage(cvGetSize(imageBGR), imageBGR.depth(), 4);
            opencv_imgproc.cvCvtColor(imageBGR, imageBGRA, opencv_imgproc.CV_BGR2BGRA);
            cvReleaseImage(imageBGR);
            return imageBGRA;
        }
    }

    public static IplImage cvLoadImageRGBA(String filename) {
        IplImage imageBGR = cvLoadImage(filename, CV_LOAD_IMAGE_COLOR);
        if (imageBGR == null) {
            return null;
        } else {
            IplImage imageRGBA = cvCreateImage(cvGetSize(imageBGR), imageBGR.depth(), 4);
            opencv_imgproc.cvCvtColor(imageBGR, imageRGBA, opencv_imgproc.CV_BGR2RGBA);
            cvReleaseImage(imageBGR);
            return imageRGBA;
        }
    }

    public static final int
            CV_IMWRITE_JPEG_QUALITY              = 1,
            CV_IMWRITE_PNG_COMPRESSION           = 16,
            CV_IMWRITE_PNG_STRATEGY              = 17,
            CV_IMWRITE_PNG_BILEVEL               = 18,
            CV_IMWRITE_PNG_STRATEGY_DEFAULT      = 0,
            CV_IMWRITE_PNG_STRATEGY_FILTERED     = 1,
            CV_IMWRITE_PNG_STRATEGY_HUFFMAN_ONLY = 2,
            CV_IMWRITE_PNG_STRATEGY_RLE          = 3,
            CV_IMWRITE_PNG_STRATEGY_FIXED        = 4,
            CV_IMWRITE_PXM_BINARY                = 32;

    public static int cvSaveImage(String filename, CvArr image) { return cvSaveImage(filename, image, null); }
    public static native int cvSaveImage(String filename, CvArr image, int[] params/*=null*/);

    public static IplImage cvDecodeImage(CvMat buf) { return cvDecodeImage(buf, CV_LOAD_IMAGE_COLOR); }
    public static native IplImage cvDecodeImage(CvMat buf, int iscolor/*=CV_LOAD_IMAGE_COLOR*/);
    public static CvMat cvDecodeImageM(CvMat buf)   { return cvDecodeImageM(buf, CV_LOAD_IMAGE_COLOR); }
    public static native CvMat cvDecodeImageM(CvMat buf, int iscolor/*=CV_LOAD_IMAGE_COLOR*/);
    public static CvMat cvEncodeImage(String ext, CvArr image) { return cvEncodeImage(ext, image, null); }
    public static native CvMat cvEncodeImage(String ext, CvArr image, int[] params/*=null*/);

    public static final int
            CV_CVTIMG_FLIP    = 1,
            CV_CVTIMG_SWAP_RB = 2;

    public static native void cvConvertImage(CvArr src, CvArr dst, int flags/*=0*/);

    public static int cvWaitKey() { return cvWaitKey(0); }
    public static native int cvWaitKey(int delay/*=0*/);

    public static class CvOpenGLCallback extends FunctionPointer {
        static { load(); }
        public    CvOpenGLCallback(Pointer p) { super(p); }
        protected CvOpenGLCallback() { allocate(); }
        private native void allocate();
        public native void call(Pointer userdata);
    }
    public static native void cvSetOpenGlDrawCallback(String window_name,
            CvOpenGLCallback callbackOpenGL, Pointer userdata/*=null*/);
    public static native void cvSetOpenGlContext(String window_name);
    public static native void cvUpdateWindow(String window_name);


    @Opaque public static class CvCapture extends Pointer {
        static { load(); }
        public CvCapture() { }
        public CvCapture(Pointer p) { super(p); }
    }
    public static native CvCapture cvCreateFileCapture(String filename);

    public static final int
            CV_CAP_ANY     = 0,

            CV_CAP_MIL     = 100,

            CV_CAP_VFW     = 200,
            CV_CAP_V4L     = 200,
            CV_CAP_V4L2    = 200,

            CV_CAP_FIREWARE= 300,
            CV_CAP_FIREWIRE= 300,
            CV_CAP_IEEE1394= 300,
            CV_CAP_DC1394  = 300,
            CV_CAP_CMU1394 = 300,

            CV_CAP_STEREO  = 400,
            CV_CAP_TYZX    = 400,
            CV_TYZX_LEFT   = 400,
            CV_TYZX_RIGHT  = 401,
            CV_TYZX_COLOR  = 402,
            CV_TYZX_Z      = 403,

            CV_CAP_QT      = 500,

            CV_CAP_UNICAP  = 600,

            CV_CAP_DSHOW   = 700,
            CV_CAP_MSMF    = 1400,

            CV_CAP_PVAPI   = 800,

            CV_CAP_OPENNI  = 900,
            CV_CAP_OPENNI_ASUS  = 910,

            CV_CAP_ANDROID = 1000,
            CV_CAP_ANDROID_BACK  = CV_CAP_ANDROID+99,
            CV_CAP_ANDROID_FRONT = CV_CAP_ANDROID+98,

            CV_CAP_XIAPI   = 1100,

            CV_CAP_AVFOUNDATION = 1200,

            CV_CAP_GIGANETIX = 1300,

            CV_CAP_INTELPERC = 1500;

    public static native CvCapture cvCreateCameraCapture(int index);
    public static native int cvGrabFrame(CvCapture capture);
    public static IplImage cvRetrieveFrame(CvCapture capture) { return cvRetrieveFrame(capture, 0); }
    public static native IplImage cvRetrieveFrame(CvCapture capture, int streamIdx/*=0*/);
    public static native IplImage cvQueryFrame(CvCapture capture);
    public static native void cvReleaseCapture(@ByPtrPtr CvCapture capture);

    public static final int
            CV_CAP_PROP_DC1394_OFF         = -4,
            CV_CAP_PROP_DC1394_MODE_MANUAL = -3,
            CV_CAP_PROP_DC1394_MODE_AUTO = -2,
            CV_CAP_PROP_DC1394_MODE_ONE_PUSH_AUTO = -1,
            CV_CAP_PROP_POS_MSEC      =  0,
            CV_CAP_PROP_POS_FRAMES    =  1,
            CV_CAP_PROP_POS_AVI_RATIO =  2,
            CV_CAP_PROP_FRAME_WIDTH   =  3,
            CV_CAP_PROP_FRAME_HEIGHT  =  4,
            CV_CAP_PROP_FPS           =  5,
            CV_CAP_PROP_FOURCC        =  6,
            CV_CAP_PROP_FRAME_COUNT   =  7,
            CV_CAP_PROP_FORMAT        =  8,
            CV_CAP_PROP_MODE          =  9,
            CV_CAP_PROP_BRIGHTNESS    = 10,
            CV_CAP_PROP_CONTRAST      = 11,
            CV_CAP_PROP_SATURATION    = 12,
            CV_CAP_PROP_HUE           = 13,
            CV_CAP_PROP_GAIN          = 14,
            CV_CAP_PROP_EXPOSURE      = 15,
            CV_CAP_PROP_CONVERT_RGB   = 16,
            CV_CAP_PROP_WHITE_BALANCE_BLUE_U = 17,
            CV_CAP_PROP_RECTIFICATION = 18,
            CV_CAP_PROP_MONOCROME     = 19,
            CV_CAP_PROP_SHARPNESS     = 20,
            CV_CAP_PROP_AUTO_EXPOSURE = 21,
            CV_CAP_PROP_GAMMA         = 22,
            CV_CAP_PROP_TEMPERATURE   = 23,
            CV_CAP_PROP_TRIGGER       = 24,
            CV_CAP_PROP_TRIGGER_DELAY = 25,
            CV_CAP_PROP_WHITE_BALANCE_RED_V = 26,
            CV_CAP_PROP_ZOOM          =27,
            CV_CAP_PROP_FOCUS         =28,
            CV_CAP_PROP_GUID          =29,
            CV_CAP_PROP_ISO_SPEED     =30,
            CV_CAP_PROP_MAX_DC1394    =31,
            CV_CAP_PROP_BACKLIGHT     =32,
            CV_CAP_PROP_PAN           =33,
            CV_CAP_PROP_TILT          =34,
            CV_CAP_PROP_ROLL          =35,
            CV_CAP_PROP_IRIS          =36,
            CV_CAP_PROP_SETTINGS      =37,

            CV_CAP_PROP_AUTOGRAB      = 1024,
            CV_CAP_PROP_SUPPORTED_PREVIEW_SIZES_STRING=1025,
            CV_CAP_PROP_PREVIEW_FORMAT=1025,

            CV_CAP_OPENNI_DEPTH_GENERATOR = 1 << 31,
            CV_CAP_OPENNI_IMAGE_GENERATOR = 1 << 30,
            CV_CAP_OPENNI_GENERATORS_MASK = CV_CAP_OPENNI_DEPTH_GENERATOR + CV_CAP_OPENNI_IMAGE_GENERATOR,

            CV_CAP_PROP_OPENNI_OUTPUT_MODE      = 100,
            CV_CAP_PROP_OPENNI_FRAME_MAX_DEPTH  = 101,
            CV_CAP_PROP_OPENNI_BASELINE         = 102,
            CV_CAP_PROP_OPENNI_FOCAL_LENGTH     = 103,
            CV_CAP_PROP_OPENNI_REGISTRATION     = 104,
            CV_CAP_PROP_OPENNI_REGISTRATION_ON  = CV_CAP_PROP_OPENNI_REGISTRATION,
            CV_CAP_PROP_OPENNI_APPROX_FRAME_SYNC = 105,
            CV_CAP_PROP_OPENNI_MAX_BUFFER_SIZE   = 106,
            CV_CAP_PROP_OPENNI_CIRCLE_BUFFER     = 107,
            CV_CAP_PROP_OPENNI_MAX_TIME_DURATION = 108,
            CV_CAP_PROP_OPENNI_GENERATOR_PRESENT = 109,
            CV_CAP_OPENNI_IMAGE_GENERATOR_PRESENT         = CV_CAP_OPENNI_IMAGE_GENERATOR + CV_CAP_PROP_OPENNI_GENERATOR_PRESENT,
            CV_CAP_OPENNI_IMAGE_GENERATOR_OUTPUT_MODE     = CV_CAP_OPENNI_IMAGE_GENERATOR + CV_CAP_PROP_OPENNI_OUTPUT_MODE,
            CV_CAP_OPENNI_DEPTH_GENERATOR_BASELINE        = CV_CAP_OPENNI_DEPTH_GENERATOR + CV_CAP_PROP_OPENNI_BASELINE,
            CV_CAP_OPENNI_DEPTH_GENERATOR_FOCAL_LENGTH    = CV_CAP_OPENNI_DEPTH_GENERATOR + CV_CAP_PROP_OPENNI_FOCAL_LENGTH,
            CV_CAP_OPENNI_DEPTH_GENERATOR_REGISTRATION    = CV_CAP_OPENNI_DEPTH_GENERATOR + CV_CAP_PROP_OPENNI_REGISTRATION,
            CV_CAP_OPENNI_DEPTH_GENERATOR_REGISTRATION_ON = CV_CAP_OPENNI_DEPTH_GENERATOR_REGISTRATION,

            CV_CAP_GSTREAMER_QUEUE_LENGTH       = 200,
            CV_CAP_PROP_PVAPI_MULTICASTIP       = 300,

            CV_CAP_PROP_XI_DOWNSAMPLING  = 400,
            CV_CAP_PROP_XI_DATA_FORMAT   = 401,
            CV_CAP_PROP_XI_OFFSET_X      = 402,
            CV_CAP_PROP_XI_OFFSET_Y      = 403,
            CV_CAP_PROP_XI_TRG_SOURCE    = 404,
            CV_CAP_PROP_XI_TRG_SOFTWARE  = 405,
            CV_CAP_PROP_XI_GPI_SELECTOR  = 406,
            CV_CAP_PROP_XI_GPI_MODE      = 407,
            CV_CAP_PROP_XI_GPI_LEVEL     = 408,
            CV_CAP_PROP_XI_GPO_SELECTOR  = 409,
            CV_CAP_PROP_XI_GPO_MODE      = 410,
            CV_CAP_PROP_XI_LED_SELECTOR  = 411,
            CV_CAP_PROP_XI_LED_MODE      = 412,
            CV_CAP_PROP_XI_MANUAL_WB     = 413,
            CV_CAP_PROP_XI_AUTO_WB       = 414,
            CV_CAP_PROP_XI_AEAG          = 415,
            CV_CAP_PROP_XI_EXP_PRIORITY  = 416,
            CV_CAP_PROP_XI_AE_MAX_LIMIT  = 417,
            CV_CAP_PROP_XI_AG_MAX_LIMIT  = 418,
            CV_CAP_PROP_XI_AEAG_LEVEL    = 419,
            CV_CAP_PROP_XI_TIMEOUT       = 420,

            CV_CAP_PROP_ANDROID_FLASH_MODE = 8001,
            CV_CAP_PROP_ANDROID_FOCUS_MODE = 8002,
            CV_CAP_PROP_ANDROID_WHITE_BALANCE = 8003,
            CV_CAP_PROP_ANDROID_ANTIBANDING = 8004,
            CV_CAP_PROP_ANDROID_FOCAL_LENGTH = 8005,
            CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_NEAR = 8006,
            CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_OPTIMAL = 8007,
            CV_CAP_PROP_ANDROID_FOCUS_DISTANCE_FAR = 8008,

            CV_CAP_PROP_IOS_DEVICE_FOCUS = 9001,
            CV_CAP_PROP_IOS_DEVICE_EXPOSURE = 9002,
            CV_CAP_PROP_IOS_DEVICE_FLASH = 9003,
            CV_CAP_PROP_IOS_DEVICE_WHITEBALANCE = 9004,
            CV_CAP_PROP_IOS_DEVICE_TORCH = 9005,

            CV_CAP_PROP_GIGA_FRAME_OFFSET_X = 10001,
            CV_CAP_PROP_GIGA_FRAME_OFFSET_Y = 10002,
            CV_CAP_PROP_GIGA_FRAME_WIDTH_MAX = 10003,
            CV_CAP_PROP_GIGA_FRAME_HEIGH_MAX = 10004,
            CV_CAP_PROP_GIGA_FRAME_SENS_WIDTH = 10005,
            CV_CAP_PROP_GIGA_FRAME_SENS_HEIGH = 10006,

            CV_CAP_PROP_INTELPERC_PROFILE_COUNT               = 11001,
            CV_CAP_PROP_INTELPERC_PROFILE_IDX                 = 11002,
            CV_CAP_PROP_INTELPERC_DEPTH_LOW_CONFIDENCE_VALUE  = 11003,
            CV_CAP_PROP_INTELPERC_DEPTH_SATURATION_VALUE      = 11004,
            CV_CAP_PROP_INTELPERC_DEPTH_CONFIDENCE_THRESHOLD  = 11005,
            CV_CAP_PROP_INTELPERC_DEPTH_FOCAL_LENGTH_HORZ     = 11006,
            CV_CAP_PROP_INTELPERC_DEPTH_FOCAL_LENGTH_VERT     = 11007,

            CV_CAP_INTELPERC_DEPTH_GENERATOR = 1 << 29,
            CV_CAP_INTELPERC_IMAGE_GENERATOR = 1 << 28,
            CV_CAP_INTELPERC_GENERATORS_MASK = CV_CAP_INTELPERC_DEPTH_GENERATOR + CV_CAP_INTELPERC_IMAGE_GENERATOR,

            CV_CAP_OPENNI_DEPTH_MAP                 = 0,
            CV_CAP_OPENNI_POINT_CLOUD_MAP           = 1,
            CV_CAP_OPENNI_DISPARITY_MAP             = 2,
            CV_CAP_OPENNI_DISPARITY_MAP_32F         = 3,
            CV_CAP_OPENNI_VALID_DEPTH_MASK          = 4,

            CV_CAP_OPENNI_BGR_IMAGE                 = 5,
            CV_CAP_OPENNI_GRAY_IMAGE                = 6,

            CV_CAP_OPENNI_VGA_30HZ     = 0,
            CV_CAP_OPENNI_SXGA_15HZ    = 1,
            CV_CAP_OPENNI_SXGA_30HZ    = 2,
            CV_CAP_OPENNI_QVGA_30HZ    = 3,
            CV_CAP_OPENNI_QVGA_60HZ    = 4,

            CV_CAP_ANDROID_COLOR_FRAME_BGR = 0,
            CV_CAP_ANDROID_COLOR_FRAME = CV_CAP_ANDROID_COLOR_FRAME_BGR,
            CV_CAP_ANDROID_GREY_FRAME  = 1,
            CV_CAP_ANDROID_COLOR_FRAME_RGB = 2,
            CV_CAP_ANDROID_COLOR_FRAME_BGRA = 3,
            CV_CAP_ANDROID_COLOR_FRAME_RGBA = 4,

            CV_CAP_ANDROID_FLASH_MODE_AUTO = 0,
            CV_CAP_ANDROID_FLASH_MODE_OFF = 1,
            CV_CAP_ANDROID_FLASH_MODE_ON = 2,
            CV_CAP_ANDROID_FLASH_MODE_RED_EYE = 3,
            CV_CAP_ANDROID_FLASH_MODE_TORCH = 4,

            CV_CAP_ANDROID_FOCUS_MODE_AUTO = 0,
            CV_CAP_ANDROID_FOCUS_MODE_CONTINUOUS_VIDEO = 1,
            CV_CAP_ANDROID_FOCUS_MODE_EDOF = 2,
            CV_CAP_ANDROID_FOCUS_MODE_FIXED = 3,
            CV_CAP_ANDROID_FOCUS_MODE_INFINITY = 4,
            CV_CAP_ANDROID_FOCUS_MODE_MACRO = 5,

            CV_CAP_ANDROID_WHITE_BALANCE_AUTO = 0,
            CV_CAP_ANDROID_WHITE_BALANCE_CLOUDY_DAYLIGHT = 1,
            CV_CAP_ANDROID_WHITE_BALANCE_DAYLIGHT = 2,
            CV_CAP_ANDROID_WHITE_BALANCE_FLUORESCENT = 3,
            CV_CAP_ANDROID_WHITE_BALANCE_INCANDESCENT = 4,
            CV_CAP_ANDROID_WHITE_BALANCE_SHADE = 5,
            CV_CAP_ANDROID_WHITE_BALANCE_TWILIGHT = 6,
            CV_CAP_ANDROID_WHITE_BALANCE_WARM_FLUORESCENT = 7,

            CV_CAP_ANDROID_ANTIBANDING_50HZ = 0,
            CV_CAP_ANDROID_ANTIBANDING_60HZ = 1,
            CV_CAP_ANDROID_ANTIBANDING_AUTO = 2,
            CV_CAP_ANDROID_ANTIBANDING_OFF = 3,

            CV_CAP_INTELPERC_DEPTH_MAP              = 0,
            CV_CAP_INTELPERC_UVDEPTH_MAP            = 1,
            CV_CAP_INTELPERC_IR_MAP                 = 2,
            CV_CAP_INTELPERC_IMAGE                  = 3;

    public static native double cvGetCaptureProperty(CvCapture capture, int property_id);
    public static native int    cvSetCaptureProperty(CvCapture capture, int property_id, double value);
    public static native int    cvGetCaptureDomain(CvCapture capture);


    @Opaque public static class CvVideoWriter extends Pointer {
        static { load(); }
        public CvVideoWriter() { }
        public CvVideoWriter(Pointer p) { super(p); }
    }

    public static int CV_FOURCC(byte c1, byte c2, byte c3, byte c4) {
        return (c1 & 255) + ((c2 & 255) << 8) + ((c3 & 255) << 16) + ((c4 & 255) << 24);
    }
    public static int CV_FOURCC(char c1, char c2, char c3, char c4) {
        return CV_FOURCC((byte)c1, (byte)c2, (byte)c3, (byte)c4);
    }

    public static final int
        CV_FOURCC_PROMPT  = -1,
        CV_FOURCC_DEFAULT = CV_FOURCC('I', 'Y', 'U', 'V');

    public static native CvVideoWriter cvCreateVideoWriter(String filename, int fourcc,
            double fps, @ByVal CvSize frame_size, int is_color/*=1*/);
    public static native int cvWriteFrame(CvVideoWriter writer, IplImage image);
    public static native void cvReleaseVideoWriter(@ByPtrPtr CvVideoWriter writer);
}
