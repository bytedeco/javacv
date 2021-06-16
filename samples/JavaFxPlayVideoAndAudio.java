import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

/**
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 * @author Jarek Sacha
 */
public class JavaFxPlayVideoAndAudio extends Application {

    private static class PlaybackTimer {
        private Long startTime = -1L;
        private final DataLine soundLine;

        public PlaybackTimer(DataLine soundLine) {
            this.soundLine = soundLine;
        }

        public PlaybackTimer() {
            this.soundLine = null;
        }

        public void start() {
            if (soundLine == null) {
                startTime = System.nanoTime();
            }
        }

        public long elapsedMicros() {
            if (soundLine == null) {
                if (startTime < 0) {
                    throw new IllegalStateException("PlaybackTimer not initialized.");
                }
                return (System.nanoTime() - startTime) / 1000;
            } else {
                return soundLine.getMicrosecondPosition();
            }
        }
    }

    private static final Logger LOG = Logger.getLogger(JavaFxPlayVideoAndAudio.class.getName());

    private static volatile Thread playThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final StackPane root = new StackPane();
        final ImageView imageView = new ImageView();

        root.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());

        final Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle("Video + audio");
        primaryStage.setScene(scene);
        primaryStage.show();

        playThread = new Thread(new Runnable() { public void run() {
            try {
                final String videoFilename = getParameters().getRaw().get(0);
                final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilename);
                grabber.start();
                primaryStage.setWidth(grabber.getImageWidth());
                primaryStage.setHeight(grabber.getImageHeight());
                final PlaybackTimer playbackTimer;
                final SourceDataLine soundLine;
                if (grabber.getAudioChannels() > 0) {
                    final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);

                    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    soundLine = (SourceDataLine) AudioSystem.getLine(info);
                    soundLine.open(audioFormat);
                    soundLine.start();
                    playbackTimer = new PlaybackTimer(soundLine);
                } else {
                    soundLine = null;
                    playbackTimer = new PlaybackTimer();
                }

                final JavaFXFrameConverter converter = new JavaFXFrameConverter();

                final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
                final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();

                final long maxReadAheadBufferMicros = 1000 * 1000L;

                long lastTimeStamp = -1L;
                while (!Thread.interrupted()) {
                    final Frame frame = grabber.grab();
                    if (frame == null) {
                        break;
                    }
                    if (lastTimeStamp < 0) {
                        playbackTimer.start();
                    }
                    lastTimeStamp = frame.timestamp;
                    if (frame.image != null) {
                        final Frame imageFrame = frame.clone();

                        imageExecutor.submit(new Runnable() {
                            public void run() {
                                final Image image = converter.convert(imageFrame);
                                final long timeStampDeltaMicros = imageFrame.timestamp - playbackTimer.elapsedMicros();
                                imageFrame.close();
                                if (timeStampDeltaMicros > 0) {
                                    final long delayMillis = timeStampDeltaMicros / 1000L;
                                    try {
                                        Thread.sleep(delayMillis);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        imageView.setImage(image);
                                    }
                                });
                            }
                        });
                    } else if (frame.samples != null) {
                        if (soundLine == null) {
                            throw new IllegalStateException("Internal error: sound playback not initialized");
                        }
                        final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                        channelSamplesShortBuffer.rewind();

                        final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

                        for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                            short val = channelSamplesShortBuffer.get(i);
                            outBuffer.putShort(val);
                        }

                        audioExecutor.submit(new Runnable() {
                            public void run() {
                                soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                outBuffer.clear();
                            }
                        });
                    }
                    final long timeStampDeltaMicros = frame.timestamp - playbackTimer.elapsedMicros();
                    if (timeStampDeltaMicros > maxReadAheadBufferMicros) {
                        Thread.sleep((timeStampDeltaMicros - maxReadAheadBufferMicros) / 1000);
                    }
                }

                if (!Thread.interrupted()) {
                    long delay = (lastTimeStamp - playbackTimer.elapsedMicros()) / 1000 +
                            Math.round(1 / grabber.getFrameRate() * 1000);
                    Thread.sleep(Math.max(0, delay));
                }
                grabber.stop();
                grabber.release();
                if (soundLine != null) {
                    soundLine.stop();
                }
                audioExecutor.shutdownNow();
                audioExecutor.awaitTermination(10, TimeUnit.SECONDS);
                imageExecutor.shutdownNow();
                imageExecutor.awaitTermination(10, TimeUnit.SECONDS);

                Platform.exit();
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, null, exception);
                System.exit(1);
            }
        }});
        playThread.start();
    }

    @Override
    public void stop() {
        playThread.interrupt();
    }

}
