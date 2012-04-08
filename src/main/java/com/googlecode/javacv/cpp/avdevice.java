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
 * This file was derived from avdevice.h include file from
 * FFmpeg 0.6.1, which are covered by the following copyright notice:
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

import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude="<libavdevice/avdevice.h>",
        includepath=genericIncludepath, linkpath=genericLinkpath, link={"avdevice", "avformat", "avcodec", "avutil"}),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload="avdevice-52"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avdevice {
    static { load(avformat.class); load(); }

    public static final int LIBAVDEVICE_VERSION_MAJOR = 52;
    public static final int LIBAVDEVICE_VERSION_MINOR =  2;
    public static final int LIBAVDEVICE_VERSION_MICRO =  0;

    public static final int    LIBAVDEVICE_VERSION_INT = AV_VERSION_INT(LIBAVDEVICE_VERSION_MAJOR,
                                                                        LIBAVDEVICE_VERSION_MINOR,
                                                                        LIBAVDEVICE_VERSION_MICRO);
    public static final String LIBAVDEVICE_VERSION     = AV_VERSION(LIBAVDEVICE_VERSION_MAJOR,
                                                                    LIBAVDEVICE_VERSION_MINOR,
                                                                    LIBAVDEVICE_VERSION_MICRO);
    public static final int    LIBAVDEVICE_BUILD       = LIBAVDEVICE_VERSION_INT;


    public static native int avdevice_version();
    public static native String avdevice_configuration();
    public static native String avdevice_license();
    public static native void avdevice_register_all();
}
