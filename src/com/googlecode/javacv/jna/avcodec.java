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

package com.googlecode.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.googlecode.javacv.jna.avutil.*;

/**
 *
 * @author Samuel Audet
 */
public class avcodec {
    public static final String[] paths = avutil.paths;
    public static final String[] libnames = { "avcodec", "avcodec-52" };
    public static final String libname = Loader.load(paths, libnames);

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
    public static final AVRational.ByValue AV_TIME_BASE_Q = new AVRational.ByValue(1, AV_TIME_BASE);


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

    public static class RcOverride extends Structure {
        public RcOverride() { }
        public RcOverride(Pointer m) { super(m); read(); }

        public int start_frame;
        public int end_frame;
        public int qscale;
        public float quality_factor;

        public static class ByReference extends RcOverride implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
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

    public static class AVPanScan extends Structure {
        public AVPanScan() { }
        public AVPanScan(Pointer m) { super(m); read(); }

        public int id;

        public int width;
        public int height;

        //int16_t position[3][2];
        public short position00, position01, position10,
                     position11, position20, position21;

        public static class ByReference extends AVPanScan implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVFrame extends AVPicture {
        public AVFrame() { }
        public AVFrame(Pointer m) { super(m); read(); }

        public Pointer base0, base1, base2, base3;
        public int key_frame;
        public int pict_type;
        public long pts;
        public int coded_picture_number;
        public int display_picture_number;
        public int quality;
        public int age;
        public int reference;
        public ByteByReference qscale_table;
        public int qstride;
        public ByteByReference mbskip_table;
        //public int16_t (*motion_val[2])[2];
        public PointerByReference motion_val0, motion_val1;
        public IntByReference mb_type;
        public byte motion_subsample_log2;
        public Pointer opaque;
        public long error0, error1, error2, error3;
        public int type;
        public int repeat_pict;
        public int qscale_type;
        public int interlaced_frame;
        public int top_field_first;
        public AVPanScan.ByReference pan_scan;
        public int palette_has_changed;
        public int buffer_hints;
        public ShortByReference dct_coeff;
        public ByteByReference ref_index0, ref_index1;
        public long reordered_opaque;
        public Pointer hwaccel_picture_private;

        public static class ByReference extends AVFrame implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
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

    public static class AVPacket extends Structure {
        public long  pts;

        public long dts;
        public Pointer data;
        public int   size;
        public int   stream_index;
        public int   flags;

        public int   duration;
        public interface Destruct extends Callback {
            void callback(AVPacket p);
        }
        public Destruct destruct;
        public Pointer priv;
        public long  pos;

        public long convergence_duration;
    }

    public static final int
            AV_PKT_FLAG_KEY  = 0x0001,
//#if LIBAVCODEC_VERSION_MAJOR < 53
            PKT_FLAG_KEY = AV_PKT_FLAG_KEY;
//#endif


    public static class AVCodecContext extends Structure {
        public AVCodecContext() { }
        public AVCodecContext(Pointer m) { useMemory(m); read(); }

        public AVClass.ByReference av_class;
        public int bit_rate;
        public int bit_rate_tolerance;
        public int flags;
        public int sub_id;
        public int me_method;

        public Pointer extradata;
        public int extradata_size;

        public AVRational time_base;
        public int width, height;

        public static final int FF_ASPECT_EXTENDED = 15;

        public int gop_size;
        public int /* enum PixelFormat */ pix_fmt;
        public int rate_emu;

        public interface Draw_horiz_band extends Callback {
            void callback(AVCodecContext s, AVFrame src, int offset[/*4*/], int y, int type, int height);
        }
        public Draw_horiz_band draw_horiz_band;

        public int sample_rate;
        public int channels;
        public int /* enum SampleFormat */ sample_fmt;

        public int frame_size;
        public int frame_number;
//#if LIBAVCODEC_VERSION_MAJOR < 53
        public int real_pict_num;
//#endif

        public int delay;
        public float qcompress;  ///< amount of qscale change between easy & hard scenes (0.0-1.0)
        public float qblur;      ///< amount of qscale smoothing over time (0.0-1.0)
        public int qmin;
        public int qmax;
        public int max_qdiff;
        public int max_b_frames;
        public float b_quant_factor;

        public int rc_strategy;
        public static final int FF_RC_STRATEGY_XVID = 1;

        public int b_frame_strategy;

        @Deprecated public int hurry_up;
        public AVCodec.ByReference codec;
        public Pointer priv_data;
        public int rtp_payload_size;

        public interface Rtp_callback extends Callback {
            void callback(AVCodecContext avctx, Pointer data, int size, int mb_nb);
        }
        public Rtp_callback rtp_callback;

        public int mv_bits;
        public int header_bits;
        public int i_tex_bits;
        public int p_tex_bits;
        public int i_count;
        public int p_count;
        public int skip_count;
        public int misc_bits;

        public int frame_bits;

        public Pointer opaque;

        public byte[] codec_name = new byte[32];
        public int /* enum AVMediaType */ codec_type;
        public int /* enum CodecID */ codec_id;

        public int codec_tag;

        public int workaround_bugs;
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

        public int luma_elim_threshold;
        public int chroma_elim_threshold;

        public int strict_std_compliance;
        public static final int
            FF_COMPLIANCE_VERY_STRICT  =  2,
            FF_COMPLIANCE_STRICT       =  1,
            FF_COMPLIANCE_NORMAL       =  0,
            FF_COMPLIANCE_INOFFICIAL   = -1,
            FF_COMPLIANCE_EXPERIMENTAL = -2;

        public float b_quant_offset;

        public int error_recognition;
        public static final int
            FF_ER_CAREFUL         = 1,
            FF_ER_COMPLIANT       = 2,
            FF_ER_AGGRESSIVE      = 3,
            FF_ER_VERY_AGGRESSIVE = 4;

        public interface Get_buffer extends Callback {
           int callback(AVCodecContext c, AVFrame pic);
        }
        public Get_buffer get_buffer;
        public interface Release_buffer extends Callback {
           void callback(AVCodecContext c, AVFrame pic);
        }
        public Release_buffer release_buffer;

        public int has_b_frames;
        public int block_align;
        public int parse_only;
        public int mpeg_quant;

        public String stats_out;
        public String stats_in;

        public float rc_qsquish;

        public float rc_qmod_amp;
        public int rc_qmod_freq;

        public RcOverride.ByReference rc_override;
        public int rc_override_count;

        public String rc_eq;
        public int rc_max_rate;
        public int rc_min_rate;

        public int rc_buffer_size;
        public float rc_buffer_aggressivity;

        public float i_quant_factor;
        public float i_quant_offset;
        public float rc_initial_cplx;

        public int dct_algo;
        public static final int
                FF_DCT_AUTO    = 0,
                FF_DCT_FASTINT = 1,
                FF_DCT_INT     = 2,
                FF_DCT_MMX     = 3,
                FF_DCT_MLIB    = 4,
                FF_DCT_ALTIVEC = 5,
                FF_DCT_FAAN    = 6;

        public float lumi_masking;
        public float temporal_cplx_masking;
        public float spatial_cplx_masking;
        public float p_masking;
        public float dark_masking;

        public int idct_algo;
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

        public int slice_count;
        public IntByReference slice_offset;

        public int error_concealment;
        public static final int
                FF_EC_GUESS_MVS  = 1,
                FF_EC_DEBLOCK    = 2;

        public int dsp_mask;
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

        public int bits_per_coded_sample;

        public int prediction_method;
        public static final int
            FF_PRED_LEFT   = 0,
            FF_PRED_PLANE  = 1,
            FF_PRED_MEDIAN = 2;

        public AVRational sample_aspect_ratio;

        public AVFrame.ByReference coded_frame;

        public int debug;
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

        public int debug_mv;
        public static final int
                FF_DEBUG_VIS_MV_P_FOR  = 0x00000001,
                FF_DEBUG_VIS_MV_B_FOR  = 0x00000002,
                FF_DEBUG_VIS_MV_B_BACK = 0x00000004;

        public long error0, error1, error2, error3;
        public int mb_qmin;
        public int mb_qmax;

        public int me_cmp;
        public int me_sub_cmp;
        public int mb_cmp;
        public int ildct_cmp;
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

        public int dia_size;
        public int last_predictor_count;
        public int pre_me;
        public int me_pre_cmp;
        public int pre_dia_size;
        public int me_subpel_quality;

        public interface Get_format extends Callback {
            int /* enum PixelFormat */ callback(AVCodecContext s, IntByReference /* enum PixelFormat* */ fmt);
        }
        public Get_format get_format;

        public int dtg_active_format;
        public static final int
                FF_DTG_AFD_SAME         = 8,
                FF_DTG_AFD_4_3          = 9,
                FF_DTG_AFD_16_9         = 10,
                FF_DTG_AFD_14_9         = 11,
                FF_DTG_AFD_4_3_SP_14_9  = 13,
                FF_DTG_AFD_16_9_SP_14_9 = 14,
                FF_DTG_AFD_SP_4_3       = 15;

        public int me_range;

        public int intra_quant_bias;
        public static final int FF_DEFAULT_QUANT_BIAS = 999999;

        public int inter_quant_bias;
        public int color_table_id;

        public int internal_buffer_count;
        public Pointer internal_buffer;

        public static final int
                FF_LAMBDA_SHIFT = 7,
                FF_LAMBDA_SCALE = (1<<FF_LAMBDA_SHIFT),
                FF_QP2LAMBDA    = 118,
                FF_LAMBDA_MAX   = (256*128-1),

                FF_QUALITY_SCALE = FF_LAMBDA_SCALE; 

        public int global_quality;
        public static final int
                FF_CODER_TYPE_VLC      = 0,
                FF_CODER_TYPE_AC       = 1,
                FF_CODER_TYPE_RAW      = 2,
                FF_CODER_TYPE_RLE      = 3,
                FF_CODER_TYPE_DEFLATE  = 4;

        public int coder_type;
        public int context_model;

//#if 0
//    uint8_t * (*realloc)(struct AVCodecContext *s, uint8_t *buf, int buf_size);
//#endif

        public int slice_flags;
        public static final int
                SLICE_FLAG_CODED_ORDER   = 0x0001,
                SLICE_FLAG_ALLOW_FIELD   = 0x0002,
                SLICE_FLAG_ALLOW_PLANE   = 0x0004;

        public int xvmc_acceleration;

        public int mb_decision;
        public static final int
                FF_MB_DECISION_SIMPLE = 0,
                FF_MB_DECISION_BITS   = 1,
                FF_MB_DECISION_RD     = 2;

        public ShortByReference intra_matrix;
        public ShortByReference inter_matrix;

        public int stream_codec_tag;
        public int scenechange_threshold;
        public int lmin;
        public int lmax;

        public AVPaletteControl.ByReference palctrl;

        public int noise_reduction;

        public interface Reget_buffer extends Callback {
            int callback(AVCodecContext c, AVFrame pic);
        }
        public Reget_buffer reget_buffer;

        public int rc_initial_buffer_occupancy;
        public int inter_threshold;
        public int flags2;
        public int error_rate;

        public int antialias_algo;
        public static final int
                FF_AA_AUTO    = 0,
                FF_AA_FASTINT = 1,
                FF_AA_INT     = 2,
                FF_AA_FLOAT   = 3;

        public int quantizer_noise_shaping;
        public int thread_count;

        public interface Func extends Callback {
            int callback(AVCodecContext c2, Pointer arg);
        }
        public interface Execute extends Callback {
            int callback(AVCodecContext c, Func func, Pointer arg2, IntByReference ret, int count, int size);
        }
        public Execute execute;
        public Pointer thread_opaque;

        public int me_threshold;
        public int mb_threshold;
        public int intra_dc_precision;
        public int nsse_weight;
        public int skip_top;
        public int skip_bottom;

        public int profile;
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

        public int level;
        public static final int FF_LEVEL_UNKNOWN = -99;

        public int lowres;
        public int coded_width, coded_height;
        public int frame_skip_threshold;
        public int frame_skip_factor;
        public int frame_skip_exp;
        public int frame_skip_cmp;
        public float border_masking;
        public int mb_lmin;
        public int mb_lmax;
        public int me_penalty_compensation;

        public int /* enum AVDiscard */ skip_loop_filter;
        public int /* enum AVDiscard */ skip_idct;
        public int /* enum AVDiscard */ skip_frame;

        public int bidir_refine;
        public int brd_scale;
        public float crf;
        public int cqp;
        public int keyint_min;
        public int refs;
        public int chromaoffset;
        public int bframebias;
        public int trellis;
        public float complexityblur;
        public int deblockalpha;
        public int deblockbeta;

        public int partitions;
        public static final int
                X264_PART_I4X4 = 0x001,
                X264_PART_I8X8 = 0x002,
                X264_PART_P8X8 = 0x010,
                X264_PART_P4X4 = 0x020,
                X264_PART_B8X8 = 0x100;

        public int directpred;
        public int cutoff;
        public int scenechange_factor;
        public int mv0_threshold;
        public int b_sensitivity;

        public int compression_level;
        public static final int FF_COMPRESSION_DEFAULT = -1;

        public int use_lpc;
        public int lpc_coeff_precision;
        public int min_prediction_order;
        public int max_prediction_order;
        public int prediction_order_method;
        public int min_partition_order;
        public int max_partition_order;
        public long timecode_frame_start;

//#if LIBAVCODEC_VERSION_MAJOR < 53
        @Deprecated public int request_channels;
//#endif

        public float drc_scale;
        public long reordered_opaque;
        public int bits_per_raw_sample;
        public long channel_layout;
        public long request_channel_layout;
        public float rc_max_available_vbv_use;
        public float rc_min_vbv_overflow_use;

        public AVHWAccel.ByReference hwaccel;
        public int ticks_per_frame;
        public Pointer hwaccel_context;

        public int /* enum AVColorPrimaries */ color_primaries;
        public int /* enum AVColorTransferCharacteristic */ color_trc;
        public int /* enum AVColorSpace */ colorspace;
        public int /* enum AVColorRange */ color_range;
        public int /* enum AVChromaLocation */ chroma_sample_location;

        public interface Func2 extends Callback {
            int callback(AVCodecContext c2, Pointer arg, int jobnr, int threadnr);
        }
        public interface Execute2 extends Callback {
            int callback(AVCodecContext c, Func2 func2, Pointer arg2, IntByReference ret, int count);
        }
        public Execute2 execute2;

        public int weighted_p_pred;
        public int aq_mode;
        public float aq_strength;
        public float psy_rd;
        public float psy_trellis;
        public int rc_lookahead;

        public static class ByReference extends AVCodecContext implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVCodec extends Structure {
        public AVCodec() { }
        public AVCodec(Pointer m) { super(m); read(); }

        public String name;
        public int /* enum AVMediaType */ type;
        public int /* enum CodecID */ id;

        public int priv_data_size;
        public interface Init extends Callback {
            int callback(AVCodecContext c);
        }
        public Init init;
        public interface Encode extends Callback {
            int callback(AVCodecContext c, Pointer buf, int buf_size, Pointer data);
        }
        public Encode encode;
        public interface Close extends Callback {
            int callback(AVCodecContext c);
        }
        public Close close;
        public interface Decode extends Callback {
            int callback(AVCodecContext c, Pointer outdata, IntByReference outdata_size, AVPacket avpkt);
        }
        public Decode decode;

        public int capabilities;
        public AVCodec.ByReference next;

        public interface Flush extends Callback {
            void callback(AVCodecContext c);
        }
        public Flush flush;
        public AVRational.ByReference supported_framerates;
        public IntByReference /* enum PixelFormat* */ pix_fmts;

        public String long_name;
        public IntByReference supported_samplerates;
        public IntByReference /* enum SampleFormat* */ sample_fmts;
        public LongByReference channel_layouts;

        public static class ByReference extends AVCodec implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVHWAccel extends Structure {
        public AVHWAccel() { }
        public AVHWAccel(Pointer m) { super(m); read(); }

        public String name;
        public int /* enum AVMediaType */ type;
        public int /* enum CodecID */ id;
        public int /* enum PixelFormat */ pix_fmt;

        public int capabilities;
        public AVHWAccel.ByReference next;

        public interface Start_frame extends Callback {
            int callback(AVCodecContext avctx, Pointer buf, int buf_size);
        }
        public Start_frame start_frame;
        public interface Decode_slice extends Callback {
            int callback(AVCodecContext avctx, Pointer buf, int buf_size);
        }
        public Decode_slice decode_slice;
        public interface End_frame extends Callback {
            int callback(AVCodecContext avctx);
        }
        public End_frame end_frame;
        public int priv_data_size;

        public static class ByReference extends AVHWAccel implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVPicture extends Structure {
        public AVPicture() { }
        public AVPicture(Pointer m) { super(m); read(); }

        public Pointer data0, data1, data2, data3;
        public int linesize0, linesize1, linesize2, linesize3;

        public static class ByReference extends AVPicture implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

//#if LIBAVCODEC_VERSION_MAJOR < 53
    @Deprecated
    public static final int
            AVPALETTE_SIZE = 1024,
            AVPALETTE_COUNT = 256;
    @Deprecated
    public static class AVPaletteControl extends Structure {
        public AVPaletteControl() { }
        public AVPaletteControl(Pointer m) { useMemory(m); read(); }

        public int palette_changed;
        public int[] palette = new int[AVPALETTE_COUNT];

        public static class ByReference extends AVPaletteControl implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }
//#endif

    //enum AVSubtitleType {
    public static final int 
            SUBTITLE_NONE = 0,
            SUBTITLE_BITMAP = 1,
            SUBTITLE_TEXT = 2,
            SUBTITLE_ASS = 3;

    public static class AVSubtitleRect extends Structure {
        public AVSubtitleRect() { }
        public AVSubtitleRect(Pointer m) { super(m); read(); }

        public int x;
        public int y;
        public int w;
        public int h;
        public int nb_colors;

        public AVPicture pict;
        public int /* enum AVSubtitleType */ type;

        String text;
        String ass;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVSubtitleRect p) {
                setStructure(p);
            }
            public AVSubtitleRect getStructure() {
                return new AVSubtitleRect(getValue());
            }
            public void getStructure(AVSubtitleRect p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVSubtitleRect p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class AVSubtitle extends Structure {
        public short format;
        public int start_display_time;
        public int end_display_time;
        public int num_rects;
        public AVSubtitleRect.PointerByReference rects;
        long pts;
    }

    public static native void av_destruct_packet(AVPacket pkt);
    public static native void av_init_packet(AVPacket pkt);
    public static native int av_new_packet(AVPacket pkt, int size);
    public static native void av_shrink_packet(AVPacket pkt, int size);
    public static native int av_dup_packet(AVPacket pkt);
    public static native void av_free_packet(AVPacket pkt);


    public static class ReSampleContext extends PointerType { };
    public static class AVResampleContext extends PointerType { };

    public static native ReSampleContext av_audio_resample_init(int output_channels, int input_channels,
            int output_rate, int input_rate, int /* enum SampleFormat */ sample_fmt_out, int /* enum SampleFormat */ sample_fmt_in, 
            int filter_length, int log2_phase_count, int linear, double cutoff);
    public static native int audio_resample(ReSampleContext s, short[] output, short[] input, int nb_samples);
    public static native int audio_resample(ReSampleContext s, ShortBuffer output, ShortBuffer input, int nb_samples);
    public static native void audio_resample_close(ReSampleContext s);

    public static native AVResampleContext av_resample_init(int out_rate, int in_rate, int filter_length, 
            int log2_phase_count, int linear, double cutoff);
    public static native int av_resample(AVResampleContext c, short[] dst, short[] src, 
            IntByReference consumed, int src_size, int dst_size, int update_ctx);
    public static native int av_resample(AVResampleContext c, ShortBuffer dst, ShortBuffer src, 
            IntByReference consumed, int src_size, int dst_size, int update_ctx);
    public static native void av_resample_compensate(AVResampleContext c, int sample_delta, int compensation_distance);
    public static native void av_resample_close(AVResampleContext c);

    public static native int avpicture_alloc(AVPicture picture, int /* enum PixelFormat */ pix_fmt, int width, int height);
    public static native void avpicture_free(AVPicture picture);

    public static native int avpicture_fill(AVPicture picture, Pointer ptr,
            int /* enum PixelFormat */ pix_fmt, int width, int height);
    public static native int avpicture_layout(AVPicture src, int /* enum PixelFormat */ pix_fmt, 
            int width, int height, byte[] dest, int dest_size);

    public static native int avpicture_get_size(int /* enum PixelFormat */ pix_fmt, int width, int height);
    public static native void avcodec_get_chroma_sub_sample(int /* enum PixelFormat */ pix_fmt, IntByReference h_shift, IntByReference v_shift);
    public static native String avcodec_get_pix_fmt_name(int /* enum PixelFormat */ pix_fmt);
    public static native void avcodec_set_dimensions(AVCodecContext s, int width, int height);

    public static native int avcodec_pix_fmt_to_codec_tag(int /* enum PixelFormat */ pix_fmt);

    public static final int 
            FF_LOSS_RESOLUTION = 0x0001,
            FF_LOSS_DEPTH      = 0x0002,
            FF_LOSS_COLORSPACE = 0x0004,
            FF_LOSS_ALPHA      = 0x0008,
            FF_LOSS_COLORQUANT = 0x0010,
            FF_LOSS_CHROMA     = 0x0020;
    public static native int avcodec_get_pix_fmt_loss(int /* enum PixelFormat */ dst_pix_fmt, 
            int /* enum PixelFormat */ src_pix_fmt, int has_alpha);
    public static native int /* enum PixelFormat */ avcodec_find_best_pix_fmt(long pix_fmt_mask, 
            int /* enum PixelFormat */ src_pix_fmt, int has_alpha, IntByReference loss_ptr);

    public static native void avcodec_pix_fmt_string (byte[] buf, int buf_size, int /* enum PixelFormat */ pix_fmt);
    public static final int
            FF_ALPHA_TRANSP      = 0x0001,
            FF_ALPHA_SEMI_TRANSP = 0x0002;

    public static native int img_get_alpha_info(AVPicture src,
            int /* enum PixelFormat */ pix_fmt, int width, int height);
    public static native int avpicture_deinterlace(AVPicture dst, AVPicture src,
            int /* enum PixelFormat */ pix_fmt, int width, int height);

    public static native AVCodec av_codec_next(AVCodec c);
    public static native int avcodec_version();
    public static native String avcodec_configuration();
    public static native String avcodec_license();
    public static native void avcodec_init();

    public static native void avcodec_register(AVCodec codec);
    public static native AVCodec avcodec_find_encoder(int /* enum CodecID */ id);
    public static native AVCodec avcodec_find_encoder_by_name(String name);
    public static native AVCodec avcodec_find_decoder(int /* enum CodecID */ id);
    public static native AVCodec avcodec_find_decoder_by_name(String name);
    public static native void avcodec_string(byte[] buf, int buf_size, AVCodecContext enc, int encode);

    public static native void avcodec_get_context_defaults(AVCodecContext s);
    public static native void avcodec_get_context_defaults2(AVCodecContext s, int /* enum AVMediaType */ type);
    public static native AVCodecContext avcodec_alloc_context();
    public static native AVCodecContext avcodec_alloc_context2(int /* enum AVMediaType */ type);
    public static native void avcodec_get_frame_defaults(AVFrame pic);
    public static native AVFrame avcodec_alloc_frame();

    public static native int avcodec_default_get_buffer(AVCodecContext s, AVFrame pic);
    public static native void avcodec_default_release_buffer(AVCodecContext s, AVFrame pic);
    public static native int avcodec_default_reget_buffer(AVCodecContext s, AVFrame pic);

    public static native void avcodec_align_dimensions(AVCodecContext s, IntByReference width, IntByReference height);
    public static native void avcodec_align_dimensions2(AVCodecContext s, IntByReference width, IntByReference height,
                               int linesize_align[/*4*/]);

    public static native int avcodec_check_dimensions(Pointer av_log_ctx, int w, int h);
    public static native int /* enum PixelFormat */ avcodec_default_get_format(AVCodecContext s, IntByReference /* enum PixelFormat* */ fmt);

    public static class ThreadExecute extends avcodec  {
        public static final String libname = Loader.load(paths, libnames);

        public static native int avcodec_thread_init(AVCodecContext s, int thread_count);
        public static native void avcodec_thread_free(AVCodecContext s);
        public interface Func extends Callback {
            int callback(AVCodecContext c2, Pointer arg2);
        }
        public static native int avcodec_default_execute(AVCodecContext c, Func func, Pointer arg, IntByReference ret, int count, int size);
        public interface Func2 extends Callback {
            int callback(AVCodecContext c2, Pointer arg2, int i1, int i2);
        }
        public static native int avcodec_default_execute2(AVCodecContext c, Func2 func2, Pointer arg, IntByReference ret, int count);
    }

    public static native int avcodec_open(AVCodecContext avctx, AVCodec codec);

    public static native int avcodec_decode_audio3(AVCodecContext avctx, short[] samples,
            IntByReference frame_size_ptr, AVPacket avpkt);
    public static native int avcodec_decode_audio3(AVCodecContext avctx, ShortBuffer samples,
            IntByReference frame_size_ptr, AVPacket avpkt);

    public static native int avcodec_decode_video2(AVCodecContext avctx, AVFrame picture,
            IntByReference got_picture_ptr, AVPacket avpkt);

    public static native int avcodec_decode_subtitle2(AVCodecContext avctx, 
            AVSubtitle sub, IntByReference got_sub_ptr, AVPacket avpkt);
//    public static native int avcodec_parse_frame(AVCodecContext avctx,
//            PointerByReference pdata, IntByReference data_size_ptr,
//            Pointer buf, int buf_size);

    public static native int avcodec_encode_audio(AVCodecContext avctx, 
            Pointer buf, int buf_size, short[] samples);
    public static native int avcodec_encode_audio(AVCodecContext avctx, 
            Pointer buf, int buf_size, ShortBuffer samples);

    public static native int avcodec_encode_video(AVCodecContext avctx, 
            Pointer buf, int buf_size, AVFrame pict);
    public static native int avcodec_encode_subtitle(AVCodecContext avctx, 
            Pointer buf, int buf_size, AVSubtitle sub);

    public static native int avcodec_close(AVCodecContext avctx);

    public static native void avcodec_register_all();

    public static native void avcodec_flush_buffers(AVCodecContext avctx);
    public static native void avcodec_default_free_buffers(AVCodecContext s);

    public static native char av_get_pict_type_char(int pict_type);
    public static native int av_get_bits_per_sample(int /* enum CodecID */ codec_id);
    public static native int av_get_bits_per_sample_format(int /* enum SampleFormat */ sample_fmt);


    public static class AVCodecParserContext extends Structure {
        public AVCodecParserContext() { }
        public AVCodecParserContext(Pointer m) { useMemory(m); read(); }

        public Pointer priv_data;
        public AVCodecParser.ByReference parser;
        public long frame_offset;
        public long cur_offset;
        public long next_frame_offset;

        public int pict_type;
        public int repeat_pict;
        public long pts;
        public long dts;

        public long last_pts;
        public long last_dts;
        public int fetch_timestamp;

        public static final int AV_PARSER_PTS_NB = 4;
        public int cur_frame_start_index;
        public long[] cur_frame_offset = new long[AV_PARSER_PTS_NB];
        public long[] cur_frame_pts = new long[AV_PARSER_PTS_NB];
        public long[] cur_frame_dts = new long[AV_PARSER_PTS_NB];

        public int flags;
        public static final int PARSER_FLAG_COMPLETE_FRAMES = 0x0001;

        public long offset;
        public long[] cur_frame_end = new long[AV_PARSER_PTS_NB];

        public int key_frame;
        public long convergence_duration;

        public int dts_sync_point;
        public int dts_ref_dts_delta;
        public int pts_dts_delta;

        public long[] cur_frame_pos = new long[AV_PARSER_PTS_NB];
        public long pos;
        public long last_pos;

        public static class ByReference extends AVCodecParserContext implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVCodecParser extends Structure {
        public AVCodecParser() { }
        public AVCodecParser(Pointer m) { super(m); read(); }

        public int codec_id0, codec_id1, codec_id2, codec_id3, codec_id4;
        public int priv_data_size;
        public interface Parser_init extends Callback {
            int callback(AVCodecParserContext s);
        }
        public Parser_init parser_init;
        public interface Parser_parse extends Callback {
            int callback(AVCodecParserContext s, AVCodecContext avctx,
                    PointerByReference poutbuf, IntByReference poutbuf_size,
                    Pointer buf, int buf_size);
        }
        public Parser_parse parser_parse;
        public interface Parser_close extends Callback {
            void callback(AVCodecParserContext s);
        }
        public Parser_close parser_close;
        public interface Split extends Callback {
            int callback(AVCodecContext avctx, Pointer buf, int buf_size);
        }
        public Split split;
        public AVCodecParser.ByReference next;

        public static class ByReference extends AVCodecParser implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static native AVCodecParser av_parser_next(AVCodecParser c);

    public static native void av_register_codec_parser(AVCodecParser parser);
    public static native AVCodecParserContext av_parser_init(int codec_id);

    public static native int av_parser_parse2(AVCodecParserContext s,
            AVCodecContext avctx,
            PointerByReference poutbuf, IntByReference poutbuf_size,
            Pointer buf, int buf_size, long pts, long dts, long pos);

    public static native int av_parser_change(AVCodecParserContext s,
            AVCodecContext avctx,
            PointerByReference poutbuf, IntByReference poutbuf_size,
            Pointer buf, int buf_size, int keyframe);
    public static native void av_parser_close(AVCodecParserContext s);


    public static class AVBitStreamFilterContext extends Structure {
        public AVBitStreamFilterContext() { }
        public AVBitStreamFilterContext(Pointer m) { super(m); read(); }

        public Pointer priv_data;
        public AVBitStreamFilter.ByReference filter;
        public AVCodecParserContext.ByReference parser;
        public AVBitStreamFilterContext.ByReference next;

        public static class ByReference extends AVBitStreamFilterContext implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }


    public static class AVBitStreamFilter extends Structure {
        public AVBitStreamFilter() { }
        public AVBitStreamFilter(Pointer m) { super(m); read(); }

        public String name;
        public int priv_data_size;
        public interface Filter extends Callback {
            int callback(AVBitStreamFilterContext bsfc, AVCodecContext avctx, String args,
                    PointerByReference poutbuf, IntByReference poutbuf_size,
                    Pointer buf, int buf_size, int keyframe);
        }
        public Filter filter;
        public interface Close extends Callback {
            void callback(AVBitStreamFilterContext bsfc);
        }
        public Close close;
        public AVBitStreamFilter.ByReference next;

        public static class ByReference extends AVBitStreamFilter implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static native void av_register_bitstream_filter(AVBitStreamFilter bsf);
    public static native AVBitStreamFilterContext av_bitstream_filter_init(String name);
    public static native int av_bitstream_filter_filter(AVBitStreamFilterContext bsfc,
            AVCodecContext avctx, String args,
            PointerByReference poutbuf, IntByReference poutbuf_size,
            Pointer buf, int buf_size, int keyframe);
    public static native void av_bitstream_filter_close(AVBitStreamFilterContext bsf);

    public static native AVBitStreamFilter av_bitstream_filter_next(AVBitStreamFilter f);

    public static native Pointer av_fast_realloc(Pointer ptr, IntByReference size, int min_size);
    public static native void av_fast_malloc(Pointer ptr, IntByReference size, int min_size);

    public static native void av_picture_copy(AVPicture dst, AVPicture src,
            int /* enum PixelFormat */ pix_fmt, int width, int height);
    public static native int av_picture_crop(AVPicture dst, AVPicture src,
            int /* enum PixelFormat */ pix_fmt, int top_band, int left_band);
    public static native int av_picture_pad(AVPicture dst, AVPicture src, int height, int width, 
            int /* enum PixelFormat */ pix_fmt, int padtop, int padbottom, int padleft, int padright,
            IntByReference color);

    public static native int av_xiphlacing(Pointer s, int v);

    public static native int av_parse_video_frame_size(IntByReference width_ptr, IntByReference height_ptr, String str);
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
    public interface Cb extends Callback {
        int callback(PointerByReference mutex, int /* enum AVLockOp */ op);
    }
    public static native int av_lockmgr_register(Cb cb);


    //#include "opt.h"
    public static class Opt extends avcodec {
        public static final String libname = Loader.load(paths, libnames);

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

        public static class AVOption extends Structure {
            public AVOption() { }
            public AVOption(Pointer m) { super(m); read(); }

            public String name;
            public String help;
            public int offset;
            public int /* enum AVOptionType */ type;
            public double default_val;
            public double min;
            public double max;

            public int flags;
            public static final int
                    AV_OPT_FLAG_ENCODING_PARAM = 1,
                    AV_OPT_FLAG_DECODING_PARAM = 2,
                    AV_OPT_FLAG_METADATA       = 4,
                    AV_OPT_FLAG_AUDIO_PARAM    = 8,
                    AV_OPT_FLAG_VIDEO_PARAM    = 16,
                    AV_OPT_FLAG_SUBTITLE_PARAM = 32;

            public String unit;

            public static class ByReference extends AVOption implements Structure.ByReference {
                public ByReference() { }
                public ByReference(Pointer m) { super(m); }
            }

            public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
                public PointerByReference() { }
                public PointerByReference(AVOption p) {
                    setStructure(p);
                }
                public AVOption getStructure() {
                    return new AVOption(getValue());
                }
                public void getStructure(AVOption p) {
                    p.useMemory(getValue());
                    p.read();
                }
                public void setStructure(AVOption p) {
                    p.write();
                    setValue(p.getPointer());
                }
            }
            public PointerByReference pointerByReference() {
                return new PointerByReference(this);
            }
        }

        public static class AVOption2 extends Structure {
            public String name;
            public String help;
            public int offset;
            public int /* enum AVOptionType */ type;

            public static class Default_val extends Union {
                public double dbl;
                public String str;
            }
            public Default_val default_val;

            public double min;
            public double max;

            public int flags;

            public String unit;
        }

        public static native AVOption av_find_opt(Pointer obj, String name, String unit, int mask, int flags);
        public static native int av_set_string3(Pointer obj, String name, String val, int alloc, AVOption.PointerByReference o_out);
        public static native AVOption av_set_double(Pointer obj, String name, double n);
        public static native AVOption av_set_q(Pointer obj, String name, AVRational.ByValue n);
        public static native AVOption av_set_int(Pointer obj, String name, long n);
        public static native double av_get_double(Pointer obj, String name, AVOption.PointerByReference o_out);
        public static native AVRational.ByValue av_get_q(Pointer obj, String name, AVOption.PointerByReference o_out);
        public static native long av_get_int(Pointer obj, String name, AVOption.PointerByReference o_out);
        public static native String av_get_string(Pointer obj, String name, AVOption.PointerByReference o_out, Pointer buf, int buf_len);
        public static native AVOption av_next_option(Pointer obj, AVOption last);
        public static native int av_opt_show(Pointer obj, Pointer av_log_obj);
        public static native void av_opt_set_defaults(Pointer s);
        public static native void av_opt_set_defaults2(Pointer s, int mask, int flags);
    }

    //#include "avfft.h"
    //typedef float FFTSample;

    public static class FFTComplex extends Structure {
        public float /* FFTSample */ re, im;
    }

    public static class FFTContext extends PointerType { }

    public static native FFTContext av_fft_init(int nbits, int inverse);
    public static native void av_fft_permute(FFTContext s, FFTComplex z);
    public static native void av_fft_calc(FFTContext s, FFTComplex z);
    public static native void av_fft_end(FFTContext s);
    public static native FFTContext av_mdct_init(int nbits, int inverse, double scale);

    public static native void av_imdct_calc(FFTContext s, float[] /* FFTSample* */ output, float[] /* FFTSample* */ input);
    public static native void av_imdct_calc(FFTContext s, FloatBuffer /* FFTSample* */ output, FloatBuffer /* FFTSample* */ input);
    public static native void av_imdct_calc(FFTContext s, Pointer /* FFTSample* */ output, Pointer /* FFTSample* */ input);
    public static native void av_imdct_half(FFTContext s, float[] /* FFTSample* */ output, float[] /* FFTSample* */ input);
    public static native void av_imdct_half(FFTContext s, FloatBuffer /* FFTSample* */ output, FloatBuffer /* FFTSample* */ input);
    public static native void av_imdct_half(FFTContext s, Pointer /* FFTSample* */ output, Pointer /* FFTSample* */ input);
    public static native void av_mdct_calc(FFTContext s, float[] /* FFTSample* */ output, float[] /* FFTSample* */ input);
    public static native void av_mdct_calc(FFTContext s, FloatBuffer /* FFTSample* */ output, FloatBuffer /* FFTSample* */ input);
    public static native void av_mdct_calc(FFTContext s, Pointer /* FFTSample* */ output, Pointer /* FFTSample* */ input);
    public static native void av_mdct_end(FFTContext s);


    //enum RDFTransformType {
    public static final int
            DFT_R2C  = 0,
            IDFT_C2R = 1,
            IDFT_R2C = 2,
            DFT_C2R  = 3;

    public static class RDFTContext extends PointerType { }

    public static native RDFTContext av_rdft_init(int nbits, int /* enum RDFTransformType */ trans);
    public static native void av_rdft_calc(RDFTContext s, float[] /* FFTSample* */ data);
    public static native void av_rdft_calc(RDFTContext s, FloatBuffer /* FFTSample* */ data);
    public static native void av_rdft_calc(RDFTContext s, Pointer /* FFTSample* */ data);
    public static native void av_rdft_end(RDFTContext s);


    public static class DCTContext extends PointerType { }

    //enum DCTTransformType {
    public static final int
        DCT_II  = 0,
        DCT_III = 1,
        DCT_I   = 2,
        DST_I   = 3;

    public static native DCTContext av_dct_init(int nbits, int /* enum DCTTransformType */ type);
    public static native void av_dct_calc(DCTContext s, float[] /* FFTSample* */ data);
    public static native void av_dct_calc(DCTContext s, FloatBuffer /* FFTSample* */ data);
    public static native void av_dct_calc(DCTContext s, Pointer /* FFTSample* */ data);
    public static native void av_dct_end (DCTContext s);


    //#include "dxva2.h"
    public static class IDirectXVideoDecoder extends PointerType { }
    public static class DXVA2_ConfigPictureDecode extends PointerType { }
    public static class LPDIRECT3DSURFACE9 extends PointerType { }

    public static class dxva_context extends Structure {
        public IDirectXVideoDecoder decoder;
        public DXVA2_ConfigPictureDecode cfg;
        public int surface_count;
        public LPDIRECT3DSURFACE9 surface;
        public long workaround;
        public int report_id;
    }

    //#include "vaapi.h"
    public static class vaapi_context extends Structure {
        public Pointer display;
        public int config_id;
        public int context_id;
        public int pic_param_buf_id;
        public int iq_matrix_buf_id;
        public int bitplane_buf_id;
        public IntByReference slice_buf_ids;
        public int n_slice_buf_ids;
        public int slice_buf_ids_alloc;
        public Pointer slice_params;
        public int slice_param_size;
        public int slice_params_alloc;
        public int slice_count;
        public Pointer slice_data;
        public int slice_data_size;
    }

    //#include "vdpau.h"
    public static final int
            FF_VDPAU_STATE_USED_FOR_RENDER = 1,
            FF_VDPAU_STATE_USED_FOR_REFERENCE = 2;

    public static class VdpBitstreamBuffer extends PointerType { }
    public static class vdpau_render_state extends Structure {
        public int /* VdpVideoSurface */ surface;
        public int state;

        //public static class VdpPictureInfo extends Union {
        //    public VdpPictureInfoH264        h264;
        //    public VdpPictureInfoMPEG1Or2    mpeg;
        //    public VdpPictureInfoVC1          vc1;
        //    public VdpPictureInfoMPEG4Part2 mpeg4;
        //}
        //public VdpPictureInfo info;
        public byte[] info = new byte[716]; // probably...

        public int bitstream_buffers_allocated;
        public int bitstream_buffers_used;

        VdpBitstreamBuffer bitstream_buffers;
    }

    //#include "xvmc.h"
//#if LIBAVCODEC_VERSION_MAJOR < 53
    public static final int
            AV_XVMC_STATE_DISPLAY_PENDING         = 1,
            AV_XVMC_STATE_PREDICTION              = 2,
            AV_XVMC_STATE_OSD_SOURCE              = 4,
//#endif
            AV_XVMC_ID                   = 0x1DC711C0;

    public static class XvMCMacroBlock extends PointerType { }
    public static class XvMCSurface extends PointerType { }

    public static class xvmc_pix_fmt extends Structure {
        public int             xvmc_id;
        public Pointer         data_blocks;
        public XvMCMacroBlock  mv_blocks;
        public int             allocated_mv_blocks;
        public int             allocated_data_blocks;
        public int             idct;
        public int             unsigned_intra;
        public XvMCSurface     p_surface;

        public XvMCSurface     p_past_surface;
        public XvMCSurface     p_future_surface;
        public int             picture_structure;
        public int             flags;

        public int             start_mv_blocks_num;
        public int             filled_mv_blocks_num;
        public int             next_free_data_block_num;

//#if LIBAVCODEC_VERSION_MAJOR < 53
        public int             state;
        public Pointer         p_osd_target_surface_render;
    }

    public static class NewFunctions extends avcodec {
        public static final String libname = Loader.load(paths, libnames);

        public static native int avcodec_copy_context(AVCodecContext dest, AVCodecContext src);

        public static native int avcodec_get_edge_width();
    }

    public static class DeprecatedFunctions extends avcodec {
        public static final String libname = Loader.load(paths, libnames);

        @Deprecated public static native void av_destruct_packet_nofree(AVPacket pkt);

        @Deprecated public static native ReSampleContext audio_resample_init(int output_channels,
                int input_channels, int output_rate, int input_rate);

        @Deprecated public static native int /* enum PixelFormat */ avcodec_get_pix_fmt(String name);

        @Deprecated public static native void register_avcodec(AVCodec codec);

        @Deprecated public static native int avcodec_decode_audio2(AVCodecContext avctx, short[] samples,
                IntByReference frame_size_ptr, Pointer buf, int buf_size);
        @Deprecated public static native int avcodec_decode_audio2(AVCodecContext avctx, ShortBuffer samples,
                IntByReference frame_size_ptr, Pointer buf, int buf_size);

        @Deprecated public static native int avcodec_decode_video(AVCodecContext avctx, AVFrame picture,
                IntByReference got_picture_ptr, Pointer buf, int buf_size);

        @Deprecated public static native int avcodec_decode_subtitle(AVCodecContext avctx, AVSubtitle sub,
                IntByReference got_sub_ptr, Pointer buf, int buf_size);

        @Deprecated public static native int av_parser_parse(AVCodecParserContext s, AVCodecContext avctx,
                PointerByReference poutbuf, IntByReference poutbuf_size,
                Pointer buf, int buf_size, long pts, long dts);

        @Deprecated public static native AVOption av_set_string(Pointer obj, String name, String val);
        @Deprecated public static native AVOption av_set_string2(Pointer obj, String name, String val, int alloc);
    }
}

