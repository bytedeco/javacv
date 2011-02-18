/*
 * Copyright (C) 2010,2011 Samuel Audet
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
 * This file was derived from avutil.h and other libavutil include files from
 * FFmpeg 0.6.1, which are covered by the following copyright notice:
 *
 * copyright (c) 2006 Michael Niedermayer <michaelni@gmx.at>
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.googlecode.javacv.cpp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude="avutil.h",
        includepath=genericIncludepath, linkpath=genericLinkpath, link="avutil"),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload="avutil-50"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avutil {
    static { load(); }
    public static final String genericIncludepath = "/opt/local/include/ffmpeg/:/usr/local/include/ffmpeg/:/opt/local/include/:/usr/include/ffmpeg/";
    public static final String genericLinkpath    = "/opt/local/lib/:/opt/local/lib64/:/usr/local/lib/:/usr/local/lib64/";
    public static final String windowsIncludepath = "C:/MinGW/local/include/ffmpeg/;C:/MinGW/include/ffmpeg/;C:/MinGW/local/include/";
    public static final String windowsLinkpath    = "C:/MinGW/local/lib/;C:/MinGW/lib/";
    public static final String windowsPreloadpath = "C:/MinGW/local/bin/;C:/MinGW/bin/";
    public static final String androidIncludepath = "../android/include/";
    public static final String androidLinkpath    = "../android/lib/";

    //#define AV_STRINGIFY(s)         AV_TOSTRING(s)
    //#define AV_TOSTRING(s) #s
    //
    //#define AV_GLUE(a, b) a ## b
    //#define AV_JOIN(a, b) AV_GLUE(a, b)
    //
    //#define AV_PRAGMA(s) _Pragma(#s)

    public static int    AV_VERSION_INT(int a, int b, int c) { return (a<<16 | b<<8 | c); }
    public static String AV_VERSION_DOT(int a, int b, int c) { return a + "." + b + "." + c; }
    public static String AV_VERSION(int a, int b, int c)     { return AV_VERSION_DOT(a, b, c); }

    public static final int LIBAVUTIL_VERSION_MAJOR = 50;
    public static final int LIBAVUTIL_VERSION_MINOR = 15;
    public static final int LIBAVUTIL_VERSION_MICRO =  1;

    public static final int    LIBAVUTIL_VERSION_INT = AV_VERSION_INT(LIBAVUTIL_VERSION_MAJOR,
                                                                      LIBAVUTIL_VERSION_MINOR,
                                                                      LIBAVUTIL_VERSION_MICRO);
    public static final String LIBAVUTIL_VERSION     = AV_VERSION(LIBAVUTIL_VERSION_MAJOR,
                                                                  LIBAVUTIL_VERSION_MINOR,
                                                                  LIBAVUTIL_VERSION_MICRO);
    public static final int    LIBAVUTIL_BUILD       = LIBAVUTIL_VERSION_INT;

    public static final String LIBAVUTIL_IDENT       = "Lavu" + LIBAVUTIL_VERSION;


    public static native int avutil_version();
    public static native String avutil_configuration();
    public static native String avutil_license();

    //enum AVMediaType {
    public static final int
            AVMEDIA_TYPE_UNKNOWN    = -1,
            AVMEDIA_TYPE_VIDEO      = 0,
            AVMEDIA_TYPE_AUDIO      = 1,
            AVMEDIA_TYPE_DATA       = 2,
            AVMEDIA_TYPE_SUBTITLE   = 3,
            AVMEDIA_TYPE_ATTACHMENT = 4,
            AVMEDIA_TYPE_NB         = 5;

    //#include "common.h"
    // ...

    //#include "mem.h"
    public static native Pointer av_malloc(int size);
    public static native Pointer av_realloc(Pointer ptr, int size);
    public static native void av_free(Pointer ptr);
    public static native Pointer av_mallocz(int size);
    public static native String av_strdup(String s);
    public static native void av_freep(Pointer ptr);

    //#include "error.h"
    public static native int av_strerror(int errnum, @Cast("char*") byte[] errbuf, int errbuf_size);

    //#include "mathematics.h"
    public static final double
            M_E           = 2.7182818284590452354,
            M_LN2         = 0.69314718055994530942,
            M_LN10        = 2.30258509299404568402,
            M_LOG2_10     = 3.32192809488736234787,
            M_PI          = 3.14159265358979323846,
            M_SQRT1_2     = 0.70710678118654752440,
            M_SQRT2       = 1.41421356237309504880,
            NAN           = (0.0/0.0),
            INFINITY      = (1.0/0.0);

    //enum AVRounding {
    public static final int
            AV_ROUND_ZERO     = 0,
            AV_ROUND_INF      = 1,
            AV_ROUND_DOWN     = 2,
            AV_ROUND_UP       = 3,
            AV_ROUND_NEAR_INF = 5;

    public static native long av_gcd(long a, long b);
    public static native long av_rescale(long a, long b, long c);
    public static native long av_rescale_rnd(long a, long b, long c, @Cast("AVRounding") int r);
    public static native long av_rescale_q(long a, @ByVal  AVRational bq, @ByVal AVRational cq);
    public static native int av_compare_ts(long ts_a, @ByVal AVRational tb_a, long ts_b, @ByVal AVRational tb_b);

    //#include "rational.h"
    public static class AVRational extends Pointer {
        public AVRational() { allocate(); }
        public AVRational(int size) { allocateArray(size); }
        public AVRational(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVRational position(int position) {
            return (AVRational)super.position(position);
        }

        public native int num(); public native AVRational num(int num);
        public native int den(); public native AVRational den(int den);
    }

    public static long av_cmp_q(AVRational a, AVRational b){
        long tmp= a.num() * (long)b.den() - b.num() * (long)a.den();

        if(tmp != 0) return (tmp>>63)|1;
        else         return 0;
    }
    public static double av_q2d(AVRational a){
        return a.num() / (double) a.den();
    }
    public static native int av_reduce(int[] dst_num, int[] dst_den, long num, long den, long max);
    public static native @ByVal AVRational av_mul_q(@ByVal AVRational b, @ByVal AVRational c);
    public static native @ByVal AVRational av_div_q(@ByVal AVRational b, @ByVal AVRational c);
    public static native @ByVal AVRational av_add_q(@ByVal AVRational b, @ByVal AVRational c);
    public static native @ByVal AVRational av_sub_q(@ByVal AVRational b, @ByVal AVRational c);
    public static native @ByVal AVRational av_d2q(double d, int max);
    public static native int av_nearer_q(@ByVal AVRational q, @ByVal AVRational q1, @ByVal AVRational q2);
    public static native int av_find_nearest_q_idx(@ByVal AVRational q, AVRational q_list);
    public static int av_find_nearest_q_idx(@ByVal AVRational q, AVRational[] q_list) {
        return av_find_nearest_q_idx(q, q_list[0]);
    }

    //#include "intfloat_readwrite.h"
    public static class AVExtFloat extends Pointer {
        public AVExtFloat() { allocate(); }
        public AVExtFloat(int size) { allocateArray(size); }
        public AVExtFloat(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVExtFloat position(int position) {
            return (AVExtFloat)super.position(position);
        }

        public native byte/*[2]*/ exponent(int i); public native AVExtFloat exponent(int i, byte exponent);
        public native byte/*[8]*/ mantissa(int i); public native AVExtFloat mantissa(int i, byte mantissa);
    }

    public static native double av_int2dbl(long v);
    public static native float av_int2flt(int v);
    public static native double av_ext2dbl(@ByVal AVExtFloat ext);
    public static native long av_dbl2int(double d);
    public static native int av_flt2int(float d);
    public static native @ByVal AVExtFloat av_dbl2ext(double d);

    //#include "log.h"
    @Opaque public static class AVOption extends Pointer {
        public AVOption() { }
        public AVOption(Pointer p) { super(p); }
    }
    public static class AVClass extends Pointer {
        public AVClass() { allocate(); }
        public AVClass(int size) { allocateArray(size); }
        public AVClass(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVClass position(int position) {
            return (AVClass)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer class_name();        public native AVClass class_name(BytePointer class_name);
        public static class Item_name extends FunctionPointer {
            public native @Cast("const char*") BytePointer call(Pointer ctx);
        }
        public native Item_name item_name();           public native AVClass item_name(Item_name item_name);
        @Cast("const AVOption*")
        public native AVOption option();               public native AVClass option(AVOption option);
//        public native int version();                   public native AVClass version(int version);
    }

    public static final int
            AV_LOG_QUIET   = -8,
            AV_LOG_PANIC   =  0,
            AV_LOG_FATAL   =  8,
            AV_LOG_ERROR   = 16,
            AV_LOG_WARNING = 24,
            AV_LOG_INFO    = 32,
            AV_LOG_VERBOSE = 40,
            AV_LOG_DEBUG   = 48;

    public static native void av_log(Pointer avcl, int level, String fmt);
    public static native void av_vlog(Pointer avcl, int level, String fmt,
            @ByVal @Cast("va_list*") Pointer vl);
    public static native int av_log_get_level();
    public static native void av_log_set_level(int l);
    public static class LogCallback extends FunctionPointer {
        public    LogCallback(Pointer p) { super(p); }
        protected LogCallback() { allocate(); }
        protected final native void allocate();
        public native void call(Pointer ptr, int i, String fmt,
                @ByVal @Cast("va_list*") Pointer vl);
    }
    public static native void av_log_set_callback(LogCallback c);
    public static native void av_log_default_callback(Pointer ptr, int level,
            String fmt, @ByVal @Cast("va_list*") Pointer vl);

    //#include "libavutil/avconfig.h"
    public static boolean AV_HAVE_BIGENDIAN() { return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN; }

    //#include "pixfmt.h"
    //enum PixelFormat {
    public static final int
            PIX_FMT_NONE        = -1,
            PIX_FMT_YUV420P     = 0,
            PIX_FMT_YUYV422     = 1,
            PIX_FMT_RGB24       = 2,
            PIX_FMT_BGR24       = 3,
            PIX_FMT_YUV422P     = 4,
            PIX_FMT_YUV444P     = 5,
            PIX_FMT_YUV410P     = 6,
            PIX_FMT_YUV411P     = 7,
            PIX_FMT_GRAY8       = 8,
            PIX_FMT_MONOWHITE   = 9,
            PIX_FMT_MONOBLACK   = 10,
            PIX_FMT_PAL8        = 11,
            PIX_FMT_YUVJ420P    = 12,
            PIX_FMT_YUVJ422P    = 13,
            PIX_FMT_YUVJ444P    = 14,
            PIX_FMT_XVMC_MPEG2_MC = 15,
            PIX_FMT_XVMC_MPEG2_IDCT = 16,
            PIX_FMT_UYVY422     = 17,
            PIX_FMT_UYYVYY411   = 18,
            PIX_FMT_BGR8        = 19,
            PIX_FMT_BGR4        = 20,
            PIX_FMT_BGR4_BYTE   = 21,
            PIX_FMT_RGB8        = 22,
            PIX_FMT_RGB4        = 23,
            PIX_FMT_RGB4_BYTE   = 24,
            PIX_FMT_NV12        = 25,
            PIX_FMT_NV21        = 26,

            PIX_FMT_ARGB        = 27,
            PIX_FMT_RGBA        = 28,
            PIX_FMT_ABGR        = 29,
            PIX_FMT_BGRA        = 30,

            PIX_FMT_GRAY16BE    = 31,
            PIX_FMT_GRAY16LE    = 32,
            PIX_FMT_YUV440P     = 33,
            PIX_FMT_YUVJ440P    = 34,
            PIX_FMT_YUVA420P    = 35,
            PIX_FMT_VDPAU_H264  = 36,
            PIX_FMT_VDPAU_MPEG1 = 37,
            PIX_FMT_VDPAU_MPEG2 = 38,
            PIX_FMT_VDPAU_WMV3  = 39,
            PIX_FMT_VDPAU_VC1   = 40,
            PIX_FMT_RGB48BE     = 41,
            PIX_FMT_RGB48LE     = 42,

            PIX_FMT_RGB565BE    = 43,
            PIX_FMT_RGB565LE    = 44,
            PIX_FMT_RGB555BE    = 45,
            PIX_FMT_RGB555LE    = 46,

            PIX_FMT_BGR565BE    = 47,
            PIX_FMT_BGR565LE    = 48,
            PIX_FMT_BGR555BE    = 49,
            PIX_FMT_BGR555LE    = 50,

            PIX_FMT_VAAPI_MOCO  = 51,
            PIX_FMT_VAAPI_IDCT  = 52,
            PIX_FMT_VAAPI_VLD   = 53,

            PIX_FMT_YUV420P16LE = 54,
            PIX_FMT_YUV420P16BE = 55,
            PIX_FMT_YUV422P16LE = 56,
            PIX_FMT_YUV422P16BE = 57,
            PIX_FMT_YUV444P16LE = 58,
            PIX_FMT_YUV444P16BE = 59,
            PIX_FMT_VDPAU_MPEG4 = 60,
            PIX_FMT_DXVA2_VLD   = 61,

            PIX_FMT_RGB444BE    = 62,
            PIX_FMT_RGB444LE    = 63,
            PIX_FMT_BGR444BE    = 64,
            PIX_FMT_BGR444LE    = 65,
            PIX_FMT_Y400A       = 66,
            PIX_FMT_NB          = 67,

            PIX_FMT_RGB32   = AV_HAVE_BIGENDIAN() ? PIX_FMT_ARGB : PIX_FMT_BGRA,
            PIX_FMT_RGB32_1 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGBA : PIX_FMT_ABGR,
            PIX_FMT_BGR32   = AV_HAVE_BIGENDIAN() ? PIX_FMT_ABGR : PIX_FMT_RGBA,
            PIX_FMT_BGR32_1 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGRA : PIX_FMT_ARGB,

            PIX_FMT_GRAY16 = AV_HAVE_BIGENDIAN() ? PIX_FMT_GRAY16BE : PIX_FMT_GRAY16LE,
            PIX_FMT_RGB48  = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB48BE : PIX_FMT_RGB48LE,
            PIX_FMT_RGB565 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB565BE : PIX_FMT_RGB565LE,
            PIX_FMT_RGB555 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB555BE : PIX_FMT_RGB555LE,
            PIX_FMT_RGB444 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB444BE : PIX_FMT_RGB444LE,
            PIX_FMT_BGR565 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR565BE : PIX_FMT_BGR565LE,
            PIX_FMT_BGR555 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR555BE : PIX_FMT_BGR555LE,
            PIX_FMT_BGR444 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR444BE : PIX_FMT_BGR444LE,

            PIX_FMT_YUV420P16 = AV_HAVE_BIGENDIAN() ? PIX_FMT_YUV420P16BE : PIX_FMT_YUV420P16LE,
            PIX_FMT_YUV422P16 = AV_HAVE_BIGENDIAN() ? PIX_FMT_YUV422P16BE : PIX_FMT_YUV422P16LE,
            PIX_FMT_YUV444P16 = AV_HAVE_BIGENDIAN() ? PIX_FMT_YUV444P16BE : PIX_FMT_YUV444P16LE;

    //#include "adler32.h"
    public static native long av_adler32_update(long adler, @Cast("uint8_t*") BytePointer buf, int len);

    //#include "avstring.h"
    public static native int av_strstart(String str, String pfx, @Cast("const char**") PointerPointer ptr);
    public static native int av_stristart(String str, String pfx, @Cast("const char**") PointerPointer ptr);
    public static native String av_stristr(String haystack, String needle);
    public static native int av_strlcpy(@Cast("char*") byte[] dst, String src, int size);
    public static native int av_strlcat(@Cast("char*") byte[] dst, String src, int size);
    public static native int av_strlcatf(@Cast("char*") byte[] dst, int size, String fmt);
    public static native String av_d2str(double d);

    //#include "base64.h"
    public static native int av_base64_decode(@Cast("uint8_t*") byte[]      out,
            @Cast("char*") byte[]      in, int out_size);
    public static native int av_base64_decode(@Cast("uint8_t*") ByteBuffer  out,
            @Cast("char*") ByteBuffer  in, int out_size);
    public static native int av_base64_decode(@Cast("uint8_t*") BytePointer out,
            @Cast("char*") BytePointer in, int out_size);
    public static native String av_base64_encode(@Cast("char*") byte[]      out,
            int out_size, @Cast("uint8_t*") byte[]      in, int in_size);
    public static native String av_base64_encode(@Cast("char*") ByteBuffer  out,
            int out_size, @Cast("uint8_t*") ByteBuffer  in, int in_size);
    public static native String av_base64_encode(@Cast("char*") BytePointer out,
            int out_size, @Cast("uint8_t*") BytePointer in, int in_size);

    //#include "crc.h"
    //typedef uint32_t AVCRC;

    //enum AVCRCId {
    public static final int
            AV_CRC_8_ATM        = 0,
            AV_CRC_16_ANSI      = 1,
            AV_CRC_16_CCITT     = 2,
            AV_CRC_32_IEEE      = 3,
            AV_CRC_32_IEEE_LE   = 4,
            AV_CRC_MAX          = 5;

    public static native int av_crc_init(@Cast("AVCRC*") int[] ctx,
            int le, int bits, int poly, int ctx_size);
    public static native @Cast("const AVCRC*") IntPointer av_crc_get_table(
            @Cast("AVCRCId") int crc_id);
    public static native int av_crc(@Cast("AVCRC*") int[] ctx, int start_crc,
            @Cast("uint8_t*") BytePointer buffer, int length);

    //#include "fifo.h"
    public static class AVFifoBuffer extends Pointer {
        public AVFifoBuffer() { allocate(); }
        public AVFifoBuffer(int size) { allocateArray(size); }
        public AVFifoBuffer(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFifoBuffer position(int position) {
            return (AVFifoBuffer)super.position(position);
        }

        @Cast("uint8_t*")
        public native BytePointer buffer(); public native AVFifoBuffer buffer(BytePointer buffer);
        @Cast("uint8_t*")
        public native BytePointer rptr();   public native AVFifoBuffer rptr(BytePointer rptr);
        @Cast("uint8_t*")
        public native BytePointer wptr();   public native AVFifoBuffer wptr(BytePointer wptr);
        @Cast("uint8_t*")
        public native BytePointer end();    public native AVFifoBuffer end(BytePointer end);
        public native int rndx();           public native AVFifoBuffer rndx(int rndx);
        public native int wndx();           public native AVFifoBuffer wndx(int wndx);
    }

    public static native AVFifoBuffer av_fifo_alloc(int size);
    public static native void av_fifo_free(AVFifoBuffer f);
    public static native void av_fifo_reset(AVFifoBuffer f);
    public static native int av_fifo_size(AVFifoBuffer f);
    public static native int av_fifo_space(AVFifoBuffer f);
    public static class Func_read extends FunctionPointer {
        public    Func_read(Pointer p) { super(p); }
        protected Func_read() { allocate(); }
        protected final native void allocate();
        public native void call(Pointer p1, Pointer p2, int i);
    }
    public static native int av_fifo_generic_read(AVFifoBuffer f, Pointer dest, int buf_size, Func_read func);
    public static class Func_write extends FunctionPointer {
        public    Func_write(Pointer p) { super(p); }
        protected Func_write() { allocate(); }
        protected final native void allocate();
        public native int call(Pointer p1, Pointer p2, int i);
    }
    public static native int av_fifo_generic_write(AVFifoBuffer f, Pointer src, int size, Func_write func);
    public static native int av_fifo_realloc2(AVFifoBuffer f, int size);
    public static native void av_fifo_drain(AVFifoBuffer f, int size);
    public static native byte av_fifo_peek(AVFifoBuffer f, int offs);

    //#include "lzo.h"
    public static final int
            AV_LZO_INPUT_DEPLETED = 1,
            AV_LZO_OUTPUT_FULL = 2,
            AV_LZO_INVALID_BACKPTR = 4,
            AV_LZO_ERROR = 8,

            AV_LZO_INPUT_PADDING = 8,
            AV_LZO_OUTPUT_PADDING = 12;

    public static native int av_lzo1x_decode(Pointer out, int[] outlen, Pointer in, int[] inlen);
    public static native void av_memcpy_backptr(@Cast("uint8_t*") BytePointer dst, int back, int cnt);

    //#include "md5.h"
    @MemberGetter public static native int av_md5_size();

    @Opaque public static class AVMD5 extends BytePointer {
        public AVMD5() { super(av_md5_size()); }
        public AVMD5(Pointer p) { super(p); }

        @Override public AVMD5 position(int position) {
            return (AVMD5)super.position(position);
        }
    }

    public static native void av_md5_init  (AVMD5 ctx);
    public static native void av_md5_update(AVMD5 ctx, @Cast("uint8_t*") byte[]      src, int len);
    public static native void av_md5_update(AVMD5 ctx, @Cast("uint8_t*") ByteBuffer  src, int len);
    public static native void av_md5_update(AVMD5 ctx, @Cast("uint8_t*") BytePointer src, int len);
    public static native void av_md5_final (AVMD5 ctx, @Cast("uint8_t*") byte[]      dst);
    public static native void av_md5_final (AVMD5 ctx, @Cast("uint8_t*") ByteBuffer  dst);
    public static native void av_md5_final (AVMD5 ctx, @Cast("uint8_t*") BytePointer dst);
    public static native void av_md5_sum(@Cast("uint8_t*") byte[]      dst,
            @Cast("uint8_t*") byte[]      src, int len);
    public static native void av_md5_sum(@Cast("uint8_t*") ByteBuffer  dst,
            @Cast("uint8_t*") ByteBuffer  src, int len);
    public static native void av_md5_sum(@Cast("uint8_t*") BytePointer dst,
            @Cast("uint8_t*") BytePointer src, int len);

    //#include "pixdesc.h"
    @NoOffset public static class AVComponentDescriptor extends Pointer {
        public AVComponentDescriptor() { allocate(); }
        public AVComponentDescriptor(int size) { allocateArray(size); }
        public AVComponentDescriptor(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVComponentDescriptor position(int position) {
            return (AVComponentDescriptor)super.position(position);
        }

        public native short plane();        public native AVComponentDescriptor plane       (short plane);       //:2
        public native short step_minus1();  public native AVComponentDescriptor step_minus1 (short step_minus1); //:3
        public native short offset_plus1(); public native AVComponentDescriptor offset_plus1(short offset_plus1);//:3
        public native short shift();        public native AVComponentDescriptor shift       (short shift);       //:3
        public native short depth_minus1(); public native AVComponentDescriptor depth_minus1(short depth_minus1);//:4
    }

    public static class AVPixFmtDescriptor extends Pointer {
        public AVPixFmtDescriptor() { allocate(); }
        public AVPixFmtDescriptor(int size) { allocateArray(size); }
        public AVPixFmtDescriptor(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVPixFmtDescriptor position(int position) {
            return (AVPixFmtDescriptor)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer name();   public native AVPixFmtDescriptor name(BytePointer name);
        public native byte nb_components(); public native AVPixFmtDescriptor nb_components(byte nb_components);

        public native byte log2_chroma_w(); public native AVPixFmtDescriptor log2_chroma_w(byte log2_chroma_w);

        public native byte log2_chroma_h(); public native AVPixFmtDescriptor log2_chroma_h(byte log2_chroma_h);
        public native byte flags();         public native AVPixFmtDescriptor flags(byte flags);

        @ByVal public native AVComponentDescriptor comp(int i);
               public native AVPixFmtDescriptor    comp(int i, AVComponentDescriptor comp);
    }

    public static final int
            PIX_FMT_BE        = 1,
            PIX_FMT_PAL       = 2,
            PIX_FMT_BITSTREAM = 4,
            PIX_FMT_HWACCEL   = 8;

    @MemberGetter public static native @Cast("const AVPixFmtDescriptor*") AVPixFmtDescriptor av_pix_fmt_descriptors();

//    public static native void read_line(@Cast("uint16_t*") short[]      dst, @Cast("const uint8_t**") PointerPointer data,
//            int linesize[/*4*/], AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component);
//    public static native void read_line(@Cast("uint16_t*") ShortBuffer  dst, @Cast("const uint8_t**") PointerPointer data,
//            int linesize[/*4*/], AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component);
//    public static native void read_line(@Cast("uint16_t*") ShortPointer dst, @Cast("const uint8_t**") PointerPointer data,
//            int linesize[/*4*/], AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component);
//    public static native void write_line(@Cast("uint16_t*") short[]      src, @Cast("uint8_t**") PointerPointer data,
//            int linesize[/*4*/], AVPixFmtDescriptor desc, int x, int y, int c, int w);
//    public static native void write_line(@Cast("uint16_t*") ShortBuffer  src, @Cast("uint8_t**") PointerPointer data,
//            int linesize[/*4*/], AVPixFmtDescriptor desc, int x, int y, int c, int w);
//    public static native void write_line(@Cast("uint16_t*") ShortPointer src, @Cast("uint8_t**") PointerPointer data,
//            int linesize[/*4*/], AVPixFmtDescriptor desc, int x, int y, int c, int w);

    public static native @Cast("PixelFormat") int av_get_pix_fmt(String name);
    public static native int av_get_bits_per_pixel(AVPixFmtDescriptor pixdesc);


    //#include "sha1.h"
    @MemberGetter public static native int av_sha1_size();

    @Opaque public static class AVSHA1 extends BytePointer {
        public AVSHA1() { super(av_sha1_size()); }
        public AVSHA1(Pointer p) { super(p); }

        @Override public AVSHA1 position(int position) {
            return (AVSHA1)super.position(position);
        }
    }

    public static native void av_sha1_init  (AVSHA1 context);
    public static native void av_sha1_update(AVSHA1 context, @Cast("uint8_t*") byte[]      data, int len);
    public static native void av_sha1_update(AVSHA1 context, @Cast("uint8_t*") ByteBuffer  data, int len);
    public static native void av_sha1_update(AVSHA1 context, @Cast("uint8_t*") BytePointer data, int len);
    public static native void av_sha1_final (AVSHA1 context, @Cast("uint8_t*") byte digest[/*20*/]);
}
