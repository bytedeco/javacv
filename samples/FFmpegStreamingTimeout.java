
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.avformat;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

/**
 *
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 */
public class FFmpegStreamingTimeout {

    /**
     * There is no universal option for streaming timeout. Each of protocols has
     * its own list of options.
     */
    private static enum TimeoutOption {
        /**
         * Depends on protocol (FTP, HTTP, RTMP, SMB, SSH, TCP, UDP, or UNIX).
         *
         * http://ffmpeg.org/ffmpeg-all.html
         */
        TIMEOUT,
        /**
         * Protocols
         *
         * Maximum time to wait for (network) read/write operations to complete,
         * in microseconds.
         *
         * http://ffmpeg.org/ffmpeg-all.html#Protocols
         */
        RW_TIMEOUT,
        /**
         * Protocols -> RTSP
         *
         * Set socket TCP I/O timeout in microseconds.
         *
         * http://ffmpeg.org/ffmpeg-all.html#rtsp
         */
        STIMEOUT;

        public String getKey() {
            return toString().toLowerCase();
        }

    }

    private static final String SOURCE_RTSP = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private static final int TIMEOUT = 10; // In seconds.

    public static void main(String[] args) {
        rtspStreamingTest();
//        testWithCallback(); // This is not working properly. It's just for test.
    }

    private static void rtspStreamingTest() {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(SOURCE_RTSP);
            /**
             * "timeout" - IS IGNORED when a network cable have been unplugged
             * before a connection and sometimes when connection is lost.
             *
             * "rw_timeout" - IS IGNORED when a network cable have been
             * unplugged before a connection but the option takes effect after a
             * connection was established.
             *
             * "stimeout" - works fine.
             */
            grabber.setOption(
                    TimeoutOption.STIMEOUT.getKey(),
                    String.valueOf(TIMEOUT * 1000000)
            ); // In microseconds.
            grabber.start();

            Frame frame = null;
            /**
             * When network is disabled (before brabber was started) grabber
             * throws exception: "org.bytedeco.javacv.FrameGrabber$Exception:
             * avformat_open_input() error -138: Could not open input...".
             *
             * When connections is lost (after a few grabbed frames)
             * grabber.grab() returns null without exception.
             */
            while ((frame = grabber.grab()) != null) {
                System.out.println("frame grabbed at " + grabber.getTimestamp());
            }
            System.out.println("loop end with frame: " + frame);
        } catch (FrameGrabber.Exception ex) {
            System.out.println("exception: " + ex);
        }
        System.out.println("end");
    }

    private static void testWithCallback() {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(SOURCE_RTSP);
            /**
             * grabber.getFormatContext() is null before grabber.start().
             *
             * But if network is disabled grabber.start() will never return.
             *
             * That's why interrupt_callback not suitable for "network disabled
             * case".
             */
            grabber.start();

            final AtomicBoolean interruptFlag = new AtomicBoolean(false);
            avformat.AVIOInterruptCB.Callback_Pointer cp = new avformat.AVIOInterruptCB.Callback_Pointer() {
                @Override
                public int call(Pointer pointer) {
                    // 0 - continue, 1 - exit
                    int interruptFlagInt = interruptFlag.get() ? 1 : 0;
                    System.out.println("callback, interrupt flag == " + interruptFlagInt);
                    return interruptFlagInt;
                }

            };
            avformat.AVFormatContext oc = grabber.getFormatContext();
            avformat.avformat_alloc_context();
            avformat.AVIOInterruptCB cb = new avformat.AVIOInterruptCB();
            cb.callback(cp);
            oc.interrupt_callback(cb);
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(TIMEOUT);
                    interruptFlag.set(true);
                    System.out.println("interrupt flag was changed");
                } catch (InterruptedException ex) {
                    System.out.println("exception in interruption thread: " + ex);
                }
            }).start();

            Frame frame = null;
            /**
             * On one of my RTSP cams grabber stops calling callback on
             * connection lost. I think it's has something to do with message:
             * "[swscaler @ 0000000029af49e0] deprecated pixel format used, make
             * sure you did set range correctly".
             *
             * So there is at least one case when grabber stops calling
             * callback.
             */
            while ((frame = grabber.grab()) != null) {
                System.out.println("frame grabbed at " + grabber.getTimestamp());
            }
            System.out.println("loop end with frame: " + frame);
        } catch (FrameGrabber.Exception ex) {
            System.out.println("exception: " + ex);
        }
        System.out.println("end");
    }
}
