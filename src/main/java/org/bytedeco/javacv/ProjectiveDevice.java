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

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Arrays;
import org.bytedeco.opencv.global.opencv_core;

import org.bytedeco.opencv.opencv_calib3d.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_calib3d.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveDevice {
    public ProjectiveDevice(String name) {
        Settings s = new Settings();
        s.name = name;
        setSettings(s);
    }
    public ProjectiveDevice(String name, File file) throws Exception {
        this(name);
        readParameters(file);
    }
    public ProjectiveDevice(String name, String filename) throws Exception {
        this(name);
        readParameters(filename);
    }
    public ProjectiveDevice(String name, FileStorage fs) throws Exception {
        this(name);
        readParameters(fs);
    }
    public ProjectiveDevice(Settings settings) throws Exception {
        setSettings(settings);
        if (settings instanceof CalibratedSettings) {
            readParameters(((CalibratedSettings)settings).parametersFile);
        }
    }

    public static class Settings extends BaseChildSettings {
        public Settings() { }
        public Settings(ProjectiveDevice.Settings settings) {
            this.name            = settings.name;
            this.responseGamma   = settings.responseGamma;
//            this.nominalDistance = settings.nominalDistance;
        }
        String name = "";
        double responseGamma = 0.0;
//        double nominalDistance = 20000;

        @Override public String getName() {
            return name;
        }
        public void setName(String name) {
            firePropertyChange("name", this.name, this.name = name);
        }

        public double getResponseGamma() {
            return responseGamma;
        }
        public void setResponseGamma(double responseGamma) {
            this.responseGamma = responseGamma;
        }

//        public double getNominalDistance() {
//            return nominalDistance;
//        }
//        public void setNominalDistance(double nominalDistance) {
//            this.nominalDistance = nominalDistance;
//        }
    }

    public static class CalibrationSettings extends Settings {
        public CalibrationSettings() { }
        public CalibrationSettings(ProjectiveDevice.CalibrationSettings settings) {
            super(settings);
            this.initAspectRatio = settings.initAspectRatio;
            this.flags           = settings.flags;
        }

        double initAspectRatio = 1.0;
        int flags = CV_CALIB_FIX_K3 | CV_CALIB_FIX_K4 |
                    CV_CALIB_FIX_K5 | CV_CALIB_FIX_K6 | CV_CALIB_FIX_INTRINSIC;

        public double getInitAspectRatio() {
            return initAspectRatio;
        }
        public void setInitAspectRatio(double initAspectRatio) {
            this.initAspectRatio = initAspectRatio;
        }

        public boolean isUseIntrinsicGuess() {
            return (flags & CV_CALIB_USE_INTRINSIC_GUESS) != 0;
        }
        public void setUseIntrinsicGuess(boolean useIntrinsicGuess) {
            if (useIntrinsicGuess) {
                flags |= CV_CALIB_USE_INTRINSIC_GUESS;
            } else {
                flags &= ~CV_CALIB_USE_INTRINSIC_GUESS;
            }
        }

        public boolean isFixAspectRatio() {
            return (flags & CV_CALIB_FIX_ASPECT_RATIO) != 0;
        }
        public void setFixAspectRatio(boolean fixAspectRatio) {
            if (fixAspectRatio) {
                flags |= CV_CALIB_FIX_ASPECT_RATIO;
            } else {
                flags &= ~CV_CALIB_FIX_ASPECT_RATIO;
            }
        }

        public boolean isFixPrincipalPoint() {
            return (flags & CV_CALIB_FIX_PRINCIPAL_POINT) != 0;
        }
        public void setFixPrincipalPoint(boolean fixPrincipalPoint) {
            if (fixPrincipalPoint) {
                flags |= CV_CALIB_FIX_PRINCIPAL_POINT;
            } else {
                flags &= ~CV_CALIB_FIX_PRINCIPAL_POINT;
            }
        }

        public boolean isZeroTangentDist() {
            return (flags & CV_CALIB_ZERO_TANGENT_DIST) != 0;
        }
        public void setZeroTangentDist(boolean zeroTangentDist) {
            if (zeroTangentDist) {
                flags |= CV_CALIB_ZERO_TANGENT_DIST;
            } else {
                flags &= ~CV_CALIB_ZERO_TANGENT_DIST;
            }
        }

        public boolean isFixFocalLength() {
            return (flags & CV_CALIB_FIX_FOCAL_LENGTH) != 0;
        }
        public void setFixFocalLength(boolean fixFocalLength) {
            if (fixFocalLength) {
                flags |= CV_CALIB_FIX_FOCAL_LENGTH;
            } else {
                flags &= ~CV_CALIB_FIX_FOCAL_LENGTH;
            }
        }

        public boolean isFixK1() {
            return (flags & CV_CALIB_FIX_K1) != 0;
        }
        public void setFixK1(boolean fixK1) {
            if (fixK1) {
                flags |= CV_CALIB_FIX_K1;
            } else {
                flags &= ~CV_CALIB_FIX_K1;
            }
        }

        public boolean isFixK2() {
            return (flags & CV_CALIB_FIX_K2) != 0;
        }
        public void setFixK2(boolean fixK2) {
            if (fixK2) {
                flags |= CV_CALIB_FIX_K2;
            } else {
                flags &= ~CV_CALIB_FIX_K2;
            }
        }

        public boolean isFixK3() {
            return (flags & CV_CALIB_FIX_K3) != 0;
        }
        public void setFixK3(boolean fixK3) {
            if (fixK3) {
                flags |= CV_CALIB_FIX_K3;
            } else {
                flags &= ~CV_CALIB_FIX_K3;
            }
        }

        public boolean isFixK4() {
            return (flags & CV_CALIB_FIX_K4) != 0;
        }
        public void setFixK4(boolean fixK4) {
            if (fixK4) {
                flags |= CV_CALIB_FIX_K4;
            } else {
                flags &= ~CV_CALIB_FIX_K4;
            }
        }

        public boolean isFixK5() {
            return (flags & CV_CALIB_FIX_K5) != 0;
        }
        public void setFixK5(boolean fixK5) {
            if (fixK5) {
                flags |= CV_CALIB_FIX_K5;
            } else {
                flags &= ~CV_CALIB_FIX_K5;
            }
        }

        public boolean isFixK6() {
            return (flags & CV_CALIB_FIX_K6) != 0;
        }
        public void setFixK6(boolean fixK6) {
            if (fixK6) {
                flags |= CV_CALIB_FIX_K6;
            } else {
                flags &= ~CV_CALIB_FIX_K6;
            }
        }

        public boolean isRationalModel() {
            return (flags & CV_CALIB_RATIONAL_MODEL) != 0;
        }
        public void setRationalModel(boolean rationalModel) {
            if (rationalModel) {
                flags |= CV_CALIB_RATIONAL_MODEL;
            } else {
                flags &= ~CV_CALIB_RATIONAL_MODEL;
            }
        }

        public boolean isStereoFixIntrinsic() {
            return (flags & CV_CALIB_FIX_INTRINSIC) != 0;
        }
        public void setStereoFixIntrinsic(boolean stereoFixIntrinsic) {
            if (stereoFixIntrinsic) {
                flags |= CV_CALIB_FIX_INTRINSIC;
            } else {
                flags &= ~CV_CALIB_FIX_INTRINSIC;
            }
        }

        public boolean isStereoSameFocalLength() {
            return (flags & CV_CALIB_SAME_FOCAL_LENGTH) != 0;
        }
        public void setStereoSameFocalLength(boolean stereoSameFocalLength) {
            if (stereoSameFocalLength) {
                flags |= CV_CALIB_SAME_FOCAL_LENGTH;
            } else {
                flags &= ~CV_CALIB_SAME_FOCAL_LENGTH;
            }
        }
    }

    public static class CalibratedSettings extends Settings {
        public CalibratedSettings() { }
        public CalibratedSettings(ProjectiveDevice.CalibratedSettings settings) {
            super(settings);
            this.parametersFile = settings.parametersFile;
        }
        File parametersFile = new File("calibration.yaml");

        public File getParametersFile() {
            return parametersFile;
        }
        public void setParametersFile(File parametersFile) {
            this.parametersFile = parametersFile;
        }
        public String getParametersFilename() {
            return parametersFile == null ? "" : parametersFile.getPath();
        }
        public void setParametersFilename(String parametersFilename) {
            this.parametersFile = parametersFilename == null ||
                    parametersFilename.length() == 0 ? null : new File(parametersFilename);
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public int imageWidth = 0, imageHeight = 0;

    public CvMat cameraMatrix = null, distortionCoeffs = null,
                 extrParams = null, reprojErrs = null;
    public double avgReprojErr, maxReprojErr;
//    public double nominalDistance = 0;

    public CvMat R = null, T = null, E = null, F = null;
    public double avgEpipolarErr, maxEpipolarErr;

    public String colorOrder = "BGR";
    public CvMat colorMixingMatrix = null, additiveLight = null;
    public double avgColorErr, colorR2 = 1.0;

    public void rescale(int imageWidth, int imageHeight) {
        if ((imageWidth != this.imageWidth || imageHeight != this.imageHeight) &&
                cameraMatrix != null) {
            double sx = (double)imageWidth /this.imageWidth;
            double sy = (double)imageHeight/this.imageHeight;
            cameraMatrix.put(0, sx*cameraMatrix.get(0));
            cameraMatrix.put(1, sx*cameraMatrix.get(1));
            cameraMatrix.put(2, sx*cameraMatrix.get(2));
            cameraMatrix.put(3, sy*cameraMatrix.get(3));
            cameraMatrix.put(4, sy*cameraMatrix.get(4));
            cameraMatrix.put(5, sy*cameraMatrix.get(5));
            this.imageWidth  = imageWidth;
            this.imageHeight = imageHeight;
            int p = mapsPyramidLevel;
            undistortMaps1[p] = undistortMaps2[p] = distortMaps1[p] = distortMaps2[p] = null;
        }
    }

    public int[] getRGBColorOrder() {
        int[] order = new int[3];
        for (int i = 0; i < 3; i++) {
            switch (Character.toUpperCase(colorOrder.charAt(i))) {
                case 'B': order[i] = 2; break;
                case 'G': order[i] = 1; break;
                case 'R': order[i] = 0; break;
                default: assert (false);
            }
        }
        return order;
    }

//    public double getNominalDistance(int objectWidth, int objectHeight) {
//        double f = (cameraMatrix.get(0)+cameraMatrix.get(4))/(2*cameraMatrix.get(8));
//        double imageSize = Math.sqrt(imageWidth *imageWidth +
//                                     imageHeight*imageHeight);
//        double objectSize = Math.sqrt(objectWidth *objectWidth +
//                                      objectHeight*objectHeight);
//        return f*objectSize/imageSize;
//    }
//    public double getNominalDistance(MarkedPlane board) {
//        return getNominalDistance(board.getWidth(), board.getHeight());
//    }

    //Compensates for radial and tangential distortion. Model From Oulu university.
    //Code ported from Camera Calibration Toolbox for Matlab by Jean-Yves Bouguet
    //http://www.vision.caltech.edu/bouguetj/calib_doc/
    //function name: comp_distortion_oulu()
    //
    //INPUT: xd: distorted (normalized) point coordinates in the image plane (2xN matrix)
    //       k: Distortion coefficients (radial and tangential) (4x1 vector)
    //
    //OUTPUT: x: undistorted (normalized) point coordinates in the image plane (2xN matrix)
    //
    //Method: Iterative method for compensation.
    //
    //NOTE: This compensation has to be done after the subtraction
    //      of the principal point, and division by the focal length.
    public static double[] undistort(double[] xd, double[] k) {
        double k1 = k[0];
        double k2 = k[1];
        double k3 = k.length > 4 ? k[4] : 0;
        // XXX: need to use those new distortion coefficients
        double k4 = k.length > 5 ? k[5] : 0;
        double k5 = k.length > 6 ? k[6] : 0;
        double k6 = k.length > 7 ? k[7] : 0;
        double p1 = k[2];
        double p2 = k[3];

        double[] xu = xd.clone(); // initial guess

        for (int i = 0; i < xd.length/2; i++) {
            double x  = xu[i*2], y  = xu[i*2 + 1];
            double xo = xd[i*2], yo = xd[i*2 + 1];
            for (int j = 0; j < 20; j++) {
                double r_2 = x*x + y*y;
                double k_radial = 1 + k1*r_2 + k2*r_2*r_2 + k3*r_2*r_2*r_2;
                double delta_x = 2*p1*x*y         + p2*(r_2 + 2*x*x);
                double delta_y = p1*(r_2 + 2*y*y) + 2*p2*x*y;
                x = (xo - delta_x)/k_radial;
                y = (yo - delta_y)/k_radial;
            }
            xu[i*2] = x; xu[i*2 + 1] = y;
        }
        return xu;
    }
    public double[] undistort(double ... x) {
        double[] xn = normalize(x, cameraMatrix);
        double[] xu = undistort(xn, distortionCoeffs.get());
        return unnormalize(xu, cameraMatrix);
    }

    public static double[] distort(double[] xu, double[] k) {
        double k1 = k[0];
        double k2 = k[1];
        double k3 = k.length > 4 ? k[4] : 0;
        // XXX: need to use those new distortion coefficients
        double k4 = k.length > 5 ? k[5] : 0;
        double k5 = k.length > 6 ? k[6] : 0;
        double k6 = k.length > 7 ? k[7] : 0;
        double p1 = k[2];
        double p2 = k[3];

        double[] xd = xu.clone();

        for (int i = 0; i < xu.length/2; i++) {
            double x = xu[i*2    ],
                   y = xu[i*2 + 1];
            double r_2 = x*x + y*y;
            double k_radial = 1 + k1*r_2 + k2*r_2*r_2 + k3*r_2*r_2*r_2;
            double delta_x = 2*p1*x*y         + p2*(r_2 + 2*x*x);
            double delta_y = p1*(r_2 + 2*y*y) + 2*p2*x*y;
            xd[i*2    ] = x*k_radial + delta_x;
            xd[i*2 + 1] = y*k_radial + delta_y;
        }
        return xd;
    }
    public double[] distort(double ... x) {
        double[] xn = normalize(x, cameraMatrix);
        double[] xd = distort(xn, distortionCoeffs.get());
        return unnormalize(xd, cameraMatrix);
    }

    public static double[] normalize(double[] xu, CvMat K) {
        double[] xn = xu.clone();

        double fx = K.get(0)/K.get(8);
        double fy = K.get(4)/K.get(8);
        double dx = K.get(2)/K.get(8);
        double dy = K.get(5)/K.get(8);
        double s  = K.get(1)/K.get(8);
        for (int i = 0; i < xu.length/2; i++) {
            xn[i*2    ] = (xu[i*2    ] - dx)/fx - s*(xu[i*2 + 1] + dy)/(fx*fy);
            xn[i*2 + 1] = (xu[i*2 + 1] - dy)/fy;
        }
        return xn;
    }
    public static double[] unnormalize(double[] xn, CvMat K) {
        double[] xu = xn.clone();

        double fx = K.get(0)/K.get(8);
        double fy = K.get(4)/K.get(8);
        double dx = K.get(2)/K.get(8);
        double dy = K.get(5)/K.get(8);
        double s  = K.get(1)/K.get(8);
        for (int i = 0; i < xn.length/2; i++) {
            xu[i*2    ] = fx*xn[i*2    ] + dx + s*xn[i*2 + 1];
            xu[i*2 + 1] = fy*xn[i*2 + 1] + dy;
        }
        return xu;
    }


    private boolean fixedPointMaps = false;
    private int mapsPyramidLevel = 0;
    private IplImage[] undistortMaps1 = { null }, undistortMaps2 = { null };
    private IplImage[] distortMaps1 = { null }, distortMaps2 = { null };
    private IplImage tempImage = null;

    public boolean isFixedPointMaps() {
        return fixedPointMaps;
    }
    public void setFixedPointMaps(boolean fixedPointMaps) {
        if (this.fixedPointMaps != fixedPointMaps) {
            this.fixedPointMaps = fixedPointMaps;
            int p = mapsPyramidLevel;
            undistortMaps1[p] = undistortMaps2[p] = distortMaps1[p] = distortMaps2[p] = null;
        }
    }

    public int getMapsPyramidLevel() {
        return mapsPyramidLevel;
    }
    public void setMapsPyramidLevel(int mapsPyramidLevel) {
        if (this.mapsPyramidLevel != mapsPyramidLevel) {
            this.mapsPyramidLevel = mapsPyramidLevel;
            int p = mapsPyramidLevel;
            if (p >= undistortMaps1.length || p >= undistortMaps2.length ||
                    p >= distortMaps1.length || p >= distortMaps2.length) {
                undistortMaps1 = Arrays.copyOf(undistortMaps1, p+1);
                undistortMaps2 = Arrays.copyOf(undistortMaps2, p+1);
                distortMaps1   = Arrays.copyOf(distortMaps1, p+1);
                distortMaps2   = Arrays.copyOf(distortMaps2, p+1);
            }
        }
    }

    private void initUndistortMaps() {
        //cvUndistort2(src, dst, cameraMatrix, distortionCoeffs);
        int p = mapsPyramidLevel;
        if (undistortMaps1[p] == null || undistortMaps2[p] == null) {
            if (fixedPointMaps) {
                undistortMaps1[p] = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_16S, 2);
                undistortMaps2[p] = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_16U, 1);
            } else {
                undistortMaps1[p] = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32F, 1);
                undistortMaps2[p] = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32F, 1);
            }
            Mat A = cvarrToMat(cameraMatrix);
            Mat m1 = cvarrToMat(undistortMaps1[p]);
            Mat m2 = cvarrToMat(undistortMaps2[p]);
            initUndistortRectifyMap(A, cvarrToMat(distortionCoeffs), new Mat(), A, m1.size(), m1.type(), m1, m2);
            if (mapsPyramidLevel > 0) {
                IplImage map1 = undistortMaps1[p];
                IplImage map2 = undistortMaps2[p];
                int w = imageWidth  >> p;
                int h = imageHeight >> p;
                undistortMaps1[p] = IplImage.create(w, h, map1.depth(), map1.nChannels());
                undistortMaps2[p] = IplImage.create(w, h, map2.depth(), map2.nChannels());
                cvResize(map1, undistortMaps1[p], CV_INTER_NN);
                cvResize(map2, undistortMaps2[p], CV_INTER_NN);
//                FloatBuffer m1 = map1.getFloatBuffer();
//                FloatBuffer n1 = undistortMaps1[p].getFloatBuffer();
//                for (int i = 0; i < 8; i++) {
//                    System.out.println(m1.get(1280*2*i) - n1.get(640*i));
//                }
            }
        }
//        if (undistortMap1 == null || undistortMap2 == null) {
//            IplImage mapx = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32F, 1);
//            IplImage mapy = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32F, 1);
//            FloatBuffer bufx = mapx.getFloatBuffer();
//            FloatBuffer bufy = mapy.getFloatBuffer();
//            int width  = mapx.width();
//            int height = mapx.height();
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    double[] undistxy = undistort(x, y);
//                    bufx.put((float)undistxy[0]);
//                    bufy.put((float)undistxy[1]);
//                }
//            }
//            if (useFixedPointMaps) {
//                undistortMap1 = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_16S, 2);
//                undistortMap2 = IplImage.create(imageWidth, imageHeight, is20or21 ? IPL_DEPTH_16U : IPL_DEPTH_16S, 1);
//                cvConvertMaps(mapx, mapy, undistortMap1, undistortMap2);
//                mapx.release();
//                mapy.release();
//            } else {
//                undistortMap1 = mapx;
//                undistortMap2 = mapy;
//            }
//        }
    }
    public IplImage getUndistortMap1() {
        initUndistortMaps();
        return undistortMaps1[mapsPyramidLevel];
    }
    public IplImage getUndistortMap2() {
        initUndistortMaps();
        return undistortMaps2[mapsPyramidLevel];
    }
    public void undistort(IplImage src, IplImage dst) {
        if (src != null && dst != null) {
            initUndistortMaps();
            cvRemap(src, dst, undistortMaps1[mapsPyramidLevel], undistortMaps2[mapsPyramidLevel],
                    CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
        }
    }
    public IplImage undistort(IplImage image) {
        if (image != null) {
            initUndistortMaps();
            tempImage = IplImage.createIfNotCompatible(tempImage, image);
            cvResetImageROI(tempImage);
            cvRemap(image, tempImage, undistortMaps1[mapsPyramidLevel], undistortMaps2[mapsPyramidLevel],
                    CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
            return tempImage;
        }
        return null;
    }

    private void initDistortMaps() {
        int p = mapsPyramidLevel;
        if (distortMaps1[p] == null || distortMaps2[p] == null) {
            IplImage mapx = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32F, 1);
            IplImage mapy = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32F, 1);
            FloatBuffer bufx = mapx.getFloatBuffer();
            FloatBuffer bufy = mapy.getFloatBuffer();
            int width  = mapx.width();
            int height = mapx.height();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double[] distxy = undistort(x, y);
                    bufx.put((float)distxy[0]);
                    bufy.put((float)distxy[1]);
                }
            }
            if (fixedPointMaps) {
                distortMaps1[p] = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_16S, 2);
                distortMaps2[p] = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_16U /* IPL_DEPTH_16S */, 1);
                cvConvertMaps(mapx, mapy, distortMaps1[p], distortMaps2[p]);
                mapx.release();
                mapy.release();
            } else {
                distortMaps1[p] = mapx;
                distortMaps2[p] = mapy;
            }
            if (mapsPyramidLevel > 0) {
                IplImage map1 = distortMaps1[p];
                IplImage map2 = distortMaps2[p];
                int w = imageWidth  >> p;
                int h = imageHeight >> p;
                distortMaps1[p] = IplImage.create(w, h, map1.depth(), map1.nChannels());
                distortMaps2[p] = IplImage.create(w, h, map2.depth(), map2.nChannels());
                cvResize(map1, distortMaps1[p], CV_INTER_NN);
                cvResize(map2, distortMaps2[p], CV_INTER_NN);
            }
        }
    }
    public IplImage getDistortMap1() {
        initDistortMaps();
        return distortMaps1[mapsPyramidLevel];
    }
    public IplImage getDistortMap2() {
        initDistortMaps();
        return distortMaps2[mapsPyramidLevel];
    }
    public void distort(IplImage src, IplImage dst) {
        if (src != null && dst != null) {
            initDistortMaps();
            cvRemap(src, dst, distortMaps1[mapsPyramidLevel], distortMaps2[mapsPyramidLevel],
                    CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
        }
    }
    public IplImage distort(IplImage image) {
        if (image != null) {
            initDistortMaps();
            tempImage = IplImage.createIfNotCompatible(tempImage, image);
            cvRemap(image, tempImage, distortMaps1[mapsPyramidLevel], distortMaps2[mapsPyramidLevel],
                    CV_INTER_LINEAR | CV_WARP_FILL_OUTLIERS, CvScalar.ZERO);
            return tempImage;
        }
        return null;
    }

    // B = [ (-R^T t)*plane^T - plane^T*(-R^T t) I ] [ (K R)^-1  ]
    //                                               [  0  0  0  ]
    // where plane = [ n | d ]
    // B is a 4x3 matrix
    private static ThreadLocal<CvMat>
            temp3x3 = CvMat.createThreadLocal(3, 3);
    public CvMat getBackProjectionMatrix(CvMat n, double d, CvMat B) {
        CvMat temp = temp3x3.get();

        temp.cols(1); temp.step(temp.step()/3);
        B.rows(3);
        cvGEMM(R, T, -1,    null, 0,  temp,  CV_GEMM_A_T);
        cvGEMM(temp, n, 1,  null, 0,  B,     CV_GEMM_B_T);
        double a = cvDotProduct(n, temp) + d;
        B.put(0, B.get(0) - a);
        B.put(4, B.get(4) - a);
        B.put(8, B.get(8) - a);
        B.rows(4);
        temp.cols(3); temp.step(temp.step()*3);

        B.put(9, n.get());

        cvMatMul(cameraMatrix, R, temp);
        cvInvert(temp, temp, CV_LU);

        cvMatMul(B, temp, B);
        cvConvertScale(B, B, 1/B.get(11), 0);

        return B;
    }

    private static ThreadLocal<CvMat>
            B4x3 = CvMat.createThreadLocal(4, 3),
            a4x1 = CvMat.createThreadLocal(4, 1),
            t3x1 = CvMat.createThreadLocal(3, 1);
    public CvMat getFrontoParallelH(double[] roipts, CvMat n, CvMat H) {
        CvMat B = B4x3.get(), a = a4x1.get(), t = t3x1.get();

        // compute rotation from n to z-axis
        double s = Math.signum(n.get(2));
        double[] dir = JavaCV.unitize(-s*n.get(1), s*n.get(0));
        double theta = Math.acos(s*n.get(2)/JavaCV.norm(n.get()));
        t.put(theta*dir[0], theta*dir[1], 0.0);
        Rodrigues(cvarrToMat(t), cvarrToMat(H), null);

        // and from z-axis to device axis
        cvMatMul(R, H, H);

        double x = 0, y = 0;
        if (roipts != null) {
            // find the middle of the ROI
            double x1 = roipts[0], y1 = roipts[1],
                   x2 = roipts[4], y2 = roipts[5],
                   x3 = roipts[2], y3 = roipts[3],
                   x4 = roipts[6], y4 = roipts[7];
            double u = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3))/
                       ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
            x = x1 + u*(x2-x1);
            y = y1 + u*(y2-y1);
        }

        // compute 3D point from the middle of the ROI, and
        // form optimal homography for n
        getBackProjectionMatrix(n, -1, B);
        t.put(x, y, 1);
        cvMatMul(B, t, a);
        H.put(2, a.get(0)/a.get(3));
        H.put(5, a.get(1)/a.get(3));
        H.put(8, a.get(2)/a.get(3));

        return H;
    }

    // returns a homography that can be applied to pixel coordinates
    private static ThreadLocal<CvMat>
            relativeR3x3 = CvMat.createThreadLocal(3, 3),
            relativeT3x1 = CvMat.createThreadLocal(3, 1),
            R13x3 = CvMat.createThreadLocal(3, 3), P13x4 = CvMat.createThreadLocal(3, 4),
            R23x3 = CvMat.createThreadLocal(3, 3), P23x4 = CvMat.createThreadLocal(3, 4);
    public CvMat getRectifyingHomography(ProjectiveDevice peer, CvMat H) {
        CvMat relativeR = relativeR3x3.get(), relativeT = relativeT3x1.get();
        cvGEMM(R,         peer.R,  1,  null, 0,  relativeR, CV_GEMM_B_T);
        cvGEMM(relativeR, peer.T, -1,  T,    1,  relativeT, 0);

        CvMat R1 = R13x3.get(); CvMat P1 = P13x4.get();
        CvMat R2 = R23x3.get(); CvMat P2 = P23x4.get();
        Size imageSize = new Size((peer.imageWidth  + imageWidth )/2,
                                  (peer.imageHeight + imageHeight)/2); // ?
        stereoRectify(cvarrToMat(peer.cameraMatrix), cvarrToMat(peer.distortionCoeffs),
                      cvarrToMat(     cameraMatrix), cvarrToMat(     distortionCoeffs),
                      imageSize, cvarrToMat(relativeR), cvarrToMat(relativeT),
                      cvarrToMat(R1), cvarrToMat(R2), cvarrToMat(P1), cvarrToMat(P2),
                      new Mat(), 0, -1, new Size(), null, null);
        cvMatMul(cameraMatrix, R2, R2);
        cvInvert(cameraMatrix, R1);
        cvMatMul(R2, R1, H);

        return H;
    }

    public static class Exception extends java.lang.Exception {
        public Exception(String message) { super(message); }
        public Exception(String message, Throwable cause) { super(message, cause); }
    }

    public static ProjectiveDevice[] read(String filename) throws Exception {
        FileStorage fs = new FileStorage(filename, FileStorage.READ);
        CameraDevice    [] cameraDevices    = CameraDevice   .read(fs);
        ProjectorDevice [] projectorDevices = ProjectorDevice.read(fs);
        ProjectiveDevice[] devices = new ProjectiveDevice[cameraDevices.length+projectorDevices.length];
        int i = 0;
        for (ProjectiveDevice d : cameraDevices) {
            devices[i++] = d;
        }
        for (ProjectiveDevice d : projectorDevices) {
            devices[i++] = d;
        }
        fs.release();
        return devices;
    }

    public static void write(String filename, ProjectiveDevice[] ... devices) {
        int totalLength = 0;
        for (ProjectiveDevice[] ds : devices) {
            totalLength += ds.length;
        }
        ProjectiveDevice[] allDevices = new ProjectiveDevice[totalLength];
        int i = 0;
        for (ProjectiveDevice[] ds : devices) {
            for (ProjectiveDevice d : ds) {
                allDevices[i++] = d;
            }
        }
        write(filename, allDevices);
    }
    public static void write(String filename, ProjectiveDevice ... devices) {
        FileStorage fs = new FileStorage(filename, FileStorage.WRITE);

        shiftLeft(shiftLeft(fs, "Cameras"), "[");
        for (ProjectiveDevice d : devices) {
            if (d instanceof CameraDevice) {
                opencv_core.write(fs, d.getSettings().getName());
            }
        }
        shiftLeft(fs, "]");

        shiftLeft(shiftLeft(fs, "Projectors"), "[");
        for (ProjectiveDevice d : devices) {
            if (d instanceof ProjectorDevice) {
                opencv_core.write(fs, d.getSettings().getName());
            }
        }
        shiftLeft(fs, "]");

        for (ProjectiveDevice d : devices) {
            d.writeParameters(fs);
        }
        fs.release();
    }

    public void writeParameters(File file) {
        writeParameters(file.getAbsolutePath());
    }
    public void writeParameters(String filename) {
        FileStorage fs = new FileStorage(filename, FileStorage.WRITE);
        writeParameters(fs);
        fs.release();
    }
    public void writeParameters(FileStorage fs) {
        shiftLeft(shiftLeft(fs, getSettings().getName()), "{");

        opencv_core.write(fs, "imageWidth", imageWidth);
        opencv_core.write(fs, "imageHeight", imageHeight);
        opencv_core.write(fs, "responseGamma", getSettings().getResponseGamma());
//        opencv_core.write(fs, "initAspectRatio", settings.initAspectRatio);
//        opencv_core.write(fs, "flags", getSettings().flags);
        if (cameraMatrix != null)
            opencv_core.write(fs, "cameraMatrix", cvarrToMat(cameraMatrix));
        if (distortionCoeffs != null)
            opencv_core.write(fs, "distortionCoeffs", cvarrToMat(distortionCoeffs));
        if (extrParams != null)
            opencv_core.write(fs, "extrParams", cvarrToMat(extrParams));
        if (reprojErrs != null)
            opencv_core.write(fs, "reprojErrs", cvarrToMat(reprojErrs));
        opencv_core.write(fs, "avgReprojErr", avgReprojErr);
        opencv_core.write(fs, "maxReprojErr", maxReprojErr);
//        opencv_core.write(fs, "nominalDistance", nominalDistance);
        if (R != null)
            opencv_core.write(fs, "R", cvarrToMat(R));
        if (T != null)
            opencv_core.write(fs, "T", cvarrToMat(T));
        if (E != null)
            opencv_core.write(fs, "E", cvarrToMat(E));
        if (F != null)
            opencv_core.write(fs, "F", cvarrToMat(F));
        opencv_core.write(fs, "avgEpipolarErr", avgEpipolarErr);
        opencv_core.write(fs, "maxEpipolarErr", maxEpipolarErr);

        opencv_core.write(fs, "colorOrder", colorOrder);
        if (colorMixingMatrix != null)
            opencv_core.write(fs, "colorMixingMatrix", cvarrToMat(colorMixingMatrix));
        if (additiveLight != null)
            opencv_core.write(fs, "additiveLight", cvarrToMat(additiveLight));
        opencv_core.write(fs, "avgColorErr", avgColorErr);
        opencv_core.write(fs, "colorR2", colorR2);

        shiftLeft(fs, "}");
    }

    public void readParameters(File file) throws Exception {
        readParameters(file.getAbsolutePath());
    }
    public void readParameters(String filename) throws Exception {
        FileStorage fs = new FileStorage(filename, FileStorage.READ);
        readParameters(fs);
        fs.release();
    }
    public void readParameters(FileStorage fs) throws Exception {
        if (fs == null) {
            throw new Exception("Error: FileStorage is null, cannot read parameters for device " + 
                    getSettings().getName() + ". Is the parametersFile correct?");
        }
        FileNode fn = fs.get(getSettings().getName());
        if (fn == null) {
            throw new Exception("Error: FileNode is null, cannot read parameters for device " + 
                    getSettings().getName() + ". Is the name correct?");
        }

        FileNode n;
        if ((n = fn.get("imageWidth")).isInt()) imageWidth = n.asInt();
        if ((n = fn.get("imageHeight")).isInt()) imageHeight = n.asInt();
        if ((n = fn.get("gamma")).isReal()) getSettings().setResponseGamma(n.asDouble());
//        if ((n = fn.get("initAspectRatio")).isReal()) getSettings().setInitAspectRatio(n.asDouble());
//        if ((n = fn.get("flags")).isInt()) getSettings().setFlags(n.asInt());
        Mat m = new Mat();
        opencv_core.read(fn.get("cameraMatrix"), m);
        cameraMatrix = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("distortionCoeffs"), m);
        distortionCoeffs = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("extrParams"), m);
        extrParams = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("reprojErrs"), m);
        reprojErrs = m.empty() ? null : cvMat(m).clone();
        if ((n = fn.get("avgReprojErr")).isReal()) avgReprojErr = n.asDouble();
        if ((n = fn.get("maxReprojErr")).isReal()) maxReprojErr = n.asDouble();
//        if ((n = fn.get("nominalDistance")).isReal()) nominalDistance = n.asDouble();
        opencv_core.read(fn.get("R"), m);
        R = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("T"), m);
        T = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("E"), m);
        E = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("F"), m);
        F = m.empty() ? null : cvMat(m).clone();
        if ((n = fn.get("avgEpipolarErr")).isReal()) avgEpipolarErr = n.asDouble();
        if ((n = fn.get("maxEpipolarErr")).isReal()) maxEpipolarErr = n.asDouble();

        if ((n = fn.get("colorOrder")).isString()) colorOrder = n.asBytePointer().getString();
        opencv_core.read(fn.get("colorMixingMatrix"), m);
        colorMixingMatrix = m.empty() ? null : cvMat(m).clone();
        opencv_core.read(fn.get("additiveLight"), m);
        additiveLight = m.empty() ? null : cvMat(m).clone();
        if ((n = fn.get("avgColorErr")).isReal()) avgColorErr = n.asDouble();
        if ((n = fn.get("colorR2")).isReal()) colorR2 = n.asDouble();
    }

    @Override public String toString() {
        String s =
        getSettings().getName() + " (" + imageWidth + " x " + imageHeight + ")\n";
        for (int i = 0; i < getSettings().getName().length(); i++) {
            s += "=";
        }
        s += "\n" +
        "Intrinsics\n" +
        "----------\n" +
        "camera matrix = " + (cameraMatrix == null ? "null" : cameraMatrix.toString(16)) + "\n" +
        "distortion coefficients = " + (distortionCoeffs == null ? "null" : distortionCoeffs) + "\n" +
        "reprojection RMS/max error (pixels) = " + (float)avgReprojErr + " / " + (float)maxReprojErr + "\n\n" +

        "Extrinsics\n" +
        "----------\n" +
        "rotation = " + (R == null ? "null" : R.toString(11)) + "\n" +
        "translation = " + (T == null ? "null" : T.toString(14)) + "\n" +
        "epipolar RMS/max error (pixels) = " + (float)avgEpipolarErr + " / " + (float)maxEpipolarErr + "\n\n" +

        "Color\n" +
        "-----\n" +
        "order = " + colorOrder + "\n" +
        "mixing matrix = " + (colorMixingMatrix == null ? "null" : colorMixingMatrix.toString(16)) + "\n" +
        "additive light = " + (additiveLight == null ? "null" : additiveLight.toString(17)) + "\n" +
        "normalized RMSE (intensity) = " + (float)avgColorErr + "\n" +
        "R^2 (intensity) = " + (float)colorR2;

        return s;
    }
}
