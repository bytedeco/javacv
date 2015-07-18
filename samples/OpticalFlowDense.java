import java.nio.FloatBuffer;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_video.*;

/**
 * This code will calculate the optical flow for every pixel using DenseOpticalFlow between two images
 * (Frame-1 & Frame-2) and put the velocity of every pixel to another image (OF) in their coordinate.
 *
 * @author Dawit Gebreyohannes
 */
public class OpticalFlowDense {
    public static void main(String[] args) {
        Mat pFrame = imread("image0.png", CV_LOAD_IMAGE_GRAYSCALE);
        Mat cFrame = imread("image1.png", CV_LOAD_IMAGE_GRAYSCALE);
        Mat pGray = new Mat();
        Mat cGray = new Mat();

        pFrame.convertTo(pGray, CV_32FC1);
        cFrame.convertTo(cGray, CV_32FC1);
        Mat Optical_Flow = new Mat();

        DenseOpticalFlow tvl1 = createOptFlow_DualTVL1();
        tvl1.calc(pGray, cGray, Optical_Flow);

        Mat OF = new Mat(pGray.rows(), pGray.cols(), CV_32FC1);
        FloatBuffer in = Optical_Flow.getFloatBuffer();
        FloatBuffer out = OF.getFloatBuffer();

        int height = pGray.rows();
        int width = pGray.cols();

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                float xVelocity = in.get();
                float yVelocity = in.get();
                float pixelVelocity = (float)Math.sqrt(xVelocity*xVelocity + yVelocity*yVelocity);
                out.put(pixelVelocity);
            }
        }
        imwrite("OF.png", OF);
    }
}
