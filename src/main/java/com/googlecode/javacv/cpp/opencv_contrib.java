/*
 * Copyright (C) 2011,2012 Samuel Audet
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
 * This file is based on information found in contrib.hpp, retina.hpp,
 * detection_based_tracker.hpp, and hybrid_tracker.hpp of OpenCV 2.4.0,
 * which are covered by the following copyright notice:
 *
 *                           License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
 * Copyright (C) 2009-2011, Willow Garage Inc., all rights reserved.
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

import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Index;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;
import com.googlecode.javacpp.annotation.ValueGetter;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/contrib/contrib.hpp>", "<opencv2/contrib/detection_based_tracker.hpp>",
                 "<opencv2/contrib/hybridtracker.hpp>", "opencv_adapters.h"},
        link={"opencv_contrib@.2.4", "opencv_ml@.2.4", "opencv_video@.2.4", "opencv_objdetect@.2.4", "opencv_calib3d@.2.4",
              "opencv_features2d@.2.4", "opencv_flann@.2.4", "opencv_highgui@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_contrib240", "opencv_ml240", "opencv_video240", "opencv_objdetect240", "opencv_calib3d240",
              "opencv_features2d240", "opencv_flann240", "opencv_highgui240", "opencv_imgproc240", "opencv_core240"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_contrib {
    static { load(opencv_calib3d.class); load(opencv_objdetect.class); load(opencv_video.class); load(opencv_ml.class); load(); }

    public static class CvAdaptiveSkinDetector extends Pointer {
        static { load(); }
        public CvAdaptiveSkinDetector() { allocate(); }
        public CvAdaptiveSkinDetector(int samplingDivider/*=1*/, int morphingMethod/*=MORPHING_METHOD_NONE*/) {
            allocate(samplingDivider, morphingMethod);
        }
        public CvAdaptiveSkinDetector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int samplingDivider/*=1*/, int morphingMethod/*=MORPHING_METHOD_NONE*/);

        public static final int
                MORPHING_METHOD_NONE = 0,
                MORPHING_METHOD_ERODE = 1,
                MORPHING_METHOD_ERODE_ERODE = 2,
                MORPHING_METHOD_ERODE_DILATE = 3;

        public native void process(IplImage inputBGRImage, IplImage outputHueMask);

//        protected native void initData(IplImage src, int widthDivider, int heightDivider);
//        protected native void adaptiveFilter();
    }


    @NoOffset public static class CvFuzzyPoint extends Pointer {
        static { load(); }
        public CvFuzzyPoint() { }
        public CvFuzzyPoint(double _x, double _y) { allocate(_x, _y); }
        public CvFuzzyPoint(Pointer p) { super(p); }
        private native void allocate(double _x, double _y);

        public native double x();     public native CvFuzzyPoint x(double x);
        public native double y();     public native CvFuzzyPoint y(double y);
        public native double value(); public native CvFuzzyPoint value(double value);
    }

    public static class CvFuzzyCurve extends Pointer {
        static { load(); }
        public CvFuzzyCurve() { allocate(); }
        public CvFuzzyCurve(int size) { allocateArray(size); }
        public CvFuzzyCurve(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFuzzyCurve position(int position) {
            return (CvFuzzyCurve)super.position(position);
        }

        public native void setCentre(double _centre);
        public native double getCentre();
        public native void clear();
        public native void addPoint(double x, double y);
        public native double calcValue(double param);
        public native double getValue();
        public native void setValue(double _value);
    }

    public static class CvFuzzyFunction extends Pointer {
        static { load(); }
        public CvFuzzyFunction() { allocate(); }
        public CvFuzzyFunction(int size) { allocateArray(size); }
        public CvFuzzyFunction(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFuzzyFunction position(int position) {
            return (CvFuzzyFunction)super.position(position);
        }

        public native void addCurve(CvFuzzyCurve curve, double value/*=0*/);
        public native void resetValues();
        public native double calcValue();
        public native CvFuzzyCurve newCurve();

        @NoOffset @Adapter("VectorAdapter<CvFuzzyCurve>")
        public native CvFuzzyCurve curves(); public native CvFuzzyFunction curves(CvFuzzyCurve curves);
    }

    public static class CvFuzzyRule extends Pointer {
        static { load(); }
        public CvFuzzyRule() { allocate(); }
        public CvFuzzyRule(int size) { allocateArray(size); }
        public CvFuzzyRule(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFuzzyRule position(int position) {
            return (CvFuzzyRule)super.position(position);
        }

        public native void setRule(CvFuzzyCurve c1, CvFuzzyCurve c2, CvFuzzyCurve o1);
        public native double calcValue(double param1, double param2);
        public native CvFuzzyCurve getOutputCurve();
    }

    public static class CvFuzzyController extends Pointer {
        static { load(); }
        public CvFuzzyController() { allocate(); }
        public CvFuzzyController(int size) { allocateArray(size); }
        public CvFuzzyController(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFuzzyController position(int position) {
            return (CvFuzzyController)super.position(position);
        }

        public native void addRule(CvFuzzyCurve c1, CvFuzzyCurve c2, CvFuzzyCurve o1);
        public native double calcOutput(double param1, double param2);
    }

    @NoOffset public static class CvFuzzyMeanShiftTracker extends Pointer {
        static { load(); }
        public CvFuzzyMeanShiftTracker() { allocate(); }
        public CvFuzzyMeanShiftTracker(int size) { allocateArray(size); }
        public CvFuzzyMeanShiftTracker(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFuzzyMeanShiftTracker position(int position) {
            return (CvFuzzyMeanShiftTracker)super.position(position);
        }

        public static final int 
        //enum TrackingState
                tsNone          = 0,
                tsSearching     = 1,
                tsTracking      = 2,
                tsSetWindow     = 3,
                tsDisabled      = 10,

        //enum ResizeMethod {
                rmEdgeDensityLinear     = 0,
                rmEdgeDensityFuzzy      = 1,
                rmInnerDensity          = 2,

                MinKernelMass           = 1000;

        //@Cast("SearchWindow")
        @MemberGetter public native @ByRef Pointer kernel(); // public native CvFuzzyMeanShiftTracker kernel(Pointer kernel);
        public native int searchMode(); public native CvFuzzyMeanShiftTracker searchMode(int searchMode);

        public native void track(IplImage maskImage, IplImage depthMap, int resizeMethod,
                @Cast("bool") boolean resetSearch, int minKernelMass/*=MinKernelMass*/);
    }


    @Namespace("cv") public static class Octree extends Pointer {
        static { load(); }
        @NoOffset public static class Node extends Pointer {
            static { load(); }
            public Node() { allocate(); }
            public Node(int size) { allocateArray(size); }
            public Node(Pointer p) { super(p); }
            private native void allocate();
            private native void allocateArray(int size);

            @Override public Node position(int position) {
                return (Node)super.position(position);
            }

            public native int begin();                public native Node begin(int begin);
            public native int end();                  public native Node end(int end);
            public native float x_min();              public native Node x_min(float x_min);
            public native float x_max();              public native Node x_max(float x_max);
            public native float y_min();              public native Node y_min(float y_min);
            public native float y_max();              public native Node y_max(float y_max);
            public native float z_min();              public native Node z_min(float z_min);
            public native float z_max();              public native Node z_max(float z_max);
            public native int maxLevels();            public native Node maxLevels(int maxLevels);
            @Cast("bool")
            public native boolean isLeaf();           public native Node isLeaf(boolean isLeaf);
            public native int/*[8]*/ children(int i); public native Node children(int i, int children);
        }
        public Octree() { allocate(); }
        public Octree(CvPoint3D32f points, int maxLevels/*=10*/, int minPoints/*=20*/) {
            allocate(points, maxLevels, minPoints);
        }
        public Octree(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Adapter("VectorAdapter<CvPoint3D32f,cv::Point3f>")
                CvPoint3D32f points, int maxLevels/*=10*/, int minPoints/*=20*/);

        public native void buildTree(@Adapter("VectorAdapter<CvPoint3D32f,cv::Point3f>")
                CvPoint3D32f points, int maxLevels/*=10*/, int minPoints/*=20*/);
        public native void getPointsWithinSphere(@ByVal CvPoint3D32f center, float radius,
                @Adapter(value="VectorAdapter<CvPoint3D32f,cv::Point3f>", out=true) CvPoint3D32f points);
        public native @Adapter("VectorAdapter<cv::Octree::Node>") Node getNodes();
    }

    @NoOffset @Namespace("cv") public static class Mesh3D extends Pointer {
        static { load(); }
        public Mesh3D() { allocate(); }
        public Mesh3D(@ByVal CvPoint3D32f vtx) { allocate(vtx); }
        public Mesh3D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Adapter("VectorAdapter<CvPoint3D32f,cv::Point3f>") CvPoint3D32f vtx);

        @Opaque public static class EmptyMeshException extends Pointer {
            static { load(); }
            public EmptyMeshException() { }
            public EmptyMeshException(Pointer p) { super(p); }
        }

        public native void buildOctree();
        public native void clearOctree();
        public native float estimateResolution(float tryRatio/*=0.1f*/);
        public native void computeNormals(float normalRadius, int minNeighbors/*=20*/);
        public native void computeNormals(@Adapter("VectorAdapter<int>") int[] subset, float normalRadius, int minNeighbors/*=20*/);

        public native void writeAsVrml(String file, @Adapter("VectorAdapter<CvScalar, cv::Scalar>") CvScalar colors/*=null*/);

        @Adapter("VectorAdapter<CvPoint3D32f,cv::Point3f>")
        public native CvPoint3D32f vtx();     public native Mesh3D vtx(CvPoint3D32f vtx);
        @Adapter("VectorAdapter<CvPoint3D32f,cv::Point3f>")
        public native CvPoint3D32f normals(); public native Mesh3D normals(CvPoint3D32f normals);
        public native float resolution();     public native Mesh3D resolution(float resolution);
        public native @ByRef Octree octree(); public native Mesh3D octree(Octree octree);

//        @MemberGetter public static native @ByVal CvPoint3D32f allzero();
    }

    @Name("std::vector<std::vector<cv::Vec2i> >")
    public static class Vec2iVectorVector extends Pointer {
        static { load(); }
        public Vec2iVectorVector()       { allocate();  }
        public Vec2iVectorVector(long n) { allocate(n); }
        public Vec2iVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index(1) long size(@Cast("size_t") long i);
        public native @Index(1) void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ValueGetter @ByVal public native CvScalar get(@Cast("size_t") long i, @Cast("size_t") long j);
        //public native Vec2iVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, CvScalar value);
    }

    @NoOffset @Namespace("cv") public static class SpinImageModel extends Pointer {
        static { load(); }
        public SpinImageModel() { allocate(); }
        public SpinImageModel(Mesh3D mesh) { allocate(mesh); }
        public SpinImageModel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByRef Mesh3D mesh);

        public native float normalRadius();             public native SpinImageModel normalRadius(float normalRadius);
        public native int minNeighbors();               public native SpinImageModel minNeighbors(int minNeighbors);

        public native float binSize();                  public native SpinImageModel binSize(float binSize);
        public native int imageWidth();                 public native SpinImageModel imageWidth(int imageWidth);

        public native float lambda();                   public native SpinImageModel lambda(float lambda);
        public native float gamma();                    public native SpinImageModel gamma(float gamma);

        public native float T_GeometriccConsistency();  public native SpinImageModel T_GeometriccConsistency(float T_GeometriccConsistency);
        public native float T_GroupingCorespondances(); public native SpinImageModel T_GroupingCorespondances(float T_GroupingCorespondances);

        public native void setLogger(@Cast("std::ostream*") Pointer log);
        public native void selectRandomSubset(float ratio);
        public native void setSubset(@Adapter("VectorAdapter<int>") int[] subset);
        public native void compute();

        public native void match(@ByRef SpinImageModel scene, @ByRef Vec2iVectorVector result);

        public native @Adapter("MatAdapter") IplImage packRandomScaledSpins(@Cast("bool") boolean separateScale/*=false*/, @Cast("size_t") long xCount/*=10*/, @Cast("size_t") long yCount/*=10*/);

        public native long getSpinCount();
        public native @Adapter("MatAdapter") IplImage getSpinImage(@Cast("size_t") long index);
        public native @ByVal CvPoint3D32f getSpinVertex(@Cast("size_t") long index);
        public native @ByVal CvPoint3D32f getSpinNormal(@Cast("size_t") long index);

        public native @ByRef Mesh3D getMesh();

        public native static boolean spinCorrelation(IplImage spin1, IplImage spin2, float lambda, @ByRef float[] result);

//        public native static @ByVal CvPoint2D32f calcSpinMapCoo(@ByVal CvPoint3D32f point, @ByVal CvPoint3D32f vertex, @ByVal CvPoint3D32f normal);
//
//        public native static float geometricConsistency(@ByVal CvPoint3D32f pointScene1, @ByVal CvPoint3D32f normalScene1,
//                @ByVal CvPoint3D32f pointModel1, @ByVal CvPoint3D32f normalModel1,
//                @ByVal CvPoint3D32f pointScene2, @ByVal CvPoint3D32f normalScene2,
//                @ByVal CvPoint3D32f pointModel2, @ByVal CvPoint3D32f normalModel2);
//
//        public native static float groupingCreteria(@ByVal CvPoint3D32f pointScene1, @ByVal CvPoint3D32f normalScene1,
//                @ByVal CvPoint3D32f pointModel1, @ByVal CvPoint3D32f normalModel1,
//                @ByVal CvPoint3D32f pointScene2, @ByVal CvPoint3D32f normalScene2,
//                @ByVal CvPoint3D32f pointModel2, @ByVal CvPoint3D32f normalModel2, float gamma);
//
//        protected native void defaultParams();
//
//        protected native void matchSpinToModel(IplImage spin,
//                @Adapter(value="VectorAdapter<int>",out=true) IntPointer indeces,
//                @Adapter(value="VectorAdapter<float>",out=true) FloatPointer corrCoeffs,
//                @Cast("bool") boolean useExtremeOutliers/*=true*/);
//
//        protected native void repackSpinImages(@Adapter(value="VectorAdapter<uchar>",out=true)
//                @Cast("uchar*") BytePointer mask, IplImage spinImages, @Cast("bool") boolean reAlloc/*=true*/);
//
//        protected native vector<int> subset;
//        protected native Mesh3D mesh;
//        protected native Mat spinImages;
//        protected native @Cast("std::ostream*") Pointer out;
    }

    @Namespace("cv") public static class TickMeter extends Pointer {
        static { load(); }
        public TickMeter() { allocate(); }
        public TickMeter(Pointer p) { super(p); }
        private native void allocate();

        public native void start();
        public native void stop();

        public native long getTimeTicks();
        public native double getTimeMicro();
        public native double getTimeMilli();
        public native double getTimeSec();
        public native long getCounter();

        public native void reset();
    }

//    public static native @ByRef std::ostream operator<<(@ByRef std::ostream out, @ByRef TickMeter tm);


    @NoOffset @Namespace("cv") public static class SelfSimDescriptor extends Pointer {
        static { load(); }
        public SelfSimDescriptor() { allocate(); }
        public SelfSimDescriptor(int _ssize, int _lsize,
                int _startDistanceBucket/*=DEFAULT_START_DISTANCE_BUCKET*/,
                int _numberOfDistanceBuckets/*=DEFAULT_NUM_DISTANCE_BUCKETS*/,
                int _nangles/*=DEFAULT_NUM_ANGLES*/) {
            allocate(_ssize, _lsize, _startDistanceBucket, _numberOfDistanceBuckets, _nangles);
        }
        public SelfSimDescriptor(@ByRef SelfSimDescriptor ss) { allocate(ss); }
        public SelfSimDescriptor(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _ssize, int _lsize,
                int _startDistanceBucket/*=DEFAULT_START_DISTANCE_BUCKET*/,
                int _numberOfDistanceBuckets/*=DEFAULT_NUM_DISTANCE_BUCKETS*/,
                int _nangles/*=DEFAULT_NUM_ANGLES*/);
        private native void allocate(@ByRef SelfSimDescriptor ss);

        public native @Name("operator=") @ByRef SelfSimDescriptor copy(@ByRef SelfSimDescriptor ss);

        public native long getDescriptorSize();
        public native @ByVal CvSize getGridSize(@ByVal CvSize imgsize, @ByVal CvSize winStride);

        public native void compute(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<float>", out=true) FloatPointer descriptors,
                @ByVal CvSize winStride/*=Size()*/, @Adapter("VectorAdapter<CvPoint,cv::Point>") CvPoint locations/*=null*/);
        public native void computeLogPolarMapping(@Adapter("MatAdapter") CvArr mappingMask);
        public native void SSD(@Adapter("MatAdapter") CvArr img, @ByVal CvPoint pt, @Adapter("MatAdapter") CvArr ssd);

        public native int smallSize();               public native SelfSimDescriptor smallSize(int smallSize);
        public native int largeSize();               public native SelfSimDescriptor largeSize(int largeSize);
        public native int startDistanceBucket();     public native SelfSimDescriptor startDistanceBucket(int startDistanceBucket);
        public native int numberOfDistanceBuckets(); public native SelfSimDescriptor numberOfDistanceBuckets(int numberOfDistanceBuckets);
        public native int numberOfAngles();          public native SelfSimDescriptor numberOfAngles(int numberOfAngles);

        public static final int DEFAULT_SMALL_SIZE = 5, DEFAULT_LARGE_SIZE = 41,
                DEFAULT_NUM_ANGLES = 20, DEFAULT_START_DISTANCE_BUCKET = 3,
                DEFAULT_NUM_DISTANCE_BUCKETS = 7;
    }


//    public static class Fjac extends FunctionPointer {
//        static { load(); }
//        public    Fjac(Pointer p) { super(p); }
//        protected Fjac() { allocate(); }
//        protected final native void allocate();
//        public native void call(int i, int j, @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat point_params,
//                @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat cam_params,
//                @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat A,
//                @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat B, Pointer data);
//    }
//
//    public static class Func extends FunctionPointer {
//        static { load(); }
//        public    Func(Pointer p) { super(p); }
//        protected Func() { allocate(); }
//        protected final native void allocate();
//        public native void call(int i, int j, @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat point_params,
//                @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat cam_params,
//                @Cast("cv::Mat&") @Adapter("MatAdapter") CvMat estim, Pointer data);
//    }
//
//    public static class BundleAdjustCallback extends FunctionPointer {
//        static { load(); }
//        public    BundleAdjustCallback(Pointer p) { super(p); }
//        protected BundleAdjustCallback() { allocate(); }
//        protected final native void allocate();
//        public native @Cast("bool") boolean call(int iteration, double norm_error, Pointer user_data);
//    }
//
//    @NoOffset @Namespace("cv") public static class LevMarqSparse extends Pointer {
//        static { load(); }
//        public LevMarqSparse() { allocate(); }
//        public LevMarqSparse(int npoints, int ncameras, int nPointParams, int nCameraParams, int nErrParams,
//                @Adapter("MatAdapter") CvMat visibility, @Adapter("MatAdapter") CvMat P0,
//                @Adapter("MatAdapter") CvMat X, @ByVal CvTermCriteria criteria, Fjac fjac, Func func, Pointer data,
//                BundleAdjustCallback cb, Pointer user_data) {
//            allocate(npoints, ncameras, nPointParams, nCameraParams, nErrParams, visibility, P0, X, criteria, fjac, func, data, cb, user_data);
//        }
//        public LevMarqSparse(Pointer p) { super(p); }
//        private native void allocate();
//        private native void allocate(int npoints, int ncameras, int nPointParams, int nCameraParams, int nErrParams,
//                @Adapter("MatAdapter") CvMat visibility, @Adapter("MatAdapter") CvMat P0,
//                @Adapter("MatAdapter") CvMat X, @ByVal CvTermCriteria criteria, Fjac fjac, Func func, Pointer data,
//                BundleAdjustCallback cb, Pointer user_data);
//
//        public native void run(int npoints, int ncameras, int nPointParams, int nCameraParams, int nErrParams,
//                @Adapter("MatAdapter") CvMat visibility, @Adapter("MatAdapter") CvMat P0,
//                @Adapter("MatAdapter") CvMat X, @ByVal CvTermCriteria criteria, Fjac fjac, Func func, Pointer data);
//
//        public native void clear();
//
//        // useful function to do simple bundle adjastment tasks
//        public static native void bundleAdjust(@Adapter("VectorAdapter<CvPoint3D32f,cv::Point3d>") CvPoint3D32f points,
//                @ByRef Point2dVectorVector imagePoints, @ByRef IntVectorVector visibility,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray cameraMatrix,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray R,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray T,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray distCoeffs,
//                @ByVal CvTermCriteria criteria/*=TermCriteria(TermCriteria::COUNT+TermCriteria::EPS, 30, DBL_EPSILON)*/,
//                BundleAdjustCallback cb/*=null*/, Pointer user_data/*=null*/);
//
//        public native void optimize(@ByRef CvMat _vis);
//
//        public native void ask_for_proj(@ByRef CvMat _vis, @Cast("bool") boolean once/*=false*/);
//        public native void ask_for_projac(@ByRef CvMat _vis);
//
//        public native CvMat err();               public native LevMarqSparse err(CvMat err);
//        public native double prevErrNorm();      public native LevMarqSparse prevErrNorm(double prevErrNorm);
//        public native double errNorm();          public native LevMarqSparse errNorm(double errNorm);
//        public native double lambda();           public native LevMarqSparse lambda(double lambda);
//        @ByRef
//        public native CvTermCriteria criteria(); public native LevMarqSparse criteria(CvTermCriteria criteria);
//        public native int iters();               public native LevMarqSparse iters(int iters);
//
//        public native CvMatArray U();            public native LevMarqSparse U(CvMatArray U);
//        public native CvMatArray V();            public native LevMarqSparse V(CvMatArray V);
//        public native CvMatArray inv_V_star();   public native LevMarqSparse inv_V_star(CvMatArray inv_V_star);
//
//        public native CvMatArray A();            public native LevMarqSparse A(CvMatArray A);
//        public native CvMatArray B();            public native LevMarqSparse B(CvMatArray B);
//        public native CvMatArray W();            public native LevMarqSparse W(CvMatArray W);
//
//        public native CvMat X();                 public native LevMarqSparse X(CvMat X);
//        public native CvMat hX();                public native LevMarqSparse hX(CvMat hX);
//
//        public native CvMat prevP();             public native LevMarqSparse prevP(CvMat prevP);
//        public native CvMat P();                 public native LevMarqSparse P(CvMat P);
//
//        public native CvMat deltaP();            public native LevMarqSparse deltaP(CvMat deltaP);
//
//        public native CvMatArray ea();           public native LevMarqSparse ea(CvMatArray ea);
//        public native CvMatArray eb();           public native LevMarqSparse eb(CvMatArray eb);
//
//        public native CvMatArray Yj();           public native LevMarqSparse Yj(CvMatArray Yj);
//        public native CvMat S();                 public native LevMarqSparse S(CvMat S);
//        public native CvMat JtJ_diag();          public native LevMarqSparse JtJ_diag(CvMat JtJ_diag);
//        public native CvMat Vis_index();         public native LevMarqSparse Vis_index(CvMat Vis_index);
//
//        public native int num_cams();            public native LevMarqSparse num_cams(int num_cams);
//        public native int num_points();          public native LevMarqSparse num_points(int num_points);
//        public native int num_err_param();       public native LevMarqSparse num_err_param(int num_err_param);
//        public native int num_cam_param();       public native LevMarqSparse num_cam_param(int num_cam_param);
//        public native int num_point_param();     public native LevMarqSparse num_point_param(int cnum_point_paramb);
//
//        public native Fjac fjac();               public native LevMarqSparse fjac(Fjac fjac);
//        public native Func func();               public native LevMarqSparse func(Func func);
//
//        public native Pointer data();            public native LevMarqSparse data(Pointer data);
//
//        public native BundleAdjustCallback cb(); public native LevMarqSparse cb(BundleAdjustCallback cb);
//        public native Pointer user_data();       public native LevMarqSparse user_data(Pointer user_data);
//    }

    @Namespace("cv") public static native int chamerMatching(@Adapter("MatAdapter") CvArr img,
            @Adapter("MatAdapter") CvArr templ, @ByRef PointVectorVector results,
            @Adapter(value="VectorAdapter<float>", out=true) FloatPointer cost,
            double templScale/*=1*/, int maxMatches/*=20*/, double minMatchDistance/*=1.0*/, int padX/*=3*/,
            int padY/*=3*/, int scales/*=5*/, double minScale/*=0.6*/, double maxScale/*=1.6*/,
            double orientationWeight/*=0.5*/, double truncate/*=20*/);


    @NoOffset @Namespace("cv") public static class StereoVar extends Pointer {
        static { load(); }
        public StereoVar() { allocate(); }
        public StereoVar(int levels, double pyrScale, int nIt, int minDisp, int maxDisp, int poly_n,
                double poly_sigma, float fi, float lambda, int penalization, int cycle, int flags) {
            allocate(levels, pyrScale, nIt, minDisp, maxDisp, poly_n, poly_sigma, fi, lambda, penalization, cycle, flags);
        }
        public StereoVar(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int levels, double pyrScale, int nIt, int minDisp, int maxDisp, int poly_n,
                double poly_sigma, float fi, float lambda, int penalization, int cycle, int flags);

        public static final int
                USE_INITIAL_DISPARITY = 1, USE_EQUALIZE_HIST = 2, USE_SMART_ID = 4, USE_AUTO_PARAMS = 8, USE_MEDIAN_FILTERING = 16,
                CYCLE_O = 0, CYCLE_V = 1,
                PENALIZATION_TICHONOV = 0, PENALIZATION_CHARBONNIER = 1, PENALIZATION_PERONA_MALIK = 2;

        public native @Name("operator()") void compute(@Adapter("MatAdapter") CvArr left,
                @Adapter("MatAdapter") CvArr right, @Adapter("MatAdapter") CvArr disp);

        public native int levels();        public native StereoVar levels(int levels);
        public native double pyrScale();   public native StereoVar pyrScale(double pyrScale);
        public native int nIt();           public native StereoVar nIt(int nIt);
        public native int minDisp();       public native StereoVar minDisp(int minDisp);
        public native int maxDisp();       public native StereoVar maxDisp(int maxDisp);
        public native int poly_n();        public native StereoVar poly_n(int poly_n);
        public native double poly_sigma(); public native StereoVar poly_sigma(double poly_sigma);
        public native float fi();          public native StereoVar fi(float fi);
        public native float lambda();      public native StereoVar lambda(float lambda);
        public native int penalization();  public native StereoVar penalization(int penalization);
        public native int cycle();         public native StereoVar cycle(int cycle);
        public native int flags();         public native StereoVar flags(int flags);
    }

    @Namespace("cv") public static native void polyfit(CvMat srcx, CvMat srcy, @Adapter("MatAdapter") CvMat dst, int order);


    @Namespace("cv") public static class Directory extends Pointer {
        static { load(); }
        public Directory() { allocate(); }
        public Directory(Pointer p) { super(p); }
        private native void allocate();

        public static native @ByVal StringVector GetListFiles  (String path, String exten/*="*"*/, boolean addPath/*=true*/);
        public static native @ByVal StringVector GetListFilesR (String path, String exten/*="*"*/, boolean addPath/*=true*/);
        public static native @ByVal StringVector GetListFolders(String path, String exten/*="*"*/, boolean addPath/*=true*/);
    }

    @Namespace("cv") public static native  void generateColors(@Adapter("VectorAdapter<CvScalar,cv::Scalar>") CvScalar colors,
            long count, long factor/*=100*/);


    public static final int
            ROTATION          = 1,
            TRANSLATION       = 2,
            RIGID_BODY_MOTION = 4;
    @Namespace("cv") public static native boolean RGBDOdometry(@Adapter("MatAdapter") CvMat Rt, CvMat initRt,
            IplImage image0, IplImage depth0, IplImage mask0,
            IplImage image1, IplImage depth1, IplImage mask1,
            CvMat cameraMatrix, float minDepth/*=0*/, float maxDepth/*=4*/, float maxDepthDiff/*=0.07*/,
            @Adapter("VectorAdapter<int>") IntPointer iterCounts/*=null*/,
            @Adapter("VectorAdapter<float>") FloatPointer minGradientMagnitudes/*=null*/,
            int transformType/*=RIGID_BODY_MOTION*/);

    @Namespace("cv") public static class LogPolar_Interp extends Pointer {
        static { load(); }
        public LogPolar_Interp() { allocate(); }
        public LogPolar_Interp(int w, int h, @ByVal CvPoint center, int R/*=70*/, double ro0/*=3.0*/,
                int interp/*=INTER_LINEAR*/, int full/*=1*/, int S/*=117*/, int sp/*=1*/) {
            allocate(w, h, center, R, ro0, interp, full, S, sp);
        }
        public LogPolar_Interp(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int w, int h, @ByVal CvPoint center, int R/*=70*/, double ro0/*=3.0*/,
                int interp/*=INTER_LINEAR*/, int full/*=1*/, int S/*=117*/, int sp/*=1*/);

        public native @Adapter("MatAdapter") CvMat to_cortical(CvMat source);
        public native @Adapter("MatAdapter") CvMat to_cartesian(CvMat source);

//        protected native @Adapter("MatAdapter") CvMat Rsri();
//        protected native @Adapter("MatAdapter") CvMat Csri();
//
//        protected native int S();
//        protected native int R();
//        protected native int M();
//        protected native int N();
//        protected native int top();
//        protected native int bottom();
//        protected native int left();
//        protected native int right();
//        protected native double ro0();
//        protected native double romax();
//        protected native double a();
//        protected native double q();
//        protected native int interp();
//
//        protected native @Adapter("MatAdapter") CvMat ETAyx();
//        protected native @Adapter("MatAdapter") CvMat CSIyx();
//
//        protected native void create_map(int M, int N, int R, int S, double ro0);
    }

    @Namespace("cv") public static class LogPolar_Overlapping extends Pointer {
        static { load(); }
        public LogPolar_Overlapping() { allocate(); }
        public LogPolar_Overlapping(int w, int h, @ByVal CvPoint center, int R/*=70*/,
                double ro0/*=3.0*/, int full/*=1*/, int S/*=117*/, int sp/*=1*/) {
            allocate(w, h, center, R, ro0, full, S, sp);
        }
        public LogPolar_Overlapping(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int w, int h, @ByVal CvPoint center, int R/*=70*/,
                double ro0/*=3.0*/, int full/*=1*/, int S/*=117*/, int sp/*=1*/);

        public native @Adapter("MatAdapter") CvMat to_cortical(CvMat source);
        public native @Adapter("MatAdapter") CvMat to_cartesian(CvMat source);

//        protected native @Adapter("MatAdapter") CvMat Rsri();
//        protected native @Adapter("MatAdapter") CvMat Csri();
//        protected native @Adapter("VectorAdapter<int>") IntPointer Rsr();
//        protected native @Adapter("VectorAdapter<int>") IntPointer Csr();
//        protected native @Adapter("VectorAdapter<double>") DoublePointer Wsr();

//        protected native int S();
//        protected native int R();
//        protected native int M();
//        protected native int N();
//        protected native int ind1();
//        protected native int top();
//        protected native int bottom();
//        protected native int left();
//        protected native int right();
//        protected native double ro0();
//        protected native double romax();
//        protected native double a();
//        protected native double q();
//
//        protected static class kernel extends Pointer {
//            static { load(); }
//            public kernel() { allocate(); }
//            public kernel(Pointer p) { super(p); }
//            private native void allocate();
//
//            public native @Adapter("VectorAdapter<double>") DoublePointer weights();
//            public native int w();
//        }
//
//        protected native @Adapter("MatAdapter") CvMat ETAyx();
//        protected native @Adapter("MatAdapter") CvMat CSIyx();
//        protected native @Adapter("VectorAdapter<kernel>") kernel w_ker_2D();
//
//        protected native void create_map(int M, int N, int R, int S, double ro0);
    }

    /**
    * Adjacent receptive fields technique
    *
    *All the Cartesian pixels, whose coordinates in the cortical domain share the same integer part, are assigned to the same RF.
    *The precision of the boundaries of the RF can be improved by breaking each pixel into subpixels and assigning each of them to the correct RF.
    *This technique is implemented from: Traver, V., Pla, F.: Log-polar mapping template design: From task-level requirements
    *to geometry parameters. Image Vision Comput. 26(10) (2008) 1354-1370
    *
    *More details can be found in http://dx.doi.org/10.1007/978-3-642-23968-7_5
    */
    @Namespace("cv") public static class LogPolar_Adjacent extends Pointer {
        static { load(); }
        public LogPolar_Adjacent() { allocate(); }
        public LogPolar_Adjacent(int w, int h, @ByVal CvPoint center, int R/*=70*/, double ro0/*=3.0*/,
                double smin/*=0.25*/, int full/*=1*/, int S/*=117*/, int sp/*=1*/) {
            allocate(w, h, center, R, ro0, smin, full, S, sp);
        }
        public LogPolar_Adjacent(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int w, int h, @ByVal CvPoint center, int R/*=70*/, double ro0/*=3.0*/,
                double smin/*=0.25*/, int full/*=1*/, int S/*=117*/, int sp/*=1*/);

        public native @Adapter("MatAdapter") CvMat to_cortical(CvMat source);
        public native @Adapter("MatAdapter") CvMat to_cartesian(CvMat source);

//        protected static class pixel extends Pointer {
//            static { load(); }
//            public pixel() { allocate(); }
//            public pixel(Pointer p) { super(p); }
//            private native void allocate();
//
//            public native int u();
//            public native int v();
//            public native double a();
//        }
//        protected native int S();
//        protected native int R();
//        protected native int M();
//        protected native int N();
//        protected native int top();
//        protected native int bottom();
//        protected native int left();
//        protected native int right();
//        protected native double ro0();
//        protected native double romax();
//        protected native double a();
//        protected native double q();
//        protected native vector<vector<pixel> > L();
//        protected native @Adapter("VectorAdapter<double>") DoublePointer A();
//
//        protected native void subdivide_recursively(double x, double y, int i, int j, double length, double smin);
//        protected native boolean get_uv(double x, double y, @ByRef int[] u, @ByRef int[] v);
//        protected native void create_map(int M, int N, int R, int S, double ro0, double smin);
    }

    @Namespace("cv") public static native @Adapter("MatAdapter") CvMat subspaceProject(@Adapter("ArrayAdapter") CvArr W,
            @Adapter("ArrayAdapter") CvArr mean, @Adapter("ArrayAdapter") CvArr src);
    @Namespace("cv") public static native @Adapter("MatAdapter") CvMat subspaceReconstruct(@Adapter("ArrayAdapter") CvArr W,
            @Adapter("ArrayAdapter") CvArr mean, @Adapter("ArrayAdapter") CvArr src);

    @Namespace("cv") public static class LDA extends Pointer {
        static { Loader.load(); }
        public LDA() { allocate(); }
        public LDA(int num_components) { allocate(num_components); }
        public LDA(CvMat src, @Adapter("VectorAdapter<int>") int[] labels, int num_components/*=0*/) {
            allocate(src, labels, num_components);
        }
        public LDA(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr labels, int num_components/*=0*/) {
            allocate(src, labels, num_components);
        }
        public LDA(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int num_components);
        private native void allocate(CvMat src, @Adapter("VectorAdapter<int>") int[] labels, int num_components/*=0*/);
        private native void allocate(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr labels, int num_components/*=0*/);

        public native void save(String filename);
        public native void load(String filename);
        public native void save(@Adapter("FileStorageAdapter") CvFileStorage fs);
        public native void load(@Adapter("FileStorageAdapter") CvFileStorage fs);

        public native void compute(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr labels);
        public native @Adapter("MatAdapter") CvMat project(@Adapter("ArrayAdapter") CvArr src);
        public native @Adapter("MatAdapter") CvMat reconstruct(@Adapter("ArrayAdapter") CvArr src);
        public native @Adapter("MatAdapter") CvMat eigenvectors();
        public native @Adapter("MatAdapter") CvMat eigenvalues();

//        protected native boolean _dataAsRow();
//        protected native int _num_components();
//        protected native @Adapter("MatAdapter") CvMat _eigenvectors();
//        protected native @Adapter("MatAdapter") CvMat _eigenvalues();
//
//        protected native void lda(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr labels);
    }

    @Namespace("cv") public static class FaceRecognizer extends Pointer {
        static { Loader.load(); }
        public FaceRecognizer() { }
        public FaceRecognizer(Pointer p) { super(p); }

        public /*abstract*/ native void train(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr labels);
        public /*abstract*/ native int predict(@Adapter("ArrayAdapter") CvArr src);
        public native void save(String filename);
        public native void load(String filename);
        public native void save(@Adapter("FileStorageAdapter") CvFileStorage fs);
        public native void load(@Adapter("FileStorageAdapter") CvFileStorage fs);
        public native @Adapter("MatAdapter") CvMat eigenvectors();
    }
    @Name("cv::Ptr<cv::FaceRecognizer>")
    public static class FaceRecognizerPtr extends Pointer {
        static { load(); }
        public FaceRecognizerPtr()       { allocate();  }
        public FaceRecognizerPtr(Pointer p) { super(p); }
        private native void allocate();

        public native FaceRecognizer get();
        public native FaceRecognizerPtr put(FaceRecognizer value);
    }

    @Namespace("cv") public static native @ByVal FaceRecognizerPtr createEigenFaceRecognizer(int num_components/*=0*/);
    @Namespace("cv") public static native @ByVal FaceRecognizerPtr createFisherFaceRecognizer(int num_components/*=0*/);
    @Namespace("cv") public static native @ByVal FaceRecognizerPtr createLBPHFaceRecognizer(int radius/*=1*/,
            int neighbors/*=8*/, int grid_x/*=8*/, int grid_y/*=8*/);

    public static final int
        COLORMAP_AUTUMN = 0,
        COLORMAP_BONE = 1,
        COLORMAP_JET = 2,
        COLORMAP_WINTER = 3,
        COLORMAP_RAINBOW = 4,
        COLORMAP_OCEAN = 5,
        COLORMAP_SUMMER = 6,
        COLORMAP_SPRING = 7,
        COLORMAP_COOL = 8,
        COLORMAP_HSV = 9,
        COLORMAP_PINK = 10,
        COLORMAP_HOT = 11,
        COLORMAP_MKPJ1 = 12,
        COLORMAP_MKPJ2 = 13;

    @Namespace("cv") public static native void applyColorMap(@Adapter("ArrayAdapter") CvArr src,
            @Adapter(value="ArrayAdapter") CvArr dst, int colormap);


    // enum RETINA_COLORSAMPLINGMETHOD
    public static final int
        RETINA_COLOR_RANDOM = 0,
        RETINA_COLOR_DIAGONAL = 1,
        RETINA_COLOR_BAYER = 2;

    @Namespace("cv") public static class Retina extends Pointer {
        static { load(); }
        public Retina(@ByVal CvSize inputSize) {
            allocate(inputSize);
        }
        public Retina(@ByVal CvSize inputSize,
                boolean colorMode, @Cast("cv::RETINA_COLORSAMPLINGMETHOD") int colorSamplingMethod/*=RETINA_COLOR_BAYER*/,
                boolean useRetinaLogSampling/*=false*/, double reductionFactor/*=1.0*/, double samplingStrenght/*=10.0*/) {
            allocate(inputSize, colorMode, colorSamplingMethod, useRetinaLogSampling, reductionFactor, samplingStrenght);
        }
        public Retina(Pointer p) { super(p); }
        private native void allocate(@ByVal CvSize inputSize);
        private native void allocate(@ByVal CvSize inputSize,
                @Cast("bool") boolean colorMode, @Cast("cv::RETINA_COLORSAMPLINGMETHOD") int colorSamplingMethod/*=RETINA_COLOR_BAYER*/,
                @Cast("bool") boolean useRetinaLogSampling/*=false*/, double reductionFactor/*=1.0*/, double samplingStrenght/*=10.0*/);

        @NoOffset public static class RetinaParameters extends Pointer {
            static { load(); }
            public RetinaParameters()       { allocate();  }
            public RetinaParameters(Pointer p) { super(p); }
            private native void allocate();

            @NoOffset public static class OPLandIplParvoParameters  extends Pointer {
                static { load(); }
                public OPLandIplParvoParameters()       { allocate();  }
                public OPLandIplParvoParameters(Pointer p) { super(p); }
                private native void allocate();

                public native boolean colorMode();                              public native OPLandIplParvoParameters colorMode(boolean colorMode);
                public native boolean normaliseOutput();                        public native OPLandIplParvoParameters normaliseOutput(boolean normaliseOutput);
                public native float photoreceptorsLocalAdaptationSensitivity(); public native OPLandIplParvoParameters photoreceptorsLocalAdaptationSensitivity(float photoreceptorsLocalAdaptationSensitivity);
                public native float photoreceptorsTemporalConstant();           public native OPLandIplParvoParameters photoreceptorsTemporalConstant(float photoreceptorsTemporalConstant);
                public native float photoreceptorsSpatialConstant();            public native OPLandIplParvoParameters photoreceptorsSpatialConstant(float photoreceptorsSpatialConstant);
                public native float horizontalCellsGain();                      public native OPLandIplParvoParameters horizontalCellsGain(float horizontalCellsGain);
                public native float hcellsTemporalConstant();                   public native OPLandIplParvoParameters hcellsTemporalConstant(float hcellsTemporalConstant);
                public native float hcellsSpatialConstant();                    public native OPLandIplParvoParameters hcellsSpatialConstant(float hcellsSpatialConstant);
                public native float ganglionCellsSensitivity();                 public native OPLandIplParvoParameters ganglionCellsSensitivity(float ganglionCellsSensitivity);
           }
           @NoOffset public static class IplMagnoParameters extends Pointer {
                static { load(); }
                public IplMagnoParameters()       { allocate();  }
                public IplMagnoParameters(Pointer p) { super(p); }
                private native void allocate();

                public native boolean normaliseOutput();                public native IplMagnoParameters normaliseOutput(boolean normaliseOutput);
                public native float parasolCells_beta();                public native IplMagnoParameters parasolCells_beta(float parasolCells_beta);
                public native float parasolCells_tau();                 public native IplMagnoParameters parasolCells_tau(float parasolCells_tau);
                public native float parasolCells_k();                   public native IplMagnoParameters parasolCells_k(float parasolCells_k);
                public native float amacrinCellsTemporalCutFrequency(); public native IplMagnoParameters amacrinCellsTemporalCutFrequency(float amacrinCellsTemporalCutFrequency);
                public native float V0CompressionParameter();           public native IplMagnoParameters V0CompressionParameter(float V0CompressionParameter);
                public native float localAdaptintegration_tau();        public native IplMagnoParameters localAdaptintegration_tau(float localAdaptintegration_tau);
                public native float localAdaptintegration_k();          public native IplMagnoParameters localAdaptintegration_k(float localAdaptintegration_k);
            }
            public native @MemberGetter @ByRef OPLandIplParvoParameters OPLandIplParvo();
            public native @MemberGetter @ByRef IplMagnoParameters IplMagno();
        }

        public native @ByVal CvSize inputSize();
        public native @ByVal CvSize outputSize();

        public native void setup(String retinaParameterFile/*=""*/, @Cast("bool") boolean applyDefaultSetupOnFailure/*=true*/);
        public native void setup(@Adapter("FileStorageAdapter") CvFileStorage fs, @Cast("bool") boolean applyDefaultSetupOnFailure/*=true*/);
        public native void setup(@ByVal RetinaParameters newParameters);
        public native @ByVal RetinaParameters getParameters();
        public native @ByRef String printSetup();
        public native void write(String fs);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
        public native void setupOPLandIPLParvoChannel(@Cast("bool") boolean colorMode/*=true*/, @Cast("bool") boolean normaliseOutput/*=true*/,
                double photoreceptorsLocalAdaptationSensitivity/*=0.7*/, double photoreceptorsTemporalConstant/*=0.5*/,
                double photoreceptorsSpatialConstant/*=0.53*/, double horizontalCellsGain/*=0*/,
                double HcellsTemporalConstant/*=1*/, double HcellsSpatialConstant/*=7*/, double ganglionCellsSensitivity/*=0.7*/);
        public native void setupIPLMagnoChannel(@Cast("bool") boolean normaliseOutput/*=true*/, double parasolCells_beta/*=0*/,
                double parasolCells_tau/*=0*/, double parasolCells_k/*=7*/, double amacrinCellsTemporalCutFrequency/*=1.2*/,
                double V0CompressionParameter/*=0.95*/, double localAdaptintegration_tau/*=0*/, double localAdaptintegration_k/*=7*/);
        public native void run(@Adapter("MatAdapter") CvArr inputImage);
        public native void getParvo(@Adapter("MatAdapter") CvArr retinaOutput_parvo);
        //public native void getParvo(@ByRef std::valarray<float> retinaOutput_parvo);
        public native void getMagno(@Adapter("MatAdapter") CvArr retinaOutput_magno);
        //public native void getMagno(@ByRef std::valarray<float> retinaOutput_magno);
        //public native @Const @ByRef std::valarray<float> getMagno();
        //public native @Const @ByRef std::valarray<float> getParvo();
        public native void setColorSaturation(@Cast("bool") boolean saturateColors/*=true*/, float colorSaturationValue/*=4.0*/);
        public native void clearBuffers();
        public native void activateMovingContoursProcessing(@Cast("bool") boolean activate);
        public native void activateContoursProcessing(@Cast("bool") boolean activate);

//        protected native RetinaParameters _retinaParameters();
//        protected native std::valarray<float> _inputBuffer();
//        protected native RetinaFilter _retinaFilter();
//        protected native void _convertValarrayGrayBuffer2cvMat(@ByRef std::valarray<float> grayMatrixToConvert,
//                int nbRows, int nbColumns, @Cast("bool") boolean colorMode, @Adapter("MatAdapter") CvArr outBuffer);
//        protected native @Cast("bool") boolean _convertCvMat2ValarrayBuffer(CvMat inputMatToConvert, @ByRef std::valarray<float> outputValarrayMatrix);
//        protected native void _init(String parametersSaveFile, @ByVal CvSize inputSize, @Cast("bool") boolean colorMode,
//                @Cast("cv::RETINA_COLORSAMPLINGMETHOD") int colorSamplingMethod/*=RETINA_COLOR_BAYER*/,
//                @Cast("bool") boolean useRetinaLogSampling/*=false*/, double reductionFactor/*=1.0*/, double samplingStrenght/*=10.0*/);
    }


    @Platform({"linux", "macosx", "android"})
    public static class DetectionBasedTracker extends Pointer {
        static { load(); }
        public DetectionBasedTracker(String cascadeFilename, @ByRef Parameters params) {
            allocate(cascadeFilename, params);
        }
        public DetectionBasedTracker(Pointer p) { super(p); }
        private native void allocate(String cascadeFilename, @ByRef Parameters params);

        @NoOffset public static class Parameters extends Pointer {
            static { load(); }
            public Parameters()       { allocate();  }
            public Parameters(Pointer p) { super(p); }
            private native void allocate();

            public native int minObjectSize();      public native Parameters minObjectSize(int minObjectSize);
            public native int maxObjectSize();      public native Parameters maxObjectSize(int maxObjectSize);
            public native double scaleFactor();     public native Parameters scaleFactor(double scaleFactor);
            public native int maxTrackLifetime();   public native Parameters maxTrackLifetime(int maxTrackLifetime);
            public native int minNeighbors();       public native Parameters minNeighbors(int minNeighbors);
            public native int minDetectionPeriod(); public native Parameters minDetectionPeriod(int minDetectionPeriod);
        }

        public native boolean run();
        public native void stop();
        public native void resetTracking();

        public native void process(IplImage imageGray);

        public native boolean setParameters(@ByRef Parameters params);
        public native @Const @ByRef Parameters getParameters();

        public native void getObjects(@Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect result);
        //public native void getObjects(@ByRef std::vector<std::pair<cv::Rect, int> > result);

//        @Opaque protected class SeparateDetectionWorkPtr extends Pointer { };
//        protected native @ByVal SeparateDetectionWorkPtr separateDetectionWork();
//
//        protected static class InnerParameters extends Pointer {
//            static { load(); }
//            public InnerParameters()       { allocate();  }
//            public InnerParameters(Pointer p) { super(p); }
//            private native void allocate();
//
//            public int numLastPositionsToTrack;
//            public int numStepsToWaitBeforeFirstShow;
//            public int numStepsToTrackWithoutDetectingIfObjectHasNotBeenShown;
//            public int numStepsToShowWithoutDetecting;
//
//            public float coeffTrackingWindowSize;
//            public float coeffObjectSizeToTrack;
//            public float coeffObjectSpeedUsingInPrediction;
//        }
//        protected native @ByRef Parameters parameters();
//        protected native @ByRef InnerParameters innerParameters();
//
//        protected static class TrackedObject extends Pointer {
//            static { load(); }
//            public TrackedObject()       { allocate();  }
//            public TrackedObject(CvRect rect) { allocate(rect); }
//            public TrackedObject(Pointer p) { super(p); }
//            private native void allocate();
//            private native void allocate(@ByVal CvRect rect);
//
//            public native @Adapter("VectorAdapter<CvRect,cv::Rect>") CvRect lastPositions();
//
//            public native int numDetectedFrames();
//            public native int numFramesNotDetected();
//            public native int id();
//
//            public static native int getNextId();
//        }
//
//        protected native int numTrackedSteps();
//        protected native @Adapter("VectorAdapter<TrackedObject>") TrackedObject trackedObjects();
//
//        protected native @Adapter("VectorAdapter<float>") FloatPointer weightsPositionsSmoothing();
//        protected native @Adapter("VectorAdapter<float>") FloatPointer weightsSizesSmoothing();
//
//        protected native @ByRef CascadeClassifier cascadeForTracking();
//
//        protected native void updateTrackedObjects(@Adapter("VectorAdapter<CvRect,cv::Rect>") CvRect detectedObjects);
//        protected native @ByVal CvRect calcTrackedObjectPositionToShow(int i);
//        protected native void detectInRegion(IplImage img, @ByVal CvRect r,
//                @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect detectedObjectsInRegions);
    }

    
    @NoOffset @Namespace("cv") public static class CvMotionModel extends Pointer {
        static { load(); }
        public CvMotionModel()       { allocate();  }
        public CvMotionModel(Pointer p) { super(p); }
        private native void allocate();

        public static final int LOW_PASS_FILTER = 0, KALMAN_FILTER = 1, EM = 2;

        public native float low_pass_gain(); public native CvMotionModel low_pass_gain(float low_pass_gain);
    }

    @NoOffset @Namespace("cv") public static class CvMeanShiftTrackerParams extends Pointer {
        static { load(); }
        public CvMeanShiftTrackerParams() { allocate(); }
        public CvMeanShiftTrackerParams(int tracking_type/*=HS*/, @ByVal CvTermCriteria term_crit/*=cvTermCriteria()*/) {
            allocate(tracking_type, term_crit);
        }
        public CvMeanShiftTrackerParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int tracking_type/*=HS*/, @ByVal CvTermCriteria term_crit/*=cvTermCriteria()*/);

        public static final int H = 0, HS = 1, HSV = 2;

        public native int tracking_type();       public native CvMeanShiftTrackerParams tracking_type(int tracking_type);
        @Adapter("VectorAdapter<float>")
        public native FloatPointer h_range();    public native CvMeanShiftTrackerParams h_range(FloatPointer h_range);
        @Adapter("VectorAdapter<float>")
        public native FloatPointer s_range();    public native CvMeanShiftTrackerParams s_range(FloatPointer s_range);
        @Adapter("VectorAdapter<float>")
        public native FloatPointer v_range();    public native CvMeanShiftTrackerParams v_range(FloatPointer v_range);
        @ByRef
        public native CvTermCriteria term_crit(); public native CvMeanShiftTrackerParams term_crit(CvTermCriteria term_crit);
    }

    @NoOffset @Namespace("cv") public static class CvFeatureTrackerParams extends Pointer {
        static { load(); }
        public CvFeatureTrackerParams() { allocate(); }
        public CvFeatureTrackerParams(int feature_type/*=0*/, int window_size/*=0*/) {
            allocate(feature_type, window_size);
        }
        public CvFeatureTrackerParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int feature_type/*=0*/, int window_size/*=0*/);

        public static final int SIFT = 0, SURF = 1, OPTICAL_FLOW = 2;

        public native int feature_type(); public native CvFeatureTrackerParams feature_type(int feature_type);
        public native int window_size();  public native CvFeatureTrackerParams window_size(int window_size);
    }

    @NoOffset @Namespace("cv") public static class CvHybridTrackerParams extends Pointer {
        static { load(); }
        public CvHybridTrackerParams() { allocate(); }
        public CvHybridTrackerParams(float ft_tracker_weight/*=0.5*/, float ms_tracker_weight/*=0.5*/,
                @ByVal CvFeatureTrackerParams ft_params/*=CvFeatureTrackerParams()*/,
                @ByVal CvMeanShiftTrackerParams ms_params/*=CvMeanShiftTrackerParams()*/,
                @ByVal CvMotionModel model/*=CvMotionModel()*/) {
            allocate(ft_tracker_weight, ms_tracker_weight, ft_params, ms_params, model);
        }
        public CvHybridTrackerParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float ft_tracker_weight/*=0.5*/, float ms_tracker_weight/*=0.5*/,
                @ByVal CvFeatureTrackerParams ft_params/*=CvFeatureTrackerParams()*/,
                @ByVal CvMeanShiftTrackerParams ms_params/*=CvMeanShiftTrackerParams()*/,
                @ByVal CvMotionModel model/*=CvMotionModel()*/);

        public native float ft_tracker_weight();            public native CvHybridTrackerParams ft_tracker_weight(float ft_tracker_weight);
        public native float ms_tracker_weight();            public native CvHybridTrackerParams ms_tracker_weight(float ms_tracker_weight);
        @ByRef
        public native CvFeatureTrackerParams ft_params();   public native CvHybridTrackerParams ft_params(CvFeatureTrackerParams ft_params);
        @ByRef
        public native CvMeanShiftTrackerParams ms_params(); public native CvHybridTrackerParams ms_params(CvMeanShiftTrackerParams ms_params);
        public native int motion_model();                   public native CvHybridTrackerParams motion_model(int motion_model);
        public native float low_pass_gain();                public native CvHybridTrackerParams low_pass_gain(float low_pass_gain);
    }

    @NoOffset @Namespace("cv") public static class CvMeanShiftTracker extends Pointer {
        static { load(); }
//        public CvMeanShiftTracker() { allocate(); }
        public CvMeanShiftTracker(@ByVal CvMeanShiftTrackerParams _params) {
            allocate(_params);
        }
        public CvMeanShiftTracker(Pointer p) { super(p); }
//        private native void allocate();
        private native void allocate(@ByVal CvMeanShiftTrackerParams _params);

        public native @MemberGetter @ByRef CvMeanShiftTrackerParams params();

        public native void newTrackingWindow(@Adapter("MatAdapter") IplImage image, @ByVal CvRect selection);
        public native @ByVal CvBox2D updateTrackingWindow(@Adapter("MatAdapter") IplImage image);
        public native @Adapter("MatAdapter") IplImage getHistogramProjection(int type);
        public native void setTrackingWindow(@ByVal CvRect _window);
        public native @ByVal CvRect getTrackingWindow();
        public native @ByVal CvBox2D getTrackingEllipse();
        public native @ByVal CvPoint2D32f getTrackingCenter();
    }

    @NoOffset @Namespace("cv") public static class CvFeatureTracker extends Pointer {
        static { load(); }
//        public CvFeatureTracker() { allocate(); }
        public CvFeatureTracker(@ByVal CvFeatureTrackerParams params) {
            allocate(params);
        }
        public CvFeatureTracker(Pointer p) { super(p); }
//        private native void allocate();
        private native void allocate(@ByVal CvFeatureTrackerParams params);

        public native @MemberGetter @Adapter("MatAdapter") CvMat disp_matches();
        public native @MemberGetter @ByRef CvFeatureTrackerParams params();

        public native void newTrackingWindow(@Adapter("MatAdapter") IplImage image, @ByVal CvRect selection);
        public native @ByVal CvRect updateTrackingWindow(@Adapter("MatAdapter") IplImage image);
        public native @ByVal CvRect updateTrackingWindowWithSIFT(@Adapter("MatAdapter") IplImage image);
        public native @ByVal CvRect updateTrackingWindowWithFlow(@Adapter("MatAdapter") IplImage image);
        public native void setTrackingWindow(@ByVal CvRect _window);
        public native @ByVal CvRect getTrackingWindow();
        public native @ByVal CvPoint2D32f getTrackingCenter();
    }

    @NoOffset @Namespace("cv") public static class CvHybridTracker extends Pointer {
        static { load(); }
        public CvHybridTracker() { allocate(); }
        public CvHybridTracker(@ByVal CvHybridTrackerParams params) {
            allocate(params);
        }
        public CvHybridTracker(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByVal CvHybridTrackerParams params);

        public native @MemberGetter @ByRef CvHybridTrackerParams params();

        public native void newTracker(@Adapter("MatAdapter") IplImage image, @ByVal CvRect selection);
        public native void updateTracker(@Adapter("MatAdapter") IplImage image);
        public native @ByVal CvRect getTrackingWindow();
    }

}
