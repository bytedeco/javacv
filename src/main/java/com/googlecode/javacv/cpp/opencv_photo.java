/*
 * Copyright (C) 2012,2013 Samuel Audet
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
 * This file is based on information found in photo_c.h and photo.hpp
 * of OpenCV 2.4.4, which are covered by the following copyright notice:
 *
 *                          License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
 * Copyright (C) 2008-2012, Willow Garage Inc., all rights reserved.
 * Third party copyrights are property of their respective owners.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * Redistribution's of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistribution's in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * The name of the copyright holders may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as is" and
 * any express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the Intel Corporation or contributors be liable for any direct,
 * indirect, incidental, special, exemplary, or consequential damages
 * (including, but not limited to, procurement of substitute goods or services;
 * loss of use, data, or profits; or business interruption) however caused
 * and on any theory of liability, whether in contract, strict liability,
 * or tort (including negligence or otherwise) arising in any way out of
 * the use of this software, even if advised of the possibility of such damage.
 *
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/photo/photo_c.h>", "<opencv2/photo/photo.hpp>", "opencv_adapters.h"},
        link={"opencv_photo@.2.4", "opencv_imgproc@.2.4", "opencv_core@.2.4"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_photo244", "opencv_imgproc244", "opencv_core244"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_photo {
    static { load(opencv_imgproc.class); load(); }

    public static final int
            CV_INPAINT_NS      = 0,
            CV_INPAINT_TELEA   = 1;

    public static native void cvInpaint(CvArr src, CvArr mask, CvArr dst, double inpaintRange, int flags);


    @Namespace("cv") public static native void fastNlMeansDenoising(@InputArray CvArr src, @InputArray CvArr dst,
            float h/*=3*/, int templateWindowSize/*=7*/, int searchWindowSize/*=21*/);

    @Namespace("cv") public static native void fastNlMeansDenoisingColored(@InputArray CvArr src, @InputArray CvArr dst,
            float h/*=3*/, float hColor/*=3*/, int templateWindowSize/*=7*/, int searchWindowSize/*=21*/);

    @Namespace("cv") public static native void fastNlMeansDenoisingMulti(@ByRef MatVector srcImgs, @InputArray CvArr dst,
            int imgToDenoiseIndex, int temporalWindowSize, float h/*=3*/,
            int templateWindowSize/*=7*/, int searchWindowSize/*=21*/);

    @Namespace("cv") public static native void fastNlMeansDenoisingColoredMulti(@ByRef MatVector srcImgs, @InputArray CvArr dst,
            int imgToDenoiseIndex, int temporalWindowSize, float h/*=3*/, float hColor/*=3*/,
            int templateWindowSize/*=7*/, int searchWindowSize/*=21*/);
}
