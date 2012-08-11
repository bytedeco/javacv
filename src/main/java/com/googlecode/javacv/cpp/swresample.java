/*
 * Copyright (C) 2012 Samuel Audet
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
 * This file was derived from swresample.h include file from
 * FFmpeg 0.11.1, which are covered by the following copyright notice:
 *
 * Copyright (C) 2011 Michael Niedermayer (michaelni@gmx.at)
 *
 * This file is part of libswresample
 *
 * libswresample is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * libswresample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with libswresample; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.googlecode.javacv.cpp;

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
@Properties({
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude="<libswresample/swresample.h>",
        includepath=genericIncludepath, linkpath=genericLinkpath, link={"swresample@.0", "avutil@.51"}),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload="swresample-0"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class swresample {
    static { load(avutil.class); load(); }

    /**
     * @file
     * libswresample public header
     */

    public static final int LIBSWRESAMPLE_VERSION_MAJOR = 0;
    public static final int LIBSWRESAMPLE_VERSION_MINOR = 15;
    public static final int LIBSWRESAMPLE_VERSION_MICRO = 100;

    public static final int    LIBSWRESAMPLE_VERSION_INT = AV_VERSION_INT(LIBSWRESAMPLE_VERSION_MAJOR,
                                                                          LIBSWRESAMPLE_VERSION_MINOR,
                                                                          LIBSWRESAMPLE_VERSION_MICRO);

    public static final int 
            SWR_CH_MAX = 32,   ///< Maximum number of channels

            SWR_FLAG_RESAMPLE = 1; ///< Force resampling even if equal sample rate
    //TODO use int resample ?
    //long term TODO can we enable this dynamically?

    public static final int // enum SwrDitherType {
        SWR_DITHER_NONE                = 0,
        SWR_DITHER_RECTANGULAR         = 1,
        SWR_DITHER_TRIANGULAR          = 2,
        SWR_DITHER_TRIANGULAR_HIGHPASS = 3;

    @Opaque public static class SwrContext extends Pointer {
        static { load(); }
        public SwrContext() { }
        public SwrContext(Pointer p) { super(p); }
    }

    /**
     * Get the AVClass for swrContext. It can be used in combination with
     * AV_OPT_SEARCH_FAKE_OBJ for examining options.
     *
     * @see av_opt_find().
     */
    public static native @Const AVClass swr_get_class();

    /**
     * Allocate SwrContext.
     *
     * If you use this function you will need to set the parameters (manually or
     * with swr_alloc_set_opts()) before calling swr_init().
     *
     * @see swr_alloc_set_opts(), swr_init(), swr_free()
     * @return NULL on error, allocated context otherwise
     */
    public static native SwrContext swr_alloc();

    /**
     * Initialize context after user parameters have been set.
     *
     * @return AVERROR error code in case of failure.
     */
    public static native int swr_init(SwrContext s);

    /**
     * Allocate SwrContext if needed and set/reset common parameters.
     *
     * This function does not require s to be allocated with swr_alloc(). On the
     * other hand, swr_alloc() can use swr_alloc_set_opts() to set the parameters
     * on the allocated context.
     *
     * @param s               Swr context, can be NULL
     * @param out_ch_layout   output channel layout (AV_CH_LAYOUT_*)
     * @param out_sample_fmt  output sample format (AV_SAMPLE_FMT_*).
     * @param out_sample_rate output sample rate (frequency in Hz)
     * @param in_ch_layout    input channel layout (AV_CH_LAYOUT_*)
     * @param in_sample_fmt   input sample format (AV_SAMPLE_FMT_*).
     * @param in_sample_rate  input sample rate (frequency in Hz)
     * @param log_offset      logging level offset
     * @param log_ctx         parent logging context, can be NULL
     *
     * @see swr_init(), swr_free()
     * @return NULL on error, allocated context otherwise
     */
    public static native SwrContext swr_alloc_set_opts(SwrContext s,
            long out_ch_layout, @Cast("AVSampleFormat") int out_sample_fmt, int out_sample_rate,
            long  in_ch_layout, @Cast("AVSampleFormat") int  in_sample_fmt, int  in_sample_rate,
            int log_offset, Pointer log_ctx);

    /**
     * Free the given SwrContext and set the pointer to NULL.
     */
    public static native void swr_free(@ByPtrPtr SwrContext s);

    /**
     * Convert audio.
     *
     * in and in_count can be set to 0 to flush the last few samples out at the
     * end.
     *
     * If more input is provided than output space then the input will be buffered.
     * You can avoid this buffering by providing more output space than input.
     * Convertion will run directly without copying whenever possible.
     *
     * @param s         allocated Swr context, with parameters set
     * @param out       output buffers, only the first one need be set in case of packed audio
     * @param out_count amount of space available for output in samples per channel
     * @param in        input buffers, only the first one need to be set in case of packed audio
     * @param in_count  number of input samples available in one channel
     *
     * @return number of samples output per channel, negative value on error
     */
    public static native int swr_convert(SwrContext s,
                  @Cast("uint8_t**") PointerPointer out, int out_count,
            @Cast("const uint8_t**") PointerPointer in , int in_count);

    /**
     * Convert the next timestamp from input to output
     * timestampe are in 1/(in_sample_rate * out_sample_rate) units.
     *
     * @note There are 2 slightly differently behaving modes.
     *       First is when automatic timestamp compensation is not used, (min_compensation >= FLT_MAX)
     *              in this case timestamps will be passed through with delays compensated
     *       Second is when automatic timestamp compensation is used, (min_compensation < FLT_MAX)
     *              in this case the output timestamps will match output sample numbers
     *
     * @param pts   timstamp for the next input sample, INT64_MIN if unknown
     * @returns the output timestamp for the next output sample
     */
    public static native long swr_next_pts(SwrContext s, long pts);

    /**
     * Activate resampling compensation.
     */
    public static native int swr_set_compensation(SwrContext s, int sample_delta, int compensation_distance);

    /**
     * Set a customized input channel mapping.
     *
     * @param s           allocated Swr context, not yet initialized
     * @param channel_map customized input channel mapping (array of channel
     *                    indexes, -1 for a muted channel)
     * @return AVERROR error code in case of failure.
     */
    public static native int swr_set_channel_mapping(SwrContext s, int[] channel_map);

    /**
     * Set a customized remix matrix.
     *
     * @param s       allocated Swr context, not yet initialized
     * @param matrix  remix coefficients; matrix[i + stride * o] is
     *                the weight of input channel i in output channel o
     * @param stride  offset between lines of the matrix
     * @return  AVERROR error code in case of failure.
     */
    public static native int swr_set_matrix(SwrContext s, double[] matrix, int stride);

    /**
     * Drops the specified number of output samples.
     */
    public static native int swr_drop_output(SwrContext s, int count);

    /**
     * Injects the specified number of silence samples.
     */
    public static native int swr_inject_silence(SwrContext s, int count);

    /**
     * Gets the delay the next input sample will experience relative to the next output sample.
     *
     * Swresample can buffer data if more input has been provided than available
     * output space, also converting between sample rates needs a delay.
     * This function returns the sum of all such delays.
     *
     * @param s     swr context
     * @param base  timebase in which the returned delay will be
     *              if its set to 1 the returned delay is in seconds
     *              if its set to 1000 the returned delay is in milli seconds
     *              if its set to the input sample rate then the returned delay is in input samples
     *              if its set to the output sample rate then the returned delay is in output samples
     *              an exact rounding free delay can be found by using LCM(in_sample_rate, out_sample_rate)
     * @returns     the delay in 1/base units.
     */
    public static native long swr_get_delay(SwrContext s, long base);

    /**
     * Return the LIBSWRESAMPLE_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int swresample_version();

    /**
     * Return the swr build-time configuration.
     */
    public static native String swresample_configuration();

    /**
     * Return the swr license.
     */
    public static native String swresample_license();
}
