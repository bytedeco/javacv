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

import java.nio.ByteBuffer;
import java.util.Arrays;

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
public class Marker implements Cloneable {
    public Marker(int id, double[] corners, double confidence) {
        this.id = id;
        this.corners = corners;
        this.confidence = confidence;
    }
    public Marker(int id, double ... corners) {
        this(id, corners, 1.0);
    }
    @Override public Marker clone() {
        return new Marker(id, corners.clone(), confidence);
    }
    public int id;
    public double[] corners;
    public double confidence;

    @Override public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        hash = 37 * hash + (this.corners != null ? this.corners.hashCode() : 0);
        return hash;
    }
    @Override public boolean equals(Object o) {
        if (o instanceof Marker) {
            Marker m = (Marker)o;
            return m.id == id && Arrays.equals(m.corners, corners);
        }
        return false;
    }

    public double[] getCenter() {
        double x = 0, y = 0;
if (true) {
// the centroid is not what we want as it does not remain at
// the same physical point under projective transformations..
// But it has the advantage of averaging noise better, and does
// give better results
        for (int i = 0; i < 4; i++) {
            x += corners[2*i  ];
            y += corners[2*i+1];
        }
        x /= 4;
        y /= 4;
} else {
        double x1 = corners[0]; double y1 = corners[1];
        double x2 = corners[4]; double y2 = corners[5];
        double x3 = corners[2]; double y3 = corners[3];
        double x4 = corners[6]; double y4 = corners[7];

        double u = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3))/
                   ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
        x = x1 + u*(x2-x1);
        y = y1 + u*(y2-y1);
}
        return new double[] { x, y };
    }

    public IplImage getImage() {
        return getImage(id);
    }

    private static IplImage imageCache[] = new IplImage[4096];
    public static IplImage getImage(int id) {
        if (imageCache[id] == null) {
            imageCache[id] = IplImage.create(8, 8, IPL_DEPTH_8U, 1);
            createImagePatternBCH(id, imageCache[id].getByteBuffer());
        }
        return imageCache[id];
    }

    private static final double[] src = { 0, 0, 8, 0, 8, 8, 0, 8 };
    public void draw(IplImage image) {
        draw(image, CvScalar.BLACK, 1, null);
    }
    public void draw(IplImage image, CvScalar color, double scale, CvMat prewarp) {
        draw(image, color, scale, scale, prewarp);
    }
    private static ThreadLocal<CvMat>
            H3x3      = CvMat.createThreadLocal(3, 3),
            srcPts4x1 = CvMat.createThreadLocal(4, 1, CV_64F, 2),
            dstPts4x1 = CvMat.createThreadLocal(4, 1, CV_64F, 2);
    public void draw(IplImage image, CvScalar color, double scaleX, double scaleY, CvMat prewarp) {
        CvMat H = H3x3.get();
        JavaCV.getPerspectiveTransform(src, corners, H);
        if (prewarp != null) {
            cvGEMM(prewarp, H, 1, null, 0, H, 0);
        }
        IplImage  marker = getImage();
        ByteBuffer  mbuf = marker.getByteBuffer();
        CvMat     srcPts = srcPts4x1.get();
        CvMat     dstPts = dstPts4x1.get();
        CvPoint  tempPts = new CvPoint(4);

        int h = marker.height();
        int w = marker.width();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (mbuf.get(y*w + x) == 0) {
                    srcPts.put((double)x, y,  x+1, y,  x+1, y+1,  x, y+1);
                    //System.out.println("srcPts" + srcPts);
                    cvPerspectiveTransform(srcPts, dstPts, H);
                    //System.out.println("dstPts" + dstPts);

                    double centerx = 0, centery = 0;
                    for (int i = 0; i < 4; i++) {
                      centerx += dstPts.get(i*2  );
                      centery += dstPts.get(i*2+1);
                    }
                    centerx /= 4;
                    centery /= 4;
                    for (int i = 0; i < 4; i++) {
                        double a = dstPts.get(i*2  );
                        double b = dstPts.get(i*2+1);
                        double dx = centerx - a;
                        double dy = centery - b;
                        dx = dx < 0 ? -1 : 0;
                        dy = dy < 0 ? -1 : 0;
                        tempPts.position(i).x((int)Math.round((a*scaleX + dx) * (1<<16)));
                        tempPts.position(i).y((int)Math.round((b*scaleY + dy) * (1<<16)));
                    }
                    cvFillConvexPoly(image, tempPts.position(0), 4, color, 8/*CV_AA*/, 16);
                }
            }
        }
    }

    public static class ArraySettings extends BaseChildSettings {
        int rows = 8, columns = 12;
        double sizeX = 200, sizeY = 200, spacingX = 300, spacingY = 300;
        boolean checkered = true;

        public int getRows() {
            return rows;
        }
        public void setRows(int rows) {
            firePropertyChange("rows", this.rows, this.rows = rows);
        }

        public int getColumns() {
            return columns;
        }
        public void setColumns(int columns) {
            firePropertyChange("columns", this.columns, this.columns = columns);
        }

        public double getSizeX() {
            return sizeX;
        }
        public void setSizeX(double sizeX) {
            firePropertyChange("sizeX", this.sizeX, this.sizeX = sizeX);
        }
        public double getSizeY() {
            return sizeY;
        }
        public void setSizeY(double sizeY) {
            firePropertyChange("sizeY", this.sizeY, this.sizeY = sizeY);
        }

        public double getSpacingX() {
            return spacingX;
        }
        public void setSpacingX(double spacingX) {
            firePropertyChange("spacingX", this.spacingX, this.spacingX = spacingX);
        }
        public double getSpacingY() {
            return spacingY;
        }
        public void setSpacingY(double spacingY) {
            firePropertyChange("spacingY", this.spacingY, this.spacingY = spacingY);
        }

        public boolean isCheckered() {
            return checkered;
        }
        public void setCheckered(boolean checkered) {
            firePropertyChange("checkered", this.checkered, this.checkered = checkered);
        }
    }
    public static Marker[][] createArray(ArraySettings settings) {
        return createArray(settings, 0, 0);
    }
    public static Marker[][] createArray(ArraySettings settings, double marginx, double marginy) {
        Marker[] markers = new Marker[settings.rows*settings.columns];
        int id = 0;
        for (int y = 0; y < settings.rows; y++) {
            for (int x = 0; x < settings.columns; x++) {
                double sx =   settings.sizeX/2;
                double sy =   settings.sizeY/2;
                double cx = x*settings.spacingX + sx + marginx;
                double cy = y*settings.spacingY + sy + marginy;
                markers[id] = new Marker(id, new double[] {
                    cx-sx, cy-sy,  cx+sx, cy-sy,  cx+sx, cy+sy,  cx-sx, cy+sy }, 1);
                id++;
            }
        }
        if (!settings.checkered) {
            return new Marker[][] { markers };
        } else {
            Marker[] markers1 = new Marker[markers.length/2];
            Marker[] markers2 = new Marker[markers.length/2];
            for (int i = 0; i < markers.length; i++) {
                int x = i%settings.columns;
                int y = i/settings.columns;
                if (x%2==0 ^ y%2==0) {
                    markers2[i/2] = markers[i];
                } else {
                    markers1[i/2] = markers[i];
                }
            }
            return new Marker[][] { markers2, markers1 };
        }
    }
    public static Marker[][] createArray(int rows, int columns, double sizeX, double sizeY,
            double spacingX, double spacingY, boolean checkered, double marginx, double marginy) {
        ArraySettings s = new ArraySettings();
        s.rows      = rows;      s.columns  = columns;
        s.sizeX     = sizeX;     s.sizeY    = sizeY;
        s.spacingX  = spacingX;  s.spacingY = spacingY;
        s.checkered = checkered;
        return createArray(s, marginx, marginy);
    }

    public static void applyWarp(Marker[] markers, CvMat warp) {
        CvMat pts = srcPts4x1.get();

        for (Marker m : markers) {
            cvPerspectiveTransform(pts.put(m.corners), pts, warp);
            pts.get(m.corners);
        }
    }

    @Override public String toString() {
        String s = "[" + id + ": " +
                "(" + corners[0] + ", " + corners[1] + ") " +
                "(" + corners[2] + ", " + corners[3] + ") " +
                "(" + corners[4] + ", " + corners[5] + ") " +
                "(" + corners[6] + ", " + corners[7] + ")]";
        return s;
    }
}
