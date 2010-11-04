/*
 * Copyright (C) 2009,2010 Samuel Audet
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
 * This file is based on information found in cvtypes.h and cv.h of 
 * OpenCV 2.0, which are covered by the following copyright notice:
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

package com.googlecode.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import java.nio.FloatBuffer;

import static com.googlecode.javacv.jna.cxcore.*;

/**
 *
 * @author Samuel Audet
 */
public class cv {
    // OpenCV does not always install itself in the PATH :(
    public static final String[] paths = cxcore.paths;
    public static final String[] libnames = { "cv", "cv_64", "cv210", "cv210_64",
            "cv200", "cv200_64", "cv110", "cv110_64", "cv100", "cv100_64" };
    public static final String libname = Loader.load(paths, libnames);


    public static class v10 extends cv {
        public static final String libname = Loader.load(paths, libnames);

        public static native int cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography);

        public static native void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
                CvMat camera_matrix, CvMat distortion_coeffs,
                CvMat rotation_vector, CvMat translation_vector);

        public static native CvConDensation cvCreateConDensation(int dynam_params, int measure_params, int sample_count);
        public static native void cvReleaseConDensation(CvConDensation.PointerByReference condens);
        public static native void cvConDensInitSampleSet(CvConDensation condens, CvMat lower_bound, CvMat upper_bound);
        public static native void cvConDensUpdateByTime(CvConDensation condens);

        public static final int CV_DOMINANT_IPAN = 1;
        public static native CvSeq cvFindDominantPoints(CvSeq contour, CvMemStorage storage,
                int method/*=CV_DOMINANT_IPAN*/, double parameter1/*=0*/,
                double parameter2/*=0*/, double parameter3/*=0*/, double parameter4/*=0*/);

        public static native double cvContourArea(CvArr contour, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/);

        public static native void cvCalcPGH(CvSeq contour, CvHistogram hist);

        public static native void cvCalcImageHomography(float[] line, CvPoint3D32f center,
                float[] intrinsic, float[] homography);
        public static native void cvCalcImageHomography(FloatBuffer line, CvPoint3D32f center,
                FloatBuffer intrinsic, FloatBuffer homography);

        public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix, CvMat distortion_coeffs);
    }
    public static class v11 extends v11or20 {
        public static final String libname = Loader.load(paths, libnames);

        public static native CvFeatureTree cvCreateFeatureTree(CvMat desc);

        public static native void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
                CvMat camera_matrix, CvMat distortion_coeffs,
                CvMat rotation_vector, CvMat translation_vector);

        public static native void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage, CvMat Q);

        public static native CvConDensation cvCreateConDensation(int dynam_params, int measure_params, int sample_count);
        public static native void cvReleaseConDensation(CvConDensation.PointerByReference condens);
        public static native void cvConDensInitSampleSet(CvConDensation condens, CvMat lower_bound, CvMat upper_bound);
        public static native void cvConDensUpdateByTime(CvConDensation condens);

        public static final int CV_DOMINANT_IPAN = 1;
        public static native CvSeq cvFindDominantPoints(CvSeq contour, CvMemStorage storage,
                int method/*=CV_DOMINANT_IPAN*/, double parameter1/*=0*/,
                double parameter2/*=0*/, double parameter3/*=0*/, double parameter4/*=0*/);

        public static native double cvContourArea(CvArr contour, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/);

        public static native void cvCalcPGH(CvSeq contour, CvHistogram hist);

        public static native void cvCalcImageHomography(float[] line, CvPoint3D32f center,
                float[] intrinsic, float[] homography);
        public static native void cvCalcImageHomography(FloatBuffer line, CvPoint3D32f center,
                FloatBuffer intrinsic, FloatBuffer homography);

        public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix, CvMat distortion_coeffs);

        public static native void cvStereoRectify(CvMat camera_matrix1, CvMat camera_matrix2,
                CvMat dist_coeffs1, CvMat dist_coeffs2, CvSize.ByValue image_size, CvMat R, CvMat T,
                CvMat R1, CvMat R2, CvMat P1, CvMat P2, CvMat Q/*=null*/,
                int flags/*=CV_CALIB_ZERO_DISPARITY*/);

        public static class CvStereoBMState extends v11or20.CvStereoBMState {
            public CvStereoBMState() { releasable = false; }
            public CvStereoBMState(Pointer m) { super(m); releasable = true;
                    if (autoSynch && getClass() == CvStereoBMState.class) read(); }

            public CvMat.ByReference preFilteredImg0;
            public CvMat.ByReference preFilteredImg1;
            public CvMat.ByReference slidingSumBuf;
            public CvMat.ByReference dbmin;
            public CvMat.ByReference dbmax;
        }
    }
    public static class v11or20 extends cv {
        public static final String libname = Loader.load(paths, libnames);

        public static class CvFeatureTree extends PointerType { };
        public static native void cvReleaseFeatureTree(CvFeatureTree tr);
        public static native void cvFindFeatures(CvFeatureTree tr, CvMat query_points, CvMat indices, CvMat dist,
                int k, int emax/*=20*/);
        public static native int cvFindFeaturesBoxed(CvFeatureTree tr, CvMat bounds_min,
                CvMat bounds_max, CvMat out_indices);


        public static class CvSURFPoint extends Structure {
            public static boolean autoSynch = true;
            public CvSURFPoint() { setAutoSynch(autoSynch); }
            public CvSURFPoint(Pointer m) { super(m); setAutoSynch(autoSynch); 
                    if (autoSynch && getClass() == CvSURFPoint.class) read(); }

            public CvSURFPoint(CvPoint2D32f pt, int laplacian, int size) {
                this(pt, laplacian, size, 0, 0);
            }
            public CvSURFPoint(CvPoint2D32f pt, int laplacian, int size,
                    float dir, float hessian) {
                this.pt = pt;
                this.laplacian = laplacian;
                this.size = size;
                this.dir = dir;
                this.hessian = hessian;
            }

            public CvPoint2D32f pt;
            public int laplacian;
            public int size;
            public float dir;
            public float hessian;
        }
        public static class CvSURFParams extends Structure {
            public static boolean autoSynch = true;
            public CvSURFParams() { setAutoSynch(autoSynch); }
            public CvSURFParams(Pointer m) { super(m); setAutoSynch(autoSynch);
                    if (autoSynch && getClass() == CvSURFParams.class) read(); }

            public int extended;
            public double hessianThreshold;
            public int nOctaves;
            public int nOctaveLayers;

            public static class ByValue extends CvSURFParams implements Structure.ByValue {
                public ByValue() { }
                public ByValue(CvSURFParams o) {
                    this.extended = o.extended;
                    this.hessianThreshold = o.hessianThreshold;
                    this.nOctaves = o.nOctaves;
                    this.nOctaveLayers = o.nOctaveLayers;
                }
            }
            public ByValue byValue() {
                return this instanceof ByValue ? (ByValue)this : new ByValue(this);
            }
        }
        public static native CvSURFParams.ByValue cvSURFParams(double hessianThreshold, int extended/*=0*/);
        public static native void cvExtractSURF(CvArr image, CvArr mask, CvSeq.PointerByReference keypoints,
                CvSeq.PointerByReference descriptors, CvMemStorage storage,
                CvSURFParams.ByValue params, int useProvidedKeyPts/*=0*/);


        public static native CvMat.PointerByReference cvCreatePyramid(CvArr img, int extra_layers,
                double rate, CvSize layer_sizes, CvArr bufarr, int calc, int filter);
        public static native void cvReleasePyramid(Pointer /* CvMat*** */ pyramid, int extra_layers);
        public static void cvReleasePyramid(CvMat.PointerByReference[] /* CvMat*** */ pyramid, int extra_layers) {
            Memory m = new Memory(Pointer.SIZE * pyramid.length);
            for (int i = 0; i < pyramid.length; i++) {
                m.setPointer(Pointer.SIZE * i, pyramid[i].getPointer());
            }
            cvReleasePyramid(m, extra_layers);
        }


        public static native void cvConvertMaps(CvArr mapx, CvArr mapy, CvArr mapxy, CvArr mapalpha);


        public static native void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
                CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
                float[] matrices, int count, CvSize.ByValue win_size, int level,
                byte[] status, float[] track_error, CvTermCriteria.ByValue criteria, int flags);
        public static void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
                CvArr curr_pyr, CvPoint2D32f[] prev_features, CvPoint2D32f[] curr_features,
                float[] matrices, int count, CvSize.ByValue win_size, int level,
                byte[] status, float[] track_error, CvTermCriteria.ByValue criteria, int flags) {
            for (Structure s : prev_features) { s.write(); }
            cvCalcAffineFlowPyrLK(prev, curr, prev_pyr, curr_pyr,
                    prev_features[0], curr_features[0], matrices, count, win_size, level,
                    status, track_error, criteria, flags);
            for (Structure s : curr_features) { s.read(); }
        }
        public static native void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
                CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
                FloatBuffer matrices, int count, CvSize.ByValue win_size, int level,
                byte[] status, FloatBuffer track_error, CvTermCriteria.ByValue criteria, int flags);
        public static void cvCalcAffineFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
                CvArr curr_pyr, CvPoint2D32f[] prev_features, CvPoint2D32f[] curr_features,
                FloatBuffer matrices, int count, CvSize.ByValue win_size, int level,
                byte[] status, FloatBuffer track_error, CvTermCriteria.ByValue criteria, int flags) {
            for (Structure s : prev_features) { s.write(); }
            cvCalcAffineFlowPyrLK(prev, curr, prev_pyr, curr_pyr,
                    prev_features[0], curr_features[0], matrices, count, win_size, level,
                    status, track_error, criteria, flags);
            for (Structure s : curr_features) { s.read(); }
        }
        public static native int cvEstimateRigidTransform(CvArr A, CvArr B, CvMat M, int full_affine);

        public static final int
                CV_LMEDS = 4,
                CV_RANSAC = 8;
        public static native int cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography,
                int method/*=0*/, double ransacReprojThreshold/*=0*/, CvMat mask/*=null*/);
        public static int cvFindHomography(CvMat src_points, CvMat dst_points, CvMat homography) {
            return cvFindHomography(src_points, dst_points, homography, 0, 0, null);
        }

        public static native void cvRQDecomp3x3(CvMat matrixM, CvMat matrixR, CvMat matrixQ,
                CvMat matrixQx/*=null*/, CvMat matrixQy/*=null*/, CvMat matrixQz/*=null*/,
                CvPoint3D64f eulerAngles/*=null*/);
        public static void cvRQDecomp3x3(CvMat matrixM, CvMat matrixR, CvMat matrixQ,
                CvMat matrixQx/*=null*/, CvMat matrixQy/*=null*/, CvMat matrixQz/*=null*/,
                CvPoint3D64f[] eulerAngles/*=null*/) {
            for (Structure s : eulerAngles) { s.write(); }
            cvRQDecomp3x3(matrixM, matrixR, matrixQ, matrixQx, matrixQy, matrixQz, eulerAngles[0]);
            for (Structure s : eulerAngles) { s.read();  }
        }

        public static native void cvDecomposeProjectionMatrix(CvMat projMatr, CvMat calibMatr,
                CvMat rotMatr, CvMat posVect, CvMat rotMatrX/*=null*/, CvMat rotMatrY/*=null*/,
                CvMat rotMatrZ/*=null*/, CvPoint3D64f eulerAngles/*=null*/);
        public static void cvDecomposeProjectionMatrix(CvMat projMatr, CvMat calibMatr,
                CvMat rotMatr, CvMat posVect, CvMat rotMatrX/*=null*/, CvMat rotMatrY/*=null*/,
                CvMat rotMatrZ/*=null*/, CvPoint3D64f[] eulerAngles/*=null*/) {
            for (Structure s : eulerAngles) { s.write(); }
            cvDecomposeProjectionMatrix(projMatr, calibMatr, rotMatr, posVect,
                    rotMatrX, rotMatrY, rotMatrZ, eulerAngles[0]);
            for (Structure s : eulerAngles) { s.read();  }
        }

        public static native void cvCalcMatMulDeriv(CvMat A, CvMat B, CvMat dABdA, CvMat dABdB);

        public static native void cvComposeRT(CvMat _rvec1, CvMat _tvec1, CvMat _rvec2,
                                              CvMat _tvec2, CvMat _rvec3, CvMat _tvec3,
                                              CvMat dr3dr1/*=null*/, CvMat dr3dt1/*=null*/,
                                              CvMat dr3dr2/*=null*/, CvMat dr3dt2/*=null*/,
                                              CvMat dt3dr1/*=null*/, CvMat dt3dt1/*=null*/,
                                              CvMat dt3dr2/*=null*/, CvMat dt3dt2/*=null*/);

        public static native void cvInitUndistortRectifyMap(CvMat camera_matrix, CvMat dist_coeffs,
                CvMat R, CvMat new_camera_matrix, CvArr mapx, CvArr mapy);
        public static native void cvUndistortPoints(CvMat src, CvMat dst, CvMat camera_matrix,
                CvMat dist_coeffs, CvMat R/*=null*/, CvMat P/*=null*/);

        public static native void cvInitIntrinsicParams2D(CvMat object_points, CvMat image_points,
                CvMat npoints, CvSize.ByValue image_size, CvMat camera_matrix, double aspect_ratio/*=1*/);

        public static native void cvCalibrationMatrixValues(CvMat camera_matrix, CvSize image_size,
                double aperture_width/*=0*/, double aperture_height/*=0*/,
                DoubleByReference fovx/*=null*/, DoubleByReference fovy/*=null*/,
                DoubleByReference focal_length/*=null*/, CvPoint2D64f principal_point/*=null*/,
                DoubleByReference pixel_aspect_ratio/*=null*/);
        public static final int
                CV_CALIB_FIX_INTRINSIC     = 256,
                CV_CALIB_SAME_FOCAL_LENGTH = 512;
        public static native double cvStereoCalibrate(CvMat object_points, CvMat image_points1,
                CvMat image_points2, CvMat point_counts,
                CvMat camera_matrix1, CvMat dist_coeffs1,
                CvMat camera_matrix2, CvMat dist_coeffs2,
                CvSize.ByValue image_size, CvMat R, CvMat T, CvMat E/*=null*/,
                CvMat F/*=null*/, CvTermCriteria.ByValue term_crit /*=cvTermCriteria(
                CV_TERMCRIT_ITER+CV_TERMCRIT_EPS,30,1e-6)*/, int flags/*=CV_CALIB_FIX_INTRINSIC*/);
        public static final int CV_CALIB_ZERO_DISPARITY = 1024;
//        public static native void cvStereoRectify(CvMat camera_matrix1, CvMat camera_matrix2,
//                CvMat dist_coeffs1, CvMat dist_coeffs2, CvSize.ByValue image_size, CvMat R, CvMat T,
//                CvMat R1, CvMat R2, CvMat P1, CvMat P2, CvMat Q/*=null*/,
//                int flags/*=CV_CALIB_ZERO_DISPARITY*/);
        public static native int cvStereoRectifyUncalibrated(CvMat points1, CvMat points2,
                CvMat F, CvSize.ByValue img_size, CvMat H1, CvMat H2, double threshold/*=5*/);

        public static final int
                CV_FM_LMEDS_ONLY = CV_LMEDS,
                CV_FM_RANSAC_ONLY = CV_RANSAC,
                CV_FM_LMEDS = CV_LMEDS,
                CV_FM_RANSAC = CV_RANSAC;


        public static native int cvRANSACUpdateNumIters(double p, double err_prob, int model_points, int max_iters);


        public static final int 
                CV_STEREO_BM_NORMALIZED_RESPONSE = 0,
                CV_STEREO_BM_XSOBEL              = 1;
        public static class CvStereoBMState extends Structure {
            public static boolean autoSynch = true;
            public CvStereoBMState() { setAutoSynch(autoSynch); releasable = false; }
            public CvStereoBMState(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                    if (autoSynch && getClass() == CvStereoBMState.class) read(); }

            public static CvStereoBMState create(int preset, int numberOfDisparities) {
                CvStereoBMState m = cvCreateStereoBMState(preset, numberOfDisparities);
                if (m != null) {
                    m.releasable = true;
                }
                return m;
            }
            public void release() {
                releasable = false;
                cvReleaseStereoBMState(pointerByReference());
            }
            @Override protected void finalize() {
                if (releasable) {
                    release();
                }
            }
            protected boolean releasable = false;

            public int preFilterType = CV_STEREO_BM_NORMALIZED_RESPONSE;
            public int preFilterSize;
            public int preFilterCap;

            public int SADWindowSize;
            public int minDisparity;
            public int numberOfDisparities;

            public int textureThreshold;
            public int uniquenessRatio;
            public int speckleWindowSize;
            public int speckleRange;

            public int trySmallerWindows;

//            public CvMat.ByReference preFilteredImg0;
//            public CvMat.ByReference preFilteredImg1;
//            public CvMat.ByReference slidingSumBuf;
//            public CvMat.ByReference dbmin;
//            public CvMat.ByReference dbmax;

            public static class ByReference extends CvStereoBMState implements Structure.ByReference { }

            public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
                public PointerByReference() { }
                public PointerByReference(CvStereoBMState p) {
                    setStructure(p);
                }
                public CvStereoBMState getStructure() {
                    return new CvStereoBMState(getValue());
                }
                public void getStructure(CvStereoBMState p) {
                    p.useMemory(getValue());
                    p.read();
                }
                public void setStructure(CvStereoBMState p) {
                    p.write();
                    setValue(p.getPointer());
                }
            }
            public PointerByReference pointerByReference() {
                return new PointerByReference(this);
            }
        }
        public static final int
                CV_STEREO_BM_BASIC = 0,
                CV_STEREO_BM_FISH_EYE = 1,
                CV_STEREO_BM_NARROW = 2;
        public static native CvStereoBMState cvCreateStereoBMState(int preset/*=CV_STEREO_BM_BASIC*/,
                int numberOfDisparities/*=0*/);
        public static native void cvReleaseStereoBMState(CvStereoBMState.PointerByReference state);
        public static native void cvFindStereoCorrespondenceBM(CvArr left, CvArr right,
                CvArr disparity, CvStereoBMState state);

        public static final int CV_STEREO_GC_OCCLUDED = Short.MAX_VALUE;
        public static class CvStereoGCState extends Structure {
            public static boolean autoSynch = true;
            public CvStereoGCState() { setAutoSynch(autoSynch); releasable = false; }
            public CvStereoGCState(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                    if (autoSynch && getClass() == CvStereoGCState.class) read(); }

            public static CvStereoGCState create(int numberOfDisparities, int maxIters) {
                CvStereoGCState m = cvCreateStereoGCState(numberOfDisparities, maxIters);
                if (m != null) {
                    m.releasable = true;
                }
                return m;
            }
            public void release() {
                releasable = false;
                cvReleaseStereoGCState(pointerByReference());
            }
            @Override protected void finalize() {
                if (releasable) {
                    release();
                }
            }
            private boolean releasable = false;

            public int Ithreshold;
            public int interactionRadius;
            public float K, lambda, lambda1, lambda2;
            public int occlusionCost;
            public int minDisparity;
            public int numberOfDisparities;
            public int maxIters;

            public CvMat.ByReference left;
            public CvMat.ByReference right;
            public CvMat.ByReference dispLeft;
            public CvMat.ByReference dispRight;
            public CvMat.ByReference ptrLeft;
            public CvMat.ByReference ptrRight;
            public CvMat.ByReference vtxBuf;
            public CvMat.ByReference edgeBuf;

            public static class ByReference extends CvStereoGCState implements Structure.ByReference { }

            public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
                public PointerByReference() { }
                public PointerByReference(CvStereoGCState p) {
                    setStructure(p);
                }
                public CvStereoGCState getStructure() {
                    return new CvStereoGCState(getValue());
                }
                public void getStructure(CvStereoGCState p) {
                    p.useMemory(getValue());
                    p.read();
                }
                public void setStructure(CvStereoGCState p) {
                    p.write();
                    setValue(p.getPointer());
                }
            }
            public PointerByReference pointerByReference() {
                return new PointerByReference(this);
            }
        }
        public static native CvStereoGCState cvCreateStereoGCState(int numberOfDisparities, int maxIters);
        public static native void cvReleaseStereoGCState(CvStereoGCState.PointerByReference state);
        public static native void cvFindStereoCorrespondenceGC(CvArr left, CvArr right,
                CvArr disparityLeft, CvArr disparityRight,
                CvStereoGCState state, int useDisparityGuess/*=0*/);

        public static native void cvConvertPointsHomogeneous(CvMat src, CvMat dst);
    }

    public static class v20or21 extends v11or20 {
        public static final String libname = Loader.load(paths, libnames);

        public static native CvFeatureTree cvCreateKDTree(CvMat desc);
        public static native CvFeatureTree cvCreateSpillTree(CvMat raw_data,
                int naive/*=50*/, double rho/*=0.7*/, double tau/*=0.1*/);


        public static class CvLSH extends PointerType {
            public CvLSH() { }
            public CvLSH(Pointer p) { super(p); }

            public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
                public PointerByReference() { }
                public PointerByReference(CvLSH p) {
                    setStructure(p);
                }
                public CvLSH getStructure() {
                    return new CvLSH(getValue());
                }
                public void getStructure(CvLSH p) {
                    p.setPointer(getValue());
                }
                public void setStructure(CvLSH p) {
                    setValue(p.getPointer());
                }
            }
            public PointerByReference pointerByReference() {
                return new PointerByReference(this);
            }
        }
        public static class CvLSHOperations extends PointerType { }
        public static native CvLSH cvCreateLSH(CvLSHOperations ops, int d, int L/*=10*/,
                int k/*=10*/, int type/*=CV_64FC1*/, double r/*=4*/, long seed/*=-1*/);
        public static native CvLSH cvCreateMemoryLSH(int d, int n, int L/*=10*/, int k/*=10*/,
                int type/*=CV_64FC1*/, double r/*=4*/, long seed/*=-1*/);
        public static native void cvReleaseLSH(CvLSH.PointerByReference lsh);
        public static native int LSHSize(CvLSH lsh);
        public static native void cvLSHAdd(CvLSH lsh, CvMat data, CvMat indices/*=null*/);
        public static native void cvLSHRemove(CvLSH lsh, CvMat indices);
        public static native void cvLSHQuery(CvLSH lsh, CvMat query_points,
                CvMat indices, CvMat dist, int k, int emax);


        public static class CvMSERParams extends Structure {
            public static boolean autoSynch = true;
            public CvMSERParams() { setAutoSynch(autoSynch); }
            public CvMSERParams(Pointer m) { super(m); setAutoSynch(autoSynch);
                    if (autoSynch && getClass() == CvMSERParams.class) read(); }

            public int delta;
            public int maxArea;
            public int minArea;
            public float maxVariation;
            public float minDiversity;
            public int maxEvolution;
            public double areaThreshold;
            public double minMargin;
            public int edgeBlurSize;

            public static class ByValue extends CvMSERParams implements Structure.ByValue {
                public ByValue() { }
                public ByValue(CvMSERParams o) {
                    this.delta = o.delta;
                    this.maxArea = o.maxArea;
                    this.minArea = o.minArea;
                    this.maxVariation = o.maxVariation;
                    this.minDiversity = o.minDiversity;
                    this.maxEvolution = o.maxEvolution;
                    this.areaThreshold = o.areaThreshold;
                    this.minMargin = o.minMargin;
                    this.edgeBlurSize = o.edgeBlurSize;
                }
            }
            public ByValue byValue() {
                return this instanceof ByValue ? (ByValue)this : new ByValue(this);
            }
        }
        public static native CvMSERParams.ByValue cvMSERParams(int delta/*=5*/, int min_area/*=60*/,
                int max_area/*=14400*/, float max_variation/*=0.25f*/, float min_diversity/*=0.2f*/,
                int max_evolution/*=200*/, double area_threshold/*=1.01*/, double min_margin/*=0.003*/,
                int edge_blur_size/*=5*/);
        public static CvMSERParams.ByValue cvMSERParams() {
            return cvMSERParams(5, 60, 14400, 0.25f, 0.2f, 200, 1.01, 0.003, 5);
        }
        public static native void cvExtractMSER(CvArr image, CvArr mask, CvSeq.PointerByReference contours,
                CvMemStorage storage, CvMSERParams.ByValue params);


        public static class CvStarKeypoint extends Structure {
            public static boolean autoSynch = true;
            public CvStarKeypoint() { setAutoSynch(autoSynch); }
            public CvStarKeypoint(Pointer m) { super(m); setAutoSynch(autoSynch);
                    if (autoSynch && getClass() == CvStarKeypoint.class) read(); }

            public CvPoint pt;
            public int size;
            public float response;

            public static class ByValue extends CvStarKeypoint implements Structure.ByValue {
                public ByValue() { }
                public ByValue(CvStarKeypoint o) {
                    this.pt = o.pt;
                    this.size = o.size;
                    this.response = o.response;
                }
            }
            public ByValue byValue() {
                return this instanceof ByValue ? (ByValue)this : new ByValue(this);
            }
        }
        public static CvStarKeypoint.ByValue cvStarKeypoint(CvPoint pt, int size, float response) {
            CvStarKeypoint.ByValue kpt = new CvStarKeypoint.ByValue();
            kpt.pt = pt;
            kpt.size = size;
            kpt.response = response;
            return kpt;
        }
        public static class CvStarDetectorParams extends Structure {
            public static boolean autoSynch = true;
            public CvStarDetectorParams() { setAutoSynch(autoSynch); }
            public CvStarDetectorParams(Pointer m) { super(m); setAutoSynch(autoSynch);
                    if (autoSynch && getClass() == CvStarDetectorParams.class) read(); }

            public int maxSize;
            public int responseThreshold;
            public int lineThresholdProjected;
            public int lineThresholdBinarized;
            public int suppressNonmaxSize;

            public static class ByValue extends CvStarDetectorParams implements Structure.ByValue {
                public ByValue() { }
                public ByValue(CvStarDetectorParams o) {
                    this.maxSize = o.maxSize;
                    this.responseThreshold = o.responseThreshold;
                    this.lineThresholdProjected = o.lineThresholdProjected;
                    this.lineThresholdBinarized = o.lineThresholdBinarized;
                    this.suppressNonmaxSize = o.suppressNonmaxSize;
                }
            }
            public ByValue byValue() {
                return this instanceof ByValue ? (ByValue)this : new ByValue(this);
            }
        }
        public static CvStarDetectorParams.ByValue cvStarDetectorParams(int maxSize/*=45*/,
                int responseThreshold/*=30*/, int lineThresholdProjected/*=10*/,
                int lineThresholdBinarized/*=8*/, int suppressNonmaxSize/*=5*/) {
            CvStarDetectorParams.ByValue params = new CvStarDetectorParams.ByValue();
            params.maxSize = maxSize;
            params.responseThreshold = responseThreshold;
            params.lineThresholdProjected = lineThresholdProjected;
            params.lineThresholdBinarized = lineThresholdBinarized;
            params.suppressNonmaxSize = suppressNonmaxSize;
            return params;
        }
        public static CvStarDetectorParams.ByValue cvStarDetectorParams() {
            return cvStarDetectorParams(45, 30, 10, 8, 5);
        }
        public static native CvSeq cvGetStarKeypoints(CvArr image, CvMemStorage storage,
            CvStarDetectorParams.ByValue params/*=cvStarDetectorParams()*/);


        public static native void cvLinearPolar(CvArr src, CvArr dst, CvPoint2D32f.ByValue center,
                double maxRadius, int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/);

        public static native void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
                CvMat camera_matrix, CvMat distortion_coeffs,
                CvMat rotation_vector, CvMat translation_vector, int use_extrinsic_guess/*=0*/);
        public static void cvFindExtrinsicCameraParams2(CvMat object_points, CvMat image_points,
                CvMat camera_matrix, CvMat distortion_coeffs,
                CvMat rotation_vector, CvMat translation_vector) {
            cvFindExtrinsicCameraParams2(object_points, image_points, camera_matrix,
                    distortion_coeffs, rotation_vector, translation_vector, 0);
        }

        public static native void cvTriangulatePoints(CvMat projMatr1, CvMat projMatr2,
                CvMat projPoints1, CvMat projPoints2, CvMat points4D);
        public static native void cvCorrectMatches(CvMat F, CvMat points1, CvMat points2,
                CvMat new_points1, CvMat new_points2);

        public static native void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage,
                CvMat Q, int handleMissingValues/*=0*/);
        public static void cvReprojectImageTo3D(CvArr disparityImage, CvArr _3dImage, CvMat Q) {
            cvReprojectImageTo3D(disparityImage, _3dImage, Q, 0);
        }
    }

    public static class v20 extends v20or21 {
        public static final String libname = Loader.load(paths, libnames);

        public static native CvConDensation cvCreateConDensation(int dynam_params, int measure_params, int sample_count);
        public static native void cvReleaseConDensation(CvConDensation.PointerByReference condens);
        public static native void cvConDensInitSampleSet(CvConDensation condens, CvMat lower_bound, CvMat upper_bound);
        public static native void cvConDensUpdateByTime(CvConDensation condens);

        public static final int CV_DOMINANT_IPAN = 1;
        public static native CvSeq cvFindDominantPoints(CvSeq contour, CvMemStorage storage,
                int method/*=CV_DOMINANT_IPAN*/, double parameter1/*=0*/,
                double parameter2/*=0*/, double parameter3/*=0*/, double parameter4/*=0*/);

        public static native double cvContourArea(CvArr contour, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/);

        public static native void cvCalcPGH(CvSeq contour, CvHistogram hist);

        public static native void cvCalcImageHomography(float[] line, CvPoint3D32f center,
                float[] intrinsic, float[] homography);
        public static native void cvCalcImageHomography(FloatBuffer line, CvPoint3D32f center,
                FloatBuffer intrinsic, FloatBuffer homography);

        public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix, CvMat distortion_coeffs);

        public static native void cvStereoRectify(CvMat camera_matrix1, CvMat camera_matrix2,
                CvMat dist_coeffs1, CvMat dist_coeffs2, CvSize.ByValue image_size, CvMat R, CvMat T,
                CvMat R1, CvMat R2, CvMat P1, CvMat P2, CvMat Q/*=null*/,
                int flags/*=CV_CALIB_ZERO_DISPARITY*/);

        public static class CvStereoBMState extends v11or20.CvStereoBMState {
            public CvStereoBMState() { releasable = false; }
            public CvStereoBMState(Pointer m) { super(m); releasable = true;
                    if (autoSynch && getClass() == CvStereoBMState.class) read(); }

            public CvMat.ByReference preFilteredImg0;
            public CvMat.ByReference preFilteredImg1;
            public CvMat.ByReference slidingSumBuf;
            public CvMat.ByReference dbmin;
            public CvMat.ByReference dbmax;
        }
    }

    public static class v21 extends v20or21 {
        public static final String libname = Loader.load(paths, libnames);

        public static class CvSubdiv2DPoint extends v20.CvSubdiv2DPoint {
            public CvSubdiv2DPoint() { }
            public CvSubdiv2DPoint(Pointer m) { super(m); 
                    if (autoSynch && getClass() == CvSubdiv2DPoint.class) read(); }

            public int id;
        }

        public static native void cvCalcOpticalFlowFarneback(CvArr prev, CvArr next,
                CvArr flow, double pyr_scale, int levels, int winsize,
                int iterations, int poly_n, double poly_sigma, int flags);

        public static native double cvContourArea(CvArr contour,
                CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/, int oriented/*=0*/);

        public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix,
                CvMat distortion_coeffs, CvMat new_camera_matrix/*=null*/);

        public static native void cvGetOptimalNewCameraMatrix(CvMat camera_matrix,
                CvMat dist_coeffs, CvSize.ByValue image_size, double alpha, CvMat new_camera_matrix,
                CvSize.ByValue new_imag_size/*=cvSize(0,0)*/, CvRect valid_pixel_ROI/*=null*/);

        public static native int cvCheckChessboard(IplImage src, CvSize.ByValue size);

        public static native void cvStereoRectify(CvMat camera_matrix1, CvMat camera_matrix2,
                CvMat dist_coeffs1, CvMat dist_coeffs2, CvSize.ByValue image_size, CvMat R, CvMat T,
                CvMat R1, CvMat R2, CvMat P1, CvMat P2, CvMat Q/*=null*/, int flags/*=CV_CALIB_ZERO_DISPARITY*/,
                double alpha/*=-1*/, CvSize.ByValue new_image_size/*=cvSize(0,0)*/,
                CvRect valid_pix_ROI1/*=null*/, CvRect valid_pix_ROI2/*=null*/);

        public static class CvStereoBMState extends v11or20.CvStereoBMState {
            public CvStereoBMState() { releasable = false; }
            public CvStereoBMState(Pointer m) { super(m); releasable = true;
                    if (autoSynch && getClass() == CvStereoBMState.class) read(); }

            public CvRect roi1, roi2;
            public int disp12MaxDiff;

            public CvMat.ByReference preFilteredImg0;
            public CvMat.ByReference preFilteredImg1;
            public CvMat.ByReference slidingSumBuf;
            public CvMat.ByReference cost;
            public CvMat.ByReference disp;
        }

        public static native CvRect.ByValue cvGetValidDisparityROI(CvRect.ByValue roi1,
                CvRect.ByValue roi2, int minDisparity, int numberOfDisparities, int SADWindowSize);
        public static native void cvValidateDisparity(CvArr disparity, CvArr cost,
                int minDisparity, int numberOfDisparities, int disp12MaxDiff/*=1*/);
    }


    public static final int
            CV_SCHARR          = -1,
            CV_MAX_SOBEL_KSIZE = 7;
    public static native void cvSobel(CvArr src, CvArr dst, int xorder, int yorder, int aperture_size/*=3*/);
    public static native void cvLaplace(CvArr src, CvArr dst, int aperture_size/*=3*/);
    public static final int CV_CANNY_L2_GRADIENT = (1 << 31);
    public static native void cvCanny(CvArr image, CvArr edges, double threshold1, double threshold2,
            int aperture_size/*=3*/);
    public static native void cvPreCornerDetect(CvArr image, CvArr corners, int aperture_size/*=3*/);
    public static native void cvCornerEigenValsAndVecs(CvArr image, CvArr eigenvv,
            int block_size, int aperture_size /*=3*/);
    public static native void cvCornerMinEigenVal(CvArr image, CvArr eigenval,
            int block_size, int aperture_size /*=3*/);
    public static native void cvCornerHarris(CvArr image, CvArr harris_responce,
            int block_size, int aperture_size/*=3*/, double k /*=0.04*/);
    public static native void cvFindCornerSubPix(CvArr image, CvPoint2D32f corners,
            int count, CvSize.ByValue win, CvSize.ByValue zero_zone,
            CvTermCriteria.ByValue criteria);
    public static void cvFindCornerSubPix(CvArr image, CvPoint2D32f[] corners,
            int count, CvSize.ByValue win, CvSize.ByValue zero_zone,
            CvTermCriteria.ByValue criteria) {
        for (Structure s : corners) { s.write(); }
        cvFindCornerSubPix(image, corners[0], count, win, zero_zone, criteria);
        for (Structure s : corners) { s.read(); }
    }
    public static native void cvGoodFeaturesToTrack(CvArr image, CvArr eig_image,
            CvArr temp_image, CvPoint2D32f corners,
            IntByReference corner_count, double quality_level,
            double  min_distance, CvArr mask/*=null*/,
            int block_size/*=3*/, int use_harris/*=0*/, double k/*=0.04*/);
    public static void cvGoodFeaturesToTrack(CvArr image, CvArr eig_image,
            CvArr temp_image, CvPoint2D32f[] corners,
            IntByReference corner_count, double quality_level,
            double  min_distance, CvArr mask/*=null*/,
            int block_size/*=3*/, int use_harris/*=0*/, double k/*=0.04*/) {
        for (Structure s : corners) { s.write(); }
        cvGoodFeaturesToTrack(image, eig_image, temp_image, corners[0],
                corner_count, quality_level, min_distance, mask, block_size, use_harris, k);
        for (Structure s : corners) { s.read(); }
    }


    public static native int cvSampleLine(CvArr image, CvPoint.ByValue pt1, CvPoint.ByValue pt2,
            Pointer buffer, int connectivity/*=8*/);
    public static native void cvGetRectSubPix(CvArr src, CvArr dst, CvPoint2D32f.ByValue center);
    public static native void cvGetQuadrangleSubPix(CvArr src, CvArr dst, CvMat map_matrix);
    public static final int
            CV_INTER_NN      = 0,
            CV_INTER_LINEAR  = 1,
            CV_INTER_CUBIC   = 2,
            CV_INTER_AREA    = 3,

            CV_WARP_FILL_OUTLIERS = 8,
            CV_WARP_INVERSE_MAP   = 16;
    public static native void cvResize(CvArr src, CvArr dst, int interpolation/*=CV_INTER_LINEAR*/);
    public static native void cvWarpAffine(CvArr src, CvArr dst, CvMat map_matrix,
            int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/,
            CvScalar.ByValue fillval/*=cvScalarAll(0)*/);
    public static void cvWarpAffine(CvArr src, CvArr dst, CvMat map_matrix) {
        cvWarpAffine(src, dst, map_matrix, CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
    }
    public static native CvMat cvGetAffineTransform(CvPoint2D32f src, CvPoint2D32f dst,
            CvMat map_matrix);
    public static CvMat cvGetAffineTransform(CvPoint2D32f[] src, CvPoint2D32f[] dst,
            CvMat map_matrix) {
        for (Structure s : src) { s.write(); } for (Structure s : dst) { s.write(); }
        return cvGetAffineTransform(src[0], dst[0], map_matrix);
    }
    public static native CvMat cv2DRotationMatrix(CvPoint2D32f.ByValue center, double angle,
            double scale, CvMat map_matrix);
    public static native void cvWarpPerspective(CvArr src, CvArr dst, CvMat map_matrix,
            int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/,
            CvScalar.ByValue fillval/*=cvScalarAll(0)*/);
    public static void cvWarpPerspective(CvArr src, CvArr dst, CvMat map_matrix) {
        cvWarpPerspective(src, dst, map_matrix, CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
    }
    public static native CvMat cvGetPerspectiveTransform(CvPoint2D32f src, CvPoint2D32f dst,
            CvMat map_matrix);
    public static CvMat cvGetPerspectiveTransform(CvPoint2D32f[] src, CvPoint2D32f[] dst,
            CvMat map_matrix) {
        for (Structure s : src) { s.write(); } for (Structure s : dst) { s.write(); }
        return cvGetPerspectiveTransform(src[0], dst[0], map_matrix);
    }
    public static native void cvRemap(CvArr src, CvArr dst, CvArr mapx, CvArr mapy,
            int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/,
            CvScalar.ByValue fillval/*=cvScalarAll(0)*/);
    public static native void cvLogPolar(CvArr src, CvArr dst, CvPoint2D32f.ByValue center,
            double M, int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/);


    public static class IplConvKernel extends Structure {
        public static boolean autoSynch = true;
        public IplConvKernel() { setAutoSynch(autoSynch); releasable = false; }
        public IplConvKernel(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == IplConvKernel.class) read(); }

        public static IplConvKernel create(int cols, int rows,
                int anchor_x, int anchor_y, int shape, int[] values/*=null*/) {
            IplConvKernel k = cvCreateStructuringElementEx(cols, rows,
                    anchor_x, anchor_y, shape, values);
            if (k != null) {
                k.releasable = true;
            }
            return k;
        }
        public void release() {
            releasable = false;
            cvReleaseStructuringElement(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public int  nCols;
        public int  nRows;
        public int  anchorX;
        public int  anchorY;
        public IntByReference values;
        public int  nShiftR;

        public static class ByReference extends IplConvKernel implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(IplConvKernel p) {
                setStructure(p);
            }
            public IplConvKernel getStructure() {
                return new IplConvKernel(getValue());
            }
            public void getStructure(IplConvKernel p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(IplConvKernel p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static final int
            CV_SHAPE_RECT     = 0,
            CV_SHAPE_CROSS    = 1,
            CV_SHAPE_ELLIPSE  = 2,
            CV_SHAPE_CUSTOM   = 100;
    public static native IplConvKernel cvCreateStructuringElementEx(int cols, int rows,
            int anchor_x, int anchor_y, int shape, int[] values/*=null*/);
    public static native void cvReleaseStructuringElement(
            IplConvKernel.PointerByReference element);
    public static native void cvErode(CvArr src, CvArr dst,
            IplConvKernel element/*=null*/, int iterations/*=1*/);
    public static native void cvDilate(CvArr src, CvArr dst,
            IplConvKernel element/*=null*/, int iterations/*=1*/);
    public static final int
            CV_MOP_ERODE       = 0,
            CV_MOP_DILATE      = 1,
            CV_MOP_OPEN        = 2,
            CV_MOP_CLOSE       = 3,
            CV_MOP_GRADIENT    = 4,
            CV_MOP_TOPHAT      = 5,
            CV_MOP_BLACKHAT    = 6;
    public static native void cvMorphologyEx(CvArr src, CvArr dst, CvArr temp,
            IplConvKernel element, int operation, int iterations/*=1*/);


    public static final int
            CV_BLUR_NO_SCALE = 0,
            CV_BLUR = 1,
            CV_GAUSSIAN = 2,
            CV_MEDIAN = 3,
            CV_BILATERAL = 4;
    public static native void cvSmooth(CvArr src, CvArr dst, int smoothtype/*=CV_GAUSSIAN*/,
            int size1/*=3*/, int size2/*=0*/, double sigma1/*=0*/, double sigma2/*=0*/);
    public static native void cvFilter2D(CvArr src, CvArr dst,
            CvMat kernel, CvPoint.ByValue anchor/*=cvPoint(-1,-1)*/);
    public static final int
            IPL_BORDER_CONSTANT  = 0,
            IPL_BORDER_REPLICATE = 1,
            IPL_BORDER_REFLECT   = 2,
            IPL_BORDER_WRAP      = 3;
    public static native void cvCopyMakeBorder(CvArr src, CvArr dst, CvPoint.ByValue offset,
            int bordertype, CvScalar.ByValue value/*=cvScalarAll(0)*/);
    public static native void cvIntegral(CvArr image, CvArr sum,
            CvArr sqsum/*=null*/, CvArr tilted_sum/*=null*/);

    public static final int
        CV_BGR2BGRA  = 0,
        CV_RGB2RGBA  = CV_BGR2BGRA,

        CV_BGRA2BGR  = 1,
        CV_RGBA2RGB  = CV_BGRA2BGR,

        CV_BGR2RGBA  = 2,
        CV_RGB2BGRA  = CV_BGR2RGBA,

        CV_RGBA2BGR  = 3,
        CV_BGRA2RGB  = CV_RGBA2BGR,

        CV_BGR2RGB   = 4,
        CV_RGB2BGR   = CV_BGR2RGB,

        CV_BGRA2RGBA = 5,
        CV_RGBA2BGRA = CV_BGRA2RGBA,

        CV_BGR2GRAY  = 6,
        CV_RGB2GRAY  = 7,
        CV_GRAY2BGR  = 8,
        CV_GRAY2RGB  = CV_GRAY2BGR,
        CV_GRAY2BGRA = 9,
        CV_GRAY2RGBA = CV_GRAY2BGRA,
        CV_BGRA2GRAY = 10,
        CV_RGBA2GRAY = 11,

        CV_BGR2BGR565 =12,
        CV_RGB2BGR565 =13,
        CV_BGR5652BGR =14,
        CV_BGR5652RGB =15,
        CV_BGRA2BGR565=16,
        CV_RGBA2BGR565=17,
        CV_BGR5652BGRA=18,
        CV_BGR5652RGBA=19,

        CV_GRAY2BGR565=20,
        CV_BGR5652GRAY=21,

        CV_BGR2BGR555 =22,
        CV_RGB2BGR555 =23,
        CV_BGR5552BGR =24,
        CV_BGR5552RGB =25,
        CV_BGRA2BGR555=26,
        CV_RGBA2BGR555=27,
        CV_BGR5552BGRA=28,
        CV_BGR5552RGBA=29,

        CV_GRAY2BGR555=30,
        CV_BGR5552GRAY=31,

        CV_BGR2XYZ   = 32,
        CV_RGB2XYZ   = 33,
        CV_XYZ2BGR   = 34,
        CV_XYZ2RGB   = 35,

        CV_BGR2YCrCb = 36,
        CV_RGB2YCrCb = 37,
        CV_YCrCb2BGR = 38,
        CV_YCrCb2RGB = 39,

        CV_BGR2HSV   = 40,
        CV_RGB2HSV   = 41,

        CV_BGR2Lab   = 44,
        CV_RGB2Lab   = 45,

        CV_BayerBG2BGR=46,
        CV_BayerGB2BGR=47,
        CV_BayerRG2BGR=48,
        CV_BayerGR2BGR=49,

        CV_BayerBG2RGB=CV_BayerRG2BGR,
        CV_BayerGB2RGB=CV_BayerGR2BGR,
        CV_BayerRG2RGB=CV_BayerBG2BGR,
        CV_BayerGR2RGB=CV_BayerGB2BGR,

        CV_BGR2Luv   = 50,
        CV_RGB2Luv   = 51,
        CV_BGR2HLS   = 52,
        CV_RGB2HLS   = 53,

        CV_HSV2BGR   = 54,
        CV_HSV2RGB   = 55,

        CV_Lab2BGR   = 56,
        CV_Lab2RGB   = 57,
        CV_Luv2BGR   = 58,
        CV_Luv2RGB   = 59,
        CV_HLS2BGR   = 60,
        CV_HLS2RGB   = 61,

        CV_COLORCVT_MAX = 100;
    public static native void cvCvtColor(CvArr src, CvArr dst, int code);

    public static final int
            CV_THRESH_BINARY     = 0,
            CV_THRESH_BINARY_INV = 1,
            CV_THRESH_TRUNC      = 2,
            CV_THRESH_TOZERO     = 3,
            CV_THRESH_TOZERO_INV = 4,
            CV_THRESH_MASK       = 7,

            CV_THRESH_OTSU       = 8;
    public static native double cvThreshold(CvArr src, CvArr dst, double threshold,
            double max_value, int threshold_type);

    public static final int
            CV_ADAPTIVE_THRESH_MEAN_C     = 0,
            CV_ADAPTIVE_THRESH_GAUSSIAN_C = 1;
    public static native void cvAdaptiveThreshold(CvArr src, CvArr dst, double max_value,
            int adaptive_method/*=CV_ADAPTIVE_THRESH_MEAN_C*/,
            int threshold_type/*=CV_THRESH_BINARY*/,
            int block_size/*=3*/, double param1/*=5*/);


    public static final int CV_GAUSSIAN_5x5 = 7;
    public static native void cvPyrDown(CvArr src, CvArr dst, int filter/*=CV_GAUSSIAN_5x5*/);
    public static native void cvPyrUp(CvArr src, CvArr dst, int filter/*=CV_GAUSSIAN_5x5*/);


    public static class CvConnectedComp extends Structure {
        public static boolean autoSynch = true;
        public CvConnectedComp() { setAutoSynch(autoSynch); }
        public CvConnectedComp(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvConnectedComp.class) read(); }

        public double area;
        public CvScalar value;
        public CvRect rect;
        // OpenCV usually returns garbage in the contour field...
        public Pointer /* CvSeq.ByReference */ contour;
    }

    public static final int
            CV_FLOODFILL_FIXED_RANGE = (1 << 16),
            CV_FLOODFILL_MASK_ONLY   = (1 << 17);
    public static native void cvFloodFill(CvArr image, CvPoint.ByValue seed_point, CvScalar.ByValue new_val,
            CvScalar.ByValue lo_diff/*=cvScalarAll(0)*/, CvScalar.ByValue up_diff/*=cvScalarAll(0)*/,
            CvConnectedComp comp/*=null*/, int flags/*=4*/, CvArr mask/*=null*/);

    public static class CvContourScanner extends PointerType {
        public CvContourScanner() { }
        public CvContourScanner(Pointer p) { super(p); }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvContourScanner p) {
                setStructure(p);
            }
            public CvContourScanner getStructure() {
                return new CvContourScanner(getValue());
            }
            public void getStructure(CvContourScanner p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvContourScanner p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static final int
            CV_RETR_EXTERNAL = 0,
            CV_RETR_LIST     = 1,
            CV_RETR_CCOMP    = 2,
            CV_RETR_TREE     = 3,

            CV_CHAIN_CODE              = 0,
            CV_CHAIN_APPROX_NONE       = 1,
            CV_CHAIN_APPROX_SIMPLE     = 2,
            CV_CHAIN_APPROX_TC89_L1    = 3,
            CV_CHAIN_APPROX_TC89_KCOS  = 4,
            CV_LINK_RUNS               = 5;

    public static class CvChainPtReader extends CvSeqReader {
        public static boolean autoSynch = true;
        public CvChainPtReader() { setAutoSynch(autoSynch); }
        public CvChainPtReader(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvChainPtReader.class) read(); }

        public char      code;
        public CvPoint   pt;
        //public byte[][]    deltas = new byte[8][2];
        public byte[]    deltas = new byte[8*2];
    }

    public static class CvContourTree extends CvSeq {
        public static boolean autoSynch = true;
        public CvContourTree() { setAutoSynch(autoSynch); }
        public CvContourTree(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvContourTree.class) read(); }

        public CvPoint p1;
        public CvPoint p2;

        public static class ByValue extends CvContourTree implements Structure.ByValue { }
    }

    public static class CvChain extends CvSeq {
        public static boolean autoSynch = true;
        public CvChain() { setAutoSynch(autoSynch); }
        public CvChain(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvChain.class) read(); }

        public CvPoint origin;

        public static class ByValue extends CvChain implements Structure.ByValue { }
    }

    public static class CvContour extends CvSeq {
        public static boolean autoSynch = true;
        public CvContour() { setAutoSynch(autoSynch); }
        public CvContour(Pointer m) { super(m); setAutoSynch(autoSynch); 
                if (autoSynch && getClass() == CvContour.class) read(); }

        public CvRect rect;
        public int color;
        public int reserved0, reserved1, reserved2;

        public static class ByValue extends CvContour implements Structure.ByValue { }
    }

    public static class CvPoint2DSeq extends CvContour { }

    public static native int cvFindContours(CvArr image, CvMemStorage storage, CvSeq.PointerByReference first_contour,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/,
            int method/*=CV_CHAIN_APPROX_SIMPLE*/, CvPoint.ByValue offset/*=cvPoint(0,0)*/);
    public static int cvFindContours(CvArr image, CvMemStorage storage, CvSeq.PointerByReference first_contour,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/, int method/*=CV_CHAIN_APPROX_SIMPLE*/) {
        return cvFindContours(image, storage, first_contour, header_size, mode, method, CvPoint.ZERO);
    }
    public static native CvContourScanner cvStartFindContours(CvArr image, CvMemStorage storage,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/,
            int method/*=CV_CHAIN_APPROX_SIMPLE*/, CvPoint.ByValue offset/*=cvPoint(0,0)*/);
    public static CvContourScanner cvStartFindContours(CvArr image, CvMemStorage storage,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/, int method/*=CV_CHAIN_APPROX_SIMPLE*/) {
        return cvStartFindContours(image, storage, header_size, mode, method, CvPoint.ZERO);
    }
    public static native CvSeq cvFindNextContour(CvContourScanner scanner);
    public static native void cvSubstituteContour(CvContourScanner scanner, CvSeq new_contour);
    public static native CvSeq cvEndFindContours(CvContourScanner.PointerByReference scanner);

    public static native void cvPyrSegmentation(IplImage src, IplImage dst, CvMemStorage storage,
            CvSeq.PointerByReference comp, int level, double threshold1, double threshold2);
    public static native void cvPyrMeanShiftFiltering(CvArr src, CvArr dst, double sp, double sr,
            int max_level/*=1*/,  CvTermCriteria termcrit
            /*=cvTermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS,5,1)*/);
    public static native void cvWatershed(CvArr image, CvArr markers);


    public static class CvMoments extends Structure {
        public static boolean autoSynch = true;
        public CvMoments() { setAutoSynch(autoSynch); }
        public CvMoments(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvMoments.class) read(); }

        public double m00, m10, m01, m20, m11, m02, m30, m21, m12, m03; 
        public double mu20, mu11, mu02, mu30, mu21, mu12, mu03; 
        public double inv_sqrt_m00; 
    }

    public static class CvHuMoments extends Structure {
        public static boolean autoSynch = true;
        public CvHuMoments() { setAutoSynch(autoSynch); }
        public CvHuMoments(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvHuMoments.class) read(); }

        public double hu1, hu2, hu3, hu4, hu5, hu6, hu7;
    }

    public static native void cvMoments(CvArr arr, CvMoments moments, int binary/*=0*/);
    public static native double cvGetSpatialMoment(CvMoments moments, int x_order, int y_order);
    public static native double cvGetCentralMoment(CvMoments moments, int x_order, int y_order);
    public static native double cvGetNormalizedCentralMoment(CvMoments moments, int x_order, int y_order);
    public static native void cvGetHuMoments(CvMoments moments, CvHuMoments hu_moments);


    public static final int
            CV_HOUGH_STANDARD = 0,
            CV_HOUGH_PROBABILISTIC = 1,
            CV_HOUGH_MULTI_SCALE = 2,
            CV_HOUGH_GRADIENT = 3;
    public static native CvSeq cvHoughLines2(CvArr image, Pointer line_storage, int method,
            double rho, double theta, int threshold,
            double param1/*=0*/, double param2/*=0*/);
    public static native CvSeq cvHoughCircles(CvArr image, Pointer circle_storage, int method,
            double dp, double min_dist, double param1/*=100*/,
            double param2/*=100*/, int min_radius/*=0*/, int max_radius/*=0*/);

    public static final int
            CV_DIST_USER   = -1,
            CV_DIST_L1     = 1,
            CV_DIST_L2     = 2,
            CV_DIST_C      = 3,
            CV_DIST_L12    = 4,
            CV_DIST_FAIR   = 5,
            CV_DIST_WELSCH = 6,
            CV_DIST_HUBER  = 7,

            CV_DIST_MASK_3 =  3,
            CV_DIST_MASK_5 =  5,
            CV_DIST_MASK_PRECISE = 0;
    public static native void cvDistTransform(CvArr src, CvArr dst, int distance_type/*=CV_DIST_L2*/,
            int mask_size/*=3*/, FloatByReference mask/*=null*/, CvArr labels/*=null*/);

    public static final int
            CV_INPAINT_NS      = 0,
            CV_INPAINT_TELEA   = 1;
    public static native void cvInpaint(CvArr src, CvArr mask, CvArr dst, double inpaintRange, int flags);


    public static final int
            CV_HIST_MAGIC_VAL     = 0x42450000,
            CV_HIST_UNIFORM_FLAG  = (1 << 10),

            CV_HIST_RANGES_FLAG   = (1 << 11),

            CV_HIST_ARRAY         = 0,
            CV_HIST_SPARSE        = 1,
            CV_HIST_TREE          = CV_HIST_SPARSE,

            CV_HIST_UNIFORM       = 1;


    public static class FloatPointerByReference extends com.sun.jna.ptr.ByReference {
        public FloatPointerByReference() { super(Pointer.SIZE); }
        public FloatPointerByReference(float[][] floatArrayArray) { 
            super(floatArrayArray.length*Pointer.SIZE); set(floatArrayArray); }

        private Memory memoryArray[];

        public float[][] get() {
            Memory memory = (Memory)getPointer();
            if (memory == null) {
                return null;
            }
            float[][] floatArrayArray = new float[(int)(memory.size()/Pointer.SIZE)][];
            for (int i = 0; i < floatArrayArray.length; i++) {
                if (memoryArray[i] != null) {
                    floatArrayArray[i] = memoryArray[i].getFloatArray(0, (int)(memoryArray[i].size()*8/Float.SIZE));
                }
            }
            return floatArrayArray;
        }
        public void set(float[][] floatArrayArray) {
            if (floatArrayArray == null) {
                setPointer(null);
                memoryArray = null;
                return;
            }
            Memory memory = (Memory)getPointer();
            long s = floatArrayArray.length*Pointer.SIZE;
            if (memory == null || memory.size() != s) {
                memory = new Memory(s);
                setPointer(memory);
            }
            memoryArray = new Memory[floatArrayArray.length];
            for (int i = 0; i < floatArrayArray.length; i++) {
                if (floatArrayArray[i] == null || floatArrayArray[i].length == 0) {
                    memoryArray[i] = null;
                } else {
                    s = floatArrayArray[i].length*Float.SIZE/8;
                    if (memoryArray[i] == null || memoryArray[i].size() != s) {
                        memoryArray[i] = new Memory(s);
                    }
                    memoryArray[i].write(0, floatArrayArray[i], 0, floatArrayArray[i].length);
                }
                memory.setPointer(Pointer.SIZE*i, memoryArray[i]);
            }
        }
    }


    public static class CvHistogram extends Structure {
        public static boolean autoSynch = true;
        public CvHistogram() { setAutoSynch(autoSynch); releasable = false; }
        public CvHistogram(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvHistogram.class) read(); }

        public static CvHistogram create(int dims, int[] sizes, int type,
                float[][] ranges/*=null*/, int uniform/*=1*/) {
            CvHistogram h = cvCreateHist(dims, sizes, type, ranges, uniform);
            if (h != null) {
                h.releasable = true;
            }
            return h;
        }
        public void release() {
            releasable = false;
            cvReleaseHist(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;


        public int /* CvHistType */    type;
        public CvArr.ByReference       bins;
        //public float[][]               thresh = new float[CV_MAX_DIM][2];
        public float[]                 thresh = new float[CV_MAX_DIM*2];
        public FloatPointerByReference thresh2;
        public CvMatND mat;


        public static class ByReference extends CvHistogram implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvHistogram p) {
                setStructure(p);
            }
            public CvHistogram getStructure() {
                return new CvHistogram(getValue());
            }
            public void getStructure(CvHistogram p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvHistogram p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static boolean CV_IS_HIST(CvHistogram hist) {
        return hist != null && (hist.type & CV_MAGIC_MASK) == CV_HIST_MAGIC_VAL && hist.bins != null;
    }
    public static boolean CV_IS_UNIFORM_HIST(CvHistogram hist) {
        return (hist.type & CV_HIST_UNIFORM_FLAG) != 0;
    }
    public static boolean CV_IS_SPARSE_HIST(CvHistogram hist) {
        return CV_IS_SPARSE_MAT(new CvSparseMat(hist.bins.getPointer()));
    }
    public static boolean CV_HIST_HAS_RANGES(CvHistogram hist) {
        return (hist.type & CV_HIST_RANGES_FLAG) != 0;
    }

    public static float cvQueryHistValue_1D(CvHistogram hist, int idx0) {
        return (float)cvGetReal1D(hist.bins, idx0);
    }
    public static float cvQueryHistValue_2D(CvHistogram hist, int idx0, int idx1) {
        return (float)cvGetReal2D(hist.bins, idx0, idx1);
    }
    public static float cvQueryHistValue_3D(CvHistogram hist, int idx0, int idx1, int idx2) {
        return (float)cvGetReal3D(hist.bins, idx0, idx1, idx2);
    }
    public static float cvQueryHistValue_nD(CvHistogram hist, int idx0, int[] idx) {
        return (float)cvGetRealND(hist.bins, idx);
    }

    public static Pointer cvGetHistValue_1D(CvHistogram hist, int idx0) {
        return cvPtr1D(hist.bins, idx0, null);
    }
    public static Pointer cvGetHistValue_2D(CvHistogram hist, int idx0, int idx1) {
        return cvPtr2D(hist.bins, idx0, idx1, null);
    }
    public static Pointer cvGetHistValue_3D(CvHistogram hist, int idx0, int idx1, int idx2) {
        return cvPtr3D(hist.bins, idx0, idx1, idx2, null);
    }
    public static Pointer cvGetHistValue_nD(CvHistogram hist, int idx0, int[] idx) {
        return cvPtrND(hist.bins, idx, null, 1, null);
    }

    public static native CvHistogram cvCreateHist(int dims, int[] sizes, int type,
            FloatPointerByReference ranges/*=null*/, int uniform/*=1*/);
    public static CvHistogram cvCreateHist(int dims, int[] sizes, int type,
            float[][] ranges/*=null*/, int uniform/*=1*/) {
        return cvCreateHist(dims, sizes, type, new FloatPointerByReference(ranges), uniform);
    }
    public static native void cvSetHistBinRanges(CvHistogram hist,
            FloatPointerByReference ranges, int uniform/*=1*/);
    public static void cvSetHistBinRanges(CvHistogram hist,
            float[][] ranges, int uniform/*=1*/) {
        cvSetHistBinRanges(hist, new FloatPointerByReference(ranges), uniform);
    }
    public static native void cvReleaseHist(CvHistogram.PointerByReference hist);
    public static native void cvClearHist(CvHistogram hist);
    public static native CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            float[] data, FloatPointerByReference ranges/*=null*/, int uniform/*=1*/);
    public static CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            float[] data, float[][] ranges/*=null*/, int uniform/*=1*/) {
        return cvMakeHistHeaderForArray(dims, sizes, hist, data, new FloatPointerByReference(ranges), uniform);
    }
    public static native CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            FloatBuffer data, FloatPointerByReference ranges/*=null*/, int uniform/*=1*/);
    public static CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            FloatBuffer data, float[][] ranges/*=null*/, int uniform/*=1*/) {
        return cvMakeHistHeaderForArray(dims, sizes, hist, data, new FloatPointerByReference(ranges), uniform);
    }
    public static native void cvGetMinMaxHistValue(CvHistogram hist,
            FloatByReference min_value, FloatByReference max_value,
            IntByReference min_idx/*=null*/, IntByReference max_idx/*=null*/);
    public static native void cvNormalizeHist(CvHistogram hist, double factor);
    public static native void cvThreshHist(CvHistogram hist, double threshold);
    public static final int
            CV_COMP_CORREL        = 0,
            CV_COMP_CHISQR        = 1,
            CV_COMP_INTERSECT     = 2,
            CV_COMP_BHATTACHARYYA = 3;
    public static native double cvCompareHist(CvHistogram hist1, CvHistogram hist2, int method);
    public static native void cvCopyHist(CvHistogram src, CvHistogram.PointerByReference dst);
    public static native void cvCalcBayesianProb(CvHistogram src, int number, CvHistogram dst);
    public static void cvCalcBayesianProb(CvHistogram[] src, int number, CvHistogram[] dst) {
        for (Structure s : src) { s.write(); } for (Structure s : dst) { s.write(); }
        cvCalcBayesianProb(src[0], number, dst[0]);
        for (Structure s : src) { s.read();  } for (Structure s : dst) { s.read();  }
    }
    public static native void cvCalcArrHist(CvArr.PointerByReference arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/);
    public static void cvCalcArrHist(CvArr[] arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(new CvArr.PointerByReference(arr), hist, accumulate, mask);
    }
    public static void cvCalcHist(IplImage.PointerByReference arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(arr, hist, accumulate, mask);
    }
    public static void cvCalcHist(IplImage[] arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(new IplImage.PointerByReference(arr), hist, accumulate, mask);
    }
    public static native void cvCalcArrBackProject(CvArr.PointerByReference image, CvArr dst, CvHistogram hist);
    public static void cvCalcArrBackProject(CvArr[] image, CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(new CvArr.PointerByReference(image), dst, hist);
    }
    public static void cvCalcBackProject(IplImage.PointerByReference image, CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(image, dst, hist);
    }
    public static void cvCalcBackProject(IplImage[] image, CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(new IplImage.PointerByReference(image), dst, hist);
    }
    public static native void cvCalcArrBackProjectPatch(CvArr.PointerByReference image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor);
    public static void cvCalcArrBackProjectPatch(CvArr[] image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(new CvArr.PointerByReference(image), dst, range, hist, method, factor);
    }
    public static void cvCalcBackProjectPatch(IplImage.PointerByReference image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(image, dst, range, hist, method, factor);
    }
    public static void cvCalcBackProjectPatch(IplImage[] image, CvArr dst, CvSize.ByValue range,
            CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(new IplImage.PointerByReference(image), dst, range, hist, method, factor);
    }
    public static native void cvCalcProbDensity(CvHistogram hist1, CvHistogram hist2,
            CvHistogram dst_hist, double scale/*=255*/);
    public static native void cvEqualizeHist(CvArr src, CvArr dst);


    public static final int
            CV_TM_SQDIFF        = 0,
            CV_TM_SQDIFF_NORMED = 1,
            CV_TM_CCORR         = 2,
            CV_TM_CCORR_NORMED  = 3,
            CV_TM_CCOEFF        = 4,
            CV_TM_CCOEFF_NORMED = 5;
    public static native void cvMatchTemplate(CvArr image, CvArr templ, CvArr result, int method);

    public static final int
            CV_CONTOURS_MATCH_I1 = 1,
            CV_CONTOURS_MATCH_I2 = 2,
            CV_CONTOURS_MATCH_I3 = 3;
    public static native double cvMatchShapes(Pointer object1, Pointer object2, int method, double parameter/*=0*/);

    public interface CvDistanceFunction extends Callback {
        float callback(FloatByReference a, FloatByReference b, Pointer user_param);
    }
    public static native float cvCalcEMD2(CvArr signature1, CvArr signature2, int distance_type,
            CvDistanceFunction distance_func/*=null*/, CvArr cost_matrix/*=null*/,
            CvArr flow/*=null*/, FloatByReference lower_bound/*=null*/, Pointer userdata/*=null*/);
    public static native float cvCalcEMD2(CvArr signature1, CvArr signature2, int distance_type,
            Function distance_func/*=null*/, CvArr cost_matrix/*=null*/,
            CvArr flow/*=null*/, FloatByReference lower_bound/*=null*/, Pointer userdata/*=null*/);


    public static native CvSeq cvApproxChains(CvSeq src_seq, CvMemStorage storage, int method/*=CV_CHAIN_APPROX_SIMPLE*/,
            double parameter/*=0*/, int minimal_perimeter/*=0*/, int recursive/*=0*/);
    public static native void cvStartReadChainPoints(CvChain chain, CvChainPtReader reader);
    public static native CvPoint.ByValue cvReadChainPoint(CvChainPtReader reader);
    public static final int CV_POLY_APPROX_DP = 0;
    public static native CvSeq cvApproxPoly(Pointer src_seq, int header_size, CvMemStorage storage,
            int method/*=CV_POLY_APPROX_DP*/, double parameter, int parameter2/*=0*/);
    public static native CvRect.ByValue cvBoundingRect(CvArr points, int update/*=0*/);
//    public static native double cvContourArea(CvArr contour, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/);
    public static native double cvArcLength(Pointer curve, CvSlice.ByValue slice/*=CV_WHOLE_SEQ*/,
            int is_closed/*=-1*/);
    public static double cvContourPerimeter(Pointer contour) {
        return cvArcLength(contour, CV_WHOLE_SEQ, 1);
    }
    public static native CvContourTree cvCreateContourTree(CvSeq contour, CvMemStorage storage, double threshold);
    public static native CvSeq cvContourFromContourTree(CvContourTree tree,
            CvMemStorage storage, CvTermCriteria.ByValue criteria);
    public static final int CV_CONTOUR_TREES_MATCH_I1 = 1;
    public static native double cvMatchContourTrees(CvContourTree tree1, CvContourTree tree2,
            int method/*=CV_CONTOUR_TREES_MATCH_I1*/, double threshold);


    public static native CvRect.ByValue cvMaxRect(CvRect rect1, CvRect rect2);
    public static native CvSeq cvPointSeqFromMat(int seq_kind, CvArr mat, CvContour contour_header, CvSeqBlock block);
    public static native void cvBoxPoints(CvBox2D.ByValue box, CvPoint2D32f pt/*[4]*/);
    public static void cvBoxPoints(CvBox2D.ByValue box, CvPoint2D32f[] pt/*[4]*/) {
        cvBoxPoints(box, pt[0]);
        for (Structure s : pt) { s.read(); }
    }
    public static native CvBox2D.ByValue cvFitEllipse2(CvArr points);
    public static native void cvFitLine(CvArr points, int dist_type, double param,
            double reps, double aeps, float[] line);
    public static native void cvFitLine(CvArr points, int dist_type, double param,
            double reps, double aeps, FloatBuffer line);
    public static final int
            CV_CLOCKWISE         = 1,
            CV_COUNTER_CLOCKWISE = 2;
    public static native CvSeq cvConvexHull2(CvArr input, Pointer hull_storage/*=null*/,
            int orientation/*=CV_CLOCKWISE*/, int return_points/*=0*/);
    public static native int cvCheckContourConvexity(CvArr contour);
    public static class CvConvexityDefect extends Structure {
        public static boolean autoSynch = true;
        public CvConvexityDefect() { setAutoSynch(autoSynch); }
        public CvConvexityDefect(Pointer m) { super(m); setAutoSynch(autoSynch); 
                if (autoSynch && getClass() == CvConvexityDefect.class) read(); }

        public CvPoint.ByReference start;
        public CvPoint.ByReference end;
        public CvPoint.ByReference depth_point;
        public float depth;
    }
    public static native CvSeq cvConvexityDefects(CvArr contour, CvArr convexhull, CvMemStorage storage/*=null*/);
    public static native double cvPointPolygonTest(CvArr contour, CvPoint2D32f.ByValue pt, int measure_dist);
    public static native CvBox2D.ByValue cvMinAreaRect2(CvArr points, CvMemStorage storage/*=null*/);
    public static native int cvMinEnclosingCircle(CvArr points, CvPoint2D32f center, FloatByReference radius);


    //typedef size_t CvSubdiv2DEdge;
    public static class CvSubdiv2DEdge extends NativeLong {
        public CvSubdiv2DEdge() { }
        public CvSubdiv2DEdge(long value) {
            super(value);
        }
    }

    public static final int CV_SUBDIV2D_VIRTUAL_POINT_FLAG = (1 << 30);

    public static class CvQuadEdge2D extends Structure {
        public static boolean autoSynch = true;
        public CvQuadEdge2D() { setAutoSynch(autoSynch); }
        public CvQuadEdge2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvQuadEdge2D.class) read(); }

        public int flags;
        public CvSubdiv2DPoint.ByReference[] pt = new CvSubdiv2DPoint.ByReference[4];
        public CvSubdiv2DEdge[] next = new CvSubdiv2DEdge[4];

        public CvSubdiv2DEdge CV_SUBDIV2D_NEXT_EDGE(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return next[i&3];
        }
        public CvSubdiv2DEdge cvSubdiv2DNextEdge(CvSubdiv2DEdge edge) {
            return CV_SUBDIV2D_NEXT_EDGE(edge);
        }
        public CvSubdiv2DEdge cvSubdiv2DGetEdge(CvSubdiv2DEdge edge, int /* CvNextEdgeType */ type) {
            int i = next[(edge.intValue() + type) & 3].intValue();
            return new CvSubdiv2DEdge((i & ~3) + ((i + (type >> 4)) & 3));
        }
        public static CvSubdiv2DEdge  cvSubdiv2DRotateEdge(CvSubdiv2DEdge edge, int rotate) {
            int i = edge.intValue();
            return new CvSubdiv2DEdge((i & ~3) + ((i + rotate) & 3));
        }
        public CvSubdiv2DPoint.ByReference cvSubdiv2DEdgeOrg(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return pt[i & 3];
        }
        public CvSubdiv2DPoint.ByReference cvSubdiv2DEdgeDst(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return pt[(i + 2) & 3];
        }
        public static CvSubdiv2DEdge cvSubdiv2DSymEdge(CvSubdiv2DEdge edge) {
            int i = edge.intValue();
            return new CvSubdiv2DEdge(i ^ 2);
        }
    }

    public static class CvSubdiv2DPoint extends Structure {
        public static boolean autoSynch = true;
        public CvSubdiv2DPoint() { setAutoSynch(autoSynch); }
        public CvSubdiv2DPoint(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSubdiv2DPoint.class) read(); }

        public int            flags;
        public CvSubdiv2DEdge first;
        public CvPoint2D32f   pt;

        public static class ByReference extends CvSubdiv2DPoint implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvSubdiv2DPoint p) {
                setStructure(p);
            }
            public CvSubdiv2DPoint getStructure() {
                return new CvSubdiv2DPoint(getValue());
            }
            public void getStructure(CvSubdiv2DPoint p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvSubdiv2DPoint p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvSubdiv2D extends CvGraph {
        public static boolean autoSynch = true;
        public CvSubdiv2D() { setAutoSynch(autoSynch); }
        public CvSubdiv2D(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvSubdiv2D.class) read(); }

        public int  quad_edges;
        public int  is_geometry_valid;
        public CvSubdiv2DEdge recent_edge;
        public CvPoint2D32f  topleft;
        public CvPoint2D32f  bottomright;

        public static class ByReference extends CvSubdiv2D implements Structure.ByReference { }
    }

    //enum CvSubdiv2DPointLocation
    final int
        CV_PTLOC_ERROR = -2,
        CV_PTLOC_OUTSIDE_RECT = -1,
        CV_PTLOC_INSIDE = 0,
        CV_PTLOC_VERTEX = 1,
        CV_PTLOC_ON_EDGE = 2;

    //enum CvNextEdgeType
    final int
        CV_NEXT_AROUND_ORG   = 0x00,
        CV_NEXT_AROUND_DST   = 0x22,
        CV_PREV_AROUND_ORG   = 0x11,
        CV_PREV_AROUND_DST   = 0x33,
        CV_NEXT_AROUND_LEFT  = 0x13,
        CV_NEXT_AROUND_RIGHT = 0x31,
        CV_PREV_AROUND_LEFT  = 0x20,
        CV_PREV_AROUND_RIGHT = 0x02;

    public static double cvTriangleArea(CvPoint2D32f a, CvPoint2D32f b, CvPoint2D32f c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public static CvSubdiv2D cvCreateSubdivDelaunay2D(CvRect.ByValue rect, CvMemStorage storage)  {
        CvSubdiv2D subdiv = cvCreateSubdiv2D(CV_SEQ_KIND_SUBDIV2D, new CvSubdiv2D().size(),
                             new CvSubdiv2DPoint().size(), new CvQuadEdge2D().size(), storage);
        cvInitSubdivDelaunay2D(subdiv, rect);
        return subdiv;
    }
    public static native void cvInitSubdivDelaunay2D(CvSubdiv2D subdiv, CvRect.ByValue rect);
    public static native CvSubdiv2D cvCreateSubdiv2D(int subdiv_type, int header_size,
            int vtx_size, int quadedge_size, CvMemStorage storage);
    public static native CvSubdiv2DPoint cvSubdivDelaunay2DInsert(CvSubdiv2D subdiv, CvPoint2D32f.ByValue pt);
    public static native int /* CvSubdiv2DPointLocation */ cvSubdiv2DLocate(CvSubdiv2D subdiv, 
            CvPoint2D32f.ByValue pt, CvSubdiv2DEdge edge, CvSubdiv2DPoint.PointerByReference vertex/*=null*/);
    public static native CvSubdiv2DPoint cvFindNearestPoint2D(CvSubdiv2D subdiv, CvPoint2D32f.ByValue pt);
    public static native void cvCalcSubdivVoronoi2D(CvSubdiv2D subdiv);
    public static native void cvClearSubdivVoronoi2D(CvSubdiv2D subdiv);


    public static native void cvAcc(CvArr image, CvArr sum, CvArr mask/*=null*/);
    public static native void cvSquareAcc(CvArr image, CvArr sqsum, CvArr mask/*=null*/);
    public static native void cvMultiplyAcc(CvArr image1, CvArr image2, CvArr acc, CvArr mask/*=null*/);
    public static native void cvRunningAvg(CvArr image, CvArr acc, double alpha, CvArr mask/*=null*/);


    public static native void cvUpdateMotionHistory(CvArr silhouette, CvArr mhi, double timestamp, double duration);
    public static native void cvCalcMotionGradient(CvArr mhi, CvArr mask, CvArr orientation,
            double delta1, double delta2, int aperture_size/*=3*/);
    public static native double cvCalcGlobalOrientation(CvArr orientation, CvArr mask, CvArr mhi,
            double timestamp, double duration);
    public static native CvSeq cvSegmentMotion(CvArr mhi, CvArr seg_mask, CvMemStorage storage,
            double timestamp, double seg_thresh);


    public static native int cvMeanShift(CvArr prob_image, CvRect.ByValue window,
            CvTermCriteria.ByValue criteria, CvConnectedComp comp);
    public static native int cvCamShift(CvArr prob_image, CvRect.ByValue window,
            CvTermCriteria.ByValue criteria, CvConnectedComp comp, CvBox2D box/*=null*/);
    public static final int
            CV_VALUE = 1,
            CV_ARRAY = 2;
    public static native void cvSnakeImage(IplImage image, CvPoint points, int length, float[] alpha,
            float[] beta, float[] gamma, int coeff_usage, CvSize.ByValue win,
            CvTermCriteria.ByValue criteria, int calc_gradient/*=1*/);
    public static void cvSnakeImage(IplImage image, CvPoint[] points, int length, float[] alpha,
            float[] beta, float[] gamma, int coeff_usage, CvSize.ByValue win,
            CvTermCriteria.ByValue criteria, int calc_gradient/*=1*/) {
        for (Structure s : points) { s.write(); }
        cvSnakeImage(image, points[0], length, alpha,
                beta, gamma, coeff_usage, win, criteria, calc_gradient);
        for (Structure s : points) { s.read(); }
    }
    public static native void cvSnakeImage(IplImage image, CvPoint points, int length, FloatBuffer alpha,
            FloatBuffer beta, FloatBuffer gamma, int coeff_usage, CvSize.ByValue win,
            CvTermCriteria.ByValue criteria, int calc_gradient/*=1*/);
    public static void cvSnakeImage(IplImage image, CvPoint[] points, int length, FloatBuffer alpha,
            FloatBuffer beta, FloatBuffer gamma, int coeff_usage, CvSize.ByValue win,
            CvTermCriteria.ByValue criteria, int calc_gradient/*=1*/) {
        for (Structure s : points) { s.write(); }
        cvSnakeImage(image, points[0], length, alpha,
                beta, gamma, coeff_usage, win, criteria, calc_gradient);
        for (Structure s : points) { s.read(); }
    }


    public static native void cvCalcOpticalFlowHS(CvArr prev, CvArr curr, int use_previous, CvArr velx,
            CvArr vely, double lambda, CvTermCriteria.ByValue criteria);
    public static native void cvCalcOpticalFlowLK(CvArr prev, CvArr curr, CvSize.ByValue win_size, CvArr velx, CvArr vely);
    public static native void cvCalcOpticalFlowBM(CvArr prev, CvArr curr, CvSize.ByValue block_size, CvSize.ByValue shift_size,
            CvSize.ByValue max_range, int use_previous, CvArr velx, CvArr vely);
    public static final int
            CV_LKFLOW_PYR_A_READY       = 1,
            CV_LKFLOW_PYR_B_READY       = 2,
            CV_LKFLOW_INITIAL_GUESSES   = 4,
            CV_LKFLOW_GET_MIN_EIGENVALS = 8;
    public static native void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            int count, CvSize.ByValue win_size, int level, byte[] status,
            float[] track_error, CvTermCriteria.ByValue criteria, int flags);
    public static void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr,  CvPoint2D32f[] prev_features, CvPoint2D32f[] curr_features,
            int count, CvSize.ByValue win_size, int level, byte[] status,
            float[] track_error, CvTermCriteria.ByValue criteria, int flags) {
        for (Structure s : prev_features) { s.write(); }
        cvCalcOpticalFlowPyrLK(prev, curr, prev_pyr, curr_pyr, prev_features[0],
                curr_features[0], count, win_size, level, status,
                track_error, criteria, flags);
        for (Structure s : curr_features) { s.read(); }
    }
    public static native void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr, CvPoint2D32f prev_features, CvPoint2D32f curr_features,
            int count, CvSize.ByValue win_size, int level, byte[] status,
            FloatBuffer track_error, CvTermCriteria.ByValue criteria, int flags);
    public static void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
            CvArr curr_pyr,  CvPoint2D32f[] prev_features, CvPoint2D32f[] curr_features,
            int count, CvSize.ByValue win_size, int level, byte[] status,
            FloatBuffer track_error, CvTermCriteria.ByValue criteria, int flags) {
        for (Structure s : prev_features) { s.write(); }
        cvCalcOpticalFlowPyrLK(prev, curr, prev_pyr, curr_pyr, prev_features[0],
                curr_features[0], count, win_size, level, status,
                track_error, criteria, flags);
        for (Structure s : curr_features) { s.read(); }
    }


    public static class CvKalman extends Structure {
        public static boolean autoSynch = true;
        public CvKalman() { setAutoSynch(autoSynch); releasable = false; }
        public CvKalman(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvKalman.class) read(); }

        public static CvKalman create(int dynam_params, int measure_params,
                int control_params/*=0*/) {
            CvKalman k = cvCreateKalman(dynam_params, measure_params, control_params);
            if (k != null) {
                k.releasable = true;
            }
            return k;
        }
        public void release() {
            releasable = false;
            cvReleaseKalman(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public int MP;
        public int DP;
        public int CP;

        public FloatByReference PosterState;
        public FloatByReference PriorState;
        public FloatByReference DynamMatr;
        public FloatByReference MeasurementMatr;
        public FloatByReference MNCovariance;
        public FloatByReference PNCovariance;
        public FloatByReference KalmGainMatr;
        public FloatByReference PriorErrorCovariance;
        public FloatByReference PosterErrorCovariance;
        public FloatByReference Temp1;
        public FloatByReference Temp2;

        public CvMat.ByReference state_pre;
        public CvMat.ByReference state_post;
        public CvMat.ByReference transition_matrix;
        public CvMat.ByReference control_matrix;
        public CvMat.ByReference measurement_matrix;
        public CvMat.ByReference process_noise_cov;
        public CvMat.ByReference measurement_noise_cov;
        public CvMat.ByReference error_cov_pre;
        public CvMat.ByReference gain;
        public CvMat.ByReference error_cov_post;

        public CvMat.ByReference temp1;
        public CvMat.ByReference temp2;
        public CvMat.ByReference temp3;
        public CvMat.ByReference temp4;
        public CvMat.ByReference temp5;

        public static class ByReference extends CvKalman implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvKalman p) {
                setStructure(p);
            }
            public CvKalman getStructure() {
                return new CvKalman(getValue());
            }
            public void getStructure(CvKalman p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvKalman p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static native CvKalman cvCreateKalman(int dynam_params, int measure_params, int control_params/*=0*/);
    public static native void cvReleaseKalman(CvKalman.PointerByReference kalman);
    public static native CvMat cvKalmanPredict(CvKalman kalman, CvMat control/*=null*/);
    public static native CvMat cvKalmanCorrect(CvKalman kalman, CvMat measurement);

    public static class CvRandState extends Structure {
        public static boolean autoSynch = true;
        public CvRandState() { setAutoSynch(autoSynch); }
        public CvRandState(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvRandState.class) read(); }

        long /* CvRNG */ state;
        int              disttype;
        CvScalar         param0, param1;

        public static class ByReference extends CvRandState implements Structure.ByReference { }
    }

    public static class CvConDensation extends Structure {
        public static boolean autoSynch = true;
        public CvConDensation() { setAutoSynch(autoSynch); releasable = false; }
        public CvConDensation(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvConDensation.class) read(); }

        private static int version;

        public static CvConDensation create(int dynam_params, int measure_params,
                int sample_count) {
            CvConDensation c = null;
            try {
                c = cvaux.v21.cvCreateConDensation(dynam_params, measure_params, sample_count);
                version = 21;
            } catch (Throwable t) {
                try {
                    c = v20.cvCreateConDensation(dynam_params, measure_params, sample_count);
                    version = 20;
                } catch (Throwable tt) {
                    try {
                        c = v11.cvCreateConDensation(dynam_params, measure_params, sample_count);
                        version = 11;
                    } catch (Throwable ttt) {
                        c = v10.cvCreateConDensation(dynam_params, measure_params, sample_count);
                        version = 10;
                    }
                }
            }
            if (c != null) {
                c.releasable = true;
            }
            return c;
        }
        public void release() {
            releasable = false;
            switch (version) {
                case 21: cvaux.v21.cvReleaseConDensation(pointerByReference()); break;
                case 20: v20.cvReleaseConDensation(pointerByReference()); break;
                case 11: v11.cvReleaseConDensation(pointerByReference()); break;
                case 10: v10.cvReleaseConDensation(pointerByReference()); break;
                default: assert (false);
            }
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public int MP;
        public int DP;
        public FloatByReference DynamMatr;
        public FloatByReference State;
        public int SamplesNum;
        public FloatPointerByReference flSamples;
        public FloatPointerByReference flNewSamples;
        public FloatByReference flConfidence;
        public FloatByReference flCumulative;
        public FloatByReference Temp;
        public FloatByReference RandomSample;
        public CvRandState.ByReference RandS;

        public static class ByReference extends CvConDensation implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvConDensation p) {
                setStructure(p);
            }
            public CvConDensation getStructure() {
                return new CvConDensation(getValue());
            }
            public void getStructure(CvConDensation p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvConDensation p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }


    public static final int CV_HAAR_MAGIC_VAL    = 0x42500000;
    public static final String CV_TYPE_NAME_HAAR = "opencv-haar-classifier";

    public static boolean CV_IS_HAAR_CLASSIFIER(CvHaarClassifierCascade haar) {
        return haar != null && (haar.flags & CV_MAGIC_MASK)==CV_HAAR_MAGIC_VAL;
    }

    public static final int CV_HAAR_FEATURE_MAX = 3;

    public static class CvHaarFeature extends Structure {
        public static boolean autoSynch = true;
        public CvHaarFeature() { setAutoSynch(autoSynch); }
        public CvHaarFeature(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvHaarFeature.class) read(); }

        public int tilted;
        public static class Rect extends Structure {
            public CvRect r;
            public float weight;
        }
        public Rect[] rect = new Rect[CV_HAAR_FEATURE_MAX];

        public static class ByReference extends CvHaarFeature implements Structure.ByReference { }
    }

    public static class CvHaarClassifier extends Structure {
        public static boolean autoSynch = true;
        public CvHaarClassifier() { setAutoSynch(autoSynch); }
        public CvHaarClassifier(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvHaarClassifier.class) read(); }

        public int count;
        public CvHaarFeature.ByReference haar_feature;
        public FloatByReference threshold;
        public IntByReference left;
        public IntByReference right;
        public FloatByReference alpha;

        public static class ByReference extends CvHaarClassifier implements Structure.ByReference { }
    }

    public static class CvHaarStageClassifier extends Structure {
        public static boolean autoSynch = true;
        public CvHaarStageClassifier() { setAutoSynch(autoSynch); }
        public CvHaarStageClassifier(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvHaarStageClassifier.class) read(); }

        public int  count;
        public float threshold;
        public CvHaarClassifier.ByReference classifier;

        public int next;
        public int child;
        public int parent;

        public static class ByReference extends CvHaarStageClassifier implements Structure.ByReference { }
    }

    public static class CvHidHaarClassifierCascade extends PointerType { }

    public static class CvHaarClassifierCascade extends Structure {
        public static boolean autoSynch = true;
        public CvHaarClassifierCascade() { setAutoSynch(autoSynch); releasable = false; }
        public CvHaarClassifierCascade(Pointer m) { super(m); setAutoSynch(autoSynch); releasable = true;
                if (autoSynch && getClass() == CvHaarClassifierCascade.class) read(); }

        public static CvHaarClassifierCascade load(String directory,
                CvSize.ByValue orig_window_size) {
            CvHaarClassifierCascade h = cvLoadHaarClassifierCascade(directory,
                    orig_window_size);
            if (h != null) {
                h.releasable = true;
            }
            return h;
        }
        public void release() {
            releasable = false;
            cvReleaseHaarClassifierCascade(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;


        public int  flags;
        public int  count;
        public CvSize orig_window_size;
        public CvSize real_window_size;
        public double scale;
        public CvHaarStageClassifier.ByReference stage_classifier;
        public CvHidHaarClassifierCascade hid_cascade;


        public static class ByReference extends CvHaarClassifierCascade implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvHaarClassifierCascade p) {
                setStructure(p);
            }
            public CvHaarClassifierCascade getStructure() {
                return new CvHaarClassifierCascade(getValue());
            }
            public void getStructure(CvHaarClassifierCascade p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(CvHaarClassifierCascade p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class CvAvgComp extends Structure {
        public static boolean autoSynch = true;
        public CvAvgComp() { setAutoSynch(autoSynch); }
        public CvAvgComp(Pointer m) { super(m); setAutoSynch(autoSynch);
                if (autoSynch && getClass() == CvAvgComp.class) read(); }

        public CvRect rect;
        public int neighbors;
    }

    public static native CvHaarClassifierCascade cvLoadHaarClassifierCascade(String directory, CvSize.ByValue orig_window_size);
    public static native void cvReleaseHaarClassifierCascade(CvHaarClassifierCascade.PointerByReference cascade);
    public static final int
            CV_HAAR_DO_CANNY_PRUNING    = 1,
            CV_HAAR_SCALE_IMAGE         = 2,
            CV_HAAR_FIND_BIGGEST_OBJECT = 4,
            CV_HAAR_DO_ROUGH_SEARCH     = 8;
    public static native CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/,
            int flags/*=0*/, CvSize.ByValue min_size/*=cvSize(0,0)*/);
    public static CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/, int flags/*=0*/) {
        return cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags, CvSize.ZERO);
    }
    public static native void cvSetImagesForHaarClassifierCascade(CvHaarClassifierCascade cascade,
            CvArr sum, CvArr sqsum, CvArr tilted_sum, double scale);
    public static native int cvRunHaarClassifierCascade(CvHaarClassifierCascade cascade,
            CvPoint.ByValue pt, int start_stage/*=0*/);


    public static native void cvProjectPoints2(CvMat object_points, CvMat rotation_vector,
            CvMat translation_vector, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat image_points,
            CvMat dpdrot/*=null*/, CvMat dpdt/*=null*/, CvMat dpd/*=null*/,
            CvMat dpdc/*=null*/, CvMat dpddist/*=null*/, double aspect_ratio/*=0*/);
    public static void cvProjectPoints2(CvMat object_points, CvMat rotation_vector,
            CvMat translation_vector, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat image_points) {
        cvProjectPoints2(object_points, rotation_vector,translation_vector,
                intrinsic_matrix, distortion_coeffs, image_points,
                null, null, null, null, null, 0);
    }


    public static final int
            CV_CALIB_USE_INTRINSIC_GUESS = 1,
            CV_CALIB_FIX_ASPECT_RATIO    = 2,
            CV_CALIB_FIX_PRINCIPAL_POINT = 4,
            CV_CALIB_ZERO_TANGENT_DIST   = 8,
            CV_CALIB_FIX_FOCAL_LENGTH    = 16,
            CV_CALIB_FIX_K1              = 32,
            CV_CALIB_FIX_K2              = 64,
            CV_CALIB_FIX_K3              = 128;
    public static native double cvCalibrateCamera2(CvMat object_points, CvMat image_points,
            CvMat point_counts, CvSize.ByValue image_size,
            CvMat intrinsic_matrix, CvMat distortion_coeffs,
            CvMat rotation_vectors/*=null*/, CvMat translation_vectors/*=null*/, int flags/*=0*/);
    public static native int cvRodrigues2(CvMat src, CvMat dst, CvMat jacobian/*=null*/);
//    public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix, CvMat distortion_coeffs);
    public static native void cvInitUndistortMap(CvMat intrinsic_matrix, CvMat distortion_coeffs,
            CvArr mapx, CvArr mapy);

    public static final int
            CV_CALIB_CB_ADAPTIVE_THRESH = 1,
            CV_CALIB_CB_NORMALIZE_IMAGE = 2,
            CV_CALIB_CB_FILTER_QUADS    = 4,
            CV_CALIB_CB_FAST_CHECK      = 8;
    public static native int cvFindChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f corners, IntByReference corner_count/*=null*/,
            int flags/*=CV_CALIB_CB_ADAPTIVE_THRESH */);
    public static int cvFindChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f[] corners, IntByReference corner_count/*=null*/,
            int flags/*=CV_CALIB_CB_ADAPTIVE_THRESH */) {
        int i = cvFindChessboardCorners(image, pattern_size, corners[0], corner_count, flags);
        for (Structure s : corners) { s.read(); }
        return i;
    }
    public static native void cvDrawChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f corners, int count, int pattern_was_found);
    public static void cvDrawChessboardCorners(CvArr image, CvSize.ByValue pattern_size,
            CvPoint2D32f[] corners, int count, int pattern_was_found) {
        for (Structure s : corners) { s.write(); }
        cvDrawChessboardCorners(image, pattern_size, corners[0], count, pattern_was_found);
    }


    public static class CvPOSITObject extends PointerType {
        public CvPOSITObject() { releasable = false; }
        public CvPOSITObject(Pointer p) { super(p); releasable = true; }

        public static CvPOSITObject create(CvPoint3D32f[] points) {
            CvPOSITObject p = cvCreatePOSITObject(points, points.length);
            if (p != null) {
                p.releasable = true;
            }
            return p;
        }

        public void release() {
            releasable = false;
            cvReleasePOSITObject(pointerByReference());
        }
        @Override protected void finalize() {
            if (releasable) {
                release();
            }
        }
        private boolean releasable = false;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(CvPOSITObject p) {
                setStructure(p);
            }
            public CvPOSITObject getStructure() {
                return new CvPOSITObject(getValue());
            }
            public void getStructure(CvPOSITObject p) {
                p.setPointer(getValue());
            }
            public void setStructure(CvPOSITObject p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static native CvPOSITObject cvCreatePOSITObject(CvPoint3D32f points, int point_count);
    public static CvPOSITObject cvCreatePOSITObject(CvPoint3D32f[] points, int point_count) {
        for (Structure s : points) { s.write(); }
        return cvCreatePOSITObject(points[0], point_count);
    }
    public static native void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f image_points,
            double focal_length, CvTermCriteria.ByValue criteria,
            float[] /*CvMatr32f*/ rotation_matrix, float[] /*CvVect32f*/ translation_vector);
    public static void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f[] image_points,
            double focal_length, CvTermCriteria.ByValue criteria,
            float[] /*CvMatr32f*/ rotation_matrix, float[] /*CvVect32f*/ translation_vector) {
        for (Structure s : image_points) { s.write(); }
        cvPOSIT(posit_object, image_points[0],
                focal_length, criteria, rotation_matrix, translation_vector);
    }
    public static native void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f image_points,
            double focal_length, CvTermCriteria.ByValue criteria,
            FloatBuffer /*CvMatr32f*/ rotation_matrix, FloatBuffer /*CvVect32f*/ translation_vector);
    public static void cvPOSIT(CvPOSITObject posit_object, CvPoint2D32f[] image_points,
            double focal_length, CvTermCriteria.ByValue criteria,
            FloatBuffer /*CvMatr32f*/ rotation_matrix, FloatBuffer /*CvVect32f*/ translation_vector) {
        for (Structure s : image_points) { s.write(); }
        cvPOSIT(posit_object, image_points[0],
                focal_length, criteria, rotation_matrix, translation_vector);
    }
    public static native void cvReleasePOSITObject(CvPOSITObject.PointerByReference posit_object);


    public static final int
            CV_FM_7POINT = 1,
            CV_FM_8POINT = 2;
    public static native int cvFindFundamentalMat(CvMat points1, CvMat points2, CvMat fundamental_matrix,
            int method/*=CV_FM_RANSAC*/, double param1/*=3*/, double param2/*=0.99*/, CvMat status/*=null*/);
    public static native void cvComputeCorrespondEpilines(CvMat points, int which_image,
            CvMat fundamental_matrix, CvMat correspondent_lines);
}
