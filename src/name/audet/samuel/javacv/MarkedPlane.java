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

import java.awt.Dimension;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

/**
 *
 * @author Samuel Audet
 */
public class MarkedPlane {

    public MarkedPlane(Dimension size, Marker[] planeMarkers, double superScale) {
        this(size.width, size.height, planeMarkers, superScale);
    }
    public MarkedPlane(int width, int height, Marker[] planeMarkers, double superScale) {
        this(width, height, planeMarkers, false, CvScalar.BLACK, CvScalar.WHITE, superScale);
    }
    public MarkedPlane(Dimension size, Marker[] planeMarkers,
            boolean initPrewarp, CvScalar foregroundColor, CvScalar backgroundColor, double superScale) {
        this(size.width, size.height, planeMarkers, initPrewarp, foregroundColor, backgroundColor, superScale);
    }
    public MarkedPlane(int width, int height, Marker[] markers,
            boolean initPrewarp, CvScalar foregroundColor, CvScalar backgroundColor, double superScale) {
        this.markers = markers;
        this.foregroundColor = foregroundColor.byValue();
        this.backgroundColor = backgroundColor.byValue();

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
    }

    private Marker[] markers = null;
//    private CvPoint[] tempPts = CvPoint.createArray(4);
//    private CvMat srcPts, dstPts;
    private CvMat prewarp;//, totalWarp, tempWarp;

    private IplImage planeImage = null, superPlaneImage = null;
    private CvScalar.ByValue foregroundColor, backgroundColor;

    public CvScalar getForegroundColor() {
        return foregroundColor;
    }
    public void setForegroundColor(CvScalar foregroundColor) {
        this.foregroundColor = foregroundColor.byValue();
        setPrewarp(prewarp);
    }

    public CvScalar getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(CvScalar backgroundColor) {
        this.backgroundColor = backgroundColor.byValue();
        setPrewarp(prewarp);
    }

    public Marker[] getMarkers() {
        return markers;
    }
    public void setColors(CvScalar foregroundColor, CvScalar backgroundColor) {
        this.foregroundColor = foregroundColor.byValue();
        this.backgroundColor = backgroundColor.byValue();
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
                        superPlaneImage.width/planeImage.width, prewarp);
            }
        }
        if (superPlaneImage != null) {
            cvResize(superPlaneImage, planeImage, CV_INTER_AREA);
        }
        //highgui.INSTANCE.cvSaveImage("planeImage.png", planeImage);
    }

    public IplImage getImage() {
        return planeImage;
    }
    public int getWidth() {
        return planeImage.width;
    }
    public int getHeight() {
        return planeImage.height;
    }

    public double getTotalWarp(Marker[] imagedMarkers, CvMat totalWarp) {
        return getTotalWarp(imagedMarkers, totalWarp, false);
    }
    public double getTotalWarp(Marker[] imagedMarkers, CvMat totalWarp, boolean useCenters) {
        double rmse = Double.POSITIVE_INFINITY;
        int pointsPerMarker = useCenters ? 1 : 4;

        CvMat tempWarp  = CvMat.take(3, 3);
        CvMat srcPts    = CvMat.take(markers.length*pointsPerMarker, 2);
        CvMat dstPts    = CvMat.take(markers.length*pointsPerMarker, 2);

        int numPoints = 0;
        for (Marker m1 : markers) {
            for (Marker m2 : imagedMarkers) {
                if (m1.id == m2.id) {
                    // I got a SIGSEGV here.. why??
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

        if (numPoints > 4 || (srcPts.rows == 4 && numPoints == 4)) {
            // compute homography ... should we use a robust method?
            srcPts.rows = dstPts.rows = numPoints;
            cvFindHomography(srcPts, dstPts, totalWarp);

            // compute transformed source<->dest RMSE
            srcPts.cols = 1; srcPts.setType(CV_64F, 2);
            dstPts.cols = 1; dstPts.setType(CV_64F, 2);
            cvPerspectiveTransform(srcPts, srcPts, totalWarp);
            srcPts.cols = 2; srcPts.setType(CV_64F, 1);
            dstPts.cols = 2; dstPts.setType(CV_64F, 1);

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
                cvInvert(prewarp, tempWarp);
                cvMatMul(totalWarp, tempWarp, totalWarp);
            }
//            System.out.println("totalWarp:\n" + totalWarp);
        }
        tempWarp.pool();
        srcPts.rows = markers.length*pointsPerMarker; srcPts.pool();
        dstPts.rows = markers.length*pointsPerMarker; dstPts.pool();
        return rmse;
    }

}
