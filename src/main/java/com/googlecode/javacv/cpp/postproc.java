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
 * This file was derived from postprocess.h include file from
 * FFmpeg 1.0, which are covered by the following copyright notice:
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
 *
 *
 * WARNING: postproc itself is covered by the full GPLv2.
 * If your program uses this class, it will become bound to that license.
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Name;
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
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude="<libpostproc/postprocess.h>",
        link={"postproc@.52", "avutil@.51"}, includepath=genericIncludepath, linkpath=genericLinkpath),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        define={"__STDC_CONSTANT_MACROS", "pp_help pp_help_bad[]; __declspec(dllimport) extern const char pp_help"},
        preloadpath=windowsPreloadpath, preload="postproc-52"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class postproc {
    static { load(avutil.class); load(); }

    /**
     * @file
     * @brief
     *     external postprocessing API
     */

    public static final int LIBPOSTPROC_VERSION_MAJOR = 52;
    public static final int LIBPOSTPROC_VERSION_MINOR =  2;
    public static final int LIBPOSTPROC_VERSION_MICRO = 100;

    public static final int    LIBPOSTPROC_VERSION_INT = AV_VERSION_INT(LIBPOSTPROC_VERSION_MAJOR,
                                                                        LIBPOSTPROC_VERSION_MINOR,
                                                                        LIBPOSTPROC_VERSION_MICRO);
    public static final String LIBPOSTPROC_VERSION     = AV_VERSION(LIBPOSTPROC_VERSION_MAJOR,
                                                                    LIBPOSTPROC_VERSION_MINOR,
                                                                    LIBPOSTPROC_VERSION_MICRO);
    public static final int    LIBPOSTPROC_BUILD       = LIBPOSTPROC_VERSION_INT;

    public static final String LIBPOSTPROC_IDENT       = "postproc" + LIBPOSTPROC_VERSION;

    /**
     * Return the LIBPOSTPROC_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int postproc_version();

    /**
     * Return the libpostproc build-time configuration.
     */
    public static native String postproc_configuration();

    /**
     * Return the libpostproc license.
     */
    public static native String postproc_license();

    public static final int PP_QUALITY_MAX = 6;

    @Opaque public static class pp_context extends Pointer {
        static { load(); }
        public pp_context() { }
        public pp_context(Pointer p) { super(p); }
    }
    @Opaque public static class pp_mode extends Pointer {
        static { load(); }
        public pp_mode() { }
        public pp_mode(Pointer p) { super(p); }
    }

    @Name("\n #undef pp_help \n pp_help")
    @MemberGetter public static native String pp_help(); ///< a simple help text

    public static native void pp_postprocess(
            @Cast("const uint8_t**")/*[3]*/ PointerPointer src, int srcStride[/*3*/],
            @Cast(      "uint8_t**")/*[3]*/ PointerPointer dst, int dstStride[/*3*/],
            int horizontalSize, int verticalSize,
            @Cast("QP_STORE_T*") byte[] QP_store, int QP_stride,
            pp_mode mode, pp_context ppContext, int pict_type);


    /**
     * Return a pp_mode or NULL if an error occurred.
     *
     * @param name    the string after "-pp" on the command line
     * @param quality a number from 0 to PP_QUALITY_MAX
     */
    public static native pp_mode pp_get_mode_by_name_and_quality(String name, int quality);
    public static native void pp_free_mode(pp_mode mode);

    public static native pp_context pp_get_context(int width, int height, int flags);
    public static native void pp_free_context(pp_context ppContext);

    public static final int
            PP_CPU_CAPS_MMX     = 0x80000000,
            PP_CPU_CAPS_MMX2    = 0x20000000,
            PP_CPU_CAPS_3DNOW   = 0x40000000,
            PP_CPU_CAPS_ALTIVEC = 0x10000000,

            PP_FORMAT       =  0x00000008,
            PP_FORMAT_420   = (0x00000011|PP_FORMAT),
            PP_FORMAT_422   = (0x00000001|PP_FORMAT),
            PP_FORMAT_411   = (0x00000002|PP_FORMAT),
            PP_FORMAT_444   = (0x00000000|PP_FORMAT),

            PP_PICT_TYPE_QP2 = 0x00000010; ///< MPEG2 style QScale
}
