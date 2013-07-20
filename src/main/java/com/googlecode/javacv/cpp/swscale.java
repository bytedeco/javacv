/*
 * Copyright (C) 2010,2011,2012,2013 Samuel Audet
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
 * This file was derived from swscale.h include file from
 * FFmpeg 1.2, which are covered by the following copyright notice:
 *
 * Copyright (C) 2001-2011 Michael Niedermayer <michaelni@gmx.at>
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
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Properties(inherit=avutil.class, value={
    @Platform(cinclude="<libswscale/swscale.h>", link="swscale@.2"),
    @Platform(value="windows", preload="swscale-2") })
public class swscale {
    static { load(); }

    /**
     * @file
     * @ingroup lsws
     * external API header
     */

    /**
     * @defgroup lsws Libswscale
     * @{
     */

    // #include "version.h"
    /**
     * @file
     * swscale version macros
     */

    public static final int LIBSWSCALE_VERSION_MAJOR = 2;
    public static final int LIBSWSCALE_VERSION_MINOR = 2;
    public static final int LIBSWSCALE_VERSION_MICRO = 100;

    public static final int    LIBSWSCALE_VERSION_INT = AV_VERSION_INT(LIBSWSCALE_VERSION_MAJOR,
                                                                       LIBSWSCALE_VERSION_MINOR,
                                                                       LIBSWSCALE_VERSION_MICRO);
    public static final String LIBSWSCALE_VERSION     = AV_VERSION(LIBSWSCALE_VERSION_MAJOR,
                                                                   LIBSWSCALE_VERSION_MINOR,
                                                                   LIBSWSCALE_VERSION_MICRO);
    public static final int    LIBSWSCALE_BUILD       = LIBSWSCALE_VERSION_INT;

    public static final String LIBSWSCALE_IDENT       = "SwS" + LIBSWSCALE_VERSION;

    /**
     * Return the LIBSWSCALE_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int swscale_version();

    /**
     * Return the libswscale build-time configuration.
     */
    public static native String swscale_configuration();

    /**
     * Return the libswscale license.
     */
    public static native String swscale_license();

    /* values for the flags, the stuff on the command line is different */
    public static final int
            SWS_FAST_BILINEAR =     1,
            SWS_BILINEAR      =     2,
            SWS_BICUBIC       =     4,
            SWS_X             =     8,
            SWS_POINT         =  0x10,
            SWS_AREA          =  0x20,
            SWS_BICUBLIN      =  0x40,
            SWS_GAUSS         =  0x80,
            SWS_SINC          = 0x100,
            SWS_LANCZOS       = 0x200,
            SWS_SPLINE        = 0x400,

            SWS_SRC_V_CHR_DROP_MASK    = 0x30000,
            SWS_SRC_V_CHR_DROP_SHIFT   = 16,

            SWS_PARAM_DEFAULT          = 123456,

            SWS_PRINT_INFO             = 0x1000,

    //the following 3 flags are not completely implemented
    //internal chrominace subsampling info
            SWS_FULL_CHR_H_INT   = 0x2000,
    //input subsampling info
            SWS_FULL_CHR_H_INP   = 0x4000,
            SWS_DIRECT_BGR       = 0x8000,
            SWS_ACCURATE_RND     = 0x40000,
            SWS_BITEXACT         = 0x80000,
            SWS_ERROR_DIFFUSION  = 0x800000;

    public static final double
            SWS_MAX_REDUCE_CUTOFF = 0.002;

    public static final int
            SWS_CS_ITU709        = 1,
            SWS_CS_FCC           = 4,
            SWS_CS_ITU601        = 5,
            SWS_CS_ITU624        = 5,
            SWS_CS_SMPTE170M     = 5,
            SWS_CS_SMPTE240M     = 7,
            SWS_CS_DEFAULT       = 5;

    /**
     * Return a pointer to yuv<->rgb coefficients for the given colorspace
     * suitable for sws_setColorspaceDetails().
     *
     * @param colorspace One of the SWS_CS_* macros. If invalid,
     * SWS_CS_DEFAULT is used.
     */
    public static native @Const IntPointer sws_getCoefficients(int colorspace);

    // when used for filters they must have an odd number of elements
    // coeffs cannot be shared between vectors
    public static class SwsVector extends Pointer {
        static { load(); }
        public SwsVector() { allocate(); }
        public SwsVector(int size) { allocateArray(size); }
        public SwsVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public SwsVector position(int position) {
            return (SwsVector)super.position(position);
        }
                                             ///< pointer to the list of coefficients
        public native DoublePointer coeff(); public native SwsVector coeff(DoublePointer coeff);
                                             ///< number of coefficients in the vector
        public native int length();          public native SwsVector length(int length);
    }

    // vectors can be shared
    public static class SwsFilter extends Pointer {
        static { load(); }
        public SwsFilter() { allocate(); }
        public SwsFilter(int size) { allocateArray(size); }
        public SwsFilter(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public SwsFilter position(int position) {
            return (SwsFilter)super.position(position);
        }

        public native SwsVector lumH(); public native SwsFilter lumH(SwsVector lumH);
        public native SwsVector lumV(); public native SwsFilter lumV(SwsVector lumV);
        public native SwsVector chrH(); public native SwsFilter chrH(SwsVector chrH);
        public native SwsVector chrV(); public native SwsFilter chrV(SwsVector chrV);
    }

    @Opaque public static class SwsContext extends Pointer {
        static { load(); }
        public SwsContext() { }
        public SwsContext(Pointer p) { super(p); }
    }

    /**
     * Return a positive value if pix_fmt is a supported input format, 0
     * otherwise.
     */
    public static native int sws_isSupportedInput(@Cast("AVPixelFormat") int pix_fmt);

    /**
     * Return a positive value if pix_fmt is a supported output format, 0
     * otherwise.
     */
    public static native int sws_isSupportedOutput(@Cast("AVPixelFormat") int pix_fmt);

    /**
     * Allocate an empty SwsContext. This must be filled and passed to
     * sws_init_context(). For filling see AVOptions, options.c and
     * sws_setColorspaceDetails().
     */
    public static native SwsContext sws_alloc_context();

    /**
     * Initialize the swscaler context sws_context.
     *
     * @return zero or positive value on success, a negative value on
     * error
     */
    public static native int sws_init_context(SwsContext sws_context, SwsFilter srcFilter, SwsFilter dstFilter);

    /**
     * Free the swscaler context swsContext.
     * If swsContext is NULL, then does nothing.
     */
    public static native void sws_freeContext(SwsContext swsContext);

    /**
     * Scale the image slice in srcSlice and put the resulting scaled
     * slice in the image in dst. A slice is a sequence of consecutive
     * rows in an image.
     *
     * Slices have to be provided in sequential order, either in
     * top-bottom or bottom-top order. If slices are provided in
     * non-sequential order the behavior of the function is undefined.
     *
     * @param c         the scaling context previously created with
     *                  sws_getContext()
     * @param srcSlice  the array containing the pointers to the planes of
     *                  the source slice
     * @param srcStride the array containing the strides for each plane of
     *                  the source image
     * @param srcSliceY the position in the source image of the slice to
     *                  process, that is the number (counted starting from
     *                  zero) in the image of the first row of the slice
     * @param srcSliceH the height of the source slice, that is the number
     *                  of rows in the slice
     * @param dst       the array containing the pointers to the planes of
     *                  the destination image
     * @param dstStride the array containing the strides for each plane of
     *                  the destination image
     * @return          the height of the output slice
     */
    public static native int sws_scale(SwsContext context, @Cast("const uint8_t**") PointerPointer srcSlice,
            int[] srcStride,      int srcSliceY, int srcSliceH,  @Cast("uint8_t**") PointerPointer dst, int dstStride[]);
    public static native int sws_scale(SwsContext context, @Cast("const uint8_t**") PointerPointer srcSlice,
            IntPointer srcStride, int srcSliceY, int srcSliceH,  @Cast("uint8_t**") PointerPointer dst, IntPointer dstStride);
    public static native int sws_scale(SwsContext context, @Cast("const uint8_t**") @ByPtrPtr BytePointer srcSlice,
            int[] srcStride,      int srcSliceY, int srcSliceH,  @Cast("uint8_t**") @ByPtrPtr BytePointer dst, int dstStride[]);
    public static native int sws_scale(SwsContext context, @Cast("const uint8_t**") @ByPtrPtr BytePointer srcSlice,
            IntPointer srcStride, int srcSliceY, int srcSliceH,  @Cast("uint8_t**") @ByPtrPtr BytePointer dst, IntPointer dstStride);

    /**
     * @param dstRange flag indicating the while-black range of the output (1=jpeg / 0=mpeg)
     * @param srcRange flag indicating the while-black range of the input (1=jpeg / 0=mpeg)
     * @param table the yuv2rgb coefficients describing the output yuv space, normally ff_yuv2rgb_coeffs[x]
     * @param inv_table the yuv2rgb coefficients describing the input yuv space, normally ff_yuv2rgb_coeffs[x]
     * @param brightness 16.16 fixed point brightness correction
     * @param contrast 16.16 fixed point contrast correction
     * @param saturation 16.16 fixed point saturation correction
     * @return -1 if not supported
     */
    public static native int sws_setColorspaceDetails(SwsContext c, int inv_table[/*4*/], int srcRange,
            int table[/*4*/], int dstRange, int brightness, int contrast, int saturation);

    /**
     * @return -1 if not supported
     */
    public static native int sws_getColorspaceDetails(SwsContext c, @ByPtrPtr IntPointer inv_table, int[] srcRange,
            @ByPtrPtr IntPointer table, int[] dstRange, int[] brightness, int[] contrast, int[] saturation);

    /**
     * Allocate and return an uninitialized vector with length coefficients.
     */
    public static native SwsVector sws_allocVec(int length);

    /**
     * Return a normalized Gaussian curve used to filter stuff
     * quality = 3 is high quality, lower is lower quality.
     */
    public static native SwsVector sws_getGaussianVec(double variance, double quality);

    /**
     * Allocate and return a vector with length coefficients, all
     * with the same value c.
     */
    public static native SwsVector sws_getConstVec(double c, int length);

    /**
     * Allocate and return a vector with just one coefficient, with
     * value 1.0.
     */
    public static native SwsVector sws_getIdentityVec();

    /**
     * Scale all the coefficients of a by the scalar value.
     */
    public static native void sws_scaleVec(SwsVector a, double scalar);

    /**
     * Scale all the coefficients of a so that their sum equals height.
     */
    public static native void sws_normalizeVec(SwsVector a, double height);
    public static native void sws_convVec(SwsVector a, SwsVector b);
    public static native void sws_addVec(SwsVector a, SwsVector b);
    public static native void sws_subVec(SwsVector a, SwsVector b);
    public static native void sws_shiftVec(SwsVector a, int shift);

    /**
     * Allocate and return a clone of the vector a, that is a vector
     * with the same coefficients as a.
     */
    public static native SwsVector sws_cloneVec(SwsVector a);

    /**
     * Print with av_log() a textual representation of the vector a
     * if log_level <= av_log_level.
     */
    public static native void sws_printVec2(SwsVector a, AVClass log_ctx, int log_level);

    public static native void sws_freeVec(SwsVector a);

    public static native SwsFilter sws_getDefaultFilter(float lumaGBlur, float chromaGBlur,
            float lumaSharpen, float chromaSharpen, float chromaHShift, float chromaVShift, int verbose);
    public static native void sws_freeFilter(SwsFilter filter);

    /**
     * Check if context can be reused, otherwise reallocate a new one.
     *
     * If context is NULL, just calls sws_getContext() to get a new
     * context. Otherwise, checks if the parameters are the ones already
     * saved in context. If that is the case, returns the current
     * context. Otherwise, frees context and gets a new context with
     * the new parameters.
     *
     * Be warned that srcFilter and dstFilter are not checked, they
     * are assumed to remain the same.
     */
    public static native SwsContext sws_getCachedContext(SwsContext context,
            int srcW, int srcH, @Cast("AVPixelFormat") int srcFormat,
            int dstW, int dstH, @Cast("AVPixelFormat") int dstFormat,
            int flags, SwsFilter srcFilter, SwsFilter dstFilter, double[] param);

    /**
     * Convert an 8-bit paletted frame into a frame with a color depth of 32 bits.
     *
     * The output frame will have the same packed format as the palette.
     *
     * @param src        source frame buffer
     * @param dst        destination frame buffer
     * @param num_pixels number of pixels to convert
     * @param palette    array with [256] entries, which must match color arrangement (RGB or BGR) of src
     */
    public static native void sws_convertPalette8ToPacked32(@Cast("uint8_t*") BytePointer src,
            @Cast("uint8_t*") BytePointer dst, int num_pixels, @Cast("uint8_t*") BytePointer palette);

    /**
     * Convert an 8-bit paletted frame into a frame with a color depth of 24 bits.
     *
     * With the palette format "ABCD", the destination frame ends up with the format "ABC".
     *
     * @param src        source frame buffer
     * @param dst        destination frame buffer
     * @param num_pixels number of pixels to convert
     * @param palette    array with [256] entries, which must match color arrangement (RGB or BGR) of src
     */
    public static native void sws_convertPalette8ToPacked24(@Cast("uint8_t*") BytePointer src,
            @Cast("uint8_t*") BytePointer dst, int num_pixels, @Cast("uint8_t*") BytePointer palette);

    /**
     * Get the AVClass for swsContext. It can be used in combination with
     * AV_OPT_SEARCH_FAKE_OBJ for examining options.
     *
     * @see av_opt_find().
     */
    public static native @Const AVClass sws_get_class();

    /**
     * @}
     */
}
