/*
 * Copyright (C) 2009-2011 Samuel Audet
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

import java.awt.Color;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class ColorCalibrator {
    public ColorCalibrator(ProjectiveDevice device) {
        this.device = device;
    }

    private ProjectiveDevice device;

    public double calibrate(Color[] referenceColors, Color[] deviceColors) {
        assert(referenceColors.length == deviceColors.length);

        int[] order = device.getRGBColorOrder();

        // solve for X and a in   c = X p + a
        CvMat A = CvMat.create(referenceColors.length*3, 12);
        CvMat b = CvMat.create(referenceColors.length*3, 1);
        CvMat x = CvMat.create(12, 1);

        double gamma = device.getSettings().getResponseGamma();

        for (int i = 0; i < referenceColors.length; i++) {
            float[] dc = deviceColors   [i].getRGBColorComponents(null);
            float[] rc = referenceColors[i].getRGBColorComponents(null);

            double dc1 = Math.pow(dc[order[0]], gamma);
            double dc2 = Math.pow(dc[order[1]], gamma);
            double dc3 = Math.pow(dc[order[2]], gamma);
            for (int j = 0; j < 3; j++) {
                int k = i*36 + j*16;
                A.put(k  , dc1);
                A.put(k+1, dc2);
                A.put(k+2, dc3);
                A.put(k+3, 1.0);
                if (j < 2) {
                    for (int m = 0; m < 12; m++) {
                        A.put(k+4+m, 0.0);
                    }
                }
            }

            b.put(i*3  , rc[order[0]]);
            b.put(i*3+1, rc[order[1]]);
            b.put(i*3+2, rc[order[2]]);
        }

        //System.out.println("A =\n" + A);
        //System.out.println("b =\n" + b);

//                A.height = b.height = 18;
        if (cvSolve(A, b, x, CV_SVD) != 1.0) {
            System.out.println("Error solving.");
        }

        // compute RMSE and R^2 coefficient ...
        CvMat b2 = CvMat.create(b.rows(), 1);
        cvMatMul(A, x, b2);
        double MSE = cvNorm(b, b2)*cvNorm(b, b2)/b.rows();
        double RMSE = Math.sqrt(MSE);
        CvScalar mean = new CvScalar(), stddev = new CvScalar();
        cvAvgSdv(b, mean, stddev, null);
        double R2  = 1 - MSE/(stddev.val(0)*stddev.val(0));
        //System.out.println("RMSE: " + RMSE + " R2: " + R2);
        //System.out.println("b2 =\n" + b2);

        device.colorMixingMatrix = CvMat.create(3, 3);
        device.additiveLight     = CvMat.create(3, 1);
        for (int i = 0; i < 3; i++) {
            double x0 = x.get(i*4  );
            double x1 = x.get(i*4+1);
            double x2 = x.get(i*4+2);
            double x3 = x.get(i*4+3);
            device.colorMixingMatrix.put(i*3  , x0);
            device.colorMixingMatrix.put(i*3+1, x1);
            device.colorMixingMatrix.put(i*3+2, x2);
            device.additiveLight    .put(i,     x3);
        }

        //System.out.println(device.colorMixingMatrix);
        //System.out.println(device.additiveLight);

        device.colorR2 = R2;
        return device.avgColorErr = RMSE;
    }

}
