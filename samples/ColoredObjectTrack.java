/*
 * Just an example using the opencv to make a colored object tracking,
 * i adpted this code to bytedeco/javacv, i think this will help some people.
 *
 * Waldemar <waldemarnt@outlook.com>
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class ColoredObjectTrack implements Runnable {
 
    public static void main(String[] args) {
        ColoredObjectTrack cot = new ColoredObjectTrack();
        Thread th = new Thread(cot);
        th.start();
    }

    final int INTERVAL = 10;// 1sec
    final int CAMERA_NUM = 0; // Default camera for this time

    /**
     * Correct the color range- it depends upon the object, camera quality,
     * environment.
     */
    static CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide dabur birko
    static CvScalar rgba_max = cvScalar(80, 80, 255, 0);

    IplImage image;
    CanvasFrame canvas = new CanvasFrame("Web Cam Live");
    CanvasFrame path = new CanvasFrame("Detection");
    int ii = 0;
    JPanel jp = new JPanel();

    public ColoredObjectTrack() {
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        path.setContentPane(jp);
    }

    @Override
    public void run() {
        try {
            FrameGrabber grabber = FrameGrabber.createDefault(CAMERA_NUM);
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            grabber.start();
            IplImage img;
            int posX = 0;
            int posY = 0;
            while (true) {
                img = converter.convert(grabber.grab());
                if (img != null) {
                    // show image on window
                    cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise
                    canvas.showImage(converter.convert(img));
                    IplImage detectThrs = getThresholdImage(img);
 
                    CvMoments moments = new CvMoments();
                    cvMoments(detectThrs, moments, 1);
                    double mom10 = cvGetSpatialMoment(moments, 1, 0);
                    double mom01 = cvGetSpatialMoment(moments, 0, 1);
                    double area = cvGetCentralMoment(moments, 0, 0);
                    posX = (int) (mom10 / area);
                    posY = (int) (mom01 / area);
                    // only if its a valid position
                    if (posX > 0 && posY > 0) {
                        paint(img, posX, posY);
                    }
                }
                // Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
        }
    }

    private void paint(IplImage img, int posX, int posY) {
        Graphics g = jp.getGraphics();
        path.setSize(img.width(), img.height());
        // g.clearRect(0, 0, img.width(), img.height());
        g.setColor(Color.RED);
        // g.fillOval(posX, posY, 20, 20);
        g.drawOval(posX, posY, 20, 20);
        System.out.println(posX + " , " + posY);
 
    }

    private IplImage getThresholdImage(IplImage orgImg) {
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //
        cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red
 
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15,0,0,0);
        cvSaveImage(++ii + "dsmthreshold.jpg", imgThreshold);
        return imgThreshold;
    }


    public IplImage Equalize(BufferedImage bufferedimg) {
        Java2DFrameConverter converter1 = new Java2DFrameConverter();
        OpenCVFrameConverter.ToIplImage converter2 = new OpenCVFrameConverter.ToIplImage();
        IplImage iploriginal = converter2.convert(converter1.convert(bufferedimg));
        IplImage srcimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        IplImage destimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
        cvEqualizeHist(srcimg, destimg);
        return destimg;
    }
}
