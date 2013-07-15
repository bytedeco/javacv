/*
 * Copyright (C) 2012,2013 Samuel Audet
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
 * GNU General Public License for more Details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * This file is based on information found in stitcher.hpp and all included
 * files of of OpenCV 2.4.6.1, which are covered by the following copyright notice:
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
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.FloatPointer;
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
import com.googlecode.javacpp.annotation.StdVector;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/stitching/stitcher.hpp>", "<opencv2/stitching/detail/autocalib.hpp>", "opencv_adapters.h"},
        link={"opencv_stitching@.2.4", "opencv_gpu@.2.4", "opencv_video@.2.4", "opencv_legacy@.2.4", "opencv_ml@.2.4", "opencv_photo@.2.4", "opencv_nonfree@.2.4",
              "opencv_objdetect@.2.4", "opencv_features2d@.2.4", "opencv_flann@.2.4", "opencv_calib3d@.2.4", "opencv_highgui@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_stitching246", "opencv_gpu246", "opencv_video246", "opencv_legacy246", "opencv_ml246", "opencv_photo246", "opencv_nonfree246",
              "opencv_objdetect246", "opencv_features2d246", "opencv_flann246", "opencv_calib3d246", "opencv_highgui246", "opencv_imgproc246", "opencv_core246"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath,
        link={"opencv_stitching", "opencv_video", "opencv_legacy", "opencv_ml", "opencv_photo", "opencv_nonfree",
              "opencv_objdetect", "opencv_features2d", "opencv_flann", "opencv_calib3d", "opencv_highgui", "opencv_imgproc", "opencv_core"}) })
public class opencv_stitching {
    static { load(opencv_calib3d.class); load(opencv_features2d.class); load(opencv_objdetect.class); load(opencv_nonfree.class);
             load(opencv_photo.class); load(opencv_ml.class); load(opencv_legacy.class); load(opencv_video.class); load(); }

    // #include "detail/warpers.hpp"
    @Namespace("cv::detail") public static class RotationWarper extends Pointer {
        static { load(); }
        public RotationWarper() { }
        public RotationWarper(Pointer p) { super(p); }

        public /*abstract*/ native @ByVal CvPoint2D32f warpPoint(@ByVal CvPoint2D32f pt, CvMat K, CvMat R);
        public /*abstract*/ native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
                @InputMat IplImage xmap, @InputMat IplImage ymap);
        public /*abstract*/ native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
                int interp_mode, int border_mode, @InputMat IplImage dst);
        public /*abstract*/ native void warpBackward(IplImage src, CvMat K, CvMat R,
                int interp_mode, int border_mode, @ByVal CvSize dst_size, @InputMat IplImage dst);
        public /*abstract*/ native @ByVal CvRect warpRoi(@ByVal CvSize src_size, CvMat K, CvMat R);

        public native float getScale();
        public native void setScale(float scale);
    }

//    @Namespace("cv::detail") public static class ProjectorBase extends Pointer {
//        static { load(); }
//        public ProjectorBase()       { allocate();  }
//        public ProjectorBase(Pointer p) { super(p); }
//        private native void allocate();
//
//        public native void setCameraParams(CvMat K/*=Mat::eye(3, 3, CV_32F)*/,
//                CvMat R/*=Mat::eye(3, 3, CV_32F)*/, CvMat T/*= Mat::zeros(3, 1, CV_32F)*/);
//
//        public native float scale();
//        public native FloatPointer k/*[9]*/();
//        public native FloatPointer rinv/*[9]*/();
//        public native FloatPointer r_kinv/*[9]*/();
//        public native FloatPointer k_rinv/*[9]*/();
//        public native FloatPointer t/*[3]*/();
//    }

//    template <class P>
//    @Namespace("cv::detail") public static class RotationWarperBase extends RotationWarper {
//        public native @ByVal CvPoint2D32f warpPoint(@ByVal CvPoint2D32f pt, CvMat K, CvMat R);
//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @InputMat IplImage xmap, @InputMat IplImage ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @InputMat IplImage dst);
//        public native void warpBackward(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @ByVal CvSize dst_size, @InputMat IplImage dst);
//        public native @ByVal CvRect warpRoi(@ByVal CvSize src_size, CvMat K, CvMat R);
//
//        public native float getScale();
//        public native void setScale(float scale);
//
//        protected native void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
//        protected native void detectResultRoiByBorder(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
//
//        protected native P projector_();
//    }

//    @Namespace("cv::detail") public static class PlaneProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::PlaneWarper") public static class DetailPlaneWarper
            extends /*RotationWarperBase<PlaneProjector>*/ RotationWarper {
        static { load(); }
        public DetailPlaneWarper() { allocate(); }
        public DetailPlaneWarper(float scale/*=1.0*/) { allocate(scale); }
        public DetailPlaneWarper(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float scale/*=1.0*/);

//        public native void setScale(float scale);
//
//        public native @ByVal CvPoint2D32f warpPoint(@ByVal CvPoint2D32f pt, CvMat K, CvMat R);
//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @InputMat IplImage xmap, @InputMat IplImage ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @InputMat IplImage dst);
//        public native @ByVal CvRect warpRoi(@ByVal CvSize src_size, CvMat K, CvMat R);

//        protected void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
    }

//    @Namespace("cv::detail") public static class SphericalProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::SphericalWarper") public static class DetailSphericalWarper extends
            /*RotationWarperBase<SphericalProjector>*/ RotationWarper {
        static { load(); }
        public DetailSphericalWarper(float scale) { allocate(scale); }
        public DetailSphericalWarper(Pointer p) { super(p); }
        private native void allocate(float scale);

//        protected void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
    }

//    @Namespace("cv::detail") public static class CylindricalProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::CylindricalWarper") public static class DetailCylindricalWarper extends
            /*RotationWarperBase<CylindricalProjector>*/ RotationWarper {
        static { load(); }
        public DetailCylindricalWarper(float scale) { allocate(scale); }
        public DetailCylindricalWarper(Pointer p) { super(p); }
        private native void allocate(float scale);

//        protected void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
    }

//    @Namespace("cv::detail") public static class FisheyeProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::FisheyeWarper") public static class DetailFisheyeWarper extends
            /*RotationWarperBase<FisheyeProjector>*/ RotationWarper {
        static { load(); }
        public DetailFisheyeWarper(float scale) { allocate(scale); }
        public DetailFisheyeWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
    }

//    @Namespace("cv::detail") public static class StereographicProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::StereographicWarper") public static class DetailStereographicWarper
            extends /*RotationWarperBase<StereographicProjector>*/ RotationWarper {
        static { load(); }
        public DetailStereographicWarper(float scale) { allocate(scale); }
        public DetailStereographicWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
    }

//    @Namespace("cv::detail") public static class CompressedRectilinearProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//
//        public native float a();
//        public native float b();
//    }

    @Name("cv::detail::CompressedRectilinearWarper") public static class DetailCompressedRectilinearWarper
            extends /*RotationWarperBase<CompressedRectilinearProjector>*/ RotationWarper {
        static { load(); }
        public DetailCompressedRectilinearWarper(float scale) { allocate(scale); }
        public DetailCompressedRectilinearWarper(float scale, float A/*=1*/, float B/*=1*/) { allocate(scale, A, B);  }
        public DetailCompressedRectilinearWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
        private native void allocate(float scale, float A/*=1*/, float B/*=1*/);
    }

//    @Namespace("cv::detail") public static class CompressedRectilinearPortraitProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//
//        public native float a();
//        public native float b();
//    }

    @Name("cv::detail::CompressedRectilinearPortraitWarper") public static class DetailCompressedRectilinearPortraitWarper
            extends /*RotationWarperBase<CompressedRectilinearPortraitProjector>*/ RotationWarper {
        static { load(); }
        public DetailCompressedRectilinearPortraitWarper(float scale) { allocate(scale); }
        public DetailCompressedRectilinearPortraitWarper(float scale, float A/*=1*/, float B/*=1*/) { allocate(scale, A, B);  }
        public DetailCompressedRectilinearPortraitWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
        private native void allocate(float scale, float A/*=1*/, float B/*=1*/);
    }

//    @Namespace("cv::detail") public static class PaniniProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//
//        public native float a();
//        public native float b();
//    }

    @Name("cv::detail::PaniniWarper") public static class DetailPaniniWarper
            extends /*RotationWarperBase<PaniniProjector>*/ RotationWarper {
        static { load(); }
        public DetailPaniniWarper(float scale) { allocate(scale); }
        public DetailPaniniWarper(float scale, float A/*=1*/, float B/*=1*/) { allocate(scale, A, B);  }
        public DetailPaniniWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
        private native void allocate(float scale, float A/*=1*/, float B/*=1*/);
    }

//    @Namespace("cv::detail") public static class PaniniPortraitProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//
//        public native float a();
//        public native float b();
//    }

    @Name("cv::detail::PaniniPortraitWarper") public static class DetailPaniniPortraitWarper
            extends /*RotationWarperBase<PaniniPortraitProjector>*/ RotationWarper {
        static { load(); }
        public DetailPaniniPortraitWarper(float scale) { allocate(scale); }
        public DetailPaniniPortraitWarper(float scale, float A/*=1*/, float B/*=1*/) { allocate(scale, A, B);  }
        public DetailPaniniPortraitWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
        private native void allocate(float scale, float A/*=1*/, float B/*=1*/);
    }

//    @Namespace("cv::detail") public static class MercatorProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::MercatorWarper") public static class DetailMercatorWarper
            extends /*RotationWarperBase<MercatorProjector>*/ RotationWarper {
        static { load(); }
        public DetailMercatorWarper(float scale) { allocate(scale); }
        public DetailMercatorWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
    }

//    @Namespace("cv::detail") public static class TransverseMercatorProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Name("cv::detail::TransverseMercatorWarper") public static class DetailTransverseMercatorWarper
            extends /*RotationWarperBase<TransverseMercatorProjector>*/ RotationWarper {
        static { load(); }
        public DetailTransverseMercatorWarper(float scale) { allocate(scale); }
        public DetailTransverseMercatorWarper(Pointer p) { super(p); }
        private native void allocate(float scale);
    }

    @Platform(not="android") @Name("cv::detail::PlaneWarperGpu") public static class DetailPlaneWarperGpu extends DetailPlaneWarper {
        static { load(); }
        public DetailPlaneWarperGpu() { allocate(); }
        public DetailPlaneWarperGpu(float scale/*=1.0*/) { allocate(scale); }
        public DetailPlaneWarperGpu(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float scale/*=1.0*/);

//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @InputMat IplImage xmap, @InputMat IplImage ymap);
        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R, CvMat T,
                @InputMat IplImage xmap, @InputMat IplImage ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @InputMat IplImage dst);
        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R, CvMat T,
                int interp_mode, int border_mode, @InputMat IplImage dst);

//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @ByRef GpuMat xmap, @ByRef GpuMat ymap);
//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R, CvMat T,
//                @ByRef GpuMat xmap, @ByRef GpuMat ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @ByRef GpuMat dst);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R, CvMat T,
//                int interp_mode, int border_mode, @ByRef GpuMat dst);
    }


    @Platform(not="android") @Name("cv::detail::SphericalWarperGpu") public static class DetailSphericalWarperGpu extends DetailSphericalWarper {
        static { load(); }
        public DetailSphericalWarperGpu(float scale) { super(scale); allocate(scale); }
        public DetailSphericalWarperGpu(Pointer p) { super(p); }
        private native void allocate(float scale);

//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @InputMat IplImage xmap, @InputMat IplImage ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @InputMat IplImage dst);

//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @ByRef GpuMat xmap, @ByRef GpuMat ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @ByRef GpuMat dst);
    }

    @Platform(not="android") @Name("cv::detail::CylindricalWarperGpu") public static class DetailCylindricalWarperGpu extends DetailCylindricalWarper {
        static { load(); }
        public DetailCylindricalWarperGpu(float scale) { super(scale); allocate(scale); }
        public DetailCylindricalWarperGpu(Pointer p) { super(p); }
        private native void allocate(float scale);

//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @InputMat IplImage xmap, @InputMat IplImage ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @InputMat IplImage dst);

//        public native @ByVal CvRect buildMaps(@ByVal CvSize src_size, CvMat K, CvMat R,
//                @ByRef GpuMat xmap, @ByRef GpuMat ymap);
//        public native @ByVal CvPoint warp(IplImage src, CvMat K, CvMat R,
//                int interp_mode, int border_mode, @ByRef GpuMat dst);
    }

//    @Namespace("cv::detail") public static class SphericalPortraitProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

//    @Namespace("cv::detail") public static class SphericalPortraitWarper
//            extends /*RotationWarperBase<SphericalPortraitProjector>*/ RotationWarper {
//        static { load(); }
//        public SphericalPortraitWarper(float scale) { allocate(scale); }
//        public SphericalPortraitWarper(Pointer p) { super(p); }
//        private native void allocate(float scale);
//
//        protected void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
//    }

//    @Namespace("cv::detail") public static class CylindricalPortraitProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Namespace("cv::detail") public static class CylindricalPortraitWarper
            extends /*RotationWarperBase<CylindricalPortraitProjector>*/ RotationWarper {
        static { load(); }
        public CylindricalPortraitWarper(float scale) { allocate(scale); }
        public CylindricalPortraitWarper(Pointer p) { super(p); }
        private native void allocate(float scale);

//        protected void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
    }

//    @Namespace("cv::detail") public static class PlanePortraitProjector extends ProjectorBase {
//        public native void mapForward(float x, float y, @ByRef float[] u, @ByRef float[] v);
//        public native void mapBackward(float u, float v, @ByRef float[] x, @ByRef float[] y);
//    }

    @Namespace("cv::detail") public static class PlanePortraitWarper
            extends /*RotationWarperBase<PlanePortraitProjector>*/ RotationWarper {
        static { load(); }
        public PlanePortraitWarper(float scale) { allocate(scale); }
        public PlanePortraitWarper(Pointer p) { super(p); }
        private native void allocate(float scale);

//        protected void detectResultRoi(@ByVal CvSize src_size, @ByRef CvPoint dst_tl, @ByRef CvPoint dst_br);
    }


    // #include "warpers.hpp"
    @Namespace("cv") public static class WarperCreator extends Pointer {
        static { load(); }
        public WarperCreator() { }
        public WarperCreator(Pointer p) { super(p); }

        public /*abstract*/ native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class PlaneWarper extends WarperCreator {
        static { load(); }
        public PlaneWarper()       { allocate();  }
        public PlaneWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class CylindricalWarper extends WarperCreator {
        static { load(); }
        public CylindricalWarper()       { allocate();  }
        public CylindricalWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class SphericalWarper extends WarperCreator {
        static { load(); }
        public SphericalWarper()       { allocate();  }
        public SphericalWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class FisheyeWarper extends WarperCreator {
        static { load(); }
        public FisheyeWarper()       { allocate();  }
        public FisheyeWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class StereographicWarper extends WarperCreator {
        static { load(); }
        public StereographicWarper()       { allocate();  }
        public StereographicWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class CompressedRectilinearWarper extends WarperCreator {
        static { load(); }
        public CompressedRectilinearWarper() { allocate(); }
        public CompressedRectilinearWarper(float A/*=1*/, float B/*=1*/) {
            allocate(A, B);
        }
        public CompressedRectilinearWarper(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float A/*=1*/, float B/*=1*/);

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class CompressedRectilinearPortraitWarper extends WarperCreator {
        static { load(); }
        public CompressedRectilinearPortraitWarper() { allocate(); }
        public CompressedRectilinearPortraitWarper(float A/*=1*/, float B/*=1*/) {
            allocate(A, B);
        }
        public CompressedRectilinearPortraitWarper(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float A/*=1*/, float B/*=1*/);

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class PaniniWarper extends WarperCreator {
        static { load(); }
        public PaniniWarper() { allocate(); }
        public PaniniWarper(float A/*=1*/, float B/*=1*/) {
            allocate(A, B);
        }
        public PaniniWarper(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float A/*=1*/, float B/*=1*/);

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class PaniniPortraitWarper extends WarperCreator {
        static { load(); }
        public PaniniPortraitWarper() { allocate(); }
        public PaniniPortraitWarper(float A/*=1*/, float B/*=1*/) {
            allocate(A, B);
        }
        public PaniniPortraitWarper(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float A/*=1*/, float B/*=1*/);

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class MercatorWarper extends WarperCreator {
        static { load(); }
        public MercatorWarper()       { allocate();  }
        public MercatorWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Namespace("cv") public static class TransverseMercatorWarper extends WarperCreator {
        static { load(); }
        public TransverseMercatorWarper()       { allocate();  }
        public TransverseMercatorWarper(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Platform(not="android") @Namespace("cv") public static class PlaneWarperGpu extends WarperCreator {
        static { load(); }
        public PlaneWarperGpu()       { allocate();  }
        public PlaneWarperGpu(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Platform(not="android") @Namespace("cv") public static class CylindricalWarperGpu extends WarperCreator {
        static { load(); }
        public CylindricalWarperGpu()       { allocate();  }
        public CylindricalWarperGpu(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }

    @Platform(not="android") @Namespace("cv") public static class SphericalWarperGpu extends WarperCreator {
        static { load(); }
        public SphericalWarperGpu()       { allocate();  }
        public SphericalWarperGpu(Pointer p) { super(p); }
        private native void allocate();

//        public native @Ptr RotationWarper create(float scale);
    }


    // #include "detail/matchers.hpp"
    @NoOffset @Namespace("cv::detail") public static class ImageFeatures extends Pointer {
        static { load(); }
        public ImageFeatures() { allocate(); }
        public ImageFeatures(int size) { allocateArray(size); }
        public ImageFeatures(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ImageFeatures position(int position) {
            return (ImageFeatures)super.position(position);
        }

        public native int img_idx();        public native ImageFeatures img_idx(int img_idx);
        @ByVal
        public native CvSize img_size();    public native ImageFeatures img_size(CvSize img_size);
        @Const @StdVector
        public native KeyPoint keypoints(); public native ImageFeatures keypoints(KeyPoint keypoints);
        @InputMat
        public native CvMat descriptors();  public native ImageFeatures descriptors(CvMat descriptors);
    }

    @Namespace("cv::detail") public static class FeaturesFinder extends Pointer {
        static { load(); }
        public FeaturesFinder() { }
        public FeaturesFinder(Pointer p) { super(p); }

        public native @Name("operator()") void find(IplImage image, @ByRef ImageFeatures features);
        public native @Name("operator()") void find(IplImage image, @ByRef ImageFeatures features, @Const @StdVector("CvRect,cv::Rect") CvRect rois);
        public native void collectGarbage();

//        protected /*abstract*/ void find(IplImage image, @ByRef ImageFeatures features);
    }

    @Namespace("cv::detail") public static class SurfFeaturesFinder extends FeaturesFinder {
        static { load(); }
        public SurfFeaturesFinder() { allocate(); }
        public SurfFeaturesFinder(double hess_thresh/*=300*/, int num_octaves/*=3*/,
                int num_layers/*=4*/, int num_octaves_descr/*=3*/, int num_layers_descr/*=4*/) {
            allocate(hess_thresh, num_octaves, num_layers, num_octaves_descr, num_layers_descr);
        }
        public SurfFeaturesFinder(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(double hess_thresh/*=300*/, int num_octaves/*=3*/,
                int num_layers/*=4*/, int num_octaves_descr/*=3*/, int num_layers_descr/*=4*/);
    }

    @Namespace("cv::detail") public static class OrbFeaturesFinder extends FeaturesFinder {
        static { load(); }
        public OrbFeaturesFinder() { allocate(); }
        public OrbFeaturesFinder(@ByVal CvSize _grid_size/*=cvSize(3,1)*/,
                int nfeatures/*=1500*/, float scaleFactor/*=1.3*/, int nlevels/*=5*/) {
            allocate(_grid_size, nfeatures, scaleFactor, nlevels);
        }
        public OrbFeaturesFinder(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByVal CvSize _grid_size/*=cvSize(3,1)*/,
                int nfeatures/*=1500*/, float scaleFactor/*=1.3*/, int nlevels/*=5*/);
    }

    @Platform(not="android") @Namespace("cv::detail") public static class SurfFeaturesFinderGpu extends FeaturesFinder {
        static { load(); }
        public SurfFeaturesFinderGpu() { allocate(); }
        public SurfFeaturesFinderGpu(double hess_thresh/*=300*/, int num_octaves/*=3*/,
                int num_layers/*=4*/, int num_octaves_descr/*=4*/, int num_layers_descr/*=2*/) {
            allocate(hess_thresh, num_octaves, num_layers, num_octaves_descr, num_layers_descr);
        }
        public SurfFeaturesFinderGpu(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(double hess_thresh/*=300*/, int num_octaves/*=3*/,
                int num_layers/*=4*/, int num_octaves_descr/*=4*/, int num_layers_descr/*=3*/);

//        public native void collectGarbage();
    }

    @NoOffset @Namespace("cv::detail") public static class MatchesInfo extends Pointer {
        static { load(); }
        public MatchesInfo() { allocate(); }
        public MatchesInfo(@ByRef MatchesInfo other) { allocate(other); }
        public MatchesInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByRef MatchesInfo other);

        public native @Name("operator=") @Const @ByRef MatchesInfo put(@ByRef MatchesInfo other);

        public native int src_img_idx();          public native MatchesInfo src_img_idx(int src_img_idx);
        public native int dst_img_idx();          public native MatchesInfo dst_img_idx(int dst_img_idx);
        @Const @StdVector
        public native DMatch matches();           public native MatchesInfo matches(DMatch matches);
        @Const @StdVector @Cast("uchar*")
        public native BytePointer inliers_mask(); public native MatchesInfo inliers_mask(BytePointer inliers_mask);
        public native int num_inliers();          public native MatchesInfo num_inliers(int num_inliers);
        @InputMat
        public native CvMat H();                  public native MatchesInfo H(CvMat H);
        public native double confidence();        public native MatchesInfo confidence(double confidence);
    }

    @Namespace("cv::detail") public static class FeaturesMatcher extends Pointer {
        static { load(); }
        public FeaturesMatcher() { }
        public FeaturesMatcher(Pointer p) { super(p); }

        public native @Name("operator()") void match(@ByRef ImageFeatures features1,
                @ByRef ImageFeatures features2, @ByRef MatchesInfo matches_info);
        public native @Name("operator()") void match(@Const @StdVector ImageFeatures features,
                @Const @StdVector MatchesInfo pairwise_matches, CvMat mask/*=null*/);

        public native @Cast("bool") boolean isThreadSafe();

        public native void collectGarbage();

//        protected native FeaturesMatcher(@Cast("bool") boolean is_thread_safe/*=false*/);
//
//        protected /*abstract*/ native void match(@ByRef ImageFeatures features1,
//                @ByRef ImageFeatures features2, @ByRef MatchesInfo matches_info);
//
//        protected native @Cast("bool") boolean is_thread_safe_();
    }

    @Namespace("cv::detail") public static class BestOf2NearestMatcher extends FeaturesMatcher {
        static { load(); }
        public BestOf2NearestMatcher() { allocate(); }
        public BestOf2NearestMatcher(@Cast("bool") boolean try_use_gpu/*=false*/, float match_conf/*=0.3*/,
                int num_matches_thresh1/*=6*/, int num_matches_thresh2/*=6*/) {
            allocate(try_use_gpu, match_conf, num_matches_thresh1, num_matches_thresh2);
        }
        public BestOf2NearestMatcher(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("bool") boolean try_use_gpu/*=false*/, float match_conf/*=0.3*/,
                int num_matches_thresh1/*=6*/, int num_matches_thresh2/*=6*/);

//        public native void collectGarbage();

//        protected native void match(@ByRef ImageFeatures features1,
//                @ByRef ImageFeatures features2, @ByRef MatchesInfo matches_info);
//
//        protected native int num_matches_thresh1_();
//        protected native int num_matches_thresh2_();
//        protected native @Const @Ptr FeaturesMatcher impl_();
    }


    // #include "detail/camera.hpp"
    @NoOffset @Namespace("cv::detail") public static class CameraParams extends Pointer {
        static { load(); }
        public CameraParams() { allocate(); }
        public CameraParams(@ByRef CameraParams other) { allocate(other); }
        public CameraParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByRef CameraParams other);

        public native @Name("operator=") @Const @ByRef CameraParams put(@ByRef CameraParams other);

        public native @OutputMat CvMat K();

        public native double focal();      public native CameraParams focal(double focal);
        public native double aspect();     public native CameraParams aspect(double aspect);
        public native double ppx();        public native CameraParams ppx(double ppx);
        public native double ppy();        public native CameraParams ppy(double ppy);
        public native @InputMat CvMat R(); public native CameraParams R(CvMat R);
        public native @InputMat CvMat t(); public native CameraParams t(CvMat t);
    }


    // #include "detail/autocalib.hpp"
    @Namespace("cv::detail") public static native void focalsFromHomography(@InputMat CvArr H,
            @ByRef double[] f0, @ByRef double[] f1, @ByRef @Cast("bool*") boolean[] f0_ok, @ByRef @Cast("bool*") boolean[] f1_ok);

    @Namespace("cv::detail") public static native void estimateFocal(@Const @StdVector ImageFeatures features,
            @Const @StdVector MatchesInfo pairwise_matches, @Const @StdVector double[] focals);

    @Namespace("cv::detail") public static native @Cast("bool") boolean calibrateRotatingCamera(@ByRef MatVector Hs, @InputMat CvArr K);


    // #include "detail/util.hpp"
    @NoOffset @Namespace("cv::detail") public static class DisjointSets extends Pointer {
        static { load(); }
        public DisjointSets() { allocate(); }
        public DisjointSets(int elem_count/*=0*/) { allocate(elem_count); }
        public DisjointSets(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int elem_count/*=0*/);

        public native void createOneElemSets(int elem_count);
        public native int findSetByElem(int elem);
        public native int mergeSets(int set1, int set2);

        public native @Const @StdVector IntPointer parent(); public native DisjointSets parent(IntPointer parent);
        public native @Const @StdVector IntPointer size();   public native DisjointSets size(IntPointer size);
    }

    @NoOffset @Namespace("cv::detail") public static class GraphEdge extends Pointer {
        static { load(); }
        public GraphEdge(int from, int to, float weight) { allocate(from, to, weight); }
        public GraphEdge(Pointer p) { super(p); }
        private native void allocate(int from, int to, float weight);

        public native @Cast("bool") @Name("operator<") boolean lessThan(@ByRef GraphEdge other);
        public native @Cast("bool") @Name("operator>") boolean greaterThan(@ByRef GraphEdge other);

        public native int from();     public native GraphEdge from(int from);
        public native int to();       public native GraphEdge to(int to);
        public native float weight(); public native GraphEdge weight(float weight);
    }

    @Namespace("cv::detail") public static class Graph extends Pointer {
        static { load(); }
        public Graph() { allocate(); }
        public Graph(int num_vertices/*=0*/) { allocate(num_vertices); }
        public Graph(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int num_vertices/*=0*/);

        public native void create(int num_vertices);
        public native int numVertices();
        public native void addEdge(int from, int to, float weight);
//        public native B forEach(B body);
//        public native B walkBreadthFirst(int from, B body);
    }

    @Namespace("cv::detail") public static native @Cast("bool") boolean overlapRoi(@ByVal CvPoint tl1,
            @ByVal CvPoint tl2, @ByVal CvSize sz1, @ByVal CvSize sz2, @Const @Adapter("RectAdapter") CvRect roi);
    @Namespace("cv::detail") public static native @ByVal CvRect resultRoi(
            @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector images);
    @Namespace("cv::detail") public static native @ByVal CvRect resultRoi(
            @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @Const @StdVector("CvSize,cv::Size") CvSize sizes);
    @Namespace("cv::detail") public static native @ByVal CvPoint resultTl(
            @Const @StdVector("CvPoint,cv::Point") CvPoint corners);

    @Namespace("cv::detail") public static native void selectRandomSubset(int count, int size, @Const @StdVector int[] subset);

    @Namespace("cv::detail") public static native @ByRef IntPointer stitchingLogLevel();


    // #include "detail/motion_estimators.hpp"
    @Namespace("cv::detail") public static class Estimator extends Pointer {
        static { load(); }
        public Estimator() { }
        public Estimator(Pointer p) { super(p); }

        public native @Name("operator()") void estimate(@Const @StdVector ImageFeatures features,
                @Const @StdVector MatchesInfo pairwise_matches, @Const @StdVector CameraParams cameras);

//        protected /*abstract*/ native void estimate(@Const @StdVector ImageFeatures features,
//                @Const @StdVector MatchesInfo pairwise_matches, @Const @StdVector CameraParams cameras);
    }

    @Namespace("cv::detail") public static class HomographyBasedEstimator extends Estimator {
        static { load(); }
        public HomographyBasedEstimator() { allocate(); }
        public HomographyBasedEstimator(@Cast("bool") boolean is_focals_estimated/*=false*/) {
            allocate(is_focals_estimated);
        }
        public HomographyBasedEstimator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("bool") boolean is_focals_estimated/*=false*/);
    }

    @Namespace("cv::detail") public static class BundleAdjusterBase extends Estimator {
        static { load(); }
        public BundleAdjusterBase() { }
        public BundleAdjusterBase(Pointer p) { super(p); }

        public native @OutputMat CvMat refinementMask();
        public native void setRefinementMask(CvMat mask);

        public native double confThresh();
        public native void setConfThresh(double conf_thresh);

        public native @ByVal CvTermCriteria termCriteria();
        public native void setTermCriteria(@ByRef CvTermCriteria term_criteria);

//        protected native BundleAdjusterBase(int num_params_per_cam, int num_errs_per_measurement);
//
//        protected native void estimate(@Const @StdVector ImageFeatures features,
//                @Const @StdVector MatchesInfo pairwise_matches, @Const @StdVector CameraParams cameras);
//
//        protected /*abstract*/ native void setUpInitialCameraParams(@Const @StdVector CameraParams cameras);
//        protected /*abstract*/ native void obtainRefinedCameraParams(@Const @StdVector CameraParams cameras);
//        protected /*abstract*/ native void calcError(@InputMat CvMat err);
//        protected /*abstract*/ native void calcJacobian(@InputMat CvMat jac);
//
//        protected native @OutputMat CvMat refinement_mask_();
//
//        protected native int num_images_();
//        protected native int total_num_matches_();
//
//        protected native int num_params_per_cam_();
//        protected native int num_errs_per_measurement_();
//
//        protected native @Const ImageFeatures features_();
//        protected native @Const MatchesInfo pairwise_matches_();
//
//        protected native double conf_thresh_();
//        protected native CvTermCriteria term_criteria_();
//        protected native @OutputMat CvMat cam_params_();
//        protected native std::vector<std::pair<int,int> > edges_();
    }

    @Namespace("cv::detail") public static class BundleAdjusterReproj extends BundleAdjusterBase {
        static { load(); }
        public BundleAdjusterReproj() { allocate(); }
        public BundleAdjusterReproj(Pointer p) { super(p); }
        private native void allocate();
    }

    @Namespace("cv::detail") public static class BundleAdjusterRay extends BundleAdjusterBase {
        static { load(); }
        public BundleAdjusterRay() { allocate(); }
        public BundleAdjusterRay(Pointer p) { super(p); }
        private native void allocate();
    }

    // enum WaveCorrectKind
    public static final int
            WAVE_CORRECT_HORIZ = 0,
            WAVE_CORRECT_VERT = 1;

    @Namespace("cv::detail") public static native void waveCorrect(@ByRef MatVector rmats, @Cast("cv::detail::WaveCorrectKind") int kind);
    @Namespace("cv::detail") public static native @ByRef String matchesGraphAsString(@ByRef StringVector pathes,
            @Const @StdVector MatchesInfo pairwise_matches,  float conf_threshold);
    @Namespace("cv::detail") public static native @StdVector IntPointer leaveBiggestComponent(
            @Const @StdVector ImageFeatures features, @Const @StdVector MatchesInfo pairwise_matches, float conf_threshold);
    @Namespace("cv::detail") public static native void findMaxSpanningTree(int num_images,
            @Const @StdVector MatchesInfo pairwise_matches, @ByRef Graph span_tree, @Const @StdVector int[] centers);


    // #include "detail/exposure_compensate.hpp"
    @Name("std::vector<std::pair<cv::Mat,uchar> >")
    public static class MatBytePairVector extends Pointer {
        static { load(); }
        public MatBytePairVector()       { allocate();  }
        public MatBytePairVector(long n) { allocate(n); }
        public MatBytePairVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(long n);

        @InputMat
        public native @Index CvMat first(long i); public native MatBytePairVector first(long i, CvMat mat);
        public native @Index byte second(long i); public native MatBytePairVector second(long i, byte uchar);
    }

    @Namespace("cv::detail") public static class ExposureCompensator extends Pointer {
        static { load(); }
        public ExposureCompensator() { }
        public ExposureCompensator(Pointer p) { super(p); }

        public static final int NO = 0, GAIN = 1, GAIN_BLOCKS = 2;
        public static native @Ptr ExposureCompensator createDefault(int type);

        public native void feed(@Const @StdVector("CvPoint,cv::Point") CvPoint corners,
                @ByRef MatVector images, @ByRef MatVector masks);
        public /*abstract*/ native void feed(@Const @StdVector("CvPoint,cv::Point") CvPoint corners,
                @ByRef MatVector images, @ByRef MatBytePairVector masks);
        public /*abstract*/ native void apply(int index, @ByVal CvPoint corner,
                @InputMat CvArr image, @InputMat CvArr mask);
    }

    @Namespace("cv::detail") public static class NoExposureCompensator extends ExposureCompensator {
        static { load(); }
        public NoExposureCompensator() { allocate(); }
        public NoExposureCompensator(Pointer p) { super(p); }
        private native void allocate();

//        public native void feed(@Const @StdVector("CvPoint,cv::Point") CvPoint corners,
//                @ByRef MatVector images, @ByRef MatBytePairVector masks);
//        public native void apply(int index, @ByVal CvPoint corner,
//                @InputMat CvArr image, @InputMat CvArr mask);

    }

    @Namespace("cv::detail") public static class GainCompensator extends ExposureCompensator {
        static { load(); }
        public GainCompensator() { allocate(); }
        public GainCompensator(Pointer p) { super(p); }
        private native void allocate();

//        public native void feed(@Const @StdVector("CvPoint,cv::Point") CvPoint corners,
//                @ByRef MatVector images, @ByRef MatBytePairVector masks);
//        public native void apply(int index, @ByVal CvPoint corner,
//                @InputMat CvArr image, @InputMat CvArr mask);

        public native @StdVector DoublePointer gains();
    }

    @Namespace("cv::detail") public static class BlocksGainCompensator extends ExposureCompensator {
        static { load(); }
        public BlocksGainCompensator() { allocate(); }
        public BlocksGainCompensator(int bl_width/*=32*/, int bl_height/*=32*/) { allocate(bl_width, bl_height); }
        public BlocksGainCompensator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int bl_width/*=32*/, int bl_height/*=32*/);

//        public native void feed(@Const @StdVector("CvPoint,cv::Point") CvPoint corners,
//                @ByRef MatVector images, @ByRef MatBytePairVector masks);
//        public native void apply(int index, @ByVal CvPoint corner,
//                @InputMat CvArr image, @InputMat CvArr mask);
    }


    // #include "detail/seam_finders.hpp"
    @Namespace("cv::detail") public static class SeamFinder extends Pointer {
        static { load(); }
        public SeamFinder() { }
        public SeamFinder(Pointer p) { super(p); }

        public /*abstract*/ native void find(@ByRef MatVector src,
                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);
    }

    @Namespace("cv::detail") public static class NoSeamFinder extends SeamFinder {
        static { load(); }
        public NoSeamFinder() { allocate(); }
        public NoSeamFinder(Pointer p) { super(p); }
        private native void allocate();

//        public native void find(@ByRef MatVector src,
//                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);
    }

    @Namespace("cv::detail") public static class PairwiseSeamFinder extends SeamFinder {
        static { load(); }
        public PairwiseSeamFinder() { }
        public PairwiseSeamFinder(Pointer p) { super(p); }

//        public native void find(@ByRef MatVector src,
//                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);

//        protected native void run();
//        protected /*abstract*/ native void findInPair(@Cast("size_t") long first, @Cast("size_t") long second, @ByVal CvRect roi);
//
//        protected native @ByRef MatVector images_();
//        protected native @ByRef MatVector sizes_();
//        protected native @ByRef MatVector corners_();
//        protected native @ByRef MatVector masks_();
    }

    @Namespace("cv::detail") public static class VoronoiSeamFinder extends PairwiseSeamFinder {
        static { load(); }
        public VoronoiSeamFinder() { allocate(); }
        public VoronoiSeamFinder(Pointer p) { super(p); }
        private native void allocate();

//        public native void find(@ByRef MatVector src,
//                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);
    }

    @Namespace("cv::detail") public static class DpSeamFinder extends SeamFinder {
        static { load(); }

        // enum CostFunction {
        public static final int COLOR = 0, COLOR_GRAD = 1;

        public DpSeamFinder() { allocate(); }
        public DpSeamFinder(int costFunc/*=COLOR*/) { allocate(costFunc); }
        public DpSeamFinder(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("cv::detail::DpSeamFinder::CostFunction") int costFunc);

        public native @Cast("cv::detail::DpSeamFinder::CostFunction") int costFunction();
        public native void setCostFunction(@Cast("cv::detail::DpSeamFinder::CostFunction") int val);

//        public native void find(@ByRef MatVector src,
//                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);
    }

    public interface GraphCutSeamFinderBase {
        public static final int COST_COLOR = 0, COST_COLOR_GRAD = 1;
    }

    @Namespace("cv::detail") public static class GraphCutSeamFinder extends SeamFinder implements GraphCutSeamFinderBase {
        static { load(); }
        public GraphCutSeamFinder() { allocate(); }
        public GraphCutSeamFinder(int cost_type/*=COST_COLOR_GRAD*/,
                float terminal_cost/*=10000*/, float bad_region_penalty/*=1000*/) {
            allocate(cost_type, terminal_cost, bad_region_penalty);
        }
        public GraphCutSeamFinder(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int cost_type/*=COST_COLOR_GRAD*/,
                float terminal_cost/*=10000*/, float bad_region_penalty/*=1000*/);

//        public native void find(@ByRef MatVector src,
//                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);
    }

    @Platform(not="android") @Namespace("cv::detail") public static class GraphCutSeamFinderGpu extends PairwiseSeamFinder implements GraphCutSeamFinderBase {
        static { load(); }
        public GraphCutSeamFinderGpu() { allocate(); }
        public GraphCutSeamFinderGpu(int cost_type/*=COST_COLOR_GRAD*/,
                float terminal_cost/*=10000*/, float bad_region_penalty/*=1000*/) {
            allocate(cost_type, terminal_cost, bad_region_penalty);
        }
        public GraphCutSeamFinderGpu(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int cost_type/*=COST_COLOR_GRAD*/,
                float terminal_cost/*=10000*/, float bad_region_penalty/*=1000*/);

//        public native void find(@ByRef MatVector src,
//                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector masks);
        public native void findInPair(@Cast("size_t") long first, @Cast("size_t") long second, @ByVal CvRect roi);
    }


    // #include "detail/blenders.hpp"
    @Namespace("cv::detail") public static class Blender extends Pointer {
        static { load(); }
        public Blender() { allocate(); }
        public Blender(Pointer p) { super(p); }
        private native void allocate();

        public static final int NO = 0, FEATHER = 1, MULTI_BAND = 2;
        public static native @Ptr Blender createDefault(int type);
        public static native @Ptr Blender createDefault(int type, @Cast("bool") boolean try_gpu/*=false*/);

        public native void prepare(@Const @StdVector("CvPoint,cv::Point") CvPoint corners,
                @Const @StdVector("CvSize,cv::Size") CvSize sizes);
        public native void prepare(@ByVal CvRect dst_roi);
        public native void feed(@InputMat IplImage img, @InputMat IplImage mask, @ByVal CvPoint tl);
        public native void blend(@OutputMat IplImage dst, @OutputMat IplImage dst_mask);

//        protected native @OutputMat CvMat dst_();
//        protected native @OutputMat CvMat dst_mask_();
//        protected native @ByVal CvRect dst_roi_();
    }

    @Namespace("cv::detail") public static class FeatherBlender extends Blender {
        static { load(); }
        public FeatherBlender() { allocate(); }
        public FeatherBlender(float sharpness/*=0.02*/) { allocate(sharpness); }
        public FeatherBlender(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float sharpness/*=0.02*/);

        public native float sharpness();
        public native void setSharpness(float val);

//        public native void prepare(@ByVal CvRect dst_roi);
//        public native void feed(@InputMat CvArr img, @InputMat CvArr mask, @ByVal CvPoint tl);
//        public native void blend(@InputMat CvArr dst, @InputMat CvArr dst_mask);

        public native @ByVal CvRect createWeightMaps(@ByRef MatVector masks,
                @Const @StdVector("CvPoint,cv::Point") CvPoint corners, @ByRef MatVector weight_maps);
    }

    @Namespace("cv::detail") public static class MultiBandBlender extends Blender {
        static { load(); }
        public MultiBandBlender() { allocate(); }
        public MultiBandBlender(@Cast("int") boolean try_gpu/*=false*/, int num_bands/*=5*/, int weight_type/*=CV_32F*/) {
            allocate(try_gpu, num_bands, weight_type);
        }
        public MultiBandBlender(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("int") boolean try_gpu/*=false*/, int num_bands/*=5*/, int weight_type/*=CV_32F*/);

        public native int numBands();
        public native void setNumBands(int val);

//        public native void prepare(@ByVal CvRect dst_roi);
//        public native void feed(@InputMat CvArr img, @InputMat CvArr mask, @ByVal CvPoint tl);
//        public native void blend(@InputMat CvArr dst, @InputMat CvArr dst_mask);
    }

    @Namespace("cv::detail") public static native void normalizeUsingWeightMap(@InputMat CvArr weight, @InputMat CvArr src);
    @Namespace("cv::detail") public static native void createWeightMap(@InputMat CvArr mask, float sharpness, @InputMat CvArr weight);

    @Namespace("cv::detail") public static native void createLaplacePyr(@InputMat CvArr img, int num_levels, @ByRef MatVector pyr);
    @Namespace("cv::detail") public static native void createLaplacePyrGpu(@InputMat CvArr img, int num_levels, @ByRef MatVector pyr);

    @Namespace("cv::detail") public static native void restoreImageFromLaplacePyr(@ByRef MatVector pyr);
    @Namespace("cv::detail") public static native void restoreImageFromLaplacePyrGpu(@ByRef MatVector pyr);


    // #include "stitcher.hpp"
    @Namespace("cv") public static class Stitcher extends Pointer {
        static { load(); }
        public Stitcher() { }
        public Stitcher(Pointer p) { super(p); }

        public static final int ORIG_RESOL = -1,
        /* enum Status */ OK = 0, ERR_NEED_MORE_IMGS = 1;

        public static native @ByVal Stitcher createDefault(@Cast("bool") boolean try_use_gpu/*=false*/);

        public native double registrationResol();
        public native void setRegistrationResol(double resol_mpx);

        public native double seamEstimationResol();
        public native void setSeamEstimationResol(double resol_mpx);

        public native double compositingResol();
        public native void setCompositingResol(double resol_mpx);

        public native double panoConfidenceThresh();
        public native void setPanoConfidenceThresh(double conf_thresh);

        public native @Cast("bool") boolean waveCorrection();
        public native void setWaveCorrection(@Cast("bool") boolean flag);

        public native @Cast("cv::detail::WaveCorrectKind") int waveCorrectKind();
        public native void setWaveCorrectKind(@Cast("cv::detail::WaveCorrectKind") int kind);

        public native @Const @Ptr FeaturesFinder featuresFinder();
        public native void setFeaturesFinder(@Ptr FeaturesFinder features_finder);

        public native @Const @Ptr FeaturesMatcher featuresMatcher();
        public native void setFeaturesMatcher(@Ptr FeaturesMatcher features_matcher);

        public native @OutputMat IplImage matchingMask();
        public native void setMatchingMask(IplImage mask);

        public native @Const @Ptr BundleAdjusterBase bundleAdjuster();
        public native void setBundleAdjuster(@Ptr BundleAdjusterBase bundle_adjuster);

        public native @Const @Ptr WarperCreator warper();
        public native void setWarper(@Ptr WarperCreator warper);

        public native @Const @Ptr ExposureCompensator exposureCompensator();
        public native void setExposureCompensator(@Ptr ExposureCompensator exposure_comp);

        public native @Const @Ptr SeamFinder seamFinder();
        public native void setSeamFinder(@Ptr SeamFinder seam_finder);

        public native @Const @Ptr Blender blender();
        public native void setBlender(@Ptr Blender blender);

        public native /* Status */ int estimateTransform(@ByRef MatVector images);
        public native /* Status */ int estimateTransform(@ByRef MatVector images, @ByRef RectVectorVector rois);

        public native /* Status */ int composePanorama(@OutputArray IplImage pano);
        public native /* Status */ int composePanorama(@ByRef MatVector images, @OutputArray IplImage pano);

        public native /* Status */ int stitch(@ByRef MatVector images, @OutputArray IplImage pano);
        public native /* Status */ int stitch(@ByRef MatVector images, @ByRef RectVectorVector rois, @OutputArray IplImage pano);

        public native @StdVector IntPointer component();
        public native @StdVector CameraParams cameras();
        public native double workScale();
    }
}
