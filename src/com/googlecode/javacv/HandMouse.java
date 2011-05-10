/*
 * Copyright (C) 2011 Samuel Audet
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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class HandMouse {
    public HandMouse(ImageAligner aligner) {
        this(aligner, new Settings());
    }
    public HandMouse(ImageAligner aligner, Settings settings) {
        this.aligner = aligner;
        setSettings(settings);
    }

    public static class Settings extends BaseChildSettings {
        public Settings() { }
        public Settings(Settings s) {
            s.mopIterations  = mopIterations;
            s.clickSteadyMax = clickSteadyMax;
            s.areaMin        = areaMin;
            s.areaMax        = areaMax;
        }

        int mopIterations     = 1;
        double clickSteadyMax = 0.005;
        double areaMin        = 0.001;
        double areaMax        = 0.1;
        int pyramidLevel      = 2;
        double thresholdHigh  = 0.5;
        double thresholdLow   = 0.25;
        double lightnessMin   = 0.1;

        public int getMopIterations() {
            return mopIterations;
        }
        public void setMopIterations(int mopIterations) {
            this.mopIterations = mopIterations;
        }

        public double getClickSteadyMax() {
            return clickSteadyMax;
        }
        public void setClickSteadyMax(double clickSteadyMax) {
            this.clickSteadyMax = clickSteadyMax;
        }

        public double getAreaMin() {
            return areaMin;
        }
        public void setAreaMin(double areaMin) {
            this.areaMin = areaMin;
        }

        public double getAreaMax() {
            return areaMax;
        }
        public void setAreaMax(double areaMax) {
            this.areaMax = areaMax;
        }

        public int getPyramidLevel() {
            return pyramidLevel;
        }
        public void setPyramidLevel(int pyramidLevel) {
            this.pyramidLevel = pyramidLevel;
        }

        public double getThresholdHigh() {
            return thresholdHigh;
        }
        public void setThresholdHigh(double thresholdHigh) {
            this.thresholdHigh = thresholdHigh;
        }

        public double getThresholdLow() {
            return thresholdLow;
        }
        public void setThresholdLow(double thresholdLow) {
            this.thresholdLow = thresholdLow;
        }

        public double getLightnessMin() {
            return lightnessMin;
        }
        public void setLightnessMin(double lightnessMin) {
            this.lightnessMin = lightnessMin;
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    private ImageAligner aligner;
    private IplImage thresholdedImage = null;
    private CvMemStorage storage = CvMemStorage.create();
    private CvPoint roiPoints = null, contourPoints = null;
    private int roiPointsSize = 0, contourPointsSize = 0;
    private IntBuffer contourPointsBuffer = null;
    private CvMoments moments = new CvMoments();
    private double tipX = -1, tipY = -1, prevTipX = tipX, prevTipY = tipY;
    private CvPoint pt1 = new CvPoint(), pt2 = new CvPoint();

    public void update() {
        update(null);
    }
    public void update(IplImage[] intermediate) {
        if (aligner.getPyramidLevel() != settings.pyramidLevel) {
            aligner.setPyramidLevel(settings.pyramidLevel);
        }
//        double RMSE = aligner.getRMSE()*((GNImageAligner)aligner).prevOutlierRatio;
//        double threshold = RMSE*settings.threshold;
//        double threshold2 = RMSE*settings.threshold2;//threshold*threshold;

        IplImage roiMask  = aligner.getRoiMaskImage();
        IplImage residual = aligner.getResidualImage();
        IplImage target   = aligner.getTargetImage();
        IplImage transformed = aligner.getTransformedImage();
        int width    = residual.width();
        int height   = residual.height();
        int channels = residual.nChannels();
        thresholdedImage = IplImage.createIfNotCompatible(thresholdedImage, roiMask);
        cvResetImageROI(thresholdedImage);

        ByteBuffer roiMaskBuf = roiMask.getByteBuffer();
        FloatBuffer residualBuf = residual.getFloatBuffer();
        FloatBuffer targetBuf = target.getFloatBuffer();
        FloatBuffer transformedBuf = transformed.getFloatBuffer();
        ByteBuffer thresholdedBuf = thresholdedImage.getByteBuffer();
        while (roiMaskBuf.hasRemaining() && residualBuf.hasRemaining() &&
                targetBuf.hasRemaining() && transformedBuf.hasRemaining() &&
                thresholdedBuf.hasRemaining()) {
            byte m = roiMaskBuf.get();
            if (m == 0) {
                residualBuf.position(residualBuf.position() + channels);
                targetBuf.position(targetBuf.position() + channels);
                transformedBuf.position(transformedBuf.position() + channels);
                thresholdedBuf.put((byte)0);
            } else {
                double relativeNorm = 0;
                double lightness = 0;
                for (int z = 0; z < channels; z++) {
                    float r = Math.abs(residualBuf.get());
                    float c = targetBuf.get();
                    float t = transformedBuf.get();
                    lightness += Math.max(c,t);
                    relativeNorm = Math.max(r/Math.max(c,t), relativeNorm);
                }
                if (lightness < channels*settings.lightnessMin) {
                    thresholdedBuf.put((byte)0);
                } else {
                    thresholdedBuf.put((byte)Math.round(255 / settings.thresholdHigh *
                            Math.min(relativeNorm, settings.thresholdHigh)));
                }

//                double mae = 0;
//                for (int z = 0; z < channels; z++) {
//                    mae += Math.abs(res.get());
//                }
//                out.put((byte)Math.round(mae * 255.0 / channels));
//                //out.put(mae < threshold*channels ? (byte)0 : (byte)255);

//                byte val = 0;
//                for (int z = 0; z < channels; z++) {
//                    float c = cam.get();
//                    float r = Math.abs(res.get());
//                    val |= r < c*threshold ? 0 : 255;
//                }
//                out.put(val);

//                double cam2 = 0;
//                double res2 = 0;
//                for (int z = 0; z < channels; z++) {
//                    float c = cam.get();
//                    float r = res.get();
//                    cam2 += c*c;
//                    res2 += r*r;
//                }
//                out.put((byte)(res2 < cam2*outlierThreshold2 ? 0 : 255));
            }
        }

        if (intermediate != null && intermediate[0] != null) {
            cvCopy(thresholdedImage, intermediate[0]);
        }

        JavaCV.hysteresisThreshold(thresholdedImage, thresholdedImage, 
                255, 255*settings.thresholdLow/settings.thresholdHigh, 255);

        if (intermediate != null && intermediate[1] != null) {
            cvResetImageROI(intermediate[1]);
            cvCopy(thresholdedImage, intermediate[1]);
        }

        CvRect roi = aligner.getRoi();
        int roiX = roi.x(), roiY = roi.y();
        cvSetImageROI(thresholdedImage, roi);

        cvMorphologyEx(thresholdedImage, thresholdedImage, null, null, CV_MOP_OPEN,  settings.mopIterations);
        cvMorphologyEx(thresholdedImage, thresholdedImage, null, null, CV_MOP_CLOSE, settings.mopIterations);
        cvDilate(thresholdedImage, thresholdedImage, null, 1);

        double[] roiPts = aligner.getTransformedRoiPts();
        for (int i = 0; i < roiPts.length/2; i++) {
            roiPts[2*i    ] -= roiX;
            roiPts[2*i + 1] -= roiY;
        }
        if (roiPoints == null || roiPointsSize < roiPts.length/2) {
            roiPoints = new CvPoint(roiPts.length/2);
            roiPointsSize = roiPts.length/2;
        }
        roiPoints.fill((byte)16, roiPts);
        CvSeq contour = new CvContour(null);
        cvFindContours(thresholdedImage, storage, contour, sizeof(CvContour.class),
                CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        double largestContourEdgeArea = 0;
        CvSeq largestContour = null;
        double edgeX = 0, edgeY = 0;
        while (contour != null && !contour.isNull()) {
            double contourArea = Math.abs((cvContourArea(contour, CV_WHOLE_SEQ, 0)));
            if (contourArea < settings.areaMin*width*height ||
                    contourArea > settings.areaMax*width*height) {
                contour = contour.h_next();
                continue;
            }

            int total = contour.total();
            if (contourPoints == null || contourPointsSize < total) {
                contourPoints = new CvPoint(total);
                contourPointsSize = total;
                contourPointsBuffer = contourPoints.asByteBuffer(2*4*total).asIntBuffer();
            }
            cvCvtSeqToArray(contour, contourPoints.position(0), CV_WHOLE_SEQ);

            cvSetZero(thresholdedImage);
            cvFillPoly(thresholdedImage, contourPoints, new int[] { total }, 1, CvScalar.WHITE, 8, 0);
            cvFillPoly(thresholdedImage, roiPoints, new int[] { roiPointsSize }, 1, CvScalar.BLACK, 8, 16);

            double contourEdgeArea = cvCountNonZero(thresholdedImage)*contourArea;
            if (contourEdgeArea > largestContourEdgeArea) {
                largestContourEdgeArea = contourEdgeArea;
                largestContour = contour;

                cvMoments(thresholdedImage, moments, 0);
                double inv_m00 = 1 / moments.m00();
                edgeX = moments.m10() * inv_m00;
                edgeY = moments.m01() * inv_m00;
            }


//            boolean intersects = false;
//intersection:
//            for (int i = 0; i < roiPts.length/2-1; i++) {
//                for (int j = 0; j < total-1; j++) {
//                    double x1 = roiPts[2*i  ], y1 = roiPts[2*i+1],
//                           x2 = roiPts[2*i+2], y2 = roiPts[2*i+3];
//                    int x3 = point.position(j  ).x(), y3 = point.y(),
//                        x4 = point.position(j+1).x(), y4 = point.y();
//                    double d = (y4 - y3)*(x2 - x1) - (x4 - x3)*(y2 - y1);
//                    double ua = ((x4 - x3)*(y1 - y3) - (y4 - y3)*(x1 - x3))/d;
//                    double ub = ((x2 - x1)*(y1 - y3) - (y2 - y1)*(x1 - x3))/d;
//                    if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1) {
//                        intersects = true;
//                        break intersection;
//                    }
//                }
//            }
//
//            if (intersects) {
//                double contourArea = cvContourArea(contour, CV_WHOLE_SEQ, 0);
//                if (contourArea > largestContourArea) {
//                    largestContour     = contour;
//                    largestContourArea = contourArea;
//                }
//            }

            contour = contour.h_next();
        }

        if (isClick()) {
            prevTipX = -1;
            prevTipY = -1;
        } else {
            prevTipX = tipX;
            prevTipY = tipY;
        }

        if (largestContour == null) {
            tipX = -1;
            tipY = -1;
        } else {
            cvMoments(largestContour, moments, 0);
            double inv_m00 = 1 / moments.m00();
            double centerX = moments.m10() * inv_m00;
            double centerY = moments.m01() * inv_m00;

            int total = largestContour.total();
            cvCvtSeqToArray(largestContour, contourPoints.position(0), CV_WHOLE_SEQ);

            double tipDist2 = 0;
            for (int i = 0; i < total; i++) {
                int x = contourPointsBuffer.get(2*i    ),
                    y = contourPointsBuffer.get(2*i + 1);
                double dx = centerX - edgeX;
                double dy = centerY - edgeY;
                double d2 = dx*dx + dy*dy;
                double u = ((x - edgeX) * (centerX - edgeX) + (y - edgeY) * (centerY - edgeY)) / d2;

                double px = edgeX + u * (centerX - edgeX);
                double py = edgeY + u * (centerY - edgeY);

                dx = px - edgeX;
                dy = py - edgeY;
                d2 = dx*dx + dy*dy;
                if (d2 > tipDist2) {
                    tipX = x;
                    tipY = y;
                    tipDist2 = d2;
                }
            }

            if (intermediate != null && intermediate[1] != null) {
                cvSetImageROI(intermediate[1], roi);
                cvCopy(intermediate[1], thresholdedImage);
            } else {
                cvSetZero(thresholdedImage);
                cvFillPoly(thresholdedImage, contourPoints, new int[] { total }, 1, CvScalar.WHITE, 8, 0);
            }

            pt1.fill((byte)16, edgeX, edgeY);
            cvCircle(thresholdedImage, pt1, 5<<16, CvScalar.GRAY, 2, 8, 16);

            pt1.fill((byte)16, centerX-5, centerY-5); pt2.fill((byte)16, centerX+5, centerY+5);
            cvRectangle(thresholdedImage, pt1, pt2, CvScalar.GRAY, 2, 8, 16);

            pt1.fill((byte)16, tipX-5, tipY-5); pt2.fill((byte)16, tipX+5, tipY+5);
            cvLine(thresholdedImage, pt1, pt2, CvScalar.GRAY, 2, 8, 16);
            pt1.fill((byte)16, tipX-5, tipY+5); pt2.fill((byte)16, tipX+5, tipY-5);
            cvLine(thresholdedImage, pt1, pt2, CvScalar.GRAY, 2, 8, 16);

            tipX = (tipX+roiX)*(1<<settings.pyramidLevel);
            tipY = (tipY+roiY)*(1<<settings.pyramidLevel);
            cvResetImageROI(thresholdedImage);
        }

        cvClearMemStorage(storage);
    }

    public IplImage getImage() {
        return thresholdedImage;
    }
    public double getX() {
        return tipX;
    }
    public double getY() {
        return tipY;
    }
    public boolean isClick() {
        if (tipX < 0 || tipY < 0 || prevTipX < 0 || prevTipY < 0) {
            return false;
        }
        double dx = tipX - prevTipX;
        double dy = tipY - prevTipY;
        int size = (thresholdedImage.width() + thresholdedImage.height())/2;
        double max = settings.clickSteadyMax*size;
        return dx*dx + dy*dy < max*max;
    }
}
