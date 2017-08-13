import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class DeinterlacedVideoPlayer {
	
	private static final int DEVICE_ID = 0;
	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;
	private static final int FRAMERATE = 25;
	private static final int PIXEL_FORMAT = avutil.AV_PIX_FMT_BGR24;;
	
	private String ffmpegString = "yadif=mode=0:parity=-1:deint=0,format=bgr24";
	private FrameGrabber grabber;
	
	public DeinterlacedVideoPlayer() {}
	
	public void start() {
		Loader.load(opencv_objdetect.class);
		FrameFilter filter = null;
		try {
			startFrameGrabber();
			CvMemStorage storage = CvMemStorage.create();
			
			Frame frame = null;
			while ((frame = grabber.grab()) != null) {
				cvClearMemStorage(storage);
				if (filter == null) {
					filter = new FFmpegFrameFilter(ffmpegString, frame.imageWidth, frame.imageHeight);
					filter.setPixelFormat(PIXEL_FORMAT);
					filter.start();
				}
				
				filter.push(frame);
				frame = filter.pull();
				
				// do something with the filtered frame
				
			}
		} catch (Exception | org.bytedeco.javacv.FrameFilter.Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			releaseGrabberAndFilter(this.grabber, filter);
		}
	}

	private void startFrameGrabber() throws Exception {
		grabber = new OpenCVFrameGrabber(DEVICE_ID);
		grabber.setImageWidth(WIDTH);
		grabber.setImageHeight(HEIGHT);
		grabber.setFrameRate(FRAMERATE);
		grabber.setPixelFormat(PIXEL_FORMAT);
		grabber.start();
	}
	
	private void releaseGrabberAndFilter(FrameGrabber grabber, FrameFilter filter) {
		try {
			if (grabber != null) {
				grabber.release();
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot release frame grabber!", e);
		} finally {
			releaseFilter(filter);
		}
	}
	
	private void releaseFilter(FrameFilter filter) {
		if (filter == null) {
			return;
		}

		try {
			filter.close();
		} catch (org.bytedeco.javacv.FrameFilter.Exception e) {
			throw new RuntimeException("Cannot close frame filter!", e);
		}
	}
	
}
