package org.bytedeco.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.Loader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FrameGrabberRealTimeTest {
	public static final String url = "";
	public static final String file = "";
	
	@Test
	public void testWithGlobalConfig() throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
		grabber.setAtFrameRate(true);
		grabber.start();
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
		try {
			recorder.setFormat("flv");
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
			recorder.setFrameRate(grabber.getFrameRate());
			recorder.setTimestamp(grabber.getTimestamp());
			recorder.start();
			Frame frame = null;
			long startTime=System.currentTimeMillis();
			while ((frame = grabber.grab()) != null) {
				//delay < 10ms
				Assert.assertTrue(frame.timestamp / 1000-(System.currentTimeMillis()-startTime)<10000);
				recorder.record(frame);
			}
		} finally {
			recorder.stop();
			recorder.release();
			grabber.stop();
			grabber.release();
		}
		
	}
}
