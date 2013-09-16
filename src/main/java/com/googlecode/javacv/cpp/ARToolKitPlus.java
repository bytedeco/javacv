/*
 * Copyright (C) 2009,2010,2011,2012,2013 Samuel Audet
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
 * This file was derived from ARToolKitPlus.h, ar.h, arMulti.h, Camera.h, Tracker.h,
 * TrackerSingleMarker.h, and TrackerMultiMarker.h files from ARToolKitPlus 2.3.0,
 * which are covered by the following copyright notice:
 *
 * Copyright (C) 2010  ARToolkitPlus Authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Authors:
 *  Daniel Wagner
 *  Pavel Rojtberg
 *
 *
 * WARNING: ARToolKitPlus itself is covered by the full GPLv3.
 * If your program uses this class, it will become bound to that license.
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByPtrRef;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;
import com.googlecode.javacpp.annotation.StdString;
import com.googlecode.javacpp.annotation.StdVector;
import java.nio.ByteBuffer;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.ARToolKitPlus.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(include="ARToolKitPlus_plus.h", link="ARToolKitPlus"),
    @Platform(value="windows-x86", includepath="C:/Program Files (x86)/ARToolKitPlus/include/",
        linkpath="C:/Program Files (x86)/ARToolKitPlus/lib/"),
    @Platform(value="windows-x86_64", includepath="C:/Program Files/ARToolKitPlus/include/",
        linkpath="C:/Program Files/ARToolKitPlus/lib/") })
@Namespace("ARToolKitPlus")
public class ARToolKitPlus {
    static { load(); }

    public static native void createImagePatternBCH   (int nID, @Cast("uint8_t*") byte       dataPtr[/*8*8*/]);
    public static native void createImagePatternBCH   (int nID, @Cast("uint8_t*") ByteBuffer dataPtr/*[8*8]*/);
    public static native void createImagePatternSimple(int nID, @Cast("uint8_t*") byte       dataPtr[/*8*8*/]);
    public static native void createImagePatternSimple(int nID, @Cast("uint8_t*") ByteBuffer dataPtr/*[8*8]*/);

    public static final int
            ARTOOLKITPLUS_VERSION_MAJOR = 2,
            ARTOOLKITPLUS_VERSION_MINOR = 2;

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

    // enum HULL_TRACKING_MODE
    public static final int
            HULL_OFF = 0,
            HULL_FOUR = 1,
            HULL_FULL = 2;

    // enum ARTKP_VERSION
    public static final int
            VERSION_MAJOR = ARTOOLKITPLUS_VERSION_MAJOR,
            VERSION_MINOR = ARTOOLKITPLUS_VERSION_MINOR;

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

    @NoOffset public static class CornerPoint extends Pointer {
        static { load(); }
        public CornerPoint() { allocate(); }
        public CornerPoint(int nX, int nY) { allocate(nX, nY); }
        public CornerPoint(int size) { allocateArray(size); }
        public CornerPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int nX, int nY);
        private native void allocateArray(int size);

        public native short x(); public native CornerPoint x(short x);
        public native short y(); public native CornerPoint y(short y);
    }

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

        public native int   area(); public native ARMarkerInfo area(int area);
        public native int   id();   public native ARMarkerInfo id(int id);
        public native int   dir();  public native ARMarkerInfo dir(int dir);
        public native float cf();   public native ARMarkerInfo cf(float cf);
        // ARFloat           pos[2];
        @MemberGetter public native FloatPointer pos();
        // ARFloat           line[4][3];
        @MemberGetter public native @Cast("ARFloat(*)[3]") FloatPointer line();
        // ARFloat           vertex[4][2];
        @MemberGetter public native @Cast("ARFloat(*)[2]") FloatPointer vertex();
    }

    public static class ARMarkerInfo2 extends Pointer {
        static { load(); }
        public ARMarkerInfo2() { allocate(); }
        public ARMarkerInfo2(int size) { allocateArray(size); }
        public ARMarkerInfo2(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ARMarkerInfo position(int position) {
            return (ARMarkerInfo)super.position(position);
        }

        public native int area(); public native ARMarkerInfo2 area(int area);
        @MemberGetter public native FloatPointer pos();
        public native int coord_num(); public native ARMarkerInfo2 coord_num(int id);
        @MemberGetter public native IntPointer x_coord();
        @MemberGetter public native IntPointer y_coord();
        @MemberGetter public native IntPointer vertex();
    }

    public static class arPrevInfo extends Pointer {
        static { load(); }
        public arPrevInfo() { allocate(); }
        public arPrevInfo(int size) { allocateArray(size); }
        public arPrevInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ARMarkerInfo position(int position) {
            return (ARMarkerInfo)super.position(position);
        }

        @ByRef
        public native ARMarkerInfo marker(); public native arPrevInfo marker(ARMarkerInfo marker);
        public native int count(); public native arPrevInfo count(int count);
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

        public native int   patt_id(); public native ARMultiEachMarkerInfoT patt_id(int patt_id);
        public native float width();   public native ARMultiEachMarkerInfoT width(float width);

        // ARFloat           center[2];
        @MemberGetter public native FloatPointer center();
        // ARFloat           trans[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") FloatPointer trans();
        // ARFloat           itrans[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") FloatPointer itrans();
        // ARFloat           pos3d[4][3];
        @MemberGetter public native @Cast("ARFloat(*)[3]") FloatPointer pos3d();
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
        @MemberGetter public native @Cast("ARFloat(*)[4]") FloatPointer trans();
        public native int                     prevF();      public native ARMultiMarkerInfoT prevF(int prevF);
        // ARFloat                            transR[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") FloatPointer transR();
    }

    @NoOffset public static class Camera extends Pointer {
        static { load(); }
        public Camera() { }
        public Camera(Pointer p) { super(p); }

        public native int xsize(); public native Camera xsize(int xsize);
        public native int ysize(); public native Camera ysize(int ysize);
        // ARFloat mat[3][4];
        @MemberGetter public native @Cast("ARFloat(*)[4]") FloatPointer mat();
        // ARFloat kc[6];
        @MemberGetter public native FloatPointer kc();

        public native void observ2Ideal(float ox, float oy, float[] ix, float[] iy);
        public native void ideal2Observ(float ix, float iy, float[] ox, float[] oy);
        public native boolean loadFromFile(@StdString String filename);
        public native Camera clone();
        public native boolean changeFrameSize(int frameWidth, int frameHeight);
        public native void printSettings();
        public native @StdString String getFileName();
    }

    public static class Tracker extends Pointer {
        static { load(); }
        public Tracker() { }
        public Tracker(Pointer p) { super(p); }

        public native boolean setPixelFormat(@Cast("ARToolKitPlus::PIXEL_FORMAT") int nFormat);
        public native boolean loadCameraFile(String nCamParamFile, float nNearClip, float nFarClip);
        public native void setLoadUndistLUT(boolean nSet);

        public native int arDetectMarker(@Cast("uint8_t*") byte[]      dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarker(@Cast("uint8_t*") ByteBuffer  dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarker(@Cast("uint8_t*") BytePointer dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarkerLite(@Cast("uint8_t*") byte[]      dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarkerLite(@Cast("uint8_t*") ByteBuffer  dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native int arDetectMarkerLite(@Cast("uint8_t*") BytePointer dataPtr, int thresh,
                @ByPtrPtr ARMarkerInfo marker_info, int[] marker_num);
        public native float arMultiGetTransMat(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);
        public native float arMultiGetTransMatHull(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);
        public native float arGetTransMat(ARMarkerInfo marker_info, float center[/*2*/],
                float width, @Cast("ARFloat(*)[4]") float conv[/*3][4*/]);
        public native float arGetTransMatCont(ARMarkerInfo marker_info,
                @Cast("ARFloat(*)[4]") float prev_conv[/*3][4*/],   float center[/*2*/],
                float width, @Cast("ARFloat(*)[4]") float conv[/*3][4*/]);
        public native float rppMultiGetTransMat(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);
        public native float rppGetTransMat(ARMarkerInfo marker_info,
                float center[/*2*/], float width, @Cast("ARFloat(*)[4]") float conv[/*3][4*/]);
        public native int arLoadPatt(@Cast("char*") String filename);
        public native int arFreePatt(int patno);
        public native int arMultiFreeConfig(ARMultiMarkerInfoT config);
        public native ARMultiMarkerInfoT arMultiReadConfigFile(String filename);
        public native void activateBinaryMarker(int nThreshold);

        public native void setMarkerMode(@Cast("ARToolKitPlus::MARKER_MODE") int nMarkerMode);
        public native void activateVignettingCompensation(boolean nEnable,
                int nCorners/*=0*/, int nLeftRight/*=0*/, int nTopBottom/*=0*/);
        public static native boolean calcCameraMatrix(String nCamParamFile,
                float nNear, float nFar, float[] nMatrix);
        public native void changeCameraSize(int nWidth, int nHeight);

        public native void setUndistortionMode(@Cast("ARToolKitPlus::UNDIST_MODE") int nMode);

        public native boolean setPoseEstimator(@Cast("ARToolKitPlus::POSE_ESTIMATOR") int nMethod);
        public native void setHullMode(@Cast("ARToolKitPlus::HULL_TRACKING_MODE") int nMode);
        public native void setBorderWidth(float nFraction);
        public native void setThreshold(int nValue);
        public native int getThreshold();
        public native void activateAutoThreshold(boolean nEnable);
        public native boolean isAutoThresholdActivated();
        public native void setNumAutoThresholdRetries(int nNumRetries);

        public native void setImageProcessingMode(@Cast("ARToolKitPlus::IMAGE_PROC_MODE") int nMode);
        public native @Const FloatPointer getModelViewMatrix();
        public native @Const FloatPointer getProjectionMatrix();
        public native @Cast("ARToolKitPlus::PIXEL_FORMAT") int getPixelFormat();
        public native int getBitsPerPixel();
        public native int getNumLoadablePatterns();

        public native Camera getCamera();
        public native void setCamera(Camera nCamera);
        public native void setCamera(Camera nCamera, float nNearClip, float nFarClip);
        public native float calcOpenGLMatrixFromMarker(ARMarkerInfo nMarkerInfo,
                float nPatternCenter[/*2*/], float nPatternSize, float[] nOpenGLMatrix);

        public native float executeSingleMarkerPoseEstimator(ARMarkerInfo marker_info,
                float center[/*2*/], float width, @Cast("ARFloat(*)[4]") float conv[/*3][4*/]);
        public native float executeMultiMarkerPoseEstimator(ARMarkerInfo marker_info,
                int marker_num, ARMultiMarkerInfoT config);

        public native @Const @StdVector CornerPoint getTrackedCorners();
    }

    public static class SingleTracker extends TrackerSingleMarker {
        static { load(); }
        public SingleTracker(int width, int height) { super(null); allocate(width, height); }
        public SingleTracker(Pointer p) { super(p); }
        private native void allocate(int width, int height);
    }

    public static class MultiTracker extends TrackerMultiMarker {
        static { load(); }
        public MultiTracker(int width, int height) { super(null); allocate(width, height); }
        public MultiTracker(Pointer p) { super(p); }
        private native void allocate(int width, int height);
    }

    public static class TrackerSingleMarker extends Tracker {
        static { load(); }
        public TrackerSingleMarker(int width, int height, int maxImagePatterns/*=8*/, int pattWidth/*=6*/,
                int pattHeight/*=6*/, int pattSamples/*=6*/, int maxLoadPatterns/*=0*/) {
            allocate(width, height, maxImagePatterns, pattWidth, pattHeight, pattSamples, maxLoadPatterns);
        }
        public TrackerSingleMarker(Pointer p) { super(p); }
        private native void allocate(int width, int height, int maxImagePatterns/*=8*/, int pattWidth/*=6*/,
                int pattHeight/*=6*/, int pattSamples/*=6*/, int maxLoadPatterns/*=0*/);

        public native boolean init(String nCamParamFile, float nNearClip, float nFarClip);
        public native int addPattern(String nFileName);
        public native @StdVector int[] calc(@Cast("uint8_t*") int[] nImage,
                @ByPtrPtr ARMarkerInfo nMarker_info/*=null*/, int[] nNumMarkers/*=null*/);
        public native void selectDetectedMarker(int id);
        public native int selectBestMarkerByCf();
        public native void setPatternWidth(float nWidth);
        public native void getARMatrix(@Cast("ARFloat(*)[4]") float nMatrix[/*3][4*/]);
        public native float getConfidence();
    }

    public static class TrackerMultiMarker extends Tracker {
        static { load(); }
        public TrackerMultiMarker(int width, int height, int maxImagePatterns/*=8*/, int pattWidth/*=6*/,
                int pattHeight/*=6*/, int pattSamples/*=6*/, int maxLoadPatterns/*=0*/) {
            allocate(width, height, maxImagePatterns, pattWidth, pattHeight, pattSamples, maxLoadPatterns);
        }
        public TrackerMultiMarker(Pointer p) { super(p); }
        private native void allocate(int width, int height, int maxImagePatterns/*=8*/, int pattWidth/*=6*/,
                int pattHeight/*=6*/, int pattSamples/*=6*/, int maxLoadPatterns/*=0*/);

        public native boolean init(String nCamParamFile, String nMultiFile,
                float nNearClip, float nFarClip);
        public native int calc(@Cast("uint8_t*") byte[]      nImage);
        public native int calc(@Cast("uint8_t*") ByteBuffer  nImage);
        public native int calc(@Cast("uint8_t*") BytePointer nImage);
        public native int getNumDetectedMarkers();
        public native void setUseDetectLite(boolean nEnable);
        public native void getDetectedMarkers(@ByPtrRef IntPointer nMarkerIDs);
        public native @Const @ByRef ARMarkerInfo getDetectedMarker(int nWhich);
        public native @Const ARMultiMarkerInfoT getMultiMarkerConfig();
        public native void getARMatrix(@Cast("ARFloat(*)[4]") float nMatrix[/*3][4*/]);
    }
}
