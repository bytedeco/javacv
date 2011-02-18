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
 *
 *
 * Adapted from find_obj.cpp in the source package of OpenCV 1.1pre1:
 *
 * A Demo to OpenCV Implementation of SURF
 * Further Information Refer to "SURF: Speed-Up Robust Feature"
 * Author: Liu Liu
 * liuliu.1987+opencv@gmail.com
 */

package com.googlecode.javacv;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_features2d.*;

/**
 *
 * @author Samuel Audet
 */
public class ObjectFinder {
    public ObjectFinder(IplImage objectImage, CvSURFParams parameters,
            double distanceThreshold, int matchesMin) throws Exception {
        settings = new Settings();
        settings.objectImage = objectImage;
        settings.parameters = parameters;
        settings.distanceThreshold = distanceThreshold;
        settings.matchesMin = matchesMin;
        setSettings(settings);
    }
    public ObjectFinder(IplImage objectImage) throws Exception {
        settings = new Settings();
        settings.objectImage = objectImage;
        setSettings(settings);
    }
    public ObjectFinder(Settings settings) throws Exception {
        setSettings(settings);

    }

    public static class Settings extends BaseSettings {
        IplImage objectImage = null;
        CvSURFParams parameters = cvSURFParams(500, 1);
        double distanceThreshold = 0.6;
        int matchesMin = 4;
        double ransacReprojThreshold = 1.0;

        public IplImage getObjectImage() {
            return objectImage;
        }
        public void setObjectImage(IplImage objectImage) {
            this.objectImage = objectImage;
        }

        public boolean isExtended() {
            return parameters.extended() != 0;
        }
        public void setExtended(boolean extended) {
            parameters.extended(extended ? 1 : 0);
        }

        public double getHessianThreshold() {
            return parameters.hessianThreshold();
        }
        public void setHessianThreshold(double hessianThreshold) {
            parameters.hessianThreshold(hessianThreshold);
        }

        public int getnOctaves() {
            return parameters.nOctaves();
        }
        public void setnOctaves(int nOctaves) {
            parameters.nOctaves(nOctaves);
        }

        public int getnOctaveLayers() {
            return parameters.nOctaveLayers();
        }
        public void setnOctaveLayers(int nOctaveLayers) {
            parameters.nOctaveLayers(nOctaveLayers);
        }

        public double getDistanceThreshold() {
            return distanceThreshold;
        }
        public void setDistanceThreshold(double distanceThreshold) {
            this.distanceThreshold = distanceThreshold;
        }

        public int getMatchesMin() {
            return matchesMin;
        }
        public void setMatchesMin(int matchesMin) {
            this.matchesMin = matchesMin;
        }

        public double getRansacReprojThreshold() {
            return ransacReprojThreshold;
        }
        public void setRansacReprojThreshold(double ransacReprojThreshold) {
            this.ransacReprojThreshold = ransacReprojThreshold;
        }
    }

    private Settings settings;
    public Settings getSettings() {
        return settings;
    }
    public void setSettings(Settings settings) throws Exception {
        this.settings = settings;

        CvSeq keypoints = new CvSeq(null), descriptors = new CvSeq(null);
        cvClearMemStorage(storage);
        cvExtractSURF(settings.objectImage, null, keypoints, descriptors, storage, settings.parameters, 0);

        int total = descriptors.total();
        int elem_size = descriptors.elem_size();
        objectKeypoints = new CvSURFPoint[total];
        objectDescriptors = new FloatBuffer[total];
        for (int i = 0; i < total; i++ ) {
            objectKeypoints[i] = new CvSURFPoint(cvGetSeqElem(keypoints, i));
            objectDescriptors[i] = cvGetSeqElem(descriptors, i).asByteBuffer(elem_size).asFloatBuffer();
        }
        logger.info(total + " object descriptors");
    }

    private static final Logger logger = Logger.getLogger(ObjectFinder.class.getName());

    private CvMemStorage storage     = CvMemStorage.create();
    private CvMemStorage tempStorage = CvMemStorage.create();
    private CvSURFPoint[] objectKeypoints   = null;
    private FloatBuffer[] objectDescriptors = null;

    public double[] find(IplImage image) {
        CvSeq keypoints = new CvSeq(null), descriptors = new CvSeq(null);
        cvExtractSURF(image, null, keypoints, descriptors, tempStorage, settings.parameters, 0);

        int total = descriptors.total();
        int elem_size = descriptors.elem_size();
        CvSURFPoint[] imageKeypoints = new CvSURFPoint[total];
        FloatBuffer[] imageDescriptors = new FloatBuffer[total];
        for (int i = 0; i < total; i++ ) {
            imageKeypoints[i] = new CvSURFPoint(cvGetSeqElem(keypoints, i));
            imageDescriptors[i] = cvGetSeqElem(descriptors, i).asByteBuffer(elem_size).asFloatBuffer();
        }
        logger.info(total + " image descriptors");

        int w = settings.objectImage.width();
        int h = settings.objectImage.height();
        double[] srcCorners = {0, 0,  w, 0,  w, h,  0, h};
        double[] dstCorners = locatePlanarObject(objectKeypoints, objectDescriptors,
                imageKeypoints, imageDescriptors, srcCorners);
        cvClearMemStorage(tempStorage);
        return dstCorners;
    }

    private double compareSURFDescriptors(FloatBuffer d1, FloatBuffer d2, double best) {
        double totalCost = 0;
        assert (d1.capacity() == d2.capacity() && d1.capacity() % 4 == 0);
        for (int i = 0; i < d1.capacity(); i += 4 ) {
            double t0 = d1.get(i  ) - d2.get(i  );
            double t1 = d1.get(i+1) - d2.get(i+1);
            double t2 = d1.get(i+2) - d2.get(i+2);
            double t3 = d1.get(i+3) - d2.get(i+3);
            totalCost += t0*t0 + t1*t1 + t2*t2 + t3*t3;
            if (totalCost > best)
                break;
        }
        return totalCost;
    }

    private int naiveNearestNeighbor(FloatBuffer vec, int laplacian,
            CvSURFPoint[] modelKeypoints, FloatBuffer[] modelDescriptors) {
        int neighbor = -1;
        double d, dist1 = 1e6, dist2 = 1e6;

        for (int i = 0; i < modelDescriptors.length; i++) {
            CvSURFPoint kp = modelKeypoints[i];
            FloatBuffer mvec = modelDescriptors[i];
            if (laplacian != kp.laplacian())
                continue;
            d = compareSURFDescriptors(vec, mvec, dist2);
            if (d < dist1) {
                dist2 = dist1;
                dist1 = d;
                neighbor = i;
            } else if (d < dist2) {
                dist2 = d;
            }
        }
        if (dist1 < settings.distanceThreshold*dist2)
            return neighbor;
        return -1;
    }

    private ArrayList<Integer> findPairs(CvSURFPoint[] objectKeypoints, FloatBuffer[] objectDescriptors,
               CvSURFPoint[] imageKeypoints, FloatBuffer[] imageDescriptors) {
        ArrayList<Integer> ptpairs = new ArrayList<Integer>(2*objectDescriptors.length);
        for (int i = 0; i < objectDescriptors.length; i++ ) {
            CvSURFPoint kp = objectKeypoints[i];
            FloatBuffer descriptor = objectDescriptors[i];
            int nearestNeighbor = naiveNearestNeighbor(descriptor, kp.laplacian(), imageKeypoints, imageDescriptors);
            if (nearestNeighbor >= 0) {
                ptpairs.add(i);
                ptpairs.add(nearestNeighbor);
            }
        }
        return ptpairs;
    }

    /* a rough implementation for object location */
    private double[] locatePlanarObject(CvSURFPoint[] objectKeypoints, FloatBuffer[] objectDescriptors,
            CvSURFPoint[] imageKeypoints, FloatBuffer[] imageDescriptors, double[] srcCorners) {
        ArrayList<Integer> ptpairs = findPairs(objectKeypoints, objectDescriptors, imageKeypoints, imageDescriptors);
        int n = ptpairs.size()/2;
        logger.info(n + " matching pairs found");
        if (n < settings.matchesMin) {
            return null;
        }

        CvMat pt1  = CvMat.take(1, objectDescriptors.length, CV_32F, 2);
        CvMat pt2  = CvMat.take(1, objectDescriptors.length, CV_32F, 2);
        CvMat mask = CvMat.take(1, objectDescriptors.length, CV_8U,  1);
        pt1.cols (n);
        pt2.cols (n);
        mask.cols(n);
        for (int i = 0; i < n; i++) {
            CvPoint2D32f p1 = objectKeypoints[ptpairs.get(i*2)].pt();
            pt1.put(i*2, p1.x()); pt1.put(i*2+1, p1.y());
            CvPoint2D32f p2 = imageKeypoints[ptpairs.get(i*2+1)].pt();
            pt2.put(i*2, p2.x()); pt2.put(i*2+1, p2.y());
        }

        CvMat H = CvMat.take(3, 3, CV_64F, 1);
        if (cvFindHomography(pt1, pt2, H, CV_RANSAC, settings.ransacReprojThreshold, mask) == 0) {
            return null;
        }
        if (cvCountNonZero(mask) < settings.matchesMin) {
            return null;
        }

        double[] h = H.get();
        double[] dstCorners = new double[8];
        for(int i = 0; i < 4; i++) {
            double x = srcCorners[2*i], y = srcCorners[2*i + 1];
            double Z = 1./(h[6]*x + h[7]*y + h[8]);
            double X = (h[0]*x + h[1]*y + h[2])*Z;
            double Y = (h[3]*x + h[4]*y + h[5])*Z;
            dstCorners[2*i    ] = X;
            dstCorners[2*i + 1] = Y;
        }

        pt1.cols (objectDescriptors.length);
        pt2.cols (objectDescriptors.length);
        mask.cols(objectDescriptors.length);
        pt1.pool();
        pt2.pool();
        mask.pool();
        H.pool();
        return dstCorners;
    }

}
