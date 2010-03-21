/*
 * Copyright (C) 2009,2010 Samuel Audet
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

package name.audet.samuel.javacv;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.nio.ByteOrder;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static net.sf.ffmpeg_java.AVCodecLibrary.*;
import static net.sf.ffmpeg_java.AVFormatLibrary.*;
import static net.sf.ffmpeg_java.AVUtilLibrary.*;
import static net.sf.ffmpeg_java.SWScaleLibrary.*;

/**
 *
 * @author Samuel Audet
 */
public class FFmpegFrameRecorder extends FrameRecorder {

    public FFmpegFrameRecorder(File file, int imageWidth, int imageHeight) {
        this(file.getAbsolutePath(), imageWidth, imageHeight);
    }
    public FFmpegFrameRecorder(String filename, int imageWidth, int imageHeight) {
        /* initialize libavcodec, and register all codecs and formats */
        av_register_all();

        this.filename    = filename;
        this.imageWidth  = imageWidth;
        this.imageHeight = imageHeight;

        this.pixelFormat = PIX_FMT_RGB32;
        this.codecID     = CODEC_ID_HUFFYUV;
        this.bitrate     = 400000;
        this.frameRate   = 30;
    }
    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() {
        try {
            release();
        } catch (Exception ex) { }
    }

    private AVFrame picture;
    private Pointer video_outbuf;
    private int video_outbuf_size;
    private AVOutputFormat fmt;
    private AVFormatContext oc;
    private AVCodecContext c;
    private AVFrame coded_frame;
    private AVOutputFormat oformat;
    private AVStream video_st;
    private SwsContext img_convert_ctx;
    private AVPacket pkt = new AVPacket();
    private PointerByReference p = new PointerByReference();

    public static final int DEFAULT_FRAME_RATE_BASE = 1001000;

    public void start() throws Exception {
        /* auto detect the output format from the name. */
        fmt = guess_format(null, filename, null);
        if (fmt == null) {
            throw new Exception("Could not find suitable output format");
        }

        /* allocate the output media context */
        oc = av_alloc_format_context();
        if (oc == null) {
            throw new Exception("Memory error");
        }
        oc.oformat = fmt.getPointer();
        byte[] a = Native.toByteArray(filename);
        System.arraycopy(a, 0, oc.filename, 0, a.length);

        /* add the audio and video streams using the format codecs
           and initialize the codecs */

        //
        // add a video output stream 
        //
        video_st = av_new_stream(oc, 0);
        if (video_st == null) {
            throw new Exception("Could not alloc stream");
        }
        video_st.setAutoSynch(false);

        c = new AVCodecContext(video_st.codec);
        c.codec_id = codecID; //fmt.video_codec;
        c.codec_type = CODEC_TYPE_VIDEO;

        /* put sample parameters */
        c.bit_rate = bitrate;
        /* resolution must be a multiple of two */
        c.width = imageWidth;
        c.height = imageHeight;
        /* time base: this is the fundamental unit of time (in seconds) in terms
           of which frame timestamps are represented. for fixed-fps content,
           timebase should be 1/framerate and timestamp increments should be
           identically 1. */
        c.time_base = new AVRational(av_d2q(1/frameRate, DEFAULT_FRAME_RATE_BASE));
        c.gop_size = 12; /* emit one intra frame every twelve frames at most */
        c.pix_fmt = pixelFormat;
        if (c.codec_id == CODEC_ID_MPEG2VIDEO) {
            /* just for testing, we also add B frames */
            c.max_b_frames = 2;
        }
        if (c.codec_id == CODEC_ID_MPEG1VIDEO) {
            /* Needed to avoid using macroblocks in which some coeffs overflow.
               This does not happen with normal video, it just happens here as
               the motion of the chroma plane does not match the luma plane. */
            c.mb_decision=2;
        }

        oformat = new AVOutputFormat(oc.oformat);
        // some formats want stream headers to be separate
        if(oformat.name.equals("mp4") || oformat.name.equals("mov") || oformat.name.equals("3gp")) {
            c.flags |= CODEC_FLAG_GLOBAL_HEADER;
        }
        c.write();


        /* set the output parameters (must be done even if no
           parameters). */
        if (av_set_parameters(oc, null) < 0) {
            throw new Exception("Invalid output format parameters");
        }

        dump_format(oc, 0, filename, 1);

        /* now that all the parameters are set, we can open the audio and
           video codecs and allocate the necessary encode buffers */
        if (video_st != null) {
            /* find the video encoder */
            AVCodec codec = avcodec_find_encoder(c.codec_id);
            if (codec == null) {
                throw new Exception("codec not found");
            }

            /* open the codec */
            if (avcodec_open(c, codec) < 0) {
                throw new Exception("could not open codec");
            }
            c.setAutoSynch(false);

            coded_frame = new AVFrame(c.coded_frame);

            /* allocate the encoded raw picture */
            picture = avcodec_alloc_frame();
            if (picture != null) {
                int size = avpicture_get_size(c.pix_fmt, c.width, c.height);
                Pointer picture_buf = av_malloc(size);
                if (picture_buf == null) {
                    av_free(picture.getPointer());
                    picture = null;
                } else {
                    avpicture_fill(picture, picture_buf, c.pix_fmt, c.width, c.height);
                }
            }
            if (picture == null) {
                avcodec_close(c);
                av_freep(video_st.getPointer());
                video_st = null;
                throw new Exception("Could not allocate picture");
            }

            video_outbuf = null;
            if ((oformat.flags & AVFMT_RAWPICTURE) == 0) {
                /* allocate output buffer */
                /* XXX: API change will be done */
                /* buffers passed into lav* can be allocated any way you prefer,
                   as long as they're aligned enough for the architecture, and
                   they're freed appropriately (such as using av_free for buffers
                   allocated with av_malloc) */
                video_outbuf_size = imageWidth*imageHeight*4; // ??
                video_outbuf = av_malloc(video_outbuf_size);
            }
        }

        /* open the output file, if needed */
        if ((fmt.flags & AVFMT_NOFILE) == 0) {
            if (url_fopen(p, filename, URL_WRONLY) < 0) {
                avcodec_close(c);
                av_free(picture.data0);
                av_free(picture.getPointer());
                av_free(video_outbuf);
                av_freep(video_st.getPointer());
                video_st = null;
                throw new Exception("Could not open '" + filename + "'");
            }
            oc.pb = p.getValue();
        }

        /* write the stream header, if any */
        av_write_header(oc);

        oc.setAutoSynch(false);
    }

    public void stop() throws Exception {
        /* close codec and free stream */
        if (video_st != null) {
            avcodec_close(c);
            av_free(picture.data0);
            av_free(picture.getPointer());
            av_free(video_outbuf);
            av_freep(video_st.getPointer());
            video_st = null;
        }

        if (oc != null) {
            /* write the trailer, if any */
            av_write_trailer(oc);

            /* free the streams */
            Pointer[] streams = oc.getStreams();
            for(int i = 0; i < oc.nb_streams; i++) {
                // double free ...
                //av_freep(new AVStream(streams[i]).codec);
                av_freep(streams[i]);
            }

            if ((fmt.flags & AVFMT_NOFILE) == 0) {
                /* close the output file */
                url_fclose(new ByteIOContext(oc.pb));
            }

            /* free the stream */
            av_free(oc.getPointer());
            oc = null;
        }
    }

    public void record(IplImage frame) throws Exception {
        record(frame, false);
    }
    public void record(IplImage frame, boolean raw) throws Exception {
        if (video_st == null) {
            throw new Exception("No video output stream");
        }

        int out_size, ret;

        if (frame == null) {
            /* no more frame to compress. The codec has a latency of a few
               frames if using B frames, so we get the last frames by
               passing the same picture again */
        } else {
            int pix_fmt = -1;
            if (frame.depth == IPL_DEPTH_8U && frame.nChannels == 3) {
                pix_fmt = PIX_FMT_BGR24;
            } else if (frame.depth == IPL_DEPTH_8U && frame.nChannels == 1) {
                pix_fmt = PIX_FMT_GRAY8;
            } else if (frame.depth == IPL_DEPTH_16U && frame.nChannels == 1) {
                pix_fmt = (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? 
                    PIX_FMT_GRAY16BE : PIX_FMT_GRAY16LE;
            } else if (frame.depth == IPL_DEPTH_8U && frame.nChannels == 4) {
                pix_fmt = PIX_FMT_BGR32;
            } else if (!raw) {
                throw new Exception("Unsupported image format");
            }

            if (c.pix_fmt != pix_fmt && !raw) {
                /* convert to the codec pixel format if needed */
                if (img_convert_ctx == null) {
                    img_convert_ctx = sws_getContext(frame.width, frame.height, pix_fmt,
                            c.width, c.height, c.pix_fmt, SWS_BICUBIC, null, null, null);
                    if (img_convert_ctx == null) {
                        throw new Exception("Cannot initialize the conversion context");
                    }
                }
                p.setValue(frame.imageData);
                sws_scale(img_convert_ctx, p.getPointer(), new int[] { frame.widthStep },
                          0, c.height, picture.getPointer(), picture.linesize);
            } else {
                picture.data0 = frame.imageData;
                picture.linesize[0] = frame.widthStep;
                picture.write();
            }
        }


        if ((oformat.flags & AVFMT_RAWPICTURE) != 0) {
            /* raw video case. The API will change slightly in the near
               futur for that */
            av_init_packet(pkt);

            pkt.flags |= PKT_FLAG_KEY;
            pkt.stream_index= video_st.index;
            pkt.data= picture.getPointer();
            pkt.size= picture.size();

            ret = av_write_frame(oc, pkt);
        } else {
            /* encode the image */
            out_size = avcodec_encode_video(c, video_outbuf, video_outbuf_size, picture);
            /* if zero size, it means the image was buffered */
            if (out_size > 0) {
                av_init_packet(pkt);
                coded_frame.read();
                if (coded_frame.pts != AV_NOPTS_VALUE)
                    pkt.pts= av_rescale_q(coded_frame.pts, c.time_base.byValue(), video_st.time_base.byValue());
                if (coded_frame.key_frame != 0)
                    pkt.flags |= PKT_FLAG_KEY;
                pkt.stream_index= video_st.index;
                pkt.data= video_outbuf;
                pkt.size= out_size;

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
