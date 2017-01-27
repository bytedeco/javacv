/*
 * Copyright (C) 2016 Samuel Audet
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.junit.Test;

import static org.bytedeco.javacpp.avutil.*;
import static org.junit.Assert.*;

/**
 * Test cases for FrameGrabber classes. Also uses other classes from JavaCV.
 *
 * @author Samuel Audet
 */
public class FrameGrabberTest {

    @Test public void testFFmpegFrameGrabber() {
        System.out.println("FFmpegFrameGrabber");

        File tempFile = new File(Loader.getTempDir(), "test.mkv");
        try {
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(new FileOutputStream(tempFile), 640, 480);
            recorder.setFormat("matroska"); // mp4 doesn't support streaming
            recorder.setPixelFormat(AV_PIX_FMT_BGR24);
            recorder.setVideoCodecName("libx264rgb");
            recorder.setVideoQuality(0); // lossless
            recorder.start();

            Frame[] frames = new Frame[1000];
            for (int n = 0; n < frames.length; n++) {
                Frame frame = new Frame(640, 480, Frame.DEPTH_UBYTE, 3);
                UByteIndexer frameIdx = frame.createIndexer();
                for (int i = 0; i < frameIdx.rows(); i++) {
                    for (int j = 0; j < frameIdx.cols(); j++) {
                        for (int k = 0; k < frameIdx.channels(); k++) {
                            frameIdx.put(i, j, k, n + i + j + k);
                        }
                    }
                }
                recorder.record(frame);
                frames[n] = frame;
            }
            recorder.stop();
            recorder.release();

            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new FileInputStream(tempFile));
            grabber.start();

            for (int n = 0; n < frames.length; n++) {
                Frame frame = frames[n];
                Frame frame2 = grabber.grab();
                assertEquals(frame.imageWidth, frame2.imageWidth);
                assertEquals(frame.imageHeight, frame2.imageHeight);
                assertEquals(frame.imageChannels, frame2.imageChannels);

                UByteIndexer frameIdx = frame.createIndexer();
                UByteIndexer frame2Idx = frame2.createIndexer();
                for (int i = 0; i < frameIdx.rows(); i++) {
                    for (int j = 0; j < frameIdx.cols(); j++) {
                        for (int k = 0; k < frameIdx.channels(); k++) {
                            int b = frameIdx.get(i, j, k);
                            assertEquals(b, frame2Idx.get(i, j, k));
                        }
                    }
                }
            }
            assertEquals(grabber.grab(), null);
            grabber.restart();
            grabber.stop();
            grabber.release();
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        } finally {
            tempFile.delete();
        }
    }

}
