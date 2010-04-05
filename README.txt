=JavaCV=

==Introduction==
JavaCV first provides wrappers to commonly used libraries by researchers in the field of computer vision: OpenCV, ARToolKitPlus, libdc1394 2.x, PGR FlyCapture, and FFmpeg. Moreover, utility classes make it easy to use their functionality on the Java platform. JavaCV also comes with hardware accelerated fullscreen image display (`CanvasFrame`), easy-to-use methods to execute code in parallel on multiple cores (`Parallel`), user-friendly geometric and color calibration of cameras and projectors (`GeometricCalibrator`, `ProCamGeometricCalibrator`, `ProCamColorCalibrator`), detection and matching of feature points (`ObjectFinder`), a set of classes that implement direct image alignment of projector-camera systems (mainly `LMImageAligner`, `ProjectiveTransformer`, `ProjectiveGainBiasTransformer`, `ProCamTransformer`, and `ReflectanceInitializer`), as well as miscellaneous functionality in the `JavaCV` class.

To learn how to use the API, please refer to the Quick Start section below as well as the source code of ProCamCalib and ProCamTracker as documentation currently lacks.

I will continue as I go to add all code that I am developing as part of my PhD research.


==Required Software==
To use JavaCV, you will need to download and install the following software:
 * An implementation of Java SE 6
  * OpenJDK 6  http://openjdk.java.net/install/  or
  * Sun JDK 6  http://java.sun.com/javase/downloads/  or
  * IBM JDK 6  http://www.ibm.com/developerworks/java/jdk/  or
  * Java SE 6 for Mac OS X  http://developer.apple.com/java/  etc.
 * OpenCV 1.0, 1.1pre1, or 2.0  http://sourceforge.net/projects/opencvlibrary/files/
 * Java Native Access 3.2.4  https://jna.dev.java.net/

Further, although not always required, some functionality of JavaCV will also use:
 * libdc1394 2.1.2 (Linux and Mac OS X)  http://sourceforge.net/projects/libdc1394/files/
 * PGR FlyCapture 1 or 2 (Windows only)  http://www.ptgrey.com/products/pgrflycapture/
 * ARToolKitPlus 2.1.1c  http://code.google.com/p/javacv/downloads/list
 * FFmpeg-Java  http://code.google.com/p/javacv/downloads/list
 * which needs FFmpeg 0.5:
  * Source code  http://ffmpeg.org/download.html
  * Precompiled Windows DLLs  http://ffmpeg.arrozcru.org/autobuilds/

To modify the source code, note that the project files were created with:
 * NetBeans 6.8  http://www.netbeans.org/downloads/

Please keep me informed of any updates or fixes you make to the code so that I may integrate them into the next release. Thank you!

And feel free to ask questions on the mailing list if you encounter any problems with the software! I am sure it is far from perfect...


==Quick Start for OpenCV==
First, put `javacv.jar` and `jna.jar` somewhere in your classpath. Then, the wrappers of OpenCV can automatically access all of the C API from the `cxcore`, `cv`, `highgui`, and `cvaux` modules. OpenCV simply needs to be placed in its default installation directory and it should work. JavaCV will look it up there. It will also search the system PATH, such as the current directory under Windows. The class definitions are basically ports to Java of the original include files in C, and I deliberately decided to keep as much of the original syntax as possible. For example, one can load an image file and smooth it with a program like this:

{{{
import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;
import static name.audet.samuel.javacv.jna.highgui.*;
import static name.audet.samuel.javacv.jna.cvaux.*;

public class Test {
    public static void main(String[] args) {
        IplImage image = cvLoadImage("test.png", 1);
        cvSmooth(image, image, CV_GAUSSIAN, 3, 0, 0, 0);
        // ...
    }
}
}}}


Additionally, I placed version specific functionality in separate classes. To access newer functions of OpenCV 2.0 for example, one would import the `v20` subclasses instead, e.g.:
{{{
import static name.audet.samuel.javacv.jna.cxcore.v20.*;
import static name.audet.samuel.javacv.jna.cv.v20.*;
import static name.audet.samuel.javacv.jna.highgui.v20.*;
import static name.audet.samuel.javacv.jna.cvaux.v20.*;
}}}
and similarly for `v10` and `v11`.


*IMPORTANT NOTE*: OpenCV might crash if it has been compiled with SSE instructions. This is known to occur on 32-bit x86 CPUs when the SSE calling conventions of the compiler used to build the Java implementation differ from the one used to compile OpenCV. The AMD64 architecture appears unaffected. JNA might be updated in the future with a workaround, but for the moment, please be advised. *Concretely, this means you should try to recompile OpenCV without SSE instructions before asking me why JavaCV crashes.*


JavaCV also comes with helper classes and methods on top of OpenCV to facilitate its integration to the Java platform. Here is a small program demonstrating the most frequently useful parts:

{{{
import name.audet.samuel.javacv.*;
import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

public class Test2 {
    public static void main(String[] args) throws Exception {
        String cascadeName = args.length > 0 ? args[0] : "haarcascade_frontalface_alt.xml";

        // Make sure to call JavaCvErrorCallback.redirectError() to prevent your 
        // application from simply crashing with no warning on some error of OpenCV.
        // JavaCvErrorCallback may be subclassed for finer control of exceptions.
        new JavaCvErrorCallback().redirectError();

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.  
        // It can also switch into fullscreen mode when called with a screenNumber.
        CanvasFrame frame = new CanvasFrame("Some Title");

        // OpenCVFrameGrabber uses highgui, but other more versatile FrameGrabbers include
        // DC1394FrameGrabber, FlyCaptureFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        // FAQ about IplImage:
        // - For custom raw processing of data, getByteBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData.
        // - To get a BufferedImage from an IplImage, you may call getBufferedImage().
        // - The createFrom() factory method can construct an IplImage from a BufferedImage.
        // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.
        IplImage grabbedImage = grabber.grab(),
                 grayImage    = IplImage.create(grabbedImage.width, grabbedImage.height, IPL_DEPTH_8U, 1),
                 rotatedImage = grabbedImage.clone();

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(cascadeName));

        // Objects allocated with a create*() or clone() factory method are automatically 
        // garbage collected, but may also explicitly be freed with the release() method.
        CvMemStorage storage = CvMemStorage.create();

        // Contiguous regions of native memory may be allocated using createArray() factory methods.
        CvPoint[] hatPoints = CvPoint.createArray(3);
        CvSeq.PointerByReference contourPointer = new CvSeq.PointerByReference(); 
        int sizeofCvContour = com.sun.jna.Native.getNativeSize(CvContour.ByValue.class);

        // Let's create some random 3D rotation...
        CvMat randomR = CvMat.create(3, 3), randomAxis = CvMat.create(3, 1);
        // We can easily and efficiently access the elements of CvMat objects
        // with the set of get() and put() methods.
        randomAxis.put((Math.random()-0.5)/4, (Math.random()-0.5)/4, (Math.random()-0.5)/4);
        cvRodrigues2(randomAxis, randomR, null);
        double f = (grabbedImage.width + grabbedImage.height)/2;
                                          randomR.put(2, randomR.get(2)*f); 
                                          randomR.put(5, randomR.get(5)*f);
        randomR.put(6, randomR.get(6)/f); randomR.put(7, randomR.get(7)/f);
        System.out.println(randomR);

        // Again, FFmpegFrameRecorder also exists as a more versatile alternative.
        FrameRecorder recorder = new OpenCVFrameRecorder("output.avi", grabbedImage.width, grabbedImage.height);
        recorder.start();

        while (frame.isVisible() && (grabbedImage = grabber.grab()) != null) {

            // Let's try to detect some faces! but we need a grayscale image...
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, 1.1, 3, 0/*CV_HAAR_DO_CANNY_PRUNING*/);
            for (int i = 0; i < faces.total; i++) {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
                cvRectangle(grabbedImage, cvPoint(r.x, r.y), cvPoint(r.x+r.width, r.y+r.height), CvScalar.RED, 1, CV_AA, 0);
                CvPoint.fillArray(hatPoints, r.x-r.width/10, r.y-5,  r.x+r.width*11/10, r.y-5,  r.x+r.width/2, r.y-r.height/2);
                cvFillConvexPoly(grabbedImage, hatPoints, hatPoints.length, CvScalar.GREEN, CV_AA, 0);
            }

            // Let's find some contours! but first some thresholding...
            cvThreshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            // To get the value of an output parameter, we need to use the getValue() or getStructure() 
            // method of its *PointerByReference object, which obviously has to be created prior to the 
            // call if we want to get the data after the call.
            cvFindContours(grayImage, storage, contourPointer, sizeofCvContour, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE); 
            CvSeq contour = contourPointer.getStructure();
            while (contour != null) {
                if (contour.elem_size > 0) {
                    CvSeq points = cvApproxPoly(contour.getPointer(), sizeofCvContour,
                            storage, CV_POLY_APPROX_DP, cvContourPerimeter(contour.getPointer())*0.02, 0);
                    cvDrawContours(grabbedImage, points, CvScalar.BLUE, CvScalar.BLUE, -1, 1, CV_AA);
                }
                contour = contour.h_next;
            }

            cvWarpPerspective(grabbedImage, rotatedImage, randomR);

            frame.showImage(rotatedImage);
            recorder.record(rotatedImage);

            storage.clearMem();
        }
        recorder.stop();
        grabber.stop();
        frame.dispose();
    }
}
}}}


==Acknowledgments==
I am currently an active member of the Okutomi & Tanaka Laboratory, Tokyo Institute of Technology, supported by a scholarship from the Ministry of Education, Culture, Sports, Science and Technology (MEXT) of the Japanese Government.


==Changes==
===April 5, 2010===
 * Fixed up `clone()` methods to avoid the need to cast
 * Removed the `fullScreen` argument from `CanvasFrame` constructors, which will now switch to full-screen mode only when a `screenNumber` is explicitly passed
 * Renamed FrameGrabber.ColorMode.GRAYSCALE to GRAY
 * Replaced deprecated functions from FFmpegFrameGrabber and FFmpegFrameRecorder
 * FFmpegFrameGrabber can now resize images

===March 21, 2010===
 * Added new classes and methods used by ProCamTracker: `cvkernels`, `JavaCV.fractalTriangleWave()`, `ImageAligner`, `LMImageAligner`, `ImageTransformer`, `ProjectiveTransformer`, `ProjectiveGainBiasTransformer`, `ProCamTransformer`, and `ReflectanceInitializer`
 * `CameraDevice.Settings` has a new `deviceFile` property (used by a `FrameGrabber`), which brings up a file dialog for some `PropertyEditor`s
 * Moved in `CameraSettings`, `ProjectorSettings`, and `FrameGrabber.PropertyEditor` from the `procamcalib` package
 * Added to `CameraDevice.Settings` and `FrameGrabber` a `triggerFlushSize` property to indicate the number of buffers to flush on `trigger()` to compensate for cheap cameras that keep old images in memory indefinitely
 * Changed the type of `CameraDevice.Settings.deviceNumber` to `Integer` so we may set it to `null`
 * Fixed and enhanced `CanvasFrame.showImage()` methods a bit
 * In `triggerMode` `DC1394FrameGrabber` nows tries to use a real software trigger and only falls back to one-shot mode on error
 * Fixed array constructors of `IplImage.PointerByReference()` and `CvImgObsInfo.PointerByReference()`
 * Added `CvPoint.fillArray()` methods to reuse preallocated arrays and changed `createArray()` a bit as well
 * Fixed and enhanced all `IplImage.copy*()` methods, including new support for ROIs and subimages, which affects `create*()` and `getBufferedImage()` methods as well
 * Updated `Marker` to support different size and spacing in X and Y
 * Added `Settings` to `ObjectFinder`
 * Fixed distortion problem in `ProjectiveDevice` and `ProCamColorCalibrator` with OpenCV 1.1pre1
 * Split `ProjectiveDevice.Settings` into `ProjectiveDevice.CalibrationSettings` (for applications like ProCamCalib) and `ProjectiveDevice.CalibratedSettings` (for applications like ProCamTracker)
 * Renamed `gamma` to `responseGamma` in `ProjectiveDevice`, and moved previous `nominalDistance` parameter to `Settings`
 * Added `ProjectiveDevice.rescale()` to rescale calibration parameters when switching a device to a new image size
 * `ProjectiveDevice.undistort()` and `distort()` can now `useFixedPointMaps` of OpenCV
 * `ProjectiveDevice` and its subclasses now `throw new Exception()` if the `parameterFile` cannot be read

===February 13, 2010===
 * Relicensed JavaCV under the GPLv2 with Classpath exception (see LICENSE.txt). Please note that if your application links with code that needs ARToolKitPlus, for example, it will become subject to the full GPL, without Classpath exception
 * Added `devicePath` setting to `CameraDevice` that works with `FFmpegFrameGrabber`, `OpenCVFrameGrabber`, and other `FrameGrabber` with a String constructor
 * Added "C:/OpenCV2.0/bin/release/" to the directory list to search for OpenCV DLLs
 * Moved `cvFindHomography()`, `cvFindExtrinsicCameraParams2()`, `cvReprojectImageTo3D()`, `cvSaveImage()`, and `cvRetrieveFrame()` to version specific classes since their number of arguments differ with the version of OpenCV
 * Enhanced `CvMat.put(CvMat mat)` to work better even when the matrices are not actually compatible
 * Added new `IplImage` factory methods `createCompatible(IplImage image)`, `createIfNotCompatible(IplImage image, IplImage template)`, and `createFrom(BufferedImage image)`
 * Fixed `distortionCoeffs` corruption that might occur in `ProjectiveDevice`

===January 3, 2010===
 * Added wrapper for the `cvaux` module of OpenCV
 * Added abstract `FrameRecorder` class and a `OpenCVFrameRecorder` class
 * Fixed read() problem that might occur within Pointer constructors
 * Running `java -jar javacv.jar` now displays version information

===December 22, 2009===
 * Fixed `CanvasFrame` from getting stuck in a maximized window
 * Removed all `setAutoWrite(false)` from `cxcore` now that the bug appears fixed in JNA
 * Added `FFmpegFrameGrabber` and `FFmpegFrameRecorder` to easily record live footage and grab back offline into JavaCV

===November 24, 2009===
 * Added more convenient constructors and factory methods for `CvPoint*`, `CvSize*`, `CvRect`, `CvTermCriteria`, `CvSlice`, and `CvAttrList`
 * Added _R2_ correlation coefficient field to `ProjectiveDevice`
 * Enhanced and fixed color conversion spaghetti code in `FlyCaptureFrameGrabber`
 * Fixed the `CvHaarFeature` Structure 
 * Renamed `CvIntScalar` factory methods to match with `CvScalar`
 * Enhanced and fixed some problems with gamma correction in `IplImage`
 * Added a `highgui.CV_FOURCC()` method that takes chars as parameter
 * Moved `MarkedPlane.drawMarkers()` to `Marker.draw()` for better code reuse
 * Added `MarkedPlane.getTotalWarp()` with a "useCenters" parameter
 * Changed default values of `MarkerDetector.binarizationKWhiteMarkers` to 1.0 and `ProjectorDevice.brightnessBackground` to 0.0
 * Fixed issue with image width and memory alignment in `MarkerDetector`
 * `Marker.getCenter()` now computes the actual physical center instead of the centroid
 * `OpenCVFrameGrabber.getDeviceDescriptions()` now throws `UnsupportedOperationException`
 * Added support in `OpenCVFrameGrabber` to grab frames from video files
 * Added `ProjectiveDevice.getRectifyingHomography()` method
 * Added `JavaCvErrorCallback` to easily catch errors of OpenCV in Java

===October 19, 2009===
 * Moved the functionality of `CvMatPool` to the `CvMat.take()` and `.pool()` methods
 * Added color calibration for projector-camera systems (`ProCamColorCalibrator`)
 * Updated `DC1394FrameGrabber` to handle more conversion use cases automatically
 * Fixed `CvIntScalar` to mirror `CvScalar`

===October 14, 2009===
 * Change of plan: JavaCV now works with any of OpenCV 1.0, 1.1pre1, or 2.0! Version specific functionality is enclosed in subclasses, e.g., the class `name.audet.samuel.javacv.jna.cv.v20` can access everything from the `cv` module of OpenCV 2.0
 * Added a few missing functions and adjusted some mappings to make them closer to the C API
 * Added a few more helper methods to `CvPoint*`
 * Added temporary storage to `ObjectFinder` to plug the memory leak

===October 2, 2009===
 * Fixed problem when loading distortion coefficients with `ProjectiveDevice`
 * Added automatic read and write for functions with arrays of `Structure` or `PointerByReference`
 * Added to `cv.java` a few missing functions related to calibration
 * Fixed up a bit helper methods for `CvPoint*`, `CvScalar`, `CvRect`, `CvBox2D`, `CvMat`, `IplImage`, `CvMemStorage`, `CvSeq`, and `CvSeqBlock`
 * Added `CvMatPool` to `MarkedPlane` and `Marker`
 * Added a few new `distort()` methods to `ProjectiveDevice`
 * Last version to support OpenCV 1.1pre1: Future version will require OpenCV 2.0

===August 27, 2009===
 * `IplImage` now flips the buffer on copy if necessary
 * Added needed Pointer constructor for `CvSURFPoint` and `CvConvexityDefect`
 * Cleaned up a bit the messy Buffers in `CvMat`

===August 26, 2009===
 * Added `get*Buffer()` functions to `IplImage`
 * Added more options for gamma correction in `IplImage` and `ProjectiveDevice`
 * Further cleaned up the namespace and constructors of `ProjectiveDevices`
 * `CanvasFrame.waitKey()` now only checks `KeyEvent.KEY_PRESSED`
 * Added `CvMatPool` to avoid recreating matrices
 * Moved `CvScalar` functions to `cxcore`

===August 19, 2009===
 * Switched to using `import static` for relief from namespace hell
 * Fixed color channel reversal of Bayer images in `DC1394FrameGrabber`

===August 11, 2009===
Initial release


----
Copyright (C) 2009,2010 Samuel Audet <samuel.audet@gmail.com>
Project site: http://code.google.com/p/javacv/

Licensed under the GNU General Public License version 2 (GPLv2) with Classpath exception.
Please refer to LICENSE.txt or http://www.gnu.org/licenses/ for details.

