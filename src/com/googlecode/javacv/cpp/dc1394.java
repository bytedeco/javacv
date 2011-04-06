/*
 * Copyright (C) 2009,2010,2011 Samuel Audet
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
 * This file was derived from dc1394.h and all its included files from
 * libdc1394 2.1.2, which were written by
 *   Gord Peters <GordPeters@smarttech.com>,
 *   Chris Urmson <curmson@ri.cmu.edu>
 *   Damien Douxchamps <ddouxchamps@users.sf.net>
 *   Dan Dennedy <ddennedy@users.sf.net>
 *   David Moore <dcm@acm.org>
 *   Frederic Devernay
 *   Rudolf Leitgeb
 *   ... and many others (see the AUTHORS file in the libdc1394 package)
 *
 * Copyright (C) 2000-2001 SMART Technologies Inc.
 * Copyright (C) 2001-2004 Universite catholique de Louvain
 * Copyright (C) 2000 Carnegie Mellon University
 * Copyright (C) 2006- Massachussets Institute of Technology
 * Copyright (C) 2004- Nara Institute of Science and Technology
 * All files are also Copyright (C) their respective author(s)
 *
 */

package com.googlecode.javacv.cpp;

import java.nio.ByteBuffer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.ShortPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;

import static com.googlecode.javacpp.Loader.*;

/**
 *
 * @author Samuel Audet
 */
@Platform(value={"linux", "macosx"}, include={"<poll.h>", "<dc1394/dc1394.h>"}, 
    includepath="/opt/local/include/", link="dc1394",
    linkpath="/opt/local/lib/:/opt/local/lib64/:/usr/local/lib/:/usr/local/lib64/")
public class dc1394 {
    static { load(); }

    public static final short
            POLLIN         = 0x001,
            POLLPRI        = 0x002,
            POLLOUT        = 0x004,
            POLLMSG        = 0x400,
            POLLREMOVE     = 0x1000,
            POLLRDHUP      = 0x2000,
            POLLERR        = 0x008,
            POLLHUP        = 0x010,
            POLLNVAL       = 0x020;

    public static class pollfd extends Pointer {
        static { load(); }
        public pollfd() { allocate(); }
        public pollfd(int size) { allocateArray(size); }
        public pollfd(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public pollfd position(int position) {
            return (pollfd)super.position(position);
        }

        public native int   fd();      public native pollfd fd     (int   fd);
        public native short events();  public native pollfd events (short fd);
        public native short revents(); public native pollfd revents(short fd);
    }

    public native static int poll(pollfd fds, @Cast("nfds_t") long nfds, int timeout);


    // enum dc1394error_t
    public static final int
            DC1394_SUCCESS                     =  0,
            DC1394_FAILURE                     = -1,
            DC1394_NOT_A_CAMERA                = -2,
            DC1394_FUNCTION_NOT_SUPPORTED      = -3,
            DC1394_CAMERA_NOT_INITIALIZED      = -4,
            DC1394_MEMORY_ALLOCATION_FAILURE   = -5,
            DC1394_TAGGED_REGISTER_NOT_FOUND   = -6,
            DC1394_NO_ISO_CHANNEL              = -7,
            DC1394_NO_BANDWIDTH                = -8,
            DC1394_IOCTL_FAILURE               = -9,
            DC1394_CAPTURE_IS_NOT_SET          = -10,
            DC1394_CAPTURE_IS_RUNNING          = -11,
            DC1394_RAW1394_FAILURE             = -12,
            DC1394_FORMAT7_ERROR_FLAG_1        = -13,
            DC1394_FORMAT7_ERROR_FLAG_2        = -14,
            DC1394_INVALID_ARGUMENT_VALUE      = -15,
            DC1394_REQ_VALUE_OUTSIDE_RANGE     = -16,
            DC1394_INVALID_FEATURE             = -17,
            DC1394_INVALID_VIDEO_FORMAT        = -18,
            DC1394_INVALID_VIDEO_MODE          = -19,
            DC1394_INVALID_FRAMERATE           = -20,
            DC1394_INVALID_TRIGGER_MODE        = -21,
            DC1394_INVALID_TRIGGER_SOURCE      = -22,
            DC1394_INVALID_ISO_SPEED           = -23,
            DC1394_INVALID_IIDC_VERSION        = -24,
            DC1394_INVALID_COLOR_CODING        = -25,
            DC1394_INVALID_COLOR_FILTER        = -26,
            DC1394_INVALID_CAPTURE_POLICY      = -27,
            DC1394_INVALID_ERROR_CODE          = -28,
            DC1394_INVALID_BAYER_METHOD        = -29,
            DC1394_INVALID_VIDEO1394_DEVICE    = -30,
            DC1394_INVALID_OPERATION_MODE      = -31,
            DC1394_INVALID_TRIGGER_POLARITY    = -32,
            DC1394_INVALID_FEATURE_MODE        = -33,
            DC1394_INVALID_LOG_TYPE            = -34,
            DC1394_INVALID_BYTE_ORDER          = -35,
            DC1394_INVALID_STEREO_METHOD       = -36,
            DC1394_BASLER_NO_MORE_SFF_CHUNKS   = -37,
            DC1394_BASLER_CORRUPTED_SFF_CHUNK  = -38,
            DC1394_BASLER_UNKNOWN_SFF_CHUNK    = -39,

            DC1394_ERROR_MIN= DC1394_BASLER_UNKNOWN_SFF_CHUNK,
            DC1394_ERROR_MAX= DC1394_SUCCESS,
            DC1394_ERROR_NUM=(DC1394_ERROR_MAX-DC1394_ERROR_MIN+1);

    // enum dc1394log_t
    public static final int
            DC1394_LOG_ERROR=768,
            DC1394_LOG_WARNING=769,
            DC1394_LOG_DEBUG=770,

            DC1394_LOG_MIN=               DC1394_LOG_ERROR,
            DC1394_LOG_MAX=               DC1394_LOG_DEBUG,
            DC1394_LOG_NUM=              (DC1394_LOG_MAX - DC1394_LOG_MIN + 1);

    public static class Log_handler extends FunctionPointer {
        static { load(); }
        public    Log_handler(Pointer p) { super(p); }
        protected Log_handler() { allocate(); }
        protected final native void allocate();
        public native void call(@Cast("dc1394log_t") int type, String message, Pointer user);
    }
    public static native @Cast("dc1394error_t") int dc1394_log_register_handler(
            @Cast("dc1394log_t") int type, Log_handler log_handler, Pointer user);
    public static native @Cast("dc1394error_t") int dc1394_log_set_default_handler(
            @Cast("dc1394log_t") int type);

    public static native void dc1394_log_error(String format);
    public static native void dc1394_log_warning(String format);
    public static native void dc1394_log_debug(String format);


    // enum dc1394video_mode_t
    public static final int
            DC1394_VIDEO_MODE_160x120_YUV444= 64,
            DC1394_VIDEO_MODE_320x240_YUV422= 65,
            DC1394_VIDEO_MODE_640x480_YUV411= 66,
            DC1394_VIDEO_MODE_640x480_YUV422= 67,
            DC1394_VIDEO_MODE_640x480_RGB8= 68,
            DC1394_VIDEO_MODE_640x480_MONO8= 69,
            DC1394_VIDEO_MODE_640x480_MONO16= 70,
            DC1394_VIDEO_MODE_800x600_YUV422= 71,
            DC1394_VIDEO_MODE_800x600_RGB8= 72,
            DC1394_VIDEO_MODE_800x600_MONO8= 73,
            DC1394_VIDEO_MODE_1024x768_YUV422= 74,
            DC1394_VIDEO_MODE_1024x768_RGB8= 75,
            DC1394_VIDEO_MODE_1024x768_MONO8= 76,
            DC1394_VIDEO_MODE_800x600_MONO16= 77,
            DC1394_VIDEO_MODE_1024x768_MONO16= 78,
            DC1394_VIDEO_MODE_1280x960_YUV422= 79,
            DC1394_VIDEO_MODE_1280x960_RGB8= 80,
            DC1394_VIDEO_MODE_1280x960_MONO8= 81,
            DC1394_VIDEO_MODE_1600x1200_YUV422= 82,
            DC1394_VIDEO_MODE_1600x1200_RGB8= 83,
            DC1394_VIDEO_MODE_1600x1200_MONO8= 84,
            DC1394_VIDEO_MODE_1280x960_MONO16= 85,
            DC1394_VIDEO_MODE_1600x1200_MONO16= 86,
            DC1394_VIDEO_MODE_EXIF= 87,
            DC1394_VIDEO_MODE_FORMAT7_0= 88,
            DC1394_VIDEO_MODE_FORMAT7_1= 89,
            DC1394_VIDEO_MODE_FORMAT7_2= 90,
            DC1394_VIDEO_MODE_FORMAT7_3= 91,
            DC1394_VIDEO_MODE_FORMAT7_4= 92,
            DC1394_VIDEO_MODE_FORMAT7_5= 93,
            DC1394_VIDEO_MODE_FORMAT7_6= 94,
            DC1394_VIDEO_MODE_FORMAT7_7= 95,

            DC1394_VIDEO_MODE_MIN=       DC1394_VIDEO_MODE_160x120_YUV444,
            DC1394_VIDEO_MODE_MAX=       DC1394_VIDEO_MODE_FORMAT7_7,
            DC1394_VIDEO_MODE_NUM=      (DC1394_VIDEO_MODE_MAX - DC1394_VIDEO_MODE_MIN + 1),

            DC1394_VIDEO_MODE_FORMAT7_MIN=       DC1394_VIDEO_MODE_FORMAT7_0,
            DC1394_VIDEO_MODE_FORMAT7_MAX=       DC1394_VIDEO_MODE_FORMAT7_7,
            DC1394_VIDEO_MODE_FORMAT7_NUM=      (DC1394_VIDEO_MODE_FORMAT7_MAX - DC1394_VIDEO_MODE_FORMAT7_MIN + 1);

    // enum dc1394color_coding_t
    public static final int
            DC1394_COLOR_CODING_MONO8= 352,
            DC1394_COLOR_CODING_YUV411= 353,
            DC1394_COLOR_CODING_YUV422= 354,
            DC1394_COLOR_CODING_YUV444= 355,
            DC1394_COLOR_CODING_RGB8= 356,
            DC1394_COLOR_CODING_MONO16= 357,
            DC1394_COLOR_CODING_RGB16= 358,
            DC1394_COLOR_CODING_MONO16S= 359,
            DC1394_COLOR_CODING_RGB16S= 360,
            DC1394_COLOR_CODING_RAW8= 361,
            DC1394_COLOR_CODING_RAW16= 362,

            DC1394_COLOR_CODING_MIN=     DC1394_COLOR_CODING_MONO8,
            DC1394_COLOR_CODING_MAX=     DC1394_COLOR_CODING_RAW16,
            DC1394_COLOR_CODING_NUM=    (DC1394_COLOR_CODING_MAX - DC1394_COLOR_CODING_MIN + 1);

    // enum dc1394color_filter_t
    public static final int
            DC1394_COLOR_FILTER_RGGB = 512,
            DC1394_COLOR_FILTER_GBRG = 513,
            DC1394_COLOR_FILTER_GRBG = 514,
            DC1394_COLOR_FILTER_BGGR = 515,

            DC1394_COLOR_FILTER_MIN =      DC1394_COLOR_FILTER_RGGB,
            DC1394_COLOR_FILTER_MAX =      DC1394_COLOR_FILTER_BGGR,
            DC1394_COLOR_FILTER_NUM =     (DC1394_COLOR_FILTER_MAX - DC1394_COLOR_FILTER_MIN + 1);

    // enum dc1394byte_order_t
    public static final int
            DC1394_BYTE_ORDER_UYVY=800,
            DC1394_BYTE_ORDER_YUYV=801,

            DC1394_BYTE_ORDER_MIN =      DC1394_BYTE_ORDER_UYVY,
            DC1394_BYTE_ORDER_MAX =      DC1394_BYTE_ORDER_YUYV,
            DC1394_BYTE_ORDER_NUM =     (DC1394_BYTE_ORDER_MAX - DC1394_BYTE_ORDER_MIN + 1);

    public static class dc1394color_codings_t extends Pointer {
        static { load(); }
        public native int num();          public native dc1394color_codings_t num(int num);

        @Cast("dc1394color_coding_t") // codings[DC1394_COLOR_CODING_NUM]
        public native int codings(int i); public native dc1394color_codings_t codings(int i, int codings);
    }

    public static class dc1394video_modes_t extends Pointer {
        static { load(); }
        public native int num();          public native dc1394video_modes_t num(int num);
        @Cast("dc1394video_mode_t") // modes[DC1394_VIDEO_MODE_NUM]
        public native int modes(int i);   public native dc1394video_modes_t modes(int i, int modes);
    }

    // enum dc1394bool_t
    public static final int
            DC1394_FALSE= 0,
            DC1394_TRUE= 1;

    // enum dc1394switch_t
    public static final int
            DC1394_OFF= 0,
            DC1394_ON= 1;


    // enum dc1394iidc_version_t
    public static final int
            DC1394_IIDC_VERSION_1_04 = 544,
            DC1394_IIDC_VERSION_1_20 = 545,
            DC1394_IIDC_VERSION_PTGREY = 546,
            DC1394_IIDC_VERSION_1_30 = 547,
            DC1394_IIDC_VERSION_1_31 = 548,
            DC1394_IIDC_VERSION_1_32 = 549,
            DC1394_IIDC_VERSION_1_33 = 550,
            DC1394_IIDC_VERSION_1_34 = 551,
            DC1394_IIDC_VERSION_1_35 = 552,
            DC1394_IIDC_VERSION_1_36 = 553,
            DC1394_IIDC_VERSION_1_37 = 554,
            DC1394_IIDC_VERSION_1_38 = 555,
            DC1394_IIDC_VERSION_1_39 = 556,

            DC1394_IIDC_VERSION_MIN =       DC1394_IIDC_VERSION_1_04,
            DC1394_IIDC_VERSION_MAX =       DC1394_IIDC_VERSION_1_39,
            DC1394_IIDC_VERSION_NUM =      (DC1394_IIDC_VERSION_MAX - DC1394_IIDC_VERSION_MIN + 1);

    // enum dc1394power_class_t
    public static final int
            DC1394_POWER_CLASS_NONE=608,
            DC1394_POWER_CLASS_PROV_MIN_15W=609,
            DC1394_POWER_CLASS_PROV_MIN_30W=610,
            DC1394_POWER_CLASS_PROV_MIN_45W=611,
            DC1394_POWER_CLASS_USES_MAX_1W=612,
            DC1394_POWER_CLASS_USES_MAX_3W=613,
            DC1394_POWER_CLASS_USES_MAX_6W=614,
            DC1394_POWER_CLASS_USES_MAX_10W=615,

            DC1394_POWER_CLASS_MIN=       DC1394_POWER_CLASS_NONE,
            DC1394_POWER_CLASS_MAX=       DC1394_POWER_CLASS_USES_MAX_10W,
            DC1394_POWER_CLASS_NUM=      (DC1394_POWER_CLASS_MAX - DC1394_POWER_CLASS_MIN + 1);

    // enum dc1394phy_delay_t
    public static final int
            DC1394_PHY_DELAY_MAX_144_NS=640,
            DC1394_PHY_DELAY_UNKNOWN_0=641,
            DC1394_PHY_DELAY_UNKNOWN_1=642,
            DC1394_PHY_DELAY_UNKNOWN_2=643,

            DC1394_PHY_DELAY_MIN=         DC1394_PHY_DELAY_MAX_144_NS,
            DC1394_PHY_DELAY_MAX=         DC1394_PHY_DELAY_UNKNOWN_0,
            DC1394_PHY_DELAY_NUM=        (DC1394_PHY_DELAY_MAX - DC1394_PHY_DELAY_MIN + 1);

    public static class dc1394camera_t extends Pointer {
        static { load(); }
        public dc1394camera_t() { allocate(); }
        public dc1394camera_t(int size) { allocateArray(size); }
        public dc1394camera_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394camera_t position(int position) {
            return (dc1394camera_t)super.position(position);
        }

        public native long guid();                     public native dc1394camera_t guid(long guid);
        public native int  unit();                     public native dc1394camera_t unit(int unit);
        public native int  unit_spec_ID();             public native dc1394camera_t unit_spec_ID(int unit_spec_ID);
        public native int  unit_sw_version();          public native dc1394camera_t unit_sw_version(int unit_sw_version);
        public native int  unit_sub_sw_version();      public native dc1394camera_t unit_sub_sw_version(int unit_sub_sw_version);
        public native int  command_registers_base();   public native dc1394camera_t command_registers_base(int command_registers_base);
        public native int  unit_directory();           public native dc1394camera_t unit_directory(int unit_directory);
        public native int  unit_dependent_directory(); public native dc1394camera_t unit_dependent_directory(int unit_dependent_directory);
        public native long advanced_features_csr();    public native dc1394camera_t advanced_features_csr(long advanced_features_csr);
        public native long PIO_control_csr();          public native dc1394camera_t PIO_control_csr(long PIO_control_csr);
        public native long SIO_control_csr();          public native dc1394camera_t SIO_control_csr(long SIO_control_csr);
        public native long strobe_control_csr();       public native dc1394camera_t strobe_control_csr(long strobe_control_csr);
        // long format7_csr[DC1394_VIDEO_MODE_FORMAT7_NUM]
        public native long format7_csr(int i);         public native dc1394camera_t format7_csr(int i, long format7_csr);
        @Cast("dc1394iidc_version_t")
        public native int  iidc_version();             public native dc1394camera_t iidc_version(int iidc_version);
        @Cast("char*")
        public native BytePointer vendor();            public native dc1394camera_t vendor(BytePointer vendor);
        @Cast("char*")
        public native BytePointer model();             public native dc1394camera_t model(BytePointer model);
        public native int  vendor_id();                public native dc1394camera_t vendor_id(int vendor_id);
        public native int  model_id();                 public native dc1394camera_t model_id(int model_id);
        @Cast("dc1394bool_t")
        public native int  bmode_capable();            public native dc1394camera_t bmode_capable(int bmode_capable);
        @Cast("dc1394bool_t")
        public native int  one_shot_capable();         public native dc1394camera_t one_shot_capable(int one_shot_capable);
        @Cast("dc1394bool_t")
        public native int  multi_shot_capable();       public native dc1394camera_t multi_shot_capable(int multi_shot_capable);
        @Cast("dc1394bool_t")
        public native int  can_switch_on_off();        public native dc1394camera_t can_switch_on_off(int can_switch_on_off);
        @Cast("dc1394bool_t")
        public native int  has_vmode_error_status();   public native dc1394camera_t has_vmode_error_status(int has_vmode_error_status);
        @Cast("dc1394bool_t")
        public native int  has_feature_error_status(); public native dc1394camera_t has_feature_error_status(int has_feature_error_status);
        public native int  max_mem_channel();          public native dc1394camera_t max_mem_channel(int max_mem_channel);

        public native int  flags();                    public native dc1394camera_t flags(int flags);
    }

    public static class dc1394camera_id_t extends Pointer {
        static { load(); }
        public dc1394camera_id_t() { allocate(); }
        public dc1394camera_id_t(int size) { allocateArray(size); }
        public dc1394camera_id_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394camera_id_t position(int position) {
            return (dc1394camera_id_t)super.position(position);
        }

        public native short unit(); public native dc1394camera_id_t unit(short unit);
        public native long  guid(); public native dc1394camera_id_t guid(long  guid);
    }

    public static class dc1394camera_list_t extends Pointer {
        static { load(); }
        public dc1394camera_list_t() { allocate(); }
        public dc1394camera_list_t(int size) { allocateArray(size); }
        public dc1394camera_list_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394camera_list_t position(int position) {
            return (dc1394camera_list_t)super.position(position);
        }

        public native int num();               public native dc1394camera_list_t num(int num);
        public native dc1394camera_id_t ids(); public native dc1394camera_list_t ids(dc1394camera_id_t ids);
    }

    @Opaque public static class dc1394_t extends Pointer {
        static { load(); }
        public dc1394_t() { }
        public dc1394_t(Pointer p) { super(p); }
    }

    public static native dc1394_t dc1394_new();
    public static native void dc1394_free(dc1394_t dc1394);
    public static native @Cast("dc1394error_t") int dc1394_camera_set_broadcast(dc1394camera_t camera,
            @Cast("dc1394bool_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_camera_get_broadcast(dc1394camera_t camera,
            @Cast("dc1394bool_t*") int[] pwr);
    public static native @Cast("dc1394error_t") int dc1394_reset_bus(dc1394camera_t camera);
    public static native @Cast("dc1394error_t") int dc1394_read_cycle_timer (dc1394camera_t camera,
            @Cast("uint32_t*") int[] cycle_timer, @Cast("uint64_t*") long[] local_time);
    public static native @Cast("dc1394error_t") int dc1394_camera_get_node(dc1394camera_t camera,
            @Cast("uint32_t*") int[] node, @Cast("uint32_t*") int[] generation);

    public static native @Cast("dc1394error_t") int dc1394_camera_enumerate(dc1394_t dc1394,
            @ByPtrPtr dc1394camera_list_t list);
    public static native void dc1394_camera_free_list(dc1394camera_list_t list);
    public static native dc1394camera_t dc1394_camera_new(dc1394_t dc1394, long guid);
    public static native dc1394camera_t dc1394_camera_new_unit(dc1394_t dc1394, long guid, int unit);
    public static native void dc1394_camera_free(dc1394camera_t camera);
    public static native @Cast("dc1394error_t") int dc1394_camera_print_info(dc1394camera_t camera,
            @Cast("FILE*") Pointer fd);


    // enum dc1394trigger_mode_t
    public static final int
            DC1394_TRIGGER_MODE_0= 384,
            DC1394_TRIGGER_MODE_1= 385,
            DC1394_TRIGGER_MODE_2= 386,
            DC1394_TRIGGER_MODE_3= 387,
            DC1394_TRIGGER_MODE_4= 388,
            DC1394_TRIGGER_MODE_5= 389,
            DC1394_TRIGGER_MODE_14= 390,
            DC1394_TRIGGER_MODE_15= 391,

            DC1394_TRIGGER_MODE_MIN=    DC1394_TRIGGER_MODE_0,
            DC1394_TRIGGER_MODE_MAX=    DC1394_TRIGGER_MODE_15,
            DC1394_TRIGGER_MODE_NUM=   (DC1394_TRIGGER_MODE_MAX - DC1394_TRIGGER_MODE_MIN + 1);

    // enum dc1394feature_t
    public static final int
            DC1394_FEATURE_BRIGHTNESS= 416,
            DC1394_FEATURE_EXPOSURE= 417,
            DC1394_FEATURE_SHARPNESS= 418,
            DC1394_FEATURE_WHITE_BALANCE= 419,
            DC1394_FEATURE_HUE= 420,
            DC1394_FEATURE_SATURATION= 421,
            DC1394_FEATURE_GAMMA= 422,
            DC1394_FEATURE_SHUTTER= 423,
            DC1394_FEATURE_GAIN= 424,
            DC1394_FEATURE_IRIS= 425,
            DC1394_FEATURE_FOCUS= 426,
            DC1394_FEATURE_TEMPERATURE= 427,
            DC1394_FEATURE_TRIGGER= 428,
            DC1394_FEATURE_TRIGGER_DELAY= 429,
            DC1394_FEATURE_WHITE_SHADING= 430,
            DC1394_FEATURE_FRAME_RATE= 431,
            DC1394_FEATURE_ZOOM= 432,
            DC1394_FEATURE_PAN= 433,
            DC1394_FEATURE_TILT= 434,
            DC1394_FEATURE_OPTICAL_FILTER= 435,
            DC1394_FEATURE_CAPTURE_SIZE= 436,
            DC1394_FEATURE_CAPTURE_QUALITY= 437,

            DC1394_FEATURE_MIN=          DC1394_FEATURE_BRIGHTNESS,
            DC1394_FEATURE_MAX=          DC1394_FEATURE_CAPTURE_QUALITY,
            DC1394_FEATURE_NUM=         (DC1394_FEATURE_MAX - DC1394_FEATURE_MIN + 1);

    // enum dc1394trigger_source_t
    public static final int
            DC1394_TRIGGER_SOURCE_0= 576,
            DC1394_TRIGGER_SOURCE_1= 577,
            DC1394_TRIGGER_SOURCE_2= 578,
            DC1394_TRIGGER_SOURCE_3= 579,
            DC1394_TRIGGER_SOURCE_SOFTWARE= 580,

            DC1394_TRIGGER_SOURCE_MIN=     DC1394_TRIGGER_SOURCE_0,
            DC1394_TRIGGER_SOURCE_MAX=     DC1394_TRIGGER_SOURCE_SOFTWARE,
            DC1394_TRIGGER_SOURCE_NUM=    (DC1394_TRIGGER_SOURCE_MAX - DC1394_TRIGGER_SOURCE_MIN + 1);

    // enum dc1394trigger_polarity_t
    public static final int
            DC1394_TRIGGER_ACTIVE_LOW= 704,
            DC1394_TRIGGER_ACTIVE_HIGH= 705,

            DC1394_TRIGGER_ACTIVE_MIN=   DC1394_TRIGGER_ACTIVE_LOW,
            DC1394_TRIGGER_ACTIVE_MAX=   DC1394_TRIGGER_ACTIVE_HIGH,
            DC1394_TRIGGER_ACTIVE_NUM=  (DC1394_TRIGGER_ACTIVE_MAX - DC1394_TRIGGER_ACTIVE_MIN + 1);

    // enum dc1394feature_mode_t
    public static final int
            DC1394_FEATURE_MODE_MANUAL= 736,
            DC1394_FEATURE_MODE_AUTO= 737,
            DC1394_FEATURE_MODE_ONE_PUSH_AUTO= 738,

            DC1394_FEATURE_MODE_MIN=     DC1394_FEATURE_MODE_MANUAL,
            DC1394_FEATURE_MODE_MAX=     DC1394_FEATURE_MODE_ONE_PUSH_AUTO,
            DC1394_FEATURE_MODE_NUM=    (DC1394_FEATURE_MODE_MAX - DC1394_FEATURE_MODE_MIN + 1);

    public static class dc1394feature_modes_t extends Pointer {
        static { load(); }
        public dc1394feature_modes_t() { allocate(); }
        public dc1394feature_modes_t(int size) { allocateArray(size); }
        public dc1394feature_modes_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394feature_modes_t position(int position) {
            return (dc1394feature_modes_t)super.position(position);
        }

        public native int num();          public native dc1394video_modes_t num(int num);
        @Cast("dc1394feature_mode_t") // modes[DC1394_FEATURE_MODE_NUM]
        public native int modes(int i);   public native dc1394video_modes_t modes(int i, int modes);
    }

    public static class dc1394trigger_modes_t extends Pointer {
        static { load(); }
        public dc1394trigger_modes_t() { allocate(); }
        public dc1394trigger_modes_t(int size) { allocateArray(size); }
        public dc1394trigger_modes_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394trigger_modes_t position(int position) {
            return (dc1394trigger_modes_t)super.position(position);
        }

        public native int num();          public native dc1394video_modes_t num(int num);
        @Cast("dc1394trigger_mode_t") // modes[DC1394_TRIGGER_MODE_NUM]
        public native int modes(int i);   public native dc1394video_modes_t modes(int i, int modes);
    }

    public static class dc1394trigger_sources_t extends Pointer {
        static { load(); }
        public dc1394trigger_sources_t() { allocate(); }
        public dc1394trigger_sources_t(int size) { allocateArray(size); }
        public dc1394trigger_sources_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394trigger_sources_t position(int position) {
            return (dc1394trigger_sources_t)super.position(position);
        }

        public native int num();          public native dc1394video_modes_t num(int num);
        @Cast("dc1394trigger_source_t") // sources[DC1394_TRIGGER_SOURCE_NUM]
        public native int sources(int i); public native dc1394video_modes_t sources(int i, int sources);
    }

    public static class dc1394feature_info_t extends Pointer {
        static { load(); }
        public dc1394feature_info_t() { allocate(); }
        public dc1394feature_info_t(int size) { allocateArray(size); }
        public dc1394feature_info_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394feature_info_t position(int position) {
            return (dc1394feature_info_t)super.position(position);
        }

        @Cast("dc1394feature_t")
        public native int id();               public native dc1394feature_info_t id(int id);
        @Cast("dc1394bool_t")
        public native int available();        public native dc1394feature_info_t available(int available);
        @Cast("dc1394bool_t")
        public native int absolute_capable(); public native dc1394feature_info_t absolute_capable(int absolute_capable);
        @Cast("dc1394bool_t")
        public native int readout_capable();  public native dc1394feature_info_t readout_capable(int readout_capable);
        @Cast("dc1394bool_t")
        public native int on_off_capable();   public native dc1394feature_info_t on_off_capable(int on_off_capable);
        @Cast("dc1394bool_t")
        public native int polarity_capable(); public native dc1394feature_info_t polarity_capable(int polarity_capable);
        @Cast("dc1394switch_t")
        public native int is_on();            public native dc1394feature_info_t is_on(int is_on);
        @Cast("dc1394feature_mode_t")
        public native int current_mode();     public native dc1394feature_info_t current_mode(int current_mode);
        @ByVal public native dc1394feature_modes_t modes();
               public native dc1394feature_info_t  modes(dc1394feature_modes_t modes);
        @ByVal public native dc1394trigger_modes_t trigger_modes();
               public native dc1394feature_info_t  trigger_modes(dc1394trigger_modes_t trigger_modes);
        @Cast("dc1394trigger_mode_t")
        public native int trigger_mode();     public native dc1394feature_info_t trigger_mode(int trigger_mode);
        @Cast("dc1394trigger_polarity_t")
        public native int trigger_polarity(); public native dc1394feature_info_t trigger_polarity(int trigger_polarity);
        @ByVal public native dc1394trigger_sources_t trigger_sources();
               public native dc1394feature_info_t    trigger_sources(dc1394trigger_sources_t trigger_sources);
        @Cast("dc1394trigger_source_t")
        public native int trigger_source();   public native dc1394feature_info_t trigger_source(int trigger_source);
        public native int min();              public native dc1394feature_info_t min(int min);
        public native int max();              public native dc1394feature_info_t max(int max);
        public native int value();            public native dc1394feature_info_t value(int value);
        public native int BU_value();         public native dc1394feature_info_t BU_value(int BU_value);
        public native int RV_value();         public native dc1394feature_info_t RV_value(int RV_value);
        public native int B_value();          public native dc1394feature_info_t B_value(int B_value);
        public native int R_value();          public native dc1394feature_info_t R_value(int R_value);
        public native int G_value();          public native dc1394feature_info_t G_value(int G_value);
        public native int target_value();     public native dc1394feature_info_t target_value(int target_value);

        @Cast("dc1394switch_t")
        public native int  abs_control();     public native dc1394feature_info_t abs_control(int abs_control);
        public native float abs_value();      public native dc1394feature_info_t abs_value(float abs_value);
        public native float abs_max();        public native dc1394feature_info_t abs_max(float abs_max);
        public native float abs_min();        public native dc1394feature_info_t abs_min(float abs_min);
    }

    public static class dc1394featureset_t extends Pointer {
        static { load(); }
        public dc1394featureset_t() { allocate(); }
        public dc1394featureset_t(int size) { allocateArray(size); }
        public dc1394featureset_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394featureset_t position(int position) {
            return (dc1394featureset_t)super.position(position);
        }

        // dc1394feature_info_t feature[DC1394_FEATURE_NUM]
        @MemberGetter public native dc1394feature_info_t feature();
    }

    public static native @Cast("dc1394error_t") int dc1394_feature_get_all(dc1394camera_t camera,
            dc1394featureset_t features);
    public static native @Cast("dc1394error_t") int dc1394_feature_get(dc1394camera_t camera,
            dc1394feature_info_t feature);
    public static native @Cast("dc1394error_t") int dc1394_feature_print(dc1394feature_info_t feature,
            @Cast("FILE*") Pointer fd);
    public static native @Cast("dc1394error_t") int dc1394_feature_print_all(dc1394featureset_t features,
            @Cast("FILE*") Pointer fd);
    public static native @Cast("dc1394error_t") int dc1394_feature_whitebalance_get_value(dc1394camera_t camera,
            @Cast("uint32_t*") int[] u_b_value, @Cast("uint32_t*") int[] v_r_value);
    public static native @Cast("dc1394error_t") int dc1394_feature_whitebalance_set_value(dc1394camera_t camera,
            int u_b_value, int v_r_value);
    public static native @Cast("dc1394error_t") int dc1394_feature_temperature_get_value(dc1394camera_t camera,
            @Cast("uint32_t*") int[] target_temperature, @Cast("uint32_t*") int[] temperature);
    public static native @Cast("dc1394error_t") int dc1394_feature_temperature_set_value(dc1394camera_t camera,
            int target_temperature);
    public static native @Cast("dc1394error_t") int dc1394_feature_whiteshading_get_value(dc1394camera_t camera,
            @Cast("uint32_t*") int[] r_value, @Cast("uint32_t*") int[] g_value, @Cast("uint32_t*") int[] b_value);
    public static native @Cast("dc1394error_t") int dc1394_feature_whiteshading_set_value(dc1394camera_t camera,
            int r_value, int g_value, int b_value);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_value(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("uint32_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_feature_set_value(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, int value);
    public static native @Cast("dc1394error_t") int dc1394_feature_is_present(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394bool_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_feature_is_readable(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394bool_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_boundaries(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("uint32_t*") int[] min, @Cast("uint32_t*") int[] max);
    public static native @Cast("dc1394error_t") int dc1394_feature_is_switchable(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394bool_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_power(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394switch_t*") int[] pwr);
    public static native @Cast("dc1394error_t") int dc1394_feature_set_power(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_modes(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, dc1394feature_modes_t modes);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_mode(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394feature_mode_t*") int[] mode);
    public static native @Cast("dc1394error_t") int dc1394_feature_set_mode(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394feature_mode_t") int mode);
    public static native @Cast("dc1394error_t") int dc1394_feature_has_absolute_control(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394bool_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_absolute_boundaries(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, float[] min, float[] max);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_absolute_value(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, float[] value);
    public static native @Cast("dc1394error_t") int dc1394_feature_set_absolute_value(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, float value);
    public static native @Cast("dc1394error_t") int dc1394_feature_get_absolute_control(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394switch_t*") int[] pwr);
    public static native @Cast("dc1394error_t") int dc1394_feature_set_absolute_control(dc1394camera_t camera,
            @Cast("dc1394feature_t") int feature, @Cast("dc1394switch_t") int pwr);

    public static native @Cast("dc1394error_t") int dc1394_external_trigger_set_polarity(dc1394camera_t camera,
            @Cast("dc1394trigger_polarity_t") int polarity);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_get_polarity(dc1394camera_t camera,
            @Cast("dc1394trigger_polarity_t*") int[] polarity);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_has_polarity(dc1394camera_t camera,
            @Cast("dc1394bool_t*") int[] polarity_capable);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_set_power(dc1394camera_t camera,
            @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_get_power(dc1394camera_t camera,
            @Cast("dc1394switch_t*") int[] pwr);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_set_mode(dc1394camera_t camera,
            @Cast("dc1394trigger_mode_t") int mode);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_get_mode(dc1394camera_t camera,
            @Cast("dc1394trigger_mode_t*") int[] mode);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_set_source(dc1394camera_t camera,
            @Cast("dc1394trigger_source_t") int source);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_get_source(dc1394camera_t camera,
            @Cast("dc1394trigger_source_t*") int[] source);
    public static native @Cast("dc1394error_t") int dc1394_external_trigger_get_supported_sources(dc1394camera_t camera,
            dc1394trigger_sources_t sources);
    public static native @Cast("dc1394error_t") int dc1394_software_trigger_set_power(dc1394camera_t camera,
            @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_software_trigger_get_power(dc1394camera_t camera,
            @Cast("dc1394switch_t*") int[] pwr);

    public static native @Cast("dc1394error_t") int dc1394_pio_set(dc1394camera_t camera, int value);
    public static native @Cast("dc1394error_t") int dc1394_pio_get(dc1394camera_t camera, @Cast("uint32_t*") int[] value);

    public static native @Cast("dc1394error_t") int dc1394_camera_reset(dc1394camera_t camera);
    public static native @Cast("dc1394error_t") int dc1394_camera_set_power(dc1394camera_t camera,
            @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_memory_busy(dc1394camera_t camera,
            @Cast("dc1394bool_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_memory_save(dc1394camera_t camera, int channel);
    public static native @Cast("dc1394error_t") int dc1394_memory_load(dc1394camera_t camera, int channel);


    // enum dc1394capture_policy_t
    public static final int
            DC1394_CAPTURE_POLICY_WAIT=672,
            DC1394_CAPTURE_POLICY_POLL=673,

            DC1394_CAPTURE_POLICY_MIN=   DC1394_CAPTURE_POLICY_WAIT,
            DC1394_CAPTURE_POLICY_MAX=   DC1394_CAPTURE_POLICY_POLL,
            DC1394_CAPTURE_POLICY_NUM=  (DC1394_CAPTURE_POLICY_MAX - DC1394_CAPTURE_POLICY_MIN + 1);

    public static final int
            DC1394_CAPTURE_FLAGS_CHANNEL_ALLOC   = 0x00000001,
            DC1394_CAPTURE_FLAGS_BANDWIDTH_ALLOC = 0x00000002,
            DC1394_CAPTURE_FLAGS_DEFAULT         = 0x00000004,
            DC1394_CAPTURE_FLAGS_AUTO_ISO        = 0x00000008;

    public static native @Cast("dc1394error_t") int dc1394_capture_setup(dc1394camera_t camera,
            int num_dma_buffers, int flags);
    public static native @Cast("dc1394error_t") int dc1394_capture_stop(dc1394camera_t camera);
    public static native int dc1394_capture_get_fileno(dc1394camera_t camera);
    public static native @Cast("dc1394error_t") int dc1394_capture_dequeue(dc1394camera_t camera,
            @Cast("dc1394capture_policy_t") int policy, @ByPtrPtr dc1394video_frame_t frame);
    public static native @Cast("dc1394error_t") int dc1394_capture_enqueue(dc1394camera_t camera,
            dc1394video_frame_t frame);
    public static native @Cast("dc1394bool_t") int dc1394_capture_is_frame_corrupt (dc1394camera_t camera,
            dc1394video_frame_t frame);


    // enum dc1394speed_t
    public static final int
            DC1394_ISO_SPEED_100= 0,
            DC1394_ISO_SPEED_200= 1,
            DC1394_ISO_SPEED_400= 2,
            DC1394_ISO_SPEED_800= 3,
            DC1394_ISO_SPEED_1600= 4,
            DC1394_ISO_SPEED_3200= 5,

            DC1394_ISO_SPEED_MIN=                  DC1394_ISO_SPEED_100,
            DC1394_ISO_SPEED_MAX=                  DC1394_ISO_SPEED_3200,
            DC1394_ISO_SPEED_NUM=                 (DC1394_ISO_SPEED_MAX - DC1394_ISO_SPEED_MIN + 1);

    // enum dc1394framerate_t
    public static final int
            DC1394_FRAMERATE_1_875= 32,
            DC1394_FRAMERATE_3_75= 33,
            DC1394_FRAMERATE_7_5= 34,
            DC1394_FRAMERATE_15= 35,
            DC1394_FRAMERATE_30= 36,
            DC1394_FRAMERATE_60= 37,
            DC1394_FRAMERATE_120= 38,
            DC1394_FRAMERATE_240= 39,

            DC1394_FRAMERATE_MIN=              DC1394_FRAMERATE_1_875,
            DC1394_FRAMERATE_MAX=              DC1394_FRAMERATE_240,
            DC1394_FRAMERATE_NUM=             (DC1394_FRAMERATE_MAX - DC1394_FRAMERATE_MIN + 1);

    //enum dc1394operation_mode_t
    public static final int
            DC1394_OPERATION_MODE_LEGACY = 480,
            DC1394_OPERATION_MODE_1394B = 481,

            DC1394_OPERATION_MODE_MIN =  DC1394_OPERATION_MODE_LEGACY,
            DC1394_OPERATION_MODE_MAX =  DC1394_OPERATION_MODE_1394B,
            DC1394_OPERATION_MODE_NUM = (DC1394_OPERATION_MODE_MAX - DC1394_OPERATION_MODE_MIN + 1);

    public static class dc1394framerates_t extends Pointer {
        static { load(); }
        public dc1394framerates_t() { allocate(); }
        public dc1394framerates_t(int size) { allocateArray(size); }
        public dc1394framerates_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394framerates_t position(int position) {
            return (dc1394framerates_t)super.position(position);
        }

        public native int num();             public native dc1394video_modes_t num(int num);
        @Cast("dc1394framerate_t") // modes[DC1394_FRAMERATE_NUM]
        public native int framerates(int i); public native dc1394video_modes_t framerates(int i, int framerates);
    }

    public static class dc1394video_frame_t extends Pointer {
        static { load(); }
        public dc1394video_frame_t() { allocate(); }
        public dc1394video_frame_t(int size) { allocateArray(size); }
        public dc1394video_frame_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394video_frame_t position(int position) {
            return (dc1394video_frame_t)super.position(position);
        }

        @Cast("unsigned char*")
        public native BytePointer image();      public native dc1394video_frame_t image(BytePointer image);
        public native int/*[2]*/  size(int i);  public native dc1394video_frame_t size(int i, int size);
        @Name("position")
        public native int/*[2]*/  pos(int i);   public native dc1394video_frame_t pos(int i, int packets_per_frame);
        @Cast("dc1394color_coding_t")
        public native int  color_coding();      public native dc1394video_frame_t color_coding(int color_coding);
        @Cast("dc1394color_filter_t")
        public native int  color_filter();      public native dc1394video_frame_t color_filter(int color_filter);
        public native int  yuv_byte_order();    public native dc1394video_frame_t yuv_byte_order(int yuv_byte_order);
        public native int  data_depth();        public native dc1394video_frame_t data_depth(int data_depth);
        public native int  stride();            public native dc1394video_frame_t stride(int stride);
        @Cast("dc1394video_mode_t")
        public native int  video_mode();        public native dc1394video_frame_t video_mode(int video_mode);
        public native long total_bytes();       public native dc1394video_frame_t total_bytes(long total_bytes);
        public native int  image_bytes();       public native dc1394video_frame_t image_bytes(int image_bytes);
        public native int  padding_bytes();     public native dc1394video_frame_t padding_bytes(int padding_bytes);
        public native int  packet_size();       public native dc1394video_frame_t packet_size(int packet_size);
        public native int  packets_per_frame(); public native dc1394video_frame_t packets_per_frame(int packets_per_frame);
        public native long timestamp();         public native dc1394video_frame_t timestamp(long timestamp);
        public native int  frames_behind();     public native dc1394video_frame_t frames_behind(int frames_behind);
        public native dc1394camera_t camera();  public native dc1394video_frame_t camera(dc1394camera_t camera);
        public native int  id();                public native dc1394video_frame_t id(int id);
        public native long allocated_image_bytes(); public native dc1394video_frame_t allocated_image_bytes(long allocated_image_bytes);
        @Cast("dc1394bool_t")
        public native int  little_endian();         public native dc1394video_frame_t little_endian(int little_endian);
        @Cast("dc1394bool_t")
        public native int  data_in_padding();       public native dc1394video_frame_t data_in_padding(int data_in_padding);

        public ByteBuffer getByteBuffer() { return image().asBuffer((int)total_bytes()); }
    }

    public static native @Cast("dc1394error_t") int dc1394_video_get_supported_modes(dc1394camera_t camera,
            dc1394video_modes_t video_modes);
    public static native @Cast("dc1394error_t") int dc1394_video_get_supported_framerates(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, dc1394framerates_t framerates);
    public static native @Cast("dc1394error_t") int dc1394_video_get_framerate(dc1394camera_t camera,
            @Cast("dc1394framerate_t*") int[] framerate);
    public static native @Cast("dc1394error_t") int dc1394_video_set_framerate(dc1394camera_t camera,
            @Cast("dc1394framerate_t") int framerate);
    public static native @Cast("dc1394error_t") int dc1394_video_get_mode(dc1394camera_t camera,
            @Cast("dc1394video_mode_t*") int[] video_mode);
    public static native @Cast("dc1394error_t") int dc1394_video_set_mode(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode);
    public static native @Cast("dc1394error_t") int dc1394_video_get_operation_mode(dc1394camera_t camera,
            @Cast("dc1394operation_mode_t*") int[] mode);
    public static native @Cast("dc1394error_t") int dc1394_video_set_operation_mode(dc1394camera_t camera,
            @Cast("dc1394operation_mode_t") int mode);
    public static native @Cast("dc1394error_t") int dc1394_video_get_iso_speed(dc1394camera_t camera,
            @Cast("dc1394speed_t*") int[] speed);
    public static native @Cast("dc1394error_t") int dc1394_video_set_iso_speed(dc1394camera_t camera,
            @Cast("dc1394speed_t") int speed);
    public static native @Cast("dc1394error_t") int dc1394_video_get_iso_channel(dc1394camera_t camera,
            @Cast("uint32_t*") int[] channel);
    public static native @Cast("dc1394error_t") int dc1394_video_set_iso_channel(dc1394camera_t camera,
            int channel);
    public static native @Cast("dc1394error_t") int dc1394_video_get_data_depth(dc1394camera_t camera,
            @Cast("uint32_t*") int[] depth);
    public static native @Cast("dc1394error_t") int dc1394_video_set_transmission(dc1394camera_t camera,
            @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_video_get_transmission(dc1394camera_t camera,
            @Cast("dc1394switch_t*") int[] pwr);
    public static native @Cast("dc1394error_t") int dc1394_video_set_one_shot(dc1394camera_t camera,
            @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_video_get_one_shot(dc1394camera_t camera,
            @Cast("dc1394bool_t*") int[] is_on);
    public static native @Cast("dc1394error_t") int dc1394_video_set_multi_shot(dc1394camera_t camera,
            int numFrames, @Cast("dc1394switch_t") int pwr);
    public static native @Cast("dc1394error_t") int dc1394_video_get_multi_shot(dc1394camera_t camera,
            @Cast("dc1394bool_t*") int[]  is_on, @Cast("uint32_t*") int[] numFrames);
    public static native @Cast("dc1394error_t") int dc1394_video_get_bandwidth_usage(dc1394camera_t camera,
            @Cast("uint32_t*") int[] bandwidth);


    // enum dc1394bayer_method_t
    public static final int
            DC1394_BAYER_METHOD_NEAREST=0,
            DC1394_BAYER_METHOD_SIMPLE=1,
            DC1394_BAYER_METHOD_BILINEAR=2,
            DC1394_BAYER_METHOD_HQLINEAR=3,
            DC1394_BAYER_METHOD_DOWNSAMPLE=4,
            DC1394_BAYER_METHOD_EDGESENSE=5,
            DC1394_BAYER_METHOD_VNG=6,
            DC1394_BAYER_METHOD_AHD=7,

            DC1394_BAYER_METHOD_MIN=     DC1394_BAYER_METHOD_NEAREST,
            DC1394_BAYER_METHOD_MAX=     DC1394_BAYER_METHOD_AHD,
            DC1394_BAYER_METHOD_NUM=    (DC1394_BAYER_METHOD_MAX-DC1394_BAYER_METHOD_MIN+1);

    // enum dc1394stereo_method_t
    public static final int
            DC1394_STEREO_METHOD_INTERLACED=0,
            DC1394_STEREO_METHOD_FIELD=1,

            DC1394_STEREO_METHOD_MIN=    DC1394_STEREO_METHOD_INTERLACED,
            DC1394_STEREO_METHOD_MAX=    DC1394_STEREO_METHOD_FIELD,
            DC1394_STEREO_METHOD_NUM=   (DC1394_STEREO_METHOD_MAX-DC1394_STEREO_METHOD_MIN+1);

    public static native @Cast("dc1394error_t") int dc1394_convert_to_YUV422(
            @Cast("uint8_t*") BytePointer src, @Cast("uint8_t*") BytePointer dest,
            int width, int height, int byte_order,
            @Cast("dc1394color_coding_t") int source_coding, int bits);
    public static native @Cast("dc1394error_t") int dc1394_convert_to_MONO8(
            @Cast("uint8_t*") BytePointer src, @Cast("uint8_t*") BytePointer dest,
            int width, int height, int byte_order,
            @Cast("dc1394color_coding_t") int source_coding, int bits);
    public static native @Cast("dc1394error_t") int dc1394_convert_to_RGB8(
            @Cast("uint8_t*") BytePointer src, @Cast("uint8_t*") BytePointer dest,
            int width, int height, int byte_order,
            @Cast("dc1394color_coding_t") int source_coding, int bits);

    public static native @Cast("dc1394error_t") int dc1394_deinterlace_stereo(
            @Cast("uint8_t*") BytePointer src, @Cast("uint8_t*") BytePointer dest,
            int width, int height);

    public static native @Cast("dc1394error_t") int dc1394_bayer_decoding_8bit(
            @Cast("uint8_t*") BytePointer bayer, @Cast("uint8_t*") BytePointer rgb,
            int width, int height, @Cast("dc1394color_filter_t") int tile,
            @Cast("dc1394bayer_method_t") int method);
    public static native @Cast("dc1394error_t") int dc1394_bayer_decoding_16bit(
            @Cast("uint16_t*") ShortPointer bayer, @Cast("uint16_t*") ShortPointer rgb,
            int width, int height, @Cast("dc1394color_filter_t") int tile,
            @Cast("dc1394bayer_method_t") int method, int bits);

    public static native @Cast("dc1394error_t") int dc1394_convert_frames(dc1394video_frame_t in,
            dc1394video_frame_t out);
    public static native @Cast("dc1394error_t") int dc1394_debayer_frames(dc1394video_frame_t in,
            dc1394video_frame_t out, @Cast("dc1394bayer_method_t") int method);
    public static native @Cast("dc1394error_t") int dc1394_deinterlace_stereo_frames(dc1394video_frame_t in,
            dc1394video_frame_t out, @Cast("dc1394stereo_method_t") int method);


    public static class dc1394format7mode_t extends Pointer {
        static { load(); }
        public dc1394format7mode_t() { allocate(); }
        public dc1394format7mode_t(int size) { allocateArray(size); }
        public dc1394format7mode_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394format7mode_t position(int position) {
            return (dc1394format7mode_t)super.position(position);
        }

        @Cast("dc1394bool_t")
        public native int present();          public native dc1394format7mode_t present(int present);

        public native int size_x();           public native dc1394format7mode_t size_x(int size_x);
        public native int size_y();           public native dc1394format7mode_t size_y(int size_y);
        public native int max_size_x();       public native dc1394format7mode_t max_size_x(int max_size_x);
        public native int max_size_y();       public native dc1394format7mode_t max_size_y(int max_size_y);

        public native int pos_x();            public native dc1394format7mode_t pos_x(int pos_x);
        public native int pos_y();            public native dc1394format7mode_t pos_y(int pos_y);

        public native int unit_size_x();      public native dc1394format7mode_t unit_size_x(int unit_size_x);
        public native int unit_size_y();      public native dc1394format7mode_t unit_size_y(int unit_size_y);
        public native int unit_pos_x();       public native dc1394format7mode_t unit_pos_x(int unit_pos_x);
        public native int unit_pos_y();       public native dc1394format7mode_t unit_pos_y(int unit_pos_y);

        @ByVal public native dc1394color_codings_t color_codings(); 
               public native dc1394format7mode_t   color_codings(dc1394color_codings_t color_codings);
        @Cast("dc1394color_coding_t")
        public native int  color_coding();    public native dc1394format7mode_t color_coding(int color_coding);

        public native int pixnum();           public native dc1394format7mode_t pixnum(int pixnum);

        public native int packet_size();      public native dc1394format7mode_t packet_size(int packet_size);
        public native int unit_packet_size(); public native dc1394format7mode_t unit_packet_size(int unit_packet_size);
        public native int max_packet_size();  public native dc1394format7mode_t max_packet_size(int max_packet_size);

        public native long total_bytes();     public native dc1394format7mode_t total_bytes(long total_bytes);

        @Cast("dc1394color_filter_t")
        public native int color_filter();     public native dc1394format7mode_t color_filter(int color_filter);
    }

    public static class dc1394format7modeset_t extends Pointer {
        static { load(); }
        public dc1394format7modeset_t() { allocate(); }
        public dc1394format7modeset_t(int size) { allocateArray(size); }
        public dc1394format7modeset_t(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dc1394format7modeset_t position(int position) {
            return (dc1394format7modeset_t)super.position(position);
        }

        // dc1394format7mode_t mode[DC1394_VIDEO_MODE_FORMAT7_NUM]
        @MemberGetter public native dc1394format7mode_t mode();
    }

    // Parameter flags for dc1394_setup_format7_capture() ??
    public static final int
            DC1394_QUERY_FROM_CAMERA = -1,
            DC1394_USE_MAX_AVAIL     = -2,
            DC1394_USE_RECOMMENDED   = -3;

    public static native @Cast("dc1394error_t") int dc1394_format7_get_max_image_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] h_size, @Cast("uint32_t*") int[] v_size);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_unit_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] h_unit, @Cast("uint32_t*") int[] v_unit);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_image_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] width, @Cast("uint32_t*") int[] height);
    public static native @Cast("dc1394error_t") int dc1394_format7_set_image_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, int width, int height);

    public static native @Cast("dc1394error_t") int dc1394_format7_get_image_position(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] left, @Cast("uint32_t*") int[] top);
    public static native @Cast("dc1394error_t") int dc1394_format7_set_image_position(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, int left, int top);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_unit_position(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] h_unit_pos, @Cast("uint32_t*") int[] v_unit_pos);

    public static native @Cast("dc1394error_t") int dc1394_format7_get_color_coding(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode,
            @Cast("dc1394color_coding_t*") int[] color_coding);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_color_codings(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode,
            @Cast("dc1394color_codings_t*") int[] codings);
    public static native @Cast("dc1394error_t") int dc1394_format7_set_color_coding(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode,
            @Cast("dc1394color_coding_t") int color_coding);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_color_filter(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode,
            @Cast("dc1394color_filter_t*") int[] color_filter);

    public static native @Cast("dc1394error_t") int dc1394_format7_get_packet_parameters(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] unit_bytes,
            @Cast("uint32_t*") int[] max_bytes);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_packet_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] packet_size);
    public static native @Cast("dc1394error_t") int dc1394_format7_set_packet_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, int packet_size);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_recommended_packet_size(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] packet_size);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_packets_per_frame(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] ppf);

    public static native @Cast("dc1394error_t") int dc1394_format7_get_data_depth(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] data_depth);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_frame_interval(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, float[] interval);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_pixel_number(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint32_t*") int[] pixnum);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_total_bytes(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("uint64_t*") long[] total_bytes);

    public static native @Cast("dc1394error_t") int dc1394_format7_get_modeset(dc1394camera_t camera,
            dc1394format7modeset_t info);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_mode_info(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, dc1394format7mode_t f7_mode);
    public static native @Cast("dc1394error_t") int dc1394_format7_set_roi(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode,
            @Cast("dc1394color_coding_t") int color_coding, int packet_size,
            int left, int top, int width, int height);
    public static native @Cast("dc1394error_t") int dc1394_format7_get_roi(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("dc1394color_coding_t*") int[] color_coding, 
            @Cast("uint32_t*") int[] packet_size,  @Cast("uint32_t*") int[] left, @Cast("uint32_t*") int[] top,
            @Cast("uint32_t*") int[] width, @Cast("uint32_t*") int[] height);


    public static native @Cast("dc1394error_t") int dc1394_iso_set_persist(dc1394camera_t camera);
    public static native @Cast("dc1394error_t") int dc1394_iso_allocate_channel(dc1394camera_t camera,
        long channels_allowed, int[] channel);
    public static native @Cast("dc1394error_t") int dc1394_iso_release_channel(dc1394camera_t camera, int channel);
    public static native @Cast("dc1394error_t") int dc1394_iso_allocate_bandwidth(dc1394camera_t camera, int bandwidth_units);
    public static native @Cast("dc1394error_t") int dc1394_iso_release_bandwidth(dc1394camera_t camera, int bandwidth_units);
    public static native @Cast("dc1394error_t") int dc1394_iso_release_all(dc1394camera_t camera);


    public static native @Cast("dc1394error_t") int dc1394_get_registers (dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value, int num_regs/*=1*/);
    public static native @Cast("dc1394error_t") int dc1394_set_registers (dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value, int num_regs/*=1*/);

    public static native @Cast("dc1394error_t") int dc1394_get_control_registers (dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value, int num_regs/*=1*/);
    public static native @Cast("dc1394error_t") int dc1394_set_control_registers (dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value, int num_regs/*=1*/);

    public static native @Cast("dc1394error_t") int dc1394_get_adv_control_registers(dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value, int num_regs/*=1*/);
    public static native @Cast("dc1394error_t") int dc1394_set_adv_control_registers(dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value, int num_regs/*=1*/);

    public static native @Cast("dc1394error_t") int dc1394_get_format7_register(dc1394camera_t camera,
            int mode, long offset, @Cast("uint32_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_set_format7_register(dc1394camera_t camera,
            int mode, long offset, int value);

    public static native @Cast("dc1394error_t") int dc1394_get_absolute_register(dc1394camera_t camera,
            int feature, long offset, @Cast("uint32_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_set_absolute_register(dc1394camera_t camera,
            int feature, long offset, int value);

    public static native @Cast("dc1394error_t") int dc1394_get_PIO_register(dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_set_PIO_register(dc1394camera_t camera,
            long offset, int value);

    public static native @Cast("dc1394error_t") int dc1394_get_SIO_register(dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_set_SIO_register(dc1394camera_t camera,
            long offset, int value);

    public static native @Cast("dc1394error_t") int dc1394_get_strobe_register(dc1394camera_t camera,
            long offset, @Cast("uint32_t*") int[] value);
    public static native @Cast("dc1394error_t") int dc1394_set_strobe_register(dc1394camera_t camera,
            long offset, int value);


    public static native @Cast("dc1394error_t") int dc1394_get_image_size_from_video_mode(dc1394camera_t camera,
            int video_mode, @Cast("uint32_t*") int[] width, @Cast("uint32_t*") int[] height);
    public static native @Cast("dc1394error_t") int dc1394_framerate_as_float(
            @Cast("dc1394framerate_t") int framerate_enum, float[] framerate);
    public static native @Cast("dc1394error_t") int dc1394_get_color_coding_data_depth(
            @Cast("dc1394color_coding_t") int color_coding, @Cast("uint32_t*") int[] bits);
    public static native @Cast("dc1394error_t") int dc1394_get_color_coding_bit_size(
            @Cast("dc1394color_coding_t") int color_coding, @Cast("uint32_t*") int[] bits);
    public static native @Cast("dc1394error_t") int dc1394_get_color_coding_from_video_mode(dc1394camera_t camera,
            @Cast("dc1394video_mode_t") int video_mode, @Cast("dc1394color_coding_t*") int[] color_coding);
    public static native @Cast("dc1394error_t") int dc1394_is_color(@Cast("dc1394color_coding_t") int color_mode,
            @Cast("dc1394bool_t*") int[] is_color);
    public static native @Cast("dc1394bool_t") int dc1394_is_video_mode_scalable(@Cast("dc1394video_mode_t") int video_mode);
    public static native @Cast("dc1394bool_t") int dc1394_is_video_mode_still_image(@Cast("dc1394video_mode_t") int video_mode);
    public static native @Cast("dc1394bool_t") int dc1394_is_same_camera(@ByVal dc1394camera_id_t id1, @ByVal dc1394camera_id_t id2);
    public static native String dc1394_feature_get_string(@Cast("dc1394feature_t") int feature);
    public static native String dc1394_error_get_string(@Cast("dc1394error_t") int error);
    public static native short dc1394_checksum_crc16(@Cast("uint8_t*") byte[]      buffer, int buffer_size);
    public static native short dc1394_checksum_crc16(@Cast("uint8_t*") ByteBuffer  buffer, int buffer_size);
    public static native short dc1394_checksum_crc16(@Cast("uint8_t*") BytePointer buffer, int buffer_size);
}
