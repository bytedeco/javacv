/*
 * Because I believe that examples are the easiest way how to use JavaCV, I am 
 * sending a sample based on http://dasl.mem.drexel.edu/~noahKuntz/openCVTut9.html
 *
 * burgetrm@gmail.com
 */

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_video.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class OpticalFlowTracker {
    private static final int MAX_CORNERS = 500;

    public static void main(String[] args) {
        // Load two images and allocate other structures
        IplImage imgA = cvLoadImage(
                "image0.png",
                CV_LOAD_IMAGE_GRAYSCALE);
        IplImage imgB = cvLoadImage(
                "image1.png",
                CV_LOAD_IMAGE_GRAYSCALE);

        CvSize img_sz = cvGetSize(imgA);
        int win_size = 15;

        // IplImage imgC = cvLoadImage("OpticalFlow1.png",
        // CV_LOAD_IMAGE_UNCHANGED);
        IplImage imgC = cvLoadImage(
                "image0.png",
                CV_LOAD_IMAGE_UNCHANGED);
        // Get the features for tracking
        IplImage eig_image = cvCreateImage(img_sz, IPL_DEPTH_32F, 1);
        IplImage tmp_image = cvCreateImage(img_sz, IPL_DEPTH_32F, 1);

        int[] corner_count = { MAX_CORNERS };
        CvPoint2D32f cornersA = new CvPoint2D32f(MAX_CORNERS);

        CvArr mask = null;
        cvGoodFeaturesToTrack(imgA, eig_image, tmp_image, cornersA,
                corner_count, 0.05, 5.0, mask, 3, 0, 0.04);

        cvFindCornerSubPix(imgA, cornersA, corner_count[0],
                cvSize(win_size, win_size), cvSize(-1, -1),
                cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.03));

        // Call Lucas Kanade algorithm
        byte[] features_found = new byte[MAX_CORNERS];
        float[] feature_errors = new float[MAX_CORNERS];

        CvSize pyr_sz = cvSize(imgA.width() + 8, imgB.height() / 3);

        IplImage pyrA = cvCreateImage(pyr_sz, IPL_DEPTH_32F, 1);
        IplImage pyrB = cvCreateImage(pyr_sz, IPL_DEPTH_32F, 1);

        CvPoint2D32f cornersB = new CvPoint2D32f(MAX_CORNERS);
        cvCalcOpticalFlowPyrLK(imgA, imgB, pyrA, pyrB, cornersA, cornersB,
                corner_count[0], cvSize(win_size, win_size), 5,
                features_found, feature_errors,
                cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3), 0);

        // Make an image of the results
        for (int i = 0; i < corner_count[0]; i++) {
            if (features_found[i] == 0 || feature_errors[i] > 550) {
                System.out.println("Error is " + feature_errors[i] + "/n");
                continue;
            }
            System.out.println("Got it/n");
            cornersA.position(i);
            cornersB.position(i);
            CvPoint p0 = cvPoint(Math.round(cornersA.x()),
                    Math.round(cornersA.y()));
            CvPoint p1 = cvPoint(Math.round(cornersB.x()),
                    Math.round(cornersB.y()));
            cvLine(imgC, p0, p1, CV_RGB(255, 0, 0), 
                    2, 8, 0);
        }

        cvSaveImage(
                "image0-1.png",
                imgC);
        cvNamedWindow( "LKpyr_OpticalFlow", 0 );
        cvShowImage( "LKpyr_OpticalFlow", imgC );
        cvWaitKey(0);
    }
}
