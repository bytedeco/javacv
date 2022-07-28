/*
 * Copyright (C) 2015-2022 Samuel Audet
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
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Locale;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerScope;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.ShortPointer;

import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avfilter.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avfilter.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

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

    public static class Exception extends FrameFilter.Exception {
        public Exception(String message) { super(message + " (For more details, make sure FFmpegLogCallback.set() has been called.)"); }
        public Exception(String message, Throwable cause) { super(message, cause); }
    }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.ffmpeg.global.avutil.class);
                Loader.load(org.bytedeco.ffmpeg.global.avcodec.class);
                Loader.load(org.bytedeco.ffmpeg.global.avformat.class);
                Loader.load(org.bytedeco.ffmpeg.global.postproc.class);
                Loader.load(org.bytedeco.ffmpeg.global.swresample.class);
                Loader.load(org.bytedeco.ffmpeg.global.swscale.class);
                Loader.load(org.bytedeco.ffmpeg.global.avfilter.class);

//                av_register_all();
//                avfilter_register_all();
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

    public FFmpegFrameFilter(String videoFilters, String audioFilters, int imageWidth, int imageHeight, int audioChannels) {
        this.filters = videoFilters;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.pixelFormat = AV_PIX_FMT_BGR24;
        this.frameRate = 30;
        this.aspectRatio = 0;
        this.videoInputs = 1;

        this.afilters = audioFilters;
        this.audioChannels = audioChannels;
        this.sampleFormat = AV_SAMPLE_FMT_S16;
        this.sampleRate = 44100;
        this.audioInputs = 1;
    }

    public FFmpegFrameFilter(String filters, int imageWidth, int imageHeight) {
        this(filters, null, imageWidth, imageHeight, 0);
    }

    public FFmpegFrameFilter(String afilters, int audioChannels) {
        this(null, afilters, 0, 0, audioChannels);
    }

    @Override public void release() throws Exception {
        synchronized (org.bytedeco.ffmpeg.global.avfilter.class) {
            releaseUnsafe();
        }
    }
    public synchronized void releaseUnsafe() throws Exception {
        started = false;

        if (image_ptr2 != null) {
            for (int i = 0; i < image_ptr2.length; i++) {
                av_free(image_ptr2[i]);
            }
            image_ptr2 = null;
        }
        if (filter_graph != null) {
            avfilter_graph_free(filter_graph);
            buffersink_ctx.releaseReference();
            for (int i = 0; i < buffersrc_ctx.length; i++) {
                buffersrc_ctx[i].releaseReference();
                setpts_ctx[i].releaseReference();
            }
            time_base.releaseReference();
            buffersink_ctx = null;
            buffersrc_ctx = null;
            setpts_ctx = null;
            filter_graph = null;
            time_base = null;
        }
        if (afilter_graph != null) {
            avfilter_graph_free(afilter_graph);
            abuffersink_ctx.releaseReference();
            for (int i = 0; i < abuffersrc_ctx.length; i++) {
                abuffersrc_ctx[i].releaseReference();
                asetpts_ctx[i].releaseReference();
            }
            atime_base.releaseReference();
            abuffersink_ctx = null;
            abuffersrc_ctx = null;
            asetpts_ctx = null;
            afilter_graph = null;
            atime_base = null;
        }
        if (image_frame != null) {
            av_frame_free(image_frame);
            image_frame = null;
        }
        if (samples_frame != null) {
            av_frame_free(samples_frame);
            samples_frame = null;
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
    AVFilterContext[] buffersrc_ctx;
    AVFilterContext[] setpts_ctx;
    AVFilterGraph filter_graph;
    AVRational time_base;

    AVFilterContext abuffersink_ctx;
    AVFilterContext[] abuffersrc_ctx;
    AVFilterContext[] asetpts_ctx;
    AVFilterGraph afilter_graph;
    AVRational atime_base;

    AVFrame image_frame;
    AVFrame samples_frame;
    AVFrame filt_frame;

    BytePointer[] image_ptr, image_ptr2;
    BytePointer[] samples_ptr;
    Buffer[] image_buf, image_buf2;
    Buffer[] samples_buf;
    Frame frame, inframe;

    private volatile boolean started = false;

    @Override public int getImageWidth() {
        return buffersink_ctx != null ? av_buffersink_get_w(buffersink_ctx) : super.getImageWidth();
    }

    @Override public int getImageHeight() {
        return buffersink_ctx != null ? av_buffersink_get_h(buffersink_ctx) : super.getImageHeight();
    }

    @Override public int getPixelFormat() {
        return buffersink_ctx != null ? av_buffersink_get_format(buffersink_ctx) : super.getPixelFormat();
    }

    @Override public double getFrameRate() {
        if (buffersink_ctx != null) {
            AVRational r = av_buffersink_get_frame_rate(buffersink_ctx);
            if (r.num() == 0 && r.den() == 0) {
                r = av_buffersink_get_time_base(buffersink_ctx);
                return (double)r.den() / r.num();
            }
            return (double)r.num() / r.den();
        } else {
            return super.getFrameRate();
        }
    }

    @Override public double getAspectRatio() {
        if (buffersink_ctx != null) {
            AVRational r = av_buffersink_get_sample_aspect_ratio(buffersink_ctx);
            double a = (double)r.num() / r.den();
            return a == 0.0 ? 1.0 : a;
        } else {
            return super.getAspectRatio();
        }
    }

    @Override public int getAudioChannels() {
        return abuffersink_ctx != null ? av_buffersink_get_channels(abuffersink_ctx) : super.getAudioChannels();
    }

    @Override public int getSampleFormat() {
        return abuffersink_ctx != null ? av_buffersink_get_format(abuffersink_ctx) : super.getSampleFormat();
    }

    @Override public int getSampleRate() {
        return abuffersink_ctx != null ? av_buffersink_get_sample_rate(abuffersink_ctx) : super.getSampleRate();
    }

    @Override public void start() throws Exception {
        synchronized (org.bytedeco.ffmpeg.global.avfilter.class) {
            startUnsafe();
        }
    }
    public synchronized void startUnsafe() throws Exception {
        try (PointerScope scope = new PointerScope()) {

        if (frame != null) {
            throw new Exception("start() has already been called: Call stop() before calling start() again.");
        }

        image_frame = av_frame_alloc();
        samples_frame = av_frame_alloc();
        filt_frame = av_frame_alloc();
        image_ptr = new BytePointer[] { null };
        image_ptr2 = new BytePointer[] { null };
        image_buf = new Buffer[] { null };
        image_buf2 = new Buffer[] { null };
        samples_ptr = new BytePointer[] { null };
        samples_buf = new Buffer[] { null };
        frame = new Frame();

        if (image_frame == null || samples_frame == null || filt_frame == null) {
            throw new Exception("Could not allocate frames");
        }
        if (filters != null && imageWidth > 0 && imageHeight > 0 && videoInputs > 0) {
            startVideoUnsafe();
        }
        if (afilters != null && audioChannels > 0 && audioInputs > 0) {
            startAudioUnsafe();
        }

        started = true;

        }
    }

    private void startVideoUnsafe() throws Exception {
        int ret;
        AVFilter buffersrc  = avfilter_get_by_name("buffer");
        AVFilter buffersink = avfilter_get_by_name("buffersink");
        AVFilter setpts = avfilter_get_by_name("setpts");
        AVFilterInOut[] outputs = new AVFilterInOut[videoInputs];
        AVFilterInOut inputs  = avfilter_inout_alloc();
        AVRational frame_rate = av_d2q(frameRate, 1001000);
        AVRational time_base = av_inv_q(frame_rate);
        int pix_fmts[] = { pixelFormat, AV_PIX_FMT_NONE };

        try {
            filter_graph = avfilter_graph_alloc();
            if (outputs == null || inputs == null || filter_graph == null) {
                throw new Exception("Could not allocate video filter graph: Out of memory?");
            }

            /* buffer video source: the decoded frames from the decoder will be inserted here. */
            AVRational r = av_d2q(aspectRatio > 0 ? aspectRatio : 1, 255);
            String args = String.format(Locale.ROOT, "video_size=%dx%d:pix_fmt=%d:time_base=%d/%d:pixel_aspect=%d/%d:frame_rate=%d/%d",
                    imageWidth, imageHeight, pixelFormat, time_base.num(), time_base.den(), r.num(), r.den(), frame_rate.num(), frame_rate.den());
            buffersrc_ctx = new AVFilterContext[videoInputs];
            setpts_ctx = new AVFilterContext[videoInputs];
            for (int i = 0; i < videoInputs; i++) {
                String name = videoInputs > 1 ? i + ":v" : "in";
                outputs[i] = avfilter_inout_alloc();

                ret = avfilter_graph_create_filter(buffersrc_ctx[i] = new AVFilterContext().retainReference(), buffersrc, name,
                                                   args, null, filter_graph);
                if (ret < 0) {
                    throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot create video buffer source.");
                }

                ret = avfilter_graph_create_filter(setpts_ctx[i] = new AVFilterContext().retainReference(), setpts, videoInputs > 1 ? "setpts" + i : "setpts",
                                                   "N", null, filter_graph);
                if (ret < 0) {
                    throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot create setpts filter.");
                }

                ret = avfilter_link(buffersrc_ctx[i], 0, setpts_ctx[i], 0);
                if (ret < 0) {
                    throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot link setpts filter.");
                }

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
                outputs[i].name(av_strdup(new BytePointer(name)));
                outputs[i].filter_ctx(setpts_ctx[i]);
                outputs[i].pad_idx(0);
                outputs[i].next(null);
                if (i > 0) {
                    outputs[i - 1].next(outputs[i]);
                }
            }

            String name = videoInputs > 1 ? "v" : "out";

            /* buffer video sink: to terminate the filter chain. */
            ret = avfilter_graph_create_filter(buffersink_ctx = new AVFilterContext().retainReference(), buffersink, name,
                                               null, null, filter_graph);
            if (ret < 0) {
                throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot create video buffer sink.");
            }
//            ret = av_opt_set_bin(buffersink_ctx, "pix_fmts", new BytePointer(new IntPointer(pix_fmts)), 4, AV_OPT_SEARCH_CHILDREN);
//            if (ret < 0) {
//                throw new Exception("av_opt_set_bin() error " + ret + ": Cannot set output pixel format.");
//            }

            /*
             * The buffer sink input must be connected to the output pad of
             * the last filter described by filters_descr; since the last
             * filter output label is not specified, it is set to "out" by
             * default.
             */
            inputs.name(av_strdup(new BytePointer(name)));
            inputs.filter_ctx(buffersink_ctx);
            inputs.pad_idx(0);
            inputs.next(null);
            if ((ret = avfilter_graph_parse_ptr(filter_graph, filters,
                                                inputs, outputs[0], null)) < 0) {
                throw new Exception("avfilter_graph_parse_ptr() error " + ret);
            }
            if ((ret = avfilter_graph_config(filter_graph, null)) < 0) {
                throw new Exception("avfilter_graph_config() error " + ret);
            }
            this.time_base = av_buffersink_get_time_base(buffersink_ctx).retainReference();
        } finally {
            avfilter_inout_free(inputs);
            avfilter_inout_free(outputs[0]);
        }
    }

    private void startAudioUnsafe() throws Exception {
        int ret;
        AVFilter abuffersrc  = avfilter_get_by_name("abuffer");
        AVFilter abuffersink = avfilter_get_by_name("abuffersink");
        AVFilter asetpts = avfilter_get_by_name("asetpts");
        AVFilterInOut[] aoutputs = new AVFilterInOut[audioInputs];
        AVFilterInOut ainputs  = avfilter_inout_alloc();
        int sample_fmts[] = { sampleFormat, AV_PIX_FMT_NONE };

        try {
            afilter_graph = avfilter_graph_alloc();
            if (aoutputs == null || ainputs == null || afilter_graph == null) {
                throw new Exception("Could not allocate audio filter graph: Out of memory?");
            }

            abuffersrc_ctx = new AVFilterContext[audioInputs];
            asetpts_ctx = new AVFilterContext[audioInputs];
            for (int i = 0; i < audioInputs; i++) {
                String name = audioInputs > 1 ? i + ":a" : "in";
                aoutputs[i] = avfilter_inout_alloc();

                /* buffer audio source: the decoded frames from the decoder will be inserted here. */
                String aargs = String.format(Locale.ROOT, "channels=%d:sample_fmt=%d:sample_rate=%d:channel_layout=%d",
                        audioChannels, sampleFormat, sampleRate, av_get_default_channel_layout(audioChannels));
                ret = avfilter_graph_create_filter(abuffersrc_ctx[i] = new AVFilterContext().retainReference(), abuffersrc, name,
                                                   aargs, null, afilter_graph);
                if (ret < 0) {
                    throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot create audio buffer source.");
                }

                ret = avfilter_graph_create_filter(asetpts_ctx[i] = new AVFilterContext().retainReference(), asetpts, audioInputs > 1 ? "asetpts" + i : "asetpts",
                                                   "N", null, afilter_graph);
                if (ret < 0) {
                    throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot create asetpts filter.");
                }

                ret = avfilter_link(abuffersrc_ctx[i], 0, asetpts_ctx[i], 0);
                if (ret < 0) {
                    throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot link asetpts filter.");
                }

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
                aoutputs[i].name(av_strdup(new BytePointer(name)));
                aoutputs[i].filter_ctx(asetpts_ctx[i]);
                aoutputs[i].pad_idx(0);
                aoutputs[i].next(null);
                if (i > 0) {
                    aoutputs[i - 1].next(aoutputs[i]);
                }
            }

            String name = audioInputs > 1 ? "a" : "out";

            /* buffer audio sink: to terminate the filter chain. */
            ret = avfilter_graph_create_filter(abuffersink_ctx = new AVFilterContext().retainReference(), abuffersink, name,
                                               null, null, afilter_graph);
            if (ret < 0) {
                throw new Exception("avfilter_graph_create_filter() error " + ret + ": Cannot create audio buffer sink.");
            }
//            ret = av_opt_set_bin(abuffersink_ctx, "sample_fmts", new BytePointer(new IntPointer(sample_fmts)), 4, AV_OPT_SEARCH_CHILDREN);
//            if (ret < 0) {
//                throw new Exception("av_opt_set_bin() error " + ret + ": Cannot set output sample format.");
//            }

            /*
             * The buffer sink input must be connected to the output pad of
             * the last filter described by filters_descr; since the last
             * filter output label is not specified, it is set to "out" by
             * default.
             */
            ainputs.name(av_strdup(new BytePointer(name)));
            ainputs.filter_ctx(abuffersink_ctx);
            ainputs.pad_idx(0);
            ainputs.next(null);
            if ((ret = avfilter_graph_parse_ptr(afilter_graph, afilters,
                                                ainputs, aoutputs[0], null)) < 0) {
                throw new Exception("avfilter_graph_parse_ptr() error " + ret);
            }
            if ((ret = avfilter_graph_config(afilter_graph, null)) < 0) {
                throw new Exception("avfilter_graph_config() error " + ret);
            }
            this.atime_base = av_buffersink_get_time_base(abuffersink_ctx).retainReference();
        } finally {
            avfilter_inout_free(ainputs);
            avfilter_inout_free(aoutputs[0]);
        }
    }

    @Override public void stop() throws Exception {
        release();
    }

    @Override public void push(Frame frame) throws Exception {
        push(frame, frame != null && frame.opaque instanceof AVFrame ? ((AVFrame)frame.opaque).format() : AV_PIX_FMT_NONE);
    }
    public void push(Frame frame, int pixelFormat) throws Exception {
        push(0, frame, pixelFormat);
    }
    public void push(int n, Frame frame) throws Exception {
        push(n, frame, frame != null && frame.opaque instanceof AVFrame ? ((AVFrame)frame.opaque).format() : AV_PIX_FMT_NONE);
    }
    public synchronized void push(int n, Frame frame, int pixelFormat) throws Exception {
        if (!started) {
            throw new Exception("start() was not called successfully!");
        }

        inframe = frame;
        if (frame != null && frame.image != null && buffersrc_ctx != null) {
            image_frame.pts(frame.timestamp * time_base.den() / (1000000L * time_base.num()));
            pushImage(n, frame.imageWidth, frame.imageHeight, frame.imageDepth,
                    frame.imageChannels, frame.imageStride, pixelFormat, frame.image);
        }
        if (frame != null && frame.samples != null && abuffersrc_ctx != null) {
            samples_frame.pts(frame.timestamp * atime_base.den() / (1000000L * atime_base.num()));
            pushSamples(n, frame.audioChannels, sampleRate, sampleFormat, frame.samples);
        }
        if (frame == null || (frame.image == null && frame.samples == null)) {
            // indicate EOF as required, for example, by the "palettegen" filter
            if (buffersrc_ctx != null && n < buffersrc_ctx.length) {
                av_buffersrc_add_frame_flags(buffersrc_ctx[n], null, AV_BUFFERSRC_FLAG_PUSH);
            }
            if (abuffersrc_ctx != null && n < abuffersrc_ctx.length) {
                av_buffersrc_add_frame_flags(abuffersrc_ctx[n], null, AV_BUFFERSRC_FLAG_PUSH);
            }
        }
    }

    public synchronized void pushImage(int n, int width, int height, int depth, int channels, int stride, int pixelFormat, Buffer ... image) throws Exception {
        try (PointerScope scope = new PointerScope()) {

        if (!started) {
            throw new Exception("start() was not called successfully!");
        }

        int ret;
        int step = stride * Math.abs(depth) / 8;
        BytePointer data = image[0] instanceof ByteBuffer
                ? new BytePointer((ByteBuffer)image[0]).position(0)
                : new BytePointer(new Pointer(image[0]).position(0));

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
            } else {
                throw new Exception("Could not guess pixel format of image: depth=" + depth + ", channels=" + channels);
            }
        }

        if (pixelFormat == AV_PIX_FMT_NV21) {
            step = width;
        }

        av_image_fill_arrays(new PointerPointer(image_frame), image_frame.linesize(), data, pixelFormat, width, height, 1);
        image_frame.linesize(0, step);
        image_frame.format(pixelFormat);
        image_frame.width(width);
        image_frame.height(height);

        /* push the decoded frame into the filtergraph */
        if ((ret = av_buffersrc_add_frame_flags(buffersrc_ctx[n], image_frame, AV_BUFFERSRC_FLAG_KEEP_REF | AV_BUFFERSRC_FLAG_PUSH)) < 0) {
            throw new Exception("av_buffersrc_add_frame_flags() error " + ret + ": Error while feeding the filtergraph.");
        }

        }
    }

    public synchronized void pushSamples(int n, int audioChannels, int sampleRate, int sampleFormat, Buffer ... samples) throws Exception {
        try (PointerScope scope = new PointerScope()) {

        if (!started) {
            throw new Exception("start() was not called successfully!");
        }

        int ret;
        Pointer[] data = new Pointer[samples.length];
        int sampleSize = samples != null ? ((samples[0].limit() - samples[0].position()) / (samples.length > 1 ? 1 : audioChannels)) : 0;
        if (samples != null && samples[0] instanceof ByteBuffer) {
            sampleFormat = data.length > 1 ? AV_SAMPLE_FMT_U8P : AV_SAMPLE_FMT_U8;
            for (int i = 0; i < data.length; i++) {
                data[i] = new BytePointer((ByteBuffer)samples[i]);
            }
        } else if (samples != null && samples[0] instanceof ShortBuffer) {
            sampleFormat = data.length > 1 ? AV_SAMPLE_FMT_S16P : AV_SAMPLE_FMT_S16;
            for (int i = 0; i < data.length; i++) {
                data[i] = new ShortPointer((ShortBuffer)samples[i]);
            }
        } else if (samples != null && samples[0] instanceof IntBuffer) {
            sampleFormat = data.length > 1 ? AV_SAMPLE_FMT_S32P : AV_SAMPLE_FMT_S32;
            for (int i = 0; i < data.length; i++) {
                data[i] = new IntPointer((IntBuffer)samples[i]);
            }
        } else if (samples != null && samples[0] instanceof FloatBuffer) {
            sampleFormat = data.length > 1 ? AV_SAMPLE_FMT_FLTP : AV_SAMPLE_FMT_FLT;
            for (int i = 0; i < data.length; i++) {
                data[i] = new FloatPointer((FloatBuffer)samples[i]);
            }
        } else if (samples != null && samples[0] instanceof DoubleBuffer) {
            sampleFormat = data.length > 1 ? AV_SAMPLE_FMT_DBLP : AV_SAMPLE_FMT_DBL;
            for (int i = 0; i < data.length; i++) {
                data[i] = new DoublePointer((DoubleBuffer)samples[i]);
            }
        } else if (samples != null) {
            for (int i = 0; i < data.length; i++) {
                data[i] = new Pointer(samples[i]);
            }
        }

        av_samples_fill_arrays(new PointerPointer(samples_frame), samples_frame.linesize(), new BytePointer(data[0]), audioChannels, sampleSize, sampleFormat, 1);
        for (int i = 0; i < samples.length; i++) {
            samples_frame.data(i, new BytePointer(data[i]));
        }
        samples_frame.channels(audioChannels);
        samples_frame.channel_layout(av_get_default_channel_layout(audioChannels));
        samples_frame.nb_samples(sampleSize);
        samples_frame.format(sampleFormat);
        samples_frame.sample_rate(sampleRate);

        /* push the decoded frame into the filtergraph */
        if ((ret = av_buffersrc_add_frame_flags(abuffersrc_ctx[n], samples_frame, AV_BUFFERSRC_FLAG_KEEP_REF | AV_BUFFERSRC_FLAG_PUSH)) < 0) {
            throw new Exception("av_buffersrc_add_frame_flags() error " + ret + ": Error while feeding the filtergraph.");
        }

        }
    }

    @Override public synchronized Frame pull() throws Exception {
        if (!started) {
            throw new Exception("start() was not called successfully!");
        }

        frame.keyFrame = false;
        frame.imageWidth = 0;
        frame.imageHeight = 0;
        frame.imageDepth = 0;
        frame.imageChannels = 0;
        frame.imageStride = 0;
        frame.image = null;
        frame.sampleRate = 0;
        frame.audioChannels = 0;
        frame.samples = null;
        frame.opaque = null;

        Frame f = null;
        if (f == null && buffersrc_ctx != null) {
            f = pullImage();
        }
        if (f == null && abuffersrc_ctx != null) {
            f = pullSamples();
        }
        if (f == null && inframe != null
                && ((inframe.image != null && buffersrc_ctx == null)
                || (inframe.samples != null && abuffersrc_ctx == null))) {
            f = inframe;
        }
        inframe = null;
        return f;
    }

    public synchronized Frame pullImage() throws Exception {
        try (PointerScope scope = new PointerScope()) {

        if (!started) {
            throw new Exception("start() was not called successfully!");
        }

        av_frame_unref(filt_frame);

        /* pull a filtered frame from the filtergraph */
        int ret = av_buffersink_get_frame(buffersink_ctx, filt_frame);
        if (ret == AVERROR_EAGAIN() || ret == AVERROR_EOF()) {
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
            // Fix bug on vflip filter, frame.imageStride can be negative
            // see https://github.com/bytedeco/javacv/issues/975
            if (ptr != null && !ptr.equals(image_ptr[0])) {
                image_ptr[0] = ptr.capacity(frame.imageHeight * Math.abs(frame.imageStride));
                image_buf[0] = ptr.asBuffer();
            }
            frame.image = image_buf;
            frame.image[0].position(0).limit(frame.imageHeight * Math.abs(frame.imageStride));
            frame.imageChannels = Math.abs(frame.imageStride) / frame.imageWidth;
            frame.opaque = filt_frame;
        } else {
            frame.imageStride = frame.imageWidth;
            int size = av_image_get_buffer_size(filt_frame.format(), frame.imageWidth, frame.imageHeight, 1);
            if (image_ptr2[0] == null || image_ptr2[0].capacity() < size) {
                av_free(image_ptr2[0]);
                image_ptr2[0] = new BytePointer(av_malloc(size)).capacity(size);
                image_buf2[0] = image_ptr2[0].asBuffer();
            }
            frame.image = image_buf2;
            frame.image[0].position(0).limit(size);
            frame.imageChannels = (size + frame.imageWidth * frame.imageHeight - 1) / (frame.imageWidth * frame.imageHeight);
            ret = av_image_copy_to_buffer(image_ptr2[0].position(0), (int)image_ptr2[0].capacity(),
                    new PointerPointer(filt_frame), filt_frame.linesize(), filt_frame.format(), frame.imageWidth, frame.imageHeight, 1);
            if (ret < 0) {
                throw new Exception("av_image_copy_to_buffer() error " + ret + ": Cannot pull image.");
            }
            frame.opaque = image_ptr2[0];
        }
        frame.timestamp = 1000000L * filt_frame.pts() * time_base.num() / time_base.den();
        return frame;

        }
    }

    public synchronized Frame pullSamples() throws Exception {
        try (PointerScope scope = new PointerScope()) {

        if (!started) {
            throw new Exception("start() was not called successfully!");
        }

        av_frame_unref(filt_frame);

        /* pull a filtered frame from the filtergraph */
        int ret = av_buffersink_get_frame(abuffersink_ctx, filt_frame);
        if (ret == AVERROR_EAGAIN() || ret == AVERROR_EOF()) {
            return null;
        } else if (ret < 0) {
            throw new Exception("av_buffersink_get_frame(): Error occurred: "
                    + av_make_error_string(new BytePointer(256), 256, ret).getString());
        }
        int sample_format = filt_frame.format();
        int planes = av_sample_fmt_is_planar(sample_format) != 0 ? (int)filt_frame.channels() : 1;
        int data_size = av_samples_get_buffer_size((IntPointer)null, filt_frame.channels(),
                filt_frame.nb_samples(), filt_frame.format(), 1) / planes;
        if (samples_buf == null || samples_buf.length != planes) {
            samples_ptr = new BytePointer[planes];
            samples_buf = new Buffer[planes];
        }
        frame.audioChannels = filt_frame.channels();
        frame.sampleRate = filt_frame.sample_rate();
        frame.samples = samples_buf;
        frame.opaque = filt_frame;
        int sample_size = data_size / av_get_bytes_per_sample(sample_format);
        for (int i = 0; i < planes; i++) {
            BytePointer p = filt_frame.data(i);
            if (!p.equals(samples_ptr[i]) || samples_ptr[i].capacity() < data_size) {
                samples_ptr[i] = p.capacity(data_size);
                ByteBuffer b   = p.asBuffer();
                switch (sample_format) {
                    case AV_SAMPLE_FMT_U8:
                    case AV_SAMPLE_FMT_U8P:  samples_buf[i] = b; break;
                    case AV_SAMPLE_FMT_S16:
                    case AV_SAMPLE_FMT_S16P: samples_buf[i] = b.asShortBuffer();  break;
                    case AV_SAMPLE_FMT_S32:
                    case AV_SAMPLE_FMT_S32P: samples_buf[i] = b.asIntBuffer();    break;
                    case AV_SAMPLE_FMT_FLT:
                    case AV_SAMPLE_FMT_FLTP: samples_buf[i] = b.asFloatBuffer();  break;
                    case AV_SAMPLE_FMT_DBL:
                    case AV_SAMPLE_FMT_DBLP: samples_buf[i] = b.asDoubleBuffer(); break;
                    default: assert false;
                }
            }
            samples_buf[i].position(0).limit(sample_size);
        }
        frame.timestamp = 1000000L * filt_frame.pts() * atime_base.num() / atime_base.den();
        return frame;

        }
    }
}
