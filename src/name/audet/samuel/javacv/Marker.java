/*
 * Copyright (C) 2009,2010 Samuel Audet
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

package name.audet.samuel.javacv;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.ARToolKitPlus.*;

/**
 *
 * @author Samuel Audet
 */
public class Marker {
    public Marker(int id, double[] corners, double confidence) {
        this.id = id;
        this.corners = corners;
        this.confidence = confidence;
    }
    public Marker(int id, double ... corners) {
        this(id, corners, 1.0);
    }
    @Override public Object clone() {
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
if (false) {
// the centroid is not what we want as it does not remain at
// the same physical point under projective transformations..
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
    public void draw(IplImage image, CvScalar color, double scaleX, double scaleY, CvMat prewarp) {
        CvMat H = CvMat.take(3, 3);
        JavaCV.getPerspectiveTransform(src, corners, H);
        if (prewarp != null) {
            cvGEMM(prewarp, H, 1, null, 0, H, 0);
        }
        IplImage    marker = getImage();
        CvScalar.ByValue c = color.byValue();
        ByteBuffer    mbuf = marker.getByteBuffer();
        CvMat       srcPts = CvMat.take(4, 1, CV_64F, 2);
        CvMat       dstPts = CvMat.take(4, 1, CV_64F, 2);
        CvPoint[]  tempPts = CvPoint.createArray(4);

        for (int y = 0; y < marker.height; y++) {
            for (int x = 0; x < marker.width; x++) {
                if (mbuf.get(y*marker.width + x) == 0) {
                    srcPts.put(0, x  ); srcPts.put(1, y  );
                    srcPts.put(2, x+1); srcPts.put(3, y  );
                    srcPts.put(4, x+1); srcPts.put(5, y+1);
                    srcPts.put(6, x  ); srcPts.put(7, y+1);
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
                        tempPts[i].x = (int)Math.round((a*scaleX + dx) * (1<<16));
                        tempPts[i].y = (int)Math.round((b*scaleY + dy) * (1<<16));
                    }
                    cvFillConvexPoly(image, tempPts, 4, c, 8/*CV_AA*/, 16);
                }
            }
        }
        dstPts.pool();
        srcPts.pool();
        H     .pool();
    }

    public static class ArraySettings extends BaseSettings {
        int rows = 8, columns = 12;
        double sizeX = 200, sizeY = 200, spacingX = 300, spacingY = 300;
        boolean checkered = true;

        public int getRows() {
            return rows;
        }
        public void setRows(int rows) {
            pcs.firePropertyChange("rows", this.rows, this.rows = rows);
        }

        public int getColumns() {
            return columns;
        }
        public void setColumns(int columns) {
            pcs.firePropertyChange("columns", this.columns, this.columns = columns);
        }

        public double getSizeX() {
            return sizeX;
        }
        public void setSizeX(double sizeX) {
            pcs.firePropertyChange("sizeX", this.sizeX, this.sizeX = sizeX);
        }
        public double getSizeY() {
            return sizeY;
        }
        public void setSizeY(double sizeY) {
            pcs.firePropertyChange("sizeY", this.sizeY, this.sizeY = sizeY);
        }

        public double getSpacingX() {
            return spacingX;
        }
        public void setSpacingX(double spacingX) {
            pcs.firePropertyChange("spacingX", this.spacingX, this.spacingX = spacingX);
        }
        public double getSpacingY() {
            return spacingY;
        }
        public void setSpacingY(double spacingY) {
            pcs.firePropertyChange("spacingY", this.spacingY, this.spacingY = spacingY);
        }

        public boolean isCheckered() {
            return checkered;
        }
        public void setCheckered(boolean checkered) {
            pcs.firePropertyChange("checkered", this.checkered, this.checkered = checkered);
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
        CvMat pts = CvMat.take(4, 1, CV_64F, 2);

        for (Marker m : markers) {
            pts.put(m.corners);
            cvPerspectiveTransform(pts, pts, warp);
            pts.get(m.corners);
        }
        pts.pool();
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
