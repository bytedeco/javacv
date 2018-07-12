/**
 * Kazemi Facemark example for JavaCV
 * 
 * @author Théophile Gonos
 *
 */

import java.io.IOException;
import java.net.URISyntaxException;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point2fVector;
import org.bytedeco.javacpp.opencv_core.Point2fVectorVector;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_face.FacemarkKazemi;
import static org.bytedeco.javacpp.opencv_face.drawFacemarks;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;


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
