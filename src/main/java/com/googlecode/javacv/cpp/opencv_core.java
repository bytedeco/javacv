/*
 * Copyright (C) 2011,2012,2013 Samuel Audet
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
 * This file is based on information found in core/types_c.h, core_c.h, and
 * core.hpp of OpenCV 2.4.6.1, which are covered by the following copyright notice:
 *
 *                          License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
 * Copyright (C) 2009-2011, Willow Garage Inc., all rights reserved.
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

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.LongPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.ShortPointer;
import com.googlecode.javacpp.SizeTPointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Convention;
import com.googlecode.javacpp.annotation.Index;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.MemberSetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;
import com.googlecode.javacpp.annotation.ValueGetter;
import com.googlecode.javacpp.annotation.ValueSetter;
import com.googlecode.javacpp.annotation.StdVector;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/core/core.hpp>", "opencv_adapters.h"}, link="opencv_core@.2.4", preload="tbb"),
    @Platform(value="windows", define="_WIN32_WINNT 0x0502", includepath=windowsIncludepath,
        link="opencv_core246", preload={"msvcr100", "msvcp100"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_core {
    public static native void SetLibraryPath(String path);
    static {
        if (load() != null) {
            String platformName = getPlatformName();
            if (platformName.equals("windows-x86")) {
                SetLibraryPath("C:/opencv/build/x86/vc10/bin/");
            } else if (platformName.equals("windows-x86_64")) {
                SetLibraryPath("C:/opencv/build/x64/vc10/bin/");
            }
        }
    }
    public static final String genericIncludepath    = "/usr/local/include:/opt/local/include/";
    public static final String genericLinkpath       = "/usr/local/lib/:/usr/local/lib64/:/opt/local/lib/:/opt/local/lib64/";
    public static final String windowsIncludepath    = "C:/opencv/build/include/";
    public static final String windowsx86Linkpath    = "C:/opencv/build/x86/vc10/lib/";
    public static final String windowsx86Preloadpath = "C:/opencv/build/x86/vc10/bin/";
    public static final String windowsx64Linkpath    = "C:/opencv/build/x64/vc10/lib/";
    public static final String windowsx64Preloadpath = "C:/opencv/build/x64/vc10/bin/";
    public static final String androidIncludepath    = "../include/";
    public static final String androidLinkpath       = "../lib/";

    public static final int
            CV_VERSION_EPOCH    = 2,
            CV_VERSION_MAJOR    = 4,
            CV_VERSION_MINOR    = 6,
            CV_VERSION_REVISION = 1,
            CV_MAJOR_VERSION    = CV_VERSION_EPOCH,
            CV_MINOR_VERSION    = CV_VERSION_MAJOR,
            CV_SUBMINOR_VERSION = CV_VERSION_MINOR;

    public static final String CV_VERSION = CV_VERSION_EPOCH + "." + CV_VERSION_MAJOR + "." + CV_VERSION_MINOR + "." + CV_VERSION_REVISION;

    @Opaque public static class CvArr extends Pointer implements Cloneable {
        static { load(); }
        protected CvArr() { }
        protected CvArr(Pointer p) { super(p); }
    }

    @Name("CvArr*")
    public static class CvArrArray extends Pointer {
        static { load(); }
        public CvArrArray(CvArr ... array) { this(array.length); put(array); position(0); }
        public CvArrArray(int size) { allocateArray(size); }
        public CvArrArray(Pointer p) { super(p); }
        private native void allocateArray(int size);

        @Override public CvArrArray position(int position) {
            return (CvArrArray)super.position(position);
        }

        public CvArrArray put(CvArr ... array) {
            for (int i = 0; i < array.length; i++) {
                position(i).put(array[i]);
            }
            return this;
        }

        public native CvArr get();
        public native CvArrArray put(CvArr p);
    }

    @Name("CvMat*")
    public static class CvMatArray extends CvArrArray {
        public CvMatArray(CvMat ... array) { this(array.length); put(array); position(0); }
        public CvMatArray(int size) { allocateArray(size); }
        public CvMatArray(Pointer p) { super(p); }
        private native void allocateArray(int size);

        @Override public CvMatArray position(int position) {
            return (CvMatArray)super.position(position);
        }
        @Override public CvMatArray put(CvArr ... array) {
            return (CvMatArray)super.put(array);
        }
        @Override @ValueGetter public native CvMat get();
        @Override public CvMatArray put(CvArr p) {
            if (p instanceof CvMat) {
                return (CvMatArray)super.put(p);
            } else {
                throw new ArrayStoreException(p.getClass().getName());
            }
        }
    }

    @Name("CvMatND*")
    public static class CvMatNDArray extends CvArrArray {
        public CvMatNDArray(CvMatND ... array) { this(array.length); put(array); position(0); }
        public CvMatNDArray(int size) { allocateArray(size); }
        public CvMatNDArray(Pointer p) { super(p); }
        private native void allocateArray(int size);

        @Override public CvMatNDArray position(int position) {
            return (CvMatNDArray)super.position(position);
        }
        @Override public CvMatNDArray put(CvArr ... array) {
            return (CvMatNDArray)super.put(array);
        }
        @Override @ValueGetter public native CvMatND get();
        @Override public CvMatNDArray put(CvArr p) {
            if (p instanceof CvMatND) {
                return (CvMatNDArray)super.put(p);
            } else {
                throw new ArrayStoreException(p.getClass().getName());
            }
        }
    }

    @Name("IplImage*")
    public static class IplImageArray extends CvArrArray {
        public IplImageArray(IplImage ... array) { this(array.length); put(array); position(0); }
        public IplImageArray(int size) { allocateArray(size); }
        public IplImageArray(Pointer p) { super(p); }
        private native void allocateArray(int size);

        @Override public IplImageArray position(int position) {
            return (IplImageArray)super.position(position);
        }
        @Override public IplImageArray put(CvArr ... array) {
            return (IplImageArray)super.put(array);
        }
        @Override @ValueGetter public native IplImage get();
        @Override public IplImageArray put(CvArr p) {
            if (p instanceof IplImage) {
                return (IplImageArray)super.put(p);
            } else {
                throw new ArrayStoreException(p.getClass().getName());
            }
        }
    }

    public static class Cv32suf extends Pointer {
        static { load(); }
        public Cv32suf() { allocate(); }
        public Cv32suf(int size) { allocateArray(size); }
        public Cv32suf(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Cv32suf position(int position) {
            return (Cv32suf)super.position(position);
        }

        public native int i();   public native Cv32suf i(int i);
        public native int u();   public native Cv32suf u(int u);
        public native float f(); public native Cv32suf f(float f);
    }

    public static class Cv64suf extends Pointer {
        static { load(); }
        public Cv64suf() { allocate(); }
        public Cv64suf(int size) { allocateArray(size); }
        public Cv64suf(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Cv64suf position(int position) {
            return (Cv64suf)super.position(position);
        }

        public native long i();   public native Cv64suf i(long i);
        public native long u();   public native Cv64suf u(long u);
        public native double f(); public native Cv64suf f(double f);
    }

    // typedef int CVStatus;

    public static final int
            CV_StsOk                    =  0,
            CV_StsBackTrace             = -1,
            CV_StsError                 = -2,
            CV_StsInternal              = -3,
            CV_StsNoMem                 = -4,
            CV_StsBadArg                = -5,
            CV_StsBadFunc               = -6,
            CV_StsNoConv                = -7,
            CV_StsAutoTrace             = -8,
            CV_HeaderIsNull             = -9,
            CV_BadImageSize             = -10,
            CV_BadOffset                = -11,
            CV_BadDataPtr               = -12,
            CV_BadStep                  = -13,
            CV_BadModelOrChSeq          = -14,
            CV_BadNumChannels           = -15,
            CV_BadNumChannel1U          = -16,
            CV_BadDepth                 = -17,
            CV_BadAlphaChannel          = -18,
            CV_BadOrder                 = -19,
            CV_BadOrigin                = -20,
            CV_BadAlign                 = -21,
            CV_BadCallBack              = -22,
            CV_BadTileSize              = -23,
            CV_BadCOI                   = -24,
            CV_BadROISize               = -25,
            CV_MaskIsTiled              = -26,
            CV_StsNullPtr               = -27,
            CV_StsVecLengthErr          = -28,
            CV_StsFilterStructContentErr= -29,
            CV_StsKernelStructContentErr= -30,
            CV_StsFilterOffsetErr       = -31,
            CV_StsBadSize               = -201,
            CV_StsDivByZero             = -202,
            CV_StsInplaceNotSupported   = -203,
            CV_StsObjectNotFound        = -204,
            CV_StsUnmatchedFormats      = -205,
            CV_StsBadFlag               = -206,
            CV_StsBadPoint              = -207,
            CV_StsBadMask               = -208,
            CV_StsUnmatchedSizes        = -209,
            CV_StsUnsupportedFormat     = -210,
            CV_StsOutOfRange            = -211,
            CV_StsParseError            = -212,
            CV_StsNotImplemented        = -213,
            CV_StsBadMemBlock           = -214,
            CV_StsAssert                = -215,
            CV_GpuNotSupported          = -216,
            CV_GpuApiCallError          = -217,
            CV_OpenGlNotSupported       = -218,
            CV_OpenGlApiCallError       = -219;


    public static final long CV_RNG_COEFF = 4164903690L;
    public static class CvRNG extends LongPointer {
        static { load(); }
        public CvRNG() { this(null); allocate(); }
        public CvRNG(Pointer p) { super(p); }
        private native void allocate();
    }
    public static CvRNG cvRNG() {
        return cvRNG(-1);
    }
    public static CvRNG cvRNG(long seed/*=-1*/) {
        return (CvRNG)new CvRNG().put(seed != 0 ? seed : -1);
    }
    public static int cvRandInt(CvRNG rng) {
        long temp = rng.get();
        temp = ((temp&0xFFFFFFFFL)*CV_RNG_COEFF) + ((temp >> 32)&0xFFFFFFFFL);
        rng.put(temp);
        return (int)temp;
    }
    public static double cvRandReal(CvRNG rng) {
        return ((long)cvRandInt(rng)&0xFFFFFFFFL)*2.3283064365386962890625e-10;
    }


    public static final int
            IPL_DEPTH_SIGN = 0x80000000,

            IPL_DEPTH_1U   = 1,
            IPL_DEPTH_8U   = 8,
            IPL_DEPTH_16U  = 16,
            IPL_DEPTH_32F  = 32,

            IPL_DEPTH_8S   = (IPL_DEPTH_SIGN| 8),
            IPL_DEPTH_16S  = (IPL_DEPTH_SIGN|16),
            IPL_DEPTH_32S  = (IPL_DEPTH_SIGN|32),
            IPL_DEPTH_64F  = 64,

            IPL_DATA_ORDER_PIXEL = 0,
            IPL_DATA_ORDER_PLANE = 1,

            IPL_ORIGIN_TL  = 0,
            IPL_ORIGIN_BL  = 1,

            IPL_ALIGN_4BYTES  =  4,
            IPL_ALIGN_8BYTES  =  8,
            IPL_ALIGN_16BYTES = 16,
            IPL_ALIGN_32BYTES = 32,

            IPL_ALIGN_DWORD  = IPL_ALIGN_4BYTES,
            IPL_ALIGN_QWORD  = IPL_ALIGN_8BYTES,

            IPL_BORDER_CONSTANT  = 0,
            IPL_BORDER_REPLICATE = 1,
            IPL_BORDER_REFLECT   = 2,
            IPL_BORDER_WRAP      = 3;

    public static class IplImage extends CvArr {
        public IplImage() { allocate(); zero(); }
        public IplImage(int size) { allocateArray(size); zero(); }
        public IplImage(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public IplImage position(int position) {
            return (IplImage)super.position(position);
        }

        public static IplImage create(CvSize size, int depth, int channels) {
            IplImage i = cvCreateImage(size, depth, channels);
            if (i != null) {
                i.deallocator(new ReleaseDeallocator(i));
            }
            return i;
        }
        public static IplImage create(int width, int height, int depth, int channels) {
            return create(opencv_core.cvSize(width, height), depth, channels);
        }
        public static IplImage create(CvSize size, int depth, int channels, int origin) {
            IplImage i = create(size, depth, channels);
            if (i != null) {
                i.origin(origin);
            }
            return i;
        }
        public static IplImage create(int width, int height, int depth, int channels, int origin) {
            IplImage i = create(width, height, depth, channels);
            if (i != null) {
                i.origin(origin);
            }
            return i;
        }

        public static IplImage createHeader(CvSize size, int depth, int channels) {
            IplImage i = cvCreateImageHeader(size, depth, channels);
            if (i != null) {
                i.deallocator(new HeaderReleaseDeallocator(i));
            }
            return i;
        }
        public static IplImage createHeader(int width, int height, int depth, int channels) {
            return createHeader(opencv_core.cvSize(width, height), depth, channels);
        }
        public static IplImage createHeader(CvSize size, int depth, int channels, int origin) {
            IplImage i = createHeader(size, depth, channels);
            if (i != null) {
                i.origin(origin);
            }
            return i;
        }
        public static IplImage createHeader(int width, int height, int depth, int channels, int origin) {
            IplImage i = createHeader(width, height, depth, channels);
            if (i != null) {
                i.origin(origin);
            }
            return i;
        }

        public static IplImage createCompatible(IplImage template) {
            return createIfNotCompatible(null, template);
        }
        public static IplImage createIfNotCompatible(IplImage image, IplImage template) {
            if (image == null || image.width() != template.width() || image.height() != template.height() ||
                    image.depth() != template.depth() || image.nChannels() != template.nChannels()) {
                image = create(template.width(), template.height(),
                        template.depth(), template.nChannels(), template.origin());
                if (template.bufferedImage != null) {
                    image.bufferedImage = template.cloneBufferedImage();
                }
            }
            image.origin(template.origin());
            return image;
        }

        public static IplImage createFrom(BufferedImage image) {
            return createFrom(image, 1.0);
        }
        public static IplImage createFrom(BufferedImage image, double gamma) {
            return createFrom(image, gamma, false);
        }
        public static IplImage createFrom(BufferedImage image, double gamma, boolean flipChannels) {
            if (image == null) {
                return null;
            }
            SampleModel sm = image.getSampleModel();
            int depth = 0, numChannels = sm.getNumBands();
            switch (image.getType()) {
                case BufferedImage.TYPE_INT_RGB:
                case BufferedImage.TYPE_INT_ARGB:
                case BufferedImage.TYPE_INT_ARGB_PRE:
                case BufferedImage.TYPE_INT_BGR:
                    depth = IPL_DEPTH_8U;
                    numChannels = 4;
                    break;
            }
            if (depth == 0 || numChannels == 0) {
                switch (sm.getDataType()) {
                    case DataBuffer.TYPE_BYTE:   depth = IPL_DEPTH_8U;  break;
                    case DataBuffer.TYPE_USHORT: depth = IPL_DEPTH_16U; break;
                    case DataBuffer.TYPE_SHORT:  depth = IPL_DEPTH_16S; break;
                    case DataBuffer.TYPE_INT:    depth = IPL_DEPTH_32S; break;
                    case DataBuffer.TYPE_FLOAT:  depth = IPL_DEPTH_32F; break;
                    case DataBuffer.TYPE_DOUBLE: depth = IPL_DEPTH_64F; break;
                    default: assert false;
                }
            }
            IplImage i = create(image.getWidth(), image.getHeight(), depth, numChannels);
            i.copyFrom(image, gamma, flipChannels);
            return i;
        }

        @Override public IplImage clone() {
            IplImage i = cvCloneImage(this);
            if (i != null) {
                i.deallocator(new ReleaseDeallocator(i));
            }
            if (bufferedImage != null) {
                i.bufferedImage = cloneBufferedImage();
            }
            return i;
        }

        protected BufferedImage cloneBufferedImage() {
            if (bufferedImage == null) {
                return null;
            }
            BufferedImage bi = (BufferedImage)bufferedImage;
            int type = bi.getType();
            if (type == BufferedImage.TYPE_CUSTOM) {
                return new BufferedImage(bi.getColorModel(),
                        bi.copyData(null), bi.isAlphaPremultiplied(), null);
            } else {
                return new BufferedImage(bi.getWidth(), bi.getHeight(), type);
            }
        }

        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends IplImage implements Deallocator {
            ReleaseDeallocator(IplImage p) { super(p); }
            @Override public void deallocate() { cvReleaseImage(this); }
        }
        protected static class HeaderReleaseDeallocator extends IplImage implements Deallocator {
            HeaderReleaseDeallocator(IplImage p) { super(p); }
            @Override public void deallocate() { cvReleaseImageHeader(this); }
        }


        public native int nSize();                   public native IplImage nSize(int nSize);
        public native int ID();                      public native IplImage ID(int ID);
        public native int nChannels();               public native IplImage nChannels(int nChannels);
        public native int alphaChannel();            public native IplImage alphaChannel(int alphaChannel);
        public native int depth();                   public native IplImage depth(int depth);
        public native int/*[4]*/ colorModel(int i);  public native IplImage colorModel(int i, int colorModel);
        public native int/*[4]*/ channelSeq(int i);  public native IplImage channelSeq(int i, int channelSeq);
        public native int dataOrder();               public native IplImage dataOrder(int dataOrder);
        public native int origin();                  public native IplImage origin(int origin);
        public native int align();                   public native IplImage align(int align);
        public native int width();                   public native IplImage width(int width);
        public native int height();                  public native IplImage height(int height);
        public native IplROI roi();                  public native IplImage roi(IplROI roi);
        public native IplImage maskROI();            public native IplImage maskROI(IplImage maskROI);
        public native Pointer imageId();             public native IplImage imageId(Pointer imageId);
        public native IplTileInfo tileInfo();        public native IplImage tileInfo(IplTileInfo tileInfo);
        public native int imageSize();               public native IplImage imageSize(int imageSize);
        @Cast("char*")
        public native BytePointer imageData();       public native IplImage imageData(BytePointer imageData);
        public native @MemberSetter IplImage imageData(@Cast("char*") ByteBuffer imageData);
        public native int widthStep();               public native IplImage widthStep(int widthStep);
        public native int/*[4]*/ BorderMode(int i);  public native IplImage BorderMode(int i, int BorderMode);
        public native int/*[4]*/ BorderConst(int i); public native IplImage BorderConst(int i, int BorderConst);
        @Cast("char*")
        public native BytePointer imageDataOrigin(); public native IplImage imageDataOrigin(BytePointer imageDataOrigin);


        public double highValue() {
            double highValue = 0.0;
            switch (depth()) {
                case IPL_DEPTH_8U:  highValue = 0xFF;              break;
                case IPL_DEPTH_16U: highValue = 0xFFFF;            break;
                case IPL_DEPTH_8S:  highValue = Byte.MAX_VALUE;    break;
                case IPL_DEPTH_16S: highValue = Short.MAX_VALUE;   break;
                case IPL_DEPTH_32S: highValue = Integer.MAX_VALUE; break;
                case IPL_DEPTH_1U:
                case IPL_DEPTH_32F:
                case IPL_DEPTH_64F: highValue = 1.0; break;
                default: assert false;
            }
            return highValue;
        }

        public CvSize cvSize() { return opencv_core.cvSize(width(), height()); }

        public ByteBuffer   getByteBuffer  (int index) { return imageData().position(index).capacity(imageSize()).asByteBuffer(); }
        public ShortBuffer  getShortBuffer (int index) { return getByteBuffer(index*2).asShortBuffer();  }
        public IntBuffer    getIntBuffer   (int index) { return getByteBuffer(index*4).asIntBuffer();    }
        public FloatBuffer  getFloatBuffer (int index) { return getByteBuffer(index*4).asFloatBuffer();  }
        public DoubleBuffer getDoubleBuffer(int index) { return getByteBuffer(index*8).asDoubleBuffer(); }
        public ByteBuffer   getByteBuffer()   { return getByteBuffer  (0); }
        public ShortBuffer  getShortBuffer()  { return getShortBuffer (0); }
        public IntBuffer    getIntBuffer()    { return getIntBuffer   (0); }
        public FloatBuffer  getFloatBuffer()  { return getFloatBuffer (0); }
        public DoubleBuffer getDoubleBuffer() { return getDoubleBuffer(0); }

        public static final byte[]
                gamma22    = new byte[256],
                gamma22inv = new byte[256];
        static {
            for (int i = 0; i < 256; i++) {
                gamma22[i]    = (byte)Math.round(Math.pow(i/255.0,   2.2)*255.0);
                gamma22inv[i] = (byte)Math.round(Math.pow(i/255.0, 1/2.2)*255.0);
            }
        }
        public static int decodeGamma22(int value) {
            return gamma22[value & 0xFF] & 0xFF;
        }
        public static int encodeGamma22(int value) {
            return gamma22inv[value & 0xFF] & 0xFF;
        }
        public static void flipCopyWithGamma(ByteBuffer srcBuf, int srcStep,
                ByteBuffer dstBuf, int dstStep, boolean signed, double gamma, boolean flip, int channels) {
            assert srcBuf != dstBuf;
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            byte[] buffer = new byte[channels];
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (signed) {
                    if (channels > 1) {
                        for (int x = 0; x < w; x+=channels) {
                            for (int z = 0; z < channels; z++) {
                                int in = srcBuf.get();
                                byte out;
                                if (gamma == 1.0) {
                                    out = (byte)in;
                                } else {
                                    out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                                }
                                buffer[z] = out;
                            }
                            for (int z = channels-1; z >= 0; z--) {
                                dstBuf.put(buffer[z]);
                            }
                        }
                    } else {
                        for (int x = 0; x < w; x++) {
                            int in = srcBuf.get();
                            byte out;
                            if (gamma == 1.0) {
                                out = (byte)in;
                            } else {
                                out = (byte)Math.round(Math.pow((double)in/Byte.MAX_VALUE, gamma)*Byte.MAX_VALUE);
                            }
                            dstBuf.put(out);
                        }
                    }
                } else {
                    if (channels > 1) {
                        for (int x = 0; x < w; x+=channels) {
                            for (int z = 0; z < channels; z++) {
                                byte out;
                                int in = srcBuf.get() & 0xFF;
                                if (gamma == 1.0) {
                                    out = (byte)in;
                                } else if (gamma == 2.2) {
                                    out = gamma22[in];
                                } else if (gamma == 1/2.2) {
                                    out = gamma22inv[in];
                                } else {
                                    out = (byte)Math.round(Math.pow((double)in/0xFF, gamma)*0xFF);
                                }
                                buffer[z] = out;
                            }
                            for (int z = channels-1; z >= 0; z--) {
                                dstBuf.put(buffer[z]);
                            }
                        }
                    } else {
                        for (int x = 0; x < w; x++) {
                            byte out;
                            int in = srcBuf.get() & 0xFF;
                            if (gamma == 1.0) {
                                out = (byte)in;
                            } else if (gamma == 2.2) {
                                out = gamma22[in];
                            } else if (gamma == 1/2.2) {
                                out = gamma22inv[in];
                            } else {
                                out = (byte)Math.round(Math.pow((double)in/0xFF, gamma)*0xFF);
                            }
                            dstBuf.put(out);
                        }
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(ShortBuffer srcBuf, int srcStep,
                ShortBuffer dstBuf, int dstStep, boolean signed, double gamma, boolean flip, int channels) {
            assert srcBuf != dstBuf;
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            short[] buffer = new short[channels];
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (signed) {
                    if (channels > 1) {
                        for (int x = 0; x < w; x+=channels) {
                            for (int z = 0; z < channels; z++) {
                                int in = srcBuf.get();
                                short out;
                                if (gamma == 1.0) {
                                    out = (short)in;
                                } else {
                                    out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                                }
                                buffer[z] = out;
                            }
                            for (int z = channels-1; z >= 0; z--) {
                                dstBuf.put(buffer[z]);
                            }
                        }
                    } else {
                        for (int x = 0; x < w; x++) {
                            int in = srcBuf.get();
                            short out;
                            if (gamma == 1.0) {
                                out = (short)in;
                            } else {
                                out = (short)Math.round(Math.pow((double)in/Short.MAX_VALUE, gamma)*Short.MAX_VALUE);
                            }
                            dstBuf.put(out);
                        }
                    }
                } else {
                    if (channels > 1) {
                        for (int x = 0; x < w; x+=channels) {
                            for (int z = 0; z < channels; z++) {
                                int in = srcBuf.get();
                                short out;
                                if (gamma == 1.0) {
                                    out = (short)in;
                                } else {
                                    out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                                }
                                buffer[z] = out;
                            }
                            for (int z = channels-1; z >= 0; z--) {
                                dstBuf.put(buffer[z]);
                            }
                        }
                    } else {
                        for (int x = 0; x < w; x++) {
                            int in = srcBuf.get() & 0xFFFF;
                            short out;
                            if (gamma == 1.0) {
                                out = (short)in;
                            } else {
                                out = (short)Math.round(Math.pow((double)in/0xFFFF, gamma)*0xFFFF);
                            }
                            dstBuf.put(out);
                        }
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(IntBuffer srcBuf, int srcStep,
                IntBuffer dstBuf, int dstStep, double gamma, boolean flip, int channels) {
            assert srcBuf != dstBuf;
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            int[] buffer = new int[channels];
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            int in = srcBuf.get();
                            int out;
                            if (gamma == 1.0) {
                                out = (int)in;
                            } else {
                                out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        int in = srcBuf.get();
                        int out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = (int)Math.round(Math.pow((double)in/Integer.MAX_VALUE, gamma)*Integer.MAX_VALUE);
                        }
                        dstBuf.put(out);
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(FloatBuffer srcBuf, int srcStep,
                FloatBuffer dstBuf, int dstStep, double gamma, boolean flip, int channels) {
            assert srcBuf != dstBuf;
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            float[] buffer = new float[channels];
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            float in = srcBuf.get();
                            float out;
                            if (gamma == 1.0) {
                                out = in;
                            } else {
                                out = (float)Math.pow(in, gamma);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        float in = srcBuf.get();
                        float out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = (float)Math.pow(in, gamma);
                        }
                        dstBuf.put(out);
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public static void flipCopyWithGamma(DoubleBuffer srcBuf, int srcStep,
                DoubleBuffer dstBuf, int dstStep, double gamma, boolean flip, int channels) {
            assert srcBuf != dstBuf;
            int w = Math.min(srcStep, dstStep);
            int srcLine = srcBuf.position(), dstLine = dstBuf.position();
            double[] buffer = new double[channels];
            while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
                if (flip) {
                    srcBuf.position(srcBuf.capacity() - srcLine - srcStep);
                } else {
                    srcBuf.position(srcLine);
                }
                dstBuf.position(dstLine);
                w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
                if (channels > 1) {
                    for (int x = 0; x < w; x+=channels) {
                        for (int z = 0; z < channels; z++) {
                            double in = srcBuf.get();
                            double out;
                            if (gamma == 1.0) {
                                out = in;
                            } else {
                                out = Math.pow(in, gamma);
                            }
                            buffer[z] = out;
                        }
                        for (int z = channels-1; z >= 0; z--) {
                            dstBuf.put(buffer[z]);
                        }
                    }
                } else {
                    for (int x = 0; x < w; x++) {
                        double in = srcBuf.get();
                        double out;
                        if (gamma == 1.0) {
                            out = in;
                        } else {
                            out = Math.pow(in, gamma);
                        }
                        dstBuf.put(out);
                    }
                }
                srcLine += srcStep;
                dstLine += dstStep;
            }
        }
        public void applyGamma(double gamma) {
            if (gamma == 1.0) {
                return;
            }
            switch (depth()) {
                case IPL_DEPTH_8U:
                    flipCopyWithGamma(getByteBuffer(), widthStep(), getByteBuffer(), widthStep(), false, gamma, false, 0);
                    break;
                case IPL_DEPTH_8S:
                    flipCopyWithGamma(getByteBuffer(), widthStep(), getByteBuffer(), widthStep(), true, gamma, false, 0);
                    break;
                case IPL_DEPTH_16U:
                    flipCopyWithGamma(getShortBuffer(), widthStep()/2, getShortBuffer(), widthStep()/2, false, gamma, false, 0);
                    break;
                case IPL_DEPTH_16S:
                    flipCopyWithGamma(getShortBuffer(), widthStep()/2, getShortBuffer(), widthStep()/2, true, gamma, false, 0);
                    break;
                case IPL_DEPTH_32S:
                    flipCopyWithGamma(getFloatBuffer(), widthStep()/4, getFloatBuffer(), widthStep()/4, gamma, false, 0);
                    break;
                case IPL_DEPTH_32F:
                    flipCopyWithGamma(getFloatBuffer(), widthStep()/4, getFloatBuffer(), widthStep()/4, gamma, false, 0);
                    break;
                case IPL_DEPTH_64F:
                    flipCopyWithGamma(getDoubleBuffer(), widthStep()/8, getDoubleBuffer(), widthStep()/8, gamma, false, 0);
                    break;
                default:
                    assert false;
            }
        }


        public void copyTo(BufferedImage image) {
            copyTo(image, 1.0);
        }
        public void copyTo(BufferedImage image, double gamma) {
            copyTo(image, gamma, false);
        }
        public void copyTo(BufferedImage image, double gamma, boolean flipChannels) {
            Rectangle r = null;
            IplROI roi = roi();
            if (roi != null) {
                r = new Rectangle(roi.xOffset(), roi.yOffset(), roi.width(), roi.height());
            }
            copyTo(image, gamma, flipChannels, r);
        }
        public void copyTo(BufferedImage image, double gamma, boolean flipChannels, Rectangle roi) {
            boolean flip = origin() == IPL_ORIGIN_BL; // need to add support for ROI..

            ByteBuffer in  = getByteBuffer(roi == null ? 0 : roi.y*widthStep() + roi.x*nChannels());
            SampleModel sm = image.getSampleModel();
            Raster r       = image.getRaster();
            DataBuffer out = r.getDataBuffer();
            int x = -r.getSampleModelTranslateX();
            int y = -r.getSampleModelTranslateY();
            int step = sm.getWidth()*sm.getNumBands();
            int channels = sm.getNumBands();
            if (sm instanceof ComponentSampleModel) {
                step = ((ComponentSampleModel)sm).getScanlineStride();
                channels = ((ComponentSampleModel)sm).getPixelStride();
            } else if (sm instanceof SinglePixelPackedSampleModel) {
                step = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
                channels = 1;
            } else if (sm instanceof MultiPixelPackedSampleModel) {
                step = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
                channels = ((MultiPixelPackedSampleModel)sm).getPixelBitStride()/8; // ??
            }
            int start = y*step + x*channels;

            if (out instanceof DataBufferByte) {
                byte[] a = ((DataBufferByte)out).getData();
                flipCopyWithGamma(in, widthStep(), ByteBuffer.wrap(a, start, a.length - start), step, false, gamma, flip, flipChannels ? channels : 0);
            } else if (out instanceof DataBufferDouble) {
                double[] a = ((DataBufferDouble)out).getData();
                flipCopyWithGamma(in.asDoubleBuffer(), widthStep()/8, DoubleBuffer.wrap(a, start, a.length - start), step, gamma, flip, flipChannels ? channels : 0);
            } else if (out instanceof DataBufferFloat) {
                float[] a = ((DataBufferFloat)out).getData();
                flipCopyWithGamma(in.asFloatBuffer(), widthStep()/4, FloatBuffer.wrap(a, start, a.length - start), step, gamma, flip, flipChannels ? channels : 0);
            } else if (out instanceof DataBufferInt) {
                int[] a = ((DataBufferInt)out).getData();
                flipCopyWithGamma(in.asIntBuffer(), widthStep()/4, IntBuffer.wrap(a, start, a.length - start), step, gamma, flip, flipChannels ? channels : 0);
            } else if (out instanceof DataBufferShort) {
                short[] a = ((DataBufferShort)out).getData();
                flipCopyWithGamma(in.asShortBuffer(), widthStep()/2, ShortBuffer.wrap(a, start, a.length - start), step, true, gamma, flip, flipChannels ? channels : 0);
            } else if (out instanceof DataBufferUShort) {
                short[] a = ((DataBufferUShort)out).getData();
                flipCopyWithGamma(in.asShortBuffer(), widthStep()/2, ShortBuffer.wrap(a, start, a.length - start), step, false, gamma, flip, flipChannels ? channels : 0);
            } else {
                assert false;
            }
        }

        public void copyFrom(BufferedImage image) {
            copyFrom(image, 1.0);
        }
        public void copyFrom(BufferedImage image, double gamma) {
            copyFrom(image, gamma, false);
        }
        public void copyFrom(BufferedImage image, double gamma, boolean flipChannels) {
            Rectangle r = null;
            IplROI roi = roi();
            if (roi != null) {
                r = new Rectangle(roi.xOffset(), roi.yOffset(), roi.width(), roi.height());
            }
            copyFrom(image, gamma, flipChannels, r);
        }
        public void copyFrom(BufferedImage image, double gamma, boolean flipChannels, Rectangle roi) {
            origin(IPL_ORIGIN_TL);

            ByteBuffer out = getByteBuffer(roi == null ? 0 : roi.y*widthStep() + roi.x);
            SampleModel sm = image.getSampleModel();
            Raster r       = image.getRaster();
            DataBuffer in  = r.getDataBuffer();
            int x = -r.getSampleModelTranslateX();
            int y = -r.getSampleModelTranslateY();
            int step = sm.getWidth()*sm.getNumBands();
            int channels = sm.getNumBands();
            if (sm instanceof ComponentSampleModel) {
                step = ((ComponentSampleModel)sm).getScanlineStride();
                channels = ((ComponentSampleModel)sm).getPixelStride();
            } else if (sm instanceof SinglePixelPackedSampleModel) {
                step = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
                channels = 1;
            } else if (sm instanceof MultiPixelPackedSampleModel) {
                step = ((MultiPixelPackedSampleModel)sm).getScanlineStride();
                channels = ((MultiPixelPackedSampleModel)sm).getPixelBitStride()/8; // ??
            }
            int start = y*step + x*channels;

            if (in instanceof DataBufferByte) {
                byte[] a = ((DataBufferByte)in).getData();
                flipCopyWithGamma(ByteBuffer.wrap(a, start, a.length - start), step, out, widthStep(), false, gamma, false, flipChannels ? channels : 0);
            } else if (in instanceof DataBufferDouble) {
                double[] a = ((DataBufferDouble)in).getData();
                flipCopyWithGamma(DoubleBuffer.wrap(a, start, a.length - start), step, out.asDoubleBuffer(), widthStep()/8, gamma, false, flipChannels ? channels : 0);
            } else if (in instanceof DataBufferFloat) {
                float[] a = ((DataBufferFloat)in).getData();
                flipCopyWithGamma(FloatBuffer.wrap(a, start, a.length - start), step, out.asFloatBuffer(), widthStep()/4, gamma, false, flipChannels ? channels : 0);
            } else if (in instanceof DataBufferInt) {
                int[] a = ((DataBufferInt)in).getData();
                flipCopyWithGamma(IntBuffer.wrap(a, start, a.length - start), step, out.asIntBuffer(), widthStep()/4, gamma, false, flipChannels ? channels : 0);
            } else if (in instanceof DataBufferShort) {
                short[] a = ((DataBufferShort)in).getData();
                flipCopyWithGamma(ShortBuffer.wrap(a, start, a.length - start), step, out.asShortBuffer(), widthStep()/2, true, gamma, false, flipChannels ? channels : 0);
            } else if (in instanceof DataBufferUShort) {
                short[] a = ((DataBufferUShort)in).getData();
                flipCopyWithGamma(ShortBuffer.wrap(a, start, a.length - start), step, out.asShortBuffer(), widthStep()/2, false, gamma, false, flipChannels ? channels : 0);
            } else {
                assert false;
            }
            if (bufferedImage == null && roi == null &&
                    image.getWidth() == width() && image.getHeight() == height()) {
                bufferedImage = image;
            }
        }
        // not declared as BufferedImage => Android friendly
        private Object bufferedImage = null;
        public int getBufferedImageType() {
            // precanned BufferedImage types are confusing... in practice though,
            // they all use the sRGB color model when blitting:
            //     http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5051418
            // and we should use them because they are *A LOT* faster with Java 2D.
            // workaround: do gamma correction ourselves ("gamma" parameter)
            //             since we'll never use getRGB() and setRGB(), right?
            int type = BufferedImage.TYPE_CUSTOM;
            if (nChannels() == 1) {
                if (depth() == IPL_DEPTH_8U || depth() == IPL_DEPTH_8S) {
                    type = BufferedImage.TYPE_BYTE_GRAY;
                } else if (depth() == IPL_DEPTH_16U) {
                    type = BufferedImage.TYPE_USHORT_GRAY;
                }
            } else if (nChannels() == 3) {
                if (depth() == IPL_DEPTH_8U || depth() == IPL_DEPTH_8S) {
                    type = BufferedImage.TYPE_3BYTE_BGR;
                }
            } else if (nChannels() == 4) {
                // The channels end up reversed of what we need for OpenCL.
                // We work around this in copyTo() and copyFrom() by
                // inversing the channels to let us use RGBA in our IplImage.
                if (depth() == IPL_DEPTH_8U || depth() == IPL_DEPTH_8S) {
                    type = BufferedImage.TYPE_4BYTE_ABGR;
                }
            }
            return type;
        }
        public BufferedImage getBufferedImage() {
            return getBufferedImage(1.0);
        }
        public BufferedImage getBufferedImage(double gamma) {
            return getBufferedImage(gamma, false);
        }
        public BufferedImage getBufferedImage(double gamma, boolean flipChannels) {
            return getBufferedImage(gamma, flipChannels, null);
        }
        public BufferedImage getBufferedImage(double gamma, boolean flipChannels, ColorSpace cs) {
            int type = getBufferedImageType();

            if (bufferedImage == null && type != BufferedImage.TYPE_CUSTOM && cs == null) {
                bufferedImage = new BufferedImage(width(), height(), type);
            }

            if (bufferedImage == null) {
                boolean alpha = false;
                int[] offsets = null;
                if (nChannels() == 1) {
                    alpha = false;
                    if (cs == null) {
                        cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    }
                    offsets = new int[] {0};
                } else if (nChannels() == 3) {
                    alpha = false;
                    if (cs == null) {
                        cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                    }
                    // raster in "BGR" order like OpenCV..
                    offsets = new int[] {2, 1, 0};
                } else if (nChannels() == 4) {
                    alpha = true;
                    if (cs == null) {
                        cs = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                    }
                    // raster in "RGBA" order for OpenCL.. alpha needs to be last
                    offsets = new int[] {0, 1, 2, 3};
                } else {
                    assert false;
                }

                ColorModel cm = null;
                WritableRaster wr = null;
                if (depth() == IPL_DEPTH_8U || depth() == IPL_DEPTH_8S) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_BYTE, width(), height(), nChannels(), widthStep(),
                            offsets), null);
                } else if (depth() == IPL_DEPTH_16U) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_USHORT, width(), height(), nChannels(), widthStep()/2,
                            offsets), null);
                } else if (depth() == IPL_DEPTH_16S) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_SHORT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_SHORT, width(), height(), nChannels(), widthStep()/2,
                            offsets), null);
                } else if (depth() == IPL_DEPTH_32S) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_INT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_INT, width(), height(), nChannels(), widthStep()/4,
                            offsets), null);
                } else if (depth() == IPL_DEPTH_32F) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_FLOAT, width(), height(), nChannels(), widthStep()/4,
                            offsets), null);
                } else if (depth() == IPL_DEPTH_64F) {
                    cm = new ComponentColorModel(cs, alpha,
                            false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
                    wr = Raster.createWritableRaster(new ComponentSampleModel(
                            DataBuffer.TYPE_DOUBLE, width(), height(), nChannels(), widthStep()/8,
                            offsets), null);
                } else {
                    assert false;
                }

                bufferedImage = new BufferedImage(cm, wr, false, null);
            }

            if (bufferedImage != null) {
                IplROI roi = roi();
                if (roi != null) {
                    copyTo(((BufferedImage)bufferedImage).getSubimage(roi.xOffset(), roi.yOffset(), roi.width(), roi.height()), gamma, flipChannels);
                } else {
                    copyTo((BufferedImage)bufferedImage, gamma, flipChannels);
                }
            }

            return (BufferedImage)bufferedImage;
        }

        public CvMat asCvMat() {
            CvMat mat = new CvMat();
            cvGetMat(this, mat, null, 0);
            return mat;
        }

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                return "IplImage[width=" + width() + ",height=" + height() +
                               ",depth=" + depth() + ",nChannels=" + nChannels() + "]";
            }
        }
    }

    @Opaque public static class IplTileInfo extends Pointer {
        static { load(); }
        public IplTileInfo() { }
        public IplTileInfo(Pointer p) { super(p); }
    }

    public static class IplROI extends Pointer {
        static { load(); }
        public IplROI() { allocate(); }
        public IplROI(int size) { allocateArray(size); }
        public IplROI(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public IplROI position(int position) {
            return (IplROI)super.position(position);
        }

        public native int coi();     public native IplROI coi(int coi);
        public native int xOffset(); public native IplROI xOffset(int xOffset);
        public native int yOffset(); public native IplROI yOffset(int yOffset);
        public native int width();   public native IplROI width(int width);
        public native int height();  public native IplROI height(int height);
    }

    public static final int
            IPL_IMAGE_HEADER = 1,
            IPL_IMAGE_DATA   = 2,
            IPL_IMAGE_ROI    = 4,

            IPL_BORDER_REFLECT_101   = 4,
            IPL_BORDER_TRANSPARENT   = 5,

            IPL_IMAGE_MAGIC_VAL  = load() == null ? 0 : sizeof(IplImage.class);
    public static final String CV_TYPE_NAME_IMAGE = "opencv-image";

    public static boolean CV_IS_IMAGE_HDR(CvArr img) {
        return img != null && new IplImage(img).nSize() == sizeof(IplImage.class);
    }
    public static boolean CV_IS_IMAGE(CvArr img) {
        return CV_IS_IMAGE_HDR(img) && new IplImage(img).imageData() != null;
    }


    public static final int
            CV_CN_MAX    = 512,
            CV_CN_SHIFT  = 3,
            CV_DEPTH_MAX = (1 << CV_CN_SHIFT),

            CV_8U  = 0,
            CV_8S  = 1,
            CV_16U = 2,
            CV_16S = 3,
            CV_32S = 4,
            CV_32F = 5,
            CV_64F = 6,
            CV_USRTYPE1 = 7,

            CV_MAT_DEPTH_MASK      = (CV_DEPTH_MAX - 1);
    public static int CV_MAT_DEPTH(int flags) { return flags & CV_MAT_DEPTH_MASK; }

    public static int CV_MAKETYPE(int depth, int cn) { return CV_MAT_DEPTH(depth) + ((cn-1) << CV_CN_SHIFT); }
    public static int CV_MAKE_TYPE(int depth, int cn) { return CV_MAKETYPE(depth, cn); }

    public static int CV_8UC (int n) { return CV_MAKETYPE(CV_8U,  n); }
    public static int CV_8SC (int n) { return CV_MAKETYPE(CV_8S,  n); }
    public static int CV_16UC(int n) { return CV_MAKETYPE(CV_8U,  n); }
    public static int CV_16SC(int n) { return CV_MAKETYPE(CV_16S, n); }
    public static int CV_32SC(int n) { return CV_MAKETYPE(CV_32S, n); }
    public static int CV_32FC(int n) { return CV_MAKETYPE(CV_32F, n); }
    public static int CV_64FC(int n) { return CV_MAKETYPE(CV_64F, n); }

    public static final int
            CV_8UC1 = CV_MAKETYPE(CV_8U,1),
            CV_8UC2 = CV_MAKETYPE(CV_8U,2),
            CV_8UC3 = CV_MAKETYPE(CV_8U,3),
            CV_8UC4 = CV_MAKETYPE(CV_8U,4),

            CV_8SC1 = CV_MAKETYPE(CV_8S,1),
            CV_8SC2 = CV_MAKETYPE(CV_8S,2),
            CV_8SC3 = CV_MAKETYPE(CV_8S,3),
            CV_8SC4 = CV_MAKETYPE(CV_8S,4),

            CV_16UC1 = CV_MAKETYPE(CV_16U,1),
            CV_16UC2 = CV_MAKETYPE(CV_16U,2),
            CV_16UC3 = CV_MAKETYPE(CV_16U,3),
            CV_16UC4 = CV_MAKETYPE(CV_16U,4),

            CV_16SC1 = CV_MAKETYPE(CV_16S,1),
            CV_16SC2 = CV_MAKETYPE(CV_16S,2),
            CV_16SC3 = CV_MAKETYPE(CV_16S,3),
            CV_16SC4 = CV_MAKETYPE(CV_16S,4),

            CV_32SC1 = CV_MAKETYPE(CV_32S,1),
            CV_32SC2 = CV_MAKETYPE(CV_32S,2),
            CV_32SC3 = CV_MAKETYPE(CV_32S,3),
            CV_32SC4 = CV_MAKETYPE(CV_32S,4),

            CV_32FC1 = CV_MAKETYPE(CV_32F,1),
            CV_32FC2 = CV_MAKETYPE(CV_32F,2),
            CV_32FC3 = CV_MAKETYPE(CV_32F,3),
            CV_32FC4 = CV_MAKETYPE(CV_32F,4),

            CV_64FC1 = CV_MAKETYPE(CV_64F,1),
            CV_64FC2 = CV_MAKETYPE(CV_64F,2),
            CV_64FC3 = CV_MAKETYPE(CV_64F,3),
            CV_64FC4 = CV_MAKETYPE(CV_64F,4),

            CV_AUTO_STEP = 0x7fffffff;
    public static final CvSlice CV_WHOLE_ARR = load() == null ? null : cvSlice(0, 0x3fffffff);

    public static final int
            CV_MAT_CN_MASK         = ((CV_CN_MAX - 1) << CV_CN_SHIFT),
            CV_MAT_TYPE_MASK       = (CV_DEPTH_MAX*CV_CN_MAX - 1),
            CV_MAT_CONT_FLAG_SHIFT = 14,
            CV_MAT_CONT_FLAG       = (1 << CV_MAT_CONT_FLAG_SHIFT),
            CV_MAT_TEMP_FLAG_SHIFT = 15,
            CV_MAT_TEMP_FLAG       = (1 << CV_MAT_TEMP_FLAG_SHIFT);
    public static int CV_MAT_CN(int flags) { return ((flags & CV_MAT_CN_MASK) >> CV_CN_SHIFT) + 1; }
    public static int CV_MAT_TYPE(int flags) { return flags & CV_MAT_TYPE_MASK; }
    public static boolean CV_IS_MAT_CONT(int flags) { return (flags & CV_MAT_CONT_FLAG) != 0; }
    public static boolean CV_IS_CONT_MAT(int flags) { return CV_IS_MAT_CONT(flags); }
    public static boolean CV_IS_TEMP_MAT(int flags) { return (flags & CV_MAT_TEMP_FLAG) != 0;}

    public static final int
            CV_MAGIC_MASK    = 0xFFFF0000,
            CV_MAT_MAGIC_VAL = 0x42420000;
    public static final String CV_TYPE_NAME_MAT   = "opencv-matrix";

    public static class CvMat extends CvArr {
        public CvMat() { allocate(); zero(); }
        public CvMat(int size) { allocateArray(size); zero(); }
        public CvMat(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMat position(int position) {
            return (CvMat)super.position(position);
        }

        public static CvMat create(int rows, int cols, int type) {
            CvMat m = cvCreateMat(rows, cols, type);
            if (m != null) {
                m.fullSize = m.size();
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }
        public static CvMat create(int rows, int cols, int depth, int channels) {
            return create(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static CvMat create(int rows, int cols) {
            return create(rows, cols, CV_64F, 1);
        }

        public static CvMat createHeader(int rows, int cols, int type) {
            CvMat m = cvCreateMatHeader(rows, cols, type);
            if (m != null) {
                m.fullSize = m.size();
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }
        public static CvMat createHeader(int rows, int cols, int depth, int channels) {
            return createHeader(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static CvMat createHeader(int rows, int cols) {
            return createHeader(rows, cols, CV_64F, 1);
        }

        public static ThreadLocal<CvMat> createThreadLocal(final int rows, final int cols, final int type) {
            return new ThreadLocal<CvMat>() { @Override protected CvMat initialValue() {
                return CvMat.create(rows, cols, type);
            }};
        }
        public static ThreadLocal<CvMat> createThreadLocal(int rows, int cols, int depth, int channels) {
            return createThreadLocal(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static ThreadLocal<CvMat> createThreadLocal(int rows, int cols) {
            return createThreadLocal(rows, cols, CV_64F, 1);
        }

        public static ThreadLocal<CvMat> createHeaderThreadLocal(final int rows, final int cols, final int type) {
            return new ThreadLocal<CvMat>() { @Override protected CvMat initialValue() {
                return CvMat.createHeader(rows, cols, type);
            }};
        }
        public static ThreadLocal<CvMat> createHeaderThreadLocal(int rows, int cols, int depth, int channels) {
            return createHeaderThreadLocal(rows, cols, CV_MAKETYPE(depth, channels));
        }
        public static ThreadLocal<CvMat> createHeaderThreadLocal(int rows, int cols) {
            return createHeaderThreadLocal(rows, cols, CV_64F, 1);
        }

        @Override public CvMat clone() {
            CvMat m = cvCloneMat(this);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }

        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends CvMat implements Deallocator {
            ReleaseDeallocator(CvMat m) { super(m); }
            @Override public void deallocate() { cvReleaseMat(this); }
        }

        @Name("type")
        public native int raw_type(); public native CvMat raw_type(int type);
        public native int step();     public native CvMat step(int step);

        public native IntPointer refcount(); public native CvMat refcount(IntPointer type);
        public native int hdr_refcount();    public native CvMat hdr_refcount(int hdr_refcount);

        // union { } data
        @Cast("uchar*")
        @Name("data.ptr") public native BytePointer   data_ptr(); public native CvMat data_ptr(BytePointer ptr);
        @Name("data.fl")  public native FloatPointer  data_fl();  public native CvMat data_fl(FloatPointer fl);
        @Name("data.db")  public native DoublePointer data_db();  public native CvMat data_db(DoublePointer db);
        @Name("data.i")   public native IntPointer    data_i();   public native CvMat data_i(IntPointer i);
        @Name("data.s")   public native ShortPointer  data_s();   public native CvMat data_s(ShortPointer s);

        public native int rows(); public native CvMat rows(int rows);
        public native int cols(); public native CvMat cols(int cols);


        public int type() {
            return CV_MAT_TYPE(raw_type());
        }
        public void type(int depth, int cn) {
            raw_type(CV_MAKETYPE(depth, cn) | CV_MAT_MAGIC_VAL);
        }
        public int depth() {
            return CV_MAT_DEPTH(type());
        }
        public int channels() {
            return CV_MAT_CN(type());
        }
        public int nChannels() {
            return CV_MAT_CN(type());
        }
        public boolean isContinuous() {
            return CV_IS_MAT_CONT(type());
        }
        public int elemSize() {
            switch (depth()) {
                case CV_8U:
                case CV_8S:  return 1;
                case CV_16U:
                case CV_16S: return 2;
                case CV_32S:
                case CV_32F: return 4;
                case CV_64F: return 8;
                default: assert false;
            }
            return 0;
        }
        public int length() {
            return rows()*cols();
        }
        public int total() {
            return rows()*cols();
        }
        public boolean empty() {
            return length() == 0;
        }
        public int size() {
            // step == 0 when height == 1...
            int rows = rows();
            return cols()*elemSize()*channels() + (rows > 1 ? step()*(rows-1) : 0);
        }

        public CvSize cvSize() { return opencv_core.cvSize(cols(), rows()); }

        public void reset() {
            fullSize = 0;
            byteBuffer = null;
            shortBuffer = null;
            intBuffer = null;
            floatBuffer = null;
            doubleBuffer = null;
        }

        private int fullSize = 0;
        private int fullSize() { return fullSize > 0 ? fullSize : (fullSize = size()); }
        private ByteBuffer byteBuffer = null;
        private ShortBuffer shortBuffer = null;
        private IntBuffer intBuffer = null;
        private FloatBuffer floatBuffer = null;
        private DoubleBuffer doubleBuffer = null;
        public ByteBuffer getByteBuffer() {
            if (byteBuffer == null) {
                byteBuffer = data_ptr().capacity(fullSize()).asBuffer();
            }
            byteBuffer.position(0);
            return byteBuffer;
        }
        public ShortBuffer getShortBuffer() {
            if (shortBuffer == null) {
                shortBuffer = data_s().capacity(fullSize()/2).asBuffer();
            }
            shortBuffer.position(0);
            return shortBuffer;
        }
        public IntBuffer getIntBuffer() {
            if (intBuffer == null) {
                intBuffer = data_i().capacity(fullSize()/4).asBuffer();
            }
            intBuffer.position(0);
            return intBuffer;
        }
        public FloatBuffer getFloatBuffer() {
            if (floatBuffer == null) {
                floatBuffer = data_fl().capacity(fullSize()/4).asBuffer();
            }
            floatBuffer.position(0);
            return floatBuffer;
        }
        public DoubleBuffer getDoubleBuffer() {
            if (doubleBuffer == null) {
                doubleBuffer = data_db().capacity(fullSize()/8).asBuffer();
            }
            doubleBuffer.position(0);
            return doubleBuffer;
        }

        public double get(int i) {
            switch (depth()) {
                case CV_8U:  return getByteBuffer()  .get(i)&0xFF;
                case CV_8S:  return getByteBuffer()  .get(i);
                case CV_16U: return getShortBuffer() .get(i)&0xFFFF;
                case CV_16S: return getShortBuffer() .get(i);
                case CV_32S: return getIntBuffer()   .get(i);
                case CV_32F: return getFloatBuffer() .get(i);
                case CV_64F: return getDoubleBuffer().get(i);
                default: assert false;
            }
            return Double.NaN;
        }
        public double get(int i, int j) {
            return get(i*step()/elemSize() + j*channels());
        }

        public double get(int i, int j, int k) {
            return get(i*step()/elemSize() + j*channels() + k);
        }
        public synchronized CvMat get(int index, double[] vv, int offset, int length) {
            int d = depth();
            switch (d) {
                case CV_8U:
                case CV_8S:
                    ByteBuffer bb = getByteBuffer();
                    bb.position(index);
                    for (int i = 0; i < length; i++) {
                        if (d == CV_8U) {
                            vv[i+offset] = bb.get(i)&0xFF;
                        } else {
                            vv[i+offset] = bb.get(i);
                        }
                    }
                    break;
                case CV_16U:
                case CV_16S:
                    ShortBuffer sb = getShortBuffer();
                    sb.position(index);
                    for (int i = 0; i < length; i++) {
                        if (d == CV_16U) {
                            vv[i+offset] = sb.get()&0xFFFF;
                        } else {
                            vv[i+offset] = sb.get();
                        }
                    }
                    break;
                case CV_32S:
                    IntBuffer ib = getIntBuffer();
                    ib.position(index);
                    for (int i = 0; i < length; i++) {
                        vv[i+offset] = ib.get();
                    }
                    break;
                case CV_32F:
                    FloatBuffer fb = getFloatBuffer();
                    fb.position(index);
                    for (int i = 0; i < length; i++) {
                        vv[i+offset] = fb.get();
                    }
                    break;
                case CV_64F:
                    getDoubleBuffer().position(index);
                    getDoubleBuffer().get(vv, offset, length);
                    break;
                default: assert false;
            }
            return this;
        }
        public CvMat get(int index, double[] vv) {
            return get(index, vv, 0, vv.length);
        }
        public CvMat get(double[] vv) {
            return get(0, vv);
        }
        public double[] get() {
            double[] vv = new double[fullSize()/elemSize()];
            get(vv);
            return vv;
        }

        public CvMat put(int i, double v) {
            switch (depth()) {
                case CV_8U:
                case CV_8S:  getByteBuffer()  .put(i, (byte)(int)v);  break;
                case CV_16U:
                case CV_16S: getShortBuffer() .put(i, (short)(int)v); break;
                case CV_32S: getIntBuffer()   .put(i, (int)v);        break;
                case CV_32F: getFloatBuffer() .put(i, (float)v);      break;
                case CV_64F: getDoubleBuffer().put(i, v);             break;
                default: assert false;
            }
            return this;
        }
        public CvMat put(int i, int j, double v) {
            return put(i*step()/elemSize() + j*channels(), v);
        }
        public CvMat put(int i, int j, int k, double v) {
            return put(i*step()/elemSize() + j*channels() + k, v);
        }
        public synchronized CvMat put(int index, double[] vv, int offset, int length) {
            switch (depth()) {
                case CV_8U:
                case CV_8S:
                    ByteBuffer bb = getByteBuffer();
                    bb.position(index);
                    for (int i = 0; i < length; i++) {
                        bb.put((byte)(int)vv[i+offset]);
                    }
                    break;
                case CV_16U:
                case CV_16S:
                    ShortBuffer sb = getShortBuffer();
                    sb.position(index);
                    for (int i = 0; i < length; i++) {
                        sb.put((short)(int)vv[i+offset]);
                    }
                    break;
                case CV_32S:
                    IntBuffer ib = getIntBuffer();
                    ib.position(index);
                    for (int i = 0; i < length; i++) {
                        ib.put((int)vv[i+offset]);
                    }
                    break;
                case CV_32F:
                    FloatBuffer fb = getFloatBuffer();
                    fb.position(index);
                    for (int i = 0; i < length; i++) {
                        fb.put((float)vv[i+offset]);
                    }
                    break;
                case CV_64F:
                    DoubleBuffer db = getDoubleBuffer();
                    db.position(index);
                    db.put(vv, offset, length);
                    break;
                default: assert false;
            }
            return this;
        }
        public CvMat put(int index, double ... vv) {
            return put(index, vv, 0, vv.length);
        }
        public CvMat put(double ... vv) {
            return put(0, vv);
        }

        public CvMat put(CvMat mat) {
            return put(0, 0, 0, mat, 0, 0, 0);
        }
        public synchronized CvMat put(int dsti, int dstj, int dstk,
                CvMat mat, int srci, int srcj, int srck) {
            if (rows() == mat.rows() && cols() == mat.cols() && step() == mat.step() && type() == mat.type() &&
                    dsti == 0 && dstj == 0 && dstk == 0 && srci == 0 && srcj == 0 && srck == 0) {
                getByteBuffer().clear();
                mat.getByteBuffer().clear();
                getByteBuffer().put(mat.getByteBuffer());
            } else {
                int w = Math.min(rows()-dsti, mat.rows()-srci);
                int h = Math.min(cols()-dstj, mat.cols()-srcj);
                int d = Math.min(channels()-dstk, mat.channels()-srck);
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        for (int k = 0; k < d; k++) {
                            put(i+dsti, j+dstj, k+dstk, mat.get(i+srci, j+srcj, k+srck));
                        }
                    }
                }
            }
            return this;
        }

        public IplImage asIplImage() {
            IplImage image = new IplImage();
            cvGetImage(this, image);
            return image;
        }

        @Override public String toString() {
            return toString(0);
        }
        public String toString(int indent) {
            StringBuilder s = new StringBuilder("[ ");
            int channels = channels();
            for (int i = 0; i < rows(); i++) {
                for (int j = 0; j < cols(); j++) {
                    CvScalar v = cvGet2D(this, i, j);
                    if (channels > 1) {
                        s.append("(");
                    }
                    for (int k = 0; k < channels; k++) {
                        s.append((float)v.val(k));
                        if (k < channels-1) {
                            s.append(", ");
                        }
                    }
                    if (channels > 1) {
                        s.append(")");
                    }
                    if (j < cols()-1) {
                        s.append(", ");
                    }
                }
                if (i < rows()-1) {
                    s.append("\n  ");
                    for (int j = 0; j < indent; j++) {
                        s.append(' ');
                    }
                }
            }
            s.append(" ]");
            return s.toString();
        }
    }

    public static boolean CV_IS_MAT_HDR(CvArr mat) {
        CvMat m = new CvMat(mat);
        return mat != null && (m.raw_type() & CV_MAGIC_MASK) == CV_MAT_MAGIC_VAL &&
               m.cols() > 0 && m.rows() > 0;
    }
    public static boolean CV_IS_MAT_HDR_Z(CvArr mat) {
        CvMat m = new CvMat(mat);
        return mat != null && (m.raw_type() & CV_MAGIC_MASK) == CV_MAT_MAGIC_VAL &&
               m.cols() >= 0 && m.rows() >= 0;
    }
    public static boolean CV_IS_MAT(CvArr mat) {
        return CV_IS_MAT_HDR(mat) && new CvMat(mat).data_ptr() != null;
    }
    public static boolean CV_IS_MASK_ARR(CvMat mat) {
        return (mat.raw_type() & (CV_MAT_TYPE_MASK & ~CV_8SC1)) == 0;
    }
    public static boolean CV_ARE_TYPES_EQ(CvMat mat1, CvMat mat2) {
        return ((mat1.raw_type() ^ mat2.raw_type()) & CV_MAT_TYPE_MASK) == 0;
    }
    public static boolean CV_ARE_CNS_EQ(CvMat mat1, CvMat mat2) {
        return ((mat1.raw_type() ^ mat2.raw_type()) & CV_MAT_CN_MASK) == 0;
    }
    public static boolean CV_ARE_DEPTHS_EQ(CvMat mat1, CvMat mat2) {
        return ((mat1.raw_type() ^ mat2.raw_type()) & CV_MAT_DEPTH_MASK) == 0;
    }
    public static boolean CV_ARE_SIZES_EQ(CvMat mat1, CvMat mat2) {
        return (mat1.rows() == mat2.rows() && mat1.cols() == mat2.cols());
    }
    public static boolean CV_IS_MAT_CONST(CvMat mat) {
        return (mat.rows()|mat.cols()) == 1;
    }
    public static int CV_ELEM_SIZE1(int type) {
        return (((sizeof(SizeTPointer.class)<<28)|0x8442211) >> CV_MAT_DEPTH(type)*4) & 15;
    }
    public static int CV_ELEM_SIZE(int type) {
        return CV_MAT_CN(type) << ((((sizeof(SizeTPointer.class)/4+1)*16384|0x3a50) >> CV_MAT_DEPTH(type)*2) & 3);
    }
    public static int IPL2CV_DEPTH(int depth) {
        return (((CV_8U)+(CV_16U<<4)+(CV_32F<<8)+(CV_64F<<16)+(CV_8S<<20) +
               (CV_16S<<24)+(CV_32S<<28)) >> (((depth & 0xF0) >> 2) +
               ((depth & IPL_DEPTH_SIGN) != 0 ? 20 : 0))) & 15;
    }

    public static CvMat cvMat(int rows, int cols, int type, Pointer data) {
        CvMat m = new CvMat();

        assert CV_MAT_DEPTH(type) >= 0 && CV_MAT_DEPTH(type) <= CV_64F;
        type = CV_MAT_TYPE(type);
        m.raw_type(CV_MAT_MAGIC_VAL | CV_MAT_CONT_FLAG | type);
        m.cols(cols);
        m.rows(rows);
        m.step(cols*CV_ELEM_SIZE(type));
        m.data_ptr(new BytePointer(data));
        m.refcount(null);
        m.hdr_refcount(0);

        return m;
    }

    public static int cvIplDepth(int type) {
        int depth = CV_MAT_DEPTH(type);
        return CV_ELEM_SIZE1(depth)*8 | (depth == CV_8S || depth == CV_16S ||
               depth == CV_32S ? IPL_DEPTH_SIGN : 0);
    }


    public static final int CV_MATND_MAGIC_VAL    = 0x42430000;
    public static final String CV_TYPE_NAME_MATND = "opencv-nd-matrix";

    public static final int
            CV_MAX_DIM          = 32,
            CV_MAX_DIM_HEAP     = 1024;

    public static class CvMatND extends CvArr {
        public CvMatND() { allocate(); zero(); }
        public CvMatND(int size) { allocateArray(size); zero(); }
        public CvMatND(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMatND position(int position) {
            return (CvMatND)super.position(position);
        }

        public static CvMatND create(int dims, int[] sizes, int type) {
            CvMatND m = cvCreateMatND(dims, sizes, type);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }

        @Override public CvMatND clone() {
            CvMatND m = cvCloneMatND(this);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }

        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends CvMatND implements Deallocator {
            ReleaseDeallocator(CvMatND p) { super(p); }
            @Override public void deallocate() { cvReleaseMatND(this); }
        }


        public native int type(); public native CvMatND type(int type);
        public native int dims(); public native CvMatND dims(int dims);

        public native IntPointer refcount(); public native CvMatND refcount(IntPointer type);
        public native int hdr_refcount();    public native CvMatND hdr_refcount(int hdr_refcount);

        // union { } data
        @Cast("uchar*")
        @Name("data.ptr") public native BytePointer   data_ptr(); public native CvMatND data_ptr(BytePointer ptr);
        @Name("data.fl")  public native FloatPointer  data_fl();  public native CvMatND data_fl(FloatPointer fl);
        @Name("data.db")  public native DoublePointer data_db();  public native CvMatND data_db(DoublePointer db);
        @Name("data.i")   public native IntPointer    data_i();   public native CvMatND data_i(IntPointer i);
        @Name("data.s")   public native ShortPointer  data_s();   public native CvMatND data_s(ShortPointer s);

        // struct { } dim[CV_MAX_DIM]
        @Name({"dim", ".size"})
        public native int dim_size(int i); public native CvMatND dim_size(int i, int size);
        @Name({"dim", ".step"})
        public native int dim_step(int i); public native CvMatND dim_step(int i, int step);
    }

    public static boolean CV_IS_MATND_HDR(CvArr mat) {
        return mat != null && (new CvMatND(mat).type() & CV_MAGIC_MASK) == CV_MATND_MAGIC_VAL;
    }
    public static boolean CV_IS_MATND(CvArr mat) {
        return CV_IS_MATND_HDR(mat) && new CvMatND(mat).data_ptr() != null;
    }


    public static final int CV_SPARSE_MAT_MAGIC_VAL    = 0x42440000;
    public static final String CV_TYPE_NAME_SPARSE_MAT = "opencv-sparse-matrix";

    public static class CvSparseMat extends CvArr {
        public CvSparseMat() { allocate(); zero(); }
        public CvSparseMat(int size) { allocateArray(size); zero(); }
        public CvSparseMat(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSparseMat position(int position) {
            return (CvSparseMat)super.position(position);
        }

        public static CvSparseMat create(int dims, int[] sizes, int type) {
            CvSparseMat m = cvCreateSparseMat(dims, sizes, type);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }

        @Override public CvSparseMat clone() {
            CvSparseMat m = cvCloneSparseMat(this);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }
        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends CvSparseMat implements Deallocator {
            ReleaseDeallocator(CvSparseMat p) { super(p); }
            @Override public void deallocate() { cvReleaseSparseMat(this); }
        }

        public native int type(); public native CvSparseMat type(int type);
        public native int dims(); public native CvSparseMat dims(int dims);
        public native IntPointer refcount(); public native CvSparseMat refcount(IntPointer type);
        public native int hdr_refcount();    public native CvSparseMat hdr_refcount(int hdr_refcount);

        public native CvSet heap();    public native CvSparseMat heap(CvSet heap);
        public native PointerPointer hashtable(); public native CvSparseMat hashtable(PointerPointer hashtable);
        public native int hashsize();  public native CvSparseMat hashsize(int hashsize);
        public native int valoffset(); public native CvSparseMat valoffset(int valoffset);
        public native int idxoffset(); public native CvSparseMat idxoffset(int idxoffset);
        public native int/*[CV_MAX_DIM]*/ size(int i); public native CvSparseMat size(int i, int size);
    }

    public static boolean CV_IS_SPARSE_MAT_HDR(CvArr mat) {
        return mat != null && (new CvSparseMat(mat).type() & CV_MAGIC_MASK) == CV_SPARSE_MAT_MAGIC_VAL;
    }
    public static boolean CV_IS_SPARSE_MAT(CvArr mat) {
        return CV_IS_SPARSE_MAT_HDR(mat);
    }

    public static class CvSparseNode extends Pointer {
        static { load(); }
        public CvSparseNode() { allocate(); }
        public CvSparseNode(int size) { allocateArray(size); }
        public CvSparseNode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSparseNode position(int position) {
            return (CvSparseNode)super.position(position);
        }

        public native int hashval();       public native CvSparseNode hashval(int hashval);
        public native CvSparseNode next(); public native CvSparseNode next(CvSparseNode next);
    }

    public static class CvSparseMatIterator extends Pointer {
        static { load(); }
        public CvSparseMatIterator() { allocate(); }
        public CvSparseMatIterator(int size) { allocateArray(size); }
        public CvSparseMatIterator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSparseMatIterator position(int position) {
            return (CvSparseMatIterator)super.position(position);
        }

        public native CvSparseMat mat();   public native CvSparseMatIterator mat(CvSparseMat mat);
        public native CvSparseNode node(); public native CvSparseMatIterator node(CvSparseNode node);
        public native int curidx();        public native CvSparseMatIterator curidx(int curidx);
    }

    public static Pointer CV_NODE_VAL(CvSparseMat mat, CvSparseNode node) {
        return new BytePointer(node).position(mat.valoffset());
    }
    public static IntPointer CV_NODE_IDX(CvSparseMat mat, CvSparseNode node) {
        return new IntPointer(new BytePointer(node).position(mat.idxoffset()));
    }


    public static class CvRect extends Pointer {
        static { load(); }
        public CvRect() { allocate(); }
        public CvRect(int size) { allocateArray(size); }
        public CvRect(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvRect position(int position) {
            return (CvRect)super.position(position);
        }

        public CvRect(int x, int y, int width, int height) {
            allocate(); x(x).y(y).width(width).height(height);
        }

        public native int x();      public native CvRect x(int x);
        public native int y();      public native CvRect y(int y);
        public native int width();  public native CvRect width(int width);
        public native int height(); public native CvRect height(int height);

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + x() + ", " + y() + "; " + width() + ", " + height() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + x() + ", " + y() + "; " + width() + ", " + height() + ")";
                }
                position(p);
                return s;
            }
        }
    }
    public static CvRect cvRect(int x, int y, int width, int height) {
        return new CvRect().x(x).y(y).width(width).height(height);
    }
    public static IplROI cvRectToROI(CvRect rect, int coi) {
        IplROI roi = new IplROI();
        roi.xOffset(rect.x());
        roi.yOffset(rect.y());
        roi.width(rect.width());
        roi.height(rect.height());
        roi.coi(coi);
        return roi;
    }
    public static CvRect cvROIToRect(IplROI roi) {
        return cvRect(roi.xOffset(), roi.yOffset(), roi.width(), roi.height());
    }


    public static final int
            CV_TERMCRIT_ITER   = 1,
            CV_TERMCRIT_NUMBER = CV_TERMCRIT_ITER,
            CV_TERMCRIT_EPS    = 2;
    public static class CvTermCriteria extends Pointer {
        static { load(); }
        public CvTermCriteria() { allocate(); }
        public CvTermCriteria(int size) { allocateArray(size); }
        public CvTermCriteria(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvTermCriteria position(int position) {
            return (CvTermCriteria)super.position(position);
        }

        public CvTermCriteria(int type, int max_iter, double epsilon) {
            allocate(); type(type).max_iter(max_iter).epsilon(epsilon);
        }

        public native int    type();     public native CvTermCriteria type(int type);
        public native int    max_iter(); public native CvTermCriteria max_iter(int max_iter);
        public native double epsilon();  public native CvTermCriteria epsilon(double epsilon);
    }
    public static CvTermCriteria cvTermCriteria(int type, int max_iter, double epsilon) {
        return new CvTermCriteria().type(type).max_iter(max_iter).epsilon(epsilon);
    }


    public static class CvPoint extends Pointer {
        static { load(); }
        public CvPoint() { allocate(); }
        public CvPoint(int size) { allocateArray(size); }
        public CvPoint(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvPoint position(int position) {
            return (CvPoint)super.position(position);
        }

        public CvPoint(int[] pts, int offset, int length) {
            this(length/2);
            put(pts, offset, length);
        }
        public CvPoint(int ... pts) {
            this(pts, 0, pts.length);
        }
        public CvPoint(byte shift, double[] pts, int offset, int length) {
            this(length/2);
            put(shift, pts, offset, length);
        }
        public CvPoint(byte shift, double ... pts) {
            this(shift, pts, 0, pts.length);
        }

        public native int x(); public native CvPoint x(int x);
        public native int y(); public native CvPoint y(int y);

        public int[] get() {
            int[] pts = new int[capacity == 0 ? 2 : 2*capacity];
            get(pts);
            return pts;
        }
        public CvPoint get(int[] pts) {
            return get(pts, 0, pts.length);
        }
        public CvPoint get(int[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                position(i);
                pts[offset + i*2  ] = x();
                pts[offset + i*2+1] = y();
            }
            return position(0);
        }

        public final CvPoint put(int[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                position(i).put(pts[offset + i*2], pts[offset + i*2+1]);
            }
            return position(0);
        }
        public final CvPoint put(int ... pts) {
            return put(pts, 0, pts.length);
        }
        public final CvPoint put(byte shift, double[] pts, int offset, int length) {
            int[] a = new int[length];
            for (int i = 0; i < length; i++) {
                a[i] = (int)Math.round(pts[offset + i] * (1<<shift));
            }
            return put(a, 0, length);
        }
        public final CvPoint put(byte shift, double ... pts) {
            return put(shift, pts, 0, pts.length);
        }

        public CvPoint put(int x, int y) {
            return x(x).y(y);
        }
        public CvPoint put(CvPoint o) {
            return x(o.x()).y(o.y());
        }
        public CvPoint put(byte shift, CvPoint2D32f o) {
            x((int)Math.round(o.x() * (1<<shift)));
            y((int)Math.round(o.y() * (1<<shift)));
            return this;
        }
        public CvPoint put(byte shift, CvPoint2D64f o) {
            x((int)Math.round(o.x() * (1<<shift)));
            y((int)Math.round(o.y() * (1<<shift)));
            return this;
        }

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + x() + ", " + y() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + x() + ", " + y() + ")";
                }
                position(p);
                return s;
            }
        }

        public static final CvPoint ZERO = new CvPoint().x(0).y(0);
    }
    public static CvPoint cvPoint(int x, int y) {
        return new CvPoint().x(x).y(y);
    }

    public static class CvPoint2D32f extends Pointer {
        static { load(); }
        public CvPoint2D32f() { allocate(); }
        public CvPoint2D32f(int size) { allocateArray(size); }
        public CvPoint2D32f(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvPoint2D32f position(int position) {
            return (CvPoint2D32f)super.position(position);
        }

        public CvPoint2D32f(double[] pts, int offset, int length) {
            this(length/2);
            put(pts, offset, length);
        }
        public CvPoint2D32f(double ... pts) {
            this(pts, 0, pts.length);
        }

        public native float x(); public native CvPoint2D32f x(float x);
        public native float y(); public native CvPoint2D32f y(float y);

        public double[] get() {
            double[] pts = new double[capacity == 0 ? 2 : 2*capacity];
            get(pts);
            return pts;
        }
        public CvPoint2D32f get(double[] pts) {
            return get(pts, 0, pts.length);
        }
        public CvPoint2D32f get(double[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                position(i);
                pts[offset + i*2  ] = x();
                pts[offset + i*2+1] = y();
            }
            return position(0);
        }

        public final CvPoint2D32f put(double[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                position(i).put(pts[offset + i*2], pts[offset + i*2+1]);
            }
            return position(0);
        }
        public final CvPoint2D32f put(double ... pts) {
            return put(pts, 0, pts.length);
        }

        public CvPoint2D32f put(double x, double y) {
            return x((float)x).y((float)y);
        }
        public CvPoint2D32f put(CvPoint o) {
            return x(o.x()).y(o.y());
        }
        public CvPoint2D32f put(CvPoint2D32f o) {
            return x(o.x()).y(o.y());
        }
        public CvPoint2D32f put(CvPoint2D64f o) {
            return x((float)o.x()).y((float)o.y());
        }

        @Override public String toString() { 
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + x() + ", " + y() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + x() + ", " + y() + ")";
                }
                position(p);
                return s;
            }
        }
    }
    public static CvPoint2D32f cvPoint2D32f(double x, double y) {
        return new CvPoint2D32f().x((float)x).y((float)y);
    }
    public static CvPoint2D32f cvPointTo32f(CvPoint point) {
        return cvPoint2D32f((float)point.x(), (float)point.y());
    }
    public static CvPoint cvPointFrom32f(CvPoint2D32f point) {
        CvPoint ipt = new CvPoint();
        ipt.x(Math.round(point.x()));
        ipt.y(Math.round(point.y()));
        return ipt;
    }

    public static class CvPoint3D32f extends Pointer {
        static { load(); }
        public CvPoint3D32f() { allocate(); }
        public CvPoint3D32f(int size) { allocateArray(size); }
        public CvPoint3D32f(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvPoint3D32f position(int position) {
            return (CvPoint3D32f)super.position(position);
        }

        public CvPoint3D32f(double[] pts, int offset, int length) {
            this(length/3);
            put(pts, offset, length);
        }
        public CvPoint3D32f(double ... pts) {
            this(pts, 0, pts.length);
        }

        public native float x(); public native CvPoint3D32f x(float x);
        public native float y(); public native CvPoint3D32f y(float y);
        public native float z(); public native CvPoint3D32f z(float z);

        public double[] get() {
            double[] pts = new double[capacity == 0 ? 3 : 3*capacity];
            get(pts);
            return pts;
        }
        public CvPoint3D32f get(double[] pts) {
            return get(pts, 0, pts.length);
        }
        public CvPoint3D32f get(double[] pts, int offset, int length) {
            for (int i = 0; i < length/3; i++) {
                position(i);
                pts[offset + i*3  ] = x();
                pts[offset + i*3+1] = y();
                pts[offset + i*3+2] = z();
            }
            return position(0);
        }

        public final CvPoint3D32f put(double[] pts, int offset, int length) {
            for (int i = 0; i < length/3; i++) {
                position(i).put(pts[offset + i*3], pts[offset + i*3+1], pts[offset + i*3+2]);
            }
            return position(0);
        }
        public final CvPoint3D32f put(double ... pts) {
            return put(pts, 0, pts.length);
        }

        public CvPoint3D32f put(double x, double y, double z) {
            return x((float)x).y((float)y).z((float)z);
        }
        public CvPoint3D32f put(CvPoint o) {
            return x(o.x()).y(o.y()).z(0);
        }
        public CvPoint3D32f put(CvPoint2D32f o) {
            return x(o.x()).y(o.y()).z(0);
        }
        public CvPoint3D32f put(CvPoint2D64f o) {
            return x((float)o.x()).y((float)o.y()).z(0);
        }

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + x() + ", " + y() + ", " + z() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + x() + ", " + y() + ", " + z() + ")";
                }
                position(p);
                return s;
            }
        }
    }
    public static CvPoint3D32f cvPoint3D32f(double x, double y, double z) {
        return new CvPoint3D32f().x((float)x).y((float)y).z((float)z);
    }

    public static class CvPoint2D64f extends Pointer {
        static { load(); }
        public CvPoint2D64f() { allocate(); }
        public CvPoint2D64f(int size) { allocateArray(size); }
        public CvPoint2D64f(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvPoint2D64f position(int position) {
            return (CvPoint2D64f)super.position(position);
        }

        public CvPoint2D64f(double[] pts, int offset, int length) {
            this(length/2);
            put(pts, offset, length);
        }
        public CvPoint2D64f(double ... pts) {
            this(pts, 0, pts.length);
        }

        public native double x(); public native CvPoint2D64f x(double x);
        public native double y(); public native CvPoint2D64f y(double y);

        public double[] get() {
            double[] pts = new double[capacity == 0 ? 2 : 2*capacity];
            get(pts);
            return pts;
        }
        public CvPoint2D64f get(double[] pts) {
            return get(pts, 0, pts.length);
        }
        public CvPoint2D64f get(double[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                position(i);
                pts[offset + i*2  ] = x();
                pts[offset + i*2+1] = y();
            }
            return position(0);
        }

        public final CvPoint2D64f put(double[] pts, int offset, int length) {
            for (int i = 0; i < length/2; i++) {
                position(i).put(pts[offset + i*2], pts[offset + i*2+1]);
            }
            return position(0);
        }
        public final CvPoint2D64f put(double ... pts) {
            return put(pts, 0, pts.length);
        }

        public CvPoint2D64f put(double x, double y) {
            return x(x).y(y);
        }
        public CvPoint2D64f put(CvPoint o) {
            return x(o.x()).y(o.y());
        }
        public CvPoint2D64f put(CvPoint2D32f o) {
            return x(o.x()).y(o.y());
        }
        public CvPoint2D64f put(CvPoint2D64f o) {
            return x(o.x()).y(o.y());
        }

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + (float)x() + ", " + (float)y() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + (float)x() + ", " + (float)y() + ")";
                }
                position(p);
                return s;
            }
        }
    }
    public static CvPoint2D64f cvPoint2D64f(double x, double y) {
        return new CvPoint2D64f().x(x).y(y);
    }

    public static class CvPoint3D64f extends Pointer {
        static { load(); }
        public CvPoint3D64f() { allocate(); }
        public CvPoint3D64f(int size) { allocateArray(size); }
        public CvPoint3D64f(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvPoint3D64f position(int position) {
            return (CvPoint3D64f)super.position(position);
        }

        public CvPoint3D64f(double[] pts, int offset, int length) {
            this(length/3);
            put(pts, offset, length);
        }
        public CvPoint3D64f(double ... pts) {
            this(pts, 0, pts.length);
        }

        public native double x(); public native CvPoint3D64f x(double x);
        public native double y(); public native CvPoint3D64f y(double y);
        public native double z(); public native CvPoint3D64f z(double z);

        public double[] get() {
            double[] pts = new double[capacity == 0 ? 3 : 3*capacity];
            get(pts);
            return pts;
        }
        public CvPoint3D64f get(double[] pts) {
            return get(pts, 0, pts.length);
        }
        public CvPoint3D64f get(double[] pts, int offset, int length) {
            for (int i = 0; i < length/3; i++) {
                position(i);
                pts[offset + i*3  ] = x();
                pts[offset + i*3+1] = y();
                pts[offset + i*3+2] = z();
            }
            return position(0);
        }

        public final CvPoint3D64f put(double[] pts, int offset, int length) {
            for (int i = 0; i < length/3; i++) {
                position(i).put(pts[offset + i*3], pts[offset + i*3+1], pts[offset + i*3+2]);
            }
            return position(0);
        }
        public final CvPoint3D64f put(double ... pts) {
            return put(pts, 0, pts.length);
        }

        public CvPoint3D64f put(double x, double y, double z) {
            return x(x()).y(y()).z(z());
        }
        public CvPoint3D64f put(CvPoint o) {
            return x(o.x()).y(o.y()).z(0);
        }
        public CvPoint3D64f put(CvPoint2D32f o) {
            return x(o.x()).y(o.y()).z(0);
        }
        public CvPoint3D64f put(CvPoint2D64f o) {
            return x(o.x()).y(o.y()).z(0);
        }

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + (float)x() + ", " + (float)y() + ", " + (float)z() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + (float)x() + ", " + (float)y() + ", " + (float)z() + ")";
                }
                position(p);
                return s;
            }
        }
    }
    public static CvPoint3D64f cvPoint3D64f(double x, double y, double z) {
        return new CvPoint3D64f().x(x).y(y).z(z);
    }

    public static class CvSize extends Pointer {
        static { load(); }
        public CvSize() { allocate(); }
        public CvSize(int size) { allocateArray(size); }
        public CvSize(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSize position(int position) {
            return (CvSize)super.position(position);
        }

        public CvSize(int width, int height) {
            allocate(); width(width).height(height);
        }

        public native int width();  public native CvSize width(int width);
        public native int height(); public native CvSize height(int height);

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + width() + ", " + height() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + width() + ", " + height() + ")";
                }
                position(p);
                return s;
            }
        }
        public static final CvSize ZERO = new CvSize().width(0).height(0);
    }
    public static CvSize cvSize(int width, int height) {
        return new CvSize().width(width).height(height);
    }

    public static class CvSize2D32f extends Pointer {
        static { load(); }
        public CvSize2D32f() { allocate(); }
        public CvSize2D32f(int size) { allocateArray(size); }
        public CvSize2D32f(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSize2D32f position(int position) {
            return (CvSize2D32f)super.position(position);
        }

        public CvSize2D32f(float width, float height) {
            allocate(); width(width).height(height);
        }

        public native float width();  public native CvSize2D32f width(float width);
        public native float height(); public native CvSize2D32f height(float height);

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + width() + ", " + height() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + width() + ", " + height() + ")";
                }
                position(p);
                return s;
            }
        }
    }
    public static CvSize2D32f cvSize2D32f(double width, double height) {
        return new CvSize2D32f().width((float)width).height((float)height);
    }

    public static class CvBox2D extends Pointer {
        static { load(); }
        public CvBox2D() { allocate(); }
        public CvBox2D(int size) { allocateArray(size); }
        public CvBox2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvBox2D position(int position) {
            return (CvBox2D)super.position(position);
        }

        public CvBox2D(CvPoint2D32f center, CvSize2D32f size, float angle) {
            allocate(); center(center).size(size).angle(angle);
        }

        public native @ByRef CvPoint2D32f center(); public native CvBox2D center(CvPoint2D32f center);
        public native @ByRef CvSize2D32f size();    public native CvBox2D size(CvSize2D32f size);
        public native float angle();                public native CvBox2D angle(float angle);

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + center() + ", " + size() + ", " + angle() + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + center() + ", " + size() + ", " + angle() + ")";
                }
                position(p);
                return s;
            }
        }
    }

    public static class CvLineIterator extends Pointer {
        static { load(); }
        public CvLineIterator() { allocate(); }
        public CvLineIterator(int size) { allocateArray(size); }
        public CvLineIterator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLineIterator position(int position) {
            return (CvLineIterator)super.position(position);
        }

        @Cast("uchar*")
        public native BytePointer ptr(); public native CvLineIterator ptr(BytePointer ptr);

        public native int err();         public native CvLineIterator err(int err);
        public native int plus_delta();  public native CvLineIterator plus_delta(int plus_delta);
        public native int minus_delta(); public native CvLineIterator minus_delta(int minus_delta);
        public native int plus_step();   public native CvLineIterator plus_step(int plus_step);
        public native int minus_step();  public native CvLineIterator minus_step(int minus_step);
    }


    public static class CvSlice extends Pointer {
        static { load(); }
        public CvSlice() { allocate(); }
        public CvSlice(int size) { allocateArray(size); }
        public CvSlice(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSlice position(int position) {
            return (CvSlice)super.position(position);
        }

        public native int start_index(); public native CvSlice start_index(int start_index);
        public native int end_index();   public native CvSlice end_index(int end_index);
    }
    public static CvSlice cvSlice(int start, int end) {
        return new CvSlice().start_index(start).end_index(end);
    }
    public static final int CV_WHOLE_SEQ_END_INDEX = 0x3fffffff;
    public static final CvSlice CV_WHOLE_SEQ = load() == null ? null : cvSlice(0, CV_WHOLE_SEQ_END_INDEX);


    public static class CvScalar extends Pointer {
        static { load(); }
        public CvScalar() { allocate(); }
        public CvScalar(int size) { allocateArray(size); }
        public CvScalar(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvScalar position(int position) {
            return (CvScalar)super.position(position);
        }

        public CvScalar(double val0, double val1, double val2, double val3) {
            allocate(); val(0, val0).val(1, val1).val(2, val2).val(3, val3);
        }

        public native double/*[4]*/ val(int i); public native CvScalar val(int i, double val);
        public double getVal(int i)               { return val(i);      } 
        public CvScalar setVal(int i, double val) { return val(i, val); }

        @MemberGetter @Name("val")
        public native DoublePointer getDoublePointerVal();

        @MemberGetter @Cast("double*") @Name("val")
        public native LongPointer getLongPointerVal();

        public void scale(double s) {
            for (int i = 0; i < 4; i++) {
                val(i, val(i) * s);
            }
        }

        public double red()      { return val(2); }
        public double green()    { return val(1); }
        public double blue()     { return val(0); }
        public CvScalar red  (double r) { val(2, r); return this; }
        public CvScalar green(double g) { val(1, g); return this; }
        public CvScalar blue (double b) { val(0, b); return this; }

        public double magnitude() {
            return Math.sqrt(val(0)*val(0) + val(1)*val(1) + val(2)*val(2) + val(3)*val(3));
        }

        @Override public String toString() {
            if (isNull()) {
                return super.toString();
            } else {
                if (capacity() == 0) {
                    return "(" + (float)val(0) + ", " + (float)val(1) + ", " +
                            (float)val(2) + ", " + (float)val(3) + ")";
                }
                String s = "";
                int p = position();
                for (int i = 0; i < capacity(); i++) {
                    position(i);
                    s += (i == 0 ? "(" : " (") + (float)val(0) + ", " + (float)val(1) + ", " +
                            (float)val(2) + ", " + (float)val(3) + ")";
                }
                position(p);
                return s;
            }
        }

        public static final CvScalar
                ZERO    = new CvScalar().val(0, 0.0).val(1, 0.0).val(2, 0.0).val(3, 0.0),
                ONE     = new CvScalar().val(0, 1.0).val(1, 1.0).val(2, 1.0).val(3, 1.0),
                ONEHALF = new CvScalar().val(0, 0.5).val(1, 0.5).val(2, 0.5).val(3, 0.5),
                ALPHA1  = new CvScalar().val(0, 0.0).val(1, 0.0).val(2, 0.0).val(3, 1.0),
                ALPHA255= new CvScalar().val(0, 0.0).val(1, 0.0).val(2, 0.0).val(3, 255.0),

                WHITE   = CV_RGB(255, 255, 255),
                GRAY    = CV_RGB(128, 128, 128),
                BLACK   = CV_RGB(  0,   0,   0),
                RED     = CV_RGB(255,   0,   0),
                GREEN   = CV_RGB(  0, 255,   0),
                BLUE    = CV_RGB(  0,   0, 255),
                CYAN    = CV_RGB(  0, 255, 255),
                MAGENTA = CV_RGB(255,   0, 255),
                YELLOW  = CV_RGB(255, 255,   0);
    }
    public static CvScalar cvScalar(double val0, double val1, double val2, double val3) {
        return new CvScalar().val(0, val0).val(1, val1).val(2, val2).val(3, val3);
    }
    public static CvScalar cvRealScalar(double val0) {
        return new CvScalar().val(0, val0).val(1, 0.0).val(2, 0.0).val(3, 0.0);
    }
    public static CvScalar cvScalarAll(double val0123) {
        return new CvScalar().val(0, val0123).val(1, val0123).val(2, val0123).val(3, val0123);
    }


    public static class CvMemBlock extends Pointer {
        static { load(); }
        public CvMemBlock() { allocate(); }
        public CvMemBlock(int size) { allocateArray(size); }
        public CvMemBlock(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMemBlock position(int position) {
            return (CvMemBlock)super.position(position);
        }

        public native CvMemBlock prev(); public native CvMemBlock prev(CvMemBlock prev);
        public native CvMemBlock next(); public native CvMemBlock next(CvMemBlock next);
    }

    public static final int CV_STORAGE_MAGIC_VAL = 0x42890000;

    public static class CvMemStorage extends Pointer {
        static { load(); }
        public CvMemStorage() { allocate(); zero(); }
        public CvMemStorage(int size) { allocateArray(size); zero(); }
        public CvMemStorage(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMemStorage position(int position) {
            return (CvMemStorage)super.position(position);
        }

        public static CvMemStorage create(int block_size) {
            CvMemStorage m = cvCreateMemStorage(block_size);
            if (m != null) {
                m.deallocator(new ReleaseDeallocator(m));
            }
            return m;
        }
        public static CvMemStorage create() {
            return create(0);
        }

        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends CvMemStorage implements Deallocator {
            ReleaseDeallocator(CvMemStorage p) { super(p); }
            @Override public void deallocate() { cvReleaseMemStorage(this); }
        }

        public native int signature();       public native CvMemStorage signature(int free_space);
        public native CvMemBlock bottom();   public native CvMemStorage bottom(CvMemBlock bottom);
        public native CvMemBlock top();      public native CvMemStorage top(CvMemBlock top);
        public native CvMemStorage parent(); public native CvMemStorage parent(CvMemStorage parent);
        public native int block_size();      public native CvMemStorage block_size(int block_size);
        public native int free_space();      public native CvMemStorage free_space(int free_space);
    }

    public static boolean CV_IS_STORAGE(CvArr storage) {
        return storage != null && (new CvMemStorage(storage).signature() & CV_MAGIC_MASK) == CV_STORAGE_MAGIC_VAL;
    }

    public static class CvMemStoragePos extends Pointer {
        static { load(); }
        public CvMemStoragePos() { allocate(); }
        public CvMemStoragePos(int size) { allocateArray(size); }
        public CvMemStoragePos(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvMemStoragePos position(int position) {
            return (CvMemStoragePos)super.position(position);
        }

        public native CvMemBlock top(); public native CvMemStoragePos top(CvMemBlock top);
        public native int free_space(); public native CvMemStoragePos free_space(int free_space);
    }


    public static class CvSeqBlock extends Pointer {
        static { load(); }
        public CvSeqBlock() { allocate(); }
        public CvSeqBlock(int size) { allocateArray(size); }
        public CvSeqBlock(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSeqBlock position(int position) {
            return (CvSeqBlock)super.position(position);
        }

        public native CvSeqBlock prev();  public native CvSeqBlock prev(CvSeqBlock prev);
        public native CvSeqBlock next();  public native CvSeqBlock next(CvSeqBlock next);
        public native int start_index();  public native CvSeqBlock start_index(int start_index);
        public native int count();        public native CvSeqBlock count(int count);
        @Cast("schar*")
        public native BytePointer data(); public native CvSeqBlock data(BytePointer data);
    }

    public static class CvSeq extends CvArr {
        public CvSeq() { allocate(); zero(); }
        public CvSeq(int size) { allocateArray(size); zero(); }
        public CvSeq(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSeq position(int position) {
            return (CvSeq)super.position(position);
        }

        public static CvSeq create(int seq_flags, int header_size, int elem_size,
                CvMemStorage storage) {
            return cvCreateSeq(seq_flags, header_size, elem_size, storage);
        }

        public native int flags();              public native CvSeq flags(int flags);
        public native int header_size();        public native CvSeq header_size(int header_size);
        public native CvSeq h_prev();           public native CvSeq h_prev(CvSeq h_prev);
        public native CvSeq h_next();           public native CvSeq h_next(CvSeq h_next);
        public native CvSeq v_prev();           public native CvSeq v_prev(CvSeq v_prev);
        public native CvSeq v_next();           public native CvSeq v_next(CvSeq v_next);
        public native int total();              public native CvSeq total(int total);
        public native int elem_size();          public native CvSeq elem_size(int elem_size);
        @Cast("schar*")
        public native BytePointer block_max();  public native CvSeq block_max(BytePointer block_max);
        @Cast("schar*")
        public native BytePointer ptr();        public native CvSeq ptr(BytePointer ptr);
        public native int delta_elems();        public native CvSeq delta_elems(int delta_elems);
        public native CvMemStorage storage();   public native CvSeq storage(CvMemStorage storage);
        public native CvSeqBlock free_blocks(); public native CvSeq free_blocks(CvSeqBlock free_blocks);
        public native CvSeqBlock first();       public native CvSeq first(CvSeqBlock first);
    }

    public static final String
            CV_TYPE_NAME_SEQ            = "opencv-sequence",
            CV_TYPE_NAME_SEQ_TREE       = "opencv-sequence-tree";


    public static class CvSetElem extends Pointer {
        static { load(); }
        public CvSetElem() { allocate(); }
        public CvSetElem(int size) { allocateArray(size); }
        public CvSetElem(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSetElem position(int position) {
            return (CvSetElem)super.position(position);
        }

        public native int flags();           public native CvSetElem flags(int flags);
        public native CvSetElem next_free(); public native CvSetElem next_free(CvSetElem next_free);
    }

    public static class CvSet extends CvSeq {
        public CvSet() { allocate(); zero(); }
        public CvSet(int size) { allocateArray(size); zero(); }
        public CvSet(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSet position(int position) {
            return (CvSet)super.position(position);
        }

        public static CvSet create(int set_flags, int header_size, int elem_size,
                CvMemStorage storage) {
            return cvCreateSet(set_flags, header_size, elem_size, storage);
        }

        public native CvSetElem free_elems(); public native CvSet free_elems(CvSetElem free_elems);
        public native int active_count();     public native CvSet active_count(int active_count);
    }
    public static final int
            CV_SET_ELEM_IDX_MASK  = ((1 << 26) - 1),
            CV_SET_ELEM_FREE_FLAG = (1 << (Integer.SIZE/32*8-1));
    public static boolean CV_IS_SET_ELEM(Pointer ptr) {
        return new CvSetElem(ptr).flags() >= 0;
    }


    public static class CvGraphEdge extends Pointer {
        static { load(); }
        public CvGraphEdge() { allocate(); }
        public CvGraphEdge(int size) { allocateArray(size); }
        public CvGraphEdge(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGraphEdge position(int position) {
            return (CvGraphEdge)super.position(position);
        }

        public native int flags();                    public native CvGraphEdge flags(int flags);
        public native float weight();                 public native CvGraphEdge weight(float weight);
        public native CvGraphEdge/*[2]*/ next(int i); public native CvGraphEdge next(int i, CvGraphEdge next);
        public native CvGraphVtx /*[2]*/ vtx(int i);  public native CvGraphEdge vtx(int i, CvGraphVtx vtx);
    }

    public static class CvGraphVtx extends Pointer {
        static { load(); }
        public CvGraphVtx() { allocate(); }
        public CvGraphVtx(int size) { allocateArray(size); }
        public CvGraphVtx(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGraphVtx position(int position) {
            return (CvGraphVtx)super.position(position);
        }

        public native int flags();         public native CvGraphVtx flags(int flags);
        public native CvGraphEdge first(); public native CvGraphVtx first(CvGraphEdge first);
    }

    public static class CvGraphVtx2D extends CvGraphVtx {
        public CvGraphVtx2D() { allocate(); }
        public CvGraphVtx2D(int size) { allocateArray(size); }
        public CvGraphVtx2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGraphVtx2D position(int position) {
            return (CvGraphVtx2D)super.position(position);
        }

        public native CvPoint2D32f ptr(); public native CvGraphVtx2D ptr(CvPoint2D32f first);
    }

    public static class CvGraph extends CvSet {
        public CvGraph() { allocate(); zero(); }
        public CvGraph(int size) { allocateArray(size); zero(); }
        public CvGraph(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGraph position(int position) {
            return (CvGraph)super.position(position);
        }

        public static CvGraph create(int graph_flags, int header_size, int vtx_size,
                int edge_size, CvMemStorage storage) {
            return cvCreateGraph(graph_flags, header_size, vtx_size, edge_size, storage);
        }

        public native CvSet edges(); public native CvGraph edges(CvSet edges);
    }

    public static final String CV_TYPE_NAME_GRAPH = "opencv-graph";


    public static class CvChain extends CvSeq {
        public CvChain() { allocate(); }
        public CvChain(int size) { allocateArray(size); }
        public CvChain(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvChain position(int position) {
            return (CvChain)super.position(position);
        }

        public native @ByRef CvPoint origin(); public native CvChain origin(CvPoint origin);
    }

    public static class CvContour extends CvSeq {
        public CvContour() { allocate(); }
        public CvContour(int size) { allocateArray(size); }
        public CvContour(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvContour position(int position) {
            return (CvContour)super.position(position);
        }

        public native @ByRef CvRect rect();       public native CvContour rect(CvRect rect);
        public native int color();                public native CvContour color(int color);
        public native int/*[3]*/ reserved(int i); public native CvContour reserved(int i, int reserved);
    }

    // typedef CvContour CvPoint2DSeq


    public static final int
            CV_SEQ_MAGIC_VAL            = 0x42990000,

            CV_SET_MAGIC_VAL            = 0x42980000,

            CV_SEQ_ELTYPE_BITS          = 12,
            CV_SEQ_ELTYPE_MASK          = ((1 << CV_SEQ_ELTYPE_BITS) - 1),

            CV_SEQ_ELTYPE_POINT         = CV_32SC2,
            CV_SEQ_ELTYPE_CODE          = CV_8UC1,
            CV_SEQ_ELTYPE_GENERIC       = 0,
            CV_SEQ_ELTYPE_PTR           = CV_USRTYPE1,
            CV_SEQ_ELTYPE_PPOINT        = CV_SEQ_ELTYPE_PTR,
            CV_SEQ_ELTYPE_INDEX         = CV_32SC1,
            CV_SEQ_ELTYPE_GRAPH_EDGE    = 0,
            CV_SEQ_ELTYPE_GRAPH_VERTEX  = 0,
            CV_SEQ_ELTYPE_TRIAN_ATR     = 0,
            CV_SEQ_ELTYPE_CONNECTED_COMP= 0,
            CV_SEQ_ELTYPE_POINT3D       = CV_32FC3,

            CV_SEQ_KIND_BITS       = 2,
            CV_SEQ_KIND_MASK       = (((1 << CV_SEQ_KIND_BITS) - 1)<<CV_SEQ_ELTYPE_BITS),


            CV_SEQ_KIND_GENERIC    = (0 << CV_SEQ_ELTYPE_BITS),
            CV_SEQ_KIND_CURVE      = (1 << CV_SEQ_ELTYPE_BITS),
            CV_SEQ_KIND_BIN_TREE   = (2 << CV_SEQ_ELTYPE_BITS),


            CV_SEQ_KIND_GRAPH      = (1 << CV_SEQ_ELTYPE_BITS),
            CV_SEQ_KIND_SUBDIV2D   = (2 << CV_SEQ_ELTYPE_BITS),

            CV_SEQ_FLAG_SHIFT      = (CV_SEQ_KIND_BITS + CV_SEQ_ELTYPE_BITS),


            CV_SEQ_FLAG_CLOSED    = (1 << CV_SEQ_FLAG_SHIFT),
            CV_SEQ_FLAG_SIMPLE    = (0 << CV_SEQ_FLAG_SHIFT),
            CV_SEQ_FLAG_CONVEX    = (0 << CV_SEQ_FLAG_SHIFT),
            CV_SEQ_FLAG_HOLE      = (2 << CV_SEQ_FLAG_SHIFT),


            CV_GRAPH_FLAG_ORIENTED = (1 << CV_SEQ_FLAG_SHIFT),

            CV_GRAPH              = CV_SEQ_KIND_GRAPH,
            CV_ORIENTED_GRAPH     = (CV_SEQ_KIND_GRAPH|CV_GRAPH_FLAG_ORIENTED),


            CV_SEQ_POINT_SET      = (CV_SEQ_KIND_GENERIC| CV_SEQ_ELTYPE_POINT),
            CV_SEQ_POINT3D_SET    = (CV_SEQ_KIND_GENERIC| CV_SEQ_ELTYPE_POINT3D),
            CV_SEQ_POLYLINE       = (CV_SEQ_KIND_CURVE  | CV_SEQ_ELTYPE_POINT),
            CV_SEQ_POLYGON        = (CV_SEQ_FLAG_CLOSED | CV_SEQ_POLYLINE ),
            CV_SEQ_CONTOUR        = CV_SEQ_POLYGON,
            CV_SEQ_SIMPLE_POLYGON = (CV_SEQ_FLAG_SIMPLE | CV_SEQ_POLYGON  ),


            CV_SEQ_CHAIN          = (CV_SEQ_KIND_CURVE  | CV_SEQ_ELTYPE_CODE),
            CV_SEQ_CHAIN_CONTOUR  = (CV_SEQ_FLAG_CLOSED | CV_SEQ_CHAIN),


            CV_SEQ_POLYGON_TREE   = (CV_SEQ_KIND_BIN_TREE  | CV_SEQ_ELTYPE_TRIAN_ATR),


            CV_SEQ_CONNECTED_COMP = (CV_SEQ_KIND_GENERIC  | CV_SEQ_ELTYPE_CONNECTED_COMP),


            CV_SEQ_INDEX          = (CV_SEQ_KIND_GENERIC  | CV_SEQ_ELTYPE_INDEX);

    public static boolean CV_IS_SEQ(CvArr seq) {
        return seq != null && (new CvSeq(seq).flags() & CV_MAGIC_MASK) == CV_SEQ_MAGIC_VAL;
    }
    public static boolean CV_IS_SET(CvArr set) {
        return set != null && (new CvSet(set).flags() & CV_MAGIC_MASK) == CV_SET_MAGIC_VAL;
    }

    public static int CV_SEQ_ELTYPE(CvSeq seq) { return seq.flags() & CV_SEQ_ELTYPE_MASK; }
    public static int CV_SEQ_KIND(CvSeq seq) { return seq.flags() & CV_SEQ_KIND_MASK; }
    public static boolean CV_IS_SEQ_INDEX(CvSeq seq) {
        return (CV_SEQ_ELTYPE(seq) == CV_SEQ_ELTYPE_INDEX) &&
               (CV_SEQ_KIND(seq) == CV_SEQ_KIND_GENERIC);
    }
    public static boolean CV_IS_SEQ_CURVE(CvSeq seq)  { return CV_SEQ_KIND(seq) == CV_SEQ_KIND_CURVE; }
    public static boolean CV_IS_SEQ_CLOSED(CvSeq seq) { return (seq.flags() & CV_SEQ_FLAG_CLOSED) != 0; }
    public static boolean CV_IS_SEQ_CONVEX(CvSeq seq) { return false; }
    public static boolean CV_IS_SEQ_HOLE(CvSeq seq)   { return (seq.flags() & CV_SEQ_FLAG_HOLE) != 0; }
    public static boolean CV_IS_SEQ_SIMPLE(CvSeq seq) { return true;  }

    public static boolean CV_IS_SEQ_POINT_SET(CvSeq seq) {
        return CV_SEQ_ELTYPE(seq) == CV_32SC2 || CV_SEQ_ELTYPE(seq) == CV_32FC2;
    }
    public static boolean CV_IS_SEQ_POINT_SUBSET(CvSeq seq) {
        return CV_IS_SEQ_INDEX(seq) || CV_SEQ_ELTYPE(seq) == CV_SEQ_ELTYPE_PPOINT;
    }
    public static boolean CV_IS_SEQ_POLYLINE(CvSeq seq) {
        return CV_SEQ_KIND(seq) == CV_SEQ_KIND_CURVE && CV_IS_SEQ_POINT_SET(seq);
    }
    public static boolean CV_IS_SEQ_POLYGON(CvSeq seq) {
        return CV_IS_SEQ_POLYLINE(seq) && CV_IS_SEQ_CLOSED(seq);
    }
    public static boolean CV_IS_SEQ_CHAIN(CvSeq seq) {
        return CV_SEQ_KIND(seq) == CV_SEQ_KIND_CURVE && seq.elem_size() == 1;
    }
    public static boolean CV_IS_SEQ_CONTOUR(CvSeq seq) {
        return CV_IS_SEQ_CLOSED(seq) && (CV_IS_SEQ_POLYLINE(seq) || CV_IS_SEQ_CHAIN(seq));
    }
    public static boolean CV_IS_SEQ_CHAIN_CONTOUR(CvSeq seq) {
        return CV_IS_SEQ_CHAIN(seq) && CV_IS_SEQ_CLOSED(seq);
    }
    public static boolean CV_IS_SEQ_POLYGON_TREE(CvSeq seq) {
        return CV_SEQ_ELTYPE(seq) == CV_SEQ_ELTYPE_TRIAN_ATR &&
               CV_SEQ_KIND(seq) == CV_SEQ_KIND_BIN_TREE;
    }
    public static boolean CV_IS_GRAPH(CvSeq seq) {
        return CV_IS_SET(seq) && CV_SEQ_KIND(seq) == CV_SEQ_KIND_GRAPH;
    }
    public static boolean CV_IS_GRAPH_ORIENTED(CvSeq seq) {
        return (seq.flags() & CV_GRAPH_FLAG_ORIENTED) != 0;
    }
    public static boolean CV_IS_SUBDIV2D(CvSeq seq) {
        return CV_IS_SET(seq) && CV_SEQ_KIND(seq) == CV_SEQ_KIND_SUBDIV2D;
    }


    public static class CvSeqWriter extends Pointer {
        static { load(); }
        public CvSeqWriter() { allocate(); }
        public CvSeqWriter(int size) { allocateArray(size); }
        public CvSeqWriter(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSeqWriter position(int position) {
            return (CvSeqWriter)super.position(position);
        }

        public native int header_size();       public native CvSeqWriter header_size(int header_size);
        public native CvSeq seq();             public native CvSeqWriter seq(CvSeq seq);
        public native CvSeqBlock block();      public native CvSeqWriter block(CvSeqBlock block);
        @Cast("schar*")
        public native BytePointer ptr();       public native CvSeqWriter ptr(BytePointer ptr);
        @Cast("schar*")
        public native BytePointer block_min(); public native CvSeqWriter block_min(BytePointer block_min);
        @Cast("schar*")
        public native BytePointer block_max(); public native CvSeqWriter block_max(BytePointer block_max);
    }

    public static class CvSeqReader extends Pointer {
        static { load(); }
        public CvSeqReader() { allocate(); }
        public CvSeqReader(int size) { allocateArray(size); }
        public CvSeqReader(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvSeqReader position(int position) {
            return (CvSeqReader)super.position(position);
        }

        public native int header_size();       public native CvSeqReader header_size(int header_size);
        public native CvSeq seq();             public native CvSeqReader seq(CvSeq seq);
        public native CvSeqBlock block();      public native CvSeqReader block(CvSeqBlock block);
        @Cast("schar*")
        public native BytePointer ptr();       public native CvSeqReader ptr(BytePointer ptr);
        @Cast("schar*")
        public native BytePointer block_min(); public native CvSeqReader block_min(BytePointer block_min);
        @Cast("schar*")
        public native BytePointer block_max(); public native CvSeqReader block_max(BytePointer block_max);
        public native int delta_index();       public native CvSeqReader delta_index(int delta_index);
        @Cast("schar*")
        public native BytePointer prev_elem(); public native CvSeqReader prev_elem(BytePointer prev_elem);
    }

    public static native void CV_WRITE_SEQ_ELEM_VAR(Pointer elem_ptr, @ByVal CvSeqWriter writer);
    public static native void CV_WRITE_SEQ_ELEM(@ByVal CvPoint elem,  @ByVal CvSeqWriter writer);
    public static native void CV_NEXT_SEQ_ELEM(int elem_size, @ByVal CvSeqReader reader);
    public static native void CV_PREV_SEQ_ELEM(int elem_size, @ByVal CvSeqReader reader);
    public static native void CV_READ_SEQ_ELEM(@ByVal CvPoint elem, @ByVal CvSeqReader reader);
    public static native void CV_REV_READ_SEQ_ELEM(@ByVal CvPoint elem, @ByVal CvSeqReader reader);
    //public static native void CV_READ_CHAIN_POINT(CvPoint _pt, CvSeqReader reader);
    public static CvPoint CV_CURRENT_POINT(CvSeqReader reader) { return new CvPoint(reader.ptr()); }
    public static CvPoint CV_PREV_POINT(CvSeqReader reader)    { return new CvPoint(reader.prev_elem()); }
    public static void CV_READ_EDGE(CvPoint pt1, CvPoint pt2, CvSeqReader reader) {
        assert reader.seq().elem_size() == sizeof(CvPoint.class);
        pt1 = CV_PREV_POINT(reader);
        pt2 = CV_CURRENT_POINT(reader);
        reader.prev_elem(reader.ptr());
        CV_NEXT_SEQ_ELEM(sizeof(CvPoint.class), reader);
    }
    public static CvGraphEdge CV_NEXT_GRAPH_EDGE(CvGraphEdge edge, CvGraphVtx vertex) {
        assert edge.vtx(0).equals(vertex) || edge.vtx(1).equals(vertex);
        return edge.next(edge.vtx(1).equals(vertex) ? 1 : 0);
    }


    @Opaque public static class CvFileStorage extends Pointer {
        static { load(); }
        public CvFileStorage() { }
        public CvFileStorage(Pointer p) { super(p); }

        public static CvFileStorage open(String filename, CvMemStorage memstorage, int flags) {
            return open(filename, memstorage, flags, null);
        }
        public static CvFileStorage open(String filename, CvMemStorage memstorage, int flags, String encoding) {
            CvFileStorage f = cvOpenFileStorage(filename, memstorage, flags, encoding);
            if (f != null) {
                f.deallocator(new ReleaseDeallocator(f));
            }
            return f;
        }

        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends CvFileStorage implements Deallocator {
            ReleaseDeallocator(CvFileStorage p) { super(p); }
            @Override public void deallocate() { cvReleaseFileStorage(this); }
        }
    }

    public static final int
            CV_STORAGE_READ         = 0,
            CV_STORAGE_WRITE        = 1,
            CV_STORAGE_WRITE_TEXT   = CV_STORAGE_WRITE,
            CV_STORAGE_WRITE_BINARY = CV_STORAGE_WRITE,
            CV_STORAGE_APPEND       = 2,
            CV_STORAGE_MEMORY       = 4,
            CV_STORAGE_FORMAT_MASK  = 7<<3,
            CV_STORAGE_FORMAT_AUTO  = 0,
            CV_STORAGE_FORMAT_XML   = 8,
            CV_STORAGE_FORMAT_YAML  =16;

    public static class CvAttrList extends Pointer {
        static { load(); }
        public CvAttrList() { allocate(); }
        public CvAttrList(int size) { allocateArray(size); }
        public CvAttrList(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvAttrList position(int position) {
            return (CvAttrList)super.position(position);
        }

        @Cast("const char**")
        public native PointerPointer attr(); public native CvAttrList attr(PointerPointer attr);
        public native CvAttrList next();     public native CvAttrList next(CvAttrList next);

        public static final CvAttrList EMPTY = new CvAttrList();
    }
    public static CvAttrList cvAttrList(PointerPointer attr, CvAttrList next) {
        return new CvAttrList().attr(attr).next(next);
    }
    public static CvAttrList cvAttrList() {
        return new CvAttrList();
    }

    public static final int
            CV_NODE_NONE       = 0,
            CV_NODE_INT        = 1,
            CV_NODE_INTEGER    = CV_NODE_INT,
            CV_NODE_REAL       = 2,
            CV_NODE_FLOAT      = CV_NODE_REAL,
            CV_NODE_STR        = 3,
            CV_NODE_STRING     = CV_NODE_STR,
            CV_NODE_REF        = 4,
            CV_NODE_SEQ        = 5,
            CV_NODE_MAP        = 6,
            CV_NODE_TYPE_MASK  = 7,

            CV_NODE_FLOW       = 8,
            CV_NODE_USER       = 16,
            CV_NODE_EMPTY      = 32,
            CV_NODE_NAMED      = 64,

            CV_NODE_SEQ_SIMPLE = 256;
    public static int     CV_NODE_TYPE(int flags)          { return flags & CV_NODE_TYPE_MASK; }
    public static boolean CV_NODE_IS_INT(int flags)        { return CV_NODE_TYPE(flags) == CV_NODE_INT; }
    public static boolean CV_NODE_IS_REAL(int flags)       { return CV_NODE_TYPE(flags) == CV_NODE_REAL; }
    public static boolean CV_NODE_IS_STRING(int flags)     { return CV_NODE_TYPE(flags) == CV_NODE_STRING; }
    public static boolean CV_NODE_IS_SEQ(int flags)        { return CV_NODE_TYPE(flags) == CV_NODE_SEQ; }
    public static boolean CV_NODE_IS_MAP(int flags)        { return CV_NODE_TYPE(flags) == CV_NODE_MAP; }
    public static boolean CV_NODE_IS_COLLECTION(int flags) { return CV_NODE_TYPE(flags) >= CV_NODE_SEQ; }
    public static boolean CV_NODE_IS_FLOW(int flags)       { return (flags & CV_NODE_FLOW) != 0; }
    public static boolean CV_NODE_IS_EMPTY(int flags)      { return (flags & CV_NODE_EMPTY) != 0; }
    public static boolean CV_NODE_IS_USER(int flags)       { return (flags & CV_NODE_USER) != 0; }
    public static boolean CV_NODE_HAS_NAME(int flags)      { return (flags & CV_NODE_NAMED) != 0; }
    public static boolean CV_NODE_SEQ_IS_SIMPLE(CvSeq seq) { return (seq.flags() & CV_NODE_SEQ_SIMPLE) != 0; }

    public static class CvString extends Pointer {
        static { load(); }
        public CvString() { allocate(); }
        public CvString(int size) { allocateArray(size); }
        public CvString(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvString position(int position) {
            return (CvString)super.position(position);
        }

        public native int len();         public native CvString len(int len);
        @Cast("char*")
        public native BytePointer ptr(); public native CvString ptr(BytePointer ptr);
    }

    public static class CvStringHashNode extends Pointer {
        static { load(); }
        public CvStringHashNode() { allocate(); }
        public CvStringHashNode(int size) { allocateArray(size); }
        public CvStringHashNode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStringHashNode position(int position) {
            return (CvStringHashNode)super.position(position);
        }

        public native int hashval();           public native CvStringHashNode hashval(int hashval);
        public native @ByRef CvString str();   public native CvStringHashNode str(CvString str);
        public native CvStringHashNode next(); public native CvStringHashNode next(CvStringHashNode next);
    }

    @Opaque public static class CvFileNodeHash extends Pointer {
        static { load(); }
        public CvFileNodeHash() { }
        public CvFileNodeHash(Pointer p) { super(p); }
    }

    public static class CvFileNode extends Pointer {
        static { load(); }
        public CvFileNode() { allocate(); }
        public CvFileNode(int size) { allocateArray(size); }
        public CvFileNode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFileNode position(int position) {
            return (CvFileNode)super.position(position);
        }

        public native int tag();         public native CvFileNode tag(int tag);
        public native CvTypeInfo info(); public native CvFileNode info(CvTypeInfo info);

        // union { } data
        @Name("data.f")   public native double          data_f();   public native CvFileNode data_f(double f);
        @Name("data.i")   public native int             data_i();   public native CvFileNode data_i(int i);
        @Name("data.str") public native @ByRef CvString data_str(); public native CvFileNode data_str(CvString str);
        @Name("data.seq") public native CvSeq           data_seq(); public native CvFileNode data_seq(CvSeq seq);
        @Name("data.map") public native CvFileNodeHash  data_map(); public native CvFileNode data_map(CvFileNodeHash map);
    }


    public static class CvIsInstanceFunc extends FunctionPointer {
        static { load(); }
        public    CvIsInstanceFunc(Pointer p) { super(p); }
        protected CvIsInstanceFunc() { allocate(); }
        private native void allocate();
        public native int call(@Const Pointer struct_ptr);
    }
    public static class CvReleaseFunc extends FunctionPointer {
        static { load(); }
        public    CvReleaseFunc(Pointer p) { super(p); }
        protected CvReleaseFunc() { allocate(); }
        private native void allocate();
        public native void call(PointerPointer struct_dblptr);
    }
    public static class CvReadFunc extends FunctionPointer {
        static { load(); }
        public    CvReadFunc(Pointer p) { super(p); }
        protected CvReadFunc() { allocate(); }
        private native void allocate();
        public native Pointer call(CvFileStorage storage, CvFileNode node);
    }
    public static class CvWriteFunc extends FunctionPointer {
        static { load(); }
        public    CvWriteFunc(Pointer p) { super(p); }
        protected CvWriteFunc() { allocate(); }
        private native void allocate();
        public native void call(CvFileStorage storage, String name,
                @Const Pointer struct_ptr, @ByVal CvAttrList attributes);
    }
    public static class CvCloneFunc extends FunctionPointer {
        static { load(); }
        public    CvCloneFunc(Pointer p) { super(p); }
        protected CvCloneFunc() { allocate(); }
        private native void allocate();
        public native Pointer call(@Const Pointer struct_ptr);
    }
    public static class CvTypeInfo extends Pointer {
        static { load(); }
        public CvTypeInfo() { allocate(); }
        public CvTypeInfo(int size) { allocateArray(size); }
        public CvTypeInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvTypeInfo position(int position) {
            return (CvTypeInfo)super.position(position);
        }

        public native int flags();             public native CvTypeInfo flags(int flags);
        public native int header_size();       public native CvTypeInfo header_size(int header_size);
        public native CvTypeInfo prev();       public native CvTypeInfo prev(CvTypeInfo prev);
        public native CvTypeInfo next();       public native CvTypeInfo next(CvTypeInfo next);
        @Cast("const char*")
        public native BytePointer type_name(); public native CvTypeInfo type_name(BytePointer type_name);

        public native CvIsInstanceFunc is_instance(); public native CvTypeInfo is_instance(CvIsInstanceFunc is_instance);
        public native CvReleaseFunc release();        public native CvTypeInfo release(CvReleaseFunc release);
        public native CvReadFunc read();              public native CvTypeInfo read(CvReadFunc read);
        public native CvWriteFunc write();            public native CvTypeInfo write(CvWriteFunc write);
        @Override
        public native CvCloneFunc clone();            public native CvTypeInfo clone(CvCloneFunc clone);
    }


    public static class CvPluginFuncInfo extends Pointer {
        static { load(); }
        public CvPluginFuncInfo() { allocate(); }
        public CvPluginFuncInfo(int size) { allocateArray(size); }
        public CvPluginFuncInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvPluginFuncInfo position(int position) {
            return (CvPluginFuncInfo)super.position(position);
        }

        public native PointerPointer func_addr();  public native CvPluginFuncInfo func_addr(PointerPointer func_addr);
        public native Pointer default_func_addr(); public native CvPluginFuncInfo default_func_addr(Pointer default_func_addr);
        @Cast("const char*")
        public native BytePointer func_names();    public native CvPluginFuncInfo func_names(BytePointer func_names);
        public native int search_modules();        public native CvPluginFuncInfo search_modules(int search_modules);
        public native int loaded_from();           public native CvPluginFuncInfo loaded_from(int loaded_from);
    }

    public static class CvModuleInfo extends Pointer {
        static { load(); }
        public CvModuleInfo() { allocate(); }
        public CvModuleInfo(int size) { allocateArray(size); }
        public CvModuleInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvModuleInfo position(int position) {
            return (CvModuleInfo)super.position(position);
        }

        public native CvModuleInfo next();          public native CvModuleInfo next(CvModuleInfo next);
        @Cast("const char*")
        public native BytePointer name();           public native CvModuleInfo name(BytePointer name);
        @Cast("const char*")
        public native BytePointer version();        public native CvModuleInfo version(BytePointer version);
        public native CvPluginFuncInfo func_tab();  public native CvModuleInfo func_tab(CvPluginFuncInfo func_tab);
    }


    public static native Pointer cvAlloc(@Cast("size_t") long size);
    public static native void cvFree_(Pointer ptr);
    public static void cvFree(Pointer ptr) {
        cvFree_(ptr);
        ptr.setNull();
    }


    public static native IplImage cvCreateImageHeader(@ByVal CvSize size, int depth, int channels);
    public static native IplImage cvInitImageHeader(IplImage image, @ByVal CvSize size, int depth,
            int channels, int origin/*=0*/, int align/*=4*/);
    public static native IplImage cvCreateImage(@ByVal CvSize size, int depth, int channels);
    public static native void cvReleaseImageHeader(@ByPtrPtr IplImage image);
    public static native void cvReleaseImage(@ByPtrPtr IplImage image);
    public static native IplImage cvCloneImage(IplImage image);
    public static native void cvSetImageCOI(IplImage image, int coi);
    public static native int cvGetImageCOI(IplImage image);
    public static native void cvSetImageROI(IplImage image, @ByVal CvRect rect);
    public static native void cvResetImageROI(IplImage image);
    public static native @ByVal CvRect cvGetImageROI(IplImage image);

    public static native CvMat cvCreateMatHeader(int rows, int cols, int type);
    public static final int CV_AUTOSTEP = 0x7fffffff;
    public static native CvMat cvInitMatHeader(CvMat mat, int rows, int cols, int type,
            Pointer data/*=null*/, int step/*=CV_AUTOSTEP*/);
    public static native CvMat cvCreateMat(int rows, int cols, int type);
    public static native void cvReleaseMat(@ByPtrPtr CvMat mat);
    public static native CvMat cvCloneMat(CvMat mat);

    public static native CvMat cvGetSubRect(CvArr arr, CvMat submat, @ByVal CvRect rect);
    public static CvMat cvGetSubArr(CvArr arr, CvMat submat, @ByVal CvRect rect) {
        return cvGetSubRect(arr, submat, rect);
    }
    public static native CvMat cvGetRows(CvArr arr, CvMat submat, int start_row,
            int end_row/*=start_row+1*/, int delta_row/*=1*/);
    public static CvMat cvGetRow(CvArr arr, CvMat submat, int row) {
        return cvGetRows(arr, submat, row, row + 1, 1);
    }
    public static native CvMat cvGetCols(CvArr arr, CvMat submat, int start_col,
            int end_col/*=start_col+1*/);
    public static CvMat cvGetCol(CvArr arr, CvMat submat, int col) {
        return cvGetCols(arr, submat, col, col + 1);
    }
    public static native CvMat cvGetDiag(CvArr arr, CvMat submat, int diag/*=0*/);
    public static native void cvScalarToRawData(CvScalar scalar, Pointer data,
            int type, int extend_to_12/*=0*/);
    public static native void cvRawDataToScalar(Pointer data, int type, CvScalar scalar);

    public static native CvMatND cvCreateMatNDHeader(int dims, int[] sizes, int type);
    public static native CvMatND cvCreateMatND(int dims, int[] sizes, int type);
    public static native CvMatND cvInitMatNDHeader(CvMatND mat, int dims, int[] sizes,
            int type, Pointer data/*=null*/);
    public static native void cvReleaseMatND(@ByPtrPtr CvMatND mat);
    public static native CvMatND cvCloneMatND(CvMatND mat);

    public static native CvSparseMat cvCreateSparseMat(int dims, int[] sizes, int type);
    public static native void cvReleaseSparseMat(@ByPtrPtr CvSparseMat mat);
    public static native CvSparseMat cvCloneSparseMat(CvSparseMat mat);
    public static native CvSparseNode cvInitSparseMatIterator(CvSparseMat mat,
            CvSparseMatIterator mat_iterator);
    public static native CvSparseNode cvGetNextSparseNode(CvSparseMatIterator mat_iterator);


    public static final int CV_MAX_ARR = 10;

    public static class CvNArrayIterator extends Pointer {
        static { load(); }
        public CvNArrayIterator() { allocate(); }
        public CvNArrayIterator(int size) { allocateArray(size); }
        public CvNArrayIterator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvNArrayIterator position(int position) {
            return (CvNArrayIterator)super.position(position);
        }

        public native int count();            public native CvNArrayIterator count(int count);
        public native int dims();             public native CvNArrayIterator dims(int dims);

        public native @ByRef CvSize size();   public native CvNArrayIterator size(CvSize size);
        @Cast("uchar*")
        public native BytePointer/*[CV_MAX_ARR]*/ ptr(int i);   public native CvNArrayIterator ptr(int i, BytePointer ptr);
        public native int        /*[CV_MAX_DIM]*/ stack(int i); public native CvNArrayIterator stack(int i, int ptr);
        public native CvMatND    /*[CV_MAX_ARR]*/ hdr(int i);   public native CvNArrayIterator hdr(int i, CvMatND hdr);
    }

    public static final int
            CV_NO_DEPTH_CHECK    = 1,
            CV_NO_CN_CHECK       = 2,
            CV_NO_SIZE_CHECK     = 4;

    public static int cvInitNArrayIterator(int count, CvArr[] arrs,
            CvArr mask, CvMatND stubs, CvNArrayIterator array_iterator, int flags/*=0*/) {
        return cvInitNArrayIterator(count, new CvArrArray(arrs), mask, stubs, array_iterator, flags);
    }
    public static native int cvInitNArrayIterator(int count, CvArrArray arrs,
            CvArr mask, CvMatND stubs, CvNArrayIterator array_iterator, int flags/*=0*/);
    public static native int cvNextNArraySlice(CvNArrayIterator array_iterator);

    public static native int cvGetElemType(CvArr arr);
    public static native int cvGetDims(CvArr arr, int[] sizes/*=null*/);
    public static native int cvGetDimSize(CvArr arr, int index);

    public static native Pointer cvPtr1D(CvArr arr, int idx0, int[] type/*=null*/);
    public static native Pointer cvPtr2D(CvArr arr, int idx0, int idx1, int[] type/*=null*/);
    public static native Pointer cvPtr3D(CvArr arr, int idx0, int idx1, int idx2, int[] type/*=null*/);
    public static native Pointer cvPtrND(CvArr arr, int[] idx, int[] type/*=null*/,
            int create_node/*=1*/, @Cast("unsigned*") int[] precalc_hashval/*=null*/);
    public static native @ByVal CvScalar cvGet1D(CvArr arr, int idx0);
    public static native @ByVal CvScalar cvGet2D(CvArr arr, int idx0, int idx1);
    public static native @ByVal CvScalar cvGet3D(CvArr arr, int idx0, int idx1, int idx2);
    public static native @ByVal CvScalar cvGetND(CvArr arr, int[] idx);
    public static native double cvGetReal1D(CvArr arr, int idx0);
    public static native double cvGetReal2D(CvArr arr, int idx0, int idx1);
    public static native double cvGetReal3D(CvArr arr, int idx0, int idx1, int idx2);
    public static native double cvGetRealND(CvArr arr, int[] idx);
    public static native void cvSet1D(CvArr arr, int idx0, @ByVal CvScalar value);
    public static native void cvSet2D(CvArr arr, int idx0, int idx1, @ByVal CvScalar value);
    public static native void cvSet3D(CvArr arr, int idx0, int idx1, int idx2, @ByVal CvScalar value);
    public static native void cvSetND(CvArr arr, int[] idx, @ByVal CvScalar value);
    public static native void cvSetReal1D(CvArr arr, int idx0, double value);
    public static native void cvSetReal2D(CvArr arr, int idx0, int idx1, double value);
    public static native void cvSetReal3D(CvArr arr, int idx0, int idx1, int idx2, double value);
    public static native void cvSetRealND(CvArr arr, int[] idx, double value);
    public static native void cvClearND(CvArr arr, int[] idx);

    public static native CvMat cvGetMat(CvArr arr, CvMat header, int[] coi/*=null*/, int allowND/*=0*/);
    public static native IplImage cvGetImage(CvArr arr, IplImage image_header);

    public static native CvArr cvReshapeMatND(CvArr arr, int sizeof_header, CvArr header,
            int new_cn, int new_dims, int[] new_sizes);
    public static CvArr cvReshapeND(CvArr arr, CvArr header, int new_cn, int new_dims, int[] new_sizes) {
        return cvReshapeMatND(arr, sizeof(header.getClass()), header, new_cn, new_dims, new_sizes);
    }
    public static native CvMat cvReshape(CvArr arr, CvMat header, int new_cn, int new_rows/*=0*/);

    public static native void cvRepeat(CvArr src, CvArr dst);

    public static native void cvCreateData(CvArr arr);
    public static native void cvReleaseData(CvArr arr);
    public static native void cvSetData(CvArr arr, Pointer data, int step);
    public static native void cvGetRawData(CvArr arr, @Cast("uchar**") @ByPtrPtr BytePointer data,
            int[] step/*=null*/, CvSize roi_size/*=null*/);

    public static native @ByVal CvSize cvGetSize(CvArr arr);

    public static native void cvCopy(CvArr src, CvArr dst, CvArr mask/*=null*/);
    public static void cvCopy(CvArr src, CvArr dst) {
        cvCopy(src, dst, null);
    }
    public static native void cvSet(CvArr arr, @ByVal CvScalar value, CvArr mask/*=null*/);
    public static void cvSet(CvArr arr, CvScalar value) {
        cvSet(arr, value, null);
    }
    public static native void cvSetZero(CvArr arr);
    public static void cvZero(CvArr arr) {
        cvSetZero(arr);
    }

    public static native void cvSplit(CvArr src, CvArr dst0, CvArr dst1, CvArr dst2, CvArr dst3);
    public static native void cvMerge(CvArr src0, CvArr src1, CvArr src2, CvArr src3, CvArr dst);
    public static void cvMixChannels(CvArr[] src, int src_count,
            CvArr[] dst, int dst_count, int[] from_to, int pair_count) {
        cvMixChannels(new CvArrArray(src), src_count, new CvArrArray(dst), dst_count, from_to, pair_count);
    }
    public static native void cvMixChannels(@Const CvArrArray src, int src_count,
            CvArrArray dst, int dst_count, int[] from_to, int pair_count);

    public static native void cvConvertScale(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/);
    public static void cvCvtScale(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/) {
        cvConvertScale(src, dst, scale, shift);
    }
    public static void cvScale(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/) {
        cvConvertScale(src, dst, scale, shift);
    }
    public static void cvConvert(CvArr src, CvArr dst) {
        cvConvertScale(src, dst, 1, 0);
    }
    public static native void cvConvertScaleAbs(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/);
    public static void cvCvtScaleAbs(CvArr src, CvArr dst, double scale/*=1*/, double shift/*=0*/) {
        cvConvertScaleAbs(src, dst, scale, shift);
    }

    public static native @ByVal CvTermCriteria cvCheckTermCriteria(@ByVal CvTermCriteria criteria,
            double default_eps, int default_max_iters);

    public static native void cvAdd(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvAddS(CvArr src, @ByVal CvScalar value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvSub(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static void cvSubS(CvArr src, CvScalar value, CvArr dst, CvArr mask/*=null*/) {
        cvAddS(src, cvScalar(-value.val(0), -value.val(1), -value.val(2), -value.val(3)), dst, mask);
    }
    public static native void cvSubRS(CvArr src, @ByVal CvScalar value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvMul(CvArr src1, CvArr src2, CvArr dst, double scale/*=1*/);
    public static native void cvDiv(CvArr src1, CvArr src2, CvArr dst, double scale/*=1*/);
    public static native void cvScaleAdd(CvArr src1, @ByVal CvScalar scale, CvArr src2, CvArr dst);
    public static void cvAXPY(CvArr A, double real_scalar, CvArr B, CvArr C) {
        cvScaleAdd(A, cvRealScalar(real_scalar), B, C);
    }
    public static native void cvAddWeighted(CvArr src1, double alpha, CvArr src2, double beta,
            double gamma, CvArr dst);
    public static native double cvDotProduct(CvArr src1, CvArr src2);
    public static native void cvAnd(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvAndS(CvArr src, @ByVal CvScalar value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvOr(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvOrS(CvArr src, @ByVal CvScalar value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvXor(CvArr src1, CvArr src2, CvArr dst, CvArr mask/*=null*/);
    public static native void cvXorS(CvArr src, @ByVal CvScalar value, CvArr dst, CvArr mask/*=null*/);
    public static native void cvNot(CvArr src, CvArr dst);
    public static native void cvInRange(CvArr src, CvArr lower, CvArr upper, CvArr dst);
    public static native void cvInRangeS(CvArr src, @ByVal CvScalar lower, @ByVal CvScalar upper, CvArr dst);

    public static final int
            CV_CMP_EQ  = 0,
            CV_CMP_GT  = 1,
            CV_CMP_GE  = 2,
            CV_CMP_LT  = 3,
            CV_CMP_LE  = 4,
            CV_CMP_NE  = 5;
    public static native void cvCmp(CvArr src1, CvArr src2, CvArr dst, int cmp_op);
    public static native void cvCmpS(CvArr src, double value, CvArr dst, int cmp_op);
    public static native void cvMin(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvMax(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvMinS(CvArr src, double value, CvArr dst);
    public static native void cvMaxS(CvArr src, double value, CvArr dst);
    public static native void cvAbsDiff(CvArr src1, CvArr src2, CvArr dst);
    public static native void cvAbsDiffS(CvArr src, CvArr dst, @ByVal CvScalar value/*=cvScalarAll(0)*/);
    public static void cvAbs(CvArr src, CvArr dst) {
        cvAbsDiffS(src, dst, cvScalarAll(0.0));
    }


    public static native void cvCartToPolar(CvArr x, CvArr y, CvArr magnitude,
            CvArr angle/*=null*/, int angle_in_degrees/*=0*/);
    public static native void cvPolarToCart(CvArr magnitude, CvArr angle,
            CvArr x, CvArr y, int angle_in_degrees/*=0*/);
    public static native void cvPow(CvArr src, CvArr dst, double power);
    public static native void cvExp(CvArr src, CvArr dst);
    public static native void cvLog(CvArr src, CvArr dst);
    public static native float cvFastArctan(float y, float x);
    public static native float cvCbrt(float value);

    public static final int
            CV_CHECK_RANGE  = 1,
            CV_CHECK_QUIET  = 2;
    public static native int cvCheckArr(CvArr arr, int flags/*=0*/, double min_val/*=0*/, double max_val/*=0*/);
    public static int cvCheckArray(CvArr arr, int flags/*=0*/, double min_val/*=0*/, double max_val/*=0*/) {
        return cvCheckArr(arr, flags, min_val, max_val);
    }

    public static final int
        CV_RAND_UNI     = 0,
        CV_RAND_NORMAL  = 1;
    public static native void cvRandArr(CvRNG rng, CvArr arr, int dist_type,
            @ByVal CvScalar param1, @ByVal CvScalar param2);
    public static native void cvRandShuffle(CvArr mat, CvRNG rng, double iter_factor/*=1*/);

    public static final int
            CV_SORT_EVERY_ROW = 0,
            CV_SORT_EVERY_COLUMN = 1,
            CV_SORT_ASCENDING = 0,
            CV_SORT_DESCENDING = 16;
    public static native void cvSort(CvArr src, CvArr dst/*=null*/, CvArr idxmat/*=null*/, int flags/*=0*/);

    public static native int cvSolveCubic(CvMat coeffs, CvMat roots);
    public static native void cvSolvePoly(CvMat coeffs, CvMat roots, int maxiter/*=20*/, int fig/*=100*/);


    public static native void cvCrossProduct(CvArr src1, CvArr src2, CvArr dst);
    public static void cvMatMulAdd(CvArr src1, CvArr src2, CvArr src3, CvArr dst) {
        cvGEMM(src1, src2, 1., src3, 1., dst, 0);
    }
    public static void cvMatMul(CvArr src1, CvArr src2, CvArr dst) {
        cvMatMulAdd(src1, src2, null, dst);
    }
    public static final int
            CV_GEMM_A_T = 1,
            CV_GEMM_B_T = 2,
            CV_GEMM_C_T = 4;
    public static native void cvGEMM(CvArr srcA, CvArr srcB, double alpha,  CvArr srcC, double beta,
            CvArr dst, int tABC/*=0*/);
    public static void cvMatMulAddEx(CvArr srcA, CvArr srcB, double alpha,  CvArr srcC, double beta,
            CvArr dst, int tABC/*=0*/) {
        cvGEMM(srcA, srcB, alpha, srcC, beta, dst, tABC);
    }
    public static native void cvTransform(CvArr src, CvArr dst, CvMat transmat, CvMat shiftvec/*=null*/);
    public static void cvMatMulAddS(CvArr src, CvArr dst, CvMat transmat, CvMat shiftvec/*=null*/) {
        cvTransform(src, dst, transmat, shiftvec);
    }
    public static native void cvPerspectiveTransform(CvArr src, CvArr dst, CvMat mat);
    public static native void cvMulTransposed(CvArr src, CvArr dst, int order, CvArr delta/*=null*/,
            double scale/*=1*/);
    public static native void cvTranspose(CvArr src, CvArr dst);
    public static void cvT(CvArr src, CvArr dst) {
        cvTranspose(src, dst);
    }
    public static native void cvCompleteSymm(CvMat matrix, int LtoR/*=0*/);
    public static native void cvFlip(CvArr src, CvArr dst, int flip_mode/*=0*/);
    public static void cvMirror(CvArr src, CvArr dst, int flip_mode/*=0*/) {
        cvFlip(src, dst, flip_mode);
    }

    public static final int
            CV_SVD_MODIFY_A  = 1,
            CV_SVD_U_T       = 2,
            CV_SVD_V_T       = 4;
    public static native void cvSVD(CvArr A, CvArr W, CvArr U/*=null*/, CvArr V/*=null*/, int flags/*=0*/);
    public static native void cvSVBkSb(CvArr W, CvArr U, CvArr V, CvArr B, CvArr X, int flags);

    public static final int
            CV_LU  = 0,
            CV_SVD = 1,
            CV_SVD_SYM = 2,
            CV_CHOLESKY = 3,
            CV_QR = 4,
            CV_LSQ = 8,
            CV_NORMAL = 16;
    public static native double cvInvert(CvArr src, CvArr dst, int method/*=CV_LU*/);
    public static double cvInvert(CvArr src, CvArr dst) {
        return cvInvert(src, dst, CV_LU);
    }
    public static double cvInv(CvArr src, CvArr dst, int method/*=CV_LU*/) {
        return cvInvert(src, dst, method);
    }
    public static double cvInv(CvArr src, CvArr dst) {
        return cvInvert(src, dst, CV_LU);
    }
    public static native int cvSolve(CvArr A, CvArr B, CvArr X, int method/*=CV_LU*/);
    public static int cvSolve(CvArr A, CvArr B, CvArr X) {
        return cvSolve(A, B, X, CV_LU);
    }
    public static native double cvDet(CvArr mat);
    public static native @ByVal CvScalar cvTrace(CvArr mat);
    public static native void cvEigenVV(CvArr mat, CvArr evects, CvArr evals, double eps/*=0*/,
            int lowindex/*=-1*/, int highindex/*=-1*/);
//    public static native void cvSelectedEigenVV(CvArr mat, CvArr evects, CvArr evals,
//            int lowindex, int highindex);

    public static native void cvSetIdentity(CvArr mat, @ByVal CvScalar value/*=cvRealScalar(1)*/);
    public static void cvSetIdentity(CvArr mat, double value) {
        cvSetIdentity(mat, cvRealScalar(value));
    }
    public static void cvSetIdentity(CvArr mat) {
        cvSetIdentity(mat, 1);
    }
    public static native CvArr cvRange(CvArr mat, double start, double end);

    public static final int
            CV_COVAR_SCRAMBLED = 0,
            CV_COVAR_NORMAL    = 1,
            CV_COVAR_USE_AVG   = 2,
            CV_COVAR_SCALE     = 4,
            CV_COVAR_ROWS      = 8,
            CV_COVAR_COLS     = 16;
    public static void cvCalcCovarMatrix(CvArr[] vects, int count, CvArr cov_mat, CvArr avg, int flags) {
        cvCalcCovarMatrix(new CvArrArray(vects), count, cov_mat, avg, flags);
    }
    public static native void cvCalcCovarMatrix(@Const CvArrArray vects,
            int count, CvArr cov_mat, CvArr avg, int flags);

    public static final int
            CV_PCA_DATA_AS_ROW = 0,
            CV_PCA_DATA_AS_COL = 1,
            CV_PCA_USE_AVG     = 2;
    public static native void cvCalcPCA(CvArr data, CvArr mean, CvArr eigenvals, CvArr eigenvects, int flags);
    public static native void cvProjectPCA(CvArr data, CvArr mean, CvArr eigenvects, CvArr result);
    public static native void cvBackProjectPCA(CvArr proj, CvArr mean, CvArr eigenvects, CvArr result);

    public static native double cvMahalanobis(CvArr vec1, CvArr vec2, CvArr mat);
    public static double cvMahalonobis(CvArr vec1, CvArr vec2, CvArr mat) {
        return cvMahalanobis(vec1, vec2, mat);
    }


    public static native @ByVal CvScalar cvSum(CvArr arr);
    public static native int cvCountNonZero(CvArr arr);
    public static native @ByVal CvScalar cvAvg(CvArr arr, CvArr mask/*=null*/);
    public static native void cvAvgSdv(CvArr arr, CvScalar mean, CvScalar std_dev, CvArr mask/*=null*/);
    public static native void cvMinMaxLoc(CvArr arr, double[] min_val, double[] max_val,
            CvPoint min_loc/*=null*/, CvPoint max_loc/*=null*/, CvArr mask/*=null*/);
    public static native void cvMinMaxLoc(CvArr arr, double[] min_val, double[] max_val,
            @Cast("CvPoint*") int[] min_loc/*=null*/, @Cast("CvPoint*") int[] max_loc/*=null*/, CvArr mask/*=null*/);
    public static void cvMinMaxLoc(CvArr arr, double[] min_val, double[] max_val) {
        cvMinMaxLoc(arr, min_val, max_val, (CvPoint)null, (CvPoint)null, null);
    }

    public static final int
            CV_C          = 1,
            CV_L1         = 2,
            CV_L2         = 4,
            CV_NORM_MASK  = 7,
            CV_RELATIVE   = 8,
            CV_DIFF       = 16,
            CV_MINMAX     = 32,

            CV_DIFF_C     = (CV_DIFF | CV_C),
            CV_DIFF_L1    = (CV_DIFF | CV_L1),
            CV_DIFF_L2    = (CV_DIFF | CV_L2),
            CV_RELATIVE_C = (CV_RELATIVE | CV_C),
            CV_RELATIVE_L1= (CV_RELATIVE | CV_L1),
            CV_RELATIVE_L2= (CV_RELATIVE | CV_L2);
    public static native double cvNorm(CvArr arr1, CvArr arr2/*=null*/,
            int norm_type/*=CV_L2*/, CvArr mask/*=null*/);
    public static double cvNorm(CvArr arr1, CvArr arr2/*=null*/,
            int norm_type/*=CV_L2*/) {
        return cvNorm(arr1, arr2, norm_type, null);
    }
    public static double cvNorm(CvArr arr1, CvArr arr2/*=null*/) {
        return cvNorm(arr1, arr2, CV_L2, null);
    }
    public static double cvNorm(CvArr arr1) {
        return cvNorm(arr1, null, CV_L2, null);
    }

    public static native void cvNormalize(CvArr src, CvArr dst, double a/*=1*/,
            double b/*=0*/, int norm_type/*=CV_L2*/, CvArr mask/*=null*/);
    public static void cvNormalize(CvArr src, CvArr dst) {
        cvNormalize(src, dst, 1, 0, CV_L2, null);
    }

    public static final int
            CV_REDUCE_SUM = 0,
            CV_REDUCE_AVG = 1,
            CV_REDUCE_MAX = 2,
            CV_REDUCE_MIN = 3;
    public static native void cvReduce(CvArr src, CvArr dst,
            int dim/*-1*/, int op/*=CV_REDUCE_SUM*/);


    public static final int
            CV_DXT_FORWARD  = 0,
            CV_DXT_INVERSE  = 1,
            CV_DXT_SCALE    = 2,
            CV_DXT_INV_SCALE = (CV_DXT_INVERSE + CV_DXT_SCALE),
            CV_DXT_INVERSE_SCALE = CV_DXT_INV_SCALE,
            CV_DXT_ROWS     = 4,
            CV_DXT_MUL_CONJ = 8;
    public static native void cvDFT(CvArr src, CvArr dst, int flags, int nonzero_rows/*=0*/);
    public static void cvFFT(CvArr src, CvArr dst, int flags, int nonzero_rows/*=0*/) {
        cvDFT(src, dst, flags, nonzero_rows);
    }
    public static native void cvMulSpectrums(CvArr src1, CvArr src2, CvArr dst, int flags);
    public static native int cvGetOptimalDFTSize(int size0);
    public static native void cvDCT(CvArr src, CvArr dst, int flags);


    public static native int cvSliceLength(@ByVal CvSlice slice, CvSeq seq);

    public static native CvMemStorage cvCreateMemStorage(int block_size/*=0*/);
    public static native CvMemStorage cvCreateChildMemStorage(CvMemStorage parent);
    public static native void cvReleaseMemStorage(@ByPtrPtr CvMemStorage storage);
    public static native void cvClearMemStorage(CvMemStorage storage);
    public static native void cvSaveMemStoragePos(CvMemStorage storage, CvMemStoragePos pos);
    public static native void cvRestoreMemStoragePos(CvMemStorage storage, CvMemStoragePos pos);
    public static native Pointer cvMemStorageAlloc(CvMemStorage storage, @Cast("size_t") long size);

    public static native @ByVal CvString cvMemStorageAllocString(CvMemStorage storage,
            String ptr, int len/*=-1*/);
    public static native @ByVal CvString cvMemStorageAllocString(CvMemStorage storage,
            @Cast("const char*") BytePointer ptr, int len/*=-1*/);

    public static native CvSeq cvCreateSeq(int seq_flags, @Cast("size_t") long header_size, int elem_size, CvMemStorage storage);
    public static native void cvSetSeqBlockSize(CvSeq seq, int delta_elems);
    public static native @Cast("schar*") BytePointer cvSeqPush(CvSeq seq, Pointer element/*=null*/);
    public static native @Cast("schar*") BytePointer cvSeqPushFront(CvSeq seq, Pointer element/*=null*/);
    public static native void cvSeqPop(CvSeq seq, Pointer element/*=null*/);
    public static native void cvSeqPopFront(CvSeq seq, Pointer element/*=null*/);
    public static final int
            CV_FRONT = 1,
            CV_BACK = 0;
    public static native void cvSeqPushMulti(CvSeq seq, Pointer elements, int count, int in_front/*=0*/);
    public static native void cvSeqPopMulti(CvSeq seq, Pointer elements, int count, int in_front/*=0*/);
    public static native Pointer cvSeqInsert(CvSeq seq, int before_index, Pointer element/*=null*/);
    public static native void cvSeqRemove(CvSeq seq, int index);
    public static native void cvClearSeq(CvSeq seq);
    public static native Pointer cvGetSeqElem(CvSeq seq, int index);
    public static native int cvSeqElemIdx(CvSeq seq, Pointer element, @ByPtrPtr CvSeqBlock block/*=null*/);

    public static native void cvStartAppendToSeq(CvSeq seq, CvSeqWriter writer);
    public static native void cvStartWriteSeq(int seq_flags, int header_size, int elem_size,
            CvMemStorage storage, CvSeqWriter writer);
    public static native CvSeq cvEndWriteSeq(CvSeqWriter writer);
    public static native void cvFlushSeqWriter(CvSeqWriter writer);

    public static native void cvStartReadSeq(CvSeq seq, CvSeqReader reader, int reverse/*=0*/);
    public static native int cvGetSeqReaderPos(CvSeqReader reader);
    public static native void cvSetSeqReaderPos(CvSeqReader reader, int index, int is_relative);

    public static Pointer cvCvtSeqToArray(CvSeq seq, Pointer elements) {
        return cvCvtSeqToArray(seq, elements, CV_WHOLE_SEQ);
    }
    public static native Pointer cvCvtSeqToArray(CvSeq seq, Pointer elements, @ByVal CvSlice slice/*=CV_WHOLE_SEQ*/);
    public static Pointer cvCvtSeqToArray(CvSeq seq, Buffer elements) {
        return cvCvtSeqToArray(seq, elements, CV_WHOLE_SEQ);
    }
    public static native Pointer cvCvtSeqToArray(CvSeq seq, Buffer elements, @ByVal CvSlice slice/*=CV_WHOLE_SEQ*/);
    public static native CvSeq cvMakeSeqHeaderForArray(int seq_type, int header_size, int elem_size,
            Pointer elements, int total, CvSeq seq, CvSeqBlock block);
    public static native CvSeq cvSeqSlice(CvSeq seq, @ByVal CvSlice slice,
            CvMemStorage storage/*=null*/, int copy_data/*=0*/);
    public static CvSeq cvCloneSeq(CvSeq seq, CvMemStorage storage/*=null*/) {
        return cvSeqSlice(seq, CV_WHOLE_SEQ, storage, 1);
    }
    public static native void cvSeqRemoveSlice(CvSeq seq, @ByVal CvSlice slice);
    public static native void cvSeqInsertSlice(CvSeq seq, int before_index, CvArr from_arr);
    public static class CvCmpFunc extends FunctionPointer {
        static { load(); }
        public    CvCmpFunc(Pointer p) { super(p); }
        protected CvCmpFunc() { allocate(); }
        private native void allocate();
        public native int call(@Const Pointer a, @Const Pointer b, Pointer userdata);
    }
    public static native void cvSeqSort(CvSeq seq, CvCmpFunc func, Pointer userdata/*=null*/);
    public static native Pointer cvSeqSearch(CvSeq seq, Pointer elem, CvCmpFunc func,
            int is_sorted, int[] elem_idx, Pointer userdata/*=null*/);
    public static native void cvSeqInvert(CvSeq seq);
    public static native int cvSeqPartition(CvSeq seq, CvMemStorage storage,
            @ByPtrPtr CvSeq labels, CvCmpFunc is_equal, Pointer userdata);

    public static native void cvChangeSeqBlock(CvSeqReader reader, int direction);
    public static native void cvCreateSeqBlock(CvSeqWriter writer);


    public static native CvSet cvCreateSet(int set_flags, int header_size, int elem_size, CvMemStorage storage);
    public static native int cvSetAdd(CvSet set_header, CvSetElem elem/*=null*/,
            @ByPtrPtr CvSetElem inserted_elem/*=null*/);
    public CvSetElem cvSetNew(CvSet set_header) {
        CvSetElem elem = set_header.free_elems();
        if (elem != null) {
            set_header.free_elems(elem.next_free());
            elem.flags(elem.flags() & CV_SET_ELEM_IDX_MASK);
            set_header.active_count(set_header.active_count() + 1);
        } else {
            cvSetAdd(set_header, null, elem);
        }
        return elem;
    }
    public void cvSetRemoveByPtr(CvSet set_header, CvSetElem elem) {
        assert elem.flags() >= 0 /*&& (elem.flags & CV_SET_ELEM_IDX_MASK) < total*/;
        elem.next_free(set_header.free_elems());
        elem.flags((elem.flags() & CV_SET_ELEM_IDX_MASK) | CV_SET_ELEM_FREE_FLAG);
        set_header.free_elems(elem);
        set_header.active_count(set_header.active_count() - 1);
    }
    public static native void cvSetRemove(CvSet set_header, int index);
    public static CvSetElem cvGetSetElem(CvSet set_header, int index) {
        CvSetElem elem = new CvSetElem(cvGetSeqElem(set_header, index));
        return elem != null && CV_IS_SET_ELEM(elem) ? elem : null;
    }
    public static native void cvClearSet(CvSet set_header);


    public static native CvGraph cvCreateGraph(int graph_flags, int header_size,
            int vtx_size, int edge_size, CvMemStorage storage);
    public static native int cvGraphAddVtx(CvGraph graph, CvGraphVtx vtx/*=null*/,
            @ByPtrPtr CvGraphVtx inserted_vtx/*=null*/);
    public static native int cvGraphRemoveVtx(CvGraph graph, int index);
    public static native int cvGraphRemoveVtxByPtr(CvGraph graph, CvGraphVtx vtx);
    public static native int cvGraphAddEdge(CvGraph graph, int start_idx, int end_idx,
            CvGraphEdge edge/*=null*/, @ByPtrPtr CvGraphEdge inserted_edge/*=null*/);
    public static native int cvGraphAddEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx,
        CvGraphEdge edge/*=null*/, @ByPtrPtr CvGraphEdge inserted_edge/*=null*/);
    public static native void cvGraphRemoveEdge(CvGraph graph, int start_idx, int end_idx);
    public static native void cvGraphRemoveEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx);
    public static native CvGraphEdge cvFindGraphEdge(CvGraph graph, int start_idx, int end_idx);
    public static native CvGraphEdge cvFindGraphEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx);
    public static CvGraphEdge cvGraphFindEdge(CvGraph graph, int start_idx, int end_idx) {
        return cvFindGraphEdge(graph, start_idx, end_idx);
    }
    public static CvGraphEdge cvGraphFindEdgeByPtr(CvGraph graph, CvGraphVtx start_vtx, CvGraphVtx end_vtx) {
        return cvFindGraphEdgeByPtr(graph, start_vtx, end_vtx);
    }
    public static native void cvClearGraph(CvGraph graph);
    public static native int  cvGraphVtxDegree(CvGraph graph, int vtx_idx);
    public static native int  cvGraphVtxDegreeByPtr(CvGraph graph, CvGraphVtx vtx);

    public static CvGraphVtx cvGetGraphVtx(CvGraph graph, int idx) { return new CvGraphVtx(cvGetSetElem(graph, idx)); }
    public static int cvGraphVtxIdx(CvGraph graph, CvGraphVtx vtx) { return vtx.flags() & CV_SET_ELEM_IDX_MASK; }
    public static int cvGraphEdgeIdx(CvGraph graph, CvGraphEdge edge) { return edge.flags() & CV_SET_ELEM_IDX_MASK; }
    public static int cvGraphGetVtxCount(CvGraph graph) { return graph.active_count(); }
    public static int cvGraphGetEdgeCount(CvGraph graph) { return graph.edges().active_count(); }

    public static final int
            CV_GRAPH_VERTEX       = 1,
            CV_GRAPH_TREE_EDGE    = 2,
            CV_GRAPH_BACK_EDGE    = 4,
            CV_GRAPH_FORWARD_EDGE = 8,
            CV_GRAPH_CROSS_EDGE   = 16,
            CV_GRAPH_ANY_EDGE     = 30,
            CV_GRAPH_NEW_TREE     = 32,
            CV_GRAPH_BACKTRACKING = 64,
            CV_GRAPH_OVER         = -1,

            CV_GRAPH_ALL_ITEMS    = -1,

            CV_GRAPH_ITEM_VISITED_FLAG = (1 << 30),

            CV_GRAPH_SEARCH_TREE_NODE_FLAG  = (1 << 29),
            CV_GRAPH_FORWARD_EDGE_FLAG      = (1 << 28);
    public static boolean CV_IS_GRAPH_EDGE_VISITED(CvGraphVtx vtx) {
        return (vtx.flags() & CV_GRAPH_ITEM_VISITED_FLAG) != 0;
    }
    public static boolean CV_IS_GRAPH_VERTEX_VISITED(CvGraphEdge edge) {
        return (edge.flags() & CV_GRAPH_ITEM_VISITED_FLAG) != 0;
    }

    public static class CvGraphScanner extends Pointer {
        static { load(); }
        public CvGraphScanner() { allocate(); zero(); }
        public CvGraphScanner(int size) { allocateArray(size); zero(); }
        public CvGraphScanner(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGraphScanner position(int position) {
            return (CvGraphScanner)super.position(position);
        }

        public static CvGraphScanner create(CvGraph graph,
                CvGraphVtx vtx/*=null*/, int mask/*=CV_GRAPH_ALL_ITEMS*/) {
            CvGraphScanner g = cvCreateGraphScanner(graph, vtx, mask);
            if (g != null) {
                g.deallocator(new ReleaseDeallocator(g));
            }
            return g;
        }
        public void release() {
            deallocate();
        }
        protected static class ReleaseDeallocator extends CvGraphScanner implements Deallocator {
            ReleaseDeallocator(CvGraphScanner p) { super(p); }
            @Override public void deallocate() { cvReleaseGraphScanner(this); }
        }

        public native CvGraphVtx vtx();   public native CvGraphScanner vtx(CvGraphVtx vtx);
        public native CvGraphVtx dst();   public native CvGraphScanner dst(CvGraphVtx dst);
        public native CvGraphEdge edge(); public native CvGraphScanner edge(CvGraphEdge edge);

        public native CvGraph graph();    public native CvGraphScanner graph(CvGraph graph);
        public native CvSeq stack();      public native CvGraphScanner stack(CvSeq stack);
        public native int index();        public native CvGraphScanner index(int index);
        public native int mask();         public native CvGraphScanner mask(int mask);
    }

    public static native CvGraphScanner cvCreateGraphScanner(CvGraph graph, CvGraphVtx vtx/*=null*/,
            int mask/*=CV_GRAPH_ALL_ITEMS*/);
    public static native void cvReleaseGraphScanner(@ByPtrPtr CvGraphScanner scanner);
    public static native int cvNextGraphItem(CvGraphScanner scanner);
    public static native CvGraph cvCloneGraph(CvGraph graph, CvMemStorage storage);


    public static CvScalar CV_RGB(double r, double g, double b) {
        return cvScalar(b, g, r, 0);
    }
    public static final int
            CV_FILLED = -1,
            CV_AA     = 16;
    public static native void cvLine(CvArr img, @ByVal CvPoint pt1, @ByVal CvPoint pt2,
            @ByVal CvScalar color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvRectangle(CvArr img, @ByVal CvPoint pt1, @ByVal CvPoint pt2,
            @ByVal CvScalar color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvRectangleR(CvArr img, @ByVal CvRect r, @ByVal CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvCircle(CvArr img, @ByVal CvPoint center, int radius,
            @ByVal CvScalar color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvEllipse(CvArr img, @ByVal CvPoint center, @ByVal CvSize axes, double angle,
            double start_angle, double end_angle, @ByVal CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static void cvEllipseBox(CvArr img, @ByVal CvBox2D box, @ByVal CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        CvSize2D32f size = box.size();
        CvSize axes = cvSize((int)Math.round(size.width()*0.5), (int)Math.round(size.height()*0.5));
        cvEllipse(img, cvPointFrom32f(box.center()), axes, box.angle(),
                   0, 360, color, thickness, line_type, shift);
    }
    public static native void cvFillConvexPoly(CvArr img, CvPoint pts, int npts,
            @ByVal CvScalar color, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvFillConvexPoly(CvArr img, @Cast("CvPoint*") int[] pts, int npts,
            @ByVal CvScalar color, int line_type/*=8*/, int shift/*=0*/);
    public static void cvFillPoly(CvArr img, CvPoint[] pts, int[] npts,
            int contours, @ByVal CvScalar color, int line_type/*=8*/, int shift/*=0*/) {
        cvFillPoly(img, new PointerPointer(pts), npts, contours, color, line_type, shift);
    }
    public static native void cvFillPoly(CvArr img, @Cast("CvPoint**") PointerPointer pts, int[] npts,
            int contours, @ByVal CvScalar color, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvFillPoly(CvArr img, @ByPtrPtr CvPoint pts, int[] npts,
            int contours, @ByVal CvScalar color, int line_type/*=8*/, int shift/*=0*/);
    public static void cvPolyLine(CvArr img, CvPoint[] pts,
            int[] npts, int contours, int is_closed, @ByVal CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, new PointerPointer(pts), npts, contours, is_closed, color, thickness, line_type, shift);
    }
    public static native void cvPolyLine(CvArr img, @Cast("CvPoint**") PointerPointer pts,
            int[] npts, int contours, int is_closed, @ByVal CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);
    public static native void cvPolyLine(CvArr img, @ByPtrPtr CvPoint pts,
            int[] npts, int contours, int is_closed, @ByVal CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/);

    public static void cvDrawRect(CvArr img, CvPoint pt1, CvPoint pt2,
            CvScalar color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvRectangle(img, pt1, pt2, color, thickness, line_type, shift);
    }
    public static void cvDrawLine(CvArr img, CvPoint pt1, CvPoint pt2,
            CvScalar color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvLine(img, pt1, pt2, color, thickness, line_type, shift);
    }
    public static void cvDrawCircle(CvArr img, CvPoint center, int radius,
            CvScalar color, int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvCircle(img, center, radius, color, thickness, line_type, shift);
    }
    public static void cvDrawEllipse(CvArr img, CvPoint center, CvSize axes, double angle,
            double start_angle, double end_angle, CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvEllipse(img, center, axes, angle, start_angle, end_angle, color,
                thickness, line_type, shift);
    }
    public static void cvDrawPolyLine(CvArr img, CvPoint[] pts,
            int[] npts, int contours, int is_closed, CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, pts, npts, contours, is_closed, color, thickness, line_type, shift);
    }
    public static void cvDrawPolyLine(CvArr img, @Cast("CvPoint**") PointerPointer pts,
            int[] npts, int contours, int is_closed, CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, pts, npts, contours, is_closed, color, thickness, line_type, shift);
    }
    public static void cvDrawPolyLine(CvArr img, @ByPtrPtr CvPoint pts,
            int[] npts, int contours, int is_closed, CvScalar color,
            int thickness/*=1*/, int line_type/*=8*/, int shift/*=0*/) {
        cvPolyLine(img, pts, npts, contours, is_closed, color, thickness, line_type, shift);
    }

    public static native int cvClipLine(@ByVal CvSize img_size, CvPoint pt1, CvPoint pt2);
    public static native int cvInitLineIterator(CvArr image, @ByVal CvPoint pt1, @ByVal CvPoint pt2,
            CvLineIterator line_iterator, int connectivity/*=8*/, int left_to_right/*=0*/);
    public static native void CV_NEXT_LINE_POINT(@ByVal CvLineIterator line_iterator);


    public static final int
            CV_FONT_HERSHEY_SIMPLEX        = 0,
            CV_FONT_HERSHEY_PLAIN          = 1,
            CV_FONT_HERSHEY_DUPLEX         = 2,
            CV_FONT_HERSHEY_COMPLEX        = 3 ,
            CV_FONT_HERSHEY_TRIPLEX        = 4,
            CV_FONT_HERSHEY_COMPLEX_SMALL  = 5,
            CV_FONT_HERSHEY_SCRIPT_SIMPLEX = 6,
            CV_FONT_HERSHEY_SCRIPT_COMPLEX = 7,

            CV_FONT_ITALIC                 = 16,  

            CV_FONT_VECTOR0   = CV_FONT_HERSHEY_SIMPLEX;
    public static class CvFont extends Pointer {
        static { load(); }
        public CvFont() { allocate(); }
        public CvFont(int size) { allocateArray(size); }
        public CvFont(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFont position(int position) {
            return (CvFont)super.position(position);
        }

        public CvFont(int font_face, double hscale, double vscale,
            double shear, int thickness, int line_type) {
            allocate();
            cvInitFont(this, font_face, hscale, vscale, shear, thickness, line_type);
        }
        public CvFont(int font_face, double scale, int thickness) {
            allocate();
            cvInitFont(this, font_face, scale, scale, 0, thickness, CV_AA);
        }

        @Cast("const char*")
        public native BytePointer nameFont();       public native CvFont nameFont(BytePointer nameFont);
        public native @ByRef CvScalar color();      public native CvFont color(CvScalar color);
        public native int font_face();              public native CvFont font_face(int font_face);
        public native @Const IntPointer ascii();    public native CvFont ascii(IntPointer ascii);
        public native @Const IntPointer greek();    public native CvFont greek(IntPointer greek);
        public native @Const IntPointer cyrillic(); public native CvFont cyrillic(IntPointer cyrillic);
        public native float hscale();               public native CvFont hscale(float hscale);
        public native float vscale();               public native CvFont vscale(float vscale);
        public native float shear();                public native CvFont shear(float shear);
        public native int thickness();              public native CvFont thickness(int thickness);
        public native float dx();                   public native CvFont dx(float dx);
        public native int line_type();              public native CvFont line_type(int line_type);
    }
    public static native void cvInitFont(CvFont font, int font_face, double hscale, double vscale,
            double shear/*=0*/, int thickness/*=1*/, int line_type/*=8*/);
    public static CvFont cvFont(double scale, int thickness/*=1*/) {
        CvFont font = new CvFont();
        cvInitFont(font, CV_FONT_HERSHEY_PLAIN, scale, scale, 0, thickness, CV_AA);
        return font;
    }
    public static native void cvPutText(CvArr img, String text, @ByVal CvPoint org,
            CvFont font, @ByVal CvScalar color);
    public static native void cvGetTextSize(String text_string, CvFont font,
            CvSize text_size, int[] baseline);

    public static native @ByVal CvScalar cvColorToScalar(double packed_color, int arrtype);

    public static native int cvEllipse2Poly(@ByVal CvPoint center, @ByVal CvSize axes,
            int angle, int arc_start, int arc_end, CvPoint pts, int delta);

    public static native void cvDrawContours(CvArr img, CvSeq contour, @ByVal CvScalar external_color,
            @ByVal CvScalar hole_color, int max_level, int thickness/*=1*/,
            int line_type/*=8*/, @ByVal CvPoint offset/*=cvPoint(0,0)*/);
    public static void cvDrawContours(CvArr img, CvSeq contour, @ByVal CvScalar external_color,
            @ByVal CvScalar hole_color, int max_level, int thickness/*=1*/, int line_type/*=8*/) {
        cvDrawContours(img, contour, external_color, hole_color, max_level, thickness, line_type, CvPoint.ZERO);
    }
    public static native void cvLUT(CvArr src, CvArr dst, CvArr lut);


    public static class CvTreeNodeIterator extends Pointer {
        static { load(); }
        public CvTreeNodeIterator() { allocate(); }
        public CvTreeNodeIterator(int size) { allocateArray(size); }
        public CvTreeNodeIterator(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvTreeNodeIterator position(int position) {
            return (CvTreeNodeIterator)super.position(position);
        }

        public native @Const Pointer node();  public native CvTreeNodeIterator node(Pointer node);
        public native int level();            public native CvTreeNodeIterator level(int level);
        public native int max_level();        public native CvTreeNodeIterator max_level(int max_level);
    }
    public static native void cvInitTreeNodeIterator(CvTreeNodeIterator tree_iterator, Pointer first, int max_level);
    public static native Pointer cvNextTreeNode(CvTreeNodeIterator tree_iterator);
    public static native Pointer cvPrevTreeNode(CvTreeNodeIterator tree_iterator);
    public static native void cvInsertNodeIntoTree(Pointer node, Pointer parent, Pointer frame);
    public static native void cvRemoveNodeFromTree(Pointer node, Pointer frame);
    public static native CvSeq cvTreeToNodeSeq(Pointer first, int header_size, CvMemStorage storage);

    public static final int CV_KMEANS_USE_INITIAL_LABELS = 1;
    public static native int cvKMeans2(CvArr samples, int cluster_count, CvArr labels,
            @ByVal CvTermCriteria termcrit, int attempts/*=1*/, CvRNG rng/*=null*/,
            int flags/*=0*/, CvArr _centers/*=null*/, double[] compactness/*=null*/);


    public static native int cvRegisterModule(CvModuleInfo module_info);
    public static native int cvUseOptimized(int on_off);
    public static native void cvGetModuleInfo(String module_name,
            @Cast("const char**") @ByPtrPtr BytePointer version,
            @Cast("const char**") @ByPtrPtr BytePointer loaded_addon_plugins);

    public static class CvAllocFunc extends FunctionPointer {
        static { load(); }
        public    CvAllocFunc(Pointer p) { super(p); }
        protected CvAllocFunc() { allocate(); }
        private native void allocate();
        public native Pointer call(@Cast("size_t") long size, Pointer userdata);
    }
    public static class CvFreeFunc extends FunctionPointer {
        static { load(); }
        public    CvFreeFunc(Pointer p) { super(p); }
        protected CvFreeFunc() { allocate(); }
        private native void allocate();
        public native int call(Pointer pptr, Pointer userdata);
    }
    public static native void cvSetMemoryManager(CvAllocFunc alloc_func/*=null*/,
            CvFreeFunc free_func/*=null*/, Pointer userdata/*=null*/);


    @Convention("CV_STDCALL")
    public static class Cv_iplCreateImageHeader extends FunctionPointer {
        static { load(); }
        public    Cv_iplCreateImageHeader(Pointer p) { super(p); }
        protected Cv_iplCreateImageHeader() { allocate(); }
        private native void allocate();
        public native IplImage call(int p0, int p1, int p2, @Cast("char*") BytePointer p3,
                @Cast("char*") BytePointer p4, int p5, int p6, int p7, int p8, int p9,
                IplROI p10, IplImage p11, Pointer p12, IplTileInfo p13);
    }
    @Convention("CV_STDCALL")
    public static class Cv_iplAllocateImageData extends FunctionPointer {
        static { load(); }
        public    Cv_iplAllocateImageData(Pointer p) { super(p); }
        protected Cv_iplAllocateImageData() { allocate(); }
        private native void allocate();
        public native void call(IplImage p0, int p1, int p2);
    }
    @Convention("CV_STDCALL")
    public static class Cv_iplDeallocate extends FunctionPointer {
        static { load(); }
        public    Cv_iplDeallocate(Pointer p) { super(p); }
        protected Cv_iplDeallocate() { allocate(); }
        private native void allocate();
        public native void call(IplImage p0, int p1);
    }
    @Convention("CV_STDCALL")
    public static class Cv_iplCreateROI extends FunctionPointer {
        static { load(); }
        public    Cv_iplCreateROI(Pointer p) { super(p); }
        protected Cv_iplCreateROI() { allocate(); }
        private native void allocate();
        public native IplROI call(int p0, int p1, int p2, int p3, int p4);
    }
    @Convention("CV_STDCALL")
    public static class Cv_iplCloneImage extends FunctionPointer {
        static { load(); }
        public    Cv_iplCloneImage(Pointer p) { super(p); }
        protected Cv_iplCloneImage() { allocate(); }
        private native void allocate();
        public native IplImage call(@Const IplImage p0);
    }
    public static native void cvSetIPLAllocators(
            Cv_iplCreateImageHeader create_header,
            Cv_iplAllocateImageData allocate_data,
            Cv_iplDeallocate deallocate,
            Cv_iplCreateROI create_roi,
            Cv_iplCloneImage clone_image);


    public static native CvFileStorage cvOpenFileStorage(String filename, CvMemStorage memstorage, int flags, String encoding/*=null*/);
    public static native void cvReleaseFileStorage(@ByPtrPtr CvFileStorage fs);
    public static native String cvAttrValue(CvAttrList attr, String attr_name);

    public static void cvStartWriteStruct(CvFileStorage fs, String name, int struct_flags, String type_name/*=null*/) {
        cvStartWriteStruct(fs, name, struct_flags, type_name, CvAttrList.EMPTY);
    }
    public static native void cvStartWriteStruct(CvFileStorage fs, String name, int struct_flags,
            String type_name/*=null*/, @ByVal CvAttrList attributes/*=cvArrtList()*/);
    public static native void cvEndWriteStruct(CvFileStorage fs);
    public static native void cvWriteInt(CvFileStorage fs, String name, int value);
    public static native void cvWriteReal(CvFileStorage fs, String name, double value);
    public static native void cvWriteString(CvFileStorage fs, String name, String str, int quote/*=0*/);
    public static native void cvWriteComment(CvFileStorage fs, String comment, int eol_comment);
    public static void cvWrite(CvFileStorage fs, String name, Pointer ptr) {
        cvWrite(fs, name, ptr, CvAttrList.EMPTY);
    }
    public static native void cvWrite(CvFileStorage fs, String name, Pointer ptr, @ByVal CvAttrList attributes/*=cvArrtList()*/);
    public static native void cvStartNextStream(CvFileStorage fs);
    public static native void cvWriteRawData(CvFileStorage fs, Pointer src, int len, String dt);
    public static native CvStringHashNode cvGetHashedKey(CvFileStorage fs, String name,
            int len/*=-1*/, int create_missing/*=0*/);
    public static native CvFileNode cvGetRootFileNode(CvFileStorage fs, int stream_index/*=0*/);
    public static native CvFileNode cvGetFileNode(CvFileStorage fs, CvFileNode map,
            CvStringHashNode key, int create_missing/*=0*/);
    public static native CvFileNode cvGetFileNodeByName(CvFileStorage fs, CvFileNode map, String name);
    public static int cvReadInt(CvFileNode node) {
        return cvReadInt(node, 0);
    }
    public static int cvReadInt(CvFileNode node, int default_value) {
        return node == null ? default_value :
            CV_NODE_IS_INT(node.tag()) ? node.data_i() :
            CV_NODE_IS_REAL(node.tag()) ? (int)Math.round(node.data_f()) : 0x7fffffff;
    }
    public static int cvReadIntByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadIntByName(fs, map, name, 0);
    }
    public static int cvReadIntByName(CvFileStorage fs, CvFileNode map, String name, int default_value) {
        return cvReadInt(cvGetFileNodeByName(fs, map, name), default_value);
    }
    public static double cvReadReal(CvFileNode node) {
        return cvReadReal(node, 0);
    }
    public static double cvReadReal(CvFileNode node, double default_value) {
        return node == null ? default_value :
            CV_NODE_IS_INT(node.tag()) ? (double)node.data_i() :
            CV_NODE_IS_REAL(node.tag()) ? node.data_f() : 1e300;
    }
    public static double cvReadRealByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadRealByName(fs, map, name, 0);
    }
    public static double cvReadRealByName(CvFileStorage fs, CvFileNode map, String name, double default_value) {
        return cvReadReal(cvGetFileNodeByName(fs, map, name), default_value);
    }
    public static String cvReadString(CvFileNode node) {
        return cvReadString(node, null);
    }
    public static String cvReadString(CvFileNode node, String default_value) {
        if (node == null) {
            return default_value;
        } else if (CV_NODE_IS_STRING(node.tag())) {
            CvString str = node.data_str();
            BytePointer pointer = str.ptr();
            byte[] bytes = new byte[str.len()];
            pointer.get(bytes);
            return new String(bytes);
        } else {
            return null;
        }
    }
    public static String cvReadStringByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadStringByName(fs, map, name, null);
    }
    public static String cvReadStringByName(CvFileStorage fs, CvFileNode map, String name, String default_value) {
        return cvReadString(cvGetFileNodeByName(fs, map, name), default_value);
    }
    public static Pointer cvRead(CvFileStorage fs, CvFileNode node) {
        return cvRead(fs, node, CvAttrList.EMPTY);
    }
    public static native Pointer cvRead(CvFileStorage fs, CvFileNode node, CvAttrList attributes/*=null*/);
    public static Pointer cvReadByName(CvFileStorage fs, CvFileNode map, String name) {
        return cvReadByName(fs, map, name, CvAttrList.EMPTY);
    }
    public static Pointer cvReadByName(CvFileStorage fs, CvFileNode map, String name, CvAttrList attributes) {
        CvFileNode n = cvGetFileNodeByName(fs, map, name);
        return cvRead(fs, n, attributes);
    }
    public static native void cvStartReadRawData(CvFileStorage fs, CvFileNode src, CvSeqReader reader);
    public static native void cvReadRawDataSlice(CvFileStorage fs, CvSeqReader reader,
            int count, Pointer dst, String dt);
    public static native void cvReadRawData(CvFileStorage fs, CvFileNode src, Pointer dst, String dt);
    public static native void cvWriteFileNode(CvFileStorage fs, String new_node_name, CvFileNode node, int embed);
    public static native String cvGetFileNodeName(CvFileNode node);


    public static native void cvRegisterType(CvTypeInfo info);
    public static native void cvUnregisterType(String type_name);
    public static native CvTypeInfo cvFirstType();
    public static native CvTypeInfo cvFindType(String type_name);
    public static native CvTypeInfo cvTypeOf(Pointer struct_ptr);

    public static native void cvRelease(PointerPointer struct_ptr);
    public static native Pointer cvClone(Pointer struct_ptr);

    public static void cvSave(String filename, Pointer struct_ptr) {
        cvSave(filename, struct_ptr, null, null, CvAttrList.EMPTY);
    }
    public static native void cvSave(String filename, Pointer struct_ptr, String name/*=null*/,
            String comment/*=null*/, @ByVal CvAttrList attributes/*=cvAttrList()*/);
    public static Pointer cvLoad(String filename) {
        return cvLoad(filename, null, null, null);
    }
    public static native Pointer cvLoad(String filename, CvMemStorage memstorage/*=null*/,
            String name/*=null*/, @Cast("const char**") @ByPtrPtr BytePointer real_name/*=null*/);


    public static native long cvGetTickCount();
    public static native double cvGetTickFrequency();

    public static final int
            CV_CPU_NONE   =  0,
            CV_CPU_MMX    =  1,
            CV_CPU_SSE    =  2,
            CV_CPU_SSE2   =  3,
            CV_CPU_SSE3   =  4,
            CV_CPU_SSSE3  =  5,
            CV_CPU_SSE4_1 =  6,
            CV_CPU_SSE4_2 =  7,
            CV_CPU_POPCNT =  8,
            CV_CPU_AVX    = 10,
            CV_HARDWARE_MAX_FEATURE = 255;

    public static native int cvCheckHardwareSupport(int feature);

    public static native int cvGetNumThreads();
    public static native void cvSetNumThreads(int threads/*=0*/);
    public static native int cvGetThreadNum();


    public static native int cvGetErrStatus();
    public static native void cvSetErrStatus(int status);
    public static final int
            CV_ErrModeLeaf    = 0,
            CV_ErrModeParent  = 1,
            CV_ErrModeSilent  = 2;
    public static native int cvGetErrMode();
    public static native int cvSetErrMode(int mode);
    public static native void cvError(int status, String func_name, String err_msg, String file_name, int line);
    public static native String cvErrorStr(int status);
    public static native int cvGetErrInfo(
            @Cast("const char**") @ByPtrPtr BytePointer errcode_desc,
            @Cast("const char**") @ByPtrPtr BytePointer description,
            @Cast("const char**") @ByPtrPtr BytePointer filename, int[] line);
    public static native int cvErrorFromIppStatus(int ipp_status);

    public static class CvErrorCallback extends FunctionPointer {
        static { load(); }
        public    CvErrorCallback(Pointer p) { super(p); }
        protected CvErrorCallback() { allocate(); }
        private native void allocate();
        public native int call(int status, String func_name,
                String err_msg, String file_name, int line, Pointer userdata);
    }
    public static native CvErrorCallback cvRedirectError(CvErrorCallback error_handler,
            Pointer userdata, @Cast("void**") @ByPtrPtr Pointer prev_userdata);

    public static native int cvNulDevReport(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata);
    public static native int cvStdErrReport(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata);
    public static native int cvGuiBoxReport(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata);


    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Const @Adapter("ArrayAdapter") public @interface InputArray { }

    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Adapter("ArrayAdapter") public @interface OutputArray { }

    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Const @Adapter("MatAdapter") public @interface InputMat { }

    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Adapter("MatAdapter") public @interface OutputMat { }

    @Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Cast({"cv::Ptr", "&"}) @Adapter("PtrAdapter") public @interface Ptr { 
        String value() default ""; /* template type */
    }

    @Name("std::vector<std::string>")
    public static class StringVector extends Pointer {
        static { load(); }
        public StringVector(String ... array) { this(array.length); put(array); }
        public StringVector()       { allocate();  }
        public StringVector(long n) { allocate(n); }
        public StringVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);

        @Index @ByRef public native String get(@Cast("size_t") long i);
        public native StringVector put(@Cast("size_t") long i, String value);

        public StringVector put(String ... array) {
            if (size() < array.length) { resize(array.length); }
            for (int i = 0; i < array.length; i++) {
                put(i, array[i]);
            }
            return this;
        }
    }

    @Name("std::vector<cv::Mat>")
    public static class MatVector extends Pointer {
        static { load(); }
        public MatVector(CvArr ... array) { this(array.length); put(array); }
        public MatVector()       { allocate();  }
        public MatVector(long n) { allocate(n); }
        public MatVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);

        @Index @ValueGetter public native @OutputMat CvMat getCvMat(@Cast("size_t") long i);
        @Index @ValueGetter public native @OutputMat CvMatND getCvMatND(@Cast("size_t") long i);
        @Index @ValueGetter public native @OutputMat IplImage getIplImage(@Cast("size_t") long i);
        @Index @ValueSetter public native MatVector put(@Cast("size_t") long i, @InputMat CvArr value);

        public MatVector put(CvArr ... array) {
            if (size() < array.length) { resize(array.length); }
            for (int i = 0; i < array.length; i++) {
                put(i, array[i]);
            }
            return this;
        }
    }

    @Name("std::vector<std::vector<char> >")
    public static class ByteVectorVector extends Pointer {
        static { load(); }
        public ByteVectorVector()       { allocate();  }
        public ByteVectorVector(long n) { allocate(n); }
        public ByteVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index long size(@Cast("size_t") long i);
        public native @Index void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index public native byte get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native ByteVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, byte value);
    }

    @Name("std::vector<std::vector<int> >")
    public static class IntVectorVector extends Pointer {
        static { load(); }
        public IntVectorVector()       { allocate();  }
        public IntVectorVector(long n) { allocate(n); }
        public IntVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index long size(@Cast("size_t") long i);
        public native @Index void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index public native int get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native IntVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, int value);
    }

    @Name("std::vector<std::vector<cv::Point> >")
    public static class PointVectorVector extends Pointer {
        static { load(); }
        public PointVectorVector()       { allocate();  }
        public PointVectorVector(long n) { allocate(n); }
        public PointVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index long size(@Cast("size_t") long i);
        public native @Index void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ByVal public native CvPoint get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native PointVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, CvPoint value);
    }

    @Name("std::vector<std::vector<cv::Point2f> >")
    public static class Point2fVectorVector extends Pointer {
        static { load(); }
        public Point2fVectorVector()       { allocate();  }
        public Point2fVectorVector(long n) { allocate(n); }
        public Point2fVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index long size(@Cast("size_t") long i);
        public native @Index void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ByVal public native CvPoint2D32f get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native Point2fVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, CvPoint2D32f value);
    }

    @Name("std::vector<std::vector<cv::Point2d> >")
    public static class Point2dVectorVector extends Pointer {
        static { load(); }
        public Point2dVectorVector()       { allocate();  }
        public Point2dVectorVector(long n) { allocate(n); }
        public Point2dVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index long size(@Cast("size_t") long i);
        public native @Index void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ByVal public native CvPoint2D32f get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native Point2dVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, CvPoint2D32f value);
    }

    @Name("std::vector<std::vector<cv::Rect> >")
    public static class RectVectorVector extends Pointer {
        static { load(); }
        public RectVectorVector()       { allocate();  }
        public RectVectorVector(long n) { allocate(n); }
        public RectVectorVector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@Cast("size_t") long n);

        public native long size();
        public native void resize(@Cast("size_t") long n);
        public native @Index long size(@Cast("size_t") long i);
        public native @Index void resize(@Cast("size_t") long i, @Cast("size_t") long n);

        @Index @ByVal public native CvRect get(@Cast("size_t") long i, @Cast("size_t") long j);
        public native RectVectorVector put(@Cast("size_t") long i, @Cast("size_t") long j, CvRect value);
    }

    public static final int NORM_INF=1, NORM_L1=2, NORM_L2=4, NORM_L2SQR=5, NORM_HAMMING=6, NORM_HAMMING2=7, NORM_TYPE_MASK=7, NORM_RELATIVE=8, NORM_MINMAX=32;

    @Namespace("cv") public static native @ByRef String getBuildInformation();

    @Namespace("cv") public static native void batchDistance(@InputArray CvArr src1,
            @InputArray CvArr src2, @OutputArray CvMat dist,
            int dtype, @OutputArray CvMat nidx, int normType/*=NORM_L2*/, int K/*=0*/,
            @InputArray CvArr mask/*=null*/, int update/*=0*/, @Cast("bool") boolean crosscheck/*=false*/);

    @NoOffset @Namespace("cv") public static class KDTree extends Pointer {
        static { load(); }
        @NoOffset public static class Node extends Pointer {
            static { load(); }
            public Node() { allocate(); }
            public Node(int size) { allocateArray(size); }
            public Node(Pointer p) { super(p); }
            public Node(int _idx, int _left, int _right, float _boundary) {
                allocate(_idx, _left, _right, _boundary);
            }
            private native void allocate();
            private native void allocate(int _idx, int _left, int _right, float _boundary);
            private native void allocateArray(int size);

            @Override public Node position(int position) {
                return (Node)super.position(position);
            }

            public native int idx();        public native Node idx(int idx);
            public native int left();       public native Node left(int left);
            public native int right();      public native Node right(int right);
            public native float boundary(); public native Node boundary(float boundary);
        }
        public KDTree() { allocate(); }
        public KDTree(Pointer p) { super(p); }
        public KDTree(CvMat _points, boolean copyAndReorderPoints/*=false*/) {
            allocate(_points, copyAndReorderPoints);
        }
        public KDTree(CvMat _points, CvMat _labels, boolean copyAndReorderPoints/*=false*/) {
            allocate(_points, _labels, copyAndReorderPoints);
        }
        private native void allocate();
        private native void allocate(@InputArray CvMat _points, @Cast("bool") boolean copyAndReorderPoints/*=false*/);
        private native void allocate(@InputArray CvMat _points, @InputArray CvMat _labels, @Cast("bool") boolean copyAndReorderPoints/*=false*/);

        public native void build(@InputArray CvMat _points, @Cast("bool") boolean copyAndReorderPoints/*=false*/);
        public native void build(@InputArray CvMat _points, @InputArray CvMat _labels, @Cast("bool") boolean copyAndReorderPoints/*=false*/);

        public native int findNearest(@InputArray CvMat vec, int K, int Emax,
                @OutputArray CvMat neighborsIdx, @OutputArray CvMat neighbors/*=null*/,
                @OutputArray CvMat dist/*=null*/, @OutputArray CvMat labels/*=null*/);
        public native int findNearest(@InputArray FloatPointer vec, int K, int Emax,
                @OutputArray IntPointer neighborsIdx, @OutputArray CvMat neighbors/*=null*/,
                @OutputArray FloatPointer dist/*=null*/, @OutputArray IntPointer labels/*=null*/);
        public native void findOrthoRange(@InputArray CvMat minBounds, @InputArray CvMat maxBounds,
                @OutputArray CvMat neighborsIdx, @OutputArray CvMat neighbors/*=null*/,
                @OutputArray CvMat labels/*=null*/);
        public native void findOrthoRange(@InputArray FloatPointer minBounds, @InputArray FloatPointer maxBounds,
                @OutputArray IntPointer neighborsIdx, @OutputArray CvMat neighbors/*=null*/,
                @OutputArray IntPointer labels/*=null*/);
        public native void getPoints(@InputArray CvMat idx, @OutputArray CvMat pts,
                @OutputArray CvMat labels/*=null*/);
        public native void getPoints(@InputArray FloatPointer idx, @OutputArray CvMat pts,
                @OutputArray IntPointer labels/*=null*/);
        public native @Const FloatPointer getPoint(int ptidx, int[] label/*=null*/);
        public native int dims();

        public native @Const @StdVector Node nodes();        public native KDTree nodes(Node nodes);
        public native @InputMat CvMat points();              public native KDTree points(CvMat points);
        public native @Const @StdVector IntPointer labels(); public native KDTree labels(IntPointer labels);
        public native int maxDepth();                        public native KDTree maxDepth(int maxDepth);
        public native int normType();                        public native KDTree normType(int normType);
    }


    @Namespace("cv") public static class Algorithm extends Pointer {
        static { load(); }
        public Algorithm() { allocate(); }
        public Algorithm(Pointer p) { super(p); }
        private native void allocate();

        public native @ByRef String name();

        public native int getInt(String name);
        public native double getDouble(String name);
        public native @Cast("bool") boolean getBool(String name);
        public native @ByRef String getString(String name);
        public native @OutputMat CvMat getMat(String name);
        public native @ByVal MatVector getMatVector(String name);
        public native @Const @Ptr Algorithm getAlgorithm(String name);

        public native void set(String name, int value);
        public native void set(String name, double value);
        public native void set(String name, @Cast("bool") boolean value);
        public native void set(String name, String value);
        public native void set(String name, CvMat value);
        public native void set(String name, @ByRef MatVector value);
        public native void set(String name, @Ptr Algorithm value);

        public native @ByRef String paramHelp(String name);
        public native int paramType(String name);
        public native void getParams(@ByRef StringVector names);

        public native void write(@Const @Adapter("FileStorageAdapter") CvFileStorage fs);
        public native void read(@Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);

        public static native void getList(@ByRef StringVector algorithms);
        public static native @Ptr Algorithm _create(String name);

        public static class Constructor extends FunctionPointer {
            static { load(); }
            public    Constructor(Pointer p) { super(p); }
            protected Constructor() { allocate(); }
            private native void allocate();
            public native Algorithm call();
        }
        @Namespace("cv::Algorithm") @Const public static class Getter extends FunctionPointer {
            static { load(); }
            public Getter(Pointer p) { super(p); }
            public native int call(Algorithm o);
        }
        @Namespace("cv::Algorithm") public static class Setter extends FunctionPointer {
            static { load(); }
            public Setter(Pointer p) { super(p); }
            public native void call(Algorithm o, int i);
        }

        public native AlgorithmInfo info();
    }

    @Namespace("cv") public static class AlgorithmInfo extends Pointer {
        static { load(); }
        public AlgorithmInfo(String name, Algorithm.Constructor create) { allocate(name, create); }
        public AlgorithmInfo(Pointer p) { super(p); }
        private native void allocate(String name, Algorithm.Constructor create);

        public native void get(Algorithm algo, String name, int argType, Pointer value);
        public native void addParam_(@ByRef Algorithm algo, String name, int argType,
                Pointer value, @Cast("bool") boolean readOnly,
                Algorithm.Getter getter, Algorithm.Setter setter, String help/*=""*/);
        public native @ByRef String paramHelp(String name);
        public native int paramType(String name);
        public native void getParams(@ByRef StringVector names);

        public native void write(Algorithm algo, @Const @Adapter("FileStorageAdapter") CvFileStorage fs);
        public native void read(Algorithm algo, @Const @Adapter(value="FileNodeAdapter", argc=2) CvFileStorage fs, CvFileNode fn);
        public native @ByRef String name();

//        protected native AlgorithmInfoData data();
//        protected native void set(Algorithm algo, String name, int argType,
//                Pointer value, @Cast("boolean") force/*=false*/);
    }

    @NoOffset @Namespace("cv") public static class Param extends Pointer {
        static { load(); }
        public Param() { allocate(); }
        public Param(int _type, @Cast("bool") boolean _readonly, int _offset,
                Algorithm.Getter _getter/*=null*/, Algorithm.Setter _setter/*=null*/, String _help/*=""*/) {
            allocate(_type, _readonly, _offset, _getter, _setter, _help);
        }
        public Param(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _type, @Cast("bool") boolean _readonly, int _offset,
                Algorithm.Getter _getter/*=null*/, Algorithm.Setter _setter/*=null*/, String _help/*=""*/);

        public static final int INT=0, BOOLEAN=1, REAL=2, STRING=3, MAT=4, MAT_VECTOR=5, ALGORITHM=6, FLOAT=7, UNSIGNED_INT=8, UINT64=9, SHORT=10, UCHAR=11;

        public native int type();                       public native Param type(int type);
        public native int offset();                     public native Param offset(int offset);
        public native @Cast("bool") boolean readonly(); public native Param readonly(boolean readonly);
        public native Algorithm.Getter getter();        public native Param getter(Algorithm.Getter getter);
        public native Algorithm.Setter setter();        public native Param setter(Algorithm.Setter setter);
        public native @ByRef String help();             public native Param help(String help);
    }


    public static class Predicate extends FunctionPointer {
        static { load(); }
        public    Predicate(Pointer p) { super(p); }
        protected Predicate() { allocate(); }
        private native void allocate();
        public native boolean call(Pointer a, Pointer b);
    }
    public static native @Name("cv::partition<void*>") int partition(@StdVector PointerPointer _vec,
            @StdVector IntPointer labels, @ByRef Predicate predicate);
}
