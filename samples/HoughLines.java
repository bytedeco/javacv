import javax.swing.JFrame;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

/**
 * C to Java translation of the houghlines.c sample provided in the c sample directory of OpenCV 2.1,
 * using the JavaCV Java wrapper of OpenCV 2.2 developped by Samuel Audet.
 *
 * @author Jeremy Nicola
 * jeremy.nicola@gmail.com
 */
public class HoughLines {

    /**
     * usage: java HoughLines imageDir\imageName TransformType
     */
    public static void main(String[] args) {

        String fileName = args.length >= 1 ? args[0] : "pic1.png"; // if no params provided, compute the defaut image
        IplImage src = cvLoadImage(fileName, 0);
        IplImage dst;
        IplImage colorDst;
        CvMemStorage storage = cvCreateMemStorage(0);
        CvSeq lines = new CvSeq();

        CanvasFrame source = new CanvasFrame("Source");
        CanvasFrame hough = new CanvasFrame("Hough");
        OpenCVFrameConverter.ToIplImage sourceConverter = new OpenCVFrameConverter.ToIplImage();
        OpenCVFrameConverter.ToIplImage houghConverter = new OpenCVFrameConverter.ToIplImage();
        if (src == null) {
            System.out.println("Couldn't load source image.");
            return;
        }

        dst = cvCreateImage(cvGetSize(src), src.depth(), 1);
        colorDst = cvCreateImage(cvGetSize(src), src.depth(), 3);

        cvCanny(src, dst, 50, 200, 3);
        cvCvtColor(dst, colorDst, CV_GRAY2BGR);

        /*
         * apply the probabilistic hough transform
         * which returns for each line deteced two points ((x1, y1); (x2,y2))
         * defining the detected segment
         */
        if (args.length == 2 && args[1].contentEquals("probabilistic")) { 
            System.out.println("Using the Probabilistic Hough Transform");
            lines = cvHoughLines2(dst, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 40, 50, 10, 0, CV_PI);
            for (int i = 0; i < lines.total(); i++) {
                // Based on JavaCPP, the equivalent of the C code:
                // CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
                // CvPoint first=line[0], second=line[1]
                // is:
                Pointer line = cvGetSeqElem(lines, i);
                CvPoint pt1  = new CvPoint(line).position(0);
                CvPoint pt2  = new CvPoint(line).position(1);

                System.out.println("Line spotted: ");
                System.out.println("\t pt1: " + pt1);
                System.out.println("\t pt2: " + pt2);
                cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0); // draw the segment on the image
            }
        }
        /*
         * Apply the multiscale hough transform which returns for each line two float parameters (rho, theta)
         * rho: distance from the origin of the image to the line
         * theta: angle between the x-axis and the normal line of the detected line
         */
        else if(args.length==2 && args[1].contentEquals("multiscale")){
                        System.out.println("Using the multiscale Hough Transform"); //
            lines = cvHoughLines2(dst, storage, CV_HOUGH_MULTI_SCALE, 1, Math.PI / 180, 40, 1, 1, 0, CV_PI);
            for (int i = 0; i < lines.total(); i++) {
                CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));

                float rho=point.x();
                float theta=point.y();

                double a = Math.cos((double) theta), b = Math.sin((double) theta);
                double x0 = a * rho, y0 = b * rho;
                CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), (int) Math.round(y0 + 1000 * (a))), pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), (int) Math.round(y0 - 1000 * (a)));
                System.out.println("Line spoted: ");
                System.out.println("\t rho= " + rho);
                System.out.println("\t theta= " + theta);
                cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
            }
        }
        /*
         * Default: apply the standard hough transform. Outputs: same as the multiscale output.
         */
        else {
            System.out.println("Using the Standard Hough Transform");
            lines = cvHoughLines2(dst, storage, CV_HOUGH_STANDARD, 1, Math.PI / 180, 90, 0, 0, 0, CV_PI);
            for (int i = 0; i < lines.total(); i++) {
                CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));

                float rho=point.x();
                float theta=point.y();

                double a = Math.cos((double) theta), b = Math.sin((double) theta);
                double x0 = a * rho, y0 = b * rho;
                CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), (int) Math.round(y0 + 1000 * (a))), pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), (int) Math.round(y0 - 1000 * (a)));
                System.out.println("Line spotted: ");
                System.out.println("\t rho= " + rho);
                System.out.println("\t theta= " + theta);
                cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
            }
        }
        source.showImage(sourceConverter.convert(src));
        hough.showImage(houghConverter.convert(colorDst));

        source.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hough.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
