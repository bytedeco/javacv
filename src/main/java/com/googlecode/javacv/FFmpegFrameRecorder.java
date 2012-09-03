/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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
 * Based on the output-example.c file included in FFmpeg 0.6.5
 * as well as on the decoding_encoding.c file included in FFmpeg 0.11.1,
 * which are covered by the following copyright notice:
 *
 * Libavformat API example: Output a media file in any supported
 * libavformat format. The default codecs are used.
 *
 * Copyright (c) 2001,2003 Fabrice Bellard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.googlecode.javacv;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.ShortPointer;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.googlecode.javacv.cpp.avcodec.*;
import static com.googlecode.javacv.cpp.avformat.*;
import static com.googlecode.javacv.cpp.avutil.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.swresample.*;
import static com.googlecode.javacv.cpp.swscale.*;

/**
 *
 * @author Samuel Audet
 */
public class FFmpegFrameRecorder extends FrameRecorder {

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(com.googlecode.javacv.cpp.avutil.class);
                Loader.load(com.googlecode.javacv.cpp.avcodec.class);
                Loader.load(com.googlecode.javacv.cpp.avformat.class);
                Loader.load(com.googlecode.javacv.cpp.swscale.class);
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception("Failed to load " + FFmpegFrameRecorder.class, t);
                }
            }
        }
    }

    public FFmpegFrameRecorder(File file, int audioChannels) {
        this(file, 0, 0, audioChannels);
    }
    public FFmpegFrameRecorder(String filename, int audioChannels) {
        this(filename, 0, 0, audioChannels);
    }
    public FFmpegFrameRecorder(File file, int imageWidth, int imageHeight) {
        this(file, imageWidth, imageHeight, 0);
    }
    public FFmpegFrameRecorder(String filename, int imageWidth, int imageHeight) {
        this(filename, imageWidth, imageHeight, 0);
    }
    public FFmpegFrameRecorder(File file, int imageWidth, int imageHeight, int audioChannels) {
        this(file.getAbsolutePath(), imageWidth, imageHeight);
    }
    public FFmpegFrameRecorder(String filename, int imageWidth, int imageHeight, int audioChannels) {
        /* initialize libavcodec, and register all codecs and formats */
        av_register_all();
        avformat_network_init();

        this.filename      = filename;
        this.imageWidth    = imageWidth;
        this.imageHeight   = imageHeight;
        this.audioChannels = audioChannels;

        this.pixelFormat   = PIX_FMT_NONE;
        this.videoCodec    = CODEC_ID_NONE;
        this.videoBitrate  = 400000;
        this.frameRate     = 30;

        this.sampleFormat  = AV_SAMPLE_FMT_NONE;
        this.audioCodec    = CODEC_ID_NONE;
        this.audioBitrate  = 64000;
        this.sampleRate    = 44100;

        this.interleaved = true;

        this.video_pkt = new AVPacket();
        this.audio_pkt = new AVPacket();
    }
    public void release() throws Exception {
        /* close each codec */
        if (video_c != null) {
            avcodec_close(video_c);
            video_c = null;
        }
        if (audio_c != null) {
            avcodec_close(audio_c);
            audio_c = null;
        }
        if (picture_buf != null) {
            av_free(picture_buf);
            picture_buf = null;
        }
        if (picture != null) {
            av_free(picture);
            picture = null;
        }
        if (tmp_picture != null) {
            av_free(tmp_picture);
            tmp_picture = null;
        }
        if (video_outbuf != null) {
            av_free(video_outbuf);
            video_outbuf = null;
        }
        if (frame != null) {
            av_free(frame);
            frame = null;
        }
        if (samples_out != null) {
            av_free(samples_out);
            samples_out = null;
        }
        if (audio_outbuf != null) {
            av_free(audio_outbuf);
            audio_outbuf = null;
        }
        video_st = null;
        audio_st = null;

        if (oc != null) {
            /* free the streams */
            int nb_streams = oc.nb_streams();
            for(int i = 0; i < nb_streams; i++) {
                av_free(oc.streams(i).codec());
                av_free(oc.streams(i));
            }

            /* free the stream */
            av_free(oc);
            oc = null;
        }

        if (img_convert_ctx != null) {
            sws_freeContext(img_convert_ctx);
            img_convert_ctx = null;
        }

        if (samples_convert_ctx != null) {
            swr_free(samples_convert_ctx);
            samples_convert_ctx = null;
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private String filename;
    private AVFrame picture, tmp_picture;
    private BytePointer picture_buf;
    private BytePointer video_outbuf;
    private int video_outbuf_size;
    private AVFrame frame;
    private Pointer samples_in;
    private BytePointer samples_out;
    private BytePointer audio_outbuf;
    private int audio_outbuf_size;
    private int audio_input_frame_size;
    private AVOutputFormat oformat;
    private AVFormatContext oc;
    private AVCodec video_codec, audio_codec;
    private AVCodecContext video_c, audio_c;
    private AVStream video_st, audio_st;
    private SwsContext img_convert_ctx;
    private SwrContext samples_convert_ctx;
    private AVPacket video_pkt, audio_pkt;
    private int[] got_video_packet, got_audio_packet;

    @Override public int getFrameNumber() {
        return picture == null ? super.getFrameNumber() : (int)picture.pts();
    }
    @Override public void setFrameNumber(int frameNumber) {
        if (picture == null) { super.setFrameNumber(frameNumber); } else { picture.pts(frameNumber); }
    }

    // best guess for timestamp in microseconds...
    @Override public long getTimestamp() {
        return Math.round(getFrameNumber() * 1000000L / getFrameRate());
    }
    @Override public void setTimestamp(long timestamp)  {
        setFrameNumber((int)Math.round(timestamp * getFrameRate() / 1000000L));
    }

    public void start() throws Exception {
        int ret;
        picture = null;
        tmp_picture = null;
        picture_buf = null;
        frame = null;
        video_outbuf = null;
        audio_outbuf = null;
        oc = null;
        video_c = null;
        audio_c = null;
        video_st = null;
        audio_st = null;
        got_video_packet = new int[1];
        got_audio_packet = new int[1];

        /* auto detect the output format from the name. */
        String format_name = format == null || format.length() == 0 ? null : format;
        if ((oformat = av_guess_format(format_name, filename, null)) == null) {
            throw new Exception("av_guess_format() error: Could not guess output format for \"" + filename + "\" and " + format + " format.");
        }
        format_name = oformat.name().getString();

        /* allocate the output media context */
        if ((oc = avformat_alloc_context()) == null) {
            throw new Exception("avformat_alloc_context() error: Could not allocate format context");
        }

        oc.oformat(oformat);
        oc.filename(filename);

        /* add the audio and video streams using the format codecs
           and initialize the codecs */

        if (imageWidth > 0 && imageHeight > 0) {
            if (videoCodec != CODEC_ID_NONE) {
                oformat.video_codec(videoCodec);
            } else if ("flv".equals(format_name)) {
                oformat.video_codec(CODEC_ID_FLV1);
            } else if ("mp4".equals(format_name)) {
                oformat.video_codec(CODEC_ID_MPEG4);
            } else if ("3gp".equals(format_name)) {
                oformat.video_codec(CODEC_ID_H263);
            } else if ("avi".equals(format_name)) {
                oformat.video_codec(CODEC_ID_HUFFYUV);
            }

            /* find the video encoder */
            if ((video_codec = avcodec_find_encoder(oformat.video_codec())) == null) {
                release();
                throw new Exception("avcodec_find_encoder() error: Video codec not found.");
            }

            /* add a video output stream */
            if ((video_st = avformat_new_stream(oc, video_codec)) == null) {
                release();
                throw new Exception("avformat_new_stream() error: Could not allocate video stream.");
            }
            video_c = video_st.codec();
            video_c.codec_id(oformat.video_codec());
            video_c.codec_type(AVMEDIA_TYPE_VIDEO);

            /* put sample parameters */
            video_c.bit_rate(videoBitrate);
            /* resolution must be a multiple of two, but round up to 16 as often required */
            video_c.width((imageWidth + 15) / 16 * 16);
            video_c.height(imageHeight);
            /* time base: this is the fundamental unit of time (in seconds) in terms
               of which frame timestamps are represented. for fixed-fps content,
               timebase should be 1/framerate and timestamp increments should be
               identically 1. */
            video_c.time_base(av_d2q(1 / frameRate, 1001000));
            video_c.gop_size(12); /* emit one intra frame every twelve frames at most */

            if (pixelFormat != PIX_FMT_NONE) {
                video_c.pix_fmt(pixelFormat);
            } else if (video_c.codec_id() == CODEC_ID_RAWVIDEO || video_c.codec_id() == CODEC_ID_PNG ||
                       video_c.codec_id() == CODEC_ID_HUFFYUV  || video_c.codec_id() == CODEC_ID_FFV1) {
                video_c.pix_fmt(PIX_FMT_RGB32);   // appropriate for common lossless formats
            } else {
                video_c.pix_fmt(PIX_FMT_YUV420P); // lossy, but works with about everything
            }

            if (video_c.codec_id() == CODEC_ID_MPEG2VIDEO) {
                /* just for testing, we also add B frames */
                video_c.max_b_frames(2);
            } else if (video_c.codec_id() == CODEC_ID_MPEG1VIDEO) {
                /* Needed to avoid using macroblocks in which some coeffs overflow.
                   This does not happen with normal video, it just happens here as
                   the motion of the chroma plane does not match the luma plane. */
                video_c.mb_decision(2);
            } else if (video_c.codec_id() == CODEC_ID_H263) {
                // H.263 does not support any other resolution than the following
                if (imageWidth <= 128 && imageHeight <= 96) {
                    video_c.width(128).height(96);
                } else if (imageWidth <= 176 && imageHeight <= 144) {
                    video_c.width(176).height(144);
                } else if (imageWidth <= 352 && imageHeight <= 288) {
                    video_c.width(352).height(288);
                } else if (imageWidth <= 704 && imageHeight <= 576) {
                    video_c.width(704).height(576);
                } else {
                    video_c.width(1408).height(1152);
                }
            } else if (video_c.codec_id() == CODEC_ID_H264) {
                // default to constrained baseline to produce content that plays back on anything,
                // without any significant tradeoffs for most use cases
                video_c.profile(AVCodecContext.FF_PROFILE_H264_CONSTRAINED_BASELINE);
                av_opt_set(video_c.priv_data(), "profile", profile != null && profile.length() > 0 ? profile : "baseline", 0);
                av_opt_set(video_c.priv_data(), "preset",  preset  != null && preset .length() > 0 ? preset  : "medium",   0);
            }

            // some formats want stream headers to be separate
            if ((oformat.flags() & AVFMT_GLOBALHEADER) != 0) {
                video_c.flags(video_c.flags() | CODEC_FLAG_GLOBAL_HEADER);
            }

            if ((video_codec.capabilities() & CODEC_CAP_EXPERIMENTAL) != 0) {
                video_c.strict_std_compliance(AVCodecContext.FF_COMPLIANCE_EXPERIMENTAL);
            }
        }

        /*
         * add an audio output stream
         */
        if (audioChannels > 0) {
            if (audioCodec != CODEC_ID_NONE) {
                oformat.audio_codec(audioCodec);
            } else if ("flv".equals(format_name) || "mp4".equals(format_name) || "3gp".equals(format_name)) {
                oformat.audio_codec(CODEC_ID_AAC);
            } else if ("avi".equals(format_name)) {
                oformat.audio_codec(CODEC_ID_PCM_S16LE);
            }

            /* find the audio encoder */
            if ((audio_codec = avcodec_find_encoder(oformat.audio_codec())) == null) {
                release();
                throw new Exception("avcodec_find_encoder() error: Audio codec not found.");
            }

            if ((audio_st = avformat_new_stream(oc, audio_codec)) == null) {
                release();
                throw new Exception("avformat_new_stream() error: Could not allocate audio stream.");
            }
            audio_c = audio_st.codec();
            audio_c.codec_id(oformat.audio_codec());
            audio_c.codec_type(AVMEDIA_TYPE_AUDIO);

            /* put sample parameters */
            audio_c.bit_rate(audioBitrate);
            audio_c.sample_rate(sampleRate);
            audio_c.channels(audioChannels);
            audio_c.channel_layout(av_get_default_channel_layout(audioChannels));
            if (sampleFormat != AV_SAMPLE_FMT_NONE) {
                audio_c.sample_fmt(sampleFormat);
            } else if (audio_c.codec_id() == CODEC_ID_AAC &&
                    (audio_codec.capabilities() & CODEC_CAP_EXPERIMENTAL) != 0) {
                audio_c.sample_fmt(AV_SAMPLE_FMT_FLT);
            } else {
                audio_c.sample_fmt(AV_SAMPLE_FMT_S16);
            }
            audio_c.time_base().num(1).den(sampleRate);
            switch (audio_c.sample_fmt()) {
                case AV_SAMPLE_FMT_U8:  audio_c.bits_per_raw_sample(8);  break;
                case AV_SAMPLE_FMT_S16: audio_c.bits_per_raw_sample(16); break;
                case AV_SAMPLE_FMT_S32: audio_c.bits_per_raw_sample(32); break;
                case AV_SAMPLE_FMT_FLT: audio_c.bits_per_raw_sample(32); break;
                case AV_SAMPLE_FMT_DBL: audio_c.bits_per_raw_sample(64); break;
                default: assert false;
            }

            // some formats want stream headers to be separate
            if ((oformat.flags() & AVFMT_GLOBALHEADER) != 0) {
                audio_c.flags(audio_c.flags() | CODEC_FLAG_GLOBAL_HEADER);
            }

            if ((audio_codec.capabilities() & CODEC_CAP_EXPERIMENTAL) != 0) {
                audio_c.strict_std_compliance(AVCodecContext.FF_COMPLIANCE_EXPERIMENTAL);
            }
        }

        av_dump_format(oc, 0, filename, 1);

        /* now that all the parameters are set, we can open the audio and
           video codecs and allocate the necessary encode buffers */
        if (video_st != null) {
            /* open the codec */
            if ((ret = avcodec_open2(video_c, video_codec, null)) < 0) {
                release();
                throw new Exception("avcodec_open2() error " + ret + ": Could not open video codec.");
            }

            video_outbuf = null;
            if ((oformat.flags() & AVFMT_RAWPICTURE) == 0) {
                /* allocate output buffer */
                /* XXX: API change will be done */
                /* buffers passed into lav* can be allocated any way you prefer,
                   as long as they're aligned enough for the architecture, and
                   they're freed appropriately (such as using av_free for buffers
                   allocated with av_malloc) */
                video_outbuf_size = Math.max(256 * 1024, 8 * video_c.width() * video_c.height()); // a la ffmpeg.c
                video_outbuf = new BytePointer(av_malloc(video_outbuf_size));
            }

            /* allocate the encoded raw picture */
            if ((picture = avcodec_alloc_frame()) == null) {
                release();
                throw new Exception("avcodec_alloc_frame() error: Could not allocate picture.");
            }
            picture.pts(0); // magic required by libx264

            int size = avpicture_get_size(video_c.pix_fmt(), video_c.width(), video_c.height());
            if ((picture_buf = new BytePointer(av_malloc(size))).isNull()) {
                release();
                throw new Exception("av_malloc() error: Could not allocate picture buffer.");
            }

            /* if the output format is not equal to the image format, then a temporary
               picture is needed too. It is then converted to the required output format */
            if ((tmp_picture = avcodec_alloc_frame()) == null) {
                release();
                throw new Exception("avcodec_alloc_frame() error: Could not allocate temporary picture.");
            }
        }

        if (audio_st != null) {
            /* open the codec */
            if ((ret = avcodec_open2(audio_c, audio_codec, null)) < 0) {
                release();
                throw new Exception("avcodec_open2() error " + ret + ": Could not open audio codec.");
            }

            audio_outbuf_size = 256 * 1024;
            audio_outbuf = new BytePointer(av_malloc(audio_outbuf_size));

            /* ugly hack for PCM codecs (will be removed ASAP with new PCM
               support to compute the input frame size in samples */
            if (audio_c.frame_size() <= 1) {
                audio_outbuf_size = FF_MIN_BUFFER_SIZE;
                audio_input_frame_size = audio_outbuf_size / audio_c.channels();
                switch (audio_c.codec_id()) {
                    case CODEC_ID_PCM_S16LE:
                    case CODEC_ID_PCM_S16BE:
                    case CODEC_ID_PCM_U16LE:
                    case CODEC_ID_PCM_U16BE:
                        audio_input_frame_size >>= 1;
                        break;
                    default:
                        break;
                }
            } else {
                audio_input_frame_size = audio_c.frame_size();
            }
            //int bufferSize = audio_input_frame_size * audio_c.bits_per_raw_sample()/8 * audio_c.channels();
            int bufferSize = av_samples_get_buffer_size(null, audio_c.channels(), audio_input_frame_size, audio_c.sample_fmt(), 1);
            samples_out = new BytePointer(av_malloc(bufferSize)).capacity(bufferSize);

            /* allocate the audio frame */
            if ((frame = avcodec_alloc_frame()) == null) {
                release();
                throw new Exception("avcodec_alloc_frame() error: Could not allocate audio frame.");
            }
        }

        /* open the output file, if needed */
        if ((oformat.flags() & AVFMT_NOFILE) == 0) {
            AVIOContext pb = new AVIOContext(null);
            if ((ret = avio_open(pb, filename, AVIO_FLAG_WRITE)) < 0) {
                release();
                throw new Exception("avio_open error() error " + ret + ": Could not open '" + filename + "'");
            }
            oc.pb(pb);
        }

        /* write the stream header, if any */
        avformat_write_header(oc, null);
    }

    public void stop() throws Exception {
        if (oc != null) {
            /* write the trailer, if any */
            av_write_trailer(oc);

            if ((oformat.flags() & AVFMT_NOFILE) == 0) {
                /* close the output file */
                avio_close(oc.pb());
            }
        }
        release();
    }

    public void record(IplImage image) throws Exception {
        record(image, PIX_FMT_NONE);
    }
    public void record(IplImage image, int pixelFormat) throws Exception {
        if (video_st == null) {
            throw new Exception("No video output stream (Is imageWidth > 0 && imageHeight > 0 and has start() been called?)");
        }
        int ret;

        if (image == null) {
            /* no more frame to compress. The codec has a latency of a few
               frames if using B frames, so we get the last frames by
               passing the same picture again */
        } else {
            int width = image.width();
            int height = image.height();
            int step = image.widthStep();
            BytePointer data = image.imageData();

            if (pixelFormat == PIX_FMT_NONE) {
                int depth = image.depth();
                int channels = image.nChannels();
                if ((depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) && channels == 3) {
                    pixelFormat = PIX_FMT_BGR24;
                } else if ((depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) && channels == 1) {
                    pixelFormat = PIX_FMT_GRAY8;
                } else if ((depth == IPL_DEPTH_16U || depth == IPL_DEPTH_16S) && channels == 1) {
                    pixelFormat = AV_HAVE_BIGENDIAN() ? PIX_FMT_GRAY16BE : PIX_FMT_GRAY16LE;
                } else if ((depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) && channels == 4) {
                    pixelFormat = PIX_FMT_RGBA;
                } else if ((depth == IPL_DEPTH_8U || depth == IPL_DEPTH_8S) && channels == 2) {
                    pixelFormat = PIX_FMT_NV21; // Android's camera capture format
                    step = width;
                } else {
                    throw new Exception("Could not guess pixel format of image: depth=" + depth + ", channels=" + channels);
                }
            }

            if (video_c.pix_fmt() != pixelFormat || video_c.width() != width || video_c.height() != height) {
                /* convert to the codec pixel format if needed */
                img_convert_ctx = sws_getCachedContext(img_convert_ctx, width, height, pixelFormat,
                        video_c.width(), video_c.height(), video_c.pix_fmt(), SWS_BILINEAR, null, null, null);
                if (img_convert_ctx == null) {
                    throw new Exception("sws_getCachedContext() error: Cannot initialize the conversion context.");
                }
                avpicture_fill(tmp_picture, data, pixelFormat, width, height);
                avpicture_fill(picture, picture_buf, video_c.pix_fmt(), video_c.width(), video_c.height());
                tmp_picture.linesize(0, step);
                sws_scale(img_convert_ctx, new PointerPointer(tmp_picture), tmp_picture.linesize(),
                          0, height, new PointerPointer(picture), picture.linesize());
            } else {
                avpicture_fill(picture, data, pixelFormat, width, height);
                picture.linesize(0, step);
            }
        }

        if ((oformat.flags() & AVFMT_RAWPICTURE) != 0) {
            /* raw video case. The API may change slightly in the future for that? */
            av_init_packet(video_pkt);
            video_pkt.flags(video_pkt.flags() | AV_PKT_FLAG_KEY);
            video_pkt.stream_index(video_st.index());
            video_pkt.data(new BytePointer(picture));
            video_pkt.size(Loader.sizeof(AVPicture.class));
        } else {
            /* encode the image */
            av_init_packet(video_pkt);
            video_pkt.data(video_outbuf);
            video_pkt.size(video_outbuf_size);
            if ((ret = avcodec_encode_video2(video_c, video_pkt, picture, got_video_packet)) < 0) {
                throw new Exception("avcodec_encode_video2() error " + ret + ": Could not encode video packet.");
            }
            picture.pts(picture.pts() + 1); // magic required by libx264

            /* if zero size, it means the image was buffered */
            if (got_video_packet[0] != 0) {
                if (video_pkt.pts() != AV_NOPTS_VALUE) {
                    video_pkt.pts(av_rescale_q(video_pkt.pts(), video_c.time_base(), video_st.time_base()));
                }
                if (video_pkt.dts() != AV_NOPTS_VALUE) {
                    video_pkt.dts(av_rescale_q(video_pkt.dts(), video_c.time_base(), video_st.time_base()));
                }
                video_pkt.stream_index(video_st.index());
            } else {
                return;
            }
        }

        synchronized (oc) {
            /* write the compressed frame in the media file */
            if (interleaved && audio_st != null) {
                if ((ret = av_interleaved_write_frame(oc, video_pkt)) < 0) {
                    throw new Exception("av_interleaved_write_frame() error " + ret + " while writing interleaved video frame.");
                }
            } else {
                if ((ret = av_write_frame(oc, video_pkt)) < 0) {
                    throw new Exception("av_write_frame() error " + ret + " while writing video frame.");
                }
            }
        }
    }

    @Override public void record(Buffer samples) throws Exception {
        if (audio_st == null) {
            throw new Exception("No audio output stream (Is audioChannels > 0 and has start() been called?)");
        }
        int ret;

        int inputSize = samples.limit() - samples.position();
        int inputFormat = AV_SAMPLE_FMT_NONE;
        int inputDepth = 0;
        int outputDepth = av_get_bytes_per_sample(audio_c.sample_fmt());
        if (samples instanceof ByteBuffer) {
            ByteBuffer b = (ByteBuffer)samples;
            inputFormat = AV_SAMPLE_FMT_U8;
            inputDepth = 1;
            if (samples_in instanceof BytePointer && samples_in.capacity() >= inputSize && b.hasArray()) {
                ((BytePointer)samples_in).position(0).put(b.array(), b.position(), inputSize);
            } else {
                samples_in = new BytePointer(b);
            }
        } else if (samples instanceof ShortBuffer) {
            ShortBuffer b = (ShortBuffer)samples;
            inputFormat = AV_SAMPLE_FMT_S16;
            inputDepth = 2;
            if (samples_in instanceof ShortPointer && samples_in.capacity() >= inputSize && b.hasArray()) {
                ((ShortPointer)samples_in).position(0).put(b.array(), samples.position(), inputSize);
            } else {
                samples_in = new ShortPointer(b);
            }
        } else if (samples instanceof IntBuffer) {
            IntBuffer b = (IntBuffer)samples;
            inputFormat = AV_SAMPLE_FMT_S32;
            inputDepth = 4;
            if (samples_in instanceof IntPointer && samples_in.capacity() >= inputSize && b.hasArray()) {
                ((IntPointer)samples_in).position(0).put(b.array(), samples.position(), inputSize);
            } else {
                samples_in = new IntPointer(b);
            }
        } else if (samples instanceof FloatBuffer) {
            FloatBuffer b = (FloatBuffer)samples;
            inputFormat = AV_SAMPLE_FMT_FLT;
            inputDepth = 4;
            if (samples_in instanceof FloatPointer && samples_in.capacity() >= inputSize && b.hasArray()) {
                ((FloatPointer)samples_in).position(0).put(b.array(), b.position(), inputSize);
            } else {
                samples_in = new FloatPointer(b);
            }
        } else if (samples instanceof DoubleBuffer) {
            DoubleBuffer b = (DoubleBuffer)samples;
            inputFormat = AV_SAMPLE_FMT_DBL;
            inputDepth = 8;
            if (samples_in instanceof DoublePointer && samples_in.capacity() >= inputSize && b.hasArray()) {
                ((DoublePointer)samples_in).position(0).put(b.array(), b.position(), inputSize);
            } else {
                samples_in = new DoublePointer(b);
            }
        } else {
            throw new Exception("Audio samples Buffer has unsupported type: " + samples);
        }

        if (samples_convert_ctx == null) {
            samples_convert_ctx = swr_alloc_set_opts(null,
                    audio_c.channel_layout(), audio_c.sample_fmt(), audio_c.sample_rate(),
                    audio_c.channel_layout(), inputFormat,          audio_c.sample_rate(), 0, null);
            if (samples_convert_ctx == null) {
                throw new Exception("swr_alloc_set_opts() error: Cannot allocate the conversion context.");
            } else if ((ret = swr_init(samples_convert_ctx)) < 0) {
                throw new Exception("swr_init() error " + ret + ": Cannot initialize the conversion context.");
            }
        }

        BytePointer samples_in_bytes = new BytePointer(samples_in).
                position(samples_in.position() * inputDepth).
                limit((samples_in.position() + inputSize) * inputDepth);
        while (samples_in_bytes.position() < samples_in_bytes.limit()) {
            int inputCount = (samples_in_bytes.limit() - samples_in_bytes.position()) / (audioChannels * inputDepth);
            int outputCount = (samples_out.limit() - samples_out.position()) / (audioChannels * outputDepth);
            int count = Math.min(inputCount, outputCount);
            if ((ret = swr_convert(samples_convert_ctx, samples_out, count, samples_in_bytes, count)) < 0) {
                throw new Exception("swr_convert() error " + ret + ": Cannot convert audio samples.");
            }
            samples_in_bytes.position(samples_in_bytes.position() + ret * audioChannels * inputDepth);
            samples_out.position(samples_out.position() + ret * audioChannels * outputDepth);

            if (samples_out.position() >= samples_out.limit()) {
                samples_out.position(0);
                frame.nb_samples(audio_input_frame_size);
                avcodec_fill_audio_frame(frame, audio_c.channels(), audio_c.sample_fmt(), samples_out, samples_out.limit(), 0);

                av_init_packet(audio_pkt);
                audio_pkt.data(audio_outbuf);
                audio_pkt.size(audio_outbuf_size);
                if ((ret = avcodec_encode_audio2(audio_c, audio_pkt, frame, got_audio_packet)) < 0) {
                    throw new Exception("avcodec_encode_audio2() error " + ret + ": Could not encode audio packet.");
                }
                if (got_audio_packet[0] != 0) {
                    if (audio_pkt.pts() != AV_NOPTS_VALUE) {
                        audio_pkt.pts(av_rescale_q(audio_pkt.pts(), audio_c.time_base(), audio_c.time_base()));
                    }
                    if (audio_pkt.dts() != AV_NOPTS_VALUE) {
                        audio_pkt.dts(av_rescale_q(audio_pkt.dts(), audio_c.time_base(), audio_c.time_base()));
                    }
                    audio_pkt.flags(audio_pkt.flags() | AV_PKT_FLAG_KEY);
                    audio_pkt.stream_index(audio_st.index());

                    /* write the compressed frame in the media file */
                    synchronized (oc) {
                        if (interleaved && video_st != null) {
                            if ((ret = av_interleaved_write_frame(oc, audio_pkt)) < 0) {
                                throw new Exception("av_interleaved_write_frame() error " + ret + " while writing interleaved audio frame.");
                            }
                        } else {
                            if ((ret = av_write_frame(oc, audio_pkt)) < 0) {
                                throw new Exception("av_write_frame() error " + ret + " while writing audio frame.");
                            }
                        }
                    }
                }
            }
        }
    }
}
