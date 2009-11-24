/*
 * Copyright (C) 2009 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.audet.samuel.javacv;

import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;
import static name.audet.samuel.javacv.jna.ARToolKitPlus.*;

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
    public static class Settings extends BaseSettings {
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
        this.subPixelSize = new CvSize(settings.subPixelWindow/2, settings.subPixelWindow/2).byValue();
        this.subPixelZeroZone = new CvSize(-1,-1).byValue();
        this.subPixelTermCriteria = new CvTermCriteria(CV_TERMCRIT_EPS, 100, 0.001).byValue();
    }

    private Tracker t = null;
    private int width = 0, height = 0, depth = 0, channels = 0;
    private IplImage tempsrc, tempsrc2, sumimage, sqsumimage, binarized;
    private CvMat points = CvMat.create(1, 4, CV_32F, 2);
    private CvMemStorage memory = CvMemStorage.create();
    private CvSize.ByValue subPixelSize = null, subPixelZeroZone = null;
    private CvTermCriteria.ByValue subPixelTermCriteria = null;
    private CvFont font = new CvFont(1, 1);

    private void init(IplImage image) {
        if (t != null && image.width == width && image.height == height && 
                image.depth == depth && image.nChannels == channels) {
            return;
        }

        width = image.width;
        height = image.height;
        depth = image.depth;
        channels = image.nChannels;

        if (image.depth != IPL_DEPTH_8U || image.nChannels > 1) {
            tempsrc = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        }
        if (image.depth != IPL_DEPTH_8U && image.nChannels > 1) {
            tempsrc2 = IplImage.create(width, height, IPL_DEPTH_8U, 3);
        }
        sumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
        sqsumimage = IplImage.create(width+1, height+1, IPL_DEPTH_64F, 1);
        binarized = IplImage.create(width, height, IPL_DEPTH_8U, 1);

        if (t != null) {
            deleteTracker(t);
            t = null;
        }

        t = newTrackerMultiMarker(binarized.widthStep, binarized.height);
//        String description = getDescription(t);
//        System.out.println("ARToolKitPlus compile-time information: " + description);
        setLoggerFunc(t, new arLogFunc() {
            public void callback(String nStr) {
                System.err.println(nStr);
            }
        });

//        if (image.depth != IPL_DEPTH_8U) {
//            throw new RuntimeException("Unsupported format: IplImage must have depth == IPL_DEPTH_8U.");
//        }
        int pixfmt = PIXEL_FORMAT_LUM;
//        switch (image.nChannels) {
//            case 4: pixfmt = PIXEL_FORMAT_BGRA; break;
//            case 3: pixfmt = PIXEL_FORMAT_BGR;  break;
//            case 1: pixfmt = PIXEL_FORMAT_LUM;  break;
//            default:
//                throw new Exception("Unsupported format: No support for IplImage with " + channels + " channels.");
//        }
        setPixelFormat(t, pixfmt);
//        if(!init(t, "data/LogitechPro4000.dat",
//                      "data/markerboard_480-499.cfg", 1.0f, 1000.0f, null)) {
//            throw new Exception("ERROR: init() failed.");
//        }
        setBorderWidth(t, 0.125);
//        setThreshold(t, 128);
//        activateAutoThreshold(t, true);
//        setNumAutoThresholdRetries(t, 10);
        setUndistortionMode(t, UNDIST_NONE);
//        setPoseEstimator(t, POSE_ESTIMATOR_RPP);
        setMarkerMode(t, MARKER_ID_BCH);
        setImageProcessingMode(t, IMAGE_FULL_RES);
    }

    @Override protected void finalize() {
        if (t != null) {
            deleteTracker(t);
            t = null;
        }
    }

    public IplImage getBinarized() { return binarized; }

    public Marker[] detect(IplImage image, boolean whiteMarkers) {
        init(image);

        if (image.depth != IPL_DEPTH_8U && image.nChannels > 1) {
            cvConvertScale(image, tempsrc2, 255/image.getMaxIntensity(), 0);
            cvCvtColor(tempsrc2, tempsrc, CV_BGR2GRAY);
            image = tempsrc;
        } else if (image.depth != IPL_DEPTH_8U) {
            cvConvertScale(image, tempsrc, 255/image.getMaxIntensity(), 0);
            image = tempsrc;
        } else if (image.nChannels > 1) {
            cvCvtColor(image, tempsrc, CV_BGR2GRAY);
            image = tempsrc;
        }
//long time1 = System.currentTimeMillis();
        JavaCV.adaptiveBinarization(image, sumimage, sqsumimage, binarized, whiteMarkers,
                settings.binarizationWindowMin, settings.binarizationWindowMax, settings.binarizationVarianceMultiplier,
                whiteMarkers ? settings.binarizationKWhiteMarkers : settings.binarizationKBlackMarkers);
//CanvasFrame.global.showImage(binarized, 0.5);
//CanvasFrame.global.waitKey();
//long time2 = System.currentTimeMillis();

        ArrayList<Marker> markers2 = new ArrayList<Marker>();

        IntByReference marker_num = new IntByReference();
        ARMarkerInfo.PointerByReference markerInfo = new ARMarkerInfo.PointerByReference();
        arDetectMarkerLite(t, binarized.getByteBuffer(),
                128 /* getThreshold(t) */, markerInfo, marker_num);
//long time3 = System.currentTimeMillis();
        int n = marker_num.getValue();
        if (n > 0) {
            ARMarkerInfo info = markerInfo.getStructure();
            ARMarkerInfo[] markers = ((ARMarkerInfo[])info.toArray(n));

            for (ARMarkerInfo m : markers) {
                if (m.id < 0)
                    // no detected ID...
                    continue;

                double[] v = m.vertex;
                int w = settings.subPixelWindow/2+1;
                if (v[0]-w < 0 || v[0]+w >= image.width || v[1]-w < 0 || v[1]+w >= image.height ||
                    v[2]-w < 0 || v[2]+w >= image.width || v[3]-w < 0 || v[3]+w >= image.height ||
                    v[4]-w < 0 || v[4]+w >= image.width || v[5]-w < 0 || v[5]+w >= image.height ||
                    v[6]-w < 0 || v[6]+w >= image.width || v[7]-w < 0 || v[7]+w >= image.height)
                    // too tight for cvFindCornerSubPix...
                        continue;

                points.put(m.vertex);
                CvBox2D box = cvMinAreaRect2(points, memory); memory.clearMem();
                if (box.size.width <= 0 || box.size.height <= 0 || 
                        box.size.width/box.size.height < 0.1 || 
                        box.size.width/box.size.height > 10) {
                    // marker is too "flat" to have been IDed correctly...
                    continue;
                }

                CvPoint2D32f[] corners = CvPoint2D32f.createArray(m.vertex);

if (false) {
                // move the search window a bit (max 1/4 of the window) toward the center...
                // this allows us to cram more markers closer to one another
                CvPoint2D32f center = new CvPoint2D32f(0,0);
                for (CvPoint2D32f p : corners) {
                    center.x += p.x;
                    center.y += p.y;
                }
                center.x /= corners.length;
                center.y /= corners.length;
                for (CvPoint2D32f p : corners) {
                    double dx = center.x - p.x;
                    double dy = center.y - p.y;
                    p.x += Math.signum(dx)*(settings.subPixelWindow/4);
                    p.y += Math.signum(dy)*(settings.subPixelWindow/4);
                }
}
                cvFindCornerSubPix(image, corners, 4, subPixelSize,
                        subPixelZeroZone, subPixelTermCriteria);
                m.vertex[0] = corners[(4-m.dir)%4].x; m.vertex[1] = corners[(4-m.dir)%4].y;
                m.vertex[2] = corners[(5-m.dir)%4].x; m.vertex[3] = corners[(5-m.dir)%4].y;
                m.vertex[4] = corners[(6-m.dir)%4].x; m.vertex[5] = corners[(6-m.dir)%4].y;
                m.vertex[6] = corners[(7-m.dir)%4].x; m.vertex[7] = corners[(7-m.dir)%4].y;

                markers2.add(new Marker(m.id, m.vertex, m.cf));
             }
        }
//long time4 = System.currentTimeMillis();
//System.out.println("binarizeTime = " + (time2-time1) + "  detectTime = " + (time3-time2) + "  subPixTime = " + (time4-time3));

        //cvCvtColor(binarized, image, CV_GRAY2BGR);
        //cvCopy(binarized, image, null);

        return markers2.toArray(new Marker[0]);
    }

    public void draw(IplImage image, Marker[] markers) {

        for (Marker m : markers) {
                CvPoint center = new CvPoint(0, 0);
                CvPoint[] pts = CvPoint.createArray(4);
                for (int i = 0; i < 4; i++) {
                    pts[i].x = (int)Math.round(m.corners[i*2  ] * (1<<16));
                    pts[i].y = (int)Math.round(m.corners[i*2+1] * (1<<16)); 
                    pts[i].write();
                    center.x += pts[i].x;
                    center.y += pts[i].y;

// draw little colored squares in corners to confirm that the corners
// are returned in the right order...
//                CvPoint pt2a = new CvPoint(pts[i].x+200000, pts[i].y+200000);
//                cvRectangle(image, pts[i].byValue(), pt2a.byValue(),
//                        i == 0? CvScalar.CV_RGB(maxIntensity, 0, 0) :
//                            i == 1? CvScalar.CV_RGB(0, maxIntensity, 0) :
//                                i == 2? CvScalar.CV_RGB(0, 0, maxIntensity) :
//                                    CvScalar.CV_RGB(maxIntensity, maxIntensity, maxIntensity),
//                        CV_FILLED, CV_AA, 16);
                }
                center.x /= 4;
                center.y /= 4;

                cvPolyLine(image, pts[0].pointerByReference(), new int[] { 4 }, 1, 1,
                        CV_RGB(0, 0, image.getMaxIntensity()), 1, CV_AA, 16);

                String text = ""+m.id;
                CvSize text_size = new CvSize();
                IntByReference baseline = new IntByReference();
                cvGetTextSize(text, font, text_size, baseline);

                CvPoint pt1 = new CvPoint(center.x - (text_size.width *3/2 << 16)/2,
                                          center.y + (text_size.height*3/2 << 16)/2);
                CvPoint pt2 = new CvPoint(center.x + (text_size.width *3/2 << 16)/2,
                                          center.y - (text_size.height*3/2 << 16)/2);
                cvRectangle(image, pt1.byValue(), pt2.byValue(),
                        CV_RGB(0, image.getMaxIntensity(), 0), CV_FILLED, CV_AA, 16);

                CvPoint pt = new CvPoint((int)Math.round((double)center.x/(1<<16) - text_size.width/2),
                                         (int)Math.round((double)center.y/(1<<16) + text_size.height/2 + 1));
                cvPutText(image, text, pt.byValue(), font,
                        CV_RGB(0, 0, 0));
        }
    }


}
