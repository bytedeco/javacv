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
public class MarkedPlane {

    public MarkedPlane(int width, int height, Marker[] planeMarkers, double superScale) {
        this(width, height, planeMarkers, false, CvScalar.BLACK, CvScalar.WHITE, superScale);
    }
    public MarkedPlane(int width, int height, Marker[] markers,
            boolean initPrewarp, CvScalar foregroundColor, CvScalar backgroundColor, double superScale) {
        this.markers = markers;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;

//        this.srcPts    = CvMat.create(planeMarkers.length*4, 2);
//        this.dstPts    = CvMat.create(planeMarkers.length*4, 2);

        this.prewarp = null;
//        this.totalWarp = CvMat.create(3, 3);
//        this.tempWarp = CvMat.create(3, 3);

        if (initPrewarp) {
            prewarp = CvMat.create(3, 3);
            double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE,
                   maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
            for (Marker m : markers) {
                double[] c = m.corners;
                minx = Math.min(Math.min(Math.min(Math.min(minx, c[0]), c[2]), c[4]), c[6]);
                miny = Math.min(Math.min(Math.min(Math.min(miny, c[1]), c[3]), c[5]), c[7]);
                maxx = Math.max(Math.max(Math.max(Math.max(maxx, c[0]), c[2]), c[4]), c[6]);
                maxy = Math.max(Math.max(Math.max(Math.max(maxy, c[1]), c[3]), c[5]), c[7]);
            }
            double aspect = (maxx-minx)/(maxy-miny);
            if (aspect > (double)width/height) {
                double h = (double)width/aspect;
//                srcPtsBuf.position(0); srcPtsBuf.put(new double[] { minx, miny, maxx, miny, maxx, maxy, minx, maxy });
//                dstPtsBuf.position(0); dstPtsBuf.put(new double[] { 0, height-h, width, height-h, width, height, 0, height });
//                srcPts.height = dstPts.height = 4;
//                cvFindHomography(srcPts, dstPts, preWarp);
                JavaCV.getPerspectiveTransform(
                        new double[] { minx, miny, maxx, miny, maxx, maxy, minx, maxy },
                        new double[] { 0, height-h, width, height-h, width, height, 0, height }, prewarp);
            } else {
                double w = height*aspect;
//                srcPtsBuf.position(0); srcPtsBuf.put(new double[] { minx, miny, maxx, miny, maxx, maxy, minx, maxy });
//                dstPtsBuf.position(0); dstPtsBuf.put(new double[] { 0, 0, w, 0, w, height, 0, height });
//                srcPts.height = dstPts.height = 4;
//                cvFindHomography(srcPts, dstPts, preWarp);
                JavaCV.getPerspectiveTransform(
                        new double[] { minx, miny, maxx, miny, maxx, maxy, minx, maxy },
                        new double[] { 0, 0, w, 0, w, height, 0, height }, prewarp);
            }
        }

        if (width > 0 && height > 0) {
            planeImage = IplImage.create(width, height, IPL_DEPTH_8U, 1);
            if (superScale == 1.0) {
                superPlaneImage = null;
            } else {
                superPlaneImage = IplImage.create((int)Math.ceil(width*superScale),
                        (int)Math.ceil(height*superScale), IPL_DEPTH_8U, 1);
            }
            setPrewarp(prewarp);
        }

        localSrcPts = CvMat.createThreadLocal(markers.length*4, 2);
        localDstPts = CvMat.createThreadLocal(markers.length*4, 2);
    }

    private Marker[] markers = null;
//    private CvPoint tempPts = new CvPoint(4);
//    private CvMat srcPts, dstPts;
    private CvMat prewarp;//, totalWarp, tempWarp;

    private IplImage planeImage = null, superPlaneImage = null;
    private CvScalar foregroundColor, backgroundColor;

    private ThreadLocal<CvMat> localSrcPts, localDstPts;

    public CvScalar getForegroundColor() {
        return foregroundColor;
    }
    public void setForegroundColor(CvScalar foregroundColor) {
        this.foregroundColor = foregroundColor;
        setPrewarp(prewarp);
    }

    public CvScalar getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(CvScalar backgroundColor) {
        this.backgroundColor = backgroundColor;
        setPrewarp(prewarp);
    }

    public Marker[] getMarkers() {
        return markers;
    }
    public void setColors(CvScalar foregroundColor, CvScalar backgroundColor) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        setPrewarp(prewarp);
    }

    public CvMat getPrewarp() {
        return prewarp;
    }
    public void setPrewarp(CvMat prewarp) {
        this.prewarp = prewarp;
        if (superPlaneImage == null) {
            cvSet(planeImage, backgroundColor);
        } else {
            cvSet(superPlaneImage, backgroundColor);
        }
        for (int i = 0; i < markers.length; i++) {
            if (superPlaneImage == null) {
                markers[i].draw(planeImage, foregroundColor, 1.0, prewarp);
            } else {
                markers[i].draw(superPlaneImage, foregroundColor, (double)
                        superPlaneImage.width()/planeImage.width(), prewarp);
            }
        }
        if (superPlaneImage != null) {
            cvResize(superPlaneImage, planeImage, CV_INTER_AREA);
        }
        //cvSaveImage("planeImage.png", planeImage);
    }

    public IplImage getImage() {
        return planeImage;
    }
    public int getWidth() {
        return planeImage.width();
    }
    public int getHeight() {
        return planeImage.height();
    }

    public double getTotalWarp(Marker[] imagedMarkers, CvMat totalWarp) {
        return getTotalWarp(imagedMarkers, totalWarp, false);
    }
    private static ThreadLocal<CvMat>
            tempWarp3x3 = CvMat.createThreadLocal(3, 3);
    public double getTotalWarp(Marker[] imagedMarkers, CvMat totalWarp, boolean useCenters) {
        double rmse = Double.POSITIVE_INFINITY;
        int pointsPerMarker = useCenters ? 1 : 4;

        CvMat srcPts = localSrcPts.get(); srcPts.rows(markers.length*pointsPerMarker);
        CvMat dstPts = localDstPts.get(); dstPts.rows(markers.length*pointsPerMarker);

        int numPoints = 0;
        for (Marker m1 : markers) {
            for (Marker m2 : imagedMarkers) {
                if (m1.id == m2.id) {
                    if (useCenters) {
                        srcPts.put(numPoints*2, m1.getCenter());
                        dstPts.put(numPoints*2, m2.getCenter());
                    } else {
                        srcPts.put(numPoints*2, m1.corners);
                        dstPts.put(numPoints*2, m2.corners);
                    }
                    numPoints += pointsPerMarker;
                    break;
                }
            }
        }

        if (numPoints > 4 || (srcPts.rows() == 4 && numPoints == 4)) {
            // compute homography ... should we use a robust method?
            srcPts.rows(numPoints); dstPts.rows(numPoints);
            if (numPoints == 4) {
                JavaCV.getPerspectiveTransform(srcPts.get(), dstPts.get(), totalWarp);
            } else {
                cvCopy(cvMat(findHomography(cvarrToMat(srcPts), cvarrToMat(dstPts))), totalWarp);
            }

            // compute transformed source<->dest RMSE
            srcPts.cols(1); srcPts.type(CV_64F, 2);
            dstPts.cols(1); dstPts.type(CV_64F, 2);
            cvPerspectiveTransform(srcPts, srcPts, totalWarp);
            srcPts.cols(2); srcPts.type(CV_64F, 1);
            dstPts.cols(2); dstPts.type(CV_64F, 1);

            rmse = 0;
            for (int i = 0; i < numPoints; i++) {
                double dx = dstPts.get(i*2  )-srcPts.get(i*2  );
                double dy = dstPts.get(i*2+1)-srcPts.get(i*2+1);
                rmse += dx*dx+dy*dy;
            }
            rmse = Math.sqrt(rmse/numPoints);
//            System.out.println(rmse);

            if (prewarp != null) {
                // remove pre-warp from total warp
                CvMat tempWarp = tempWarp3x3.get();
                cvInvert(prewarp, tempWarp);
                cvMatMul(totalWarp, tempWarp, totalWarp);
            }
//            System.out.println("totalWarp:\n" + totalWarp);
        }
        return rmse;
    }
}
