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

import org.bytedeco.javacv.ImageTransformer.Parameters;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

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
