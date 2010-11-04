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
 * This file was derived from avdevice.h include file from
 * FFmpeg 0.6.1, which are covered by the following copyright notice:
 *
 * Copyright (C) 2001-2003 Michael Niedermayer <michaelni@gmx.at>
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

import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;

import static com.googlecode.javacv.jna.avutil.*;

/**
 *
 * @author Samuel Audet
 */
public class swscale {
    public static final String[] paths = avutil.paths;
    public static final String[] libnames = { "swscale", "swscale-0" };
    public static final String libname = Loader.load(paths, libnames);
    public static final NativeLibrary nativeLibrary = NativeLibrary.getInstance(libname);

    public static int LIBSWSCALE_VERSION_MAJOR = 0;
    public static int LIBSWSCALE_VERSION_MINOR = 11;
    public static int LIBSWSCALE_VERSION_MICRO = 0;

    public static int    LIBSWSCALE_VERSION_INT = AV_VERSION_INT(LIBSWSCALE_VERSION_MAJOR,
                                                                 LIBSWSCALE_VERSION_MINOR,
                                                                 LIBSWSCALE_VERSION_MICRO);
    public static String LIBSWSCALE_VERSION     = AV_VERSION(LIBSWSCALE_VERSION_MAJOR,
                                                             LIBSWSCALE_VERSION_MINOR,
                                                             LIBSWSCALE_VERSION_MICRO);
    public static int    LIBSWSCALE_BUILD       = LIBSWSCALE_VERSION_INT;

    public static String LIBSWSCALE_IDENT       = "SwS" + LIBSWSCALE_VERSION;


    public static native int swscale_version();
    public static native String swscale_configuration();
    public static native String swscale_license();

    public static final int
            SWS_FAST_BILINEAR   = 1,
            SWS_BILINEAR        = 2,
            SWS_BICUBIC         = 4,
            SWS_X               = 8,
            SWS_POINT        = 0x10,
            SWS_AREA         = 0x20,
            SWS_BICUBLIN     = 0x40,
            SWS_GAUSS        = 0x80,
            SWS_SINC        = 0x100,
            SWS_LANCZOS     = 0x200,
            SWS_SPLINE      = 0x400,

            SWS_SRC_V_CHR_DROP_MASK   = 0x30000,
            SWS_SRC_V_CHR_DROP_SHIFT  = 16,

            SWS_PARAM_DEFAULT         = 123456,

            SWS_PRINT_INFO            = 0x1000,

            SWS_FULL_CHR_H_INT  = 0x2000,
            SWS_FULL_CHR_H_INP  = 0x4000,
            SWS_DIRECT_BGR      = 0x8000,
            SWS_ACCURATE_RND    = 0x40000,
            SWS_BITEXACT        = 0x80000,

            SWS_CPU_CAPS_MMX    = 0x80000000,
            SWS_CPU_CAPS_MMX2   = 0x20000000,
            SWS_CPU_CAPS_3DNOW  = 0x40000000,
            SWS_CPU_CAPS_ALTIVEC= 0x10000000,
            SWS_CPU_CAPS_BFIN   = 0x01000000;

    public static final double
            SWS_MAX_REDUCE_CUTOFF=0.002;

    public static final int
            SWS_CS_ITU709       = 1,
            SWS_CS_FCC          = 4,
            SWS_CS_ITU601       = 5,
            SWS_CS_ITU624       = 5,
            SWS_CS_SMPTE170M    = 5,
            SWS_CS_SMPTE240M    = 7,
            SWS_CS_DEFAULT      = 5;

    public static native IntByReference sws_getCoefficients(int colorspace);

    public static class SwsVector extends Structure {
        public SwsVector() { }
        public SwsVector(Pointer m) { super(m); read(); }

        public DoubleByReference coeff;
        public int length;

        public static class ByReference extends SwsVector implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class SwsFilter extends Structure {
        public SwsVector.ByReference lumH;
        public SwsVector.ByReference lumV;
        public SwsVector.ByReference chrH;
        public SwsVector.ByReference chrV;
    }

    public static class SwsContext extends PointerType { }

    public static native int sws_isSupportedInput(int /* enum PixelFormat */ pix_fmt);
    public static native int sws_isSupportedOutput(int /* enum PixelFormat */ pix_fmt);
    public static native void sws_freeContext(SwsContext swsContext);
    public static native SwsContext sws_getContext(int srcW, int srcH, int /* enum PixelFormat */ srcFormat,
            int dstW, int dstH, int /* enum PixelFormat */ dstFormat,
            int flags, SwsFilter srcFilter, SwsFilter dstFilter, double[] param);
    public static native SwsContext sws_getContext(int srcW, int srcH, int /* enum PixelFormat */ srcFormat,
            int dstW, int dstH, int /* enum PixelFormat */ dstFormat,
            int flags, Pointer srcFilter, Pointer dstFilter, Pointer param);

    public static native int sws_scale(SwsContext context, Pointer srcSlice, Pointer srcStride,
            int srcSliceY, int srcSliceH, Pointer dst, Pointer dstStride);
    public static native int sws_scale(SwsContext context, Pointer srcSlice, int srcStride[],
            int srcSliceY, int srcSliceH, Pointer dst, int dstStride[]);
    public static int sws_scale(SwsContext context, Pointer srcSlice[], int srcStride[],
            int srcSliceY, int srcSliceH, Pointer dst[], int dstStride[]) {
        return sws_scale(context, srcSlice[0], srcStride, srcSliceY, srcSliceH, dst[0], dstStride);
    }

    public static native int sws_setColorspaceDetails(SwsContext c, int inv_table[/*4*/],
            int srcRange, int table[/*4*/], int dstRange, int brightness, int contrast, int saturation);
    public static native int sws_getColorspaceDetails(SwsContext c, PointerByReference /* int** */ inv_table,
            IntByReference srcRange, PointerByReference /* int** */ table, IntByReference dstRange,
            IntByReference brightness, IntByReference contrast, IntByReference saturation);

    public static native SwsVector sws_allocVec(int length);
    public static native SwsVector sws_getGaussianVec(double variance, double quality);
    public static native SwsVector sws_getConstVec(double c, int length);
    public static native SwsVector sws_getIdentityVec();
    public static native void sws_scaleVec(SwsVector a, double scalar);

    public static native void sws_normalizeVec(SwsVector a, double height);
    public static native void sws_convVec(SwsVector a, SwsVector b);
    public static native void sws_addVec(SwsVector a, SwsVector b);
    public static native void sws_subVec(SwsVector a, SwsVector b);
    public static native void sws_shiftVec(SwsVector a, int shift);

    public static native SwsVector sws_cloneVec(SwsVector a);
    public static native void sws_printVec2(SwsVector a, AVClass log_ctx, int log_level);
    public static native void sws_freeVec(SwsVector a);

    public static native SwsFilter sws_getDefaultFilter(float lumaGBlur, float chromaGBlur,
            float lumaSharpen, float chromaSharpen, float chromaHShift, float chromaVShift, int verbose);
    public static native void sws_freeFilter(SwsFilter filter);

    public static native SwsContext sws_getCachedContext(SwsContext context,
            int srcW, int srcH, int /* enum PixelFormat */ srcFormat,
            int dstW, int dstH, int /* enum PixelFormat */ dstFormat,
            int flags, SwsFilter srcFilter, SwsFilter dstFilter, double[] param);

    public static class NewFunctions extends swscale {
        public static final String libname = Loader.load(paths, libnames);

        public static native void sws_convertPalette8ToPacked32(Pointer src, Pointer dst, NativeLong num_pixels, byte[] palette);
        public static native void sws_convertPalette8ToPacked32(Pointer src, Pointer dst, NativeLong num_pixels, ByteBuffer palette);
        public static native void sws_convertPalette8ToPacked32(Pointer src, Pointer dst, NativeLong num_pixels, Pointer palette);

        public static native void sws_convertPalette8ToPacked24(Pointer src, Pointer dst, NativeLong num_pixels, byte[] palette);
        public static native void sws_convertPalette8ToPacked24(Pointer src, Pointer dst, NativeLong num_pixels, ByteBuffer palette);
        public static native void sws_convertPalette8ToPacked24(Pointer src, Pointer dst, NativeLong num_pixels, Pointer palette);
    }

    public static class DeprecatedFunctions extends swscale {
        public static final String libname = Loader.load(paths, libnames);

        @Deprecated public static native void sws_printVec(SwsVector a);
    }
}
