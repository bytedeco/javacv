/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.MemberSetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;
import com.googlecode.javacv.Parallel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(define="MAX_SIZE 16", include="cvkernels.h", options="fastfpu",
                               includepath=genericIncludepath),
    @Platform(value="windows", includepath=windowsIncludepath),
    @Platform(value="android", includepath=androidIncludepath) })
public class cvkernels {
    static { load(); }

    public static class KernelData extends Pointer {
        static { load(); }
        public KernelData() { allocate(); }
        public KernelData(int size) { allocateArray(size); }
        public KernelData(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public KernelData position(int position) {
            return (KernelData)super.position(position);
        }

        // input
        public native IplImage srcImg();         public native KernelData srcImg(IplImage srcImg);
        public native IplImage srcImg2();        public native KernelData srcImg2(IplImage srcImg2);
        public native IplImage subImg();         public native KernelData subImg(IplImage subImg);
        public native IplImage srcDotImg();      public native KernelData srcDotImg(IplImage srcDotImg);
        public native IplImage mask();           public native KernelData mask(IplImage mask);
        public native double zeroThreshold();    public native KernelData zeroThreshold(double zeroThreshold);
        public native double outlierThreshold(); public native KernelData outlierThreshold(double outlierThreshold);
        public native CvMat H1();                public native KernelData H1(CvMat H1);
        public native CvMat H2();                public native KernelData H2(CvMat H2);
        public native CvMat X();                 public native KernelData X (CvMat X);

        // output
        public native IplImage transImg();       public native KernelData transImg(IplImage transImg);
        public native IplImage dstImg();         public native KernelData dstImg(IplImage dstImg);
        public native int dstCount();            public native KernelData dstCount(int dstCount);
        public native int dstCountZero();        public native KernelData dstCountZero(int dstCountZero);
        public native int dstCountOutlier();     public native KernelData dstCountOutlier(int dstCountOutlier);
        public native double srcDstDot();        public native KernelData srcDstDot(double srcDstDot);
//        public native DoublePointer dstDstDot(); public native KernelData dstDstDot(DoublePointer dstDstDot);

        // Hack to let us use DoubleBuffer directly instead of DoublePointer, which also
        // provides us with Java references to boot, keeping the garbage collector happy
        private native @MemberSetter @Name("dstDstDot") KernelData setDstDstDot(DoubleBuffer dstDstDot);
        private DoubleBuffer[] dstDstDotBuffers = new DoubleBuffer[1];
        public DoubleBuffer dstDstDot() {
            return dstDstDotBuffers[position];
        }
        public KernelData dstDstDot(DoubleBuffer dstDstDot) {
            if (dstDstDotBuffers.length < capacity) {
                dstDstDotBuffers = Arrays.copyOf(dstDstDotBuffers, capacity);
            }
            dstDstDotBuffers[position] = dstDstDot;
            return setDstDstDot(dstDstDot);
        }

        private native @Name("operator=") @ByRef KernelData put(@ByRef KernelData x);
    }

    private static class ParallelData {
        KernelData data = null;
        CvRect roi = new CvRect();
    }
    private static ThreadLocal<ParallelData[]> parallelData = new ThreadLocal<ParallelData[]>() {
        @Override protected ParallelData[] initialValue() {
            ParallelData[] pd = new ParallelData[Parallel.getNumThreads()];
            for (int i = 0; i < pd.length; i++) {
                pd[i] = new ParallelData();
            }
            return pd;
        }
    };

    public static native void multiWarpColorTransform32F(KernelData data, int size, CvRect roi, CvScalar fillColor);
    public static native void multiWarpColorTransform8U(KernelData data, int size, CvRect roi, CvScalar fillColor);
    public static void multiWarpColorTransform(final KernelData data, final CvRect roi, final CvScalar fillColor) {
        final int size = data.capacity();
        final ParallelData[] pd = parallelData.get();

        // Copy all data to completely independent data sets
        for (int i = 0; i < pd.length; i++) {
            if (pd[i].data == null || pd[i].data.capacity() < size) {
                pd[i].data = new KernelData(size);
                for (int j = 0; j < size; j++) {
                    KernelData d = pd[i].data.position(j);
                    data.position(j);
                    if (data.dstDstDot() != null) {
                        d.dstDstDot(ByteBuffer.allocateDirect(data.dstDstDot().capacity()*8).
                                order(ByteOrder.nativeOrder()).asDoubleBuffer());
                    }
                }
            }
            for (int j = 0; j < size; j++) {
                KernelData d = pd[i].data.position(j);
                d.put(data.position(j));
                d.dstDstDot(d.dstDstDot()); // reset dstDstDot pointer
            }
        }

        // Transform in parallel on multiple threads
        final IplImage img = data.position(0).srcImg();
        final int depth = img.depth();
        final int x, y, w, h;
        if (roi != null) {
            x = roi.x();     y = roi.y();
            w = roi.width(); h = roi.height();
        } else {
            x = 0;           y = 0;
            w = img.width(); h = img.height();
        }
        Parallel.loop(y, y+h, pd.length, new Parallel.Looper() {
        public void loop(int from, int to, int looperID) {
            CvRect r = pd[looperID].roi.x(x).y(from).width(w).height(to-from);
            if (depth == IPL_DEPTH_32F) {
                multiWarpColorTransform32F(pd[looperID].data.position(0), size, r, fillColor);
            } else if (depth == IPL_DEPTH_8U) {
                multiWarpColorTransform8U(pd[looperID].data.position(0), size, r, fillColor);
            } else {
                assert false;
            }
        }});

        // Reduce data as required
        for (int i = 0; i < size; i++) {
            int dstCount = 0;
            int dstCountZero = 0;
            int dstCountOutlier = 0;
            double srcDstDot = 0;
            double[] dstDstDot = null;
            if (data.dstDstDot() != null) {
                dstDstDot = new double[data.dstDstDot().capacity()];
            }
            for (int j = 0; j < pd.length; j++) {
                KernelData d = pd[j].data.position(i);
                dstCount        += d.dstCount();
                dstCountZero    += d.dstCountZero();
                dstCountOutlier += d.dstCountOutlier();
                srcDstDot       += d.srcDstDot();
                if (dstDstDot != null && d.dstDstDot() != null) {
                    for (int k = 0; k < dstDstDot.length; k++) {
                        dstDstDot[k] += d.dstDstDot().get(k);
                    }
                }
            }
            data.position(i);
            data.dstCount(dstCount);
            data.dstCountZero(dstCountZero);
            data.dstCountOutlier(dstCountOutlier);
            data.srcDstDot(srcDstDot);
            if (dstDstDot != null && data.dstDstDot() != null) {
                data.dstDstDot().position(0);
                data.dstDstDot().put(dstDstDot);
            }
        }
    }
}
