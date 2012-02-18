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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public interface ImageTransformer {

    public class Data {
        public Data() { this(null, null, null, null, 0, 0, 0, null, null, 0); }
        public Data(IplImage srcImg, IplImage subImg, IplImage srcDotImg, IplImage mask,
                double zeroThreshold, double outlierThreshold, int pyramidLevel,
                IplImage transImg, IplImage dstImg, int dstDstDotLength) {
            this.srcImg    = srcImg;
            this.subImg    = subImg;
            this.srcDotImg = srcDotImg;
            this.mask      = mask;
            this.zeroThreshold    = zeroThreshold;
            this.outlierThreshold = outlierThreshold;
            this.pyramidLevel     = pyramidLevel;
            this.transImg  = transImg;
            this.dstImg    = dstImg;
            this.dstDstDot = dstDstDotLength == 0 ? null : 
                    ByteBuffer.allocateDirect(dstDstDotLength*8).
                            order(ByteOrder.nativeOrder()).asDoubleBuffer();
        }

        // input
        public IplImage srcImg, subImg, srcDotImg, mask;
        public double   zeroThreshold, outlierThreshold;
        public int      pyramidLevel;

        // output
        public IplImage transImg, dstImg;
        public int      dstCount, dstCountZero, dstCountOutlier;
        public double   srcDstDot;
        public DoubleBuffer dstDstDot;
    }

    public interface Parameters extends Cloneable {
        int size();
        double[] get();
        double   get(int i);
        void set(double ... p);
        void set(int i, double p);
        void set(Parameters p);
        void reset(boolean asIdentity);
        double getConstraintError();
        void compose(Parameters p1, boolean inverse1, Parameters p2, boolean inverse2);
        boolean preoptimize();
        double[] getSubspace();
        void setSubspace(double ... p);
        Parameters clone();
    }

    Parameters createParameters();
    void transform(Data[] data, CvRect roi, Parameters[] parameters, boolean[] inverses);
    void transform(CvMat srcPts, CvMat dstPts, Parameters parameters, boolean inverse);
}
