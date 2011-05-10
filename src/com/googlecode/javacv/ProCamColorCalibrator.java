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

import java.awt.Color;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class ProCamColorCalibrator {

    public ProCamColorCalibrator(Settings settings, MarkerDetector.Settings detectorSettings,
            MarkedPlane boardPlane, CameraDevice camera, ProjectorDevice projector) {
        this.settings = settings;
        this.markerDetector = new MarkerDetector(detectorSettings);
        this.boardPlane = boardPlane;
        this.camera = camera;
        this.projector = projector;

        Marker[] boardMarkers = boardPlane.getMarkers();
        boardSrcPts = CvMat.create(4 + boardMarkers.length*4, 1, CV_64F, 2);
        boardDstPts = CvMat.create(4 + boardMarkers.length*4, 1, CV_64F, 2);
        boardSrcPts.put(0.0,                   0.0,
                        boardPlane.getWidth(), 0.0,
                        boardPlane.getWidth(), boardPlane.getHeight(),
                        0.0,                   boardPlane.getHeight());
        for (int i = 0; i < boardMarkers.length; i++) {
            boardSrcPts.put(8 + i*8, boardMarkers[i].corners);
        }
        projSrcPts = CvMat.create(4, 1, CV_64F, 2);
        projDstPts = CvMat.create(4, 1, CV_64F, 2);
        projSrcPts.put(0.0,               0.0,
                       projector.imageWidth-1, 0.0,
                       projector.imageWidth-1, projector.imageHeight-1,
                       0.0,                    projector.imageHeight-1);
        camKinv = CvMat.create(3, 3);
//        CvMat projKinv = CvMat.create(3, 3);
        cvInvert(camera.cameraMatrix, camKinv);
//        cvInvert(projector.cameraMatrix, projKinv);
    }

    public static class Settings extends BaseChildSettings {
        int samplesPerChannel = 4;
        double trimmingFraction = 0.01;
        double detectedBoardMin = 0.5;

        public int getSamplesPerChannel() {
            return samplesPerChannel;
        }
        public void setSamplesPerChannel(int samplesPerChannel) {
            this.samplesPerChannel = samplesPerChannel;
        }

//        public double getTrimmingFraction() {
//            return trimmingFraction;
//        }
//        public void setTrimmingFraction(double trimmingFraction) {
//            this.trimmingFraction = trimmingFraction;
//        }

        public double getDetectedBoardMin() {
            return detectedBoardMin;
        }
        public void setDetectedBoardMin(double detectedBoardMin) {
            this.detectedBoardMin = detectedBoardMin;
        }
    }

    private Settings settings;

    private MarkerDetector markerDetector = null;
    private MarkedPlane boardPlane = null;
    private CameraDevice camera = null;
    private ProjectorDevice projector = null;
    private Color[] projectorColors = null, cameraColors = null;
    private int counter = 0;

    private CvMat boardSrcPts, boardDstPts;
    private CvMat projSrcPts, projDstPts;
    private CvMat camKinv;
    private IplImage mask, mask2, undistImage;

    public int getColorCount() {
        return counter;
    }

    public Color[] getProjectorColors() {
        double invgamma = 1/projector.getSettings().getResponseGamma();
        int s = settings.samplesPerChannel;
        if (projectorColors == null) {
            projectorColors = new Color[s*s*s];
            cameraColors    = new Color[s*s*s];
            for (int i = 0; i < projectorColors.length; i++) {
                 int j = i/s;
                 int k = j/s;
                double r = Math.pow((double)(i%s)/(s-1), invgamma);
                double g = Math.pow((double)(j%s)/(s-1), invgamma);
                double b = Math.pow((double)(k%s)/(s-1), invgamma);
                projectorColors[i] = new Color((float)r, (float)g, (float)b);
            }
        }
        return projectorColors;
    }
    public Color getProjectorColor() {
        return getProjectorColors()[counter];
    }

    public Color[] getCameraColors() {
        return cameraColors;
    }
    public Color getCameraColor() {
        return getCameraColors()[counter];
    }

    public void addCameraColor() {
        counter++;
    }
    public void addCameraColor(Color color) {
        cameraColors[counter++] = color;
    }

    public IplImage getMaskImage() {
        return mask;
    }
    public IplImage getUndistortedCameraImage() {
        return undistImage;
    }

//    public CvScalar getProjectorColor() {
//        if (counter == 0) {
//            return CvScalar.CV_RGB(0,0,0);
//        } else if (counter == 1) {
//            return CvScalar.CV_RGB(255,255,255);
//        }
//        // smallest (power of 2) number of channel that can accomodate "counter" number of samples
//        int level = (int)Math.ceil(Math.log(Math.cbrt(counter+1))/Math.log(2));
//        int samplesPerChannel = (int)Math.pow(2, level)+1;
//        int prevSamplesPerChannel = (int)Math.pow(2, level-1)+1;
//        int ignoreAmount = prevSamplesPerChannel*prevSamplesPerChannel*prevSamplesPerChannel;
//        int samplesToJump = counter - Math.max(ignoreAmount, 2);
//
//        int n = 0;
//        for (int i = 0; ; i++) {
//            int j = i/samplesPerChannel;
//            int k = j/samplesPerChannel;
//
//            int ri = (i%samplesPerChannel);
//            int gi = (j%samplesPerChannel);
//            int bi = (k%samplesPerChannel);
//
//            // only count odd samples, that are "in between"
//            // samples we have already returned previously
//            if (ri%2 != 0 || gi%2 != 0 || bi%2 != 0) {
//                n++;
//            }
//
//            if (n > samplesToJump) {
//                int r = ri*255/(samplesPerChannel-1);
//                int g = gi*255/(samplesPerChannel-1);
//                int b = bi*255/(samplesPerChannel-1);
//                return CvScalar.CV_RGB(r,g,b);
//            }
//        }
//
//    }

    private static ThreadLocal<CvMat>
            H3x3 = CvMat.createThreadLocal(3, 3),
            R3x3 = CvMat.createThreadLocal(3, 3),
            t3x1 = CvMat.createThreadLocal(3, 1),
            n3x1 = CvMat.createThreadLocal(3, 1),
            z3x1 = CvMat.createThreadLocal(3, 1);
    public boolean processCameraImage(IplImage cameraImage) {
        if (undistImage == null ||
                undistImage.width()  != cameraImage.width()  ||
                undistImage.height() != cameraImage.height() ||
                undistImage.depth()  != cameraImage.depth()) {
            undistImage = cameraImage.clone();
        }

        if (mask == null || mask2 == null ||
                mask.width()  != cameraImage.width()  || mask2.width()  != cameraImage.width() ||
                mask.height() != cameraImage.height() || mask2.height() != cameraImage.width()) {
            mask = IplImage.create(cameraImage.width(), cameraImage.height(),
                    IPL_DEPTH_8U, 1, cameraImage.origin());
            mask2 = IplImage.create(cameraImage.width(), cameraImage.height(),
                    IPL_DEPTH_8U, 1, cameraImage.origin());
        }

        CvMat H = H3x3.get();
        CvMat R = R3x3.get();
        CvMat t = t3x1.get();
        CvMat n = n3x1.get();
        CvMat z = z3x1.get();
        z.put(0.0, 0.0, 1.0);

        // detect the markers in the camera image, to
        // 1. find the expected attenuation due to geometry
        // 2. use only regions we know are "white" on the board
        camera.undistort(cameraImage, undistImage, false);
        Marker[] detectedBoardMarkers = markerDetector.detect(undistImage, false);
        if (detectedBoardMarkers.length >= boardPlane.getMarkers().length*settings.detectedBoardMin) {
            // use detected markers in the camera image, to
            // 1. find the expected attenuation due to geometry
            // 2. use only regions we know are "white" on the board
            boardPlane.getTotalWarp(detectedBoardMarkers, H);
            cvPerspectiveTransform(boardSrcPts, boardDstPts, H);
            double[] boardPts = boardDstPts.get();

            // Extract R and t from the board homography, using it as our
            // "first camera", so we need to use z as the normal of the plane...
            cvMatMul(camKinv, H, R);
            double error = JavaCV.HnToRt(R, z, R, t);
//            System.out.println(error);

            // find the plane equation of the board in the camera's frame
            // (normal vector n and distance d) and get the back-projection
            // matrix of the camera
            cvMatMul(R, z, n);
            double d = cvDotProduct(t, z);

            // find the homography from the camera to the projector
            // H =  K_p (R - T*n^T/d)) K_c^-1
            cvGEMM  (projector.T, n, -1/d,      projector.R, 1,  H, CV_GEMM_B_T);
            cvMatMul(projector.cameraMatrix, H,                  H);
            cvMatMul(H, camKinv,                                 H);
            cvConvertScale(H, H, 1/H.get(8), 0);
//            System.out.println(H);

            // find the homography from the projector to the camera
            cvInvert(H, H);
            cvConvertScale(H, H, 1/H.get(8), 0);

            // reproject projector edges into the camera image
            cvPerspectiveTransform(projSrcPts, projDstPts, H);
            double[] projPts = projDstPts.get();

            // create mask containing only blank regions of the board
            // intersected with regions coverable by the projector
            cvSetZero(mask);
            double cx = 0, cy = 0;
            for (int j = 0; j < 4; j++) {
                cx += boardPts[j*2    ];
                cy += boardPts[j*2 + 1];
            }
            cx/=4; cy/=4;
            for (int j = 0; j < 4; j++) {
                boardPts[j*2    ] -= (boardPts[j*2    ] - cx)*settings.trimmingFraction;
                boardPts[j*2 + 1] -= (boardPts[j*2 + 1] - cy)*settings.trimmingFraction;
            }
            cvFillConvexPoly(mask, new CvPoint((byte)16, boardPts, 0, 8),
                    4, CvScalar.WHITE, 8, 16);

            for (int j = 0; j < (boardPts.length-8)/8; j++) {
                cvFillConvexPoly(mask, new CvPoint((byte)16, boardPts, 8 + j*8, 8),
                        4, CvScalar.BLACK, 8, 16);
            }

            cvSetZero(mask2);
            cx = 0; cy = 0;
            for (int j = 0; j < 4; j++) {
                cx += projPts[j*2    ];
                cy += projPts[j*2 + 1];
            }
            cx/=4; cy/=4;
            for (int j = 0; j < 4; j++) {
                projPts[j*2    ] -= (projPts[j*2    ] - cx)*settings.trimmingFraction;
                projPts[j*2 + 1] -= (projPts[j*2 + 1] - cy)*settings.trimmingFraction;
            }
            cvFillConvexPoly(mask2, new CvPoint((byte)16, projPts, 0, 8),
                    4, CvScalar.WHITE, 8, 16);

            cvAnd(mask, mask2, mask, null);
            cvErode(mask, mask, null, 1);

//cvSaveImage("masked" + i + ".png", cameraImages[i]);
//try {
//    Thread.sleep(1000);
//} catch (InterruptedException ex) { }

            // take the average as the camera response, and also
            // compensate for attenuation caused by the geometry
            CvScalar c = cvAvg(undistImage, mask);
            int[] o = camera.getRGBColorOrder();
            double s = cameraImage.highValue();
            cameraColors[counter] = new Color((float)(c.val(o[0])/s),
                    (float)(c.val(o[1])/s), (float)(c.val(o[2])/s));

            return true;
        }

        return false;
    }

    public double calibrate() {
        Color[] cc = getCameraColors();
        Color[] pc = getProjectorColors();
        assert (counter == pc.length);

        ColorCalibrator calibrator = new ColorCalibrator(projector);
        projector.avgColorErr = calibrator.calibrate(cc, pc);
        camera.colorMixingMatrix = CvMat.create(3, 3);
        camera.additiveLight     = CvMat.create(3, 1);
        cvSetIdentity(camera.colorMixingMatrix);
        cvSetZero    (camera.additiveLight);
        counter = 0;
        return projector.avgColorErr;
    }
}
