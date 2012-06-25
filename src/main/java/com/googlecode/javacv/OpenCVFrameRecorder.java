/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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

import com.googlecode.javacpp.Loader;
import java.io.File;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 *
 * @author Samuel Audet
 */
public class OpenCVFrameRecorder extends FrameRecorder {

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(com.googlecode.javacv.cpp.opencv_highgui.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + OpenCVFrameRecorder.class, t);
            }
        }
    }

    public OpenCVFrameRecorder(File file, int imageWidth, int imageHeight) {
        this(file.getAbsolutePath(), imageWidth, imageHeight);
    }
    public OpenCVFrameRecorder(String filename, int imageWidth, int imageHeight) {
        this.filename    = filename;
        this.imageWidth  = imageWidth;
        this.imageHeight = imageHeight;

        this.pixelFormat = 1;
        this.videoCodec  = windows ? CV_FOURCC_PROMPT : CV_FOURCC_DEFAULT;
        this.frameRate   = 30;
    }
    public void release() throws Exception {
        if (writer != null) {
            cvReleaseVideoWriter(writer);
            writer = null;
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private static final boolean windows = Loader.getPlatformName().startsWith("windows");
    private String filename;
    private CvVideoWriter writer = null;

    public void start() throws Exception {
        writer = cvCreateVideoWriter(filename, videoCodec, frameRate, cvSize(imageWidth, imageHeight), pixelFormat);
        if (writer == null) {
            throw new Exception("cvCreateVideoWriter(): Could not create a writer");
        }
    }

    public void stop() throws Exception {
        release();
    }

    public void record(IplImage frame) throws Exception {
        if (writer != null) {
            if (cvWriteFrame(writer, frame) == 0) {
                throw new Exception("cvWriteFrame(): Could not record frame");
            }
        } else {
            throw new Exception("Cannot record: There is no writer (Has start() been called?)");
        }
    }
}
