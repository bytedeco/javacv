/*
 * Copyright (C) 2009-2016 Samuel Audet
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

import org.bytedeco.libdc1394.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.libdc1394.global.dc1394.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class DC1394FrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();

        dc1394_t d = dc1394_new();
        if (d == null) {
            throw new Exception("dc1394_new() Error: Failed to initialize libdc1394.");
        }
        dc1394camera_list_t list = new dc1394camera_list_t(null);
        int err = dc1394_camera_enumerate(d, list);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_camera_enumerate() Error " + err + ": Failed to enumerate cameras.");
        }
        int num = list.num();
        String[] descriptions = new String[num];

        if (num > 0) {
            dc1394camera_id_t ids = list.ids();
            for (int i = 0; i < num; i ++) {
                ids.position(i);
                dc1394camera_t camera = dc1394_camera_new_unit(d, ids.guid(), ids.unit());
                if (camera == null) {
                    throw new Exception("dc1394_camera_new_unit() Error: Failed to initialize camera with GUID 0x" +
                            Long.toHexString(ids.guid())+ " / " + camera.unit() + ".");
                }
                descriptions[i] = camera.vendor().getString() + " " + camera.model().getString() + " 0x" +
                        Long.toHexString(camera.guid()) + " / " + camera.unit();
                dc1394_camera_free(camera);
            }
        }

        dc1394_camera_free_list(list);
        dc1394_free(d);
        return descriptions;
    }

    public static DC1394FrameGrabber createDefault(File deviceFile)   throws Exception { throw new Exception(DC1394FrameGrabber.class + " does not support device files."); }
    public static DC1394FrameGrabber createDefault(String devicePath) throws Exception { throw new Exception(DC1394FrameGrabber.class + " does not support device paths."); }
    public static DC1394FrameGrabber createDefault(int deviceNumber)  throws Exception { return new DC1394FrameGrabber(deviceNumber); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.libdc1394.global.dc1394.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + DC1394FrameGrabber.class, t);
            }
        }
    }

    public DC1394FrameGrabber(int deviceNumber) throws Exception {
        d = dc1394_new();
        dc1394camera_list_t list = new dc1394camera_list_t(null);
        int err = dc1394_camera_enumerate (d, list);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_camera_enumerate() Error " + err + ": Failed to enumerate cameras.");
        }
        int num = list.num();
        if (num <= deviceNumber) {
            throw new Exception("DC1394Grabber() Error: Camera number " + deviceNumber +
                    " not found. There are only " + num + " devices.");
        }
        dc1394camera_id_t ids = list.ids().position(deviceNumber);
        camera = dc1394_camera_new_unit(d, ids.guid(), ids.unit());
        if (camera == null) {
            throw new Exception("dc1394_camera_new_unit() Error: Failed to initialize camera with GUID 0x" +
                    Long.toHexString(ids.guid())+ " / " + camera.unit() + ".");
        }
        dc1394_camera_free_list(list);
//System.out.println("Using camera with GUID 0x" + Long.toHexString(camera.guid) + " / " + camera.unit);
    }
    public void release() throws Exception {
        if (camera != null) {
            stop();
            dc1394_camera_free(camera);
            camera = null;
        }
        if (d != null) {
            dc1394_free(d);
            d = null;
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private static final boolean linux = Loader.getPlatform().startsWith("linux");
    private dc1394_t d = null;
    private dc1394camera_t camera = null;
    private pollfd fds = linux ? new pollfd() : null;
    private boolean oneShotMode = false;
    private boolean resetDone   = false;
    private dc1394video_frame_t[] raw_image =
        { new dc1394video_frame_t(null), new dc1394video_frame_t(null) };
    private dc1394video_frame_t conv_image = new dc1394video_frame_t();
    private dc1394video_frame_t frame = null;
    private dc1394video_frame_t enqueue_image = null;
    private IplImage temp_image, return_image = null;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();
    private final int[] out = new int[1];
    private final float[] outFloat = new float[1];
    private final float[] gammaOut = new float[1];

    @Override public double getGamma() {
        return Float.isNaN(gammaOut[0]) || Float.isInfinite(gammaOut[0]) || gammaOut[0] == 0.0f ? 2.2 : gammaOut[0];
    }

    @Override public int getImageWidth() {
        return return_image == null ? super.getImageWidth() : return_image.width();
    }

    @Override public int getImageHeight() {
        return return_image == null ? super.getImageHeight() : return_image.height();
    }

    @Override public double getFrameRate() {
        if (camera == null) {
            return super.getFrameRate();
        } else {
            if (dc1394_feature_get_absolute_value(camera, 
                    DC1394_FEATURE_FRAME_RATE, outFloat) != DC1394_SUCCESS) {
                dc1394_video_get_framerate(camera, out);
                dc1394_framerate_as_float(out[0], outFloat);
            }
            return outFloat[0];
        }
    }

    @Override public void setImageMode(ImageMode imageMode) {
        if (imageMode != this.imageMode) {
            temp_image = null;
            return_image = null;
        }
        super.setImageMode(imageMode);
    }

    public void start() throws Exception {
        start(true, true);
    }
    public void start(boolean tryReset, boolean try1394b) throws Exception {
        int c = -1;
        if (imageMode == ImageMode.COLOR || imageMode == ImageMode.RAW) {
            if (imageWidth <= 0 || imageHeight <= 0) {
                c = -1;
            } else if (imageWidth <= 640 && imageHeight <= 480) {
                c = DC1394_VIDEO_MODE_640x480_RGB8;
            } else if (imageWidth <= 800 && imageHeight <= 600) {
                c = DC1394_VIDEO_MODE_800x600_RGB8;
            } else if (imageWidth <= 1024 && imageHeight <= 768) {
                c = DC1394_VIDEO_MODE_1024x768_RGB8;
            } else if (imageWidth <= 1280 && imageHeight <= 960) {
                c = DC1394_VIDEO_MODE_1280x960_RGB8;
            } else if (imageWidth <= 1600 && imageHeight <= 1200) {
                c = DC1394_VIDEO_MODE_1600x1200_RGB8;
            }
        } else if (imageMode == ImageMode.GRAY) {
            if (imageWidth <= 0 || imageHeight <= 0) {
                c = -1;
            } else if (imageWidth <= 640 && imageHeight <= 480) {
                c = bpp > 8 ? DC1394_VIDEO_MODE_640x480_MONO16 : DC1394_VIDEO_MODE_640x480_MONO8;
            } else if (imageWidth <= 800 && imageHeight <= 600) {
                c = bpp > 8 ? DC1394_VIDEO_MODE_800x600_MONO16 : DC1394_VIDEO_MODE_800x600_MONO8;
            } else if (imageWidth <= 1024 && imageHeight <= 768) {
                c = bpp > 8 ? DC1394_VIDEO_MODE_1024x768_MONO16 : DC1394_VIDEO_MODE_1024x768_MONO8;
            } else if (imageWidth <= 1280 && imageHeight <= 960) {
                c = bpp > 8 ? DC1394_VIDEO_MODE_1280x960_MONO16 : DC1394_VIDEO_MODE_1280x960_MONO8;
            } else if (imageWidth <= 1600 && imageHeight <= 1200) {
                c = bpp > 8 ? DC1394_VIDEO_MODE_1600x1200_MONO16 : DC1394_VIDEO_MODE_1600x1200_MONO8;
            }
        }
        
        if (c == -1) {
            // otherwise, still need to set current video mode to kick start the ISO channel...
            dc1394_video_get_mode(camera, out);
            c = out[0];
        }

        int f = -1;
        if (frameRate <= 0) {
            f = -1;
        } else if (frameRate <= 1.876) {
            f = DC1394_FRAMERATE_1_875;
        } else if (frameRate <= 3.76) {
            f = DC1394_FRAMERATE_3_75;
        } else if (frameRate <= 7.51) {
            f = DC1394_FRAMERATE_7_5;
        } else if (frameRate <= 15.01) {
            f = DC1394_FRAMERATE_15;
        } else if (frameRate <= 30.01) {
            f = DC1394_FRAMERATE_30;
        } else if (frameRate <= 60.01) {
            f = DC1394_FRAMERATE_60;
        } else if (frameRate <= 120.01) {
            f = DC1394_FRAMERATE_120;
        } else if (frameRate <= 240.01) {
            f = DC1394_FRAMERATE_240;
        }

        if (f == -1) {
            // otherwise, still need to set current framerate to kick start the ISO channel...
            dc1394_video_get_framerate(camera, out);
            f = out[0];
        }

        try {
            oneShotMode = false;
            if (triggerMode) {
                int err = dc1394_external_trigger_set_power(camera, DC1394_ON);
                if (err != DC1394_SUCCESS) {
                    // no trigger support, use one-shot mode instead
                    oneShotMode = true;
                } else {
                    err = dc1394_external_trigger_set_mode(camera, DC1394_TRIGGER_MODE_14);
                    if (err != DC1394_SUCCESS) {
                        // try with trigger mode 0 instead
                        err = dc1394_external_trigger_set_mode(camera, DC1394_TRIGGER_MODE_0);
                    }
                    err = dc1394_external_trigger_set_source(camera, DC1394_TRIGGER_SOURCE_SOFTWARE);
                    if (err != DC1394_SUCCESS) {
                        // no support for software trigger, use one-shot mode instead
                        oneShotMode = true;
                        dc1394_external_trigger_set_power(camera, DC1394_OFF);
                    }
                }
            }

            int err = dc1394_video_set_operation_mode(camera, DC1394_OPERATION_MODE_LEGACY);
            if (try1394b) {
                err = dc1394_video_set_operation_mode(camera, DC1394_OPERATION_MODE_1394B);
                if (err == DC1394_SUCCESS) {
                    err = dc1394_video_set_iso_speed(camera, DC1394_ISO_SPEED_800);
                }
            }
            if (err != DC1394_SUCCESS || !try1394b) {
                err = dc1394_video_set_iso_speed(camera, DC1394_ISO_SPEED_400);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_video_set_iso_speed() Error " + err + ": Could not set maximum iso speed.");
                }
            }

            err = dc1394_video_set_mode(camera, c);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_video_set_mode() Error " + err + ": Could not set video mode.");
            }

            if (dc1394_is_video_mode_scalable(c) == DC1394_TRUE) {
                err = dc1394_format7_set_roi(camera, c,
                        DC1394_QUERY_FROM_CAMERA, DC1394_QUERY_FROM_CAMERA,
                        DC1394_QUERY_FROM_CAMERA, DC1394_QUERY_FROM_CAMERA,
                        DC1394_QUERY_FROM_CAMERA, DC1394_QUERY_FROM_CAMERA);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_format7_set_roi() Error " + err + ": Could not set format7 mode.");
                }
            } else {
                err = dc1394_video_set_framerate(camera, f);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_video_set_framerate() Error " + err + ": Could not set framerate.");
                }
            }

            err = dc1394_capture_setup(camera, numBuffers, DC1394_CAPTURE_FLAGS_DEFAULT);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_capture_setup() Error " + err + ": Could not setup camera-\n" +
                        "make sure that the video mode and framerate are\nsupported by your camera.");
            }

            if (gamma != 0.0) {
                err = dc1394_feature_set_absolute_value(camera, DC1394_FEATURE_GAMMA, (float)gamma);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_feature_set_absolute_value() Error " + err + ": Could not set gamma.");
                }
            }
            err = dc1394_feature_get_absolute_value(camera, DC1394_FEATURE_GAMMA, gammaOut);
            if (err != DC1394_SUCCESS) {
                gammaOut[0] = 2.2f;
            }

            if (linux) {
                fds.fd(dc1394_capture_get_fileno(camera));
            }

            if (!oneShotMode) {
                err = dc1394_video_set_transmission(camera, DC1394_ON);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_video_set_transmission() Error " + err + ": Could not start camera iso transmission.");
                }
            }
        } catch (Exception e) {
            // if we couldn't start, try again with a bus reset
            if (tryReset && !resetDone) {
                try {
                    dc1394_reset_bus(camera);
                    Thread.sleep(100);
                    resetDone = true;
                    start(false, try1394b);
                } catch (InterruptedException ex) {
                    // reset interrupt to be nice
                    Thread.currentThread().interrupt();
                    throw new Exception("dc1394_reset_bus() Error: Could not reset bus and try to start again.", ex);
                }
            } else {
                throw e;
            }
        } finally {
            resetDone = false;
        }

        if (linux && try1394b) {
            if (triggerMode) {
                trigger();
            }
            fds.events(POLLIN);
            if (poll(fds, 1, timeout) == 0) {
                // we are obviously not getting anything..
                // try again without 1394b
                stop();
                start(tryReset, false);
            } else if (triggerMode) {
                grab();
                enqueue();
            }
        }
    }

    public void stop() throws Exception {
        enqueue_image = null;
        temp_image    = null;
        return_image  = null;
        timestamp   = 0;
        frameNumber = 0;

        int err = dc1394_video_set_transmission(camera, DC1394_OFF);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_video_set_transmission() Error " + err + ": Could not stop the camera?");
        }
        err = dc1394_capture_stop(camera);
        if (err != DC1394_SUCCESS && err != DC1394_CAPTURE_IS_NOT_SET) {
            throw new Exception("dc1394_capture_stop() Error " + err + ": Could not stop the camera?");
        }
        err = dc1394_external_trigger_get_mode(camera, out);
        if (err == DC1394_SUCCESS && out[0] >= DC1394_TRIGGER_MODE_0) {
            err = dc1394_external_trigger_set_power(camera, DC1394_OFF);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_external_trigger_set_power() Error " + err + ": Could not switch off external trigger.");
            }
        }
    }

    private void enqueue() throws Exception {
        enqueue(enqueue_image);
        enqueue_image = null;
    }
    private void enqueue(dc1394video_frame_t image) throws Exception {
        if (image != null) {
            int err = dc1394_capture_enqueue(camera, image);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_capture_enqueue() Error " + err + ": Could not release a frame.");
            }
        }
    }

    public void trigger() throws Exception {
        enqueue();
        if (oneShotMode) {
            int err = dc1394_video_set_one_shot(camera, DC1394_ON);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_video_set_one_shot() Error " + err + ": Could not set camera into one-shot mode.");
            }
        } else {
            long time = System.currentTimeMillis();
            do {
                dc1394_software_trigger_get_power(camera, out);
                if (System.currentTimeMillis() - time > timeout) {
                    break;
                    //throw new Exception("trigger() Error: Timeout occured.");
                }
            } while (out[0] == DC1394_ON);
            int err = dc1394_software_trigger_set_power(camera, DC1394_ON);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_software_trigger_set_power() Error " + err + ": Could not trigger camera.");
            }
        }
    }

    public Frame grab() throws Exception {
        enqueue();
        if (linux) {
            fds.events(POLLIN);
            if (poll(fds, 1, timeout) == 0) {
                throw new Exception("poll() Error: Timeout occured. (Has start() been called?)");
            }
        }
        int i = 0;
        int err = dc1394_capture_dequeue(camera, DC1394_CAPTURE_POLICY_WAIT, raw_image[i]);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_capture_dequeue(WAIT) Error " + err + ": Could not capture a frame. (Has start() been called?)");
        }
        // try to poll for more images, to get the most recent one...
        int numDequeued = 0;
        while (!raw_image[i].isNull()) {
            enqueue();
            enqueue_image = raw_image[i];
            i = (i+1)%2;
            numDequeued++;
            err = dc1394_capture_dequeue(camera, DC1394_CAPTURE_POLICY_POLL, raw_image[i]);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_capture_dequeue(POLL) Error " + err + ": Could not capture a frame.");
            }
        }
        frame = raw_image[(i+1)%2];
        int w = frame.size(0);
        int h = frame.size(1);
        int depth = frame.data_depth();
        int iplDepth = 0;
        switch (depth) {
            case 8:  iplDepth = IPL_DEPTH_8U;  break;
            case 16: iplDepth = IPL_DEPTH_16U; break;
            default: assert false;
        }
        int stride = frame.stride();
        int size = frame.image_bytes();
        int numChannels = stride/w*8/depth;
        ByteOrder frameEndian = frame.little_endian() != 0 ?
                ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        boolean alreadySwapped = false;
        int color_coding = frame.color_coding();
        boolean colorbayer = color_coding == DC1394_COLOR_CODING_RAW8 ||
                             color_coding == DC1394_COLOR_CODING_RAW16;
        boolean colorrgb   = color_coding == DC1394_COLOR_CODING_RGB8 ||
                             color_coding == DC1394_COLOR_CODING_RGB16;
        boolean coloryuv   = color_coding == DC1394_COLOR_CODING_YUV411 ||
                             color_coding == DC1394_COLOR_CODING_YUV422 ||
                             color_coding == DC1394_COLOR_CODING_YUV444;
        BytePointer imageData = frame.image();

        if ((depth <= 8 || frameEndian.equals(ByteOrder.nativeOrder())) && !coloryuv &&
                (imageMode == ImageMode.RAW || (imageMode == ImageMode.COLOR && numChannels == 3) ||
                (imageMode == ImageMode.GRAY && numChannels == 1 && !colorbayer))) {
            if (return_image == null) {
                return_image = IplImage.createHeader(w, h, iplDepth, numChannels);
            }
            return_image.widthStep(stride);
            return_image.imageSize(size);
            return_image.imageData(imageData);
        } else {
            // in the padding, there's sometimes timeframe information and stuff
            // that libdc1394 will copy for us, so we need to allocate it
            int padding_bytes = frame.padding_bytes();
            int padding1 = (int)Math.ceil((double)padding_bytes/(w * depth/8));
            int padding3 = (int)Math.ceil((double)padding_bytes/(w*3*depth/8));
            if (return_image == null) {
                int c       = imageMode == ImageMode.COLOR ? 3 : 1;
                int padding = imageMode == ImageMode.COLOR ? padding3 : padding1;
                return_image = IplImage.create(w, h+padding, iplDepth, c);
                return_image.height(return_image.height() - padding);
            }
            if (temp_image == null) {
                if (imageMode == ImageMode.COLOR &&
                        (numChannels > 1 || depth > 8) && !coloryuv && !colorbayer) {
                    temp_image = IplImage.create(w, h+padding1, iplDepth, numChannels);
                    temp_image.height(temp_image.height() - padding1);
                } else if (imageMode == ImageMode.GRAY &&
                        (coloryuv || colorbayer || (colorrgb && depth > 8))) {
                    temp_image = IplImage.create(w, h+padding3, iplDepth, 3);
                    temp_image.height(temp_image.height() - padding3);
                } else if (imageMode == ImageMode.GRAY && colorrgb) {
                    temp_image = IplImage.createHeader(w, h, iplDepth, 3);
                } else if (imageMode == ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer) {
                    temp_image = IplImage.createHeader(w, h, iplDepth, 1);
                } else {
                    temp_image = return_image;
                }
            }
            conv_image.size(0, temp_image.width());
            conv_image.size(1, temp_image.height());
            if (depth > 8) {
                conv_image.color_coding(imageMode == ImageMode.RAW  ? DC1394_COLOR_CODING_RAW16  :
                                        temp_image.nChannels() == 1 ? DC1394_COLOR_CODING_MONO16 :
                                                                      DC1394_COLOR_CODING_RGB16);
                conv_image.data_depth(16);
            } else {
                conv_image.color_coding(imageMode == ImageMode.RAW  ? DC1394_COLOR_CODING_RAW8   :
                                        temp_image.nChannels() == 1 ? DC1394_COLOR_CODING_MONO8  :
                                                                      DC1394_COLOR_CODING_RGB8);
                conv_image.data_depth(8);
            }
            conv_image.stride(temp_image.widthStep());
            int temp_size = temp_image.imageSize();
            conv_image.allocated_image_bytes(temp_size).
                    total_bytes(temp_size).image_bytes(temp_size);
            conv_image.image(temp_image.imageData());

            if (colorbayer) {
                // from raw Bayer... invert R and B to get BGR images
                // (like OpenCV wants them) instead of RGB
                int c = frame.color_filter();
                if (c               == DC1394_COLOR_FILTER_RGGB) {
                    frame.color_filter(DC1394_COLOR_FILTER_BGGR);
                } else if (c        == DC1394_COLOR_FILTER_GBRG) {
                    frame.color_filter(DC1394_COLOR_FILTER_GRBG);
                } else if (c        == DC1394_COLOR_FILTER_GRBG) {
                    frame.color_filter(DC1394_COLOR_FILTER_GBRG);
                } else if (c        == DC1394_COLOR_FILTER_BGGR) {
                    frame.color_filter(DC1394_COLOR_FILTER_RGGB);
                } else {
                    assert false;
                }
                // other better methods than "simple" give garbage at 16 bits..
                err = dc1394_debayer_frames(frame, conv_image, DC1394_BAYER_METHOD_SIMPLE);
                frame.color_filter(c);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_debayer_frames() Error " + err + ": Could not debayer frame.");
                }
            } else if (depth > 8 &&
                    frame.data_depth()   == conv_image.data_depth() &&
                    frame.color_coding() == conv_image.color_coding() &&
                    frame.stride()       == conv_image.stride()) {
                // we just need a copy to swap bytes..
                ShortBuffer in  = frame.getByteBuffer().order(frameEndian).asShortBuffer();
                ShortBuffer out = temp_image.getByteBuffer().order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
                alreadySwapped = true;
            } else if ((imageMode == ImageMode.GRAY && colorrgb) ||
                    (imageMode == ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer)) {
                temp_image.widthStep(stride);
                temp_image.imageSize(size);
                temp_image.imageData(imageData);
            } else if (!colorrgb && (colorbayer || coloryuv || numChannels > 1)) {
                // from YUV, etc.
                err = dc1394_convert_frames(frame, conv_image);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_convert_frames() Error " + err + ": Could not convert frame.");
                }
            }

            if (!alreadySwapped && depth > 8 && !frameEndian.equals(ByteOrder.nativeOrder())) {
                // ack, the camera's endianness doesn't correspond to our machine ...
                // swap bytes of 16-bit images
                ByteBuffer  bb  = temp_image.getByteBuffer();
                ShortBuffer in  = bb.order(frameEndian).asShortBuffer();
                ShortBuffer out = bb.order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
            }

            // should we copy the padding as well?
            if (imageMode == ImageMode.COLOR && numChannels == 1 && !coloryuv && !colorbayer) {
                cvCvtColor(temp_image, return_image, CV_GRAY2BGR);
            } else if (imageMode == ImageMode.GRAY && (colorbayer || colorrgb || coloryuv)) {
                cvCvtColor(temp_image, return_image, CV_BGR2GRAY);
            }
        }

        switch (frame.color_filter()) {
            case DC1394_COLOR_FILTER_RGGB: sensorPattern = SENSOR_PATTERN_RGGB; break;
            case DC1394_COLOR_FILTER_GBRG: sensorPattern = SENSOR_PATTERN_GBRG; break;
            case DC1394_COLOR_FILTER_GRBG: sensorPattern = SENSOR_PATTERN_GRBG; break;
            case DC1394_COLOR_FILTER_BGGR: sensorPattern = SENSOR_PATTERN_BGGR; break;
            default: sensorPattern = -1L;
        }

        enqueue_image = frame;
        timestamp = frame.timestamp();
        frameNumber += numDequeued;
//        int[] cycle_timer = { 0 };
//        long[] local_time = { 0 };
//        dc1394_read_cycle_timer(camera, cycle_timer, local_time);
//System.out.println("frame age = " + (local_time[0] - timestamp));
        return converter.convert(return_image);
    }
}
