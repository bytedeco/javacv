/*
 * Copyright (C) 2009,2010,2011,2012,2013 Samuel Audet
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
 * Based on the avcodec_sample.0.5.0.c file available at
 * http://web.me.com/dhoerl/Home/Tech_Blog/Entries/2009/1/22_Revised_avcodec_sample.c_files/avcodec_sample.0.5.0.c
 * by Martin Böhme, Stephen Dranger, and David Hoerl
 * as well as on the decoding_encoding.c file included in FFmpeg 0.11.1,
 * which is covered by the following copyright notice:
 *
 * Copyright (c) 2001 Fabrice Bellard
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

import static com.googlecode.javacv.cpp.avcodec.*;
import static com.googlecode.javacv.cpp.avdevice.*;
import static com.googlecode.javacv.cpp.avformat.*;
import static com.googlecode.javacv.cpp.avutil.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.swscale.*;

/**
 *
 * @author Samuel Audet
 */
public class FFmpegFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();
        throw new UnsupportedOperationException("Device enumeration not support by FFmpeg.");
    }

    public static FFmpegFrameGrabber createDefault(File deviceFile)   throws Exception { return new FFmpegFrameGrabber(deviceFile); }
    public static FFmpegFrameGrabber createDefault(String devicePath) throws Exception { return new FFmpegFrameGrabber(devicePath); }
    public static FFmpegFrameGrabber createDefault(int deviceNumber)  throws Exception { return null; }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(com.googlecode.javacv.cpp.avutil.class);
                Loader.load(com.googlecode.javacv.cpp.avcodec.class);
                Loader.load(com.googlecode.javacv.cpp.avformat.class);
                Loader.load(com.googlecode.javacv.cpp.avdevice.class);
                Loader.load(com.googlecode.javacv.cpp.swscale.class);
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception("Failed to load " + FFmpegFrameGrabber.class, t);
                }
            }
        }
    }

    public FFmpegFrameGrabber(File file) {
        this(file.getAbsolutePath());
    }
    public FFmpegFrameGrabber(String filename) {
        // Register all formats and codecs
        avcodec_register_all();
        avdevice_register_all();
        av_register_all();
        avformat_network_init();

        this.filename = filename;
    }
    public void release() throws Exception {
        if (pkt != null && pkt2 != null) {
            if (pkt2.size() > 0) {
                av_free_packet(pkt);
            }
            pkt = pkt2 = null;
        }

        // Free the RGB image
        if (buffer_rgb != null) {
            av_free(buffer_rgb);
            buffer_rgb = null;
        }
        if (picture_rgb != null) {
            avcodec_free_frame(picture_rgb);
            picture_rgb = null;
        }

        // Free the native format picture frame
        if (picture != null) {
            avcodec_free_frame(picture);
            picture = null;
        }

        // Close the video codec
        if (video_c != null) {
            avcodec_close(video_c);
            video_c = null;
        }

        // Free the audio samples frame
        if (samples_frame != null) {
            avcodec_free_frame(samples_frame);
            samples_frame = null;
        }

        // Close the audio codec
        if (audio_c != null) {
            avcodec_close(audio_c);
            audio_c = null;
        }

        // Close the video file
        if (oc != null && !oc.isNull()) {
            avformat_close_input(oc);
            oc = null;
        }

        if (img_convert_ctx != null) {
            sws_freeContext(img_convert_ctx);
            img_convert_ctx = null;
        }

        got_frame     = null;
        return_image  = null;
        frameGrabbed  = false;
        frame         = null;
        timestamp     = 0;
        frameNumber   = 0;
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private String          filename;
    private AVFormatContext oc;
    private AVStream        video_st, audio_st;
    private AVCodecContext  video_c, audio_c;
    private AVFrame         picture, picture_rgb;
    private BytePointer     buffer_rgb;
    private AVFrame         samples_frame;
    private BytePointer[]   samples_ptr;
    private Buffer[]        samples_buf;
    private AVPacket        pkt, pkt2;
    private int             sizeof_pkt;
    private int[]           got_frame;
    private SwsContext      img_convert_ctx;
    private IplImage        return_image;
    private boolean         frameGrabbed;
    private Frame           frame;

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    @Override public String getFormat() {
        if (oc == null) {
            return super.getFormat();
        } else {
            return oc.iformat().name().getString();
        }
    }

    @Override public int getImageWidth() {
        return return_image == null ? super.getImageWidth() : return_image.width();
    }

    @Override public int getImageHeight() {
        return return_image == null ? super.getImageHeight() : return_image.height();
    }

    @Override public int getAudioChannels() {
        return audio_c == null ? super.getAudioChannels() : audio_c.channels();
    }

    @Override public int getPixelFormat() {
        if (imageMode == ImageMode.COLOR || imageMode == ImageMode.GRAY) {
            if (pixelFormat == AV_PIX_FMT_NONE) {
                return imageMode == ImageMode.COLOR ? AV_PIX_FMT_BGR24 : AV_PIX_FMT_GRAY8;
            } else {
                return pixelFormat;
            }
        } else if (video_c != null) { // RAW
            return video_c.pix_fmt();
        } else {
            return super.getPixelFormat();
        }
    }

    @Override public double getFrameRate() {
        if (video_st == null) {
            return super.getFrameRate();
        } else {
            AVRational r = video_st.r_frame_rate();
            return (double)r.num() / r.den();
        }
    }

    @Override public int getSampleFormat() {
        return audio_c == null ? super.getSampleFormat() : audio_c.sample_fmt();
    }

    @Override public int getSampleRate() {
        return audio_c == null ? super.getSampleRate() : audio_c.sample_rate();
    }

    @Override public void setFrameNumber(int frameNumber) throws Exception {
        // best guess, AVSEEK_FLAG_FRAME has not been implemented in FFmpeg...
        setTimestamp(Math.round(1000000L * frameNumber / getFrameRate()));
    }

    @Override public void setTimestamp(long timestamp) throws Exception {
        int ret;
        if (oc == null || video_c == null) {
            super.setTimestamp(timestamp);
        } else {
            timestamp = timestamp * AV_TIME_BASE / 1000000L;
            /* add the stream start time */
            if (oc.start_time() != AV_NOPTS_VALUE) {
                timestamp += oc.start_time();
            }
            if ((ret = avformat_seek_file(oc, -1, Long.MIN_VALUE, timestamp, Long.MAX_VALUE, AVSEEK_FLAG_BACKWARD)) < 0) {
                throw new Exception("avformat_seek_file() error " + ret + ": Could not seek file to timestamp " + timestamp + ".");
            }
            avcodec_flush_buffers(video_c);
            if (audio_c != null) {
                avcodec_flush_buffers(audio_c);
            }
            while (this.timestamp > timestamp && grab(false) != null) {
                // flush frames if seeking backwards
            }
            while (this.timestamp < timestamp && grab(false) != null) {
                // decode up to the desired frame
            }
            frameGrabbed = true;
        }
    }

    @Override public int getLengthInFrames() {
        // best guess...
        return (int)(getLengthInTime() * getFrameRate() / 1000000L);
    }
    @Override public long getLengthInTime() {
        return oc.duration() * 1000000L / AV_TIME_BASE;
    }

    public void start() throws Exception {
        int ret;
        img_convert_ctx = null;
        oc              = new AVFormatContext(null);
        video_c         = null;
        audio_c         = null;
        pkt             = new AVPacket();
        pkt2            = new AVPacket();
        sizeof_pkt      = pkt.sizeof();
        got_frame       = new int[1];
        return_image    = null;
        frameGrabbed    = false;
        frame           = new Frame();
        timestamp       = 0;
        frameNumber     = 0;

        pkt2.size(0);

        // Open video file
        AVInputFormat f = null;
        if (format != null && format.length() > 0) {
            if ((f = av_find_input_format(format)) == null) {
                throw new Exception("av_find_input_format() error: Could not find input format \"" + format + "\".");
            }
        }
        AVDictionary options = new AVDictionary(null);
        if (frameRate > 0) {
            AVRational r = av_d2q(frameRate, 1001000);
            av_dict_set(options, "framerate", r.num() + "/" + r.den(), 0);
        }
        if (imageMode != ImageMode.RAW) {
            av_dict_set(options, "pixel_format", imageMode == ImageMode.COLOR ? "bgr24" : "gray8", 0);
        }
        if (imageWidth > 0 && imageHeight > 0) {
            av_dict_set(options, "video_size", imageWidth + "x" + imageHeight, 0);
        }
        if (sampleRate > 0) {
            av_dict_set(options, "sample_rate", "" + sampleRate, 0);
        }
        if (audioChannels > 0) {
            av_dict_set(options, "channels", "" + audioChannels, 0);
        }
        if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
            throw new Exception("avformat_open_input() error " + ret + ": Could not open input \"" + filename + "\". (Has setFormat() been called?)");
        }
        av_dict_free(options);

        // Retrieve stream information
        if ((ret = avformat_find_stream_info(oc, null)) < 0) {
            throw new Exception("avformat_find_stream_info() error " + ret + ": Could not find stream information.");
        }

        // Dump information about file onto standard error
        av_dump_format(oc, 0, filename, 0);

        // Find the first video and stream
        video_st = audio_st = null;
        int nb_streams = oc.nb_streams();
        for (int i = 0; i < nb_streams; i++) {
            AVStream st = oc.streams(i);
            // Get a pointer to the codec context for the video or audio stream
            AVCodecContext c = st.codec();
            if (video_st == null && c.codec_type() == AVMEDIA_TYPE_VIDEO) {
                video_st = st;
                video_c = c;
            } else if (audio_st == null && c.codec_type() == AVMEDIA_TYPE_AUDIO) {
                audio_st = st;
                audio_c = c;
            }
        }
        if (video_st == null && audio_st == null) {
            throw new Exception("Did not find a video or audio stream inside \"" + filename + "\".");
        }

        if (video_st != null) {
            // Find the decoder for the video stream
            AVCodec codec = avcodec_find_decoder(video_c.codec_id());
            if (codec == null) {
                throw new Exception("avcodec_find_decoder() error: Unsupported video format or codec not found: " + video_c.codec_id() + ".");
            }

            // Open video codec
            if ((ret = avcodec_open2(video_c, codec, null)) < 0) {
                throw new Exception("avcodec_open2() error " + ret + ": Could not open video codec.");
            }

            // Hack to correct wrong frame rates that seem to be generated by some codecs
            if (video_c.time_base().num() > 1000 && video_c.time_base().den() == 1) {
                video_c.time_base().den(1000);
            }

            // Allocate video frame and an AVFrame structure for the RGB image
            if ((picture = avcodec_alloc_frame()) == null) {
                throw new Exception("avcodec_alloc_frame() error: Could not allocate raw picture frame.");
            }
            if ((picture_rgb = avcodec_alloc_frame()) == null) {
                throw new Exception("avcodec_alloc_frame() error: Could not allocate RGB picture frame.");
            }

            int width  = getImageWidth()  > 0 ? getImageWidth()  : video_c.width();
            int height = getImageHeight() > 0 ? getImageHeight() : video_c.height();

            switch (imageMode) {
                case COLOR:
                case GRAY:
                    int fmt = getPixelFormat();

                    // Determine required buffer size and allocate buffer
                    int size = avpicture_get_size(fmt, width, height);
                    buffer_rgb = new BytePointer(av_malloc(size));

                    // Assign appropriate parts of buffer to image planes in picture_rgb
                    // Note that picture_rgb is an AVFrame, but AVFrame is a superset of AVPicture
                    avpicture_fill(picture_rgb, buffer_rgb, fmt, width, height);

                    return_image = IplImage.createHeader(width, height, IPL_DEPTH_8U, 1);
                    break;

                case RAW:
                    buffer_rgb = null;
                    return_image = IplImage.createHeader(video_c.width(), video_c.height(), IPL_DEPTH_8U, 1);
                    break;

                default:
                    assert false;
            }
        }

        if (audio_st != null) {
            // Find the decoder for the audio stream
            AVCodec codec = avcodec_find_decoder(audio_c.codec_id());
            if (codec == null) {
                throw new Exception("avcodec_find_decoder() error: Unsupported audio format or codec not found: " + audio_c.codec_id() + ".");
            }

            // Open audio codec
            if ((ret = avcodec_open2(audio_c, codec, null)) < 0) {
                throw new Exception("avcodec_open2() error " + ret + ": Could not open audio codec.");
            }

            // Allocate audio samples frame
            if ((samples_frame = avcodec_alloc_frame()) == null) {
                throw new Exception("avcodec_alloc_frame() error: Could not allocate audio frame.");
            }
        }
    }

    public void stop() throws Exception {
        release();
    }

    public void trigger() throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not trigger: No AVFormatContext. (Has start() been called?)");
        }
        if (pkt2.size() > 0) {
            pkt2.size(0);
            av_free_packet(pkt);
        }
        for (int i = 0; i < numBuffers+1; i++) {
            if (av_read_frame(oc, pkt) < 0) {
                return;
            }
            av_free_packet(pkt);
        }
    }

    private void processImage() throws Exception {
        switch (imageMode) {
            case COLOR:
            case GRAY:
                // Deinterlace Picture
                if (deinterlace) {
                    avpicture_deinterlace(picture, picture, video_c.pix_fmt(), video_c.width(), video_c.height());
                }

                // Convert the image into BGR or GRAY format that OpenCV uses
                img_convert_ctx = sws_getCachedContext(img_convert_ctx,
                        video_c.width(), video_c.height(), video_c.pix_fmt(),
                        getImageWidth(), getImageHeight(), getPixelFormat(), SWS_BILINEAR, null, null, null);
                if (img_convert_ctx == null) {
                    throw new Exception("sws_getCachedContext() error: Cannot initialize the conversion context.");
                }

                // Convert the image from its native format to RGB or GRAY
                sws_scale(img_convert_ctx, new PointerPointer(picture), picture.linesize(), 0,
                        video_c.height(), new PointerPointer(picture_rgb), picture_rgb.linesize());
                return_image.imageData(buffer_rgb);
                return_image.widthStep(picture_rgb.linesize(0));
                break;

            case RAW:
                assert video_c.width()  == return_image.width() &&
                       video_c.height() == return_image.height();
                return_image.imageData(picture.data(0));
                return_image.widthStep(picture.linesize(0));
                break;

            default:
                assert false;
        }
        return_image.imageSize(return_image.height() * return_image.widthStep());
        return_image.nChannels(return_image.widthStep() / return_image.width());
    }

    public IplImage grab() throws Exception {
        Frame frame = grabFrame(true, false);
        return frame != null ? frame.image : null;
    }
    private IplImage grab(boolean processImage) throws Exception {
        Frame frame = grabFrame(processImage, false);
        return frame != null ? frame.image : null;
    }
    @Override public Frame grabFrame() throws Exception {
        return grabFrame(true, true);
    }
    private Frame grabFrame(boolean processImage, boolean doAudio) throws Exception {
        frame.image = null;
        frame.samples = null;
        if (frameGrabbed) {
            frameGrabbed = false;
            if (processImage) {
                processImage();
            }
            frame.image = return_image;
            return frame;
        }
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
        }
        boolean done = false;
        while (!done) {
            if (pkt2.size() <= 0) {
                if (av_read_frame(oc, pkt) < 0) {
                    // The video codec may have buffered some frames
                    pkt.data(null);
                    pkt.size(0);
                }
            }

            // Is this a packet from the video stream?
            if (video_st != null && (pkt.stream_index() == video_st.index() ||
                    pkt.data().isNull() || pkt.size() == 0)) {
                // Decode video frame
                int len = avcodec_decode_video2(video_c, picture, got_frame, pkt);

                // Did we get a video frame?
                if (len >= 0 && got_frame[0] != 0) {
                    long pts = av_frame_get_best_effort_timestamp(picture);
                    AVRational time_base = video_st.time_base();
                    timestamp = 1000000L * pts * time_base.num() / time_base.den();
                    // best guess, AVCodecContext.frame_number = number of decoded frames...
                    frameNumber = (int)(1000000L * getFrameRate() / timestamp);
                    if (processImage) {
                        processImage();
                    }
                    done = true;
                    frame.image = return_image;
                }
            } else if (doAudio && audio_st != null && pkt.stream_index() == audio_st.index()) {
                if (pkt2.size() <= 0) {
                    // HashMap is unacceptably slow on Android
                    // pkt2.put(pkt);
                    BytePointer.memcpy(pkt2, pkt, sizeof_pkt);
                }
                avcodec_get_frame_defaults(samples_frame);
                // Decode audio frame
                int len = avcodec_decode_audio4(audio_c, samples_frame, got_frame, pkt2);
                if (len <= 0) {
                    // On error, trash the whole packet
                    pkt2.size(0);
                } else {
                    pkt2.data(pkt2.data().position(len));
                    pkt2.size(pkt2.size() - len);
                    if (got_frame[0] != 0) {
                        /* if a frame has been decoded, output it */
                        done = true;
                        int sample_format = samples_frame.format();
                        int planes = av_sample_fmt_is_planar(sample_format) != 0 ? (int)samples_frame.channels() : 1;
                        int data_size = av_samples_get_buffer_size(null, audio_c.channels(),
                                samples_frame.nb_samples(), audio_c.sample_fmt(), 1) / planes;
                        if (samples_buf == null || samples_buf.length != planes) {
                            samples_ptr = new BytePointer[planes];
                            samples_buf = new Buffer[planes];
                        }
                        frame.samples = samples_buf;
                        int sample_size = data_size / av_get_bytes_per_sample(sample_format);
                        for (int i = 0; i < planes; i++) {
                            BytePointer p = samples_frame.data(i);
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
                    }
                }
            }

            if (pkt2.size() <= 0) {
                // Free the packet that was allocated by av_read_frame
                av_free_packet(pkt);
            }
        }
        return frame;
    }
}
