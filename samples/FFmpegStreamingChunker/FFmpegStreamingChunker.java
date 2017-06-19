package FFmpegStreamingChunker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 *
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 */
public class FFmpegStreamingChunker {

    private final static long MIN_CHUNK_DURATION = 20000000;

    private Consumer<File> chunkHandler = null;
    private final String source;
    private final FpsCalculator fpsCalculator = new FpsCalculator();
    private final ChunkDetector chunkDetector;
    private boolean hasVideo = false;
    private double fps = 25;
    private FFmpegFrameGrabber grabber = null;
    private FFmpegFrameRecorder recorder = null;
    private final List<Frame> probeFrames = new ArrayList<>();
    private boolean started = false;
    private long begin = -1;

    public FFmpegStreamingChunker(String source, File targetDir) {
        this(source, targetDir, MIN_CHUNK_DURATION);
    }

    public FFmpegStreamingChunker(String source, File targetDir, long duration) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(targetDir);
        if (duration < MIN_CHUNK_DURATION) {
            throw new IllegalArgumentException();
        }
        targetDir.mkdirs();
        if (!targetDir.canWrite()) {
            throw new RuntimeException("Target directory isn't writable.");
        }
        this.source = source;

        chunkDetector = new ChunkDetector(duration);
        chunkDetector.setChunkListener(new ChunkListener() {
            
            File outputFile;
            
            @Override
            public void onChunkBegin() throws Exception {
                outputFile = new File(targetDir, (begin + chunkDetector.getCalculatedChunkBegin()) + ".mp4");
                
                recorder = new FFmpegFrameRecorder(
                        outputFile,
                        grabber.getImageWidth(),
                        grabber.getImageHeight());
                recorder.setFormat("mp4");
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setVideoOption("preset", "ultrafast");
                //recorder.setVideoOption("crf", "0");
                //recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.setFrameRate(fps);
                recorder.start();
            }

            @Override
            public void onChunkEnd() throws Exception {
                if (hasVideo) {
                    fps = fpsCalculator.getFpsRounded();
                    fpsCalculator.reset();
                }

                recorder.stop();

                if (chunkHandler != null) {
                    chunkHandler.accept(outputFile);
                }
            }
        });
    }

    public void start() throws Exception {
        for (int pfn = 0, vfn = 0; pfn < 100 && vfn < 10; pfn++) {
            Frame frame = grab().clone();
            if (frame.image != null) {
                hasVideo = true;
                vfn++;
            }
            probeFrames.add(frame);
        }

        for (Frame probeFrame : probeFrames) {
            record(probeFrame);
        }

        started = true;
    }

    public void stop() throws Exception {
        started = false;
        fpsCalculator.reset();
        chunkDetector.reset();
        hasVideo = false;
        fps = 25;
        begin = -1;
        if (grabber != null) {
            grabber.stop();
            grabber = null;
        }
        if (recorder != null) {
            recorder.stop();
            recorder = null;
            boolean gcRequired = !probeFrames.isEmpty();
            probeFrames.clear();
            if (gcRequired) {
                System.gc();
            }
        }
    }

    public boolean next() throws Exception {
        if (!started) {
            throw new IllegalStateException();
        }
        Frame frame;
        if (!probeFrames.isEmpty()) {
            frame = probeFrames.remove(0);
            if (probeFrames.isEmpty()) {
                System.gc();
            }
        } else {
            frame = grab();
        }
        if (frame == null) {
            return false;
        }
        record(frame);
        return true;
    }

    private Frame grab() throws Exception {
        if (grabber == null) {
            grabber = new FFmpegFrameGrabber(source);
            grabber.setOption("stimeout", "10000000");
            grabber.start();

            begin = System.currentTimeMillis() * 1000;
        }

        Frame frame = grabber.grab();
        if (frame != null) {
            frame.timestamp = grabber.getTimestamp();
            if (frame.image != null) {
                fpsCalculator.addTimestamp(frame.timestamp);
            }
        }
        
        return frame;
    }

    private void record(Frame frame) throws Exception {
        chunkDetector.next(frame.keyFrame, frame.timestamp);
        recorder.record(frame);
    }

    public Consumer<File> getChunkHandler() {
        return chunkHandler;
    }

    public void setChunkHandler(Consumer<File> chunkHandler) {
        this.chunkHandler = chunkHandler;
    }

}
