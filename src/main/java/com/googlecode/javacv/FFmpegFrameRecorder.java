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
 * Based on the output_example.c file included in some version of FFmpeg 0.4.9,
 * which includes the following copyright notice:
 *
 * Libavformat API example: Output a media file in any supported
 * libavformat format. The default codecs are used.
 *
 * Copyright (c) 2003 Fabrice Bellard
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

    public FFmpegFrameRecorder(File file, int imageWidth, int imageHeight) {
        this(file.getAbsolutePath(), imageWidth, imageHeight);
    }
    public FFmpegFrameRecorder(String filename, int imageWidth, int imageHeight) {
        /* initialize libavcodec, and register all codecs and formats */
        av_register_all();

        this.filename    = filename;
        this.imageWidth  = imageWidth;
        this.imageHeight = imageHeight;

        this.pixelFormat = PIX_FMT_NONE;
        this.codecID     = CODEC_ID_NONE;
        this.bitrate     = 400000;
        this.frameRate   = 30;

        this.pkt         = new AVPacket();
        this.tempPicture = new AVPicture();
    }
    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private String filename;
    private AVFrame picture;
    private BytePointer video_outbuf;
    private int video_outbuf_size;
    private AVOutputFormat oformat;
    private AVFormatContext oc;
    private AVCodecContext c;
    private AVStream video_st;
    private SwsContext img_convert_ctx;
    private AVPacket pkt;
    private AVPicture tempPicture;
    private int frameNumber;

    public static final int DEFAULT_FRAME_RATE_BASE = 1001000;

    public void start() throws Exception {
        frameNumber = 0;

        /* auto detect the output format from the name. */
        String formatName = format == null || format.length() == 0 ? null : format;
        oformat = av_guess_format(formatName, filename, null);
        if (oformat == null) {
            throw new Exception("Could not find suitable output format");
        }

        /* allocate the output media context */
        oc = avformat_alloc_context();
        if (oc == null) {
            throw new Exception("Memory error");
        }

        oc.oformat(oformat);
        oc.filename(filename);

        /* add the audio and video streams using the format codecs
           and initialize the codecs */

        //
        // add a video output stream 
        //
        video_st = av_new_stream(oc, 0);
        if (video_st == null) {
            throw new Exception("Could not alloc stream");
        }

        c = video_st.codec();

        // some formats want stream headers to be separate
        String name = oformat.name().getString();
        if (name.equals("mp4") || name.equals("mov") || name.equals("3gp")) {
            c.flags(c.flags() | CODEC_FLAG_GLOBAL_HEADER);
        }
        if (codecID == CODEC_ID_NONE) {
            if ("mp4".equals(name)) {
                codecID = CODEC_ID_MPEG4;
            } else if ("3gp".equals(name)) {
                codecID = CODEC_ID_H263;
            } else if ("avi".equals(name)) {
                codecID = CODEC_ID_HUFFYUV;
            }
        }
        c.codec_id(codecID == CODEC_ID_NONE ? oformat.video_codec() : codecID);
        c.codec_type(CODEC_TYPE_VIDEO);

        /* put sample parameters */
        c.bit_rate(bitrate);
        /* resolution must be a multiple of two */
        c.width(imageWidth);
        c.height(imageHeight);
        /* time base: this is the fundamental unit of time (in seconds) in terms
           of which frame timestamps are represented. for fixed-fps content,
           timebase should be 1/framerate and timestamp increments should be
           identically 1. */
        c.time_base(av_d2q(1/frameRate, DEFAULT_FRAME_RATE_BASE));
        c.gop_size(12); /* emit one intra frame every twelve frames at most */

        if (pixelFormat == PIX_FMT_NONE) {
            if (c.codec_id() == CODEC_ID_HUFFYUV || c.codec_id() == CODEC_ID_FFV1 ||
                    c.codec_id() == CODEC_ID_PNG || c.codec_id() == CODEC_ID_RAWVIDEO) {
                pixelFormat = PIX_FMT_RGB32; // appropriate for common lossless formats
            } else {
                pixelFormat = PIX_FMT_YUV420P; // lossy, but works with about everything
            }
        }
        c.pix_fmt(pixelFormat);

        if (c.codec_id() == CODEC_ID_MPEG2VIDEO) {
            /* just for testing, we also add B frames */
            c.max_b_frames(2);
        } else if (c.codec_id() == CODEC_ID_MPEG1VIDEO) {
            /* Needed to avoid using macroblocks in which some coeffs overflow.
               This does not happen with normal video, it just happens here as
               the motion of the chroma plane does not match the luma plane. */
            c.mb_decision(2);
        } else if (c.codec_id() == CODEC_ID_H263) {
            if (imageWidth <= 128 && imageHeight <= 96) {
                c.width(128).height(96);
            } else if (imageWidth <= 176 && imageHeight <= 144) {
                c.width(176).height(144);
            } else if (imageWidth <= 352 && imageHeight <= 288) {
                c.width(352).height(288);
            } else if (imageWidth <= 704 && imageHeight <= 576) {
                c.width(704).height(576);
            } else {
                c.width(1408).height(1152);
            }
        } else if (c.codec_id() == CODEC_ID_H264) {
            // libx264-default.ffpreset
            c.coder_type(1); // coder=1
            c.flags(c.flags() | CODEC_FLAG_LOOP_FILTER); // flags=+loop
            c.me_cmp(c.me_cmp() | 1); // cmp=+chroma
            c.partitions(c.partitions() | c.X264_PART_I8X8 | c.X264_PART_I4X4 | c.X264_PART_P8X8 |
                    c.X264_PART_B8X8); // partitions=+parti8x8+parti4x4+partp8x8+partb8x8
            c.me_method(ME_HEX); // me_method=hex
            c.me_subpel_quality(7); // subq=7
            c.me_range(16); // me_range=16
            c.gop_size(250); // g=250
            c.keyint_min(25); // keyint_min=25
            c.scenechange_threshold(40); // sc_threshold=40
            c.i_quant_factor(0.71f); // i_qfactor=0.71
            c.b_frame_strategy(1); // b_strategy=1
            c.qcompress(0.6f); // qcomp=0.6
            c.qmin(10); // qmin=10
            c.qmax(51); // qmax=51
            c.max_qdiff(4); // qdiff=4
            c.max_b_frames(3); // bf=3
            c.refs(3); // refs=3
            c.directpred(1); // directpred=1
            c.trellis(1); // trellis=1
            c.flags2(c.flags2() | CODEC_FLAG2_BPYRAMID | CODEC_FLAG2_MIXED_REFS | CODEC_FLAG2_WPRED |
                    CODEC_FLAG2_8X8DCT | CODEC_FLAG2_FASTPSKIP); // flags2=+bpyramid+mixed_refs+wpred+dct8x8+fastpskip
            c.weighted_p_pred(2); // wpredp=2

            // libx264-baseline.ffpreset (works on mobile devices with no real tradeoffs)
            c.coder_type(0); // coder=0
            c.max_b_frames(0); // bf=0
            c.flags2(c.flags2() & ~CODEC_FLAG2_WPRED & ~CODEC_FLAG2_8X8DCT); // flags2=-wpred-dct8x8
            c.weighted_p_pred(0); // wpredp=0
        }

        /* set the output parameters (must be done even if no parameters). */
        if (av_set_parameters(oc, null) < 0) {
            av_freep(video_st);
            video_st = null;
            throw new Exception("Invalid output format parameters");
        }

        dump_format(oc, 0, filename, 1);

        /* now that all the parameters are set, we can open the audio and
           video codecs and allocate the necessary encode buffers */
        if (video_st != null) {
            /* find the video encoder */
            AVCodec codec = avcodec_find_encoder(c.codec_id());
            if (codec == null) {
                av_freep(video_st);
                video_st = null;
                throw new Exception("Codec not found");
            }

            /* open the codec */
            if (avcodec_open(c, codec) < 0) {
                av_freep(video_st);
                video_st = null;
                throw new Exception("Could not open codec");
            }

            /* allocate the encoded raw picture */
            picture = avcodec_alloc_frame();
            if (picture != null) {
                int size = avpicture_get_size(c.pix_fmt(), c.width(), c.height());
                BytePointer picture_buf = new BytePointer(av_malloc(size));
                if (picture_buf == null) {
                    av_free(picture);
                    picture = null;
                } else {
                    avpicture_fill(picture, picture_buf, c.pix_fmt(), c.width(), c.height());
                }
            }
            if (picture == null) {
                avcodec_close(c);
                av_freep(video_st);
                video_st = null;
                throw new Exception("Could not allocate picture");
            }

            video_outbuf = null;
            if ((oformat.flags() & AVFMT_RAWPICTURE) == 0) {
                /* allocate output buffer */
                /* XXX: API change will be done */
                /* buffers passed into lav* can be allocated any way you prefer,
                   as long as they're aligned enough for the architecture, and
                   they're freed appropriately (such as using av_free for buffers
                   allocated with av_malloc) */
                video_outbuf_size = Math.max(256 * 1024, 8 * c.width() * c.height()); // a la ffmpeg.c
                video_outbuf = new BytePointer(av_malloc(video_outbuf_size));
            }
        }

        /* open the output file, if needed */
        if ((oformat.flags() & AVFMT_NOFILE) == 0) {
            ByteIOContext p = new ByteIOContext(null);
            if (url_fopen(p, filename, URL_WRONLY) < 0) {
                avcodec_close(c);
                av_free(picture.data(0));
                av_free(picture);
                av_free(video_outbuf);
                av_freep(video_st);
                video_st = null;
                throw new Exception("Could not open '" + filename + "'");
            }
            oc.pb(p);
        }

        /* write the stream header, if any */
        av_write_header(oc);
    }

    public void stop() throws Exception {
        if (oc != null) {
            /* write the trailer, if any */
            av_write_trailer(oc);
        }

        /* close codec and free stream */
        if (video_st != null) {
            avcodec_close(c);
            av_free(picture.data(0));
            av_free(picture);
            av_free(video_outbuf);
            av_freep(video_st);
            video_st = null;
        }

        if (oc != null) {
            /* free the streams */
            int nb_streams = oc.nb_streams();
            for(int i = 0; i < nb_streams; i++) {
                // double free ...
                //av_freep(oc.streams(i).codec());
                av_freep(oc.streams(i));
            }

            if ((oformat.flags() & AVFMT_NOFILE) == 0) {
                /* close the output file */
                url_fclose(oc.pb());
            }

            /* free the stream */
            av_free(oc);
            oc = null;
        }
    }

    public void record(IplImage frame) throws Exception {
        record(frame, PIX_FMT_NONE);
    }
    public void record(IplImage frame, int pixelFormat) throws Exception {
        if (video_st == null) {
            throw new Exception("No video output stream (Has start() been called?)");
        }

        int out_size, ret;

        if (frame == null) {
            /* no more frame to compress. The codec has a latency of a few
               frames if using B frames, so we get the last frames by
               passing the same picture again */
        } else {
            int width = frame.width();
            int height = frame.height();
            int step = frame.widthStep();
            BytePointer data = frame.imageData();

            if (pixelFormat == PIX_FMT_NONE) {
                int depth = frame.depth();
                int channels = frame.nChannels();
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

            if (c.pix_fmt() != pixelFormat) {
                /* convert to the codec pixel format if needed */
                if (img_convert_ctx == null) {
                    img_convert_ctx = sws_getContext(width, height, pixelFormat,
                            c.width(), c.height(), c.pix_fmt(), SWS_BILINEAR, null, null, null);
                    if (img_convert_ctx == null) {
                        throw new Exception("Cannot initialize the conversion context");
                    }
                }
                avpicture_fill(tempPicture, data, pixelFormat, width, height);
                //tempPicture.data(0, data);
                tempPicture.linesize(0, step);
                sws_scale(img_convert_ctx, new PointerPointer(tempPicture), tempPicture.linesize(),
                          0, c.height(), new PointerPointer(picture), picture.linesize());
            } else {
                avpicture_fill(picture, data, c.pix_fmt(), c.width(), c.height());
                //picture.data(0, data);
                picture.linesize(0, step);
            }
        }


        if ((oformat.flags() & AVFMT_RAWPICTURE) != 0) {
            /* raw video case. The API will change slightly in the near
               futur for that */
            av_init_packet(pkt);

            pkt.flags(pkt.flags() | PKT_FLAG_KEY);
            pkt.stream_index(video_st.index());
            pkt.data(new BytePointer(picture));
            pkt.size(Loader.sizeof(AVPicture.class));

            ret = av_write_frame(oc, pkt);
        } else {
            picture.pts(frameNumber++); // magic required by libx264
            /* encode the image */
            out_size = avcodec_encode_video(c, video_outbuf, video_outbuf_size, picture);
            /* if zero size, it means the image was buffered */
            if (out_size > 0) {
                av_init_packet(pkt);
                AVFrame coded_frame = c.coded_frame();
                long pts = coded_frame.pts();
                if (coded_frame.pts() != AV_NOPTS_VALUE)
                    pkt.pts(av_rescale_q(pts, c.time_base(), video_st.time_base()));
                if (coded_frame.key_frame() != 0)
                    pkt.flags(pkt.flags() | PKT_FLAG_KEY);
                pkt.stream_index(video_st.index());
                pkt.data(video_outbuf);
                pkt.size(out_size);

                /* write the compressed frame in the media file */
                ret = av_write_frame(oc, pkt);
            } else {
                ret = 0;
            }
        }
        if (ret != 0) {
            throw new Exception("Error while writing video frame");
        }
    }
}
