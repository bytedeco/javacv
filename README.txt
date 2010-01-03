=JavaCV=

==Introduction==
JavaCV first provides wrappers to commonly used libraries by researchers in the field of computer vision: OpenCV, ARToolKitPlus, libdc1394 2.x, PGR FlyCapture, and FFmpeg. Moreover, utility classes make it easy to use their functionality on the Java platform. JavaCV also comes with hardware accelerated fullscreen image display (`CanvasFrame`), easy-to-use methods to execute code in parallel on multiple cores (`Parallel`), user-friendly geometric and color calibration of cameras and projectors (`GeometricCalibrator`, `ProCamGeometricCalibrator`, `ProCamColorCalibrator`), adaptive binarization (`JavaCV.adaptiveBinarization()`), and code to detect and match feature points (`ObjectFinder`).

Please refer to the source code of ProCamCalib as well as methods in the `JavaCV` and `ObjectFinder` classes to learn how to use the API.

Future versions will include all code I will have developed as part of my PhD research.


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
 * PGR FlyCapture 1 or 2 (Windows)  http://www.ptgrey.com/products/pgrflycapture/
 * ARToolKitPlus 2.1.1c  http://code.google.com/p/javacv/downloads/list
 * FFmpeg-Java  http://code.google.com/p/javacv/downloads/list
 * FFmpeg 0.5   http://ffmpeg.org/download.html

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

IMPORTANT: OpenCV might crash if it has been compiled with SSE instructions. This is known to occur on 32-bit x86 when the calling conventions of the compiler used to build the Java implementation differ from the one used to compile OpenCV. The AMD64 architecture appears unaffected. JNA will probably be updated in the near future with a workaround, but for the moment, please be advised.


==Licensing Issues==
If the GPL causes licensing problems with your own code, please contact me. I can reissue JavaCV (minus `ARToolKitPlus.java` and `PGRFlyCapture.java`) under a different license if required.


==Acknowledgments==
I am currently an active member of the Okutomi & Tanaka Laboratory, Tokyo Institute of Technology, supported by a scholarship from the Ministry of Education, Culture, Sports, Science and Technology (MEXT) of the Japanese Government.


==Changes==
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

Licensed under the GNU General Public License version 2 (GPLv2).
Please refer to LICENSE.txt or http://www.gnu.org/licenses/ for details.

