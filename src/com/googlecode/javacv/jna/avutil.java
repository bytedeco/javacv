/*
 * Copyright (C) 2010 Samuel Audet
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

package com.googlecode.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 *
 * @author Samuel Audet
 */
public class avutil {
    public static final String[] paths = { "/usr/local/lib/", "/usr/local/lib64/" };
    public static final String[] libnames = { "avutil", "avutil-50" };
    public static final String libname = Loader.load(paths, libnames);
    public static final NativeLibrary nativeLibrary = NativeLibrary.getInstance(libname);

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

    public static int LIBAVUTIL_VERSION_MAJOR = 50;
    public static int LIBAVUTIL_VERSION_MINOR = 15;
    public static int LIBAVUTIL_VERSION_MICRO =  1;

    public static int    LIBAVUTIL_VERSION_INT = AV_VERSION_INT(LIBAVUTIL_VERSION_MAJOR,
                                                                LIBAVUTIL_VERSION_MINOR,
                                                                LIBAVUTIL_VERSION_MICRO);
    public static String LIBAVUTIL_VERSION     = AV_VERSION(LIBAVUTIL_VERSION_MAJOR,
                                                            LIBAVUTIL_VERSION_MINOR,
                                                            LIBAVUTIL_VERSION_MICRO);
    public static int    LIBAVUTIL_BUILD       = LIBAVUTIL_VERSION_INT;

    public static String LIBAVUTIL_IDENT       = "Lavu" + LIBAVUTIL_VERSION;


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
    public static class Error extends avutil {
        public static final String libname = Loader.load(paths, libnames);

        public static native int av_strerror(int errnum, byte[] errbuf, size_t errbuf_size);
    }

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
    public static native long av_rescale_rnd(long a, long b, long c, int /* enum AVRounding */ r);
    public static native long av_rescale_q(long a, AVRational.ByValue bq, AVRational.ByValue cq);
    public static native int av_compare_ts(long ts_a, AVRational.ByValue tb_a, long ts_b, AVRational.ByValue tb_b);

    //#include "rational.h"
    public static class AVRational extends Structure {
        public AVRational() { };
        public AVRational(int num, int den) {
            this.num = num;
            this.den = den;
        }
        public int num;
        public int den;

        public ByValue byValue() { return new ByValue(num, den); }

        public static class ByValue extends AVRational implements Structure.ByValue {
            public ByValue() { };
            public ByValue(int num, int den) { super(num, den); }
        }
        public static class ByReference extends AVRational implements Structure.ByReference {
            public ByReference() { };
            public ByReference(int num, int den) { super(num, den); }
        }
    }

    public static long av_cmp_q(AVRational a, AVRational b){
        long tmp= a.num * (long)b.den - b.num * (long)a.den;

        if(tmp != 0) return (tmp>>63)|1;
        else         return 0;
    }
    public static double av_q2d(AVRational a){
        return a.num / (double) a.den;
    }
    public static native int av_reduce(IntByReference dst_num, IntByReference dst_den, long num, long den, long max);
    public static native AVRational.ByValue av_mul_q(AVRational.ByValue b, AVRational.ByValue c);
    public static native AVRational.ByValue av_div_q(AVRational.ByValue b, AVRational.ByValue c);
    public static native AVRational.ByValue av_add_q(AVRational.ByValue b, AVRational.ByValue c);
    public static native AVRational.ByValue av_sub_q(AVRational.ByValue b, AVRational.ByValue c);
    public static native AVRational.ByValue av_d2q(double d, int max);
    public static native int av_nearer_q(AVRational.ByValue q, AVRational.ByValue q1, AVRational.ByValue q2);
    public static native int av_find_nearest_q_idx(AVRational.ByValue q, AVRational q_list);
    public static int av_find_nearest_q_idx(AVRational.ByValue q, AVRational[] q_list) {
        return av_find_nearest_q_idx(q, q_list[0]);
    }

    //#include "intfloat_readwrite.h"
    public static class AVExtFloat extends Structure {
        public byte exponent0, exponent1;
        public byte mantissa0, mantissa1, mantissa2, mantissa3, 
                    mantissa4, mantissa5, mantissa6, mantissa7;

        public static class ByValue extends AVExtFloat implements Structure.ByValue { }
    }

    public static native double av_int2dbl(long v);
    public static native float av_int2flt(int v);
    public static native double av_ext2dbl(AVExtFloat.ByValue ext);
    public static native long av_dbl2int(double d);
    public static native int av_flt2int(float d);
    public static native AVExtFloat.ByValue av_dbl2ext(double d);

    //#include "log.h"
    public static class AVOption extends PointerType { }
    public static class AVClass extends Structure {
        public AVClass() { }
        public AVClass(Pointer m) { super(m); read(); }

        public String class_name;
        public interface Item_name extends Callback {
            String callback(Pointer ctx);
        }
        public Item_name item_name;
        public AVOption option;
        public int version;

        public static class ByReference extends AVClass implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
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

    public interface ComplexCalls extends Library {
        final ComplexCalls I = (ComplexCalls)Native.loadLibrary(libname, ComplexCalls.class);
        void av_log(Pointer ptr, int level, String fmt, Object ... p);
        size_t av_strlcatf(byte[] dst, size_t size, String fmt, Object ... p);
    }
    public static void av_log(Pointer ptr, int level, String fmt, Object ... p) {
        ComplexCalls.I.av_log(ptr, level, fmt, p);
    }
    public static native void av_vlog(Pointer ptr, int level, String fmt, Pointer /* va_list */ vl);
    public static native int av_log_get_level();
    public static native void av_log_set_level(int l);
    public interface C extends Callback {
        void callback(Pointer ptr, int i, String fmt, Pointer /* va_list */ vl);
    }
    public static native void av_log_set_callback(C c);
    public static native void av_log_default_callback(Pointer ptr, int level, String fmt, Pointer /* va_list */ vl);

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
    public static native NativeLong av_adler32_update(NativeLong adler, Pointer buf, int len);

    //#include "avstring.h"
    public static native int av_strstart(String str, String pfx, StringByReference ptr);
    public static native int av_stristart(String str, String pfx, StringByReference ptr);
    public static native String av_stristr(String haystack, String needle);
    public static native size_t av_strlcpy(byte[] dst, String src, size_t size);
    public static native size_t av_strlcat(byte[] dst, String src, size_t size);
    public static size_t av_strlcatf(byte[] dst, size_t size, String fmt, Object ... p) {
        return ComplexCalls.I.av_strlcatf(dst, size, fmt, p);
    }
    public static native String av_d2str(double d);

    //#include "base64.h"
    public static native int av_base64_decode(byte[] out, byte[] in, int out_size);
    public static native int av_base64_decode(ByteBuffer out, ByteBuffer in, int out_size);
    public static native int av_base64_decode(Pointer out, Pointer in, int out_size);
    public static native String av_base64_encode(byte[] out, int out_size, byte[] in, int in_size);
    public static native String av_base64_encode(ByteBuffer out, int out_size, ByteBuffer in, int in_size);
    public static native String av_base64_encode(Pointer out, int out_size, Pointer in, int in_size);

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

    public static native int av_crc_init(IntByReference /* AVCRC* */ ctx, int le, int bits, int poly, int ctx_size);
    public static native IntByReference /* AVCRC* */ av_crc_get_table(int /* AVCRCId */ crc_id);
    public static native int av_crc(IntByReference /* AVCRC* */ ctx, int start_crc, Pointer buffer, size_t length);

    //#include "fifo.h"
    public static class AVFifoBuffer extends Structure {
        public Pointer buffer;
        public Pointer rptr, wptr, end;
        public int rndx, wndx;
    }

    public static native AVFifoBuffer av_fifo_alloc(int size);
    public static native void av_fifo_free(AVFifoBuffer f);
    public static native void av_fifo_reset(AVFifoBuffer f);
    public static native int av_fifo_size(AVFifoBuffer f);
    public static native int av_fifo_space(AVFifoBuffer f);
    public interface Func_read extends Callback {
        void callback(Pointer p1, Pointer p2, int i);
    }
    public static native int av_fifo_generic_read(AVFifoBuffer f, Pointer dest, int buf_size, Func_read func);
    public interface Func_write extends Callback {
        int callback(Pointer p1, Pointer p2, int i);
    }
    public static native int av_fifo_generic_write(AVFifoBuffer f, Pointer src, int size, Func_write func);
    public static native int av_fifo_realloc2(AVFifoBuffer f, int size);
    public static native void av_fifo_drain(AVFifoBuffer f, int size);
    public static byte av_fifo_peek(AVFifoBuffer f, int offs) {
        Pointer ptr = f.rptr.share(offs);
        if (Pointer.nativeValue(ptr) >= Pointer.nativeValue(f.end))
            ptr.share(-(Pointer.nativeValue(f.end) - Pointer.nativeValue(f.buffer)));
        return ptr.getByte(0);
    }

    //#include "lzo.h"
    public static final int
            AV_LZO_INPUT_DEPLETED = 1,
            AV_LZO_OUTPUT_FULL = 2,
            AV_LZO_INVALID_BACKPTR = 4,
            AV_LZO_ERROR = 8,

            AV_LZO_INPUT_PADDING = 8,
            AV_LZO_OUTPUT_PADDING = 12;

    public static native int av_lzo1x_decode(Pointer out, IntByReference outlen, Pointer in, IntByReference inlen);
    public static native void av_memcpy_backptr(Pointer dst, int back, int cnt);

    //#include "md5.h"
    public static final int av_md5_size = nativeLibrary.
            getGlobalVariableAddress("av_md5_size").getInt(0);

    public static class AVMD5 extends Structure { 
        public byte[] state = new byte[av_md5_size];
    }

    public static native void av_md5_init(AVMD5 ctx);
    public static native void av_md5_update(AVMD5 ctx, byte[] src, int len);
    public static native void av_md5_update(AVMD5 ctx, ByteBuffer src, int len);
    public static native void av_md5_update(AVMD5 ctx, Pointer src, int len);
    public static native void av_md5_final(AVMD5 ctx, byte[] dst);
    public static native void av_md5_final(AVMD5 ctx, ByteBuffer dst);
    public static native void av_md5_final(AVMD5 ctx, Pointer dst);
    public static native void av_md5_sum(byte[] dst, byte[] src, int len);
    public static native void av_md5_sum(ByteBuffer dst, ByteBuffer src, int len);
    public static native void av_md5_sum(Pointer dst, Pointer src, int len);

    //#include "pixdesc.h"
    public static class Pixdesc extends avutil {
        public static final String libname = Loader.load(paths, libnames);

        public static class AVComponentDescriptor extends Structure {
            public AVComponentDescriptor() { }
            public AVComponentDescriptor(Pointer m) { super(m); read(); }

            public short bitfield;
            //public short plane        :2;
            //public short step_minus1  :3;
            //public short offset_plus1 :3;
            //public short shift        :3;
            //public short depth_minus1 :4;
        }

        public static class AVPixFmtDescriptor extends Structure {
            public AVPixFmtDescriptor() { }
            public AVPixFmtDescriptor(Pointer m) { super(m); read(); }

            public String name;
            public byte nb_components;

            public byte log2_chroma_w;

            public byte log2_chroma_h;
            public byte flags;

            public AVComponentDescriptor comp0, comp1, comp2, comp3;
        }

        public static final int
                PIX_FMT_BE        = 1,
                PIX_FMT_PAL       = 2,
                PIX_FMT_BITSTREAM = 4,
                PIX_FMT_HWACCEL   = 8;

        public static final AVPixFmtDescriptor av_pix_fmt_descriptors = new AVPixFmtDescriptor(nativeLibrary.
                getGlobalVariableAddress("av_pix_fmt_descriptors"));

        public static native void read_line(short[] dst, Pointer data/*[4]*/, int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component);
        public static native void read_line(ShortBuffer dst, Pointer data/*[4]*/, int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component);
        public static native void read_line(Pointer dst, Pointer data/*[4]*/, int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component);
        public static native void write_line(short[] src, Pointer data/*[4]*/, int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w);
        public static native void write_line(ShortBuffer src, Pointer data/*[4]*/, int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w);
        public static native void write_line(Pointer src, Pointer data/*[4]*/, int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w);

        public static void read_line(short[] dst, Pointer data[/*4*/], int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component) {
            read_line(dst, data[0], linesize, desc, x, y, c, w, read_pal_component);
        }
        public static void read_line(ShortBuffer dst, Pointer data[/*4*/], int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component) {
            read_line(dst, data[0], linesize, desc, x, y, c, w, read_pal_component);
        }
        public static void read_line(Pointer dst, Pointer data[/*4*/], int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w, int read_pal_component) {
            read_line(dst, data[0], linesize, desc, x, y, c, w, read_pal_component);
        }
        public static void write_line(short[] src, Pointer data[/*4*/], int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w) {
            write_line(src, data[0], linesize, desc, x, y, c, w);
        }
        public static void write_line(ShortBuffer src, Pointer data[/*4*/], int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w) {
            write_line(src, data[0], linesize, desc, x, y, c, w);
        }
        public static void write_line(Pointer src, Pointer data[/*4*/], int linesize[/*4*/],
                AVPixFmtDescriptor desc, int x, int y, int c, int w) {
            write_line(src, data[0], linesize, desc, x, y, c, w);
        }

        public static native int /* enum PixelFormat */ av_get_pix_fmt(String name);
        public static native int av_get_bits_per_pixel(AVPixFmtDescriptor pixdesc);
    }

    //#include "sha1.h"
    public static final int av_sha1_size = nativeLibrary.
            getGlobalVariableAddress("av_sha1_size").getInt(0);

    public static class AVSHA1 extends Structure { 
        public byte[] state = new byte[av_sha1_size];
    }

    public static native void av_sha1_init(AVSHA1 context);
    public static native void av_sha1_update(AVSHA1 context, byte[] data, int len);
    public static native void av_sha1_update(AVSHA1 context, ByteBuffer data, int len);
    public static native void av_sha1_update(AVSHA1 context, Pointer data, int len);
    public static native void av_sha1_final(AVSHA1 context, byte digest[/*20*/]);
}

