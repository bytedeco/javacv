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
 * WARNING: ARToolKitPlus itself is covered by the full GPLv2.
 * If your program uses this class, it will become bound to that license.
 */

package com.googlecode.javacv.cpp;

import java.nio.ByteBuffer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByPtrRef;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;

import static com.googlecode.javacpp.Loader.*;

/**
 *
 * @author Samuel Audet
 */
@Platform(define="LIBRPP_STATIC", include={"<ARToolKitPlus/template.h>", "<MemoryManager.cpp>",
    "<librpp/rpp.cpp>", "<librpp/rpp_quintic.cpp>", "<librpp/rpp_vecmat.cpp>", "<librpp/rpp_svd.cpp>",
    "<librpp/librpp.cpp>", "<extra/Profiler.cpp>"}, includepath={"/usr/include/malloc/", 
    "../ARToolKitPlus_2.1.1t/include/", "../ARToolKitPlus_2.1.1t/src/"}, options="fastfpu")
@Namespace("ARToolKitPlus")
public class ARToolKitPlus {
    static { load(); }

    public static native void createImagePatternBCH   (int nID, @Cast("ARToolKitPlus::ARUint8*") byte       dataPtr[/*8*8*/]);
    public static native void createImagePatternBCH   (int nID, @Cast("ARToolKitPlus::ARUint8*") ByteBuffer dataPtr/*[8*8]*/);
    public static native void createImagePatternSimple(int nID, @Cast("ARToolKitPlus::ARUint8*") byte       dataPtr[/*8*8*/]);
    public static native void createImagePatternSimple(int nID, @Cast("ARToolKitPlus::ARUint8*") ByteBuffer dataPtr/*[8*8]*/);

    // enum PIXEL_FORMAT
    public static final int
            PIXEL_FORMAT_ABGR = 1,
            PIXEL_FORMAT_BGRA = 2,
            PIXEL_FORMAT_BGR = 3,
            PIXEL_FORMAT_RGBA = 4,
            PIXEL_FORMAT_RGB = 5,
            PIXEL_FORMAT_RGB565 = 6,
            PIXEL_FORMAT_LUM = 7;

    // enum UNDIST_MODE
    public static final int
            UNDIST_NONE = 0,
            UNDIST_STD = 1,
            UNDIST_LUT = 2;

    // enum IMAGE_PROC_MODE
    public static final int
            IMAGE_HALF_RES = 0,
            IMAGE_FULL_RES = 1;

    // enum MARKER_MODE
    public static final int
            MARKER_TEMPLATE = 0,
            MARKER_ID_SIMPLE = 1,
            MARKER_ID_BCH = 2;

    // enum POSE_ESTIMATOR
    public static final int
            POSE_ESTIMATOR_ORIGINAL = 0,
            POSE_ESTIMATOR_ORIGINAL_CONT = 1,
            POSE_ESTIMATOR_RPP = 2;

    public static class ARMarkerInfo extends Pointer {
        static { load(); }
        public ARMarkerInfo() { allocate(); }
        public ARMarkerInfo(int size) { allocateArray(size); }
        public ARMarkerInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ARMarkerInfo position(int position) {
            return (ARMarkerInfo)super.position(position);
        }

        public native int    area(); public native ARMarkerInfo area(int area);
        public native int    id();   public native ARMarkerInfo id(int id);
        public native int    dir();  public native ARMarkerInfo dir(int dir);
        public native double cf();   public native ARMarkerInfo cf(double cf);
        // ARFloat           pos[2];
        @MemberGetter public native DoublePointer pos();
        // ARFloat           line[4][3];
        @MemberGetter public native @Cast("ARFloat(*)[3]") DoublePointer line();
        // ARFloat           vertex[4][2];
        @MemberGetter public native @Cast("ARFloat(*)[2]") DoublePointer vertex();
    }

    public static class ARMultiEachMarkerInfoT extends Pointer {
        static { load(); }
        public ARMultiEachMarkerInfoT() { allocate(); }
        public ARMultiEachMarkerInfoT(int size) { allocateArray(size); }
        public ARMultiEachMarkerInfoT(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ARMultiEachMarkerInfoT position(int position) {
            return (ARMultiEachMarkerInfoT)super.position(position);
        }

        public native int    patt_id(); public native ARMultiEachMarkerInfoT patt_id(int patt_id);
        public native double width();   public native ARMultiEachMarkerInfoT width(double width);

        // ARFloat           center[2];
        @MemberGetter public native DoublePointer center();
        // ARFloat           trans[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") DoublePointer trans();
        // ARFloat           itrans[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") DoublePointer itrans();
        // ARFloat           pos3d[4][3];
        @MemberGetter public native @Cast("ARFloat(*)[3]") DoublePointer pos3d();
        public native int    visible();  public native ARMultiEachMarkerInfoT visible(int visible);
        public native int    visibleR(); public native ARMultiEachMarkerInfoT visibleR(int visibleR);
    }

    public static class ARMultiMarkerInfoT extends Pointer {
        static { load(); }
        public ARMultiMarkerInfoT() { allocate(); }
        public ARMultiMarkerInfoT(int size) { allocateArray(size); }
        public ARMultiMarkerInfoT(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ARMultiMarkerInfoT position(int position) {
            return (ARMultiMarkerInfoT)super.position(position);
        }

        public native ARMultiEachMarkerInfoT  marker();     public native ARMultiMarkerInfoT marker(ARMultiEachMarkerInfoT marker);
        public native int                     marker_num(); public native ARMultiMarkerInfoT marker_num(int marker_num);
        // ARFloat                            trans[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") DoublePointer trans();
        public native int                     prevF();      public native ARMultiMarkerInfoT prevF(int prevF);
        // ARFloat                            transR[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") DoublePointer transR();
    }

    @Opaque public static class Logger extends Pointer {
        static { load(); }
        public Logger() { }
        public Logger(Pointer p) { super(p); }

        public native void artLog(String nStr);
    }

    @Opaque public static class Camera extends Pointer {
        public Camera() { }
        public Camera(Pointer p) { super(p); }
    }

    @Opaque public static class Profiler extends Pointer {
        public Profiler() { }
        public Profiler(Pointer p) { super(p); }
    }

    @Opaque public static class Tracker extends Pointer {
        static { load(); }
        public Tracker() { }
        public Tracker(Pointer p) { super(p); }

        public native boolean setPixelFormat(@Cast("ARToolKitPlus::PIXEL_FORMAT") int nFormat);
        public native boolean loadCameraFile(String nCamParamFile,
                double nNearClip, double nFarClip);
        public native void setLoadUndistLUT(boolean nSet);
        public native void setLogger(Logger nLogger);

        public native int arDetectMarker(@Cast("ARToolKitPlus::ARUint8*") byte[]      dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarker(@Cast("ARToolKitPlus::ARUint8*") ByteBuffer  dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarker(@Cast("ARToolKitPlus::ARUint8*") BytePointer dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarkerLite(@Cast("ARToolKitPlus::ARUint8*") byte[]      dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarkerLite(@Cast("ARToolKitPlus::ARUint8*") ByteBuffer  dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarkerLite(@Cast("ARToolKitPlus::ARUint8*") BytePointer dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native double arMultiGetTransMat(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);
        public native double arGetTransMat(ARMarkerInfo marker_info, double center[/*2*/],
                double width, @Cast("ARFloat(*)[4]") double conv[/*3][4*/]);
        public native double arGetTransMatCont(ARMarkerInfo marker_info,
                @Cast("ARFloat(*)[4]") double prev_conv[/*3][4*/],   double center[/*2*/],
                double width, @Cast("ARFloat(*)[4]") double conv[/*3][4*/]);
        public native double rppMultiGetTransMat(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);
        public native double rppGetTransMat(ARMarkerInfo marker_info,
                double center[/*2*/], double width, @Cast("ARFloat(*)[4]") double conv[/*3][4*/]);
        public native int arLoadPatt(@Cast("char*") String filename);
        public native int arFreePatt(int patno);
        public native int arMultiFreeConfig(ARMultiMarkerInfoT config);
        public native ARMultiMarkerInfoT arMultiReadConfigFile(String filename);
        public native void activateBinaryMarker(int nThreshold);

        public native void setMarkerMode(@Cast("ARToolKitPlus::MARKER_MODE") int nMarkerMode);
        public native void activateVignettingCompensation(boolean nEnable,
                int nCorners/*=0*/, int nLeftRight/*=0*/, int nTopBottom/*=0*/);
        public native void changeCameraSize(int nWidth, int nHeight);

        public native void setUndistortionMode(@Cast("ARToolKitPlus::UNDIST_MODE") int nMode);

        public native boolean setPoseEstimator(@Cast("ARToolKitPlus::POSE_ESTIMATOR") int nMethod);
        public native void setBorderWidth(double nFraction);
        public native void setThreshold(int nValue);
        public native int getThreshold();
        public native void activateAutoThreshold(boolean nEnable);
        public native boolean isAutoThresholdActivated();
        public native void setNumAutoThresholdRetries(int nNumRetries);

        public native void setImageProcessingMode(@Cast("ARToolKitPlus::IMAGE_PROC_MODE") int nMode);
        public native @Cast("const ARFloat*") DoublePointer getModelViewMatrix();
        public native @Cast("const ARFloat*") DoublePointer getProjectionMatrix();
        public native String getDescription();
        public native @Cast("ARToolKitPlus::PIXEL_FORMAT") int getPixelFormat();
        public native int getBitsPerPixel();
        public native int getNumLoadablePatterns();

        public native Camera getCamera();
        public native void setCamera(Camera nCamera);
        public native void setCamera(Camera nCamera, double nNearClip, double nFarClip);
        public native double calcOpenGLMatrixFromMarker(ARMarkerInfo nMarkerInfo,
                double nPatternCenter[/*2*/], double nPatternSize, double[] nOpenGLMatrix);

        public native @ByRef Profiler getProfiler();
        public native double executeSingleMarkerPoseEstimator(ARMarkerInfo marker_info, 
                double center[/*2*/], double width, @Cast("ARFloat(*)[4]") double conv[/*3][4*/]);
        public native double executeMultiMarkerPoseEstimator(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);
    }

    public static class ArtLogFunction extends FunctionPointer {
        static { load(); }
        public    ArtLogFunction(Pointer p) { super(p); }
        protected ArtLogFunction() { allocate(); }
        protected final native void allocate();
        public native void call(String nStr);
    }
    @Opaque public static class FunctionLogger extends Logger {
        static { load(); }
        public FunctionLogger(ArtLogFunction f) {  allocate(f); }
        public FunctionLogger(Pointer p) { super(p); }
        private native void allocate(ArtLogFunction f);
    }

    public static class SingleTracker extends Tracker {
        static { load(); }
        public SingleTracker(int width, int height) { allocate(width, height); }
        public SingleTracker(Pointer p) { super(p); }
        private native void allocate(int width, int height);

        public native void setLoggerFunction(ArtLogFunction f);
    }

    public static class MultiTracker extends Tracker {
        static { load(); }
        public MultiTracker(int width, int height) { allocate(width, height); }
        public MultiTracker(Pointer p) { super(p); }
        private native void allocate(int width, int height);

        public native void setLoggerFunction(ArtLogFunction f);

        public native boolean init(String nCamParamFile, String nMultiFile,
                double nNearClip, double nFarClip, Logger nLogger);
        public native int calc(@Cast("unsigned char*") byte[]      nImage);
        public native int calc(@Cast("unsigned char*") ByteBuffer  nImage);
        public native int calc(@Cast("unsigned char*") BytePointer nImage);
        public native int getNumDetectedMarkers();
        public native void setUseDetectLite(boolean nEnable);
        public native void getDetectedMarkers(@ByPtrRef IntPointer nMarkerIDs);
        public native @Cast("const ARToolKitPlus::ARMarkerInfo*") @ByRef ARMarkerInfo getDetectedMarker(int nWhich);
        public native @Cast("const ARToolKitPlus::ARMultiMarkerInfoT*") ARMultiMarkerInfoT getMultiMarkerConfig();
        public native void getARMatrix(@Cast("ARFloat(*)[4]") double nMatrix[/*3][4*/]);
    }
}
