/*
 * Copyright (C) 2009,2010,2011 Samuel Audet
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

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public interface ImageTransformer {
    public static class Data {
        public Data(IplImage srcImg, IplImage subImg, IplImage srcDotImg,
                IplImage transImg, IplImage dstImg, int dstDstDotLength) {
            this.srcImg    = srcImg;
            this.subImg    = subImg;
            this.srcDotImg = srcDotImg;
            this.transImg  = transImg;
            this.dstImg    = dstImg;
            this.dstDstDot = dstDstDotLength == 0 ? null : new double[dstDstDotLength];
        }

        // input
        public IplImage srcImg  = null, subImg = null, srcDotImg = null;
        public boolean  inverse = false;

        // output
        public IplImage transImg  = null, dstImg = null;
        public int      dstCount  = 0, dstCountZero = 0;
        public double   srcDstDot = 0.0;
        public double[] dstDstDot = null;

        // private data
        protected Object cache = null;
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
    void transform(Data[] data, IplImage mask, CvRect roi, double zeroThreshold,
            int pyramidLevel, Parameters[] parameters);
    void transform(CvMat srcPts, CvMat dstPts, Parameters parameters, boolean inverse);
}
