/*
 * Copyright (C) 2009,2010 Samuel Audet
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

package name.audet.samuel.javacv.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.nio.DoubleBuffer;

import static name.audet.samuel.javacv.jna.cxcore.*;

/**
 *
 * @author Samuel Audet
 */
public class cvkernels {
    public static final String[] paths = {
            "../cvkernels/dist/Debug/GNU-Linux-x86/",
            "../cvkernels/dist/Release/GNU-Linux-x86/",
            "../cvkernels/dist/Release-MinGW/",
            "../cvkernels/dist/Release-MinGW-w64/",
            "../cvkernels/dist/Release-32/GNU-Linux-x86/",
            "../cvkernels/dist/Release-64/GNU-Linux-x86/" };
    public static final String libname = Loader.load(paths, new String[] { "cvkernels" });

    public static class MultiWarpColorTransformData extends Structure {
        // input
        public Pointer /* IplImage */ srcImg, srcImg2, subImg, srcDotImg;
        public Pointer /* CvMat    */ H1,     H2, X;

        // output
        public Pointer /* IplImage */ transImg, dstImg;
        public int          dstCount, dstCountZero;
        public double       srcDstDot;
        public DoubleBuffer dstDstDot;
    }
    public static void multiWarpColorTransform(MultiWarpColorTransformData[] data,
            IplImage inversedMask, CvRect roi, double zeroThreshold, CvScalar fillColor) {
        for (Structure s : data) { s.write(); }
        multiWarpColorTransform(data[0], data.length, inversedMask, roi, zeroThreshold, fillColor);
        for (Structure s : data) { s.readField("dstCount"); s.readField("dstCountZero"); s.readField("srcDstDot"); }
    }
    public static native void multiWarpColorTransform(MultiWarpColorTransformData data, int size,
            IplImage inversedMask, CvRect roi, double zeroThreshold, CvScalar fillColor);
}
