import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Created by Johan Swanberg on 2018-09-21
 * <p>
 * An example of how to use the perspective warp method in JavaCV.
 */

public class PerspectiveWarpDemo extends Thread {

    static CanvasFrame frame = new CanvasFrame("Perspective Warp Demo - warped image");
    static CanvasFrame frameUnedited = new CanvasFrame("Perspective Warp Demo - Unedited image");

    public static void main(String[] args) {
        Mat image = imread("shapes1.jpg");
        Mat perspectiveWarpedImg = performPerspectiveWarp(image, 30, 0, 200, 0, 400, 250, 40, 260);

        OpenCVFrameConverter converter = new OpenCVFrameConverter.ToIplImage();
        frame.showImage(converter.convert(perspectiveWarpedImg));
        frameUnedited.showImage(converter.convert(image));

        image.release();
        perspectiveWarpedImg.release();

        frameUnedited.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frameUnedited.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setLocation(frameUnedited.getX()+frameUnedited.getWidth(), frameUnedited.getY());
    }


    /**
     * Performs a perspective warp that takes four corners and stretches them to the corners of the image.
     * x1,y1 represents the top left corner, 2 top right, going clockwise.
     * This method does not release/deallocate the input image Mat, call inputMat.release() after this method
     * if you don't plan on using the input more after this method.
     *
     * @param imageMat The image to perform the stretch on
     * @return A stretched image mat.
     */
    private static Mat performPerspectiveWarp(Mat imageMat, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {

        double originalImgWidth = imageMat.size().width();
        double originalImgHeight = imageMat.size().height();

        FloatPointer srcCorners = new FloatPointer(
                x1, y1,
                x2, y2,
                x3, y3,
                x4, y4);


        FloatPointer dstCorners = new FloatPointer(
                0, 0,
                (int) originalImgWidth, 0,
                (int) originalImgWidth, (int) originalImgHeight,
                0, (int) originalImgHeight);

        //create matrices with width 2 to hold the x,y values, and 4 rows, to hold the 4 different corners.
        Mat src = new Mat(new Size(2, 4), CV_32F, srcCorners);
        Mat dst = new Mat(new Size(2, 4), CV_32F, dstCorners);

        Mat perspective = getPerspectiveTransform(src, dst);
        Mat result = new Mat();
        warpPerspective(imageMat, result, perspective, new Size((int) originalImgWidth, (int) originalImgHeight));

        src.release();
        dst.release();
        srcCorners.deallocate();
        dstCorners.deallocate();

        return result;
    }


}

