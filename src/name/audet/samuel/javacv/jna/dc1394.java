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

package name.audet.samuel.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import java.nio.ByteBuffer;

/**
 *
 * @author Samuel Audet
 */
public class dc1394 {
    public static final String libname;
    static {
        Native.register(libname = "dc1394");
    }

    public static class Poll {
        public static final String libname;
        static {
            Native.register(libname = "c");
        }

        public static final int
                POLLIN         = 0x001,
                POLLPRI        = 0x002,
                POLLOUT        = 0x004,
                POLLMSG        = 0x400,
                POLLREMOVE     = 0x1000,
                POLLRDHUP      = 0x2000,
                POLLERR        = 0x008,
                POLLHUP        = 0x010,
                POLLNVAL       = 0x020;

        public static class pollfd extends Structure {
            public int   fd;
            public short events;
            public short revents;
        }

        public static class timespec extends Structure {
            public NativeLong    tv_sec;
            public NativeLong    tv_nsec;
        }

        public static final int SIGSET_NWORDS = (1024 / (8 * (NativeLong.SIZE)));
        public static class sigset_t extends Structure {
            NativeLong[] val = new NativeLong[SIGSET_NWORDS];
        }

        public native static int poll(pollfd fds, NativeLong nfds, int timeout);
        public static int poll(pollfd[] fds, NativeLong nfds, int timeout) {
            for (Structure s : fds) { s.write(); }
            int i = poll(fds[0], nfds, timeout);
            for (Structure s : fds) { s.read(); }
            return i;
        }
        public native static int ppoll(pollfd fds, NativeLong nfds, timespec timeout, sigset_t sigmask);
        public static int ppoll(pollfd[] fds, NativeLong nfds, timespec timeout, sigset_t sigmask) {
            for (Structure s : fds) { s.write(); }
            int i = ppoll(fds[0], nfds, timeout, sigmask);
            for (Structure s : fds) { s.read(); }
            return i;
        }
    }

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

    public static interface Log_handler extends Callback {
        void callback(int /* dc1394log_t */ type, String message, Pointer user);
    }
    public static native int /* dc1394error_t */ dc1394_log_register_handler(
            int /* dc1394log_t */ type, Log_handler log_handler, Pointer user);
    public static native int /* dc1394error_t */ dc1394_log_register_handler(
            int /* dc1394log_t */ type, Function log_handler, Pointer user);
    public static native int /* dc1394error_t */ dc1394_log_set_default_handler(
            int /* dc1394log_t */ type);

    public static interface ComplexCalls extends Library {
        final ComplexCalls I =
                (ComplexCalls)Native.loadLibrary(libname, ComplexCalls.class);
        void dc1394_log_error(String ... format);
        void dc1394_log_warning(String ... format);
        void dc1394_log_debug(String ... format);
    }
    public static void dc1394_log_error(String ... format) {
        ComplexCalls.I.dc1394_log_error(format);
    }
    public static void dc1394_log_warning(String ... format) {
        ComplexCalls.I.dc1394_log_warning(format);
    }
    public static void dc1394_log_debug(String ... format) {
        ComplexCalls.I.dc1394_log_debug(format);
    }


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

    public static class dc1394color_codings_t extends Structure {
        public int   num;
        public int[] codings = new int/* dc1394color_coding_t */[DC1394_COLOR_CODING_NUM];
    }

    public static class dc1394video_modes_t extends Structure {
        public int   num;
        public int[] modes = new int/* dc1394video_mode_t */[DC1394_VIDEO_MODE_NUM];
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

    public static class dc1394camera_t extends Structure {
        public dc1394camera_t() { setAutoWrite(false); }

        public long                 guid;
        public int                  unit;
        public int                  unit_spec_ID;
        public int                  unit_sw_version;
        public int                  unit_sub_sw_version;
        public int                  command_registers_base;
        public int                  unit_directory;
        public int                  unit_dependent_directory;
        public long                 advanced_features_csr;
        public long                 PIO_control_csr;
        public long                 SIO_control_csr;
        public long                 strobe_control_csr;
        public long[]               format7_csr = new long[DC1394_VIDEO_MODE_FORMAT7_NUM];
        public int /*dc1394iidc_version_t*/ iidc_version;
        public String               vendor;
        public String               model;
        public int                  vendor_id;
        public int                  model_id;
        public int /*dc1394bool_t*/ bmode_capable;
        public int /*dc1394bool_t*/ one_shot_capable;
        public int /*dc1394bool_t*/ multi_shot_capable;
        public int /*dc1394bool_t*/ can_switch_on_off;
        public int /*dc1394bool_t*/ has_vmode_error_status;
        public int /*dc1394bool_t*/ has_feature_error_status;
        public int                  max_mem_channel;

        public int                  flags;

        public static class ByReference extends dc1394camera_t implements Structure.ByReference { }
    }

    public static class dc1394camera_id_t extends Structure {
        public short         unit;
        public long          guid;

        public static class ByReference extends dc1394camera_id_t implements Structure.ByReference { }
    }

    public static class dc1394camera_list_t extends Structure {
        public dc1394camera_list_t() { }
        public dc1394camera_list_t(Pointer m) { super(m); read(); }

        public int           num;
        public dc1394camera_id_t.ByReference ids;

        public static class ByReference extends dc1394camera_list_t implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(dc1394camera_list_t p) {
                setStructure(p);
            }
            public dc1394camera_list_t getStructure() {
                dc1394camera_list_t l = new dc1394camera_list_t(getValue());
                l.setAutoWrite(false);
                return l;
            }
            public void getStructure(dc1394camera_list_t p) {
                p.useMemory(getValue());
                p.read();
                p.setAutoWrite(false);
            }
            public void setStructure(dc1394camera_list_t p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class dc1394_t extends PointerType { }

    public static native dc1394_t dc1394_new();
    public static native void dc1394_free(dc1394_t dc1394);
    public static native int /* dc1394error_t */ dc1394_camera_set_broadcast(dc1394camera_t camera,
            int /* dc1394bool_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_camera_get_broadcast(dc1394camera_t camera,
            IntByReference /* dc1394bool_t* */ pwr);
    public static native int /* dc1394error_t */ dc1394_reset_bus(dc1394camera_t camera);
    public static native int /* dc1394error_t */ dc1394_read_cycle_timer (dc1394camera_t camera,
            IntByReference cycle_timer, LongByReference local_time);
    public static native int /* dc1394error_t */ dc1394_camera_get_node(dc1394camera_t camera,
            IntByReference node, IntByReference generation);

    public static native int /* dc1394error_t */ dc1394_camera_enumerate(dc1394_t dc1394,
            dc1394camera_list_t.PointerByReference list);
    public static native void dc1394_camera_free_list(dc1394camera_list_t list);
    public static native dc1394camera_t dc1394_camera_new(dc1394_t dc1394, long guid);
    public static native dc1394camera_t dc1394_camera_new_unit(dc1394_t dc1394, long guid, int unit);
    public static native void dc1394_camera_free(dc1394camera_t camera);
    public static native int /* dc1394error_t */ dc1394_camera_print_info(dc1394camera_t camera,
            Pointer /*FILE* */ fd);


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

    public static class dc1394feature_modes_t extends Structure {
        public int   num;
        public int[] modes = new int/* dc1394feature_mode_t */[DC1394_FEATURE_MODE_NUM];
    }

    public static class dc1394trigger_modes_t extends Structure {
        public int   num;
        public int[] modes = new int/* dc1394trigger_mode_t */[DC1394_TRIGGER_MODE_NUM];
    }

    public static class dc1394trigger_sources_t extends Structure {
        public int   num;
        public int[] sources = new int/* dc1394trigger_source_t */[DC1394_TRIGGER_SOURCE_NUM];
    }

    public static class dc1394feature_info_t extends Structure {
        public int /* dc1394feature_t */   id;
        public int /* dc1394bool_t */      available;
        public int /* dc1394bool_t */      absolute_capable;
        public int /* dc1394bool_t */      readout_capable;
        public int /* dc1394bool_t */      on_off_capable;
        public int /* dc1394bool_t */      polarity_capable;
        public int /* dc1394bool_t */      is_on;
        public int /* dc1394feature_mode_t */     current_mode;
        public dc1394feature_modes_t              modes;
        public dc1394trigger_modes_t              trigger_modes;
        public int /* dc1394trigger_mode_t */     trigger_mode;
        public int /* dc1394trigger_polarity_t */ trigger_polarity;
        public dc1394trigger_sources_t            trigger_sources;
        public int /* dc1394trigger_source_t */   trigger_source;
        public int         min;
        public int         max;
        public int         value;
        public int         BU_value;
        public int         RV_value;
        public int         B_value;
        public int         R_value;
        public int         G_value;
        public int         target_value;

        public int /* dc1394switch_t */ abs_control;
        public float       abs_value;
        public float       abs_max;
        public float       abs_min;
    }

    public static class dc1394featureset_t extends Structure {
        public dc1394feature_info_t[] feature = new dc1394feature_info_t[DC1394_FEATURE_NUM];
    }

    public static native int /* dc1394error_t */ dc1394_feature_get_all(dc1394camera_t camera,
            dc1394featureset_t features);
    public static native int /* dc1394error_t */ dc1394_feature_get(dc1394camera_t camera,
            dc1394feature_info_t feature);
    public static native int /* dc1394error_t */ dc1394_feature_print(dc1394feature_info_t feature,
            Pointer /* FILE* */ fd);
    public static native int /* dc1394error_t */ dc1394_feature_print_all(dc1394featureset_t features,
            Pointer /* FILE* */ fd);
    public static native int /* dc1394error_t */ dc1394_feature_whitebalance_get_value(dc1394camera_t camera,
            IntByReference u_b_value, IntByReference v_r_value);
    public static native int /* dc1394error_t */ dc1394_feature_whitebalance_set_value(dc1394camera_t camera,
            int u_b_value, int v_r_value);
    public static native int /* dc1394error_t */ dc1394_feature_temperature_get_value(dc1394camera_t camera,
            IntByReference target_temperature, IntByReference temperature);
    public static native int /* dc1394error_t */ dc1394_feature_temperature_set_value(dc1394camera_t camera,
            int target_temperature);
    public static native int /* dc1394error_t */ dc1394_feature_whiteshading_get_value(dc1394camera_t camera,
            IntByReference r_value, IntByReference g_value, IntByReference b_value);
    public static native int /* dc1394error_t */ dc1394_feature_whiteshading_set_value(dc1394camera_t camera,
            int r_value, int g_value, int b_value);
    public static native int /* dc1394error_t */ dc1394_feature_get_value(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference value);
    public static native int /* dc1394error_t */ dc1394_feature_set_value(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, int value);
    public static native int /* dc1394error_t */ dc1394_feature_is_present(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394bool_t* */ value);
    public static native int /* dc1394error_t */ dc1394_feature_is_readable(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394bool_t* */ value);
    public static native int /* dc1394error_t */ dc1394_feature_get_boundaries(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference min, IntByReference max);
    public static native int /* dc1394error_t */ dc1394_feature_is_switchable(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394bool_t* */ value);
    public static native int /* dc1394error_t */ dc1394_feature_get_power(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394switch_t* */ pwr);
    public static native int /* dc1394error_t */ dc1394_feature_set_power(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_feature_get_modes(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, dc1394feature_modes_t modes);
    public static native int /* dc1394error_t */ dc1394_feature_get_mode(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394feature_mode_t* */ mode);
    public static native int /* dc1394error_t */ dc1394_feature_set_mode(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, int /* dc1394feature_mode_t */ mode);
    public static native int /* dc1394error_t */ dc1394_feature_has_absolute_control(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394bool_t* */ value);
    public static native int /* dc1394error_t */ dc1394_feature_get_absolute_boundaries(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, FloatByReference min, FloatByReference max);
    public static native int /* dc1394error_t */ dc1394_feature_get_absolute_value(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, FloatByReference value);
    public static native int /* dc1394error_t */ dc1394_feature_set_absolute_value(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, float value);
    public static native int /* dc1394error_t */ dc1394_feature_get_absolute_control(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, IntByReference /* dc1394switch_t* */ pwr);
    public static native int /* dc1394error_t */ dc1394_feature_set_absolute_control(dc1394camera_t camera,
            int /* dc1394feature_t */ feature, int /* dc1394switch_t */ pwr);

    public static native int /* dc1394error_t */ dc1394_external_trigger_set_polarity(dc1394camera_t camera,
            int /* dc1394trigger_polarity_t */ polarity);
    public static native int /* dc1394error_t */ dc1394_external_trigger_get_polarity(dc1394camera_t camera,
            IntByReference /* dc1394trigger_polarity_t* */ polarity);
    public static native int /* dc1394error_t */ dc1394_external_trigger_has_polarity(dc1394camera_t camera,
            IntByReference /* dc1394bool_t* */ polarity_capable);
    public static native int /* dc1394error_t */ dc1394_external_trigger_set_power(dc1394camera_t camera,
            int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_external_trigger_get_power(dc1394camera_t camera,
            IntByReference /* dc1394switch_t* */ pwr);
    public static native int /* dc1394error_t */ dc1394_external_trigger_set_mode(dc1394camera_t camera,
            int /* dc1394trigger_mode_t */ mode);
    public static native int /* dc1394error_t */ dc1394_external_trigger_get_mode(dc1394camera_t camera,
            IntByReference /* dc1394trigger_mode_t* */ mode);
    public static native int /* dc1394error_t */ dc1394_external_trigger_set_source(dc1394camera_t camera,
            int /* dc1394trigger_source_t */ source);
    public static native int /* dc1394error_t */ dc1394_external_trigger_get_source(dc1394camera_t camera,
            IntByReference /* dc1394trigger_source_t* */ source);
    public static native int /* dc1394error_t */ dc1394_external_trigger_get_supported_sources(dc1394camera_t camera,
            dc1394trigger_sources_t sources);
    public static native int /* dc1394error_t */ dc1394_software_trigger_set_power(dc1394camera_t camera,
            int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_software_trigger_get_power(dc1394camera_t camera,
            IntByReference /* dc1394switch_t* */ pwr);

    public static native int /* dc1394error_t */ dc1394_pio_set(dc1394camera_t camera, int value);
    public static native int /* dc1394error_t */ dc1394_pio_get(dc1394camera_t camera, IntByReference value);

    public static native int /* dc1394error_t */ dc1394_camera_reset(dc1394camera_t camera);
    public static native int /* dc1394error_t */ dc1394_camera_set_power(dc1394camera_t camera,
            int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_memory_busy(dc1394camera_t camera,
            IntByReference /* dc1394bool_t* */ value);
    public static native int /* dc1394error_t */ dc1394_memory_save(dc1394camera_t camera, int channel);
    public static native int /* dc1394error_t */ dc1394_memory_load(dc1394camera_t camera, int channel);


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

    public static native int /* dc1394error_t */ dc1394_capture_setup(dc1394camera_t camera,
            int num_dma_buffers, int flags);
    public static native int /* dc1394error_t */ dc1394_capture_stop(dc1394camera_t camera);
    public static native int dc1394_capture_get_fileno(dc1394camera_t camera);
    public static native int /* dc1394error_t */ dc1394_capture_dequeue(dc1394camera_t camera,
            int /* dc1394capture_policy_t*/ policy, dc1394video_frame_t.PointerByReference frame);
    public static native int /* dc1394error_t */ dc1394_capture_enqueue(dc1394camera_t camera,
            dc1394video_frame_t frame);
    public static native int /* dc1394bool_t */ dc1394_capture_is_frame_corrupt (dc1394camera_t camera,
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

    public static class dc1394framerates_t extends Structure {
        public int   num;
        public int[] framerates = new int/* dc1394framerate_t */[DC1394_FRAMERATE_NUM];
    }

    public static class dc1394video_frame_t extends Structure {
        public dc1394video_frame_t() { }
        public dc1394video_frame_t(Pointer m) { super(m); read(); }

        public Pointer           image;
        public int[]             size = new int[2];
        public int[]             position = new int[2];
        public int /* dc1394color_coding_t */ color_coding;
        public int /* dc1394color_filter_t */ color_filter;
        public int               yuv_byte_order;
        public int               data_depth;
        public int               stride;
        public int /* dc1394video_mode_t */   video_mode;
        public long              total_bytes;
        public int               image_bytes;
        public int               padding_bytes;
        public int               packet_size;
        public int               packets_per_frame;
        public long              timestamp;
        public int               frames_behind;
        public dc1394camera_t.ByReference     camera;
        public int               id;
        public long              allocated_image_bytes;
        public int /* dc1394bool_t */         little_endian;
        public int /* dc1394bool_t */         data_in_padding;

        public ByteBuffer getByteBuffer() { return image.getByteBuffer(0, total_bytes); }

        public static class ByReference extends dc1394video_frame_t implements Structure.ByReference { }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(dc1394video_frame_t p) {
                setStructure(p);
            }
            public dc1394video_frame_t getStructure() {
                dc1394video_frame_t f = new dc1394video_frame_t(getValue());
                f.setAutoWrite(false);
                return f;
            }
            public void getStructure(dc1394video_frame_t p) {
                p.useMemory(getValue());
                p.read();
                p.setAutoWrite(false);
            }
            public void setStructure(dc1394video_frame_t p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native int /* dc1394error_t */ dc1394_video_get_supported_modes(dc1394camera_t camera,
            dc1394video_modes_t video_modes);
    public static native int /* dc1394error_t */ dc1394_video_get_supported_framerates(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, dc1394framerates_t framerates);
    public static native int /* dc1394error_t */ dc1394_video_get_framerate(dc1394camera_t camera,
            IntByReference /* dc1394framerate_t* */ framerate);
    public static native int /* dc1394error_t */ dc1394_video_set_framerate(dc1394camera_t camera,
            int /* dc1394framerate_t */ framerate);
    public static native int /* dc1394error_t */ dc1394_video_get_mode(dc1394camera_t camera,
            IntByReference /* dc1394video_mode_t* */ video_mode);
    public static native int /* dc1394error_t */ dc1394_video_set_mode(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode);
    public static native int /* dc1394error_t */ dc1394_video_get_operation_mode(dc1394camera_t camera,
            IntByReference /* dc1394operation_mode_t* */ mode);
    public static native int /* dc1394error_t */ dc1394_video_set_operation_mode(dc1394camera_t camera,
            int /* dc1394operation_mode_t */ mode);
    public static native int /* dc1394error_t */ dc1394_video_get_iso_speed(dc1394camera_t camera,
            IntByReference /* dc1394speed_t* */ speed);
    public static native int /* dc1394error_t */ dc1394_video_set_iso_speed(dc1394camera_t camera,
            int /* dc1394speed_t */ speed);
    public static native int /* dc1394error_t */ dc1394_video_get_iso_channel(dc1394camera_t camera,
            IntByReference channel);
    public static native int /* dc1394error_t */ dc1394_video_set_iso_channel(dc1394camera_t camera,
            int channel);
    public static native int /* dc1394error_t */ dc1394_video_get_data_depth(dc1394camera_t camera,
            IntByReference depth);
    public static native int /* dc1394error_t */ dc1394_video_set_transmission(dc1394camera_t camera,
            int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_video_get_transmission(dc1394camera_t camera,
            IntByReference /* dc1394switch_t* */ pwr);
    public static native int /* dc1394error_t */ dc1394_video_set_one_shot(dc1394camera_t camera,
            int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_video_get_one_shot(dc1394camera_t camera,
            IntByReference /* dc1394bool_t* */ is_on);
    public static native int /* dc1394error_t */ dc1394_video_set_multi_shot(dc1394camera_t camera,
            int numFrames, int /* dc1394switch_t */ pwr);
    public static native int /* dc1394error_t */ dc1394_video_get_multi_shot(dc1394camera_t camera,
            IntByReference /* dc1394bool_t* */  is_on, IntByReference numFrames);
    public static native int /* dc1394error_t */ dc1394_video_get_bandwidth_usage(dc1394camera_t camera,
            IntByReference bandwidth);


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

    public static native int /* dc1394error_t */ dc1394_convert_to_YUV422(Pointer src, Pointer dest, int width,
            int height, int byte_order, int /* dc1394color_coding_t */ source_coding, int bits);
    public static native int /* dc1394error_t */ dc1394_convert_to_MONO8(Pointer src, Pointer dest, int width,
            int height, int byte_order, int /* dc1394color_coding_t */ source_coding, int bits);
    public static native int /* dc1394error_t */ dc1394_convert_to_RGB8(Pointer src, Pointer dest, int width,
            int height, int byte_order, int /* dc1394color_coding_t */ source_coding, int bits);

    public static native int /* dc1394error_t */ dc1394_deinterlace_stereo(
            Pointer src, Pointer dest, int width, int height);

    public static native int /* dc1394error_t */ dc1394_bayer_decoding_8bit(Pointer bayer, Pointer rgb, int width,
            int height, int /* dc1394color_filter_t */ tile, int /* dc1394bayer_method_t */ method);
    public static native int /* dc1394error_t */ dc1394_bayer_decoding_16bit(Pointer bayer, Pointer rgb, int width,
            int height, int /* dc1394color_filter_t */ tile, int /* dc1394bayer_method_t */ method, int bits);

    public static native int /* dc1394error_t */ dc1394_convert_frames(dc1394video_frame_t in,
            dc1394video_frame_t out);
    public static native int /* dc1394error_t */ dc1394_debayer_frames(dc1394video_frame_t in,
            dc1394video_frame_t out, int /* dc1394bayer_method_t */ method);
    public static native int /* dc1394error_t */ dc1394_deinterlace_stereo_frames(dc1394video_frame_t in,
            dc1394video_frame_t out, int /* dc1394stereo_method_t */ method);


    public static class dc1394format7mode_t extends Structure {
        public int /* dc1394bool_t */ present;

        public int size_x;
        public int size_y;
        public int max_size_x;
        public int max_size_y;

        public int pos_x;
        public int pos_y;

        public int unit_size_x;
        public int unit_size_y;
        public int unit_pos_x;
        public int unit_pos_y;

        dc1394color_codings_t color_codings;
        public int /* dc1394color_coding_t */ color_coding;

        public int pixnum;

        public int packet_size;
        public int unit_packet_size;
        public int max_packet_size;

        public long total_bytes;

        public int /* dc1394color_filter_t */ color_filter;
    }

    public static class dc1394format7modeset_t extends Structure {
        dc1394format7mode_t[] mode = new dc1394format7mode_t[DC1394_VIDEO_MODE_FORMAT7_NUM];
    }

    // Parameter flags for dc1394_setup_format7_capture() ??
    public static final int
            DC1394_QUERY_FROM_CAMERA = -1,
            DC1394_USE_MAX_AVAIL     = -2,
            DC1394_USE_RECOMMENDED   = -3;

    public static native int /* dc1394error_t */ dc1394_format7_get_max_image_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference h_size, IntByReference v_size);
    public static native int /* dc1394error_t */ dc1394_format7_get_unit_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference h_unit, IntByReference v_unit);
    public static native int /* dc1394error_t */ dc1394_format7_get_image_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference width, IntByReference height);
    public static native int /* dc1394error_t */ dc1394_format7_set_image_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, int width, int height);

    public static native int /* dc1394error_t */ dc1394_format7_get_image_position(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference left, IntByReference top);
    public static native int /* dc1394error_t */ dc1394_format7_set_image_position(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, int left, int top);
    public static native int /* dc1394error_t */ dc1394_format7_get_unit_position(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference h_unit_pos, 
            IntByReference v_unit_pos);

    public static native int /* dc1394error_t */ dc1394_format7_get_color_coding(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode,
            IntByReference /*dc1394color_coding_t* */ color_coding);
    public static native int /* dc1394error_t */ dc1394_format7_get_color_codings(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode,
            IntByReference /*dc1394color_coding_t* */ codings);
    public static native int /* dc1394error_t */ dc1394_format7_set_color_coding(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode,
            int /* dc1394color_coding_t */ color_coding);
    public static native int /* dc1394error_t */ dc1394_format7_get_color_filter(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode,
            IntByReference /* dc1394color_filter_t* */ color_filter);

    public static native int /* dc1394error_t */ dc1394_format7_get_packet_parameters(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference unit_bytes,
            IntByReference max_bytes);
    public static native int /* dc1394error_t */ dc1394_format7_get_packet_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference packet_size);
    public static native int /* dc1394error_t */ dc1394_format7_set_packet_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, int packet_size);
    public static native int /* dc1394error_t */ dc1394_format7_get_recommended_packet_size(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference packet_size);
    public static native int /* dc1394error_t */ dc1394_format7_get_packets_per_frame(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference ppf);

    public static native int /* dc1394error_t */ dc1394_format7_get_data_depth(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference data_depth);
    public static native int /* dc1394error_t */ dc1394_format7_get_frame_interval(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, FloatByReference interval);
    public static native int /* dc1394error_t */ dc1394_format7_get_pixel_number(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference pixnum);
    public static native int /* dc1394error_t */ dc1394_format7_get_total_bytes(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, LongByReference total_bytes);

    public static native int /* dc1394error_t */ dc1394_format7_get_modeset(dc1394camera_t camera,
            dc1394format7modeset_t info);
    public static native int /* dc1394error_t */ dc1394_format7_get_mode_info(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, dc1394format7mode_t f7_mode);
    public static native int /* dc1394error_t */ dc1394_format7_set_roi(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode,
            int /* dc1394color_coding_t */ color_coding, int packet_size,
            int left, int top, int width, int height);
    public static native int /* dc1394error_t */ dc1394_format7_get_roi(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, 
            IntByReference /*dc1394color_coding_t* */ color_coding, IntByReference packet_size,
            IntByReference left, IntByReference top, IntByReference width, IntByReference height);


    public static native int /* dc1394error_t */ dc1394_iso_set_persist(dc1394camera_t camera);
    public static native int /* dc1394error_t */ dc1394_iso_allocate_channel(dc1394camera_t camera,
        long channels_allowed, IntByReference channel);
    public static native int /* dc1394error_t */ dc1394_iso_release_channel(dc1394camera_t camera, int channel);
    public static native int /* dc1394error_t */ dc1394_iso_allocate_bandwidth(dc1394camera_t camera, int bandwidth_units);
    public static native int /* dc1394error_t */ dc1394_iso_release_bandwidth(dc1394camera_t camera, int bandwidth_units);
    public static native int /* dc1394error_t */ dc1394_iso_release_all(dc1394camera_t camera);


    public static native int /* dc1394error_t */  dc1394_get_registers (dc1394camera_t camera,
            long offset, int[] value, int num_regs/*=1*/);
    public static native int /* dc1394error_t */  dc1394_set_registers (dc1394camera_t camera,
            long offset, int[] value, int num_regs/*=1*/);

    public static native int /* dc1394error_t */  dc1394_get_control_registers (dc1394camera_t camera,
            long offset, int[] value, int num_regs/*=1*/);
    public static native int /* dc1394error_t */  dc1394_set_control_registers (dc1394camera_t camera,
            long offset, int[] value, int num_regs/*=1*/);

    public static native int /* dc1394error_t */ dc1394_get_adv_control_registers(dc1394camera_t camera,
            long offset, int[] value, int num_regs/*=1*/);
    public static native int /* dc1394error_t */ dc1394_set_adv_control_registers(dc1394camera_t camera,
            long offset, int[] value, int num_regs/*=1*/);

    public static native int /* dc1394error_t */ dc1394_get_format7_register(dc1394camera_t camera,
            int mode, long offset, IntByReference value);
    public static native int /* dc1394error_t */ dc1394_set_format7_register(dc1394camera_t camera,
            int mode, long offset, int value);

    public static native int /* dc1394error_t */ dc1394_get_absolute_register(dc1394camera_t camera,
            int feature, long offset, IntByReference value);
    public static native int /* dc1394error_t */ dc1394_set_absolute_register(dc1394camera_t camera,
            int feature, long offset, int value);

    public static native int /* dc1394error_t */ dc1394_get_PIO_register(dc1394camera_t camera,
            long offset, IntByReference value);
    public static native int /* dc1394error_t */ dc1394_set_PIO_register(dc1394camera_t camera,
            long offset, int value);

    public static native int /* dc1394error_t */ dc1394_get_SIO_register(dc1394camera_t camera,
            long offset, IntByReference value);
    public static native int /* dc1394error_t */ dc1394_set_SIO_register(dc1394camera_t camera,
            long offset, int value);

    public static native int /* dc1394error_t */ dc1394_get_strobe_register(dc1394camera_t camera,
            long offset, IntByReference value);
    public static native int /* dc1394error_t */ dc1394_set_strobe_register(dc1394camera_t camera,
            long offset, int value);


    public static native int /* dc1394error_t */ dc1394_get_image_size_from_video_mode(dc1394camera_t camera,
            int video_mode, IntByReference width, IntByReference height);
    public static native int /* dc1394error_t */ dc1394_framerate_as_float(
            int /* dc1394framerate_t */ framerate_enum, FloatByReference framerate);
    public static native int /* dc1394error_t */ dc1394_get_color_coding_data_depth(
            int /* dc1394color_coding_t */ color_coding, IntByReference bits);
    public static native int /* dc1394error_t */ dc1394_get_color_coding_bit_size(
            int /* dc1394color_coding_t */ color_coding, IntByReference bits);
    public static native int /* dc1394error_t */ dc1394_get_color_coding_from_video_mode(dc1394camera_t camera,
            int /* dc1394video_mode_t */ video_mode, IntByReference /* dc1394color_coding_t* */ color_coding);
    public static native int /* dc1394error_t */ dc1394_is_color(int /* dc1394color_coding_t */ color_mode,
            IntByReference /* dc1394bool_t* */ is_color);
    public static native int /* dc1394bool_t */ dc1394_is_video_mode_scalable(int /* dc1394video_mode_t */ video_mode);
    public static native int /* dc1394bool_t */ dc1394_is_video_mode_still_image(int /* dc1394video_mode_t */ video_mode);
    public static native int /* dc1394bool_t */ dc1394_is_same_camera(dc1394camera_id_t id1, dc1394camera_id_t id2);
    public static native String dc1394_feature_get_string(int /* dc1394feature_t */ feature);
    public static native String dc1394_error_get_string(int /* dc1394error_t */ error);
    public static native short dc1394_checksum_crc16(byte[] buffer, int buffer_size);
    public static native short dc1394_checksum_crc16(ByteBuffer buffer, int buffer_size);
}
