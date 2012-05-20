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
 * This file is based on information found in imgproc/types_c.h, imgproc_c.h, and
 * imgproc.hpp of OpenCV 2.4.0, which are covered by the following copyright notice:
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
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.SizeTPointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByPtrPtr;
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

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/imgproc/imgproc_c.h>", "<opencv2/imgproc/imgproc.hpp>", "opencv_adapters.h"},
        link={"opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_imgproc240", "opencv_core240"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_imgproc {
    static { load(opencv_core.class); load(); }

    public static class CvConnectedComp extends Pointer {
        static { load(); }
        public CvConnectedComp() { allocate(); }
        public CvConnectedComp(int size) { allocateArray(size); }
        public CvConnectedComp(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvConnectedComp position(int position) {
            return (CvConnectedComp)super.position(position);
        }

        public native double area();           public native CvConnectedComp area(double area);
        public native @ByRef CvScalar value(); public native CvConnectedComp value(CvScalar value);
        public native @ByRef CvRect rect();    public native CvConnectedComp rect(CvRect rect);
        public native CvSeq contour();         public native CvConnectedComp contour(CvSeq contour);
    }

    public static final int
            CV_BLUR_NO_SCALE = 0,
            CV_BLUR = 1,
            CV_GAUSSIAN = 2,
            CV_MEDIAN = 3,
            CV_BILATERAL = 4;

    public static final int
            CV_GAUSSIAN_5x5 = 7;

    public static final int
            CV_SCHARR          = -1,
            CV_MAX_SOBEL_KSIZE = 7;

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

            CV_BayerBG2BGR_VNG =62,
            CV_BayerGB2BGR_VNG =63,
            CV_BayerRG2BGR_VNG =64,
            CV_BayerGR2BGR_VNG =65,

            CV_BayerBG2RGB_VNG =CV_BayerRG2BGR_VNG,
            CV_BayerGB2RGB_VNG =CV_BayerGR2BGR_VNG,
            CV_BayerRG2RGB_VNG =CV_BayerBG2BGR_VNG,
            CV_BayerGR2RGB_VNG =CV_BayerGB2BGR_VNG,

            CV_BGR2HSV_FULL = 66,
            CV_RGB2HSV_FULL = 67,
            CV_BGR2HLS_FULL = 68,
            CV_RGB2HLS_FULL = 69,

            CV_HSV2BGR_FULL = 70,
            CV_HSV2RGB_FULL = 71,
            CV_HLS2BGR_FULL = 72,
            CV_HLS2RGB_FULL = 73,

            CV_LBGR2Lab     = 74,
            CV_LRGB2Lab     = 75,
            CV_LBGR2Luv     = 76,
            CV_LRGB2Luv     = 77,

            CV_Lab2LBGR     = 78,
            CV_Lab2LRGB     = 79,
            CV_Luv2LBGR     = 80,
            CV_Luv2LRGB     = 81,

            CV_BGR2YUV      = 82,
            CV_RGB2YUV      = 83,
            CV_YUV2BGR      = 84,
            CV_YUV2RGB      = 85,

            CV_BayerBG2GRAY = 86,
            CV_BayerGB2GRAY = 87,
            CV_BayerRG2GRAY = 88,
            CV_BayerGR2GRAY = 89,

            CV_YUV2RGB_NV12 = 90,
            CV_YUV2BGR_NV12 = 91,
            CV_YUV2RGB_NV21 = 92,
            CV_YUV2BGR_NV21 = 93,
            CV_YUV420sp2RGB = CV_YUV2RGB_NV21,
            CV_YUV420sp2BGR = CV_YUV2BGR_NV21,

            CV_YUV2RGBA_NV12 = 94,
            CV_YUV2BGRA_NV12 = 95,
            CV_YUV2RGBA_NV21 = 96,
            CV_YUV2BGRA_NV21 = 97,
            CV_YUV420sp2RGBA = CV_YUV2RGBA_NV21,
            CV_YUV420sp2BGRA = CV_YUV2BGRA_NV21,

            CV_YUV2RGB_YV12 = 98,
            CV_YUV2BGR_YV12 = 99,
            CV_YUV2RGB_IYUV = 100,
            CV_YUV2BGR_IYUV = 101,
            CV_YUV2RGB_I420 = CV_YUV2RGB_IYUV,
            CV_YUV2BGR_I420 = CV_YUV2BGR_IYUV,
            CV_YUV420p2RGB = CV_YUV2RGB_YV12,
            CV_YUV420p2BGR = CV_YUV2BGR_YV12,

            CV_YUV2RGBA_YV12 = 102,
            CV_YUV2BGRA_YV12 = 103,
            CV_YUV2RGBA_IYUV = 104,
            CV_YUV2BGRA_IYUV = 105,
            CV_YUV2RGBA_I420 = CV_YUV2RGBA_IYUV,
            CV_YUV2BGRA_I420 = CV_YUV2BGRA_IYUV,
            CV_YUV420p2RGBA = CV_YUV2RGBA_YV12,
            CV_YUV420p2BGRA = CV_YUV2BGRA_YV12,

            CV_YUV2GRAY_420 = 106,
            CV_YUV2GRAY_NV21 = CV_YUV2GRAY_420,
            CV_YUV2GRAY_NV12 = CV_YUV2GRAY_420,
            CV_YUV2GRAY_YV12 = CV_YUV2GRAY_420,
            CV_YUV2GRAY_IYUV = CV_YUV2GRAY_420,
            CV_YUV2GRAY_I420 = CV_YUV2GRAY_420,
            CV_YUV420sp2GRAY = CV_YUV2GRAY_420,
            CV_YUV420p2GRAY = CV_YUV2GRAY_420,

            CV_YUV2RGB_UYVY = 107,
            CV_YUV2BGR_UYVY = 108,
            //CV_YUV2RGB_VYUY = 109,
            //CV_YUV2BGR_VYUY = 110,
            CV_YUV2RGB_Y422 = CV_YUV2RGB_UYVY,
            CV_YUV2BGR_Y422 = CV_YUV2BGR_UYVY,
            CV_YUV2RGB_UYNV = CV_YUV2RGB_UYVY,
            CV_YUV2BGR_UYNV = CV_YUV2BGR_UYVY,

            CV_YUV2RGBA_UYVY = 111,
            CV_YUV2BGRA_UYVY = 112,
            //CV_YUV2RGBA_VYUY = 113,
            //CV_YUV2BGRA_VYUY = 114,
            CV_YUV2RGBA_Y422 = CV_YUV2RGBA_UYVY,
            CV_YUV2BGRA_Y422 = CV_YUV2BGRA_UYVY,
            CV_YUV2RGBA_UYNV = CV_YUV2RGBA_UYVY,
            CV_YUV2BGRA_UYNV = CV_YUV2BGRA_UYVY,

            CV_YUV2RGB_YUY2 = 115,
            CV_YUV2BGR_YUY2 = 116,
            CV_YUV2RGB_YVYU = 117,
            CV_YUV2BGR_YVYU = 118,
            CV_YUV2RGB_YUYV = CV_YUV2RGB_YUY2,
            CV_YUV2BGR_YUYV = CV_YUV2BGR_YUY2,
            CV_YUV2RGB_YUNV = CV_YUV2RGB_YUY2,
            CV_YUV2BGR_YUNV = CV_YUV2BGR_YUY2,

            CV_YUV2RGBA_YUY2 = 119,
            CV_YUV2BGRA_YUY2 = 120,
            CV_YUV2RGBA_YVYU = 121,
            CV_YUV2BGRA_YVYU = 122,
            CV_YUV2RGBA_YUYV = CV_YUV2RGBA_YUY2,
            CV_YUV2BGRA_YUYV = CV_YUV2BGRA_YUY2,
            CV_YUV2RGBA_YUNV = CV_YUV2RGBA_YUY2,
            CV_YUV2BGRA_YUNV = CV_YUV2BGRA_YUY2,

            CV_YUV2GRAY_UYVY = 123,
            CV_YUV2GRAY_YUY2 = 124,
            //CV_YUV2GRAY_VYUY = CV_YUV2GRAY_UYVY,
            CV_YUV2GRAY_Y422 = CV_YUV2GRAY_UYVY,
            CV_YUV2GRAY_UYNV = CV_YUV2GRAY_UYVY,
            CV_YUV2GRAY_YVYU = CV_YUV2GRAY_YUY2,
            CV_YUV2GRAY_YUYV = CV_YUV2GRAY_YUY2,
            CV_YUV2GRAY_YUNV = CV_YUV2GRAY_YUY2,

            CV_COLORCVT_MAX  = 125;

    public static final int
            CV_INTER_NN        = 0,
            CV_INTER_LINEAR    = 1,
            CV_INTER_CUBIC     = 2,
            CV_INTER_AREA      = 3,
            CV_INTER_LANCZOS4  = 4,

            CV_WARP_FILL_OUTLIERS = 8,
            CV_WARP_INVERSE_MAP   = 16;

    public static final int
            CV_SHAPE_RECT     = 0,
            CV_SHAPE_CROSS    = 1,
            CV_SHAPE_ELLIPSE  = 2,
            CV_SHAPE_CUSTOM   = 100;

    public static final int
            CV_MOP_ERODE       = 0,
            CV_MOP_DILATE      = 1,
            CV_MOP_OPEN        = 2,
            CV_MOP_CLOSE       = 3,
            CV_MOP_GRADIENT    = 4,
            CV_MOP_TOPHAT      = 5,
            CV_MOP_BLACKHAT    = 6;


    public static class CvMoments extends Pointer {
        static { load(); }
        public CvMoments() { allocate(); }
        public CvMoments(int size) { allocateArray(size); }
        public CvMoments(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMoments position(int position) {
            return (CvMoments)super.position(position);
        }

        public static ThreadLocal<CvMoments> createThreadLocal() {
            return new ThreadLocal<CvMoments>() {
                @Override protected CvMoments initialValue() {
                    return new CvMoments();
                }
            };
        }

        public native double m00(); public native CvMoments m00(double m00);
        public native double m10(); public native CvMoments m10(double m10);
        public native double m01(); public native CvMoments m01(double m01);
        public native double m20(); public native CvMoments m20(double m20);
        public native double m11(); public native CvMoments m11(double m11);
        public native double m02(); public native CvMoments m02(double m02);
        public native double m30(); public native CvMoments m30(double m30);
        public native double m21(); public native CvMoments m21(double m21);
        public native double m12(); public native CvMoments m12(double m12);
        public native double m03(); public native CvMoments m03(double m03);

        public native double mu20(); public native CvMoments mu20(double mu20);
        public native double mu11(); public native CvMoments mu11(double mu11);
        public native double mu02(); public native CvMoments mu02(double mu02);
        public native double mu30(); public native CvMoments mu30(double mu30);
        public native double mu21(); public native CvMoments mu21(double mu21);
        public native double mu12(); public native CvMoments mu12(double mu12);
        public native double mu03(); public native CvMoments mu03(double mu03);

        public native double inv_sqrt_m00(); public native CvMoments inv_sqrt_m00(double inv_sqrt_m00);
    }

    public static class CvHuMoments extends Pointer {
        static { load(); }
        public CvHuMoments() { allocate(); }
        public CvHuMoments(int size) { allocateArray(size); }
        public CvHuMoments(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHuMoments position(int position) {
            return (CvHuMoments)super.position(position);
        }

        public native double hu1(); public native CvHuMoments hu1(double hu1);
        public native double hu2(); public native CvHuMoments hu2(double hu2);
        public native double hu3(); public native CvHuMoments hu3(double hu3);
        public native double hu4(); public native CvHuMoments hu4(double hu4);
        public native double hu5(); public native CvHuMoments hu5(double hu5);
        public native double hu6(); public native CvHuMoments hu6(double hu6);
        public native double hu7(); public native CvHuMoments hu7(double hu7);
    }


    public static final int
            CV_TM_SQDIFF        = 0,
            CV_TM_SQDIFF_NORMED = 1,
            CV_TM_CCORR         = 2,
            CV_TM_CCORR_NORMED  = 3,
            CV_TM_CCOEFF        = 4,
            CV_TM_CCOEFF_NORMED = 5;

    public static class CvDistanceFunction extends FunctionPointer {
        static { load(); }
        public    CvDistanceFunction(Pointer p) { super(p); }
        protected CvDistanceFunction() { allocate(); }
        protected final native void allocate();
        public native float call(@Const FloatPointer a, @Const FloatPointer b, Pointer user_param);
    }

    public static final int
            CV_RETR_EXTERNAL = 0,
            CV_RETR_LIST     = 1,
            CV_RETR_CCOMP    = 2,
            CV_RETR_TREE     = 3,
            CV_RETR_FLOODFILL= 4,

            CV_CHAIN_CODE              = 0,
            CV_CHAIN_APPROX_NONE       = 1,
            CV_CHAIN_APPROX_SIMPLE     = 2,
            CV_CHAIN_APPROX_TC89_L1    = 3,
            CV_CHAIN_APPROX_TC89_KCOS  = 4,
            CV_LINK_RUNS               = 5;

    @Opaque public static class CvContourScanner extends Pointer {
        static { load(); }
        public CvContourScanner() { }
        public CvContourScanner(Pointer p) { super(p); }
    }

    public static class CvChainPtReader extends CvSeqReader {
        static { load(); }
        public CvChainPtReader() { allocate(); }
        public CvChainPtReader(int size) { allocateArray(size); }
        public CvChainPtReader(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvChainPtReader position(int position) {
            return (CvChainPtReader)super.position(position);
        }

        public native byte           code();               public native CvChainPtReader code(byte code);
        public native @ByRef CvPoint pt();                 public native CvChainPtReader pt(CvPoint pt);
        public native byte/*[8][2]*/ deltas(int i, int j); public native CvChainPtReader deltas(int i, int j, byte deltas);
    }

    public static native void CV_INIT_3X3_DELTAS(int[] deltas, int step, int nch);


    public static class CvSubdiv2DEdge extends SizeTPointer {
        static { load(); }
        public CvSubdiv2DEdge() { super(1); }
        public CvSubdiv2DEdge(int size) { super(size); }
        public CvSubdiv2DEdge(Pointer p) { super(p); }
    }

    public static final int CV_SUBDIV2D_VIRTUAL_POINT_FLAG = (1 << 30);

    public static class CvQuadEdge2D extends Pointer {
        static { load(); }
        public CvQuadEdge2D() { allocate(); }
        public CvQuadEdge2D(int size) { allocateArray(size); }
        public CvQuadEdge2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvQuadEdge2D position(int position) {
            return (CvQuadEdge2D)super.position(position);
        }

        public native int flags();                           public native CvQuadEdge2D flags(int flags);
        public native CvSubdiv2DPoint/*[4]*/ pt(int i);      public native CvQuadEdge2D pt(int i, CvSubdiv2DPoint pt);
        public native @Cast("CvSubdiv2DEdge") long next(int i); public native CvQuadEdge2D next(int i, long next);

        public @Cast("CvSubdiv2DEdge") long CV_SUBDIV2D_NEXT_EDGE(@Cast("CvSubdiv2DEdge") long edge) {
            return next((int)edge & 3);
        }
        public @Cast("CvSubdiv2DEdge") long cvSubdiv2DNextEdge(@Cast("CvSubdiv2DEdge") long edge) {
            return CV_SUBDIV2D_NEXT_EDGE(edge);
        }
        public @Cast("CvSubdiv2DEdge") long cvSubdiv2DGetEdge(@Cast("CvSubdiv2DEdge") long edge, @Cast("CvNextEdgeType") int type) {
            edge = next(((int)edge + type) & 3);
            return (edge & ~3) + ((edge + (type >> 4)) & 3);
        }
        public static @Cast("CvSubdiv2DEdge") long cvSubdiv2DRotateEdge(@Cast("CvSubdiv2DEdge") long edge, int rotate) {
            return (edge & ~3) + ((edge + rotate) & 3);
        }
        public CvSubdiv2DPoint cvSubdiv2DEdgeOrg(@Cast("CvSubdiv2DEdge") long edge) {
            return pt((int)edge & 3);
        }
        public CvSubdiv2DPoint cvSubdiv2DEdgeDst(@Cast("CvSubdiv2DEdge") long edge) {
            return pt(((int)edge + 2) & 3);
        }
        public static @Cast("CvSubdiv2DEdge") long cvSubdiv2DSymEdge(@Cast("CvSubdiv2DEdge") long edge) {
            return edge ^ 2;
        }

    }

    public static class CvSubdiv2DPoint extends Pointer {
        static { load(); }
        public CvSubdiv2DPoint() { allocate(); }
        public CvSubdiv2DPoint(int size) { allocateArray(size); }
        public CvSubdiv2DPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSubdiv2DPoint position(int position) {
            return (CvSubdiv2DPoint)super.position(position);
        }

        public native int                          flags(); public native CvSubdiv2DPoint flags(int first);
        public native @Cast("CvSubdiv2DEdge") long first(); public native CvSubdiv2DPoint first(long first);
        public native @ByRef CvPoint2D32f             pt(); public native CvSubdiv2DPoint pt(CvPoint2D32f pt);
        public native int                             id(); public native CvSubdiv2DPoint id(int id);
    }

    public static class CvSubdiv2D extends CvGraph {
        static { load(); }
        public CvSubdiv2D() { allocate(); }
        public CvSubdiv2D(int size) { allocateArray(size); }
        public CvSubdiv2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSubdiv2D position(int position) {
            return (CvSubdiv2D)super.position(position);
        }

        public native int quad_edges();                           public native CvSubdiv2D quad_edges(int quad_edges);
        public native int is_geometry_valid();                    public native CvSubdiv2D is_geometry_valid(int is_geometry_valid);
        public native @Cast("CvSubdiv2DEdge") long recent_edge(); public native CvSubdiv2D recent_edge(long recent_edge);
        public native @ByRef CvPoint2D32f topleft();              public native CvSubdiv2D topleft(CvPoint2D32f topleft);
        public native @ByRef CvPoint2D32f bottomright();          public native CvSubdiv2D bottomright(CvPoint2D32f bottomright);
    }

    // enum CvSubdiv2DPointLocation
    public static final int
        CV_PTLOC_ERROR = -2,
        CV_PTLOC_OUTSIDE_RECT = -1,
        CV_PTLOC_INSIDE = 0,
        CV_PTLOC_VERTEX = 1,
        CV_PTLOC_ON_EDGE = 2;

    // enum CvNextEdgeType
    public static final int
        CV_NEXT_AROUND_ORG   = 0x00,
        CV_NEXT_AROUND_DST   = 0x22,
        CV_PREV_AROUND_ORG   = 0x11,
        CV_PREV_AROUND_DST   = 0x33,
        CV_NEXT_AROUND_LEFT  = 0x13,
        CV_NEXT_AROUND_RIGHT = 0x31,
        CV_PREV_AROUND_LEFT  = 0x20,
        CV_PREV_AROUND_RIGHT = 0x02;

    public static native @ByVal CvSubdiv2DEdge CV_SUBDIV2D_NEXT_EDGE(@ByVal CvSubdiv2DEdge edge);


    public static final int
            CV_POLY_APPROX_DP = 0;

    public static final int
            CV_CONTOURS_MATCH_I1 = 1,
            CV_CONTOURS_MATCH_I2 = 2,
            CV_CONTOURS_MATCH_I3 = 3;

    public static final int
            CV_CLOCKWISE         = 1,
            CV_COUNTER_CLOCKWISE = 2;


    public static class CvConvexityDefect extends Pointer {
        static { load(); }
        public CvConvexityDefect() { allocate(); }
        public CvConvexityDefect(int size) { allocateArray(size); }
        public CvConvexityDefect(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvConvexityDefect position(int position) {
            return (CvConvexityDefect)super.position(position);
        }

        public native CvPoint start();       public native CvConvexityDefect start(CvPoint start);
        public native CvPoint end();         public native CvConvexityDefect end(CvPoint end);
        public native CvPoint depth_point(); public native CvConvexityDefect depth_point(CvPoint depth_point);
        public native float depth();         public native CvConvexityDefect depth(float depth);
    }


    public static final int
            CV_COMP_CORREL        = 0,
            CV_COMP_CHISQR        = 1,
            CV_COMP_INTERSECT     = 2,
            CV_COMP_BHATTACHARYYA = 3;

    public static final int
            CV_DIST_MASK_3 =  3,
            CV_DIST_MASK_5 =  5,
            CV_DIST_MASK_PRECISE = 0,

            CV_DIST_LABEL_CCOMP = 0,
            CV_DIST_LABEL_PIXEL = 1,

            CV_DIST_USER   = -1,
            CV_DIST_L1     = 1,
            CV_DIST_L2     = 2,
            CV_DIST_C      = 3,
            CV_DIST_L12    = 4,
            CV_DIST_FAIR   = 5,
            CV_DIST_WELSCH = 6,
            CV_DIST_HUBER  = 7;

    public static final int
            CV_THRESH_BINARY     = 0,
            CV_THRESH_BINARY_INV = 1,
            CV_THRESH_TRUNC      = 2,
            CV_THRESH_TOZERO     = 3,
            CV_THRESH_TOZERO_INV = 4,
            CV_THRESH_MASK       = 7,
            CV_THRESH_OTSU       = 8;

    public static final int
            CV_ADAPTIVE_THRESH_MEAN_C     = 0,
            CV_ADAPTIVE_THRESH_GAUSSIAN_C = 1;

    public static final int
            CV_FLOODFILL_FIXED_RANGE = (1 << 16),
            CV_FLOODFILL_MASK_ONLY   = (1 << 17);

    public static final int
            CV_CANNY_L2_GRADIENT = (1 << 31);

    public static final int
            CV_HOUGH_STANDARD = 0,
            CV_HOUGH_PROBABILISTIC = 1,
            CV_HOUGH_MULTI_SCALE = 2,
            CV_HOUGH_GRADIENT = 3;

    @Opaque public static class CvFeatureTree extends Pointer {
        static { load(); }
        public CvFeatureTree() { }
        public CvFeatureTree(Pointer p) { super(p); }
    }


    public static native void cvAcc(CvArr image, CvArr sum, CvArr mask/*=null*/);
    public static native void cvSquareAcc(CvArr image, CvArr sqsum, CvArr mask/*=null*/);
    public static native void cvMultiplyAcc(CvArr image1, CvArr image2, CvArr acc, CvArr mask/*=null*/);
    public static native void cvRunningAvg(CvArr image, CvArr acc, double alpha, CvArr mask/*=null*/);


    public static native void cvCopyMakeBorder(CvArr src, CvArr dst, @ByVal CvPoint offset,
            int bordertype, @ByVal CvScalar value/*=cvScalarAll(0)*/);
    public static void cvSmooth(CvArr src, CvArr dst, int smoothtype/*=CV_GAUSSIAN*/, int size1/*=3*/) {
        cvSmooth(src, dst, smoothtype, size1, 0, 0, 0);
    }
    public static native void cvSmooth(CvArr src, CvArr dst, int smoothtype/*=CV_GAUSSIAN*/,
            int size1/*=3*/, int size2/*=0*/, double sigma1/*=0*/, double sigma2/*=0*/);
    public static native void cvFilter2D(CvArr src, CvArr dst,
            CvMat kernel, @ByVal CvPoint anchor/*=cvPoint(-1,-1)*/);
    public static native void cvIntegral(CvArr image, CvArr sum,
            CvArr sqsum/*=null*/, CvArr tilted_sum/*=null*/);
    public static native void cvPyrDown(CvArr src, CvArr dst, int filter/*=CV_GAUSSIAN_5x5*/);
    public static native void cvPyrUp(CvArr src, CvArr dst, int filter/*=CV_GAUSSIAN_5x5*/);
    public static native CvMatArray cvCreatePyramid(CvArr img, int extra_layers, double rate,
            CvSize layer_sizes/*=null*/, CvArr bufarr/*=null*/, int calc/*=1*/, int filter/*=CV_GAUSSIAN_5x5*/);
    public static native void cvReleasePyramid(@ByPtrPtr CvMatArray pyramid, int extra_layers);
    public static native void cvPyrMeanShiftFiltering(CvArr src, CvArr dst, double sp, double sr, int max_level/*=1*/,
            @ByVal CvTermCriteria termcrit/*=cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 5, 1)*/);
    public static native void cvWatershed(CvArr image, CvArr markers);
    public static native void cvSobel(CvArr src, CvArr dst, int xorder, int yorder, int aperture_size/*=3*/);
    public static native void cvLaplace(CvArr src, CvArr dst, int aperture_size/*=3*/);
    public static native void cvCvtColor(CvArr src, CvArr dst, int code);
    public static void cvResize(CvArr src, CvArr dst) {
        cvResize(src, dst, CV_INTER_LINEAR);
    }
    public static native void cvResize(CvArr src, CvArr dst, int interpolation/*=CV_INTER_LINEAR*/);
    public static void cvWarpAffine(CvArr src, CvArr dst, CvMat map_matrix) {
        cvWarpAffine(src, dst, map_matrix, CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
    }
    public static native void cvWarpAffine(CvArr src, CvArr dst, CvMat map_matrix,
            int flags/*=CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS*/,
            @ByVal CvScalar fillval/*=cvScalarAll(0)*/);
    public static native CvMat cvGetAffineTransform(CvPoint2D32f src, CvPoint2D32f dst,
            CvMat map_matrix);
    public static native CvMat cvGetAffineTransform(@Cast("CvPoint2D32f*") float[] src,
            @Cast("CvPoint2D32f*") float[] dst, CvMat map_matrix);
    public static native CvMat cv2DRotationMatrix(@ByVal CvPoint2D32f center, double angle,
            double scale, CvMat map_matrix);
    public static void cvWarpPerspective(CvArr src, CvArr dst, CvMat map_matrix) {
        cvWarpPerspective(src, dst, map_matrix, CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
    }
    public static native void cvWarpPerspective(CvArr src, CvArr dst, CvMat map_matrix,
            int flags/*=CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS*/,
            @ByVal CvScalar fillval/*=cvScalarAll(0)*/);
    public static native CvMat cvGetPerspectiveTransform(CvPoint2D32f src, CvPoint2D32f dst,
            CvMat map_matrix);
    public static native CvMat cvGetPerspectiveTransform(@Cast("CvPoint2D32f*") float[] src,
            @Cast("CvPoint2D32f*") float[] dst, CvMat map_matrix);
    public static native void cvRemap(CvArr src, CvArr dst, CvArr mapx, CvArr mapy,
            int flags/*=CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS*/,
            @ByVal CvScalar fillval/*=cvScalarAll(0)*/);
    public static native void cvConvertMaps(CvArr mapx, CvArr mapy, CvArr mapxy, CvArr mapalpha);
    public static native void cvLogPolar(CvArr src, CvArr dst, @ByVal CvPoint2D32f center,
            double M, int flags/*=CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS*/);
    public static native void cvLinearPolar(CvArr src, CvArr dst, @ByVal CvPoint2D32f center,
            double maxRadius, int flags/*=CV_INTER_LINEAR+CV_WARP_FILL_OUTLIERS*/);
    public static void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix, CvMat distortion_coeffs) {
        cvUndistort2(src, dst, intrinsic_matrix, distortion_coeffs, null);
    }
    public static native void cvUndistort2(CvArr src, CvArr dst, CvMat intrinsic_matrix,
            CvMat distortion_coeffs, CvMat new_camera_matrix/*=null*/);
    public static native void cvInitUndistortMap(CvMat intrinsic_matrix, CvMat distortion_coeffs,
            CvArr mapx, CvArr mapy);
    public static native void cvInitUndistortRectifyMap(CvMat camera_matrix, CvMat dist_coeffs,
            CvMat R, CvMat new_camera_matrix, CvArr mapx, CvArr mapy);
    public static native void cvUndistortPoints(CvMat src, CvMat dst, CvMat camera_matrix,
            CvMat dist_coeffs, CvMat R/*=null*/, CvMat P/*=null*/);

    public static class IplConvKernel extends Pointer {
        static { load(); }
        public IplConvKernel() { allocate(); }
        public IplConvKernel(int size) { allocateArray(size); }
        public IplConvKernel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public IplConvKernel position(int position) {
            return (IplConvKernel)super.position(position);
        }

        public static IplConvKernel create(int cols, int rows,
                int anchor_x, int anchor_y, int shape, int[] values/*=null*/) {
            IplConvKernel p = cvCreateStructuringElementEx(cols, rows,
                    anchor_x, anchor_y, shape, values);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends IplConvKernel implements Deallocator {
            ReleaseDeallocator(IplConvKernel p) { super(p); }
            @Override public void deallocate() { cvReleaseStructuringElement(this); }
        }

        public native int nCols();         public native IplConvKernel nCols(int nCols);
        public native int nRows();         public native IplConvKernel nRows(int nRows);
        public native int anchorX();       public native IplConvKernel anchorX(int anchorX);
        public native int anchorY();       public native IplConvKernel anchorY(int anchorY);
        public native IntPointer values(); public native IplConvKernel values(IntPointer values);
        public native int nShiftR();       public native IplConvKernel nShiftR(int nShiftR);
    }

    public static class IplConvKernelFP extends Pointer {
        static { load(); }
        public IplConvKernelFP() { allocate(); }
        public IplConvKernelFP(int size) { allocateArray(size); }
        public IplConvKernelFP(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public IplConvKernelFP position(int position) {
            return (IplConvKernelFP)super.position(position);
        }

        public native int nCols();           public native IplConvKernelFP nCols(int nCols);
        public native int nRows();           public native IplConvKernelFP nRows(int nRows);
        public native int anchorX();         public native IplConvKernelFP anchorX(int anchorX);
        public native int anchorY();         public native IplConvKernelFP anchorY(int anchorY);
        public native FloatPointer values(); public native IplConvKernelFP values(FloatPointer values);
    }

    public static native IplConvKernel cvCreateStructuringElementEx(int cols, int rows,
            int anchor_x, int anchor_y, int shape, int[] values/*=null*/);
    public static native void cvReleaseStructuringElement(@ByPtrPtr IplConvKernel element);
    public static native void cvErode(CvArr src, CvArr dst,
            IplConvKernel element/*=null*/, int iterations/*=1*/);
    public static native void cvDilate(CvArr src, CvArr dst,
            IplConvKernel element/*=null*/, int iterations/*=1*/);
    public static native void cvMorphologyEx(CvArr src, CvArr dst, CvArr temp,
            IplConvKernel element, int operation, int iterations/*=1*/);

    public static native void cvMoments(CvArr arr, CvMoments moments, int binary/*=0*/);
    public static native double cvGetSpatialMoment(CvMoments moments, int x_order, int y_order);
    public static native double cvGetCentralMoment(CvMoments moments, int x_order, int y_order);
    public static native double cvGetNormalizedCentralMoment(CvMoments moments, int x_order, int y_order);
    public static native void cvGetHuMoments(CvMoments moments, CvHuMoments hu_moments);


    public static native int cvSampleLine(CvArr image, @ByVal CvPoint pt1, @ByVal CvPoint pt2,
            Pointer buffer, int connectivity/*=8*/);
    public static native void cvGetRectSubPix(CvArr src, CvArr dst, @ByVal CvPoint2D32f center);
    public static native void cvGetQuadrangleSubPix(CvArr src, CvArr dst, CvMat map_matrix);
    public static native void cvMatchTemplate(CvArr image, CvArr templ, CvArr result, int method);
    public static native float cvCalcEMD2(CvArr signature1, CvArr signature2, int distance_type,
            CvDistanceFunction distance_func/*=null*/, CvArr cost_matrix/*=null*/,
            CvArr flow/*=null*/, float[] lower_bound/*=null*/, Pointer userdata/*=null*/);


    public static int cvFindContours(CvArr image, CvMemStorage storage, @ByPtrPtr CvSeq first_contour,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/, int method/*=CV_CHAIN_APPROX_SIMPLE*/) {
        return cvFindContours(image, storage, first_contour, header_size, mode, method, CvPoint.ZERO);
    }
    public static native int cvFindContours(CvArr image, CvMemStorage storage, @ByPtrPtr CvSeq first_contour,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/,
            int method/*=CV_CHAIN_APPROX_SIMPLE*/, @ByVal CvPoint offset/*=cvPoint(0,0)*/);
    public static CvContourScanner cvStartFindContours(CvArr image, CvMemStorage storage,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/, int method/*=CV_CHAIN_APPROX_SIMPLE*/) {
        return cvStartFindContours(image, storage, header_size, mode, method, CvPoint.ZERO);
    }
    public static native @ByVal CvContourScanner cvStartFindContours(CvArr image, CvMemStorage storage,
            int header_size/*=sizeof(CvContour)*/, int mode/*=CV_RETR_LIST*/,
            int method/*=CV_CHAIN_APPROX_SIMPLE*/, @ByVal CvPoint offset/*=cvPoint(0,0)*/);
    public static native CvSeq cvFindNextContour(@ByVal CvContourScanner scanner);
    public static native void cvSubstituteContour(@ByVal CvContourScanner scanner, CvSeq new_contour);
    public static native CvSeq cvEndFindContours(CvContourScanner scanner);

    public static native CvSeq cvApproxChains(CvSeq src_seq, CvMemStorage storage, int method/*=CV_CHAIN_APPROX_SIMPLE*/,
            double parameter/*=0*/, int minimal_perimeter/*=0*/, int recursive/*=0*/);
    public static native void cvStartReadChainPoints(CvChain chain, CvChainPtReader reader);
    public static native @ByVal CvPoint cvReadChainPoint(CvChainPtReader reader);


    public static native CvSeq cvApproxPoly(Pointer src_seq, int header_size, CvMemStorage storage,
            int method/*=CV_POLY_APPROX_DP*/, double parameter, int parameter2/*=0*/);
    public static native double cvArcLength(Pointer curve, @ByVal CvSlice slice/*=CV_WHOLE_SEQ*/,
            int is_closed/*=-1*/);
    public static double cvContourPerimeter(Pointer contour) {
        return cvArcLength(contour, CV_WHOLE_SEQ, 1);
    }
    public static native @ByVal CvRect cvBoundingRect(CvArr points, int update/*=0*/);
    public static native double cvContourArea(CvArr contour,
            @ByVal CvSlice slice/*=CV_WHOLE_SEQ*/, int oriented/*=0*/);
    public static native @ByVal CvBox2D cvMinAreaRect2(CvArr points, CvMemStorage storage/*=null*/);
    public static native int cvMinEnclosingCircle(CvArr points, CvPoint2D32f center, float[] radius);
    public static native int cvMinEnclosingCircle(CvArr points, @Cast("CvPoint2D32f*") float[] center, float[] radius);
    public static native double cvMatchShapes(Pointer object1, Pointer object2, int method, double parameter/*=0*/);
    public static native CvSeq cvConvexHull2(CvArr input, Pointer hull_storage/*=null*/,
            int orientation/*=CV_CLOCKWISE*/, int return_points/*=0*/);
    public static native int cvCheckContourConvexity(CvArr contour);
    public static native CvSeq cvConvexityDefects(CvArr contour, CvArr convexhull, CvMemStorage storage/*=null*/);
    public static native @ByVal CvBox2D cvFitEllipse2(CvArr points);
    public static native @ByVal CvRect cvMaxRect(CvRect rect1, CvRect rect2);
    public static native void cvBoxPoints(@ByVal CvBox2D box, CvPoint2D32f pt/*[4]*/);
    public static native void cvBoxPoints(@ByVal CvBox2D box, @Cast("CvPoint2D32f*") float[/*8*/] pt);
    public static native CvSeq cvPointSeqFromMat(int seq_kind, CvArr mat, CvContour contour_header, CvSeqBlock block);
    public static native double cvPointPolygonTest(CvArr contour, @ByVal CvPoint2D32f pt, int measure_dist);


    // typedef int CvHistType;

    public static final int
            CV_HIST_MAGIC_VAL     = 0x42450000,
            CV_HIST_UNIFORM_FLAG  = (1 << 10),

            CV_HIST_RANGES_FLAG   = (1 << 11),

            CV_HIST_ARRAY         = 0,
            CV_HIST_SPARSE        = 1,
            CV_HIST_TREE          = CV_HIST_SPARSE,

            CV_HIST_UNIFORM       = 1;

    public static class CvHistogram extends Pointer {
        static { load(); }
        public CvHistogram() { allocate(); }
        public CvHistogram(int size) { allocateArray(size); }
        public CvHistogram(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHistogram position(int position) {
            return (CvHistogram)super.position(position);
        }

        public static CvHistogram create(int dims, int[] sizes, int type,
                float[][] ranges/*=null*/, int uniform/*=1*/) {
            CvHistogram h = cvCreateHist(dims, sizes, type, ranges, uniform);
            if (h != null) {
                h.deallocator(new ReleaseDeallocator(h));
            }
            return h;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvHistogram implements Deallocator {
            ReleaseDeallocator(CvHistogram p) { super(p); }
            @Override public void deallocate() { cvReleaseHist(this); }
        }

        public native @Cast("CvHistType") int type();                public native CvHistogram type(int type);
        public native CvArr bins();                                  public native CvHistogram bins(CvArr bins);
        public native float/*[CV_MAX_DIM][2]*/ thresh(int i, int j); public native CvHistogram thresh(int i, int j, float thresh);
        public native @Cast("float**") PointerPointer thresh2();     public native CvHistogram thresh2(PointerPointer thresh2);
        public native @ByRef CvMatND mat();                          public native CvHistogram mat(CvMatND mat);
    }

    public static boolean CV_IS_HIST(CvArr hist) {
        CvHistogram h = new CvHistogram(hist);
        return hist != null && (h.type() & CV_MAGIC_MASK) == CV_HIST_MAGIC_VAL && h.bins() != null;
    }
    public static boolean CV_IS_UNIFORM_HIST(CvHistogram hist) {
        return (hist.type() & CV_HIST_UNIFORM_FLAG) != 0;
    }
    public static boolean CV_IS_SPARSE_HIST(CvHistogram hist) {
        return CV_IS_SPARSE_MAT(hist.bins());
    }
    public static boolean CV_HIST_HAS_RANGES(CvHistogram hist) {
        return (hist.type() & CV_HIST_RANGES_FLAG) != 0;
    }

    public static CvHistogram cvCreateHist(int dims, int[] sizes, int type,
            float[][] ranges/*=null*/, int uniform/*=1*/) {
        return cvCreateHist(dims, sizes, type, ranges == null ? null : new PointerPointer(ranges), uniform);
    }
    public static native CvHistogram cvCreateHist(int dims, int[] sizes, int type,
            @Cast("float**") PointerPointer ranges/*=null*/, int uniform/*=1*/);
    public static void cvSetHistBinRanges(CvHistogram hist,
            float[][] ranges, int uniform/*=1*/) {
        cvSetHistBinRanges(hist, ranges == null ? null : new PointerPointer(ranges), uniform);
    }
    public static native void cvSetHistBinRanges(CvHistogram hist,
            @Cast("float**") PointerPointer ranges, int uniform/*=1*/);

    public static CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            float[] data, float[][] ranges/*=null*/, int uniform/*=1*/) {
        return cvMakeHistHeaderForArray(dims, sizes, hist, data, ranges == null ? null : new PointerPointer(ranges), uniform);
    }
    public static native CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            float[] data, @Cast("float**") PointerPointer ranges/*=null*/, int uniform/*=1*/);
    public static CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            FloatPointer data, float[][] ranges/*=null*/, int uniform/*=1*/) {
        return cvMakeHistHeaderForArray(dims, sizes, hist, data, ranges == null ? null : new PointerPointer(ranges), uniform);
    }
    public static native CvHistogram cvMakeHistHeaderForArray(int dims, int[] sizes, CvHistogram hist,
            FloatPointer data, @Cast("float**") PointerPointer ranges/*=null*/, int uniform/*=1*/);

    public static native void cvReleaseHist(@ByPtrPtr CvHistogram hist);
    public static native void cvClearHist(CvHistogram hist);
    public static native void cvGetMinMaxHistValue(CvHistogram hist,
            float[] min_value, float[] max_value, int[] min_idx/*=null*/, int[] max_idx/*=null*/);
    public static native void cvNormalizeHist(CvHistogram hist, double factor);
    public static native void cvThreshHist(CvHistogram hist, double threshold);
    public static native double cvCompareHist(CvHistogram hist1, CvHistogram hist2, int method);
    public static native void cvCopyHist(CvHistogram src, @ByPtrPtr CvHistogram dst);
    public static native void cvCalcBayesianProb(@ByPtrPtr CvHistogram src, int number, @ByPtrPtr CvHistogram dst);

    public static void cvCalcArrHist(CvArr[] arr, CvHistogram hist, int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(new CvArrArray(arr), hist, accumulate, mask);
    }
    public static native void cvCalcArrHist(CvArrArray arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/);
    public static void cvCalcHist(IplImage[] arr, CvHistogram hist, int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcHist(new IplImageArray(arr), hist, accumulate, mask);
    }
    public static void cvCalcHist(IplImageArray arr, CvHistogram hist,
            int accumulate/*=0*/, CvArr mask/*=null*/) {
        cvCalcArrHist(arr, hist, accumulate, mask);
    }

    public static void cvCalcArrBackProject(CvArr[] image, CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(new CvArrArray(image), dst, hist);
    }
    public static native void cvCalcArrBackProject(CvArrArray image,
            CvArr dst, CvHistogram hist);
    public static void cvCalcBackProject(IplImage[] image, CvArr dst, CvHistogram hist) {
        cvCalcBackProject(new IplImageArray(image), dst, hist);
    }
    public static void cvCalcBackProject(IplImageArray image,
            CvArr dst, CvHistogram hist) {
        cvCalcArrBackProject(image, dst, hist);
    }

    public static void cvCalcArrBackProjectPatch(CvArr[] image,
            CvArr dst, @ByVal CvSize range, CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(new CvArrArray(image), dst, range, hist, method, factor);
    }
    public static native void cvCalcArrBackProjectPatch(CvArrArray image,
            CvArr dst, @ByVal CvSize range, CvHistogram hist, int method, double factor);
    public static void cvCalcBackProjectPatch(IplImage[] image,
            CvArr dst, @ByVal CvSize range, CvHistogram hist, int method, double factor) {
        cvCalcBackProjectPatch(new IplImageArray(image), dst, range, hist, method, factor);
    }
    public static void cvCalcBackProjectPatch(IplImageArray image,
            CvArr dst, CvSize range, CvHistogram hist, int method, double factor) {
        cvCalcArrBackProjectPatch(image, dst, range, hist, method, factor);
    }

    public static native void cvCalcProbDensity(CvHistogram hist1, CvHistogram hist2,
            CvHistogram dst_hist, double scale/*=255*/);
    public static native void cvEqualizeHist(CvArr src, CvArr dst);

    public static native void cvDistTransform(CvArr src, CvArr dst, int distance_type/*=CV_DIST_L2*/,
            int mask_size/*=3*/, FloatPointer mask/*=null*/, CvArr labels/*=null*/, int labelType/*=CV_DIST_LABEL_CCOMP*/);
    public static native double cvThreshold(CvArr src, CvArr dst, double threshold,
            double max_value, int threshold_type);
    public static native void cvAdaptiveThreshold(CvArr src, CvArr dst, double max_value,
            int adaptive_method/*=CV_ADAPTIVE_THRESH_MEAN_C*/, int threshold_type/*=CV_THRESH_BINARY*/,
            int block_size/*=3*/, double param1/*=5*/);
    public static native void cvFloodFill(CvArr image, @ByVal CvPoint seed_point, @ByVal CvScalar new_val,
            @ByVal CvScalar lo_diff/*=cvScalarAll(0)*/, @ByVal CvScalar up_diff/*=cvScalarAll(0)*/,
            CvConnectedComp comp/*=null*/, int flags/*=4*/, CvArr mask/*=null*/);


    public static native void cvCanny(CvArr image, CvArr edges,
            double threshold1, double threshold2, int aperture_size/*=3*/);
    public static native void cvPreCornerDetect(CvArr image, CvArr corners,
            int aperture_size/*=3*/);
    public static native void cvCornerEigenValsAndVecs(CvArr image, CvArr eigenvv,
            int block_size, int aperture_size/*=3*/);
    public static native void cvCornerMinEigenVal(CvArr image, CvArr eigenval,
            int block_size, int aperture_size /*=3*/);
    public static native void cvCornerHarris(CvArr image, CvArr harris_responce,
            int block_size, int aperture_size/*=3*/, double k/*=0.04*/);
    public static native void cvFindCornerSubPix(CvArr image, CvPoint2D32f corners, int count,
            @ByVal CvSize win, @ByVal CvSize zero_zone, @ByVal CvTermCriteria criteria);
    public static native void cvGoodFeaturesToTrack(CvArr image, CvArr eig_image, CvArr temp_image,
            CvPoint2D32f corners, int[] corner_count, double quality_level, double min_distance,
            CvArr mask/*=null*/, int block_size/*=3*/, int use_harris/*=0*/, double k/*=0.04*/);

    public static native CvSeq cvHoughLines2(CvArr image, Pointer line_storage, int method,
            double rho, double theta, int threshold, double param1/*=0*/, double param2/*=0*/);
    public static native CvSeq cvHoughCircles(CvArr image, Pointer circle_storage, int method,
            double dp, double min_dist, double param1/*=100*/, double param2/*=100*/,
            int min_radius/*=0*/, int max_radius/*=0*/);
    public static native void cvFitLine(CvArr points, int dist_type, double param,
            double reps, double aeps, float[] line);
    public static native void cvFitLine(CvArr points, int dist_type, double param,
            double reps, double aeps, FloatPointer line);


    public static final int
            BORDER_REPLICATE=IPL_BORDER_REPLICATE,BORDER_CONSTANT=IPL_BORDER_CONSTANT,
            BORDER_REFLECT=IPL_BORDER_REFLECT, BORDER_WRAP=IPL_BORDER_WRAP,
            BORDER_REFLECT_101=IPL_BORDER_REFLECT_101, BORDER_REFLECT101=BORDER_REFLECT_101,
            BORDER_TRANSPARENT=IPL_BORDER_TRANSPARENT,
            BORDER_DEFAULT=BORDER_REFLECT_101, BORDER_ISOLATED=16;

    @Namespace("cv") public static native int borderInterpolate(int p, int len, int borderType);

    @NoOffset @Namespace("cv") public static class BaseRowFilter extends Pointer {
        static { load(); }
        public BaseRowFilter() { }
        public BaseRowFilter(Pointer p) { super(p); }

        public /*abstract*/ native @Name("operator()") void filter(@Cast("uchar*") BytePointer src,
                @Cast("uchar*") BytePointer dst, int width, int cn);

        public native int ksize();  public native BaseRowFilter ksize(int ksize);
        public native int anchor(); public native BaseRowFilter anchor(int anchor);
    }

    @NoOffset @Namespace("cv") public static class BaseColumnFilter extends Pointer {
        static { load(); }
        public BaseColumnFilter() { }
        public BaseColumnFilter(Pointer p) { super(p); }

        public /*abstract*/ native @Name("operator()") void filter(@Cast("const uchar**") PointerPointer src,
                @Cast("uchar*") BytePointer dst, int dststep, int dstcount, int width);
        public native void reset();

        public native int ksize();  public native BaseColumnFilter ksize(int ksize);
        public native int anchor(); public native BaseColumnFilter anchor(int anchor);
    }

    @NoOffset @Namespace("cv") public static class BaseFilter extends Pointer {
        static { load(); }
        public BaseFilter() { }
        public BaseFilter(Pointer p) { super(p); }

        public /*abstract*/ native @Name("operator()") void filter(@Cast("const uchar**") PointerPointer src,
                @Cast("uchar*") BytePointer dst, int dststep, int dstcount, int width, int cn);
        public native void reset();

        public native @ByVal CvSize ksize();   public native BaseFilter ksize(CvSize ksize);
        public native @ByVal CvPoint anchor(); public native BaseFilter anchor(CvPoint anchor);
    }

    @NoOffset @Namespace("cv") public static class FilterEngine extends Pointer {
        static { load(); }
        public FilterEngine() { allocate(); }
        public FilterEngine(@ByRef BaseFilterPtr _filter2D, @ByRef BaseRowFilterPtr _rowFilter,
                @ByRef BaseColumnFilterPtr _columnFilter, int srcType, int dstType, int bufType,
                int _rowBorderType/*=BORDER_REPLICATE*/, int _columnBorderType/*=-1*/,
                @ByVal CvScalar _borderValue/*=Scalar()*/) {
            allocate(_filter2D, _rowFilter, _columnFilter, srcType, dstType, bufType,
                    _rowBorderType, _columnBorderType, _borderValue);
        }
        public FilterEngine(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByRef BaseFilterPtr _filter2D, @ByRef BaseRowFilterPtr _rowFilter,
                @ByRef BaseColumnFilterPtr _columnFilter, int srcType, int dstType, int bufType,
                int _rowBorderType/*=BORDER_REPLICATE*/, int _columnBorderType/*=-1*/,
                @ByVal CvScalar _borderValue/*=Scalar()*/);

        public native void init(@ByRef BaseFilterPtr _filter2D, @ByRef BaseRowFilterPtr _rowFilter,
                @ByRef BaseColumnFilterPtr _columnFilter, int srcType, int dstType, int bufType,
                int _rowBorderType/*=BORDER_REPLICATE*/, int _columnBorderType/*=-1*/,
                @ByVal CvScalar _borderValue/*=Scalar()*/);

        public native int start(@ByVal CvSize wholeSize, @ByVal CvRect roi, int maxBufRows/*=-1*/);
        public native int start(@Adapter("MatAdapter") CvArr src, @ByVal CvRect srcRoi/*=Rect(0,0,-1,-1)*/,
                @Cast("bool") boolean isolated/*=false*/, int maxBufRows/*=-1*/);
        public native int proceed(@Cast("uchar*") BytePointer src, int srcStep, int srcCount,
                @Cast("uchar*") BytePointer dst, int dstStep);
        public native void apply(@Adapter("MatAdapter") CvArr src, @Adapter("MatAdapter") CvArr dst,
                @ByVal CvRect srcRoi/*=Rect(0,0,-1,-1)*/, @ByVal CvPoint dstOfs/*=Point(0,0)*/,
                @Cast("bool") boolean isolated/*=false*/);
        public native boolean isSeparable();
        public native int remainingInputRows();
        public native int remainingOutputRows();

        public native int srcType();                  public native FilterEngine srcType(int srcType);
        public native int dstType();                  public native FilterEngine dstType(int dstType);
        public native int bufType();                  public native FilterEngine bufType(int bufType);
        public native @ByVal CvSize ksize();          public native FilterEngine ksize(CvSize ksize);
        public native @ByVal CvPoint anchor();        public native FilterEngine anchor(CvPoint anchor);
        public native int maxWidth();                 public native FilterEngine maxWidth(int maxWidth);
        public native @ByVal CvSize wholeSize();      public native FilterEngine wholeSize(CvSize wholeSize);
        public native @ByVal CvRect roi();            public native FilterEngine roi(CvRect roi);
        public native int dx1();                      public native FilterEngine dx1(int dx1);
        public native int dx2();                      public native FilterEngine dx2(int dx2);
        public native int rowBorderType();            public native FilterEngine rowBorderType(int rowBorderType);
        public native int columnBorderType();         public native FilterEngine columnBorderType(int columnBorderType);
        @Adapter("VectorAdapter<int>")
        public native IntPointer borderTab();         public native FilterEngine borderTab(IntPointer borderTab);
        public native int borderElemSize();           public native FilterEngine borderElemSize(int borderElemSize);
        @Adapter("VectorAdapter<uchar>") @Cast("uchar*")
        public native BytePointer ringBuf();          public native FilterEngine ringBuf(BytePointer ringBuf);
        @Adapter("VectorAdapter<uchar>") @Cast("uchar*")
        public native BytePointer srcRow();           public native FilterEngine srcRow(BytePointer srcRow);
        @Adapter("VectorAdapter<uchar>") @Cast("uchar*")
        public native BytePointer constBorderValue(); public native FilterEngine constBorderValue(BytePointer constBorderValue);
        @Adapter("VectorAdapter<uchar>") @Cast("uchar*")
        public native BytePointer constBorderRow();   public native FilterEngine constBorderRow(BytePointer constBorderRow);
        public native int bufStep();                  public native FilterEngine bufStep(int bufStep);
        public native int startY();                   public native FilterEngine startY(int startY);
        public native int startY0();                  public native FilterEngine startY0(int startY0);
        public native int endY();                     public native FilterEngine endY(int endY);
        public native int rowCount();                 public native FilterEngine rowCount(int rowCount);
        public native int dstY();                     public native FilterEngine dstY(int dstY);
        @Adapter("VectorAdapter<uchar*>") @Cast("uchar**")
        public native PointerPointer rows();          public native FilterEngine rows(PointerPointer rows);

        public native @ByRef BaseFilterPtr filter2D();           public native FilterEngine filter2D(BaseFilterPtr filter2D);
        public native @ByRef BaseRowFilterPtr rowFilter();       public native FilterEngine rowFilter(BaseRowFilterPtr rowFilter);
        public native @ByRef BaseColumnFilterPtr columnFilter(); public native FilterEngine columnFilter(BaseColumnFilterPtr columnFilter);
    }

    @Name("cv::Ptr<cv::BaseFilter>")
    public static class BaseFilterPtr extends Pointer {
        static { load(); }
        public BaseFilterPtr()       { allocate();  }
        public BaseFilterPtr(Pointer p) { super(p); }
        private native void allocate();

        public native BaseFilter get();
        public native BaseFilterPtr put(BaseFilter value);
    }

    @Name("cv::Ptr<cv::BaseRowFilter>")
    public static class BaseRowFilterPtr extends Pointer {
        static { load(); }
        public BaseRowFilterPtr()       { allocate();  }
        public BaseRowFilterPtr(Pointer p) { super(p); }
        private native void allocate();

        public native BaseRowFilter get();
        public native BaseRowFilterPtr put(BaseRowFilter value);
    }

    @Name("cv::Ptr<cv::BaseColumnFilter>")
    public static class BaseColumnFilterPtr extends Pointer {
        static { load(); }
        public BaseColumnFilterPtr()       { allocate();  }
        public BaseColumnFilterPtr(Pointer p) { super(p); }
        private native void allocate();

        public native BaseColumnFilter get();
        public native BaseColumnFilterPtr put(BaseColumnFilter value);
    }

    @Name("cv::Ptr<cv::FilterEngine>")
    public static class FilterEnginePtr extends Pointer {
        static { load(); }
        public FilterEnginePtr()       { allocate();  }
        public FilterEnginePtr(Pointer p) { super(p); }
        private native void allocate();

        public native FilterEngine get();
        public native FilterEnginePtr put(FilterEngine value);
    }

    public static final int
            KERNEL_GENERAL=0, KERNEL_SYMMETRICAL=1, KERNEL_ASYMMETRICAL=2,
            KERNEL_SMOOTH=4, KERNEL_INTEGER=8;

    @Namespace("cv") public static native int getKernelType(@Adapter("ArrayAdapter") CvMat kernel, @ByVal CvPoint anchor);

    @Namespace("cv") public static native @ByVal BaseRowFilterPtr getLinearRowFilter(int srcType, int bufType,
            @Adapter("ArrayAdapter") CvMat kernel, int anchor, int symmetryType);
    @Namespace("cv") public static native @ByVal BaseColumnFilterPtr getLinearColumnFilter(int bufType, int dstType,
            @Adapter("ArrayAdapter") CvMat kernel, int anchor, int symmetryType, double delta/*=0*/, int bits/*=0*/);
    @Namespace("cv") public static native @ByVal BaseFilterPtr getLinearFilter(int srcType, int dstType,
            @Adapter("ArrayAdapter") CvMat kernel, @ByVal CvPoint anchor/*=Point(-1,-1)*/, double delta/*=0*/, int bits/*=0*/);

    @Namespace("cv") public static native @ByVal FilterEnginePtr createSeparableLinearFilter(int srcType, int dstType,
            @Adapter("ArrayAdapter") CvMat rowKernel, @Adapter("ArrayAdapter") CvMat columnKernel, @ByVal CvPoint _anchor/*=Point(-1,-1)*/, double delta/*=0*/,
            int _rowBorderType/*=BORDER_DEFAULT*/, int _columnBorderType/*=-1*/, @ByVal CvScalar _borderValue/*=Scalar()*/);
    @Namespace("cv") public static native @ByVal FilterEnginePtr createLinearFilter(int srcType, int dstType,
            @Adapter("ArrayAdapter") CvMat kernel, @ByVal CvPoint _anchor/*=Point(-1,-1)*/, double delta/*=0*/,
            int _rowBorderType/*=BORDER_DEFAULT*/, int _columnBorderType/*=-1*/, @ByVal CvScalar _borderValue/*=Scalar()*/);

    @Namespace("cv") public static native @Adapter("MatAdapter") CvMat getGaussianKernel(int ksize, double sigma, int ktype/*=CV_64F*/);
    @Namespace("cv") public static native @ByVal FilterEnginePtr createGaussianFilter(int type, @ByVal CvSize ksize,
            double sigma1, double sigma2/*=0*/, int borderType/*=BORDER_DEFAULT*/);

    @Namespace("cv") public static native void getDerivKernels(@Adapter(value="ArrayAdapter", out=true) CvMat kx,
            @Adapter(value="ArrayAdapter", out=true) CvMat ky, int dx, int dy, int ksize,
            @Cast("bool") boolean normalize/*=false*/, int ktype/*=CV_32F*/);
    @Namespace("cv") public static native @ByVal FilterEnginePtr createDerivFilter(int srcType, int dstType,
            int dx, int dy, int ksize, int borderType/*=BORDER_DEFAULT*/);
    
    @Namespace("cv") public static native @ByVal BaseRowFilterPtr getRowSumFilter(int srcType, int sumType,
            int ksize, int anchor/*=-1*/);
    @Namespace("cv") public static native @ByVal BaseColumnFilterPtr getColumnSumFilter( int sumType, int dstType,
            int ksize, int anchor/*=-1*/, double scale/*=1*/);
    @Namespace("cv") public static native @ByVal FilterEnginePtr createBoxFilter(int srcType, int dstType, @ByVal CvSize ksize,
            @ByVal CvPoint anchor/*=Point(-1,-1)*/, @Cast("bool") boolean normalize/*=true*/, int borderType/*=BORDER_DEFAULT*/);
    @Namespace("cv") public static native @Adapter("MatAdapter") CvMat getGaborKernel(@ByVal CvSize ksize, double sigma,
            double theta, double lambd, double gamma, double psi/*=Math.PI*0.5*/, int ktype/*=CV_64F*/);

    public static final int
            MORPH_ERODE=CV_MOP_ERODE, MORPH_DILATE=CV_MOP_DILATE,
            MORPH_OPEN=CV_MOP_OPEN, MORPH_CLOSE=CV_MOP_CLOSE,
            MORPH_GRADIENT=CV_MOP_GRADIENT, MORPH_TOPHAT=CV_MOP_TOPHAT, MORPH_BLACKHAT=CV_MOP_BLACKHAT;

    @Namespace("cv") public static native @ByVal BaseRowFilterPtr getMorphologyRowFilter(
            int op, int type, int ksize, int anchor/*=-1*/);
    @Namespace("cv") public static native @ByVal BaseColumnFilterPtr getMorphologyColumnFilter(
            int op, int type, int ksize, int anchor/*=-1*/);
    @Namespace("cv") public static native @ByVal BaseFilterPtr getMorphologyFilter(
            int op, int type, @Adapter("ArrayAdapter") CvMat kernel, @ByVal CvPoint anchor/*=Point(-1,-1)*/);
    
    @Namespace("cv") public static native @ByVal CvScalar morphologyDefaultBorderValue();

    @Namespace("cv") public static native @ByVal FilterEnginePtr createMorphologyFilter(int op, int type, @Adapter("ArrayAdapter") CvMat kernel,
            @ByVal CvPoint anchor/*=Point(-1,-1)*/, int _rowBorderType/*=BORDER_CONSTANT*/, int _columnBorderType/*=-1*/,
            @ByVal CvScalar _borderValue/*=morphologyDefaultBorderValue()*/);

    @Namespace("cv") public static native void medianBlur(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst, int ksize);
    @Namespace("cv") public static native void GaussianBlur(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst,
            @ByVal CvSize ksize, double sigma1, double sigma2/*=0*/, int borderType/*=BORDER_DEFAULT*/);
    @Namespace("cv") public static native void bilateralFilter(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst, int d,
            double sigmaColor, double sigmaSpace, int borderType/*=BORDER_DEFAULT*/);
    @Namespace("cv") public static native void boxFilter(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst, int ddepth,
            @ByVal CvSize ksize, @ByVal CvPoint anchor/*=Point(-1,-1)*/, @Cast("bool") boolean normalize/*=true*/, int borderType/*=BORDER_DEFAULT*/);
    @Namespace("cv") public static native void blur(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst,
            @ByVal CvSize ksize, @ByVal CvPoint anchor/*=Point(-1,-1)*/, int borderType/*=BORDER_DEFAULT*/);
    @Namespace("cv") public static native void filter2D(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst, int ddepth,
            @Adapter("ArrayAdapter") CvMat kernel, @ByVal CvPoint anchor/*=Point(-1,-1)*/, double delta/*=0*/, int borderType/*=BORDER_DEFAULT*/);
    @Namespace("cv") public static native void sepFilter2D(@Adapter("ArrayAdapter") CvArr src, @Adapter("ArrayAdapter") CvArr dst, int ddepth,
            @Adapter("ArrayAdapter") CvMat kernelX, @Adapter("ArrayAdapter") CvMat kernelY, @ByVal CvPoint anchor/*=Point(-1,-1)*/, double delta/*=0*/, int borderType/*=BORDER_DEFAULT*/);

    @Namespace("cv") public static native void eigen2x2(float[] a, float[] e, int n);
    @Namespace("cv") public static native void eigen2x2(FloatPointer a, FloatPointer e, int n);

    @Namespace("cv") public static native double PSNR(@Adapter("ArrayAdapter") CvArr src1, @Adapter("ArrayAdapter") CvArr src2);

    @Namespace("cv") public static native @Adapter("Point2dAdapter") CvPoint2D64f phaseCorrelate(@Adapter("ArrayAdapter") CvArr src1,
            @Adapter("ArrayAdapter") CvArr src2, @Adapter("ArrayAdapter") CvArr window/*=null*/);
    @Namespace("cv") public static native void createHanningWindow(@Adapter(value="ArrayAdapter", out=true) CvMat dst,
            @ByVal CvSize winSize, int type);

    public static final int
            PROJ_SPHERICAL_ORTHO = 0,
            PROJ_SPHERICAL_EQRECT = 1;

    @Namespace("cv") public static native float initWideAngleProjMap(
            @Adapter("ArrayAdapter") CvMat cameraMatrix, @Adapter("ArrayAdapter") CvMat distCoeffs,
            @ByVal CvSize imageSize, int destImageWidth, int m1type,
            @Adapter("ArrayAdapter") CvArr map1, @Adapter("ArrayAdapter") CvArr map2,
            int projType/*=PROJ_SPHERICAL_EQRECT*/, double alpha/*=0*/);

    @Namespace("cv") public static native float EMD(@Adapter("ArrayAdapter") CvArr signature1,
            @Adapter("ArrayAdapter") CvArr signature2, int distType, @Adapter("ArrayAdapter") CvArr cost/*=null*/,
            float[] lowerBound/*=null*/, @Adapter("ArrayAdapter") CvArr flow/*=null*/);

    public static final int
            GC_BGD    = 0,
            GC_FGD    = 1,
            GC_PR_BGD = 2,
            GC_PR_FGD = 3,

            GC_INIT_WITH_RECT  = 0,
            GC_INIT_WITH_MASK  = 1,
            GC_EVAL            = 2;

    @Namespace("cv") public static native void grabCut(@Adapter("ArrayAdapter") CvArr img, @Adapter("ArrayAdapter") CvArr mask, @ByVal CvRect rect,
            @Adapter("ArrayAdapter") CvArr bgdModel, @Adapter("ArrayAdapter") CvArr fgdModel, int iterCount, int mode/*=GC_EVAL*/);

    @Namespace("cv") public static native @Cast("bool") boolean isContourConvex(@Adapter("ArrayAdapter") CvArr contour);
    @Namespace("cv") public static native float intersectConvexConvex(@Adapter("ArrayAdapter") CvArr _p1, @Adapter("ArrayAdapter") CvArr _p2,
            @Adapter(value="ArrayAdapter", out=true) CvMat _p12, @Cast("bool") boolean handleNested/*=true*/);
}
