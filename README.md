JavaCV
======

Introduction
------------
JavaCV uses wrappers from the [JavaCPP Presets](https://github.com/bytedeco/javacpp-presets) of commonly used libraries by researchers in the field of computer vision ([OpenCV](http://opencv.org/), [FFmpeg](http://ffmpeg.org/), [libdc1394](http://damien.douxchamps.net/ieee1394/libdc1394/), [PGR FlyCapture](http://www.ptgrey.com/products/pgrflycapture/), [OpenKinect](http://openkinect.org/), [videoInput](http://muonics.net/school/spring05/videoInput/), and [ARToolKitPlus](http://studierstube.icg.tugraz.at/handheld_ar/artoolkitplus.php)), and provides utility classes to make their functionality easier to use on the Java platform, including Android.

JavaCV also comes with hardware accelerated full-screen image display (`CanvasFrame` and `GLCanvasFrame`), easy-to-use methods to execute code in parallel on multiple cores (`Parallel`), user-friendly geometric and color calibration of cameras and projectors (`GeometricCalibrator`, `ProCamGeometricCalibrator`, `ProCamColorCalibrator`), detection and matching of feature points (`ObjectFinder`), a set of classes that implement direct image alignment of projector-camera systems (mainly `GNImageAligner`, `ProjectiveTransformer`, `ProjectiveColorTransformer`, `ProCamTransformer`, and `ReflectanceInitializer`), a blob analysis package (`Blobs`), as well as miscellaneous functionality in the `JavaCV` class. Some of these classes also have an OpenCL and OpenGL counterpart, their names ending with `CL` or starting with `GL`, i.e.: `JavaCVCL`, `GLCanvasFrame`, etc.

To learn how to use the API, since documentation currently lacks, please refer to the [Quick Start for OpenCV and FFmpeg](#quick-start-for-opencv-and-ffmpeg) section below as well as the [sample programs](https://github.com/bytedeco/javacv/tree/master/samples/), including two for Android (`FacePreview.java` and `RecordActivity.java`), also found in the `samples` directory. You may also find it useful to refer to the source code of [ProCamCalib](https://github.com/bytedeco/procamcalib) and [ProCamTracker](https://github.com/bytedeco/procamtracker) as well as [Examples ported from OpenCV2 Cookbook](http://code.google.com/p/javacv/source/browse?repo=examples) and the associated [Wiki pages](http://code.google.com/p/javacv/wiki/OpenCV2_Cookbook_Examples).

Please keep me informed of any updates or fixes you make to the code so that I may integrate them into the next release. Thank you! And feel free to ask questions on [the mailing list](http://groups.google.com/group/javacv) if you encounter any problems with the software! I am sure it is far from perfect...


Downloads
---------
 * JavaCV 0.8 binary package  [javacv-0.8-bin.zip](http://search.maven.org/remotecontent?filepath=org/bytedeco/javacv/0.8/javacv-0.8-bin.zip)
 * JavaCV 0.8 source package  [javacv-0.8-src.zip](http://search.maven.org/remotecontent?filepath=org/bytedeco/javacv/0.8/javacv-0.8-src.zip)

The binary package contains builds for Linux, Mac OS X, Windows, and Android.


Required Software
-----------------
To use JavaCV, you will first need to download and install the following software:

 * An implementation of Java SE 6 or newer
   * OpenJDK  http://openjdk.java.net/install/  or
   * Sun JDK  http://www.oracle.com/technetwork/java/javase/downloads/  or
   * IBM JDK  http://www.ibm.com/developerworks/java/jdk/  or
   * Java SE for Mac OS X  http://developer.apple.com/java/  etc.

Further, although not always required, some functionality of JavaCV also relies on:

 * CL Eye Platform SDK (Windows only)  http://codelaboratories.com/downloads/
 * Android SDK API 8 or newer  http://developer.android.com/sdk/
 * JOCL and JOGL from JogAmp  http://jogamp.org/

Finally, please make sure everything has the same bitness: **32-bit and 64-bit modules do not mix under any circumstances**.


Build Instructions
------------------
To rebuild the source code, please note that the project files were created for:

 * Maven 2 or 3  http://maven.apache.org/download.html
 * JavaCPP 0.8  https://github.com/bytedeco/javacpp
 * JavaCPP Presets 0.8  https://github.com/bytedeco/javacpp-presets

Once installed, simply call the usual `mvn install` command for JavaCPP, its Presets, and JavaCV. By default, all the dependencies listed above are NOT required, except for OpenCV and a C++ compiler for JavaCPP. Please refer to the comments inside the `pom.xml` files for further details.


Quick Start for OpenCV and FFmpeg
---------------------------------
Simply put all the JAR files of JavaCPP, JavaCV, OpenCV, and FFmpeg (`javacpp.jar`, `javacv.jar`, `opencv-*.jar`, and `ffmpeg-*.jar`, respectively) somewhere in your CLASSPATH, or point your build file to the [Maven Central Repository](http://search.maven.org/). Here are some more specific instructions for common cases:

NetBeans (Java SE 6 or newer):

 1. In the Projects window, right-click the Libraries node of your project, and select "Add JAR/Folder...".
 2. Locate the JAR files, select them, and click OK.

Eclipse (Java SE 6 or newer):

 1. Navigate to Project > Properties > Java Build Path > Libraries and click "Add External JARs...".
 2. Locate the JAR files, select them, and click OK.

Eclipse (Android 2.2 or newer):

 1. Follow the instructions on this page: http://developer.android.com/training/basics/firstapp/
 2. Go to File > New > Folder, select your project as parent folder, type "libs/armeabi" as Folder name, and click Finish.
 3. Copy `javacpp.jar`, `javacv.jar`, `opencv.jar`, and `ffmpeg.jar` into the newly created "libs" folder.
 4. Extract all the `*.so` files from `opencv-android-arm.jar` and `ffmpeg-android-arm.jar` directly into the newly created "libs/armeabi" folder, without creating any of the subdirectories found in the JAR files.
 5. Navigate to Project > Properties > Java Build Path > Libraries and click "Add JARs...".
 6. Select all of `javacpp.jar`, `javacv.jar`, `opencv.jar`, and `ffmpeg.jar` from the newly created "libs" folder.

After that, the wrapper classes for OpenCV and FFmpeg can automatically access all of their C/C++ APIs:

 * [OpenCV documentation](http://docs.opencv.org/)
 * [FFmpeg documentation](http://ffmpeg.org/doxygen/)

The class definitions are basically ports to Java of the original header files in C/C++, and I deliberately decided to keep as much of the original syntax as possible. For example, here is a method that tries to load an image file, smooth it, and save it back to disk:

```java
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class Smoother {
    public static void smooth(String filename) { 
        IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSmooth(image, image, CV_GAUSSIAN, 3);
            cvSaveImage(filename, image);
            cvReleaseImage(image);
        }
    }
}
```

JavaCV also comes with helper classes and methods on top of OpenCV and FFmpeg to facilitate their integration to the Java platform. Here is a small demo program demonstrating the most frequently useful parts:

```java
import java.io.File;
import java.net.URL;
import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class Demo {
    public static void main(String[] args) throws Exception {
        String classifierName = null;
        if (args.length > 0) {
            classifierName = args[0];
        } else {
            URL url = new URL("https://raw.github.com/Itseez/opencv/2.4/data/haarcascades/haarcascade_frontalface_alt.xml");
            File file = Loader.extractResource(url, null, "classifier", ".xml");
            file.deleteOnExit();
            classifierName = file.getAbsolutePath();
        }

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_highgui),
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        // FAQ about IplImage:
        // - For custom raw processing of data, getByteBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, we may call getBufferedImage().
        // - The createFrom() factory method can construct an IplImage from a BufferedImage.
        // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.
        IplImage grabbedImage = grabber.grab();
        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        IplImage rotatedImage = grabbedImage.clone();

        // Objects allocated with a create*() or clone() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling release().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        CvMemStorage storage = CvMemStorage.create();

        // The OpenCVFrameRecorder class simply uses the CvVideoWriter of opencv_highgui,
        // but FFmpegFrameRecorder also exists as a more versatile alternative.
        FrameRecorder recorder = FrameRecorder.createDefault("output.avi", width, height);
        recorder.start();

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma()/grabber.getGamma());

        // Let's create some random 3D rotation...
        CvMat randomR = CvMat.create(3, 3), randomAxis = CvMat.create(3, 1);
        // We can easily and efficiently access the elements of CvMat objects
        // with the set of get() and put() methods.
        randomAxis.put((Math.random()-0.5)/4, (Math.random()-0.5)/4, (Math.random()-0.5)/4);
        cvRodrigues2(randomAxis, randomR, null);
        double f = (width + height)/2.0;        randomR.put(0, 2, randomR.get(0, 2)*f);
                                                randomR.put(1, 2, randomR.get(1, 2)*f);
        randomR.put(2, 0, randomR.get(2, 0)/f); randomR.put(2, 1, randomR.get(2, 1)/f);
        System.out.println(randomR);

        // We can allocate native arrays using constructors taking an integer as argument.
        CvPoint hatPoints = new CvPoint(3);

        while (frame.isVisible() && (grabbedImage = grabber.grab()) != null) {
            cvClearMemStorage(storage);

            // Let's try to detect some faces! but we need a grayscale image...
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
                    1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            int total = faces.total();
            for (int i = 0; i < total; i++) {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.RED, 1, CV_AA, 0);

                // To access or pass as argument the elements of a native array, call position() before.
                hatPoints.position(0).x(x-w/10)   .y(y-h/10);
                hatPoints.position(1).x(x+w*11/10).y(y-h/10);
                hatPoints.position(2).x(x+w/2)    .y(y-h/2);
                cvFillConvexPoly(grabbedImage, hatPoints.position(0), 3, CvScalar.GREEN, CV_AA, 0);
            }

            // Let's find some contours! but first some thresholding...
            cvThreshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            // To check if an output argument is null we may call either isNull() or equals(null).
            CvSeq contour = new CvSeq(null);
            cvFindContours(grayImage, storage, contour, Loader.sizeof(CvContour.class),
                    CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            while (contour != null && !contour.isNull()) {
                if (contour.elem_size() > 0) {
                    CvSeq points = cvApproxPoly(contour, Loader.sizeof(CvContour.class),
                            storage, CV_POLY_APPROX_DP, cvContourPerimeter(contour)*0.02, 0);
                    cvDrawContours(grabbedImage, points, CvScalar.BLUE, CvScalar.BLUE, -1, 1, CV_AA);
                }
                contour = contour.h_next();
            }

            cvWarpPerspective(grabbedImage, rotatedImage, randomR);

            frame.showImage(rotatedImage);
            recorder.record(rotatedImage);
        }
        frame.dispose();
        recorder.stop();
        grabber.stop();
    }
}
```


----
Original author: Samuel Audet &lt;samuel.audet `at` gmail.com&gt;  
Project site: https://github.com/bytedeco/javacv  
Discussion group: http://groups.google.com/group/javacv

Licensed under the GNU General Public License version 2 (GPLv2) **with Classpath exception**.  
Please refer to LICENSE.txt or http://www.gnu.org/software/classpath/license.html for details.
