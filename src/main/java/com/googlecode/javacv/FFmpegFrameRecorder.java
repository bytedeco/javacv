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
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.PointerPointer;
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

        this.filename      = filename;
        this.imageWidth    = imageWidth;
        this.imageHeight   = imageHeight;
        this.audioChannels = audioChannels;

        this.pixelFormat   = PIX_FMT_NONE;
        this.videoCodec    = CODEC_ID_NONE;
        this.videoBitrate  = 400000;
        this.frameRate     = 30;

        this.sampleFormat  = AV_SAMPLE_FMT_S16;
        this.audioCodec    = CODEC_ID_NONE;
        this.audioBitrate  = 64000;
        this.sampleRate    = 44100;

        this.pkt = new AVPacket();
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
        if (samplesPointer != null) {
            av_free(samplesPointer);
            samplesPointer = null;
            samplesBuffer = null;
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
    private BytePointer samplesPointer;
    private ByteBuffer samplesBuffer;
    private BytePointer audio_outbuf;
    private int audio_outbuf_size;
    private int audio_input_frame_size;
    private AVOutputFormat oformat;
    private AVFormatContext oc;
    private AVCodec video_codec, audio_codec;
    private AVCodecContext video_c, audio_c;
    private AVStream video_st, audio_st;
    private SwsContext img_convert_ctx;
    private AVPacket pkt;
    private int[] got_packet;

    public void start() throws Exception {
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
        got_packet = new int[1];

        /* auto detect the output format from the name. */
        String format_name = format == null || format.length() == 0 ? null : format;
        oformat = av_guess_format(format_name, filename, null);
        if (oformat == null) {
            throw new Exception("Could not find suitable output format");
        }
        format_name = oformat.name().getString();

        /* allocate the output media context */
        oc = avformat_alloc_context();
        if (oc == null) {
            throw new Exception("Could not allocate format context");
        }

        oc.oformat(oformat);
        oc.filename(filename);

        /* add the audio and video streams using the format codecs
           and initialize the codecs */

        if (imageWidth > 0 && imageHeight > 0) {
            if (videoCodec != CODEC_ID_NONE) {
                oformat.video_codec(videoCodec);
            } else if ("mp4".equals(format_name)) {
                oformat.video_codec(CODEC_ID_MPEG4);
            } else if ("3gp".equals(format_name)) {
                oformat.video_codec(CODEC_ID_H263);
            } else if ("avi".equals(format_name)) {
                oformat.video_codec(CODEC_ID_HUFFYUV);
            }

            /* find the video encoder */
            video_codec = avcodec_find_encoder(oformat.video_codec());
            if (video_codec == null) {
                release();
                throw new Exception("Video codec not found");
            }

            /* add a video output stream */
            video_st = avformat_new_stream(oc, video_codec);
            if (video_st == null) {
                release();
                throw new Exception("Could not allocate video stream");
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
                // use constrained baseline to produce content that plays back on anything,
                // without any significant tradeoffs for most use cases
                video_c.profile(AVCodecContext.FF_PROFILE_H264_CONSTRAINED_BASELINE);
                av_opt_set(video_c.priv_data(), "profile", "baseline", 0);
                av_opt_set(video_c.priv_data(), "preset", "medium", 0);
            }

            // some formats want stream headers to be separate
            if ((oformat.flags() & AVFMT_GLOBALHEADER) != 0) {
                video_c.flags(video_c.flags() | CODEC_FLAG_GLOBAL_HEADER);
            }
        }

        /*
         * add an audio output stream
         */
        if (audioChannels > 0) {
            if (audioCodec != CODEC_ID_NONE) {
                oformat.audio_codec(audioCodec);
            }

            /* find the audio encoder */
            audio_codec = avcodec_find_encoder(oformat.audio_codec());
            if (audio_codec == null) {
                release();
                throw new Exception("Audio codec not found");
            }

            audio_st = avformat_new_stream(oc, audio_codec);
            if (audio_st == null) {
                release();
                throw new Exception("Could not allocate audio stream");
            }
            audio_c = audio_st.codec();
            audio_c.codec_id(oformat.audio_codec());
            audio_c.codec_type(AVMEDIA_TYPE_AUDIO);

            /* put sample parameters */
            audio_c.bit_rate(audioBitrate);
            audio_c.sample_rate(sampleRate);
            audio_c.channels(audioChannels);
            audio_c.sample_fmt(sampleFormat);
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
        }

        av_dump_format(oc, 0, filename, 1);

        /* now that all the parameters are set, we can open the audio and
           video codecs and allocate the necessary encode buffers */
        if (video_st != null) {
            /* open the codec */
            if (avcodec_open2(video_c, video_codec, null) < 0) {
                release();
                throw new Exception("Could not open video codec");
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
            picture = avcodec_alloc_frame();
            if (picture == null) {
                release();
                throw new Exception("Could not allocate picture");
            }
            picture.pts(0); // magic required by libx264

            int size = avpicture_get_size(video_c.pix_fmt(), video_c.width(), video_c.height());
            picture_buf = new BytePointer(av_malloc(size));
            if (picture_buf == null) {
                release();
                throw new Exception("Could not allocate picture buffer");
            }

            /* if the output format is not equal to the image format, then a temporary
               picture is needed too. It is then converted to the required output format */
            tmp_picture = avcodec_alloc_frame();
            if (tmp_picture == null) {
                release();
                throw new Exception("Could not allocate temporary picture");
            }
        }

        if (audio_st != null) {
            /* open the codec */
            if (avcodec_open2(audio_c, audio_codec, null) < 0) {
                release();
                throw new Exception("Could not open audio codec");
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
            int bufferSize = av_samples_get_buffer_size(null, audio_c.channels(), audio_input_frame_size, sampleFormat, 1);
            samplesPointer = new BytePointer(av_malloc(bufferSize));
            samplesBuffer = samplesPointer.capacity(bufferSize).asBuffer();

            /* allocate the audio frame */
            frame = avcodec_alloc_frame();
            if (frame == null) {
                release();
                throw new Exception("Could not allocate audio frame");
            }
        }

        /* open the output file, if needed */
        if ((oformat.flags() & AVFMT_NOFILE) == 0) {
            AVIOContext pb = new AVIOContext(null);
            if (avio_open(pb, filename, AVIO_FLAG_WRITE) < 0) {
                release();
                throw new Exception("Could not open '" + filename + "'");
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
                    throw new Exception("Cannot initialize the conversion context");
                }
                avpicture_fill(tmp_picture, data, pixelFormat, width, height);
                avpicture_fill(picture, picture_buf, video_c.pix_fmt(), video_c.width(), video_c.height());
                tmp_picture.linesize(0, step);
                sws_scale(img_convert_ctx, new PointerPointer(tmp_picture), tmp_picture.linesize(),
                          0, video_c.height(), new PointerPointer(picture), picture.linesize());
            } else {
                avpicture_fill(picture, data, pixelFormat, width, height);
                picture.linesize(0, step);
            }
        }

        if ((oformat.flags() & AVFMT_RAWPICTURE) != 0) {
            /* raw video case. The API may change slightly in the future for that? */
            av_init_packet(pkt);
            pkt.flags(pkt.flags() | AV_PKT_FLAG_KEY);
            pkt.stream_index(video_st.index());
            pkt.data(new BytePointer(picture));
            pkt.size(Loader.sizeof(AVPicture.class));

            if (audio_st != null) {
                ret = av_interleaved_write_frame(oc, pkt);
            } else {
                ret = av_write_frame(oc, pkt);
            }
        } else {
            /* encode the image */
            av_init_packet(pkt);
            pkt.data(video_outbuf);
            pkt.size(video_outbuf_size);
            ret = avcodec_encode_video2(video_c, pkt, picture, got_packet);
            /* if zero size, it means the image was buffered */
            if (got_packet[0] != 0) {
                if (pkt.pts() != AV_NOPTS_VALUE) {
                    pkt.pts(av_rescale_q(pkt.pts(), video_c.time_base(), video_st.time_base()));
                }
                if (pkt.dts() != AV_NOPTS_VALUE) {
                    pkt.dts(av_rescale_q(pkt.dts(), video_c.time_base(), video_st.time_base()));
                }
                pkt.stream_index(video_st.index());

                /* write the compressed frame in the media file */
                if (audio_st != null) {
                    ret = av_interleaved_write_frame(oc, pkt);
                } else {
                    ret = av_write_frame(oc, pkt);
                }
            }
            picture.pts(picture.pts() + 1); // magic required by libx264
        }
        if (ret != 0) {
            throw new Exception("Error while writing video frame");
        }
    }

    @Override public void record(Buffer samples) throws Exception {
        if (audio_st == null) {
            throw new Exception("No audio output stream (Is audioChannels > 0 and has start() been called?)");
        }
        int ret;

        while (samples.hasRemaining()) {
            if (samples instanceof ByteBuffer) {
                ByteBuffer b = (ByteBuffer)samples;
                while (samplesBuffer.hasRemaining() && b.hasRemaining()) {
                    samplesBuffer.put(b.get());
                }
            } else if (samples instanceof ShortBuffer) {
                ShortBuffer b = (ShortBuffer)samples;
                while (samplesBuffer.hasRemaining() && b.hasRemaining()) {
                    samplesBuffer.putShort(b.get());
                }
            } else if (samples instanceof IntBuffer) {
                IntBuffer b = (IntBuffer)samples;
                while (samplesBuffer.hasRemaining() && b.hasRemaining()) {
                    samplesBuffer.putInt(b.get());
                }
            } else if (samples instanceof FloatBuffer) {
                FloatBuffer b = (FloatBuffer)samples;
                while (samplesBuffer.hasRemaining() && b.hasRemaining()) {
                    samplesBuffer.putFloat(b.get());
                }
            } else if (samples instanceof DoubleBuffer) {
                DoubleBuffer b = (DoubleBuffer)samples;
                while (samplesBuffer.hasRemaining() && b.hasRemaining()) {
                    samplesBuffer.putDouble(b.get());
                }
            } else {
                assert false;
            }
            if (!samplesBuffer.hasRemaining()) {
                samplesBuffer.clear();

                frame.nb_samples(audio_input_frame_size);
                avcodec_fill_audio_frame(frame, audioChannels, sampleFormat, samplesPointer, samplesPointer.capacity(), 0);

                av_init_packet(pkt);
                pkt.data(audio_outbuf);
                pkt.size(audio_outbuf_size);
                ret = avcodec_encode_audio2(audio_c, pkt, frame, got_packet);
                if (got_packet[0] != 0) {
                    if (pkt.pts() != AV_NOPTS_VALUE) {
                        pkt.pts(av_rescale_q(pkt.pts(), audio_c.time_base(), audio_c.time_base()));
                    }
                    if (pkt.dts() != AV_NOPTS_VALUE) {
                        pkt.dts(av_rescale_q(pkt.dts(), audio_c.time_base(), audio_c.time_base()));
                    }
                    pkt.flags(pkt.flags() | AV_PKT_FLAG_KEY);
                    pkt.stream_index(audio_st.index());

                    /* write the compressed frame in the media file */
                    if (video_st != null) {
                        ret = av_interleaved_write_frame(oc, pkt);
                    } else {
                        ret = av_write_frame(oc, pkt);
                    }
                }
                if (ret != 0) {
                    throw new Exception("Error while writing audio frame");
                }
            }
        }
    }
}
