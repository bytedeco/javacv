/*
 * Copyright (C) 2011,2012,2013 Samuel Audet
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
 * This file is based on information found in config.h, defines.h, and miniflann.hpp
 * of OpenCV 2.4.8, which ares covered by the following copyright notice:
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
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;
import com.googlecode.javacpp.annotation.StdVector;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties(inherit=opencv_core.class, value={
    @Platform(include="<opencv2/flann/miniflann.hpp>", link="opencv_flann@.2.4"),
    @Platform(value="windows", link="opencv_flann248") })
public class opencv_flann {
    static { load(); }

    public static final String FLANN_VERSION_ = "1.6.10";

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
            FLANN_CENTERS_KMEANSPP = 2,

    // enum flann_distance_t
            FLANN_DIST_EUCLIDEAN = 1,
            FLANN_DIST_L2 = 1,
            FLANN_DIST_MANHATTAN = 2,
            FLANN_DIST_L1 = 2,
            FLANN_DIST_MINKOWSKI = 3,
            FLANN_DIST_MAX   = 4,
            FLANN_DIST_HIST_INTERSECT   = 5,
            FLANN_DIST_HELLINGER = 6,
            FLANN_DIST_CHI_SQUARE = 7,
            FLANN_DIST_CS         = 7,
            FLANN_DIST_KULLBACK_LEIBLER  = 8,
            FLANN_DIST_KL                = 8,
            FLANN_DIST_HAMMING          = 9;

    @NoOffset @Namespace("cv::flann") public static class IndexParams extends Pointer {
        static { load(); }
        public IndexParams() { allocate(); }
        public IndexParams(Pointer p) { super(p); }
        private native void allocate();

        public native @ByRef String getString(String key, String defaultVal/*=""*/);
        public native int getInt(String key, int defaultVal/*=-1*/);
        public native double getDouble(String key, double defaultVal/*=-1*/);

        public native void setString(String key, String value);
        public native void setInt(String key, int value);
        public native void setDouble(String key, double value);
        public native void setFloat(String key, float value);
        public native void setBool(String key, @Cast("bool") boolean value);
        public native void setAlgorithm(int value);

        public native void getAll(@ByRef StringVector names, @StdVector IntPointer types,
                @ByRef StringVector strValues, @StdVector DoublePointer numValues);

        public native Pointer params(); public native IndexParams params(Pointer params);
    }

    @Namespace("cv::flann") public static class KDTreeIndexParams extends IndexParams {
        static { load(); }
        public KDTreeIndexParams() { allocate(); }
        public KDTreeIndexParams(int trees/*=4*/) { allocate(trees); }
        public KDTreeIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int trees/*=4*/);
    }

    @Namespace("cv::flann") public static class LinearIndexParams extends IndexParams {
        static { load(); }
        public LinearIndexParams() { allocate(); }
        public LinearIndexParams(Pointer p) { super(p); }
        private native void allocate();
    }

    @Namespace("cv::flann") public static class CompositeIndexParams extends IndexParams {
        static { load(); }
        public CompositeIndexParams() { allocate(); }
        public CompositeIndexParams(int trees/*=4*/, int branching/*=32*/, int iterations/*=11*/,
                int centers_init/*=FLANN_CENTERS_RANDOM*/, float cb_index/*=0.2*/) {
            allocate(trees, branching, iterations, centers_init, cb_index);
        }
        public CompositeIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int trees/*=4*/, int branching/*=32*/, int iterations/*=11*/,
                @Cast("cvflann::flann_centers_init_t") int centers_init/*=CENTERS_RANDOM*/, float cb_index/*=0.2*/);
    }

    @Namespace("cv::flann") public static class AutotunedIndexParams extends IndexParams {
        static { load(); }
        public AutotunedIndexParams() { allocate(); }
        public AutotunedIndexParams(float target_precision/*=0.8*/, float build_weight/*=0.01*/,
                float memory_weight/*=0*/, float sample_fraction/*=0.1*/) {
            allocate(target_precision, build_weight, memory_weight, sample_fraction);
        }
        public AutotunedIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(float target_precision/*=0.8*/, float build_weight/*=0.01*/,
                float memory_weight/*=0*/, float sample_fraction/*=0.1*/);
    }

    @Namespace("cv::flann") public static class  HierarchicalClusteringIndexParams extends IndexParams {
        static { load(); }
        public HierarchicalClusteringIndexParams() { allocate(); }
        public HierarchicalClusteringIndexParams(int branching/*=32*/,
                int centers_init/*=FLANN_CENTERS_RANDOM*/, int trees/*=4*/, int leaf_size/*=100*/) {
            allocate(branching, centers_init, trees, leaf_size);
        }
        public HierarchicalClusteringIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int branching/*=32*/, @Cast("cvflann::flann_centers_init_t")
                int centers_init/*=FLANN_CENTERS_RANDOM*/, int trees/*=4*/, int leaf_size/*=100*/);
    }

    @Namespace("cv::flann") public static class KMeansIndexParams extends IndexParams {
        static { load(); }
        public KMeansIndexParams() { allocate(); }
        public KMeansIndexParams(int branching/*=32*/, int iterations/*= 11*/,
                int centers_init/*=FLANN_CENTERS_RANDOM*/, float cb_index/*=0.2*/) {
            allocate(branching, iterations, centers_init, cb_index);
        }
        public KMeansIndexParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int branching/*=32*/, int iterations/*= 11*/,
                @Cast("cvflann::flann_centers_init_t") int centers_init/*=CENTERS_RANDOM*/, float cb_index/*=0.2*/);
    }

    @Namespace("cv::flann") public static class LshIndexParams extends IndexParams {
        static { load(); }
        public LshIndexParams(int table_number, int key_size, int multi_probe_level) {
            allocate(table_number, key_size, multi_probe_level);
        }
        public LshIndexParams(Pointer p) { super(p); }
        private native void allocate(int table_number, int key_size, int multi_probe_level);
    }

    @Namespace("cv::flann") public static class SavedIndexParams extends IndexParams {
        static { load(); }
        public SavedIndexParams() { }
        public SavedIndexParams(String filename) { allocate(filename); }
        public SavedIndexParams(Pointer p) { super(p); }
        private native void allocate(String filename);
    }

    @Namespace("cv::flann") public static class SearchParams extends IndexParams {
        static { load(); }
        public SearchParams() { allocate(); }
        public SearchParams(int checks/*=32*/, float eps/*=0*/, boolean sorted/*=true*/) {
            allocate(checks, eps, sorted);
        }
        public SearchParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int checks/*=32*/, float eps/*=0*/, @Cast("bool") boolean sorted/*=true*/);
    }

    @Namespace("cv::flann") public static class Index extends Pointer {
        static { Loader.load(); }
        public Index() { allocate(); }
        public Index(CvArr features, IndexParams params, int distType/*=FLANN_DIST_L2*/) {
            allocate(features, params, distType);
        }
        public Index(FloatPointer features, IndexParams params, int distType/*=FLANN_DIST_L2*/) {
            allocate(features, params, distType);
        }
        public Index(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@InputArray CvArr features, @ByRef IndexParams params,
                @Cast("cvflann::flann_distance_t") int distType);
        private native void allocate(@InputArray FloatPointer features, @ByRef IndexParams params,
                @Cast("cvflann::flann_distance_t") int distType);

        public native void build(@InputArray CvArr features, @ByRef IndexParams params,
                @Cast("cvflann::flann_distance_t") int distType/*=FLANN_DIST_L2*/);
        public native void build(@InputArray FloatPointer features, @ByRef IndexParams params,
                @Cast("cvflann::flann_distance_t") int distType/*=FLANN_DIST_L2*/);
        public native void knnSearch(@InputArray CvArr query,
                @OutputArray CvMat indices, @OutputArray CvMat dists, int knn,
                @ByRef SearchParams params/*=SearchParams()*/);
        public native void knnSearch(@InputArray FloatPointer query, @OutputArray IntPointer indices,
                @OutputArray FloatPointer dists, int knn, @ByRef SearchParams params/*=SearchParams()*/);

        public native int radiusSearch(@InputArray CvArr query, @OutputArray CvMat indices,
                @OutputArray CvMat dists, double radius, int maxResults, @ByRef SearchParams params/*=SearchParams()*/);
        public native int radiusSearch(@InputArray FloatPointer query,
                @OutputArray IntPointer indices, @OutputArray FloatPointer dists,
                double radius, int maxResults, @ByRef SearchParams params/*=SearchParams()*/);

        public native void save(String filename);
        public native boolean load(@InputArray CvArr features, String filename);
        public native boolean load(@InputArray FloatPointer features, String filename);
        public native void release();
        public native @Cast("cvflann::flann_distance_t") int getDistance();
        public native @Cast("cvflann::flann_algorithm_t") int getAlgorithm();

//        protected native @Cast("cvflann::flann_distance_t") int distType();
//        protected native @Cast("cvflann::flann_algorithm_t") int algo();
//        protected native int featureType();
//        protected native Pointer index();
    }
}
