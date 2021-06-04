package org.bytedeco.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.Loader;
import org.junit.Test;

import java.io.File;

public class FrameGrabberRealTimeTest {
	public static final String url = "";
	public static final String file = "";
	
	@Test
	public void testWithMethodCall() throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
		grabber.start();
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
		try {
			recorder.setFormat("flv");
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
			recorder.setFrameRate(grabber.getFrameRate());
			recorder.setTimestamp(grabber.getTimestamp());
			recorder.start();
			Frame frame = null;
			while ((frame = grabber.grabFrame(true,true,true,false,true,true)) != null) {
				recorder.record(frame);
			}
		} finally {
			recorder.stop();
			recorder.release();
			grabber.stop();
			grabber.release();
		}
		
	}
	@Test
	public void testWithGlobalConfig() throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
		grabber.setOption("re","true");
		grabber.start();
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
		try {
			recorder.setFormat("flv");
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
			recorder.setFrameRate(grabber.getFrameRate());
			recorder.setTimestamp(grabber.getTimestamp());
			recorder.start();
			Frame frame = null;
			while ((frame = grabber.grab()) != null) {
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
