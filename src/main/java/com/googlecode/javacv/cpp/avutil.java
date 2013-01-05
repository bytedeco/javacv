/*
 * Copyright (C) 2010,2011,2012 Samuel Audet
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
 * FFmpeg 1.0, which are covered by the following copyright notice:
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

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.SizeTPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;
import java.nio.ByteOrder;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude={"<libavutil/avutil.h>", "<libavutil/audioconvert.h>",
        "<libavutil/cpu.h>", "<libavutil/dict.h>", "<libavutil/opt.h>", "<libavutil/samplefmt.h>", "<libavutil/imgutils.h>"},
        includepath=genericIncludepath, linkpath=genericLinkpath, link="avutil@.51"),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload={"avutil-51", "avutil-52"}),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avutil {
    static { load(); }
    public static final String genericIncludepath = "/usr/local/include/ffmpeg/:/usr/local/include/:/opt/local/include/ffmpeg/:/opt/local/include/:/usr/include/ffmpeg/";
    public static final String genericLinkpath    = "/usr/local/lib/:/usr/local/lib64/:/opt/local/lib/:/opt/local/lib64/";
    public static final String windowsIncludepath = "C:/MinGW/local/include/ffmpeg/;C:/MinGW/include/ffmpeg/;C:/MinGW/local/include/;src/main/resources/com/googlecode/javacv/cpp/";
    public static final String windowsLinkpath    = "C:/MinGW/local/lib/;C:/MinGW/lib/";
    public static final String windowsPreloadpath = "C:/MinGW/local/bin/;C:/MinGW/bin/";
    public static final String androidIncludepath = "../include/";
    public static final String androidLinkpath    = "../lib/";

    /**
     * @file
     * external API header
     */

    /*
     * @mainpage
     *
     * @section ffmpeg_intro Introduction
     *
     * This document describes the usage of the different libraries
     * provided by FFmpeg.
     *
     * @li @ref libavc "libavcodec" encoding/decoding library
     * @li @subpage libavfilter graph based frame editing library
     * @li @ref libavf "libavformat" I/O and muxing/demuxing library
     * @li @ref lavd "libavdevice" special devices muxing/demuxing library
     * @li @ref lavu "libavutil" common utility library
     * @li @subpage libpostproc post processing library
     * @li @subpage libswscale  color conversion and scaling library
     */

    /**
     * @defgroup lavu Common utility functions
     *
     * @brief
     * libavutil contains the code shared across all the other FFmpeg
     * libraries
     *
     * @note In order to use the functions provided by avutil you must include
     * the specific header.
     *
     * @{
     *
     * @defgroup lavu_crypto Crypto and Hashing
     *
     * @{
     * @}
     *
     * @defgroup lavu_math Maths
     * @{
     *
     * @}
     *
     * @defgroup lavu_string String Manipulation
     *
     * @{
     *
     * @}
     *
     * @defgroup lavu_mem Memory Management
     *
     * @{
     *
     * @}
     *
     * @defgroup lavu_data Data Structures
     * @{
     *
     * @}
     *
     * @defgroup lavu_audio Audio related
     *
     * @{
     *
     * @}
     *
     * @defgroup lavu_error Error Codes
     *
     * @{
     *
     * @}
     *
     * @defgroup lavu_misc Other
     *
     * @{
     *
     * @defgroup lavu_internal Internal
     *
     * Not exported functions, for internal usage only
     *
     * @{
     *
     * @}
     */


    /**
     * @defgroup preproc_misc Preprocessor String Macros
     *
     * String manipulation macros
     *
     * @{
     */

//            AV_STRINGIFY(s)         AV_TOSTRING(s)
//            AV_TOSTRING(s) #s
//    
//            AV_GLUE(a, b) a ## b
//            AV_JOIN(a, b) AV_GLUE(a, b)
//    
//            AV_PRAGMA(s) _Pragma(#s)

    /**
     * @}
     */

    /**
     * @defgroup version_utils Library Version Macros
     *
     * Useful to check and match library version in order to maintain
     * backward compatibility.
     *
     * @{
     */

    public static int    AV_VERSION_INT(int a, int b, int c) { return (a<<16 | b<<8 | c); }
    public static String AV_VERSION_DOT(int a, int b, int c) { return a + "." + b + "." + c; }
    public static String AV_VERSION(int a, int b, int c)     { return AV_VERSION_DOT(a, b, c); }

    /**
     * @}
     */


    // #include "version.h"
    /**
     * @file
     * @ingroup lavu
     * Libavutil version macros
     */

    /**
     * @defgroup lavu_ver Version and Build diagnostics
     *
     * Macros and function useful to check at compiletime and at runtime
     * which version of libavutil is in use.
     *
     * @{
     */

    public static final int LIBAVUTIL_VERSION_MAJOR = 51;
    public static final int LIBAVUTIL_VERSION_MINOR = 73;
    public static final int LIBAVUTIL_VERSION_MICRO = 101;

    public static final int    LIBAVUTIL_VERSION_INT = AV_VERSION_INT(LIBAVUTIL_VERSION_MAJOR,
                                                                      LIBAVUTIL_VERSION_MINOR,
                                                                      LIBAVUTIL_VERSION_MICRO);
    public static final String LIBAVUTIL_VERSION     = AV_VERSION(LIBAVUTIL_VERSION_MAJOR,
                                                                  LIBAVUTIL_VERSION_MINOR,
                                                                  LIBAVUTIL_VERSION_MICRO);
    public static final int    LIBAVUTIL_BUILD       = LIBAVUTIL_VERSION_INT;

    public static final String LIBAVUTIL_IDENT       = "Lavu" + LIBAVUTIL_VERSION;

    /**
     * @}
     */


    /**
     * @addtogroup lavu_ver
     * @{
     */

    /**
     * Return the LIBAVUTIL_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int avutil_version();

    /**
     * Return the libavutil build-time configuration.
     */
    public static native String avutil_configuration();

    /**
     * Return the libavutil license.
     */
    public static native String avutil_license();

    /**
     * @}
     */

    /**
     * @addtogroup lavu_media Media Type
     * @brief Media Type
     */

    public static final int
            AVMEDIA_TYPE_UNKNOWN    = -1, ///< Usually treated as AVMEDIA_TYPE_DATA
            AVMEDIA_TYPE_VIDEO      = 0,
            AVMEDIA_TYPE_AUDIO      = 1,
            AVMEDIA_TYPE_DATA       = 2,  ///< Opaque data information usually continuous
            AVMEDIA_TYPE_SUBTITLE   = 3,
            AVMEDIA_TYPE_ATTACHMENT = 4,  ///< Opaque data information usually sparse
            AVMEDIA_TYPE_NB         = 5;

    /**
     * Return a string describing the media_type enum, NULL if media_type
     * is unknown.
     */
    public static native String av_get_media_type_string(@Cast("AVMediaType") int media_type);

    /**
     * @defgroup lavu_const Constants
     * @{
     *
     * @defgroup lavu_enc Encoding specific
     *
     * @note those definition should move to avcodec
     * @{
     */
    public static final int
            FF_LAMBDA_SHIFT = 7,
            FF_LAMBDA_SCALE = (1<<FF_LAMBDA_SHIFT),
            FF_QP2LAMBDA = 118, ///< factor to convert from H.263 QP to lambda
            FF_LAMBDA_MAX = (256*128-1),

            FF_QUALITY_SCALE = FF_LAMBDA_SCALE; //FIXME maybe remove

    /**
     * @}
     * @defgroup lavu_time Timestamp specific
     *
     * FFmpeg internal timebase and timestamp definitions
     *
     * @{
     */

    /**
     * @brief Undefined timestamp value
     *
     * Usually reported by demuxer that work on containers that do not provide
     * either pts or dts.
     */

    public static final long AV_NOPTS_VALUE       = 0x8000000000000000L;

    /**
     * Internal time base represented as integer
     */

    public static final int AV_TIME_BASE          = 1000000;

    /**
     * Internal time base represented as fractional value
     */

    public static final AVRational AV_TIME_BASE_Q =
            load() == null ? null : new AVRational().num(1).den(AV_TIME_BASE);

    /**
     * @}
     * @}
     * @defgroup lavu_picture Image related
     *
     * AVPicture types, pixel formats and basic image planes manipulation.
     *
     * @{
     */

    public static final int // enum AVPictureType {
            AV_PICTURE_TYPE_NONE = 0, ///< Undefined
            AV_PICTURE_TYPE_I    = 2, ///< Intra
            AV_PICTURE_TYPE_P    = 3, ///< Predicted
            AV_PICTURE_TYPE_B    = 4, ///< Bi-dir predicted
            AV_PICTURE_TYPE_S    = 5, ///< S(GMC)-VOP MPEG4
            AV_PICTURE_TYPE_SI   = 6, ///< Switching Intra
            AV_PICTURE_TYPE_SP   = 7, ///< Switching Predicted
            AV_PICTURE_TYPE_BI   = 8; ///< BI type

    /**
     * Return a single letter to describe the given picture type
     * pict_type.
     *
     * @param[in] pict_type the picture type @return a single character
     * representing the picture type, '?' if pict_type is unknown
     */
    public static native @Cast("char") int av_get_picture_type_char(@Cast("AVPictureType") int pict_type);

    /**
     * @}
     */


    // #include "attributes.h"
    /**
     * @file
     * Macro definitions for various function/variable attributes
     */
    // ...


    // #include "avconfig.h"
    public static boolean AV_HAVE_BIGENDIAN() { return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN; }


    // #include "common.h"
    /**
     * @file
     * common internal and external API header
     */
    public static int MKTAG  (int a, int b, int c, int d) { return a | (b << 8) | (c << 16) | (d << 24); }
    public static int MKBETAG(int a, int b, int c, int d) { return d | (c << 8) | (b << 16) | (a << 24); }

    // ...


    // #include "error.h"
    /**
     * @file
     * error code definitions
     */

    /**
     * @addtogroup lavu_error
     *
     * @{
     */

//    /* error handling */
//    #if EDOM > 0
//            AVERROR(e) (-(e))   ///< Returns a negative error code from a POSIX error code, to return from library functions.
//            AVUNERROR(e) (-(e)) ///< Returns a POSIX error code from a library function error return value.
//    #else
//    /* Some platforms have E* and errno already negated. */
//            AVERROR(e) (e)
//            AVUNERROR(e) (e)
//    #endif

    public static int FFERRTAG(int a, int b, int c, int d) { return -(int)MKTAG(a, b, c, d); }

    public static final int
            AVERROR_BSF_NOT_FOUND      = FFERRTAG(0xF8,'B','S','F'), ///< Bitstream filter not found
            AVERROR_BUG                = FFERRTAG( 'B','U','G','!'), ///< Internal bug, also see AVERROR_BUG2
            AVERROR_BUFFER_TOO_SMALL   = FFERRTAG( 'B','U','F','S'), ///< Buffer too small
            AVERROR_DECODER_NOT_FOUND  = FFERRTAG(0xF8,'D','E','C'), ///< Decoder not found
            AVERROR_DEMUXER_NOT_FOUND  = FFERRTAG(0xF8,'D','E','M'), ///< Demuxer not found
            AVERROR_ENCODER_NOT_FOUND  = FFERRTAG(0xF8,'E','N','C'), ///< Encoder not found
            AVERROR_EOF                = FFERRTAG( 'E','O','F',' '), ///< End of file
            AVERROR_EXIT               = FFERRTAG( 'E','X','I','T'), ///< Immediate exit was requested; the called function should not be restarted
            AVERROR_EXTERNAL           = FFERRTAG( 'E','X','T',' '), ///< Generic error in an external library
            AVERROR_FILTER_NOT_FOUND   = FFERRTAG(0xF8,'F','I','L'), ///< Filter not found
            AVERROR_INVALIDDATA        = FFERRTAG( 'I','N','D','A'), ///< Invalid data found when processing input
            AVERROR_MUXER_NOT_FOUND    = FFERRTAG(0xF8,'M','U','X'), ///< Muxer not found
            AVERROR_OPTION_NOT_FOUND   = FFERRTAG(0xF8,'O','P','T'), ///< Option not found
            AVERROR_PATCHWELCOME       = FFERRTAG( 'P','A','W','E'), ///< Not yet implemented in FFmpeg, patches welcome
            AVERROR_PROTOCOL_NOT_FOUND = FFERRTAG(0xF8,'P','R','O'), ///< Protocol not found
            AVERROR_STREAM_NOT_FOUND   = FFERRTAG(0xF8,'S','T','R'), ///< Stream not found

    /**
     * This is semantically identical to AVERROR_BUG
     * it has been introduced in Libav after our AVERROR_BUG and with a modified value.
     */
            AVERROR_BUG2               = FFERRTAG( 'B','U','G',' '),
            AVERROR_UNKNOWN            = FFERRTAG( 'U','N','K','N'), ///< Unknown error, typically from an external library

            AV_ERROR_MAX_STRING_SIZE   = 64;

    /**
     * Put a description of the AVERROR code errnum in errbuf.
     * In case of failure the global variable errno is set to indicate the
     * error. Even in case of failure av_strerror() will print a generic
     * error message indicating the errnum provided to errbuf.
     *
     * @param errnum      error code to describe
     * @param errbuf      buffer to which description is written
     * @param errbuf_size the size in bytes of errbuf
     * @return 0 on success, a negative value if a description for errnum
     * cannot be found
     */
    public static native int av_strerror(int errnum, @Cast("char*") byte[] errbuf, @Cast("size_t") long errbuf_size);

    /**
     * Fill the provided buffer with a string containing an error string
     * corresponding to the AVERROR code errnum.
     *
     * @param errbuf         a buffer
     * @param errbuf_size    size in bytes of errbuf
     * @param errnum         error code to describe
     * @return the buffer in input, filled with the error description
     * @see av_strerror()
     */
    public static native @Cast("char*") BytePointer av_make_error_string(@Cast("char*") BytePointer errbuf,
            @Cast("size_t") long errbuf_size, int errnum);

    /**
     * Convenience macro, the return value should be used only directly in
     * function arguments but never stand-alone.
     */
//    public static native String av_err2str(int errnum);

    /**
     * @}
     */


    // #include "mem.h"
    /**
     * @file
     * memory handling functions
     */

    /**
     * @addtogroup lavu_mem
     * @{
     */
    // ... 

    /**
     * Allocate a block of size bytes with alignment suitable for all
     * memory accesses (including vectors if available on the CPU).
     * @param size Size in bytes for the memory block to be allocated.
     * @return Pointer to the allocated block, NULL if the block cannot
     * be allocated.
     * @see av_mallocz()
     */
    public static native Pointer av_malloc(@Cast("size_t") long size);

    /**
     * Helper function to allocate a block of size * nmemb bytes with
     * using av_malloc()
     * @param nmemb Number of elements
     * @param size Size of the single element
     * @return Pointer to the allocated block, NULL if the block cannot
     * be allocated.
     * @see av_malloc()
     */
    public static native Pointer av_malloc_array(@Cast("size_t") long nmemb, @Cast("size_t") long size);

    /**
     * Allocate or reallocate a block of memory.
     * If ptr is NULL and size > 0, allocate a new block. If
     * size is zero, free the memory block pointed to by ptr.
     * @param ptr Pointer to a memory block already allocated with
     * av_malloc(z)() or av_realloc() or NULL.
     * @param size Size in bytes for the memory block to be allocated or
     * reallocated.
     * @return Pointer to a newly reallocated block or NULL if the block
     * cannot be reallocated or the function is used to free the memory block.
     * @see av_fast_realloc()
     */
    public static native Pointer av_realloc(Pointer ptr, @Cast("size_t") long size);

    /**
     * Allocate or reallocate a block of memory.
     * This function does the same thing as av_realloc, except:
     * - It takes two arguments and checks the result of the multiplication for
     *   integer overflow.
     * - It frees the input block in case of failure, thus avoiding the memory
     *   leak with the classic "buf = realloc(buf); if (!buf) return -1;".
     */
    public static native Pointer av_realloc_f(Pointer ptr, @Cast("size_t") long nelem, @Cast("size_t") long elsize);

    /**
     * Free a memory block which has been allocated with av_malloc(z)() or
     * av_realloc().
     * @param ptr Pointer to the memory block which should be freed.
     * @note ptr = NULL is explicitly allowed.
     * @note It is recommended that you use av_freep() instead.
     * @see av_freep()
     */
    public static native void av_free(Pointer ptr);

    /**
     * Allocate a block of size bytes with alignment suitable for all
     * memory accesses (including vectors if available on the CPU) and
     * zero all the bytes of the block.
     * @param size Size in bytes for the memory block to be allocated.
     * @return Pointer to the allocated block, NULL if it cannot be allocated.
     * @see av_malloc()
     */
    public static native Pointer av_mallocz(@Cast("size_t") long size);

    /**
     * Allocate a block of nmemb * size bytes with alignment suitable for all
     * memory accesses (including vectors if available on the CPU) and
     * zero all the bytes of the block.
     * The allocation will fail if nmemb * size is greater than or equal
     * to INT_MAX.
     * @param nmemb
     * @param size
     * @return Pointer to the allocated block, NULL if it cannot be allocated.
     */
    public static native Pointer av_calloc(@Cast("size_t") long nmemb, @Cast("size_t") long size);

    /**
     * Helper function to allocate a block of size * nmemb bytes with
     * using av_mallocz()
     * @param nmemb Number of elements
     * @param size Size of the single element
     * @return Pointer to the allocated block, NULL if the block cannot
     * be allocated.
     * @see av_mallocz()
     * @see av_malloc_array()
     */
    public static native Pointer av_mallocz_array(@Cast("size_t") long nmemb, @Cast("size_t") long size);

    /**
     * Duplicate the string s.
     * @param s string to be duplicated
     * @return Pointer to a newly allocated string containing a
     * copy of s or NULL if the string cannot be allocated.
     */
    public static native @Cast("char*") BytePointer av_strdup(String s);

    /**
     * Free a memory block which has been allocated with av_malloc(z)() or
     * av_realloc() and set the pointer pointing to it to NULL.
     * @param ptr Pointer to the pointer to the memory block which should
     * be freed.
     * @see av_free()
     */
    public static native void av_freep(Pointer ptr);

    /**
     * Add an element to a dynamic array.
     *
     * @param tab_ptr Pointer to the array.
     * @param nb_ptr  Pointer to the number of elements in the array.
     * @param elem    Element to be added.
     */
    public static native void av_dynarray_add(Pointer tab_ptr, int[] nb_ptr, Pointer elem);

    /**
     * Multiply two size_t values checking for overflow.
     * @return  0 if success, AVERROR(EINVAL) if overflow.
     */
    public static native int av_size_mult(@Cast("size_t") long a, @Cast("size_t") long b, SizeTPointer r);

    /**
     * Set the maximum size that may me allocated in one block.
     */
    public static native void av_max_alloc(@Cast("size_t") long max);

    /**
     * @}
     */


    // #include "rational.h"
    /**
     * @file
     * rational numbers
     * @author Michael Niedermayer <michaelni@gmx.at>
     */

    /**
     * @addtogroup lavu_math
     * @{
     */

    /**
     * rational number numerator/denominator
     */
    public static class AVRational extends Pointer {
        static { load(); }
        public AVRational() { allocate(); }
        public AVRational(int size) { allocateArray(size); }
        public AVRational(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVRational position(int position) {
            return (AVRational)super.position(position);
        }

        public native int num(); public native AVRational num(int num); ///< numerator
        public native int den(); public native AVRational den(int den); ///< denominator
    }

    /**
     * Compare two rationals.
     * @param a first rational
     * @param b second rational
     * @return 0 if a==b, 1 if a>b, -1 if a<b, and INT_MIN if one of the
     * values is of the form 0/0
     */
    public static native int av_cmp_q(@ByVal AVRational a, @ByVal AVRational b);

    /**
     * Convert rational to double.
     * @param a rational to convert
     * @return (double) a
     */
    public static native double av_q2d(@ByVal AVRational a);

    /**
     * Reduce a fraction.
     * This is useful for framerate calculations.
     * @param dst_num destination numerator
     * @param dst_den destination denominator
     * @param num source numerator
     * @param den source denominator
     * @param max the maximum allowed for dst_num & dst_den
     * @return 1 if exact, 0 otherwise
     */
    public static native int av_reduce(int[] dst_num, int[] dst_den, long num, long den, long max);

    /**
     * Multiply two rationals.
     * @param b first rational
     * @param c second rational
     * @return b*c
     */
    public static native @ByVal AVRational av_mul_q(@ByVal AVRational b, @ByVal AVRational c);

    /**
     * Divide one rational by another.
     * @param b first rational
     * @param c second rational
     * @return b/c
     */
    public static native @ByVal AVRational av_div_q(@ByVal AVRational b, @ByVal AVRational c);

    /**
     * Add two rationals.
     * @param b first rational
     * @param c second rational
     * @return b+c
     */
    public static native @ByVal AVRational av_add_q(@ByVal AVRational b, @ByVal AVRational c);

    /**
     * Subtract one rational from another.
     * @param b first rational
     * @param c second rational
     * @return b-c
     */
    public static native @ByVal AVRational av_sub_q(@ByVal AVRational b, @ByVal AVRational c);

    /**
     * Invert a rational.
     * @param q value
     * @return 1 / q
     */
    public static native @ByVal AVRational av_inv_q(@ByVal AVRational q);

    /**
     * Convert a double precision floating point number to a rational.
     * inf is expressed as {1,0} or {-1,0} depending on the sign.
     *
     * @param d double to convert
     * @param max the maximum allowed numerator and denominator
     * @return (AVRational) d
     */
    public static native @ByVal AVRational av_d2q(double d, int max);

    /**
     * @return 1 if q1 is nearer to q than q2, -1 if q2 is nearer
     * than q1, 0 if they have the same distance.
     */
    public static native int av_nearer_q(@ByVal AVRational q, @ByVal AVRational q1, @ByVal AVRational q2);

    /**
     * Find the nearest value in q_list to q.
     * @param q_list an array of rationals terminated by {0, 0}
     * @return the index of the nearest value found in the array
     */
    public static native int av_find_nearest_q_idx(@ByVal AVRational q, AVRational q_list);

    /**
     * @}
     */


    // #include "intfloat.h"
    // ...


    // #include "mathematics.h"
    public static final double
            M_E           = 2.7182818284590452354,   /* e */
            M_LN2         = 0.69314718055994530942,  /* log_e 2 */
            M_LN10        = 2.30258509299404568402,  /* log_e 10 */
            M_LOG2_10     = 3.32192809488736234787,  /* log_2 10 */
            M_PHI         = 1.61803398874989484820,   /* phi / golden ratio */
            M_PI          = 3.14159265358979323846,  /* pi */
            M_SQRT1_2     = 0.70710678118654752440,  /* 1/sqrt(2) */
            M_SQRT2       = 1.41421356237309504880,  /* sqrt(2) */
            NAN           = Float.intBitsToFloat(0x7fc00000),
            INFINITY      = Float.intBitsToFloat(0x7f800000);

    /**
     * @addtogroup lavu_math
     * @{
     */

    public static final int // enum AVRounding {
        AV_ROUND_ZERO     = 0, ///< Round toward zero.
        AV_ROUND_INF      = 1, ///< Round away from zero.
        AV_ROUND_DOWN     = 2, ///< Round toward -infinity.
        AV_ROUND_UP       = 3, ///< Round toward +infinity.
        AV_ROUND_NEAR_INF = 5; ///< Round to nearest and halfway cases away from zero.

    /**
     * Return the greatest common divisor of a and b.
     * If both a and b are 0 or either or both are <0 then behavior is
     * undefined.
     */
    public static native long av_gcd(long a, long b);

    /**
     * Rescale a 64-bit integer with rounding to nearest.
     * A simple a*b/c isn't possible as it can overflow.
     */
    public static native long av_rescale(long a, long b, long c);

    /**
     * Rescale a 64-bit integer with specified rounding.
     * A simple a*b/c isn't possible as it can overflow.
     */
    public static native long av_rescale_rnd(long a, long b, long c, @Cast("AVRounding") int r);

    /**
     * Rescale a 64-bit integer by 2 rational numbers.
     */
    public static native long av_rescale_q(long a, @ByVal AVRational bq, @ByVal AVRational cq);

    /**
     * Rescale a 64-bit integer by 2 rational numbers with specified rounding.
     */
    public static native long av_rescale_q_rnd(long a, @ByVal AVRational bq, @ByVal AVRational cq,
            @Cast("AVRounding") int r);

    /**
     * Compare 2 timestamps each in its own timebases.
     * The result of the function is undefined if one of the timestamps
     * is outside the int64_t range when represented in the others timebase.
     * @return -1 if ts_a is before ts_b, 1 if ts_a is after ts_b or 0 if they represent the same position
     */
    public static native int av_compare_ts(long ts_a, @ByVal AVRational tb_a,
            long ts_b, @ByVal AVRational tb_b);

    /**
     * Compare 2 integers modulo mod.
     * That is we compare integers a and b for which only the least
     * significant log2(mod) bits are known.
     *
     * @param mod must be a power of 2
     * @return a negative value if a is smaller than b
     *         a positive value if a is greater than b
     *         0                if a equals          b
     */
    public static native long av_compare_mod(@Cast("uint64_t") long a, @Cast("uint64_t") long b,
            @Cast("uint64_t") long mod);

    /**
     * @}
     */


    // #include "intfloat_readwrite.h"
    // ...


    // #include "log.h"
    public static final int // enum AVClassCategory {
            AV_CLASS_CATEGORY_NA = 0,
            AV_CLASS_CATEGORY_INPUT = 1,
            AV_CLASS_CATEGORY_OUTPUT = 2,
            AV_CLASS_CATEGORY_MUXER = 3,
            AV_CLASS_CATEGORY_DEMUXER = 4,
            AV_CLASS_CATEGORY_ENCODER = 5,
            AV_CLASS_CATEGORY_DECODER = 6,
            AV_CLASS_CATEGORY_FILTER = 7,
            AV_CLASS_CATEGORY_BITSTREAM_FILTER = 8,
            AV_CLASS_CATEGORY_SWSCALER = 9,
            AV_CLASS_CATEGORY_SWRESAMPLER = 10;

    /**
     * Describe the class of an AVClass context structure. That is an
     * arbitrary struct of which the first field is a pointer to an
     * AVClass struct (e.g. AVCodecContext, AVFormatContext etc.).
     */
    public static class AVClass extends Pointer {
        static { load(); }
        public AVClass() { allocate(); }
        public AVClass(int size) { allocateArray(size); }
        public AVClass(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVClass position(int position) {
            return (AVClass)super.position(position);
        }

        /**
         * The name of the class; usually it is the same name as the
         * context structure type to which the AVClass is associated.
         */
        @Cast("const char*")
        public native BytePointer class_name();        public native AVClass class_name(BytePointer class_name);

        /**
         * A pointer to a function which returns the name of a context
         * instance ctx associated with the class.
         */
        public static class Item_name extends FunctionPointer {
            static { load(); }
            public    Item_name(Pointer p) { super(p); }
            protected Item_name() { allocate(); }
            private native void allocate();
            public native @Cast("const char*") BytePointer call(Pointer ctx);
        }
        public native Item_name item_name();           public native AVClass item_name(Item_name item_name);

        /**
         * a pointer to the first option specified in the class if any or NULL
         *
         * @see av_set_default_options()
         */
        public native @Const AVOption option();        public native AVClass option(AVOption option);

        /**
         * LIBAVUTIL_VERSION with which this structure was created.
         * This is used to allow fields to be added without requiring major
         * version bumps everywhere.
         */

        public native int version();                   public native AVClass version(int version);

        /**
         * Offset in the structure where log_level_offset is stored.
         * 0 means there is no such variable
         */
        public native int log_level_offset_offset();   public native AVClass log_level_offset_offset(int log_level_offset_offset);

        /**
         * Offset in the structure where a pointer to the parent context for loging is stored.
         * for example a decoder that uses eval.c could pass its AVCodecContext to eval as such
         * parent context. And a av_log() implementation could then display the parent context
         * can be NULL of course
         */
        public native int parent_log_context_offset(); public native AVClass parent_log_context_offset(int parent_log_context_offset);

        /**
         * Return next AVOptions-enabled child or NULL
         */
        public static class Child_next extends FunctionPointer {
            static { load(); }
            public    Child_next(Pointer p) { super(p); }
            protected Child_next() { allocate(); }
            private native void allocate();
            public native Pointer call(Pointer obj, Pointer prev);
        }
        public native Child_next child_next();         public native AVClass child_next(Child_next child_next);

        /**
         * Return an AVClass corresponding to next potential
         * AVOptions-enabled child.
         *
         * The difference between child_next and this is that
         * child_next iterates over _already existing_ objects, while
         * child_class_next iterates over _all possible_ children.
         */
        public static class Child_class_next extends FunctionPointer {
            static { load(); }
            public    Child_class_next(Pointer p) { super(p); }
            protected Child_class_next() { allocate(); }
            private native void allocate();
            public native @Const AVClass call(@Const AVClass prev);
        }
        public native Child_class_next child_class_next(); public native AVClass child_class_next(Child_class_next child_class_next);

        /**
         * Category used for visualization (like color)
         * This is only set if the category is equal for all objects using this class.
         * available since version (51 << 16 | 56 << 8 | 100)
         */
        @Cast("AVClassCategory")
        public native int category(); public native AVClass category(int category);

        /**
         * Callback to return the category.
         * available since version (51 << 16 | 59 << 8 | 100)
         */
        public static class Get_category extends FunctionPointer {
            static { load(); }
            public    Get_category(Pointer p) { super(p); }
            protected Get_category() { allocate(); }
            private native void allocate();
            public native @Cast("AVClassCategory") int call(Pointer ctx);
        }
        public native Get_category get_category(); public native AVClass get_category(Get_category get_category);
    }

    /* av_log API */
    public static final int
            AV_LOG_QUIET   = -8,

    /**
     * Something went really wrong and we will crash now.
     */
            AV_LOG_PANIC   =  0,

    /**
     * Something went wrong and recovery is not possible.
     * For example, no header was found for a format which depends
     * on headers or an illegal combination of parameters is used.
     */
            AV_LOG_FATAL   =  8,

    /**
     * Something went wrong and cannot losslessly be recovered.
     * However, not all future data is affected.
     */
            AV_LOG_ERROR   = 16,

    /**
     * Something somehow does not look correct. This may or may not
     * lead to problems. An example would be the use of '-vstrict -2'.
     */
            AV_LOG_WARNING = 24,

            AV_LOG_INFO    = 32,
            AV_LOG_VERBOSE = 40,

    /**
     * Stuff which is only useful for libav* developers.
     */
            AV_LOG_DEBUG   = 48,

            AV_LOG_MAX_OFFSET = (AV_LOG_DEBUG - AV_LOG_QUIET);

    /**
     * Send the specified message to the log if the level is less than or equal
     * to the current av_log_level. By default, all logging messages are sent to
     * stderr. This behavior can be altered by setting a different av_vlog callback
     * function.
     *
     * @param avcl A pointer to an arbitrary struct of which the first field is a
     * pointer to an AVClass struct.
     * @param level The importance level of the message, lower values signifying
     * higher importance.
     * @param fmt The format string (printf-compatible) that specifies how
     * subsequent arguments are converted to output.
     * @see av_vlog
     */
    public static native void av_log(Pointer avcl, int level, String fmt);

    public static native void av_vlog(Pointer avcl, int level, String fmt,
            @ByVal @Cast("va_list*") Pointer vl);
    public static native int av_log_get_level();
    public static native void av_log_set_level(int l);
    public static class LogCallback extends FunctionPointer {
        static { load(); }
        public    LogCallback(Pointer p) { super(p); }
        protected LogCallback() { allocate(); }
        private native void allocate();
        public native void call(Pointer ptr, int i, String fmt,
                @ByVal @Cast("va_list*") Pointer vl);
    }
    public static native void av_log_set_callback(LogCallback c);
    public static native void av_log_default_callback(Pointer ptr, int level,
            String fmt, @ByVal @Cast("va_list*") Pointer vl);
    public static native String av_default_item_name(Pointer ctx);
    public static native @Cast("AVClassCategory") int av_default_get_category(Pointer ptr);

    /**
     * Format a line of log the same way as the default callback.
     * @param line          buffer to receive the formated line
     * @param line_size     size of the buffer
     * @param print_prefix  used to store whether the prefix must be printed;
     *                      must point to a persistent integer initially set to 1
     */
    public static native void av_log_format_line(Pointer ptr, int level, String fmt,
            @ByVal @Cast("va_list*") Pointer vl, @Cast("char*") byte[] line, int line_size, int[] print_prefix);

    /**
     * av_dlog macros
     * Useful to print debug messages that shouldn't get compiled in normally.
     */
    // ...

    /**
     * Skip repeated messages, this requires the user app to use av_log() instead of
     * (f)printf as the 2 would otherwise interfere and lead to
     * "Last message repeated x times" messages below (f)printf messages with some
     * bad luck.
     * Also to receive the last, "last repeated" line if any, the user app must
     * call av_log(NULL, AV_LOG_QUIET, "%s", ""); at the end
     */
    public static final int AV_LOG_SKIP_REPEATED = 1;
    public static native void av_log_set_flags(int arg);


    // #include "pixfmt.h"
    /**
     * @file
     * pixel format definitions
     *
     */

    public static final int
            AVPALETTE_SIZE = 1024,
            AVPALETTE_COUNT = 256;

    /**
     * Pixel format.
     *
     * @note
     * PIX_FMT_RGB32 is handled in an endian-specific manner. An RGBA
     * color is put together as:
     *  (A << 24) | (R << 16) | (G << 8) | B
     * This is stored as BGRA on little-endian CPU architectures and ARGB on
     * big-endian CPUs.
     *
     * @par
     * When the pixel format is palettized RGB (PIX_FMT_PAL8), the palettized
     * image data is stored in AVFrame.data[0]. The palette is transported in
     * AVFrame.data[1], is 1024 bytes long (256 4-byte entries) and is
     * formatted the same as in PIX_FMT_RGB32 described above (i.e., it is
     * also endian-specific). Note also that the individual RGB palette
     * components stored in AVFrame.data[1] should be in the range 0..255.
     * This is important as many custom PAL8 video codecs that were designed
     * to run on the IBM VGA graphics adapter use 6-bit palette components.
     *
     * @par
     * For all the 8bit per pixel formats, an RGB32 palette is in data[1] like
     * for pal8. This palette is filled in automatically by the function
     * allocating the picture.
     *
     * @note
     * make sure that all newly added big endian formats have pix_fmt&1==1
     * and that all newly added little endian formats have pix_fmt&1==0
     * this allows simpler detection of big vs little endian.
     */
    public static final int // enum PixelFormat {
            PIX_FMT_NONE      = -1,
            PIX_FMT_YUV420P   =  0, ///< planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
            PIX_FMT_YUYV422   =  1, ///< packed YUV 4:2:2, 16bpp, Y0 Cb Y1 Cr
            PIX_FMT_RGB24     =  2, ///< packed RGB 8:8:8, 24bpp, RGBRGB...
            PIX_FMT_BGR24     =  3, ///< packed RGB 8:8:8, 24bpp, BGRBGR...
            PIX_FMT_YUV422P   =  4, ///< planar YUV 4:2:2, 16bpp, (1 Cr & Cb sample per 2x1 Y samples)
            PIX_FMT_YUV444P   =  5, ///< planar YUV 4:4:4, 24bpp, (1 Cr & Cb sample per 1x1 Y samples)
            PIX_FMT_YUV410P   =  6, ///< planar YUV 4:1:0,  9bpp, (1 Cr & Cb sample per 4x4 Y samples)
            PIX_FMT_YUV411P   =  7, ///< planar YUV 4:1:1, 12bpp, (1 Cr & Cb sample per 4x1 Y samples)
            PIX_FMT_GRAY8     =  8, ///<        Y        ,  8bpp
            PIX_FMT_MONOWHITE =  9, ///<        Y        ,  1bpp, 0 is white, 1 is black, in each byte pixels are ordered from the msb to the lsb
            PIX_FMT_MONOBLACK = 10, ///<        Y        ,  1bpp, 0 is black, 1 is white, in each byte pixels are ordered from the msb to the lsb
            PIX_FMT_PAL8      = 11, ///< 8 bit with PIX_FMT_RGB32 palette
            PIX_FMT_YUVJ420P  = 12, ///< planar YUV 4:2:0, 12bpp, full scale (JPEG), deprecated in favor of PIX_FMT_YUV420P and setting color_range
            PIX_FMT_YUVJ422P  = 13, ///< planar YUV 4:2:2, 16bpp, full scale (JPEG), deprecated in favor of PIX_FMT_YUV422P and setting color_range
            PIX_FMT_YUVJ444P  = 14, ///< planar YUV 4:4:4, 24bpp, full scale (JPEG), deprecated in favor of PIX_FMT_YUV444P and setting color_range
            PIX_FMT_XVMC_MPEG2_MC = 15,///< XVideo Motion Acceleration via common packet passing
            PIX_FMT_XVMC_MPEG2_IDCT = 16,
            PIX_FMT_UYVY422   = 17, ///< packed YUV 4:2:2, 16bpp, Cb Y0 Cr Y1
            PIX_FMT_UYYVYY411 = 18, ///< packed YUV 4:1:1, 12bpp, Cb Y0 Y1 Cr Y2 Y3
            PIX_FMT_BGR8      = 19, ///< packed RGB 3:3:2,  8bpp, (msb)2B 3G 3R(lsb)
            PIX_FMT_BGR4      = 20, ///< packed RGB 1:2:1 bitstream,  4bpp, (msb)1B 2G 1R(lsb), a byte contains two pixels, the first pixel in the byte is the one composed by the 4 msb bits
            PIX_FMT_BGR4_BYTE = 21, ///< packed RGB 1:2:1,  8bpp, (msb)1B 2G 1R(lsb)
            PIX_FMT_RGB8      = 22, ///< packed RGB 3:3:2,  8bpp, (msb)2R 3G 3B(lsb)
            PIX_FMT_RGB4      = 23, ///< packed RGB 1:2:1 bitstream,  4bpp, (msb)1R 2G 1B(lsb), a byte contains two pixels, the first pixel in the byte is the one composed by the 4 msb bits
            PIX_FMT_RGB4_BYTE = 24, ///< packed RGB 1:2:1,  8bpp, (msb)1R 2G 1B(lsb)
            PIX_FMT_NV12      = 25, ///< planar YUV 4:2:0, 12bpp, 1 plane for Y and 1 plane for the UV components, which are interleaved (first byte U and the following byte V)
            PIX_FMT_NV21      = 26, ///< as above, but U and V bytes are swapped

            PIX_FMT_ARGB      = 27, ///< packed ARGB 8:8:8:8, 32bpp, ARGBARGB...
            PIX_FMT_RGBA      = 28, ///< packed RGBA 8:8:8:8, 32bpp, RGBARGBA...
            PIX_FMT_ABGR      = 29, ///< packed ABGR 8:8:8:8, 32bpp, ABGRABGR...
            PIX_FMT_BGRA      = 30, ///< packed BGRA 8:8:8:8, 32bpp, BGRABGRA...

            PIX_FMT_GRAY16BE  = 31, ///<        Y        , 16bpp, big-endian
            PIX_FMT_GRAY16LE  = 32, ///<        Y        , 16bpp, little-endian
            PIX_FMT_YUV440P   = 33, ///< planar YUV 4:4:0 (1 Cr & Cb sample per 1x2 Y samples)
            PIX_FMT_YUVJ440P  = 34, ///< planar YUV 4:4:0 full scale (JPEG), deprecated in favor of PIX_FMT_YUV440P and setting color_range
            PIX_FMT_YUVA420P  = 35, ///< planar YUV 4:2:0, 20bpp, (1 Cr & Cb sample per 2x2 Y & A samples)
            PIX_FMT_VDPAU_H264 = 36,///< H.264 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
            PIX_FMT_VDPAU_MPEG1 = 37,///< MPEG-1 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
            PIX_FMT_VDPAU_MPEG2 = 38,///< MPEG-2 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
            PIX_FMT_VDPAU_WMV3 = 39,///< WMV3 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
            PIX_FMT_VDPAU_VC1 = 40, ///< VC-1 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
            PIX_FMT_RGB48BE   = 41, ///< packed RGB 16:16:16, 48bpp, 16R, 16G, 16B, the 2-byte value for each R/G/B component is stored as big-endian
            PIX_FMT_RGB48LE   = 42, ///< packed RGB 16:16:16, 48bpp, 16R, 16G, 16B, the 2-byte value for each R/G/B component is stored as little-endian

            PIX_FMT_RGB565BE  = 43, ///< packed RGB 5:6:5, 16bpp, (msb)   5R 6G 5B(lsb), big-endian
            PIX_FMT_RGB565LE  = 44, ///< packed RGB 5:6:5, 16bpp, (msb)   5R 6G 5B(lsb), little-endian
            PIX_FMT_RGB555BE  = 45, ///< packed RGB 5:5:5, 16bpp, (msb)1A 5R 5G 5B(lsb), big-endian, most significant bit to 0
            PIX_FMT_RGB555LE  = 46, ///< packed RGB 5:5:5, 16bpp, (msb)1A 5R 5G 5B(lsb), little-endian, most significant bit to 0

            PIX_FMT_BGR565BE  = 47, ///< packed BGR 5:6:5, 16bpp, (msb)   5B 6G 5R(lsb), big-endian
            PIX_FMT_BGR565LE  = 48, ///< packed BGR 5:6:5, 16bpp, (msb)   5B 6G 5R(lsb), little-endian
            PIX_FMT_BGR555BE  = 49, ///< packed BGR 5:5:5, 16bpp, (msb)1A 5B 5G 5R(lsb), big-endian, most significant bit to 1
            PIX_FMT_BGR555LE  = 50, ///< packed BGR 5:5:5, 16bpp, (msb)1A 5B 5G 5R(lsb), little-endian, most significant bit to 1

            PIX_FMT_VAAPI_MOCO = 51, ///< HW acceleration through VA API at motion compensation entry-point, Picture.data[3] contains a vaapi_render_state struct which contains macroblocks as well as various fields extracted from headers
            PIX_FMT_VAAPI_IDCT = 52, ///< HW acceleration through VA API at IDCT entry-point, Picture.data[3] contains a vaapi_render_state struct which contains fields extracted from headers
            PIX_FMT_VAAPI_VLD  = 53, ///< HW decoding through VA API, Picture.data[3] contains a vaapi_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers

            PIX_FMT_YUV420P16LE = 54, ///< planar YUV 4:2:0, 24bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
            PIX_FMT_YUV420P16BE = 55, ///< planar YUV 4:2:0, 24bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
            PIX_FMT_YUV422P16LE = 56, ///< planar YUV 4:2:2, 32bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
            PIX_FMT_YUV422P16BE = 57, ///< planar YUV 4:2:2, 32bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
            PIX_FMT_YUV444P16LE = 58, ///< planar YUV 4:4:4, 48bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
            PIX_FMT_YUV444P16BE = 59, ///< planar YUV 4:4:4, 48bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
            PIX_FMT_VDPAU_MPEG4 = 60, ///< MPEG4 HW decoding with VDPAU, data[0] contains a vdpau_render_state struct which contains the bitstream of the slices as well as various fields extracted from headers
            PIX_FMT_DXVA2_VLD   = 61, ///< HW decoding through DXVA2, Picture.data[3] contains a LPDIRECT3DSURFACE9 pointer

            PIX_FMT_RGB444LE  = 62, ///< packed RGB 4:4:4, 16bpp, (msb)4A 4R 4G 4B(lsb), little-endian, most significant bits to 0
            PIX_FMT_RGB444BE  = 63, ///< packed RGB 4:4:4, 16bpp, (msb)4A 4R 4G 4B(lsb), big-endian, most significant bits to 0
            PIX_FMT_BGR444LE  = 64, ///< packed BGR 4:4:4, 16bpp, (msb)4A 4B 4G 4R(lsb), little-endian, most significant bits to 1
            PIX_FMT_BGR444BE  = 65, ///< packed BGR 4:4:4, 16bpp, (msb)4A 4B 4G 4R(lsb), big-endian, most significant bits to 1
            PIX_FMT_GRAY8A    = 66, ///< 8bit gray, 8bit alpha
            PIX_FMT_BGR48BE   = 67, ///< packed RGB 16:16:16, 48bpp, 16B, 16G, 16R, the 2-byte value for each R/G/B component is stored as big-endian
            PIX_FMT_BGR48LE   = 68, ///< packed RGB 16:16:16, 48bpp, 16B, 16G, 16R, the 2-byte value for each R/G/B component is stored as little-endian

            //the following 10 formats have the disadvantage of needing 1 format for each bit depth, thus
            //If you want to support multiple bit depths, then using PIX_FMT_YUV420P16* with the bpp stored separately
            //is better
            PIX_FMT_YUV420P9BE  = 69, ///< planar YUV 4:2:0, 13.5bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
            PIX_FMT_YUV420P9LE  = 70, ///< planar YUV 4:2:0, 13.5bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
            PIX_FMT_YUV420P10BE = 71, ///< planar YUV 4:2:0, 15bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
            PIX_FMT_YUV420P10LE = 72, ///< planar YUV 4:2:0, 15bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
            PIX_FMT_YUV422P10BE = 73, ///< planar YUV 4:2:2, 20bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
            PIX_FMT_YUV422P10LE = 74, ///< planar YUV 4:2:2, 20bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
            PIX_FMT_YUV444P9BE  = 75, ///< planar YUV 4:4:4, 27bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
            PIX_FMT_YUV444P9LE  = 76, ///< planar YUV 4:4:4, 27bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
            PIX_FMT_YUV444P10BE = 77, ///< planar YUV 4:4:4, 30bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
            PIX_FMT_YUV444P10LE = 78, ///< planar YUV 4:4:4, 30bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
            PIX_FMT_YUV422P9BE  = 79, ///< planar YUV 4:2:2, 18bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
            PIX_FMT_YUV422P9LE  = 80, ///< planar YUV 4:2:2, 18bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
            PIX_FMT_VDA_VLD     = 81, ///< hardware decoding through VDA

            PIX_FMT_GBRP     = 86, ///< planar GBR 4:4:4 24bpp
            PIX_FMT_GBRP9BE  = 87, ///< planar GBR 4:4:4 27bpp, big endian
            PIX_FMT_GBRP9LE  = 88, ///< planar GBR 4:4:4 27bpp, little endian
            PIX_FMT_GBRP10BE = 89, ///< planar GBR 4:4:4 30bpp, big endian
            PIX_FMT_GBRP10LE = 90, ///< planar GBR 4:4:4 30bpp, little endian
            PIX_FMT_GBRP16BE = 91, ///< planar GBR 4:4:4 48bpp, big endian
            PIX_FMT_GBRP16LE = 92, ///< planar GBR 4:4:4 48bpp, little endian

            PIX_FMT_RGBA64BE=0x123,   ///< packed RGBA 16:16:16:16, 64bpp, 16R, 16G, 16B, 16A, the 2-byte value for each R/G/B/A component is stored as big-endian
            PIX_FMT_RGBA64LE=0x123+1, ///< packed RGBA 16:16:16:16, 64bpp, 16R, 16G, 16B, 16A, the 2-byte value for each R/G/B/A component is stored as little-endian
            PIX_FMT_BGRA64BE=0x123+2, ///< packed RGBA 16:16:16:16, 64bpp, 16B, 16G, 16R, 16A, the 2-byte value for each R/G/B/A component is stored as big-endian
            PIX_FMT_BGRA64LE=0x123+3, ///< packed RGBA 16:16:16:16, 64bpp, 16B, 16G, 16R, 16A, the 2-byte value for each R/G/B/A component is stored as little-endian
            PIX_FMT_0RGB=0x123+4,     ///< packed RGB 8:8:8, 32bpp, 0RGB0RGB...
            PIX_FMT_RGB0=0x123+5,     ///< packed RGB 8:8:8, 32bpp, RGB0RGB0...
            PIX_FMT_0BGR=0x123+6,     ///< packed BGR 8:8:8, 32bpp, 0BGR0BGR...
            PIX_FMT_BGR0=0x123+7,     ///< packed BGR 8:8:8, 32bpp, BGR0BGR0...
            PIX_FMT_YUVA444P=0x123+8, ///< planar YUV 4:4:4 32bpp, (1 Cr & Cb sample per 1x1 Y & A samples)
            PIX_FMT_YUVA422P=0x123+9, ///< planar YUV 4:2:2 24bpp, (1 Cr & Cb sample per 2x1 Y & A samples)

            PIX_FMT_YUV420P12BE=0x123+9,  ///< planar YUV 4:2:0,18bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
            PIX_FMT_YUV420P12LE=0x123+10, ///< planar YUV 4:2:0,18bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
            PIX_FMT_YUV420P14BE=0x123+11, ///< planar YUV 4:2:0,21bpp, (1 Cr & Cb sample per 2x2 Y samples), big-endian
            PIX_FMT_YUV420P14LE=0x123+12, ///< planar YUV 4:2:0,21bpp, (1 Cr & Cb sample per 2x2 Y samples), little-endian
            PIX_FMT_YUV422P12BE=0x123+13, ///< planar YUV 4:2:2,24bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
            PIX_FMT_YUV422P12LE=0x123+14, ///< planar YUV 4:2:2,24bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
            PIX_FMT_YUV422P14BE=0x123+15, ///< planar YUV 4:2:2,28bpp, (1 Cr & Cb sample per 2x1 Y samples), big-endian
            PIX_FMT_YUV422P14LE=0x123+16, ///< planar YUV 4:2:2,28bpp, (1 Cr & Cb sample per 2x1 Y samples), little-endian
            PIX_FMT_YUV444P12BE=0x123+17, ///< planar YUV 4:4:4,36bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
            PIX_FMT_YUV444P12LE=0x123+18, ///< planar YUV 4:4:4,36bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
            PIX_FMT_YUV444P14BE=0x123+19, ///< planar YUV 4:4:4,42bpp, (1 Cr & Cb sample per 1x1 Y samples), big-endian
            PIX_FMT_YUV444P14LE=0x123+20, ///< planar YUV 4:4:4,42bpp, (1 Cr & Cb sample per 1x1 Y samples), little-endian
            PIX_FMT_GBRP12BE=0x123+21,    ///< planar GBR 4:4:4 36bpp, big endian
            PIX_FMT_GBRP12LE=0x123+22,    ///< planar GBR 4:4:4 36bpp, little endian
            PIX_FMT_GBRP14BE=0x123+23,    ///< planar GBR 4:4:4 42bpp, big endian
            PIX_FMT_GBRP14LE=0x123+24,    ///< planar GBR 4:4:4 42bpp, little endian

            PIX_FMT_Y400A  = PIX_FMT_GRAY8A,
            PIX_FMT_GBR24P = PIX_FMT_GBRP,

            PIX_FMT_RGB32   = AV_HAVE_BIGENDIAN() ? PIX_FMT_ARGB : PIX_FMT_BGRA,
            PIX_FMT_RGB32_1 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGBA : PIX_FMT_ABGR,
            PIX_FMT_BGR32   = AV_HAVE_BIGENDIAN() ? PIX_FMT_ABGR : PIX_FMT_RGBA,
            PIX_FMT_BGR32_1 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGRA : PIX_FMT_ARGB,
            PIX_FMT_0RGB32  = AV_HAVE_BIGENDIAN() ? PIX_FMT_0RGB : PIX_FMT_0BGR,
            PIX_FMT_0BGR32  = AV_HAVE_BIGENDIAN() ? PIX_FMT_0BGR : PIX_FMT_0RGB,

            PIX_FMT_GRAY16 = AV_HAVE_BIGENDIAN() ? PIX_FMT_GRAY16BE : PIX_FMT_GRAY16LE,
            PIX_FMT_RGB48  = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB48BE  : PIX_FMT_RGB48LE,
            PIX_FMT_RGB565 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB565BE : PIX_FMT_RGB565LE,
            PIX_FMT_RGB555 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB555BE : PIX_FMT_RGB555LE,
            PIX_FMT_RGB444 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGB444BE : PIX_FMT_RGB444LE,
            PIX_FMT_BGR48  = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR48BE  : PIX_FMT_BGR48LE,
            PIX_FMT_BGR565 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR565BE : PIX_FMT_BGR565LE,
            PIX_FMT_BGR555 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR555BE : PIX_FMT_BGR555LE,
            PIX_FMT_BGR444 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGR444BE : PIX_FMT_BGR444LE,

            PIX_FMT_YUV420P9  = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV420P9BE  : PIX_FMT_YUV420P9LE,
            PIX_FMT_YUV422P9  = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV422P9BE  : PIX_FMT_YUV422P9LE,
            PIX_FMT_YUV444P9  = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV444P9BE  : PIX_FMT_YUV444P9LE,
            PIX_FMT_YUV420P10 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV420P10BE : PIX_FMT_YUV420P10LE,
            PIX_FMT_YUV422P10 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV422P10BE : PIX_FMT_YUV422P10LE,
            PIX_FMT_YUV444P10 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV444P10BE : PIX_FMT_YUV444P10LE,
            PIX_FMT_YUV420P12 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV420P12BE : PIX_FMT_YUV420P12LE,
            PIX_FMT_YUV422P12 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV422P12BE : PIX_FMT_YUV422P12LE,
            PIX_FMT_YUV444P12 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV444P12BE : PIX_FMT_YUV444P12LE,
            PIX_FMT_YUV420P14 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV420P14BE : PIX_FMT_YUV420P14LE,
            PIX_FMT_YUV422P14 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV422P14BE : PIX_FMT_YUV422P14LE,
            PIX_FMT_YUV444P14 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV444P14BE : PIX_FMT_YUV444P14LE,
            PIX_FMT_YUV420P16 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV420P16BE : PIX_FMT_YUV420P16LE,
            PIX_FMT_YUV422P16 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV422P16BE : PIX_FMT_YUV422P16LE,
            PIX_FMT_YUV444P16 = AV_HAVE_BIGENDIAN() ?  PIX_FMT_YUV444P16BE : PIX_FMT_YUV444P16LE,

            PIX_FMT_RGBA64 = AV_HAVE_BIGENDIAN() ? PIX_FMT_RGBA64BE : PIX_FMT_RGBA64LE,
            PIX_FMT_BGRA64 = AV_HAVE_BIGENDIAN() ? PIX_FMT_BGRA64BE : PIX_FMT_BGRA64LE,
            PIX_FMT_GBRP9  = AV_HAVE_BIGENDIAN() ? PIX_FMT_GBRP9BE  : PIX_FMT_GBRP9LE,
            PIX_FMT_GBRP10 = AV_HAVE_BIGENDIAN() ? PIX_FMT_GBRP10BE : PIX_FMT_GBRP10LE,
            PIX_FMT_GBRP12 = AV_HAVE_BIGENDIAN() ? PIX_FMT_GBRP12BE : PIX_FMT_GBRP12LE,
            PIX_FMT_GBRP14 = AV_HAVE_BIGENDIAN() ? PIX_FMT_GBRP14BE : PIX_FMT_GBRP14LE,
            PIX_FMT_GBRP16 = AV_HAVE_BIGENDIAN() ? PIX_FMT_GBRP16BE : PIX_FMT_GBRP16LE;


    /**
     * Return x default pointer in case p is NULL.
     */
    public static native Pointer av_x_if_null(Pointer p, Pointer x);

    /**
     * @}
     * @}
     */


    // #include "audioconvert.h"
    /**
     * @file
     * audio conversion routines
     */

    /**
     * @addtogroup lavu_audio
     * @{
     */

    /**
     * @defgroup channel_masks Audio channel masks
     * @{
     */
    public static final int
            AV_CH_FRONT_LEFT            = 0x00000001,
            AV_CH_FRONT_RIGHT           = 0x00000002,
            AV_CH_FRONT_CENTER          = 0x00000004,
            AV_CH_LOW_FREQUENCY         = 0x00000008,
            AV_CH_BACK_LEFT             = 0x00000010,
            AV_CH_BACK_RIGHT            = 0x00000020,
            AV_CH_FRONT_LEFT_OF_CENTER  = 0x00000040,
            AV_CH_FRONT_RIGHT_OF_CENTER = 0x00000080,
            AV_CH_BACK_CENTER           = 0x00000100,
            AV_CH_SIDE_LEFT             = 0x00000200,
            AV_CH_SIDE_RIGHT            = 0x00000400,
            AV_CH_TOP_CENTER            = 0x00000800,
            AV_CH_TOP_FRONT_LEFT        = 0x00001000,
            AV_CH_TOP_FRONT_CENTER      = 0x00002000,
            AV_CH_TOP_FRONT_RIGHT       = 0x00004000,
            AV_CH_TOP_BACK_LEFT         = 0x00008000,
            AV_CH_TOP_BACK_CENTER       = 0x00010000,
            AV_CH_TOP_BACK_RIGHT        = 0x00020000,
            AV_CH_STEREO_LEFT           = 0x20000000, ///< Stereo downmix.
            AV_CH_STEREO_RIGHT          = 0x40000000; ///< See AV_CH_STEREO_LEFT.

    public static final long
            AV_CH_WIDE_LEFT             = 0x0000000080000000L,
            AV_CH_WIDE_RIGHT            = 0x0000000100000000L,
            AV_CH_SURROUND_DIRECT_LEFT  = 0x0000000200000000L,
            AV_CH_SURROUND_DIRECT_RIGHT = 0x0000000400000000L,

    /** Channel mask value used for AVCodecContext.request_channel_layout
        to indicate that the user requests the channel order of the decoder output
        to be the native codec channel order. */
            AV_CH_LAYOUT_NATIVE         = 0x8000000000000000L;

    /**
     * @}
     * @defgroup channel_mask_c Audio channel convenience macros
     * @{
     * */
    public static final int
            AV_CH_LAYOUT_MONO              = (AV_CH_FRONT_CENTER),
            AV_CH_LAYOUT_STEREO            = (AV_CH_FRONT_LEFT|AV_CH_FRONT_RIGHT),
            AV_CH_LAYOUT_2POINT1           = (AV_CH_LAYOUT_STEREO|AV_CH_LOW_FREQUENCY),
            AV_CH_LAYOUT_2_1               = (AV_CH_LAYOUT_STEREO|AV_CH_BACK_CENTER),
            AV_CH_LAYOUT_SURROUND          = (AV_CH_LAYOUT_STEREO|AV_CH_FRONT_CENTER),
            AV_CH_LAYOUT_3POINT1           = (AV_CH_LAYOUT_SURROUND|AV_CH_LOW_FREQUENCY),
            AV_CH_LAYOUT_4POINT0           = (AV_CH_LAYOUT_SURROUND|AV_CH_BACK_CENTER),
            AV_CH_LAYOUT_4POINT1           = (AV_CH_LAYOUT_4POINT0|AV_CH_LOW_FREQUENCY),
            AV_CH_LAYOUT_2_2               = (AV_CH_LAYOUT_STEREO|AV_CH_SIDE_LEFT|AV_CH_SIDE_RIGHT),
            AV_CH_LAYOUT_QUAD              = (AV_CH_LAYOUT_STEREO|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT),
            AV_CH_LAYOUT_5POINT0           = (AV_CH_LAYOUT_SURROUND|AV_CH_SIDE_LEFT|AV_CH_SIDE_RIGHT),
            AV_CH_LAYOUT_5POINT1           = (AV_CH_LAYOUT_5POINT0|AV_CH_LOW_FREQUENCY),
            AV_CH_LAYOUT_5POINT0_BACK      = (AV_CH_LAYOUT_SURROUND|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT),
            AV_CH_LAYOUT_5POINT1_BACK      = (AV_CH_LAYOUT_5POINT0_BACK|AV_CH_LOW_FREQUENCY),
            AV_CH_LAYOUT_6POINT0           = (AV_CH_LAYOUT_5POINT0|AV_CH_BACK_CENTER),
            AV_CH_LAYOUT_6POINT0_FRONT     = (AV_CH_LAYOUT_2_2|AV_CH_FRONT_LEFT_OF_CENTER|AV_CH_FRONT_RIGHT_OF_CENTER),
            AV_CH_LAYOUT_HEXAGONAL         = (AV_CH_LAYOUT_5POINT0_BACK|AV_CH_BACK_CENTER),
            AV_CH_LAYOUT_6POINT1           = (AV_CH_LAYOUT_5POINT1|AV_CH_BACK_CENTER),
            AV_CH_LAYOUT_6POINT1_BACK      = (AV_CH_LAYOUT_5POINT1_BACK|AV_CH_BACK_CENTER),
            AV_CH_LAYOUT_6POINT1_FRONT     = (AV_CH_LAYOUT_6POINT0_FRONT|AV_CH_LOW_FREQUENCY),
            AV_CH_LAYOUT_7POINT0           = (AV_CH_LAYOUT_5POINT0|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT),
            AV_CH_LAYOUT_7POINT0_FRONT     = (AV_CH_LAYOUT_5POINT0|AV_CH_FRONT_LEFT_OF_CENTER|AV_CH_FRONT_RIGHT_OF_CENTER),
            AV_CH_LAYOUT_7POINT1           = (AV_CH_LAYOUT_5POINT1|AV_CH_BACK_LEFT|AV_CH_BACK_RIGHT),
            AV_CH_LAYOUT_7POINT1_WIDE      = (AV_CH_LAYOUT_5POINT1|AV_CH_FRONT_LEFT_OF_CENTER|AV_CH_FRONT_RIGHT_OF_CENTER),
            AV_CH_LAYOUT_7POINT1_WIDE_BACK = (AV_CH_LAYOUT_5POINT1_BACK|AV_CH_FRONT_LEFT_OF_CENTER|AV_CH_FRONT_RIGHT_OF_CENTER),
            AV_CH_LAYOUT_OCTAGONAL         = (AV_CH_LAYOUT_5POINT0|AV_CH_BACK_LEFT|AV_CH_BACK_CENTER|AV_CH_BACK_RIGHT),
            AV_CH_LAYOUT_STEREO_DOWNMIX    = (AV_CH_STEREO_LEFT|AV_CH_STEREO_RIGHT);

    public static final int // enum AVMatrixEncoding {
            AV_MATRIX_ENCODING_NONE  = 0,
            AV_MATRIX_ENCODING_DOLBY = 1,
            AV_MATRIX_ENCODING_DPLII = 2,
            AV_MATRIX_ENCODING_NB    = 3;

    /**
     * @}
     */

    /**
     * Return a channel layout id that matches name, or 0 if no match is found.
     *
     * name can be one or several of the following notations,
     * separated by '+' or '|':
     * - the name of an usual channel layout (mono, stereo, 4.0, quad, 5.0,
     *   5.0(side), 5.1, 5.1(side), 7.1, 7.1(wide), downmix);
     * - the name of a single channel (FL, FR, FC, LFE, BL, BR, FLC, FRC, BC,
     *   SL, SR, TC, TFL, TFC, TFR, TBL, TBC, TBR, DL, DR);
     * - a number of channels, in decimal, optionally followed by 'c', yielding
     *   the default channel layout for that number of channels (@see
     *   av_get_default_channel_layout);
     * - a channel layout mask, in hexadecimal starting with "0x" (see the
     *   AV_CH_* macros).
     *
     * Example: "stereo+FC" = "2+FC" = "2c+1c" = "0x7"
     */
    public static native long av_get_channel_layout(String name);

    /**
     * Return a description of a channel layout.
     * If nb_channels is <= 0, it is guessed from the channel_layout.
     *
     * @param buf put here the string containing the channel layout
     * @param buf_size size in bytes of the buffer
     */
    public static native void av_get_channel_layout_string(@Cast("char*") BytePointer buf, int buf_size,
            int nb_channels, long channel_layout);

    @Opaque public static class AVBPrint extends Pointer {
        static { load(); }
        public AVBPrint() { }
        public AVBPrint(Pointer p) { super(p); }
    }
    /**
     * Append a description of a channel layout to a bprint buffer.
     */
    public static native void av_bprint_channel_layout(AVBPrint bp, int nb_channels, long channel_layout);

    /**
     * Return the number of channels in the channel layout.
     */
    public static native int av_get_channel_layout_nb_channels(long channel_layout);

    /**
     * Return default channel layout for a given number of channels.
     */
    public static native long av_get_default_channel_layout(int nb_channels);

    /**
     * Get the index of a channel in channel_layout.
     *
     * @param channel a channel layout describing exactly one channel which must be
     *                present in channel_layout.
     *
     * @return index of channel in channel_layout on success, a negative AVERROR
     *         on error.
     */
    public static native int av_get_channel_layout_channel_index(long channel_layout, long channel);

    /**
     * Get the channel with the given index in channel_layout.
     */
    public static native long av_channel_layout_extract_channel(long channel_layout, int index);

    /**
     * Get the name of a given channel.
     *
     * @return channel name on success, NULL on error.
     */
    public static native String av_get_channel_name(long channel);

    /**
     * Get the description of a given channel.
     *
     * @param channel  a channel layout with a single channel
     * @return  channel description on success, NULL on error
     */
    public static native String av_get_channel_description(long channel);

    /**
     * Get the value and name of a standard channel layout.
     *
     * @param[in]  index   index in an internal list, starting at 0
     * @param[out] layout  channel layout mask
     * @param[out] name    name of the layout
     * @return  0  if the layout exists,
     *          <0 if index is beyond the limits
     */
    public static native int av_get_standard_channel_layout(@Cast("unsigned") int index,
            @Cast("uint64_t*") long[] layout, @Cast("const char**") @ByPtrPtr BytePointer name);

    /**
     * @}
     */


    // #include "cpu.h"
    public static final int
            AV_CPU_FLAG_FORCE    = 0x80000000, /* force usage of selected flags (OR) */

            /* lower 16 bits - CPU features */
            AV_CPU_FLAG_MMX          = 0x0001, ///< standard MMX
            AV_CPU_FLAG_MMXEXT       = 0x0002, ///< SSE integer functions or AMD MMX ext
            AV_CPU_FLAG_3DNOW        = 0x0004, ///< AMD 3DNOW
            AV_CPU_FLAG_SSE          = 0x0008, ///< SSE functions
            AV_CPU_FLAG_SSE2         = 0x0010, ///< PIV SSE2 functions
            AV_CPU_FLAG_SSE2SLOW = 0x40000000, ///< SSE2 supported, but usually not faster
            AV_CPU_FLAG_3DNOWEXT     = 0x0020, ///< AMD 3DNowExt
            AV_CPU_FLAG_SSE3         = 0x0040, ///< Prescott SSE3 functions
            AV_CPU_FLAG_SSE3SLOW = 0x20000000, ///< SSE3 supported, but usually not faster
            AV_CPU_FLAG_SSSE3        = 0x0080, ///< Conroe SSSE3 functions
            AV_CPU_FLAG_ATOM     = 0x10000000, ///< Atom processor, some SSSE3 instructions are slower
            AV_CPU_FLAG_SSE4         = 0x0100, ///< Penryn SSE4.1 functions
            AV_CPU_FLAG_SSE42        = 0x0200, ///< Nehalem SSE4.2 functions
            AV_CPU_FLAG_AVX          = 0x4000, ///< AVX functions: requires OS support even if YMM registers aren't used
            AV_CPU_FLAG_CMOV         = 0x1000, ///< supports cmov instruction
            AV_CPU_FLAG_XOP          = 0x0400, ///< Bulldozer XOP functions
            AV_CPU_FLAG_FMA4         = 0x0800, ///< Bulldozer FMA4 functions
            AV_CPU_FLAG_ALTIVEC      = 0x0001, ///< standard

            AV_CPU_FLAG_ARMV5TE      = (1 << 0),
            AV_CPU_FLAG_ARMV6        = (1 << 1),
            AV_CPU_FLAG_ARMV6T2      = (1 << 2),
            AV_CPU_FLAG_VFP          = (1 << 3),
            AV_CPU_FLAG_VFPV3        = (1 << 4),
            AV_CPU_FLAG_NEON         = (1 << 5);

    /**
     * Return the flags which specify extensions supported by the CPU.
     */
    public static native int av_get_cpu_flags();

    /**
     * Disables cpu detection and forces the specified flags.
     * -1 is a special case that disables forcing of specific flags.
     */
    public static native void av_force_cpu_flags(int flags);

    /**
     * Parse CPU flags from a string.
     *
     * The returned flags contain the specified flags as well as related unspecified flags.
     *
     * This function exists only for compatibility with libav.
     * Please use av_parse_cpu_caps() when possible.
     * @return a combination of AV_CPU_* flags, negative on error.
     */
    @Deprecated
    public static native int av_parse_cpu_flags(String s);

    /**
     * Parse CPU caps from a string and update the given AV_CPU_* flags based on that.
     *
     * @return negative on error.
     */
    public static native int av_parse_cpu_caps(@Cast("unsigned*") int[] flags, String s);


    // #include "dict.h"
    /**
     * @file
     * Public dictionary API.
     * @deprecated
     *  AVDictionary is provided for compatibility with libav. It is both in
     *  implementation as well as API inefficient. It does not scale and is
     *  extremely slow with large dictionaries.
     *  It is recommended that new code uses our tree container from tree.c/h
     *  where applicable, which uses AVL trees to achieve O(log n) performance.
     */

    /**
     * @addtogroup lavu_dict AVDictionary
     * @ingroup lavu_data
     *
     * @brief Simple key:value store
     *
     * @{
     * Dictionaries are used for storing key:value pairs. To create
     * an AVDictionary, simply pass an address of a NULL pointer to
     * av_dict_set(). NULL can be used as an empty dictionary wherever
     * a pointer to an AVDictionary is required.
     * Use av_dict_get() to retrieve an entry or iterate over all
     * entries and finally av_dict_free() to free the dictionary
     * and all its contents.
     *
     * @code
     * AVDictionary *d = NULL;                // "create" an empty dictionary
     * av_dict_set(&d, "foo", "bar", 0);      // add an entry
     *
     * char *k = av_strdup("key");            // if your strings are already allocated,
     * char *v = av_strdup("value");          // you can avoid copying them like this
     * av_dict_set(&d, k, v, AV_DICT_DONT_STRDUP_KEY | AV_DICT_DONT_STRDUP_VAL);
     *
     * AVDictionaryEntry *t = NULL;
     * while (t = av_dict_get(d, "", t, AV_DICT_IGNORE_SUFFIX)) {
     *     <....>                             // iterate over all entries in d
     * }
     *
     * av_dict_free(&d);
     * @endcode
     *
     */

    public static final int
            AV_DICT_MATCH_CASE      = 1,
            AV_DICT_IGNORE_SUFFIX   = 2,
            AV_DICT_DONT_STRDUP_KEY = 4,   /**< Take ownership of a key that's been
                                                allocated with av_malloc() and children. */
            AV_DICT_DONT_STRDUP_VAL = 8,   /**< Take ownership of a value that's been
                                                allocated with av_malloc() and chilren. */
            AV_DICT_DONT_OVERWRITE  = 16,  ///< Don't overwrite existing entries.
            AV_DICT_APPEND          = 32;  /**< If the entry already exists, append to it.  Note that no
                                                delimiter is added, the strings are simply concatenated. */

    public static class AVDictionaryEntry extends Pointer {
        static { load(); }
        public AVDictionaryEntry() { allocate(); }
        public AVDictionaryEntry(int size) { allocateArray(size); }
        public AVDictionaryEntry(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVDictionaryEntry position(int position) {
            return (AVDictionaryEntry)super.position(position);
        }

        public native @Cast("char*") BytePointer key();   public native AVDictionaryEntry key(BytePointer key);
        public native @Cast("char*") BytePointer value(); public native AVDictionaryEntry value(BytePointer value);
    }

    @Opaque public static class AVDictionary extends Pointer {
        public AVDictionary() { }
        public AVDictionary(Pointer p) { super(p); }
    }

    /**
     * Get a dictionary entry with matching key.
     *
     * @param prev Set to the previous matching element to find the next.
     *             If set to NULL the first matching element is returned.
     * @param flags Allows case as well as suffix-insensitive comparisons.
     * @return Found entry or NULL, changing key or value leads to undefined behavior.
     */
    public static native AVDictionaryEntry av_dict_get(AVDictionary m, String key, AVDictionaryEntry prev, int flags);

    /**
     * Get number of entries in dictionary.
     *
     * @param m dictionary
     * @return  number of entries in dictionary
     */
    public static native int av_dict_count(AVDictionary m);

    /**
     * Set the given entry in *pm, overwriting an existing entry.
     *
     * @param pm pointer to a pointer to a dictionary struct. If *pm is NULL
     * a dictionary struct is allocated and put in *pm.
     * @param key entry key to add to *pm (will be av_strduped depending on flags)
     * @param value entry value to add to *pm (will be av_strduped depending on flags).
     *        Passing a NULL value will cause an existing entry to be deleted.
     * @return >= 0 on success otherwise an error code <0
     */
    public static native int av_dict_set(@ByPtrPtr AVDictionary pm, String key, String value, int flags);

    /**
     * Copy entries from one AVDictionary struct into another.
     * @param dst pointer to a pointer to a AVDictionary struct. If *dst is NULL,
     *            this function will allocate a struct for you and put it in *dst
     * @param src pointer to source AVDictionary struct
     * @param flags flags to use when setting entries in *dst
     * @note metadata is read using the AV_DICT_IGNORE_SUFFIX flag
     */
    public static native void av_dict_copy(@ByPtrPtr AVDictionary dst, AVDictionary src, int flags);

    /**
     * Free all the memory allocated for an AVDictionary struct
     * and all keys and values.
     */
    public static native void av_dict_free(@ByPtrPtr AVDictionary m);

    /**
     * @}
     */


    // #include "opt.h"
    /**
     * @file
     * AVOptions
     */

    /**
     * @defgroup avoptions AVOptions
     * @ingroup lavu_data
     * @{
     * AVOptions provide a generic system to declare options on arbitrary structs
     * ("objects"). An option can have a help text, a type and a range of possible
     * values. Options may then be enumerated, read and written to.
     *
     * @section avoptions_implement Implementing AVOptions
     * This section describes how to add AVOptions capabilities to a struct.
     *
     * All AVOptions-related information is stored in an AVClass. Therefore
     * the first member of the struct should be a pointer to an AVClass describing it.
     * The option field of the AVClass must be set to a NULL-terminated static array
     * of AVOptions. Each AVOption must have a non-empty name, a type, a default
     * value and for number-type AVOptions also a range of allowed values. It must
     * also declare an offset in bytes from the start of the struct, where the field
     * associated with this AVOption is located. Other fields in the AVOption struct
     * should also be set when applicable, but are not required.
     *
     * The following example illustrates an AVOptions-enabled struct:
     * @code
     * typedef struct test_struct {
     *     AVClass *class;
     *     int      int_opt;
     *     char    *str_opt;
     *     uint8_t *bin_opt;
     *     int      bin_len;
     * } test_struct;
     *
     * static const AVOption options[] = {
     *   { "test_int", "This is a test option of int type.", offsetof(test_struct, int_opt),
     *     AV_OPT_TYPE_INT, { .i64 = -1 }, INT_MIN, INT_MAX },
     *   { "test_str", "This is a test option of string type.", offsetof(test_struct, str_opt),
     *     AV_OPT_TYPE_STRING },
     *   { "test_bin", "This is a test option of binary type.", offsetof(test_struct, bin_opt),
     *     AV_OPT_TYPE_BINARY },
     *   { NULL },
     * };
     *
     * static const AVClass test_class = {
     *     .class_name = "test class",
     *     .item_name  = av_default_item_name,
     *     .option     = options,
     *     .version    = LIBAVUTIL_VERSION_INT,
     * };
     * @endcode
     *
     * Next, when allocating your struct, you must ensure that the AVClass pointer
     * is set to the correct value. Then, av_opt_set_defaults() can be called to
     * initialize defaults. After that the struct is ready to be used with the
     * AVOptions API.
     *
     * When cleaning up, you may use the av_opt_free() function to automatically
     * free all the allocated string and binary options.
     *
     * Continuing with the above example:
     *
     * @code
     * test_struct *alloc_test_struct(void)
     * {
     *     test_struct *ret = av_malloc(sizeof(*ret));
     *     ret->class = &test_class;
     *     av_opt_set_defaults(ret);
     *     return ret;
     * }
     * void free_test_struct(test_struct **foo)
     * {
     *     av_opt_free(*foo);
     *     av_freep(foo);
     * }
     * @endcode
     *
     * @subsection avoptions_implement_nesting Nesting
     *      It may happen that an AVOptions-enabled struct contains another
     *      AVOptions-enabled struct as a member (e.g. AVCodecContext in
     *      libavcodec exports generic options, while its priv_data field exports
     *      codec-specific options). In such a case, it is possible to set up the
     *      parent struct to export a child's options. To do that, simply
     *      implement AVClass.child_next() and AVClass.child_class_next() in the
     *      parent struct's AVClass.
     *      Assuming that the test_struct from above now also contains a
     *      child_struct field:
     *
     *      @code
     *      typedef struct child_struct {
     *          AVClass *class;
     *          int flags_opt;
     *      } child_struct;
     *      static const AVOption child_opts[] = {
     *          { "test_flags", "This is a test option of flags type.",
     *            offsetof(child_struct, flags_opt), AV_OPT_TYPE_FLAGS, { .i64 = 0 }, INT_MIN, INT_MAX },
     *          { NULL },
     *      };
     *      static const AVClass child_class = {
     *          .class_name = "child class",
     *          .item_name  = av_default_item_name,
     *          .option     = child_opts,
     *          .version    = LIBAVUTIL_VERSION_INT,
     *      };
     *
     *      void *child_next(void *obj, void *prev)
     *      {
     *          test_struct *t = obj;
     *          if (!prev && t->child_struct)
     *              return t->child_struct;
     *          return NULL
     *      }
     *      const AVClass child_class_next(const AVClass *prev)
     *      {
     *          return prev ? NULL : &child_class;
     *      }
     *      @endcode
     *      Putting child_next() and child_class_next() as defined above into
     *      test_class will now make child_struct's options accessible through
     *      test_struct (again, proper setup as described above needs to be done on
     *      child_struct right after it is created).
     *
     *      From the above example it might not be clear why both child_next()
     *      and child_class_next() are needed. The distinction is that child_next()
     *      iterates over actually existing objects, while child_class_next()
     *      iterates over all possible child classes. E.g. if an AVCodecContext
     *      was initialized to use a codec which has private options, then its
     *      child_next() will return AVCodecContext.priv_data and finish
     *      iterating. OTOH child_class_next() on AVCodecContext.av_class will
     *      iterate over all available codecs with private options.
     *
     * @subsection avoptions_implement_named_constants Named constants
     *      It is possible to create named constants for options. Simply set the unit
     *      field of the option the constants should apply to to a string and
     *      create the constants themselves as options of type AV_OPT_TYPE_CONST
     *      with their unit field set to the same string.
     *      Their default_val field should contain the value of the named
     *      constant.
     *      For example, to add some named constants for the test_flags option
     *      above, put the following into the child_opts array:
     *      @code
     *      { "test_flags", "This is a test option of flags type.",
     *        offsetof(child_struct, flags_opt), AV_OPT_TYPE_FLAGS, { .i64 = 0 }, INT_MIN, INT_MAX, "test_unit" },
     *      { "flag1", "This is a flag with value 16", 0, AV_OPT_TYPE_CONST, { .i64 = 16 }, 0, 0, "test_unit" },
     *      @endcode
     *
     * @section avoptions_use Using AVOptions
     * This section deals with accessing options in an AVOptions-enabled struct.
     * Such structs in FFmpeg are e.g. AVCodecContext in libavcodec or
     * AVFormatContext in libavformat.
     *
     * @subsection avoptions_use_examine Examining AVOptions
     * The basic functions for examining options are av_opt_next(), which iterates
     * over all options defined for one object, and av_opt_find(), which searches
     * for an option with the given name.
     *
     * The situation is more complicated with nesting. An AVOptions-enabled struct
     * may have AVOptions-enabled children. Passing the AV_OPT_SEARCH_CHILDREN flag
     * to av_opt_find() will make the function search children recursively.
     *
     * For enumerating there are basically two cases. The first is when you want to
     * get all options that may potentially exist on the struct and its children
     * (e.g.  when constructing documentation). In that case you should call
     * av_opt_child_class_next() recursively on the parent struct's AVClass.  The
     * second case is when you have an already initialized struct with all its
     * children and you want to get all options that can be actually written or read
     * from it. In that case you should call av_opt_child_next() recursively (and
     * av_opt_next() on each result).
     *
     * @subsection avoptions_use_get_set Reading and writing AVOptions
     * When setting options, you often have a string read directly from the
     * user. In such a case, simply passing it to av_opt_set() is enough. For
     * non-string type options, av_opt_set() will parse the string according to the
     * option type.
     *
     * Similarly av_opt_get() will read any option type and convert it to a string
     * which will be returned. Do not forget that the string is allocated, so you
     * have to free it with av_free().
     *
     * In some cases it may be more convenient to put all options into an
     * AVDictionary and call av_opt_set_dict() on it. A specific case of this
     * are the format/codec open functions in lavf/lavc which take a dictionary
     * filled with option as a parameter. This allows to set some options
     * that cannot be set otherwise, since e.g. the input file format is not known
     * before the file is actually opened.
     */

    public static final int // enum AVOptionType {
        AV_OPT_TYPE_FLAGS    = 0,
        AV_OPT_TYPE_INT      = 1,
        AV_OPT_TYPE_INT64    = 2,
        AV_OPT_TYPE_DOUBLE   = 3,
        AV_OPT_TYPE_FLOAT    = 4,
        AV_OPT_TYPE_STRING   = 5,
        AV_OPT_TYPE_RATIONAL = 6,
        AV_OPT_TYPE_BINARY   = 7,  ///< offset must point to a pointer immediately followed by an int for the length
        AV_OPT_TYPE_CONST    = 128,
        AV_OPT_TYPE_IMAGE_SIZE = MKBETAG('S','I','Z','E'), ///< offset must point to two consecutive integers
        AV_OPT_TYPE_PIXEL_FMT  = MKBETAG('P','F','M','T');


    /**
     * AVOption
     */
    public static class AVOption extends Pointer {
        static { load(); }
        public AVOption() { allocate(); }
        public AVOption(int size) { allocateArray(size); }
        public AVOption(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVOption position(int position) {
            return (AVOption)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer name();   public native AVOption name(BytePointer name);

        /**
         * short English help text
         * @todo What about other languages?
         */
        @Cast("const char*")
        public native BytePointer help();   public native AVOption help(BytePointer help);

        /**
         * The offset relative to the context structure where the option
         * value is stored. It should be 0 for named constants.
         */
        public native int offset();         public native AVOption offset(int offset);
        @Cast("AVOptionType")
        public native int type();           public native AVOption type(int type);

        /**
         * the default value for scalar options
         */
        @Name("default_val.i64")
        public native long default_val_i64();        public native AVOption default_val_i64(long default_val_str);
        @Name("default_val.dbl") 
        public native double default_val_dbl();      public native AVOption default_val_dbl(double default_val_dbl);
        @Name("default_val.str") @Cast("const char*")
        public native BytePointer default_val_str(); public native AVOption default_val_str(BytePointer default_val_str);
        /* TODO those are unused now */
        @Name("default_val.q") @ByVal
        public native AVRational default_val_q();    public native AVOption default_val_q(AVRational default_val_q);

        public native double min();         public native AVOption min(double min); ///< minimum valid value for the option
        public native double max();         public native AVOption max(double max); ///< maximum valid value for the option

        public native int flags();          public native AVOption flags(int flags);
        public static final int
                AV_OPT_FLAG_ENCODING_PARAM = 1, ///< a generic parameter which can be set by the user for muxing or encoding
                AV_OPT_FLAG_DECODING_PARAM = 2, ///< a generic parameter which can be set by the user for demuxing or decoding
                AV_OPT_FLAG_METADATA       = 4, ///< some data extracted or inserted into the file like title, comment, ...
                AV_OPT_FLAG_AUDIO_PARAM    = 8,
                AV_OPT_FLAG_VIDEO_PARAM    = 16,
                AV_OPT_FLAG_SUBTITLE_PARAM = 32,
                AV_OPT_FLAG_FILTERING_PARAM = (1<<16); ///< a generic parameter which can be set by the user for filtering
        //FIXME think about enc-audio, ... style flags

        /**
         * The logical unit to which the option belongs. Non-constant
         * options and corresponding named constants share the same
         * unit. May be NULL.
         */
        @Cast("const char*")
        public native BytePointer unit();   public native AVOption unit(BytePointer unit);
    }

    /**
     * Show the obj options.
     *
     * @param req_flags requested flags for the options to show. Show only the
     * options for which it is opt->flags & req_flags.
     * @param rej_flags rejected flags for the options to show. Show only the
     * options for which it is !(opt->flags & req_flags).
     * @param av_log_obj log context to use for showing the options
     */
    public static native int av_opt_show2(Pointer obj, Pointer av_log_obj, int req_flags, int rej_flags);

    /**
     * Set the values of all AVOption fields to their default values.
     *
     * @param s an AVOption-enabled struct (its first member must be a pointer to AVClass)
     */
    public static native void av_opt_set_defaults(Pointer s);

    /**
     * Parse the key/value pairs list in opts. For each key/value pair
     * found, stores the value in the field in ctx that is named like the
     * key. ctx must be an AVClass context, storing is done using
     * AVOptions.
     *
     * @param opts options string to parse, may be NULL
     * @param key_val_sep a 0-terminated list of characters used to
     * separate key from value
     * @param pairs_sep a 0-terminated list of characters used to separate
     * two pairs from each other
     * @return the number of successfully set key/value pairs, or a negative
     * value corresponding to an AVERROR code in case of error:
     * AVERROR(EINVAL) if opts cannot be parsed,
     * the error code issued by av_set_string3() if a key/value pair
     * cannot be set
     */
    public static native int av_set_options_string(Pointer ctx, String opts, String key_val_sep, String pairs_sep);

    /**
     * Free all string and binary options in obj.
     */
    public static native void av_opt_free(Pointer obj);

    /**
     * Check whether a particular flag is set in a flags field.
     *
     * @param field_name the name of the flag field option
     * @param flag_name the name of the flag to check
     * @return non-zero if the flag is set, zero if the flag isn't set,
     *         isn't of the right type, or the flags field doesn't exist.
     */
    public static native int av_opt_flag_is_set(Pointer obj, String field_name, String flag_name);

    /*
     * Set all the options from a given dictionary on an object.
     *
     * @param obj a struct whose first element is a pointer to AVClass
     * @param options options to process. This dictionary will be freed and replaced
     *                by a new one containing all options not found in obj.
     *                Of course this new dictionary needs to be freed by caller
     *                with av_dict_free().
     *
     * @return 0 on success, a negative AVERROR if some option was found in obj,
     *         but could not be set.
     *
     * @see av_dict_copy()
     */
    public static native int av_opt_set_dict(Pointer obj, @ByPtrPtr AVDictionary options);

    /**
     * @defgroup opt_eval_funcs Evaluating option strings
     * @{
     * This group of functions can be used to evaluate option strings
     * and get numbers out of them. They do the same thing as av_opt_set(),
     * except the result is written into the caller-supplied pointer.
     *
     * @param obj a struct whose first element is a pointer to AVClass.
     * @param o an option for which the string is to be evaluated.
     * @param val string to be evaluated.
     * @param *_out value of the string will be written here.
     *
     * @return 0 on success, a negative number on failure.
     */
    public static native int av_opt_eval_flags (Pointer obj, AVOption o, String val, int[] flags_out);
    public static native int av_opt_eval_int   (Pointer obj, AVOption o, String val, int[] int_out);
    public static native int av_opt_eval_int64 (Pointer obj, AVOption o, String val, long[] int64_out);
    public static native int av_opt_eval_float (Pointer obj, AVOption o, String val, float[] float_out);
    public static native int av_opt_eval_double(Pointer obj, AVOption o, String val, double[] double_out);
    public static native int av_opt_eval_q     (Pointer obj, AVOption o, String val, AVRational q_out);
    /**
     * @}
     */

    public static final int AV_OPT_SEARCH_CHILDREN  = 0x0001; /**< Search in possible children of the
                                                                   given object first. */
    /**
     *  The obj passed to av_opt_find() is fake -- only a double pointer to AVClass
     *  instead of a required pointer to a struct containing AVClass. This is
     *  useful for searching for options without needing to allocate the corresponding
     *  object.
     */
    public static final int AV_OPT_SEARCH_FAKE_OBJ  = 0x0002;

    /**
     * Look for an option in an object. Consider only options which
     * have all the specified flags set.
     *
     * @param[in] obj A pointer to a struct whose first element is a
     *                pointer to an AVClass.
     *                Alternatively a double pointer to an AVClass, if
     *                AV_OPT_SEARCH_FAKE_OBJ search flag is set.
     * @param[in] name The name of the option to look for.
     * @param[in] unit When searching for named constants, name of the unit
     *                 it belongs to.
     * @param opt_flags Find only options with all the specified flags set (AV_OPT_FLAG).
     * @param search_flags A combination of AV_OPT_SEARCH_*.
     *
     * @return A pointer to the option found, or NULL if no option
     *         was found.
     *
     * @note Options found with AV_OPT_SEARCH_CHILDREN flag may not be settable
     * directly with av_set_string3(). Use special calls which take an options
     * AVDictionary (e.g. avformat_open_input()) to set options found with this
     * flag.
     */
    public static native @Const AVOption av_opt_find(Pointer obj, String name, String unit,
            int opt_flags, int search_flags);

    /**
     * Look for an option in an object. Consider only options which
     * have all the specified flags set.
     *
     * @param[in] obj A pointer to a struct whose first element is a
     *                pointer to an AVClass.
     *                Alternatively a double pointer to an AVClass, if
     *                AV_OPT_SEARCH_FAKE_OBJ search flag is set.
     * @param[in] name The name of the option to look for.
     * @param[in] unit When searching for named constants, name of the unit
     *                 it belongs to.
     * @param opt_flags Find only options with all the specified flags set (AV_OPT_FLAG).
     * @param search_flags A combination of AV_OPT_SEARCH_*.
     * @param[out] target_obj if non-NULL, an object to which the option belongs will be
     * written here. It may be different from obj if AV_OPT_SEARCH_CHILDREN is present
     * in search_flags. This parameter is ignored if search_flags contain
     * AV_OPT_SEARCH_FAKE_OBJ.
     *
     * @return A pointer to the option found, or NULL if no option
     *         was found.
     */
    public static native @Const AVOption av_opt_find2(Pointer obj, String name, String unit,
            int opt_flags, int search_flags, PointerPointer target_obj);

    /**
     * Iterate over all AVOptions belonging to obj.
     *
     * @param obj an AVOptions-enabled struct or a double pointer to an
     *            AVClass describing it.
     * @param prev result of the previous call to av_opt_next() on this object
     *             or NULL
     * @return next AVOption or NULL
     */
    public static native @Const AVOption av_opt_next(Pointer obj, AVOption prev);

    /**
     * Iterate over AVOptions-enabled children of obj.
     *
     * @param prev result of a previous call to this function or NULL
     * @return next AVOptions-enabled child or NULL
     */
    public static native Pointer av_opt_child_next(Pointer obj, Pointer prev);

    /**
     * Iterate over potential AVOptions-enabled children of parent.
     *
     * @param prev result of a previous call to this function or NULL
     * @return AVClass corresponding to next potential child or NULL
     */
    public static native @Const AVClass av_opt_child_class_next(AVClass parent, AVClass prev);

    /**
     * @defgroup opt_set_funcs Option setting functions
     * @{
     * Those functions set the field of obj with the given name to value.
     *
     * @param[in] obj A struct whose first element is a pointer to an AVClass.
     * @param[in] name the name of the field to set
     * @param[in] val The value to set. In case of av_opt_set() if the field is not
     * of a string type, then the given string is parsed.
     * SI postfixes and some named scalars are supported.
     * If the field is of a numeric type, it has to be a numeric or named
     * scalar. Behavior with more than one scalar and +- infix operators
     * is undefined.
     * If the field is of a flags type, it has to be a sequence of numeric
     * scalars or named flags separated by '+' or '-'. Prefixing a flag
     * with '+' causes it to be set without affecting the other flags;
     * similarly, '-' unsets a flag.
     * @param search_flags flags passed to av_opt_find2. I.e. if AV_OPT_SEARCH_CHILDREN
     * is passed here, then the option may be set on a child of obj.
     *
     * @return 0 if the value has been set, or an AVERROR code in case of
     * error:
     * AVERROR_OPTION_NOT_FOUND if no matching option exists
     * AVERROR(ERANGE) if the value is out of range
     * AVERROR(EINVAL) if the value is not valid
     */
    public static native int av_opt_set       (Pointer obj, String name, String             val, int search_flags);
    public static native int av_opt_set_int   (Pointer obj, String name, long               val, int search_flags);
    public static native int av_opt_set_double(Pointer obj, String name, double             val, int search_flags);
    public static native int av_opt_set_q     (Pointer obj, String name, @ByVal AVRational  val, int search_flags);
    public static native int av_opt_set_bin   (Pointer obj, String name, @Cast("uint8_t*") BytePointer val, int size, int search_flags);
    /**
     * @}
     */

    /**
     * @defgroup opt_get_funcs Option getting functions
     * @{
     * Those functions get a value of the option with the given name from an object.
     *
     * @param[in] obj a struct whose first element is a pointer to an AVClass.
     * @param[in] name name of the option to get.
     * @param[in] search_flags flags passed to av_opt_find2. I.e. if AV_OPT_SEARCH_CHILDREN
     * is passed here, then the option may be found in a child of obj.
     * @param[out] out_val value of the option will be written here
     * @return 0 on success, a negative error code otherwise
     */
    /**
     * @note the returned string will av_malloc()ed and must be av_free()ed by the caller
     */
    public static native int av_opt_get       (Pointer obj, String name, int search_flags, @Cast("uint8_t**") @ByPtrPtr BytePointer out_val);
    public static native int av_opt_get_int   (Pointer obj, String name, int search_flags, long[]     out_val);
    public static native int av_opt_get_double(Pointer obj, String name, int search_flags, double[]   out_val);
    public static native int av_opt_get_q     (Pointer obj, String name, int search_flags, AVRational out_val);
    /**
     * @}
     */
    /**
     * Gets a pointer to the requested field in a struct.
     * This function allows accessing a struct even when its fields are moved or
     * renamed since the application making the access has been compiled,
     *
     * @returns a pointer to the field, it can be cast to the correct type and read
     *          or written to.
     */
    public static native Pointer av_opt_ptr(AVClass avclass, Pointer obj, String name);
    /**
     * @}
     */


    // #include "samplefmt.h"
    /**
     * Audio Sample Formats
     *
     * @par
     * The data described by the sample format is always in native-endian order.
     * Sample values can be expressed by native C types, hence the lack of a signed
     * 24-bit sample format even though it is a common raw audio data format.
     *
     * @par
     * The floating-point formats are based on full volume being in the range
     * [-1.0, 1.0]. Any values outside this range are beyond full volume level.
     *
     * @par
     * The data layout as used in av_samples_fill_arrays() and elsewhere in Libav
     * (such as AVFrame in libavcodec) is as follows:
     *
     * For planar sample formats, each audio channel is in a separate data plane,
     * and linesize is the buffer size, in bytes, for a single plane. All data
     * planes must be the same size. For packed sample formats, only the first data
     * plane is used, and samples for each channel are interleaved. In this case,
     * linesize is the buffer size, in bytes, for the 1 plane.
     */
    public static final int // enum AVSampleFormat {
            AV_SAMPLE_FMT_NONE = -1,
            AV_SAMPLE_FMT_U8   =  0,   ///< unsigned 8 bits
            AV_SAMPLE_FMT_S16  =  1,   ///< signed 16 bits
            AV_SAMPLE_FMT_S32  =  2,   ///< signed 32 bits
            AV_SAMPLE_FMT_FLT  =  3,   ///< float
            AV_SAMPLE_FMT_DBL  =  4,   ///< double

            AV_SAMPLE_FMT_U8P  =  5,   ///< unsigned 8 bits, planar
            AV_SAMPLE_FMT_S16P =  6,   ///< signed 16 bits, planar
            AV_SAMPLE_FMT_S32P =  7,   ///< signed 32 bits, planar
            AV_SAMPLE_FMT_FLTP =  8,   ///< float, planar
            AV_SAMPLE_FMT_DBLP =  9;   ///< double, planar


    /**
     * Return the name of sample_fmt, or NULL if sample_fmt is not
     * recognized.
     */
    public static native String av_get_sample_fmt_name(@Cast("AVSampleFormat") int sample_fmt);

    /**
     * Return a sample format corresponding to name, or AV_SAMPLE_FMT_NONE
     * on error.
     */
    public static native @Cast("AVSampleFormat") int av_get_sample_fmt(String name);

    /**
     * Return the planar<->packed alternative form of the given sample format, or
     * AV_SAMPLE_FMT_NONE on error. If the passed sample_fmt is already in the
     * requested planar/packed format, the format returned is the same as the
     * input.
     */
    public static native @Cast("AVSampleFormat") int av_get_alt_sample_fmt(@Cast("AVSampleFormat") int sample_fmt, int planar);

    /**
     * Get the packed alternative form of the given sample format.
     *
     * If the passed sample_fmt is already in packed format, the format returned is
     * the same as the input.
     *
     * @return  the packed alternative form of the given sample format or
                AV_SAMPLE_FMT_NONE on error.
     */
    public static native @Cast("AVSampleFormat") int av_get_packed_sample_fmt(@Cast("AVSampleFormat") int sample_fmt);

    /**
     * Get the planar alternative form of the given sample format.
     *
     * If the passed sample_fmt is already in planar format, the format returned is
     * the same as the input.
     *
     * @return  the planar alternative form of the given sample format or
                AV_SAMPLE_FMT_NONE on error.
     */
    public static native @Cast("AVSampleFormat") int av_get_planar_sample_fmt(@Cast("AVSampleFormat") int sample_fmt);

    /**
     * Generate a string corresponding to the sample format with
     * sample_fmt, or a header if sample_fmt is negative.
     *
     * @param buf the buffer where to write the string
     * @param buf_size the size of buf
     * @param sample_fmt the number of the sample format to print the
     * corresponding info string, or a negative value to print the
     * corresponding header.
     * @return the pointer to the filled buffer or NULL if sample_fmt is
     * unknown or in case of other errors
     */
    public static native String av_get_sample_fmt_string(@Cast("char*") BytePointer buf, int buf_size,
            @Cast("AVSampleFormat") int sample_fmt);

    /**
     * Return number of bytes per sample.
     *
     * @param sample_fmt the sample format
     * @return number of bytes per sample or zero if unknown for the given
     * sample format
     */
    public static native int av_get_bytes_per_sample(@Cast("AVSampleFormat") int sample_fmt);

    /**
     * Check if the sample format is planar.
     *
     * @param sample_fmt the sample format to inspect
     * @return 1 if the sample format is planar, 0 if it is interleaved
     */
    public static native int av_sample_fmt_is_planar(@Cast("AVSampleFormat") int sample_fmt);

    /**
     * Get the required buffer size for the given audio parameters.
     *
     * @param[out] linesize calculated linesize, may be NULL
     * @param nb_channels   the number of channels
     * @param nb_samples    the number of samples in a single channel
     * @param sample_fmt    the sample format
     * @param align         buffer size alignment (0 = default, 1 = no alignment)
     * @return              required buffer size, or negative error code on failure
     */
    public static native int av_samples_get_buffer_size(int[] linesize, int nb_channels, int nb_samples,
            @Cast("AVSampleFormat") int sample_fmt, int align);

    /**
     * Fill plane data pointers and linesize for samples with sample
     * format sample_fmt.
     *
     * The audio_data array is filled with the pointers to the samples data planes:
     * for planar, set the start point of each channel's data within the buffer,
     * for packed, set the start point of the entire buffer only.
     *
     * The value pointed to by linesize is set to the aligned size of each
     * channel's data buffer for planar layout, or to the aligned size of the
     * buffer for all channels for packed layout.
     *
     * The buffer in buf must be big enough to contain all the samples
     * (use av_samples_get_buffer_size() to compute its minimum size),
     * otherwise the audio_data pointers will point to invalid data.
     *
     * @see enum AVSampleFormat
     * The documentation for AVSampleFormat describes the data layout.
     *
     * @param[out] audio_data  array to be filled with the pointer for each channel
     * @param[out] linesize    calculated linesize, may be NULL
     * @param buf              the pointer to a buffer containing the samples
     * @param nb_channels      the number of channels
     * @param nb_samples       the number of samples in a single channel
     * @param sample_fmt       the sample format
     * @param align            buffer size alignment (0 = default, 1 = no alignment)
     * @return                 0 on success or a negative error code on failure
     */
    public static native int av_samples_fill_arrays(@Cast("uint8_t**") PointerPointer audio_data, int[] linesize,
            @Cast("uint8_t*") BytePointer buf, int nb_channels, int nb_samples,
            @Cast("AVSampleFormat") int sample_fmt, int align);

    /**
     * Allocate a samples buffer for nb_samples samples, and fill data pointers and
     * linesize accordingly.
     * The allocated samples buffer can be freed by using av_freep(&audio_data[0])
     *
     * @see enum AVSampleFormat
     * The documentation for AVSampleFormat describes the data layout.
     *
     * @param[out] audio_data  array to be filled with the pointer for each channel
     * @param[out] linesize    aligned size for audio buffer(s), may be NULL
     * @param nb_channels      number of audio channels
     * @param nb_samples       number of samples per channel
     * @param align            buffer size alignment (0 = default, 1 = no alignment)
     * @return                 0 on success or a negative error code on failure
     * @see av_samples_fill_arrays()
     */
    public static native int av_samples_alloc(@Cast("uint8_t**") PointerPointer audio_data, int[] linesize,
            int nb_channels, int nb_samples, @Cast("AVSampleFormat") int sample_fmt, int align);

    /**
     * Copy samples from src to dst.
     *
     * @param dst destination array of pointers to data planes
     * @param src source array of pointers to data planes
     * @param dst_offset offset in samples at which the data will be written to dst
     * @param src_offset offset in samples at which the data will be read from src
     * @param nb_samples number of samples to be copied
     * @param nb_channels number of audio channels
     * @param sample_fmt audio sample format
     */
    public static native int av_samples_copy(@Cast("uint8_t**") PointerPointer dst, 
            @Cast("uint8_t**") PointerPointer src, int dst_offset, int src_offset,
            int nb_samples, int nb_channels, @Cast("AVSampleFormat") int sample_fmt);

    /**
     * Fill an audio buffer with silence.
     *
     * @param audio_data  array of pointers to data planes
     * @param offset      offset in samples at which to start filling
     * @param nb_samples  number of samples to fill
     * @param nb_channels number of audio channels
     * @param sample_fmt  audio sample format
     */
    public static native int av_samples_set_silence(@Cast("uint8_t**") PointerPointer audio_data, int offset,
            int nb_samples, int nb_channels, @Cast("AVSampleFormat") int sample_fmt);


    // include "imgutils.h"
    /**
     * @file
     * misc image utilities
     *
     * @addtogroup lavu_picture
     * @{
     */

    /**
     * Compute the max pixel step for each plane of an image with a
     * format described by pixdesc.
     *
     * The pixel step is the distance in bytes between the first byte of
     * the group of bytes which describe a pixel component and the first
     * byte of the successive group in the same plane for the same
     * component.
     *
     * @param max_pixsteps an array which is filled with the max pixel step
     * for each plane. Since a plane may contain different pixel
     * components, the computed max_pixsteps[plane] is relative to the
     * component in the plane with the max pixel step.
     * @param max_pixstep_comps an array which is filled with the component
     * for each plane which has the max pixel step. May be NULL.
     */
    public static native void av_image_fill_max_pixsteps(int max_pixsteps[/*4*/], int max_pixstep_comps[/*4*/],
            @Cast("AVPixFmtDescriptor*") int[] pixdesc);

    /**
     * Compute the size of an image line with format pix_fmt and width
     * width for the plane plane.
     *
     * @return the computed size in bytes
     */
    public static native int av_image_get_linesize(@Cast("PixelFormat") int pix_fmt, int width, int plane);

    /**
     * Fill plane linesizes for an image with pixel format pix_fmt and
     * width width.
     *
     * @param linesizes array to be filled with the linesize for each plane
     * @return >= 0 in case of success, a negative error code otherwise
     */
    public static native int av_image_fill_linesizes(int linesizes[/*4*/], @Cast("PixelFormat") int pix_fmt, int width);

    /**
     * Fill plane data pointers for an image with pixel format pix_fmt and
     * height height.
     *
     * @param data pointers array to be filled with the pointer for each image plane
     * @param ptr the pointer to a buffer which will contain the image
     * @param linesizes the array containing the linesize for each
     * plane, should be filled by av_image_fill_linesizes()
     * @return the size in bytes required for the image buffer, a negative
     * error code in case of failure
     */
    public static native int av_image_fill_pointers(@Cast("uint8_t**") PointerPointer data/*[4]*/,
            @Cast("PixelFormat") int pix_fmt, int height, @Cast("uint8_t*") BytePointer ptr, int linesizes[/*4*/]);

    /**
     * Allocate an image with size w and h and pixel format pix_fmt, and
     * fill pointers and linesizes accordingly.
     * The allocated image buffer has to be freed by using
     * av_freep(&pointers[0]).
     *
     * @param align the value to use for buffer size alignment
     * @return the size in bytes required for the image buffer, a negative
     * error code in case of failure
     */
    public static native int av_image_alloc(@Cast("uint8_t**") PointerPointer pointers/*[4]*/, int linesizes[/*4*/],
            int w, int h, @Cast("PixelFormat") int pix_fmt, int align);

    /**
     * Copy image plane from src to dst.
     * That is, copy "height" number of lines of "bytewidth" bytes each.
     * The first byte of each successive line is separated by *_linesize
     * bytes.
     *
     * @param dst_linesize linesize for the image plane in dst
     * @param src_linesize linesize for the image plane in src
     */
    public static native void av_image_copy_plane(@Cast("uint8_t*") BytePointer dst, int dst_linesize,
            @Cast("uint8_t*") BytePointer src, int src_linesize, int bytewidth, int height);

    /**
     * Copy image in src_data to dst_data.
     *
     * @param dst_linesizes linesizes for the image in dst_data
     * @param src_linesizes linesizes for the image in src_data
     */
    public static native void av_image_copy(@Cast("uint8_t**") PointerPointer dst_data/*[4]*/, int dst_linesizes[/*4*/],
            @Cast("const uint8_t **") PointerPointer src_data/*[4]*/, int src_linesizes[/*4*/],
            @Cast("PixelFormat") int pix_fmt, int width, int height);

    /**
     * Setup the data pointers and linesizes based on the specified image
     * parameters and the provided array.
     *
     * The fields of the given image are filled in by using the src
     * address which points to the image data buffer. Depending on the
     * specified pixel format, one or multiple image data pointers and
     * line sizes will be set.  If a planar format is specified, several
     * pointers will be set pointing to the different picture planes and
     * the line sizes of the different planes will be stored in the
     * lines_sizes array. Call with src == NULL to get the required
     * size for the src buffer.
     *
     * To allocate the buffer and fill in the dst_data and dst_linesize in
     * one call, use av_image_alloc().
     *
     * @param dst_data      data pointers to be filled in
     * @param dst_linesizes linesizes for the image in dst_data to be filled in
     * @param src           buffer which will contain or contains the actual image data, can be NULL
     * @param pix_fmt       the pixel format of the image
     * @param width         the width of the image in pixels
     * @param height        the height of the image in pixels
     * @param align         the value used in src for linesize alignment
     * @return the size in bytes required for src, a negative error code
     * in case of failure
     */
    public static native int av_image_fill_arrays(@Cast("uint8_t**") PointerPointer dst_data/*[4]*/, int dst_linesize[/*4*/],
            @Cast("uint8_t*") BytePointer src, @Cast("PixelFormat") int pix_fmt, int width, int height, int align);

    /**
     * Return the size in bytes of the amount of data required to store an
     * image with the given parameters.
     *
     * @param[in] align the assumed linesize alignment
     */
    public static native int av_image_get_buffer_size(@Cast("PixelFormat") int pix_fmt, int width, int height, int align);

    /**
     * Copy image data from an image into a buffer.
     *
     * av_image_get_buffer_size() can be used to compute the required size
     * for the buffer to fill.
     *
     * @param dst           a buffer into which picture data will be copied
     * @param dst_size      the size in bytes of dst
     * @param src_data      pointers containing the source image data
     * @param src_linesizes linesizes for the image in src_data
     * @param pix_fmt       the pixel format of the source image
     * @param width         the width of the source image in pixels
     * @param height        the height of the source image in pixels
     * @param align         the assumed linesize alignment for dst
     * @return the number of bytes written to dst, or a negative value
     * (error code) on error
     */
    public static native int av_image_copy_to_buffer(@Cast("uint8_t*") BytePointer dst, int dst_size,
            @Cast("const uint8_t * const*") PointerPointer src_data/*[4]*/, int src_linesize[/*4*/],
            @Cast("PixelFormat") int pix_fmt, int width, int height, int align);

    /**
     * Check if the given dimension of an image is valid, meaning that all
     * bytes of the image can be addressed with a signed int.
     *
     * @param w the width of the picture
     * @param h the height of the picture
     * @param log_offset the offset to sum to the log level for logging with log_ctx
     * @param log_ctx the parent logging context, it may be NULL
     * @return >= 0 if valid, a negative error code otherwise
     */
    public static native int av_image_check_size(@Cast("unsigned") int w, @Cast("unsigned") int h, int log_offset, Pointer log_ctx);

//    public static native int ff_set_systematic_pal2(@Cast("uint32_t*") int[/*256*/] pal, @Cast("PixelFormat") int pix_fmt);

    /**
     * @}
     */

}
