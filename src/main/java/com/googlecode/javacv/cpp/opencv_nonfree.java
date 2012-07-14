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
 * This file is based on information found in nonfree/features2d.hpp and
 * nonfree.hpp of OpenCV 2.4.2, which are covered by the following copyright notice:
 *
 *                          License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
 * Copyright (C) 2009-2012, Willow Garage Inc., all rights reserved.
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
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
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
        include={"<opencv2/nonfree/nonfree.hpp>", "<opencv2/features2d/features2d.hpp>", "opencv_adapters.h"},
        link={"opencv_nonfree@.2.4", "opencv_features2d@.2.4", "opencv_flann@.2.4", "opencv_highgui@.2.4", "opencv_imgproc@2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_nonfree242", "opencv_features2d242", "opencv_flann242", "opencv_highgui242", "opencv_imgproc242", "opencv_core242"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_nonfree {
    static { load(opencv_highgui.class); load(opencv_flann.class); load(opencv_features2d.class);
        if (load() != null) {
            initModule_nonfree();
        }
    }

    @Namespace("cv") public static native @Cast("bool") boolean initModule_nonfree();

    @Namespace("cv") public static class SIFT extends Feature2D {
        static { load(); }
        public SIFT() { allocate(); }
        public SIFT(Pointer p) { super(p); }
        public SIFT(int nfeatures/*=0*/, int nOctaveLayers/*=3*/, double contrastThreshold/*=0.04*/,
                double edgeThreshold/*=10*/, double sigma/*=1.6*/) {
            allocate(nfeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
        }
        private native void allocate();
        private native void allocate(int nfeatures/*=0*/, int nOctaveLayers/*=3*/, double contrastThreshold/*=0.04*/,
                double edgeThreshold/*=10*/, double sigma/*=1.6*/);

        public native int descriptorSize();
        public native int descriptorType();

        public native @Name("operator()") void detect(@Adapter("ArrayAdapter") CvArr img, @Adapter("ArrayAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
        public native @Name("operator()") void detect(@Adapter("ArrayAdapter") CvArr img, @Adapter("ArrayAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="ArrayAdapter", out=true) CvMat descriptors, @Cast("bool") boolean useProvidedKeypoints/*=false*/);

        public native AlgorithmInfo info();

        public native void buildGaussianPyramid(IplImage base, @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr, int nOctaves);
        public native void buildDoGPyramid(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray pyr,
                                           @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray dogpyr);
        public native void findScaleSpaceExtrema(@Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray gauss_pyr,
                                                 @Adapter("VectorAdapter<IplImage*,cv::Mat>") IplImageArray dog_pyr,
                                                 @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);

//        protected native int nfeatures();
//        protected native int nOctaveLayers();
//        protected native double contrastThreshold();
//        protected native double edgeThreshold();
//        protected native double sigma();
    }

    @NoOffset @Namespace("cv") public static class SURF extends Feature2D {
        static { load(); }
        public SURF(double hessianThreshold) { allocate(hessianThreshold); }
        public SURF(Pointer p) { super(p); }
        public SURF(double hessianThreshold, int nOctaves/*=4*/, int nOctaveLayers/*=2*/,
                @Cast("bool") boolean extended/*=true*/, @Cast("bool") boolean upright/*=false*/) {
            allocate(hessianThreshold, nOctaves, nOctaveLayers, extended, upright);
        }
        private native void allocate(double hessianThreshold);
        private native void allocate(double hessianThreshold, int nOctaves/*=4*/, int nOctaveLayers/*=2*/,
                @Cast("bool") boolean extended/*=true*/, @Cast("bool") boolean upright/*=false*/);

        public native int descriptorSize();
        public native int descriptorType();

        public native @Name("operator()") void detect(@Adapter("ArrayAdapter") CvArr img, @Adapter("ArrayAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints);
        public native @Name("operator()") void detect(@Adapter("ArrayAdapter") CvArr img, @Adapter("ArrayAdapter") CvArr mask,
                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
                @Adapter(value="ArrayAdapter", out=true) CvMat descriptors, @Cast("bool") boolean useProvidedKeypoints/*=false*/);

        public native AlgorithmInfo info();

        public native double hessianThreshold();        public native SURF hessianThreshold(double hessianThreshold);
        public native int nOctaves();                   public native SURF nOctaves(int nOctaves);
        public native int nOctaveLayers();              public native SURF nOctaveLayers(int nOctaveLayers);
        public native @Cast("bool") boolean extended(); public native SURF extended(boolean extended);
        public native @Cast("bool") boolean upright();  public native SURF upright(boolean upright);

//        protected native void detectImpl(@Adapter("MatAdapter") CvArr image, @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints, @Adapter("MatAdapter") CvArr mask/*=null*/);
//        protected native void computeImpl(@Adapter("MatAdapter") CvArr image,
//                @Adapter(value="VectorAdapter<cv::KeyPoint>", out=true) KeyPoint keypoints,
//                @Adapter(value="MatAdapter", out=true) CvMat descriptors);
    }
}
