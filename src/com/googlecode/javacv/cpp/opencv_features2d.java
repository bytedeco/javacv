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
 * This file is based on information found in features2d.hpp of OpenCV 2.3.1,
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

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Index;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/features2d/features2d.hpp>", "opencv_adapters.h"},
        link={"opencv_features2d", "opencv_flann", "opencv_calib3d", "opencv_highgui", "opencv_imgproc", "opencv_core"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_features2d231", "opencv_flann231", "opencv_calib3d231", "opencv_highgui231", "opencv_imgproc231", "opencv_core231"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_features2d {
    static { load(opencv_highgui.class); load(opencv_calib3d.class); load(opencv_flann.class); load(); }

    public static class CvSURFPoint extends Pointer {
        static { load(); }
        public CvSURFPoint() { allocate(); }
        public CvSURFPoint(int size) { allocateArray(size); }
        public CvSURFPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSURFPoint position(int position) {
            return (CvSURFPoint)super.position(position);
        }

        public native @ByRef CvPoint2D32f pt(); public native CvSURFPoint pt(CvPoint2D32f pt);
        public native int laplacian();          public native CvSURFPoint laplacian(int pt);
        public native int size();               public native CvSURFPoint size(int size);
        public native float dir();              public native CvSURFPoint dir(float dir);
        public native float hessian();          public native CvSURFPoint hessian(float hessian);
    }
    public static CvSURFPoint cvSURFPoint(CvPoint2D32f pt, int laplacian, int size) {
        return cvSURFPoint(pt, laplacian, size, 0, 0);
    }
    public static CvSURFPoint cvSURFPoint(CvPoint2D32f pt, int laplacian, int size,
            float dir/*=0*/, float hessian/*=0*/) {
        CvSURFPoint kp = new CvSURFPoint();
        kp.pt(pt);
        kp.laplacian(laplacian);
        kp.size(size);
        kp.dir(dir);
        kp.hessian(hessian);
        return kp;
    }

    public static class CvSURFParams extends Pointer {
        static { load(); }
        public CvSURFParams() { allocate(); }
        public CvSURFParams(int size) { allocateArray(size); }
        public CvSURFParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSURFParams position(int position) {
            return (CvSURFParams)super.position(position);
        }

        public native int extended();            public native CvSURFParams extended(int extended);
        public native int upright();             public native CvSURFParams upright(int upright);
        public native double hessianThreshold(); public native CvSURFParams hessianThreshold(double hessianThreshold);
        public native int nOctaves();            public native CvSURFParams nOctaves(int nOctaves);
        public native int nOctaveLayers();       public native CvSURFParams nOctaveLayers(int nOctaveLayers);
    }

    public static native @ByVal CvSURFParams cvSURFParams(double hessianThreshold, int extended/*=0*/);
    public static native void cvExtractSURF(CvArr image, CvArr mask,
            @ByPtrPtr CvSeq keypoints, @ByPtrPtr CvSeq descriptors,
            CvMemStorage storage, @ByVal CvSURFParams params, int useProvidedKeyPts/*=0*/);


    public static class CvMSERParams extends Pointer {
        static { load(); }
        public CvMSERParams() { allocate(); }
        public CvMSERParams(int size) { allocateArray(size); }
        public CvMSERParams(Pointer p) { super(p);  }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMSERParams position(int position) {
            return (CvMSERParams)super.position(position);
        }

        public native int delta();            public native CvMSERParams delta(int delta);
        public native int maxArea();          public native CvMSERParams maxArea(int maxArea);
        public native int minArea();          public native CvMSERParams minArea(int minArea);
        public native float maxVariation();   public native CvMSERParams maxVariation(float maxVariation);
        public native float minDiversity();   public native CvMSERParams minDiversity(float minDiversity);

        public native int maxEvolution();     public native CvMSERParams maxEvolution(int maxEvolution);
        public native double areaThreshold(); public native CvMSERParams areaThreshold(double areaThreshold);
        public native double minMargin();     public native CvMSERParams minMargin(double minMargin);
        public native int edgeBlurSize();     public native CvMSERParams edgeBlurSize(int edgeBlurSize);
    }

    public static CvMSERParams cvMSERParams() {
        return cvMSERParams(5, 60, 14400, 0.25f, 0.2f, 200, 1.01, 0.003, 5);
    }
    public static native @ByVal CvMSERParams cvMSERParams(int delta/*=5*/, int min_area/*=60*/,
            int max_area/*=14400*/, float max_variation/*=0.25f*/, float min_diversity/*=0.2f*/,
            int max_evolution/*=200*/, double area_threshold/*=1.01*/, double min_margin/*=0.003*/,
            int edge_blur_size/*=5*/);

    public static native void cvExtractMSER(CvArr image, CvArr mask, @ByPtrPtr CvSeq contours,
            CvMemStorage storage, @ByVal CvMSERParams params);


    public static class CvStarKeypoint extends Pointer {
        static { load(); }
        public CvStarKeypoint() { allocate(); }
        public CvStarKeypoint(int size) { allocateArray(size); }
        public CvStarKeypoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStarKeypoint position(int position) {
            return (CvStarKeypoint)super.position(position);
        }

        public native @ByRef CvPoint pt(); public native CvStarKeypoint pt(CvPoint pt);
        public native int size();          public native CvStarKeypoint size(int size);
        public native float response();    public native CvStarKeypoint response(float response);
    }
    public static CvStarKeypoint cvStarKeypoint(CvPoint pt, int size, float response) {
        CvStarKeypoint kpt = new CvStarKeypoint();
        kpt.pt(pt);
        kpt.size(size);
        kpt.response(response);
        return kpt;
    }

    public static class CvStarDetectorParams extends Pointer {
        static { load(); }
        public CvStarDetectorParams() { allocate(); }
        public CvStarDetectorParams(int size) { allocateArray(size); }
        public CvStarDetectorParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStarDetectorParams position(int position) {
            return (CvStarDetectorParams)super.position(position);
        }

        public native int maxSize();                public native CvStarDetectorParams maxSize(int maxSize);
        public native int responseThreshold();      public native CvStarDetectorParams responseThreshold(int responseThreshold);
        public native int lineThresholdProjected(); public native CvStarDetectorParams lineThresholdProjected(int lineThresholdProjected);
        public native int lineThresholdBinarized(); public native CvStarDetectorParams lineThresholdBinarized(int lineThresholdBinarized);
        public native int suppressNonmaxSize();     public native CvStarDetectorParams suppressNonmaxSize(int suppressNonmaxSize);
    }
    public static CvStarDetectorParams cvStarDetectorParams(int maxSize/*=45*/,
            int responseThreshold/*=30*/, int lineThresholdProjected/*=10*/,
            int lineThresholdBinarized/*=8*/, int suppressNonmaxSize/*=5*/) {
        CvStarDetectorParams params = new CvStarDetectorParams();
        params.maxSize(maxSize);
        params.responseThreshold(responseThreshold);
        params.lineThresholdProjected(lineThresholdProjected);
        params.lineThresholdBinarized(lineThresholdBinarized);
        params.suppressNonmaxSize(suppressNonmaxSize);
        return params;
    }
    public static CvStarDetectorParams cvStarDetectorParams() {
        return cvStarDetectorParams(45, 30, 10, 8, 5);
    }

    public static CvSeq cvGetStarKeypoints(CvArr image, CvMemStorage storage) {
        return cvGetStarKeypoints(image, storage, cvStarDetectorParams());
    }
    public static native CvSeq cvGetStarKeypoints(CvArr image, CvMemStorage storage,
            @ByVal CvStarDetectorParams params/*=cvStarDetectorParams()*/);


    @NoOffset @Namespace("cv") public static class DefaultRngAuto extends Pointer  {
        static { load(); }
        public DefaultRngAuto() { allocate(); }
        public DefaultRngAuto(int size) { allocateArray(size); }
        public DefaultRngAuto(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @MemberGetter public native long old_state();
    }

    @Namespace("cv") public static class CvAffinePose extends Pointer {
        static { load(); }
        public CvAffinePose() { allocate(); }
        public CvAffinePose(int size) { allocateArray(size); }
        public CvAffinePose(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native float phi();     public native CvAffinePose phi(float phi);
        public native float theta();   public native CvAffinePose theta(float theta);
        public native float lambda1(); public native CvAffinePose lambda1(float lambda1);
        public native float lambda2(); public native CvAffinePose lambda2(float lambda2);
    }

    @NoOffset @Namespace("cv") public static class KeyPoint extends Pointer {
        static { load(); }
        public KeyPoint() { allocate(); }
        public KeyPoint(Pointer p) { super(p); }
        public KeyPoint(CvPoint2D32f _pt, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/) {
            allocate(_pt, _size, _angle, _response, _octave, _class_id);
        }
        public KeyPoint(float x, float y, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/) {
            allocate(x, y, _size, _angle, _response, _octave, _class_id);
        }
        private native void allocate();
        private native void allocate(@ByRef CvPoint2D32f _pt, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/);
        private native void allocate(float x, float y, float _size, float _angle/*=-1*/,
                float _response/*=0*/, int _octave/*=0*/, int _class_id/*=-1*/);

        public native long hash();

        public static native void convert(@Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints,
                @Adapter(value="VectorAdapter<CvPoint2D32f,cv::Point2f>", out=true) CvPoint2D32f points2f,
                @Adapter("VectorAdapter<int>") int[] keypointIndexes/*=null*/);
        public static native void convert(@Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f points2f,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                float size/*=1*/, float response/*=1*/, int octave/*=0*/, int class_id/*=-1*/);

        public static native float overlap(@ByRef KeyPoint kp1, @ByRef KeyPoint kp2);

        @ByVal
        public native CvPoint2D32f pt(); public native KeyPoint pt(CvPoint2D32f pt);
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
        public static native void runByKeypointSize(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, float minSize, float maxSize/*=std::numeric_limits<float>::max()*/);
        public static native void runByPixelsMask(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask);
        public static native void removeDuplicated(@Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
    }

    @Namespace("cv") public static class SIFT extends Pointer {
        static { load(); }
        @NoOffset public static class CommonParams extends Pointer {
            static { load(); }
            public static final int
                    DEFAULT_NOCTAVES = 4,
                    DEFAULT_NOCTAVE_LAYERS = 3,
                    DEFAULT_FIRST_OCTAVE = -1,
                    FIRST_ANGLE = 0,
                    AVERAGE_ANGLE = 1;

            public CommonParams() { allocate(); }
            public CommonParams(Pointer p) { super(p); }
            public CommonParams(int _nOctaves, int _nOctaveLayers, int _firstOctave, int _angleMode) {
                allocate(_nOctaves, _nOctaveLayers, _firstOctave, _angleMode);
            }
            private native void allocate();
            private native void allocate(int _nOctaves, int _nOctaveLayers, int _firstOctave, int _angleMode);

            public native int nOctaves();      public native CommonParams nOctaves(int nOctaves);
            public native int nOctaveLayers(); public native CommonParams nOctaveLayers(int nOctaveLayers);
            public native int firstOctave();   public native CommonParams firstOctave(int firstOctave);
            public native int angleMode();     public native CommonParams angleMode(int angleMode);
        }

        @NoOffset public static class DetectorParams extends Pointer {
            static { load(); }
            public static native double GET_DEFAULT_THRESHOLD();
            public static native double GET_DEFAULT_EDGE_THRESHOLD();

            public DetectorParams() { allocate(); }
            public DetectorParams(Pointer p) { super(p); }
            public DetectorParams(double _threshold, double _edgeThreshold) {
                allocate(_threshold, _edgeThreshold);
            }
            private native void allocate();
            private native void allocate(double _threshold, double _edgeThreshold);

            public native double threshold();     public native DetectorParams threshold(double threshold);
            public native double edgeThreshold(); public native DetectorParams edgeThreshold(double edgeThreshold);
        }

        @NoOffset public static class DescriptorParams extends Pointer {
            static { load(); }
            public static native double GET_DEFAULT_MAGNIFICATION();
            public static final boolean DEFAULT_IS_NORMALIZE = true;
            public static final int DESCRIPTOR_SIZE = 128;

            public DescriptorParams() { allocate(); }
            public DescriptorParams(Pointer p) { super(p); }
            public DescriptorParams(double _magnification, boolean _isNormalize, boolean _recalculateAngles) {
                allocate(_magnification, _isNormalize, _recalculateAngles);
            }
            private native void allocate();
            private native void allocate(double _magnification, boolean _isNormalize, boolean _recalculateAngles);

            public native double magnification();      public native DescriptorParams magnification(double magnification);
            public native boolean isNormalize();       public native DescriptorParams isNormalize(boolean isNormalize);
            public native boolean recalculateAngles(); public native DescriptorParams recalculateAngles(boolean recalculateAngles);
        }

        public SIFT() { allocate(); }
        public SIFT(Pointer p) { super(p); }
        public SIFT(double _threshold, double _edgeThreshold,
              int _nOctaves/*=CommonParams::DEFAULT_NOCTAVES*/,
              int _nOctaveLayers/*=CommonParams::DEFAULT_NOCTAVE_LAYERS*/,
              int _firstOctave/*=CommonParams::DEFAULT_FIRST_OCTAVE*/,
              int _angleMode/*=CommonParams::FIRST_ANGLE*/) {
            allocate(_threshold, _edgeThreshold,
                    _nOctaves, _nOctaveLayers, _firstOctave, _angleMode);
        }
        public SIFT(double _magnification, boolean _isNormalize/*=true*/,
              boolean _recalculateAngles/*=true*/,
              int _nOctaves/*=CommonParams::DEFAULT_NOCTAVES*/,
              int _nOctaveLayers/*=CommonParams::DEFAULT_NOCTAVE_LAYERS*/,
              int _firstOctave/*=CommonParams::DEFAULT_FIRST_OCTAVE*/,
              int _angleMode/*=CommonParams::FIRST_ANGLE*/) {
            allocate(_magnification, _isNormalize, _recalculateAngles,
                    _nOctaves, _nOctaveLayers, _firstOctave, _angleMode);
        }
        public SIFT(CommonParams _commParams,
              DetectorParams _detectorParams/*=DetectorParams()*/,
              DescriptorParams _descriptorParams/*=DescriptorParams()*/) {
            allocate(_commParams, _detectorParams, _descriptorParams);
        }
        private native void allocate();
        private native void allocate(double _threshold, double _edgeThreshold,
                int _nOctaves, int _nOctaveLayers, int _firstOctave, int _angleMode);
        private native void allocate(double _magnification, boolean _isNormalize, boolean _recalculateAngles,
                int _nOctaves, int _nOctaveLayers, int _firstOctave, int _angleMode);
        private native void allocate(@ByRef CommonParams _commParams,
              @ByRef DetectorParams _detectorParams, @ByRef DescriptorParams _descriptorParams);

        public native int descriptorSize();

        public native @Name("operator()") void detect(@Adapter("MatAdapter") CvArr img, @Adapter("MatAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
        public native @Name("operator()") void detect(@Adapter("MatAdapter") CvArr img, @Adapter("MatAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="MatAdapter", out=true) CvMat descriptors, boolean useProvidedKeypoints/*=false*/);

        public native @ByVal CommonParams getCommonParams();
        public native @ByVal DetectorParams getDetectorParams();
        public native @ByVal DescriptorParams getDescriptorParams();
//        protected CommonParams commParams;
//        protected DetectorParams detectorParams;
//        protected DescriptorParams descriptorParams;
    }

    @NoOffset @Namespace("cv") public static class ORB extends Pointer {
        static { load(); }
        public static final int kBytes = 32;
        @NoOffset public static class CommonParams extends Pointer {
            static { load(); }
            public static final int DEFAULT_N_LEVELS = 3, DEFAULT_FIRST_LEVEL = 0;

            public CommonParams() { allocate(); }
            public CommonParams(Pointer p) { super(p); }
            public CommonParams(float scale_factor/*=1.2f*/, int n_levels/*=DEFAULT_N_LEVELS*/,
                    int edge_threshold/*=31*/, int first_level/*=DEFAULT_FIRST_LEVEL*/) {
                allocate(scale_factor, n_levels, edge_threshold, first_level);
            }
            private native void allocate();
            private native void allocate(float scale_factor/*=1.2f*/, int n_levels/*=DEFAULT_N_LEVELS*/,
                    int edge_threshold/*=31*/, int first_level/*=DEFAULT_FIRST_LEVEL*/);

            public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
            public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

            public native float scale_factor_(); public native CommonParams scale_factor_(float scale_factor_);
            public native int n_levels_();       public native CommonParams n_levels_(int n_levels_);
            public native int first_level_();    public native CommonParams first_level_(int first_level_);
            public native int edge_threshold_(); public native CommonParams edge_threshold_(int edge_threshold_);

//            protected native int patch_size_();  protected native CommonParams patch_size_(int patch_size_);
        }

        public ORB() { allocate(); }
        public ORB(Pointer p) { super(p); }
        public ORB(long n_features/*=500*/, @ByRef CommonParams detector_params/*=CommonParams()*/) {
            allocate(n_features, detector_params);
        }
        private native void allocate();
        private native void allocate(long n_features/*=500*/, @ByRef CommonParams detector_params/*=CommonParams()*/);

        public native int descriptorSize();

        public native @Name("operator()") void detect(@Adapter("MatAdapter") CvArr image, @Adapter("MatAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
        public native @Name("operator()") void detect(@Adapter("MatAdapter") CvArr image, @Adapter("MatAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="MatAdapter", out=true) CvMat descriptors, boolean useProvidedKeypoints/*=false*/);
    }

    @Namespace("cv") public static native void FAST(@Adapter("MatAdapter") CvArr image,
            @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true)
            KeyPoint keypoints, int threshold, boolean nonmaxSupression/*=true*/);

    @NoOffset @Namespace("cv") public static class PatchGenerator extends Pointer {
        public PatchGenerator() { allocate(); }
        public PatchGenerator(Pointer p) { super(p); }
        public PatchGenerator(double _backgroundMin, double _backgroundMax, double _noiseRange, 
                boolean _randomBlur/*=true*/, double _lambdaMin/*=0.6*/, double _lambdaMax/*=1.5*/,
                double _thetaMin/*=-CV_PI*/, double _thetaMax/*=CV_PI*/, double _phiMin/*=-CV_PI*/, double _phiMax/*=CV_PI*/) {
            allocate(_backgroundMin, _backgroundMax, _noiseRange, _randomBlur,
                    _lambdaMin, _lambdaMax, _thetaMin, _thetaMax, _phiMin, _phiMax);
        }
        private native void allocate();
        private native void allocate(double _backgroundMin, double _backgroundMax, double _noiseRange,
                boolean _randomBlur/*=true*/, double _lambdaMin/*=0.6*/, double _lambdaMax/*=1.5*/,
                double _thetaMin/*=-CV_PI*/, double _thetaMax/*=CV_PI*/, double _phiMin/*=-CV_PI*/, double _phiMax/*=CV_PI*/);

        public native @Name("operator()") void generate(@Adapter("MatAdapter") CvArr image, @ByVal CvPoint2D32f pt,
                @Adapter("MatAdapter") CvArr patch, @ByVal CvSize patchSize, @Adapter(value="RNGAdapter", out=true) CvRNG rng);
        public native @Name("operator()") void generate(@Adapter("MatAdapter") CvArr image, CvMat transform,
                @Adapter("MatAdapter") CvArr patch, @ByVal CvSize patchSize, @Adapter(value="RNGAdapter", out=true) CvRNG rng);
        public native void warpWholeImage(@Adapter("MatAdapter") CvArr image, @Adapter("MatAdapter") CvMat matT, @Adapter("MatAdapter") CvArr buf,
                @Adapter("MatAdapter") CvArr warped, int border, @Adapter(value="RNGAdapter", out=true) CvRNG rng);
        public native void generateRandomTransform(@ByVal CvPoint2D32f srcCenter, @ByVal CvPoint2D32f dstCenter,
                @Adapter("MatAdapter") CvMat transform, @Adapter(value="RNGAdapter", out=true) CvRNG rng, boolean inverse/*=false*/);
        public native void setAffineParam(double lambda, double theta, double phi);

        public native double backgroundMin(); public native PatchGenerator backgroundMin(double backgroundMin);
        public native double backgroundMax(); public native PatchGenerator backgroundMax(double backgroundMax);
        public native double noiseRange();    public native PatchGenerator noiseRange(double backgroundMin);
        public native boolean randomBlur();   public native PatchGenerator randomBlur(boolean randomBlur);
        public native double lambdaMin();     public native PatchGenerator lambdaMin(double lambdaMin);
        public native double lambdaMax();     public native PatchGenerator lambdaMax(double lambdaMax);
        public native double thetaMin();      public native PatchGenerator thetaMin(double thetaMin);
        public native double thetaMax();      public native PatchGenerator thetaMax(double thetaMax);
        public native double phiMin();        public native PatchGenerator phiMin(double phiMin);
        public native double phiMax();        public native PatchGenerator phiMax(double phiMax);
    }

    @NoOffset @Namespace("cv") public static class LDetector extends Pointer {
        static { load(); }
        public LDetector() { allocate(); }
        public LDetector(Pointer p) { super(p); }
        public LDetector(int _radius, int _threshold, int _nOctaves,
               int _nViews, double _baseFeatureSize, double _clusteringDistance) {
            allocate(_radius, _threshold, _nOctaves, _nViews, _baseFeatureSize, _clusteringDistance);
        }
        private native void allocate();
        private native void allocate(int _radius, int _threshold, int _nOctaves,
                int _nViews, double _baseFeatureSize, double _clusteringDistance);

        public native @Name("operator()") void detect(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                int maxCount/*=0*/, boolean scaleCoords/*=true*/);
        public native @Name("operator()") void detect(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                int maxCount/*=0*/, boolean scaleCoords/*=true*/);
        public native void getMostStable2D(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                int maxCount, @ByRef PatchGenerator patchGenerator);
        public native void setVerbose(boolean verbose);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs, String name);

        public native int radius();      public native LDetector radius(int radius);
        public native int threshold();   public native LDetector threshold(int threshold);
        public native int nOctaves();    public native LDetector nOctaves(int nOctaves);
        public native int nViews();      public native LDetector nViews(int nViews);
        public native boolean verbose(); public native LDetector verbose(boolean verbose);

        public native double baseFeatureSize();    public native LDetector baseFeatureSize(double baseFeatureSize);
        public native double clusteringDistance(); public native LDetector clusteringDistance(double clusteringDistance);
    }

//    typedef LDetector YAPE;

    @Namespace("cv") public static class FernClassifier extends Pointer {
        static { load(); }
        public FernClassifier() { allocate(); }
        public FernClassifier(Pointer p) { super(p); }
        public FernClassifier(CvFileStorage fs, CvFileNode node) { allocate(fs, node); }
        public FernClassifier(@ByRef Point2fVectorVector points,
                   @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray refimgs,
                   @ByRef IntVectorVector labels/*=vector<vector<int> >()*/,
                   int _nclasses/*=0*/, int _patchSize/*=PATCH_SIZE*/,
                   int _signatureSize/*=DEFAULT_SIGNATURE_SIZE*/,
                   int _nstructs/*=DEFAULT_STRUCTS*/,
                   int _structSize/*=DEFAULT_STRUCT_SIZE*/,
                   int _nviews/*=DEFAULT_VIEWS*/,
                   int _compressionMethod/*=COMPRESSION_NONE*/,
                   @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/) {
            allocate(points, refimgs, labels, _nclasses, _patchSize, _signatureSize,
                    _nstructs, _structSize, _nviews, _compressionMethod, patchGenerator);
        }
        private native void allocate();
        private native void allocate(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode node);
        private native void allocate(@ByRef Point2fVectorVector points,
                   @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray refimgs,
                   @ByRef IntVectorVector labels/*=vector<vector<int> >()*/,
                   int _nclasses/*=0*/, int _patchSize/*=PATCH_SIZE*/,
                   int _signatureSize/*=DEFAULT_SIGNATURE_SIZE*/,
                   int _nstructs/*=DEFAULT_STRUCTS*/,
                   int _structSize/*=DEFAULT_STRUCT_SIZE*/,
                   int _nviews/*=DEFAULT_VIEWS*/,
                   int _compressionMethod/*=COMPRESSION_NONE*/,
                   @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode n);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs, String name);
        public native void trainFromSingleView(@Adapter("MatAdapter") CvArr image,
                @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints,
                int _patchSize/*=PATCH_SIZE*/, int _signatureSize/*=DEFAULT_SIGNATURE_SIZE*/,
                int _nstructs/*=DEFAULT_STRUCTS*/, int _structSize/*=DEFAULT_STRUCT_SIZE*/,
                int _nviews/*=DEFAULT_VIEWS*/, int _compressionMethod/*=COMPRESSION_NONE*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);
        public native void train(@ByRef Point2fVectorVector points,
                @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray refimgs,
                @ByRef IntVectorVector labels/*=vector<vector<int> >()*/,
                int _nclasses/*=0*/, int _patchSize/*=PATCH_SIZE*/,
                int _signatureSize/*=DEFAULT_SIGNATURE_SIZE*/, int _nstructs/*=DEFAULT_STRUCTS*/,
                int _structSize/*=DEFAULT_STRUCT_SIZE*/, int _nviews/*=DEFAULT_VIEWS*/,
                int _compressionMethod/*=COMPRESSION_NONE*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);
        public native @Name("operator()") int classify(@Adapter("MatAdapter") CvArr img, @ByVal CvPoint2D32f kpt,
                @Adapter(value="VectorAdapter<float>", out=true) FloatPointer signature);
        public native @Name("operator()") int classify(@Adapter("MatAdapter") CvArr patch,
                @Adapter(value="VectorAdapter<float>", out=true) FloatPointer signature);
        public native void clear();
        public native void setVerbose(boolean verbose);

        public native int getClassCount();
        public native int getStructCount();
        public native int getStructSize();
        public native int getSignatureSize();
        public native int getCompressionMethod();
        public native @ByVal CvSize getPatchSize();

        public static class Feature extends Pointer {
            public byte x1, y1, x2, y2;
            public Feature() { allocate(); }
            public Feature(int _x1, int _y1, int _x2, int _y2) {
                allocate(_x1, _y1, _x2, _y2);
            }
            private native void allocate();
            private native void allocate(int _x1, int _y1, int _x2, int _y2);

//            template<typename _Tp> bool operator ()(const Mat_<_Tp>& patch) const
        }

        public static final int
                PATCH_SIZE = 31,
                DEFAULT_STRUCTS = 50,
                DEFAULT_STRUCT_SIZE = 9,
                DEFAULT_VIEWS = 5000,
                DEFAULT_SIGNATURE_SIZE = 176,
                COMPRESSION_NONE = 0,
                COMPRESSION_RANDOM_PROJ = 1,
                COMPRESSION_PCA = 2,
                DEFAULT_COMPRESSION_METHOD = COMPRESSION_NONE;

//        protected native void prepare(int _nclasses, int _patchSize, int _signatureSize,
//                         int _nstructs, int _structSize,
//                         int _nviews, int _compressionMethod);
//        protected native void finalize(@Adapter(value="RNGAdapter", out=true) CvRNG rng);
//        protected native int getLeaf(int fidx, @Adapter("MatAdapter") CvArr img patch);
//
//        protected native boolean verbose();
//        protected native int nstructs();
//        protected native int structSize();
//        protected native int nclasses();
//        protected native int signatureSize();
//        protected native int compressionMethod();
//        protected native int leavesPerStruct();
//        protected native @ByVal CvSize patchSize();
//        protected native @Adapter("VectorAdapter<Feature>") Feature features();
//        protected native @Adapter("VectorAdapter<int>") int[] classCounters();
//        protected native @Adapter("VectorAdapter<float>") float[] posteriors();
    }


    @NoOffset @Namespace("cv") public static class BaseKeypoint extends Pointer {
        static { load(); }
        public BaseKeypoint() { allocate(); }
        public BaseKeypoint(int x, int y, IplImage image) { allocate(x, y, image); }
        public BaseKeypoint(int size) { allocateArray(size); }
        public BaseKeypoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int x, int y, IplImage image);
        private native void allocateArray(int size);

        public native int x();          public native BaseKeypoint x(int x);
        public native int y();          public native BaseKeypoint y(int y);
        public native IplImage image(); public native BaseKeypoint image(IplImage image);
    }

    @Namespace("cv") public static class RandomizedTree extends Pointer {
        static { load(); }
        public static final byte PATCH_SIZE = 32;
        public static final int
                DEFAULT_DEPTH = 9,
                DEFAULT_VIEWS = 5000,
                DEFAULT_REDUCED_NUM_DIM = 176;
        public static native float GET_LOWER_QUANT_PERC();
        public static native float GET_UPPER_QUANT_PERC();

        public RandomizedTree() { allocate(); }
        public RandomizedTree(Pointer p) { super(p); }
        private native void allocate();

        public native void train(@Adapter("VectorAdapter<cv::BaseKeypoint>") BaseKeypoint base_set,
                @Adapter("RNGAdapter") CvRNG rng, int depth, int views, long reduced_num_dim, int num_quant_bits);
        public native void train(@Adapter("VectorAdapter<cv::BaseKeypoint>") BaseKeypoint base_set,
                @Adapter("RNGAdapter") CvRNG rng, @ByRef PatchGenerator make_patch,
                int depth, int views, long reduced_num_dim, int num_quant_bits);

        public static native void quantizeVector(float[] vec, int dim, int N, float bnds[/*2*/], int clamp_mode/*=0*/);
        public static native void quantizeVector(float[] src, int dim, int N, float bnds[/*2*/], @Cast("uchar*") BytePointer dst);

        public native FloatPointer getPosterior(@Cast("uchar*") BytePointer patch_data);
        public native @Cast("uchar*") BytePointer getPosterior2(@Cast("uchar*") BytePointer patch_data);

        public native void read(String file_name, int num_quant_bits);
        public native void read(@ByRef @Cast("std::istream*") Pointer is, int num_quant_bits);
        public native void write(String file_name);
        public native void write(@ByRef @Cast("std::ostream*") Pointer os);

        public native int classes();
        public native int depth();

        //public native void setKeepFloatPosteriors(boolean b);
        public native void discardFloatPosteriors();

        public native void applyQuantization(int num_quant_bits);

        public native void savePosteriors(String url, boolean append/*=false*/);
        public native void savePosteriors2(String url, boolean append/*=false*/);
    }

    @NoOffset @Namespace("cv") public static class RTreeNode extends Pointer {
        static { load(); }
        public RTreeNode() { allocate(); }
        public RTreeNode(byte x1, byte y1, byte x2, byte y2) { allocate(x1, y1, x2, y2); }
        private native void allocate();
        private native void allocate(byte x1, byte y1, byte x2, byte y2);

        public native short offset1(); public native RTreeNode offset1(short offset1);
        public native short offset2(); public native RTreeNode offset2(short offset2);

        public native @Name("operator()") boolean compare(@Cast("uchar*") BytePointer patch_data);
    }

    @Namespace("cv") public static class RTreeClassifier extends Pointer {
        static { load(); }
        public static final int
                DEFAULT_TREES = 48,
                DEFAULT_NUM_QUANT_BITS = 4;

        public RTreeClassifier() { allocate(); }
        public RTreeClassifier(Pointer p) { super(p); }
        private native void allocate();

        public native void train(@Adapter("VectorAdapter<cv::BaseKeypoint>") BaseKeypoint base_set,
             @Adapter("RNGAdapter") CvRNG rng, int num_trees/* = RTreeClassifier::DEFAULT_TREES*/,
             int depth/* = RandomizedTree::DEFAULT_DEPTH*/,
             int views/* = RandomizedTree::DEFAULT_VIEWS*/,
             long reduced_num_dim/* = RandomizedTree::DEFAULT_REDUCED_NUM_DIM*/,
             int num_quant_bits/* = DEFAULT_NUM_QUANT_BITS*/);
        public native void train(@Adapter("VectorAdapter<cv::BaseKeypoint>") BaseKeypoint base_set,
             @Adapter("RNGAdapter") CvRNG rng, @ByRef PatchGenerator make_patch,
             int num_trees/* = RTreeClassifier::DEFAULT_TREES*/,
             int depth/* = RandomizedTree::DEFAULT_DEPTH*/,
             int views/* = RandomizedTree::DEFAULT_VIEWS*/,
             long reduced_num_dim/* = RandomizedTree::DEFAULT_REDUCED_NUM_DIM*/,
             int num_quant_bits/* = DEFAULT_NUM_QUANT_BITS*/);

        public native void getSignature(IplImage patch, @Cast("uchar*") byte[] sig);
        public native void getSignature(IplImage patch, float[] sig);
        public native void getSparseSignature(IplImage patch, float[] sig, float thresh);
        public native void getFloatSignature(IplImage patch, float[] sig);

        public static native int countNonZeroElements(float[] vec, int n, double tol/*=1e-10*/);
//        public static native void safeSignatureAlloc(@Cast("uchar**") @ByPtrPtr BytePointer sig, int num_sig/*=1*/, int sig_len/*=176*/);
//        public static native @Cast("uchar*") BytePointer safeSignatureAlloc(int num_sig/*=1*/, int sig_len/*=176*/);

        public native int classes();
        public native int original_num_classes();

        public native void setQuantization(int num_quant_bits);
        public native void discardFloatPosteriors();

        public native void read(String file_name);
        public native void read(@ByRef @Cast("std::istream*") Pointer is);
        public native void write(String file_name);
        public native void write(@ByRef @Cast("std::ostream*") Pointer os);

        public native void saveAllFloatPosteriors(String file_url);
        public native void saveAllBytePosteriors(String file_url);
        public native void setFloatPosteriorsFromTextfile_176(String url);
        public native float countZeroElements();

        @NoOffset @Adapter("VectorAdapter<cv::RandomizedTree>")
        public native RandomizedTree trees_(); public native RTreeClassifier trees_(RandomizedTree trees_);
    }


    @Namespace("cv") public static class OneWayDescriptor extends Pointer {
        static { load(); }
        public OneWayDescriptor() { allocate(); }
        public OneWayDescriptor(Pointer p) { super(p); }
        private native void allocate();

        public native void Allocate(int pose_count, @ByVal CvSize size, int nChannels);
        public native void GenerateSamples(int pose_count, IplImage frontal, int norm/* = 0*/);
        public native void GenerateSamplesFast(IplImage frontal, CvMat pca_hr_avg,
                CvMat pca_hr_eigenvectors, OneWayDescriptor pca_descriptors);
        public native void SetTransforms(CvAffinePose poses, CvMatArray transforms);
        public native void Initialize(int pose_count, IplImage frontal, String feature_name/* = null*/, int norm/* = 0*/);
        public native void InitializeFast(int pose_count, IplImage frontal, String feature_name,
                CvMat pca_hr_avg, CvMat pca_hr_eigenvectors, OneWayDescriptor pca_descriptors);
        public native void ProjectPCASample(IplImage patch, CvMat avg, CvMat eigenvectors, CvMat pca_coeffs);
        public native void InitializePCACoeffs(CvMat avg, CvMat eigenvectors);
        public native void EstimatePose(IplImage patch, @ByRef int[] pose_idx, @ByRef float[] distance);
        public native void EstimatePosePCA(CvArr patch, @ByRef int[] pose_idx, @ByRef float[] distance, CvMat avg, CvMat eigenvalues);
        public native @ByVal CvSize GetPatchSize();
        public native @ByVal CvSize GetInputPatchSize();
        public native IplImage GetPatch(int index);
        public native @ByVal CvAffinePose GetPose(int index);
        public native void Save(String path);
        public native int ReadByName(CvFileStorage fs, CvFileNode parent, String name);
//        public native int ReadByName(@ByRef FileNode parent, String name);
        public native void Write(CvFileStorage fs, String name);
        public native String GetFeatureName();
        public native @ByVal CvPoint GetCenter();

        public native void SetPCADimHigh(int pca_dim_high);
        public native void SetPCADimLow(int pca_dim_low);

        public native int GetPCADimLow();
        public native int GetPCADimHigh();

        public native CvMatArray GetPCACoeffs();

//        protected native int m_pose_count();
//        protected native CvSize m_patch_size();
//        protected native IplImageArray m_samples();
//        protected native IplImage m_input_patch();
//        protected native IplImage m_train_patch();
//        protected native CvMatArray m_pca_coeffs();
//        protected native CvAffinePose m_affine_poses();
//        protected native CvMatArray m_transforms();
//
//        protected native String m_feature_name();
//        protected native CvPoint m_center();
//
//        protected native int m_pca_dim_high();
//        protected native int m_pca_dim_low();
    }

    @Namespace("cv") public static class OneWayDescriptorBase extends Pointer {
        static { load(); }
        public OneWayDescriptorBase() { }
        public OneWayDescriptorBase(Pointer p) { super(p); }
        public OneWayDescriptorBase(@ByVal CvSize patch_size, int pose_count, String train_path/*=null*/,
                String pca_config/*=null*/, String pca_hr_config/*=null*/, String pca_desc_config/*=null*/,
                int pyr_levels/*=1*/, int pca_dim_high/*=100*/, int pca_dim_low/*=100*/) {
            allocate(patch_size, pose_count, train_path, pca_config, pca_hr_config,
                    pca_desc_config, pyr_levels, pca_dim_high, pca_dim_low);
        }
        public OneWayDescriptorBase(@ByVal CvSize patch_size, int pose_count, String pca_filename, String train_path/*=""*/,
                String images_list/*=""*/, float _scale_min/*=0.7f*/, float _scale_max/*=1.5f*/, float _scale_step/*=1.2f*/,
                int pyr_levels/*=1*/, int pca_dim_high/*=100*/, int pca_dim_low/*=100*/) {
            allocate(patch_size, pose_count, pca_filename, train_path, images_list,
                    _scale_min, _scale_max, _scale_step, pyr_levels, pca_dim_high, pca_dim_low);
        }
        private native void allocate(@ByVal CvSize patch_size, int pose_count, String train_path/*=null*/,
                String pca_config/*=null*/, String pca_hr_config/*=null*/, String pca_desc_config/*=null*/,
                int pyr_levels/*=1*/, int pca_dim_high/*=100*/, int pca_dim_low/*=100*/);
        private native void allocate(@ByVal CvSize patch_size, int pose_count, String pca_filename, String train_path/*=""*/,
                String images_list/*=""*/, float _scale_min/*=0.7f*/, float _scale_max/*=1.5f*/, float _scale_step/*=1.2f*/,
                int pyr_levels/*=1*/, int pca_dim_high/*=100*/, int pca_dim_low/*=100*/);

        public native void clear();
        public native void Allocate(int train_feature_count);
        public native void AllocatePCADescriptors();

        public native @ByVal CvSize GetPatchSize();
        public native int GetPoseCount();
        public native int GetPyrLevels();
        public native int GetDescriptorCount();

        public native void CreateDescriptorsFromImage(IplImage src,
                @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint features);
        public native void CreatePCADescriptors();

        public native @Const OneWayDescriptor GetDescriptor(int desc_idx);

        public native void FindDescriptor(IplImage patch, @ByRef int[] desc_idx, @ByRef int[] pose_idx,
                @ByRef float[] distance, float[] _scale/*=null*/, float[] scale_ranges/*=null*/);
        public native void FindDescriptor(IplImage patch, int n, @Adapter("VectorAdapter<int>") int[] desc_idxs,
                @Adapter("VectorAdapter<int>") int[] pose_idxs, @Adapter("VectorAdapter<float>") float[] distances,
                @Adapter("VectorAdapter<float>") float[] _scales, float[] scale_ranges/*=null*/);
        public native void FindDescriptor(IplImage src, @ByVal CvPoint2D32f pt,
                @ByRef int[] desc_idx, @ByRef int[] pose_idx, @ByRef float[] distance);

        public native void InitializePoses();
        public native void InitializeTransformsFromPoses();
        public native void InitializePoseTransforms();
        public native void InitializeDescriptor(int desc_idx, IplImage train_image, String feature_label);
        public native void InitializeDescriptor(int desc_idx, IplImage train_image, @ByRef KeyPoint keypoint, String feature_label);
        public native void InitializeDescriptors(IplImage train_image, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint features,
                String feature_label/*=""*/, int desc_start_idx/*=0*/);

        public native void Write(@Adapter("FileStorageAdapter") CvFileStorage fs);
        public native void Read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);

        public native int LoadPCADescriptors(String filename);
        public native void LoadPCADescriptors(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void SavePCADescriptors(String filename);
        public native void SavePCADescriptors(CvFileStorage fs);

        public native void GeneratePCA(String img_path, String images_list, int pose_count/*=500*/);
        public native void SetPCAHigh(CvMat avg, CvMat eigenvectors);
        public native void SetPCALow(CvMat avg, CvMat eigenvectors);
        public native int GetLowPCA(@ByPtrPtr CvMat avg, @ByPtrPtr CvMat eigenvectors);

        public native int GetPCADimLow();
        public native int GetPCADimHigh();

//        public native void ConvertDescriptorsArrayToTree();

        public native static @ByRef String GetPCAFilename();

        public native boolean empty();

//        protected native @ByRef CvSize m_patch_size();
//        protected native int m_pose_count();
//        protected native int m_train_feature_count();
//        protected native OneWayDescriptor m_descriptors();
//        protected native CvMat m_pca_avg();
//        protected native CvMat m_pca_eigenvectors();
//        protected native CvMat m_pca_hr_avg();
//        protected native CvMat m_pca_hr_eigenvectors();
//        protected native OneWayDescriptor m_pca_descriptors();
//
//        protected native cv::flann::Index m_pca_descriptors_tree();
//        protected native CvMat m_pca_descriptors_matrix();
//
//        protected native CvAffinePose m_poses();
//        protected native CvMatArray m_transforms();
//
//        protected native int m_pca_dim_high();
//        protected native int m_pca_dim_low();
//
//        protected native int m_pyr_levels();
//        protected native float scale_min();
//        protected native float scale_max();
//        protected native float scale_step();
//
//        protected native void SavePCAall(@Adapter("FileStorageAdapter") CvFileStorage fs);
//        protected native void LoadPCAall(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
    }

    @Namespace("cv") public static class OneWayDescriptorObject extends OneWayDescriptorBase {
        static { load(); }
        public OneWayDescriptorObject() { }
        public OneWayDescriptorObject(Pointer p) { super(p); }
        public OneWayDescriptorObject(@ByVal CvSize patch_size, int pose_count, String train_path, String pca_config,
                String pca_hr_config/*=null*/, String pca_desc_config/*=null*/, int pyr_levels/*=1*/) {
            allocate(patch_size, pose_count, train_path, pca_config, pca_hr_config, pca_desc_config, pyr_levels);
        }
        public OneWayDescriptorObject(@ByVal CvSize patch_size, int pose_count, String pca_filename,
                String train_path/*=""*/, String images_list/*=""*/,
                float _scale_min/*=0.7f*/, float _scale_max/*=1.5f*/, float _scale_step/*=1.2f*/, int pyr_levels/*=1*/) {
            allocate(patch_size, pose_count, pca_filename, train_path, images_list, _scale_min, _scale_max, _scale_step, pyr_levels);
        }
        private native void allocate(@ByVal CvSize patch_size, int pose_count, String train_path, String pca_config,
                String pca_hr_config/*=null*/, String pca_desc_config/*=null*/, int pyr_levels/*=1*/);
        private native void allocate(@ByVal CvSize patch_size, int pose_count, String pca_filename,
                String train_path/*=""*/, String images_list/*=""*/,
                float _scale_min/*=0.7f*/, float _scale_max/*=1.5f*/, float _scale_step/*=1.2f*/, int pyr_levels/*=1*/);

        public native void Allocate(int train_feature_count, int object_feature_count);

        public native void SetLabeledFeatures(@Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint features);
        public native @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint GetLabeledFeatures();
        public native @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint _GetLabeledFeatures();

        public native int IsDescriptorObject(int desc_idx);
        public native int MatchPointToPart(@ByVal CvPoint pt);
        public native int GetDescriptorPart(int desc_idx);
        public native void InitializeObjectDescriptors(IplImage train_image,
                @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint features,
                String feature_label, int desc_start_idx/*=0*/, float scale/*=1.0f*/,
                int is_background/*=0*/);
        public native int GetObjectFeatureCount();

//        protected native IntPointer m_part_id();
//        protected native @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint m_train_features();
//        protected native int m_object_feature_count();
    }


    @Name("std::vector<std::vector<cv::KeyPoint> >") @Index
    public static class KeyPointVectorVector extends Pointer {
        static { load(); }
        public KeyPointVectorVector()       { allocate();  }
        public KeyPointVectorVector(long n) { allocate(n); }
        public KeyPointVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(long n);

        public native long size();
        public native void resize(long n);
        public native @Index(1) long size(long i);
        public native @Index(1) void resize(long i, long n);

        @ByRef public native KeyPoint get(long i, long j);
        public native KeyPointVectorVector put(long i, long j, KeyPoint value);
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

    @Namespace("cv") public static class FeatureDetector extends Pointer {
        static { load(); }
        public FeatureDetector() { }
        public FeatureDetector(Pointer p) { super(p); }

        public native void detect(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
        public native void detect(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray images,
                @ByRef KeyPointVectorVector keypoints, @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray masks/*=null*/);
        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
        public native boolean empty();
        public native static @ByVal FeatureDetectorPtr create(String detectorType);

//        protected abstract void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected static native void removeInvalidPoints(@Adapter("MatAdapter") CvArr mask, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
    }

    @Namespace("cv") public static class FastFeatureDetector extends FeatureDetector {
        static { load(); }
        public FastFeatureDetector() { }
        public FastFeatureDetector(Pointer p) { super(p); }
        public FastFeatureDetector(int threshold/*=10*/, boolean nonmaxSuppression/*=true*/) {
            allocate(threshold, nonmaxSuppression);
        }
        private native void allocate(int threshold/*=10*/, boolean nonmaxSuppression/*=true*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native int threshold();
//        protected native boolean nonmaxSuppression();
    }

    @Namespace("cv") public static class GoodFeaturesToTrackDetector extends FeatureDetector {
        static { load(); }
        @NoOffset public static class Params extends Pointer {
            static { load(); }
            public Params() { allocate(); }
            public Params(int maxCorners/*=1000*/, double qualityLevel/*=0.01*/, double minDistance/*=1.*/,
                    int blockSize/*=3*/, boolean useHarrisDetector/*=false*/, double k/*=0.04*/) {
                allocate(maxCorners, qualityLevel, minDistance, blockSize, useHarrisDetector, k);
            }
            public Params(int size) { allocateArray(size); }
            public Params(Pointer p) { super(p); }
            private native void allocate();
            private native void allocate(int maxCorners/*=1000*/, double qualityLevel/*=0.01*/, double minDistance/*=1.*/,
                    int blockSize/*=3*/, boolean useHarrisDetector/*=false*/, double k/*=0.04*/);
            private native void allocateArray(int size);

            public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
            public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

            public native int maxCorners();            public native Params maxCorners(int maxCorners);
            public native double qualityLevel();       public native Params qualityLevel(double qualityLevel);
            public native double minDistance();        public native Params minDistance(double minDistance);
            public native int blockSize();             public native Params blockSize(int blockSize);
            public native boolean useHarrisDetector(); public native Params useHarrisDetector(boolean useHarrisDetector);
            public native double k();                  public native Params k(double k);
        }
        public GoodFeaturesToTrackDetector() { allocate(); }
        public GoodFeaturesToTrackDetector(Pointer p) { super(p); }
        public GoodFeaturesToTrackDetector(Params params/*=Params()*/) { allocate(params); }
        public GoodFeaturesToTrackDetector(int maxCorners, double qualityLevel, double minDistance,
                int blockSize/*=3*/, boolean useHarrisDetector/*=false*/, double k/*=0.04*/) {
            allocate(maxCorners, qualityLevel, minDistance, blockSize, useHarrisDetector, k);
        }
        private native void allocate();
        private native void allocate(@ByRef Params params);
        private native void allocate(int maxCorners, double qualityLevel, double minDistance,
                int blockSize/*=3*/, boolean useHarrisDetector/*=false*/, double k/*=0.04*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native @ByRef Params params();
    }

    @Namespace("cv") public static class MserFeatureDetector extends FeatureDetector {
        static { load(); }
        public MserFeatureDetector() { allocate(); }
        public MserFeatureDetector(Pointer p) { super(p); }
        public MserFeatureDetector(CvMSERParams params/*=cvMSERParams()*/) { allocate(params); }
        public MserFeatureDetector(int delta, int minArea, int maxArea, double maxVariation, double minDiversity,
                int maxEvolution, double areaThreshold, double minMargin, int edgeBlurSize) {
            allocate(delta, minArea, maxArea, maxVariation, minDiversity,
                    maxEvolution, areaThreshold, minMargin, edgeBlurSize);
        }
        private native void allocate();
        private native void allocate(@ByVal CvMSERParams params);
        private native void allocate(int delta, int minArea, int maxArea, double maxVariation, double minDiversity,
                int maxEvolution, double areaThreshold, double minMargin, int edgeBlurSize);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native @ByRef MSER mser();
    }

    @Namespace("cv") public static class StarFeatureDetector extends FeatureDetector {
        static { load(); }
        public StarFeatureDetector() { allocate(); }
        public StarFeatureDetector(Pointer p) { super(p); }
        public StarFeatureDetector(CvStarDetectorParams params/*=cvStarDetectorParams()*/) { allocate(params); }
        public StarFeatureDetector(int maxSize, int responseThreshold/*=30*/, int lineThresholdProjected/*=10*/,
                int lineThresholdBinarized/*=8*/, int suppressNonmaxSize/*=5*/) {
            allocate(maxSize, responseThreshold, lineThresholdProjected, lineThresholdBinarized, suppressNonmaxSize);
        }
        private native void allocate();
        private native void allocate(@ByRef CvStarDetectorParams params);
        private native void allocate(int maxSize, int responseThreshold/*=30*/, int lineThresholdProjected/*=10*/,
                int lineThresholdBinarized/*=8*/, int suppressNonmaxSize/*=5*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native @ByRef StarDetector star();
    }

    @Namespace("cv") public static class SiftFeatureDetector extends FeatureDetector {
        static { load(); }
        public SiftFeatureDetector() { allocate(); }
        public SiftFeatureDetector(Pointer p) { super(p); }
        public SiftFeatureDetector(SIFT.DetectorParams detectorParams/*=SIFT::DetectorParams()*/,
                SIFT.CommonParams commonParams/*=SIFT::CommonParams()*/) {
            allocate(detectorParams, commonParams);
        }
        public SiftFeatureDetector(double threshold, double edgeThreshold,
                int nOctaves/*=SIFT::CommonParams::DEFAULT_NOCTAVES*/,
                int nOctaveLayers/*=SIFT::CommonParams::DEFAULT_NOCTAVE_LAYERS*/,
                int firstOctave/*=SIFT::CommonParams::DEFAULT_FIRST_OCTAVE*/,
                int angleMode/*=SIFT::CommonParams::FIRST_ANGLE*/) {
            allocate(threshold, edgeThreshold, nOctaves, nOctaveLayers, firstOctave, angleMode);
        }
        private native void allocate();
        private native void allocate(@ByRef SIFT.DetectorParams detectorParams, @ByRef SIFT.CommonParams commonParams);
        private native void allocate(double threshold, double edgeThreshold,
                int nOctaves/*=SIFT::CommonParams::DEFAULT_NOCTAVES*/,
                int nOctaveLayers/*=SIFT::CommonParams::DEFAULT_NOCTAVE_LAYERS*/,
                int firstOctave/*=SIFT::CommonParams::DEFAULT_FIRST_OCTAVE*/,
                int angleMode/*=SIFT::CommonParams::FIRST_ANGLE*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native @ByRef SIFT sift();
    }

    @Namespace("cv") public static class SurfFeatureDetector extends FeatureDetector {
        static { load(); }
        public SurfFeatureDetector() { allocate(); }
        public SurfFeatureDetector(Pointer p) { super(p); }
        public SurfFeatureDetector(double hessianThreshold/*=400.*/, int octaves/*=3*/, int octaveLayers/*=4*/, boolean upright/*=false*/) {
            allocate(hessianThreshold, octaves, octaveLayers, upright);
        }
        private native void allocate();
        private native void allocate(double hessianThreshold/*=400.*/, int octaves/*=3*/, int octaveLayers/*=4*/, boolean upright/*=false*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native @ByRef SURF surf();
    }

    @Namespace("cv") public static class OrbFeatureDetector extends FeatureDetector {
        static { load(); }
        public OrbFeatureDetector() { allocate(); }
        public OrbFeatureDetector(Pointer p) { super(p); }
        public OrbFeatureDetector(long n_features/*=700*/, @ByVal ORB.CommonParams params/*=new ORB.CommonParams()*/) {
            allocate(n_features, params);
        }
        private native void allocate();
        private native void allocate(long n_features/*=700*/, @ByVal ORB.CommonParams params/*=new ORB.CommonParams()*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
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

            public native float thresholdStep();         public native Params thresholdStep(float thresholdStep);
            public native float minThreshold();          public native Params minThreshold(float minThreshold);
            public native float maxThreshold();          public native Params maxThreshold(float maxThreshold);
            public native long minRepeatability();       public native Params minRepeatability(long minRepeatability);
            public native float minDistBetweenBlobs();   public native Params minDistBetweenBlobs(float minDistBetweenBlobs);

            public native boolean filterByColor();       public native Params filterByColor(boolean filterByColor);
            public native byte blobColor();              public native Params blobColor(byte blobColor);

            public native boolean filterByArea();        public native Params filterByArea(boolean filterByArea);
            public native float minArea();               public native Params minArea(float minArea);
            public native float maxArea();               public native Params maxArea(float maxArea);

            public native boolean filterByCircularity(); public native Params filterByCircularity(boolean filterByCircularity);
            public native float minCircularity();        public native Params minCircularity(float minCircularity);
            public native float maxCircularity();        public native Params maxCircularity(float maxCircularity);

            public native boolean filterByInertia();     public native Params filterByInertia(boolean filterByInertia);
            public native float minInertiaRatio();       public native Params minInertiaRatio(float minInertiaRatio);
            public native float maxInertiaRatio();       public native Params maxInertiaRatio(float maxInertiaRatio);

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
        @NoOffset public static class Params extends Pointer {
            static { load(); }
            public Params() { allocate(); }
            public Params(float initFeatureScale/*=1.f*/, int featureScaleLevels/*=1*/, float featureScaleMul/*=0.1f*/,
                    int initXyStep/*=6*/, int initImgBound/*=0*/, boolean varyXyStepWithScale/*=true*/, boolean varyImgBoundWithScale/*=false*/) {
                allocate(initFeatureScale, featureScaleLevels, featureScaleMul, initXyStep, initImgBound, varyXyStepWithScale, varyImgBoundWithScale);
            }
            public Params(int size) { allocateArray(size); }
            public Params(Pointer p) { super(p); }
            private native void allocate();
            private native void allocate(float initFeatureScale/*=1.f*/, int featureScaleLevels/*=1*/, float featureScaleMul/*=0.1f*/,
                    int initXyStep/*=6*/, int initImgBound/*=0*/, boolean varyXyStepWithScale/*=true*/, boolean varyImgBoundWithScale/*=false*/);
            private native void allocateArray(int size);

            public native float initFeatureScale();        public native Params initFeatureScale(float initFeatureScale);
            public native int featureScaleLevels();        public native Params featureScaleLevels(int featureScaleLevels);
            public native float featureScaleMul();         public native Params featureScaleMul(float featureScaleMul);

            public native int initXyStep();                public native Params initXyStep(int initXyStep);
            public native int initImgBound();              public native Params initImgBound(int initImgBound);

            public native boolean varyXyStepWithScale();   public native Params varyXyStepWithScale(boolean varyXyStepWithScale);
            public native boolean varyImgBoundWithScale(); public native Params varyImgBoundWithScale(boolean varyImgBoundWithScale);

            public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
            public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
        }
        public DenseFeatureDetector() { allocate(); }
        public DenseFeatureDetector(Pointer p) { super(p); }
        public DenseFeatureDetector(Params params/*=Params()*/) { allocate(params); }
        private native void allocate();
        private native void allocate(@ByRef Params params);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native @ByRef Params params();
    }

    @Namespace("cv") public static class GridAdaptedFeatureDetector extends FeatureDetector {
        static { load(); }
        public GridAdaptedFeatureDetector() { }
        public GridAdaptedFeatureDetector(Pointer p) { super(p); }
        public GridAdaptedFeatureDetector(@ByRef FeatureDetectorPtr detector, int maxTotalKeypoints/*=1000*/,
                int gridRows/*=4*/, int gridCols/*=4*/) {
            allocate(detector, maxTotalKeypoints, gridRows, gridCols);
        }
        private native void allocate(@ByRef FeatureDetectorPtr detector, int maxTotalKeypoints/*=1000*/,
                int gridRows/*=4*/, int gridCols/*=4*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//        public native boolean empty();
//
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

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//        public native boolean empty();
//
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
        public DynamicAdaptedFeatureDetector(@ByRef AdjusterAdapterPtr adjaster,
                int min_features/*=400*/, int max_features/*=500*/, int max_iters/*=5*/) {
            allocate(adjaster, min_features, max_features, max_iters);
        }
        private native void allocate(@ByRef AdjusterAdapterPtr adjaster,
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
        private native void allocate(int init_thresh/*=20*/, boolean nonmax/*=true*/, int min_thresh/*=1*/, int max_thresh/*=200*/);

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
//        protected double thresh_, init_thresh_, min_thresh_, max_thresh_;
//        protected native @ByRef CvStarDetectorParams params_();
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
//        protected double thresh_, init_thresh_, min_thresh_, max_thresh_;
    }

    public static native @Adapter("MatAdapter") CvMat windowedMatchingMask(
            @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints1,
            @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint keypoints2,
            float maxDeltaX, float maxDeltaY);

    
    @Name("cv::Ptr<cv::DescriptorExtractor>")
    public static class DescriptorExtractorPtr extends Pointer {
        static { load(); }
        public DescriptorExtractorPtr()       { allocate();  }
        public DescriptorExtractorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native DescriptorExtractor get();
        public native DescriptorExtractorPtr put(DescriptorExtractor value);
    }

    @Namespace("cv") public static class DescriptorExtractor extends Pointer {
        static { load(); }
        public DescriptorExtractor() { }
        public DescriptorExtractor(Pointer p) { super(p); }

        public native void compute(@Adapter("MatAdapter") CvArr image,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
        public native void compute(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray images,
                @ByRef KeyPointVectorVector keypoints,
                @Adapter(value="VectorAdapter<CvMat*,cv::Mat>"/*, out=true*/) CvMatArray descriptors);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

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

    @Namespace("cv") public static class SiftDescriptorExtractor extends DescriptorExtractor {
        static { load(); }
        public SiftDescriptorExtractor() { allocate(); }
        public SiftDescriptorExtractor(Pointer p) { super(p); }
        public SiftDescriptorExtractor(SIFT.DescriptorParams descriptorParams/*=SIFT::DescriptorParams()*/,
                SIFT.CommonParams commonParams/*=SIFT::CommonParams()*/) {
            allocate(descriptorParams, commonParams);
        }
        public SiftDescriptorExtractor(double magnification, boolean isNormalize/*=true*/,
                boolean recalculateAngles/*=true*/, int nOctaves/*=SIFT::CommonParams::DEFAULT_NOCTAVES*/,
                int nOctaveLayers/*=SIFT::CommonParams::DEFAULT_NOCTAVE_LAYERS*/,
                int firstOctave/*=SIFT::CommonParams::DEFAULT_FIRST_OCTAVE*/,
                int angleMode/*=SIFT::CommonParams::FIRST_ANGLE*/) {
            allocate(magnification, isNormalize, recalculateAngles, nOctaves, nOctaveLayers, firstOctave, angleMode);
        }
        private native void allocate();
        private native void allocate(@ByRef SIFT.DescriptorParams descriptorParams,
                @ByRef SIFT.CommonParams commonParams);
        private native void allocate(double magnification, boolean isNormalize/*=true*/,
                boolean recalculateAngles/*=true*/, int nOctaves/*=SIFT::CommonParams::DEFAULT_NOCTAVES*/,
                int nOctaveLayers/*=SIFT::CommonParams::DEFAULT_NOCTAVE_LAYERS*/,
                int firstOctave/*=SIFT::CommonParams::DEFAULT_FIRST_OCTAVE*/,
                int angleMode/*=SIFT::CommonParams::FIRST_ANGLE*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        public native int descriptorSize();
//        public native int descriptorType();

//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
//        protected native @ByRef SIFT sift();
    }

    @Namespace("cv") public static class SurfDescriptorExtractor extends DescriptorExtractor {
        static { load(); }
        public SurfDescriptorExtractor() { allocate(); }
        public SurfDescriptorExtractor(Pointer p) { super(p); }
        public SurfDescriptorExtractor(int nOctaves/*=4*/, int nOctaveLayers/*=2*/, boolean extended/*=false*/, boolean upright/*=false*/) {
            allocate(nOctaves, nOctaveLayers, extended, upright);
        }
        private native void allocate();
        private native void allocate(int nOctaves/*=4*/, int nOctaveLayers/*=2*/, boolean extended/*=false*/, boolean upright/*=false*/);

//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        public native int descriptorSize();
//        public native int descriptorType();

//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
//        protected native @ByRef SURF surf();
    }

    @Namespace("cv") public static class OrbDescriptorExtractor extends DescriptorExtractor {
        static { load(); }
        public OrbDescriptorExtractor() { allocate(); }
        public OrbDescriptorExtractor(Pointer p) { super(p); }
        public OrbDescriptorExtractor(@ByVal ORB.CommonParams params/*=new ORB.CommonParams()*/) {
            allocate(params);
        }
        private native void allocate();
        private native void allocate(@ByVal ORB.CommonParams params/*=new ORB.CommonParams()*/);

//        public native int descriptorSize();
//        public native int descriptorType();
//
//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
    }

    @Name("cv::CalonderDescriptorExtractor<float>")
    public static class FloatCalonderDescriptorExtractor extends DescriptorExtractor {
        static { load(); }
        public FloatCalonderDescriptorExtractor() { }
        public FloatCalonderDescriptorExtractor(Pointer p) { super(p); }
        public FloatCalonderDescriptorExtractor(String classifierFile) {
            allocate(classifierFile);
        }
        private native void allocate(String classifierFile);

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
//        protected native @ByRef RTreeClassifier classifier_();
        protected static final int BORDER_SIZE = 16;
    }

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


    @Namespace("cv") public static class HammingLUT extends Pointer {
        static { load(); }
        public HammingLUT() { allocate(); }
        public HammingLUT(Pointer p) { super(p); }
        private native void allocate();

        public native @Name("operator()") int d(@Cast("unsigned char*") byte[] a,
                @Cast("unsigned char*") byte[] b, int size);
        public static native byte byteBitsLookUp(byte b);
    }

    @Namespace("cv") public static class Hamming extends Pointer {
        static { load(); }
        public Hamming() { allocate(); }
        public Hamming(Pointer p) { super(p); }
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
        private native void allocate();
        private native void allocate(int _queryIdx, int _trainIdx, float _distance);
        private native void allocate(int _queryIdx, int _trainIdx, int _imgIdx, float _distance);

        public native int queryIdx();   public native DMatch queryIdx(int queryIdx);
        public native int trainIdx();   public native DMatch trainIdx(int trainIdx);
        public native int imgIdx();     public native DMatch imgIdx(int imgIdx);

        public native float distance(); public native DMatch distance(float distance);

        public native @Name("operator<") boolean compare(@ByRef DMatch m);
    }

    @Name("std::vector<std::vector<cv::DMatch> >") @Index
    public static class DMatchVectorVector extends Pointer {
        static { load(); }
        public DMatchVectorVector()       { allocate();  }
        public DMatchVectorVector(long n) { allocate(n); }
        public DMatchVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(long n);

        public native long size();
        public native void resize(long n);
        public native @Index(1) long size(long i);
        public native @Index(1) void resize(long i, long n);

        @ByRef public native DMatch get(long i, long j);
        public native DMatchVectorVector put(long i, long j, DMatch value);
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

    @Namespace("cv") public static class DescriptorMatcher extends Pointer {
        static { load(); }
        public DescriptorMatcher() { }
        public DescriptorMatcher(Pointer p) { super(p); }

        public native void add(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray descriptors);
        public native @Name("getTrainDescriptors().at") @Adapter("MatAdapter") CvMat getTrainDescriptors(int i);
        public native void clear();
        public native boolean empty();
        public /*abstract*/ native boolean isMaskSupported();
        public native void train();

        public native void match(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter("MatAdapter") CvArr trainDescriptors,
                @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches, @Adapter("MatAdapter") CvArr mask/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter("MatAdapter") CvArr trainDescriptors,
                @ByRef DMatchVectorVector matches, int k, @Adapter("MatAdapter") CvArr mask/*=null*/, boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter("MatAdapter") CvArr trainDescriptors,
                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("MatAdapter") CvArr mask/*=null*/, boolean compactResult/*=false*/);

        public native void match(@Adapter("MatAdapter") CvArr queryDescriptors, @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/, boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/, boolean compactResult/*=false*/);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

        public /*abstract*/ native @ByVal DescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);

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
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//        protected /*abstract*/ native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//
//        protected native static boolean isPossibleMatch(@Adapter("MatAdapter") CvArr mask, int queryIdx, int trainIdx);
//        protected native static boolean isMaskedOut(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvArrArray masks, int queryIdx);
//
//        protected static native @Adapter("MatAdapter") CvMat clone_op(@Adapter("MatAdapter") CvMat m);
//        protected static native @Adapter("MatAdapter") IplImage clone_op(@Adapter("MatAdapter") IplImage m);
//        protected native void checkMasks(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, int queryDescriptorsCount);
//
//        protected native @Name("trainDescCollection.at") @Adapter("MatAdapter") CvMat trainDescCollection(int i);
    }

    @Name("cv::BruteForceMatcher<cv::L2<float> >")
    public static class FloatL2BruteForceMatcher extends DescriptorMatcher {
        static { load(); }
        public FloatL2BruteForceMatcher()       { allocate();  }
//        public FloatL2BruteForceMatcher(cv::L2<float> d = cv::L2<float>());
        public FloatL2BruteForceMatcher(Pointer p) { super(p); }
        private native void allocate();

//        public native boolean isMaskSupported();
//        public native @ByVal DescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//
//        protected cv::L2<float> distance();
    }

    @Name("cv::BruteForceMatcher<cv::SL2<float> >")
    public static class FloatSL2BruteForceMatcher extends DescriptorMatcher {
        static { load(); }
        public FloatSL2BruteForceMatcher()       { allocate();  }
//        public FloatSL2BruteForceMatcher(cv::SL2<float> d = cv::SL2<float>());
        public FloatSL2BruteForceMatcher(Pointer p) { super(p); }
        private native void allocate();

//        public native boolean isMaskSupported();
//        public native @ByVal DescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//
//        protected cv::SL2<float> distance();
    }

    @Namespace("cv") public static class FlannBasedMatcher extends DescriptorMatcher {
        static { load(); }
        public FlannBasedMatcher()       { allocate();  }
//        public FlannBasedMatcher(@ByRef Ptr<flann::IndexParams>& indexParams=new flann::KDTreeIndexParams(),
//                @ByRef Ptr<flann::SearchParams> searchParams=new flann::SearchParams());
        public FlannBasedMatcher(Pointer p) { super(p); }
        private native void allocate();

//        public native void add(@Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray descriptors);
//        public native void clear();
//
//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//
//        public native void train();
//        public native boolean isMaskSupported();
//        public native DescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
//
//        protected static native void convertToDMatches(@ByRef DescriptorCollection descriptors,
//                CvMat indices, CvMat distances, @ByRef DMatchVectorVector matches );
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, int k,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryDescriptors, @ByRef DMatchVectorVector matches, float maxDistance,
//                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=vector<Mat>()*/, boolean compactResult/*=false*/);
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

        public native void add(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray images,
                @ByRef KeyPointVectorVector keypoints);
        public native @Name("getTrainImages().at") @Adapter("MatAdapter") IplImage getTrainImages(int i);
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
                @ByRef DMatchVectorVector matches, int k, @Adapter("MatAdapter") CvArr mask/*=null*/, boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter("MatAdapter") CvArr trainImage, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint trainKeypoints,
                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("MatAdapter") CvArr mask/*=null*/, boolean compactResult/*=false*/);

        public native void match(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @Adapter(value="VectorAdapter<cv::DMatch>", out=true) DMatch matches,
                @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks/*=null*/);
        public native void knnMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>")
                CvMatArray masks/*=null*/, boolean compactResult/*=false*/);
        public native void radiusMatch(@Adapter("MatAdapter") CvArr queryImage,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint queryKeypoints,
                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>")
                CvMatArray masks/*=null*/, boolean compactResult/*=false*/);

        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);

        public native boolean empty();

        public /*abstract*/ native @ByVal GenericDescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
        public native static @ByVal GenericDescriptorMatcherPtr create(String genericDescritptorMatcherType,
                String paramsFilename/*=""*/);

//        protected /*abstract*/ native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
//        protected /*abstract*/ native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
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
//            public native @Name("getImages().at") @Adapter("MatAdapter") IplImage getImages();
//            public native @Adapter("MatAdapter") IplImage getImage(int imgIdx);
//
//            protected native int pointCount();
//
//            protected native @Name("images.at") @Adapter("MatAdapter") IplImage images(int i);
//            protected native @ByRef KeyPointVectorVector keypoints();
//            protected native @Adapter("VectorAdapter<int>") int[] startIndices();
//        }
//
//        protected native KeyPointCollection trainPointCollection();
    }

    @Name("cv::Ptr<cv::OneWayDescriptorBase>")
    public static class OneWayDescriptorBasePtr extends Pointer {
        static { load(); }
        public OneWayDescriptorBasePtr()       { allocate();  }
        public OneWayDescriptorBasePtr(Pointer p) { super(p); }
        private native void allocate();

        public native OneWayDescriptorBase get();
        public native OneWayDescriptorBasePtr put(OneWayDescriptorBase value);
    }

    @Namespace("cv") public static class OneWayDescriptorMatcher extends GenericDescriptorMatcher {
        static { load(); }
        @NoOffset public static class Params extends Pointer {
            static { load(); }
            public static final int
                    POSE_COUNT = 500,
                    PATCH_WIDTH = 24,
                    PATCH_HEIGHT = 24;
            public static native float GET_MIN_SCALE();
            public static native float GET_MAX_SCALE();
            public static native float GET_STEP_SCALE();

            public Params() { allocate(); }
            public Params(int poseCount/*=POSE_COUNT*/, @ByVal CvSize patchSize/*=cvSize(PATCH_WIDTH, PATCH_HEIGHT)*/,
                    String pcaFilename/*=""*/, String trainPath/*=""*/, String trainImagesList/*=""*/,
                    float minScale/*=GET_MIN_SCALE()*/, float maxScale/*=GET_MAX_SCALE()*/, float stepScale/*=GET_STEP_SCALE()*/) {
                allocate(poseCount, patchSize, pcaFilename, trainPath, trainImagesList, minScale, maxScale, stepScale);
            }
            public Params(int size) { allocateArray(size); }
            public Params(Pointer p) { super(p); }
            private native void allocate();
            private native void allocate(int poseCount/*=POSE_COUNT*/, @ByVal CvSize patchSize/*=cvSize(PATCH_WIDTH, PATCH_HEIGHT)*/,
                    String pcaFilename/*=""*/, String trainPath/*=""*/, String trainImagesList/*=""*/,
                    float minScale/*=GET_MIN_SCALE()*/, float maxScale/*=GET_MAX_SCALE()*/, float stepScale/*=GET_STEP_SCALE()*/);
            private native void allocateArray(int size);

            public native int poseCount();                 public native Params poseCount(int poseCount);
            public native @ByVal CvSize patchSize();       public native Params patchSize(CvSize patchSize);
            public native @ByRef String pcaFilename();     public native Params pcaFilename(String pcaFilename);
            public native @ByRef String trainPath();       public native Params trainPath(String trainPath);
            public native @ByRef String trainImagesList(); public native Params trainImagesList(String trainImagesList);

            public native float minScale();                public native Params minScale(float minScale);
            public native float maxScale();                public native Params maxScale(float maxScale);
            public native float stepScale();               public native Params stepScale(float stepScale);
        }
        public OneWayDescriptorMatcher() { allocate(); }
        public OneWayDescriptorMatcher(Pointer p) { super(p); }
        public OneWayDescriptorMatcher(Params params/*=Params()*/) { allocate(params); }
        private native void allocate();
        private native void allocate(@ByRef Params params);

        public native void initialize(@ByRef Params params, @ByRef OneWayDescriptorBasePtr base/*=OneWayDescriptorBasePtr()*/);

//        public native void clear();
//        public native void train();
//        public native boolean isMaskSupported();
//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//        public native boolean empty();
//        public native GenericDescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
//
//        protected native @ByRef OneWayDescriptorBasePtr base();
//        protected native Params params();
//        protected native int prevTrainCount();
    }

    @Namespace("cv") public static class FernDescriptorMatcher extends GenericDescriptorMatcher {
        static { load(); }
        @NoOffset public static class Params extends Pointer {
            static { load(); }
            public Params() { allocate(); }
            public Params(int nclasses/*=0*/, int patchSize/*=FernClassifier::PATCH_SIZE*/,
                    int signatureSize/*=FernClassifier::DEFAULT_SIGNATURE_SIZE*/,
                    int nstructs/*=FernClassifier::DEFAULT_STRUCTS*/,
                    int structSize/*=FernClassifier::DEFAULT_STRUCT_SIZE*/,
                    int nviews/*=FernClassifier::DEFAULT_VIEWS*/,
                    int compressionMethod/*=FernClassifier::COMPRESSION_NONE*/,
                    @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/) {
                allocate(nclasses, patchSize, signatureSize, nstructs, structSize,
                        nviews, compressionMethod, patchGenerator);
            }
            public Params(String filename) { allocate(filename); }
            public Params(int size) { allocateArray(size); }
            public Params(Pointer p) { super(p); }
            private native void allocate();
            private native void allocate(int nclasses/*=0*/, int patchSize/*=FernClassifier::PATCH_SIZE*/,
                int signatureSize/*=FernClassifier::DEFAULT_SIGNATURE_SIZE*/,
                int nstructs/*=FernClassifier::DEFAULT_STRUCTS*/,
                int structSize/*=FernClassifier::DEFAULT_STRUCT_SIZE*/,
                int nviews/*=FernClassifier::DEFAULT_VIEWS*/,
                int compressionMethod/*=FernClassifier::COMPRESSION_NONE*/,
                @ByRef PatchGenerator patchGenerator/*=PatchGenerator()*/);
            private native void allocate(String filename);
            private native void allocateArray(int size);

            public native int nclasses();          public native Params nclasses(int nclasses);
            public native int patchSize();         public native Params patchSize(int patchSize);
            public native int signatureSize();     public native Params signatureSize(int signatureSize);
            public native int nstructs();          public native Params nstructs(int nstructs);
            public native int structSize();        public native Params structSize(int structSize);
            public native int nviews();            public native Params nviews(int nviews);
            public native int compressionMethod(); public native Params compressionMethod(int compressionMethod);
            public native @ByRef PatchGenerator patchGenerator(); public native Params patchGenerator(PatchGenerator patchGenerator);

            public native @ByRef String filename(); public native Params filename(String filename);
        }
        public FernDescriptorMatcher() { allocate(); }
        public FernDescriptorMatcher(Pointer p) { super(p); }
        public FernDescriptorMatcher(Params params/*=Params()*/) { allocate(params); }
        private native void allocate();
        private native void allocate(@ByRef Params params);

//        public native void clear();
//        public native void train();
//        public native boolean isMaskSupported();
//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//        public native boolean empty();
//        public native GenericDescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
//
//        protected native void trainFernClassifier();
//        protected native void calcBestProbAndMatchIdx(@Adapter("MatAdapter") CvArr image, @ByRef CvPoint2D32f pt,
//                @ByRef float[] bestProb, @ByRef int[] bestMatchIdx,
//                @Adapter(value="VectorAdapter<float>", out=true) float[] signature);
//        protected native FernClassifierPtr classifier();
//        protected native @ByRef Params params();
//        protected native int prevTrainCount();
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
//
//        public native void clear();
//        public native void train();
//        public native boolean isMaskSupported();
//        public native void read(@Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
//        public native void write(@Adapter("FileStorageAdapter") CvFileStorage fs);
//        public native boolean empty();
//        public native GenericDescriptorMatcherPtr clone(boolean emptyTrainData/*=false*/);
//
//        protected native void knnMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, int k, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
//        protected native void radiusMatchImpl(@Adapter("MatAdapter") CvArr queryImage, @Adapter("VectorAdapter<cv::KeyPoint>") KeyPoint queryKeypoints,
//                @ByRef DMatchVectorVector matches, float maxDistance, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray masks, boolean compactResult);
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
        public native @Name("getDescriptors().at") @Adapter("MatAdapter") CvMat getDescriptors(int i);
        public native int descripotorsCount();

        public native void clear();

        public /*abstract*/ native @Adapter("MatAdapter") CvMat cluster();
        public /*abstract*/ native @Adapter("MatAdapter") CvMat cluster(CvMat descriptors);

//        protected native @Name("descriptors.at") @Adapter("MatAdapter") CvMat descriptors(int i);
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
//
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
