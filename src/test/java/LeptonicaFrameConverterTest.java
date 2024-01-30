import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.LeptonicaFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.leptonica;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_imgproc.line;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * These tests compare that the Frame to Leptonica PIX converter works correctly against known-good
 * files that come in different stride lengths.
 *
 * @link <a href="https://github.com/bytedeco/javacv/issues/1115">Source bug</a>
 */
public class LeptonicaFrameConverterTest {
    @TempDir
    static Path tempDir;

    @ParameterizedTest
    @ValueSource(ints = {8, 9, 10, 11, 12})
    public void testBw(final int cols) {
        LeptonicaFrameConverter lfcFixed = new LeptonicaFrameConverter();
        OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();

        final int rows = 10;

        Mat originalImage = new Mat(rows, cols, CV_8UC1);
        int stepX = 255 / cols;
        int stepY = 255 / rows;
        int stepTotal = Math.min(stepX, stepY);
        for (int i = 0; i < originalImage.rows(); i++) {
            for (int j = 0; j < originalImage.cols(); j++) {
                line(originalImage, new Point(j, i), new Point(j, i), new Scalar(stepTotal * j));
            }
        }

        //System.out.println(String.format("orig\n  capacity %d\n  w %d\n  h %d\n  ch %d\n  dpt %d\n  str %d", originalImage.asByteBuffer().capacity(), originalImage.cols(), originalImage.rows(), originalImage.channels(), originalImage.depth(), originalImage.step()));
        opencv_imgcodecs.imwrite(tempDir + "/mat-bw-" + rows + "x" + cols + ".bmp", originalImage);
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                assertArrayEquals(
                        Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("LeptonicaFrameConverter/mat-bw-" + rows + "x" + cols + ".bmp").getPath())),
                        Files.readAllBytes(Paths.get(tempDir + "/mat-bw-" + rows + "x" + cols + ".bmp")),
                        "Mat file differs");
            }
        });

        Frame ocrFrame = matConverter.convert(originalImage);
        //System.out.println(String.format("frame\n  capacity %d\n  w %d\n  h %d\n  ch %d\n  dpt %d\n  str %d", ocrFrame.image[0].capacity(), ocrFrame.imageWidth, ocrFrame.imageHeight, ocrFrame.imageChannels, ocrFrame.imageDepth, ocrFrame.imageStride));

        PIX converted = lfcFixed.convert(ocrFrame);
        //System.out.println(String.format("fixconverted pix\n  capacity %d\n  w %d\n  h %d\n  ch %d\n  dpt %d\n  wpl %d", converted.createBuffer().capacity(), converted.w(), converted.h(), -1, converted.d(), converted.wpl()));
        leptonica.pixWrite(tempDir + "/pix-bw-" + rows + "x" + cols + ".bmp", converted, leptonica.IFF_BMP);
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                assertArrayEquals(
                        Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("LeptonicaFrameConverter/pix-bw-" + rows + "x" + cols + ".bmp").getPath())),
                        Files.readAllBytes(Paths.get(tempDir + "/pix-bw-" + rows + "x" + cols + ".bmp")),
                        "Pix file differs");
            }
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 9, 10, 11, 12})
    public void testRgb(final int cols) {
        LeptonicaFrameConverter lfcFixed = new LeptonicaFrameConverter();
        OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();

        final int rows = 10;

        Mat originalImage = new Mat(rows, cols, CV_8UC3);
        int stepX = 255 / cols;
        int stepY = 255 / rows;
        for (int i = 0; i < originalImage.rows(); i++) {
            for (int j = 0; j < originalImage.cols(); j++) {
                // Warning: OpenCV uses BGR ordering under the hood!
                // See https://learnopencv.com/why-does-opencv-use-bgr-color-format/
                line(originalImage, new Point(j, i), new Point(j, i), new Scalar(i * stepY, j * stepX, 0, 0));
            }
        }

        //System.out.println(String.format("orig\n  capacity %d\n  w %d\n  h %d\n  ch %d\n  dpt %d\n  str %d", originalImage.asByteBuffer().capacity(), originalImage.cols(), originalImage.rows(), originalImage.channels(), originalImage.depth(), originalImage.step()));
        opencv_imgcodecs.imwrite(tempDir + "/mat-rgb-" + rows + "x" + cols + ".bmp", originalImage);
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                assertArrayEquals(
                        Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("LeptonicaFrameConverter/mat-rgb-" + rows + "x" + cols + ".bmp").getPath())),
                        Files.readAllBytes(Paths.get(tempDir + "/mat-rgb-" + rows + "x" + cols + ".bmp")),
                        "Mat file differs");
            }
        });

        Frame ocrFrame = matConverter.convert(originalImage);
        //System.out.println(String.format("frame\n  capacity %d\n  w %d\n  h %d\n  ch %d\n  dpt %d\n  str %d", ocrFrame.image[0].capacity(), ocrFrame.imageWidth, ocrFrame.imageHeight, ocrFrame.imageChannels, ocrFrame.imageDepth, ocrFrame.imageStride));

        PIX converted = lfcFixed.convert(ocrFrame);
        //System.out.println(String.format("fixconverted pix\n  capacity %d\n  w %d\n  h %d\n  ch %d\n  dpt %d\n  wpl %d", converted.createBuffer().capacity(), converted.w(), converted.h(), -1, converted.d(), converted.wpl()));
        leptonica.pixWrite(tempDir + "/pix-rgb-" + rows + "x" + cols + ".bmp", converted, leptonica.IFF_BMP);
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                assertArrayEquals(
                        Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("LeptonicaFrameConverter/pix-rgb-" + rows + "x" + cols + ".bmp").getPath())),
                        Files.readAllBytes(Paths.get(tempDir + "/pix-rgb-" + rows + "x" + cols + ".bmp")),
                        "Pix file differs");
            }
        });
    }
}
