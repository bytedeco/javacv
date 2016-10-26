/*
 * Copyright (C) 2014 Jeremy Laviole, Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytedeco.javacv;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.RealSense;
import org.bytedeco.javacpp.RealSense.context;
import org.bytedeco.javacpp.RealSense.device;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 *
 * @author Jeremy Laviole
 */
public class RealSenseFrameGrabber extends FrameGrabber {

    public static String[] getDeviceDescriptions() throws FrameGrabber.Exception {
        tryLoad();
        String[] desc = new String[1];
        desc[0] = "No description yet.";
        return desc;
    }

    public static int DEFAULT_DEPTH_WIDTH = 640;
    public static int DEFAULT_DEPTH_HEIGHT = 480;
    public static int DEFAULT_COLOR_WIDTH = 640;
    public static int DEFAULT_COLOR_HEIGHT = 480;

    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private int depthImageWidth = DEFAULT_DEPTH_WIDTH;
    private int depthImageHeight = DEFAULT_DEPTH_HEIGHT;
    private int depthFrameRate = 60;

    private int IRImageWidth = DEFAULT_DEPTH_WIDTH;
    private int IRImageHeight = DEFAULT_DEPTH_HEIGHT;
    private int IRFrameRate = 60;

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public static RealSenseFrameGrabber createDefault(int deviceNumber) throws FrameGrabber.Exception {
        return new RealSenseFrameGrabber(deviceNumber);
    }

    public static RealSenseFrameGrabber createDefault(File deviceFile) throws Exception {
        throw new Exception(RealSenseFrameGrabber.class + " does not support File devices.");
    }

    public static RealSenseFrameGrabber createDefault(String devicePath) throws Exception {
        throw new Exception(RealSenseFrameGrabber.class + " does not support path.");
    }

    private static FrameGrabber.Exception loadingException = null;

    public static void tryLoad() throws FrameGrabber.Exception {
        if (loadingException != null) {
            loadingException.printStackTrace();
            throw loadingException;
        } else {
            try {
                if (context != null) {
                    return;
                }
                Loader.load(org.bytedeco.javacpp.RealSense.class);

                // Context is shared accross cameras. 
                context = new context();
                System.out.println("RealSense devices found: " + context.get_device_count());
            } catch (Throwable t) {
                throw loadingException = new FrameGrabber.Exception("Failed to load " + RealSenseFrameGrabber.class, t);
            }
        }
    }

    private static context context;
    private final device device;
    private boolean depth = false; // default to "video"
    private boolean colorEnabled = false;
    private boolean depthEnabled = false;
    private boolean IREnabled = false;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    public RealSenseFrameGrabber(int deviceNumber) {
        if (context == null || context.get_device_count() <= deviceNumber) {
            System.out.println("FATAL error: Realsense camera: " + deviceNumber + " not connected/found");
            System.exit(-1);
        }
        device = context.get_device(deviceNumber);
    }

    public static void main(String[] args) {
        context context = new context();
        System.out.println("Devices found: " + context.get_device_count());
        device device = context.get_device(0);
        System.out.println("Using device 0, an " + device.get_name());
        System.out.println(" Serial number: " + device.get_serial());
    }

    public void enableColorStream() {
        if (!colorEnabled) {
            if (imageWidth == 0) {
                imageWidth = DEFAULT_COLOR_WIDTH;
            }
            if (imageHeight == 0) {
                imageHeight = DEFAULT_COLOR_HEIGHT;
            }
            device.enable_stream(RealSense.color, imageWidth, imageHeight, RealSense.rgb8, (int) frameRate);
            colorEnabled = true;
        }
    }

    public void disableColorStream() {
        if (colorEnabled) {
            device.disable_stream(RealSense.color);
            colorEnabled = false;
        }
    }

    public void enableDepthStream() {
        if (!depthEnabled) {
            device.enable_stream(RealSense.depth, depthImageWidth, depthImageHeight, RealSense.z16, depthFrameRate);
            depthEnabled = true;
        }
    }

    public void disableDepthStream() {
        if (depthEnabled) {
            device.disable_stream(RealSense.depth);
            depthEnabled = false;
        }
    }

    public void enableIRStream() {
        if (!IREnabled) {
            device.enable_stream(RealSense.infrared, IRImageWidth, IRImageHeight, RealSense.y8, IRFrameRate);
            IREnabled = true;
        }
    }

    public void disableIRStream() {
        if (IREnabled) {
            device.disable_stream(RealSense.infrared);
            IREnabled = false;
        }
    }

    public void release() throws FrameGrabber.Exception {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    /**
     * Warning can lead to unsafe situations.
     *
     * @return
     */
    public device getRealSenseDevice() {
        return this.device;
    }

    public float getDepthScale() {
        return device.get_depth_scale();
    }

    @Override
    public double getFrameRate() {  // TODO: check this. 
        return super.getFrameRate();
    }

    @Override
    public void start() throws FrameGrabber.Exception {
        device.start();
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void stop() throws FrameGrabber.Exception {
        device.stop();
        frameNumber = 0;
    }
    private Pointer rawDepthImageData = new Pointer((Pointer) null),
            rawVideoImageData = new Pointer((Pointer) null),
            rawIRImageData = new Pointer((Pointer) null);
    private IplImage rawDepthImage = null, rawVideoImage = null, rawIRImage = null, returnImage = null;

    public IplImage grabDepth() throws Exception {

        if (!depthEnabled) {
            System.out.println("Depth stream not enabled, impossible to get the image.");
            return null;
        }
        rawDepthImageData = device.get_frame_data(RealSense.depth);
//        ShortBuffer bb = data.position(0).limit(640 * 480 * 2).asByteBuffer().asShortBuffer();

        int iplDepth = IPL_DEPTH_16S, channels = 1;
        int deviceWidth = device.get_stream_width(RealSense.depth);
        int deviceHeight = device.get_stream_height(RealSense.depth);

        // AUTOMATIC
//        int deviceWidth = 0;
//        int deviceHeight = 0;
        if (rawDepthImage == null || rawDepthImage.width() != deviceWidth || rawDepthImage.height() != deviceHeight) {
            rawDepthImage = IplImage.createHeader(deviceWidth, deviceHeight, iplDepth, channels);
        }
        
        cvSetData(rawDepthImage, rawDepthImageData, deviceWidth * channels * iplDepth / 8);

        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer bb = rawDepthImage.getByteBuffer();
            ShortBuffer in = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

        return rawDepthImage;
    }

    public IplImage grabVideo() {

        if (!colorEnabled) {
            System.out.println("Color stream not enabled, impossible to get the image.");
            return null;
        }

        int iplDepth = IPL_DEPTH_8U, channels = 3;

        rawVideoImageData = device.get_frame_data(RealSense.color);
        int deviceWidth = device.get_stream_width(RealSense.color);
        int deviceHeight = device.get_stream_height(RealSense.color);
        
        if (rawVideoImage == null || rawVideoImage.width() != deviceWidth || rawVideoImage.height() != deviceHeight) {
            rawVideoImage = IplImage.createHeader(deviceWidth, deviceHeight, iplDepth, channels);
        }
        
        cvSetData(rawVideoImage, rawVideoImageData, deviceWidth * channels * iplDepth / 8);

        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer bb = rawVideoImage.getByteBuffer();
            ShortBuffer in = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

//        if (channels == 3) {
//            cvCvtColor(rawVideoImage, rawVideoImage, CV_RGB2BGR);
//        }
        return rawVideoImage;
    }

    public IplImage grabIR() {

        if (!IREnabled) {
            System.out.println("IR stream not enabled, impossible to get the image.");
            return null;
        }

        int iplDepth = IPL_DEPTH_8U, channels = 1;

        rawIRImageData = device.get_frame_data(RealSense.infrared);

        int deviceWidth = device.get_stream_width(RealSense.infrared);
        int deviceHeight = device.get_stream_height(RealSense.infrared);

        
        if (rawIRImage == null || rawIRImage.width() != deviceWidth || rawIRImage.height() != deviceHeight) {
            rawIRImage = IplImage.createHeader(deviceWidth, deviceHeight, iplDepth, channels);
        }
        cvSetData(rawIRImage, rawIRImageData, deviceWidth * channels * iplDepth / 8);
        
        if (iplDepth > 8 && !ByteOrder.nativeOrder().equals(byteOrder)) {
            // ack, the camera's endianness doesn't correspond to our machine ...
            // swap bytes of 16-bit images
            ByteBuffer bb = rawIRImage.getByteBuffer();
            ShortBuffer in = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            ShortBuffer out = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            out.put(in);
        }

        return rawIRImage;
    }

    /**
     *
     * @return null grabs all images, get them with grabColor, grabDepth, and
     * grabIR instead.
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     */
    public Frame grab() throws Exception {
        device.wait_for_frames();

//        if (colorEnabled) {
//            IplImage image = grabVideo();
//            return converter.convert(image);
//        }
        return null;
    }

    @Override
    public void trigger() throws Exception {
        device.wait_for_frames();
    }

    public int getDepthImageWidth() {
        return depthImageWidth;
    }

    public void setDepthImageWidth(int depthImageWidth) {
        this.depthImageWidth = depthImageWidth;
    }

    public int getDepthImageHeight() {
        return depthImageHeight;
    }

    public void setDepthImageHeight(int depthImageHeight) {
        this.depthImageHeight = depthImageHeight;
    }

    public int getIRImageWidth() {
        return IRImageWidth;
    }

    public void setIRImageWidth(int IRImageWidth) {
        this.IRImageWidth = IRImageWidth;
    }

    public int getIRImageHeight() {
        return IRImageHeight;
    }

    public void setIRImageHeight(int IRImageHeight) {
        this.IRImageHeight = IRImageHeight;
    }

    public int getDepthFrameRate() {
        return depthFrameRate;
    }

    public void setDepthFrameRate(int frameRate) {
        this.depthFrameRate = frameRate;
    }

// --- Presets --- 
    public void setPreset(int preset) {
        /* Provide access to several recommend sets of option presets for ivcam */
        RealSense.apply_ivcam_preset(device, preset);
    }

    public void setShortRange() {
        setPreset(RealSense.RS_IVCAM_PRESET_SHORT_RANGE);
    }

    public void setLongRange() {
        setPreset(RealSense.RS_IVCAM_PRESET_LONG_RANGE);
    }

    public void setMidRange() {
        setPreset(RealSense.RS_IVCAM_PRESET_MID_RANGE);
    }

    public void setDefaultPreset() {
        setPreset(RealSense.RS_IVCAM_PRESET_DEFAULT);
    }

    public void setObjectScanningPreset() {
        setPreset(RealSense.RS_IVCAM_PRESET_OBJECT_SCANNING);
    }

    public void setCursorPreset() {
        setPreset(RealSense.RS_IVCAM_PRESET_GR_CURSOR);
    }

    public void setGestureRecognitionPreset() {
        setPreset(RealSense.RS_IVCAM_PRESET_GESTURE_RECOGNITION);
    }

    public void setBackgroundSegmentationPreset() {
        setPreset(RealSense.RS_IVCAM_PRESET_BACKGROUND_SEGMENTATION);
    }

    public void setIROnlyPreset() {
        setPreset(RealSense.RS_IVCAM_PRESET_IR_ONLY);
    }

    // ---- Options ---- 
    public void setOption(int option, int value) {
        device.set_option(option, value);
    }

    /**
     * Enable / disable color backlight compensation
     */
    public void set(int value) {
        setOption(RealSense.RS_OPTION_COLOR_BACKLIGHT_COMPENSATION, value);
    }

    /**
     * Color image brightness
     */
    public void setColorBrightness(int value) {
        setOption(RealSense.RS_OPTION_COLOR_BRIGHTNESS, value);
    }

    /**
     * COLOR image contrast
     */
    public void setColorContrast(int value) {
        setOption(RealSense.RS_OPTION_COLOR_CONTRAST, value);
    }

    /**
     * Controls exposure time of color camera. Setting any value will disable
     * auto exposure
     */
    public void setColorExposure(int value) {
        setOption(RealSense.RS_OPTION_COLOR_EXPOSURE, value);
    }

    /**
     * Color image gain
     */
    public void setColorGain(int value) {
        setOption(RealSense.RS_OPTION_COLOR_GAIN, value);
    }

    /**
     * Color image gamma setting
     */
    public void setColorGamma(int value) {
        setOption(RealSense.RS_OPTION_COLOR_GAMMA, value);
    }

    /**
     * Color image hue
     */
    public void setColorHue(int value) {
        setOption(RealSense.RS_OPTION_COLOR_HUE, value);
    }

    /**
     * Color image saturation setting
     */
    public void setColorSaturation(int value) {
        setOption(RealSense.RS_OPTION_COLOR_SATURATION, value);
    }

    /**
     * Color image sharpness setting
     */
    public void setColorSharpness(int value) {
        setOption(RealSense.RS_OPTION_COLOR_SHARPNESS, value);
    }

    /**
     * Controls white balance of color image. Setting any value will disable
     * auto white balance
     */
    public void setColorWhiteBalance(int value) {
        setOption(RealSense.RS_OPTION_COLOR_WHITE_BALANCE, value);
    }

    /**
     * Enable / disable color image auto-exposure
     */
    public void setColorEnableAutoExposure(int value) {
        setOption(RealSense.RS_OPTION_COLOR_ENABLE_AUTO_EXPOSURE, value);
    }

    /**
     * Enable / disable color image auto-white-balance
     */
    public void setColorEnableAutoWhiteBalance(int value) {
        setOption(RealSense.RS_OPTION_COLOR_ENABLE_AUTO_WHITE_BALANCE, value);
    }

    /**
     * Power of the F200 / SR300 projector, with 0 meaning projector off
     */
    public void setLaserPower(int value) {
        setOption(RealSense.RS_OPTION_F200_LASER_POWER, value);
    }

    /**
     * Set the number of patterns projected per frame. The higher the accuracy
     * value the more patterns projected. Increasing the number of patterns help
     * to achieve better accuracy. Note that this control is affecting the Depth
     * FPS
     */
    public void setAccuracy(int value) {
        setOption(RealSense.RS_OPTION_F200_ACCURACY, value);
    }

    /**
     * Motion vs. Range trade-off, with lower values allowing for better motion
     * sensitivity and higher values allowing for better depth range
     */
    public void setMotionRange(int value) {
        setOption(RealSense.RS_OPTION_F200_MOTION_RANGE, value);
    }

    /**
     * Set the filter to apply to each depth frame. Each one of the filter is
     * optimized per the application requirements
     */
    public void setFilterOption(int value) {
        setOption(RealSense.RS_OPTION_F200_FILTER_OPTION, value);
    }

    /**
     * The confidence level threshold used by the Depth algorithm pipe to set
     * whether a pixel will get a valid range or will be marked with invalid
     * range
     */
    public void setConfidenceThreshold(int value) {
        setOption(RealSense.RS_OPTION_F200_CONFIDENCE_THRESHOLD, value);
    }

    /**
     * (F200-only) Allows to reduce FPS without restarting streaming. Valid
     * values are {2, 5, 15, 30, 60}
     */
    public void setDynamicFPS(int value) {
        setOption(RealSense.RS_OPTION_F200_DYNAMIC_FPS, value);
    }

    /**
     * Enables / disables R200 auto-exposure. This will affect both IR and depth
     * image.
     */
    public void setLR_AutoExposureEnabled(int value) {
        setOption(RealSense.RS_OPTION_R200_LR_AUTO_EXPOSURE_ENABLED, value);
    }

    /**
     * IR image gain
     */
    public void setLR_Gain(int value) {
        setOption(RealSense.RS_OPTION_R200_LR_GAIN, value);
    }

    /**
     * This control allows manual adjustment of the exposure time value for the
     * L/R imagers
     */
    public void setLR_Exposure(int value) {
        setOption(RealSense.RS_OPTION_R200_LR_EXPOSURE, value);
    }

    /**
     * Enables / disables R200 emitter
     */
    public void setEmitterEnabled(int value) {
        setOption(RealSense.RS_OPTION_R200_EMITTER_ENABLED, value);
    }

    /**
     * Micrometers per increment in integer depth values, 1000 is default (mm
     * scale). Set before streaming
     */
    public void setDepthUnits(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_UNITS, value);
    }

    /**
     * Minimum depth in current depth units that will be output. Any values less
     * than �Min Depth� will be mapped to 0 during the conversion between
     * disparity and depth. Set before streaming
     */
    public void setDepthClampMin(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CLAMP_MIN, value);
    }

    /**
     * Maximum depth in current depth units that will be output. Any values
     * greater than �Max Depth� will be mapped to 0 during the conversion
     * between disparity and depth. Set before streaming
     */
    public void setDepthClampMax(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CLAMP_MAX, value);
    }

    /**
     * The disparity scale factor used when in disparity output mode. Can only
     * be set before streaming
     */
    public void setDisparityMultiplier(int value) {
        setOption(RealSense.RS_OPTION_R200_DISPARITY_MULTIPLIER, value);
    }

    /**
     * {0 - 512}. Can only be set before streaming starts
     */
    public void setDisparityShift(int value) {
        setOption(RealSense.RS_OPTION_R200_DISPARITY_SHIFT, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Mean intensity set point
     */
    public void setAutoExposureMeanIntensitySetPoint(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_MEAN_INTENSITY_SET_POINT, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Bright ratio set point
     */
    public void setAutoExposureBrightRatioSetPoint(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_BRIGHT_RATIO_SET_POINT, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Kp Gain
     */
    public void setAutoExposureKpGain(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_KP_GAIN, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Kp Exposure
     */
    public void setAutoExposureKpExposure(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_KP_EXPOSURE, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Kp Dark Threshold
     */
    public void setAutoExposureKpDarkThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_KP_DARK_THRESHOLD, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Auto-Exposure region-of-interest top edge
     * (in pixels)
     */
    public void setAutoExposureTopEdge(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_TOP_EDGE, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Auto-Exposure region-of-interest bottom
     * edge (in pixels)
     */
    public void setAutoExposureBottomEdge(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_BOTTOM_EDGE, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Auto-Exposure region-of-interest left edge
     * (in pixels)
     */
    public void setAutoExposureLeftEdge(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_LEFT_EDGE, value);
    }

    /**
     * (Requires LR-Auto-Exposure ON) Auto-Exposure region-of-interest right
     * edge (in pixels)
     */
    public void setAutoExposureRightEdge(int value) {
        setOption(RealSense.RS_OPTION_R200_AUTO_EXPOSURE_RIGHT_EDGE, value);
    }

    /**
     * Value to subtract when estimating the median of the correlation surface
     */
    public void setDepthControlEstimateMedianDecrement(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_ESTIMATE_MEDIAN_DECREMENT, value);
    }

    /**
     * Value to add when estimating the median of the correlation surface
     */
    public void setDepthControlEstimateMedianIncrement(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_ESTIMATE_MEDIAN_INCREMENT, value);
    }

    /**
     * A threshold by how much the winning score must beat the median
     */
    public void setDepthControlMedianThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_MEDIAN_THRESHOLD, value);
    }

    /**
     * The minimum correlation score that is considered acceptable
     */
    public void setDepthControlMinimumThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_SCORE_MINIMUM_THRESHOLD, value);
    }

    /**
     * The maximum correlation score that is considered acceptable
     */
    public void setDepthControlScoreMaximumThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_SCORE_MAXIMUM_THRESHOLD, value);
    }

    /**
     * A parameter for determining whether the texture in the region is
     * sufficient to justify a depth result
     */
    public void setDepthControlTextureCountThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_TEXTURE_COUNT_THRESHOLD, value);
    }

    /**
     * A parameter for determining whether the texture in the region is
     * sufficient to justify a depth result
     */
    public void setDepthControlTextureDifference(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_TEXTURE_DIFFERENCE_THRESHOLD, value);
    }

    /**
     * A threshold on how much the minimum correlation score must differ from
     * the next best score
     */
    public void setDepthControlSecondPeakThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_SECOND_PEAK_THRESHOLD, value);
    }

    /**
     * Neighbor threshold value for depth calculation
     */
    public void setDepthControlNeighborThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_NEIGHBOR_THRESHOLD, value);
    }

    /**
     * Left-Right threshold value for depth calculation
     */
    public void setDepthControlLRThreshold(int value) {
        setOption(RealSense.RS_OPTION_R200_DEPTH_CONTROL_LR_THRESHOLD, value);
    }

    /**
     * Fisheye image exposure time in msec
     */
    public void setFisheyeExposure(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_EXPOSURE, value);
    }

    /**
     * Fisheye image gain
     */
    public void setFisheyeGain(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_GAIN, value);
    }

    /**
     * Enables / disables fisheye strobe. When enabled this will align
     * timestamps to common clock-domain with the motion events
     */
    public void setFisheyeStobe(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_STROBE, value);
    }

    /**
     * Enables / disables fisheye external trigger mode. When enabled fisheye
     * image will be aquired in-sync with the depth image
     */
    public void setFisheyeExternalTrigger(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_EXTERNAL_TRIGGER, value);
    }

    /**
     * Enable / disable fisheye auto-exposure
     */
    public void setFisheyeEnableAutoExposure(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_ENABLE_AUTO_EXPOSURE, value);
    }

    /**
     * 0 - static auto-exposure, 1 - anti-flicker auto-exposure, 2 - hybrid
     */
    public void setFisheyeAutoExposureMode(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_AUTO_EXPOSURE_MODE, value);
    }

    /**
     * Fisheye auto-exposure anti-flicker rate, can be 50 or 60 Hz
     */
    public void setFisheyeAutoExposureAntiflickerRate(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_AUTO_EXPOSURE_ANTIFLICKER_RATE, value);
    }

    /**
     * In Fisheye auto-exposure sample frame every given number of pixels
     */
    public void setFisheyeAutoExposurePixelSampleRate(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_AUTO_EXPOSURE_PIXEL_SAMPLE_RATE, value);
    }

    /**
     * In Fisheye auto-exposure sample every given number of frames
     */
    public void setFisheyeAutoExposureSkipFrames(int value) {
        setOption(RealSense.RS_OPTION_FISHEYE_AUTO_EXPOSURE_SKIP_FRAMES, value);
    }

    /**
     * Number of frames the user is allowed to keep per stream. Trying to
     * hold-on to more frames will cause frame-drops.
     */
    public void setFramesQueueSize(int value) {
        setOption(RealSense.RS_OPTION_FRAMES_QUEUE_SIZE, value);
    }

    /**
     * Enable / disable fetching log data from the device
     */
    public void setHardwareLoggerEnabled(int value) {
        setOption(RealSense.RS_OPTION_HARDWARE_LOGGER_ENABLED, value);
    }

}
