=JavaCV=

==Introduction==
JavaCV first provides wrappers to commonly used libraries by researchers in the field of computer vision: [http://opencv.willowgarage.com/ OpenCV], [http://www.ffmpeg.org/ FFmpeg], [http://damien.douxchamps.net/ieee1394/libdc1394/ libdc1394], [http://www.ptgrey.com/products/pgrflycapture/ PGR FlyCapture], [http://openkinect.org/ OpenKinect], [http://muonics.net/school/spring05/videoInput/ videoInput], and [http://studierstube.icg.tugraz.at/handheld_ar/artoolkitplus.php ARToolKitPlus]. The following classes, found under the `com.googlecode.javacv.cpp` package namespace, expose their complete APIs: `opencv_core`, `opencv_imgproc`, `opencv_video`, `opencv_flann`, `opencv_features2d`, `opencv_calib3d`, `opencv_objdetect`, `opencv_highgui`, `opencv_legacy`, `opencv_ml`, `opencv_contrib`, `avutil`, `avcodec`, `avformat`, `avdevice`, `avfilter`, `postproc`, `swscale`, `dc1394`, `PGRFlyCapture`, `freenect`, `videoInputLib`, and `ARToolKitPlus`, respectively. Moreover, utility classes make it easy to use their functionality on the Java platform, including Android.

JavaCV also comes with hardware accelerated full-screen image display (`CanvasFrame` and `GLCanvasFrame`), easy-to-use methods to execute code in parallel on multiple cores (`Parallel`), user-friendly geometric and color calibration of cameras and projectors (`GeometricCalibrator`, `ProCamGeometricCalibrator`, `ProCamColorCalibrator`), detection and matching of feature points (`ObjectFinder`), a set of classes that implement direct image alignment of projector-camera systems (mainly `GNImageAligner`, `ProjectiveTransformer`, `ProjectiveColorTransformer`, `ProCamTransformer`, and `ReflectanceInitializer`), as well as miscellaneous functionality in the `JavaCV` class. Some of these classes also have an OpenCL and OpenGL counterpart, their names ending with `CL`, i.e.: `JavaCVCL`, etc. except for `GLCanvasFrame`.

To learn how to use the API, since documentation currently lacks, please refer to the [#Quick_Start_for_OpenCV_and_FFmpeg] section below as well as the sample programs, including one for Android, found in the `samples` directory. You may also find it useful to refer to the source code of [http://code.google.com/p/javacv/source/browse/trunk/procamcalib/ ProCamCalib] and [http://code.google.com/p/javacv/source/browse/trunk/procamtracker/ ProCamTracker].

I will continue to add all code that I am developing for my doctoral research as I go.


==Required Software==
To use JavaCV, you will need to download and install the following software:
 * An implementation of Java SE 6 or 7
  * OpenJDK  http://openjdk.java.net/install/  or
  * Sun JDK  http://www.oracle.com/technetwork/java/javase/downloads/  or
  * IBM JDK  http://www.ibm.com/developerworks/java/jdk/  or
  * Java SE for Mac OS X  http://developer.apple.com/java/  etc.
 * OpenCV 2.3.1  http://sourceforge.net/projects/opencvlibrary/files/
  * Precompiled for Android 2.2  http://code.google.com/p/javacv/downloads/list

And please make sure your Java and OpenCV have the same bitness: *32-bit and 64-bit modules do not mix under any circumstances*. Further, although not always required, some functionality of JavaCV also relies on:
 * FFmpeg 0.6.x or 0.7.x  http://ffmpeg.org/download.html
  * Precompiled for Windows  http://ffmpeg.zeranoe.com/builds/  Known compatible builds:
   * http://ffmpeg.zeranoe.com/builds/win32/shared/ffmpeg-0.7.1-win32-shared.7z
   * http://ffmpeg.zeranoe.com/builds/win64/shared/ffmpeg-0.7.1-win64-shared.7z
  * Precompiled for Android 2.2  http://code.google.com/p/javacv/downloads/list
 * libdc1394 2.1.x (Linux and Mac OS X)  http://sourceforge.net/projects/libdc1394/files/
 * PGR FlyCapture 1.7~2.2 (Windows only)  http://www.ptgrey.com/products/pgrflycapture/
 * OpenKinect  http://openkinect.org/
 * CL Eye Platform SDK  http://codelaboratories.com/downloads/
 * Android SDK API 8 or newer  http://developer.android.com/sdk/
 * JOCL and JOGL from JogAmp  http://jogamp.org/

To modify the source code, please note that the project files were created for:
 * NetBeans 6.9  http://netbeans.org/downloads/
 * JavaCPP  http://code.google.com/p/javacpp/
 * ARToolKitPlus 2.1.1t  http://code.google.com/p/javacv/downloads/list

Please keep me informed of any updates or fixes you make to the code so that I may integrate them into the next release. Thank you!

And feel free to ask questions on [http://groups.google.com/group/javacv the mailing list] if you encounter any problems with the software! I am sure it is far from perfect...


==Quick Start for OpenCV and FFmpeg==
First, put all the JAR files of JavaCV (`javacpp.jar`, `javacv.jar`, and `javacv-*.jar`) somewhere in your classpath, and make sure that the library files of OpenCV and FFmpeg (`*.so`, `*.dylib`, or `*.dll`) can be found either in their default installation directory or in the system PATH, which under Windows includes the current working directory. (For answers to problems frequently encountered with OpenCV on the Windows platform, please refer to [http://code.google.com/p/javacv/wiki/Windows7AndOpenCV  Common issues with OpenCV under Windows 7].) Here are some more specific instructions for common cases:

NetBeans (Java SE 6 or 7):
 * In the Projects window, right-click the Libraries node of your project, and select "Add JAR/Folder...".
 * Locate the JAR files, select them, and click OK.

Eclipse (Java SE 6 or 7):
 * Navigate to Project > Properties > Java Build Path > Libraries and click "Add External JARs..."
 * Locate the JAR files, select them, and click OK.

Eclipse (Android 2.2 or newer):
 * Follow the instructions on this page: http://developer.android.com/resources/tutorials/hello-world.html
 * Go to File > New > Folder, select your project as parent folder, type "libs/armeabi" as Folder name, and click Finish.
 * Copy `javacpp.jar` and `javacv.jar` in the newly created "libs" folder.
 * Extract directly all the `*.so` files from `javacv-android-arm.jar` *as well as* the ones from `OpenCV-2.3.1-android-arm.zip` and `ffmpeg-0.7.11-android-arm.zip` in the newly created "libs/armeabi" folder, without creating any new subdirectories.
 * Navigate to Project > Properties > Java Build Path > Libraries and click "Add JARs..."
 * Select both `javacpp.jar` and `javacv.jar` from the newly created "libs" folder.

After that, the wrapper classes for OpenCV and FFmpeg can automatically access all of their C/C++ APIs. The class definitions are basically ports to Java of the original include files in C, plus the missing functionality exposed only by the C++ API of OpenCV, and I deliberately decided to keep as much of the original syntax as possible. For example, here is a method that tries to load an image file, smooth it, and save it back to disk:

{{{
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

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
}}}

JavaCV also comes with helper classes and methods on top of OpenCV and FFmpeg to facilitate their integration to the Java platform. Here is a small demo program demonstrating the most frequently useful parts:

{{{
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class Demo {
    public static void main(String[] args) throws Exception {
        String classifierName = null;
        if (args.length > 0) {
            classifierName = args[0];
        } else {
            System.err.println("Please provide the path to \"haarcascade_frontalface_alt.xml\".");
            System.exit(1);
        }

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        CanvasFrame frame = new CanvasFrame("Some Title");

        // OpenCVFrameGrabber uses opencv_highgui, but other more versatile FrameGrabbers
        // include DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        // FAQ about IplImage:
        // - For custom raw processing of data, getByteBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData.
        // - To get a BufferedImage from an IplImage, you may call getBufferedImage().
        // - The createFrom() factory method can construct an IplImage from a BufferedImage.
        // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.
        IplImage grabbedImage = grabber.grab();
        int width  = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage    = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        IplImage rotatedImage = grabbedImage.clone();

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

        // Objects allocated with a create*() or clone() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling release().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        CvMemStorage storage = CvMemStorage.create();

        // We can allocate native arrays using constructors taking an integer as argument.
        CvPoint hatPoints = new CvPoint(3);

        // Again, FFmpegFrameRecorder also exists as a more versatile alternative.
        FrameRecorder recorder = new OpenCVFrameRecorder("output.avi", width, height);
        recorder.start();

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

                // To access the elements of a native array, use the position() method.
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
        recorder.stop();
        grabber.stop();
        frame.dispose();
    }
}
}}}


==Acknowledgments==
I am currently an active member of the Okutomi & Tanaka Laboratory, Tokyo Institute of Technology, supported by a scholarship from the Ministry of Education, Culture, Sports, Science and Technology (MEXT) of the Japanese Government.


==Changes==
===February 18, 2012===
 * Added `GLCanvasFrame` to show OpenGL renderbuffers on screen, plus a new factory method `JavaCVCL.createCLGLImageFrom()` to create compatible ones from `IplImage` objects, as well as more user-friendly `getGLContext()`, `getGL()` and `getGL2()` methods
 * Fixed various things of the original `CanvasFrame`, and `JavaCV.createCLImage()` and `createIplImage()`, also appending `From` to their names 
 * New `createPinnedBuffer()` and `createPinnedIplImage()` factory methods in `JavaCVCL` to allocate page-locked memory for faster CPU<->GPU transfers, but it does not seem to work for OpenCL image objects, only linear buffer objects :(
 * Fixed and enhanced `GNImageAlignerCL` and `ProjectorDevice` (its `useOpenGL` property) to support fully OpenCL and OpenGL acceleration
 * Refactored `Parallel` a bit so that we may set the number of threads it uses via its static `numThreads` property or the "com.googlecode.javacv.numthreads" system property, which defaults to `Parallel.getNumCores() = Runtime.getRuntime().availableProcessors()`
 * Cleaned up and renamed some methods in `JavaCV`, while adding `boundingRect()`, functionally similar to `cvBoundingRect`, but better adapted to compute a properly aligned and padded ROI
 * Inserted a couple of missing `allocate()` inside `opencv_flann`
 * Updated `ObjectFinder` with a `Settings.useFLANN` property to let it use FLANN via OpenCV
 * Cleaned up and optimized `HandMouse`
 * `CanvasFrame`, `FrameGrabber`, `FrameRecorder`, and `ProjectiveDevice` objects now throw `Exception` objects of a nested class instead of the too generic `java.lang.Exception` one
 * Moved parallel execution of `cvkernels.multiWarpColorTransform()`, modifying `ImageTransformer` classes, from `GNImageAligner` into `cvkernels`, which now also supports other image types than `float`
 * Renamed some `Settings` properties here and there to correct typos and reflect better their meanings
 * Updated `freenect` to reflect the latest changes of OpenKinect's master branch
 * FFmpeg and other libraries did not work under Android when compiled with the latest NDK, r7 (issue #147): Fixed in JavaCPP
 * Moved `IplImage.timestamp` to `FrameGrabber`, also adding a `frameNumber` property, both allowing to seek within streams too
 * Removed `triggerFlushSize` property from `CameraDevice` and `FrameGrabber`, instead relying on the `numBuffers` property to decide the required size of a buffer flush
 * Corrected the logic behind `FFmpegFrameGrabber.getFrameRate()` and `getTimestamp()` (issue #151)
 * Created a `BufferRing` class for convenient circular rings of large buffers that require manual release of resources, such as OpenCL memory
 * Added a few more useful methods to `FrameGrabber`, including `restart()`, `flush()`, and `delayedGrab()` (to be used in conjunction with `getDelayedTime()` and `getDelayedImage()`)
 * Inserted `cvLoadImageBGRA()` and `cvLoadImageRGBA()` methods into `opencv_highgui` to load color images compatible with OpenCL more easily
 * `JavaCvErrorCallback` now outputs messages to `Logger` instead of `System.err`
 * Defined `VI_COM_MULTI_THREADED` for `videoInput`, allowing it to run on multiple threads if needed

===January 8, 2012===
 * JavaCV should now have an easier time automatically finding libraries inside standard directories such as `/usr/local/lib/`, `/opt/local/lib/`, and `C:\opencv\`, even when they are not part of the system configuration or PATH (issue #127)
 * Renamed `set()` and `fill()` methods to `put()` inside `CvPoint*` classes, for better naming consistency
 * Renamed `FrameGrabber.ColorMode` to `ImageMode` and its `BGR` value to `COLOR` to reflect the fact that a `FrameGrabber` instance can return color images in some arbitrary format, but added a new `pixelFormat` property to let users know or specify the exact pixel format desired, such as `PIX_FMT_BGR24`, etc. in the case of `FFmpegFrameGrabber`
 * After `FFmpegFrameGrabber.start()`, the `format`, `imageWidth`, `imageHeight`, and `frameRate` properties switch to their effective values
 * Added new `FrameGrabber.sensorPattern` property to obtain the Bayer filter layout of raw data from `DC1394FrameGrabber` and `FlyCaptureFrameGrabber`
 * Readded to `KDTree`, `Index`, and `HOGDescriptor` some functions with `FloatPointer` and `IntPointer` arguments that were mistakenly removed when OpenCV switched to using `cv::InputArray` and `cv::OutputArray` parameter types (issue #134)
 * Renamed `ProjectiveGainBiasTransformer` to `ProjectiveColorTransformer`
 * Added a few classes to do some processing using OpenCL and OpenGL: `JavaCVCL`, `GNImageAlignerCL`, `ProjectiveTransformerCL`, `ProjectiveColorTransformerCL`, and `ProCamTransformerCL` with some other related files
 * Renamed `Parallel.numCores` to the more conventional `Parallel.NUM_CORES`
 * Added new `FaceRecognition.java` sample from Stephen L. Reed
 * Inserted a couple of missing calls to `Loader.load()` (issue #142)
 * Improved hacks for `Loader.load()` in JavaCPP make JavaCV work on Android 4.0
 * New `PS3EyeFrameGrabber` from Jiri Masa can now grab images using the SDK from Code Laboratories

===October 1, 2011===
 * Fixed `DC1394FrameGrabber` and `FlyCaptureFrameGrabber` to behave as expected with all Bayer/Raw/Mono/RGB/YUV cameras modes (within the limits of libdc1394 and PGR FlyCapture) (issue #91)
 * Fixed regression of `IplImage.copyFrom()` and `createFrom()` with `BufferedImage` objects of `SinglePixelPackedSampleModel` (issue #102)
 * C++ functions using `std::vector` objects as output parameters now work on Windows Vista and Windows 7 as well

===August 20, 2011===
 * Upgraded support to OpenCV 2.3.1
 * An output argument of type `cv::Mat` or `cv::OutputArray` returned with a size 0 now correctly sets `CvArr.address = 0`
 * Fixed `IplImage.createFrom()` and `copyFrom()` when called on objects returned by `BufferedImage.getSubimage()`
 * Added missing allocator to `CvRNG`
 * `OpenCVFrameGrabber` now detects when CV_CAP_PROP_POS_MSEC is broken and gives up calling `cvGetCaptureProperty()`
 * New `OpenKinectFrameGrabber.grabDepth()` and `grabVideo()` methods to capture "depth" and "video" simultaneously, regardless of the mode

===July 5, 2011===
 * Upgraded support to OpenCV 2.3.0
 * Fixed `OpenKinectFrameGrabber`, which can now also capture depth images when `setFormat("depth")` is called before `start()`
 * Fixed `CvMatArray` and `IplImageArray` as well as histogram related functions
 * Fixed `FFmpegFrameGrabber`, and `FFmpegFrameRecorder` now works on Android also
 * Fixed calls, such as `opencv_flann.Index.knnSearch()`, that require a `MatAdapter` or an `ArrayAdapter` for output

===June 10, 2011===
 * New `freenect` wrapper and corresponding `OpenKinectFrameGrabber` to capture from Microsoft's Kinect stereo camera using OpenKinect
 * JavaCV now exposes all C++ functions and classes of OpenCV not covered by the C API
 * Fixed various erroneous declarations and calls, including those due to changes in JavaCPP

===May 11, 2011===
 * Removed `CvMat` object pooling in favor of more efficient `ThreadLocal` objects created by `CvMat.createThreadLocal()`
 * Changed `Marker.getCenter()` back to the centroid, because it has better noise averaging properties and gives in practice more accurate results than the actual center
 * Added hack to `OpenCVFrameGrabber.start()` to wait for `cvRetrieveFrame()` to return something else than `null` under Mac OS X
 * FFmpeg now works properly on Windows and Android (issue #63) with newer binaries
 * New `videoInputLib` wrapper and corresponding `VideoInputFrameGrabber` to capture using DirectShow, useful under Windows 7 where OpenCV and FFmpeg can fail to capture using Video for Windows (issue #58)
 * `GeometricCalibrator` now reports the maximum errors in addition to the average (RMS) errors

===April 7, 2011===
 * Added a `format` property to `CameraDevice`, `FrameGrabber`, and `FrameRecorder`, mostly useful for `FFmpegFrameGrabber`, where interesting values include "dv1394", "mjpeg", "video4linux2", "vfwcap", and "x11grab"
 * `OpenCVFrameRecorder` now uses `CV_FOURCC_PROMPT` under Windows as default since `CV_FOURCC_DEFAULT` crashes (issue #49)
 * Added hack to make sure the temporarily extracted library files get properly deleted under Windows
 * JavaCPP now loads classes more lazily
 * Fixed most occurences of `UnsatisfiedLinkError` (issue #54), but some corner cases may require a call to `Loader.load()` on the class one wishes to use
 * Added (rudimentary) outlier detection and modified zero threshold handling in the image alignment framework
 * New `JavaCV.hysteresisThreshold()` feature
 * New `HandMouse` functionality, which depends on the image alignment framework
 * Fixed `ProjectiveDevice.distort()`, which mistakenly undistorted images instead
 * New `HoughLines` sample thanks to Jeremy Nicola

===February 19, 2011===
 * Switched from JNA to JavaCPP, which has a lower overhead and supports C++, bringing hope that future versions of JavaCV will support features of OpenCV available only through the C++ API
 * Consequently, the syntax of various operations have changed a bit, but the transition should not be too painful
 * As a happier consequence, this also fixes the problem with SSE instructions on 32-bit x86 (issue #36)
 * Also, JavaCPP does not have any limitations or performance issues with large data structures (issue #10 and issue #14)
 * Added support for OpenCV 2.2 (issue #42), but dropped support for all previous versions
 * Added samples provided by users (issue #1, issue #45, and issue #46)
 * Added deinterlace setting to `FFmpegFrameGrabber` having it call `avpicture_deinterlace()` (issue #38)
 * Enhanced a few things of the image alignment algorithm
 * Tried to fix image format conversion inside `FlyCaptureFrameGrabber`, but this is going to require more careful debugging
 * Fixed and added various other things I forget

===December 2, 2010===
 * Now works on Android with the Dalvik VM (for more details, please refer to the FacePreview sample available on the download page)
 * Added more hacks to `CanvasFrame` in the hope to make it behave better outside the EDT
 * Made clearer the error messages thrown from `FrameGrabber` objects, when `start()` may not have been called
 * Fixed version specific declarations of `CvStereoBMState` and related functions
 * Fixed conditions that could crash `cvkernels`

===November 4, 2010===
 * Renamed the package namespace to `com.googlecode.javacv`, which makes more sense now that JavaCV has been well anchored at Google Code for more than a year, piggybacking on the unique and easy-to-remember domain name
 * Included new FFmpeg wrapper classes `avutil`, `avcodec`, `avformat`, `avdevice`, `avfilter`, `postprocess`, and `swscale`, eliminating the need of the separate FFmpeg-Java package
 * `CanvasFrame` now redraws its `Canvas` after the user resizes the `Frame`
 * Fixed the `Error` thrown when calling `CanvasFrame.showImage()` from the EDT
 * Added check to `DC1394FrameGrabber` so that a "Failed to initialize libdc1394" does not crash the JVM
 * `FFmpegFrameGrabber` does not crash anymore when forgetting to call `start()` before a `grab()` or `trigger()`
 * `FrameGrabber` now selects the default grabber a bit better
 * Made sweeping changes (for the better, but still not finalized) to `GNImageAligner`, `ProjectiveTransformer`, `ProjectiveGainBiasTransformer`, and `ProCamTransformer`...
 * Added to `JavaCV` more methods related to transformation of planes: `perspectiveTransform()`, `getPlaneParameters()`, `getPerspectiveTransform()`, and `HtoRt()`, as well as `ProjectiveDevice.getFrontoParallelH()`
 * Added a static `autoSynch` flag to all `Structure` classes of `cxcore`, `cv`, and `cvaux`, which you may set to `false` prior to the return of things like big and heavy `CvSeq` to make them load faster and to avoid stack overflows, but accessing fields will then require manual calls to `readField()` and `writeField()` (issue #10 and #14)
 * Added missing `ByValue` subclasses to `CvSeq`, `CvSet`, `CvContourTree`, and `CvChain`... Any others missing?
 * Fixed `Exception` thrown from `cvCreateHist()` under JNA 3.2.7 (issue #26)
 * Enhanced `CvMat.put()`, which now supports setting submatrices
 * Improved inside `IplImage` the support of `BufferedImage`, especially those using a `DirectColorModel` (issue #23)
 * Fixed crash in `cvkernels` when color transformation `X` is `null`

===July 30, 2010===
 * Fixed crash that would occur in `CanvasFrame` for some video drivers
 * `FFmpegFrameGrabber` now supports other input formats (devices), such as `x11grab` that can be used for screencasting
 * Added `JavaCV.median()` function, and `JavaCV.fractalTriangleWave()` now respects image ROI
 * Fixed background subtraction in `cvaux`
 * Fixed crash inside the code for direct alignment caused by the ROI getting set outside the image plane
 * Added `deltaScale` and `tryToFixPlane` to `GNImageAligner.Settings` (the first used in `ImageTransformer.Parameters` as increment, randomly selected forward or backward, for finite difference), which sometimes help to jump over local minima

===May 30, 2010===
 * Removed redundant `CvMemStorage.clearMem()` method, use `cvClearMemStorage()`
 * Fixed the sample `Test2` class that did not work under Windows
 * Fixed corruption by the `cvkernels` `transformer` at the borders
 * Modified `CanvasFrame` constructors and added a `gamma` argument used by `showImage(IplImage)`
 * `CanvasFrame` now lets users resize the frame, while displayed images are stretched to fit the new size
 * Renamed `CanvasFrame.acquireGraphics()` to `createGraphics()` for consistency
 * When `FlyCaptureFrameGrabber` cannot set fastest speed, it now safely fails by setting any supported speed
 * Added a new `Parallel.loop()` method that can use more threads than the number of CPU cores detected
 * Added new `numThreads` property to `GNImageAligner` and fixed a few minor inconsistencies as well
 * Fixed incorrect `Java.HnToRt()`, and added a few `norm()` and `randn()` methods
 * For functions with `float[]` and `double[]` arguments in `cvaux` and `cv`, added complementary `FloatBuffer` and `DoubleBuffer` declarations
 * Fixed loading problems with `cvaux`
 * Fixed and enhanced histogram, back projection, and other CAMSHIFT related functionality
 * Added code for `CvRNG`
 * Added "/opt/local/lib/" and "/opt/local/lib64/" (standard on Mac OS X) to the default list of search paths for OpenCV
 * Added `CvScalar.getVal()` and `CvIntScalar.getVal()`, which simply return the `val` field, convenient for Scala where `val` is a reserved word
 * Fixed the construction of `IplImage` from a `Pointer`
 * Removed incorrect cases when an `IplImage` gets converted to a `BufferedImage.TYPE_CUSTOM`
 * Made `CvArr.PointerByReference` a bit more consistent and general

===April 16, 2010===
 * Modified `IplImage`, `FrameGrabber`, and `CanvasFrame` to get better default behavior of gamma correction
 * Fixed `cv.CvHistogram` and related histogram functions
 * `CameraDevice.Settings.triggerFlushSize` now defaults to 5 (only affects `OpenCVFrameGrabber` and `FFmpegFrameGrabber`)
 * Replaced `LMImageAligner` by `GNImageAligner`, a more appropriate name for Gauss-Newton with `lineSearch`
 * Fixed a few things related with `ProjectiveDevice.Settings`

===April 8, 2010===
 * Added support for OpenCV 2.1

===April 5, 2010===
 * Fixed up `clone()` methods to avoid the need to cast
 * Removed the `fullScreen` argument from `CanvasFrame` constructors, which will now switch to full-screen mode only when a `screenNumber` is explicitly passed
 * Renamed `FrameGrabber.ColorMode.GRAYSCALE` to `GRAY`
 * Replaced deprecated functions from `FFmpegFrameGrabber` and `FFmpegFrameRecorder`
 * `FFmpegFrameGrabber` can now resize images

===March 21, 2010===
 * Added new classes and methods used by ProCamTracker: `cvkernels`, `JavaCV.fractalTriangleWave()`, `ImageAligner`, `LMImageAligner`, `ImageTransformer`, `ProjectiveTransformer`, `ProjectiveGainBiasTransformer`, `ProCamTransformer`, and `ReflectanceInitializer`
 * `CameraDevice.Settings` has a new `deviceFile` property (used by a `FrameGrabber`), which brings up a file dialog for some `PropertyEditor`s
 * Moved in `CameraSettings`, `ProjectorSettings`, and `FrameGrabber.PropertyEditor` from the `procamcalib` package
 * Added to `CameraDevice.Settings` and `FrameGrabber` a `triggerFlushSize` property to indicate the number of buffers to flush on `trigger()` to compensate for cheap cameras that keep old images in memory indefinitely
 * Changed the type of `CameraDevice.Settings.deviceNumber` to `Integer` so we may set it to `null`
 * Fixed and enhanced `CanvasFrame.showImage()` methods a bit
 * In `triggerMode` `DC1394FrameGrabber` now tries to use a real software trigger and only falls back to one-shot mode on error
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
 * Change of plan: JavaCV now works with any of OpenCV 1.0, 1.1pre1, or 2.0! Version specific functionality is enclosed in subclasses, e.g., the class `cv.v20` can access everything from the `cv` module of OpenCV 2.0
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
Copyright (C) 2009-2012 Samuel Audet <samuel.audet@gmail.com>
Project site: http://code.google.com/p/javacv/

Licensed under the GNU General Public License version 2 (GPLv2) with Classpath exception.
Please refer to LICENSE.txt or http://www.gnu.org/licenses/ for details.

