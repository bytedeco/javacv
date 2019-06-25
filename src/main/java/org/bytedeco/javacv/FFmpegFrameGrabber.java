/*
 * Copyright (C) 2009-2019 Samuel Audet
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

package org.bytedeco.javacv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.PointerScope;

import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avdevice.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;
import org.bytedeco.ffmpeg.swresample.*;
import org.bytedeco.ffmpeg.swscale.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avdevice.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swresample.*;
import static org.bytedeco.ffmpeg.global.swscale.*;

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
    public static FFmpegFrameGrabber createDefault(int deviceNumber)  throws Exception { throw new Exception(FFmpegFrameGrabber.class + " does not support device numbers."); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.ffmpeg.global.avutil.class);
                Loader.load(org.bytedeco.ffmpeg.global.swresample.class);
                Loader.load(org.bytedeco.ffmpeg.global.avcodec.class);
                Loader.load(org.bytedeco.ffmpeg.global.avformat.class);
                Loader.load(org.bytedeco.ffmpeg.global.swscale.class);

                // Register all formats and codecs
                av_jni_set_java_vm(Loader.getJavaVM(), null);
                avcodec_register_all();
                av_register_all();
                avformat_network_init();

                Loader.load(org.bytedeco.ffmpeg.global.avdevice.class);
                avdevice_register_all();
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception("Failed to load " + FFmpegFrameGrabber.class, t);
                }
            }
        }
    }

    static {
        try {
            tryLoad();
            FFmpegLockCallback.init();
        } catch (Exception ex) { }
    }

    public FFmpegFrameGrabber(File file) {
        this(file.getAbsolutePath());
    }
    public FFmpegFrameGrabber(String filename) {
        this.filename = filename;
        this.pixelFormat = AV_PIX_FMT_NONE;
        this.sampleFormat = AV_SAMPLE_FMT_NONE;
    }
    /** Calls {@code FFmpegFrameGrabber(inputStream, Integer.MAX_VALUE - 8)}
     *  so that the whole input stream is seekable. */
    public FFmpegFrameGrabber(InputStream inputStream) {
        this(inputStream, Integer.MAX_VALUE - 8);
    }
    public FFmpegFrameGrabber(InputStream inputStream, int maximumSize) {
        this.inputStream = inputStream;
        this.closeInputStream = true;
        this.pixelFormat = AV_PIX_FMT_NONE;
        this.sampleFormat = AV_SAMPLE_FMT_NONE;
        this.maximumSize = maximumSize;
    }
    public void release() throws Exception {
        synchronized (org.bytedeco.ffmpeg.global.avcodec.class) {
            releaseUnsafe();
        }
    }
    public void releaseUnsafe() throws Exception {
        if (pkt != null && pkt2 != null) {
            if (pkt2.size() > 0) {
                av_packet_unref(pkt);
            }
            pkt = pkt2 = null;
        }

        // Free the RGB image
        if (image_ptr != null) {
            for (int i = 0; i < image_ptr.length; i++) {
                av_free(image_ptr[i]);
            }
            image_ptr = null;
        }
        if (picture_rgb != null) {
            av_frame_free(picture_rgb);
            picture_rgb = null;
        }

        // Free the native format picture frame
        if (picture != null) {
            av_frame_free(picture);
            picture = null;
        }

        // Close the video codec
        if (video_c != null) {
            avcodec_free_context(video_c);
            video_c = null;
        }

        // Free the audio samples frame
        if (samples_frame != null) {
            av_frame_free(samples_frame);
            samples_frame = null;
        }

        // Close the audio codec
        if (audio_c != null) {
            avcodec_free_context(audio_c);
            audio_c = null;
        }

        // Close the video file
        if (inputStream == null && oc != null && !oc.isNull()) {
            avformat_close_input(oc);
            oc = null;
        }

        if (img_convert_ctx != null) {
            sws_freeContext(img_convert_ctx);
            img_convert_ctx = null;
        }

        if (samples_ptr_out != null) {
            for (int i = 0; i < samples_ptr_out.length; i++) {
                av_free(samples_ptr_out[i].position(0));
            }
            samples_ptr_out = null;
            samples_buf_out = null;
        }

        if (samples_convert_ctx != null) {
            swr_free(samples_convert_ctx);
            samples_convert_ctx = null;
        }

        got_frame     = null;
        frameGrabbed  = false;
        frame         = null;
        timestamp     = 0;
        frameNumber   = 0;

        if (inputStream != null) {
            try {
                if (oc == null) {
                    // when called a second time
                    if (closeInputStream) {
                        inputStream.close();
                    }
                } else {
                    inputStream.reset();
                }
            } catch (IOException ex) {
                throw new Exception("Error on InputStream.close(): ", ex);
            } finally {
                inputStreams.remove(oc);
                if (avio != null) {
                    if (avio.buffer() != null) {
                        av_free(avio.buffer());
                        avio.buffer(null);
                    }
                    av_free(avio);
                    avio = null;
                }
                if (oc != null) {
                    avformat_free_context(oc);
                    oc = null;
                }
            }
        }
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    static Map<Pointer,InputStream> inputStreams = Collections.synchronizedMap(new HashMap<Pointer,InputStream>());

    static class ReadCallback extends Read_packet_Pointer_BytePointer_int {
        @Override public int call(Pointer opaque, BytePointer buf, int buf_size) {
            try {
                byte[] b = new byte[buf_size];
                InputStream is = inputStreams.get(opaque);
                int size = is.read(b, 0, buf_size);
                if (size < 0) {
                    return 0;
                } else {
                    buf.put(b, 0, size);
                    return size;
                }
            }
            catch (Throwable t) {
                System.err.println("Error on InputStream.read(): " + t);
                return -1;
            }
        }
    }

    static class SeekCallback extends Seek_Pointer_long_int {
        @Override public long call(Pointer opaque, long offset, int whence) {
            try {
                InputStream is = inputStreams.get(opaque);
                long size = 0;
                switch (whence) {
                    case 0: is.reset(); break; // SEEK_SET
                    case 1: break;             // SEEK_CUR
                    case 2:                    // SEEK_END
                        is.reset();
                        while (true) {
                            long n = is.skip(Long.MAX_VALUE);
                            if (n == 0) break;
                            size += n;
                        }
                        offset += size;
                        is.reset();
                        break;
                    case AVSEEK_SIZE:
                        long remaining = 0;
                        while (true) {
                            long n = is.skip(Long.MAX_VALUE);
                            if (n == 0) break;
                            remaining += n;
                        }
                        is.reset();
                        while (true) {
                            long n = is.skip(Long.MAX_VALUE);
                            if (n == 0) break;
                            size += n;
                        }
                        offset = size - remaining;
                        is.reset();
                        break;
                    default: return -1;
                }
                long remaining = offset;
                while (remaining > 0) {
                    long skipped = is.skip(remaining);
                    if (skipped == 0) break; // end of the stream
                    remaining -= skipped;
                }
                return whence == AVSEEK_SIZE ? size : 0;
            } catch (Throwable t) {
                System.err.println("Error on InputStream.reset() or skip(): " + t);
                return -1;
            }
        }
    }

    static ReadCallback readCallback = new ReadCallback();
    static SeekCallback seekCallback = new SeekCallback();
    static {
        PointerScope s = PointerScope.getInnerScope();
        if (s != null) {
            s.detach(readCallback);
            s.detach(seekCallback);
        }
    }

    private InputStream     inputStream;
    private boolean         closeInputStream;
    private int             maximumSize;
    private AVIOContext     avio;
    private String          filename;
    private AVFormatContext oc;
    private AVStream        video_st, audio_st;
    private AVCodecContext  video_c, audio_c;
    private AVFrame         picture, picture_rgb;
    private BytePointer[]   image_ptr;
    private Buffer[]        image_buf;
    private AVFrame         samples_frame;
    private BytePointer[]   samples_ptr;
    private Buffer[]        samples_buf;
    private BytePointer[]   samples_ptr_out;
    private Buffer[]        samples_buf_out;
    private AVPacket        pkt, pkt2;
    private int             sizeof_pkt;
    private int[]           got_frame;
    private SwsContext      img_convert_ctx;
    private SwrContext      samples_convert_ctx;
    private int             samples_channels, samples_format, samples_rate;
    private boolean         frameGrabbed;
    private Frame           frame;

    public boolean isCloseInputStream() {
        return closeInputStream;
    }
    public void setCloseInputStream(boolean closeInputStream) {
        this.closeInputStream = closeInputStream;
    }

    /**
     * Is there a video stream?
     * @return  {@code video_st!=null;}
     */
    public boolean hasVideo() {
        return video_st!=null;
    }

    /**
     * Is there an audio stream?
     * @return  {@code audio_st!=null;}
     */
    public boolean hasAudio() {
        return audio_st!=null;
    }

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
        return imageWidth > 0 || video_c == null ? super.getImageWidth() : video_c.width();
    }

    @Override public int getImageHeight() {
        return imageHeight > 0 || video_c == null ? super.getImageHeight() : video_c.height();
    }

    @Override public int getAudioChannels() {
        return audioChannels > 0 || audio_c == null ? super.getAudioChannels() : audio_c.channels();
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

    @Override public int getVideoCodec() {
        return video_c == null ? super.getVideoCodec() : video_c.codec_id();
    }

    @Override public int getVideoBitrate() {
        return video_c == null ? super.getVideoBitrate() : (int)video_c.bit_rate();
    }

    @Override public double getAspectRatio() {
        if (video_st == null) {
            return super.getAspectRatio();
        } else {
            AVRational r = av_guess_sample_aspect_ratio(oc, video_st, picture);
            double a = (double)r.num() / r.den();
            return a == 0.0 ? 1.0 : a;
        }
    }

    /** Returns {@link #getVideoFrameRate()} */
    @Override public double getFrameRate() {
        return getVideoFrameRate();
    }

    /**Estimation of audio frames per second
     *
     * @return (double) getSampleRate()) / samples_frame.nb_samples()
     * if samples_frame.nb_samples() is not zero, otherwise return 0
     */
    public double getAudioFrameRate() {
        if (audio_st == null) {
            return 0.0;
        } else {
            if (samples_frame == null || samples_frame.nb_samples() == 0) {
                try {
                    grabFrame(true, false, false, false);
                    frameGrabbed = true;
                } catch (Exception e) {
                    return 0.0;
                }
            }
            if (samples_frame != null || samples_frame.nb_samples() != 0)
                return ((double) getSampleRate()) / samples_frame.nb_samples();
            else return 0.0;

        }
    }

    public double getVideoFrameRate() {
        if (video_st == null) {
            return super.getFrameRate();
        } else {
            AVRational r = video_st.avg_frame_rate();
            if (r.num() == 0 && r.den() == 0) {
                r = video_st.r_frame_rate();
            }
            return (double)r.num() / r.den();
        }
    }

    @Override public int getAudioCodec() {
        return audio_c == null ? super.getAudioCodec() : audio_c.codec_id();
    }

    @Override public int getAudioBitrate() {
        return audio_c == null ? super.getAudioBitrate() : (int)audio_c.bit_rate();
    }

    @Override public int getSampleFormat() {
        if (sampleMode == SampleMode.SHORT || sampleMode == SampleMode.FLOAT) {
            if (sampleFormat == AV_SAMPLE_FMT_NONE) {
                return sampleMode == SampleMode.SHORT ? AV_SAMPLE_FMT_S16 : AV_SAMPLE_FMT_FLT;
            } else {
                return sampleFormat;
            }
        } else if (audio_c != null) { // RAW
            return audio_c.sample_fmt();
        } else {
            return super.getSampleFormat();
        }
    }

    @Override public int getSampleRate() {
        return sampleRate > 0 || audio_c == null ? super.getSampleRate() : audio_c.sample_rate();
    }

    @Override public Map<String, String> getMetadata() {
        if (oc == null) {
            return super.getMetadata();
        }
        AVDictionaryEntry entry = null;
        Map<String, String> metadata = new HashMap<String, String>();
        while ((entry = av_dict_get(oc.metadata(), "", entry, AV_DICT_IGNORE_SUFFIX)) != null) {
            metadata.put(entry.key().getString(), entry.value().getString());
        }
        return metadata;
    }

    @Override public Map<String, String> getVideoMetadata() {
        if (video_st == null) {
            return super.getVideoMetadata();
        }
        AVDictionaryEntry entry = null;
        Map<String, String> metadata = new HashMap<String, String>();
        while ((entry = av_dict_get(video_st.metadata(), "", entry, AV_DICT_IGNORE_SUFFIX)) != null) {
            metadata.put(entry.key().getString(), entry.value().getString());
        }
        return metadata;
    }

    @Override public Map<String, String> getAudioMetadata() {
        if (audio_st == null) {
            return super.getAudioMetadata();
        }
        AVDictionaryEntry entry = null;
        Map<String, String> metadata = new HashMap<String, String>();
        while ((entry = av_dict_get(audio_st.metadata(), "", entry, AV_DICT_IGNORE_SUFFIX)) != null) {
            metadata.put(entry.key().getString(), entry.value().getString());
        }
        return metadata;
    }

    @Override public String getMetadata(String key) {
        if (oc == null) {
            return super.getMetadata(key);
        }
        AVDictionaryEntry entry = av_dict_get(oc.metadata(), key, null, 0);
        return entry == null || entry.value() == null ? null : entry.value().getString();
    }

    @Override public String getVideoMetadata(String key) {
        if (video_st == null) {
            return super.getVideoMetadata(key);
        }
        AVDictionaryEntry entry = av_dict_get(video_st.metadata(), key, null, 0);
        return entry == null || entry.value() == null ? null : entry.value().getString();
    }

    @Override public String getAudioMetadata(String key) {
        if (audio_st == null) {
            return super.getAudioMetadata(key);
        }
        AVDictionaryEntry entry = av_dict_get(audio_st.metadata(), key, null, 0);
        return entry == null || entry.value() == null ? null : entry.value().getString();
    }

    /** default override of super.setFrameNumber implies setting
     *  of a frame close to a video frame having that number */
    @Override public void setFrameNumber(int frameNumber) throws Exception {
        if (hasVideo()) setTimestamp(Math.round(1000000L * frameNumber / getFrameRate()));
        else super.frameNumber = frameNumber;
    }

    /** if there is video stream tries to seek to video frame with corresponding timestamp
     *  otherwise sets super.frameNumber only because frameRate==0 if there is no video stream */
    public void setVideoFrameNumber(int frameNumber) throws Exception {
        // best guess, AVSEEK_FLAG_FRAME has not been implemented in FFmpeg...
        if (hasVideo()) setVideoTimestamp(Math.round(1000000L * frameNumber / getFrameRate()));
        else super.frameNumber = frameNumber;
    }

    /** if there is audio stream tries to seek to audio frame with corresponding timestamp
     *  ignoring otherwise */
    public void setAudioFrameNumber(int frameNumber) throws Exception {
        // best guess, AVSEEK_FLAG_FRAME has not been implemented in FFmpeg...
        if (hasAudio()) setAudioTimestamp(Math.round(1000000L * frameNumber / getAudioFrameRate()));

    }

    /** setTimestamp without checking frame content (using old code used in JavaCV versions prior to 1.4.1) */
    @Override public void setTimestamp(long timestamp) throws Exception {
        setTimestamp(timestamp, false);
    }

    /** setTimestamp with possibility to select between old quick seek code or new code
     * doing check of frame content. The frame check can be useful with corrupted files, when seeking may
     * end up with an empty frame not containing video nor audio */
    public void setTimestamp(long timestamp, boolean checkFrame) throws Exception {
        setTimestamp(timestamp, checkFrame ? EnumSet.of(Frame.Type.VIDEO, Frame.Type.AUDIO) : null);
    }

    /** setTimestamp with resulting video frame type if there is a video stream*/
    public void setVideoTimestamp(long timestamp) throws Exception {
        setTimestamp(timestamp, EnumSet.of(Frame.Type.VIDEO));
    }

    /** setTimestamp with resulting audio frame type if there is an audio stream*/
    public void setAudioTimestamp(long timestamp) throws Exception {
        setTimestamp(timestamp, EnumSet.of(Frame.Type.AUDIO));
    }

    /** setTimestamp with a priority the resulting frame should be:
     *  video (frameTypesToSeek contains only Frame.Type.VIDEO),
     *  audio (frameTypesToSeek contains only Frame.Type.AUDIO),
     *  or any (frameTypesToSeek contains both)
     */
    private void setTimestamp(long timestamp, EnumSet<Frame.Type> frameTypesToSeek) throws Exception {
        int ret;
        if (oc == null) {
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
            if (video_c != null) {
                avcodec_flush_buffers(video_c);
            }
            if (audio_c != null) {
                avcodec_flush_buffers(audio_c);
            }
            if (pkt2.size() > 0) {
                pkt2.size(0);
                av_packet_unref(pkt);
            }
            /*     After the call of ffmpeg's avformat_seek_file(...) with the flag set to AVSEEK_FLAG_BACKWARD
             * the decoding position should be located before the requested timestamp in a closest position
             * from which all the active streams can be decoded successfully.
             * The following seeking consists of two stages:
             * 1. Grab frames till the frame corresponding to that "closest" position
             * (the first frame containing decoded data).
             *
             * 2. Grab frames till the desired timestamp is reached. The number of steps is restricted
             * by doubled estimation of frames between that "closest" position and the desired position.
             *
             * frameTypesToSeek parameter sets the preferred type of frames to seek.
             * It can be chosen from three possible types: VIDEO, AUDIO or any of them.
             * The setting means only a preference in the type. That is, if VIDEO or AUDIO is
             * specified but the file does not have video or audio stream - any type will be used instead.
             *
             *
             * TODO
             *  Sometimes the ffmpeg's avformat_seek_file(...) function brings us not to a position before
             *  the desired but few frames after.... What can be a the solution in this case if we really need
             *  a frame-precision seek? Probably we may try to request even earlier timestamp and look if this
             *  will bring us before the desired position.
             *
            */

            if (frameTypesToSeek != null) { //new code providing check of frame content while seeking to the timestamp
                boolean has_video = hasVideo();
                boolean has_audio = hasAudio();

                if (has_video || has_audio) {
                    if ((frameTypesToSeek.contains(Frame.Type.VIDEO) && !has_video ) ||
                            (frameTypesToSeek.contains(Frame.Type.AUDIO) && !has_audio ))
                        frameTypesToSeek = EnumSet.of(Frame.Type.VIDEO, Frame.Type.AUDIO);

                    long initialSeekPosition = Long.MIN_VALUE;
                    long maxSeekSteps = 0;
                    long count = 0;
                    Frame seekFrame = null;

                    while(count++ < 1000) { //seek to a first frame containing video or audio after avformat_seek_file(...)
                        seekFrame = grabFrame(true, true, false, false);
                        if (seekFrame == null) return; //is it better to throw NullPointerException?
                        EnumSet<Frame.Type> frameTypes = seekFrame.getTypes();
                        frameTypes.retainAll(frameTypesToSeek);
                        if (!frameTypes.isEmpty()) {
                            initialSeekPosition = seekFrame.timestamp;
                            //the position closest to the requested timestamp from which it can be reached by sequential grabFrame calls
                            break;
                        }
                    }
                    if (has_video && this.getFrameRate() > 0) {
                        //estimation of video frame duration
                        double deltaTimeStamp = 1000000.0/this.getFrameRate();
                        if (initialSeekPosition < timestamp - deltaTimeStamp/2)
                            maxSeekSteps = (long)(10*(timestamp - initialSeekPosition)/deltaTimeStamp);
                    } else if (has_audio && this.getAudioFrameRate() > 0) {
                        //estimation of audio frame duration
                        double deltaTimeStamp = 1000000.0/this.getAudioFrameRate();
                        if (initialSeekPosition < timestamp - deltaTimeStamp/2)
                            maxSeekSteps = (long)(10*(timestamp - initialSeekPosition)/deltaTimeStamp);
                    } else
                        //zero frameRate
                        if (initialSeekPosition < timestamp - 1L) maxSeekSteps = 1000;

                    count = 0;
                    while(count < maxSeekSteps) {
                        seekFrame = grabFrame(true, true, false, false);
                        if (seekFrame == null) return; //is it better to throw NullPointerException?
                        EnumSet<Frame.Type> frameTypes = seekFrame.getTypes();
                        frameTypes.retainAll(frameTypesToSeek);
                        if (!frameTypes.isEmpty()) {
                            count++;
                            if (this.timestamp >= timestamp - 1) break;
                        }
                    }

                    frameGrabbed = true;
                }
            } else { //old quick seeking code used in JavaCV versions prior to 1.4.1
                /* comparing to timestamp +/- 1 avoids rouding issues for framerates
                which are no proper divisors of 1000000, e.g. where
                av_frame_get_best_effort_timestamp in grabFrame sets this.timestamp
                to ...666 and the given timestamp has been rounded to ...667
                (or vice versa)
                 */
                int count = 0; // prevent infinite loops with corrupted files
                while (this.timestamp > timestamp + 1 && grabFrame(true, true, false, false) != null && count++ < 1000) {
                    // flush frames if seeking backwards
                }
                count = 0;
                while (this.timestamp < timestamp - 1 && grabFrame(true, true, false, false) != null && count++ < 1000) {
                    // decode up to the desired frame
                }
                frameGrabbed = true;
            }
        }
    }

    /** Returns {@link #getLengthInVideoFrames()} */
    @Override public int getLengthInFrames() {
        // best guess...
        return getLengthInVideoFrames();
    }

    @Override public long getLengthInTime() {
        return oc.duration() * 1000000L / AV_TIME_BASE;
    }

    /** Returns {@code (int) Math.round(getLengthInTime() * getFrameRate() / 1000000L)}, which is an approximation in general. */
    public int getLengthInVideoFrames() {
        // best guess...
        return (int) Math.round(getLengthInTime() * getFrameRate() / 1000000L);
    }

    public int getLengthInAudioFrames() {
        // best guess...
        double afr = getAudioFrameRate();
        if (afr > 0) return (int) (getLengthInTime() * afr / 1000000L);
        else return 0;
    }

    public AVFormatContext getFormatContext() {
        return oc;
    }

    public void start() throws Exception {
        synchronized (org.bytedeco.ffmpeg.global.avcodec.class) {
            startUnsafe();
        }
    }
    public void startUnsafe() throws Exception {
        if (oc != null && !oc.isNull()) {
            throw new Exception("start() has already been called: Call stop() before calling start() again.");
        }

        int ret;
        img_convert_ctx = null;
        oc              = new AVFormatContext(null);
        video_c         = null;
        audio_c         = null;
        pkt             = new AVPacket();
        pkt2            = new AVPacket();
        sizeof_pkt      = pkt.sizeof();
        got_frame       = new int[1];
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
        if (pixelFormat >= 0) {
            av_dict_set(options, "pixel_format", av_get_pix_fmt_name(pixelFormat).getString(), 0);
        } else if (imageMode != ImageMode.RAW) {
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
        for (Entry<String, String> e : this.options.entrySet()) {
            av_dict_set(options, e.getKey(), e.getValue(), 0);
        }
        if (inputStream != null) {
            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream);
            }
            inputStream.mark(maximumSize);
            oc = avformat_alloc_context();
            avio = avio_alloc_context(new BytePointer(av_malloc(4096)), 4096, 0, oc, readCallback, null, seekCallback);
            oc.pb(avio);

            filename = inputStream.toString();
            inputStreams.put(oc, inputStream);
        }
        if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
            av_dict_set(options, "pixel_format", null, 0);
            if ((ret = avformat_open_input(oc, filename, f, options)) < 0) {
                throw new Exception("avformat_open_input() error " + ret + ": Could not open input \"" + filename + "\". (Has setFormat() been called?)");
            }
        }
        av_dict_free(options);

        oc.max_delay(maxDelay);

        // Retrieve stream information
        if ((ret = avformat_find_stream_info(oc, (PointerPointer)null)) < 0) {
            throw new Exception("avformat_find_stream_info() error " + ret + ": Could not find stream information.");
        }

        if (av_log_get_level() >= AV_LOG_INFO) {
            // Dump information about file onto standard error
            av_dump_format(oc, 0, filename, 0);
        }

        // Find the first video and audio stream, unless the user specified otherwise
        video_st = audio_st = null;
        AVCodecParameters video_par = null, audio_par = null;
        int nb_streams = oc.nb_streams();
        for (int i = 0; i < nb_streams; i++) {
            AVStream st = oc.streams(i);
            // Get a pointer to the codec context for the video or audio stream
            AVCodecParameters par = st.codecpar();
            if (video_st == null && par.codec_type() == AVMEDIA_TYPE_VIDEO && (videoStream < 0 || videoStream == i)) {
                video_st = st;
                video_par = par;
                videoStream = i;
            } else if (audio_st == null && par.codec_type() == AVMEDIA_TYPE_AUDIO && (audioStream < 0 || audioStream == i)) {
                audio_st = st;
                audio_par = par;
                audioStream = i;
            }
        }
        if (video_st == null && audio_st == null) {
            throw new Exception("Did not find a video or audio stream inside \"" + filename
                    + "\" for videoStream == " + videoStream + " and audioStream == " + audioStream + ".");
        }

        if (video_st != null) {
            // Find the decoder for the video stream
            AVCodec codec = avcodec_find_decoder_by_name(videoCodecName);
            if (codec == null) {
                codec = avcodec_find_decoder(video_par.codec_id());
            }
            if (codec == null) {
                throw new Exception("avcodec_find_decoder() error: Unsupported video format or codec not found: " + video_par.codec_id() + ".");
            }

            /* Allocate a codec context for the decoder */
            if ((video_c = avcodec_alloc_context3(codec)) == null) {
                throw new Exception("avcodec_alloc_context3() error: Could not allocate video decoding context.");
            }

            /* copy the stream parameters from the muxer */
            if ((ret = avcodec_parameters_to_context(video_c, video_st.codecpar())) < 0) {
                releaseUnsafe();
                throw new Exception("avcodec_parameters_to_context() error: Could not copy the video stream parameters.");
            }

            options = new AVDictionary(null);
            for (Entry<String, String> e : videoOptions.entrySet()) {
                av_dict_set(options, e.getKey(), e.getValue(), 0);
            }

            // Enable multithreading when available
            video_c.thread_count(0);

            // Open video codec
            if ((ret = avcodec_open2(video_c, codec, options)) < 0) {
                throw new Exception("avcodec_open2() error " + ret + ": Could not open video codec.");
            }
            av_dict_free(options);

            // Hack to correct wrong frame rates that seem to be generated by some codecs
            if (video_c.time_base().num() > 1000 && video_c.time_base().den() == 1) {
                video_c.time_base().den(1000);
            }

            // Allocate video frame and an AVFrame structure for the RGB image
            if ((picture = av_frame_alloc()) == null) {
                throw new Exception("av_frame_alloc() error: Could not allocate raw picture frame.");
            }
            if ((picture_rgb = av_frame_alloc()) == null) {
                throw new Exception("av_frame_alloc() error: Could not allocate RGB picture frame.");
            }

            initPictureRGB();
        }

        if (audio_st != null) {
            // Find the decoder for the audio stream
            AVCodec codec = avcodec_find_decoder_by_name(audioCodecName);
            if (codec == null) {
                codec = avcodec_find_decoder(audio_par.codec_id());
            }
            if (codec == null) {
                throw new Exception("avcodec_find_decoder() error: Unsupported audio format or codec not found: " + audio_par.codec_id() + ".");
            }

            /* Allocate a codec context for the decoder */
            if ((audio_c = avcodec_alloc_context3(codec)) == null) {
                throw new Exception("avcodec_alloc_context3() error: Could not allocate audio decoding context.");
            }

            /* copy the stream parameters from the muxer */
            if ((ret = avcodec_parameters_to_context(audio_c, audio_st.codecpar())) < 0) {
                releaseUnsafe();
                throw new Exception("avcodec_parameters_to_context() error: Could not copy the audio stream parameters.");
            }

            options = new AVDictionary(null);
            for (Entry<String, String> e : audioOptions.entrySet()) {
                av_dict_set(options, e.getKey(), e.getValue(), 0);
            }

            // Enable multithreading when available
            audio_c.thread_count(0);

            // Open audio codec
            if ((ret = avcodec_open2(audio_c, codec, options)) < 0) {
                throw new Exception("avcodec_open2() error " + ret + ": Could not open audio codec.");
            }
            av_dict_free(options);

            // Allocate audio samples frame
            if ((samples_frame = av_frame_alloc()) == null) {
                throw new Exception("av_frame_alloc() error: Could not allocate audio frame.");
            }

            samples_ptr = new BytePointer[] { null };
            samples_buf = new Buffer[] { null };
        }
    }

    private void initPictureRGB() {
        int width  = imageWidth  > 0 ? imageWidth  : video_c.width();
        int height = imageHeight > 0 ? imageHeight : video_c.height();

        switch (imageMode) {
            case COLOR:
            case GRAY:
                // If size changes I new allocation is needed -> free the old one.
                if (image_ptr != null) {
                    // First kill all references, then free it.
                    image_buf = null;
                    BytePointer[] temp = image_ptr;
                    image_ptr = null;
                    av_free(temp[0]);
                }
                int fmt = getPixelFormat();

                // Determine required buffer size and allocate buffer
                int size = av_image_get_buffer_size(fmt, width, height, 1);
                image_ptr = new BytePointer[] { new BytePointer(av_malloc(size)).capacity(size) };
                image_buf = new Buffer[] { image_ptr[0].asBuffer() };

                // Assign appropriate parts of buffer to image planes in picture_rgb
                // Note that picture_rgb is an AVFrame, but AVFrame is a superset of AVPicture
                av_image_fill_arrays(new PointerPointer(picture_rgb), picture_rgb.linesize(), image_ptr[0], fmt, width, height, 1);
                picture_rgb.format(fmt);
                picture_rgb.width(width);
                picture_rgb.height(height);
                break;

            case RAW:
                image_ptr = new BytePointer[] { null };
                image_buf = new Buffer[] { null };
                break;

            default:
                assert false;
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
            av_packet_unref(pkt);
        }
        for (int i = 0; i < numBuffers+1; i++) {
            if (av_read_frame(oc, pkt) < 0) {
                return;
            }
            av_packet_unref(pkt);
        }
    }

    private void processImage() throws Exception {
        frame.imageWidth  = imageWidth  > 0 ? imageWidth  : video_c.width();
        frame.imageHeight = imageHeight > 0 ? imageHeight : video_c.height();
        frame.imageDepth = Frame.DEPTH_UBYTE;
        switch (imageMode) {
            case COLOR:
            case GRAY:
                // Deinterlace Picture
                if (deinterlace) {
                    throw new Exception("Cannot deinterlace: Functionality moved to FFmpegFrameFilter.");
                }

                // Has the size changed?
                if (frame.imageWidth != picture_rgb.width() || frame.imageHeight != picture_rgb.height()) {
                    initPictureRGB();
                }

                // Convert the image into BGR or GRAY format that OpenCV uses
                img_convert_ctx = sws_getCachedContext(img_convert_ctx,
                        video_c.width(), video_c.height(), video_c.pix_fmt(),
                        frame.imageWidth, frame.imageHeight, getPixelFormat(),
                        imageScalingFlags != 0 ? imageScalingFlags : SWS_BILINEAR,
                        null, null, (DoublePointer)null);
                if (img_convert_ctx == null) {
                    throw new Exception("sws_getCachedContext() error: Cannot initialize the conversion context.");
                }

                // Convert the image from its native format to RGB or GRAY
                sws_scale(img_convert_ctx, new PointerPointer(picture), picture.linesize(), 0,
                        video_c.height(), new PointerPointer(picture_rgb), picture_rgb.linesize());
                frame.imageStride = picture_rgb.linesize(0);
                frame.image = image_buf;
                frame.opaque = picture_rgb;
                break;

            case RAW:
                frame.imageStride = picture.linesize(0);
                BytePointer ptr = picture.data(0);
                if (ptr != null && !ptr.equals(image_ptr[0])) {
                    image_ptr[0] = ptr.capacity(frame.imageHeight * frame.imageStride);
                    image_buf[0] = ptr.asBuffer();
                }
                frame.image = image_buf;
                frame.opaque = picture;
                break;

            default:
                assert false;
        }
        frame.image[0].limit(frame.imageHeight * frame.imageStride);
        frame.imageChannels = frame.imageStride / frame.imageWidth;
    }

    private void processSamples() throws Exception {
        int ret;

        int sample_format = samples_frame.format();
        int planes = av_sample_fmt_is_planar(sample_format) != 0 ? (int)samples_frame.channels() : 1;
        int data_size = av_samples_get_buffer_size((IntPointer)null, audio_c.channels(),
                samples_frame.nb_samples(), audio_c.sample_fmt(), 1) / planes;
        if (samples_buf == null || samples_buf.length != planes) {
            samples_ptr = new BytePointer[planes];
            samples_buf = new Buffer[planes];
        }
        frame.sampleRate = audio_c.sample_rate();
        frame.audioChannels = audio_c.channels();
        frame.samples = samples_buf;
        frame.opaque = samples_frame;
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

        if (audio_c.channels() != getAudioChannels() || audio_c.sample_fmt() != getSampleFormat() || audio_c.sample_rate() != getSampleRate()) {
            if (samples_convert_ctx == null || samples_channels != getAudioChannels() || samples_format != getSampleFormat() || samples_rate != getSampleRate()) {
                samples_convert_ctx = swr_alloc_set_opts(samples_convert_ctx, av_get_default_channel_layout(getAudioChannels()), getSampleFormat(), getSampleRate(),
                        av_get_default_channel_layout(audio_c.channels()), audio_c.sample_fmt(), audio_c.sample_rate(), 0, null);
                if (samples_convert_ctx == null) {
                    throw new Exception("swr_alloc_set_opts() error: Cannot allocate the conversion context.");
                } else if ((ret = swr_init(samples_convert_ctx)) < 0) {
                    throw new Exception("swr_init() error " + ret + ": Cannot initialize the conversion context.");
                }
                samples_channels = getAudioChannels();
                samples_format = getSampleFormat();
                samples_rate = getSampleRate();
            }

            int sample_size_in = samples_frame.nb_samples();
            int planes_out = av_sample_fmt_is_planar(samples_format) != 0 ? (int)samples_frame.channels() : 1;
            int sample_size_out = swr_get_out_samples(samples_convert_ctx, sample_size_in);
            int sample_bytes_out = av_get_bytes_per_sample(samples_format);
            int buffer_size_out = sample_size_out * sample_bytes_out * (planes_out > 1 ? 1 : samples_channels);
            if (samples_buf_out == null || samples_buf.length != planes_out || samples_ptr_out[0].capacity() < buffer_size_out) {
                for (int i = 0; samples_ptr_out != null && i < samples_ptr_out.length; i++) {
                    av_free(samples_ptr_out[i].position(0));
                }
                samples_ptr_out = new BytePointer[planes_out];
                samples_buf_out = new Buffer[planes_out];

                for (int i = 0; i < planes_out; i++) {
                    samples_ptr_out[i] = new BytePointer(av_malloc(buffer_size_out)).capacity(buffer_size_out);
                    ByteBuffer b = samples_ptr_out[i].asBuffer();
                    switch (samples_format) {
                        case AV_SAMPLE_FMT_U8:
                        case AV_SAMPLE_FMT_U8P:  samples_buf_out[i] = b; break;
                        case AV_SAMPLE_FMT_S16:
                        case AV_SAMPLE_FMT_S16P: samples_buf_out[i] = b.asShortBuffer();  break;
                        case AV_SAMPLE_FMT_S32:
                        case AV_SAMPLE_FMT_S32P: samples_buf_out[i] = b.asIntBuffer();    break;
                        case AV_SAMPLE_FMT_FLT:
                        case AV_SAMPLE_FMT_FLTP: samples_buf_out[i] = b.asFloatBuffer();  break;
                        case AV_SAMPLE_FMT_DBL:
                        case AV_SAMPLE_FMT_DBLP: samples_buf_out[i] = b.asDoubleBuffer(); break;
                        default: assert false;
                    }
                }
            }
            frame.sampleRate = samples_rate;
            frame.audioChannels = samples_channels;
            frame.samples = samples_buf_out;

            if ((ret = swr_convert(samples_convert_ctx, new PointerPointer(samples_ptr_out), sample_size_out, new PointerPointer(samples_ptr), sample_size_in)) < 0) {
                throw new Exception("swr_convert() error " + ret + ": Cannot convert audio samples.");
            }
            for (int i = 0; i < planes_out; i++) {
                samples_ptr_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : samples_channels));
                samples_buf_out[i].position(0).limit(ret * (planes_out > 1 ? 1 : samples_channels));
            }
        }
    }

    public Frame grab() throws Exception {
        return grabFrame(true, true, true, false);
    }
    public Frame grabImage() throws Exception {
        return grabFrame(false, true, true, false);
    }
    public Frame grabSamples() throws Exception {
        return grabFrame(true, false, true, false);
    }
    public Frame grabKeyFrame() throws Exception {
        return grabFrame(false, true, true, true);
    }
    public Frame grabFrame(boolean doAudio, boolean doVideo, boolean doProcessing, boolean keyFrames) throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
        } else if ((!doVideo || video_st == null) && (!doAudio || audio_st == null)) {
            return null;
        }
        boolean videoFrameGrabbed = frameGrabbed && frame.image != null;
        boolean audioFrameGrabbed = frameGrabbed && frame.samples != null;
        frameGrabbed = false;
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
        if (doVideo && videoFrameGrabbed) {
            if (doProcessing) {
                processImage();
            }
            frame.keyFrame = picture.key_frame() != 0;
            return frame;
        } else if (doAudio && audioFrameGrabbed) {
            if (doProcessing) {
                processSamples();
            }
            frame.keyFrame = samples_frame.key_frame() != 0;
            return frame;
        }
        boolean done = false;
        while (!done) {
            if (pkt2.size() <= 0) {
                if (av_read_frame(oc, pkt) < 0) {
                    if (doVideo && video_st != null) {
                        // The video codec may have buffered some frames
                        pkt.stream_index(video_st.index());
                        pkt.flags(AV_PKT_FLAG_KEY);
                        pkt.data(null);
                        pkt.size(0);
                    } else {
                        return null;
                    }
                }
            }

            // Is this a packet from the video stream?
            if (doVideo && video_st != null && pkt.stream_index() == video_st.index()
                    && (!keyFrames || pkt.flags() == AV_PKT_FLAG_KEY)) {
                // Decode video frame
                int len = avcodec_decode_video2(video_c, picture, got_frame, pkt);

                // Did we get a video frame?
                if (len >= 0 && got_frame[0] != 0
                        && (!keyFrames || picture.pict_type() == AV_PICTURE_TYPE_I)) {
                    long pts = av_frame_get_best_effort_timestamp(picture);
                    AVRational time_base = video_st.time_base();
                    timestamp = 1000000L * pts * time_base.num() / time_base.den();
                    // best guess, AVCodecContext.frame_number = number of decoded frames...
                    frameNumber = (int)Math.round(timestamp * getFrameRate() / 1000000L);
                    frame.image = image_buf;
                    if (doProcessing) {
                        processImage();
                    }
                    done = true;
                    frame.timestamp = timestamp;
                    frame.keyFrame = picture.key_frame() != 0;
                } else if (pkt.data() == null && pkt.size() == 0) {
                    return null;
                }
            } else if (doAudio && audio_st != null && pkt.stream_index() == audio_st.index()) {
                if (pkt2.size() <= 0) {
                    // HashMap is unacceptably slow on Android
                    // pkt2.put(pkt);
                    BytePointer.memcpy(pkt2, pkt, sizeof_pkt);
                }
                av_frame_unref(samples_frame);
                // Decode audio frame
                int len = avcodec_decode_audio4(audio_c, samples_frame, got_frame, pkt2);
                if (len <= 0) {
                    // On error, trash the whole packet
                    pkt2.size(0);
                } else {
                    pkt2.data(pkt2.data().position(len));
                    pkt2.size(pkt2.size() - len);
                    if (got_frame[0] != 0) {
                        long pts = av_frame_get_best_effort_timestamp(samples_frame);
                        AVRational time_base = audio_st.time_base();
                        timestamp = 1000000L * pts * time_base.num() / time_base.den();
                        frame.samples = samples_buf;
                        /* if a frame has been decoded, output it */
                        if (doProcessing) {
                            processSamples();
                        }
                        done = true;
                        frame.timestamp = timestamp;
                        frame.keyFrame = samples_frame.key_frame() != 0;
                    }
                }
            }

            if (pkt2.size() <= 0) {
                // Free the packet that was allocated by av_read_frame
                av_packet_unref(pkt);
            }
        }
        return frame;
    }

    public AVPacket grabPacket() throws Exception {
        if (oc == null || oc.isNull()) {
            throw new Exception("Could not trigger: No AVFormatContext. (Has start() been called?)");
        }

        // Return the next frame of a stream.
        if (av_read_frame(oc, pkt) < 0) {
            return null;
        }

        return pkt;
    }
}
