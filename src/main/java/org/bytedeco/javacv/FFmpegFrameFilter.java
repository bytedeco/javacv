/*
 * Copyright (C) 2015 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Based on the filtering_video.c file included in FFmpeg 2.7.1
 * which is covered by the following copyright notice:
 *
 * Copyright (c) 2010 Nicolas George
 * Copyright (c) 2011 Stefano Sabatini
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

package org.bytedeco.javacv;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;

import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avfilter.*;
import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avutil.*;

/**
 * A {@link FrameFilter} that uses FFmpeg to filter frames. We can refer to
 * <a href="https://ffmpeg.org/ffmpeg-filters.html">FFmpeg Filters Documentation</a>
 * to get a list of filters and the options we can use. The input image width and
 * height must be specified on the constructor, while other optional values may be
 * set via corresponding properties.
 *
 * @author Samuel Audet
 */
public class FFmpegFrameFilter extends FrameFilter {

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.javacpp.avutil.class);
                Loader.load(org.bytedeco.javacpp.avcodec.class);
                Loader.load(org.bytedeco.javacpp.avformat.class);
                Loader.load(org.bytedeco.javacpp.postproc.class);
                Loader.load(org.bytedeco.javacpp.swresample.class);
                Loader.load(org.bytedeco.javacpp.swscale.class);
                Loader.load(org.bytedeco.javacpp.avfilter.class);

                av_register_all();
                avfilter_register_all();
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception("Failed to load " + FFmpegFrameRecorder.class, t);
                }
            }
        }
    }

    static {
        try {
            tryLoad();
        } catch (Exception ex) { }
    }

    public FFmpegFrameFilter(String filters, int imageWidth, int imageHeight) {
        this.filters = filters;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.pixelFormat = AV_PIX_FMT_BGR24;
        this.frameRate = 30;
        this.aspectRatio = 0;
    }
    public void release() throws Exception {
        synchronized (org.bytedeco.javacpp.avfilter.class) {
            releaseUnsafe();
        }
    }
    void releaseUnsafe() throws Exception {
        if (filter_graph != null) {
            avfilter_graph_free(filter_graph);
            buffersink_ctx = null;
            buffersrc_ctx = null;
            filter_graph = null;
        }
        if (image_frame != null) {
            av_frame_free(image_frame);
            image_frame = null;
        }
        if (filt_frame != null) {
            av_frame_free(filt_frame);
            filt_frame = null;
        }
        frame = null;
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    AVFilterContext buffersink_ctx;
    AVFilterContext buffersrc_ctx;
    AVFilterGraph filter_graph;

    AVPacket packet;
    AVFrame image_frame;
    AVFrame filt_frame;

    BytePointer[] image_ptr;
    Buffer[] image_buf;
    Frame frame;

    public void start() throws Exception {
        synchronized (org.bytedeco.javacpp.avfilter.class) {
            startUnsafe();
        }
    }
    void startUnsafe() throws Exception {
        image_frame = av_frame_alloc();
        filt_frame = av_frame_alloc();
        image_ptr = new BytePointer[] { null };
        image_buf = new Buffer[] { null };
        frame = new Frame();

        if (image_frame == null || filt_frame == null) {
            throw new Exception("Could not allocate frame");
        }

        int ret;
        AVFilter buffersrc  = avfilter_get_by_name("buffer");
        AVFilter buffersink = avfilter_get_by_name("buffersink");
        AVFilterInOut outputs = avfilter_inout_alloc();
        AVFilterInOut inputs  = avfilter_inout_alloc();
        AVRational time_base = av_inv_q(av_d2q(frameRate, 1001000));
        int pix_fmts[] = { pixelFormat, AV_PIX_FMT_NONE };

        try {
            filter_graph = avfilter_graph_alloc();
            if (outputs == null || inputs == null || filter_graph == null) {
                throw new Exception("Could not allocate filter graph: Out of memory?");
            }

            /* buffer video source: the decoded frames from the decoder will be inserted here. */
            AVRational r = av_d2q(aspectRatio > 0 ? aspectRatio : 1, 255);
            String args = String.format(
                    "video_size=%dx%d:pix_fmt=%d:time_base=%d/%d:pixel_aspect=%d/%d",
                    imageWidth, imageHeight, pixelFormat, time_base.num(), time_base.den(), r.num(), r.den());
            ret = avfilter_graph_create_filter(buffersrc_ctx = new AVFilterContext(), buffersrc, "in",
                                               args, null, filter_graph);
            if (ret < 0) {
                throw new Exception("avfilter_graph_create_filter(): Cannot create buffer source.");
            }

            /* buffer video sink: to terminate the filter chain. */
            ret = avfilter_graph_create_filter(buffersink_ctx = new AVFilterContext(), buffersink, "out",
                                               null, null, filter_graph);
            if (ret < 0) {
                throw new Exception("avfilter_graph_create_filter(): Cannot create buffer sink.");
            }
//            ret = av_opt_set_bin(buffersink_ctx, "pix_fmts", new BytePointer(new IntPointer(pix_fmts)), 4, AV_OPT_SEARCH_CHILDREN);
//            if (ret < 0) {
//                throw new Exception("av_opt_set_bin(): Cannot set output pixel format.");
//            }

            /*
             * Set the endpoints for the filter graph. The filter_graph will
             * be linked to the graph described by filters_descr.
             */

            /*
             * The buffer source output must be connected to the input pad of
             * the first filter described by filters_descr; since the first
             * filter input label is not specified, it is set to "in" by
             * default.
             */
            outputs.name(av_strdup(new BytePointer("in")));
            outputs.filter_ctx(buffersrc_ctx);
            outputs.pad_idx(0);
            outputs.next(null);

            /*
             * The buffer sink input must be connected to the output pad of
             * the last filter described by filters_descr; since the last
             * filter output label is not specified, it is set to "out" by
             * default.
             */
            inputs.name(av_strdup(new BytePointer("out")));
            inputs.filter_ctx(buffersink_ctx);
            inputs.pad_idx(0);
            inputs.next(null);
            if ((ret = avfilter_graph_parse_ptr(filter_graph, filters,
                                                inputs, outputs, null)) < 0) {
                throw new Exception("avfilter_graph_parse_ptr()");
            }
            if ((ret = avfilter_graph_config(filter_graph, null)) < 0) {
                throw new Exception("avfilter_graph_config()");
            }
        } finally {
            avfilter_inout_free(inputs);
            avfilter_inout_free(outputs);
        }
    }

    public void stop() throws Exception {
        release();
    }

    public void push(Frame frame) throws Exception {
        push(frame, AV_PIX_FMT_NONE);
    }
    public void push(Frame frame, int pixelFormat) throws Exception {
        if (frame.image != null) {
            pushImage(frame.imageWidth, frame.imageHeight, frame.imageDepth,
                    frame.imageChannels, frame.imageStride, pixelFormat, frame.image);
        }
        if (frame.samples != null) {
//            pushSamples(frame.sampleRate, frame.audioChannels, frame.samples);
        }
    }

    public void pushImage(int width, int height, int depth, int channels, int stride, int pixelFormat, Buffer ... image) throws Exception {
        int step = stride * Math.abs(depth) / 8;
        BytePointer data = image[0] instanceof ByteBuffer
                ? new BytePointer((ByteBuffer)image[0].position(0))
                : new BytePointer(new Pointer(image[0].position(0)));

        if (pixelFormat == AV_PIX_FMT_NONE) {
            if ((depth == Frame.DEPTH_UBYTE || depth == Frame.DEPTH_BYTE) && channels == 3) {
                pixelFormat = AV_PIX_FMT_BGR24;
            } else if ((depth == Frame.DEPTH_UBYTE || depth == Frame.DEPTH_BYTE) && channels == 1) {
                pixelFormat = AV_PIX_FMT_GRAY8;
            } else if ((depth == Frame.DEPTH_USHORT || depth == Frame.DEPTH_SHORT) && channels == 1) {
                pixelFormat = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ?
                        AV_PIX_FMT_GRAY16BE : AV_PIX_FMT_GRAY16LE;
            } else if ((depth == Frame.DEPTH_UBYTE || depth == Frame.DEPTH_BYTE) && channels == 4) {
                pixelFormat = AV_PIX_FMT_RGBA;
            } else if ((depth == Frame.DEPTH_UBYTE || depth == Frame.DEPTH_BYTE) && channels == 2) {
                pixelFormat = AV_PIX_FMT_NV21; // Android's camera capture format
                step = width;
            } else {
                throw new Exception("Could not guess pixel format of image: depth=" + depth + ", channels=" + channels);
            }
        }

        avpicture_fill(new AVPicture(image_frame), data, pixelFormat, width, height);
        image_frame.linesize(0, step);
        image_frame.format(pixelFormat);
        image_frame.width(width);
        image_frame.height(height);

        /* push the decoded frame into the filtergraph */
        if (av_buffersrc_add_frame_flags(buffersrc_ctx, image_frame, AV_BUFFERSRC_FLAG_KEEP_REF) < 0) {
            throw new Exception("av_buffersrc_add_frame_flags(): Error while feeding the filtergraph.");
        }
    }

    public Frame pull() throws Exception {
        av_frame_unref(filt_frame);

        /* pull a filtered frame from the filtergraph */
        int ret = av_buffersink_get_frame(buffersink_ctx, filt_frame);
        if (ret == -11 /*AVERROR(EAGAIN)*/ || ret == AVERROR_EOF) {
            return null;
        } else if (ret < 0) {
            throw new Exception("av_buffersink_get_frame(): Error occurred: "
                    + av_make_error_string(new BytePointer(256), 256, ret).getString());
        }
        frame.imageWidth  = filt_frame.width();
        frame.imageHeight = filt_frame.height();
        frame.imageDepth = Frame.DEPTH_UBYTE;
        if (filt_frame.data(1) == null) {
            frame.imageStride = filt_frame.linesize(0);
            BytePointer ptr = filt_frame.data(0);
            if (ptr != null && !ptr.equals(image_ptr[0])) {
                image_ptr[0] = ptr.capacity(frame.imageHeight * frame.imageStride);
                image_buf[0] = ptr.asBuffer();
            }
            frame.image = image_buf;
            frame.image[0].position(0).limit(frame.imageHeight * frame.imageStride);
            frame.imageChannels = frame.imageStride / frame.imageWidth;
        } else {
            frame.imageStride = frame.imageWidth;
            int size = avpicture_get_size(filt_frame.format(), frame.imageWidth, frame.imageHeight);
            if (image_ptr[0] == null || image_ptr[0].capacity() < size) {
                image_ptr[0] = new BytePointer(size);
                image_buf[0] = image_ptr[0].asBuffer();
            }
            frame.image = image_buf;
            frame.image[0].position(0).limit(size);
            frame.imageChannels = 2;
            ret = avpicture_layout(new AVPicture(filt_frame), filt_frame.format(),
                    frame.imageWidth, frame.imageHeight, image_ptr[0].position(0), image_ptr[0].capacity());
        }
        return frame;
    }
}
