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
 * This file is based on information found in calib3d.hpp
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

import java.nio.FloatBuffer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(include="<opencv2/calib3d/calib3d.hpp>", includepath=genericIncludepath,
        linkpath=genericLinkpath,       link="opencv_calib3d"),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, link="opencv_calib3d220"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_calib3d {
    static { load(opencv_imgproc.class); load(); }

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
            FloatBuffer /*CvMatr32f*/ rotation_matrix, FloatBuffer /*CvVect32f*/ translation_vector);
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
            CV_FM_RANSAC = CV_RANSAC;

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
            @ByVal CvSize new_imag_size/*=cvSize(0,0)*/, CvRect valid_pixel_ROI/*=null*/);

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

        public native @ByVal CvRect roi1();      public native CvStereoBMState roi1(CvRect roi1);
        public native @ByVal CvRect roi2();      public native CvStereoBMState roi2(CvRect roi2);
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


    public static final int CV_STEREO_GC_OCCLUDED = Short.MAX_VALUE;

    public static class CvStereoGCState extends Pointer {
        static { load(); }
        public CvStereoGCState() { allocate(); }
        public CvStereoGCState(int size) { allocateArray(size); }
        public CvStereoGCState(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStereoGCState position(int position) {
            return (CvStereoGCState)super.position(position);
        }

        public static CvStereoGCState create(int numberOfDisparities, int maxIters) {
            CvStereoGCState p = cvCreateStereoGCState(numberOfDisparities, maxIters);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }
        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvStereoGCState implements Deallocator {
            ReleaseDeallocator(CvStereoGCState p) { super(p); }
            @Override public void deallocate() { cvReleaseStereoGCState(this); }
        }

        public native int Ithreshold();          public native CvStereoGCState Ithreshold(int Ithreshold);
        public native int interactionRadius();   public native CvStereoGCState interactionRadius(int interactionRadius);
        public native float K();                 public native CvStereoGCState K(float K);
        public native float lambda();            public native CvStereoGCState lambda(float lambda);
        public native float lambda1();           public native CvStereoGCState lambda1(float lambda1);
        public native float lambda2();           public native CvStereoGCState lambda2(float lambda2);
        public native int occlusionCost();       public native CvStereoGCState occlusionCost(int occlusionCost);
        public native int minDisparity();        public native CvStereoGCState minDisparity(int minDisparity);
        public native int numberOfDisparities(); public native CvStereoGCState numberOfDisparities(int numberOfDisparities);
        public native int maxIters();            public native CvStereoGCState maxIters(int maxIters);

        public native CvMat left();              public native CvStereoGCState left(CvMat left);
        public native CvMat right();             public native CvStereoGCState right(CvMat left);
        public native CvMat dispLeft();          public native CvStereoGCState dispLeft(CvMat left);
        public native CvMat dispRight();         public native CvStereoGCState dispRight(CvMat left);
        public native CvMat ptrLeft();           public native CvStereoGCState ptrLeft(CvMat left);
        public native CvMat ptrRight();          public native CvStereoGCState ptrRight(CvMat left);
        public native CvMat vtxBuf();            public native CvStereoGCState vtxBuf(CvMat left);
        public native CvMat edgeBuf();           public native CvStereoGCState edgeBuf(CvMat left);
    }

    public static native CvStereoGCState cvCreateStereoGCState(int numberOfDisparities, int maxIters);
    public static native void cvReleaseStereoGCState(@ByPtrPtr CvStereoGCState state);
    public static native void cvFindStereoCorrespondenceGC(CvArr left, CvArr right,
            CvArr disparityLeft, CvArr disparityRight,
            CvStereoGCState state, int useDisparityGuess/*=0*/);

    public static void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage, CvMat Q) {
        cvReprojectImageTo3D(disparityImage, _3dImage, Q, 0);
    }
    public static native void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage,
            CvMat Q, int handleMissingValues/*=0*/);
}
