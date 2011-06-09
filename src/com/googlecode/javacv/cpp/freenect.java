/*
 * Copyright (C) 2011 Samuel Audet
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
 * master branch package OpenKinect-libfreenect-4a159f8, which was covered
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

import com.googlecode.javacpp.FunctionPointer;
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
    @Platform(value={"linux", "macosx"}, include={"<libfreenect.h>", "<libfreenect_sync.h>"}, link={"freenect", "freenect_sync"},
        includepath  ={"/opt/local/include/libfreenect/", "/usr/local/include/libfreenect/", "/opt/local/include/", "/usr/include/libfreenect/"},
        linkpath     ={"/opt/local/lib/", "/opt/local/lib64/", "/usr/local/lib/", "/usr/local/lib64/"}) ,
    @Platform(value="windows", include={"<libfreenect.h>", "<libfreenect_sync.h>", "<libfreenect_sync.c>"}, link={"freenect", "pthreadVC2"},
        includepath  ={"C:/libfreenect/include/", "C:/libfreenect/wrappers/c_sync/", "C:/pthreads-w32-2-8-0-release/", "C:/pthreads.2/", "C:/Pre-built.2/include/"},
        linkpath     ={"C:/libfreenect/lib/", "C:/pthreads-w32-2-8-0-release/", "C:/pthreads.2/", "C:/Pre-built.2/lib/"}) })
public class freenect {
    static { load(); }

    // #include "libfreenect.h"
    public static final int 
            FREENECT_COUNTS_PER_G = 819,

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
            FREENECT_DEPTH_10BIT_PACKED = 3;

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
        protected final native void allocate();
        public native void call(freenect_context dev, @Cast("freenect_loglevel") int level, String msg);
    }
    public static native void freenect_set_log_level(freenect_context ctx, @Cast("freenect_loglevel") int level);
    public static native void freenect_set_log_callback(freenect_context ctx, freenect_log_cb cb);

    public static native int freenect_process_events(freenect_context ctx);
    public static native int freenect_num_devices(freenect_context ctx);
    public static native int freenect_open_device(freenect_context ctx, @ByPtrPtr freenect_device dev, int index);
    public static native int freenect_close_device(freenect_device dev);
    public static native void freenect_set_user(freenect_device dev, Pointer user);
    public static native Pointer freenect_get_user(freenect_device dev);

    public static class freenect_depth_cb extends FunctionPointer {
        static { load(); }
        public    freenect_depth_cb(Pointer p) { super(p); }
        protected freenect_depth_cb() { allocate(); }
        protected final native void allocate();
        public native void call(freenect_device dev, Pointer depth, @Cast("uint32_t") int timestamp);
    }
    public static class freenect_video_cb extends FunctionPointer {
        static { load(); }
        public    freenect_video_cb(Pointer p) { super(p); }
        protected freenect_video_cb() { allocate(); }
        protected final native void allocate();
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

    // #include "libfreenect_sync.h"
    public static native int freenect_sync_get_video(@ByPtrPtr Pointer video,
            @Cast("uint32_t*") int[] timestamp, int index, @Cast("freenect_video_format") int fmt);
    public static native int freenect_sync_get_depth(@ByPtrPtr Pointer depth,
            @Cast("uint32_t*") int[] timestamp, int index, @Cast("freenect_depth_format") int fmt);
    public static native int freenect_sync_set_tilt_degs(int angle, int index);
    public static native int freenect_sync_get_tilt_state(@ByPtrPtr freenect_raw_tilt_state state, int index);
    public static native int freenect_sync_set_led(@Cast("freenect_led_options") int led, int index);
    public static native void freenect_sync_stop();
}
