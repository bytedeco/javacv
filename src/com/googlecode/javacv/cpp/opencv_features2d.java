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
 * This file is based on information found in features2d.hpp
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

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(include="<opencv2/features2d/features2d.hpp>", includepath=genericIncludepath,
        linkpath=genericLinkpath,       link="opencv_features2d",    preload="opencv_flann"),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, link="opencv_features2d220", preload="opencv_flann220"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_features2d {
    static { load(opencv_calib3d.class); load(opencv_highgui.class); load(); }

    public static class CvSURFPoint extends Pointer {
        public CvSURFPoint() { allocate(); }
        public CvSURFPoint(int size) { allocateArray(size); }
        public CvSURFPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSURFPoint position(int position) {
            return (CvSURFPoint)super.position(position);
        }

        public native @ByVal CvPoint2D32f pt(); public native CvSURFPoint pt(CvPoint2D32f pt);
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
        public CvSURFParams() { allocate(); }
        public CvSURFParams(int size) { allocateArray(size); }
        public CvSURFParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSURFParams position(int position) {
            return (CvSURFParams)super.position(position);
        }

        public native int extended();            public native CvSURFParams extended(int extended);
        public native double hessianThreshold(); public native CvSURFParams hessianThreshold(double hessianThreshold);
        public native int nOctaves();            public native CvSURFParams nOctaves(int nOctaves);
        public native int nOctaveLayers();       public native CvSURFParams nOctaveLayers(int nOctaveLayers);
    }

    public static native @ByVal CvSURFParams cvSURFParams(double hessianThreshold, int extended/*=0*/);
    public static native void cvExtractSURF(CvArr image, CvArr mask,
            @ByPtrPtr CvSeq keypoints, @ByPtrPtr CvSeq descriptors,
            CvMemStorage storage, @ByVal CvSURFParams params, int useProvidedKeyPts/*=0*/);


    public static class CvMSERParams extends Pointer {
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
        public CvStarKeypoint() { allocate(); }
        public CvStarKeypoint(int size) { allocateArray(size); }
        public CvStarKeypoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStarKeypoint position(int position) {
            return (CvStarKeypoint)super.position(position);
        }

        public native @ByVal CvPoint pt(); public native CvStarKeypoint pt(CvPoint pt);
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
}
