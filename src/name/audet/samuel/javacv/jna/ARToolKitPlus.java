/*
 * Copyright (C) 2009 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.audet.samuel.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;

/**
 *
 * @author Samuel Audet
 */
public class ARToolKitPlus {
    public static final String libname;
    static {
        Native.register(libname = "ARToolKitPlus");
    }

    public static native void createImagePatternBCH(int nID, byte[] dataPtr/*[8*8]*/);
    public static native void createImagePatternSimple(int nID, byte[] dataPtr/*[8*8]*/);
    public static native void createImagePatternBCH(int nID, ByteBuffer dataPtr/*[8*8]*/);
    public static native void createImagePatternSimple(int nID, ByteBuffer dataPtr/*[8*8]*/);

    public static class Tracker extends PointerType { }
    public static class TrackerSingleMarker extends Tracker { }
    public static class TrackerMultiMarker extends Tracker { }

    public static native TrackerSingleMarker newTrackerSingleMarker(int width, int height);
    public static native TrackerMultiMarker newTrackerMultiMarker(int width, int height);
    public static native void deleteTracker(Tracker t);

    //enum PIXEL_FORMAT {
    public static final int
            PIXEL_FORMAT_ABGR = 1,
            PIXEL_FORMAT_BGRA = 2,
            PIXEL_FORMAT_BGR = 3,
            PIXEL_FORMAT_RGBA = 4,
            PIXEL_FORMAT_RGB = 5,
            PIXEL_FORMAT_RGB565 = 6,
            PIXEL_FORMAT_LUM = 7;
    public static native boolean setPixelFormat(Tracker t, 
            int /* PIXEL_FORMAT */ nFormat);
    public static native boolean loadCameraFile(Tracker t, String nCamParamFile,
            double nNearClip, double nFarClip);
    public static native void setLoadUndistLUT(Tracker t, boolean nSet);
    public static native void setLogger(Tracker t, Pointer /* Logger* */ nLogger);
    public static interface arLogFunc extends Callback {
        void callback(String nStr);
    }
    public static native void setLoggerFunc(Tracker t, arLogFunc f);
    public static native void setLoggerFunc(Tracker t, Function f);

    public static class ARMarkerInfo extends Structure {
        public ARMarkerInfo() { }
        public ARMarkerInfo(Pointer m) { useMemory(m); read(); }

        public int      area;
        public int      id;
        public int      dir;
        public double   cf;
        public double[] pos    = new double[2];
//        public double[][] line   = new double[4][3];
//        public double[][] vertex = new double[4][2];
        public double[] line   = new double[4*3];
        public double[] vertex = new double[4*2];

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(ARMarkerInfo p) {
                setStructure(p);
            }
            public ARMarkerInfo getStructure() {
                return new ARMarkerInfo(getValue());
            }
            public void getStructure(ARMarkerInfo p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(ARMarkerInfo p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class ARMultiEachMarkerInfoT extends Structure {
        public int      patt_id;
        public double   width;
        public double[] center = new double[2];
//        public double[][]  trans  = new double[3][4];
//        public double[][]  itrans = new double[3][4];
//        public double[][]  pos3d  = new double[3][4];
        public double[] trans  = new double[3*4];
        public double[] itrans = new double[3*4];
        public double[] pos3d  = new double[3*4];
        public int      visible;
        public int      visibleR;

        public static class ByReference extends ARMultiEachMarkerInfoT implements Structure.ByReference { }
    }

    public static class ARMultiMarkerInfoT extends Structure {
        public ARMultiEachMarkerInfoT.ByReference marker;
        public int                     marker_num;
//        public double[][]              trans = new double[3][4];
        public double[]                trans = new double[3*4];
        public int                     prevF;
//        public double[][]              transR = new double[3][4];
        public double[]                transR = new double[3*4];
    }

    public static native int arDetectMarker(Tracker t, byte[] dataPtr, int thresh,
            ARMarkerInfo.PointerByReference marker_info, IntByReference marker_num);
    public static native int arDetectMarkerLite(Tracker t, byte[] dataPtr, int thresh,
            ARMarkerInfo.PointerByReference marker_info, IntByReference marker_num);
    public static native int arDetectMarker(Tracker t, ByteBuffer dataPtr, int thresh,
            ARMarkerInfo.PointerByReference marker_info, IntByReference marker_num);
    public static native int arDetectMarkerLite(Tracker t, ByteBuffer dataPtr, int thresh,
            ARMarkerInfo.PointerByReference marker_info, IntByReference marker_num);
    public static native double arMultiGetTransMat(Tracker t, ARMarkerInfo marker_info,
            int marker_num, ARMultiMarkerInfoT config);
    public static native double arGetTransMat(Tracker t, ARMarkerInfo marker_info,
            double[] center/*[2]*/, double width, double[] conv/*[3][4]*/);
    public static native double arGetTransMatCont(Tracker t, ARMarkerInfo marker_info, 
            double[] prev_conv/*[3][4]*/,  double[] center/*[2]*/,
            double width, double[] conv/*[3][4]*/);
    public static native double rppMultiGetTransMat(Tracker t, ARMarkerInfo marker_info,
            int marker_num, ARMultiMarkerInfoT config);
    public static native double rppGetTransMat(Tracker t, ARMarkerInfo marker_info,
            double[] center/*[2]*/, double width, double[] conv/*[3][4]*/);
    public static native int arLoadPatt(Tracker t, String filename);
    public static native int arFreePatt(Tracker t, int patno);
    public static native int arMultiFreeConfig(Tracker t,  ARMultiMarkerInfoT config);
    public static native ARMultiMarkerInfoT arMultiReadConfigFile(Tracker t, String filename);
    public static native void activateBinaryMarker(Tracker t, int nThreshold);

    //enum MARKER_MODE
    public static final int
            MARKER_TEMPLATE = 0,
            MARKER_ID_SIMPLE = 1,
            MARKER_ID_BCH = 2;
    public static native void setMarkerMode(Tracker t, int /*MARKER_MODE*/ nMarkerMode);
    public static native void activateVignettingCompensation(Tracker t, boolean nEnable,
            int nCorners/*=0*/, int nLeftRight/*=0*/, int nTopBottom/*=0*/);
    public static native void changeCameraSize(Tracker t, int nWidth, int nHeight);

    //enum UNDIST_MODE {
    public static final int
            UNDIST_NONE = 0,
            UNDIST_STD = 1,
            UNDIST_LUT = 2;
    public static native void setUndistortionMode(Tracker t, int /*UNDIST_MODE*/ nMode);

    //enum POSE_ESTIMATOR {
    public static final int
            POSE_ESTIMATOR_ORIGINAL = 0,
            POSE_ESTIMATOR_ORIGINAL_CONT = 1,
            POSE_ESTIMATOR_RPP = 2;
    public static native boolean setPoseEstimator(Tracker t, 
            int /* POSE_ESTIMATOR */ nMethod);
    public static native void setBorderWidth(Tracker t, double nFraction);
    public static native void setThreshold(Tracker t, int nValue);
    public static native int getThreshold(Tracker t);
    public static native void activateAutoThreshold(Tracker t, boolean nEnable);
    public static native boolean isAutoThresholdActivated(Tracker t);
    public static native void setNumAutoThresholdRetries(Tracker t, int nNumRetries);

    //enum IMAGE_PROC_MODE {
    public static final int
            IMAGE_HALF_RES = 0,
            IMAGE_FULL_RES = 1;
    public static native void setImageProcessingMode(Tracker t, 
            int /* IMAGE_PROC_MODE */ nMode);
    public static native DoubleByReference getModelViewMatrix(Tracker t);
    public static native DoubleByReference getProjectionMatrix(Tracker t);
    public static native String getDescription(Tracker t);
    public static native int /*PIXEL_FORMAT*/ getPixelFormat(Tracker t);
    public static native int getBitsPerPixel(Tracker t);
    public static native int getNumLoadablePatterns(Tracker t);

    public static class Camera extends PointerType { }

    public static native Camera getCamera(Tracker t);
    public static native void setCamera(Tracker t, Camera nCamera);
    public static native void setCamera2(Tracker t, Camera nCamera,
            double nNearClip, double nFarClip);
    public static native double calcOpenGLMatrixFromMarker(Tracker t, ARMarkerInfo nMarkerInfo,
            double[] nPatternCenter/*[2]*/, double nPatternSize, double[] nOpenGLMatrix);

    public static class Profiler extends PointerType { }

    public static native Profiler getProfiler(Tracker t);
    public static native double executeSingleMarkerPoseEstimator(Tracker t,
            ARMarkerInfo marker_info, double[] center/*[2]*/, double width,
            double[] conv/*[3][4]*/);
    public static native double executeMultiMarkerPoseEstimator(Tracker t,
            ARMarkerInfo marker_info, int marker_num, ARMultiMarkerInfoT config);

    //
    // TrackerMultiMarker only
    //
    public static native boolean init(TrackerMultiMarker t, String nCamParamFile, 
            String nMultiFile, double nNearClip, double nFarClip,
            Pointer /* Logger* */ nLogger);
    public static native int calc(TrackerMultiMarker t, byte[] nImage);
    public static native int calc(TrackerMultiMarker t, ByteBuffer nImage);
    public static native int getNumDetectedMarkers(TrackerMultiMarker t);
    public static native void setUseDetectLite(TrackerMultiMarker t, boolean nEnable);
    public static native void getDetectedMarkers(TrackerMultiMarker t,
            PointerByReference /* int*& */ nMarkerIDs);
    public static native ARMarkerInfo getDetectedMarker(TrackerMultiMarker t, int nWhich);
    public static native ARMultiMarkerInfoT getMultiMarkerConfig(TrackerMultiMarker t);
    public static native void getARMatrix(TrackerMultiMarker t, double[] nMatrix/*[3][4]*/);
}
