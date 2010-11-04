/*
 * Copyright (C) 2009,2010 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.javacv;

import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;
import static com.googlecode.javacv.jna.dc1394.*;

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
        dc1394camera_list_t.PointerByReference list = new dc1394camera_list_t.PointerByReference();
        int err=dc1394_camera_enumerate (d, list);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_camera_enumerate() Error " + err + ": Failed to enumerate cameras.");
        }
        dc1394camera_list_t l = list.getStructure();
        String[] descriptions = new String[l.num];

        if (l.num > 0) {
            dc1394camera_id_t[] ids = (dc1394camera_id_t[])(l.ids.toArray(l.num));
            for (int i = 0; i < l.num; i ++) {
                dc1394camera_t camera = dc1394_camera_new_unit(d, ids[i].guid, ids[i].unit);
                if (camera == null) {
                    throw new Exception("dc1394_camera_new_unit() Error: Failed to initialize camera with GUID 0x" +
                            Long.toHexString(ids[i].guid)+ " / " + camera.unit + ".");
                }
                descriptions[i] = camera.vendor + " " + camera.model + " 0x" +
                        Long.toHexString(camera.guid) + " / " + camera.unit;
                camera.setAutoSynch(false);
                dc1394_camera_free(camera);
            }
        }

        l.setAutoSynch(false);
        dc1394_camera_free_list(l);
        dc1394_free(d);
        return descriptions;
    }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                String s = com.googlecode.javacv.jna.dc1394.libname;
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception(t);
                }
            }
        }
    }

    public DC1394FrameGrabber(int deviceNumber) throws Exception {
        d = dc1394_new();
        dc1394camera_list_t.PointerByReference list = new dc1394camera_list_t.PointerByReference();
        int err=dc1394_camera_enumerate (d, list);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_camera_enumerate() Error " + err + ": Failed to enumerate cameras.");
        }
        dc1394camera_list_t l = list.getStructure();
        if (l.num <= deviceNumber) {
            throw new Exception("DC1394Grabber() Error: Camera number " + deviceNumber +
                    " not found. There are only " + l.num + " devices.");
        }
        dc1394camera_id_t[] ids = (dc1394camera_id_t[])(l.ids.toArray(l.num));
        camera = dc1394_camera_new_unit(d, ids[deviceNumber].guid, ids[deviceNumber].unit);
        if (camera == null) {
            throw new Exception("dc1394_camera_new_unit() Error: Failed to initialize camera with GUID 0x" +
                    Long.toHexString(ids[deviceNumber].guid)+ " / " + camera.unit + ".");
        }
        l.setAutoSynch(false);
        dc1394_camera_free_list(l);
//System.out.println("Using camera with GUID 0x" + Long.toHexString(camera.guid) + " / " + camera.unit);

        camera.setAutoSynch(false);
    }

    public void release() throws Exception {
        if (camera != null) {
            stop();
            camera.setAutoSynch(false);
            dc1394_camera_free(camera);
            camera = null;
        }
        if (d != null) {
            dc1394_free(d);
            d = null;
        }
    }
    @Override protected void finalize() {
        try {
            release();
        } catch (Exception ex) {
            Logger.getLogger(DC1394FrameGrabber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private dc1394_t d = null;
    private dc1394camera_t camera = null;
    private Poll.pollfd fds = new Poll.pollfd();
    private NativeLong one = new NativeLong(1);
    private boolean oneShotMode = false;
    private boolean resetDone   = false;
    private dc1394video_frame_t.PointerByReference raw_image = new dc1394video_frame_t.PointerByReference();
    private dc1394video_frame_t conv_image = new dc1394video_frame_t();
    private dc1394video_frame_t frame = new dc1394video_frame_t();
    private dc1394video_frame_t enqueue_image = null;
    private IplImage temp_image, return_image = null;
    private IntByReference tempInt = new IntByReference();
    private FloatByReference gammaRef = new FloatByReference();

    @Override public double getGamma() {
        return gammaRef.getValue();
    }

    public void start() throws Exception {
        start(true, true);
    }
    public void start(boolean tryReset, boolean try1394b) throws Exception {
        int c = -1;
        if (colorMode == ColorMode.BGR || colorMode == ColorMode.RAW) {
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
        } else if (colorMode == ColorMode.GRAY) {
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
            dc1394_video_get_mode(camera, tempInt);
            c = tempInt.getValue();
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
            dc1394_video_get_framerate(camera, tempInt);
            f = tempInt.getValue();
        }

        try {
            oneShotMode = false;
            if (triggerMode) {
                int err=dc1394_external_trigger_set_power(camera, DC1394_ON);
                if (err != DC1394_SUCCESS) {
                    // no trigger support, use one-shot mode instead
                    oneShotMode = true;
                } else {
                    dc1394_external_trigger_set_mode(camera, DC1394_TRIGGER_MODE_0);
                    err=dc1394_external_trigger_set_source(camera, DC1394_TRIGGER_SOURCE_SOFTWARE);
                    if (err != DC1394_SUCCESS) {
                        // no support for software trigger, use one-shot mode instead
                        oneShotMode = true;
                        dc1394_external_trigger_set_power(camera, DC1394_OFF);
                    }
                }
            }

            int err=dc1394_video_set_operation_mode(camera, DC1394_OPERATION_MODE_LEGACY);
            if (try1394b) {
                err=dc1394_video_set_operation_mode(camera, DC1394_OPERATION_MODE_1394B);
                if (err == DC1394_SUCCESS) {
                    err=dc1394_video_set_iso_speed(camera, DC1394_ISO_SPEED_800);
                }
            }
            if (err != DC1394_SUCCESS || !try1394b) {
                err=dc1394_video_set_iso_speed(camera, DC1394_ISO_SPEED_400);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_video_set_iso_speed() Error " + err + ": Could not set maximum iso speed.");
                }
            }

            err=dc1394_video_set_mode(camera, c);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_video_set_mode() Error " + err + ": Could not set video mode.");
            }

            if (dc1394_is_video_mode_scalable(c) == DC1394_TRUE) {
                err=dc1394_format7_set_roi(camera, c,
                        DC1394_QUERY_FROM_CAMERA, DC1394_QUERY_FROM_CAMERA,
                        DC1394_QUERY_FROM_CAMERA, DC1394_QUERY_FROM_CAMERA,
                        DC1394_QUERY_FROM_CAMERA, DC1394_QUERY_FROM_CAMERA);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_format7_set_roi() Error " + err + ": Could not set format7 mode.");
                }
            } else {
                err=dc1394_video_set_framerate(camera, f);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_video_set_framerate() Error " + err + ": Could not set framerate.");
                }
            }

            err=dc1394_capture_setup(camera, numBuffers, DC1394_CAPTURE_FLAGS_DEFAULT);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_capture_setup() Error " + err + ": Could not setup camera-\n" +
                        "make sure that the video mode and framerate are\nsupported by your camera.");
            }

            if (gamma != 0.0) {
                err=dc1394_feature_set_absolute_value(camera, DC1394_FEATURE_GAMMA, (float)gamma);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_feature_set_absolute_value() Error " + err + ": Could not set gamma.");
                }
            }
            err=dc1394_feature_get_absolute_value(camera, DC1394_FEATURE_GAMMA, gammaRef);
            if (err != DC1394_SUCCESS) {
                gammaRef.setValue(2.2f);
            }

            if (Platform.isLinux()) {
                fds.fd = dc1394_capture_get_fileno(camera);
            }

            if (!oneShotMode) {
                err=dc1394_video_set_transmission(camera, DC1394_ON);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_video_set_transmission() Error " + err + ": Could not start camera iso transmission.");
                }
            }
        } catch(Exception e) {
            // if we couldn't start, try again with a bus reset
            if (tryReset && !resetDone) {
                dc1394_reset_bus(camera);
                Thread.sleep(100);
                resetDone = true;
                start(false, try1394b);
            } else {
                throw e;
            }
        } finally {
            resetDone = false;
        }

        if (Platform.isLinux() && try1394b) {
            if (triggerMode) {
                trigger();
            }
            fds.events = Poll.POLLIN;
            if (Poll.poll(fds, one, timeout) == 0) {
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

        int err=dc1394_video_set_transmission(camera, DC1394_OFF);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_video_set_transmission() Error " + err + ": Could not stop the camera?");
        }
        err=dc1394_capture_stop(camera);
        if (err != DC1394_SUCCESS && err != DC1394_CAPTURE_IS_NOT_SET) {
            throw new Exception("dc1394_capture_stop() Error " + err + ": Could not stop the camera?");
        }
        if (triggerMode && !oneShotMode) {
            err=dc1394_external_trigger_set_power(camera, DC1394_OFF);
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
            image.setAutoSynch(false);
            int err=dc1394_capture_enqueue(camera, image);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_capture_enqueue() Error " + err + ": Could not release a frame.");
            }
        }
    }

    public void trigger() throws Exception {
        enqueue();
        if (oneShotMode) {
            int err=dc1394_video_set_one_shot(camera, DC1394_ON);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_video_set_one_shot() Error " + err + ": Could not set camera into one-shot mode.");
            }
        } else {
            long time = System.currentTimeMillis();
            do {
                dc1394_software_trigger_get_power(camera, tempInt);
                if (System.currentTimeMillis() - time > timeout) {
                    break;
                    //throw new Exception("trigger() Error: Timeout occured.");
                }
            } while (tempInt.getValue() == DC1394_ON);
            int err=dc1394_software_trigger_set_power(camera, DC1394_ON);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_software_trigger_set_power() Error " + err + ": Could not trigger camera.");
            }
        }
    }

    public IplImage grab() throws Exception {
        enqueue();
        if (Platform.isLinux()) {
            fds.events = Poll.POLLIN;
            if (Poll.poll(fds, one, timeout) == 0) {
                throw new Exception("poll() Error: Timeout occured.");
            }
        }
        int err=dc1394_capture_dequeue(camera, DC1394_CAPTURE_POLICY_WAIT, raw_image);
        if (err != DC1394_SUCCESS) {
            throw new Exception("dc1394_capture_dequeue(WAIT) Error " + err + ": Could not capture a frame.");
        }
        // try to poll for more images, to get the most recent one...
        while (raw_image.getValue() != null) {
            enqueue();
            raw_image.getStructure(frame);
            enqueue_image = frame;
            err=dc1394_capture_dequeue(camera, DC1394_CAPTURE_POLICY_POLL, raw_image);
            if (err != DC1394_SUCCESS) {
                throw new Exception("dc1394_capture_dequeue(POLL) Error " + err + ": Could not capture a frame.");
            }
        }
        int w = frame.size[0];
        int h = frame.size[1];
        int depth = frame.data_depth;
        int iplDepth = 0;
        switch (depth) {
            case 8:  iplDepth = IPL_DEPTH_8U;  break;
            case 16: iplDepth = IPL_DEPTH_16U; break;
            default: assert (false);
        }
        int stride = frame.stride;
        int size = frame.image_bytes;
        int numChannels = stride/w*8/depth;
        ByteOrder frameEndian = frame.little_endian != 0 ?
                ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        boolean alreadySwapped = false;
        boolean colorbayer = frame.color_coding == DC1394_COLOR_CODING_RAW8 ||
                             frame.color_coding == DC1394_COLOR_CODING_RAW16;
        boolean colorrgb   = frame.color_coding == DC1394_COLOR_CODING_RGB8 ||
                             frame.color_coding == DC1394_COLOR_CODING_RGB16;
        boolean coloryuv   = frame.color_coding == DC1394_COLOR_CODING_YUV411 ||
                             frame.color_coding == DC1394_COLOR_CODING_YUV422 ||
                             frame.color_coding == DC1394_COLOR_CODING_YUV444;

        if ((depth <= 8 || frameEndian.equals(ByteOrder.nativeOrder())) && !coloryuv &&
                (colorMode == ColorMode.RAW || (colorMode == ColorMode.BGR && numChannels == 3) ||
                (colorMode == ColorMode.GRAY && numChannels == 1 && !colorbayer))) {
            if (return_image == null) {
                return_image = IplImage.createHeader(w, h, iplDepth, numChannels);
            }
            return_image.widthStep = stride;
            return_image.imageSize = size;
            return_image.imageData = frame.image;
        } else {
            // in the padding, there's sometimes timeframe information and stuff
            // that libdc1394 will copy for us, so we need to allocate it
            int padding1 = (int)Math.ceil((double)frame.padding_bytes/(w * depth/8));
            int padding3 = (int)Math.ceil((double)frame.padding_bytes/(w*3*depth/8));
            if (return_image == null) {
                int c       = colorMode == ColorMode.BGR ? 3 : 1;
                int padding = colorMode == ColorMode.BGR ? padding3 : padding1;
                return_image = IplImage.create(w, h+padding, iplDepth, c);
                return_image.height -= padding;
            }
            if (temp_image == null) {
                if (colorMode == ColorMode.BGR && numChannels != 3 && !colorbayer) {
                    temp_image = IplImage.create(w, h+padding1, iplDepth, 1);
                    temp_image.height -= padding1;
                } else if (colorMode == ColorMode.GRAY &&
                        (coloryuv || colorbayer || (colorrgb && depth > 8))) {
                    temp_image = IplImage.create(w, h+padding3, iplDepth, 3);
                    temp_image.height -= padding3;
                } else if (colorMode == ColorMode.GRAY && colorrgb) {
                    temp_image = IplImage.createHeader(w, h, iplDepth, 3);
                    temp_image.widthStep = stride;
                    temp_image.imageSize = size;
                    temp_image.imageData = frame.image;
                } else {
                    temp_image = return_image;
                }
            }
            conv_image.size[0] = temp_image.width;
            conv_image.size[1] = temp_image.height;
            if (depth > 8) {
                conv_image.color_coding = colorMode == ColorMode.RAW ? DC1394_COLOR_CODING_RAW16  :
                                          temp_image.nChannels == 1  ? DC1394_COLOR_CODING_MONO16 :
                                                                       DC1394_COLOR_CODING_RGB16  ;
                conv_image.data_depth = 16;
            } else {
                conv_image.color_coding = colorMode == ColorMode.RAW ? DC1394_COLOR_CODING_RAW8   :
                                          temp_image.nChannels == 1  ? DC1394_COLOR_CODING_MONO8  :
                                                                       DC1394_COLOR_CODING_RGB8   ;
                conv_image.data_depth = 8;
            }
            conv_image.stride = temp_image.widthStep;
            conv_image.allocated_image_bytes = conv_image.total_bytes =
                    conv_image.image_bytes = temp_image.imageSize;
            conv_image.image = temp_image.imageData;

            if (colorbayer) {
                // from raw Bayer... invert R and B to get BGR images
                // (like OpenCV wants them) instead of RGB
                int c = frame.color_filter;
                if (c                  == DC1394_COLOR_FILTER_RGGB) {
                    frame.color_filter  = DC1394_COLOR_FILTER_BGGR;
                } else if (c           == DC1394_COLOR_FILTER_GBRG) {
                    frame.color_filter  = DC1394_COLOR_FILTER_GRBG;
                } else if (c           == DC1394_COLOR_FILTER_GRBG) {
                    frame.color_filter  = DC1394_COLOR_FILTER_GBRG;
                } else if (c           == DC1394_COLOR_FILTER_BGGR) {
                    frame.color_filter  = DC1394_COLOR_FILTER_RGGB;
                } else {
                    assert(false);
                }
                frame.writeField("color_filter");
                // other better methods than "simple" give garbage at 16 bits..
                err=dc1394_debayer_frames(frame, conv_image, DC1394_BAYER_METHOD_SIMPLE);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_debayer_frames() Error " + err + ": Could not debayer frame.");
                }
                frame.writeField("color_filter", c);
            } else if (frame.color_coding == conv_image.color_coding &&
                       frame.data_depth == conv_image.data_depth && 
                       frame.stride == conv_image.stride && depth > 8) {
                // we just need a copy to swap bytes..
                ShortBuffer in  = frame.getByteBuffer().order(frameEndian).asShortBuffer();
                ShortBuffer out = temp_image.getByteBuffer().order(ByteOrder.nativeOrder()).asShortBuffer();
                out.put(in);
                alreadySwapped = true;
            } else if (!colorrgb) {
                // from YUV, etc.
                err=dc1394_convert_frames(frame, conv_image);
                if (err != DC1394_SUCCESS) {
                    throw new Exception("dc1394_convert_frames() Error " + err + ": Could not convert frame.");
                }
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
        if (colorMode == ColorMode.BGR && numChannels != 3 && !colorbayer) {
            cvCvtColor(temp_image, return_image, CV_GRAY2BGR);
        } else if (colorMode == ColorMode.GRAY &&
                (colorbayer || colorrgb || coloryuv)) {
            cvCvtColor(temp_image, return_image, CV_BGR2GRAY);
        }

        enqueue_image = frame;
        return_image.setTimestamp(frame.timestamp);
//System.out.println(frame.timestamp);
        return return_image;
    }

}
