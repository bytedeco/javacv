/*
 * Copyright (C) 2012 Samuel Audet
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
 * This file is based on information found in videostab.hpp and all included
 * files of OpenCV 2.4.0, which are covered by the following copyright notice:
 *
 *                          License Agreement
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

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Namespace;
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
        include={"<opencv2/videostab/videostab.hpp>", "opencv_adapters.h"},
        link={"opencv_videostab", "opencv_video", "opencv_nonfree", "opencv_gpu", "opencv_photo", "opencv_objdetect", "opencv_features2d",
              "opencv_flann", "opencv_calib3d", "opencv_highgui", "opencv_imgproc", "opencv_core"}),
    @Platform(value="android",
        link={"opencv_videostab", "opencv_video", "opencv_nonfree", "opencv_photo", "opencv_objdetect", "opencv_features2d",
              "opencv_flann", "opencv_calib3d", "opencv_highgui", "opencv_imgproc", "opencv_core"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_videostab240", "opencv_video240", "opencv_nonfree240", "opencv_gpu240", "opencv_photo240", "opencv_objdetect240", "opencv_features2d240",
              "opencv_flann240", "opencv_calib3d240", "opencv_highgui240", "opencv_imgproc240", "opencv_core240"},  preload="opencv_gpu240"),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_videostab {
    static { load(opencv_calib3d.class); load(opencv_features2d.class); load(opencv_objdetect.class);
             load(opencv_photo.class); load(opencv_nonfree.class); load(opencv_video.class); load(); }

    // #include "opencv2/videostab/optical_flow.hpp"
    @Namespace("cv::videostab") public static class ISparseOptFlowEstimator extends Pointer {
        static { load(); }
        public ISparseOptFlowEstimator() { }
        public ISparseOptFlowEstimator(Pointer p) { super(p); }

        public /*abstract*/ native void run(@Adapter("ArrayAdapter") CvArr frame0, @Adapter("ArrayAdapter") CvArr frame1,
                @Adapter("ArrayAdapter") CvArr points0, @Adapter("ArrayAdapter") CvArr points1,
                @Adapter("ArrayAdapter") CvArr status,  @Adapter("ArrayAdapter") CvArr errors);
    }

    @Name("cv::Ptr<cv::videostab::ISparseOptFlowEstimator>")
    public static class ISparseOptFlowEstimatorPtr extends Pointer {
        static { load(); }
        public ISparseOptFlowEstimatorPtr()       { allocate();  }
        public ISparseOptFlowEstimatorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native ISparseOptFlowEstimator get();
        public native ISparseOptFlowEstimatorPtr put(ISparseOptFlowEstimator value);
    }

    @Namespace("cv::videostab") public static class IDenseOptFlowEstimator extends Pointer {
        static { load(); }
        public IDenseOptFlowEstimator() { }
        public IDenseOptFlowEstimator(Pointer p) { super(p); }

        public /*abstract*/ native void run(@Adapter("ArrayAdapter") CvArr frame0, @Adapter("ArrayAdapter") CvArr frame1,
                @Adapter("ArrayAdapter") CvArr flowX, @Adapter("ArrayAdapter") CvArr flowY, @Adapter("ArrayAdapter") CvArr errors);
    }

    @Name("cv::Ptr<cv::videostab::IDenseOptFlowEstimator>")
    public static class IDenseOptFlowEstimatorPtr extends Pointer {
        static { load(); }
        public IDenseOptFlowEstimatorPtr()       { allocate();  }
        public IDenseOptFlowEstimatorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native IDenseOptFlowEstimator get();
        public native IDenseOptFlowEstimatorPtr put(IDenseOptFlowEstimator value);
    }

    @Namespace("cv::videostab") public static class PyrLkOptFlowEstimatorBase extends Pointer {
        static { load(); }
        public PyrLkOptFlowEstimatorBase() { allocate(); }
        public PyrLkOptFlowEstimatorBase(Pointer p) { super(p); }
        private native void allocate();

        public native void setWinSize(@ByVal CvSize val);
        public native @Const @ByVal CvSize winSize();

        public native void setMaxLevel(int val);
        public native int maxLevel();

//        protected native @ByRef CvSize winSize_();
//        protected native int maxLevel_();
    }

    public static native @Name("dynamic_cast<cv::videostab::PyrLkOptFlowEstimatorBase*>")
            PyrLkOptFlowEstimatorBase castPyrLkOptFlowEstimatorBase(SparsePyrLkOptFlowEstimator pointer);
    public static native @Name("dynamic_cast<cv::videostab::ISparseOptFlowEstimator*>")
            ISparseOptFlowEstimator castISparseOptFlowEstimator(SparsePyrLkOptFlowEstimator pointer);

    @Namespace("cv::videostab") public static class SparsePyrLkOptFlowEstimator extends
            /*PyrLkOptFlowEstimatorBase, ISparseOptFlowEstimator*/ Pointer {
        static { load(); }
        public SparsePyrLkOptFlowEstimator() { allocate(); }
        public SparsePyrLkOptFlowEstimator(Pointer p) { super(p); }
        private native void allocate();

        public native void run(@Adapter("ArrayAdapter") CvArr frame0, @Adapter("ArrayAdapter") CvArr frame1,
                @Adapter("ArrayAdapter") CvArr points0, @Adapter("ArrayAdapter") CvArr points1,
                @Adapter("ArrayAdapter") CvArr status,  @Adapter("ArrayAdapter") CvArr errors);

        public PyrLkOptFlowEstimatorBase getPyrLkOptFlowEstimatorBase() { return castPyrLkOptFlowEstimatorBase(this); }
        public ISparseOptFlowEstimator getISparseOptFlowEstimator() { return castISparseOptFlowEstimator(this); }
    }

    @Platform(not="android") public static native @Name("dynamic_cast<cv::videostab::PyrLkOptFlowEstimatorBase*>")
            PyrLkOptFlowEstimatorBase castPyrLkOptFlowEstimatorBase(DensePyrLkOptFlowEstimatorGpu pointer);
    @Platform(not="android") public static native @Name("dynamic_cast<cv::videostab::IDenseOptFlowEstimator*>")
            IDenseOptFlowEstimator castIDenseOptFlowEstimator(DensePyrLkOptFlowEstimatorGpu pointer);

    @Platform(not="android") @Namespace("cv::videostab") public static class DensePyrLkOptFlowEstimatorGpu extends
            /*PyrLkOptFlowEstimatorBase, IDenseOptFlowEstimator*/ Pointer {
        static { load(); }
        public DensePyrLkOptFlowEstimatorGpu() { allocate(); }
        public DensePyrLkOptFlowEstimatorGpu(Pointer p) { super(p); }
        private native void allocate();

        public native void run(@Adapter("ArrayAdapter") CvArr frame0, @Adapter("ArrayAdapter") CvArr frame1,
                @Adapter("ArrayAdapter") CvArr flowX, @Adapter("ArrayAdapter") CvArr flowY, @Adapter("ArrayAdapter") CvArr errors);

        public PyrLkOptFlowEstimatorBase getPyrLkOptFlowEstimatorBase() { return castPyrLkOptFlowEstimatorBase(this); }
        public IDenseOptFlowEstimator getIDenseOptFlowEstimator() { return castIDenseOptFlowEstimator(this); }
    }


    // #include "opencv2/videostab/global_motion.hpp"
    // enum MotionModel
    public static final int
            TRANSLATION = 0,
            TRANSLATION_AND_SCALE = 1,
            LINEAR_SIMILARITY = 2,
            AFFINE = 3;

    @Namespace("cv::videostab") public static native @Adapter("MatAdapter") CvMat estimateGlobalMotionLeastSquares(
            @Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f points0,
            @Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f points1,
            int model/*=AFFINE*/, float[] rmse/*=null*/);

    @NoOffset @Namespace("cv::videostab") public static class RansacParams extends Pointer {
        static { load(); }
        public RansacParams(int size, float thresh, float eps, float prob) {
            allocate(size, thresh, eps, prob);
        }
        public RansacParams(Pointer p) { super(p); }
        private native void allocate(int size, float thresh, float eps, float prob);

        public native int size();     public native RansacParams size(int size);
        public native float thresh(); public native RansacParams thresh(float tresh);
        public native float eps();    public native RansacParams eps(float eps);
        public native float prob();   public native RansacParams prob(float prob);

        static RansacParams translationMotionStd() { return new RansacParams(2, 0.5f, 0.5f, 0.99f); }
        static RansacParams translationAndScale2dMotionStd() { return new RansacParams(3, 0.5f, 0.5f, 0.99f); }
        static RansacParams linearSimilarityMotionStd() { return new RansacParams(4, 0.5f, 0.5f, 0.99f); }
        static RansacParams affine2dMotionStd() { return new RansacParams(6, 0.5f, 0.5f, 0.99f); }
    }

    @Namespace("cv::videostab") public static native @Adapter("MatAdapter") CvMat estimateGlobalMotionRobust(
            @Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f points0,
            @Adapter("VectorAdapter<CvPoint2D32f,cv::Point2f>") CvPoint2D32f points1,
            int model/*=AFFINE*/, @ByRef RansacParams params/*=RansacParams.affine2dMotionStd()*/,
            float[] rmse/*=null*/, int[] ninliers/*=null*/);

    @Namespace("cv::videostab") public static class IGlobalMotionEstimator extends Pointer {
        static { load(); }
        public IGlobalMotionEstimator() { }
        public IGlobalMotionEstimator(Pointer p) { super(p); }

        public /*absstract*/ native @Adapter("MatAdapter") CvMat estimate(IplImage frame0, IplImage frame1);
    }

    @Name("cv::Ptr<cv::videostab::IGlobalMotionEstimator>")
    public static class IGlobalMotionEstimatorPtr extends Pointer {
        static { load(); }
        public IGlobalMotionEstimatorPtr()       { allocate();  }
        public IGlobalMotionEstimatorPtr(Pointer p) { super(p); }
        private native void allocate();

        public native IGlobalMotionEstimator get();
        public native IGlobalMotionEstimatorPtr put(IGlobalMotionEstimator value);
    }

    @Namespace("cv::videostab") public static class PyrLkRobustMotionEstimator extends IGlobalMotionEstimator {
        static { load(); }
        public PyrLkRobustMotionEstimator() { allocate(); }
        public PyrLkRobustMotionEstimator(Pointer p) { super(p); }
        private native void allocate();

        public native void setDetector(@ByVal FeatureDetectorPtr val);
        public native @ByVal FeatureDetectorPtr detector();

        public native void setOptFlowEstimator(@ByVal ISparseOptFlowEstimatorPtr val);
        public native @ByVal ISparseOptFlowEstimatorPtr optFlowEstimator();

        public native void setMotionModel(@Cast("cv::videostab::MotionModel") int val);
        public native @Cast("cv::videostab::MotionModel") int motionModel();

        public native void setRansacParams(@ByRef RansacParams val);
        public native @ByVal RansacParams ransacParams();

        public native void setMaxRmse(float val);
        public native float maxRmse();

        public native void setMinInlierRatio(float val);
        public native float minInlierRatio();

//        public native @Adapter("MatAdapter") CvMat estimate(IplImage frame0, IplImage frame1);
    }

    @Namespace("cv::videostab") public static native @Adapter("MatAdapter") CvMat getMotion(int from, int to, @Adapter("MatAdapter") CvMat motions, int size);
    @Namespace("cv::videostab") public static native @Adapter("MatAdapter") CvMat getMotion(int from, int to, @Adapter("VectorAdapter<CvMat*,cv::Mat>") CvMatArray motions);


    // #include "opencv2/videostab/motion_stabilizing.hpp"
    @Namespace("cv::videostab") public static class IMotionStabilizer extends Pointer {
        static { load(); }
        public IMotionStabilizer() { }
        public IMotionStabilizer(Pointer p) { super(p); }

        public /*abstract*/ native void stabilize(@Adapter("MatAdapter") CvMat motions, int size, @Adapter("MatAdapter") CvMat stabilizationMotions);
    }

    @Name("cv::Ptr<cv::videostab::IMotionStabilizer>")
    public static class IMotionStabilizerPtr extends Pointer {
        static { load(); }
        public IMotionStabilizerPtr()       { allocate();  }
        public IMotionStabilizerPtr(Pointer p) { super(p); }
        private native void allocate();

        public native IMotionStabilizer get();
        public native IMotionStabilizerPtr put(IMotionStabilizer value);
    }

    @Namespace("cv::videostab") public static class MotionFilterBase extends IMotionStabilizer {
        static { load(); }
        public MotionFilterBase() { }
        public MotionFilterBase(Pointer p) { super(p); }

        public native void setRadius(int val);
        public native int radius();

        public native void update();

        public /*abstract*/ native @Adapter("MatAdapter") CvMat stabilize(int index, @Adapter("MatAdapter") CvMat motions, int size);
//        public native void stabilize(@Adapter("MatAdapter") CvMat motions, int size, @Adapter("MatAdapter") CvMat stabilizationMotions);

//        protected native int radius_();
    }

    @Name("cv::Ptr<cv::videostab::MotionFilterBase>")
    public static class MotionFilterBasePtr extends Pointer {
        static { load(); }
        public MotionFilterBasePtr()       { allocate();  }
        public MotionFilterBasePtr(Pointer p) { super(p); }
        private native void allocate();

        public native MotionFilterBase get();
        public native MotionFilterBasePtr put(MotionFilterBase value);
    }

    @Namespace("cv::videostab") public static class GaussianMotionFilter extends MotionFilterBase {
        static { load(); }
        public GaussianMotionFilter() { allocate(); }
        public GaussianMotionFilter(Pointer p) { super(p); }
        private native void allocate();

        public native void setStdev(float val);
        public native float stdev();

//        public native void update();

//        public native @Adapter("MatAdapter") CvMat stabilize(int index, @Adapter("MatAdapter") CvMat motions, int size);
    }

    @Namespace("cv::videostab") public static native @Adapter("MatAdapter") CvMat ensureInclusionConstraint(CvMat M, @ByVal CvSize size, float trimRatio);
    @Namespace("cv::videostab") public static native float estimateOptimalTrimRatio(CvMat M, @ByVal CvSize size);


    // #include "opencv2/videostab/frame_source.hpp"
    @Namespace("cv::videostab") public static class IFrameSource extends Pointer {
        static { load(); }
        public IFrameSource() { }
        public IFrameSource(Pointer p) { super(p); }

        public /*abstract*/ native void reset();
        public /*abstract*/ native @Adapter("MatAdapter") IplImage nextFrame();
    }

    @Name("cv::Ptr<cv::videostab::IFrameSource>")
    public static class IFrameSourcePtr extends Pointer {
        static { load(); }
        public IFrameSourcePtr()       { allocate();  }
        public IFrameSourcePtr(Pointer p) { super(p); }
        private native void allocate();

        public native IFrameSource get();
        public native IFrameSourcePtr put(IFrameSource value);
    }

    @Namespace("cv::videostab") public static class NullFrameSource extends IFrameSource {
        static { load(); }
        public NullFrameSource() { allocate(); }
        public NullFrameSource(Pointer p) { super(p); }
        private native void allocate();

//        public /*abstract*/ native void reset();
//        public /*abstract*/ native @Adapter("MatAdapter") IplImage nextFrame();
    }

    @Namespace("cv::videostab") public static class VideoFileSource extends IFrameSource {
        static { load(); }
        public VideoFileSource(String path) { allocate(path); }
        public VideoFileSource(String path, @Cast("bool") boolean volatileFrame/*=false*/) {
            allocate(path, volatileFrame);
        }
        public VideoFileSource(Pointer p) { super(p); }
        private native void allocate(String path);
        private native void allocate(String path, @Cast("bool") boolean volatileFrame/*=false*/);

//        public /*abstract*/ native void reset();
//        public /*abstract*/ native @Adapter("MatAdapter") IplImage nextFrame();

        public native int frameCount();
        public native double fps();
    }


    // #include "opencv2/videostab/log.hpp"
    @Namespace("cv::videostab") public static class ILog extends Pointer {
        static { load(); }
        public ILog() { }
        public ILog(Pointer p) { super(p); }

        public /*abstract*/ native void print(String format);
    }

    @Name("cv::Ptr<cv::videostab::ILog>")
    public static class ILogPtr extends Pointer {
        static { load(); }
        public ILogPtr()       { allocate();  }
        public ILogPtr(Pointer p) { super(p); }
        private native void allocate();

        public native ILog get();
        public native ILogPtr put(ILog value);
    }

    @Namespace("cv::videostab") public static class NullLog extends ILog {
        static { load(); }
        public NullLog() { allocate(); }
        public NullLog(Pointer p) { super(p); }
        private native void allocate();

//        public native void print(String format);
    }

    @Namespace("cv::videostab") public static class LogToStdout extends ILog {
        static { load(); }
        public LogToStdout() { allocate(); }
        public LogToStdout(Pointer p) { super(p); }
        private native void allocate();

//        public native void print(String format);
    }


    // #include "opencv2/videostab/fast_marching_inl.hpp"
    // #include "opencv2/videostab/fast_marching.hpp"
//    @Name("cv::videostab::FastMarchingMethod<Inpaint>") public static class InpaintFastMarchingMethod extends Pointer {
//        static { load(); }
//        public InpaintFastMarchingMethod() { allocate(); }
//        public InpaintFastMarchingMethod(Pointer p) { super(p); }
//        private native void allocate();
//
//        public native Inpaint run(IplImage mask, Inpaint inpaint);
//
//        public native @Adapter("MatAdapter") IplImage distanceMap();
//    }


    // #include "opencv2/videostab/inpainting.hpp"
    @Namespace("cv::videostab") public static class InpainterBase extends Pointer {
        static { load(); }
        public InpainterBase() { }
        public InpainterBase(Pointer p) { super(p); }

        public native void setRadius(int val);
        public native int radius();

        public native void setFrames(@Const @ByRef MatVector val);
        public native @Const @ByRef MatVector frames();

        public native void setMotions(@Const @ByRef MatVector val);
        public native @Const @ByRef MatVector motions();

        public native void setStabilizedFrames(@Const @ByRef MatVector val);
        public native @Const @ByRef MatVector stabilizedFrames();

        public native void setStabilizationMotions(@Const @ByRef MatVector val);
        public native @Const @ByRef MatVector stabilizationMotions();

        public native void update();

        public /*abstract*/ native void inpaint(int idx, @Adapter("MatAdapter") IplImage frame, @Adapter("MatAdapter") IplImage mask);

//        protected native int radius_();
//        protected native MatVector frames_();
//        protected native MatVector motions_();
//        protected native MatVector stabilizedFrames_();
//        protected native MatVector stabilizationMotions_();
    }

    @Name("cv::Ptr<cv::videostab::InpainterBase>")
    public static class InpainterBasePtr extends Pointer {
        static { load(); }
        public InpainterBasePtr()       { allocate();  }
        public InpainterBasePtr(Pointer p) { super(p); }
        private native void allocate();

        public native InpainterBase get();
        public native InpainterBasePtr put(InpainterBase value);
    }

    @Namespace("cv::videostab") public static class NullInpainter extends InpainterBase {
        static { load(); }
        public NullInpainter() { allocate(); }
        public NullInpainter(Pointer p) { super(p); }
        private native void allocate();

//        public native void inpaint(int idx, @Adapter("MatAdapter") IplImage frame, @Adapter("MatAdapter") IplImage mask);
    }

    @Namespace("cv::videostab") public static class InpaintingPipeline extends InpainterBase {
        static { load(); }
        public InpaintingPipeline() { allocate(); }
        public InpaintingPipeline(Pointer p) { super(p); }
        private native void allocate();

        public native void pushBack(@ByVal InpainterBasePtr inpainter);
        public native @Cast("bool") boolean empty();

//        public native void setRadius(int val);
//        public native void setFrames(@ByRef MatVector val);
//        public native void setMotions(@ByRef MatVector val);
//        public native void setStabilizedFrames(@ByRef MatVector val);
//        public native void setStabilizationMotions(@ByRef MatVector val);

//        public native  void update();

//        public native  void inpaint(int idx, IplImage frame, IplImage mask);
    }

    @Namespace("cv::videostab") public static class ConsistentMosaicInpainter extends InpainterBase {
        static { load(); }
        public ConsistentMosaicInpainter() { allocate(); }
        public ConsistentMosaicInpainter(Pointer p) { super(p); }
        private native void allocate();

        public native void setStdevThresh(float val);
        public native float stdevThresh();

//        public native  void inpaint(int idx, IplImage frame, IplImage mask);
    }

    @Namespace("cv::videostab") public static class MotionInpainter extends InpainterBase {
        static { load(); }
        public MotionInpainter() { allocate(); }
        public MotionInpainter(Pointer p) { super(p); }
        private native void allocate();

        public native void setOptFlowEstimator(@ByVal IDenseOptFlowEstimatorPtr val);
        public native @ByVal IDenseOptFlowEstimatorPtr optFlowEstimator();

        public native void setFlowErrorThreshold(float val);
        public native float flowErrorThreshold();

        public native void setDistThreshold(float val);
        public native float distThresh();

        public native void setBorderMode(int val);
        public native int borderMode();

//        public native  void inpaint(int idx, IplImage frame, IplImage mask);
    }

    @Namespace("cv::videostab") public static class ColorAverageInpainter extends InpainterBase {
        static { load(); }
        public ColorAverageInpainter() { allocate(); }
        public ColorAverageInpainter(Pointer p) { super(p); }
        private native void allocate();

//        public native  void inpaint(int idx, IplImage frame, IplImage mask);
    }

    @Namespace("cv::videostab") public static class ColorInpainter extends InpainterBase {
        static { load(); }
        public ColorInpainter() { allocate(); }
        public ColorInpainter(int method/*=INPAINT_TELEA*/, double radius/*=2.0*/) { allocate(method, radius); }
        public ColorInpainter(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int method/*=INPAINT_TELEA*/, double radius/*=2.0*/);

//        public native  void inpaint(int idx, IplImage frame, IplImage mask);
    }

    @Namespace("cv::videostab") public static native void calcFlowMask(IplImage flowX, IplImage flowY,
            IplImage errors, float maxError, IplImage mask0, IplImage mask1, @Adapter("MatAdapter") IplImage flowMask);

    @Namespace("cv::videostab") public static native void completeFrameAccordingToFlow(IplImage flowMask,
            IplImage flowX, IplImage flowY, IplImage frame1,  IplImage mask1, float distThresh,
            @Adapter("MatAdapter") IplImage frame0, @Adapter("MatAdapter") IplImage mask0);

    // #include "opencv2/videostab/deblurring.hpp"
    @Namespace("cv::videostab") public static native float calcBlurriness(IplImage frame);

    @Namespace("cv::videostab") public static class DeblurerBase extends Pointer {
        static { load(); }
        public DeblurerBase() { }
        public DeblurerBase(Pointer p) { super(p); }

        public native void setRadius(int val);
        public native int radius();

        public native void setFrames(@Const @ByRef MatVector val);
        public native @Const @ByRef MatVector frames();

        public native void setMotions(@Const @ByRef MatVector val);
        public native @Const @ByRef MatVector motions();

        public native void setBlurrinessRates(@Adapter("VectorAdapter<float>") FloatPointer val);
        public native @Adapter("VectorAdapter<float>") FloatPointer blurrinessRates();

        public native void update();

        public /*abstract*/ native void deblur(int idx, @Adapter("MatAdapter") IplImage frame);

//        protected native int radius_();
//        protected native MatVector frames_();
//        protected native MatVector motions_();
//        protected native FloatVector blurrinessRates_();
    }

    @Name("cv::Ptr<cv::videostab::DeblurerBase>")
    public static class DeblurerBasePtr extends Pointer {
        static { load(); }
        public DeblurerBasePtr()       { allocate();  }
        public DeblurerBasePtr(Pointer p) { super(p); }
        private native void allocate();

        public native DeblurerBase get();
        public native DeblurerBasePtr put(DeblurerBase value);
    }

    @Namespace("cv::videostab") public static class NullDeblurer extends DeblurerBase {
        static { load(); }
        public NullDeblurer() { allocate(); }
        public NullDeblurer(Pointer p) { super(p); }
        private native void allocate();

//        public native void deblur(int idx, @Adapter("MatAdapter") IplImage frame);
    }

    @Namespace("cv::videostab") public static class WeightingDeblurer extends DeblurerBase {
        static { load(); }
        public WeightingDeblurer() { allocate(); }
        public WeightingDeblurer(Pointer p) { super(p); }
        private native void allocate();

        public native void setSensitivity(float val);
        public native float sensitivity();

//        public native void deblur(int idx, @Adapter("MatAdapter") IplImage frame);
    }


    // #include "opencv2/videostab/stabilizer.hpp"
    @Namespace("cv::videostab") public static class StabilizerBase extends Pointer {
        static { load(); }
        public StabilizerBase() { }
        public StabilizerBase(Pointer p) { super(p); }

        public native void setLog(@ByVal ILogPtr log);
        public native @ByVal ILogPtr log();

        public native void setRadius(int val);
        public native int radius();

        public native void setFrameSource(@ByVal IFrameSourcePtr val);
        public native @ByVal IFrameSourcePtr frameSource();

        public native void setMotionEstimator(@ByVal IGlobalMotionEstimatorPtr val);
        public native @ByVal IGlobalMotionEstimatorPtr motionEstimator();

        public native void setDeblurer(@ByVal DeblurerBasePtr val);
        public native @ByVal DeblurerBasePtr deblurrer();

        public native void setTrimRatio(float val);
        public native float trimRatio();

        public native void setCorrectionForInclusion(@Cast("bool") boolean val);
        public native @Cast("bool") boolean doCorrectionForInclusion();

        public native void setBorderMode(int val);
        public native int borderMode();

        public native void setInpainter(@ByVal InpainterBasePtr val);
        public native @ByVal InpainterBasePtr inpainter();

//        protected native void setUp(int cacheSize, IplImage frame);
//        protected native @Adapter("MatAdapter") IplImage nextStabilizedFrame();
//        protected native @Cast("bool") boolean doOneIteration();
//        protected native void stabilizeFrame(CvMat stabilizationMotion);
//
//        protected /*abstract*/ native void setUp(@Adapter("MatAdapter") IplImage firstFrame);
//        protected /*abstract*/ native void stabilizeFrame();
//        protected /*abstract*/ native void estimateMotion();
//
//        protected native @ByRef ILogPtr log_();
//        protected native @ByRef IFrameSourcePtr frameSource_();
//        protected native @ByRef IGlobalMotionEstimatorPtr motionEstimator_();
//        protected native @ByRef DeblurerBasePtr deblurer_();
//        protected native @ByRef InpainterBasePtr inpainter_();
//        protected native int radius_();
//        protected native float trimRatio_();
//        protected native @Cast("bool") boolean doCorrectionForInclusion_();
//        protected native int borderMode_();
//
//        protected native @ByVal CvSize frameSize_();
//        protected native @Adapter("MatAdapter") IplImage frameMask_();
//        protected native int curPos_();
//        protected native int curStabilizedPos_();
//        protected native @Cast("bool") boolean doDeblurring_();
//        protected native @Adapter("MatAdapter") IplImage preProcessedFrame_();
//        protected native @Cast("bool") boolean doInpainting_();
//        protected native @Adapter("MatAdapter") IplImage inpaintingMask_();
//        protected native @ByRef MatVector frames_();
//        protected native @ByRef MatVector motions_();
//        protected native @ByRef FloatVector blurrinessRates_();
//        protected native @ByRef MatVector stabilizedFrames_();
//        protected native @ByRef MatVector stabilizedMasks_();
//        protected native @ByRef MatVector stabilizationMotions_();
    }

    public static native @Name("dynamic_cast<cv::videostab::StabilizerBase*>") StabilizerBase castStabilizerBase(OnePassStabilizer pointer);
    public static native @Name("dynamic_cast<cv::videostab::IFrameSource*>") IFrameSource castIFrameSource(OnePassStabilizer pointer);

    @Namespace("cv::videostab") public static class OnePassStabilizer extends /*StabilizerBase, IFrameSource*/ Pointer {
        static { load(); }
        public OnePassStabilizer() { allocate(); }
        public OnePassStabilizer(Pointer p) { super(p); }
        private native void allocate();

        public native void setMotionFilter(@ByVal MotionFilterBasePtr val);
        public native @ByVal MotionFilterBasePtr motionFilter();

        public native void reset();
        public native @Adapter("MatAdapter") IplImage nextFrame();

        public StabilizerBase getStabilizerBase() { return castStabilizerBase(this); }
        public IFrameSource getIFrameSource() { return castIFrameSource(this); }
    }

    public static native @Name("dynamic_cast<cv::videostab::StabilizerBase*>") StabilizerBase castStabilizerBase(TwoPassStabilizer pointer);
    public static native @Name("dynamic_cast<cv::videostab::IFrameSource*>") IFrameSource castIFrameSource(TwoPassStabilizer pointer);

    @Namespace("cv::videostab") public static class TwoPassStabilizer extends /*StabilizerBase, IFrameSource*/ Pointer {
        static { load(); }
        public TwoPassStabilizer() { allocate(); }
        public TwoPassStabilizer(Pointer p) { super(p); }
        private native void allocate();

        public native void setMotionStabilizer(@ByVal IMotionStabilizerPtr val);
        public native @ByVal IMotionStabilizerPtr motionStabilizer();

        public native void setEstimateTrimRatio(@Cast("bool") boolean val);
        public native @Cast("bool") boolean mustEstimateTrimaRatio();

        public native void reset();
        public native @Adapter("MatAdapter") IplImage nextFrame();

        public native @ByVal MatVector motions();

        public StabilizerBase getStabilizerBase() { return castStabilizerBase(this); }
        public IFrameSource getIFrameSource() { return castIFrameSource(this); }
    }
}
