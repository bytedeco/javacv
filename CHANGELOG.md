 * Fix a timestamp rounding issue in `FFmpegFrameGrabber` that causes `setFrameNumber()` to sometimes pick the wrong frame if FPS is not a proper divisor of 1000000 ([issue #5](https://github.com/bytedeco/javacv/issues/5))
 * Increase the flexibility of the `pom.xml` file by making it possible to specify a custom version of JavaCPP
 * Add missing dependencies for JogAmp in the `pom.xml` file ([issue #2](https://github.com/bytedeco/javacv/issues/2))
 * Add new `OpenCVFaceRecognizer` sample, thanks to Petter Christian Bjelland
 * Add new `OpticalFlowDense` sample, thanks to Dawit Gebreyohannes ([issue #468](http://code.google.com/p/javacv/issues/detail?id=468))
 * Make it easier to try out the `FaceRecognition.java` sample ([issue #1](https://github.com/bytedeco/javacv/issues/1))

### April 28, 2014 version 0.8
 * Move from Google Code to GitHub as main source code repository
 * Upgrade support to OpenCV 2.4.9
 * Upgrade supported FFmpeg API to the 2.2 release branch
 * Fix `FFmpegFrameRecorder` not refreshing the resampler when the format of samples changes (issue #465)
 * Rename the `com.googlecode.javacv.cpp` package to `org.bytedeco.javacpp`, and `com.googlecode.javacv` to `org.bytedeco.javacv`
 * Removed old NetBeans project files that cause a conflict when trying to open as a Maven project (issue #210)
 * Adjusted the samples a bit because of small changes in the API with the move to the JavaCPP Presets
 * Fixed `ObjectFinder` not working with recent versions of OpenCV, especially on Android (issue #214)
 * Added new `FrameRecorder.gopSize` property to let users set a desired GOP size instead of the default one of 12
 * `FFmpegFrameGrabber` now takes into account calls to `setPixelFormat()` (issue #429), but does not enforce it
 * Added a `Frame.audioChannels` field for resampling purposes in `FFmpegFrameRecorder` (issue #388)
 * In `FFmpegFrameRecorder`, fixed audio encoding with the Vorbis codec (issue #428) and the WebM container (issue #435), and other audio related things
 * Added missing `allocateArray()` constructors to `CameraParams` and `MatchesInfo` (issue #421)
 * Fixed errors such as "jniopencv_nonfree.dll: Can't find dependent libraries" by adding the `opencv_ocl` module as dependency
 * Added support to seek in audio-only streams with `FFmpegFrameGrabber.setTimestamp()` (issue #417)
 * Fixed potential thread concurrency issues and crash in the `stopRecording()` and `onDestroy()` methods of the `RecordActivity` sample, thanks to Jacob Duron
 * To capture the last frame of a video file, reverted `FFmpegFrameGrabber.setTimestamp()` to its previous behavior (issue #413)
 * Updated `samples/FaceApplet.jnlp` to make it work with JDK/JRE 7u45

### January 6, 2014 version 0.7
 * Upgraded support to OpenCV 2.4.8
 * Upgraded supported FFmpeg API to the 2.1 release branch
 * Updated `freenect` to reflect the latest changes of OpenKinect's master branch
 * Updated `videoInput` to reflect the latest changes in the "update2013" branch
 * Added `Frame.opaque` field to give access to the raw `AVFrame` in the case of `FFmpegFrameGrabber` (issue #399)
 * Added new `FFmpegFrameGrabber.grabKeyFrame()` method to grab key frames (I-frames) directly (issue #312)
 * `VideoInputFrameGrabber` now uses 640x480 as default image size to prevent "videoInput.getPixels() Error: Could not get pixels."
 * Fixed `FFmpegFrameGrabber.setTimestamp()` not working for streams with audio (issue #398)
 * Fixed wrong `haarcascade_frontalface_alt.xml` file getting downloaded by the `Demo` class (issue #402)
 * Added a `Frame.sampleRate` field to allow audio samples to be resampled by `FFmpegFrameRecorder` (issue #388)
 * Incorporated `IPCameraFrameGrabber` from Greg Perry (issue #384)
 * Fixed thread safety issues with FFmpeg in `FFmpegFrameGrabber` and `FFmpegFrameRecorder` (issue #377)
 * Fixed memory leak in the `MotionDetector.java` sample file (issue #372)
 * New `videoCodecName` and `audioCodecName` properties to allow users of `FFmpegFrameRecorder` to use codecs such as "libx264rgb" (issue #369)

### September 15, 2013 version 0.6
 * Upgraded supported FFmpeg API to the 2.0 release branch (with Java interface files now based on code automatically produced by [JavaCPP Presets](https://github.com/bytedeco/javacpp-presets))
 * Fixed `FFmpegFrameGrabber.getFrameNumber()`
 * Upgraded support to OpenCV 2.4.6
 * Fixed callbacks when used with custom class loaders such as with Web containers
 * Upgraded to ARToolKitPlus 2.3.0 (issue #234)
 * Fixed drawing issues with `MarkerDetector.draw()`
 * Fixed `FFmpegFrameGrabber.getTimestamp()` not returning values for audio frames (issue #328)
 * Added new `Frame.keyFrame` field returned by `FFmpegFrameGrabber.grabFrame()` to know when a grabbed frame is a key frame or not (issue #312)
 * Worked around problem in `samples/RecordActivity.java` that would happen when trying to record a frame with an invalid timestamp (issue #313)
 * Fixed potential resource leak that could occur after `FFmpegFrameRecorder` throwing an `Exception`
 * Fixed `FFmpegFrameGrabber` not returning the last few frames of video streams (issue #315)
 * Fixed wrong dependencies of OpenCV preventing correct loading (issue #304)
 * Renamed `FrameRecorder.record(Buffer[] samples)` to a cleaner `record(Buffer ... samples)` (issue #303)
 * Fixed `FFmpegFrameRecorder` not flushing buffers on `stop()` (issue #302)

### April 7, 2013 version 0.5
 * Upgraded support to OpenCV 2.4.5
 * Upgraded supported FFmpeg API to the 1.2 release branch
 * New methods `FFmpegFrameRecorder.setVideoOption()` and `setAudioOption()` generalize the way to set arbitrary codec options, such as "profile", "preset", "tune", etc. used by the x264 codec
 * Included better format guessing inside `FFmpegFrameRecorder` for protocols like RTP
 * Added support for planar audio formats to `FFmpegFrameGrabber` and `FFmpegFrameRecorder`, as required by newer versions of FFmpeg for at least MP3 and AAC
 * Enhanced `FFmpegFrameRecorder` by making it use the closest supported frame rate for the given codec instead of failing
 * To support variable bitrate (VBR) encoding, appended new `videoQuality` and `audioQuality` properties to `FFmpegFrameRecorder`, which usually have an effective range of [0, 51] and overrides the `videoBitrate` and `audioBitrate` properties

### March 3, 2013 version 0.4
 * Upgraded support to OpenCV 2.4.4
 * `CanvasFrame.waitKey(-1)` does not wait anymore and returns the last `KeyEvent` dispatched since the last call to it
 * Upgraded supported FFmpeg API to the 1.1 release branch
 * Fixed bug in `FaceRecognition.java` sample (issue #276)
 * Included `Sobel()`, `Scharr()`, `Laplacian()`, and `Canny()` from `opencv_imgproc` whose equivalent functions in the C API have missing parameters
 * Extended `OpenKinectFrameGrabber` with `setDepthFormat()` and `setVideoFormat()` methods to be able to set both formats independently (issue #273)
 * Fixed `Blender.blend()` having its `@OutputMat` incorrectly annotated as `@InputMat` (issue #272)
 * Added new `RecordActivity.java` Android sample from Shawn Van Every and Qianliang Zhang
 * Added missing `allocate()` methods for `FunctionPointer` in `AVIOContext` and others, which prevented these FFmpeg callbacks from functioning
 * Fixed infinite loop in `FrameGrabber.Array.grab()` (as used by ProCamCalib in the case of stereo cameras, issue #262) when `FrameGrabber.getTimestamp()` returns an invalid negative value (as with `opencv_highgui`) or when using different types of (unsynchronized) `FrameGrabber` together
 * Fixed `cvQueryHistValue_1D()` and other functions that use a raw `CvArr` object
 * Fixed problem when subclassing `CanvasFrame`

### November 4, 2012 version 0.3
 * Upgraded support to OpenCV 2.4.3 (issue #233)
 * Fixed functions like `Algorithm.getMat()` and `HOGDescriptor.getDefaultPeopleDetector()` returning `null` instead of the expected data
 * Implemented better, more transparent, handling of `cv::Ptr`
 * When allocating an empty `IplImage`, `CvMat`, `CvBGCodeBookModel`, etc. its memory content now gets zeroed out, giving OpenCV a better chance of displaying an error message instead of crashing
 * Upgraded supported FFmpeg API to the 1.0 release branch
 * Appended to `StringVector` and `MatVector` new convenient bulk constructors and `put()` methods taking arrays of `String`, `IplImage`, `CvMat`, etc.
 * Included new `Blobs` module from David Grossman and the corresponding `BlobDemo` sample
 * Added missing `opencv_core.partition()` function (issue #144)
 * Fixed up the samples a bit (issue #229 and issue #230)
 * Switched the majority of `@Adapter` annotations to more concise ones like `@StdVector` as allowed by new capabilities of JavaCPP
 * Fixed `FFmpegFrameGrabber.getLengthInFrames()` and `OpenCVFrameGrabber.getLengthInTime()` (issue #231 and issue #236)
 * Enhanced `FFmpegFrameRecorder` to support conversion between audio sample formats (for the experimental AAC encoder among other things) and to let two different threads call `record(samples)` and `record(image)` simultaneously, plus a couple of other features like `setFrameNumber()`, which lets users skip image frames (achieving variable frame rate)
 * Added a `javacpp.skip` property to `pom.xml`, such that a command like `mvn package -Pall -Djavacpp.skip=true` only recompiles the Java source files, but also added `platform.root` and `compiler.path` properties, which map directly to JavaCPP's for convenience

### July 21, 2012 version 0.2
 * Provided new `javacv-linux-arm.jar` build thanks to Jeremy Nicola (issue #184)
 * Additional default properties inside `pom.xml` make it easier to build JavaCV from source (issue #202), calling `mvn package` now succeeds with only OpenCV and a C++ compiler for JavaCPP
 * Made a few minor updates for OpenCV 2.4.2
 * New `Pointer.limit` property of JavaCPP can now be used to get the `size` of an output parameter, and to specify the maximum `size` on input as well
 * Upgraded supported FFmpeg API to the 0.11 release branch
 * Added audio support to `FFmpegFrameGrabber` (call `grabFrame()` instead of `grab()`) and `FFmpegFrameRecorder` (call `setAudioChannels()` before `start()`, and `record(Frame)` instead of `record(IplImage)`) (issue #160)
 * Gave better default `FFmpegFrameRecorder` settings to H.263, MPEG-4, etc. codecs and fixed H.264 encoding with libx264 (issue #160)
 * Refined the `FaceApplet` sample
 * Fixed `FlannBasedMatcher` constructor, `FaceRecognizer.train()`, and `Stitcher.stitch()/composePanorama()` (issue #211)
 * Fixed `CanvasFrame` sometimes blanking out under Windows and maybe Linux (issue #212)

### May 27, 2012 version 0.1
 * Started using version numbers, friendly to tools like Maven, and placing packages in a sort of [Maven repository](http://maven2.javacv.googlecode.com/git/)
 * JavaCV can now extract and load native dependent libraries such as `libopencv_core.so.2.4`, `libopencv_core.2.4.dylib`, `opencv_core240.dll`, etc. from Java resources placed inside the `com.googlecode.javacv.cpp.<platform.name>` package (i.e.: under the `/com/googlecode/javacv/cpp/<platform.name>/` directory of a JAR file in the classpath) (issue #146)
 * Included new `FaceApplet` sample to demonstrate [How to use JavaCV in an applet](http://code.google.com/p/javacv/wiki/HowToMakeAnApplet)
 * Added handy `IplImage.asCvMat()` and `CvMat.asIplImage()` conversion methods
 * Fixed a few small things with `OpenCVFrameGrabber`, `opencv_contrib`, `opencv_legacy`, and `opencv_stitching`

### May 12, 2012
 * Upgraded support to OpenCV 2.4.0 (issue #187)
 * Moved the source code repository to Git
 * Added `pom.xml` file for Maven support and changed the directory structure of the source code to match Maven's standard directory layout
 * Made it easier to create one massive statically linked native library by passing something like "-Xcompiler -Wl,-static -o javacv" as command line options to JavaCPP, usually from inside `build.xml` or `pom.xml` (issue #146)
 * Fixed missing parameter from `CvANN_MLP.create()`
 * Added methods `cvCalcCovarMatrixEx()`, `cvEigenDecomposite()`, and `cvEigenProjection()` taking an `IplImage[]` as argument for convenience
 * `VideoInputFrameGrabber.start()` now accepts a `connection` argument such as `VI_COMPOSITE` to support analog cameras and what not
 * Fixed `FaceRecognition` sample (issue #188)
 * Added a few convenience methods to avoid the need to create empty `CvAttrList`

### March 29, 2012
 * Added missing array allocators and `position()` methods to `KDTree.Node`, `DefaultRngAuto`, `CvAffinePose`, `KeyPoint`, `BaseKeypoint`, `ReferenceTrees`, `DMatch`, `*.Params`, `CvFuzzy*`, `Octree.Node`, `CvDefParam`, `Cv*Blob*`, `Cv*Track*`, `CvDrawShape`, `CvVectors`, `CvParamGrid`, `Cv*Params`, `CvSVM*`, `CvPair16u32s`, `CvDTree*`  `CvTrainTestSplit`, `CvMLData`, `FeatureEvaluator`, and `*DataMatrixCode`
 * Increased versatility of `IplImage.createFrom()`, `copyFrom()`, `copyTo()`, `getBufferedImage()` by providing a `flipChannels` parameter, whose effect was previously mistakenly forced onto four-channel images of byte values only (issue #163)
 * Fixed a couple of things with `CvMat.get()/put()` (issue #167)
 * In addition to an `IplImage`, we may now specify the pixel format of the data when calling `FFmpegFrameRecorder.record()`, but otherwise when `IplImage.nChannels == 2`, it assumes `PIX_FMT_NV21`, allowing for easy and efficient encoding of data captured from the camera on Android (issue #160), image objects we can also convert to RGB using `cvCvtColor()` with `CV_YUV420sp2BGR`
 * Fixed seeking capabilities of `FFmpegFrameGrabber` (issue #162) and added `getLengthInFrames()` and `getLengthInTime()` methods to query the duration of streams, when known
 * Enhanced `IplImage.clone()` and `create*Compatible()` with cloning of their `BufferedImage` to make it easier to keep color components in the right order (issue #163)
 * Refactored `FrameGrabber` and `FrameRecorder` a bit to accommodate new `createDefault(...)` and `create(String className, ...)` factory methods, offering to users an easier selection method to work around limitations of some APIs (issue #70)
 * Adjusted `GNImageAligner`, `ProCamTransformer`, etc. to support alignment of only the projector display on textureless surface planes
 * Renamed a few more `Settings` properties to reflect better their meanings

### February 18, 2012
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

### January 8, 2012
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

### October 1, 2011
 * Fixed `DC1394FrameGrabber` and `FlyCaptureFrameGrabber` to behave as expected with all Bayer/Raw/Mono/RGB/YUV cameras modes (within the limits of libdc1394 and PGR FlyCapture) (issue #91)
 * Fixed regression of `IplImage.copyFrom()` and `createFrom()` with `BufferedImage` objects of `SinglePixelPackedSampleModel` (issue #102)
 * C++ functions using `std::vector` objects as output parameters now work on Windows Vista and Windows 7 as well

### August 20, 2011
 * Upgraded support to OpenCV 2.3.1
 * An output argument of type `cv::Mat` or `cv::OutputArray` returned with a size 0 now correctly sets `CvArr.address = 0`
 * Fixed `IplImage.createFrom()` and `copyFrom()` when called on objects returned by `BufferedImage.getSubimage()`
 * Added missing allocator to `CvRNG`
 * `OpenCVFrameGrabber` now detects when CV_CAP_PROP_POS_MSEC is broken and gives up calling `cvGetCaptureProperty()`
 * New `OpenKinectFrameGrabber.grabDepth()` and `grabVideo()` methods to capture "depth" and "video" simultaneously, regardless of the mode

### July 5, 2011
 * Upgraded support to OpenCV 2.3.0
 * Fixed `OpenKinectFrameGrabber`, which can now also capture depth images when `setFormat("depth")` is called before `start()`
 * Fixed `CvMatArray` and `IplImageArray` as well as histogram related functions
 * Fixed `FFmpegFrameGrabber`, and `FFmpegFrameRecorder` now works on Android also
 * Fixed calls, such as `opencv_flann.Index.knnSearch()`, that require a `MatAdapter` or an `ArrayAdapter` for output

### June 10, 2011
 * New `freenect` wrapper and corresponding `OpenKinectFrameGrabber` to capture from Microsoft's Kinect stereo camera using OpenKinect
 * JavaCV now exposes all C++ functions and classes of OpenCV not covered by the C API
 * Fixed various erroneous declarations and calls, including those due to changes in JavaCPP

### May 11, 2011
 * Removed `CvMat` object pooling in favor of more efficient `ThreadLocal` objects created by `CvMat.createThreadLocal()`
 * Changed `Marker.getCenter()` back to the centroid, because it has better noise averaging properties and gives in practice more accurate results than the actual center
 * Added hack to `OpenCVFrameGrabber.start()` to wait for `cvRetrieveFrame()` to return something else than `null` under Mac OS X
 * FFmpeg now works properly on Windows and Android (issue #63) with newer binaries
 * New `videoInputLib` wrapper and corresponding `VideoInputFrameGrabber` to capture using DirectShow, useful under Windows 7 where OpenCV and FFmpeg can fail to capture using Video for Windows (issue #58)
 * `GeometricCalibrator` now reports the maximum errors in addition to the average (RMS) errors

### April 7, 2011
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

### February 19, 2011
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

### December 2, 2010
 * Now works on Android with the Dalvik VM (for more details, please refer to the FacePreview sample available on the download page)
 * Added more hacks to `CanvasFrame` in the hope to make it behave better outside the EDT
 * Made clearer the error messages thrown from `FrameGrabber` objects, when `start()` may not have been called
 * Fixed version specific declarations of `CvStereoBMState` and related functions
 * Fixed conditions that could crash `cvkernels`

### November 4, 2010
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

### July 30, 2010
 * Fixed crash that would occur in `CanvasFrame` for some video drivers
 * `FFmpegFrameGrabber` now supports other input formats (devices), such as `x11grab` that can be used for screencasting
 * Added `JavaCV.median()` function, and `JavaCV.fractalTriangleWave()` now respects image ROI
 * Fixed background subtraction in `cvaux`
 * Fixed crash inside the code for direct alignment caused by the ROI getting set outside the image plane
 * Added `deltaScale` and `tryToFixPlane` to `GNImageAligner.Settings` (the first used in `ImageTransformer.Parameters` as increment, randomly selected forward or backward, for finite difference), which sometimes help to jump over local minima

### May 30, 2010
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

### April 16, 2010
 * Modified `IplImage`, `FrameGrabber`, and `CanvasFrame` to get better default behavior of gamma correction
 * Fixed `cv.CvHistogram` and related histogram functions
 * `CameraDevice.Settings.triggerFlushSize` now defaults to 5 (only affects `OpenCVFrameGrabber` and `FFmpegFrameGrabber`)
 * Replaced `LMImageAligner` by `GNImageAligner`, a more appropriate name for Gauss-Newton with `lineSearch`
 * Fixed a few things related with `ProjectiveDevice.Settings`

### April 8, 2010
 * Added support for OpenCV 2.1

### April 5, 2010
 * Fixed up `clone()` methods to avoid the need to cast
 * Removed the `fullScreen` argument from `CanvasFrame` constructors, which will now switch to full-screen mode only when a `screenNumber` is explicitly passed
 * Renamed `FrameGrabber.ColorMode.GRAYSCALE` to `GRAY`
 * Replaced deprecated functions from `FFmpegFrameGrabber` and `FFmpegFrameRecorder`
 * `FFmpegFrameGrabber` can now resize images

### March 21, 2010
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

### February 13, 2010
 * Relicensed JavaCV under the GPLv2 with Classpath exception (see LICENSE.txt). Please note that if your application links with code that needs ARToolKitPlus, for example, it will become subject to the full GPL, without Classpath exception
 * Added `devicePath` setting to `CameraDevice` that works with `FFmpegFrameGrabber`, `OpenCVFrameGrabber`, and other `FrameGrabber` with a String constructor
 * Added "C:/OpenCV2.0/bin/release/" to the directory list to search for OpenCV DLLs
 * Moved `cvFindHomography()`, `cvFindExtrinsicCameraParams2()`, `cvReprojectImageTo3D()`, `cvSaveImage()`, and `cvRetrieveFrame()` to version specific classes since their number of arguments differ with the version of OpenCV
 * Enhanced `CvMat.put(CvMat mat)` to work better even when the matrices are not actually compatible
 * Added new `IplImage` factory methods `createCompatible(IplImage image)`, `createIfNotCompatible(IplImage image, IplImage template)`, and `createFrom(BufferedImage image)`
 * Fixed `distortionCoeffs` corruption that might occur in `ProjectiveDevice`

### January 3, 2010
 * Added wrapper for the `cvaux` module of OpenCV
 * Added abstract `FrameRecorder` class and a `OpenCVFrameRecorder` class
 * Fixed read() problem that might occur within Pointer constructors
 * Running `java -jar javacv.jar` now displays version information

### December 22, 2009
 * Fixed `CanvasFrame` from getting stuck in a maximized window
 * Removed all `setAutoWrite(false)` from `cxcore` now that the bug appears fixed in JNA
 * Added `FFmpegFrameGrabber` and `FFmpegFrameRecorder` to easily record live footage and grab back offline into JavaCV

### November 24, 2009
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

### October 19, 2009
 * Moved the functionality of `CvMatPool` to the `CvMat.take()` and `.pool()` methods
 * Added color calibration for projector-camera systems (`ProCamColorCalibrator`)
 * Updated `DC1394FrameGrabber` to handle more conversion use cases automatically
 * Fixed `CvIntScalar` to mirror `CvScalar`

### October 14, 2009
 * Change of plan: JavaCV now works with any of OpenCV 1.0, 1.1pre1, or 2.0! Version specific functionality is enclosed in subclasses, e.g., the class `cv.v20` can access everything from the `cv` module of OpenCV 2.0
 * Added a few missing functions and adjusted some mappings to make them closer to the C API
 * Added a few more helper methods to `CvPoint*`
 * Added temporary storage to `ObjectFinder` to plug the memory leak

### October 2, 2009
 * Fixed problem when loading distortion coefficients with `ProjectiveDevice`
 * Added automatic read and write for functions with arrays of `Structure` or `PointerByReference`
 * Added to `cv.java` a few missing functions related to calibration
 * Fixed up a bit helper methods for `CvPoint*`, `CvScalar`, `CvRect`, `CvBox2D`, `CvMat`, `IplImage`, `CvMemStorage`, `CvSeq`, and `CvSeqBlock`
 * Added `CvMatPool` to `MarkedPlane` and `Marker`
 * Added a few new `distort()` methods to `ProjectiveDevice`
 * Last version to support OpenCV 1.1pre1: Future version will require OpenCV 2.0

### August 27, 2009
 * `IplImage` now flips the buffer on copy if necessary
 * Added needed Pointer constructor for `CvSURFPoint` and `CvConvexityDefect`
 * Cleaned up a bit the messy Buffers in `CvMat`

### August 26, 2009
 * Added `get*Buffer()` functions to `IplImage`
 * Added more options for gamma correction in `IplImage` and `ProjectiveDevice`
 * Further cleaned up the namespace and constructors of `ProjectiveDevices`
 * `CanvasFrame.waitKey()` now only checks `KeyEvent.KEY_PRESSED`
 * Added `CvMatPool` to avoid recreating matrices
 * Moved `CvScalar` functions to `cxcore`

### August 19, 2009
 * Switched to using `import static` for relief from namespace hell
 * Fixed color channel reversal of Bayer images in `DC1394FrameGrabber`

### August 11, 2009
Initial release


Acknowledgments
---------------
This project was conceived at the [Okutomi & Tanaka Laboratory](http://www.ok.ctrl.titech.ac.jp/), Tokyo Institute of Technology, where I was supported for my doctoral research program by a generous scholarship from the Ministry of Education, Culture, Sports, Science and Technology (MEXT) of the Japanese Government. I extend my gratitude further to all who have reported bugs, donated code, or made suggestions for improvements (details above)!
