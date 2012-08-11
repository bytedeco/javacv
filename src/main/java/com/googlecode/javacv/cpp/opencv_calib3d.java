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
 * This file is based on information found in calib3d.hpp
 * of OpenCV 2.4.2, which are covered by the following copyright notice:
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
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByPtrRef;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
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
        include={"<opencv2/calib3d/calib3d.hpp>", "opencv_adapters.h"},
        link={"opencv_calib3d@.2.4", "opencv_features2d@.2.4", "opencv_flann@.2.4", "opencv_highgui@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_calib3d242", "opencv_features2d242", "opencv_flann242", "opencv_highgui242", "opencv_imgproc242", "opencv_core242"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_calib3d {
    static { load(opencv_highgui.class); load(opencv_flann.class); load(opencv_features2d.class); load(); }

    @Opaque public static class CvPOSITObject extends Pointer {
        static { load(); }
        public CvPOSITObject() { }
        public CvPOSITObject(Pointer p) { super(p); }

        public static CvPOSITObject create(CvPoint3D32f points, int point_count) {
            CvPOSITObject p = cvCreatePOSITObject(points, point_count);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvPOSITObject implements Deallocator {
            ReleaseDeallocator(CvPOSITObject p) { super(p); }
            @Override public void deallocate() { cvReleasePOSITObject(this); }
        }
    }
    public static native CvPOSITObject cvCreatePOSITObject(CvPoint3D32f points, int point_count);
    public static native void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f image_points,
            double focal_length, @ByVal CvTermCriteria criteria,
            float[] /*CvMatr32f*/ rotation_matrix, float[] /*CvVect32f*/ translation_vector);
    public static native void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f image_points,
            double focal_length, @ByVal CvTermCriteria criteria,
            FloatPointer /*CvMatr32f*/ rotation_matrix, FloatPointer /*CvVect32f*/ translation_vector);
    public static native void cvReleasePOSITObject(@ByPtrPtr CvPOSITObject posit_object);

    public static native int cvRANSACUpdateNumIters(double p, double err_prob, int model_points, int max_iters);

    public static native void cvConvertPointsHomogeneous(CvMat src, CvMat dst);

    public static final int
            CV_FM_7POINT = 1,
            CV_FM_8POINT = 2,

            CV_LMEDS = 4,
            CV_RANSAC = 8,

            CV_FM_LMEDS_ONLY = CV_LMEDS,
            CV_FM_RANSAC_ONLY = CV_RANSAC,
            CV_FM_LMEDS = CV_LMEDS,
            CV_FM_RANSAC = CV_RANSAC,

            CV_ITERATIVE = 0,
            CV_EPNP = 1,
            CV_P3P = 2;

    public static native int cvFindFundamentalMat(CvMat points1, CvMat points2, CvMat fundamental_matrix,
            int method/*=CV_FM_RANSAC*/, double param1/*=3*/, double param2/*=0.99*/, CvMat status/*=null*/);
    public static native void cvComputeCorrespondEpilines(CvMat points, int which_image,
            CvMat fundamental_matrix, CvMat correspondent_lines);

    public static native void cvTriangulatePoints(CvMat projMatr1, CvMat projMatr2,
            CvMat projPoints1, CvMat projPoints2, CvMat points4D);
    public static native void cvCorrectMatches(CvMat F, CvMat points1, CvMat points2,
            CvMat new_points1, CvMat new_points2);

    public static native void cvGetOptimalNewCameraMatrix(CvMat camera_matrix,
            CvMat dist_coeffs, @ByVal CvSize image_size, double alpha, CvMat new_camera_matrix,
            @ByVal CvSize new_imag_size/*=cvSize(0,0)*/, CvRect valid_pixel_ROI/*=null*/, 
            int center_principal_point/*=0*/);

    public static native int cvRodrigues2(CvMat src, CvMat dst, CvMat jacobian/*=null*/);

    public static int cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography) {
        return cvFindHomography(src_points, dst_points, homography, 0, 3, null);
    }
    public static native int cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography,
            int method/*=0*/, double ransacReprojThreshold/*=3*/, CvMat mask/*=null*/);

    public static native void cvRQDecomp3x3(CvMat matrixM, CvMat matrixR, CvMat matrixQ,
            CvMat matrixQx/*=null*/, CvMat matrixQy/*=null*/, CvMat matrixQz/*=null*/,
            CvPoint3D64f eulerAngles/*=null*/);

    public static native void cvDecomposeProjectionMatrix(CvMat projMatr, CvMat calibMatr,
            CvMat rotMatr, CvMat posVect, CvMat rotMatrX/*=null*/, CvMat rotMatrY/*=null*/,
            CvMat rotMatrZ/*=null*/, CvPoint3D64f eulerAngles/*=null*/);

    public static native void cvCalcMatMulDeriv(CvMat A, CvMat B, CvMat dABdA, CvMat dABdB);

    public static native void cvComposeRT(CvMat _rvec1, CvMat _tvec1, CvMat _rvec2,
                                          CvMat _tvec2, CvMat _rvec3, CvMat _tvec3,
                                          CvMat dr3dr1/*=null*/, CvMat dr3dt1/*=null*/,
                                          CvMat dr3dr2/*=null*/, CvMat dr3dt2/*=null*/,
                                          CvMat dt3dr1/*=null*/, CvMat dt3dt1/*=null*/,
                                          CvMat dt3dr2/*=null*/, CvMat dt3dt2/*=null*/);

    public static void cvProjectPoints2(CvMat object_points, CvMat rotation_vector,
            CvMat translation_vector, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat image_points) {
        cvProjectPoints2(object_points, rotation_vector,translation_vector,
                intrinsic_matrix, distortion_coeffs, image_points,
                null, null, null, null, null, 0);
    }
    public static native void cvProjectPoints2(CvMat object_points, CvMat rotation_vector,
            CvMat translation_vector, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat image_points,
            CvMat dpdrot/*=null*/, CvMat dpdt/*=null*/, CvMat dpd/*=null*/,
            CvMat dpdc/*=null*/, CvMat dpddist/*=null*/, double aspect_ratio/*=0*/);

    public static void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
            CvMat camera_matrix, CvMat distortion_coeffs,
            CvMat rotation_vector, CvMat translation_vector) {
        cvFindExtrinsicCameraParams2(object_points, image_points, camera_matrix,
                distortion_coeffs, rotation_vector, translation_vector, 0);
    }
    public static native void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
            CvMat camera_matrix, CvMat distortion_coeffs,
            CvMat rotation_vector, CvMat translation_vector, int use_extrinsic_guess/*=0*/);

    public static void cvInitIntrinsicParams2D(CvMat object_points, CvMat image_points,
            CvMat npoints, @ByVal CvSize image_size, CvMat camera_matrix) {
        cvInitIntrinsicParams2D(object_points, image_points, npoints, image_size, camera_matrix, -1);
    }
    public static native void cvInitIntrinsicParams2D(CvMat object_points, CvMat image_points,
            CvMat npoints, @ByVal CvSize image_size, CvMat camera_matrix, double aspect_ratio/*=1*/);

    public static final int
            CV_CALIB_CB_ADAPTIVE_THRESH = 1,
            CV_CALIB_CB_NORMALIZE_IMAGE = 2,
            CV_CALIB_CB_FILTER_QUADS    = 4,
            CV_CALIB_CB_FAST_CHECK      = 8;

    public static native int cvCheckChessboard(IplImage src, @ByVal CvSize size);
    public static native int cvFindChessboardCorners(CvArr image, @ByVal CvSize pattern_size,
            CvPoint2D32f corners, int[] corner_count/*=null*/, 
            int flags/*=CV_CALIB_CB_ADAPTIVE_THRESH | CV_CALIB_CB_NORMALIZE_IMAGE */);
    public static native void cvDrawChessboardCorners(CvArr image, @ByVal CvSize pattern_size,
            CvPoint2D32f corners, int count, int pattern_was_found);

    public static final int
            CV_CALIB_USE_INTRINSIC_GUESS = 1,
            CV_CALIB_FIX_ASPECT_RATIO    = 2,
            CV_CALIB_FIX_PRINCIPAL_POINT = 4,
            CV_CALIB_ZERO_TANGENT_DIST   = 8,
            CV_CALIB_FIX_FOCAL_LENGTH    = 16,
            CV_CALIB_FIX_K1              = 32,
            CV_CALIB_FIX_K2              = 64,
            CV_CALIB_FIX_K3              = 128,
            CV_CALIB_FIX_K4              = 2048,
            CV_CALIB_FIX_K5              = 4096,
            CV_CALIB_FIX_K6              = 8192,
            CV_CALIB_RATIONAL_MODEL      = 16384;

    public static native double cvCalibrateCamera2(CvMat object_points, CvMat image_points,
            CvMat point_counts, @ByVal CvSize image_size, CvMat camera_matrix, CvMat distortion_coeffs,
            CvMat rotation_vectors/*=null*/, CvMat translation_vectors/*=null*/, int flags/*=0*/);
    public static native double cvCalibrateCamera2(CvMat object_points, CvMat image_points,
            CvMat point_counts, @ByVal CvSize image_size, CvMat camera_matrix, CvMat distortion_coeffs,
            CvMat rotation_vectors/*=null*/, CvMat translation_vectors/*=null*/, int flags/*=0*/,
            @ByVal CvTermCriteria term_crit/*=cvTermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS,30,DBL_EPSILON)*/);

    public static native void cvCalibrationMatrixValues(CvMat camera_matrix, @ByVal CvSize image_size,
            double aperture_width/*=0*/, double aperture_height/*=0*/,
            double[] fovx/*=null*/, double[] fovy/*=null*/,
            double[] focal_length/*=null*/, CvPoint2D64f principal_point/*=null*/,
            double[] pixel_aspect_ratio/*=null*/);

    public static final int
            CV_CALIB_FIX_INTRINSIC     = 256,
            CV_CALIB_SAME_FOCAL_LENGTH = 512;

    public static native double cvStereoCalibrate(
            CvMat object_points, CvMat image_points1, CvMat image_points2, CvMat npoints,
            CvMat camera_matrix1, CvMat dist_coeffs1, CvMat camera_matrix2, CvMat dist_coeffs2,
            @ByVal CvSize image_size, CvMat R, CvMat T, CvMat E/*=null*/, CvMat F/*=null*/, 
            @ByVal CvTermCriteria term_crit/*=cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 30, 1e-6)*/,
            int flags/*=CV_CALIB_FIX_INTRINSIC*/);

    public static final int CV_CALIB_ZERO_DISPARITY = 1024;

    public static native void cvStereoRectify(CvMat camera_matrix1, CvMat camera_matrix2,
            CvMat dist_coeffs1, CvMat dist_coeffs2, @ByVal CvSize image_size, CvMat R, CvMat T,
            CvMat R1, CvMat R2, CvMat P1, CvMat P2, CvMat Q/*=null*/,
            int flags/*=CV_CALIB_ZERO_DISPARITY*/, double alpha/*=-1*/,
            @ByVal CvSize new_image_size/*=cvSize(0,0)*/,
            CvRect valid_pix_ROI1/*=null*/, CvRect valid_pix_ROI2/*=null*/);
    public static native int cvStereoRectifyUncalibrated(CvMat points1, CvMat points2,
            CvMat F, @ByVal CvSize img_size, CvMat H1, CvMat H2, double threshold/*=5*/);


    public static final int
            CV_STEREO_BM_NORMALIZED_RESPONSE = 0,
            CV_STEREO_BM_XSOBEL              = 1;

    public static class CvStereoBMState extends Pointer {
        static { load(); }
        public CvStereoBMState() { allocate(); }
        public CvStereoBMState(int size) { allocateArray(size); }
        public CvStereoBMState(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStereoBMState position(int position) {
            return (CvStereoBMState)super.position(position);
        }

        public static CvStereoBMState create(int preset, int numberOfDisparities) {
            CvStereoBMState p = cvCreateStereoBMState(preset, numberOfDisparities);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }
        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvStereoBMState implements Deallocator {
            ReleaseDeallocator(CvStereoBMState p) { super(p); }
            @Override public void deallocate() { cvReleaseStereoBMState(this); }
        }

        public native int preFilterType();       public native CvStereoBMState preFilterType(int preFilterType);
        public native int preFilterSize();       public native CvStereoBMState preFilterSize(int preFilterSize);
        public native int preFilterCap();        public native CvStereoBMState preFilterCap(int preFilterCap);

        public native int SADWindowSize();       public native CvStereoBMState SADWindowSize(int SADWindowSize);
        public native int minDisparity();        public native CvStereoBMState minDisparity(int minDisparity);
        public native int numberOfDisparities(); public native CvStereoBMState numberOfDisparities(int numberOfDisparities);

        public native int textureThreshold();    public native CvStereoBMState textureThreshold(int textureThreshold);
        public native int uniquenessRatio();     public native CvStereoBMState uniquenessRatio(int uniquenessRatio);
        public native int speckleWindowSize();   public native CvStereoBMState speckleWindowSize(int speckleWindowSize);
        public native int speckleRange();        public native CvStereoBMState speckleRange(int speckleRange);

        public native int trySmallerWindows();   public native CvStereoBMState trySmallerWindows(int trySmallerWindows);

        public native @ByRef CvRect roi1();      public native CvStereoBMState roi1(CvRect roi1);
        public native @ByRef CvRect roi2();      public native CvStereoBMState roi2(CvRect roi2);
        public native int disp12MaxDiff();       public native CvStereoBMState disp12MaxDiff(int disp12MaxDiff);

        public native CvMat preFilteredImg0();   public native CvStereoBMState preFilteredImg0(CvMat preFilteredImg0);
        public native CvMat preFilteredImg1();   public native CvStereoBMState preFilteredImg1(CvMat preFilteredImg1);
        public native CvMat slidingSumBuf();     public native CvStereoBMState slidingSumBuf(CvMat slidingSumBuf);
        public native CvMat cost();              public native CvStereoBMState cost(CvMat cost);
        public native CvMat disp();              public native CvStereoBMState disp(CvMat disp);
    }

    public static final int
            CV_STEREO_BM_BASIC = 0,
            CV_STEREO_BM_FISH_EYE = 1,
            CV_STEREO_BM_NARROW = 2;

    public static native CvStereoBMState cvCreateStereoBMState(
            int preset/*=CV_STEREO_BM_BASIC*/, int numberOfDisparities/*=0*/);
    public static native void cvReleaseStereoBMState(@ByPtrPtr CvStereoBMState state);
    public static native void cvFindStereoCorrespondenceBM(CvArr left, CvArr right,
            CvArr disparity, CvStereoBMState state);

    public static native @ByVal CvRect cvGetValidDisparityROI(@ByVal CvRect roi1,
            @ByVal CvRect roi2, int minDisparity, int numberOfDisparities, int SADWindowSize);
    public static native void cvValidateDisparity(CvArr disparity, CvArr cost,
            int minDisparity, int numberOfDisparities, int disp12MaxDiff/*=1*/);


    public static void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage, CvMat Q) {
        cvReprojectImageTo3D(disparityImage, _3dImage, Q, 0);
    }
    public static native void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage,
            CvMat Q, int handleMissingValues/*=0*/);


    @NoOffset public static class CvLevMarq extends Pointer {
        static { load(); }
        public CvLevMarq() { allocate(); }
        public CvLevMarq(int nparams, int nerrs,
                @ByVal CvTermCriteria criteria/*=cvTermCriteria(CV_TERMCRIT_EPS+CV_TERMCRIT_ITER,30,DBL_EPSILON)*/,
                boolean completeSymmFlag/*=false*/) {
            allocate(nparams, nerrs, criteria, completeSymmFlag);
        }
        public CvLevMarq(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int nparams, int nerrs,
                @ByVal CvTermCriteria criteria/*=cvTermCriteria(CV_TERMCRIT_EPS+CV_TERMCRIT_ITER,30,DBL_EPSILON)*/,
                boolean completeSymmFlag/*=false*/);

        public native void init(int nparams, int nerrs,
                @ByVal CvTermCriteria criteria/*=cvTermCriteria(CV_TERMCRIT_EPS+CV_TERMCRIT_ITER,30,DBL_EPSILON)*/,
                boolean completeSymmFlag/*=false*/);
        public native boolean update(@Cast("const CvMat*&") CvMat param, @ByPtrRef CvMat J, @ByPtrRef CvMat err);
        public native boolean updateAlt(@Cast("const CvMat*&") CvMat param, @ByPtrRef CvMat JtJ, @ByPtrRef CvMat JtErr, @ByPtrRef DoublePointer errNorm);

        public native void clear();
        public native void step();
        public static final int DONE=0, STARTED=1, CALC_J=2, CHECK_ERR=3;

        public native CvMat mask();               public native CvLevMarq mask(CvMat mask);
        public native CvMat prevParam();          public native CvLevMarq prevParam(CvMat prevParam);
        public native CvMat param();              public native CvLevMarq param(CvMat param);
        public native CvMat J();                  public native CvLevMarq J(CvMat J);
        public native CvMat err();                public native CvLevMarq err(CvMat err);
        public native CvMat JtJ();                public native CvLevMarq JtJ(CvMat JtJ);
        public native CvMat JtJN();               public native CvLevMarq JtJN(CvMat JtJN);
        public native CvMat JtErr();              public native CvLevMarq JtErr(CvMat JtErr);
        public native CvMat JtJV();               public native CvLevMarq JtJV(CvMat JtJV);
        public native CvMat JtJW();               public native CvLevMarq JtJW(CvMat JtJW);
        public native double prevErrNorm();       public native CvLevMarq prevErrNorm(double prevErrNorm);
        public native double errNorm();           public native CvLevMarq errNorm(double errNorm);
        public native int lambdaLg10();           public native CvLevMarq lambdaLg10(int lambdaLg10);
        @ByRef
        public native CvTermCriteria criteria();  public native CvLevMarq criteria(CvTermCriteria criteria);
        public native int state();                public native CvLevMarq state(int state);
        public native int iters();                public native CvLevMarq iters(int iters);
        public native boolean completeSymmFlag(); public native CvLevMarq completeSymmFlag(boolean completeSymmFlag);
    }

    public static final int
        ITERATIVE=CV_ITERATIVE,
        EPNP=CV_EPNP,
        P3P=CV_P3P;

    @Namespace("cv") public static native boolean solvePnP(@InputArray CvMat objectPoints,
            @InputArray CvMat imagePoints, @InputArray CvMat cameraMatrix,
            @InputArray CvMat distCoeffs,  @OutputArray CvMat rvec,
            @OutputArray CvMat tvec, boolean useExtrinsicGuess/*=false*/, int flags/*=ITERATIVE*/);
    @Namespace("cv") public static native void solvePnPRansac(@InputArray CvMat objectPoints,
            @InputArray CvMat imagePoints, @InputArray CvMat cameraMatrix,
            @InputArray CvMat distCoeffs,  @InputArray CvMat rvec,
            @InputArray CvMat tvec, boolean useExtrinsicGuess/*=false*/,
            int iterationsCount/*=100*/, float reprojectionError/*=8.0*/, int minInliersCount/*=100*/,
            @OutputArray CvMat inliers/*=noArray()*/, int flags/*=ITERATIVE*/);

    @Namespace("cv") public static native boolean find4QuadCornerSubpix(@InputArray CvArr img,
            @InputArray CvArr corners, @ByVal CvSize region_size);
    
    public static final int CALIB_CB_SYMMETRIC_GRID = 1, CALIB_CB_ASYMMETRIC_GRID = 2,
            CALIB_CB_CLUSTERING = 4;
    @Namespace("cv") public static native boolean findCirclesGrid(@InputArray CvArr image, @ByVal CvSize patternSize,
            @OutputArray CvMat centers, int flags/*=CALIB_CB_SYMMETRIC_GRID*/,
            @ByRef FeatureDetectorPtr blobDetector/*=new SimpleBlobDetector()*/);
    @Namespace("cv") public static native boolean findCirclesGridDefault(@InputArray CvArr image, @ByVal CvSize patternSize,
            @OutputArray CvMat centers, int flags/*=CALIB_CB_SYMMETRIC_GRID*/);

    @Namespace("cv") public static native float rectify3Collinear(@InputArray CvMat cameraMatrix1, @InputArray CvMat distCoeffs1,
            @InputArray CvMat cameraMatrix2, @InputArray CvMat distCoeffs2, @InputArray CvMat cameraMatrix3, @InputArray CvMat distCoeffs3,
            @ByRef Point2fVectorVector imgpt1, @ByRef Point2fVectorVector imgpt3, @ByVal CvSize imageSize,
            @InputArray CvMat R12, @InputArray CvMat T12, @InputArray CvMat R13, @InputArray CvMat T13,
            @InputArray CvMat R1, @InputArray CvMat R2, @InputArray CvMat R3,
            @InputArray CvMat P1, @InputArray CvMat P2, @InputArray CvMat P3,
            @InputArray CvMat Q,  double alpha, @ByVal CvSize newImgSize,
            @Const @Adapter("RectAdapter") CvRect roi1, @Const @Adapter("RectAdapter") CvRect roi2, int flags);

    @NoOffset @Namespace("cv") public static class StereoBM extends Pointer {
        static { load(); }
        public StereoBM() { allocate(); }
        public StereoBM(int preset, int ndisparities/*=0*/, int SADWindowSize/*=21*/) {
            allocate(preset, ndisparities, SADWindowSize);
        }
        public StereoBM(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int preset, int ndisparities/*=0*/, int SADWindowSize/*=21*/);

        public static final int
                PREFILTER_NORMALIZED_RESPONSE = 0, PREFILTER_XSOBEL = 1,
                BASIC_PRESET=0, FISH_EYE_PRESET=1, NARROW_PRESET=2;

        public native void init(int preset, int ndisparities/*=0*/, int SADWindowSize/*=21*/);
        public native @Name("operator()") void compute(@InputArray CvArr left,
                @InputArray CvArr right, @InputArray CvArr disparity, int disptype/*=CV_16S*/);

        public native CvStereoBMState state(); public native StereoBM state(CvStereoBMState state);
    }

    @NoOffset @Namespace("cv") public static class StereoSGBM extends Pointer {
        static { load(); }
        public StereoSGBM() { allocate(); }
        public StereoSGBM(int minDisparity, int numDisparities, int SADWindowSize,
               int P1/*=0*/, int P2/*=0*/, int disp12MaxDiff/*=0*/, int preFilterCap/*=0*/, int uniquenessRatio/*=0*/,
               int speckleWindowSize/*=0*/, int speckleRange/*=0*/, boolean fullDP/*=false*/) {
            allocate(minDisparity, numDisparities, SADWindowSize, P1, P2, disp12MaxDiff, preFilterCap,
                    uniquenessRatio, speckleWindowSize, speckleRange, fullDP);
        }
        public StereoSGBM(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int minDisparity, int numDisparities, int SADWindowSize,
               int P1/*=0*/, int P2/*=0*/, int disp12MaxDiff/*=0*/, int preFilterCap/*=0*/, int uniquenessRatio/*=0*/,
               int speckleWindowSize/*=0*/, int speckleRange/*=0*/, boolean fullDP/*=false*/);

        public static final int DISP_SHIFT=4, DISP_SCALE = (1<<DISP_SHIFT);

        public native @Name("operator()") void compute(@InputArray CvArr left, @InputArray CvArr right, @InputArray CvArr disp);

        public native int minDisparity();        public native StereoSGBM minDisparity(int minDisparity);
        public native int numberOfDisparities(); public native StereoSGBM numberOfDisparities(int numberOfDisparities);
        public native int SADWindowSize();       public native StereoSGBM SADWindowSize(int SADWindowSize);
        public native int preFilterCap();        public native StereoSGBM preFilterCap(int preFilterCap);
        public native int uniquenessRatio();     public native StereoSGBM uniquenessRatio(int uniquenessRatio);
        public native int P1();                  public native StereoSGBM P1(int P1);
        public native int P2();                  public native StereoSGBM P2(int P2);
        public native int speckleWindowSize();   public native StereoSGBM speckleWindowSize(int speckleWindowSize);
        public native int speckleRange();        public native StereoSGBM speckleRange(int speckleRange);
        public native int disp12MaxDiff();       public native StereoSGBM disp12MaxDiff(int disp12MaxDiff);
        public native boolean fullDP();          public native StereoSGBM fullDP(boolean fullDP);

//        protected native @OutputMat CvMat buffer();
    }

    @Namespace("cv") public static native void filterSpeckles(@InputArray CvArr img, double newVal,
            int maxSpeckleSize, double maxDiff, @InputArray CvArr buf);

    @Namespace("cv") public static native int estimateAffine3D(@InputArray CvArr src, @InputArray CvArr dst,
            @OutputArray CvMat out, @OutputArray CvMat inliers, double ransacThreshold/*=3*/, double confidence/*=0.99*/);
}
