import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacpp.opencv_objdetect;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 *
 * @author Samuel Audet
 */
public class FaceApplet extends Applet implements Runnable {

    private CvHaarClassifierCascade classifier = null;
    private CvMemStorage storage = null;
    private FrameGrabber grabber = null;
    private IplImage grabbedImage = null, grayImage = null, smallImage = null;
    private CvSeq faces = null;
    private boolean stop = false;
    private Exception exception = null;

    @Override public void init() {
        try {
            // Load the classifier file from Java resources.
            String classiferName = "haarcascade_frontalface_alt.xml";
            File classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");
            if (classifierFile == null || classifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
            }

            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
            classifierFile.delete();
            if (classifier.isNull()) {
                throw new IOException("Could not load the classifier file.");
            }

            storage = CvMemStorage.create();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    @Override public void start() {
        try {
            new Thread(this).start();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    public void run() {
        try {
            try {
                grabber = FrameGrabber.createDefault(0);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            } catch (Exception e) {
                if (grabber != null) grabber.release();
                grabber = new OpenCVFrameGrabber(0);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            }
            grayImage  = IplImage.create(grabbedImage.width(),   grabbedImage.height(),   IPL_DEPTH_8U, 1);
            smallImage = IplImage.create(grabbedImage.width()/4, grabbedImage.height()/4, IPL_DEPTH_8U, 1);
            stop = false;
            while (!stop && (grabbedImage = grabber.grab()) != null) {
                if (faces == null) {
                    cvClearMemStorage(storage);
                    cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                    cvResize(grayImage, smallImage, CV_INTER_AREA);
                    faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    repaint();
                }
            }
            grabbedImage = grayImage = smallImage = null;
            grabber.stop();
            grabber.release();
            grabber = null;
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    @Override public void update(Graphics g) {
        paint(g);
    }

    @Override public void paint(Graphics g) {
        if (grabbedImage != null) {
            BufferedImage image = grabbedImage.getBufferedImage(2.2/grabber.getGamma());
            Graphics2D g2 = image.createGraphics();
            if (faces != null) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(2));
                int total = faces.total();
                for (int i = 0; i < total; i++) {
                    CvRect r = new CvRect(cvGetSeqElem(faces, i));
                    g2.drawRect(r.x()*4, r.y()*4, r.width()*4, r.height()*4);
                }
                faces = null;
            }
            g.drawImage(image, 0, 0, null);
        }
        if (exception != null) {
            int y = 0, h = g.getFontMetrics().getHeight();
            g.drawString(exception.toString(), 5, y += h);
            for (StackTraceElement e : exception.getStackTrace()) {
                g.drawString("        at " + e.toString(), 5, y += h);
            }
        }
    }

    @Override public void stop() {
        stop = true;
    }

    @Override public void destroy() { }
}
