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
 */

package com.googlecode.javacv;

import java.util.LinkedList;
import java.util.List;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 *
 * @author Samuel Audet
 */
public abstract class FrameRecorder {

    public static final List<Class<? extends FrameRecorder>> list =
            new LinkedList<Class<? extends FrameRecorder>>();
    static {
        list.add(FFmpegFrameRecorder.class);
        list.add(OpenCVFrameRecorder.class);
    }
    public static void init() {
        for (Class<? extends FrameRecorder> c : list) {
            try {
                c.getMethod("tryLoad").invoke(null);
            } catch (Exception ex) { }
        }
    }

    protected String filename;
    protected int    imageWidth, imageHeight;
    protected int    pixelFormat, codecID, bitrate;
    protected double frameRate;

    public int getImageWidth() {
        return imageWidth;
    }
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getPixelFormat() {
        return pixelFormat;
    }
    public void setPixelFormat(int pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    public int getCodecID() {
        return codecID;
    }
    public void setCodecID(int codecID) {
        this.codecID = codecID;
    }

    public int getBitrate() {
        return bitrate;
    }
    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public double getFrameRate() {
        return frameRate;
    }
    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public abstract void start() throws Exception;
    public abstract void stop() throws Exception;
    public abstract void record(IplImage frame) throws Exception;
    public abstract void release() throws Exception;

}
