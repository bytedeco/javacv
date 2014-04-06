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

package com.googlecode.javacv;

import com.googlecode.javacv.Parallel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class cvkernels extends com.googlecode.javacpp.cvkernels {

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
