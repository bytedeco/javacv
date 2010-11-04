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
 * Copyright (C) 2001-2003 Michael Niedermayer (michaelni@gmx.at)
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.googlecode.javacv.jna;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

import static com.googlecode.javacv.jna.avutil.*;

/**
 *
 * @author Samuel Audet
 */
public class postprocess {
    public static final String[] paths = avutil.paths;
    public static final String[] libnames = { "postprocess", "postprocess-51" };
    public static final String libname = Loader.load(paths, libnames);
    public static final NativeLibrary nativeLibrary = NativeLibrary.getInstance(libname);

    public static int LIBPOSTPROC_VERSION_MAJOR = 51;
    public static int LIBPOSTPROC_VERSION_MINOR =  2;
    public static int LIBPOSTPROC_VERSION_MICRO =  0;

    public static int    LIBPOSTPROC_VERSION_INT = AV_VERSION_INT(LIBPOSTPROC_VERSION_MAJOR,
                                                                  LIBPOSTPROC_VERSION_MINOR,
                                                                  LIBPOSTPROC_VERSION_MICRO);
    public static String LIBPOSTPROC_VERSION     = AV_VERSION(LIBPOSTPROC_VERSION_MAJOR,
                                                              LIBPOSTPROC_VERSION_MINOR,
                                                              LIBPOSTPROC_VERSION_MICRO);
    public static int    LIBPOSTPROC_BUILD       = LIBPOSTPROC_VERSION_INT;

    public static String LIBPOSTPROC_IDENT       = "postproc" + LIBPOSTPROC_VERSION;


    public static native int postproc_version();
    public static native String postproc_configuration();
    public static native String postproc_license();

    public static final int PP_QUALITY_MAX = 6;

//#define QP_STORE_T int8_t

    public static class pp_context extends PointerType { }
    public static class pp_mode extends PointerType { }

    public static final String pp_help = nativeLibrary.
            getGlobalVariableAddress("pp_help").getString(0);

    public static native void pp_postprocess(Pointer src/*[3]*/, int srcStride[/*3*/],
            Pointer dst/*[3]*/, int dstStride[/*3*/], int horizontalSize, int verticalSize,
            byte[] /* QP_STORE_T* */ QP_store,  int QP_stride,
            pp_mode mode, pp_context ppContext, int pict_type);
    public static void pp_postprocess(Pointer src[/*3*/], int srcStride[/*3*/],
            Pointer dst[/*3*/], int dstStride[/*3*/], int horizontalSize, int verticalSize,
            byte[] /* QP_STORE_T* */ QP_store,  int QP_stride,
            pp_mode mode, pp_context ppContext, int pict_type) {
        pp_postprocess(src[0], srcStride, dst[0], dstStride, horizontalSize, verticalSize,
                QP_store, QP_stride, mode, ppContext, pict_type);
    }

    public static native pp_mode pp_get_mode_by_name_and_quality(String name, int quality);
    public static native void pp_free_mode(pp_mode mode);

    public static native pp_context pp_get_context(int width, int height, int flags);
    public static native void pp_free_context(pp_context ppContext);

    public static final int
            PP_CPU_CAPS_MMX   = 0x80000000,
            PP_CPU_CAPS_MMX2  = 0x20000000,
            PP_CPU_CAPS_3DNOW = 0x40000000,
            PP_CPU_CAPS_ALTIVEC = 0x10000000,

            PP_FORMAT       =  0x00000008,
            PP_FORMAT_420   = (0x00000011|PP_FORMAT),
            PP_FORMAT_422   = (0x00000001|PP_FORMAT),
            PP_FORMAT_411   = (0x00000002|PP_FORMAT),
            PP_FORMAT_444   = (0x00000000|PP_FORMAT),

            PP_PICT_TYPE_QP2 = 0x00000010;
}