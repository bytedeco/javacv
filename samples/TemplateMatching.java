package matching;
import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

/**
 * Example of template javacv (opencv) template matching using the last java build
 *
 * We need 2 default parameters like this (source image, image to find )
 * "C:\Users\Waldema\Desktop\bg.jpg" "C:\Users\Waldema\Desktop\imageToFind.jpg" 
 *
 * @author Waldemar Neto
 */
public class TemplateMatching {

    public static void main(String[] args) throws Exception {
        //get color source image to draw red rect on later
        IplImage srcColor = cvLoadImage(args[0]);
        //create blank 1 channel image same size as the source 
        IplImage src = cvCreateImage(cvGetSize(srcColor), IPL_DEPTH_8U, 1);
        //convert source to grey and copy to src
        cvCvtColor(srcColor, src, CV_BGR2GRAY);
        //get the image to match loaded in greyscale. 
        IplImage tmp = cvLoadImage(args[1], 0);
        //this image will hold the strength of the match
        //as the template is translated across the image 
        IplImage result = cvCreateImage(
                cvSize(src.width() - tmp.width() + 1,
                        src.height() - tmp.height() + 1), IPL_DEPTH_32F, src.nChannels());

        cvZero(result);

        // Match Template Function from OpenCV
        cvMatchTemplate(src, tmp, result, CV_TM_CCORR_NORMED);

        // double[] min_val = new double[2];
        // double[] max_val = new double[2];
        DoublePointer min_val = new DoublePointer();
        DoublePointer max_val = new DoublePointer();

        CvPoint minLoc = new CvPoint();
        CvPoint maxLoc = new CvPoint();

        cvMinMaxLoc(result, min_val, max_val, minLoc, maxLoc, null);

        // Get the Max or Min Correlation Value
        // System.out.println(Arrays.toString(min_val));
        // System.out.println(Arrays.toString(max_val));

        CvPoint point = new CvPoint();
        point.x(maxLoc.x() + tmp.width());
        point.y(maxLoc.y() + tmp.height());
        // cvMinMaxLoc(src, min_val, max_val,0,0,result);

        cvRectangle(srcColor, maxLoc, point, CvScalar.RED, 2, 8, 0); // Draw a
                                                                // Rectangle for
                                                                // Matched
                                                                // Region

        cvShowImage("Lena Image", srcColor);
        cvWaitKey(0);
        cvReleaseImage(srcColor);
        cvReleaseImage(src);
        cvReleaseImage(tmp);
        cvReleaseImage(result);
    }
}
