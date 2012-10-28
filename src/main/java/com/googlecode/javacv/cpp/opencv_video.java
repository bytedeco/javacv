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
 * This file is based on information found in tracking.hpp and background_segm.hpp
 * of OpenCV 2.4.3rc, which are covered by the following copyright notice:
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

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/video/video.hpp>", "opencv_adapters.h"},
        link={"opencv_video@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_video243", "opencv_imgproc243", "opencv_core243"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_video {
    static { load(opencv_imgproc.class);
        if (load() != null) {
            initModule_video();
        }
    }

    @Namespace("cv") public static native @Cast("bool") boolean initModule_video();

    public static final int
            CV_LKFLOW_PYR_A_READY       = 1,
            CV_LKFLOW_PYR_B_READY       = 2,
            CV_LKFLOW_INITIAL_GUESSES   = 4,
            CV_LKFLOW_GET_MIN_EIGENVALS = 8;

    public static native void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            int count, @ByVal CvSize win_size, int level, @Cast("char*") byte[] status,
            float[] track_error, @ByVal CvTermCriteria criteria, int flags);
    public static native void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            int count, @ByVal CvSize win_size, int level, @Cast("char*") byte[] status,
            FloatPointer track_error, @ByVal CvTermCriteria criteria, int flags);
    public static native void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            float[] matrices, int count, @ByVal CvSize win_size, int level,
            @Cast("char*") byte[] status, float[] track_error, @ByVal CvTermCriteria criteria, int flags);
    public static native void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            FloatPointer matrices, int count, @ByVal CvSize win_size, int level,
            @Cast("char*") byte[] status, FloatPointer track_error, @ByVal CvTermCriteria criteria, int flags);
    public static native int cvEstimateRigidTransform(CvArr A, CvArr B, CvMat M, int full_affine);
    public static native void cvCalcOpticalFlowFarneback(CvArr prev, CvArr next,
            CvArr flow, double pyr_scale, int levels, int winsize,
            int iterations, int poly_n, double poly_sigma, int flags);

    public static native void cvUpdateMotionHistory(CvArr silhouette, CvArr mhi,
            double timestamp, double duration);
    public static native void cvCalcMotionGradient(CvArr mhi, CvArr mask, CvArr orientation,
            double delta1, double delta2, int aperture_size/*=3*/);
    public static native double cvCalcGlobalOrientation(CvArr orientation,
            CvArr mask, CvArr mhi, double timestamp, double duration);
    public static native CvSeq cvSegmentMotion(CvArr mhi, CvArr seg_mask,
            CvMemStorage storage, double timestamp, double seg_thresh);

    public static native int cvCamShift(CvArr prob_image, @ByVal CvRect window,
            @ByVal CvTermCriteria criteria, CvConnectedComp comp, CvBox2D box/*=null*/);
    public static native int cvMeanShift(CvArr prob_image, @ByVal CvRect window,
            @ByVal CvTermCriteria criteria, CvConnectedComp comp);


    public static class CvKalman extends Pointer {
        static { load(); }
        public CvKalman() { allocate(); zero(); }
        public CvKalman(int size) { allocateArray(size); zero(); }
        public CvKalman(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvKalman position(int position) {
            return (CvKalman)super.position(position);
        }

        public static CvKalman create(int dynam_params, int measure_params,
                int control_params/*=0*/) {
            CvKalman k = cvCreateKalman(dynam_params, measure_params, control_params);
            if (k != null) {
                k.deallocator(new ReleaseDeallocator(k));
            }
            return k;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvKalman implements Deallocator {
            ReleaseDeallocator(CvKalman p) { super(p); }
            @Override public void deallocate() { cvReleaseKalman(this); }
        }


        public native int MP(); public native CvKalman MP(int MP);
        public native int DP(); public native CvKalman DP(int DP);
        public native int CP(); public native CvKalman CP(int CP);

        public native FloatPointer PosterState();           public native CvKalman PosterState(FloatPointer PosterState);
        public native FloatPointer PriorState();            public native CvKalman PriorState(FloatPointer PriorState);
        public native FloatPointer DynamMatr();             public native CvKalman DynamMatr(FloatPointer DynamMatr);
        public native FloatPointer MeasurementMatr();       public native CvKalman MeasurementMatr(FloatPointer MeasurementMatr);
        public native FloatPointer MNCovariance();          public native CvKalman MNCovariance(FloatPointer MNCovariance);
        public native FloatPointer PNCovariance();          public native CvKalman PNCovariance(FloatPointer PNCovariance);
        public native FloatPointer KalmGainMatr();          public native CvKalman KalmGainMatr(FloatPointer KalmGainMatr);
        public native FloatPointer PriorErrorCovariance();  public native CvKalman PriorErrorCovariance(FloatPointer PriorErrorCovariance);
        public native FloatPointer PosterErrorCovariance(); public native CvKalman PosterErrorCovariance(FloatPointer PosterErrorCovariance);
        public native FloatPointer Temp1();                 public native CvKalman Temp1(FloatPointer Temp1);
        public native FloatPointer Temp2();                 public native CvKalman Temp2(FloatPointer Temp2);

        public native CvMat state_pre();             public native CvKalman state_pre(CvMat state_pre);
        public native CvMat state_post();            public native CvKalman state_post(CvMat state_post);
        public native CvMat transition_matrix();     public native CvKalman transition_matrix(CvMat transition_matrix);
        public native CvMat control_matrix();        public native CvKalman control_matrix(CvMat control_matrix);
        public native CvMat measurement_matrix();    public native CvKalman measurement_matrix(CvMat measurement_matrix);
        public native CvMat process_noise_cov();     public native CvKalman process_noise_cov(CvMat process_noise_cov);
        public native CvMat measurement_noise_cov(); public native CvKalman measurement_noise_cov(CvMat measurement_noise_cov);
        public native CvMat error_cov_pre();         public native CvKalman error_cov_pre(CvMat error_cov_pre);
        public native CvMat gain();                  public native CvKalman gain(CvMat gain);
        public native CvMat error_cov_post();        public native CvKalman error_cov_post(CvMat error_cov_post);

        public native CvMat temp1(); public native CvKalman temp1(CvMat temp1);
        public native CvMat temp2(); public native CvKalman temp2(CvMat temp2);
        public native CvMat temp3(); public native CvKalman temp3(CvMat temp3);
        public native CvMat temp4(); public native CvKalman temp4(CvMat temp4);
        public native CvMat temp5(); public native CvKalman temp5(CvMat temp5);
    }
    public static native CvKalman cvCreateKalman(int dynam_params, int measure_params, int control_params/*=0*/);
    public static native void cvReleaseKalman(@ByPtrPtr CvKalman kalman);
    public static native @Const CvMat cvKalmanPredict(CvKalman kalman, CvMat control/*=null*/);
    public static native @Const CvMat cvKalmanCorrect(CvKalman kalman, CvMat measurement);
    public static CvMat cvKalmanUpdateByTime(CvKalman kalman, CvMat control/*=null*/) {
        return cvKalmanPredict(kalman, control);
    }
    public static CvMat cvKalmanUpdateByMeasurement(CvKalman kalman, CvMat measurement) {
        return cvKalmanCorrect(kalman, measurement);
    }


    public static final int
            OPTFLOW_USE_INITIAL_FLOW = CV_LKFLOW_INITIAL_GUESSES,
            OPTFLOW_LK_GET_MIN_EIGENVALS = CV_LKFLOW_GET_MIN_EIGENVALS,
            OPTFLOW_FARNEBACK_GAUSSIAN  = 256;

    @Namespace("cv") public static native int buildOpticalFlowPyramid(@InputArray CvArr img,
            @OutputArray IplImageArray pyramid, @ByVal CvSize winSize, int maxLevel,
            @Cast("bool") boolean withDerivatives/*=true*/, int pyrBorder/*=BORDER_REFLECT_101*/,
            int derivBorder/*=BORDER_CONSTANT*/, @Cast("bool") boolean tryReuseInputImage/*=true*/);
    @Namespace("cv") public static native void calcOpticalFlowPyrLK(@InputArray IplImageArray prevImg,
            @InputArray IplImageArray nextImg, @InputArray CvArr prevPts,
            @InputArray CvArr nextPts, @InputArray CvArr status,
            @InputArray CvArr err, @ByVal CvSize winSize/*=Size(21,21)*/, int maxLevel/*=3*/,
            @ByVal CvTermCriteria criteria/*=TermCriteria(TermCriteria::COUNT+TermCriteria::EPS, 30, 0.01)*/,
            int flags/*=0*/, double minEigThreshold/*=1e-4*/);
    @Namespace("cv") public static native void calcOpticalFlowFarneback(@InputArray IplImageArray prev,
            @InputArray IplImageArray next, @InputArray CvArr flow, double pyr_scale,
            int levels, int winsize, int iterations, int poly_n, double poly_sigma, int flags);
    @Namespace("cv") public static native @OutputMat CvMat estimateRigidTransform(@InputArray CvArr src,
            @InputArray CvArr dst, boolean fullAffine);
    @Namespace("cv") public static native void calcOpticalFlowSF(@InputMat CvArr from, @InputMat CvArr to,
            @InputMat CvArr flow, int layers, int averaging_block_size, int max_flow);
    @Namespace("cv") public static native void calcOpticalFlowSF(@InputMat CvArr from, @InputMat CvArr to,
            @InputMat CvArr flow, int layers, int averaging_block_size, int max_flow, double sigma_dist, double sigma_color,
            int postprocess_window, double sigma_dist_fix, double sigma_color_fix, double occ_thr, int upscale_averaging_radius,
            double upscale_sigma_dist, double upscale_sigma_color, double speed_up_thr);


    @Namespace("cv") public static class BackgroundSubtractor extends Algorithm {
        static { load(); }
        public BackgroundSubtractor() { }
        public BackgroundSubtractor(Pointer p) { super(p); }

        public native @Name("operator()") void apply(@InputArray CvArr image,
                @InputArray CvArr fgmask, double learningRate/*=0*/);

        public native void getBackgroundImage(@InputArray CvArr backgroundImage);
    }

    @NoOffset @Namespace("cv") public static class BackgroundSubtractorMOG extends BackgroundSubtractor {
        static { load(); }
        public BackgroundSubtractorMOG() { allocate(); }
        public BackgroundSubtractorMOG(int history, int nmixtures, double backgroundRatio, double noiseSigma/*=0*/) {
            allocate(history, nmixtures, backgroundRatio, noiseSigma);
        }
        public BackgroundSubtractorMOG(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int history, int nmixtures, double backgroundRatio, double noiseSigma/*=0*/);

//        public native @Name("operator()") void apply(@InputArray CvArr image,
//                @InputArray CvArr fgmask, double learningRate/*=0*/);

        public native void initialize(@ByVal CvSize frameSize, int frameType);

//        public native AlgorithmInfo info();

//        protected native @ByVal CvSize frameSize(); protected native BackgroundSubtractorMOG frameSize(CvSize frameSize);
//        protected native int frameType();           protected native BackgroundSubtractorMOG frameType(int frameType);
//        @InputMat
//        protected native IplImage bgmodel();        protected native BackgroundSubtractorMOG bgmodel(IplImage bgmodel);
//        protected native int nframes();             protected native BackgroundSubtractorMOG nframes(int nframes);
//        protected native int history();             protected native BackgroundSubtractorMOG history(int history);
//        protected native int nmixtures();           protected native BackgroundSubtractorMOG nmixtures(int nmixtures);
//        protected native double varThreshold();     protected native BackgroundSubtractorMOG varThreshold(double varThreshold);
//        protected native double backgroundRatio();  protected native BackgroundSubtractorMOG backgroundRatio(double backgroundRatio);
//        protected native double noiseSigma();       protected native BackgroundSubtractorMOG noiseSigma(double noiseSigma);
    }

    @NoOffset @Namespace("cv") public static class BackgroundSubtractorMOG2 extends BackgroundSubtractor {
        static { load(); }
        public BackgroundSubtractorMOG2() { allocate(); }
        public BackgroundSubtractorMOG2(int history,  float varThreshold, boolean bShadowDetection/*=true*/) {
            allocate(history, varThreshold, bShadowDetection);
        }
        public BackgroundSubtractorMOG2(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int history, float varThreshold, boolean bShadowDetection/*=true*/);

//        public native @Name("operator()") void apply(@InputArray CvArr image,
//                @InputArray CvArr fgmask, double learningRate/*=-1*/);
//
//        public native void getBackgroundImage(@InputArray CvArr backgroundImage);

        public native void initialize(@ByVal CvSize frameSize, int frameType);

//        public native AlgorithmInfo info();

//        protected native @ByVal CvSize frameSize();   protected native BackgroundSubtractorMOG2 frameSize(CvSize frameSize);
//        protected native int frameType();             protected native BackgroundSubtractorMOG2 frameType(int frameType);
//        @InputMat
//        protected native IplImage bgmodel();          protected native BackgroundSubtractorMOG2 bgmodel(IplImage bgmodel);
//        @InputMat
//        protected native IplImage bgmodelUsedModes(); protected native BackgroundSubtractorMOG2 bgmodelUsedModes(IplImage bgmodelUsedModes);
//        protected native int nframes();               protected native BackgroundSubtractorMOG2 nframes(int nframes);
//        protected native int history();               protected native BackgroundSubtractorMOG2 history(int history);
//        protected native int nmixtures();             protected native BackgroundSubtractorMOG2 nmixtures(int nmixtures);
//        protected native float varThreshold();        protected native BackgroundSubtractorMOG2 varThreshold(float varThreshold);
//        protected native float backgroundRatio();     protected native BackgroundSubtractorMOG2 backgroundRatio(float backgroundRatio);
//        protected native float varThresholdGen();     protected native BackgroundSubtractorMOG2 varThresholdGen(float varThresholdGen);
//        protected native float fVarInit();            protected native BackgroundSubtractorMOG2 fVarInit(float fVarInit);
//        protected native float fVarMin();             protected native BackgroundSubtractorMOG2 fVarMin(float fVarMin);
//        protected native float fVarMax();             protected native BackgroundSubtractorMOG2 fVarMax(float fVarMax);
//        protected native float fCT();                 protected native BackgroundSubtractorMOG2 fCT(float fCT);
//        protected native boolean bShadowDetection();  protected native BackgroundSubtractorMOG2 bShadowDetection(boolean bShadowDetection);
//        protected native byte nShadowDetection();     protected native BackgroundSubtractorMOG2 nShadowDetection(byte nShadowDetection);
//        protected native float fTau();                protected native BackgroundSubtractorMOG2 fTau(float fTau);
    }

    @NoOffset @Namespace("cv") public static class BackgroundSubtractorGMG extends BackgroundSubtractor {
        static { load(); }
        public BackgroundSubtractorGMG() { allocate(); }
        public BackgroundSubtractorGMG(Pointer p) { super(p); }
        private native void allocate();

//        public native AlgorithmInfo info();

        public native void initialize(@ByVal CvSize frameSize, double min, double max);

//        public native @Name("operator()") void apply(@InputArray CvArr image,
//                @InputArray CvArr fgmask, double learningRate/*=-1.0*/);

        public native void release();

        public native int     maxFeatures();             public native BackgroundSubtractorGMG maxFeatures(int maxFeatures);
        public native double  learningRate();            public native BackgroundSubtractorGMG learningRate(double learningRate);
        public native int     numInitializationFrames(); public native BackgroundSubtractorGMG numInitializationFrames(int numInitializationFrames);
        public native int     quantizationLevels();      public native BackgroundSubtractorGMG quantizationLevels(int quantizationLevels);
        public native double  backgroundPrior();         public native BackgroundSubtractorGMG backgroundPrior(double backgroundPrior);
        public native double  decisionThreshold();       public native BackgroundSubtractorGMG decisionThreshold(double decisionThreshold);
        public native int     smoothingRadius();         public native BackgroundSubtractorGMG smoothingRadius(int smoothingRadius);
        public native boolean updateBackgroundModel();   public native BackgroundSubtractorGMG updateBackgroundModel(boolean updateBackgroundModel);
    }
}
