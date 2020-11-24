/**
 * LBF Facemark example for JavaCV with Video camera and Transparent API
 * 
 * @author Théophile Gonos
 *
 * you can find the lbfmodel here:
 * https://raw.githubusercontent.com/kurnianggoro/GSOC2017/master/data/lbfmodel.yaml
 */

import java.io.IOException;
import java.net.URISyntaxException;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.opencv_highgui.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.bytedeco.opencv.opencv_videoio.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_face.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_videoio.*;

public class LBFFacemarkExampleWithVideo {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Load Face Detector
        CascadeClassifier faceDetector = new CascadeClassifier ("haarcascade_frontalface_alt2.xml");
 
        // Create an instance of Facemark
        Facemark facemark = FacemarkLBF.create();
 
        // Load landmark detector 
        facemark.loadModel("lbfmodel.yaml");
 
        // Set up webcam for video capture
        VideoCapture cam = new VideoCapture (0);
        // Variable to store a video frame and its grayscale 
        Mat frame = new Mat ();
        
        // Read a frame
        while(cam.read(frame)) {
            // convert to grayscale and equalize histograe for better detection
            // + use of transparent API
            UMat gray = new UMat ();
            frame.copyTo(gray);
            cvtColor(gray, gray, COLOR_BGR2GRAY);
            equalizeHist( gray, gray );
       
            // Find faces on the image
            RectVector faces = new RectVector ();
            faceDetector.detectMultiScale(gray, faces);
            
            System.out.println ("Faces detected: "+faces.size());
            // Verify is at least one face is detected
            // With some Facemark algorithms it crashes if there is no faces
            if (!faces.empty()) {
        
                // Variable for landmarks. 
                // Landmarks for one face is a vector of points
                // There can be more than one face in the image.
                Point2fVectorVector landmarks = new Point2fVectorVector();

                // Run landmark detector
                boolean success = facemark.fit(frame, faces, landmarks);
        
                if(success) {
                    // If successful, render the landmarks on the face
                    for (long i = 0; i < landmarks.size(); i++) {
                        Point2fVector v = landmarks.get(i);
                        drawFacemarks(frame, v, Scalar.YELLOW);
                    }
                }
            }
            // Display results 
            imshow("LBF Facial Landmark", frame);
            // Exit loop if ESC is pressed
            if (waitKey(1) == 27) break;
        }
    }
}
