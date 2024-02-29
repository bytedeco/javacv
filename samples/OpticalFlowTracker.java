/*
 * Because I believe that examples are the easiest way how to use JavaCV, I am 
 * sending a sample based on http://dasl.mem.drexel.edu/~noahKuntz/openCVTut9.html
 *
 * burgetrm@gmail.com
 */

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_highgui.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_video.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_video.*;

public class OpticalFlowTracker {
    private static final int MAX_CORNERS = 500;
    private static final int win_size = 15;

    public static void main(String[] args) {
        // Load two images and allocate other structures
        Mat imgA = imread(
                "image0.png",
                IMREAD_GRAYSCALE);
        Mat imgB = imread(
                "image1.png",
                IMREAD_GRAYSCALE);

        // Mat imgC = imread("OpticalFlow1.png",
        // IMREAD_UNCHANGED);
        Mat imgC = imread(
                "image0.png",
                IMREAD_UNCHANGED);

        // Get the features for tracking
        Mat cornersA = new Mat();
        goodFeaturesToTrack(imgA, cornersA, MAX_CORNERS,
                0.05, 5.0, null, 3, false, 0.04);

        cornerSubPix(imgA, cornersA,
                new Size(win_size, win_size), new Size(-1, -1),
                new TermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.03));

        // Call Lucas Kanade algorithm
        Mat features_found = new Mat();
        Mat feature_errors = new Mat();

        Mat cornersB = new Mat();
        calcOpticalFlowPyrLK(imgA, imgB, cornersA, cornersB,
                features_found, feature_errors, new Size(win_size, win_size), 5,
                new TermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3), 0, 1e-4);

        // Make an image of the results
        FloatIndexer cornersAidx = cornersA.createIndexer();
        FloatIndexer cornersBidx = cornersB.createIndexer();
        UByteIndexer features_found_idx = features_found.createIndexer();
        FloatIndexer feature_errors_idx = feature_errors.createIndexer();
        for (int i = 0; i < cornersAidx.size(0); i++) {
            if (features_found_idx.get(i) == 0 || feature_errors_idx.get(i) > 550) {
                System.out.println("Error is " + feature_errors_idx.get(i) + "/n");
                continue;
            }
            System.out.println("Got it/n");
            Point p0 = new Point(Math.round(cornersAidx.get(i, 0)),
                    Math.round(cornersAidx.get(i, 1)));
            Point p1 = new Point(Math.round(cornersBidx.get(i, 0)),
                    Math.round(cornersBidx.get(i, 1)));
            line(imgC, p0, p1, RGB(255, 0, 0),
                    2, 8, 0);
        }

        imwrite(
                "image0-1.png",
                imgC);
        namedWindow("LKpyr_OpticalFlow", 0);
        imshow("LKpyr_OpticalFlow", imgC);
        waitKey(0);
    }
}
