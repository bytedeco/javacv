/*
 * Copyright (C) 2011,2012 Samuel Audet
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

import com.googlecode.javacpp.Loader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class HandMouse {
    public HandMouse() {
        this(new Settings());
    }
    public HandMouse(Settings settings) {
        setSettings(settings);
    }

    public static class Settings extends BaseChildSettings {
        public Settings() { }
        public Settings(Settings s) {
            s.mopIterations   = mopIterations;
            s.clickSteadySize = clickSteadySize;
            s.clickSteadyTime = clickSteadyTime;
            s.edgeAreaMin     = edgeAreaMin;
            s.edgeAreaMax     = edgeAreaMax;
            s.thresholdHigh   = thresholdHigh;
            s.thresholdLow    = thresholdLow;
            s.brightnessMin   = brightnessMin;
            s.updateAlpha     = updateAlpha;
        }

        int    mopIterations   = 1;
        double clickSteadySize = 0.05;
        long   clickSteadyTime = 250;
        double edgeAreaMin     = 0.001;
        double edgeAreaMax     = 0.1;
        double thresholdHigh   = 0.5;
        double thresholdLow    = 0.25;
        double brightnessMin   = 0.1;
        double updateAlpha     = 0.5;

        public int getMopIterations() {
            return mopIterations;
        }
        public void setMopIterations(int mopIterations) {
            this.mopIterations = mopIterations;
        }

        public double getClickSteadySize() {
            return clickSteadySize;
        }
        public void setClickSteadySize(double clickSteadySize) {
            this.clickSteadySize = clickSteadySize;
        }

        public long getClickSteadyTime() {
            return clickSteadyTime;
        }
        public void setClickSteadyTime(long clickSteadyTime) {
            this.clickSteadyTime = clickSteadyTime;
        }

        public double getEdgeAreaMin() {
            return edgeAreaMin;
        }
        public void setEdgeAreaMin(double edgeAreaMin) {
            this.edgeAreaMin = edgeAreaMin;
        }

        public double getEdgeAreaMax() {
            return edgeAreaMax;
        }
        public void setEdgeAreaMax(double edgeAreaMax) {
            this.edgeAreaMax = edgeAreaMax;
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

        public double getBrightnessMin() {
            return brightnessMin;
        }
        public void setBrightnessMin(double brightnessMin) {
            this.brightnessMin = brightnessMin;
        }

        public double getUpdateAlpha() {
            return updateAlpha;
        }
        public void setUpdateAlpha(double updateAlpha) {
            this.updateAlpha = updateAlpha;
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    private IplImage relativeResidual = null, binaryImage = null;
    private CvRect roi = null;
    private CvMemStorage storage = CvMemStorage.create();
    private int contourPointsSize = 0;
    private CvPoint contourPoints = null;
    private IntBuffer contourPointsBuffer = null;
    private CvMoments moments = new CvMoments();
    private double edgeX = 0, edgeY = 0, centerX = 0, centerY = 0;
    private double imageTipX = -1, tipX = -1, prevTipX = -1;
    private double imageTipY = -1, tipY = -1, prevTipY = -1;
    private long tipTime = 0, prevTipTime = 0;
    private CvPoint pt1 = new CvPoint(), pt2 = new CvPoint();
    private boolean imageUpdateNeeded = false;

    public void reset() {
        tipX = tipY = prevTipX = prevTipY = -1;
    }

    public void update(IplImage[] images, int pyramidLevel, CvRect roi, double[] roiPts) {
        this.roi = roi;
//        double RMSE = aligner.getRMSE()*((GNImageAligner)aligner).prevOutlierRatio;
//        double threshold = RMSE*settings.threshold;
//        double threshold2 = RMSE*settings.threshold2;//threshold*threshold;

        IplImage target      = images[1];
        IplImage transformed = images[2];
        IplImage residual    = images[3];
        IplImage mask        = images[4];
        int width    = roi.width();
        int height   = roi.height();
        int channels = residual.nChannels();
        relativeResidual = IplImage.createIfNotCompatible(relativeResidual, mask);
        binaryImage      = IplImage.createIfNotCompatible(binaryImage,      mask);
        cvResetImageROI(relativeResidual);
        cvResetImageROI(binaryImage);

        double brightnessMin = (channels > 3 ? 3 : channels)*settings.brightnessMin;
        double contourEdgeAreaMax = (width+height)/2*width*height*settings.edgeAreaMax;
        double contourEdgeAreaMin = (width+height)/2*width*height*settings.edgeAreaMin;
        ByteBuffer maskBuf = mask.getByteBuffer();
        FloatBuffer residualBuf = residual.getFloatBuffer();
        FloatBuffer targetBuf = target.getFloatBuffer();
        FloatBuffer transformedBuf = transformed.getFloatBuffer();
        ByteBuffer relResBuf = relativeResidual.getByteBuffer();
        while (maskBuf.hasRemaining() && residualBuf.hasRemaining() &&
                targetBuf.hasRemaining() && transformedBuf.hasRemaining() &&
                relResBuf.hasRemaining()) {
            byte m = maskBuf.get();
            if (m == 0) {
                residualBuf.position(residualBuf.position() + channels);
                targetBuf.position(targetBuf.position() + channels);
                transformedBuf.position(transformedBuf.position() + channels);
                relResBuf.put((byte)0);
            } else {
                double relativeNorm = 0;
                double brightness = 0;
                for (int z = 0; z < channels; z++) {
                    float r = Math.abs(residualBuf.get());
                    float c = targetBuf.get();
                    float t = transformedBuf.get();
                    if (z < 3) {
                        float maxct = Math.max(c,t);
                        brightness += maxct;
                        relativeNorm = Math.max(r/maxct, relativeNorm);
                    } // ignore alpha channel
                }
                if (brightness < brightnessMin) {
                    relResBuf.put((byte)0);
                } else {
                    relResBuf.put((byte)Math.round(255 / settings.thresholdHigh *
                            Math.min(relativeNorm, settings.thresholdHigh)));
                }
            }
        }

        JavaCV.hysteresisThreshold(relativeResidual, binaryImage,
                255, 255*settings.thresholdLow/settings.thresholdHigh, 255);

        int roiX = roi.x(), roiY = roi.y();
        cvSetImageROI(binaryImage, roi);

        if (settings.mopIterations > 0) {
            cvMorphologyEx(binaryImage, binaryImage, null, null, CV_MOP_OPEN,  settings.mopIterations);
            cvMorphologyEx(binaryImage, binaryImage, null, null, CV_MOP_CLOSE, settings.mopIterations);
        }
        CvSeq contour = new CvContour(null);
        cvFindContours(binaryImage, storage, contour, Loader.sizeof(CvContour.class),
                CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
        double largestContourEdgeArea = 0;
        CvSeq largestContour = null;
        while (contour != null && !contour.isNull()) {
            contourPointsSize = contour.total();
            if (contourPoints == null || contourPoints.capacity() < contourPointsSize) {
                contourPoints = new CvPoint(contourPointsSize);
                contourPointsBuffer = contourPoints.asByteBuffer().asIntBuffer();
            }
            cvCvtSeqToArray(contour, contourPoints.position(0));

            double[] edgePts = new double[roiPts.length];
            for (int i = 0; i < roiPts.length/2; i++) {
                edgePts[2*i    ] = roiPts[2*i    ]/(1<<pyramidLevel) - roiX;
                edgePts[2*i + 1] = roiPts[2*i + 1]/(1<<pyramidLevel) - roiY;
            }

            double m00 = 0, m10 = 0, m01 = 0;
            for (int i = 0; i < contourPointsSize; i++) {
                int x = contourPointsBuffer.get(2*i    ),
                    y = contourPointsBuffer.get(2*i + 1);
                for (int j = 0; j < roiPts.length/2; j++) {
                    double x1 = edgePts[ 2*j                     ],
                           y1 = edgePts[ 2*j + 1                 ],
                           x2 = edgePts[(2*j + 2) % edgePts.length],
                           y2 = edgePts[(2*j + 3) % edgePts.length];
                    double dx = x2 - x1;
                    double dy = y2 - y1;
                    double d2 = dx*dx + dy*dy;
                    double u = ((x - x1)*dx + (y - y1)*dy) / d2;

                    double px = x1 + u*dx;
                    double py = y1 + u*dy;

                    dx = px - x;
                    dy = py - y;
                    d2 = dx*dx + dy*dy;
                    if (d2 < 2) {
                        m00 += 1;
                        m10 += x;
                        m01 += y;
                        break;
                    }
                }
            }
            double contourEdgeArea = m00*Math.abs(cvContourArea(contour, CV_WHOLE_SEQ, 0));
            if (contourEdgeArea > contourEdgeAreaMin && contourEdgeArea < contourEdgeAreaMax &&
                    contourEdgeArea > largestContourEdgeArea) {
                largestContourEdgeArea = contourEdgeArea;
                largestContour = contour;

                double inv_m00 = 1 / m00;
                edgeX = m10 * inv_m00;
                edgeY = m01 * inv_m00;
            }
            contour = contour.h_next();
        }

        if (isClick()) {
            prevTipX = -1;
            prevTipY = -1;
            prevTipTime = 0;
        } else if (!isSteady()) {
            prevTipX = tipX;
            prevTipY = tipY;
            prevTipTime = System.currentTimeMillis();
        }

        if (largestContour == null) {
            tipX = -1;
            tipY = -1;
            tipTime = 0;
            imageUpdateNeeded = false;
        } else {
            cvMoments(largestContour, moments, 0);
            double inv_m00 = 1 / moments.m00();
            centerX = moments.m10() * inv_m00;
            centerY = moments.m01() * inv_m00;

            contourPointsSize = largestContour.total();
            cvCvtSeqToArray(largestContour, contourPoints.position(0));

            double tipDist2 = 0;
            int tipIndex = 0;
            for (int i = 0; i < contourPointsSize; i++) {
                int x = contourPointsBuffer.get(2*i    ),
                    y = contourPointsBuffer.get(2*i + 1);
                double dx = centerX - edgeX;
                double dy = centerY - edgeY;
                double d2 = dx*dx + dy*dy;
                double u = ((x - edgeX)*dx + (y - edgeY)*dy) / d2;

                double px = edgeX + u*dx;
                double py = edgeY + u*dy;

                dx = px - edgeX;
                dy = py - edgeY;
                d2 = dx*dx + dy*dy;
                if (d2 > tipDist2) {
                    tipIndex = i;
                    tipDist2 = d2;
                }
            }
            double a = imageTipX < 0 || imageTipY < 0 ? 1.0 : settings.updateAlpha;
            imageTipX = a*contourPointsBuffer.get(2*tipIndex    ) + (1-a)*imageTipX;
            imageTipY = a*contourPointsBuffer.get(2*tipIndex + 1) + (1-a)*imageTipY;
            tipX = (imageTipX+roiX)*(1<<pyramidLevel);
            tipY = (imageTipY+roiY)*(1<<pyramidLevel);
            tipTime = System.currentTimeMillis();
            imageUpdateNeeded = true;
        }

        cvClearMemStorage(storage);
    }

    public IplImage getRelativeResidual() {
        return relativeResidual;
    }
    public IplImage getResultImage() {
        if (imageUpdateNeeded) {
            cvSetZero(binaryImage);
            cvFillPoly(binaryImage, contourPoints, new int[] { contourPointsSize }, 1, CvScalar.WHITE, 8, 0);

            pt1.put((byte)16, edgeX, edgeY);
            cvCircle(binaryImage, pt1, 5<<16, CvScalar.GRAY, 2, 8, 16);

            pt1.put((byte)16, centerX-5, centerY-5); pt2.put((byte)16, centerX+5, centerY+5);
            cvRectangle(binaryImage, pt1, pt2, CvScalar.GRAY, 2, 8, 16);

            pt1.put((byte)16, imageTipX-5, imageTipY-5); pt2.put((byte)16, imageTipX+5, imageTipY+5);
            cvLine(binaryImage, pt1, pt2, CvScalar.GRAY, 2, 8, 16);
            pt1.put((byte)16, imageTipX-5, imageTipY+5); pt2.put((byte)16, imageTipX+5, imageTipY-5);
            cvLine(binaryImage, pt1, pt2, CvScalar.GRAY, 2, 8, 16);

            cvResetImageROI(binaryImage);
            imageUpdateNeeded = false;
        }
        return binaryImage;
    }

    public double getX() {
        return tipX;
    }
    public double getY() {
        return tipY;
    }

    public boolean isSteady() {
        if (tipX >= 0 && tipY >= 0 && prevTipX >= 0 && prevTipY >= 0) {
            double dx = tipX - prevTipX;
            double dy = tipY - prevTipY;
            int imageSize = (roi.width() + roi.height())/2;
            double steadySize = settings.clickSteadySize*imageSize;
            return dx*dx + dy*dy < steadySize*steadySize;
        }
        return false;
    }
    public boolean isClick() {
        return isSteady() && tipTime - prevTipTime > settings.clickSteadyTime;
    }
}
