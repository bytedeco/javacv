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
 * This file was derived from avcodec.h and other libavcodec include files from
 * FFmpeg 0.6.1, which are covered by the following copyright notice:
 *
 * copyright (c) 2001 Fabrice Bellard
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
 *
 */

package com.googlecode.javacv.cpp;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.LongPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.ShortPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
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
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude={
        "<libavcodec/avcodec.h>", "<libavcodec/opt.h>", "<libavcodec/avfft.h>"},
        includepath=genericIncludepath, linkpath=genericLinkpath, link={"avcodec", "avutil"}),
    @Platform(value={"linux", "freebsd", "solaris", "sunos"}, cinclude={
        "<libavcodec/vaapi.h>", "<libavcodec/vdpau.h>", "<libavcodec/xvmc.h>",
        "<libavcodec/avcodec.h>", "<libavcodec/opt.h>", "<libavcodec/avfft.h>"}),
    @Platform(value="windows", includepath=windowsIncludepath, cinclude={
        "<DShow.h>", "<d3d9.h>", "<vmr9.h>", "<evr9.h>", "<libavcodec/dxva2.h>",
        "<libavcodec/avcodec.h>", "<libavcodec/opt.h>", "<libavcodec/avfft.h>"},
        linkpath=windowsLinkpath, preloadpath=windowsPreloadpath, preload="avcodec-52"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avcodec {
    static { load(avutil.class); load(); }

    public static final int LIBAVCODEC_VERSION_MAJOR = 52;
    public static final int LIBAVCODEC_VERSION_MINOR = 72;
    public static final int LIBAVCODEC_VERSION_MICRO =  2;

    public static final int    LIBAVCODEC_VERSION_INT = AV_VERSION_INT(LIBAVCODEC_VERSION_MAJOR,
                                                                       LIBAVCODEC_VERSION_MINOR,
                                                                       LIBAVCODEC_VERSION_MICRO);
    public static final String LIBAVCODEC_VERSION     = AV_VERSION(LIBAVCODEC_VERSION_MAJOR,
                                                                   LIBAVCODEC_VERSION_MINOR,
                                                                   LIBAVCODEC_VERSION_MICRO);
    public static final int    LIBAVCODEC_BUILD       = LIBAVCODEC_VERSION_INT;

    public static final String LIBAVCODEC_IDENT       = "Lavc" + LIBAVCODEC_VERSION;

    public static final long AV_NOPTS_VALUE           = 0x8000000000000000L;
    public static final int AV_TIME_BASE              = 1000000;
    public static final AVRational AV_TIME_BASE_Q     =
            load() == null ? null : new AVRational().num(1).den(AV_TIME_BASE);


    //enum CodecID {
    public static final int
            CODEC_ID_NONE = 0,

            CODEC_ID_MPEG1VIDEO         = 1,
            CODEC_ID_MPEG2VIDEO         = 2,
            CODEC_ID_MPEG2VIDEO_XVMC    = 3,
            CODEC_ID_H261               = 4,
            CODEC_ID_H263               = 5,
            CODEC_ID_RV10               = 6,
            CODEC_ID_RV20               = 7,
            CODEC_ID_MJPEG              = 8,
            CODEC_ID_MJPEGB             = 9,
            CODEC_ID_LJPEG              = 10,
            CODEC_ID_SP5X               = 11,
            CODEC_ID_JPEGLS             = 12,
            CODEC_ID_MPEG4              = 13,
            CODEC_ID_RAWVIDEO           = 14,
            CODEC_ID_MSMPEG4V1          = 15,
            CODEC_ID_MSMPEG4V2          = 16,
            CODEC_ID_MSMPEG4V3          = 17,
            CODEC_ID_WMV1               = 18,
            CODEC_ID_WMV2               = 19,
            CODEC_ID_H263P              = 20,
            CODEC_ID_H263I              = 21,
            CODEC_ID_FLV1               = 22,
            CODEC_ID_SVQ1               = 23,
            CODEC_ID_SVQ3               = 24,
            CODEC_ID_DVVIDEO            = 25,
            CODEC_ID_HUFFYUV            = 26,
            CODEC_ID_CYUV               = 27,
            CODEC_ID_H264               = 28,
            CODEC_ID_INDEO3             = 29,
            CODEC_ID_VP3                = 30,
            CODEC_ID_THEORA             = 31,
            CODEC_ID_ASV1               = 32,
            CODEC_ID_ASV2               = 33,
            CODEC_ID_FFV1               = 34,
            CODEC_ID_4XM                = 35,
            CODEC_ID_VCR1               = 36,
            CODEC_ID_CLJR               = 37,
            CODEC_ID_MDEC               = 38,
            CODEC_ID_ROQ                = 39,
            CODEC_ID_INTERPLAY_VIDEO    = 40,
            CODEC_ID_XAN_WC3            = 41,
            CODEC_ID_XAN_WC4            = 42,
            CODEC_ID_RPZA               = 43,
            CODEC_ID_CINEPAK            = 44,
            CODEC_ID_WS_VQA             = 45,
            CODEC_ID_MSRLE              = 46,
            CODEC_ID_MSVIDEO1           = 47,
            CODEC_ID_IDCIN              = 48,
            CODEC_ID_8BPS               = 49,
            CODEC_ID_SMC                = 50,
            CODEC_ID_FLIC               = 51,
            CODEC_ID_TRUEMOTION1        = 52,
            CODEC_ID_VMDVIDEO           = 53,
            CODEC_ID_MSZH               = 54,
            CODEC_ID_ZLIB               = 55,
            CODEC_ID_QTRLE              = 56,
            CODEC_ID_SNOW               = 57,
            CODEC_ID_TSCC               = 58,
            CODEC_ID_ULTI               = 59,
            CODEC_ID_QDRAW              = 60,
            CODEC_ID_VIXL               = 61,
            CODEC_ID_QPEG               = 62,
//#if LIBAVCODEC_VERSION_MAJOR < 53
            CODEC_ID_XVID               = 63,
//#endif
            CODEC_ID_PNG                = 64,
            CODEC_ID_PPM                = 65,
            CODEC_ID_PBM                = 66,
            CODEC_ID_PGM                = 67,
            CODEC_ID_PGMYUV             = 68,
            CODEC_ID_PAM                = 69,
            CODEC_ID_FFVHUFF            = 70,
            CODEC_ID_RV30               = 71,
            CODEC_ID_RV40               = 72,
            CODEC_ID_VC1                = 73,
            CODEC_ID_WMV3               = 74,
            CODEC_ID_LOCO               = 75,
            CODEC_ID_WNV1               = 76,
            CODEC_ID_AASC               = 77,
            CODEC_ID_INDEO2             = 78,
            CODEC_ID_FRAPS              = 79,
            CODEC_ID_TRUEMOTION2        = 80,
            CODEC_ID_BMP                = 81,
            CODEC_ID_CSCD               = 82,
            CODEC_ID_MMVIDEO            = 83,
            CODEC_ID_ZMBV               = 84,
            CODEC_ID_AVS                = 85,
            CODEC_ID_SMACKVIDEO         = 86,
            CODEC_ID_NUV                = 87,
            CODEC_ID_KMVC               = 88,
            CODEC_ID_FLASHSV            = 89,
            CODEC_ID_CAVS               = 90,
            CODEC_ID_JPEG2000           = 91,
            CODEC_ID_VMNC               = 92,
            CODEC_ID_VP5                = 93,
            CODEC_ID_VP6                = 94,
            CODEC_ID_VP6F               = 95,
            CODEC_ID_TARGA              = 96,
            CODEC_ID_DSICINVIDEO        = 97,
            CODEC_ID_TIERTEXSEQVIDEO    = 98,
            CODEC_ID_TIFF               = 99,
            CODEC_ID_GIF                = 100,
            CODEC_ID_FFH264             = 101,
            CODEC_ID_DXA                = 102,
            CODEC_ID_DNXHD              = 103,
            CODEC_ID_THP                = 104,
            CODEC_ID_SGI                = 105,
            CODEC_ID_C93                = 106,
            CODEC_ID_BETHSOFTVID        = 107,
            CODEC_ID_PTX                = 108,
            CODEC_ID_TXD                = 109,
            CODEC_ID_VP6A               = 110,
            CODEC_ID_AMV                = 111,
            CODEC_ID_VB                 = 112,
            CODEC_ID_PCX                = 113,
            CODEC_ID_SUNRAST            = 114,
            CODEC_ID_INDEO4             = 115,
            CODEC_ID_INDEO5             = 116,
            CODEC_ID_MIMIC              = 117,
            CODEC_ID_RL2                = 118,
            CODEC_ID_8SVX_EXP           = 119,
            CODEC_ID_8SVX_FIB           = 120,
            CODEC_ID_ESCAPE124          = 121,
            CODEC_ID_DIRAC              = 122,
            CODEC_ID_BFI                = 123,
            CODEC_ID_CMV                = 124,
            CODEC_ID_MOTIONPIXELS       = 125,
            CODEC_ID_TGV                = 126,
            CODEC_ID_TGQ                = 127,
            CODEC_ID_TQI                = 128,
            CODEC_ID_AURA               = 129,
            CODEC_ID_AURA2              = 130,
            CODEC_ID_V210X              = 131,
            CODEC_ID_TMV                = 132,
            CODEC_ID_V210               = 133,
            CODEC_ID_DPX                = 134,
            CODEC_ID_MAD                = 135,
            CODEC_ID_FRWU               = 136,
            CODEC_ID_FLASHSV2           = 137,
            CODEC_ID_CDGRAPHICS         = 138,
            CODEC_ID_R210               = 139,
            CODEC_ID_ANM                = 140,
            CODEC_ID_BINKVIDEO          = 141,
            CODEC_ID_IFF_ILBM           = 142,
            CODEC_ID_IFF_BYTERUN1       = 143,
            CODEC_ID_KGV1               = 144,
            CODEC_ID_YOP                = 145,
            CODEC_ID_VP8                = 146,

            CODEC_ID_PCM_S16LE          = 0x10000,
            CODEC_ID_PCM_S16BE          = 0x10000 + 1,
            CODEC_ID_PCM_U16LE          = 0x10000 + 2,
            CODEC_ID_PCM_U16BE          = 0x10000 + 3,
            CODEC_ID_PCM_S8             = 0x10000 + 4,
            CODEC_ID_PCM_U8             = 0x10000 + 5,
            CODEC_ID_PCM_MULAW          = 0x10000 + 6,
            CODEC_ID_PCM_ALAW           = 0x10000 + 7,
            CODEC_ID_PCM_S32LE          = 0x10000 + 8,
            CODEC_ID_PCM_S32BE          = 0x10000 + 9,
            CODEC_ID_PCM_U32LE          = 0x10000 + 10,
            CODEC_ID_PCM_U32BE          = 0x10000 + 11,
            CODEC_ID_PCM_S24LE          = 0x10000 + 12,
            CODEC_ID_PCM_S24BE          = 0x10000 + 13,
            CODEC_ID_PCM_U24LE          = 0x10000 + 14,
            CODEC_ID_PCM_U24BE          = 0x10000 + 15,
            CODEC_ID_PCM_S24DAUD        = 0x10000 + 16,
            CODEC_ID_PCM_ZORK           = 0x10000 + 17,
            CODEC_ID_PCM_S16LE_PLANAR   = 0x10000 + 18,
            CODEC_ID_PCM_DVD            = 0x10000 + 19,
            CODEC_ID_PCM_F32BE          = 0x10000 + 20,
            CODEC_ID_PCM_F32LE          = 0x10000 + 21,
            CODEC_ID_PCM_F64BE          = 0x10000 + 22,
            CODEC_ID_PCM_F64LE          = 0x10000 + 23,
            CODEC_ID_PCM_BLURAY         = 0x10000 + 24,

            CODEC_ID_ADPCM_IMA_QT       = 0x11000,
            CODEC_ID_ADPCM_IMA_WAV      = 0x11000 + 1,
            CODEC_ID_ADPCM_IMA_DK3      = 0x11000 + 2,
            CODEC_ID_ADPCM_IMA_DK4      = 0x11000 + 3,
            CODEC_ID_ADPCM_IMA_WS       = 0x11000 + 4,
            CODEC_ID_ADPCM_IMA_SMJPEG   = 0x11000 + 5,
            CODEC_ID_ADPCM_MS           = 0x11000 + 6,
            CODEC_ID_ADPCM_4XM          = 0x11000 + 7,
            CODEC_ID_ADPCM_XA           = 0x11000 + 8,
            CODEC_ID_ADPCM_ADX          = 0x11000 + 9,
            CODEC_ID_ADPCM_EA           = 0x11000 + 10,
            CODEC_ID_ADPCM_G726         = 0x11000 + 11,
            CODEC_ID_ADPCM_CT           = 0x11000 + 12,
            CODEC_ID_ADPCM_SWF          = 0x11000 + 13,
            CODEC_ID_ADPCM_YAMAHA       = 0x11000 + 14,
            CODEC_ID_ADPCM_SBPRO_4      = 0x11000 + 15,
            CODEC_ID_ADPCM_SBPRO_3      = 0x11000 + 16,
            CODEC_ID_ADPCM_SBPRO_2      = 0x11000 + 17,
            CODEC_ID_ADPCM_THP          = 0x11000 + 18,
            CODEC_ID_ADPCM_IMA_AMV      = 0x11000 + 19,
            CODEC_ID_ADPCM_EA_R1        = 0x11000 + 20,
            CODEC_ID_ADPCM_EA_R3        = 0x11000 + 21,
            CODEC_ID_ADPCM_EA_R2        = 0x11000 + 22,
            CODEC_ID_ADPCM_IMA_EA_SEAD  = 0x11000 + 23,
            CODEC_ID_ADPCM_IMA_EA_EACS  = 0x11000 + 24,
            CODEC_ID_ADPCM_EA_XAS       = 0x11000 + 25,
            CODEC_ID_ADPCM_EA_MAXIS_XA  = 0x11000 + 26,
            CODEC_ID_ADPCM_IMA_ISS      = 0x11000 + 27,

            CODEC_ID_AMR_NB             = 0x12000,
            CODEC_ID_AMR_WB             = 0x12000 + 1,

            CODEC_ID_RA_144             = 0x13000,
            CODEC_ID_RA_288             = 0x13000 + 1,

            CODEC_ID_ROQ_DPCM           = 0x14000,
            CODEC_ID_INTERPLAY_DPCM     = 0x14000 + 1,
            CODEC_ID_XAN_DPCM           = 0x14000 + 2,
            CODEC_ID_SOL_DPCM           = 0x14000 + 3,

            CODEC_ID_MP2                = 0x15000,
            CODEC_ID_MP3                = 0x15000 + 1,
            CODEC_ID_AAC                = 0x15000 + 2,
            CODEC_ID_AC3                = 0x15000 + 3,
            CODEC_ID_DTS                = 0x15000 + 4,
            CODEC_ID_VORBIS             = 0x15000 + 5,
            CODEC_ID_DVAUDIO            = 0x15000 + 6,
            CODEC_ID_WMAV1              = 0x15000 + 7,
            CODEC_ID_WMAV2              = 0x15000 + 8,
            CODEC_ID_MACE3              = 0x15000 + 9,
            CODEC_ID_MACE6              = 0x15000 + 10,
            CODEC_ID_VMDAUDIO           = 0x15000 + 12,
            CODEC_ID_SONIC              = 0x15000 + 13,
            CODEC_ID_SONIC_LS           = 0x15000 + 14,
            CODEC_ID_FLAC               = 0x15000 + 15,
            CODEC_ID_MP3ADU             = 0x15000 + 16,
            CODEC_ID_MP3ON4             = 0x15000 + 17,
            CODEC_ID_SHORTEN            = 0x15000 + 18,
            CODEC_ID_ALAC               = 0x15000 + 19,
            CODEC_ID_WESTWOOD_SND1      = 0x15000 + 20,
            CODEC_ID_GSM                = 0x15000 + 21,
            CODEC_ID_QDM2               = 0x15000 + 22,
            CODEC_ID_COOK               = 0x15000 + 23,
            CODEC_ID_TRUESPEECH         = 0x15000 + 24,
            CODEC_ID_TTA                = 0x15000 + 25,
            CODEC_ID_SMACKAUDIO         = 0x15000 + 26,
            CODEC_ID_QCELP              = 0x15000 + 27,
            CODEC_ID_WAVPACK            = 0x15000 + 28,
            CODEC_ID_DSICINAUDIO        = 0x15000 + 29,
            CODEC_ID_IMC                = 0x15000 + 30,
            CODEC_ID_MUSEPACK7          = 0x15000 + 31,
            CODEC_ID_MLP                = 0x15000 + 32,
            CODEC_ID_GSM_MS             = 0x15000 + 33,
            CODEC_ID_ATRAC3             = 0x15000 + 34,
            CODEC_ID_VOXWARE            = 0x15000 + 35,
            CODEC_ID_APE                = 0x15000 + 36,
            CODEC_ID_NELLYMOSER         = 0x15000 + 37,
            CODEC_ID_MUSEPACK8          = 0x15000 + 38,
            CODEC_ID_SPEEX              = 0x15000 + 39,
            CODEC_ID_WMAVOICE           = 0x15000 + 40,
            CODEC_ID_WMAPRO             = 0x15000 + 41,
            CODEC_ID_WMALOSSLESS        = 0x15000 + 42,
            CODEC_ID_ATRAC3P            = 0x15000 + 43,
            CODEC_ID_EAC3               = 0x15000 + 44,
            CODEC_ID_SIPR               = 0x15000 + 45,
            CODEC_ID_MP1                = 0x15000 + 46,
            CODEC_ID_TWINVQ             = 0x15000 + 47,
            CODEC_ID_TRUEHD             = 0x15000 + 48,
            CODEC_ID_MP4ALS             = 0x15000 + 49,
            CODEC_ID_ATRAC1             = 0x15000 + 50,
            CODEC_ID_BINKAUDIO_RDFT     = 0x15000 + 51,
            CODEC_ID_BINKAUDIO_DCT      = 0x15000 + 52,

            CODEC_ID_DVD_SUBTITLE       = 0x17000,
            CODEC_ID_DVB_SUBTITLE       = 0x17000 + 1,
            CODEC_ID_TEXT               = 0x17000 + 2,
            CODEC_ID_XSUB               = 0x17000 + 3,
            CODEC_ID_SSA                = 0x17000 + 4,
            CODEC_ID_MOV_TEXT           = 0x17000 + 5,
            CODEC_ID_HDMV_PGS_SUBTITLE  = 0x17000 + 6,
            CODEC_ID_DVB_TELETEXT       = 0x17000 + 7,

            CODEC_ID_TTF                = 0x18000,

            CODEC_ID_PROBE              = 0x19000,

            CODEC_ID_MPEG2TS            = 0x20000;

//#if LIBAVCODEC_VERSION_MAJOR < 53
    //#define CodecType AVMediaType

    public static final int
            CODEC_TYPE_UNKNOWN    = AVMEDIA_TYPE_UNKNOWN,
            CODEC_TYPE_VIDEO      = AVMEDIA_TYPE_VIDEO,
            CODEC_TYPE_AUDIO      = AVMEDIA_TYPE_AUDIO,
            CODEC_TYPE_DATA       = AVMEDIA_TYPE_DATA,
            CODEC_TYPE_SUBTITLE   = AVMEDIA_TYPE_SUBTITLE,
            CODEC_TYPE_ATTACHMENT = AVMEDIA_TYPE_ATTACHMENT,
            CODEC_TYPE_NB         = AVMEDIA_TYPE_NB;
//#endif

    //enum SampleFormat {
    public static final int
            SAMPLE_FMT_NONE     = -1,
            SAMPLE_FMT_U8       = 0,
            SAMPLE_FMT_S16      = 1,
            SAMPLE_FMT_S32      = 2,
            SAMPLE_FMT_FLT      = 3,
            SAMPLE_FMT_DBL      = 4,
            SAMPLE_FMT_NB       = 5;

    public static final int
            CH_FRONT_LEFT            = 0x00000001,
            CH_FRONT_RIGHT           = 0x00000002,
            CH_FRONT_CENTER          = 0x00000004,
            CH_LOW_FREQUENCY         = 0x00000008,
            CH_BACK_LEFT             = 0x00000010,
            CH_BACK_RIGHT            = 0x00000020,
            CH_FRONT_LEFT_OF_CENTER  = 0x00000040,
            CH_FRONT_RIGHT_OF_CENTER = 0x00000080,
            CH_BACK_CENTER           = 0x00000100,
            CH_SIDE_LEFT             = 0x00000200,
            CH_SIDE_RIGHT            = 0x00000400,
            CH_TOP_CENTER            = 0x00000800,
            CH_TOP_FRONT_LEFT        = 0x00001000,
            CH_TOP_FRONT_CENTER      = 0x00002000,
            CH_TOP_FRONT_RIGHT       = 0x00004000,
            CH_TOP_BACK_LEFT         = 0x00008000,
            CH_TOP_BACK_CENTER       = 0x00010000,
            CH_TOP_BACK_RIGHT        = 0x00020000,
            CH_STEREO_LEFT           = 0x20000000,
            CH_STEREO_RIGHT          = 0x40000000;

    public static final long
            CH_LAYOUT_NATIVE         = 0x8000000000000000L;

    public static final int
            CH_LAYOUT_MONO           = (CH_FRONT_CENTER),
            CH_LAYOUT_STEREO         = (CH_FRONT_LEFT|CH_FRONT_RIGHT),
            CH_LAYOUT_2_1            = (CH_LAYOUT_STEREO|CH_BACK_CENTER),
            CH_LAYOUT_SURROUND       = (CH_LAYOUT_STEREO|CH_FRONT_CENTER),
            CH_LAYOUT_4POINT0        = (CH_LAYOUT_SURROUND|CH_BACK_CENTER),
            CH_LAYOUT_2_2            = (CH_LAYOUT_STEREO|CH_SIDE_LEFT|CH_SIDE_RIGHT),
            CH_LAYOUT_QUAD           = (CH_LAYOUT_STEREO|CH_BACK_LEFT|CH_BACK_RIGHT),
            CH_LAYOUT_5POINT0        = (CH_LAYOUT_SURROUND|CH_SIDE_LEFT|CH_SIDE_RIGHT),
            CH_LAYOUT_5POINT1        = (CH_LAYOUT_5POINT0|CH_LOW_FREQUENCY),
            CH_LAYOUT_5POINT0_BACK   = (CH_LAYOUT_SURROUND|CH_BACK_LEFT|CH_BACK_RIGHT),
            CH_LAYOUT_5POINT1_BACK   = (CH_LAYOUT_5POINT0_BACK|CH_LOW_FREQUENCY),
            CH_LAYOUT_7POINT0        = (CH_LAYOUT_5POINT0|CH_BACK_LEFT|CH_BACK_RIGHT),
            CH_LAYOUT_7POINT1        = (CH_LAYOUT_5POINT1|CH_BACK_LEFT|CH_BACK_RIGHT),
            CH_LAYOUT_7POINT1_WIDE   = (CH_LAYOUT_5POINT1_BACK|
                                         CH_FRONT_LEFT_OF_CENTER|CH_FRONT_RIGHT_OF_CENTER),
            CH_LAYOUT_STEREO_DOWNMIX = (CH_STEREO_LEFT|CH_STEREO_RIGHT),

            AVCODEC_MAX_AUDIO_FRAME_SIZE = 192000,

            FF_INPUT_BUFFER_PADDING_SIZE = 8,

            FF_MIN_BUFFER_SIZE = 16384;

    //enum Motion_Est_ID {
    public static final int
            ME_ZERO  = 1,
            ME_FULL  = 2,
            ME_LOG   = 3,
            ME_PHODS = 4,
            ME_EPZS  = 5,
            ME_X1    = 6,
            ME_HEX   = 7,
            ME_UMH   = 8,
            ME_ITER  = 9,
            ME_TESA  = 10;

    //enum AVDiscard{
    public static final int
            AVDISCARD_NONE   =-16,
            AVDISCARD_DEFAULT=  0,
            AVDISCARD_NONREF =  8,
            AVDISCARD_BIDIR  = 16,
            AVDISCARD_NONKEY = 32,
            AVDISCARD_ALL    = 48;

    //enum AVColorPrimaries{
    public static final int
            AVCOL_PRI_BT709       = 1,
            AVCOL_PRI_UNSPECIFIED = 2,
            AVCOL_PRI_BT470M      = 4,
            AVCOL_PRI_BT470BG     = 5,
            AVCOL_PRI_SMPTE170M   = 6,
            AVCOL_PRI_SMPTE240M   = 7,
            AVCOL_PRI_FILM        = 8,
            AVCOL_PRI_NB          = 9;

    //enum AVColorTransferCharacteristic{
    public static final int
            AVCOL_TRC_BT709       = 1,
            AVCOL_TRC_UNSPECIFIED = 2,
            AVCOL_TRC_GAMMA22     = 4,
            AVCOL_TRC_GAMMA28     = 5,
            AVCOL_TRC_NB          = 6;

    //enum AVColorSpace{
    public static final int
            AVCOL_SPC_RGB         = 0,
            AVCOL_SPC_BT709       = 1,
            AVCOL_SPC_UNSPECIFIED = 2,
            AVCOL_SPC_FCC         = 4,
            AVCOL_SPC_BT470BG     = 5,
            AVCOL_SPC_SMPTE170M   = 6,
            AVCOL_SPC_SMPTE240M   = 7,
            AVCOL_SPC_NB          = 8;

    //enum AVColorRange{
    public static final int
            AVCOL_RANGE_UNSPECIFIED = 0,
            AVCOL_RANGE_MPEG        = 1,
            AVCOL_RANGE_JPEG        = 2,
            AVCOL_RANGE_NB          = 3;

    //enum AVChromaLocation{
    public static final int
            AVCHROMA_LOC_UNSPECIFIED = 0,
            AVCHROMA_LOC_LEFT        = 1,
            AVCHROMA_LOC_CENTER      = 2,
            AVCHROMA_LOC_TOPLEFT     = 3,
            AVCHROMA_LOC_TOP         = 4,
            AVCHROMA_LOC_BOTTOMLEFT  = 5,
            AVCHROMA_LOC_BOTTOM      = 6,
            AVCHROMA_LOC_NB          = 7;

    public static class RcOverride extends Pointer {
        static { load(); }
        public RcOverride() { allocate(); }
        public RcOverride(int size) { allocateArray(size); }
        public RcOverride(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public RcOverride position(int position) {
            return (RcOverride)super.position(position);
        }

        public native int start_frame();      public native RcOverride start_frame(int start_frame);
        public native int end_frame();        public native RcOverride end_frame(int end_frame);
        public native int qscale();           public native RcOverride qscale(int qscale);
        public native float quality_factor(); public native RcOverride quality_factor(float quality_factor);
    }

    public static final int
            FF_MAX_B_FRAMES = 16,

            CODEC_FLAG_QSCALE = 0x0002,
            CODEC_FLAG_4MV    = 0x0004,
            CODEC_FLAG_QPEL   = 0x0010,
            CODEC_FLAG_GMC    = 0x0020,
            CODEC_FLAG_MV0    = 0x0040,
            CODEC_FLAG_PART   = 0x0080,

            CODEC_FLAG_INPUT_PRESERVED = 0x0100,
            CODEC_FLAG_PASS1           = 0x0200,
            CODEC_FLAG_PASS2           = 0x0400,
            CODEC_FLAG_EXTERN_HUFF     = 0x1000,
            CODEC_FLAG_GRAY            = 0x2000,
            CODEC_FLAG_EMU_EDGE        = 0x4000,
            CODEC_FLAG_PSNR            = 0x8000,
            CODEC_FLAG_TRUNCATED      = 0x00010000,
            CODEC_FLAG_NORMALIZE_AQP  = 0x00020000,
            CODEC_FLAG_INTERLACED_DCT = 0x00040000,
            CODEC_FLAG_LOW_DELAY      = 0x00080000,
            CODEC_FLAG_ALT_SCAN       = 0x00100000,
            CODEC_FLAG_GLOBAL_HEADER  = 0x00400000,
            CODEC_FLAG_BITEXACT       = 0x00800000,
            CODEC_FLAG_AC_PRED        = 0x01000000,
            CODEC_FLAG_H263P_UMV      = 0x02000000,
            CODEC_FLAG_CBP_RD         = 0x04000000,
            CODEC_FLAG_QP_RD          = 0x08000000,
            CODEC_FLAG_H263P_AIV      = 0x00000008,
            CODEC_FLAG_OBMC           = 0x00000001,
            CODEC_FLAG_LOOP_FILTER    = 0x00000800,
            CODEC_FLAG_H263P_SLICE_STRUCT = 0x10000000,
            CODEC_FLAG_INTERLACED_ME  = 0x20000000,
            CODEC_FLAG_SVCD_SCAN_OFFSET = 0x40000000,
            CODEC_FLAG_CLOSED_GOP     = 0x80000000,
            CODEC_FLAG2_FAST          = 0x00000001,
            CODEC_FLAG2_STRICT_GOP    = 0x00000002,
            CODEC_FLAG2_NO_OUTPUT     = 0x00000004,
            CODEC_FLAG2_LOCAL_HEADER  = 0x00000008,
            CODEC_FLAG2_BPYRAMID      = 0x00000010,
            CODEC_FLAG2_WPRED         = 0x00000020,
            CODEC_FLAG2_MIXED_REFS    = 0x00000040,
            CODEC_FLAG2_8X8DCT        = 0x00000080,
            CODEC_FLAG2_FASTPSKIP     = 0x00000100,
            CODEC_FLAG2_AUD           = 0x00000200,
            CODEC_FLAG2_BRDO          = 0x00000400,
            CODEC_FLAG2_INTRA_VLC     = 0x00000800,
            CODEC_FLAG2_MEMC_ONLY     = 0x00001000,
            CODEC_FLAG2_DROP_FRAME_TIMECODE = 0x00002000,
            CODEC_FLAG2_SKIP_RD       = 0x00004000,
            CODEC_FLAG2_CHUNKS        = 0x00008000,
            CODEC_FLAG2_NON_LINEAR_QUANT = 0x00010000,
            CODEC_FLAG2_BIT_RESERVOIR = 0x00020000,
            CODEC_FLAG2_MBTREE        = 0x00040000,
            CODEC_FLAG2_PSY           = 0x00080000,
            CODEC_FLAG2_SSIM          = 0x00100000,

            CODEC_CAP_DRAW_HORIZ_BAND = 0x0001,

            CODEC_CAP_DR1             = 0x0002,

            CODEC_CAP_PARSE_ONLY      = 0x0004,
            CODEC_CAP_TRUNCATED       = 0x0008,

            CODEC_CAP_HWACCEL         = 0x0010,

            CODEC_CAP_DELAY           = 0x0020,

            CODEC_CAP_SMALL_LAST_FRAME = 0x0040,

            CODEC_CAP_HWACCEL_VDPAU    = 0x0080,

            CODEC_CAP_SUBFRAMES        = 0x0100,

            CODEC_CAP_EXPERIMENTAL     = 0x0200,

            MB_TYPE_INTRA4x4   = 0x0001,
            MB_TYPE_INTRA16x16 = 0x0002,
            MB_TYPE_INTRA_PCM  = 0x0004,
            MB_TYPE_16x16      = 0x0008,
            MB_TYPE_16x8       = 0x0010,
            MB_TYPE_8x16       = 0x0020,
            MB_TYPE_8x8        = 0x0040,
            MB_TYPE_INTERLACED = 0x0080,
            MB_TYPE_DIRECT2    = 0x0100,
            MB_TYPE_ACPRED     = 0x0200,
            MB_TYPE_GMC        = 0x0400,
            MB_TYPE_SKIP       = 0x0800,
            MB_TYPE_P0L0       = 0x1000,
            MB_TYPE_P1L0       = 0x2000,
            MB_TYPE_P0L1       = 0x4000,
            MB_TYPE_P1L1       = 0x8000,
            MB_TYPE_L0         = (MB_TYPE_P0L0 | MB_TYPE_P1L0),
            MB_TYPE_L1         = (MB_TYPE_P0L1 | MB_TYPE_P1L1),
            MB_TYPE_L0L1       = (MB_TYPE_L0   | MB_TYPE_L1),
            MB_TYPE_QUANT      = 0x00010000,
            MB_TYPE_CBP        = 0x00020000;

    public static class AVPanScan extends Pointer {
        static { load(); }
        public AVPanScan() { allocate(); }
        public AVPanScan(int size) { allocateArray(size); }
        public AVPanScan(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVPanScan position(int position) {
            return (AVPanScan)super.position(position);
        }

        public native int id();     public native AVPanScan id(int id);

        public native int width();  public native AVPanScan width(int width);
        public native int height(); public native AVPanScan height(int height);

        //int16_t position[3][2];
        public native short position(int i, int j); public native AVPanScan position(int i, int j, short position);
    }

    public static class AVFrame extends AVPicture {
        static { load(); }
        public AVFrame() { allocate(); }
        public AVFrame(int size) { allocateArray(size); }
        public AVFrame(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFrame position(int position) {
            return (AVFrame)super.position(position);
        }

        @Cast("uint8_t*") // [4]
        public native BytePointer base(int i);           public native AVFrame base(int i, BytePointer base);
        public native int key_frame();                   public native AVFrame key_frame(int key_frame);
        public native int pict_type();                   public native AVFrame pict_type(int pict_type);
        public native long pts();                        public native AVFrame pts(long pts);
        public native int coded_picture_number();        public native AVFrame coded_picture_number(int coded_picture_number);
        public native int display_picture_number();      public native AVFrame display_picture_number(int display_picture_number);
        public native int quality();                     public native AVFrame quality(int quality);
        public native int age();                         public native AVFrame age(int age);
        public native int reference();                   public native AVFrame reference(int reference);
        @Cast("int8_t*")
        public native BytePointer qscale_table();        public native AVFrame qscale_table(BytePointer qscale_table);
        public native int qstride();                     public native AVFrame qstride(int qstride);
        @Cast("uint8_t*")
        public native BytePointer mbskip_table();        public native AVFrame mbskip_table(BytePointer mbskip_table);
        //public int16_t (*motion_val[2])[2];
        @Cast("int16_t (*)[2]")
        public native PointerPointer motion_val(int i);      public native AVFrame motion_val(int i, PointerPointer motion_val);
        public native short motion_val(int i, int j, int k); public native AVFrame motion_val(int i, int j, int k, short motion_val);
        @Cast("uint32_t*")
        public native IntPointer mb_type();              public native AVFrame mb_type(IntPointer mb_type);
        public native byte motion_subsample_log2();      public native AVFrame motion_subsample_log2(byte motion_subsample_log2);
        public native Pointer opaque();                  public native AVFrame opaque(Pointer opaque);
        public native long/*[4]*/ error(int i);          public native AVFrame error(int i, long error);
        public native int type();                        public native AVFrame type(int type);
        public native int repeat_pict();                 public native AVFrame repeat_pict(int repeat_pict);
        public native int qscale_type();                 public native AVFrame qscale_type(int qscale_type);
        public native int interlaced_frame();            public native AVFrame interlaced_frame(int interlaced_frame);
        public native int top_field_first();             public native AVFrame top_field_first(int top_field_first);
        public native AVPanScan pan_scan();              public native AVFrame pan_scan(AVPanScan pan_scan);
        public native int palette_has_changed();         public native AVFrame palette_has_changed(int palette_has_changed);
        public native int buffer_hints();                public native AVFrame buffer_hints(int buffer_hints);
        public native ShortPointer dct_coeff();          public native AVFrame dct_coeff(ShortPointer dct_coeff);
        @Cast("int8_t*")
        public native BytePointer/*[2]*/ref_index(int i);public native AVFrame ref_index(int i, BytePointer ref_index);
        public native long reordered_opaque();           public native AVFrame reordered_opaque(long reordered_opaque);
        public native Pointer hwaccel_picture_private(); public native AVFrame hwaccel_picture_private(Pointer hwaccel_picture_private);
    }


    public static final int
            FF_QSCALE_TYPE_MPEG1 = 0,
            FF_QSCALE_TYPE_MPEG2 = 1,
            FF_QSCALE_TYPE_H264  = 2,
            FF_QSCALE_TYPE_VP56  = 3,

            FF_BUFFER_TYPE_INTERNAL = 1,
            FF_BUFFER_TYPE_USER     = 2,
            FF_BUFFER_TYPE_SHARED   = 4,
            FF_BUFFER_TYPE_COPY     = 8,


            FF_I_TYPE  = 1,
            FF_P_TYPE  = 2,
            FF_B_TYPE  = 3,
            FF_S_TYPE  = 4,
            FF_SI_TYPE = 5,
            FF_SP_TYPE = 6,
            FF_BI_TYPE = 7,

            FF_BUFFER_HINTS_VALID    = 0x01,
            FF_BUFFER_HINTS_READABLE = 0x02,
            FF_BUFFER_HINTS_PRESERVE = 0x04,
            FF_BUFFER_HINTS_REUSABLE = 0x08;

    public static class AVPacket extends Pointer {
        static { load(); }
        public AVPacket() { allocate(); }
        public AVPacket(int size) { allocateArray(size); }
        public AVPacket(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVPacket position(int position) {
            return (AVPacket)super.position(position);
        }

        public native long  pts();                 public native AVPacket pts(long pts);

        public native long  dts();                 public native AVPacket dts(long dts);
        @Cast("uint8_t*")
        public native BytePointer data();          public native AVPacket data(BytePointer data);
        public native int   size();                public native AVPacket size(int size);
        public native int   stream_index();        public native AVPacket stream_index(int stream_index);
        public native int   flags();               public native AVPacket flags(int flags);

        public native int   duration();            public native AVPacket duration(int duration);
        public static class Destruct extends FunctionPointer {
            static { load(); }
            public native void call(AVPacket p);
        }
        public native Destruct destruct();         public native AVPacket destruct(Destruct destruct);
        public native Pointer  priv();             public native AVPacket priv(Pointer priv);
        public native long     pos();              public native AVPacket pos(long pos);

        public native long convergence_duration(); public native AVPacket convergence_duration(long convergence_duration);
    }

    public static final int
            AV_PKT_FLAG_KEY  = 0x0001,
//#if LIBAVCODEC_VERSION_MAJOR < 53
            PKT_FLAG_KEY = AV_PKT_FLAG_KEY;
//#endif


    public static class AVCodecContext extends Pointer {
        static { load(); }
        public AVCodecContext() { allocate(); }
        public AVCodecContext(int size) { allocateArray(size); }
        public AVCodecContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVCodecContext position(int position) {
            return (AVCodecContext)super.position(position);
        }

        @Const
        public native AVClass av_class();       public native AVCodecContext av_class(AVClass av_class);
        public native int bit_rate();           public native AVCodecContext bit_rate(int bit_rate);
        public native int bit_rate_tolerance(); public native AVCodecContext bit_rate_tolerance(int bit_rate_tolerance);
        public native int flags();              public native AVCodecContext flags(int flags);
        public native int sub_id();             public native AVCodecContext sub_id(int sub_id);
        public native int me_method();          public native AVCodecContext me_method(int me_method);

        @Cast("uint8_t*")
        public native BytePointer extradata();  public native AVCodecContext extradata(BytePointer extradata);
        public native int extradata_size();     public native AVCodecContext extradata_size(int extradata_size);

        @ByRef
        public native AVRational time_base();   public native AVCodecContext time_base(AVRational time_base);
        public native int width();              public native AVCodecContext width(int width);
        public native int height();             public native AVCodecContext height(int height);

        public static final int FF_ASPECT_EXTENDED = 15;

        public native int gop_size();           public native AVCodecContext gop_size(int gop_size);
        @Cast("PixelFormat")
        public native int pix_fmt();            public native AVCodecContext pix_fmt(int pix_fmt);
        public native int rate_emu();           public native AVCodecContext rate_emu(int rate_emu);

        public static class Draw_horiz_band extends FunctionPointer {
            static { load(); }
            public    Draw_horiz_band(Pointer p) { super(p); }
            protected Draw_horiz_band() { allocate(); }
            protected final native void allocate();
            public native void call(AVCodecContext s, @Const AVFrame src,
                    IntPointer offset/*[4]*/, int y, int type, int height);
        }
        public native Draw_horiz_band draw_horiz_band();
        public native AVCodecContext draw_horiz_band(Draw_horiz_band draw_horiz_band);

        public native int sample_rate();        public native AVCodecContext sample_rate(int sample_rate);
        public native int channels();           public native AVCodecContext channels(int channels);
        @Cast("SampleFormat")
        public native int sample_fmt();         public native AVCodecContext sample_fmt(int sample_fmt);

        public native int frame_size();         public native AVCodecContext frame_size(int frame_size);
        public native int frame_number();       public native AVCodecContext frame_number(int frame_number);
//#if LIBAVCODEC_VERSION_MAJOR < 53
        public native int real_pict_num();      public native AVCodecContext real_pict_num(int real_pict_num);
//#endif

        public native int delay();              public native AVCodecContext delay(int delay);
        public native float qcompress();        public native AVCodecContext qcompress(float qcompress);
        public native float qblur();            public native AVCodecContext qblur(float qblur);
        public native int qmin();               public native AVCodecContext qmin(int qmin);
        public native int qmax();               public native AVCodecContext qmax(int qmax);
        public native int max_qdiff();          public native AVCodecContext max_qdiff(int max_qdiff);
        public native int max_b_frames();       public native AVCodecContext max_b_frames(int max_b_frames);
        public native float b_quant_factor();   public native AVCodecContext b_quant_factor(float b_quant_factor);

        public native int rc_strategy();        public native AVCodecContext rc_strategy(int rc_strategy);
        public static final int FF_RC_STRATEGY_XVID = 1;

        public native int b_frame_strategy();   public native AVCodecContext b_frame_strategy(int b_frame_strategy);

        @Deprecated 
        public native int hurry_up();           public native AVCodecContext hurry_up(int hurry_up);
        public native AVCodec codec();          public native AVCodecContext codec(AVCodec codec);
        public native Pointer priv_data();      public native AVCodecContext priv_data(Pointer priv_data);
        public native int rtp_payload_size();   public native AVCodecContext rtp_payload_size(int rtp_payload_size);

        public static class Rtp_callback extends FunctionPointer {
            static { load(); }
            public    Rtp_callback(Pointer p) { super(p); }
            protected Rtp_callback() { allocate(); }
            protected final native void allocate();
            public native void call(AVCodecContext avctx, Pointer data, int size, int mb_nb);
        }
        public native Rtp_callback rtp_callback();
        public native AVCodecContext rtp_callback(Rtp_callback rtp_callback);

        public native int mv_bits();            public native AVCodecContext mv_bits(int mv_bits);
        public native int header_bits();        public native AVCodecContext header_bits(int header_bits);
        public native int i_tex_bits();         public native AVCodecContext i_tex_bits(int i_tex_bits);
        public native int p_tex_bits();         public native AVCodecContext p_tex_bits(int p_tex_bits);
        public native int i_count();            public native AVCodecContext i_count(int i_count);
        public native int p_count();            public native AVCodecContext p_count(int p_count);
        public native int skip_count();         public native AVCodecContext skip_count(int skip_count);
        public native int misc_bits();          public native AVCodecContext misc_bits(int misc_bits);

        public native int frame_bits();         public native AVCodecContext frame_bits(int frame_bits);

        public native Pointer opaque();         public native AVCodecContext opaque(Pointer opaque);

        // char codec_name[32];
        public native String codec_name();      public native AVCodecContext codec_name(String codec_name);
        @Cast("AVMediaType")
        public native int codec_type();         public native AVCodecContext codec_type(int codec_type);
        @Cast("CodecID")
        public native int codec_id();           public native AVCodecContext codec_id(int codec_id);

        public native int codec_tag();          public native AVCodecContext codec_tag(int codec_tag);

        public native int workaround_bugs();    public native AVCodecContext workaround_bugs(int workaround_bugs);
        public static final int
            FF_BUG_AUTODETECT       = 1,
            FF_BUG_OLD_MSMPEG4      = 2,
            FF_BUG_XVID_ILACE       = 4,
            FF_BUG_UMP4             = 8,
            FF_BUG_NO_PADDING       = 16,
            FF_BUG_AMV              = 32,
            FF_BUG_AC_VLC           = 0,
            FF_BUG_QPEL_CHROMA      = 64,
            FF_BUG_STD_QPEL         = 128,
            FF_BUG_QPEL_CHROMA2     = 256,
            FF_BUG_DIRECT_BLOCKSIZE = 512,
            FF_BUG_EDGE             = 1024,
            FF_BUG_HPEL_CHROMA      = 2048,
            FF_BUG_DC_CLIP          = 4096,
            FF_BUG_MS               = 8192,
            FF_BUG_TRUNCATED        = 16384;
//            FF_BUG_FAKE_SCALABILITY = 16;

        public native int luma_elim_threshold();   public native AVCodecContext luma_elim_threshold(int luma_elim_threshold);
        public native int chroma_elim_threshold(); public native AVCodecContext chroma_elim_threshold(int chroma_elim_threshold);

        public native int strict_std_compliance(); public native AVCodecContext strict_std_compliance(int strict_std_compliance);
        public static final int
            FF_COMPLIANCE_VERY_STRICT  =  2,
            FF_COMPLIANCE_STRICT       =  1,
            FF_COMPLIANCE_NORMAL       =  0,
            FF_COMPLIANCE_INOFFICIAL   = -1,
            FF_COMPLIANCE_EXPERIMENTAL = -2;

        public native float b_quant_offset();   public native AVCodecContext b_quant_offset(float b_quant_offset);

        public native int error_recognition();  public native AVCodecContext error_recognition(int error_recognition);
        public static final int
            FF_ER_CAREFUL         = 1,
            FF_ER_COMPLIANT       = 2,
            FF_ER_AGGRESSIVE      = 3,
            FF_ER_VERY_AGGRESSIVE = 4;

        public static class Get_buffer extends FunctionPointer {
            static { load(); }
            public    Get_buffer(Pointer p) { super(p); }
            protected Get_buffer() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext c, AVFrame pic);
        }
        public native Get_buffer get_buffer();
        public native AVCodecContext get_buffer(Get_buffer get_buffer);

        public static class Release_buffer extends FunctionPointer {
            static { load(); }
            public    Release_buffer(Pointer p) { super(p); }
            protected Release_buffer() { allocate(); }
            protected final native void allocate();
            public native void call(AVCodecContext c, AVFrame pic);
        }
        public native Release_buffer release_buffer();
        public native AVCodecContext release_buffer(Release_buffer release_buffer);

        public native int has_b_frames();       public native AVCodecContext has_b_frames(int has_b_frames);
        public native int block_align();        public native AVCodecContext block_align(int block_align);
        public native int parse_only();         public native AVCodecContext parse_only(int parse_only);
        public native int mpeg_quant();         public native AVCodecContext mpeg_quant(int mpeg_quant);

        @Cast("char*")
        public native BytePointer stats_out();  public native AVCodecContext stats_out(BytePointer stats_out);
        @Cast("char*")
        public native BytePointer stats_in();   public native AVCodecContext stats_in(BytePointer stats_in);

        public native float rc_qsquish();       public native AVCodecContext rc_qsquish(float rc_qsquish);

        public native float rc_qmod_amp();      public native AVCodecContext rc_qmod_amp(float rc_qmod_amp);
        public native int rc_qmod_freq();       public native AVCodecContext rc_qmod_freq(int rc_qmod_freq);

        public native RcOverride rc_override(); public native AVCodecContext rc_override(RcOverride rc_override);
        public native int rc_override_count();  public native AVCodecContext rc_override_count(int rc_override_count);

        @Cast("const char*")
        public native BytePointer rc_eq();      public native AVCodecContext rc_eq(BytePointer rc_eq);
        public native int rc_max_rate();        public native AVCodecContext rc_max_rate(int rc_max_rate);
        public native int rc_min_rate();        public native AVCodecContext rc_min_rate(int rc_min_rate);

        public native int rc_buffer_size();     public native AVCodecContext rc_buffer_size(int rc_buffer_size);
        public native float rc_buffer_aggressivity(); public native AVCodecContext rc_buffer_aggressivity(float rc_buffer_aggressivity);

        public native float i_quant_factor();   public native AVCodecContext i_quant_factor(float i_quant_factor);
        public native float i_quant_offset();   public native AVCodecContext i_quant_offset(float i_quant_offset);
        public native float rc_initial_cplx();  public native AVCodecContext rc_initial_cplx(float rc_initial_cplx);

        public native int dct_algo();           public native AVCodecContext dct_algo(int dct_algo);
        public static final int
                FF_DCT_AUTO    = 0,
                FF_DCT_FASTINT = 1,
                FF_DCT_INT     = 2,
                FF_DCT_MMX     = 3,
                FF_DCT_MLIB    = 4,
                FF_DCT_ALTIVEC = 5,
                FF_DCT_FAAN    = 6;

        public native float lumi_masking();          public native AVCodecContext lumi_masking(float lumi_masking);
        public native float temporal_cplx_masking(); public native AVCodecContext temporal_cplx_masking(float temporal_cplx_masking);
        public native float spatial_cplx_masking();  public native AVCodecContext spatial_cplx_masking(float spatial_cplx_masking);
        public native float p_masking();             public native AVCodecContext p_masking(float p_masking);
        public native float dark_masking();          public native AVCodecContext dark_masking(float dark_masking);

        public native int idct_algo();               public native AVCodecContext idct_algo(int idct_algo);
        public static final int
                FF_IDCT_AUTO          = 0,
                FF_IDCT_INT           = 1,
                FF_IDCT_SIMPLE        = 2,
                FF_IDCT_SIMPLEMMX     = 3,
                FF_IDCT_LIBMPEG2MMX   = 4,
                FF_IDCT_PS2           = 5,
                FF_IDCT_MLIB          = 6,
                FF_IDCT_ARM           = 7,
                FF_IDCT_ALTIVEC       = 8,
                FF_IDCT_SH4           = 9,
                FF_IDCT_SIMPLEARM     = 10,
                FF_IDCT_H264          = 11,
                FF_IDCT_VP3           = 12,
                FF_IDCT_IPP           = 13,
                FF_IDCT_XVIDMMX       = 14,
                FF_IDCT_CAVS          = 15,
                FF_IDCT_SIMPLEARMV5TE = 16,
                FF_IDCT_SIMPLEARMV6   = 17,
                FF_IDCT_SIMPLEVIS     = 18,
                FF_IDCT_WMV2          = 19,
                FF_IDCT_FAAN          = 20,
                FF_IDCT_EA            = 21,
                FF_IDCT_SIMPLENEON    = 22,
                FF_IDCT_SIMPLEALPHA   = 23,
                FF_IDCT_BINK          = 24;

        public native int slice_count();         public native AVCodecContext slice_count(int slice_count);
        public native IntPointer slice_offset(); public native AVCodecContext slice_offset(IntPointer slice_offset);

        public native int error_concealment();   public native AVCodecContext error_concealment(int error_concealment);
        public static final int
                FF_EC_GUESS_MVS  = 1,
                FF_EC_DEBLOCK    = 2;

        public native int dsp_mask();            public native AVCodecContext dsp_mask(int dsp_mask);
        public static final int
                FF_MM_FORCE    = 0x80000000,
                FF_MM_MMX      = 0x0001,
                FF_MM_3DNOW    = 0x0004,
//#if LIBAVCODEC_VERSION_MAJOR < 53
                FF_MM_MMXEXT   = 0x0002,
//#endif
                FF_MM_MMX2     = 0x0002,
                FF_MM_SSE      = 0x0008,
                FF_MM_SSE2     = 0x0010,
                FF_MM_3DNOWEXT = 0x0020,
                FF_MM_SSE3     = 0x0040,
                FF_MM_SSSE3    = 0x0080,
                FF_MM_SSE4     = 0x0100,
                FF_MM_SSE42    = 0x0200,
                FF_MM_IWMMXT   = 0x0100,
                FF_MM_ALTIVEC  = 0x0001;

        public native int bits_per_coded_sample(); public native AVCodecContext bits_per_coded_sample(int bits_per_coded_sample);

        public native int prediction_method();     public native AVCodecContext prediction_method(int prediction_method);
        public static final int
            FF_PRED_LEFT   = 0,
            FF_PRED_PLANE  = 1,
            FF_PRED_MEDIAN = 2;

        @ByRef public native AVRational sample_aspect_ratio();
               public native AVCodecContext sample_aspect_ratio(AVRational sample_aspect_ratio);

        public native AVFrame coded_frame();    public native AVCodecContext coded_frame(AVFrame coded_frame);

        public native int debug();              public native AVCodecContext debug(int debug);
        public static final int
                FF_DEBUG_PICT_INFO   = 1,
                FF_DEBUG_RC          = 2,
                FF_DEBUG_BITSTREAM   = 4,
                FF_DEBUG_MB_TYPE     = 8,
                FF_DEBUG_QP          = 16,
                FF_DEBUG_MV          = 32,
                FF_DEBUG_DCT_COEFF   = 0x00000040,
                FF_DEBUG_SKIP        = 0x00000080,
                FF_DEBUG_STARTCODE   = 0x00000100,
                FF_DEBUG_PTS         = 0x00000200,
                FF_DEBUG_ER          = 0x00000400,
                FF_DEBUG_MMCO        = 0x00000800,
                FF_DEBUG_BUGS        = 0x00001000,
                FF_DEBUG_VIS_QP      = 0x00002000,
                FF_DEBUG_VIS_MB_TYPE = 0x00004000,
                FF_DEBUG_BUFFERS     = 0x00008000;

        public native int debug_mv();           public native AVCodecContext debug_mv(int debug_mv);
        public static final int
                FF_DEBUG_VIS_MV_P_FOR  = 0x00000001,
                FF_DEBUG_VIS_MV_B_FOR  = 0x00000002,
                FF_DEBUG_VIS_MV_B_BACK = 0x00000004;

        public native long error(int i);        public native AVCodecContext error(int i, long error);
        public native int mb_qmin();            public native AVCodecContext mb_qmin(int mb_qmin);
        public native int mb_qmax();            public native AVCodecContext mb_qmax(int mb_qmax);

        public native int me_cmp();             public native AVCodecContext me_cmp(int me_cmp);
        public native int me_sub_cmp();         public native AVCodecContext me_sub_cmp(int me_sub_cmp);
        public native int mb_cmp();             public native AVCodecContext mb_cmp(int mb_cmp);
        public native int ildct_cmp();          public native AVCodecContext ildct_cmp(int ildct_cmp);
        public static final int
                FF_CMP_SAD    = 0,
                FF_CMP_SSE    = 1,
                FF_CMP_SATD   = 2,
                FF_CMP_DCT    = 3,
                FF_CMP_PSNR   = 4,
                FF_CMP_BIT    = 5,
                FF_CMP_RD     = 6,
                FF_CMP_ZERO   = 7,
                FF_CMP_VSAD   = 8,
                FF_CMP_VSSE   = 9,
                FF_CMP_NSSE   = 10,
                FF_CMP_W53    = 11,
                FF_CMP_W97    = 12,
                FF_CMP_DCTMAX = 13,
                FF_CMP_DCT264 = 14,
                FF_CMP_CHROMA = 256;

        public native int dia_size();             public native AVCodecContext dia_size(int dia_size);
        public native int last_predictor_count(); public native AVCodecContext last_predictor_count(int last_predictor_count);
        public native int pre_me();               public native AVCodecContext pre_me(int pre_me);
        public native int me_pre_cmp();           public native AVCodecContext me_pre_cmp(int me_pre_cmp);
        public native int pre_dia_size();         public native AVCodecContext pre_dia_size(int pre_dia_size);
        public native int me_subpel_quality();    public native AVCodecContext me_subpel_quality(int me_subpel_quality);

        public static class Get_format extends FunctionPointer {
            static { load(); }
            public    Get_format(Pointer p) { super(p); }
            protected Get_format() { allocate(); }
            protected final native void allocate();
            public native @Cast("PixelFormat") int call(AVCodecContext s, @Cast("const PixelFormat*") IntPointer fmt);
        }
        public native Get_format get_format();
        public native AVCodecContext get_format(Get_format get_format);

        public native int dtg_active_format();      public native AVCodecContext dtg_active_format(int dtg_active_format);
        public static final int
                FF_DTG_AFD_SAME         = 8,
                FF_DTG_AFD_4_3          = 9,
                FF_DTG_AFD_16_9         = 10,
                FF_DTG_AFD_14_9         = 11,
                FF_DTG_AFD_4_3_SP_14_9  = 13,
                FF_DTG_AFD_16_9_SP_14_9 = 14,
                FF_DTG_AFD_SP_4_3       = 15;

        public native int me_range();               public native AVCodecContext me_range(int me_range);

        public native int intra_quant_bias();       public native AVCodecContext intra_quant_bias(int intra_quant_bias);
        public static final int FF_DEFAULT_QUANT_BIAS = 999999;

        public native int inter_quant_bias();       public native AVCodecContext inter_quant_bias(int inter_quant_bias);
        public native int color_table_id();         public native AVCodecContext color_table_id(int color_table_id);

        public native int internal_buffer_count();  public native AVCodecContext internal_buffer_count(int internal_buffer_count);
        public native Pointer internal_buffer();    public native AVCodecContext internal_buffer(Pointer internal_buffer);

        public static final int
                FF_LAMBDA_SHIFT = 7,
                FF_LAMBDA_SCALE = (1<<FF_LAMBDA_SHIFT),
                FF_QP2LAMBDA    = 118,
                FF_LAMBDA_MAX   = (256*128-1),

                FF_QUALITY_SCALE = FF_LAMBDA_SCALE;

        public native int global_quality();         public native AVCodecContext global_quality(int global_quality);
        public static final int
                FF_CODER_TYPE_VLC      = 0,
                FF_CODER_TYPE_AC       = 1,
                FF_CODER_TYPE_RAW      = 2,
                FF_CODER_TYPE_RLE      = 3,
                FF_CODER_TYPE_DEFLATE  = 4;

        public native int coder_type();             public native AVCodecContext coder_type(int coder_type);
        public native int context_model();          public native AVCodecContext context_model(int context_model);

//#if 0
//    uint8_t * (*realloc)(struct AVCodecContext *s, uint8_t *buf, int buf_size);
//#endif

        public native int slice_flags();            public native AVCodecContext slice_flags(int slice_flags);
        public static final int
                SLICE_FLAG_CODED_ORDER   = 0x0001,
                SLICE_FLAG_ALLOW_FIELD   = 0x0002,
                SLICE_FLAG_ALLOW_PLANE   = 0x0004;

        public native int xvmc_acceleration();      public native AVCodecContext xvmc_acceleration(int xvmc_acceleration);

        public native int mb_decision();            public native AVCodecContext mb_decision(int mb_decision);
        public static final int
                FF_MB_DECISION_SIMPLE = 0,
                FF_MB_DECISION_BITS   = 1,
                FF_MB_DECISION_RD     = 2;

        @Cast("uint16_t*")
        public native ShortPointer intra_matrix();  public native AVCodecContext intra_matrix(ShortPointer intra_matrix);
        @Cast("uint16_t*")
        public native ShortPointer inter_matrix();  public native AVCodecContext inter_matrix(ShortPointer inter_matrix);

        public native int stream_codec_tag();       public native AVCodecContext stream_codec_tag(int stream_codec_tag);
        public native int scenechange_threshold();  public native AVCodecContext scenechange_threshold(int scenechange_threshold);
        public native int lmin();                   public native AVCodecContext lmin(int lmin);
        public native int lmax();                   public native AVCodecContext lmax(int lmax);

//        public native AVPaletteControl palctrl();   public native AVCodecContext palctrl(AVPaletteControl palctrl);

        public native int noise_reduction();        public native AVCodecContext noise_reduction(int noise_reduction);

        public static class Reget_buffer extends FunctionPointer {
            static { load(); }
            public    Reget_buffer(Pointer p) { super(p); }
            protected Reget_buffer() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext c, AVFrame pic);
        }
        public native Reget_buffer reget_buffer();
        public native AVCodecContext reget_buffer(Reget_buffer reget_buffer);

        public native int rc_initial_buffer_occupancy(); public native AVCodecContext rc_initial_buffer_occupancy(int rc_initial_buffer_occupancy);
        public native int inter_threshold();             public native AVCodecContext inter_threshold(int inter_threshold);
        public native int flags2();                      public native AVCodecContext flags2(int flags2);
        public native int error_rate();                  public native AVCodecContext error_rate(int error_rate);

        public native int antialias_algo();              public native AVCodecContext antialias_algo(int antialias_algo);
        public static final int
                FF_AA_AUTO    = 0,
                FF_AA_FASTINT = 1,
                FF_AA_INT     = 2,
                FF_AA_FLOAT   = 3;

        public native int quantizer_noise_shaping();     public native AVCodecContext quantizer_noise_shaping(int quantizer_noise_shaping);
        public native int thread_count();                public native AVCodecContext thread_count(int thread_count);

        public static class Execute extends FunctionPointer {
            static { load(); }
            public static class Func extends FunctionPointer {
                static { load(); }
                public    Func(Pointer p) { super(p); }
                protected Func() { allocate(); }
                protected final native void allocate();
                public native int call(AVCodecContext c2, Pointer arg);
            }
            public    Execute(Pointer p) { super(p); }
            protected Execute() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext c, Func func, Pointer arg2, IntPointer ret, int count, int size);
        }
        public native Execute execute();        public native AVCodecContext execute(Execute execute);
        public native Pointer thread_opaque();  public native AVCodecContext thread_opaque(Pointer thread_opaque);

        public native int me_threshold();       public native AVCodecContext me_threshold(int me_threshold);
        public native int mb_threshold();       public native AVCodecContext mb_threshold(int mb_threshold);
        public native int intra_dc_precision(); public native AVCodecContext intra_dc_precision(int intra_dc_precision);
        public native int nsse_weight();        public native AVCodecContext nsse_weight(int nsse_weight);
        public native int skip_top();           public native AVCodecContext skip_top(int skip_top);
        public native int skip_bottom();        public native AVCodecContext skip_bottom(int skip_bottom);

        public native int profile();            public native AVCodecContext profile(int profile);
        public static final int
                FF_PROFILE_UNKNOWN = -99,

                FF_PROFILE_AAC_MAIN = 0,
                FF_PROFILE_AAC_LOW  = 1,
                FF_PROFILE_AAC_SSR  = 2,
                FF_PROFILE_AAC_LTP  = 3,

                FF_PROFILE_H264_BASELINE    = 66,
                FF_PROFILE_H264_MAIN        = 77,
                FF_PROFILE_H264_EXTENDED    = 88,
                FF_PROFILE_H264_HIGH        = 100,
                FF_PROFILE_H264_HIGH_10     = 110,
                FF_PROFILE_H264_HIGH_422    = 122,
                FF_PROFILE_H264_HIGH_444    = 244,
                FF_PROFILE_H264_CAVLC_444   = 44;

        public native int level();                  public native AVCodecContext level(int level);
        public static final int FF_LEVEL_UNKNOWN = -99;

        public native int lowres();                 public native AVCodecContext lowres(int lowres);
        public native int coded_width();            public native AVCodecContext coded_width(int coded_width);
        public native int coded_height();           public native AVCodecContext coded_height(int coded_height);
        public native int frame_skip_threshold();   public native AVCodecContext frame_skip_threshold(int frame_skip_threshold);
        public native int frame_skip_factor();      public native AVCodecContext frame_skip_factor(int frame_skip_factor);
        public native int frame_skip_exp();         public native AVCodecContext frame_skip_exp(int frame_skip_exp);
        public native int frame_skip_cmp();         public native AVCodecContext frame_skip_cmp(int frame_skip_cmp);
        public native float border_masking();       public native AVCodecContext border_masking(float border_masking);
        public native int mb_lmin();                public native AVCodecContext mb_lmin(int mb_lmin);
        public native int mb_lmax();                public native AVCodecContext mb_lmax(int mb_lmax);
        public native int me_penalty_compensation();public native AVCodecContext me_penalty_compensation(int me_penalty_compensation);

        @Cast("AVDiscard")
        public native int skip_loop_filter();       public native AVCodecContext skip_loop_filter(int skip_loop_filter);
        @Cast("AVDiscard")
        public native int skip_idct();              public native AVCodecContext skip_idct(int skip_idct);
        @Cast("AVDiscard")
        public native int skip_frame();             public native AVCodecContext skip_frame(int skip_frame);

        public native int bidir_refine();           public native AVCodecContext bidir_refine(int bidir_refine);
        public native int brd_scale();              public native AVCodecContext brd_scale(int brd_scale);
        public native float crf();                  public native AVCodecContext crf(float crf);
        public native int cqp();                    public native AVCodecContext cqp(int cqp);
        public native int keyint_min();             public native AVCodecContext keyint_min(int keyint_min);
        public native int refs();                   public native AVCodecContext refs(int refs);
        public native int chromaoffset();           public native AVCodecContext chromaoffset(int chromaoffset);
        public native int bframebias();             public native AVCodecContext bframebias(int bframebias);
        public native int trellis();                public native AVCodecContext trellis(int trellis);
        public native float complexityblur();       public native AVCodecContext complexityblur(float complexityblur);
        public native int deblockalpha();           public native AVCodecContext deblockalpha(int deblockalpha);
        public native int deblockbeta();            public native AVCodecContext deblockbeta(int deblockbeta);

        public native int partitions();             public native AVCodecContext partitions(int partitions);
        public static final int
                X264_PART_I4X4 = 0x001,
                X264_PART_I8X8 = 0x002,
                X264_PART_P8X8 = 0x010,
                X264_PART_P4X4 = 0x020,
                X264_PART_B8X8 = 0x100;

        public native int directpred();             public native AVCodecContext directpred(int directpred);
        public native int cutoff();                 public native AVCodecContext cutoff(int cutoff);
        public native int scenechange_factor();     public native AVCodecContext scenechange_factor(int scenechange_factor);
        public native int mv0_threshold();          public native AVCodecContext mv0_threshold(int mv0_threshold);
        public native int b_sensitivity();          public native AVCodecContext b_sensitivity(int b_sensitivity);

        public native int compression_level();      public native AVCodecContext compression_level(int compression_level);
        public static final int FF_COMPRESSION_DEFAULT = -1;

        public native int use_lpc();                public native AVCodecContext use_lpc(int use_lpc);
        public native int lpc_coeff_precision();    public native AVCodecContext lpc_coeff_precision(int lpc_coeff_precision);
        public native int min_prediction_order();   public native AVCodecContext min_prediction_order(int min_prediction_order);
        public native int max_prediction_order();   public native AVCodecContext max_prediction_order(int max_prediction_order);
        public native int prediction_order_method();public native AVCodecContext prediction_order_method(int prediction_order_method);
        public native int min_partition_order();    public native AVCodecContext min_partition_order(int min_partition_order);
        public native int max_partition_order();    public native AVCodecContext max_partition_order(int max_partition_order);
        public native long timecode_frame_start();  public native AVCodecContext timecode_frame_start(long timecode_frame_start);

//#if LIBAVCODEC_VERSION_MAJOR < 53
        @Deprecated 
        public native int request_channels();       public native AVCodecContext request_channels(int request_channels);
//#endif

        public native float drc_scale();                public native AVCodecContext drc_scale(float drc_scale);
        public native long reordered_opaque();          public native AVCodecContext reordered_opaque(long reordered_opaque);
        public native int bits_per_raw_sample();        public native AVCodecContext bits_per_raw_sample(int bits_per_raw_sample);
        public native long channel_layout();            public native AVCodecContext channel_layout(long channel_layout);
        public native long request_channel_layout();    public native AVCodecContext request_channel_layout(long request_channel_layout);
        public native float rc_max_available_vbv_use(); public native AVCodecContext rc_max_available_vbv_use(float rc_max_available_vbv_use);
        public native float rc_min_vbv_overflow_use();  public native AVCodecContext rc_min_vbv_overflow_use(float rc_min_vbv_overflow_use);

        public native AVHWAccel hwaccel();              public native AVCodecContext hwaccel(AVHWAccel hwaccel);
        public native int ticks_per_frame();            public native AVCodecContext ticks_per_frame(int ticks_per_frame);
        public native Pointer hwaccel_context();        public native AVCodecContext hwaccel_context(Pointer hwaccel_context);

        @Cast("AVColorPrimaries")
        public native int color_primaries();            public native AVCodecContext color_primaries(int color_primaries);
        @Cast("AVColorTransferCharacteristic")
        public native int color_trc();                  public native AVCodecContext color_trc(int color_trc);
        @Cast("AVColorSpace")
        public native int colorspace();                 public native AVCodecContext colorspace(int colorspace);
        @Cast("AVColorRange")
        public native int color_range();                public native AVCodecContext color_range(int color_range);
        @Cast("AVChromaLocation")
        public native int chroma_sample_location();     public native AVCodecContext chroma_sample_location(int chroma_sample_location);

        public static class Execute2 extends FunctionPointer {
            static { load(); }
            public static class Func2 extends FunctionPointer {
                static { load(); }
                public    Func2(Pointer p) { super(p); }
                protected Func2() { allocate(); }
                protected final native void allocate();
                public native int call(AVCodecContext c2, Pointer arg, int jobnr, int threadnr);
            }
            public    Execute2(Pointer p) { super(p); }
            protected Execute2() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext c, Func2 func2, Pointer arg2, IntPointer ret, int count);
        }
        public native Execute2 execute2();      public native AVCodecContext execute2(Execute2 execute2);

        public native int weighted_p_pred();    public native AVCodecContext weighted_p_pred(int weighted_p_pred);
        public native int aq_mode();            public native AVCodecContext aq_mode(int aq_mode);
        public native float aq_strength();      public native AVCodecContext aq_strength(float aq_strength);
        public native float psy_rd();           public native AVCodecContext psy_rd(float psy_rd);
        public native float psy_trellis();      public native AVCodecContext psy_trellis(float psy_trellis);
        public native int rc_lookahead();       public native AVCodecContext rc_lookahead(int rc_lookahead);
    }

    public static class AVCodec extends Pointer {
        static { load(); }
        public AVCodec() { allocate(); }
        public AVCodec(int size) { allocateArray(size); }
        public AVCodec(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVCodec position(int position) {
            return (AVCodec)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer name();   public native AVCodec name(BytePointer name);
        @Cast("AVMediaType")
        public native int type();           public native AVCodec type(int type);
        @Cast("CodecID")
        public native int id();             public native AVCodec id(int id);

        public native int priv_data_size(); public native AVCodec priv_data_size(int priv_data_size);
        public static class Init extends FunctionPointer {
            static { load(); }
            public native int call(AVCodecContext c);
        }
        public native Init init();          public native AVCodec init(Init init);
        public static class Encode extends FunctionPointer {
            static { load(); }
            public native int call(AVCodecContext c, @Cast("uint8_t*") BytePointer buf, int buf_size, Pointer data);
        }
        public native Encode encode();      public native AVCodec encode(Encode encode);
        public static class Close extends FunctionPointer {
            static { load(); }
            public native int call(AVCodecContext c);
        }
        public native Close close();        public native AVCodec close(Close close);
        public static class Decode extends FunctionPointer {
            static { load(); }
            public native int call(AVCodecContext c, Pointer outdata, IntPointer outdata_size, AVPacket avpkt);
        }
        public native Decode decode();      public native AVCodec decode(Decode decode);

        public native int capabilities();   public native AVCodec capabilities(int capabilities);
        public native AVCodec next();       public native AVCodec next(AVCodec next);

        public static class Flush extends FunctionPointer {
            static { load(); }
            public native void call(AVCodecContext c);
        }
        public native Flush flush();        public native AVCodec flush(Flush flush);
        @Const
        public native AVRational supported_framerates();    public native AVCodec supported_framerates(AVRational supported_framerates);
        @Cast("const PixelFormat*")
        public native IntPointer pix_fmts();                public native AVCodec pix_fmts(IntPointer pix_fmts);

        @Cast("const char*")
        public native BytePointer long_name();              public native AVCodec long_name(BytePointer long_name);
        @Const
        public native IntPointer supported_samplerates();   public native AVCodec supported_samplerates(IntPointer supported_samplerates);
        @Cast("const SampleFormat*")
        public native IntPointer sample_fmts();             public native AVCodec sample_fmts(IntPointer sample_fmts);
        @Const
        public native LongPointer channel_layouts();        public native AVCodec channel_layouts(LongPointer channel_layouts);
    }

    public static class AVHWAccel extends Pointer {
        static { load(); }
        public AVHWAccel() { allocate(); }
        public AVHWAccel(int size) { allocateArray(size); }
        public AVHWAccel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVHWAccel position(int position) {
            return (AVHWAccel)super.position(position);
        }

        @Cast("const char *")
        public native BytePointer name();   public native AVHWAccel name(BytePointer name);
        @Cast("AVMediaType")
        public native int type();           public native AVHWAccel type(int type);
        @Cast("CodecID")
        public native int id();             public native AVHWAccel id(int id);
        @Cast("PixelFormat")
        public native int pix_fmt();        public native AVHWAccel pix_fmt(int pix_fmt);

        public native int capabilities();   public native AVHWAccel capabilities(int capabilities);
        public native AVHWAccel next();     public native AVHWAccel next(AVHWAccel next);

        public static class Start_frame extends FunctionPointer {
            static { load(); }
            public    Start_frame(Pointer p) { super(p); }
            protected Start_frame() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext avctx, @Cast("const uint8_t*") BytePointer buf, @Cast("uint32_t") int buf_size);
        }
        public native Start_frame start_frame();
        public native AVHWAccel start_frame(Start_frame start_frame);

        public static class Decode_slice extends FunctionPointer {
            static { load(); }
            public    Decode_slice(Pointer p) { super(p); }
            protected Decode_slice() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext avctx, @Cast("const uint8_t*") BytePointer buf, @Cast("uint32_t") int buf_size);
        }
        public native Decode_slice decode_slice();
        public native AVHWAccel decode_slice(Decode_slice decode_slice);

        public static class End_frame extends FunctionPointer {
            static { load(); }
            public    End_frame(Pointer p) { super(p); }
            protected End_frame() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext avctx);
        }
        public native End_frame end_frame();
        public native AVHWAccel end_frame(End_frame end_frame);

        public native int priv_data_size(); public native AVHWAccel priv_data_size(int priv_data_size);
    }

    public static class AVPicture extends Pointer {
        static { load(); }
        public AVPicture() { allocate(); }
        public AVPicture(int size) { allocateArray(size); }
        public AVPicture(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVPicture position(int position) {
            return (AVPicture)super.position(position);
        }

        @Cast("uint8_t*") // [4]
        public native BytePointer data(int i);    public native AVPicture data(int i, BytePointer data);
        public native int/*[4]*/ linesize(int i); public native AVPicture linesize(int i, int linesize);
        @MemberGetter public native IntPointer linesize();
    }

//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated public static final int
//            AVPALETTE_SIZE = 1024,
//            AVPALETTE_COUNT = 256;
//    @Deprecated public static class AVPaletteControl extends Pointer {
//        static { load(); }
//        public AVPaletteControl() { allocate(); }
//        public AVPaletteControl(int size) { allocateArray(size); }
//        public AVPaletteControl(Pointer p) { super(p); }
//        private native void allocate();
//        private native void allocateArray(int size);
//
//        @Override public AVPaletteControl position(int position) {
//            return (AVPaletteControl)super.position(position);
//        }
//
//        public native int palette_changed(); public native AVPaletteControl palette_changed(int palette_changed);
//        // int palette[AVPALETTE_COUNT];
//        public native int palette(int i);    public native AVPaletteControl palette(int i, int palette);
//    }
//#endif

    //enum AVSubtitleType {
    public static final int
            SUBTITLE_NONE = 0,
            SUBTITLE_BITMAP = 1,
            SUBTITLE_TEXT = 2,
            SUBTITLE_ASS = 3;

    public static class AVSubtitleRect extends Pointer {
        static { load(); }
        public AVSubtitleRect() { allocate(); }
        public AVSubtitleRect(int size) { allocateArray(size); }
        public AVSubtitleRect(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVSubtitleRect position(int position) {
            return (AVSubtitleRect)super.position(position);
        }

        public native int x();            public native AVSubtitleRect x(int x);
        public native int y();            public native AVSubtitleRect y(int y);
        public native int w();            public native AVSubtitleRect w(int w);
        public native int h();            public native AVSubtitleRect h(int h);
        public native int nb_colors();    public native AVSubtitleRect nb_colors(int nb_colors);

        @ByRef
        public native AVPicture pict();   public native AVSubtitleRect pict(AVPicture pict);
        @Cast("AVSubtitleType")
        public native int type();         public native AVSubtitleRect type(int type);

        @Cast("char *")
        public native BytePointer text(); public native AVSubtitleRect text(BytePointer text);
        @Cast("char *")
        public native BytePointer ass();  public native AVSubtitleRect ass(BytePointer ass);
    }

    public static class AVSubtitle extends Pointer {
        static { load(); }
        public AVSubtitle() { allocate(); }
        public AVSubtitle(int size) { allocateArray(size); }
        public AVSubtitle(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVSubtitle position(int position) {
            return (AVSubtitle)super.position(position);
        }

        public native short format();           public native AVSubtitle format(short format);
        public native int start_display_time(); public native AVSubtitle start_display_time(int start_display_time);
        public native int end_display_time();   public native AVSubtitle end_display_time(int end_display_time);
        public native int num_rects();          public native AVSubtitle num_rects(int num_rects);
        @Cast("AVSubtitleRect**")
        public native PointerPointer rects();   public native AVSubtitle rects(PointerPointer rects);
        public native long pts();               public native AVSubtitle pts(long pts);
    }

//    @Deprecated
//    public static native void av_destruct_packet_nofree(AVPacket pkt);
    public static native void av_destruct_packet(AVPacket pkt);
    public static native void av_init_packet(AVPacket pkt);
    public static native int av_new_packet(AVPacket pkt, int size);
    public static native void av_shrink_packet(AVPacket pkt, int size);
    public static native int av_dup_packet(AVPacket pkt);
    public static native void av_free_packet(AVPacket pkt);


    @Opaque public static class ReSampleContext extends Pointer {
        static { load(); }
        public ReSampleContext() { }
        public ReSampleContext(Pointer p) { super(p); }
    }
    @Opaque public static class AVResampleContext extends Pointer {
        static { load(); }
        public AVResampleContext() { }
        public AVResampleContext(Pointer p) { super(p); }
    }

//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated
//    public static native ReSampleContext audio_resample_init(int output_channels,
//            int input_channels, int output_rate, int input_rate);
//#endif
    public static native ReSampleContext av_audio_resample_init(int output_channels, int input_channels,
            int output_rate, int input_rate, @Cast("SampleFormat") int sample_fmt_out, @Cast("SampleFormat") int sample_fmt_in,
            int filter_length, int log2_phase_count, int linear, double cutoff);
    public static native int audio_resample(ReSampleContext s, short[] output, short[] input, int nb_samples);
    public static native int audio_resample(ReSampleContext s, ShortBuffer output, ShortBuffer input, int nb_samples);
    public static native void audio_resample_close(ReSampleContext s);

    public static native AVResampleContext av_resample_init(int out_rate, int in_rate, int filter_length,
            int log2_phase_count, int linear, double cutoff);
    public static native int av_resample(AVResampleContext c, short[] dst, short[] src,
            int[] consumed, int src_size, int dst_size, int update_ctx);
    public static native int av_resample(AVResampleContext c, ShortBuffer dst, ShortBuffer src,
            int[] consumed, int src_size, int dst_size, int update_ctx);
    public static native void av_resample_compensate(AVResampleContext c, int sample_delta, int compensation_distance);
    public static native void av_resample_close(AVResampleContext c);

    public static native int avpicture_alloc(AVPicture picture, @Cast("PixelFormat") int pix_fmt, int width, int height);
    public static native void avpicture_free(AVPicture picture);

    public static native int avpicture_fill(AVPicture picture, @Cast("uint8_t*") BytePointer ptr,
            @Cast("PixelFormat") int pix_fmt, int width, int height);
    public static native int avpicture_layout(AVPicture src, @Cast("PixelFormat") int pix_fmt,
            int width, int height, @Cast("unsigned char*") BytePointer dest, int dest_size);

    public static native int avpicture_get_size(@Cast("PixelFormat") int pix_fmt, int width, int height);
    public static native void avcodec_get_chroma_sub_sample(@Cast("PixelFormat") int pix_fmt, int[] h_shift, int[] v_shift);
    public static native String avcodec_get_pix_fmt_name(@Cast("PixelFormat") int pix_fmt);
//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated
//    public static native @Cast("PixelFormat") int avcodec_get_pix_fmt(String name);
//#endif
    public static native void avcodec_set_dimensions(AVCodecContext s, int width, int height);

    public static native int avcodec_pix_fmt_to_codec_tag(@Cast("PixelFormat") int pix_fmt);

    public static final int
            FF_LOSS_RESOLUTION = 0x0001,
            FF_LOSS_DEPTH      = 0x0002,
            FF_LOSS_COLORSPACE = 0x0004,
            FF_LOSS_ALPHA      = 0x0008,
            FF_LOSS_COLORQUANT = 0x0010,
            FF_LOSS_CHROMA     = 0x0020;
    public static native int avcodec_get_pix_fmt_loss(@Cast("PixelFormat") int dst_pix_fmt,
            @Cast("PixelFormat") int src_pix_fmt, int has_alpha);
    public static native @Cast("PixelFormat") int avcodec_find_best_pix_fmt(long pix_fmt_mask,
            @Cast("PixelFormat") int src_pix_fmt, int has_alpha, int[] loss_ptr);

    public static native void avcodec_pix_fmt_string (@Cast("char*") byte[] buf, int buf_size,
            @Cast("PixelFormat") int pix_fmt);
    public static final int
            FF_ALPHA_TRANSP      = 0x0001,
            FF_ALPHA_SEMI_TRANSP = 0x0002;

    public static native int img_get_alpha_info(AVPicture src,
            @Cast("PixelFormat") int pix_fmt, int width, int height);
    public static native int avpicture_deinterlace(AVPicture dst, AVPicture src,
            @Cast("PixelFormat") int pix_fmt, int width, int height);

    public static native AVCodec av_codec_next(AVCodec c);
    public static native int avcodec_version();
    public static native String avcodec_configuration();
    public static native String avcodec_license();
    public static native void avcodec_init();

//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated
//    public static native void register_avcodec(AVCodec codec);
//#endif
    public static native void avcodec_register(AVCodec codec);
    public static native AVCodec avcodec_find_encoder(@Cast("CodecID") int id);
    public static native AVCodec avcodec_find_encoder_by_name(String name);
    public static native AVCodec avcodec_find_decoder(@Cast("CodecID") int id);
    public static native AVCodec avcodec_find_decoder_by_name(String name);
    public static native void avcodec_string(@Cast("char*") byte[] buf, int buf_size, AVCodecContext enc, int encode);

    public static native void avcodec_get_context_defaults(AVCodecContext s);
    public static native void avcodec_get_context_defaults2(AVCodecContext s, @Cast("AVMediaType") int type);
    public static native AVCodecContext avcodec_alloc_context();
    public static native AVCodecContext avcodec_alloc_context2(@Cast("AVMediaType") int type);
    public static native int avcodec_copy_context(AVCodecContext dest, AVCodecContext src);
    public static native void avcodec_get_frame_defaults(AVFrame pic);
    public static native AVFrame avcodec_alloc_frame();

    public static native int avcodec_default_get_buffer(AVCodecContext s, AVFrame pic);
    public static native void avcodec_default_release_buffer(AVCodecContext s, AVFrame pic);
    public static native int avcodec_default_reget_buffer(AVCodecContext s, AVFrame pic);

    public static native int avcodec_get_edge_width();
    public static native void avcodec_align_dimensions(AVCodecContext s, int[] width, int[] height);
    public static native void avcodec_align_dimensions2(AVCodecContext s, int[] width, int[] height,
                               int linesize_align[/*4*/]);

    public static native int avcodec_check_dimensions(Pointer av_log_ctx, int w, int h);
    public static native @Cast("PixelFormat") int avcodec_default_get_format(AVCodecContext s, @Cast("PixelFormat*") int[] fmt);


    public static native int avcodec_thread_init(AVCodecContext s, int thread_count);
    public static native void avcodec_thread_free(AVCodecContext s);
    public static class Func extends FunctionPointer {
        static { load(); }
        public    Func(Pointer p) { super(p); }
        protected Func() { allocate(); }
        protected final native void allocate();
        public native int call(AVCodecContext c2, Pointer arg2);
    }
    public static native int avcodec_default_execute(AVCodecContext c, Func func, Pointer arg, int[] ret, int count, int size);
    public static class Func2 extends FunctionPointer {
        static { load(); }
        public    Func2(Pointer p) { super(p); }
        protected Func2() { allocate(); }
        protected final native void allocate();
        public native int call(AVCodecContext c2, Pointer arg2, int i1, int i2);
    }
    public static native int avcodec_default_execute2(AVCodecContext c, Func2 func2, Pointer arg, int[] ret, int count);


    public static native int avcodec_open(AVCodecContext avctx, AVCodec codec);

//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated
//    public static native int avcodec_decode_audio2(AVCodecContext avctx, short[] samples,
//            int[] frame_size_ptr, @Cast("uint8_t*") BytePointer buf, int buf_size);
//    @Deprecated
//    public static native int avcodec_decode_audio2(AVCodecContext avctx, ShortBuffer samples,
//            int[] frame_size_ptr, @Cast("uint8_t*") BytePointer buf, int buf_size);
//#endif
    public static native int avcodec_decode_audio3(AVCodecContext avctx, short[] samples,
            int[] frame_size_ptr, AVPacket avpkt);
    public static native int avcodec_decode_audio3(AVCodecContext avctx, ShortBuffer samples,
            int[] frame_size_ptr, AVPacket avpkt);

//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated
//    public static native int avcodec_decode_video(AVCodecContext avctx, AVFrame picture,
//            int[] got_picture_ptr, @Cast("uint8_t*") BytePointer buf, int buf_size);
//#endif
    public static native int avcodec_decode_video2(AVCodecContext avctx, AVFrame picture,
            int[] got_picture_ptr, AVPacket avpkt);

//    @Deprecated
//    public static native int avcodec_decode_subtitle(AVCodecContext avctx, AVSubtitle sub,
//            int[] got_sub_ptr, @Cast("uint8_t*") BytePointer buf, int buf_size);
    public static native int avcodec_decode_subtitle2(AVCodecContext avctx,
            AVSubtitle sub, int[] got_sub_ptr, AVPacket avpkt);
//    public static native int avcodec_parse_frame(AVCodecContext avctx,
//            @Cast("uint8_t**") PointerPointer pdata, int[] data_size_ptr,
//            @Cast("uint8_t*") BytePointer buf, int buf_size);

    public static native int avcodec_encode_audio(AVCodecContext avctx,
            @Cast("uint8_t*") BytePointer buf, int buf_size, short[] samples);
    public static native int avcodec_encode_audio(AVCodecContext avctx,
            @Cast("uint8_t*") BytePointer buf, int buf_size, ShortBuffer samples);
    public static native int avcodec_encode_audio(AVCodecContext avctx,
            @Cast("uint8_t*") BytePointer buf, int buf_size, ShortPointer samples);

    public static native int avcodec_encode_video(AVCodecContext avctx,
            @Cast("uint8_t*") BytePointer buf, int buf_size, AVFrame pict);
    public static native int avcodec_encode_subtitle(AVCodecContext avctx,
            @Cast("uint8_t*") BytePointer buf, int buf_size, AVSubtitle sub);

    public static native int avcodec_close(AVCodecContext avctx);

    public static native void avcodec_register_all();

    public static native void avcodec_flush_buffers(AVCodecContext avctx);
    public static native void avcodec_default_free_buffers(AVCodecContext s);

    public static native char av_get_pict_type_char(int pict_type);
    public static native int av_get_bits_per_sample(@Cast("CodecID") int codec_id);
    public static native int av_get_bits_per_sample_format(@Cast("SampleFormat") int sample_fmt);


    public static class AVCodecParserContext extends Pointer {
        static { load(); }
        public AVCodecParserContext() { allocate(); }
        public AVCodecParserContext(int size) { allocateArray(size); }
        public AVCodecParserContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVCodecParserContext position(int position) {
            return (AVCodecParserContext)super.position(position);
        }

        public native Pointer priv_data();      public native AVCodecParserContext priv_data(Pointer priv_data);
        public native AVCodecParser parser();   public native AVCodecParserContext parser(AVCodecParser parser);
        public native long frame_offset();      public native AVCodecParserContext frame_offset(long frame_offset);
        public native long cur_offset();        public native AVCodecParserContext cur_offset(long cur_offset);
        public native long next_frame_offset(); public native AVCodecParserContext next_frame_offset(long next_frame_offset);

        public native int pict_type();          public native AVCodecParserContext pict_type(int pict_type);
        public native int repeat_pict();        public native AVCodecParserContext repeat_pict(int repeat_pict);
        public native long pts();               public native AVCodecParserContext pts(long pts);
        public native long dts();               public native AVCodecParserContext dts(long dts);

        public native long last_pts();          public native AVCodecParserContext last_pts(long last_pts);
        public native long last_dts();          public native AVCodecParserContext last_dts(long last_dts);
        public native int fetch_timestamp();    public native AVCodecParserContext fetch_timestamp(int fetch_timestamp);

        public static final int AV_PARSER_PTS_NB = 4;
        public native int cur_frame_start_index();  public native AVCodecParserContext cur_frame_start_index(int cur_frame_start_index);
        public native long cur_frame_offset(int i); public native AVCodecParserContext cur_frame_offset(int i, long cur_frame_offset);
        public native long cur_frame_pts(int i);    public native AVCodecParserContext cur_frame_pts(int i, long cur_frame_pts);
        public native long cur_frame_dts(int i);    public native AVCodecParserContext cur_frame_dts(int i, long cur_frame_dts);

        public native int flags();                  public native AVCodecParserContext flags(int flags);
        public static final int PARSER_FLAG_COMPLETE_FRAMES = 0x0001;

        public native long offset();                public native AVCodecParserContext offset(long offset);
        public native long cur_frame_end(int i);    public native AVCodecParserContext cur_frame_end(int i, long cur_frame_end);

        public native int key_frame();              public native AVCodecParserContext key_frame(int key_frame);
        public native long convergence_duration();  public native AVCodecParserContext convergence_duration(long convergence_duration);

        public native int dts_sync_point();         public native AVCodecParserContext dts_sync_point(int dts_sync_point);
        public native int dts_ref_dts_delta();      public native AVCodecParserContext dts_ref_dts_delta(int dts_ref_dts_delta);
        public native int pts_dts_delta();          public native AVCodecParserContext pts_dts_delta(int pts_dts_delta);

        public native long cur_frame_pos(int i);    public native AVCodecParserContext cur_frame_pos(int i, long cur_frame_pos);
        public native long pos();                   public native AVCodecParserContext pos(long pos);
        public native long last_pos();              public native AVCodecParserContext last_pos(long last_pos);
    }

    public static class AVCodecParser extends Pointer {
        static { load(); }
        public AVCodecParser() { allocate(); }
        public AVCodecParser(int size) { allocateArray(size); }
        public AVCodecParser(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVCodecParser position(int position) {
            return (AVCodecParser)super.position(position);
        }

        public native int/*[5]*/ codec_ids(int i); public native AVCodecParser codec_ids(int i, int codec_id);
        public native int priv_data_size();        public native AVCodecParser priv_data_size(int priv_data_size);

        public static class Parser_init extends FunctionPointer {
            static { load(); }
            public    Parser_init(Pointer p) { super(p); }
            protected Parser_init() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecParserContext s);
        }
        public native Parser_init parser_init();   public native AVCodecParser parser_init(Parser_init parser_init);

        public static class Parser_parse extends FunctionPointer {
            static { load(); }
            public    Parser_parse(Pointer p) { super(p); }
            protected Parser_parse() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecParserContext s, AVCodecContext avctx,
                    @Cast("const uint8_t**") PointerPointer poutbuf, IntPointer poutbuf_size,
                    @Cast("const uint8_t*") BytePointer buf, int buf_size);
        }
        public native Parser_parse parser_parse(); public native AVCodecParser parser_parse(Parser_parse parser_parse);

        public static class Parser_close extends FunctionPointer {
            static { load(); }
            public    Parser_close(Pointer p) { super(p); }
            protected Parser_close() { allocate(); }
            protected final native void allocate();
            public native void call(AVCodecParserContext s);
        }
        public native Parser_close parser_close(); public native AVCodecParser parser_close(Parser_close parser_close);

        public static class Split extends FunctionPointer {
            static { load(); }
            public    Split(Pointer p) { super(p); }
            protected Split() { allocate(); }
            protected final native void allocate();
            public native int call(AVCodecContext avctx, @Cast("const uint8_t*") BytePointer buf, int buf_size);
        }
        public native Split split();               public native AVCodecParser split(Split split);

        public native AVCodecParser next();        public native AVCodecParser next(AVCodecParser next);
    }

    public static native AVCodecParser av_parser_next(AVCodecParser c);

    public static native void av_register_codec_parser(AVCodecParser parser);
    public static native AVCodecParserContext av_parser_init(int codec_id);

//#if LIBAVCODEC_VERSION_MAJOR < 53
//    @Deprecated
//    public static native int av_parser_parse(AVCodecParserContext s, AVCodecContext avctx,
//            @Cast("uint8_t**") PointerPointer poutbuf, int[] poutbuf_size,
//            @Cast("uint8_t*") BytePointer buf, int buf_size, long pts, long dts);
//#endif
    public static native int av_parser_parse2(AVCodecParserContext s,
            AVCodecContext avctx, @Cast("uint8_t**") PointerPointer poutbuf, int[] poutbuf_size,
            @Cast("uint8_t*") BytePointer buf, int buf_size, long pts, long dts, long pos);

    public static native int av_parser_change(AVCodecParserContext s,
            AVCodecContext avctx, @Cast("uint8_t**") PointerPointer poutbuf, int[] poutbuf_size,
            @Cast("uint8_t*") BytePointer buf, int buf_size, int keyframe);
    public static native void av_parser_close(AVCodecParserContext s);


    public static class AVBitStreamFilterContext extends Pointer {
        static { load(); }
        public AVBitStreamFilterContext() { allocate(); }
        public AVBitStreamFilterContext(int size) { allocateArray(size); }
        public AVBitStreamFilterContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVBitStreamFilterContext position(int position) {
            return (AVBitStreamFilterContext)super.position(position);
        }

        public native Pointer priv_data();             public native AVBitStreamFilterContext priv_data(Pointer priv_data);
        public native AVBitStreamFilter filter();      public native AVBitStreamFilterContext filter(AVBitStreamFilter filter);
        public native AVCodecParserContext parser();   public native AVBitStreamFilterContext parser(AVCodecParserContext parser);
        public native AVBitStreamFilterContext next(); public native AVBitStreamFilterContext next(AVBitStreamFilterContext next);
    }


    public static class AVBitStreamFilter extends Pointer {
        static { load(); }
        public AVBitStreamFilter() { allocate(); }
        public AVBitStreamFilter(int size) { allocateArray(size); }
        public AVBitStreamFilter(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVBitStreamFilter position(int position) {
            return (AVBitStreamFilter)super.position(position);
        }

        @Cast("const char *")
        public native BytePointer name();   public native AVBitStreamFilter name(BytePointer name);
        public native int priv_data_size(); public native AVBitStreamFilter priv_data_size(int priv_data_size);

        public static class Filter extends FunctionPointer {
            static { load(); }
            public    Filter(Pointer p) { super(p); }
            protected Filter() { allocate(); }
            protected final native void allocate();
            public native int call(AVBitStreamFilterContext bsfc, AVCodecContext avctx,
                    String args, @Cast("uint8_t**") PointerPointer poutbuf, IntPointer poutbuf_size,
                    @Cast("const uint8_t*") BytePointer buf, int buf_size, int keyframe);
        }
        public native Filter filter();      public native AVBitStreamFilter filter(Filter filter);

        public static class Close extends FunctionPointer {
            static { load(); }
            public    Close(Pointer p) { super(p); }
            protected Close() { allocate(); }
            protected final native void allocate();
            public native void call(AVBitStreamFilterContext bsfc);
        }
        public native Close close();        public native AVBitStreamFilter close(Close close);

        public native AVBitStreamFilter next(); public native AVBitStreamFilter next(AVBitStreamFilter next);
    }

    public static native void av_register_bitstream_filter(AVBitStreamFilter bsf);
    public static native AVBitStreamFilterContext av_bitstream_filter_init(String name);
    public static native int av_bitstream_filter_filter(AVBitStreamFilterContext bsfc,
            AVCodecContext avctx, String args, @Cast("uint8_t**") PointerPointer poutbuf, int[] poutbuf_size,
            @Cast("uint8_t*") BytePointer buf, int buf_size, int keyframe);
    public static native void av_bitstream_filter_close(AVBitStreamFilterContext bsf);

    public static native AVBitStreamFilter av_bitstream_filter_next(AVBitStreamFilter f);

    public static native Pointer av_fast_realloc(Pointer ptr, @Cast("unsigned int*") int[] size, int min_size);
    public static native void av_fast_malloc(Pointer ptr, @Cast("unsigned int*") int[] size, int min_size);

    public static native void av_picture_copy(AVPicture dst, AVPicture src,
            @Cast("PixelFormat") int pix_fmt, int width, int height);
    public static native int av_picture_crop(AVPicture dst, AVPicture src,
            @Cast("PixelFormat") int pix_fmt, int top_band, int left_band);
    public static native int av_picture_pad(AVPicture dst, AVPicture src, int height, int width,
            @Cast("PixelFormat") int pix_fmt, int padtop, int padbottom, int padleft, int padright,
            int[] color);

    public static native int av_xiphlacing(@Cast("unsigned char*") BytePointer s, int v);

    public static native int av_parse_video_frame_size(int[] width_ptr, int[] height_ptr, String str);
    public static native int av_parse_video_frame_rate(AVRational frame_rate, String str);

    public static native void av_log_missing_feature(Pointer avc, String feature, int want_sample);
    public static native void av_log_ask_for_sample(Pointer avc, String msg);

    public static native void av_register_hwaccel(AVHWAccel hwaccel);
    public static native AVHWAccel av_hwaccel_next(AVHWAccel hwaccel);

    //enum AVLockOp {
    public static final int
            AV_LOCK_CREATE = 0,
            AV_LOCK_OBTAIN = 1,
            AV_LOCK_RELEASE = 2,
            AV_LOCK_DESTROY = 3;
    public static class Cb extends FunctionPointer {
        static { load(); }
        public    Cb(Pointer p) { super(p); }
        protected Cb() { allocate(); }
        protected final native void allocate();
        public native int call(PointerPointer mutex, @Cast("AVLockOp") int op);
    }
    public static native int av_lockmgr_register(Cb cb);


    //#include "opt.h"

    //enum AVOptionType{
    public static final int
            FF_OPT_TYPE_FLAGS    = 0,
            FF_OPT_TYPE_INT      = 1,
            FF_OPT_TYPE_INT64    = 2,
            FF_OPT_TYPE_DOUBLE   = 3,
            FF_OPT_TYPE_FLOAT    = 4,
            FF_OPT_TYPE_STRING   = 5,
            FF_OPT_TYPE_RATIONAL = 6,
            FF_OPT_TYPE_BINARY   = 7,
            FF_OPT_TYPE_CONST    = 128;

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

        @Cast("const char *")
        public native BytePointer name();   public native AVOption name(BytePointer name);
        @Cast("const char *")
        public native BytePointer help();   public native AVOption help(BytePointer help);
        public native int offset();         public native AVOption offset(int offset);
        @Cast("AVOptionType")
        public native int type();           public native AVOption type(int type);
        public native double default_val(); public native AVOption default_val(double default_val);
        public native double min();         public native AVOption min(double min);
        public native double max();         public native AVOption max(double max);

        public native int flags();          public native AVOption flags(int flags);
        public static final int
                AV_OPT_FLAG_ENCODING_PARAM = 1,
                AV_OPT_FLAG_DECODING_PARAM = 2,
                AV_OPT_FLAG_METADATA       = 4,
                AV_OPT_FLAG_AUDIO_PARAM    = 8,
                AV_OPT_FLAG_VIDEO_PARAM    = 16,
                AV_OPT_FLAG_SUBTITLE_PARAM = 32;

        @Cast("const char *")
        public native BytePointer unit();   public native AVOption unit(BytePointer unit);
    }

    public static class AVOption2 extends Pointer {
        static { load(); }
        public AVOption2() { allocate(); }
        public AVOption2(int size) { allocateArray(size); }
        public AVOption2(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVOption2 position(int position) {
            return (AVOption2)super.position(position);
        }

        @Cast("const char *")
        public native BytePointer name();   public native AVOption2 name(BytePointer name);
        @Cast("const char *")
        public native BytePointer help();   public native AVOption2 help(BytePointer help);
        public native int offset();         public native AVOption2 offset(int offset);
        @Cast("AVOptionType")
        public native int type();           public native AVOption2 type(int type);

        // union { } default_val
        @Name("default_val.dbl") public native double      default_val_dbl();
                                 public native AVOption2    default_val_dbl(double default_val_dbl);
        @Name("default_val.str") @Cast("const char*")
                                 public native BytePointer default_val_str();
                                 public native AVOption2    default_val_str(BytePointer default_val_str);

        public native double min();         public native AVOption2 min(double min);
        public native double max();         public native AVOption2 max(double max);

        public native int flags();          public native AVOption2 flags(int flags);

        @Cast("const char *")
        public native BytePointer unit();   public native AVOption2 unit(BytePointer unit);
    }

    public static native @Const AVOption av_find_opt(Pointer obj, String name, String unit, int mask, int flags);
//    @Deprecated
//    public static native @Const AVOption av_set_string(Pointer obj, String name, String val);
//    @Deprecated
//    public static native @Const AVOption av_set_string2(Pointer obj, String name, String val, int alloc);
    public static native int av_set_string3(Pointer obj, String name, String val, int alloc, @Const @ByPtrPtr AVOption o_out);
    public static native @Const AVOption av_set_double(Pointer obj, String name, double n);
    public static native @Const AVOption av_set_q(Pointer obj, String name, @ByVal AVRational n);
    public static native @Const AVOption av_set_int(Pointer obj, String name, long n);
    public static native double av_get_double(Pointer obj, String name, @Const @ByPtrPtr AVOption o_out);
    public static native @ByVal AVRational av_get_q(Pointer obj, String name, @Const @ByPtrPtr AVOption o_out);
    public static native long av_get_int(Pointer obj, String name, @Const @ByPtrPtr AVOption o_out);
    public static native String av_get_string(Pointer obj, String name, @Const @ByPtrPtr AVOption o_out,
            @Cast("char*") byte[] buf, int buf_len);
    public static native @Const AVOption av_next_option(Pointer obj, AVOption last);
    public static native int av_opt_show(Pointer obj, Pointer av_log_obj);
    public static native void av_opt_set_defaults(Pointer s);
    public static native void av_opt_set_defaults2(Pointer s, int mask, int flags);

    //#include "avfft.h"
    //typedef float FFTSample;

    public static class FFTComplex extends Pointer {
        static { load(); }
        public FFTComplex() { allocate(); }
        public FFTComplex(int size) { allocateArray(size); }
        public FFTComplex(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public FFTComplex position(int position) {
            return (FFTComplex)super.position(position);
        }

        /* FFTSample */
        public native float re(); public native FFTComplex re(float re);
        public native float im(); public native FFTComplex im(float im);
    }

    @Opaque public static class FFTContext extends Pointer {
        static { load(); }
        public FFTContext() { }
        public FFTContext(Pointer p) { super(p); }
    }

    public static native FFTContext av_fft_init(int nbits, int inverse);
    public static native void av_fft_permute(FFTContext s, FFTComplex z);
    public static native void av_fft_calc(FFTContext s, FFTComplex z);
    public static native void av_fft_end(FFTContext s);
    public static native FFTContext av_mdct_init(int nbits, int inverse, double scale);

    public static native void av_imdct_calc(FFTContext s, float[]      /* FFTSample* */ output, float[]      /* FFTSample* */ input);
    public static native void av_imdct_calc(FFTContext s, FloatBuffer  /* FFTSample* */ output, FloatBuffer  /* FFTSample* */ input);
    public static native void av_imdct_calc(FFTContext s, FloatPointer /* FFTSample* */ output, FloatPointer /* FFTSample* */ input);
    public static native void av_imdct_half(FFTContext s, float[]      /* FFTSample* */ output, float[]      /* FFTSample* */ input);
    public static native void av_imdct_half(FFTContext s, FloatBuffer  /* FFTSample* */ output, FloatBuffer  /* FFTSample* */ input);
    public static native void av_imdct_half(FFTContext s, FloatPointer /* FFTSample* */ output, FloatPointer /* FFTSample* */ input);
    public static native void av_mdct_calc (FFTContext s, float[]      /* FFTSample* */ output, float[]      /* FFTSample* */ input);
    public static native void av_mdct_calc (FFTContext s, FloatBuffer  /* FFTSample* */ output, FloatBuffer  /* FFTSample* */ input);
    public static native void av_mdct_calc (FFTContext s, FloatPointer /* FFTSample* */ output, FloatPointer /* FFTSample* */ input);
    public static native void av_mdct_end  (FFTContext s);


    //enum RDFTransformType {
    public static final int
            DFT_R2C  = 0,
            IDFT_C2R = 1,
            IDFT_R2C = 2,
            DFT_C2R  = 3;

    @Opaque public static class RDFTContext extends Pointer {
        static { load(); }
        public RDFTContext() { }
        public RDFTContext(Pointer p) { super(p); }
    }

    public static native RDFTContext av_rdft_init(int nbits, @Cast("RDFTransformType") int trans);
    public static native void av_rdft_calc(RDFTContext s, float[]      /* FFTSample* */ data);
    public static native void av_rdft_calc(RDFTContext s, FloatBuffer  /* FFTSample* */ data);
    public static native void av_rdft_calc(RDFTContext s, FloatPointer /* FFTSample* */ data);
    public static native void av_rdft_end (RDFTContext s);


    @Opaque public static class DCTContext extends Pointer {
        static { load(); }
        public DCTContext() { }
        public DCTContext(Pointer p) { super(p); }
    }

    //enum DCTTransformType {
    public static final int
        DCT_II  = 0,
        DCT_III = 1,
        DCT_I   = 2,
        DST_I   = 3;

    public static native DCTContext av_dct_init(int nbits, @Cast("DCTTransformType") int type);
    public static native void av_dct_calc(DCTContext s, float[]      /* FFTSample* */ data);
    public static native void av_dct_calc(DCTContext s, FloatBuffer  /* FFTSample* */ data);
    public static native void av_dct_calc(DCTContext s, FloatPointer /* FFTSample* */ data);
    public static native void av_dct_end (DCTContext s);


    //#include "dxva2.h"
    @Platform("windows") @Opaque public static class IDirectXVideoDecoder extends Pointer {
        static { load(); }
        public IDirectXVideoDecoder() { }
        public IDirectXVideoDecoder(Pointer p) { super(p); }
    }
    @Platform("windows") @Opaque public static class DXVA2_ConfigPictureDecode extends Pointer {
        static { load(); }
        public DXVA2_ConfigPictureDecode() { }
        public DXVA2_ConfigPictureDecode(Pointer p) { super(p); }
    }
    @Platform("windows") @Opaque public static class LPDIRECT3DSURFACE9 extends Pointer {
        static { load(); }
        public LPDIRECT3DSURFACE9() { }
        public LPDIRECT3DSURFACE9(Pointer p) { super(p); }
    }

    @Platform("windows") public static class dxva_context extends Pointer {
        static { load(); }
        public dxva_context() { allocate(); }
        public dxva_context(int size) { allocateArray(size); }
        public dxva_context(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public dxva_context position(int position) {
            return (dxva_context)super.position(position);
        }

        public native IDirectXVideoDecoder decoder();  public native dxva_context decoder(IDirectXVideoDecoder decoder);
        @Const
        public native DXVA2_ConfigPictureDecode cfg(); public native dxva_context cfg(DXVA2_ConfigPictureDecode cfg);
        public native int surface_count();             public native dxva_context surface_count(int surface_count);
        public native LPDIRECT3DSURFACE9 surface();    public native dxva_context surface(LPDIRECT3DSURFACE9 surface);
        public native long workaround();               public native dxva_context workaround(long workaround);
        public native int report_id();                 public native dxva_context report_id(int report_id);
    }

    //#include "vaapi.h"
    @Platform({"linux", "freebsd", "solaris", "sunos"})
    public static class vaapi_context extends Pointer {
        static { load(); }
        public vaapi_context() { allocate(); }
        public vaapi_context(int size) { allocateArray(size); }
        public vaapi_context(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public vaapi_context position(int position) {
            return (vaapi_context)super.position(position);
        }

        public native Pointer display();          public native vaapi_context display(Pointer display);
        public native int config_id();            public native vaapi_context config_id(int config_id);
        public native int context_id();           public native vaapi_context context_id(int context_id);
        public native int pic_param_buf_id();     public native vaapi_context pic_param_buf_id(int pic_param_buf_id);
        public native int iq_matrix_buf_id();     public native vaapi_context iq_matrix_buf_id(int iq_matrix_buf_id);
        public native int bitplane_buf_id();      public native vaapi_context bitplane_buf_id(int bitplane_buf_id);
        @Cast("uint32_t*")
        public native IntPointer slice_buf_ids(); public native vaapi_context slice_buf_ids(IntPointer slice_buf_ids);
        public native int n_slice_buf_ids();      public native vaapi_context n_slice_buf_ids(int n_slice_buf_ids);
        public native int slice_buf_ids_alloc();  public native vaapi_context slice_buf_ids_alloc(int slice_buf_ids_alloc);
        public native Pointer slice_params();     public native vaapi_context slice_params(Pointer slice_params);
        public native int slice_param_size();     public native vaapi_context slice_param_size(int slice_param_size);
        public native int slice_params_alloc();   public native vaapi_context slice_params_alloc(int slice_params_alloc);
        public native int slice_count();          public native vaapi_context slice_count(int slice_count);
        @Cast("const uint8_t *")
        public native BytePointer slice_data();   public native vaapi_context slice_data(BytePointer slice_data);
        public native int slice_data_size();      public native vaapi_context slice_data_size(int slice_data_size);
    }

    //#include "vdpau.h"
    public static final int
            FF_VDPAU_STATE_USED_FOR_RENDER = 1,
            FF_VDPAU_STATE_USED_FOR_REFERENCE = 2;

    @Platform({"linux", "freebsd", "solaris", "sunos"})
    @Opaque public static class VdpBitstreamBuffer extends Pointer {
        static { load(); }
        public VdpBitstreamBuffer() { }
        public VdpBitstreamBuffer(Pointer p) { super(p); }
    }
    @Platform({"linux", "freebsd", "solaris", "sunos"})
    public static class vdpau_render_state extends Pointer {
        static { load(); }
        public vdpau_render_state() { allocate(); }
        public vdpau_render_state(int size) { allocateArray(size); }
        public vdpau_render_state(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public vdpau_render_state position(int position) {
            return (vdpau_render_state)super.position(position);
        }

        @Cast("VdpVideoSurface")
        public native int surface();         public native vdpau_render_state surface(int surface);
        public native int state();           public native vdpau_render_state state(int state);

        @Opaque public static class VdpPictureInfo extends Pointer {
            static { load(); }
            public VdpPictureInfo() { }
            public VdpPictureInfo(Pointer p) { super(p); }
        }
        @ByRef public native VdpPictureInfo info(); public native vdpau_render_state info(VdpPictureInfo info);

        public native int bitstream_buffers_allocated();      public native vdpau_render_state bitstream_buffers_allocated(int bitstream_buffers_allocated);
        public native int bitstream_buffers_used();           public native vdpau_render_state bitstream_buffers_used(int bitstream_buffers_used);

        public native VdpBitstreamBuffer bitstream_buffers(); public native vdpau_render_state bitstream_buffers(VdpBitstreamBuffer bitstream_buffers);
    }

    //#include "xvmc.h"
//#if LIBAVCODEC_VERSION_MAJOR < 53
    public static final int
            AV_XVMC_STATE_DISPLAY_PENDING         = 1,
            AV_XVMC_STATE_PREDICTION              = 2,
            AV_XVMC_STATE_OSD_SOURCE              = 4,
//#endif
            AV_XVMC_ID                   = 0x1DC711C0;

    @Platform({"linux", "freebsd", "solaris", "sunos"})
    @Opaque public static class XvMCMacroBlock extends Pointer {
        static { load(); }
        public XvMCMacroBlock() { }
        public XvMCMacroBlock(Pointer p) { super(p); }
    }
    @Platform({"linux", "freebsd", "solaris", "sunos"})
    @Opaque public static class XvMCSurface extends Pointer {
        static { load(); }
        public XvMCSurface() { }
        public XvMCSurface(Pointer p) { super(p); }
    }

    @Platform({"linux", "freebsd", "solaris", "sunos"})
    public static class xvmc_pix_fmt extends Pointer {
        static { load(); }
        public xvmc_pix_fmt() { allocate(); }
        public xvmc_pix_fmt(int size) { allocateArray(size); }
        public xvmc_pix_fmt(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public xvmc_pix_fmt position(int position) {
            return (xvmc_pix_fmt)super.position(position);
        }

        public native int xvmc_id();                  public native xvmc_pix_fmt xvmc_id(int xvmc_id);
        public native ShortPointer data_blocks();     public native xvmc_pix_fmt data_blocks(ShortPointer data_blocks);
        public native XvMCMacroBlock mv_blocks();     public native xvmc_pix_fmt mv_blocks(XvMCMacroBlock mv_blocks);
        public native int allocated_mv_blocks();      public native xvmc_pix_fmt allocated_mv_blocks(int allocated_mv_blocks);
        public native int allocated_data_blocks();    public native xvmc_pix_fmt allocated_data_blocks(int allocated_data_blocks);
        public native int idct();                     public native xvmc_pix_fmt idct(int idct);
        public native int unsigned_intra();           public native xvmc_pix_fmt unsigned_intra(int unsigned_intra);
        public native XvMCSurface p_surface();        public native xvmc_pix_fmt p_surface(XvMCSurface p_surface);

        public native XvMCSurface p_past_surface();   public native xvmc_pix_fmt p_past_surface(XvMCSurface p_past_surface);
        public native XvMCSurface p_future_surface(); public native xvmc_pix_fmt p_future_surface(XvMCSurface p_future_surface);
        public native int picture_structure();        public native xvmc_pix_fmt picture_structure(int picture_structure);
        public native int flags();                    public native xvmc_pix_fmt flags(int flags);

        public native int start_mv_blocks_num();      public native xvmc_pix_fmt start_mv_blocks_num(int start_mv_blocks_num);
        public native int filled_mv_blocks_num();     public native xvmc_pix_fmt filled_mv_blocks_num(int filled_mv_blocks_num);
        public native int next_free_data_block_num(); public native xvmc_pix_fmt next_free_data_block_num(int next_free_data_block_num);

//#if LIBAVCODEC_VERSION_MAJOR < 53
        public native int state();                    public native xvmc_pix_fmt state(int state);
        public native Pointer p_osd_target_surface_render();
        public native xvmc_pix_fmt p_osd_target_surface_render(Pointer p_osd_target_surface_render);
//#endif
    }
}
