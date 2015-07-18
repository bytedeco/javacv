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
 * We need 4 default parameters like this
 * "C:\Users\Waldema\Desktop\bg.jpg" "C:\Users\Waldema\Desktop\logosiemens.jpg" "C:\Users\Waldema\Desktop\imageToFind.jpg" 100 200
 *
 * @author Waldemar Neto
 */
public class TemplateMatching {

    public static void main(String[] args) throws Exception {
        int width = Integer.parseInt(args[3]);
        int height = Integer.parseInt(args[4]);

        IplImage src = cvLoadImage(
                args[0], 0);
        IplImage tmp = cvLoadImage(
                args[1], 0);

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

        cvRectangle(src, maxLoc, point, CvScalar.RED, 2, 8, 0); // Draw a
                                                                // Rectangle for
                                                                // Matched
                                                                // Region
        CvRect rect = new CvRect();
        rect.x(maxLoc.x());
        rect.y(maxLoc.y());
        rect.width(tmp.width() + width);
        rect.height(tmp.width() + height);
        cvSetImageROI(src, rect);
        IplImage imageNew = cvCreateImage(cvGetSize(src), src.depth(),
                src.nChannels());
        cvCopy(src, imageNew);
        cvSaveImage(args[2], imageNew);

        cvShowImage("Lena Image", src);
        cvWaitKey(0);
        cvReleaseImage(src);
        cvReleaseImage(tmp);
        cvReleaseImage(result);
    }
}

