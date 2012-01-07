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

import java.util.Arrays;
import java.util.logging.Logger;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.ARToolKitPlus.*;

/**
 *
 * @author Samuel Audet
 */
public class MarkerDetector {
    public MarkerDetector(Settings settings) {
        setSettings(settings);
    }
    public MarkerDetector() {
        this(new Settings());
    }

    // the k's will depend strongly on the ratio between ambient light
    // (including minimum projector intensity) and the intensity level
    // used for the projector markers... this is because we use binary
    // thresholding while we actually have three levels..
    public static class Settings extends BaseChildSettings {
        int binarizationWindowMin = 5;
        int binarizationWindowMax = 63;
        double binarizationVarianceMultiplier = 1;
        double binarizationKBlackMarkers = 0.6;
        double binarizationKWhiteMarkers = 1.0;
        int subPixelWindow = 11;

        public int getBinarizationWindowMin() {
            return binarizationWindowMin;
        }
        public void setBinarizationWindowMin(int binarizationWindowMin) {
            this.binarizationWindowMin = binarizationWindowMin;
        }

        public int getBinarizationWindowMax() {
            return binarizationWindowMax;
        }
        public void setBinarizationWindowMax(int binarizationWindowMax) {
            this.binarizationWindowMax = binarizationWindowMax;
        }

        public double getBinarizationVarianceMultiplier() {
            return binarizationVarianceMultiplier;
        }
        public void setBinarizationVarianceMultiplier(double binarizationVarianceMultiplier) {
            this.binarizationVarianceMultiplier = binarizationVarianceMultiplier;
        }

        public double getBinarizationKBlackMarkers() {
            return binarizationKBlackMarkers;
        }
        public void setBinarizationKBlackMarkers(double binarizationKBlackMarkers) {
            this.binarizationKBlackMarkers = binarizationKBlackMarkers;
        }

        public double getBinarizationKWhiteMarkers() {
            return binarizationKWhiteMarkers;
        }
        public void setBinarizationKWhiteMarkers(double binarizationKWhiteMarkers) {
            this.binarizationKWhiteMarkers = binarizationKWhiteMarkers;
        }

        public int getSubPixelWindow() {
            return subPixelWindow;
        }
        public void setSubPixelWindow(int subPixelWindow) {
            this.subPixelWindow = subPixelWindow;
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
        this.subPixelSize = cvSize(settings.subPixelWindow/2, settings.subPixelWindow/2);
        this.subPixelZeroZone = cvSize(-1,-1);
        this.subPixelTermCriteria = cvTermCriteria(CV_TERMCRIT_EPS, 100, 0.001);
    }

    private MultiTracker tracker = null;
    private int width = 0, height = 0, depth = 0, channels = 0;
    private IplImage tempsrc, tempsrc2, sumimage, sqsumimage, binarized;
    private CvMat points = CvMat.create(1, 4, CV_32F, 2);
    private CvPoint2D32f corners = new CvPoint2D32f(4);
    private CvMemStorage memory = CvMemStorage.create();
    private CvSize subPixelSize = null, subPixelZeroZone = null;
    private CvTermCriteria subPixelTermCriteria = null;

    private CvPoint pts = new CvPoint(4), pt1 = new CvPoint(), pt2 = new CvPoint();
    private CvFont font = new CvFont(CV_FONT_HERSHEY_PLAIN, 1, 1);
    private CvSize text_size = new CvSize();

    private void init(IplImage image) {
        if (tracker != null && image.width() == width && image.height() == height &&
                image.depth() == depth && image.nChannels() == channels) {
            return;
        }

        width    = image.width();
        height   = image.height();
        depth    = image.depth();
        channels = image.nChannels();

        if (depth != IPL_DEPTH_8U || channels > 1) {
            tempsrc  = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        }
        if (depth != IPL_DEPTH_8U && channels > 1) {
            tempsrc2 = IplImage.create(width, height, IPL_DEPTH_8U, 3);
        }
        sumimage   = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
        sqsumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
        binarized  = IplImage.create(width,   height,   IPL_DEPTH_8U,  1);

        tracker = new MultiTracker(binarized.widthStep(), binarized.height());
//        String description = tracker.getDescription();
//        System.out.println("ARToolKitPlus compile-time information: " + description);
        tracker.setLoggerFunction(new ArtLogFunction() {
            @Override public void call(String nStr) {
                Logger.getLogger(MarkerDetector.class.getName()).warning(nStr);
            }
        });

//        if (depth != IPL_DEPTH_8U) {
//            throw new Exception("Unsupported format: IplImage must have depth == IPL_DEPTH_8U.");
//        }
        int pixfmt = PIXEL_FORMAT_LUM;
//        switch (nChannels) {
//            case 4: pixfmt = PIXEL_FORMAT_BGRA; break;
//            case 3: pixfmt = PIXEL_FORMAT_BGR;  break;
//            case 1: pixfmt = PIXEL_FORMAT_LUM;  break;
//            default:
//                throw new Exception("Unsupported format: No support for IplImage with " + channels + " channels.");
//        }
        tracker.setPixelFormat(pixfmt);
//        if(!tracker.init("data/LogitechPro4000.dat",
//                   "data/markerboard_480-499.cfg", 1.0f, 1000.0f, null)) {
//            throw new Exception("ERROR: init() failed.");
//        }
        tracker.setBorderWidth(0.125);
//        tracker.setThreshold(128);
//        tracker.activateAutoThreshold(true);
//        tracker.setNumAutoThresholdRetries(10);
        tracker.setUndistortionMode(UNDIST_NONE);
//        tracker.setPoseEstimator(POSE_ESTIMATOR_RPP);
        tracker.setMarkerMode(MARKER_ID_BCH);
        tracker.setImageProcessingMode(IMAGE_FULL_RES);
    }

    public IplImage getBinarized() { return binarized; }

    public Marker[] detect(IplImage image, boolean whiteMarkers) {
        init(image);

        if (depth != IPL_DEPTH_8U && channels > 1) {
            cvConvertScale(image, tempsrc2, 255/image.highValue(), 0);
            cvCvtColor(tempsrc2, tempsrc, channels > 3 ? CV_RGBA2GRAY : CV_BGR2GRAY);
            image = tempsrc;
        } else if (depth != IPL_DEPTH_8U) {
            cvConvertScale(image, tempsrc, 255/image.highValue(), 0);
            image = tempsrc;
        } else if (channels > 1) {
            cvCvtColor(image, tempsrc, channels > 3 ? CV_RGBA2GRAY : CV_BGR2GRAY);
            image = tempsrc;
        }
//long time1 = System.currentTimeMillis();
        JavaCV.adaptiveBinarization(image, sumimage, sqsumimage, binarized, whiteMarkers,
                settings.binarizationWindowMin, settings.binarizationWindowMax, settings.binarizationVarianceMultiplier,
                whiteMarkers ? settings.binarizationKWhiteMarkers : settings.binarizationKBlackMarkers);
//CanvasFrame.global.showImage(binarized, 0.5);
//CanvasFrame.global.waitKey();
//long time2 = System.currentTimeMillis();

        int[] n = new int[1];
        ARMarkerInfo markers = new ARMarkerInfo(null);
        tracker.arDetectMarkerLite(binarized.getByteBuffer(), 128 /* tracker.getThreshold() */, markers, n);
//long time3 = System.currentTimeMillis();
        Marker[] markers2 = new Marker[n[0]];
        int n2 = 0;
        for (int i = 0; i < n[0] && !markers.isNull(); i++) {
            markers.position(i);
            int id = markers.id();
            if (id < 0) {
                // no detected ID...
                continue;
            }
            int dir = markers.dir();
            double confidence = markers.cf();
            double[] vertex = new double[8];
            markers.vertex().capacity(8).asBuffer().get(vertex);

            int w = settings.subPixelWindow/2+1;
            if (vertex[0]-w < 0 || vertex[0]+w >= width || vertex[1]-w < 0 || vertex[1]+w >= height ||
                vertex[2]-w < 0 || vertex[2]+w >= width || vertex[3]-w < 0 || vertex[3]+w >= height ||
                vertex[4]-w < 0 || vertex[4]+w >= width || vertex[5]-w < 0 || vertex[5]+w >= height ||
                vertex[6]-w < 0 || vertex[6]+w >= width || vertex[7]-w < 0 || vertex[7]+w >= height) {
                // too tight for cvFindCornerSubPix...
                    continue;
            }

            points.put(vertex);
            CvBox2D box = cvMinAreaRect2(points, memory);
            float bw = box.size().width();
            float bh = box.size().height();
            cvClearMemStorage(memory);
            if (bw <= 0 || bh <= 0 || bw/bh < 0.1 || bw/bh > 10) {
                // marker is too "flat" to have been IDed correctly...
                continue;
            }

            for (int j = 0; j < 4; j++) {
                corners.position(j).put(vertex[2*j], vertex[2*j+1]);
            }

if (false) {
            // move the search window a bit (max 1/4 of the window) toward the center...
            // this allows us to cram more markers closer to one another
            double cx = 0, cy = 0;
            for (int j = 0; j < 4; j++) {
                corners.position(j);
                cx += corners.x();
                cy += corners.y();
            }
            cx /= 4;
            cy /= 4;
            for (int j = 0; j < 4; j++) {
                corners.position(j);
                float x = corners.x();
                float y = corners.y();
                double dx = cx - x;
                double dy = cy - y;
                corners.x(x + (float)Math.signum(dx)*(settings.subPixelWindow/4));
                corners.y(y + (float)Math.signum(dy)*(settings.subPixelWindow/4));
            }
}
            cvFindCornerSubPix(image, corners.position(0), 4, subPixelSize, subPixelZeroZone, subPixelTermCriteria);
            vertex[0] = corners.position((4-dir)%4).x(); vertex[1] = corners.position((4-dir)%4).y();
            vertex[2] = corners.position((5-dir)%4).x(); vertex[3] = corners.position((5-dir)%4).y();
            vertex[4] = corners.position((6-dir)%4).x(); vertex[5] = corners.position((6-dir)%4).y();
            vertex[6] = corners.position((7-dir)%4).x(); vertex[7] = corners.position((7-dir)%4).y();

            markers2[n2++] = new Marker(id, vertex, confidence);
        }
//long time4 = System.currentTimeMillis();
//System.out.println("binarizeTime = " + (time2-time1) + "  detectTime = " + (time3-time2) + "  subPixTime = " + (time4-time3));

        //cvCvtColor(binarized, image, CV_GRAY2BGR);
        //cvCopy(binarized, image, null);

        return Arrays.copyOf(markers2, n2);
    }

    public void draw(IplImage image, Marker[] markers) {
        for (Marker m : markers) {
            int cx = 0, cy = 0;
            for (int i = 0; i < 4; i++) {
                int x = (int)Math.round(m.corners[i*2  ] * (1<<16));
                int y = (int)Math.round(m.corners[i*2+1] * (1<<16));
                pts.position(i).x(x);
                pts.position(i).y(y);
                cx += x;
                cy += y;

// draw little colored squares in corners to confirm that the corners
// are returned in the right order...
//                CvPoint pt2a = cvPoint(pts[i].x+200000, pts[i].y+200000);
//                cvRectangle(image, pts, pt2a,
//                        i == 0? CV_RGB(maxIntensity, 0, 0) :
//                            i == 1? CV_RGB(0, maxIntensity, 0) :
//                                i == 2? CV_RGB(0, 0, maxIntensity) :
//                                    CV_RGB(maxIntensity, maxIntensity, maxIntensity),
//                        CV_FILLED, CV_AA, 16);
            }
            cx /= 4;
            cy /= 4;

            cvPolyLine(image, pts, new int[] { 4 }, 1, 1, CV_RGB(0, 0, image.highValue()), 1, CV_AA, 16);

            String text = Integer.toString(m.id);
            int[] baseline = new int[1];
            cvGetTextSize(text, font, text_size, baseline);

            pt1.x(cx - (text_size.width() *3/2 << 16)/2);
            pt1.y(cy + (text_size.height()*3/2 << 16)/2);
            pt2.x(cx + (text_size.width() *3/2 << 16)/2);
            pt2.y(cy - (text_size.height()*3/2 << 16)/2);
            cvRectangle(image, pt1, pt2, CV_RGB(0, image.highValue(), 0), CV_FILLED, CV_AA, 16);

            pt1.x((int)Math.round((double)cx/(1<<16) - text_size.width()/2));
            pt1.y((int)Math.round((double)cy/(1<<16) + text_size.height()/2 + 1));
            cvPutText(image, text, pt1, font, CvScalar.BLACK);
        }
    }
}
