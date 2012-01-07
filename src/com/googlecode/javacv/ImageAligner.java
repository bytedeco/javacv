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

import com.googlecode.javacv.ImageTransformer.Parameters;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public interface ImageAligner {

    public class Settings extends BaseChildSettings implements Cloneable {
        public Settings() { }
        public Settings(Settings s) {
            minPyramidLevel = s.minPyramidLevel;
            maxPyramidLevel = s.maxPyramidLevel;
            gammaTgamma     = s.gammaTgamma;
            tikhonovAlpha   = s.tikhonovAlpha;
            constrained     = s.constrained;
            zeroThresholds    = s.zeroThresholds;
            outlierThresholds = s.outlierThresholds;
        }

        int minPyramidLevel  = 0;
        int maxPyramidLevel  = 4;
        CvMat gammaTgamma    = null;
        double tikhonovAlpha = 0;
        boolean constrained  = false;
        double[] zeroThresholds    = { 0.04, 0.03, 0.02, 0.01, 0 };
        double[] outlierThresholds = { 0.1 };

        public int getMinPyramidLevel() {
            return minPyramidLevel;
        }
        public void setMinPyramidLevel(int minPyramidLevel) {
            this.minPyramidLevel = minPyramidLevel;
        }

        public int getMaxPyramidLevel() {
            return maxPyramidLevel;
        }
        public void setMaxPyramidLevel(int maxPyramidLevel) {
            this.maxPyramidLevel = maxPyramidLevel;
        }

        public CvMat getGammaTgamma() {
            return gammaTgamma;
        }
        public void setGammaTgamma(CvMat gammaTgamma) {
            this.gammaTgamma = gammaTgamma;
        }

        public double getTikhonovAlpha() {
            return tikhonovAlpha;
        }
        public void setTikhonovAlpha(double tikhonovAlpha) {
            this.tikhonovAlpha = tikhonovAlpha;
        }

//        public boolean isConstrained() {
//            return constrained;
//        }
//        public void setConstrained(boolean constrained) {
//            this.constrained = constrained;
//        }

        public double[] getZeroThresholds() {
            return zeroThresholds;
        }
        public void setZeroThresholds(double[] zeroThresholds) {
            this.zeroThresholds = zeroThresholds;
        }

        public double[] getOutlierThresholds() {
            return outlierThresholds;
        }
        public void setOutlierThresholds(double[] outlierThresholds) {
            this.outlierThresholds = outlierThresholds;
        }

        @Override public Settings clone() {
            return new Settings(this);
        }
    }
    Settings getSettings();
    void setSettings(Settings settings);

    IplImage getTemplateImage();
    void setTemplateImage(IplImage template0, double[] roiPts);

    IplImage getTargetImage();
    void setTargetImage(IplImage target0);

    int getPyramidLevel();
    void setPyramidLevel(int pyramidLevel);

    Parameters getParameters();
    void setParameters(Parameters parameters);

    double[] getTransformedRoiPts();
    IplImage getTransformedImage();
    IplImage getResidualImage();
    IplImage getMaskImage();
    double getRMSE();
    CvRect getRoi();

    boolean iterate(double[] delta);
}
