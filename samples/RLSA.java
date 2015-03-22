import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Based on "Implementation Run Length Smoothing Algorithm in C++":
 * http://stackoverflow.com/questions/21554431/implementation-run-length-smoothing-algorithm-in-c
 *
 * @author Nicholas Woodward
 */
public class RLSA {

    public static void main(String[] args) {
        String imagePath = args[0].trim();

        IplImage image = null;
        try {
            Java2DFrameConverter converter1 = new Java2DFrameConverter();
            OpenCVFrameConverter.ToIplImage converter2 = new OpenCVFrameConverter.ToIplImage();
            BufferedImage img = ImageIO.read(new File(imagePath));
            image = converter2.convert(converter1.convert(img));
        } catch (Exception ex)  {
            ex.printStackTrace();
        }

        if (image != null) {
            IplImage rlsaImage = runLengthSmoothingAlgorithm(image);
            // do something with the result image

            image.release();
            rlsaImage.release();
        }
    }

    public static IplImage runLengthSmoothingAlgorithm(IplImage image) {
        IplImage gry = image.clone();
        cvThreshold(gry, gry, 128, 255, CV_THRESH_BINARY_INV);

        CvMat tmpImg = gry.asCvMat();
        ByteBuffer buffer = gry.getByteBuffer();

        CvScalar temp = new CvScalar();
        temp.val(255);

        int hor_thres = 77; // adjust for your text size
        int zero_count = 0;
        int one_flag = 0;
        for (int i = 0; i < tmpImg.rows(); i++) {
            for (int j = 0; j < tmpImg.cols(); j++) {
                int ind = i * gry.widthStep() + j * gry.nChannels() + 1;
                double val = -1;
                if (ind < buffer.capacity()) {
                    val = (buffer.get(ind) & 0xFF);
                }
                if (val == 255) {
                    if (one_flag == 255) {
                        if (zero_count <= hor_thres) {
                            tmpImg.put(i, j, 255);
                            for (int n = (j-zero_count); n < j; n++) {
                                tmpImg.put(i, n, 255);
                            }
                        } else {
                            one_flag = 0;
                        }
                        zero_count = 0;
                    }
                    one_flag = 255;
                } else if (one_flag == 255) {
                    zero_count = zero_count + 1;
                }
            }
        }

        int ver_thres = 44; // adjustable
        zero_count = 0;
        one_flag = 0;
        for (int i = 0; i < tmpImg.cols(); i++) {
            for (int j = 0; j < tmpImg.rows(); j++) {
                int ind = j * gry.widthStep() + i * gry.nChannels() + 1;
                double val = -1;
                if (ind < buffer.capacity()) {
                    val = (buffer.get(ind) & 0xFF);
                }
                if (val == 255) {
                    if (one_flag == 255) {
                        if (zero_count <= ver_thres) {
                            tmpImg.put(j, i, 255);
                            for (int n = ((j-zero_count) >= 0) ? (j-zero_count) : 0; n < j; n++) {
                                tmpImg.put(n, i, 255);
                            }
                        } else {
                            one_flag = 0;
                        }
                        zero_count = 0;
                    }
                    one_flag = 255;
                } else if (one_flag == 255) {
                    zero_count = zero_count + 1;
                }
            }
        }
        return gry;
    }
}
