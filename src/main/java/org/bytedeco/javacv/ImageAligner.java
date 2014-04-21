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

package org.bytedeco.javacv;

import org.bytedeco.javacv.ImageTransformer.Parameters;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public interface ImageAligner {

    public class Settings extends BaseChildSettings implements Cloneable {
        public Settings() { }
        public Settings(Settings s) {
            pyramidLevelMin   = s.pyramidLevelMin;
            pyramidLevelMax   = s.pyramidLevelMax;
            thresholdsZero    = s.thresholdsZero;
            thresholdsOutlier = s.thresholdsOutlier;
            thresholdsMulRMSE = s.thresholdsMulRMSE;
        }

        int pyramidLevelMin        = 0;
        int pyramidLevelMax        = 4;
        double[] thresholdsZero    = { 0.04, 0.03, 0.02, 0.01, 0 };
        double[] thresholdsOutlier = { 0.2 };
        boolean thresholdsMulRMSE  = false;

        public int getPyramidLevelMin() {
            return pyramidLevelMin;
        }
        public void setPyramidLevelMin(int pyramidLevelMin) {
            this.pyramidLevelMin = pyramidLevelMin;
        }

        public int getPyramidLevelMax() {
            return pyramidLevelMax;
        }
        public void setPyramidLevelMax(int pyramidLevelMax) {
            this.pyramidLevelMax = pyramidLevelMax;
        }

        public double[] getThresholdsZero() {
            return thresholdsZero;
        }
        public void setThresholdsZero(double[] thresholdsZero) {
            this.thresholdsZero = thresholdsZero;
        }

        public double[] getThresholdsOutlier() {
            return thresholdsOutlier;
        }
        public void setThresholdsOutlier(double[] thresholdsOutlier) {
            this.thresholdsOutlier = thresholdsOutlier;
        }

        public boolean isThresholdsMulRMSE() {
            return thresholdsMulRMSE;
        }
        public void setThresholdsMulRMSE(boolean thresholdsMulRMSE) {
            this.thresholdsMulRMSE = thresholdsMulRMSE;
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

    IplImage[] getImages();

    boolean iterate(double[] delta);
}
