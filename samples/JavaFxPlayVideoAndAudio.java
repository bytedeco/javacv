package javacvtest;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
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
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 */
public class JavaFxPlayVideoAndAudio extends Application {

    private static final Logger LOG = Logger.getLogger(JavaFxPlayVideoAndAudio.class.getName());
    private static final double SC16 = (double) 0x7FFF + 0.4999999999999999;

    private static volatile Thread playThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        ImageView imageView = new ImageView();

        root.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());

        Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle("Video + audio");
        primaryStage.setScene(scene);
        primaryStage.show();

        playThread = new Thread(() -> {
            try {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("C:\\Users\\gda\\Desktop\\bunny_move\\1486430724718.mp4");
                grabber.start();
                AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, true);

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                soundLine.open(audioFormat);
                soundLine.start();

                OpenCVFrameConverter converter = new OpenCVFrameConverter.ToIplImage();
                Java2DFrameConverter paintConverter = new Java2DFrameConverter();

                ExecutorService executor = Executors.newSingleThreadExecutor();

                while (!Thread.interrupted()) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        break;
                    }
                    if (frame.image != null) {
                        Image image = SwingFXUtils.toFXImage(paintConverter.convert(frame), null);
                        Platform.runLater(() -> {
                            imageView.setImage(image);
                        });
                    } else if (frame.samples != null) {
                        FloatBuffer channelSamplesFloatBuffer = (FloatBuffer) frame.samples[0];
                        channelSamplesFloatBuffer.rewind();

                        ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesFloatBuffer.capacity() * 2);

                        for (int i = 0; i < channelSamplesFloatBuffer.capacity(); i++) {
                            /**
                             * FloatBuffer is converted to ByteBuffer with some
                             * magic constant SC16 (~Short.MAX_VALUE). I found
                             * it on some forum with this explanation:
                             *
                             * For 16 bit signed to float, divide by 32768. For
                             * float to 16 bit, multiply by 32768.
                             *
                             * Going from float to integer, do the initial
                             * conversion into a container bigger than the
                             * destination container so that it doesn't
                             * accidentally wrap on overs. For instance, on 16
                             * or 24 bit, you can use signed int 32.
                             *
                             * Or alternately, do the clipping on the scaled
                             * float value, before casting into integer. That
                             * way you can save the clipped float direct to a 16
                             * bit container and not have to fool with an
                             * intermediate 32 bit container.
                             *
                             * Clip the float to int results to stay in bounds.
                             * Anything lower than 0x8000 clipped to 0x8000, and
                             * anything higher than 0x7FFFF clipped to 0x7FFFF.
                             *
                             * The advantage of using a factor of 32768 is that
                             * bit patterns will stay the same after conversion.
                             * If you use 32767, the bit patterns will change.
                             * Not much change, but it just doesn't seem elegant
                             * to have them change if it can be avoided.
                             *
                             * If you want to do it as fast as possible it is
                             * just a matter of optimizing the code in whatever
                             * way seems sensible.
                             */
                            // Could be replaced with: short val = (short) (channelSamplesFloatBuffer.get(i) * Short.MAX_VALUE);
                            short val = (short) ((double) channelSamplesFloatBuffer.get(i) * SC16);
                            outBuffer.putShort(val);
                        }

                        /**
                         * We need this because soundLine.write ignores
                         * interruptions during writing.
                         */
                        try {
                            executor.submit(() -> {
                                soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                outBuffer.clear();
                            }).get();
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
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
        });
        playThread.start();
    }

    @Override
    public void stop() throws Exception {
        playThread.interrupt();
    }

}
