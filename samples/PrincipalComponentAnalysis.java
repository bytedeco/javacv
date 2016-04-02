import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.tools.Slf4jLogger;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * PrincipalComponentAnalysis with JavaCV
 * https://github.com/bytedeco/javacv
 * Based on "Introduction to Principal Component Analysis (PrincipalComponentAnalysis) ":
 * http://docs.opencv.org/3.0.0/d1/dee/tutorial_introduction_to_pca.html
 *
 * @author Maurice Betzel
 */

public class PrincipalComponentAnalysis {

    static {
        System.setProperty("org.bytedeco.javacpp.logger", "slf4jlogger");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    private static final Slf4jLogger logger = (Slf4jLogger) org.bytedeco.javacpp.tools.Logger.create(PrincipalComponentAnalysis.class);

    public static void main(String[] args) {
        try {
            logger.info(String.valueOf(logger.isDebugEnabled()));
            logger.info("Start");
            new PrincipalComponentAnalysis().execute(args);
            logger.info("Stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute(String[] args) throws Exception {
        // If no params provided, compute the default image
        BufferedImage bufferedImage = args.length >= 1 ? ImageIO.read(new File(args[0])) : ImageIO.read(this.getClass().getResourceAsStream("shapes2.jpg"));
        System.out.println("Image type: " + bufferedImage.getType());
        // Convert BufferedImage to Mat and create AutoCloseable objects
        try (Mat matrix = new OpenCVFrameConverter.ToMat().convert(new Java2DFrameConverter().convert(bufferedImage));
             Mat mask = new Mat();
             Mat gray = new Mat();
             Mat denoised = new Mat();
             Mat bin = new Mat();
             Mat hierarchy = new Mat();
             MatVector contours = new MatVector()) {

            printMat(matrix);
            cvtColor(matrix, gray, COLOR_BGR2GRAY);
            //Normalize
            GaussianBlur(gray, denoised, new Size(5, 5), 0);
            threshold(denoised, mask, 0, 255, THRESH_BINARY_INV | THRESH_OTSU);
            normalize(gray, gray, 0, 255, NORM_MINMAX, -1, mask);
            // Convert image to binary
            threshold(gray, bin, 150, 255, THRESH_BINARY);
            // Find contours
            findContours(bin, contours, hierarchy, RETR_TREE, CHAIN_APPROX_NONE);
            long contourCount = contours.size();
            System.out.println("Countour count " + contourCount);

            for (int i = 0; i < contourCount; ++i) {
                // Calculate the area of each contour
                Mat contour = contours.get(i);
                double area = contourArea(contour);
                // Ignore contours that are too small or too large
                if (area > 128 && area < 8192) {
                    principalComponentAnalysis(contour, i, matrix);
                }
            }
            CanvasFrame canvas = new CanvasFrame("PrincipalComponentAnalysis", 1);
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            canvas.setCanvasSize(320, 240);
            OpenCVFrameConverter converter = new OpenCVFrameConverter.ToIplImage();
            canvas.showImage(converter.convert(matrix));
        }
    }

    // contour is a one dimensional array
    private void principalComponentAnalysis(Mat contour, int entry, Mat matrix) throws Exception {
        PCA pca_analysis = null;
        Mat mean = null;
        Mat eigenVector = null;
        Mat eigenValues = null;
        //Construct a buffer used by the pca analysis
        try (Mat data_pts = new Mat(contour.rows(), 2, CV_64FC1);
             Mat placeholder = new Mat();
             Point cntr = new Point()) {

            IntIndexer contourIndexer = contour.createIndexer();
            DoubleIndexer data_idx = data_pts.createIndexer();
            for (int i = 0; i < contour.rows(); i++) {
                data_idx.put(i, 0, contourIndexer.get(i, 0));
                data_idx.put(i, 1, contourIndexer.get(i, 1));
            }
            contourIndexer.release();
            data_idx.release();
            //Perform PrincipalComponentAnalysis analysis
            ArrayList<Point2d> eigen_vecs = new ArrayList(2);
            ArrayList<Double> eigen_val = new ArrayList(2);
            pca_analysis = new PCA(data_pts, placeholder, CV_PCA_DATA_AS_ROW);
            mean = pca_analysis.mean();
            eigenVector = pca_analysis.eigenvectors();
            eigenValues = pca_analysis.eigenvalues();
            DoubleIndexer mean_idx = mean.createIndexer();
            DoubleIndexer eigenVectorIndexer = eigenVector.createIndexer();
            DoubleIndexer eigenValuesIndexer = eigenValues.createIndexer();
            for (int i = 0; i < 2; ++i) {
                eigen_vecs.add(new Point2d(eigenVectorIndexer.get(i, 0), eigenVectorIndexer.get(i, 1)));
                eigen_val.add(eigenValuesIndexer.get(0, i));
            }
            double cntrX = mean_idx.get(0, 0);
            double cntrY = mean_idx.get(0, 1);
            mean_idx.release();
            eigenVectorIndexer.release();
            eigenValuesIndexer.release();
            double x1 = cntrX + 0.02 * (eigen_vecs.get(0).x() * eigen_val.get(0));
            double y1 = cntrY + 0.02 * (eigen_vecs.get(0).y() * eigen_val.get(0));
            double x2 = cntrX - 0.02 * (eigen_vecs.get(1).x() * eigen_val.get(1));
            double y2 = cntrY - 0.02 * (eigen_vecs.get(1).y() * eigen_val.get(1));
            // Draw the principal components, keep accuracy during calculations
            cntr.x((int) Math.rint(cntrX));
            cntr.y((int) Math.rint(cntrY));
            circle(matrix, cntr, 5, new Scalar(255, 0, 255, 0));
            double radian1 = Math.atan2(cntrY - y1, cntrX - x1);
            double radian2 = Math.atan2(cntrY - y2, cntrX - x2);
            double hypotenuse1 = Math.sqrt((cntrY - y1) * (cntrY - y1) + (cntrX - x1) * (cntrX - x1));
            double hypotenuse2 = Math.sqrt((cntrY - y2) * (cntrY - y2) + (cntrX - x2) * (cntrX - x2));
            //Enhance the vector signal by a factor of 2
            double point1x = cntrX - 2 * hypotenuse1 * Math.cos(radian1);
            double point1y = cntrY - 2 * hypotenuse1 * Math.sin(radian1);
            double point2x = cntrX - 2 * hypotenuse2 * Math.cos(radian2);
            double point2y = cntrY - 2 * hypotenuse2 * Math.sin(radian2);
            drawAxis(matrix, radian1, cntr, point1x, point1y, Scalar.BLUE);
            drawAxis(matrix, radian2, cntr, point2x, point2y, Scalar.CYAN);
        } finally {
            if(pca_analysis != null) {
                pca_analysis.deallocate();
            }
            if(mean != null) {
                mean.deallocate();
            }
            if(eigenVector != null) {
                eigenVector.deallocate();
            }
            if(eigenValues != null) {
                eigenValues.deallocate();
            }
        }
    }

    private void drawAxis(Mat matrix, double radian, Point cntr, double x, double y, Scalar colour) throws Exception {
        try(Point q = new Point((int) x, (int) y);
            Point arrowHook1 = new Point((int) (q.x() + 9 * Math.cos(radian + CV_PI / 4)), (int) (q.y() + 9 * Math.sin(radian + CV_PI / 4)));
            Point arrowHook2 = new Point((int) (q.x() + 9 * Math.cos(radian - CV_PI / 4)), (int) (q.y() + 9 * Math.sin(radian - CV_PI / 4)))) {
            // draw
            line(matrix, cntr, q, colour);
            line(matrix, arrowHook1, q, colour);
            line(matrix, arrowHook2, q, colour);
        }
    }

    public static void printMat(Mat mat) {
        System.out.println("Channels: " + mat.channels());
        System.out.println("Rows: " + mat.rows());
        System.out.println("Cols: " + mat.cols());
        System.out.println("Type: " + mat.type());
        System.out.println("Dims: " + mat.dims());
        System.out.println("Depth: " + mat.depth());
    }

}