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
 *
 *
 * Adapted from find_obj.cpp in the source package of OpenCV 1.1pre1:
 *
 * A Demo to OpenCV Implementation of SURF
 * Further Information Refer to "SURF: Speed-Up Robust Feature"
 * Author: Liu Liu
 * liuliu.1987+opencv@gmail.com
 */

package name.audet.samuel.javacv;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.v11or20.*;

/**
 *
 * @author Samuel Audet
 */
public class ObjectFinder {
    public ObjectFinder(IplImage object, CvSURFParams params, double distThreshold, int minMatches) {
        this.object = object;
        this.params = params.byValue();
        this.distThreshold = distThreshold;
        this.minMatches = minMatches;

        CvSeq.PointerByReference keypointsRef = new CvSeq.PointerByReference(),
                                 descriptorsRef = new CvSeq.PointerByReference();
        cvExtractSURF(object, null, keypointsRef, descriptorsRef, storage, this.params, 0);
        CvSeq keypoints = keypointsRef.getStructure();
        CvSeq descriptors = descriptorsRef.getStructure();

        descriptors.readField("total");
        descriptors.readField("elem_size");
        objectKeypoints = new CvSURFPoint[descriptors.total];
        objectDescriptors = new FloatBuffer[descriptors.total];
        for (int i = 0; i < descriptors.total; i++ ) {
            objectKeypoints[i] = new CvSURFPoint(cvGetSeqElem(keypoints, i));
            objectDescriptors[i] = cvGetSeqElem(descriptors, i).getByteBuffer(0, descriptors.elem_size).asFloatBuffer();
        }
        System.out.println("Object Descriptors: " +  descriptors.total);
    }
    public ObjectFinder(IplImage object, CvSURFParams params) {
        this(object, params, 0.6, 6);
    }
    public ObjectFinder(IplImage object) {
        this(object, cvSURFParams(500, 1));
    }

    private IplImage object = null;
    private CvSURFParams.ByValue params = null;
    private double distThreshold;
    private int minMatches;
    private CvMemStorage storage = CvMemStorage.create();
    private CvMemStorage tempStorage = CvMemStorage.create();
    private CvSURFPoint[] objectKeypoints = null;
    private FloatBuffer[] objectDescriptors = null;

    public CvPoint2D64f[] find(IplImage image) {
        CvSeq.PointerByReference imageKeypointsRef = new CvSeq.PointerByReference(),
                                 imageDescriptorsRef = new CvSeq.PointerByReference();
        cvExtractSURF(image, null, imageKeypointsRef, imageDescriptorsRef, tempStorage, params, 0);
        CvSeq keypoints = imageKeypointsRef.getStructure();
        CvSeq descriptors = imageDescriptorsRef.getStructure();

        descriptors.readField("total");
        descriptors.readField("elem_size");
        CvSURFPoint[] imageKeypoints = new CvSURFPoint[descriptors.total];
        FloatBuffer[] imageDescriptors = new FloatBuffer[descriptors.total];
        for (int i = 0; i < descriptors.total; i++ ) {
            imageKeypoints[i] = new CvSURFPoint(cvGetSeqElem(keypoints, i));
            imageDescriptors[i] = cvGetSeqElem(descriptors, i).getByteBuffer(0, descriptors.elem_size).asFloatBuffer();
        }
        System.out.println("Image Descriptors: " + descriptors.total);

        CvPoint2D64f[] srcCorners = CvPoint2D64f.createArray(0, 0,  object.width, 0,
                object.width, object.height,  0, object.height);
        CvPoint2D64f[] dstCorners = locatePlanarObject(objectKeypoints, objectDescriptors,
                imageKeypoints, imageDescriptors, srcCorners);
        tempStorage.clearMem();
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
            if (laplacian != kp.laplacian)
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
        if (dist1 < distThreshold*dist2)
            return neighbor;
        return -1;
    }

    private ArrayList<Integer> findPairs(CvSURFPoint[] objectKeypoints, FloatBuffer[] objectDescriptors,
               CvSURFPoint[] imageKeypoints, FloatBuffer[] imageDescriptors) {
        ArrayList<Integer> ptpairs = new ArrayList<Integer>(2*objectDescriptors.length);
        for (int i = 0; i < objectDescriptors.length; i++ ) {
            CvSURFPoint kp = objectKeypoints[i];
            FloatBuffer descriptor = objectDescriptors[i];
            int nearestNeighbor = naiveNearestNeighbor(descriptor, kp.laplacian, imageKeypoints, imageDescriptors);
            if (nearestNeighbor >= 0) {
                ptpairs.add(i);
                ptpairs.add(nearestNeighbor);
            }
        }
        return ptpairs;
    }

    /* a rough implementation for object location */
    private CvPoint2D64f[] locatePlanarObject(CvSURFPoint[] objectKeypoints, FloatBuffer[] objectDescriptors,
            CvSURFPoint[] imageKeypoints, FloatBuffer[] imageDescriptors,
            CvPoint2D64f[] srcCorners) {
        ArrayList<Integer> ptpairs = findPairs(objectKeypoints, objectDescriptors, imageKeypoints, imageDescriptors);
        int n = ptpairs.size()/2;
        System.out.println("Found " + n + " pairs");
        if (n < minMatches) {
            return null;
        }

        CvMat pt1  = CvMat.take(1, objectDescriptors.length, CV_32F, 2);
        CvMat pt2  = CvMat.take(1, objectDescriptors.length, CV_32F, 2);
        CvMat mask = CvMat.take(1, objectDescriptors.length, CV_8U,  1);
        pt1.cols  = n;
        pt2.cols  = n;
        mask.cols = n;
        for (int i = 0; i < n; i++) {
            CvPoint2D32f p1 = objectKeypoints[ptpairs.get(i*2)].pt;
            pt1.put(i*2, p1.x); pt1.put(i*2+1, p1.y);
            CvPoint2D32f p2 = imageKeypoints[ptpairs.get(i*2+1)].pt;
            pt2.put(i*2, p2.x); pt2.put(i*2+1, p2.y);
        }

        CvMat H = CvMat.take(3, 3, CV_64F, 1);
        if (cvFindHomography(pt1, pt2, H, CV_RANSAC, 1, null) == 0) {
            return null;
        }
        if (cvCountNonZero(mask) < minMatches) {
            return null;
        }

        double[] h = H.get();
        CvPoint2D64f[] dstCorners = CvPoint2D64f.createArray(4);
        for(int i = 0; i < 4; i++) {
            double x = srcCorners[i].x, y = srcCorners[i].y;
            double Z = 1./(h[6]*x + h[7]*y + h[8]);
            double X = (h[0]*x + h[1]*y + h[2])*Z;
            double Y = (h[3]*x + h[4]*y + h[5])*Z;
            dstCorners[i].x = X;
            dstCorners[i].y = Y;
        }

        pt1.cols  = objectDescriptors.length;
        pt2.cols  = objectDescriptors.length;
        mask.cols = objectDescriptors.length;
        pt1.pool();
        pt2.pool();
        mask.pool();
        H.pool();
        return dstCorners;
    }

}
