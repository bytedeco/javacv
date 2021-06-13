import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private static final Logger LOG = Logger.getLogger(JavaFxPlayVideoAndAudio.class.getName());

    private static volatile Thread playThread;
    private static final AtomicBoolean requestPlayThreadStop = new AtomicBoolean(false);

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
                final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);

                final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                final SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                soundLine.open(audioFormat);
                soundLine.start();

                final JavaFXFrameConverter converter = new JavaFXFrameConverter();

                ExecutorService executor = Executors.newSingleThreadExecutor();

                while (!Thread.interrupted() && !requestPlayThreadStop.get()) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        break;
                    }
                    if (frame.image != null) {
                        final Image image = converter.convert(frame);
                        final long timeStampDeltaMicros = frame.timestamp - soundLine.getMicrosecondPosition();
                        if (timeStampDeltaMicros > 0) {
                            // We are ahead we will need to slow down to keep synced with audio
                            long delayMillis = timeStampDeltaMicros / 1000L;
                            // Wait for the next frame
                            Thread.sleep(delayMillis);
                        }
                        Platform.runLater(new Runnable() { public void run() {
                            imageView.setImage(image);
                        }});
                    } else if (frame.samples != null) {
                        final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                        channelSamplesShortBuffer.rewind();

                        final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

                        for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                            short val = channelSamplesShortBuffer.get(i);
                            outBuffer.putShort(val);
                        }

                        executor.submit(new Runnable() { public void run() {
                            soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                            outBuffer.clear();
                        }});
                    }
                }
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
                soundLine.stop();
                grabber.stop();
                grabber.release();
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
        requestPlayThreadStop.set(true);
    }

}
