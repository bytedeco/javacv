/*
 * LBF Facemark example for JavaCV with Video camera and Transparent API
 * 
 * @author Théophile Gonos
 *
 * you can find the lbfmodel here:
 * https://raw.githubusercontent.com/kurnianggoro/GSOC2017/master/data/lbfmodel.yaml
 */

import java.io.IOException;
import java.net.URISyntaxException;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point2fVector;
import org.bytedeco.javacpp.opencv_core.Point2fVectorVector;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.UMat;
import org.bytedeco.javacpp.opencv_face.Facemark;
import org.bytedeco.javacpp.opencv_face.FacemarkLBF;
import static org.bytedeco.javacpp.opencv_face.drawFacemarks;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

/**
 *
 * @author theo
 */
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
                boolean success = facemark.fit(frame, faces, landmarks, null);
        
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
