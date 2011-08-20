/*
 * Copyright (C) 2011 Samuel Audet
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
 * This file is based on information found in objdetect.hpp of OpenCV 2.3.1,
 * which is covered by the following copyright notice:
 *
 *                          License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
 * Copyright (C) 2009, Willow Garage Inc., all rights reserved.
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
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Index;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/objdetect/objdetect.hpp>", "opencv_adapters.h"},
        link={"opencv_objdetect", "opencv_features2d", "opencv_flann", "opencv_calib3d", "opencv_highgui", "opencv_imgproc", "opencv_core"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_objdetect231", "opencv_features2d231", "opencv_flann231", "opencv_calib3d231", "opencv_highgui231", "opencv_imgproc231", "opencv_core231"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_objdetect {
    static { load(opencv_features2d.class); load(); }

    public static final int CV_HAAR_MAGIC_VAL    = 0x42500000;
    public static final String CV_TYPE_NAME_HAAR = "opencv-haar-classifier";

    public static boolean CV_IS_HAAR_CLASSIFIER(CvHaarClassifierCascade haar) {
        return haar != null && (haar.flags() & CV_MAGIC_MASK)==CV_HAAR_MAGIC_VAL;
    }

    public static final int CV_HAAR_FEATURE_MAX = 3;

    public static class CvHaarFeature extends Pointer {
        static { load(); }
        public CvHaarFeature() { allocate(); }
        public CvHaarFeature(int size) { allocateArray(size); }
        public CvHaarFeature(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarFeature position(int position) {
            return (CvHaarFeature)super.position(position);
        }

        public native int tilted(); public native CvHaarFeature tilted(int tilted);
        // struct { } rect[CV_HAAR_FEATURE_MAX]
        @Name({"rect", ".r"})
        public native @ByRef CvRect rect_r(int i); public native CvHaarFeature rect_r(int i, CvRect r);
        @Name({"rect", ".weight"})
        public native float rect_weight(int i); public native CvHaarFeature rect_weight(int i, float weight);
    }

    public static class CvHaarClassifier extends Pointer {
        static { load(); }
        public CvHaarClassifier() { allocate(); }
        public CvHaarClassifier(int size) { allocateArray(size); }
        public CvHaarClassifier(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarClassifier position(int position) {
            return (CvHaarClassifier)super.position(position);
        }

        public native int count();                  public native CvHaarClassifier count(int count);
        public native CvHaarFeature haar_feature(); public native CvHaarClassifier haar_feature(CvHaarFeature haar_feature);
        public native FloatPointer threshold();     public native CvHaarClassifier threshold(FloatPointer threshold);
        public native IntPointer left();            public native CvHaarClassifier left(IntPointer left);
        public native IntPointer right();           public native CvHaarClassifier right(IntPointer right);
        public native FloatPointer alpha();         public native CvHaarClassifier alpha(FloatPointer alpha);
    }

    public static class CvHaarStageClassifier extends Pointer {
        static { load(); }
        public CvHaarStageClassifier() { allocate(); }
        public CvHaarStageClassifier(int size) { allocateArray(size); }
        public CvHaarStageClassifier(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarStageClassifier position(int position) {
            return (CvHaarStageClassifier)super.position(position);
        }

        public native int count();                   public native CvHaarStageClassifier count(int count);
        public native float threshold();             public native CvHaarStageClassifier threshold(float threshold);
        public native CvHaarClassifier classifier(); public native CvHaarStageClassifier classifier(CvHaarClassifier classifier);

        public native int next();                    public native CvHaarStageClassifier next(int next);
        public native int child();                   public native CvHaarStageClassifier child(int child);
        public native int parent();                  public native CvHaarStageClassifier parent(int parent);
    }

    @Opaque public static class CvHidHaarClassifierCascade extends Pointer {
        static { load(); }
        public CvHidHaarClassifierCascade() { }
        public CvHidHaarClassifierCascade(Pointer p) { super(p); }
    }

    public static class CvHaarClassifierCascade extends Pointer {
        static { com.googlecode.javacpp.Loader.load(); }
        public CvHaarClassifierCascade() { allocate(); }
        public CvHaarClassifierCascade(int size) { allocateArray(size); }
        public CvHaarClassifierCascade(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarClassifierCascade position(int position) {
            return (CvHaarClassifierCascade)super.position(position);
        }

        public static CvHaarClassifierCascade load(String directory,
                CvSize orig_window_size) {
            CvHaarClassifierCascade h = cvLoadHaarClassifierCascade(directory,
                    orig_window_size);
            if (h != null) {
                h.deallocator(new ReleaseDeallocator(h));
            }
            return h;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvHaarClassifierCascade implements Deallocator {
            ReleaseDeallocator(CvHaarClassifierCascade p) { super(p); }
            @Override public void deallocate() { cvReleaseHaarClassifierCascade(this); }
        }


        public native int flags();                              public native CvHaarClassifierCascade flags(int flags);
        public native int count();                              public native CvHaarClassifierCascade count(int count);
        public native @ByRef CvSize orig_window_size();         public native CvHaarClassifierCascade orig_window_size(CvSize orig_window_size);
        public native @ByRef CvSize real_window_size();         public native CvHaarClassifierCascade real_window_size(CvSize real_window_size);
        public native double scale();                           public native CvHaarClassifierCascade scale(double scale);
        public native CvHaarStageClassifier stage_classifier(); public native CvHaarClassifierCascade stage_classifier(CvHaarStageClassifier stage_classifier);
        public native CvHidHaarClassifierCascade hid_cascade(); public native CvHaarClassifierCascade hid_cascade(CvHidHaarClassifierCascade hid_cascade);
    }

    public static class CvAvgComp extends Pointer {
        static { load(); }
        public CvAvgComp() { allocate(); }
        public CvAvgComp(int size) { allocateArray(size); }
        public CvAvgComp(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvAvgComp position(int position) {
            return (CvAvgComp)super.position(position);
        }

        public native @ByRef CvRect rect(); public native CvAvgComp rect(CvRect rect);
        public native int neighbors();      public native CvAvgComp neighbors(int neighbors);
    }

    public static native CvHaarClassifierCascade cvLoadHaarClassifierCascade(
            String directory, @ByVal CvSize orig_window_size);
    public static native void cvReleaseHaarClassifierCascade(
            @ByPtrPtr CvHaarClassifierCascade cascade);

    public static final int
            CV_HAAR_DO_CANNY_PRUNING    = 1,
            CV_HAAR_SCALE_IMAGE         = 2,
            CV_HAAR_FIND_BIGGEST_OBJECT = 4,
            CV_HAAR_DO_ROUGH_SEARCH     = 8;

    public static CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/, int flags/*=0*/) {
        return cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags, CvSize.ZERO, CvSize.ZERO);
    }
    public static native CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/, int flags/*=0*/,
            @ByVal CvSize min_size/*=cvSize(0,0)*/, @ByVal CvSize max_size/*=cvSize(0,0)*/);
    public static native void cvSetImagesForHaarClassifierCascade(CvHaarClassifierCascade cascade,
            CvArr sum, CvArr sqsum, CvArr tilted_sum, double scale);
    public static native int cvRunHaarClassifierCascade(CvHaarClassifierCascade cascade,
            @ByVal CvPoint pt, int start_stage/*=0*/);


    public static class CvLSVMFilterPosition extends Pointer {
        static { load(); }
        public CvLSVMFilterPosition() { allocate(); }
        public CvLSVMFilterPosition(int size) { allocateArray(size); }
        public CvLSVMFilterPosition(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLSVMFilterPosition position(int position) {
            return (CvLSVMFilterPosition)super.position(position);
        }

        public native int x(); public native CvLSVMFilterPosition x(int x);
        public native int y(); public native CvLSVMFilterPosition y(int y);
        public native int l(); public native CvLSVMFilterPosition l(int l);
    }

    public static class CvLSVMFilterObject extends Pointer {
        static { load(); }
        public CvLSVMFilterObject() { allocate(); }
        public CvLSVMFilterObject(int size) { allocateArray(size); }
        public CvLSVMFilterObject(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLSVMFilterObject position(int position) {
            return (CvLSVMFilterObject)super.position(position);
        }

        public native @ByRef CvLSVMFilterPosition V();  public native CvLSVMFilterObject V(CvLSVMFilterPosition V);
        public native float/*[4]*/ fineFunction(int i); public native CvLSVMFilterObject fineFunction(int i, float fineFunction);
        public native int sizeX();                      public native CvLSVMFilterObject sizeX(int sizeX);
        public native int sizeY();                      public native CvLSVMFilterObject sizeY(int sizeY);
        public native int numFeatures();                public native CvLSVMFilterObject numFeatures(int numFeatures);
        public native FloatPointer H();                 public native CvLSVMFilterObject H(FloatPointer H);
    }

    public static class CvLatentSvmDetector extends Pointer {
        static { load(); }
        public CvLatentSvmDetector() { allocate(); }
        public CvLatentSvmDetector(int size) { allocateArray(size); }
        public CvLatentSvmDetector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLatentSvmDetector position(int position) {
            return (CvLatentSvmDetector)super.position(position);
        }

        public native int num_filters();             public native CvLatentSvmDetector num_filters(int num_filters);
	public native int num_components();          public native CvLatentSvmDetector num_components(int num_components);
	public native IntPointer num_part_filters(); public native CvLatentSvmDetector num_part_filters(IntPointer num_part_filters);
        @Cast("CvLSVMFilterObject**")
	public native PointerPointer filters();      public native CvLatentSvmDetector filters(PointerPointer filters);
	public native FloatPointer b();              public native CvLatentSvmDetector b(FloatPointer b);
	public native float score_threshold();       public native CvLatentSvmDetector score_threshold(float score_threshold);
    }

    public static class CvObjectDetection extends Pointer {
        static { load(); }
        public CvObjectDetection() { allocate(); }
        public CvObjectDetection(int size) { allocateArray(size); }
        public CvObjectDetection(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvObjectDetection position(int position) {
            return (CvObjectDetection)super.position(position);
        }

        public native @ByRef CvRect rect(); public native CvObjectDetection rect(CvRect rect);
	public native float score();        public native CvObjectDetection score(float score);
    }

    public static native CvLatentSvmDetector cvLoadLatentSvmDetector(String filename);
    public static native void cvReleaseLatentSvmDetector(@ByPtrPtr CvLatentSvmDetector detector);
    public static native CvSeq cvLatentSvmDetectObjects(@Adapter("MatAdapter") CvArr image,
            CvLatentSvmDetector detector, CvMemStorage storage,	float overlap_threshold/*=0.5*/, int numThreads/*=-1*/);

    public static native CvSeq cvHaarDetectObjectsForROC(CvArr image, CvHaarClassifierCascade cascade, CvMemStorage storage,
            @Adapter(value="VectorAdapter<int>", out=true) IntPointer rejectLevels,
            @Adapter(value="VectorAdapter<double>", out=true) DoublePointer levelWeightds,
            double scale_factor/*=1.1*/, int min_neighbors/*=3*/, int flags/*=0*/,
            @ByVal CvSize min_size/*=cvSize(0,0)*/, @ByVal CvSize max_size/*=cvSize(0,0)*/, boolean outputRejectLevels/*=false*/);


    @Namespace("cv") public static native void groupRectangles(@Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
            CvRect rectList, int groupThreshold, double eps/*=0.2*/);
    @Namespace("cv") public static native void groupRectangles(@Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
            CvRect rectList, @Adapter(value="VectorAdapter<int>", out=true) IntPointer weights, int groupThreshold, double eps/*=0.2*/);
    @Namespace("cv") public static native void groupRectangles(@Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
            CvRect rectList, int groupThreshold, double eps, @Adapter(value="VectorAdapter<int>", out=true) IntPointer weights,
            @Adapter(value="VectorAdapter<double>", out=true) DoublePointer levelWeights);
    @Namespace("cv") public static native void groupRectangles(@Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
            CvRect rectList, @Adapter(value="VectorAdapter<int>", out=true) IntPointer rejectLevels,
            @Adapter(value="VectorAdapter<double>", out=true) DoublePointer levelWeights, int groupThreshold, double eps/*=0.2*/);
    @Namespace("cv") public static native void groupRectangles_meanshift(@Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
            CvRect rectList, @Adapter(value="VectorAdapter<double>", out=true) DoublePointer foundWeights,
            @Adapter(value="VectorAdapter<double>", out=true) DoublePointer foundScales,
            double detectThreshold/*=0.0*/, @ByVal CvSize winDetSize/*=cvSize(64, 128)*/);

    @Namespace("cv") public static class FeatureEvaluator extends Pointer {
        static { load(); }
        public FeatureEvaluator() { allocate(); }
        public FeatureEvaluator(int size) { allocateArray(size); }
        public FeatureEvaluator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public static final int HAAR = 0, LBP = 1;

        public native boolean read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        @Override public native @ByVal FeatureEvaluatorPtr clone();
        public native int getFeatureType();

        public native boolean setImage(@Adapter("MatAdapter") CvArr image, @ByVal CvSize origWinSize);
        public native boolean setWindow(@ByVal CvPoint p);

        public native double calcOrd(int featureIdx);
        public native int calcCat(int featureIdx);

        public static native @ByVal FeatureEvaluatorPtr create(int type);
    }

    @Name("cv::Ptr<cv::FeatureEvaluator>")
    public static class FeatureEvaluatorPtr extends Pointer {
        static { load(); }
        public FeatureEvaluatorPtr()       { allocate();  }
        public FeatureEvaluatorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native FeatureEvaluator get();
        public native FeatureEvaluatorPtr put(FeatureEvaluator value);
    }

    @NoOffset @Namespace("cv") public static class CascadeClassifier extends Pointer {
        static { Loader.load(); }
        public CascadeClassifier() { allocate(); }
        public CascadeClassifier(String filename) { allocate(filename); }
        public CascadeClassifier(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(String filename);

        public native boolean empty();
        public native boolean load(String filename);
        public native boolean read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        public native void detectMultiScale(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
                CvRect objects, double scaleFactor/*=1.1*/, int minNeighbors/*=3*/, int flags/*=0*/,
                @ByVal CvSize minSize/*=Size()*/, @ByVal CvSize maxSize/*=Size()*/);

        public native void detectMultiScale(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true)
                CvRect objects, @Adapter(value="VectorAdapter<int>", out=true) IntPointer rejectLevels,
                @Adapter(value="VectorAdapter<double>", out=true) DoublePointer levelWeights,
                double scaleFactor/*=1.1*/, int minNeighbors/*=3*/, int flags/*=0*/,
                @ByVal CvSize minSize/*=Size()*/, @ByVal CvSize maxSize/*=Size()*/, boolean outputRejectLevels/*=false*/);

        public native boolean isOldFormatCascade();
        public native @ByVal CvSize getOriginalWindowSize();
        public native int getFeatureType();
        public native boolean setImage(@Adapter("MatAdapter") CvArr image);

//        protected native boolean detectSingleScale(@Adapter("MatAdapter") CvArr image, int stripCount, @ByVal CvSize processingRectSize,
//                int stripSize, int yStep, double factor, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect candidates);
//
//        protected native boolean detectSingleScale(@Adapter("MatAdapter") CvArr image, int stripCount, @ByVal CvSize processingRectSize,
//                int stripSize, int yStep, double factor, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect candidates,
//                @Adapter(value="VectorAdapter<int>", out=true) IntPointer rejectLevels,
//                @Adapter(value="VectorAdapter<double>", out=true) DoublePointer levelWeights, boolean outputRejectLevels/*=false*/);
//
//        protected static final int BOOST = 0,
//                DO_CANNY_PRUNING = 1, SCALE_IMAGE = 2,
//                FIND_BIGGEST_OBJECT = 4, DO_ROUGH_SEARCH = 8;
//
//        protected native int predictOrdered(@ByRef CascadeClassifier cascade, @ByRef FeatureEvaluatorPtr featureEvaluator, @ByRef double[] weight);
//        protected native int predictCategorical(@ByRef CascadeClassifier cascade, @ByRef FeatureEvaluatorPtr featureEvaluator, @ByRef double[] weight);
//        protected native int predictOrderedStump(@ByRef CascadeClassifier cascade, @ByRef FeatureEvaluatorPtr featureEvaluator, @ByRef double[] weight);
//        protected native int predictCategoricalStump(@ByRef CascadeClassifier cascade, @ByRef FeatureEvaluatorPtr featureEvaluator, @ByRef double[] weight);
//
//        protected native boolean setImage(@ByRef FeatureEvaluatorPtr feval, @Adapter("MatAdapter") CvArr image);
//        protected native int runAt(@ByRef FeatureEvaluatorPtr feval, @ByVal CvPoint p, @ByRef double[] weight);
//
//        protected static class Data extends Pointer {
//            static { Loader.load(); }
//            public Data() { allocate(); }
//            public Data(int size) { allocateArray(size); }
//            public Data(Pointer p) { super(p); }
//            private native void allocate();
//            private native void allocateArray(int size);
//
//            public static class DTreeNode extends Pointer {
//                static { Loader.load(); }
//                public DTreeNode() { allocate(); }
//                public DTreeNode(int size) { allocateArray(size); }
//                public DTreeNode(Pointer p) { super(p); }
//                private native void allocate();
//                private native void allocateArray(int size);
//
//                public native int featureIdx();  public native DTreeNode featureIdx(int featureIdx);
//                public native float threshold(); public native DTreeNode threshold(float threshold);
//                public native int left();        public native DTreeNode left(int left);
//                public native int right();       public native DTreeNode right(int right);
//            }
//
//            public static class DTree extends Pointer {
//                static { Loader.load(); }
//                public DTree() { allocate(); }
//                public DTree(int size) { allocateArray(size); }
//                public DTree(Pointer p) { super(p); }
//                private native void allocate();
//                private native void allocateArray(int size);
//
//                public native int nodeCount(); public native DTree nodeCount(int nodeCount);
//            }
//
//            public static class Stage extends Pointer {
//                static { Loader.load(); }
//                public Stage() { allocate(); }
//                public Stage(int size) { allocateArray(size); }
//                public Stage(Pointer p) { super(p); }
//                private native void allocate();
//                private native void allocateArray(int size);
//
//                public native int first();       public native Stage first(int first);
//                public native int ntrees();      public native Stage ntrees(int ntrees);
//                public native float threshold(); public native Stage threshold(float threshold);
//            }
//
//            public native boolean read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
//
//            public native boolean isStumpBased(); public native CascadeClassifier isStumpBased(boolean is_stump_based);
//
//            public native int stageType();       public native CascadeClassifier stageType(int stageType);
//            public native int featureType();     public native CascadeClassifier featureType(int featureType);
//            public native int ncategories();     public native CascadeClassifier ncategories(int ncategories);
//            @ByVal
//            public native CvSize origWinSize();  public native CascadeClassifier origWinSize(CvSize origWinSize);
//
//            @Adapter("VectorAdapter<cv::CascadeClassifier::Stage>")
//            public native Stage stages();        public native CascadeClassifier stages(Stage stages);
//            @Adapter("VectorAdapter<cv::CascadeClassifier::DTree>")
//            public native DTree classifiers();   public native CascadeClassifier classifiers(DTree classifiers);
//            @Adapter("VectorAdapter<cv::CascadeClassifier::DTreeNode>")
//            public native DTreeNode nodes();     public native CascadeClassifier nodes(DTreeNode nodes);
//            @Adapter("VectorAdapter<float>")
//            public native FloatPointer leaves(); public native CascadeClassifier leaves(FloatPointer leaves);
//            @Adapter("VectorAdapter<int>")
//            public native IntPointer subsets();  public native CascadeClassifier subsets(IntPointer subsets);
//        }
//
//        protected native Data data();                            protected native CascadeClassifier data(Data data);
//        @ByRef
//        protected native FeatureEvaluatorPtr featureEvaluator(); protected native CascadeClassifier featureEvaluator(FeatureEvaluatorPtr featureEvaluator);
//        protected native CvHaarClassifierCascade oldCascade();   protected native CascadeClassifier oldCascade(CvHaarClassifierCascade oldCascade);
    }


    @NoOffset @Namespace("cv") public static class HOGDescriptor extends Pointer {
        static { Loader.load(); }
        public HOGDescriptor() { allocate(); }
        public HOGDescriptor(@ByVal CvSize _winSize, @ByVal CvSize _blockSize, @ByVal CvSize _blockStride,
                @ByVal CvSize _cellSize, int _nbins, int _derivAperture/*=1*/, double _winSigma/*=-1*/,
                int _histogramNormType/*=HOGDescriptor::L2Hys*/,
                double _L2HysThreshold/*=0.2*/, boolean _gammaCorrection/*=false*/,
                int _nlevels/*=HOGDescriptor::DEFAULT_NLEVELS*/) { 
            allocate(_winSize, _blockSize, _blockStride, _cellSize, _nbins, _derivAperture,
                    _winSigma, _histogramNormType, _L2HysThreshold, _gammaCorrection, _nlevels);
        }
        public HOGDescriptor(String filename) { allocate(filename); }
        public HOGDescriptor(@ByRef HOGDescriptor d) { allocate(d); }
        public HOGDescriptor(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByVal CvSize _winSize, @ByVal CvSize _blockSize, @ByVal CvSize _blockStride,
                @ByVal CvSize _cellSize, int _nbins, int _derivAperture/*=1*/, double _winSigma/*=-1*/,
                int _histogramNormType/*=HOGDescriptor::L2Hys*/,
                double _L2HysThreshold/*=0.2*/, boolean _gammaCorrection/*=false*/,
                int _nlevels/*=HOGDescriptor::DEFAULT_NLEVELS*/);
        private native void allocate(String filename);
        private native void allocate(@ByRef HOGDescriptor d);

        public static final int
                L2Hys=0,
                DEFAULT_NLEVELS=64;

        public native long getDescriptorSize();
        public native boolean checkDetectorSize();
        public native double getWinSigma();

        public native void setSVMDetector(@Adapter("ArrayAdapter") CvArr _svmdetector);

        public native boolean read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs, String objname);

        public native boolean load(String filename, String objname/*=""*/);
        public native void save(String filename, String objname/*=""*/);
        public native void copyTo(@ByRef HOGDescriptor c);

        public native void compute(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<float>", out=true) FloatPointer descriptors,
                @ByVal CvSize winStride/*=Size()*/, @ByVal CvSize padding/*=Size()*/,
                @Adapter("VectorAdapter<CvPoint,cv::Point>") CvPoint locations/*=vector<Point>()*/);
        public native void detect(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<CvPoint,cv::Point>", out=true) CvPoint foundLocations,
                @Adapter(value="VectorAdapter<double>", out=true) DoublePointer weights, double hitThreshold/*=0*/,
                @ByVal CvSize winStride/*=cvSize()*/, @ByVal CvSize padding/*=cvSize()*/,
                @Adapter("VectorAdapter<CvPoint,cv::Point>") CvPoint searchLocations/*=vector<Point>()*/);
        public native void detect(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<CvPoint,cv::Point>", out=true) CvPoint foundLocations,
                double hitThreshold/*=0*/, @ByVal CvSize winStride/*=Size()*/, @ByVal CvSize padding/*=Size()*/,
                @Adapter("VectorAdapter<CvPoint,cv::Point>") CvPoint searchLocations/*=vector<Point>()*/);
        public native void detectMultiScale(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect foundLocations,
                double hitThreshold/*=0*/, @ByVal CvSize winStride/*=Size()*/, @ByVal CvSize padding/*=Size()*/,
                double scale/*=1.05*/, int groupThreshold/*=2*/);
        public native void detectMultiScale(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect foundLocations,
                @Adapter(value="VectorAdapter<double>", out=true) DoublePointer foundWeights, double hitThreshold/*=0*/,
                @ByVal CvSize winStride/*=cvSize()*/, @ByVal CvSize padding/*=cvSize()*/, double scale/*=1.05*/,
                double finalThreshold/*=2.0*/, boolean useMeanshiftGrouping/*=false*/);
        public native void detectMultiScale(@Adapter("MatAdapter") CvArr img, @Adapter(value="VectorAdapter<CvRect,cv::Rect>", out=true) CvRect foundLocations,
                double hitThreshold/*=0*/, @ByVal CvSize winStride/*=cvSize()*/, @ByVal CvSize padding/*=cvSize()*/, double scale/*=1.05*/,
                double finalThreshold/*=2.0*/, boolean useMeanshiftGrouping/*=false*/);

        public native void computeGradient(@Adapter("MatAdapter") CvArr img, @Adapter("MatAdapter") CvArr grad,
                @Adapter("MatAdapter") CvArr angleOfs, @ByVal CvSize paddingTL/*=Size()*/, @ByVal CvSize paddingBR/*=Size()*/);

        public static native @Adapter("VectorAdapter<float>") FloatPointer getDefaultPeopleDetector();
        public static native @Adapter("VectorAdapter<float>") FloatPointer getDaimlerPeopleDetector();

        public native @ByVal CvSize winSize();     public native HOGDescriptor winSize(CvSize winSize);
        public native @ByVal CvSize blockSize();   public native HOGDescriptor blockSize(CvSize blockSize);
        public native @ByVal CvSize blockStride(); public native HOGDescriptor blockStride(CvSize blockStride);
        public native @ByVal CvSize cellSize();    public native HOGDescriptor cellSize(CvSize cellSize);
        public native int nbins();                 public native HOGDescriptor nbins(int nbins);
        public native int derivAperture();         public native HOGDescriptor derivAperture(int derivAperture);
        public native double winSigma();           public native HOGDescriptor winSigma(double winSigma);
        public native int histogramNormType();     public native HOGDescriptor histogramNormType(int histogramNormType);
        public native double L2HysThreshold();     public native HOGDescriptor L2HysThreshold(double L2HysThreshold);
        public native boolean gammaCorrection();   public native HOGDescriptor gammaCorrection(boolean gammaCorrection);
        @Adapter("VectorAdapter<float>")
        public native FloatPointer svmDetector();  public native HOGDescriptor svmDetector(FloatPointer svmDetector);
        public native int nlevels();               public native HOGDescriptor nlevels(int nlevels);
    }


    @Namespace("cv") public static class PlanarObjectDetector extends Pointer {
        public PlanarObjectDetector() { allocate(); }
        public PlanarObjectDetector(Pointer p) { super(p); }
        public PlanarObjectDetector(CvFileStorage fs, CvFileNode node) { allocate(fs, node); }
        public PlanarObjectDetector(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                int _npoints/*=300*/, int _patchSize/*=FernClassifier::PATCH_SIZE*/,
                int _nstructs/*=FernClassifier::DEFAULT_STRUCTS*/,
                int _structSize/*=FernClassifier::DEFAULT_STRUCT_SIZE*/,
                int _nviews/*=FernClassifier::DEFAULT_VIEWS*/,
                @ByRef LDetector detector/*=LDetector()*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/) {
            allocate(pyr, _npoints, _patchSize, _nstructs,
                    _structSize, _nviews, detector, patchGenerator);
        }
        private native void allocate();
        private native void allocate(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        private native void allocate(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                int _npoints/*=300*/, int _patchSize/*=FernClassifier::PATCH_SIZE*/,
                int _nstructs/*=FernClassifier::DEFAULT_STRUCTS*/,
                int _structSize/*=FernClassifier::DEFAULT_STRUCT_SIZE*/,
                int _nviews/*=FernClassifier::DEFAULT_VIEWS*/,
                @ByRef LDetector detector/*=LDetector()*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);

        public native void train(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                int _npoints/*=300*/, int _patchSize/*=FernClassifier::PATCH_SIZE*/,
                int _nstructs/*=FernClassifier::DEFAULT_STRUCTS*/,
                int _structSize/*=FernClassifier::DEFAULT_STRUCT_SIZE*/,
                int _nviews/*=FernClassifier::DEFAULT_VIEWS*/,
                @ByRef LDetector detector/*=LDetector()*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);
        public native void train(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints,
                int _patchSize/*=FernClassifier::PATCH_SIZE*/,
                int _nstructs/*=FernClassifier::DEFAULT_STRUCTS*/,
                int _structSize/*=FernClassifier::DEFAULT_STRUCT_SIZE*/,
                int _nviews/*=FernClassifier::DEFAULT_VIEWS*/,
                @ByRef LDetector detector/*=LDetector()*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);

//        public native @ByVal CvRect getModelROI();
        public native @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint getModelPoints();
//        public native @Const @ByRef LDetector getDetector();
//        public native @Const @ByRef FernClassifier getClassifier();
        public native void setVerbose(boolean verbose);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs, String name);
        public native @Name("operator()") boolean detect(@Adapter("MatAdapter") CvArr image, @Adapter("MatAdapter") CvMat H,
                @Adapter(value="VectorAdapter<CvPoint2D32f,cv::Point2f>", out=true) CvPoint2D32f corners);
        public native @Name("operator()") boolean detect(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints, @Adapter("MatAdapter") CvMat H,
                @Adapter(value="VectorAdapter<CvPoint2D32f,cv::Point2f>", out=true) CvPoint2D32f corners,
                @Adapter(value="VectorAdapter<int>", out=true) IntPointer pairs/*=null*/);

//        protected native boolean verbose();
//        protected native @ByVal CvRect modelROI();
//        protected native @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint modelPoints();
//        protected native @ByRef LDetector ldetector();
//        protected native @ByRef FernClassifier fernClassifier();
    }


    @NoOffset @Namespace("cv") public static class DataMatrixCode extends Pointer {
        static { load(); }
        public DataMatrixCode() { allocate(); }
        public DataMatrixCode(int size) { allocateArray(size); }
        public DataMatrixCode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native String msg();                  public native DataMatrixCode msg(String msg);
        @Adapter("MatAdapter")
        public native CvMat original();              public native DataMatrixCode original(CvMat original);
        @ByVal
        public native CvPoint/*[4]*/ corners(int i); public native DataMatrixCode corners(int i, CvPoint corners);
    }

    @Namespace("cv") public static native void findDataMatrix(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::DataMatrixCode>", out=true) DataMatrixCode codes);
    @Namespace("cv") public static native void drawDataMatrixCodes(@Adapter("VectorAdapter<cv::DataMatrixCode>") DataMatrixCode codes, @Adapter("MatAdapter") CvArr drawImage);
 
    public static class CvDataMatrixCode extends Pointer {
        static { load(); }
        public CvDataMatrixCode() { allocate(); }
        public CvDataMatrixCode(int size) { allocateArray(size); }
        public CvDataMatrixCode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native String msg();     public native CvDataMatrixCode msg(String msg);
        public native CvMat original(); public native CvDataMatrixCode original(CvMat original);
        public native CvMat corners();  public native CvDataMatrixCode corners(CvMat corners);
    }

    @Name("std::deque<CvDataMatrixCode>") @Index
    public static class CvDataMatrixCodeDeque extends Pointer {
        static { load(); }
        public CvDataMatrixCodeDeque()       { allocate();  }
        public CvDataMatrixCodeDeque(long n) { allocate(n); }
        public CvDataMatrixCodeDeque(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(long n);

        public native long size();
        public native void resize(long n);

        @ByRef public native CvDataMatrixCode get(long i);
        public native CvDataMatrixCodeDeque put(long i, CvDataMatrixCode value);
    }

    public static native @ByVal CvDataMatrixCodeDeque cvFindDataMatrix(CvMat im);
}
