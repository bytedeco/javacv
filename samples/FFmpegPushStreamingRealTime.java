import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;


/**
 * @author wangxi <346461036@qq.com>
 * it is a example for simulate ffmpeg param  "-re"
 * Read input at native frame rate.
 * Mainly used to simulate a grab device, or live input stream (e.g. when reading from a file).
 * Should not be used with actual grab devices or live input streams (where it can cause packet loss).
 */
public class FFmpegPushStreamingRealTime {
	
	public static final String RTMP_SERVER_URL = "";
	
	public static final String LOCAL_FILE = "";
	
	public static void main(String[] args) throws Exception {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(RTMP_SERVER_URL);
		grabber.start();
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(LOCAL_FILE, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
		try {
			recorder.setFormat("flv");
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
			recorder.setFrameRate(grabber.getFrameRate());
			recorder.setTimestamp(grabber.getTimestamp());
			recorder.start();
			
			/**
			 * grab() may take some time,so we should record by real timestamp
			 */
			long begin = System.currentTimeMillis();
			Frame frame = null;
			while ((frame = grabber.grab()) != null) {
				long delay = frame.timestamp / 1000 - (System.currentTimeMillis() - begin);
				/**
				 * If the streaming is too fast, we sleep for a period of time according to the delay
				 */
				if (delay > 0) {
					Thread.sleep(delay);
				}
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