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

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 *
 * @author Samuel Audet
 */
public class OpenCVFrameRecorder extends FrameRecorder {
    public OpenCVFrameRecorder(String filename, int imageWidth, int imageHeight) {
        this.filename    = filename;
        this.imageWidth  = imageWidth;
        this.imageHeight = imageHeight;

        this.pixelFormat = 1;
        this.codecID     = CV_FOURCC_DEFAULT;
        this.bitrate     = 0;
        this.frameRate   = 30;
    }
    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() {
        try {
            release();
        } catch (Exception ex) { }
    }

    private CvVideoWriter writer = null;

    public void start() throws Exception {
        writer = cvCreateVideoWriter(filename, codecID, frameRate, cvSize(imageWidth, imageHeight), pixelFormat);
        if (writer == null) {
            throw new Exception("cvCreateVideoWriter(): Could not create a writer");
        }
    }

    public void stop() throws Exception {
        if (writer != null) {
            cvReleaseVideoWriter(writer);
            writer = null;
        }
    }

    public void record(IplImage frame) throws Exception {
        if (writer != null) {
            if (cvWriteFrame(writer, frame) == 0) {
                throw new Exception("cvWriteFrame(): Could not record frame");
            }
        } else {
            throw new Exception("Cannot record: There is no writer");
        }
    }
}
