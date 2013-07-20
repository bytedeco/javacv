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
 * This file was derived from avdevice.h include file from
 * FFmpeg 1.2, which are covered by the following copyright notice:
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

import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Properties(inherit=avfilter.class, value={
    @Platform(cinclude="<libavdevice/avdevice.h>", link="avdevice@.54"),
    @Platform(value="windows", preload="avdevice-54") })
public class avdevice {
    static { load(); }

    // #include <version.h>
    /**
     * @file
     * @ingroup lavd
     * Libavdevice version macros
     */

    public static final int LIBAVDEVICE_VERSION_MAJOR = 54;
    public static final int LIBAVDEVICE_VERSION_MINOR =  3;
    public static final int LIBAVDEVICE_VERSION_MICRO = 103;

    public static final int    LIBAVDEVICE_VERSION_INT = AV_VERSION_INT(LIBAVDEVICE_VERSION_MAJOR,
                                                                        LIBAVDEVICE_VERSION_MINOR,
                                                                        LIBAVDEVICE_VERSION_MICRO);
    public static final String LIBAVDEVICE_VERSION     = AV_VERSION(LIBAVDEVICE_VERSION_MAJOR,
                                                                    LIBAVDEVICE_VERSION_MINOR,
                                                                    LIBAVDEVICE_VERSION_MICRO);
    public static final int    LIBAVDEVICE_BUILD       = LIBAVDEVICE_VERSION_INT;

    public static final String LIBAVDEVICE_IDENT       = "Lavf" + LIBAVDEVICE_VERSION;

    /**
     * @file
     * @ingroup lavd
     * Main libavdevice API header
     */

    /**
     * @defgroup lavd Special devices muxing/demuxing library
     * @{
     * Libavdevice is a complementary library to @ref libavf "libavformat". It
     * provides various "special" platform-specific muxers and demuxers, e.g. for
     * grabbing devices, audio capture and playback etc. As a consequence, the
     * (de)muxers in libavdevice are of the AVFMT_NOFILE type (they use their own
     * I/O functions). The filename passed to avformat_open_input() often does not
     * refer to an actually existing file, but has some special device-specific
     * meaning - e.g. for x11grab it is the display name.
     *
     * To use libavdevice, simply call avdevice_register_all() to register all
     * compiled muxers and demuxers. They all use standard libavformat API.
     * @}
     */

    /**
     * Return the LIBAVDEVICE_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int avdevice_version();

    /**
     * Return the libavdevice build-time configuration.
     */
    public static native String avdevice_configuration();

    /**
     * Return the libavdevice license.
     */
    public static native String avdevice_license();

    /**
     * Initialize libavdevice and register all the input and output devices.
     * @warning This function is not thread safe.
     */
    public static native void avdevice_register_all();
}
