package org.bytedeco.javacv;

import org.bytedeco.javacpp.avcodec;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PacketRecorderTest {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd__hhmmSSS");

    private static final int RECORD_LENGTH = 5000;

    private static final boolean AUDIO_ENABLED = false;

    public static void main(String[] args) throws FrameRecorder.Exception, FrameGrabber.Exception {

        String inputFile = "/home/usr/videos/VIDEO_FILE_NAME.mp4";

        // Decodes-encodes
        String outputFile = "/tmp/" + DATE_FORMAT.format(new Date()) + "_frameRecord.mp4";
        PacketRecorderTest.frameRecord(inputFile, outputFile);

        // copies codec (no need to re-encode)
        outputFile = "/tmp/" + DATE_FORMAT.format(new Date()) + "_packetRecord.mp4";
        PacketRecorderTest.packetRecord(inputFile, outputFile);

    }

    public static void frameRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);

        grabber.start();
        recorder.start();

        Frame frame;
        long t1 = System.currentTimeMillis();
        while ((frame = grabber.grabFrame(AUDIO_ENABLED, true, true, false)) != null) {
            recorder.record(frame);
            if ((System.currentTimeMillis() - t1) > RECORD_LENGTH) {
                break;
            }
        }
        recorder.stop();
        grabber.stop();
    }

    public static void packetRecord(String inputFile, String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {

        int audioChannel = AUDIO_ENABLED ? 1 : 0;

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);

        grabber.start();
        recorder.start(grabber.getFormatContext());

        avcodec.AVPacket packet;
        long t1 = System.currentTimeMillis();
        while ((packet = grabber.grabPacket()) != null) {
            recorder.recordPacket(packet);
            if ((System.currentTimeMillis() - t1) > RECORD_LENGTH) {
                break;
            }
        }

        recorder.stop();
        grabber.stop();

    }

}
