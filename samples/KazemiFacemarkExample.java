/**
 * Kazemi Facemark example for JavaCV
 * 
 * @author Théophile Gonos
 *
 * Link to Kazemi model : 
 * https://raw.githubusercontent.com/opencv/opencv_3rdparty/contrib_face_alignment_20170818/face_landmark_model.dat
 */

import java.io.IOException;
import java.net.URISyntaxException;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.opencv_highgui.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_face.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;


public class KazemiFacemarkExample {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Load Face Detector
        CascadeClassifier faceDetector = new CascadeClassifier ("haarcascade_frontalface_alt2.xml");
 
        // Create an instance of Facemark
        FacemarkKazemi facemark = FacemarkKazemi.create();
 
        // Load landmark detector 
        facemark.loadModel("face_landmark_model.dat");
 
        // Load image
        Mat img = imread("face.jpg");
        
        // convert to grayscale and equalize histograe for better detection
        Mat gray = new Mat ();
        cvtColor(img, gray, COLOR_BGR2GRAY);
        equalizeHist( gray, gray );
       
        // Find faces on the image
        RectVector faces = new RectVector ();
        faceDetector.detectMultiScale(gray, faces);
        
        System.out.println ("Faces detected: "+faces.size());
        // Variable for landmarks. 
        // Landmarks for one face is a vector of points
        // There can be more than one face in the image.
        Point2fVectorVector landmarks = new Point2fVectorVector();

        // Run landmark detector
        boolean success = facemark.fit(img, faces, landmarks);
        
        if(success) {
            // If successful, render the landmarks on each face
            for (long i = 0; i < landmarks.size(); i++) {
                Point2fVector v = landmarks.get(i);
                drawFacemarks(img, v, Scalar.YELLOW);
            }
        }

        // Display results 
        imshow("Kazemi Facial Landmark", img);
        cvWaitKey(0);
        // Save results
        imwrite ("kazemi_landmarks.jpg", img);
    }
}
