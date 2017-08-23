/*
 * Copyright (C) 2016-2017 Samuel Audet
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
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.junit.Test;

import de.zft.maintelrob.ffmpegPacket.test.Test2ChangingResolutionDuringRuntime;

import static org.bytedeco.javacpp.avutil.*;
import static org.junit.Assert.*;

/**
 * Test cases for FrameGrabber classes. Also uses other classes from JavaCV.
 *
 * @author Samuel Audet
 */
public class FrameGrabberTestChangingResolution {
	private File tempFile = new File(Loader.getTempDir(), "test.mkv");
	private File tempTargetFile = new File(Loader.getTempDir(), "target.mkv");
	private boolean endRequested;

	private void makeTestfile() throws Exception {
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(new FileOutputStream(tempFile), 640, 480, 2);
		recorder.setFormat("matroska"); // mp4 doesn't support streaming
		recorder.setPixelFormat(AV_PIX_FMT_BGR24);
		recorder.setVideoCodecName("libx264rgb");
		recorder.setVideoQuality(0); // lossless
		recorder.setFrameRate(30);
		recorder.start();

		Frame[] frames = new Frame[60];
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
	}

	final public void setupUDPSender(int x, int y, int bandwidth, int count) throws IOException {
		FrameGrabber fg = new FFmpegFrameGrabber(tempFile);
		fg.setFrameRate(30);

		FrameRecorder fr = new FFmpegFrameRecorder("udp://127.0.0.1:2345", 0);
		fr.setVideoCodecName("mpeg2video");
		fr.setFormat("mpegts");

		fr.setImageWidth(x);
		fr.setImageHeight(y);
		fr.setVideoBitrate(bandwidth);

		fr.setFrameRate(30);

		fg.start();
		fr.start();

		final boolean[] b = new boolean[1];
		Thread t = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < count; i++) {
						/*- System.out.println("S: " + fg.getFrameNumber() + " " + fg.getTimestamp() + " "
								+ fg.getFrameRate() + " " + fg.getImageWidth() + "x" + fg.getImageHeight() + " "
								+ fg.getVideoCodec() + " " + fg.getVideoBitrate() + " " + i); */
						Frame source = fg.grabFrame();
						fr.record(source);
					}
					fg.close();
					fr.close();
					b[0] = true;
				} catch (Exception e) {
					fail("Exception should not have been thrown: " + e);
					try {
						fg.close();
						fr.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					b[0] = true;
				}
			}
		};
		t.setName("Sender");
		t.start();

		while (!b[0]) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	final public void setupUDPReceiver() throws IOException {
		Thread t = new Thread() {

			public void run() {
				FrameGrabber fg = new FFmpegFrameGrabber("udp://127.0.0.1:2345");
				fg.setFrameRate(30);

				FrameRecorder fr = new FFmpegFrameRecorder(tempTargetFile, 0);
				fr.setVideoCodecName("mpeg2video");
				fr.setFormat("mpegts");

				fr.setImageWidth(640);
				fr.setImageHeight(480);
				fr.setVideoBitrate(8000000);

				fr.setFrameRate(30);

				try {
					fg.start();
					fr.start();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Tests whether the width of the picture changes trough all
				// qualities and every step has a few pictures.
				try {
					int n = 0;
					int m = 0; // Pictures in this quality
					int q = 0; // which quality state?
					int[] qualities = { 160, 320, 640, 160, 320, 640, 320, 160 };
					while (!endRequested) {
						/*- System.out.println("R: " + fg.getFrameNumber() + " " + fg.getTimestamp() + " "
								+ fg.getFrameRate() + " " + fg.getImageWidth() + "x" + fg.getImageHeight() + " "
								+ fg.getVideoCodec() + " " + fg.getVideoBitrate()); */
						Frame source = fg.grabFrame();
						n++;
						m++;
						// System.out.println("WRITTEN: " + n + " " + m + " " +
						// q + " " + source.imageWidth);
						if (source.imageWidth != qualities[q]) {
							q++;
							assertEquals(source.imageWidth, qualities[q]);
							assertTrue(m > 10);
							assertTrue(m <= 60);
							m = 0;
						}
						fr.record(source);
					}
					assertEquals(q, qualities.length - 1);
					fr.close();
				} catch (Exception e) {
					fail("Exception should not have been thrown: " + e);
					try {
						fg.close();
						fr.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		t.setName("Receiver");
		t.start();
	}

	@Test
	public void testFFmpegFrameGrabber() {
		System.out.println("FFmpegFrameGrabber");

		try {
			makeTestfile();

			setupUDPReceiver();

			System.out.println("Changing to 160x120");
			setupUDPSender(160, 120, 50000, 60);

			System.out.println("Changing to 320x240");
			setupUDPSender(320, 240, 100000, 60);

			System.out.println("Changing to 640x480");
			setupUDPSender(640, 480, 200000, 60);

			System.out.println("Changing to 160x120");
			setupUDPSender(160, 120, 50000, 60);

			System.out.println("Changing to 320x240");
			setupUDPSender(320, 240, 100000, 60);

			System.out.println("Changing to 640x480");
			setupUDPSender(640, 480, 200000, 60);

			System.out.println("Changing to 320x240");
			setupUDPSender(320, 240, 100000, 60);

			System.out.println("Changing to 160x120");
			setupUDPSender(160, 120, 50000, 60);

			Thread.sleep(3000);
			endRequested = true;
		} catch (Exception e) {
			tempFile.delete();
			tempTargetFile.delete();
			fail("Exception should not have been thrown: " + e);
		}

		try {

			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new FileInputStream(tempTargetFile));
			grabber.setSampleMode(FrameGrabber.SampleMode.FLOAT);
			grabber.start();

			int n = 0;
			Frame frame2;
			while ((frame2 = grabber.grab()) != null) {
				if (frame2.image != null) {
					n++;
					assertEquals(640, frame2.imageWidth);
				}
			}

			// It seems that ffmpeg lose some frames while switching (ideal
			// value would be 240)
			// System.out.println("END NUMBER: " + n);
			assertTrue(n > 300);
			assertTrue(n <= 480);
			assertEquals(null, grabber.grab());
			grabber.restart();
			grabber.stop();
			grabber.release();
		} catch (Exception e) {
			fail("Exception should not have been thrown: " + e);
		} finally {
			// tempFile.delete();
			// tempTargetFile.delete();
		}
	}

}
