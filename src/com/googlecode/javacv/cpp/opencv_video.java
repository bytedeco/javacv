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
 * This file is based on information found in tracking.hpp and background_segm.hpp
 * of OpenCV 2.2, which are covered by the following copyright notice:
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
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
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
    @Platform(include={"<opencv2/video/tracking.hpp>", "<opencv2/video/background_segm.hpp>"},
        linkpath=genericLinkpath,       link={"opencv_video", "opencv_imgproc", "opencv_core"}, includepath=genericIncludepath),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, link={"opencv_video220", "opencv_imgproc220", "opencv_core220"}),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_video {
    static { load(opencv_imgproc.class); load(); }

    public static native void cvCalcOpticalFlowLK(CvArr prev, CvArr curr, @ByVal CvSize win_size,
            CvArr velx, CvArr vely);
    public static native void cvCalcOpticalFlowBM(CvArr prev, CvArr curr, @ByVal CvSize block_size,
            @ByVal CvSize shift_size, @ByVal CvSize max_range, int use_previous, CvArr velx, CvArr vely);
    public static native void cvCalcOpticalFlowHS(CvArr prev, CvArr curr, int use_previous,
            CvArr velx, CvArr vely, double lambda, @ByVal CvTermCriteria criteria);
    public static final int
            CV_LKFLOW_PYR_A_READY       = 1,
            CV_LKFLOW_PYR_B_READY       = 2,
            CV_LKFLOW_INITIAL_GUESSES   = 4,
            CV_LKFLOW_GET_MIN_EIGENVALS = 8,

            OPTFLOW_USE_INITIAL_FLOW    = 4,
            OPTFLOW_FARNEBACK_GAUSSIAN  = 256;
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
        public CvKalman() { allocate(); }
        public CvKalman(int size) { allocateArray(size); }
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
            CV_BG_MODEL_FGD        = 0,
            CV_BG_MODEL_MOG        = 1,
            CV_BG_MODEL_FGD_SIMPLE = 2;

    public static class CvReleaseBGStatModel extends FunctionPointer {
        static { load(); }
        public native void call(@ByPtrPtr CvBGStatModel bg_model);
    }
    public static class CvUpdateBGStatModel extends FunctionPointer {
        static { load(); }
        public native int call(IplImage curr_frame, CvBGStatModel bg_model, double learningRate);
    }

    public static class CvBGStatModel extends Pointer {
        static { load(); }
        public CvBGStatModel() { allocate(); }
        public CvBGStatModel(int size) { allocateArray(size); }
        public CvBGStatModel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBGStatModel position(int position) {
            return (CvBGStatModel)super.position(position);
        }

        public static CvBGStatModel create(IplImage first_frame, CvFGDStatModelParams parameters) {
            CvBGStatModel m = cvCreateFGDStatModel(first_frame, parameters);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }
        public static CvBGStatModel create(IplImage first_frame, CvGaussBGStatModelParams parameters) {
            CvBGStatModel m = cvCreateGaussianBGModel(first_frame, parameters);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }

        public void release2() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvBGStatModel implements Deallocator {
            ReleaseDeallocator(CvBGStatModel p) { super(p); }
            @Override public void deallocate() { cvReleaseBGStatModel(this); }
        }


        public native int type();                     public native CvBGStatModel type(int type) ;
        public native CvReleaseBGStatModel release(); public native CvBGStatModel release(CvReleaseBGStatModel release);
        public native CvUpdateBGStatModel update();   public native CvBGStatModel update(CvUpdateBGStatModel update);
        public native IplImage background();          public native CvBGStatModel background(IplImage background);
        public native IplImage foreground();          public native CvBGStatModel foreground(IplImage foreground);
        public native IplImageArray layers();         public native CvBGStatModel layers(IplImageArray layers);
        public native int layer_count();              public native CvBGStatModel layer_count(int layer_count);
        public native CvMemStorage storage();         public native CvBGStatModel storage(CvMemStorage storage);
        public native CvSeq foreground_regions();     public native CvBGStatModel foreground_regions(CvSeq foreground_regions);
    }

    public static native void cvReleaseBGStatModel(@ByPtrPtr CvBGStatModel bg_model);
    public static native int cvUpdateBGStatModel(IplImage current_frame, CvBGStatModel bg_model, double learningRate/*=-1*/);
    public static native void cvRefineForegroundMaskBySegm(CvSeq segments, CvBGStatModel bg_model);
    public static native int cvChangeDetection(IplImage prev_frame, IplImage curr_frame, IplImage change_mask);

    public static final int
            CV_BGFG_FGD_LC             = 128,
            CV_BGFG_FGD_N1C            = 15,
            CV_BGFG_FGD_N2C            = 25,

            CV_BGFG_FGD_LCC            = 64,
            CV_BGFG_FGD_N1CC           = 25,
            CV_BGFG_FGD_N2CC           = 40;

    public static final float
            CV_BGFG_FGD_ALPHA_1        = 0.1f,
            CV_BGFG_FGD_ALPHA_2        = 0.005f,
            CV_BGFG_FGD_ALPHA_3        = 0.1f,
            CV_BGFG_FGD_DELTA          = 2,
            CV_BGFG_FGD_T              = 0.9f,
            CV_BGFG_FGD_MINAREA        = 15.f,
            CV_BGFG_FGD_BG_UPDATE_TRESH= 0.5f;

    public static class CvFGDStatModelParams extends Pointer {
        static { load(); }
        public CvFGDStatModelParams() { allocate(); }
        public CvFGDStatModelParams(int size) { allocateArray(size); }
        public CvFGDStatModelParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFGDStatModelParams position(int position) {
            return (CvFGDStatModelParams)super.position(position);
        }

        public native int    Lc();      public native CvFGDStatModelParams Lc(int Lc);
        public native int    N1c();     public native CvFGDStatModelParams N1c(int N1c);
        public native int    N2c();     public native CvFGDStatModelParams N2c(int N2c);

        public native int    Lcc();     public native CvFGDStatModelParams Lcc(int Lcc);
        public native int    N1cc();    public native CvFGDStatModelParams N1cc(int N1cc);
        public native int    N2cc();    public native CvFGDStatModelParams N2cc(int N2cc);

        public native int    is_obj_without_holes(); public native CvFGDStatModelParams is_obj_without_holes(int is_obj_without_holes);
        public native int    perform_morphing();     public native CvFGDStatModelParams perform_morphing(int perform_morphing);

        public native float  alpha1();  public native CvFGDStatModelParams alpha1(float alpha1);
        public native float  alpha2();  public native CvFGDStatModelParams alpha2(float alpha2);
        public native float  alpha3();  public native CvFGDStatModelParams alpha3(float alpha3);

        public native float  delta();   public native CvFGDStatModelParams delta(float delta);
        public native float  T();       public native CvFGDStatModelParams T(float T);
        public native float  minArea(); public native CvFGDStatModelParams minArea(float minArea);
    }

    public static class CvBGPixelCStatTable extends Pointer {
        static { load(); }
        public CvBGPixelCStatTable() { allocate(); }
        public CvBGPixelCStatTable(int size) { allocateArray(size); }
        public CvBGPixelCStatTable(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBGPixelCStatTable position(int position) {
            return (CvBGPixelCStatTable)super.position(position);
        }

        public native float Pv();           public native CvBGPixelCStatTable Pv(float Pv);
        public native float Pvb();          public native CvBGPixelCStatTable Pvb(float Pvb);
        public native byte/*[3]*/ v(int i); public native CvBGPixelCStatTable v(int i, byte v);
    }

    public static class CvBGPixelCCStatTable extends Pointer {
        static { load(); }
        public CvBGPixelCCStatTable() { allocate(); }
        public CvBGPixelCCStatTable(int size) { allocateArray(size); }
        public CvBGPixelCCStatTable(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBGPixelCCStatTable position(int position) {
            return (CvBGPixelCCStatTable)super.position(position);
        }

        public native float Pv();           public native CvBGPixelCCStatTable Pv(float Pv);
        public native float Pvb();          public native CvBGPixelCCStatTable Pvb(float Pvb);
        public native byte/*[6]*/ v(int i); public native CvBGPixelCCStatTable v(int i, byte v);
    }

    public static class CvBGPixelStat extends Pointer {
        static { load(); }
        public CvBGPixelStat() { allocate(); }
        public CvBGPixelStat(int size) { allocateArray(size); }
        public CvBGPixelStat(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBGPixelStat position(int position) {
            return (CvBGPixelStat)super.position(position);
        }

        public native float Pbc();                    public native CvBGPixelStat Pbc(float Pbc);
        public native float Pbcc();                   public native CvBGPixelStat Pbcc(float Pbcc);
        public native CvBGPixelCStatTable ctable();   public native CvBGPixelStat ctable(CvBGPixelCStatTable ctable);
        public native CvBGPixelCCStatTable cctable(); public native CvBGPixelStat cctable(CvBGPixelCCStatTable cctable);
        public native byte is_trained_st_model();     public native CvBGPixelStat is_trained_st_model(byte is_trained_st_model);
        public native byte is_trained_dyn_model();    public native CvBGPixelStat is_trained_dyn_model(byte is_trained_dyn_model);
    }

    public static class CvFGDStatModel extends CvBGStatModel {
        static { load(); }
        public CvFGDStatModel() { allocate(); }
        public CvFGDStatModel(int size) { allocateArray(size); }
        public CvFGDStatModel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFGDStatModel position(int position) {
            return (CvFGDStatModel)super.position(position);
        }

        public native CvBGPixelStat pixel_stat();    public native CvFGDStatModel pixel_stat(CvBGPixelStat pixel_stat);
        public native IplImage Ftd();                public native CvFGDStatModel Ftd(IplImage Ftd);
        public native IplImage Fbd();                public native CvFGDStatModel Fbd(IplImage Fbd);
        public native IplImage prev_frame();         public native CvFGDStatModel prev_frame(IplImage prev_frame);
        @ByRef
        public native CvFGDStatModelParams params(); public native CvFGDStatModel params(CvFGDStatModelParams params);
    }

    public static native CvBGStatModel cvCreateFGDStatModel(IplImage first_frame,
            CvFGDStatModelParams parameters/*=null*/);


    public static final int
            CV_BGFG_MOG_MAX_NGAUSSIANS = 500,

            CV_BGFG_MOG_WINDOW_SIZE             = 200,
            CV_BGFG_MOG_NGAUSSIANS              = 5,

            CV_BGFG_MOG_NCOLORS                 = 3;

    public static final double
            CV_BGFG_MOG_BACKGROUND_THRESHOLD    = 0.7,
            CV_BGFG_MOG_STD_THRESHOLD           = 2.5,
            CV_BGFG_MOG_WEIGHT_INIT             = 0.05,
            CV_BGFG_MOG_SIGMA_INIT              = 30,
            CV_BGFG_MOG_MINAREA                 = 15.f;

    public static class CvGaussBGStatModelParams extends CvBGStatModel {
        static { load(); }
        public CvGaussBGStatModelParams() { allocate(); }
        public CvGaussBGStatModelParams(int size) { allocateArray(size); }
        public CvGaussBGStatModelParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGaussBGStatModelParams position(int position) {
            return (CvGaussBGStatModelParams)super.position(position);
        }

        public native int    win_size();      public native CvGaussBGStatModelParams win_size(int win_size);
        public native int    n_gauss();       public native CvGaussBGStatModelParams n_gauss(int n_gauss);
        public native double bg_threshold();  public native CvGaussBGStatModelParams bg_threshold(double bg_threshold);
        public native double std_threshold(); public native CvGaussBGStatModelParams std_threshold(double std_threshold);
        public native double minArea();       public native CvGaussBGStatModelParams minArea(double minArea);
        public native double weight_init();   public native CvGaussBGStatModelParams weight_init(double weight_init);
        public native double variance_init(); public native CvGaussBGStatModelParams variance_init(double variance_init);
    }

    public static class CvGaussBGValues extends Pointer {
        static { load(); }
        public CvGaussBGValues() { allocate(); }
        public CvGaussBGValues(int size) { allocateArray(size); }
        public CvGaussBGValues(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGaussBGValues position(int position) {
            return (CvGaussBGValues)super.position(position);
        }

        public native int    match_sum();     public native CvGaussBGValues match_sum(int match_sum);
        public native double weight();        public native CvGaussBGValues weight(double weight);
        /*double[CV_BGFG_MOG_NCOLORS]*/
        public native double variance(int i); public native CvGaussBGValues variance(int i, double variance);
        public native double mean(int i);     public native CvGaussBGValues mean(int i, double mean);
    }

    public static class CvGaussBGPoint extends Pointer {
        static { load(); }
        public CvGaussBGPoint() { allocate(); }
        public CvGaussBGPoint(int size) { allocateArray(size); }
        public CvGaussBGPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGaussBGPoint position(int position) {
            return (CvGaussBGPoint)super.position(position);
        }

        public native CvGaussBGValues g_values(); public native CvGaussBGPoint g_values(CvGaussBGValues g_values);
    }

    public static class CvGaussBGModel extends CvBGStatModel {
        static { load(); }
        public CvGaussBGModel() { allocate(); }
        public CvGaussBGModel(int size) { allocateArray(size); }
        public CvGaussBGModel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGaussBGModel position(int position) {
            return (CvGaussBGModel)super.position(position);
        }

        @ByRef
        public native CvGaussBGStatModelParams params(); public native CvGaussBGModel params(CvGaussBGStatModelParams params);
        public native CvGaussBGPoint g_point();          public native CvGaussBGModel g_point(CvGaussBGPoint g_point);
        public native int countFrames();                 public native CvGaussBGModel countFrames(int countFrames);
    }

    public static native CvBGStatModel cvCreateGaussianBGModel(IplImage first_frame,
            CvGaussBGStatModelParams parameters/*=null*/);


    public static class CvBGCodeBookElem extends Pointer {
        static { load(); }
        public CvBGCodeBookElem() { allocate(); }
        public CvBGCodeBookElem(int size) { allocateArray(size); }
        public CvBGCodeBookElem(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBGCodeBookElem position(int position) {
            return (CvBGCodeBookElem)super.position(position);
        }

        public native CvBGCodeBookElem next();     public native CvBGCodeBookElem next(CvBGCodeBookElem next);
        public native int tLastUpdate();           public native CvBGCodeBookElem tLastUpdate(int tLastUpdate);
        public native int stale();                 public native CvBGCodeBookElem stale(int stale);
        public native byte/*[3]*/ boxMin(int i);   public native CvBGCodeBookElem boxMin(int i, byte boxMin);
        public native byte/*[3]*/ boxMax(int i);   public native CvBGCodeBookElem boxMax(int i, byte boxMax);
        public native byte/*[3]*/ learnMin(int i); public native CvBGCodeBookElem learnMin(int i, byte learnMin);
        public native byte/*[3]*/ learnMax(int i); public native CvBGCodeBookElem learnMax(int i, byte learnMax);
    }

    public static class CvBGCodeBookModel extends Pointer {
        static { load(); }
        public CvBGCodeBookModel() { allocate(); }
        public CvBGCodeBookModel(int size) { allocateArray(size); }
        public CvBGCodeBookModel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBGCodeBookModel position(int position) {
            return (CvBGCodeBookModel)super.position(position);
        }

        public static CvBGCodeBookModel create() {
            CvBGCodeBookModel m = cvCreateBGCodeBookModel();
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvBGCodeBookModel implements Deallocator {
            ReleaseDeallocator(CvBGCodeBookModel p) { super(p); }
            @Override public void deallocate() { cvReleaseBGCodeBookModel(this); }
        }


        @ByRef
        public native CvSize size();               public native CvBGCodeBookModel size(CvSize size);
        public native int t();                     public native CvBGCodeBookModel t(int t);
        public native byte/*[3]*/ cbBounds(int i); public native CvBGCodeBookModel cbBounds(int i, byte cbBounds);
        public native byte/*[3]*/ modMin(int i);   public native CvBGCodeBookModel modMin(int i, byte modMin);
        public native byte/*[3]*/ modMax(int i);   public native CvBGCodeBookModel modMax(int i, byte modMax);
        @Cast("CvBGCodeBookElem**")
        public native PointerPointer cbmap();      public native CvBGCodeBookModel cbmap(PointerPointer cbmap);
        public native CvMemStorage storage();      public native CvBGCodeBookModel storage(CvMemStorage storage);
        public native CvBGCodeBookElem freeList(); public native CvBGCodeBookModel freeList(CvBGCodeBookElem freeList);
    }

    public static native CvBGCodeBookModel cvCreateBGCodeBookModel();
    public static native void cvReleaseBGCodeBookModel(@ByPtrPtr CvBGCodeBookModel model);
    public static native void cvBGCodeBookUpdate(CvBGCodeBookModel model, CvArr image,
            @ByVal CvRect roi/*=cvRect(0,0,0,0)*/, CvArr mask/*=null*/);
    public static native int cvBGCodeBookDiff(CvBGCodeBookModel model, CvArr image,
            CvArr fgmask, @ByVal CvRect roi/*=cvRect(0,0,0,0)*/);
    public static native void cvBGCodeBookClearStale(CvBGCodeBookModel model, int staleThresh,
            @ByVal CvRect roi/*=cvRect(0,0,0,0)*/, CvArr mask/*=null*/);
    public static native CvSeq cvSegmentFGMask(CvArr fgmask, int poly1Hull0/*=1*/, float perimScale/*=4.f*/,
            CvMemStorage storage/*=null*/, @ByVal CvPoint offset/*=cvPoint(0,0)*/);
}
