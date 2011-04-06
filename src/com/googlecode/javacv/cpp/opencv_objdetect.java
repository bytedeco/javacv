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
 * This file is based on information found in objdetect.hpp
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
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Name;
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
    @Platform(include="<opencv2/objdetect/objdetect.hpp>", includepath=genericIncludepath,
        linkpath=genericLinkpath,       link="opencv_objdetect"),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, link="opencv_objdetect220"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_objdetect {
    static { load(opencv_highgui.class); load(); }

    public static final int CV_HAAR_MAGIC_VAL    = 0x42500000;
    public static final String CV_TYPE_NAME_HAAR = "opencv-haar-classifier";

    public static boolean CV_IS_HAAR_CLASSIFIER(CvHaarClassifierCascade haar) {
        return haar != null && (haar.flags() & CV_MAGIC_MASK)==CV_HAAR_MAGIC_VAL;
    }

    public static final int CV_HAAR_FEATURE_MAX = 3;

    public static class CvHaarFeature extends Pointer {
        static { load(); }
        public CvHaarFeature() { allocate(); }
        public CvHaarFeature(int size) { allocateArray(size); }
        public CvHaarFeature(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarFeature position(int position) {
            return (CvHaarFeature)super.position(position);
        }

        public native int tilted(); public native CvHaarFeature tilted(int tilted);
        // struct { } rect[CV_HAAR_FEATURE_MAX]
        @Name(value="rect", suffix=".r")
        public native @ByVal CvRect rect_r(int i); public native CvHaarFeature rect_r(int i, CvRect r);
        @Name(value="rect", suffix=".weight")
        public native float    rect_weight(int i); public native CvHaarFeature rect_weight(int i, float weight);
    }

    public static class CvHaarClassifier extends Pointer {
        static { load(); }
        public CvHaarClassifier() { allocate(); }
        public CvHaarClassifier(int size) { allocateArray(size); }
        public CvHaarClassifier(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarClassifier position(int position) {
            return (CvHaarClassifier)super.position(position);
        }

        public native int count();                  public native CvHaarClassifier count(int count);
        public native CvHaarFeature haar_feature(); public native CvHaarClassifier haar_feature(CvHaarFeature haar_feature);
        public native FloatPointer threshold();     public native CvHaarClassifier threshold(FloatPointer threshold);
        public native IntPointer left();            public native CvHaarClassifier left(IntPointer left);
        public native IntPointer right();           public native CvHaarClassifier right(IntPointer right);
        public native FloatPointer alpha();         public native CvHaarClassifier alpha(FloatPointer alpha);
    }

    public static class CvHaarStageClassifier extends Pointer {
        static { load(); }
        public CvHaarStageClassifier() { allocate(); }
        public CvHaarStageClassifier(int size) { allocateArray(size); }
        public CvHaarStageClassifier(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarStageClassifier position(int position) {
            return (CvHaarStageClassifier)super.position(position);
        }

        public native int count();                   public native CvHaarStageClassifier count(int count);
        public native float threshold();             public native CvHaarStageClassifier threshold(float threshold);
        public native CvHaarClassifier classifier(); public native CvHaarStageClassifier classifier(CvHaarClassifier classifier);

        public native int next();                    public native CvHaarStageClassifier next(int next);
        public native int child();                   public native CvHaarStageClassifier child(int child);
        public native int parent();                  public native CvHaarStageClassifier parent(int parent);
    }

    @Opaque public static class CvHidHaarClassifierCascade extends Pointer {
        static { load(); }
        public CvHidHaarClassifierCascade() { }
        public CvHidHaarClassifierCascade(Pointer p) { super(p); }
    }

    public static class CvHaarClassifierCascade extends Pointer {
        static { com.googlecode.javacpp.Loader.load(); }
        public CvHaarClassifierCascade() { allocate(); }
        public CvHaarClassifierCascade(int size) { allocateArray(size); }
        public CvHaarClassifierCascade(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvHaarClassifierCascade position(int position) {
            return (CvHaarClassifierCascade)super.position(position);
        }

        public static CvHaarClassifierCascade load(String directory,
                CvSize orig_window_size) {
            CvHaarClassifierCascade h = cvLoadHaarClassifierCascade(directory,
                    orig_window_size);
            if (h != null) {
                h.deallocator(new ReleaseDeallocator(h));
            }
            return h;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvHaarClassifierCascade implements Deallocator {
            ReleaseDeallocator(CvHaarClassifierCascade p) { super(p); }
            @Override public void deallocate() { cvReleaseHaarClassifierCascade(this); }
        }


        public native int flags();                              public native CvHaarClassifierCascade flags(int flags);
        public native int count();                              public native CvHaarClassifierCascade count(int count);
        public native @ByVal CvSize orig_window_size();         public native CvHaarClassifierCascade orig_window_size(CvSize orig_window_size);
        public native @ByVal CvSize real_window_size();         public native CvHaarClassifierCascade real_window_size(CvSize real_window_size);
        public native double scale();                           public native CvHaarClassifierCascade scale(double scale);
        public native CvHaarStageClassifier stage_classifier(); public native CvHaarClassifierCascade stage_classifier(CvHaarStageClassifier stage_classifier);
        public native CvHidHaarClassifierCascade hid_cascade(); public native CvHaarClassifierCascade hid_cascade(CvHidHaarClassifierCascade hid_cascade);
    }

    public static class CvAvgComp extends Pointer {
        static { load(); }
        public CvAvgComp() { allocate(); }
        public CvAvgComp(int size) { allocateArray(size); }
        public CvAvgComp(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvAvgComp position(int position) {
            return (CvAvgComp)super.position(position);
        }

        public native @ByVal CvRect rect(); public native CvAvgComp rect(CvRect rect);
        public native int neighbors();      public native CvAvgComp neighbors(int neighbors);
    }

    public static native CvHaarClassifierCascade cvLoadHaarClassifierCascade(
            String directory, @ByVal CvSize orig_window_size);
    public static native void cvReleaseHaarClassifierCascade(
            @ByPtrPtr CvHaarClassifierCascade cascade);

    public static final int
            CV_HAAR_DO_CANNY_PRUNING    = 1,
            CV_HAAR_SCALE_IMAGE         = 2,
            CV_HAAR_FIND_BIGGEST_OBJECT = 4,
            CV_HAAR_DO_ROUGH_SEARCH     = 8;

    public static CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/, int flags/*=0*/) {
        return cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors, flags, CvSize.ZERO, CvSize.ZERO);
    }
    public static native CvSeq cvHaarDetectObjects(CvArr image, CvHaarClassifierCascade cascade,
            CvMemStorage storage, double scale_factor/*=1.1*/, int min_neighbors/*=3*/, int flags/*=0*/,
            @ByVal CvSize min_size/*=cvSize(0,0)*/, @ByVal CvSize max_size/*=cvSize(0,0)*/);
    public static native void cvSetImagesForHaarClassifierCascade(CvHaarClassifierCascade cascade,
            CvArr sum, CvArr sqsum, CvArr tilted_sum, double scale);
    public static native int cvRunHaarClassifierCascade(CvHaarClassifierCascade cascade,
            @ByVal CvPoint pt, int start_stage/*=0*/);


    public static class CvLSVMFilterPosition extends Pointer {
        static { load(); }
        public CvLSVMFilterPosition() { allocate(); }
        public CvLSVMFilterPosition(int size) { allocateArray(size); }
        public CvLSVMFilterPosition(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLSVMFilterPosition position(int position) {
            return (CvLSVMFilterPosition)super.position(position);
        }

        public native int x(); public native CvLSVMFilterPosition x(int x);
        public native int y(); public native CvLSVMFilterPosition y(int y);
        public native int l(); public native CvLSVMFilterPosition l(int l);
    }

    public static class CvLSVMFilterObject extends Pointer {
        static { load(); }
        public CvLSVMFilterObject() { allocate(); }
        public CvLSVMFilterObject(int size) { allocateArray(size); }
        public CvLSVMFilterObject(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLSVMFilterObject position(int position) {
            return (CvLSVMFilterObject)super.position(position);
        }

        public native @ByVal CvLSVMFilterPosition V();  public native CvLSVMFilterObject V(CvLSVMFilterPosition V);
        public native float/*[4]*/ fineFunction(int i); public native CvLSVMFilterObject fineFunction(int i, float fineFunction);
        public native int sizeX();                      public native CvLSVMFilterObject sizeX(int sizeX);
        public native int sizeY();                      public native CvLSVMFilterObject sizeY(int sizeY);
        public native int p();                          public native CvLSVMFilterObject p(int p);
        public native int xp();                         public native CvLSVMFilterObject xp(int xp);
        public native FloatPointer H();                 public native CvLSVMFilterObject H(FloatPointer H);
    }

    public static class CvLatentSvmDetector extends Pointer {
        static { load(); }
        public CvLatentSvmDetector() { allocate(); }
        public CvLatentSvmDetector(int size) { allocateArray(size); }
        public CvLatentSvmDetector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLatentSvmDetector position(int position) {
            return (CvLatentSvmDetector)super.position(position);
        }

        public native int num_filters();             public native CvLatentSvmDetector num_filters(int num_filters);
	public native int num_components();          public native CvLatentSvmDetector num_components(int num_components);
	public native IntPointer num_part_filters(); public native CvLatentSvmDetector num_part_filters(IntPointer num_part_filters);
        @Cast("CvLSVMFilterObject**")
	public native PointerPointer filters();      public native CvLatentSvmDetector filters(PointerPointer filters);
	public native FloatPointer b();              public native CvLatentSvmDetector b(FloatPointer b);
	public native float score_threshold();       public native CvLatentSvmDetector score_threshold(float score_threshold);
    }

    public static class CvObjectDetection extends Pointer {
        static { load(); }
        public CvObjectDetection() { allocate(); }
        public CvObjectDetection(int size) { allocateArray(size); }
        public CvObjectDetection(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvObjectDetection position(int position) {
            return (CvObjectDetection)super.position(position);
        }

        public native @ByVal CvRect rect(); public native CvObjectDetection rect(CvRect rect);
	public native float score();        public native CvObjectDetection score(float score);
    }

    public static native CvLatentSvmDetector cvLoadLatentSvmDetector(String filename);
    public static native void cvReleaseLatentSvmDetector(@ByPtrPtr CvLatentSvmDetector detector);
    public static native CvSeq cvLatentSvmDetectObjects(IplImage image,
            CvLatentSvmDetector detector, CvMemStorage storage,	float overlap_threshold/*=0.5*/);
}
