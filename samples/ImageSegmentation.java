/*
 * JavaCV version of OpenCV imageSegmentation.cpp
 * https://github.com/opencv/opencv/blob/master/samples/cpp/tutorial_code/ImgTrans/imageSegmentation.cpp
 *
 * The OpenCV example image is available at the following address
 * https://github.com/opencv/opencv/blob/master/samples/data/cards.png
 *
 * Paolo Bolettieri <paolo.bolettieri@gmail.com>
 */

import static org.bytedeco.javacpp.helper.opencv_core.RGB;
import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.CV_8U;
import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import static org.bytedeco.javacpp.opencv_core.CV_8UC3;
import static org.bytedeco.javacpp.opencv_core.NORM_MINMAX;
import static org.bytedeco.javacpp.opencv_core.bitwise_not;
import static org.bytedeco.javacpp.opencv_core.multiply;
import static org.bytedeco.javacpp.opencv_core.normalize;
import static org.bytedeco.javacpp.opencv_core.subtract;
import static org.bytedeco.javacpp.opencv_core.theRNG;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_DIST_L2;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.circle;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.dilate;
import static org.bytedeco.javacpp.opencv_imgproc.distanceTransform;
import static org.bytedeco.javacpp.opencv_imgproc.drawContours;
import static org.bytedeco.javacpp.opencv_imgproc.filter2D;
import static org.bytedeco.javacpp.opencv_imgproc.findContours;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;
import static org.bytedeco.javacpp.opencv_imgproc.watershed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class ImageSegmentation {
    private static final int[] WHITE = {255, 255, 255};
    private static final int[] BLACK = {0, 0, 0};

    public static void main(String[] args) {
        // Load the image
        Mat src = imread(args[0]);
        // Check if everything was fine
        if (src.data().isNull())
            return;
        // Show source image
        imshow("Source Image", src);

        // Change the background from white to black, since that will help later to extract
        // better results during the use of Distance Transform
        UByteIndexer srcIndexer = src.createIndexer();
        for (int x = 0; x < srcIndexer.rows(); x++) {
            for (int y = 0; y < srcIndexer.cols(); y++) {
                int[] values = new int[3];
                srcIndexer.get(x, y, values);
                if (Arrays.equals(values, WHITE)) {
                    srcIndexer.put(x, y, BLACK);
                }
            }
        }
        // Show output image
        imshow("Black Background Image", src);

        // Create a kernel that we will use for accuting/sharpening our image
        Mat kernel = Mat.ones(3, 3, CV_32F).asMat();
        FloatIndexer kernelIndexer = kernel.createIndexer();
        kernelIndexer.put(1, 1, -8); // an approximation of second derivative, a quite strong kernel

        // do the laplacian filtering as it is
        // well, we need to convert everything in something more deeper then CV_8U
        // because the kernel has some negative values,
        // and we can expect in general to have a Laplacian image with negative values
        // BUT a 8bits unsigned int (the one we are working with) can contain values from 0 to 255
        // so the possible negative number will be truncated
        Mat imgLaplacian = new Mat();
        Mat sharp = src; // copy source image to another temporary one
        filter2D(sharp, imgLaplacian, CV_32F, kernel);
        src.convertTo(sharp, CV_32F);
        Mat imgResult = subtract(sharp, imgLaplacian).asMat();
        // convert back to 8bits gray scale
        imgResult.convertTo(imgResult, CV_8UC3);
        imgLaplacian.convertTo(imgLaplacian, CV_8UC3);
        // imshow( "Laplace Filtered Image", imgLaplacian );
        imshow("New Sharped Image", imgResult);

        src = imgResult; // copy back
        // Create binary image from source image
        Mat bw = new Mat();
        cvtColor(src, bw, CV_BGR2GRAY);
        threshold(bw, bw, 40, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);
        imshow("Binary Image", bw);

        // Perform the distance transform algorithm
        Mat dist = new Mat();
        distanceTransform(bw, dist, CV_DIST_L2, 3);
        // Normalize the distance image for range = {0.0, 1.0}
        // so we can visualize and threshold it
        normalize(dist, dist, 0, 1., NORM_MINMAX, -1, null);
        imshow("Distance Transform Image", dist);

        // Threshold to obtain the peaks
        // This will be the markers for the foreground objects
        threshold(dist, dist, .4, 1., CV_THRESH_BINARY);
        // Dilate a bit the dist image
        Mat kernel1 = Mat.ones(3, 3, CV_8UC1).asMat();
        dilate(dist, dist, kernel1);
        imshow("Peaks", dist);
        // Create the CV_8U version of the distance image
        // It is needed for findContours()
        Mat dist_8u = new Mat();
        dist.convertTo(dist_8u, CV_8U);
        // Find total markers
        MatVector contours = new MatVector();
        findContours(dist_8u, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
        // Create the marker image for the watershed algorithm
        Mat markers = Mat.zeros(dist.size(), CV_32SC1).asMat();
        // Draw the foreground markers
        for (int i = 0; i < contours.size(); i++)
            drawContours(markers, contours, i, Scalar.all((i) + 1));
        // Draw the background marker
        circle(markers, new Point(5, 5), 3, RGB(255, 255, 255));
        imshow("Markers", multiply(markers, 10000).asMat());

        // Perform the watershed algorithm
        watershed(src, markers);
        Mat mark = Mat.zeros(markers.size(), CV_8UC1).asMat();
        markers.convertTo(mark, CV_8UC1);
        bitwise_not(mark, mark);
//            imshow("Markers_v2", mark); // uncomment this if you want to see how the mark
                                    // image looks like at that point
        // Generate random colors
        List<int[]> colors = new ArrayList<int[]>();
        for (int i = 0; i < contours.size(); i++) {
            int b = theRNG().uniform(0, 255);
            int g = theRNG().uniform(0, 255);
            int r = theRNG().uniform(0, 255);
            int[] color = { b, g, r };
            colors.add(color);
        }
        // Create the result image
        Mat dst = Mat.zeros(markers.size(), CV_8UC3).asMat();
        // Fill labeled objects with random colors
        IntIndexer markersIndexer = markers.createIndexer();
        UByteIndexer dstIndexer = dst.createIndexer();
        for (int i = 0; i < markersIndexer.rows(); i++) {
            for (int j = 0; j < markersIndexer.cols(); j++) {
                int index = markersIndexer.get(i, j);
                if (index > 0 && index <= contours.size())
                    dstIndexer.put(i, j, colors.get(index - 1));
                else
                    dstIndexer.put(i, j, BLACK);
            }
        }
        // Visualize the final image
        imshow("Final Result", dst);
    }

    //I wrote a custom imshow method for problems using the OpenCV original one
    private static void imshow(String txt, Mat img) {
        CanvasFrame canvasFrame = new CanvasFrame(txt);
        canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasFrame.setCanvasSize(img.cols(), img.rows());
        canvasFrame.showImage(new OpenCVFrameConverter.ToMat().convert(img));
    }

}

