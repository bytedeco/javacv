package org.bytedeco.javacv;

import org.bytedeco.ffmpeg.global.avcodec;
import org.junit.Test;

public class FrameGrabberRealTimeTest {
	public static final String url = "rtmp://10.69.180.26:21935/stream/sister";
	public static final String file = "E:\\CloudMusic\\MV\\好妹妹 - 云野.mp4";
	
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
			while ((frame = grabber.grabAtFrameRate()) != null) {
				recorder.record(frame);
			}
		} catch (InterruptedException | FrameGrabber.Exception e) {
			e.printStackTrace();
		} finally {
			recorder.stop();
			recorder.release();
			grabber.stop();
			grabber.release();
		}
		
	}
}
