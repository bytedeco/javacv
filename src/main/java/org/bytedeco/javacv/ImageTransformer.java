/*
 * Copyright (C) 2009-2012 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bytedeco.javacv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

import static org.bytedeco.javacpp.opencv_core.*;

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
