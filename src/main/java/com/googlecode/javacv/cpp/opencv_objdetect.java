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
 * This file is based on information found in objdetect.hpp of OpenCV 2.4.5,
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
import com.googlecode.javacpp.annotation.StdVector;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/objdetect/objdetect.hpp>", "opencv_adapters.h"},
        link={"opencv_objdetect@.2.4", "opencv_highgui@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_objdetect245", "opencv_highgui245", "opencv_imgproc245", "opencv_core245"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_objdetect {
    static { load(opencv_imgproc.class); load(opencv_highgui.class); load(); }

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
        static { Loader.load(); }
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
    public static native CvSeq cvLatentSvmDetectObjects(@InputMat CvArr image,
            CvLatentSvmDetector detector, CvMemStorage storage,	float overlap_threshold/*=0.5*/, int numThreads/*=-1*/);

    public static native CvSeq cvHaarDetectObjectsForROC(CvArr image, CvHaarClassifierCascade cascade, CvMemStorage storage,
            @StdVector IntPointer rejectLevels, @StdVector DoublePointer levelWeightds, double scale_factor/*=1.1*/,
            int min_neighbors/*=3*/, int flags/*=0*/, @ByVal CvSize min_size/*=cvSize(0,0)*/,
            @ByVal CvSize max_size/*=cvSize(0,0)*/, @Cast("bool") boolean outputRejectLevels/*=false*/);


    @Namespace("cv") public static native void groupRectangles(@StdVector("CvRect,cv::Rect") CvRect rectList,
            int groupThreshold, double eps/*=0.2*/);
    @Namespace("cv") public static native void groupRectangles(@StdVector("CvRect,cv::Rect") CvRect rectList,
            @StdVector IntPointer weights, int groupThreshold, double eps/*=0.2*/);
    @Namespace("cv") public static native void groupRectangles(@StdVector("CvRect,cv::Rect") CvRect rectList,
            int groupThreshold, double eps, @StdVector IntPointer weights, @StdVector DoublePointer levelWeights);
    @Namespace("cv") public static native void groupRectangles(@StdVector("CvRect,cv::Rect") CvRect rectList,
            @StdVector IntPointer rejectLevels, @StdVector DoublePointer levelWeights,
            int groupThreshold, double eps/*=0.2*/);
    @Namespace("cv") public static native void groupRectangles_meanshift(@StdVector("CvRect,cv::Rect") CvRect rectList,
            @StdVector DoublePointer foundWeights, @StdVector DoublePointer foundScales,
            double detectThreshold/*=0.0*/, @ByVal CvSize winDetSize/*=cvSize(64, 128)*/);

    @Namespace("cv") public static class FeatureEvaluator extends Pointer {
        static { load(); }
        public FeatureEvaluator() { allocate(); }
        public FeatureEvaluator(int size) { allocateArray(size); }
        public FeatureEvaluator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FeatureEvaluator position(int position) {
            return (FeatureEvaluator)super.position(position);
        }

        public static final int HAAR = 0, LBP = 1, HOG = 2;

        public native boolean read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        @Override public native @Ptr FeatureEvaluator clone();
        public native int getFeatureType();

        public native boolean setImage(@InputMat CvArr img, @ByVal CvSize origWinSize);
        public native boolean setWindow(@ByVal CvPoint p);

        public native double calcOrd(int featureIdx);
        public native int calcCat(int featureIdx);

        public static native @Ptr FeatureEvaluator create(int type);
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
        public native boolean read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        public native void detectMultiScale(@InputMat CvArr image, @StdVector("CvRect,cv::Rect") CvRect objects,
                double scaleFactor/*=1.1*/, int minNeighbors/*=3*/, int flags/*=0*/, @ByVal CvSize minSize/*=Size()*/, @ByVal CvSize maxSize/*=Size()*/);

        public native void detectMultiScale(@InputMat CvArr image, @StdVector("CvRect,cv::Rect") CvRect objects,
                @StdVector IntPointer rejectLevels, @StdVector DoublePointer levelWeights, double scaleFactor/*=1.1*/,
                int minNeighbors/*=3*/, int flags/*=0*/, @ByVal CvSize minSize/*=Size()*/,
                @ByVal CvSize maxSize/*=Size()*/, @Cast("bool") boolean outputRejectLevels/*=false*/);

        public native boolean isOldFormatCascade();
        public native @ByVal CvSize getOriginalWindowSize();
        public native int getFeatureType();
        public native boolean setImage(@InputMat CvArr image);

//        protected native boolean detectSingleScale(@InputMat CvArr image, int stripCount, @ByVal CvSize processingRectSize,
//                int stripSize, int yStep, double factor, @Const @StdVector("CvRect,cv::Rect") CvRect candidates);
//
//        protected native boolean detectSingleScale(@InputMat CvArr image, int stripCount, @ByVal CvSize processingRectSize,
//                int stripSize, int yStep, double factor, @Const @StdVector("CvRect,cv::Rect") CvRect candidates,
//                @StdVector IntPointer rejectLevels, @StdVector DoublePointer levelWeights,
//                @Cast("bool") boolean outputRejectLevels/*=false*/);
//
//        protected static final int BOOST = 0,
//                DO_CANNY_PRUNING = 1, SCALE_IMAGE = 2,
//                FIND_BIGGEST_OBJECT = 4, DO_ROUGH_SEARCH = 8;
//
//        protected native int predictOrdered(@ByRef CascadeClassifier cascade, @Ptr FeatureEvaluator featureEvaluator, @ByRef double[] weight);
//        protected native int predictCategorical(@ByRef CascadeClassifier cascade, @Ptr FeatureEvaluator featureEvaluator, @ByRef double[] weight);
//        protected native int predictOrderedStump(@ByRef CascadeClassifier cascade, @Ptr FeatureEvaluator featureEvaluator, @ByRef double[] weight);
//        protected native int predictCategoricalStump(@ByRef CascadeClassifier cascade, @Ptr FeatureEvaluator featureEvaluator, @ByRef double[] weight);
//
//        protected native boolean setImage(@Ptr FeatureEvaluator feval, @InputMat CvArr image);
//        protected native int runAt(@Ptr FeatureEvaluator feval, @ByVal CvPoint p, @ByRef double[] weight);
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
//            public native boolean read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
//
//            public native boolean isStumpBased(); public native CascadeClassifier isStumpBased(@Cast("bool") boolean is_stump_based);
//
//            public native int stageType();       public native CascadeClassifier stageType(int stageType);
//            public native int featureType();     public native CascadeClassifier featureType(int featureType);
//            public native int ncategories();     public native CascadeClassifier ncategories(int ncategories);
//            @ByVal
//            public native CvSize origWinSize();  public native CascadeClassifier origWinSize(CvSize origWinSize);
//
//            public native @Const @StdVector Stage stages();        public native CascadeClassifier stages(Stage stages);
//            public native @Const @StdVector DTree classifiers();   public native CascadeClassifier classifiers(DTree classifiers);
//            public native @Const @StdVector DTreeNode nodes();     public native CascadeClassifier nodes(DTreeNode nodes);
//            public native @Const @StdVector FloatPointer leaves(); public native CascadeClassifier leaves(FloatPointer leaves);
//            public native @Const @StdVector IntPointer subsets();  public native CascadeClassifier subsets(IntPointer subsets);
//        }
//
//        protected native Data data();                          protected native CascadeClassifier data(Data data);
//        @Const @Ptr
//        protected native FeatureEvaluator featureEvaluator();  protected native CascadeClassifier featureEvaluator(FeatureEvaluator featureEvaluator);
//        protected native CvHaarClassifierCascade oldCascade(); protected native CascadeClassifier oldCascade(CvHaarClassifierCascade oldCascade);
        public static class MaskGenerator extends Pointer {
            static { Loader.load(); }
            public MaskGenerator() { }
            public MaskGenerator(Pointer p) { super(p); }

            public /*abstract*/ native @OutputMat CvMat generateMask(CvMat src);
            public /*abstract*/ native void initializeMask(CvMat src);
        };
        public native void setMaskGenerator(@Ptr MaskGenerator maskGenerator);
        public native @Const @Ptr MaskGenerator getMaskGenerator();

        public native void setFaceDetectionMaskGenerator();

//        protected native @Const @Ptr MaskGenerator maskGenerator();
    }


    @NoOffset @Namespace("cv") public static class DetectionROI extends Pointer {
        static { load(); }
        public DetectionROI() { allocate(); }
        public DetectionROI(int size) { allocateArray(size); }
        public DetectionROI(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public DetectionROI position(int position) {
            return (DetectionROI)super.position(position);
        }

        public native double scale();              public native DetectionROI scale(double scale);
        @StdVector("CvPoint,cv::Point")
        public native CvPoint locations();         public native DetectionROI locations(CvPoint locations);
        @StdVector
        public native DoublePointer confidences(); public native DetectionROI confidences(DoublePointer confidences);
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
                double _L2HysThreshold/*=0.2*/, @Cast("bool") boolean _gammaCorrection/*=false*/,
                int _nlevels/*=HOGDescriptor::DEFAULT_NLEVELS*/);
        private native void allocate(String filename);
        private native void allocate(@ByRef HOGDescriptor d);

        public static final int
                L2Hys=0,
                DEFAULT_NLEVELS=64;

        public native long getDescriptorSize();
        public native boolean checkDetectorSize();
        public native double getWinSigma();

        public native void setSVMDetector(@InputArray CvArr _svmdetector);
        public native void setSVMDetector(@InputArray FloatPointer _svmdetector);

        public native boolean read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs, String objname);

        public native boolean load(String filename, String objname/*=""*/);
        public native void save(String filename, String objname/*=""*/);
        public native void copyTo(@ByRef HOGDescriptor c);

        public native void compute(@InputMat CvArr img, @StdVector FloatPointer descriptors, @ByVal CvSize winStride/*=Size()*/,
                @ByVal CvSize padding/*=Size()*/, @Const @StdVector("CvPoint,cv::Point") CvPoint locations/*=null*/);
        public native void detect(@InputMat CvArr img, @StdVector("CvPoint,cv::Point") CvPoint foundLocations,
                @StdVector DoublePointer weights, double hitThreshold/*=0*/, @ByVal CvSize winStride/*=cvSize()*/,
                @ByVal CvSize padding/*=cvSize()*/, @Const @StdVector("CvPoint,cv::Point") CvPoint searchLocations/*=null*/);
        public native void detect(@InputMat CvArr img, @StdVector("CvPoint,cv::Point") CvPoint foundLocations,
                double hitThreshold/*=0*/, @ByVal CvSize winStride/*=Size()*/, @ByVal CvSize padding/*=Size()*/,
                @Const @StdVector("CvPoint,cv::Point") CvPoint searchLocations/*=vector<Point>()*/);
        public native void detectMultiScale(@InputMat CvArr img, @StdVector("CvRect,cv::Rect") CvRect foundLocations,
                double hitThreshold/*=0*/, @ByVal CvSize winStride/*=Size()*/, @ByVal CvSize padding/*=Size()*/,
                double scale/*=1.05*/, int groupThreshold/*=2*/);
        public native void detectMultiScale(@InputMat CvArr img, @StdVector("CvRect,cv::Rect") CvRect foundLocations,
                @StdVector DoublePointer foundWeights, double hitThreshold/*=0*/, @ByVal CvSize winStride/*=cvSize()*/,
                @ByVal CvSize padding/*=cvSize()*/, double scale/*=1.05*/, double finalThreshold/*=2.0*/, @Cast("bool") boolean useMeanshiftGrouping/*=false*/);
        public native void detectMultiScale(@InputMat CvArr img, @StdVector("CvRect,cv::Rect") CvRect foundLocations,
                double hitThreshold/*=0*/, @ByVal CvSize winStride/*=cvSize()*/, @ByVal CvSize padding/*=cvSize()*/, double scale/*=1.05*/,
                double finalThreshold/*=2.0*/, @Cast("bool") boolean useMeanshiftGrouping/*=false*/);

        public native void computeGradient(@InputMat CvArr img, @InputMat CvArr grad,
                @InputMat CvArr angleOfs, @ByVal CvSize paddingTL/*=Size()*/, @ByVal CvSize paddingBR/*=Size()*/);

        public static native @StdVector FloatPointer getDefaultPeopleDetector();
        public static native @StdVector FloatPointer getDaimlerPeopleDetector();

        public native @ByVal CvSize winSize();     public native HOGDescriptor winSize(CvSize winSize);
        public native @ByVal CvSize blockSize();   public native HOGDescriptor blockSize(CvSize blockSize);
        public native @ByVal CvSize blockStride(); public native HOGDescriptor blockStride(CvSize blockStride);
        public native @ByVal CvSize cellSize();    public native HOGDescriptor cellSize(CvSize cellSize);
        public native int nbins();                 public native HOGDescriptor nbins(int nbins);
        public native int derivAperture();         public native HOGDescriptor derivAperture(int derivAperture);
        public native double winSigma();           public native HOGDescriptor winSigma(double winSigma);
        public native int histogramNormType();     public native HOGDescriptor histogramNormType(int histogramNormType);
        public native double L2HysThreshold();     public native HOGDescriptor L2HysThreshold(double L2HysThreshold);
        @Cast("bool")
        public native boolean gammaCorrection();   public native HOGDescriptor gammaCorrection(boolean gammaCorrection);
        @Const @StdVector
        public native FloatPointer svmDetector();  public native HOGDescriptor svmDetector(FloatPointer svmDetector);
        public native int nlevels();               public native HOGDescriptor nlevels(int nlevels);

        public native void detectROI(@InputMat CvArr img, @Const @StdVector("CvPoint,cv::Point") CvPoint locations,
                @StdVector("CvPoint,cv::Point") CvPoint foundLocations,
                @StdVector DoublePointer confidences, double hitThreshold/*=0*/,
                @ByVal CvSize winStride/*=cvSize(0,0)*/, @ByVal CvSize padding/*=cvSize(0,0)*/);

        public native void detectMultiScaleROI(@InputMat CvArr img, @StdVector("CvRect,cv::Rect") CvRect foundLocations,
                @StdVector DetectionROI locations, double hitThreshold/*=0*/, int groupThreshold/*=0*/);

        public native void readALTModel(String modelfile);
    }


    @Namespace("cv") public static native void findDataMatrix(@InputArray CvArr image,
            @ByRef StringVector codes, @OutputArray CvMat corners/*=null*/,
            @OutputArray CvMatArray dmtx/*=null*/);
    @Namespace("cv") public static native void drawDataMatrixCodes(@InputArray CvArr image,
            @ByRef StringVector codes, @InputArray CvArr corners);

    public static class CvDataMatrixCode extends Pointer {
        static { load(); }
        public CvDataMatrixCode() { allocate(); }
        public CvDataMatrixCode(int size) { allocateArray(size); }
        public CvDataMatrixCode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvDataMatrixCode position(int position) {
            return (CvDataMatrixCode)super.position(position);
        }

        public native String msg();     public native CvDataMatrixCode msg(String msg);
        public native CvMat original(); public native CvDataMatrixCode original(CvMat original);
        public native CvMat corners();  public native CvDataMatrixCode corners(CvMat corners);
    }

    @Name("std::deque<CvDataMatrixCode>")
    public static class CvDataMatrixCodeDeque extends Pointer {
        static { load(); }
        public CvDataMatrixCodeDeque()       { allocate();  }
        public CvDataMatrixCodeDeque(long n) { allocate(n); }
        public CvDataMatrixCodeDeque(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);

        @Index @ByRef public native CvDataMatrixCode get(@Cast("size_t") long i);
        public native CvDataMatrixCodeDeque put(@Cast("size_t") long i, CvDataMatrixCode value);
    }

    public static native @ByVal CvDataMatrixCodeDeque cvFindDataMatrix(CvMat im);


    @NoOffset @Namespace("cv::linemod") public static class Feature extends Pointer {
        static { load(); }
        public Feature() { allocate(); }
        public Feature(int x, int y, int label) { allocate(x, y, label); }
        public Feature(int size) { allocateArray(size); }
        public Feature(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int x, int y, int label);
        private native void allocateArray(int size);

        @Override public Feature position(int position) {
            return (Feature)super.position(position);
        }

        public native int x();     public native Feature x(int x);
        public native int y();     public native Feature y(int y);
        public native int label(); public native Feature label(int label);

        public native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);
    }

    @NoOffset @Namespace("cv::linemod") public static class Template extends Pointer {
        static { load(); }
        public Template() { allocate(); }
        public Template(int size) { allocateArray(size); }
        public Template(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Template position(int position) {
            return (Template)super.position(position);
        }

        public native int width();         public native Template width(int width);
        public native int height();        public native Template height(int height);
        public native int pyramid_level(); public native Template pyramid_level(int pyramid_level);
        @Const @StdVector
        public native Feature features();  public native Template features(Feature features);

        public native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);
    }

    @Namespace("cv::linemod") public static class QuantizedPyramid extends Pointer {
        static { load(); }
        public QuantizedPyramid() { }
        public QuantizedPyramid(Pointer p) { super(p); }

        public /*abstract*/ native void quantize(@InputMat CvMat dst);
        public /*abstract*/ native @Cast("bool") boolean extractTemplate(@ByRef Template templ);
        public /*abstract*/ native void pyrDown();

//        protected static class Candidate extends Pointer {
//            static { load(); }
//            public Candidate() { allocate(); }
//            public Candidate(int x, int y, int label, float score) { allocate(x, y, label, score); }
//            public Candidate(int size) { allocateArray(size); }
//            public Candidate(Pointer p) { super(p); }
//            private native void allocate();
//            private native void allocate(int x, int y, int label, float score);
//            private native void allocateArray(int size);
//
//            @Override public Candidate position(int position) {
//                return (Candidate)super.position(position);
//            }
//
//            public native @Cast("bool") @Name("operator<") boolean compare (@ByRef Candidate rhs);
//
//            public native @ByRef Feature f(); public native Candidate f(Feature f);
//            public native float score();      public native Candidate score(float score);
//        }
//
//        protected native static void selectScatteredFeatures(@Const @StdVector Candidate candidates,
//                @Const @StdVector Feature features, @Cast("size_t") long num_features, float distance);
    }

    @Namespace("cv::linemod") public static class Modality extends Pointer {
        static { load(); }
        public Modality() { }
        public Modality(Pointer p) { super(p); }

        public native @Ptr QuantizedPyramid process(@InputMat CvArr src, @InputMat CvArr mask/*=null*/);

        public /*abstract*/ native @ByRef String name();

        public /*abstract*/ native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public /*abstract*/ native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);

        public static native @Ptr Modality create(String modality_type);
        public static native @Ptr Modality create(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);

//        protected /*abstract*/ native @Ptr QuantizedPyramid processImpl(@InputMat CvArr src, @InputMat CvArr mask/*=null*/);
    }

    @NoOffset @Namespace("cv::linemod") public static class ColorGradient extends Modality {
        static { load(); }
        public ColorGradient() { allocate(); }
        public ColorGradient(float weak_threshold, @Cast("size_t") long num_features, float strong_threshold) {
            allocate(weak_threshold, num_features, strong_threshold);
        }
        public ColorGradient(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float weak_threshold, @Cast("size_t") long num_features, float strong_threshold);

//        public native String name();
//
//        public native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);

        public native float weak_threshold();   public native ColorGradient weak_threshold(float weak_threshold);
        public native long num_features();      public native ColorGradient num_features(long num_features);
        public native float strong_threshold(); public native ColorGradient strong_threshold(float strong_threshold);

//        protected native @Ptr QuantizedPyramid processImpl(@InputMat CvArr src, @InputMat CvArr mask/*=null*/);
    }

    @NoOffset @Namespace("cv::linemod") public static class DepthNormal extends Modality {
        static { load(); }
        public DepthNormal() { allocate(); }
        public DepthNormal(int distance_threshold, int difference_threshold, @Cast("size_t") long num_features, int extract_threshold) {
            allocate(distance_threshold, difference_threshold, num_features, extract_threshold);
        }
        public DepthNormal(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int distance_threshold, int difference_threshold, @Cast("size_t") long num_features, int extract_threshold);

//        public native String name();
//
//        public native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);

        public native int distance_threshold();   public native DepthNormal distance_threshold(int distance_threshold);
        public native int difference_threshold(); public native DepthNormal difference_threshold(int difference_threshold);
        @Cast("size_t")
        public native long num_features();        public native DepthNormal num_features(long num_features);
        public native int extract_threshold();    public native DepthNormal extract_threshold(int extract_threshold);

//        protected native @Ptr QuantizedPyramid processImpl(@InputMat CvArr src, @InputMat CvArr mask/*=null*/);
    }

//    @Namespace("cv::linemod") public static native void colormap(@InputMat CvArr quantized, @InputMat CvArr dst);

    @NoOffset @Namespace("cv::linemod") public static class Match extends Pointer {
        static { load(); }
        public Match() { allocate(); }
        public Match(int x, int y, float similarity, String class_id, int template_id) {
            allocate(x, y, similarity, class_id, template_id);
        }
        public Match(int size) { allocateArray(size); }
        public Match(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int x, int y, float similarity, String class_id, int template_id);
        private native void allocateArray(int size);

        @Override public Match position(int position) {
            return (Match)super.position(position);
        }

        public native @Cast("bool") @Name("operator<") boolean compare(@ByRef Match rhs);
        public native @Cast("bool") @Name("operator==") boolean equals(@ByRef Match rhs);

        public native int x();            public native Match x(int x);
        public native int y();            public native Match y(int y);
        public native float similarity(); public native Match similarity(float similarity);
        @ByRef
        public native String class_id();  public native Match class_id(String class_id);
        public native int template_id();  public native Match template_id(int template_id);
    }

    @Name("std::vector<cv::Ptr<cv::linemod::Modality> >")
    public static class ModalityVector extends Pointer {
        static { load(); }
        public ModalityVector(Modality ... array) { this(array.length); put(array); }
        public ModalityVector()       { allocate();  }
        public ModalityVector(long n) { allocate(n); }
        public ModalityVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);

        @Index @Const @Ptr public native Modality get(@Cast("size_t") long i);
        public native ModalityVector put(@Cast("size_t") long i, Modality value);

        public ModalityVector put(Modality ... array) {
            if (size() < array.length) { resize(array.length); }
            for (int i = 0; i < array.length; i++) {
                put(i, array[i]);
            }
            return this;
        }
    }

    @Namespace("cv::linemod") public static class Detector extends Pointer {
        static { load(); }
        public Detector() { allocate(); }
        public Detector(@ByRef ModalityVector modalities, @Const @StdVector int[] T_pyramid) {
            allocate(modalities, T_pyramid);
        }
        public Detector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByRef ModalityVector modalities, @Const @StdVector int[] T_pyramid);

        public native void match(@Const(true) @StdVector("IplImage*,cv::Mat") IplImageArray sources, float threshold,
                @StdVector Match matches, @ByRef StringVector class_ids/*=null*/, @InputArray IplImageArray quantized_images/*=null*/,
                @Const(true) @StdVector("IplImage*,cv::Mat") IplImageArray masks/*=null*/);

        public native int addTemplate(@Const(true) @StdVector("IplImage*,cv::Mat") IplImageArray sources,
                String class_id, IplImage object_mask, @Const @Adapter("RectAdapter") CvRect bounding_box/*=null*/);

        public native int addSyntheticTemplate(@Const @StdVector Template templates, String class_id);

        public native @Const @ByRef ModalityVector getModalities();

        public native int getT(int pyramid_level);

        public native int pyramidLevels();

        public native @StdVector Template getTemplates(String class_id, int template_id);

        public native int numTemplates();
        public native int numTemplates(String class_id);
        public native int numClasses();

        public native @ByVal StringVector classIds();

        public native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);

        public native @ByRef String readClass(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn, String class_id_override/*=""*/);
        public native void writeClass(String class_id, @Const @Adapter("FileStorageAdapter") CvFileStorage fs);

        public native void readClasses(@ByRef StringVector class_ids, String format/*="templates_%s.yml.gz"*/);
        public native void writeClasses(String format/*="templates_%s.yml.gz"*/);

//        protected native @Const @ByRef ModalityVector modalities();
//        protected native int pyramid_levels();
//        protected native @StdVector int[] T_at_level();
//
//        protected native std::map<std::string, std::vector<std::vector<Template> > > class_templates;
//
//        protected native void matchClass(const std::vector<std::vector<std::vector<Mat> > >& lm_pyramid,
//                      const std::vector<Size>& sizes,
//                      float threshold, std::vector<Match>& matches,
//                      const std::string& class_id,
//                      const std::vector<std::vector<Template> >& template_pyramids) const;
    }

    @Namespace("cv::linemod") public static native @Ptr Detector getDefaultLINE();
    @Namespace("cv::linemod") public static native @Ptr Detector getDefaultLINEMOD();
}
