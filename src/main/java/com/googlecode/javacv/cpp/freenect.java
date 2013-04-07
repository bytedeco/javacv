/*
 * Copyright (C) 2011,2012,2013 Samuel Audet
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
 *
 *
 * This file was derived from libfreenect.h and libfreenect_sync.h from the
 * master branch package OpenKinect-libfreenect-v0.1.2-1-ge1365de, covered
 * by the following copyright notice:
 *
 * This file is part of the OpenKinect Project. http://www.openkinect.org
 *
 * Copyright (c) 2010 individual OpenKinect contributors. See the CONTRIB file
 * for details.
 *
 * This code is licensed to you under the terms of the Apache License, version
 * 2.0, or, at your option, the terms of the GNU General Public License,
 * version 2.0. See the APACHE20 and GPL2 files for the text of the licenses,
 * or the following URLs:
 * http://www.apache.org/licenses/LICENSE-2.0
 * http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * If you redistribute this file in source form, modified or unmodified, you
 * may:
 *   1) Leave this header intact and distribute it under the same terms,
 *      accompanying it with the APACHE20 and GPL20 files, or
 *   2) Delete the Apache 2.0 clause and accompany it with the GPL2 file, or
 *   3) Delete the GPL v2 clause and accompany it with the APACHE20 file
 * In all cases you must keep the copyright notice intact and include a copy
 * of the CONTRIB file.
 *
 * Binary distributions must follow the binary distribution requirements of
 * either License.
 *
 * The CONTRIB file
 * ================
 * The following people have contributed to libfreenect:
 * 
 * Aditya Gaddam <adityagaddam@gmail.com>
 * Andrew <amiller@dappervision.com>
 * Antonio Ospite <ospite@studenti.unina.it>
 * arnebe <arne@alamut.de>
 * Brandyn A. White <bwhite@dappervision.com>
 * Chris J (cryptk) <cryptk@gmail.com>
 * David García Garzón <david.garcia@barcelonamedia.org>
 * Dominick D'Aniello <netpro2k@gmail.com>
 * Drew Fisher <drew.m.fisher@gmail.com>
 * Eric Monti <esmonti@gmail.com>
 * Francois Coulombe <fcoulombe@silentfalls.org>
 * Hector Martin <hector@marcansoft.com>
 * Jochen Kerdels <jochen@kerdels.de>
 * Josh Grunzweig <jgrunzweig@gizmotron.local>
 * Joshua Blake <joshblake@gmail.com>
 * Juan Carlos del Valle <jc.ekinox@gmail.com>
 * Kai Ritterbusch <kai.ritterbusch@gmx.de>
 * Kai R <phen@gmx.de>
 * Kelvie Wong <kelvie@ieee.org>
 * Kenneth Johansson <ken@kenjo.org>
 * Kyle Machulis <kyle@nonpolynomial.com>
 * Marcos Paulo Berteli Slomp <mslomp@gmail.com>
 * Mark Renouf <mark.renouf@gmail.com>
 * Melonee Wise <mwise@willowgarage.com>
 * Peter Kropf <pkropf@gmail.com>
 * Radu Bogdan Rusu <rusu@cs.tum.edu>
 * Rich Mattes <jpgr87@gmail.com>
 * Stéphane Magnenat <stephane@magnenat.net>
 * Stephen Sinclair <radarsat1@gmail.com>
 * Steven Lovegrove <stevenlovegrove@gmail.com>
 * Theo Watson <theo@openframeworks.cc>
 * Thomas Roefer <Thomas.Roefer@dfki.de>
 * Tim Niemueller <niemueller@kbsg.rwth-aachen.de>
 * Tully Foote <tfoote@willowgarage.com>
 * Vincent Le Ligeour <yoda-jm@users.sourceforge.net>
 * Yaroslav Halchenko <debian@onerussian.com>
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.ShortPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(value={"linux", "macosx"}, include={"<libfreenect.h>", "<libfreenect-registration.h>", "<libfreenect_sync.h>"}, link={"freenect@0.1", "freenect_sync@0.1"},
        includepath  ={"/usr/local/include/libfreenect/", "/usr/local/include/", "/opt/local/include/libfreenect/", "/opt/local/include/", "/usr/include/libfreenect/"},
        linkpath     ={"/opt/local/lib/", "/opt/local/lib64/", "/usr/local/lib/", "/usr/local/lib64/"}) ,
    @Platform(value="windows", include={"<WinSock2.h>", "<libfreenect.h>", "<libfreenect-registration.h>", "<libfreenect_sync.h>", "<libfreenect_sync.c>"}, link={"freenect", "pthreadVC2"},
        includepath  ={"C:/libfreenect/include/", "C:/libfreenect/wrappers/c_sync/", "C:/pthreads-w32-2-8-0-release/",
                       "C:/pthreads.2/", "C:/Pre-built.2/include/", "src/com/googlecode/javacv/cpp/"},
        linkpath     ={"C:/libfreenect/lib/", "C:/pthreads-w32-2-8-0-release/", "C:/pthreads.2/", "C:/Pre-built.2/lib/"}) })
public class freenect {
    static { load(); }

    public static class timeval extends Pointer {
        static { load(); }
        public timeval() { allocate(); }
        public timeval(int size) { allocateArray(size); }
        public timeval(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native long tv_sec();  public native timeval tv_sec (long tv_sec);
        public native long tv_usec(); public native timeval tv_usec(long tv_usec);
    }

    // #include "libfreenect.h"
    public static final int 
            FREENECT_COUNTS_PER_G = 819,

            FREENECT_DEPTH_MM_MAX_VALUE = 10000,
            FREENECT_DEPTH_MM_NO_VALUE = 0,
            FREENECT_DEPTH_RAW_MAX_VALUE = 2048,
            FREENECT_DEPTH_RAW_NO_VALUE = 2047,

            // enum freenect_device_flags
            FREENECT_DEVICE_CAMERA = 0x02,
            FREENECT_DEVICE_AUDIO  = 0x04;

    public static class freenect_device_attributes extends Pointer {
        static { load(); }
        public freenect_device_attributes() { allocate(); }
        public freenect_device_attributes(int size) { allocateArray(size); }
        public freenect_device_attributes(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native freenect_device_attributes next(); public native freenect_device_attributes next(freenect_device_attributes next);
        @Cast("const char*")
        public native BytePointer camera_serial();       public native freenect_device_attributes camera_serial(BytePointer camera_serial);
    }

    public static final int
    // enum freenect_resolution
            FREENECT_RESOLUTION_LOW    = 0,
            FREENECT_RESOLUTION_MEDIUM = 1,
            FREENECT_RESOLUTION_HIGH   = 2,

    // enum freenect_video_format
            FREENECT_VIDEO_RGB             = 0,
            FREENECT_VIDEO_BAYER           = 1,
            FREENECT_VIDEO_IR_8BIT         = 2,
            FREENECT_VIDEO_IR_10BIT        = 3,
            FREENECT_VIDEO_IR_10BIT_PACKED = 4,
            FREENECT_VIDEO_YUV_RGB         = 5,
            FREENECT_VIDEO_YUV_RAW         = 6,

    // enum freenect_depth_format
            FREENECT_DEPTH_11BIT        = 0,
            FREENECT_DEPTH_10BIT        = 1,
            FREENECT_DEPTH_11BIT_PACKED = 2,
            FREENECT_DEPTH_10BIT_PACKED = 3,
            FREENECT_DEPTH_REGISTERED   = 4,
            FREENECT_DEPTH_MM           = 5;

    public static class freenect_frame_mode extends Pointer {
        static { load(); }
        public freenect_frame_mode() { allocate(); }
        public freenect_frame_mode(int size) { allocateArray(size); }
        public freenect_frame_mode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int reserved();                public native freenect_frame_mode reserved(int reserved);
        @Cast("freenect_resolution")
        public native int resolution();              public native freenect_frame_mode resolution(int resolution);
        @Cast("freenect_video_format")
        public native int video_format();            public native freenect_frame_mode video_format(int video_format);
        @Cast("freenect_depth_format")
        public native int depth_format();            public native freenect_frame_mode depth_format(int depth_format);
        public native int bytes();                   public native freenect_frame_mode bytes(int bytes);
        public native short width();                 public native freenect_frame_mode width(short width);
        public native short height();                public native freenect_frame_mode height(short height);
        public native byte data_bits_per_pixel();    public native freenect_frame_mode data_bits_per_pixel(byte data_bits_per_pixel);
        public native byte padding_bits_per_pixel(); public native freenect_frame_mode padding_bits_per_pixel(byte padding_bits_per_pixel);
        public native byte framerate();              public native freenect_frame_mode framerate(byte framerate);
        public native byte is_valid();               public native freenect_frame_mode is_valid(byte is_valid);
    }

    public static final int
    // enum freenect_led_options
            LED_OFF              = 0,
            LED_GREEN            = 1,
            LED_RED              = 2,
            LED_YELLOW           = 3,
            LED_BLINK_GREEN      = 4,
            LED_BLINK_RED_YELLOW = 6,

    // enum freenect_tilt_status_code
            TILT_STATUS_STOPPED = 0x00,
            TILT_STATUS_LIMIT   = 0x01,
            TILT_STATUS_MOVING  = 0x04;

    public static class freenect_raw_tilt_state extends Pointer {
        static { load(); }
        public freenect_raw_tilt_state() { allocate(); }
        public freenect_raw_tilt_state(int size) { allocateArray(size); }
        public freenect_raw_tilt_state(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native short accelerometer_x(); public native freenect_raw_tilt_state accelerometer_x(short accelerometer_x);
        public native short accelerometer_y(); public native freenect_raw_tilt_state accelerometer_y(short accelerometer_y);
        public native short accelerometer_z(); public native freenect_raw_tilt_state accelerometer_z(short accelerometer_z);
        public native byte  tilt_angle();      public native freenect_raw_tilt_state tilt_angle(byte tilt_angle);
        @Cast("freenect_tilt_status_code")
        public native int   tilt_status();     public native freenect_raw_tilt_state tilt_status(int tilt_status);
    }

    @Opaque public static class freenect_context extends Pointer {
        static { load(); }
        public freenect_context() { }
        public freenect_context(Pointer p) { super(p); }
    }

    @Opaque public static class freenect_device extends Pointer {
        static { load(); }
        public freenect_device() { }
        public freenect_device(Pointer p) { super(p); }
    }

    @Opaque public static class freenect_usb_context extends Pointer {
        static { load(); }
        public freenect_usb_context() { }
        public freenect_usb_context(Pointer p) { super(p); }
    }

    public static final int
    // enum freenect_loglevel
            FREENECT_LOG_FATAL = 0,
            FREENECT_LOG_ERROR = 1,
            FREENECT_LOG_WARNING = 2,
            FREENECT_LOG_NOTICE = 3,
            FREENECT_LOG_INFO = 4,
            FREENECT_LOG_DEBUG = 5,
            FREENECT_LOG_SPEW = 6,
            FREENECT_LOG_FLOOD = 7;

    public static native int freenect_init(@ByPtrPtr freenect_context ctx, freenect_usb_context usb_ctx);
    public static native int freenect_shutdown(freenect_context ctx);

    public static class freenect_log_cb extends FunctionPointer {
        static { load(); }
        public    freenect_log_cb(Pointer p) { super(p); }
        protected freenect_log_cb() { allocate(); }
        private native void allocate();
        public native void call(freenect_context dev, @Cast("freenect_loglevel") int level, String msg);
    }
    public static native void freenect_set_log_level(freenect_context ctx, @Cast("freenect_loglevel") int level);
    public static native void freenect_set_log_callback(freenect_context ctx, freenect_log_cb cb);

    public static native int freenect_process_events(freenect_context ctx);
    public static native int freenect_process_events_timeout(freenect_context ctx, timeval timeout);
    public static native int freenect_num_devices(freenect_context ctx);
    public static native int freenect_list_device_attributes(freenect_context ctx, @ByPtrPtr freenect_device_attributes attribute_list);
    public static native void freenect_free_device_attributes(freenect_device_attributes attribute_list);
    public static native int freenect_supported_subdevices();
    public static native void freenect_select_subdevices(freenect_context ctx, @Cast("freenect_device_flags") int subdevs);
    public static native int freenect_open_device(freenect_context ctx, @ByPtrPtr freenect_device dev, int index);
    public static native int freenect_open_device_by_camera_serial(freenect_context ctx, @ByPtrPtr freenect_device dev, String camera_serial);
    public static native int freenect_open_device_by_camera_serial(freenect_context ctx, @ByPtrPtr freenect_device dev, @Cast("char*") BytePointer camera_serial);
    public static native int freenect_close_device(freenect_device dev);
    public static native void freenect_set_user(freenect_device dev, Pointer user);
    public static native Pointer freenect_get_user(freenect_device dev);

    public static class freenect_depth_cb extends FunctionPointer {
        static { load(); }
        public    freenect_depth_cb(Pointer p) { super(p); }
        protected freenect_depth_cb() { allocate(); }
        private native void allocate();
        public native void call(freenect_device dev, Pointer depth, @Cast("uint32_t") int timestamp);
    }
    public static class freenect_video_cb extends FunctionPointer {
        static { load(); }
        public    freenect_video_cb(Pointer p) { super(p); }
        protected freenect_video_cb() { allocate(); }
        private native void allocate();
        public native void call(freenect_device dev, Pointer video, @Cast("uint32_t") int timestamp);
    }
    public static native void freenect_set_depth_callback(freenect_device dev, freenect_depth_cb cb);
    public static native void freenect_set_video_callback(freenect_device dev, freenect_video_cb cb);
    public static native int freenect_set_depth_buffer(freenect_device dev, Pointer buf);
    public static native int freenect_set_video_buffer(freenect_device dev, Pointer buf);
    public static native int freenect_start_depth(freenect_device dev);
    public static native int freenect_start_video(freenect_device dev);
    public static native int freenect_stop_depth(freenect_device dev);
    public static native int freenect_stop_video(freenect_device dev);

    public static native int freenect_update_tilt_state(freenect_device dev);
    public static native freenect_raw_tilt_state freenect_get_tilt_state(freenect_device dev);
    public static native double freenect_get_tilt_degs(freenect_raw_tilt_state state);
    public static native int freenect_set_tilt_degs(freenect_device dev, double angle);
    public static native @Cast("freenect_tilt_status_code") int freenect_get_tilt_status(freenect_raw_tilt_state state);

    public static native int freenect_set_led(freenect_device dev, @Cast("freenect_led_options") int option);
    public static native void freenect_get_mks_accel(freenect_raw_tilt_state state, double[] x, double[] y, double[] z);

    public static native int freenect_get_video_mode_count();
    public static native @ByVal freenect_frame_mode freenect_get_video_mode(int mode_num);
    public static native @ByVal freenect_frame_mode freenect_get_current_video_mode(freenect_device dev);
    public static native @ByVal freenect_frame_mode freenect_find_video_mode(
            @Cast("freenect_resolution") int res, @Cast("freenect_video_format") int fmt);
    public static native int freenect_set_video_mode(freenect_device dev, @ByVal freenect_frame_mode mode);

    public static native int freenect_get_depth_mode_count();
    public static native @ByVal freenect_frame_mode freenect_get_depth_mode(int mode_num);
    public static native @ByVal freenect_frame_mode freenect_get_current_depth_mode(freenect_device dev);
    public static native @ByVal freenect_frame_mode freenect_find_depth_mode(
            @Cast("freenect_resolution") int res, @Cast("freenect_depth_format") int fmt);
    public static native int freenect_set_depth_mode(freenect_device dev, @ByVal freenect_frame_mode mode);

    // #include "libfreenect-registration.h"
    public static class freenect_reg_info extends Pointer {
        static { load(); }
        public freenect_reg_info() { allocate(); }
        public freenect_reg_info(int size) { allocateArray(size); }
        public freenect_reg_info(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int dx_center(); public native freenect_reg_info dx_center(int dx_center);

        public native int ax();        public native freenect_reg_info ax(int ax);
        public native int bx();        public native freenect_reg_info bx(int bx);
        public native int cx();        public native freenect_reg_info cx(int cx);
        public native int dx();        public native freenect_reg_info dx(int dx);

        public native int dx_start();  public native freenect_reg_info dx_start(int dx_start);

        public native int ay();        public native freenect_reg_info ay(int ay);
        public native int by();        public native freenect_reg_info by(int by);
        public native int cy();        public native freenect_reg_info cy(int cy);
        public native int dy();        public native freenect_reg_info dy(int dy);

        public native int dy_start();  public native freenect_reg_info dy_start(int dy_start);

        public native int dx_beta_start(); public native freenect_reg_info dx_beta_start(int dx_beta_start);
        public native int dy_beta_start(); public native freenect_reg_info dy_beta_start(int dy_beta_start);

        public native int rollout_blank(); public native freenect_reg_info rollout_blank(int rollout_blank);
        public native int rollout_size();  public native freenect_reg_info rollout_size(int rollout_size);

        public native int dx_beta_inc();   public native freenect_reg_info dx_beta_inc(int dx_beta_inc);
        public native int dy_beta_inc();   public native freenect_reg_info dy_beta_inc(int dy_beta_inc);

        public native int dxdx_start();    public native freenect_reg_info dxdx_start(int dxdx_start);
        public native int dxdy_start();    public native freenect_reg_info dxdy_start(int dxdy_start);
        public native int dydx_start();    public native freenect_reg_info dydx_start(int dydx_start);
        public native int dydy_start();    public native freenect_reg_info dydy_start(int dydy_start);

        public native int dxdxdx_start();  public native freenect_reg_info dxdxdx_start(int dxdxdx_start);
        public native int dydxdx_start();  public native freenect_reg_info dydxdx_start(int dydxdx_start);
        public native int dxdxdy_start();  public native freenect_reg_info dxdxdy_start(int dxdxdy_start);
        public native int dydxdy_start();  public native freenect_reg_info dydxdy_start(int dydxdy_start);

        public native int back_comp1();    public native freenect_reg_info back_comp1(int back_comp1);

        public native int dydydx_start();  public native freenect_reg_info dydydx_start(int dydydx_start);

        public native int back_comp2();    public native freenect_reg_info back_comp2(int back_comp2);

        public native int dydydy_start();  public native freenect_reg_info dydydy_start(int dydydy_start);
    }

    public static class freenect_reg_pad_info extends Pointer {
        static { load(); }
        public freenect_reg_pad_info() { allocate(); }
        public freenect_reg_pad_info(int size) { allocateArray(size); }
        public freenect_reg_pad_info(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native short start_lines();    public native freenect_reg_pad_info start_lines(short start_lines);
        public native short end_lines();      public native freenect_reg_pad_info end_lines(short end_lines);
        public native short cropping_lines(); public native freenect_reg_pad_info cropping_lines(short cropping_lines);
    }

    public static class freenect_zero_plane_info extends Pointer {
        static { load(); }
        public freenect_zero_plane_info() { allocate(); }
        public freenect_zero_plane_info(int size) { allocateArray(size); }
        public freenect_zero_plane_info(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native float dcmos_emitter_dist();   public native freenect_zero_plane_info dcmos_emitter_dist(float dcmos_emitter_dist);
        public native float dcmos_rcmos_dist();     public native freenect_zero_plane_info dcmos_rcmos_dist(float dcmos_rcmos_dist);
        public native float reference_distance();   public native freenect_zero_plane_info reference_distance(float reference_distance);
        public native float reference_pixel_size(); public native freenect_zero_plane_info reference_pixel_size(float reference_pixel_size);
    }

    public static class freenect_registration extends Pointer {
        static { load(); }
        public freenect_registration() { allocate(); }
        public freenect_registration(int size) { allocateArray(size); }
        public freenect_registration(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native @ByVal freenect_reg_info        reg_info();        public native freenect_registration reg_info(freenect_reg_info reg_info);
        public native @ByVal freenect_reg_pad_info    reg_pad_info();    public native freenect_registration reg_pad_info(freenect_reg_pad_info reg_pad_info);
        public native @ByVal freenect_zero_plane_info zero_plane_info(); public native freenect_registration zero_plane_info(freenect_zero_plane_info zero_plane_info);

        public native double const_shift();            public native freenect_registration const_shift(double const_shift);

        @Cast("uint16_t*")
        public native ShortPointer raw_to_mm_shift();  public native freenect_registration raw_to_mm_shift(ShortPointer raw_to_mm_shift);
        public native IntPointer depth_to_rgb_shift(); public native freenect_registration depth_to_rgb_shift(IntPointer depth_to_rgb_shift);
        @Cast("int32_t(*)[2]")
        public native IntPointer registration_table(); public native freenect_registration registration_table(IntPointer registration_table);
    }

    public static native @ByVal freenect_registration freenect_copy_registration(freenect_device dev);
    public static native int freenect_destroy_registration(freenect_registration reg);
    public static native void freenect_camera_to_world(freenect_device dev,
            int cx, int cy, int wz, double[] wx, double[] wy);

    // #include "libfreenect_sync.h"
    public static native int freenect_sync_get_video(@Cast("void**") @ByPtrPtr Pointer video,
            @Cast("uint32_t*") int[] timestamp, int index, @Cast("freenect_video_format") int fmt);
    public static native int freenect_sync_get_depth(@Cast("void**") @ByPtrPtr Pointer depth,
            @Cast("uint32_t*") int[] timestamp, int index, @Cast("freenect_depth_format") int fmt);
    public static native int freenect_sync_set_tilt_degs(int angle, int index);
    public static native int freenect_sync_get_tilt_state(@ByPtrPtr freenect_raw_tilt_state state, int index);
    public static native int freenect_sync_set_led(@Cast("freenect_led_options") int led, int index);
    public static native void freenect_sync_stop();
}
