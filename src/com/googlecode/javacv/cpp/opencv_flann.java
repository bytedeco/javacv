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
 * This file is based on information found in flann.hpp of OpenCV 2.3.0,
 * which is covered by the following copyright notice:
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

import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
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
        include={"opencv_adapters.h", "<opencv2/flann/flann.hpp>"}, link={"opencv_flann", "opencv_core"}),
    @Platform(value="windows", includepath=windowsIncludepath, link={"opencv_flann230", "opencv_core230"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_flann {
    static { load(opencv_core.class); load(); }

    public static final int
    // enum flann_algorithm_t
            FLANN_INDEX_LINEAR = 0,
            FLANN_INDEX_KDTREE = 1,
            FLANN_INDEX_KMEANS = 2,
            FLANN_INDEX_COMPOSITE = 3,
            FLANN_INDEX_SAVED = 254,
            FLANN_INDEX_AUTOTUNED = 255,

    // enum flann_centers_init_t
            FLANN_CENTERS_RANDOM = 0,
            FLANN_CENTERS_GONZALES = 1,
            FLANN_CENTERS_KMEANSPP = 2;
    
    @NoOffset @Namespace("cvflann") public static class IndexParams extends Pointer {
        static { load(); }
        public IndexParams() { }
        public IndexParams(Pointer p) { super(p); }
//        protected native IndexParams(@Cast("flann_algorithm_t" int algorithm_);

        public /*abstract*/ native @Cast("cvflann::flann_algorithm_t") int getIndexType();
        public /*abstract*/ native void print();

        @Cast("cvflann::flann_algorithm_t")
        public native int algorithm(); public native IndexParams algorithm(int algorithm);
    }

    @Namespace("cvflann") public static class LinearIndexParams extends IndexParams {
        static { load(); }
        public LinearIndexParams() { allocate(); }
        public LinearIndexParams(Pointer p) { super(p); }
        private native void allocate();

//        public native @Cast("cvflann::flann_algorithm_t") int getIndexType();
//        public native void print();
    }

    @NoOffset @Namespace("cvflann") public static class KDTreeIndexParams extends IndexParams {
        static { load(); }
        public KDTreeIndexParams() { allocate(); }
        public KDTreeIndexParams(int trees_/*=4*/) { allocate(trees_); }
        public KDTreeIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int trees_/*=4*/);

        public native int trees(); public native KDTreeIndexParams trees(int trees);

//        public native @Cast("cvflann::flann_algorithm_t") int getIndexType();
//        public native void print();
    }

    @NoOffset @Namespace("cvflann") public static class KMeansIndexParams extends IndexParams {
        static { load(); }
        public KMeansIndexParams() { allocate(); }
        public KMeansIndexParams(int branching_/*=32*/, int iterations_/*= 11*/,
                int centers_init_/*=CENTERS_RANDOM*/, float cb_index_/*=0.2*/) {
            allocate(branching_, iterations_, centers_init_, cb_index_);
        }
        public KMeansIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int branching_/*=32*/, int iterations_/*= 11*/,
                @Cast("cvflann::flann_centers_init_t") int centers_init_/*=CENTERS_RANDOM*/, float cb_index_/*=0.2*/);

        public native int branching();    public native KMeansIndexParams branching(int branching);
        public native int iterations();   public native KMeansIndexParams iterations(int iterations);
        @Cast("cvflann::flann_centers_init_t")
        public native int centers_init(); public native KMeansIndexParams centers_init(int centers_init);
        public native float cb_index();   public native KMeansIndexParams cb_index(float cb_index);

//        public native @Cast("cvflann::flann_algorithm_t") int getIndexType();
//        public native void print();
    }

    @NoOffset @Namespace("cvflann") public static class CompositeIndexParams extends IndexParams {
        static { load(); }
        public CompositeIndexParams() { allocate(); }
        public CompositeIndexParams(int trees_/*=4*/, int branching_/*=32*/, int iterations_/*=11*/,
                int centers_init_/*=CENTERS_RANDOM*/, float cb_index_/*=0.2*/) {
            allocate(trees_, branching_, iterations_, centers_init_, cb_index_);
        }
        public CompositeIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int trees_/*=4*/, int branching_/*=32*/, int iterations_/*=11*/,
                @Cast("cvflann::flann_centers_init_t") int centers_init_/*=CENTERS_RANDOM*/, float cb_index_/*=0.2*/);

        public native int trees();        public native CompositeIndexParams trees(int trees);
        public native int branching();    public native CompositeIndexParams branching(int branching);
        public native int iterations();   public native CompositeIndexParams iterations(int iterations);
        @Cast("cvflann::flann_centers_init_t")
        public native int centers_init(); public native CompositeIndexParams centers_init(int centers_init);
        public native float cb_index();   public native CompositeIndexParams cb_index(float cb_index);

//        public native @Cast("cvflann::flann_algorithm_t") int getIndexType();
//        public native void print();
    }

    @NoOffset @Namespace("cvflann") public static class AutotunedIndexParams extends IndexParams {
        static { load(); }
        public AutotunedIndexParams() { allocate(); }
        public AutotunedIndexParams(float target_precision_/*=0.8*/, float build_weight_/*=0.01*/,
                float memory_weight_/*=0*/, float sample_fraction_/*=0.1*/) {
            allocate(target_precision_, build_weight_, memory_weight_, sample_fraction_);
        }
        public AutotunedIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float target_precision_/*=0.8*/, float build_weight_/*=0.01*/,
                float memory_weight_/*=0*/, float sample_fraction_/*=0.1*/);

        public native float target_precision(); public native AutotunedIndexParams target_precision(float target_precision);
        public native float build_weight();     public native AutotunedIndexParams build_weight(float build_weight);
        public native float memory_weight();    public native AutotunedIndexParams memory_weight(float memory_weight);
        public native float sample_fraction();  public native AutotunedIndexParams sample_fraction(float sample_fraction);

//        public native @Cast("cvflann::flann_algorithm_t") int getIndexType();
//        public native void print();
    }

    @NoOffset @Namespace("cvflann") public static class SavedIndexParams extends IndexParams {
        static { load(); }
        public SavedIndexParams() { }
        public SavedIndexParams(String filename_) { allocate(filename_); }
        public SavedIndexParams(Pointer p) { super(p); }
        private native void allocate(String filename_);

        public native @ByRef String filename(); public native SavedIndexParams filename(String filename);

//        public native @Cast("cvflann::flann_algorithm_t") int getIndexType();
//        public native void print();
    }

    @Namespace("cvflann") public static class SearchParams extends Pointer {
        static { load(); }
        public SearchParams() { allocate(); }
        public SearchParams(int checks_/*=32*/) { allocate(checks_); }
        public SearchParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int checks_/*= 32*/);

        @NoOffset public native int checks(); public native SearchParams checks(int checks);
    }

    @Namespace("cv::flann") public static class Index extends Pointer {
        static { load(); }
        public Index() { }
        public Index(CvMat features, @ByRef IndexParams params) { allocate(features, params); }
        public Index(Pointer p) { super(p); }
        private native void allocate(CvMat features, @ByRef IndexParams params);

        public native void knnSearch(@Adapter("VectorAdapter<float>") FloatPointer query,
                @Adapter(value="VectorAdapter<int>",out=true) IntPointer indices,
                @Adapter(value="VectorAdapter<float>",out=true) FloatPointer dists, int knn, @ByRef SearchParams params);
        public native void knnSearch(CvMat queries, @Adapter("MatAdapter") CvMat indices,
                @Adapter("MatAdapter") CvMat dists, int knn, @ByRef SearchParams params);

        public native int radiusSearch(@Adapter("VectorAdapter<float>") FloatPointer query,
                @Adapter(value="VectorAdapter<int>",out=true) IntPointer indices,
                @Adapter(value="VectorAdapter<float>",out=true) FloatPointer dists, float radius, @ByRef SearchParams params);
        public native int radiusSearch(CvMat query, @Adapter("MatAdapter") CvMat indices,
                @Adapter("MatAdapter") CvMat dists, float radius, @ByRef SearchParams params);

        public native void save(String filename);

        public native int veclen();

        public native int size();

        public native @Const IndexParams getIndexParameters();
    }
}
