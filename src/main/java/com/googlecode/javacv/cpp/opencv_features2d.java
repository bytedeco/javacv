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
 * This file is based on information found in features2d.hpp of OpenCV 2.4.2,
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

import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Index;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_flann.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/features2d/features2d.hpp>", "opencv_adapters.h"},
        link={"opencv_features2d@.2.4", "opencv_flann@.2.4", "opencv_highgui@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_features2d242", "opencv_flann242", "opencv_highgui242", "opencv_imgproc242", "opencv_core242"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_features2d {
    static { load(opencv_highgui.class); load(opencv_flann.class);
        if (load() != null) {
            initModule_features2d();
        }
    }

    @Namespace("cv") public static native @Cast("bool") boolean initModule_features2d();

    @NoOffset @Namespace("cv") public static class KeyPoint extends Pointer {
        static { load(); }
        public KeyPoint() { allocate(); }
        public KeyPoint(int size) { allocateArray(size); }
        public KeyPoint(CvPoint2D32f _pt, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/) {
            allocate(_pt, _size, _angle, _response, _octave, _class_id);
        }
        public KeyPoint(float x, float y, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/) {
            allocate(x, y, _size, _angle, _response, _octave, _class_id);
        }
        public KeyPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByRef CvPoint2D32f _pt, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/);
        private native void allocate(float x, float y, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/);
        private native void allocateArray(int size);

        @Override public KeyPoint position(int position) {
            return (KeyPoint)super.position(position);
        }

        public native long hash();

        public static native void convert(@Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints,
                @Adapter(value="VectorAdapter<CvPoint2D32f,cv::Point2f>", out=true) CvPoint2D32f points2f,
                @Adapter("VectorAdapter<int>") int[] keypointIndexes/*=null*/);
        public static native void convert(@Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f points2f,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                float size/*=1*/, float response/*=1*/, int octave/*=0*/, int class_id/*=-1*/);

        public static native float overlap(@ByRef KeyPoint kp1, @ByRef KeyPoint kp2);

        public native @ByVal CvPoint2D32f pt();   public native KeyPoint pt(CvPoint2D32f pt);
        public native @Name("pt.x") float pt_x(); public native KeyPoint pt_x(float x);
        public native @Name("pt.y") float pt_y(); public native KeyPoint pt_y(float y);
        public native float size();      public native KeyPoint size(float size);
        public native float angle();     public native KeyPoint angle(float angle);
        public native float response();  public native KeyPoint response(float response);
        public native int octave();      public native KeyPoint octave(int octave);
        public native int class_id();    public native KeyPoint class_id(int class_id);
    }

    @Namespace("cv") public static native void write(@Adapter("FileStorageAdapter") CvFileStorage fs,
            String name, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints);
    @Namespace("cv") public static native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs,
            CvFileNode node, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);

    @Namespace("cv") public static class KeyPointsFilter extends Pointer {
        static { load(); }
        public KeyPointsFilter() { allocate(); }
        public KeyPointsFilter(Pointer p) { super(p); }
        private native void allocate();

        public static native void runByImageBorder(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @ByVal CvSize imageSize, int borderSize);
        public static native void runByKeypointSize(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, float minSize, float maxSize/*=Float.MAX_VALUE*/);
        public static native void runByPixelsMask(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask);
        public static native void removeDuplicated(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
    }

    @Name("std::vector<std::vector<cv::KeyPoint> >")
    public static class KeyPointVectorVector extends Pointer {
        static { load(); }
        public KeyPointVectorVector()       { allocate();  }
        public KeyPointVectorVector(long n) { allocate(n); }
        public KeyPointVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index(1) long size(@Cast("size_t") long i);
        public native @Index(1) void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ByRef public native KeyPoint get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native KeyPointVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, KeyPoint value);
    }

    @Name("cv::Ptr<cv::FeatureDetector>")
    public static class FeatureDetectorPtr extends Pointer {
        static { load(); }
        public FeatureDetectorPtr()       { allocate();  }
        public FeatureDetectorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native FeatureDetector get();
        public native FeatureDetectorPtr put(FeatureDetector value);
    }

    @Namespace("cv") public static class FeatureDetector extends Algorithm {
        static { load(); }
        public FeatureDetector() { }
        public FeatureDetector(Pointer p) { super(p); }

        public native void detect(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
        public native void detect(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray images,
                @ByRef KeyPointVectorVector keypoints, @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray masks/*=null*/);
        public native boolean empty();
        public native static @ByVal FeatureDetectorPtr create(String detectorType);

//        protected abstract void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected static native void removeInvalidPoints(@Adapter("MatAdapter") CvArr mask, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
    }

    @Name("cv::Ptr<cv::DescriptorExtractor>")
    public static class DescriptorExtractorPtr extends Pointer {
        static { load(); }
        public DescriptorExtractorPtr()       { allocate();  }
        public DescriptorExtractorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native DescriptorExtractor get();
        public native DescriptorExtractorPtr put(DescriptorExtractor value);
    }

    @Namespace("cv") public static class DescriptorExtractor extends Algorithm {
        static { load(); }
        public DescriptorExtractor() { }
        public DescriptorExtractor(Pointer p) { super(p); }

        public native void compute(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
        public native void compute(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray images,
                @ByRef KeyPointVectorVector keypoints,
                @Adapter(value="VectorAdapter<CvMat*,cv::Mat>"/*, out=true*/) CvMatArray descriptors);

        public /*abstract*/ native int descriptorSize();
        public /*abstract*/ native int descriptorType();

        public native boolean empty();

        public static native @ByVal DescriptorExtractorPtr create(String descriptorExtractorType);

//        protected abstract native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
//        protected static native void removeBorderKeypoints(
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @ByVal CvSize imageSize, int borderSize);
    }

    @Name("cv::Ptr<cv::Feature2D>")
    public static class Feature2DPtr extends Pointer {
        static { load(); }
        public Feature2DPtr()       { allocate();  }
        public Feature2DPtr(Pointer p) { super(p); }
        private native void allocate();

        public native Feature2D get();
        public native Feature2DPtr put(Feature2D value);
    }

    public static native @Name("dynamic_cast<cv::FeatureDetector*>") FeatureDetector castFeatureDetector(Feature2D pointer);
    public static native @Name("dynamic_cast<cv::DescriptorExtractor*>") DescriptorExtractor castDescriptorExtractor(Feature2D pointer);

    @Namespace("cv") public static class Feature2D extends /*FeatureDetector, DescriptorExtractor*/ Pointer {
        static { load(); }
        public Feature2D() { }
        public Feature2D(Pointer p) { super(p); }

        public /*abstract*/ native @Name("operator()") void detectAndCompute(@Adapter("ArrayAdapter") CvArr image,
                @Adapter("ArrayAdapter") CvArr mask, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="ArrayAdapter", out=true) CvMat descriptors, @Cast("bool") boolean useProvidedKeypoints/*=false*/);

        public FeatureDetector getFeatureDetector() { return castFeatureDetector(this); }
        public DescriptorExtractor getDescriptorExtractor() { return castDescriptorExtractor(this); }

//        public static native @ByVal Feature2DPtr create(String name);
    }

    @NoOffset @Namespace("cv") public static class ORB extends Feature2D {
        static { load(); }
        public static final int kBytes = 32, HARRIS_SCORE=0, FAST_SCORE=1;
        public ORB() { allocate(); }
        public ORB(Pointer p) { super(p); }
        public ORB(int nfeatures/*=500*/, float scaleFactor/*=1.2*/, int nlevels/*=8*/, int edgeThreshold/*=31*/,
                int firstLevel/*=0*/, int WTA_K/*=2*/, int scoreType/*=HARRIS_SCORE*/, int patchSize/*=31*/) {
            allocate(nfeatures, scaleFactor, nlevels, edgeThreshold, firstLevel, WTA_K, scoreType, patchSize);
        }
        private native void allocate();
        private native void allocate(int nfeatures/*=500*/, float scaleFactor/*=1.2*/, int nlevels/*=8*/, int edgeThreshold/*=31*/,
                int firstLevel/*=0*/, int WTA_K/*=2*/, int scoreType/*=HARRIS_SCORE*/, int patchSize/*=31*/);

        public native int descriptorSize();
        public native int descriptorType();

        public native @Name("operator()") void detect(@Adapter("ArrayAdapter") CvArr image, @Adapter("ArrayAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
        public native @Name("operator()") void detect(@Adapter("ArrayAdapter") CvArr image, @Adapter("ArrayAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="ArrayAdapter", out=true) CvMat descriptors, @Cast("bool") boolean useProvidedKeypoints/*=false*/);

        public native AlgorithmInfo info();

//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter(value="MatAdapter", out=true) CvMat descriptors);
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//
//        protected native int nfeatures();
//        protected native double scaleFactor();
//        protected native int nlevels();
//        protected native int edgeThreshold();
//        protected native int firstLevel();
//        protected native int WTA_K();
//        protected native int scoreType();
//        protected native int patchSize();
    }


    @Namespace("cv") public static class FREAK extends DescriptorExtractor {
        static { load(); }
        public static final int kBytes = 32, HARRIS_SCORE=0, FAST_SCORE=1;
        public FREAK() { allocate(); }
        public FREAK(Pointer p) { super(p); }
//        public FREAK(FREAK rhs) { allocate(rhs); }
        public FREAK(boolean orientationNormalized/*=true*/, boolean scaleNormalized/*=true*/,
               float patternScale/*=22.0f*/, int nOctaves/*=4*/, @Adapter("VectorAdapter<int>") IntPointer selectedPairs/*=null*/) {
            allocate(orientationNormalized, scaleNormalized, patternScale, nOctaves, selectedPairs);
        }
        private native void allocate();
//        private native void allocate(@ByRef FREAK rhs);
        private native void allocate(boolean orientationNormalized/*=true*/, boolean scaleNormalized/*=true*/,
               float patternScale/*=22.0f*/, int nOctaves/*=4*/, @Adapter("VectorAdapter<int>") IntPointer selectedPairs/*=null*/);

//        public native @Name("operator=") @ByRef FREAK copy(@ByRef FREAK rhs);

//        public native int descriptorSize();
//        public native int descriptorType();

        public native @Adapter("VectorAdapter<int>") IntPointer selectPairs(@ByRef MatVector images,
                @ByRef KeyPointVectorVector keypoints, double corrThresh/*=0.7*/, boolean verbose/*=true*/);

//        public native AlgorithmInfo info();

        public static final int
                NB_SCALES = 64, NB_PAIRS = 512, NB_ORIENPAIRS = 45;

//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter(value="MatAdapter", out=true) CvMat descriptors);
//        protected native void buildPattern();
//        protected native @Cast("uchar") byte meanIntensity(@Adapter("MatAdapter") CvArr image, @Adapter("MatAdapter") CvArr integral,
//                float kp_x, float kp_y, @Cast("unsigned") int scale, @Cast("unsigned") int rot, @Cast("unsigned") int point);
//
//        protected native @Cast("bool") boolean orientationNormalized();
//        protected native @Cast("bool") boolean scaleNormalized();
//        protected native double patternScale();
//        protected native int nOctaves();
//        protected native @Cast("bool") boolean extAll();
//
//        protected native double patternScale0();
//        protected native int nOctaves0();
//        protected native @Adapter("VectorAdapter<int>") IntPointer selectedPairs0();
//
//        protected static class PatternPoint extends Pointer {
//            public native float x();
//            public native float y();
//            public native float sigma();
//        }
//
//        protected static class  DescriptionPair extends Pointer {
//            public native @Cast("uchar") byte i();
//            public native @Cast("uchar") byte j();
//        }
//
//        protected static class  OrientationPair extends Pointer {
//            public native @Cast("uchar") byte i();
//            public native @Cast("uchar") byte j();
//            public native int weight_dx();
//            public native int weight_dy();
//        }
//
//        protected native @Adapter("VectorAdapter<PatternPoint>") PatternPoint patternLookup();
//        protected native int patternSizes(int i); // [NB_SCALES];
//        protected native DescriptionPair descriptionPairs(int i); // [NB_PAIRS];
//        protected native OrientationPair orientationPairs(int i); // [NB_ORIENPAIRS];
    }


    @Namespace("cv") public static class MSER extends FeatureDetector {
        static { load(); }
        public MSER() { allocate(); }
        public MSER(Pointer p) { super(p); }
        public MSER(int _delta/*=5*/, int _min_area/*=60*/, int _max_area/*=14400*/,
                double _max_variation/*=0.25*/, double _min_diversity/*=0.2*/,
                int _max_evolution/*=200*/, double _area_threshold/*=1.01*/,
                double _min_margin/*=0.003*/, int _edge_blur_size/*=5*/) {
            allocate(_delta, _min_area, _max_area, _max_variation, _min_diversity,
                    _max_evolution, _area_threshold, _min_margin, _edge_blur_size);
        }
        private native void allocate();
        private native void allocate(int _delta/*=5*/, int _min_area/*=60*/, int _max_area/*=14400*/,
                double _max_variation/*=0.25*/, double _min_diversity/*=0.2*/,
                int _max_evolution/*=200*/, double _area_threshold/*=1.01*/,
                double _min_margin/*=0.003*/, int _edge_blur_size/*=5*/);

//        public native void detect(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint msers, @Adapter("MatAdapter") CvArr mask/*=null*/);
//
//        public native AlgorithmInfo info();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native int delta();
//        protected native int minArea();
//        protected native int maxArea();
//        protected native double maxVariation();
//        protected native double minDiversity();
//        protected native int maxEvolution();
//        protected native double areaThreshold();
//        protected native double minMargin();
//        protected native int edgeBlurSize();
    }

    @Namespace("cv") public static class StarDetector extends FeatureDetector {
        static { load(); }
        public StarDetector() { allocate(); }
        public StarDetector(Pointer p) { super(p); }
        public StarDetector(int _maxSize/*=45*/, int _responseThreshold/*=30*/,
                int _lineThresholdProjected/*=10*/, int _lineThresholdBinarized/*=8*/, int _suppressNonmaxSize/*=5*/) {
            allocate(_maxSize, _responseThreshold, _lineThresholdProjected, _lineThresholdBinarized, _suppressNonmaxSize);
        }
        private native void allocate();
        private native void allocate(int _maxSize/*=45*/, int _responseThreshold/*=30*/,
                int _lineThresholdProjected/*=10*/, int _lineThresholdBinarized/*=8*/, int _suppressNonmaxSize/*=5*/);

        public native void detect(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);

//        public native AlgorithmInfo info();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native int maxSize();
//        protected native int responseThreshold();
//        protected native int lineThresholdProjected();
//        protected native int lineThresholdBinarized();
//        protected native int suppressNonmaxSize();
    }

    @Namespace("cv") public static native void FAST(@Adapter("ArrayAdapter") CvArr image,
            @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
            int threshold, @Cast("bool") boolean nonmaxSupression/*=true*/);

    @Namespace("cv") public static class FastFeatureDetector extends FeatureDetector {
        static { load(); }
        public FastFeatureDetector() { }
        public FastFeatureDetector(Pointer p) { super(p); }
        public FastFeatureDetector(int threshold/*=10*/, boolean nonmaxSuppression/*=true*/) {
            allocate(threshold, nonmaxSuppression);
        }
        private native void allocate(int threshold/*=10*/, @Cast("bool") boolean nonmaxSuppression/*=true*/);

//        public native AlgorithmInfo info();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native int threshold();
//        protected native @Cast("bool") boolean nonmaxSuppression();
    }

    @Namespace("cv") public static class GFTTDetector extends FeatureDetector {
        static { load(); }
        public GFTTDetector() { allocate(); }
        public GFTTDetector(Pointer p) { super(p); }
        public GFTTDetector(int maxCorners/*=1000*/, double qualityLevel/*=0.01*/, double minDistance/*=1*/,
                int blockSize/*=3*/, @Cast("bool") boolean useHarrisDetector/*=false*/, double k/*=0.04*/) {
            allocate(maxCorners, qualityLevel, minDistance, blockSize, useHarrisDetector, k);
        }
        private native void allocate();
        private native void allocate(int maxCorners/*=1000*/, double qualityLevel/*=0.01*/, double minDistance/*=1*/,
                int blockSize/*=3*/, @Cast("bool") boolean useHarrisDetector/*=false*/, double k/*=0.04*/);

//        public native AlgorithmInfo info();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native int nfeatures();
//        protected native double qualityLevel();
//        protected native double minDistance();
//        protected native int blockSize();
//        protected native @Cast("bool") boolean useHarrisDetector();
//        protected native double k();
    }

    @Namespace("cv") public static class SimpleBlobDetector extends FeatureDetector {
        static { load(); }
        public SimpleBlobDetector() { allocate(); }
        public SimpleBlobDetector(Pointer p) { super(p); }
        public SimpleBlobDetector(@ByRef Params parameters/*=new Params()*/) {
            allocate(parameters);
        }
        private native void allocate();
        private native void allocate(@ByRef Params parameters/*=new Params()*/);

        @NoOffset public static class Params extends Pointer {
            static { load(); }
            public Params() { allocate(); }
            public Params(int size) { allocateArray(size); }
            public Params(Pointer p) { super(p); }
            private native void allocate();
            private native void allocateArray(int size);

            @Override public Params position(int position) {
                return (Params)super.position(position);
            }

            public native float thresholdStep();         public native Params thresholdStep(float thresholdStep);
            public native float minThreshold();          public native Params minThreshold(float minThreshold);
            public native float maxThreshold();          public native Params maxThreshold(float maxThreshold);
            @Cast("size_t")
            public native long minRepeatability();       public native Params minRepeatability(long minRepeatability);
            public native float minDistBetweenBlobs();   public native Params minDistBetweenBlobs(float minDistBetweenBlobs);
            @Cast("bool")
            public native boolean filterByColor();       public native Params filterByColor(boolean filterByColor);
            public native byte blobColor();              public native Params blobColor(byte blobColor);
            @Cast("bool")
            public native boolean filterByArea();        public native Params filterByArea(boolean filterByArea);
            public native float minArea();               public native Params minArea(float minArea);
            public native float maxArea();               public native Params maxArea(float maxArea);
            @Cast("bool")
            public native boolean filterByCircularity(); public native Params filterByCircularity(boolean filterByCircularity);
            public native float minCircularity();        public native Params minCircularity(float minCircularity);
            public native float maxCircularity();        public native Params maxCircularity(float maxCircularity);
            @Cast("bool")
            public native boolean filterByInertia();     public native Params filterByInertia(boolean filterByInertia);
            public native float minInertiaRatio();       public native Params minInertiaRatio(float minInertiaRatio);
            public native float maxInertiaRatio();       public native Params maxInertiaRatio(float maxInertiaRatio);
            @Cast("bool")
            public native boolean filterByConvexity();   public native Params filterByConvexity(boolean filterByConvexity);
            public native float minConvexity();          public native Params minConvexity(float minConvexity);
            public native float maxConvexity();          public native Params maxConvexity(float maxConvexity);

            public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
            public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
        }

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected static class Center extends Pointer {
//            public native @ByVal CvPoint2D32f location();
//            public native double radius();
//            public native double confidence();
//        }
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native void findBlobs(@Adapter("MatAdapter") CvArr image, @Adapter("MatAdapter") CvArr binaryImage, @Adapter(value="VectorAdapter<cv::SimpleBlobDetector::Center>", out=true) Center centers);
//
//        protected native Params params();
    }

    @Namespace("cv") public static class DenseFeatureDetector extends FeatureDetector {
        static { load(); }
        public DenseFeatureDetector() { allocate(); }
        public DenseFeatureDetector(Pointer p) { super(p); }
        public DenseFeatureDetector(float initFeatureScale/*=1.0*/, int featureScaleLevels/*=1*/,
                float featureScaleMul/*=0.1*/, int initXyStep/*=6*/, int initImgBound/*=0*/,
                @Cast("bool") boolean varyXyStepWithScale/*=true*/, @Cast("bool") boolean varyImgBoundWithScale/*=false*/) {
            allocate(initFeatureScale, featureScaleLevels, featureScaleMul, initXyStep,
                    initImgBound, varyXyStepWithScale, varyImgBoundWithScale); }
        private native void allocate();
        private native void allocate(float initFeatureScale/*=1.0*/, int featureScaleLevels/*=1*/,
                float featureScaleMul/*=0.1*/, int initXyStep/*=6*/, int initImgBound/*=0*/,
                @Cast("bool") boolean varyXyStepWithScale/*=true*/, @Cast("bool") boolean varyImgBoundWithScale/*=false*/);

//        public native AlgorithmInfo info();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native double initFeatureScale();
//        protected native int featureScaleLevels();
//        protected native double featureScaleMul();
//
//        protected native int initXyStep();
//        protected native int initImgBound();
//
//        protected native @Cast("bool") boolean varyXyStepWithScale();
//        protected native @Cast("bool") boolean varyImgBoundWithScale();
    }

    @Namespace("cv") public static class GridAdaptedFeatureDetector extends FeatureDetector {
        static { load(); }
        public GridAdaptedFeatureDetector() { allocate(); }
        public GridAdaptedFeatureDetector(Pointer p) { super(p); }
        public GridAdaptedFeatureDetector(@ByRef FeatureDetectorPtr detector/*=null*/, int maxTotalKeypoints/*=1000*/,
                int gridRows/*=4*/, int gridCols/*=4*/) {
            allocate(detector, maxTotalKeypoints, gridRows, gridCols);
        }
        private native void allocate();
        private native void allocate(@ByRef FeatureDetectorPtr detector/*=null*/, int maxTotalKeypoints/*=1000*/,
                int gridRows/*=4*/, int gridCols/*=4*/);

//        public native boolean empty();
//
//        public native AlgorithmInfo info();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native @ByRef FeatureDetectorPtr detector();
//        protected native int maxTotalKeypoints();
//        protected native int gridRows();
//        protected native int gridCols();
    }

    @Namespace("cv") public static class PyramidAdaptedFeatureDetector extends FeatureDetector {
        static { load(); }
        public PyramidAdaptedFeatureDetector() { }
        public PyramidAdaptedFeatureDetector(Pointer p) { super(p); }
        public PyramidAdaptedFeatureDetector(@ByRef FeatureDetectorPtr detector, int maxLevel/*=2*/) {
            allocate(detector, maxLevel);
        }
        private native void allocate(@ByRef FeatureDetectorPtr detector, int maxLevel/*=2*/);

//        public native boolean empty();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native @ByRef FeatureDetectorPtr detector();
//        protected native int maxLevel();
    }


    @Name("cv::Ptr<cv::AdjusterAdapter>")
    public static class AdjusterAdapterPtr extends Pointer {
        static { load(); }
        public AdjusterAdapterPtr()       { allocate();  }
        public AdjusterAdapterPtr(Pointer p) { super(p); }
        private native void allocate();

        public native AdjusterAdapter get();
        public native AdjusterAdapterPtr put(AdjusterAdapter value);
    }

    @Namespace("cv") public static class AdjusterAdapter extends FeatureDetector {
        static { load(); }
        public AdjusterAdapter() { }
        public AdjusterAdapter(Pointer p) { super(p); }

    	public /*abstract*/ native void tooFew(int min, int n_detected);
    	public /*abstract*/ native void tooMany(int max, int n_detected);
    	public /*abstract*/ native boolean good();

//        public /*abstract*/ native AdjusterAdapterPtr clone();

        public static native @Name("create") @ByVal AdjusterAdapterPtr createAdjusterAdapter(String detectorType);
    }

    @Namespace("cv") public static class DynamicAdaptedFeatureDetector extends FeatureDetector {
        static { load(); }
        public DynamicAdaptedFeatureDetector() { }
        public DynamicAdaptedFeatureDetector(Pointer p) { super(p); }
        public DynamicAdaptedFeatureDetector(@ByRef AdjusterAdapterPtr adjuster,
                int min_features/*=400*/, int max_features/*=500*/, int max_iters/*=5*/) {
            allocate(adjuster, min_features, max_features, max_iters);
        }
        private native void allocate(@ByRef AdjusterAdapterPtr adjuster,
                int min_features/*=400*/, int max_features/*=500*/, int max_iters/*=5*/);

//        public native boolean empty();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
    }

    @Namespace("cv") public static class FastAdjuster extends AdjusterAdapter {
        static { load(); }
        public FastAdjuster() { allocate(); }
        public FastAdjuster(Pointer p) { super(p); }
        public FastAdjuster(int init_thresh/*=20*/, boolean nonmax/*=true*/, int min_thresh/*=1*/, int max_thresh/*=200*/) {
            allocate(init_thresh, nonmax, min_thresh, max_thresh);
        }
        private native void allocate();
        private native void allocate(int init_thresh/*=20*/, @Cast("bool") boolean nonmax/*=true*/, int min_thresh/*=1*/, int max_thresh/*=200*/);

//        public native void tooFew(int min, int n_detected);
//        public native void tooMany(int max, int n_detected);
//        public native boolean good();
//
//        public native AdjusterAdapterPtr clone();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native int thresh_();
//        protected native boolean nonmax_();
//        protected int init_thresh_, min_thresh_, max_thresh_;
    }

    @Namespace("cv") public static class StarAdjuster extends AdjusterAdapter {
        static { load(); }
        public StarAdjuster() { allocate(); }
        public StarAdjuster(Pointer p) { super(p); }
        public StarAdjuster(double initial_thresh/*=30.0*/, double min_thresh/*=2.*/, double max_thresh/*=200.*/) {
            allocate(initial_thresh, min_thresh, max_thresh);
        }
        private native void allocate();
        private native void allocate(double initial_thresh/*=30.0*/, double min_thresh/*=2.*/, double max_thresh/*=200.*/);

//        public native void tooFew(int min, int n_detected);
//        public native void tooMany(int max, int n_detected);
//        public native boolean good();
//
//        public native AdjusterAdapterPtr clone();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native double thresh_();
//        protected native double init_thresh_();
//        protected native double min_thresh_();
//        protected native double max_thresh_();
    }

    @Namespace("cv") public static class SurfAdjuster extends AdjusterAdapter {
        static { load(); }
        public SurfAdjuster() { allocate(); }
        public SurfAdjuster(double initial_thresh/*=400.f*/, double min_thresh/*=2*/, double max_thresh/*=1000*/) {
            allocate(initial_thresh, min_thresh, max_thresh);
        }
        public SurfAdjuster(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(double initial_thresh/*=400.f*/, double min_thresh/*=2*/, double max_thresh/*=1000*/);

//        public native void tooFew(int min, int n_detected);
//        public native void tooMany(int max, int n_detected);
//        public native boolean good();
//
//        public native AdjusterAdapterPtr clone();

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);

//        protected native double thresh_();
//        protected native double init_thresh_();
//        protected native double min_thresh_();
//        protected native double max_thresh_();
    }

    public static native @Adapter("MatAdapter") CvMat windowedMatchingMask(
            @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints1,
            @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints2,
            float maxDeltaX, float maxDeltaY);


    @Namespace("cv") public static class OpponentColorDescriptorExtractor extends DescriptorExtractor {
        static { load(); }
        public OpponentColorDescriptorExtractor() { }
        public OpponentColorDescriptorExtractor(Pointer p) { super(p); }
        public OpponentColorDescriptorExtractor(@ByRef DescriptorExtractorPtr descriptorExtractor) {
            allocate(descriptorExtractor);
        }
        private native void allocate(@ByRef DescriptorExtractorPtr descriptorExtractor);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        public native int descriptorSize();
//        public native int descriptorType();
//
//        public native boolean empty();

//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);

//        protected native @ByRef DescriptorExtractorPtr descriptorExtractor();
    }

    @Namespace("cv") public static class BriefDescriptorExtractor extends DescriptorExtractor {
        static { load(); }
        public static final int
                PATCH_SIZE = 48,
                KERNEL_SIZE = 9;

        public BriefDescriptorExtractor() { allocate(); }
        public BriefDescriptorExtractor(Pointer p) { super(p); }
        public BriefDescriptorExtractor(int bytes/*=32*/) {
            allocate(bytes);
        }
        private native void allocate();
        private native void allocate(int bytes/*=32*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        public native int descriptorSize();
//        public native int descriptorType();

//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);

//        typedef void(*PixelTestFn)(const Mat&, const std::vector<KeyPoint>&, Mat&);
//        protected native int bytes_();
//        protected native PixelTestFn test_fn_();
    }


    @Namespace("cv") public static class Hamming extends Pointer {
        static { load(); }
        public Hamming() { allocate(); }
        public Hamming(Pointer p) { super(p); }
        private native void allocate();

        public static final int normType = NORM_HAMMING;

        public native @Name("operator()") int d(@Cast("unsigned char*") byte[] a,
                @Cast("unsigned char*") byte[] b, int size);
    }

    @Name("cv::HammingMultilevel<2>") public static class HammingMultilevel2 extends Pointer {
        static { load(); }
        public HammingMultilevel2() { allocate(); }
        public HammingMultilevel2(Pointer p) { super(p); }
        private native void allocate();

        public native @Name("operator()") int d(@Cast("unsigned char*") byte[] a,
                @Cast("unsigned char*") byte[] b, int size);
    }
    @Name("cv::HammingMultilevel<4>") public static class HammingMultilevel4 extends Pointer {
        static { load(); }
        public HammingMultilevel4() { allocate(); }
        public HammingMultilevel4(Pointer p) { super(p); }
        private native void allocate();

        public native @Name("operator()") int d(@Cast("unsigned char*") byte[] a,
                @Cast("unsigned char*") byte[] b, int size);
    }


    @NoOffset @Namespace("cv") public static class DMatch extends Pointer {
        static { load(); }
        public DMatch() { allocate(); }
        public DMatch(int _queryIdx, int _trainIdx, float _distance) {
            allocate(_queryIdx, _trainIdx, _distance);
        }
        public DMatch(int _queryIdx, int _trainIdx, int _imgIdx, float _distance) {
            allocate(_queryIdx, _trainIdx, _imgIdx, _distance);
        }
        public DMatch(int size) { allocateArray(size); }
        public DMatch(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _queryIdx, int _trainIdx, float _distance);
        private native void allocate(int _queryIdx, int _trainIdx, int _imgIdx, float _distance);
        private native void allocateArray(int size);

        @Override public DMatch position(int position) {
            return (DMatch)super.position(position);
        }

        public native int queryIdx();   public native DMatch queryIdx(int queryIdx);
        public native int trainIdx();   public native DMatch trainIdx(int trainIdx);
        public native int imgIdx();     public native DMatch imgIdx(int imgIdx);

        public native float distance(); public native DMatch distance(float distance);

        public native @Name("operator<") boolean compare(@ByRef DMatch m);
    }

    @Name("std::vector<std::vector<cv::DMatch> >")
    public static class DMatchVectorVector extends Pointer {
        static { load(); }
        public DMatchVectorVector()       { allocate();  }
        public DMatchVectorVector(long n) { allocate(n); }
        public DMatchVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index(1) long size(@Cast("size_t") long i);
        public native @Index(1) void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ByRef public native DMatch get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native DMatchVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, DMatch value);
    }

    @Name("cv::Ptr<cv::DescriptorMatcher>")
    public static class DescriptorMatcherPtr extends Pointer {
        static { load(); }
        public DescriptorMatcherPtr()       { allocate();  }
        public DescriptorMatcherPtr(Pointer p) { super(p); }
        private native void allocate();

        public native DescriptorMatcher get();
        public native DescriptorMatcherPtr put(DescriptorMatcher value);
    }

    @Namespace("cv") public static class DescriptorMatcher extends Algorithm {
        static { load(); }
        public DescriptorMatcher() { }
        public DescriptorMatcher(Pointer p) { super(p); }

        public native void add(@ByRef MatVector descriptors);
        public native @Const @ByRef MatVector getTrainDescriptors();
        public native void clear();
        public native boolean empty();
        public /*abstract*/ native boolean isMaskSupported();
        public native void train();

        public native void match(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter("MatAdapter") CvArr trainDescriptors,
                @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches, @Adapter("MatAdapter") CvArr mask/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter("MatAdapter") CvArr trainDescriptors,
                @ByRef DMatchVectorVector matches, int k, @Adapter("MatAdapter") CvArr mask/*=null*/, @Cast("bool") boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter("MatAdapter") CvArr trainDescriptors,
                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("MatAdapter") CvArr mask/*=null*/, @Cast("bool") boolean compactResult/*=false*/);

        public native void match(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/, @Cast("bool") boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/, @Cast("bool") boolean compactResult/*=false*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

        public /*abstract*/ native @ByVal DescriptorMatcherPtr clone(@Cast("bool") boolean emptyTrainData/*=false*/);

        public native static @ByVal DescriptorMatcherPtr create(String descriptorMatcherType);

//        protected static class DescriptorCollection extends Pointer {
//            static { load(); }
//            public DescriptorCollection() { allocate(); }
//            public DescriptorCollection(Pointer p) { super(p); }
//            public DescriptorCollection(DescriptorCollection collection) { allocate(collection); }
//            private native void allocate();
//            private native void allocate(@ByRef DescriptorCollection collection);
//
//            public native void set(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray descriptors);
//            public native void clear();
//
//            public native @Adapter("MatAdapter") CvMat getDescriptors();
//            public native @Adapter("MatAdapter") CvMat getDescriptor(int imgIdx, int localDescIdx);
//            public native @Adapter("MatAdapter") CvMat getDescriptor(int globalDescIdx);
//            public native void getLocalIdx(int globalDescIdx, @ByRef int[] imgIdx, @ByRef int[] localDescIdx);
//
//            public native int size();
//
//            protected native @Adapter("MatAdapter") CvMat mergedDescriptors();
//            protected native @Adapter("VectorAdapter<int>") int[] startIdxs();
//        }
//
//        protected /*abstract*/ native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, @Cast("bool") boolean compactResult/*=false*/);
//        protected /*abstract*/ native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, @Cast("bool") boolean compactResult/*=false*/);
//
//        protected native static boolean isPossibleMatch(@Adapter("MatAdapter") CvArr mask, int queryIdx, int trainIdx);
//        protected native static boolean isMaskedOut(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvArrArray masks, int queryIdx);
//
//        protected static native @Adapter("MatAdapter") CvMat clone_op(@Adapter("MatAdapter") CvMat m);
//        protected static native @Adapter("MatAdapter") IplImage clone_op(@Adapter("MatAdapter") IplImage m);
//        protected native void checkMasks(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, int queryDescriptorsCount);
//
//        protected native @ByRef MatVector trainDescCollection(int i);
    }

    @Namespace("cv") public static class BFMatcher extends DescriptorMatcher {
        static { load(); }
        public BFMatcher(int normType) { allocate(normType); }
        public BFMatcher(int normType, @Cast("bool") boolean crossCheck/*=false*/) { allocate(normType, crossCheck); }
        public BFMatcher(Pointer p) { super(p); }
        private native void allocate(int normType);
        private native void allocate(int normType, @Cast("bool") boolean crossCheck/*=false*/);

//        public native boolean isMaskSupported();
//        public native @ByVal DescriptorMatcherPtr clone(@Cast("bool") boolean emptyTrainData/*=false*/);

//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, @Cast("bool") boolean compactResult/*=false*/);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, @Cast("bool") boolean compactResult/*=false*/);

//        protected native int normType();
//        protected native @Cast("bool") boolean crossCheck();
    }

    @Namespace("cv") public static class FlannBasedMatcher extends DescriptorMatcher {
        static { load(); }
        public FlannBasedMatcher()       { allocate();  }
        public FlannBasedMatcher(IndexParams indexParams/*=new KDTreeIndexParams()*/,
                SearchParams searchParams/*=new SearchParams()*/) {
            allocate(indexParams, searchParams);
        }
        public FlannBasedMatcher(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(IndexParams indexParams, SearchParams searchParams);

//        public native void add(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray descriptors);
//        public native void clear();
//
//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        public native void train();
//        public native boolean isMaskSupported();
//        public native DescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);

//        protected static native void convertToDMatches(@ByRef DescriptorCollection descriptors,
//                CvMat indices, CvMat distances, @ByRef DMatchVectorVector matches );
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, @Cast("bool") boolean compactResult/*=false*/);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, @Cast("bool") boolean compactResult/*=false*/);
//
//        protected native @ByRef Ptr<flann::IndexParams> indexParams();
//        protected native @ByRef Ptr<flann::SearchParams> searchParams();
//        protected native @ByRef Ptr<flann::Index> flannIndex();
//
//        protected native @ByRef DescriptorCollection mergedDescriptors();
//        protected native int addedDescCount();
    }

    @Name("cv::Ptr<cv::GenericDescriptorMatcher>")
    public static class GenericDescriptorMatcherPtr extends Pointer {
        static { load(); }
        public GenericDescriptorMatcherPtr()       { allocate();  }
        public GenericDescriptorMatcherPtr(Pointer p) { super(p); }
        private native void allocate();

        public native GenericDescriptorMatcher get();
        public native GenericDescriptorMatcherPtr put(GenericDescriptorMatcher value);
    }

    @Namespace("cv") public static class GenericDescriptorMatcher extends Pointer {
        static { load(); }
        public GenericDescriptorMatcher() { }
        public GenericDescriptorMatcher(Pointer p) { super(p); }

        public native void add(@ByRef MatVector images, @ByRef KeyPointVectorVector keypoints);
        public native @Const @ByRef MatVector getTrainImages();
        public native @Const @ByRef KeyPointVectorVector getTrainKeypoints();
        public native void clear();
        public /*abstract*/ native boolean isMaskSupported();
        public native void train();

        public native void classify(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter("MatAdapter") CvArr trainImage, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint trainKeypoints);
        public native void classify(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints);

        public native void match(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter("MatAdapter") CvArr trainImage, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint trainKeypoints,
                @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches, @Adapter("MatAdapter") CvArr mask/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter("MatAdapter") CvArr trainImage, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint trainKeypoints,
                @ByRef DMatchVectorVector matches, int k, @Adapter("MatAdapter") CvArr mask/*=null*/, @Cast("bool") boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter("MatAdapter") CvArr trainImage, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint trainKeypoints,
                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("MatAdapter") CvArr mask/*=null*/, @Cast("bool") boolean compactResult/*=false*/);

        public native void match(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>")
                CvMatArray masks/*=null*/, @Cast("bool") boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>")
                CvMatArray masks/*=null*/, @Cast("bool") boolean compactResult/*=false*/);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

        public native boolean empty();
        public /*abstract*/ native @ByVal GenericDescriptorMatcherPtr clone(@Cast("bool") boolean emptyTrainData/*=false*/);
        public native static @ByVal GenericDescriptorMatcherPtr create(String genericDescritptorMatcherType,
                String paramsFilename/*=""*/);

//        protected /*abstract*/ native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, @Cast("bool") boolean compactResult);
//        protected /*abstract*/ native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, @Cast("bool") boolean compactResult);
//
//        protected static class KeyPointCollection extends Pointer {
//            static { load(); }
//            public KeyPointCollection()       { allocate();  }
//            public KeyPointCollection(KeyPointCollection collection) { allocate(collection); }
//            public KeyPointCollection(Pointer p) { super(p); }
//            private native void allocate();
//            private native void allocate(@ByRef KeyPointCollection collection);
//
//            public native void add(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray images,
//                    @ByRef KeyPointVectorVector keypoints );
//            public native void clear();
//
//            public native long keypointCount();
//            public native long imageCount();
//
//            public native @ByRef KeyPointVectorVector getKeypoints();
//            public native @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint getKeypoints(int imgIdx);
//            public native @ByRef KeyPoint getKeyPoint(int imgIdx, int localPointIdx);
//            public native @ByRef KeyPoint getKeyPoint(int globalPointIdx);
//            public native void getLocalIdx(int globalPointIdx, @ByRef int[] imgIdx, @ByRef int[] localPointIdx);
//
//            public native @ByRef MatVector IplImage getImages();
//            public native @Adapter("MatAdapter") IplImage getImage(int imgIdx);
//
//            protected native int pointCount();
//
//            protected native @ByRef MatVector images();
//            protected native @ByRef KeyPointVectorVector keypoints();
//            protected native @Adapter("VectorAdapter<int>") int[] startIndices();
//        }
//
//        protected native KeyPointCollection trainPointCollection();
    }

    @Namespace("cv") public static class VectorDescriptorMatcher extends GenericDescriptorMatcher {
        static { load(); }
        public VectorDescriptorMatcher() { }
        public VectorDescriptorMatcher(DescriptorExtractorPtr extractor, DescriptorMatcherPtr matcher) {
            allocate(extractor, matcher);
        }
        public VectorDescriptorMatcher(Pointer p) { super(p); }
        private native void allocate(@ByRef DescriptorExtractorPtr extractor, @ByRef DescriptorMatcherPtr matcher);

//        public native void add(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray imgCollection,
//                @ByRef KeyPointVectorVector pointCollection );
//        public native void clear();
//        public native void train();
//        public native boolean isMaskSupported();

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

//        public native boolean empty();
//        public native GenericDescriptorMatcherPtr clone(@Cast("bool") boolean emptyTrainData/*=false*/);

//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, @Cast("bool") boolean compactResult);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, @Cast("bool") boolean compactResult);
//
//        protected native @ByRef DescriptorExtractorPtr extractor();
//        protected native @ByRef DescriptorMatcherPtr matcher();
    }


    public static class DrawMatchesFlags {
        public static final int
                DEFAULT = 0,
                DRAW_OVER_OUTIMG = 1,
                NOT_DRAW_SINGLE_POINTS = 2,
                DRAW_RICH_KEYPOINTS = 4;
    }

    public static native void drawKeypoints(@Adapter("MatAdapter") CvArr image, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints,
            @Adapter("MatAdapter") CvArr outImage, @ByRef CvScalar color/*=cvScalarAll(-1)*/, int flags/*=DrawMatchesFlags::DEFAULT*/);

    public static native void drawMatches(@Adapter("MatAdapter") CvArr img1, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints1,
            @Adapter("MatAdapter") CvArr img2, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints2,
            @Adapter("VectorAdapter<cv::DMatch>") DMatch matches1to2, @Adapter("MatAdapter") CvArr outImg,
            @ByRef CvScalar matchColor/*=cvScalarAll(-1)*/, @ByRef CvScalar singlePointColor/*=cvScalarAll(-1)*/,
            @Adapter("VectorAdapter<char>") @Cast("char*") byte[] matchesMask/*=vector<vector<char> >()*/, int flags/*=DrawMatchesFlags::DEFAULT*/);

    public static native void drawMatches(@Adapter("MatAdapter") CvArr img1, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints1,
            @Adapter("MatAdapter") CvArr img2, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints2,
            @ByRef DMatchVectorVector matches1to2, @Adapter("MatAdapter") CvArr outImg,
            @ByRef CvScalar matchColor/*=cvScalarAll(-1)*/, @ByRef CvScalar singlePointColor/*=cvScalarAll(-1)*/,
            @ByRef ByteVectorVector matchesMask/*=vector<vector<char> >()*/, int flags/*=DrawMatchesFlags::DEFAULT*/);


    public static native void evaluateFeatureDetector(@Adapter("MatAdapter") CvArr img1, @Adapter("MatAdapter") CvArr img2, CvMat H1to2,
            @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints1,
            @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints2,
            @ByRef float[] repeatability, @ByRef int[] correspCount, @ByRef FeatureDetectorPtr fdetector/*=FeatureDetectorPtr()*/);

    public static native void computeRecallPrecisionCurve(@ByRef DMatchVectorVector matches1to2,
            @ByRef @Cast("std::vector<std::vector<uchar> >*") ByteVectorVector correctMatches1to2Mask,
            @Adapter(value="VectorAdapter<CvPoint2D32f,cv::Point2f>", out=true) CvPoint2D32f recallPrecisionCurve);

    public static native float getRecall(@Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f recallPrecisionCurve, float l_precision);
    public static native int getNearestPoint(@Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f recallPrecisionCurve, float l_precision);

    public static native void evaluateGenericDescriptorMatcher(@Adapter("MatAdapter") CvArr img1, @Adapter("MatAdapter") CvArr img2, CvMat H1to2,
            @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints1,
            @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints2,
            DMatchVectorVector matches1to2, @Cast("std::vector<std::vector<uchar> >*") ByteVectorVector correctMatches1to2Mask,
            @Adapter(value="VectorAdapter<CvPoint2D32f,cv::Point2f>", out=true) CvPoint2D32f recallPrecisionCurve,
            @ByRef GenericDescriptorMatcherPtr dmatch/*=GenericDescriptorMatcherPtr()*/);


    @Namespace("cv") public static class BOWTrainer extends Pointer {
        static { load(); }
        public BOWTrainer() { }
        public BOWTrainer(Pointer p) { super(p); }

        public native void add(CvMat descriptors);
        public native @Const @ByRef MatVector getDescriptors();
        public native int descripotorsCount();

        public native void clear();

        public /*abstract*/ native @Adapter("MatAdapter") CvMat cluster();
        public /*abstract*/ native @Adapter("MatAdapter") CvMat cluster(CvMat descriptors);

//        protected native @ByRef MatVector descriptors(int i);
//        protected native int size();
    }

    @Namespace("cv") public static class BOWKMeansTrainer extends BOWTrainer {
        static { load(); }
        public BOWKMeansTrainer(int clusterCount) { allocate(clusterCount); }
        public BOWKMeansTrainer(int clusterCount, @ByRef CvTermCriteria termcrit/*=cvTermCriteria()*/,
                int attempts/*=3*/, int flags/*=KMEANS_PP_CENTERS*/) {
            allocate(clusterCount, termcrit, attempts, flags);
        }
        public BOWKMeansTrainer(Pointer p) { super(p); }
        private native void allocate(int clusterCount);
        private native void allocate(int clusterCount, @ByRef CvTermCriteria termcrit/*=cvTermCriteria()*/,
                int attempts/*=3*/, int flags/*=KMEANS_PP_CENTERS*/);

//        public native @Adapter("MatAdapter") CvMat cluster();
//        public native @Adapter("MatAdapter") CvMat cluster(CvMat descriptors);

//        protected native int clusterCount();
//        protected native @ByVal CvTermCriteria termcrit();
//        protected native int attempts();
//        protected native int flags();
    }

    @Namespace("cv") public static class BOWImgDescriptorExtractor extends Pointer {
        static { load(); }
        public BOWImgDescriptorExtractor(DescriptorExtractorPtr dextractor, DescriptorMatcherPtr dmatcher) {
            allocate(dextractor, dmatcher);
        }
        public BOWImgDescriptorExtractor(Pointer p) { super(p); }
        private native void allocate(@ByRef DescriptorExtractorPtr dextractor, @ByRef DescriptorMatcherPtr dmatcher);

        public native void setVocabulary(CvMat vocabulary);
        public native @Adapter("MatAdapter") CvMat getVocabulary();
        public native void compute(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="MatAdapter", out=true) CvMat imgDescriptor,
                IntVectorVector pointIdxsOfClusters/*=null*/,
                @Adapter(value="MatAdapter", out=true) CvMat descriptors/*=null*/);

        public native int descriptorSize();
        public native int descriptorType();

//        protected native @Adapter("MatAdapter") CvMat vocabulary();
//        protected native @ByRef DescriptorExtractorPtr dextractor();
//        protected native @ByRef DescriptorMatcherPtr dmatcher();
    }
}
