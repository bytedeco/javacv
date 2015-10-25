JavaCV
======

Introduction
------------
JavaCV uses wrappers from the [JavaCPP Presets](https://github.com/bytedeco/javacpp-presets) of commonly used libraries by researchers in the field of computer vision ([OpenCV](http://opencv.org/), [FFmpeg](http://ffmpeg.org/), [libdc1394](http://damien.douxchamps.net/ieee1394/libdc1394/), [PGR FlyCapture](http://www.ptgrey.com/products/pgrflycapture/), [OpenKinect](http://openkinect.org/), [videoInput](http://muonics.net/school/spring05/videoInput/), [ARToolKitPlus](http://studierstube.icg.tugraz.at/handheld_ar/artoolkitplus.php), and [flandmark](http://cmp.felk.cvut.cz/~uricamic/flandmark/)), and provides utility classes to make their functionality easier to use on the Java platform, including Android.

JavaCV also comes with hardware accelerated full-screen image display (`CanvasFrame` and `GLCanvasFrame`), easy-to-use methods to execute code in parallel on multiple cores (`Parallel`), user-friendly geometric and color calibration of cameras and projectors (`GeometricCalibrator`, `ProCamGeometricCalibrator`, `ProCamColorCalibrator`), detection and matching of feature points (`ObjectFinder`), a set of classes that implement direct image alignment of projector-camera systems (mainly `GNImageAligner`, `ProjectiveTransformer`, `ProjectiveColorTransformer`, `ProCamTransformer`, and `ReflectanceInitializer`), a blob analysis package (`Blobs`), as well as miscellaneous functionality in the `JavaCV` class. Some of these classes also have an OpenCL and OpenGL counterpart, their names ending with `CL` or starting with `GL`, i.e.: `JavaCVCL`, `GLCanvasFrame`, etc.

To learn how to use the API, since documentation currently lacks, please refer to the [Sample Usage](#sample-usage) section below as well as the [sample programs](https://github.com/bytedeco/javacv/tree/master/samples/), including two for Android (`FacePreview.java` and `RecordActivity.java`), also found in the `samples` directory. You may also find it useful to refer to the source code of [ProCamCalib](https://github.com/bytedeco/procamcalib) and [ProCamTracker](https://github.com/bytedeco/procamtracker) as well as [examples ported from OpenCV2 Cookbook](https://github.com/bytedeco/javacv-examples/) and the associated [wiki pages](https://github.com/bytedeco/javacv-examples/blob/master/OpenCV2_Cookbook).

Please keep me informed of any updates or fixes you make to the code so that I may integrate them into the next release. Thank you! And feel free to ask questions on [the mailing list](http://groups.google.com/group/javacv) if you encounter any problems with the software! I am sure it is far from perfect...


Downloads
---------
To install manually the JAR files, obtain the following archives and follow the instructions in the [Manual Installation](#manual-installation) section below.

 * JavaCV 1.1 binary archive  [javacv-1.1-bin.zip](http://search.maven.org/remotecontent?filepath=org/bytedeco/javacv/1.1/javacv-1.1-bin.zip) (140 MB)
 * JavaCV 1.1 source archive  [javacv-1.1-src.zip](http://search.maven.org/remotecontent?filepath=org/bytedeco/javacv/1.1/javacv-1.1-src.zip) (415 KB)

The binary archive contains builds for Android, Linux, Mac OS X, and Windows. The JAR files for specific child modules or platforms can also be obtained individually from the [Maven Central Repository](http://search.maven.org/#search|ga|1|bytedeco).


We can also have everything downloaded and installed automatically with:

 * Maven (inside the `pom.xml` file)
```xml
  <dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>javacv</artifactId>
    <version>1.1</version>
  </dependency>
```

 * Gradle (inside the `build.gradle` file)
```groovy
  dependencies {
    compile group: 'org.bytedeco', name: 'javacv', version: '1.1'
  }
```

 * sbt (inside the `build.sbt` file)
```scala
  classpathTypes += "maven-plugin"
  libraryDependencies += "org.bytedeco" % "javacv" % "1.1"
```

Additionally, we need to either set the `platform` system property (via the `-D` command line option) to something like `android-arm`, or set the `platform.dependencies` one to `true` to get all the binaries for Android, Linux, Mac OS X, and Windows. **On build systems where this does not work, we need to add the platform-specific artifacts manually.**


Required Software
-----------------
To use JavaCV, you will first need to download and install the following software:

 * An implementation of Java SE 7 or newer:
   * OpenJDK  http://openjdk.java.net/install/  or
   * Sun JDK  http://www.oracle.com/technetwork/java/javase/downloads/  or
   * IBM JDK  http://www.ibm.com/developerworks/java/jdk/

Further, although not always required, some functionality of JavaCV also relies on:

 * CL Eye Platform SDK (Windows only)  http://codelaboratories.com/downloads/
 * Android SDK API 14 or newer  http://developer.android.com/sdk/
 * JOCL and JOGL from JogAmp  http://jogamp.org/

Finally, please make sure everything has the same bitness: **32-bit and 64-bit modules do not mix under any circumstances**.


Manual Installation
-------------------
Simply put all the desired JAR files (`opencv*.jar`, `ffmpeg*.jar`, etc.), in addition to `javacpp.jar` and `javacv.jar`, somewhere in your class path. Here are some more specific instructions for common cases:

NetBeans (Java SE 7 or newer):

 1. In the Projects window, right-click the Libraries node of your project, and select "Add JAR/Folder...".
 2. Locate the JAR files, select them, and click OK.

Eclipse (Java SE 7 or newer):

 1. Navigate to Project > Properties > Java Build Path > Libraries and click "Add External JARs...".
 2. Locate the JAR files, select them, and click OK.

IntelliJ IDEA (Android 4.0 or newer):

 1. Follow the instructions on this page: http://developer.android.com/training/basics/firstapp/
 2. Copy all the JAR files into the `app/libs` subdirectory.
 3. Navigate to File > Project Structure > app > Dependencies, click `+`, and select "2 File dependency".
 4. Select all the JAR files from the `libs` subdirectory.

After that, the wrapper classes for OpenCV and FFmpeg, for example, can automatically access all of their C/C++ APIs:

 * [OpenCV documentation](http://docs.opencv.org/master/)
 * [FFmpeg documentation](http://ffmpeg.org/doxygen/trunk/)


Sample Usage
------------
The class definitions are basically ports to Java of the original header files in C/C++, and I deliberately decided to keep as much of the original syntax as possible. For example, here is a method that tries to load an image file, smooth it, and save it back to disk:

```java
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

public class Smoother {
    public static void smooth(String filename) { 
        IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSmooth(image, image);
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
import org.bytedeco.javacpp.indexer.*;
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
            URL url = new URL("https://raw.github.com/Itseez/opencv/2.4.0/data/haarcascades/haarcascade_frontalface_alt.xml");
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

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, or OpenCV).
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        // FAQ about IplImage and Mat objects from OpenCV:
        // - For custom raw processing of data, createBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
        //   Java2DFrameConverter and OpenCVFrameConverter, one after the other.
        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
        //   data more directly between BufferedImage and IplImage or Mat via Frame objects.
        IplImage grabbedImage = converter.convert(grabber.grab());
        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        IplImage rotatedImage = grabbedImage.clone();

        // Objects allocated with a create*() or clone() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling release().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        CvMemStorage storage = CvMemStorage.create();

        // The OpenCVFrameRecorder class simply uses the CvVideoWriter of opencv_videoio,
        // but FFmpegFrameRecorder also exists as a more versatile alternative.
        FrameRecorder recorder = FrameRecorder.createDefault("output.avi", width, height);
        recorder.start();

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma()/grabber.getGamma());

        // Let's create some random 3D rotation...
        CvMat randomR = CvMat.create(3, 3), randomAxis = CvMat.create(3, 1);
        // We can easily and efficiently access the elements of matrices and images
        // through an Indexer object with the set of get() and put() methods.
        DoubleIndexer Ridx = randomR.createIndexer(), axisIdx = randomAxis.createIndexer();
        axisIdx.put(0, (Math.random()-0.5)/4, (Math.random()-0.5)/4, (Math.random()-0.5)/4);
        cvRodrigues2(randomAxis, randomR, null);
        double f = (width + height)/2.0;  Ridx.put(0, 2, Ridx.get(0, 2)*f);
                                          Ridx.put(1, 2, Ridx.get(1, 2)*f);
        Ridx.put(2, 0, Ridx.get(2, 0)/f); Ridx.put(2, 1, Ridx.get(2, 1)/f);
        System.out.println(Ridx);

        // We can allocate native arrays using constructors taking an integer as argument.
        CvPoint hatPoints = new CvPoint(3);

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            cvClearMemStorage(storage);

            // Let's try to detect some faces! but we need a grayscale image...
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage,
                    1.1, 3, CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
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

            Frame rotatedFrame = converter.convert(rotatedImage);
            frame.showImage(rotatedFrame);
            recorder.record(rotatedFrame);
        }
        frame.dispose();
        recorder.stop();
        grabber.stop();
    }
}
```

Furthermore, after creating a `pom.xml` file with the following content:
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.bytedeco.javacv</groupId>
    <artifactId>demo</artifactId>
    <version>1.1</version>
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv</artifactId>
            <version>1.1</version>
        </dependency>
    </dependencies>
</project>
```

And by placing the source code above in `src/main/java/Demo.java`, we can use the following command to have everything first installed automatically and then executed by Maven:
```bash
 $ mvn package exec:java -Dexec.mainClass=Demo
```


Build Instructions
------------------
If the binary files available above are not enough for your needs, you might need to rebuild them from the source code. To this end, the project files were created for:

 * Maven 3.x  http://maven.apache.org/download.html
 * JavaCPP 1.1  https://github.com/bytedeco/javacpp
 * JavaCPP Presets 1.1  https://github.com/bytedeco/javacpp-presets

Once installed, simply call the usual `mvn install` command for JavaCPP, its Presets, and JavaCV. By default, no other dependencies than a C++ compiler for JavaCPP are required. Please refer to the comments inside the `pom.xml` files for further details.


----
Project lead: Samuel Audet [samuel.audet `at` gmail.com](mailto:samuel.audet at gmail.com)  
Developer site: https://github.com/bytedeco/javacv  
Discussion group: http://groups.google.com/group/javacv
