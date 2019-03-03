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

import java.util.Arrays;
import org.bytedeco.javacpp.IntPointer;

import org.bytedeco.artoolkitplus.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.artoolkitplus.global.ARToolKitPlus.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

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
        int thresholdWindowMin = 5;
        int thresholdWindowMax = 63;
        double thresholdVarMultiplier = 1.0;
        double thresholdKBlackMarkers = 0.6;
        double thresholdKWhiteMarkers = 1.0;
        int subPixelWindow = 11;

        public int getThresholdWindowMin() {
            return thresholdWindowMin;
        }
        public void setThresholdWindowMin(int thresholdWindowMin) {
            this.thresholdWindowMin = thresholdWindowMin;
        }

        public int getThresholdWindowMax() {
            return thresholdWindowMax;
        }
        public void setThresholdWindowMax(int thresholdWindowMax) {
            this.thresholdWindowMax = thresholdWindowMax;
        }

        public double getThresholdVarMultiplier() {
            return thresholdVarMultiplier;
        }
        public void setThresholdVarMultiplier(double thresholdVarMultiplier) {
            this.thresholdVarMultiplier = thresholdVarMultiplier;
        }

        public double getThresholdKBlackMarkers() {
            return thresholdKBlackMarkers;
        }
        public void setThresholdKBlackMarkers(double thresholdKBlackMarkers) {
            this.thresholdKBlackMarkers = thresholdKBlackMarkers;
        }

        public double getThresholdKWhiteMarkers() {
            return thresholdKWhiteMarkers;
        }
        public void setThresholdKWhiteMarkers(double thresholdKWhiteMarkers) {
            this.thresholdKWhiteMarkers = thresholdKWhiteMarkers;
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
    private IntPointer markerNum = new IntPointer(1);
    private int width = 0, height = 0, depth = 0, channels = 0;
    private IplImage tempImage, tempImage2, sumImage, sqSumImage, thresholdedImage;
    private CvMat points = CvMat.create(1, 4, CV_32F, 2);
    private CvPoint2D32f corners = new CvPoint2D32f(4);
    private CvMemStorage memory = CvMemStorage.create();
    private CvSize subPixelSize = null, subPixelZeroZone = null;
    private CvTermCriteria subPixelTermCriteria = null;

    private CvFont font = cvFont(1, 1);
    private CvSize textSize = new CvSize();

    public IplImage getThresholdedImage() {
        return thresholdedImage;
    }

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
            tempImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        }
        if (depth != IPL_DEPTH_8U && channels > 1) {
            tempImage2   = IplImage.create(width, height, IPL_DEPTH_8U, 3);
        }
        sumImage         = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
        sqSumImage       = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
        thresholdedImage = IplImage.create(width,   height,   IPL_DEPTH_8U,  1);

        tracker = new MultiTracker(thresholdedImage.widthStep(), thresholdedImage.height());

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
        tracker.setBorderWidth(0.125f);
//        tracker.setThreshold(128);
//        tracker.activateAutoThreshold(true);
//        tracker.setNumAutoThresholdRetries(10);
        tracker.setUndistortionMode(UNDIST_NONE);
//        tracker.setPoseEstimator(POSE_ESTIMATOR_RPP);
        tracker.setMarkerMode(MARKER_ID_BCH);
        tracker.setImageProcessingMode(IMAGE_FULL_RES);
    }

    public Marker[] detect(IplImage image, boolean whiteMarkers) {
        init(image);

        if (depth != IPL_DEPTH_8U && channels > 1) {
            cvConvertScale(image, tempImage2, 255/image.highValue(), 0);
            cvCvtColor(tempImage2, tempImage, channels > 3 ? CV_RGBA2GRAY : CV_BGR2GRAY);
            image = tempImage;
        } else if (depth != IPL_DEPTH_8U) {
            cvConvertScale(image, tempImage, 255/image.highValue(), 0);
            image = tempImage;
        } else if (channels > 1) {
            cvCvtColor(image, tempImage, channels > 3 ? CV_RGBA2GRAY : CV_BGR2GRAY);
            image = tempImage;
        }
//long time1 = System.currentTimeMillis();
        JavaCV.adaptiveThreshold(image, sumImage, sqSumImage, thresholdedImage, whiteMarkers,
                settings.thresholdWindowMax, settings.thresholdWindowMin, settings.thresholdVarMultiplier,
                whiteMarkers ? settings.thresholdKWhiteMarkers : settings.thresholdKBlackMarkers);
//CanvasFrame.global.showImage(thresholded, 0.5);
//CanvasFrame.global.waitKey();
//long time2 = System.currentTimeMillis();

        int n = 0;
        ARMarkerInfo markers = new ARMarkerInfo(null);
        tracker.arDetectMarkerLite(thresholdedImage.imageData(), 128 /*tracker.getThreshold()*/, markers, markerNum);
//long time3 = System.currentTimeMillis();
        Marker[] markers2 = new Marker[markerNum.get(0)];
        for (int i = 0; i < markers2.length && !markers.isNull(); i++) {
            markers.position(i);
            int id = markers.id();
            if (id < 0) {
                // no detected ID...
                continue;
            }
            int dir = markers.dir();
            float confidence = markers.cf();
            float[] vertex = new float[8];
            markers.vertex().get(vertex);

            int w = settings.subPixelWindow/2+1;
            if (vertex[0]-w < 0 || vertex[0]+w >= width || vertex[1]-w < 0 || vertex[1]+w >= height ||
                vertex[2]-w < 0 || vertex[2]+w >= width || vertex[3]-w < 0 || vertex[3]+w >= height ||
                vertex[4]-w < 0 || vertex[4]+w >= width || vertex[5]-w < 0 || vertex[5]+w >= height ||
                vertex[6]-w < 0 || vertex[6]+w >= width || vertex[7]-w < 0 || vertex[7]+w >= height) {
                // too tight for cvFindCornerSubPix...
                    continue;
            }

            points.getFloatBuffer().put(vertex);
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
            double[] d = { corners.position((4-dir)%4).x(), corners.position((4-dir)%4).y(),
                           corners.position((5-dir)%4).x(), corners.position((5-dir)%4).y(),
                           corners.position((6-dir)%4).x(), corners.position((6-dir)%4).y(),
                           corners.position((7-dir)%4).x(), corners.position((7-dir)%4).y() };

            markers2[n++] = new Marker(id, d, confidence);
        }
//long time4 = System.currentTimeMillis();
//System.out.println("thresholdTime = " + (time2-time1) + "  detectTime = " + (time3-time2) + "  subPixTime = " + (time4-time3));

        //cvCvtColor(thresholdedImage, image, CV_GRAY2BGR);
        //cvCopy(thresholdedImage, image, null);

        return Arrays.copyOf(markers2, n);
    }

    public void draw(IplImage image, Marker[] markers) {
        for (Marker m : markers) {
            int cx = 0, cy = 0;
            int[] pts = new int[8];
            for (int i = 0; i < 4; i++) {
                int x = (int)Math.round(m.corners[i*2  ] * (1<<16));
                int y = (int)Math.round(m.corners[i*2+1] * (1<<16));
                pts[2*i    ] = x;
                pts[2*i + 1] = y;
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

            cvPolyLine(image, pts, new int[] { pts.length/2 }, 1, 1, CV_RGB(0, 0, image.highValue()), 1, CV_AA, 16);

            String text = Integer.toString(m.id);
            int[] baseline = new int[1];
            cvGetTextSize(text, font, textSize, baseline);

            int[] pt1 = { cx - (textSize.width() *3/2 << 16)/2,
                          cy + (textSize.height()*3/2 << 16)/2 };
            int[] pt2 = { cx + (textSize.width() *3/2 << 16)/2,
                          cy - (textSize.height()*3/2 << 16)/2 };
            cvRectangle(image, pt1, pt2, CV_RGB(0, image.highValue(), 0), CV_FILLED, CV_AA, 16);

            int[] pt = { (int)Math.round((double)cx/(1<<16) - textSize.width()/2),
                         (int)Math.round((double)cy/(1<<16) + textSize.height()/2) + 1 };
            cvPutText(image, text, pt, font, CvScalar.BLACK);
        }
    }
}
