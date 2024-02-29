import java.nio.FloatBuffer;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_optflow.*;
import org.bytedeco.opencv.opencv_video.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_optflow.*;
import static org.bytedeco.opencv.global.opencv_video.*;

/**
 * This code will calculate the optical flow for every pixel using DenseOpticalFlow between two images (Frame-1 &
 * Frame-2) and put the velocity of every pixel to another image (OF) in their coordinate.
 *
 * @author Dawit Gebreyohannes
 */
public class OpticalFlowDense {

    public static void main(final String[] args) {
        final Mat pFrame = imread("samples/image0.png", IMREAD_GRAYSCALE),
                cFrame = imread("samples/image1.png", IMREAD_GRAYSCALE),
                pGray = new Mat(), cGray = new Mat(), Optical_Flow = new Mat();

        pFrame.convertTo(pGray, CV_32FC1);
        cFrame.convertTo(cGray, CV_32FC1);

        final DenseOpticalFlow tvl1 = DualTVL1OpticalFlow.create();
        tvl1.calc(pGray, cGray, Optical_Flow);

        final Mat OF = new Mat(pGray.rows(), pGray.cols(), CV_32FC1);
        final FloatBuffer in = Optical_Flow.createBuffer(),
                out = OF.createBuffer();

        final int height = pGray.rows(), width = pGray.cols();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final float xVelocity = in.get();
                final float yVelocity = in.get();
                final float pixelVelocity = (float) Math
                        .sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
                out.put(pixelVelocity);
            }
        }
        imwrite("OF.png", OF);
    }
}
