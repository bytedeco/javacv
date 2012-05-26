import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_objdetect;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 *
 * @author Samuel Audet
 */
public class FaceApplet extends Applet implements Runnable {

    private CvHaarClassifierCascade classifier;
    private CvMemStorage storage;
    private FrameGrabber grabber;
    private IplImage grabbedImage, grayImage;
    private CvSeq faces;
    private boolean stop;
    private Exception exception = null;

    @Override public void init() {
        try {
            // Load the classifier file from Java resources.
            String classiferName = "haarcascade_frontalface_alt.xml";
            File classifierFile = Loader.extractResource(classiferName, null, null, null);
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
            grabber = FrameGrabber.createDefault(0);
            grabber.setImageWidth(getWidth());
            grabber.setImageHeight(getHeight());
            grabbedImage = grayImage = null;
            faces = null;
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    @Override public void start() {
        try {
            stop = false;
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
            grabber.start();
            while (!stop) {
                grabbedImage = grabber.grab();
                if (grayImage == null) {
                    grayImage = IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
                }
                if (faces == null) {
                    cvClearMemStorage(storage);
                    cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                    faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    repaint();
                }
            }
            grabbedImage = grayImage = null; 
            grabber.stop();
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
                    g2.drawRect(r.x(), r.y(), r.width(), r.height());
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

    @Override public void destroy() {
        try {
            grabber.release();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }
}
