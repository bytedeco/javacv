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
 * Based on the avcodec_sample.0.5.0.c file available at
 * http://web.me.com/dhoerl/Home/Tech_Blog/Entries/2009/1/22_Revised_avcodec_sample.c_files/avcodec_sample.0.5.0.c
 * by Martin Böhme, Stephen Dranger, and David Hoerl
 * as well as hacks documented in the ffplay.c file (Copyright (c) 2003 Fabrice Bellard)
 * that came with FFmpeg 0.6.5
 */

package com.googlecode.javacv;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.PointerPointer;
import java.io.File;
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
        avcodec_init();
        avcodec_register_all();
        avdevice_register_all();
        av_register_all();

        this.filename = filename;
    }
    public void release() throws Exception {
        // Free the RGB image
        if (buffer != null) {
            av_free(buffer);
            buffer = null;
        }
        if (pFrameRGB != null) {
            av_free(pFrameRGB);
            pFrameRGB = null;
        }

        // Free the native format frame
        if (pFrame != null) {
            av_free(pFrame);
            pFrame = null;
        }

        // Close the video codec
        if (pVideoCodecCtx != null) {
            avcodec_close(pVideoCodecCtx);
            pVideoCodecCtx = null;
        }

        if (samplesPointer != null) {
            av_free(samplesPointer);
            samplesPointer = null;
            samplesBuffer = null;
        }

        // Close the audio codec
        if (pAudioCodecCtx != null) {
            avcodec_close(pAudioCodecCtx);
            pAudioCodecCtx = null;
        }

        // Close the video file
        if (pFormatCtx != null && !pFormatCtx.isNull()) {
            av_close_input_file(pFormatCtx);
            pFormatCtx = null;
        }

        if (img_convert_ctx != null && !img_convert_ctx.isNull()) {
            sws_freeContext(img_convert_ctx);
            img_convert_ctx = null;
        }

        packet        = null;
        frameFinished = null;
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
    private AVFormatContext pFormatCtx;
    private int             videoStream, audioStream;
    private AVStream        pVideoStream, pAudioStream;
    private AVCodecContext  pVideoCodecCtx, pAudioCodecCtx;
    private AVCodec         pVideoCodec, pAudioCodec;
    private AVFrame         pFrame, pFrameRGB;
    private BytePointer     samplesPointer;
    private ByteBuffer      samplesBuffer;
    private AVPacket        packet;
    private int[]           frameFinished;
    private int             numBytes;
    private BytePointer     buffer;
    private SwsContext      img_convert_ctx;
    private IplImage        return_image;
    private boolean         frameGrabbed;
    private Frame           frame;

    private long faulty_pts;
    private long faulty_dts;
    private long last_dts_for_fault_detection;
    private long last_pts_for_fault_detection;

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    @Override public String getFormat() {
        if (pFormatCtx == null) {
            return super.getFormat();
        } else {
            return pFormatCtx.iformat().name().getString();
        }
    }

    @Override public int getImageWidth() {
        return return_image == null ? super.getImageWidth() : return_image.width();
    }

    @Override public int getImageHeight() {
        return return_image == null ? super.getImageHeight() : return_image.height();
    }

    @Override public int getAudioChannels() {
        return pAudioCodecCtx == null ? super.getAudioChannels() : pAudioCodecCtx.channels();
    }

    @Override public int getPixelFormat() {
        if (imageMode == ImageMode.COLOR || imageMode == ImageMode.GRAY) {
            if (pixelFormat == PIX_FMT_NONE) {
                return imageMode == ImageMode.COLOR ? PIX_FMT_BGR24 : PIX_FMT_GRAY8;
            } else {
                return pixelFormat;
            }
        } else if (pVideoCodecCtx != null) { // RAW
            return pVideoCodecCtx.pix_fmt();
        } else {
            return super.getPixelFormat();
        }
    }

    @Override public double getFrameRate() {
        if (pVideoStream == null) {
            return super.getFrameRate();
        } else {
            AVRational r = pVideoStream.r_frame_rate();
            return (double)r.num()/r.den();
        }
    }

    @Override public int getSampleFormat() {
        return pAudioCodecCtx == null ? super.getSampleFormat() : pAudioCodecCtx.sample_fmt();
    }

    @Override public int getSampleRate() {
        return pAudioCodecCtx == null ? super.getSampleRate() : pAudioCodecCtx.sample_rate();
    }

    @Override public void setFrameNumber(int frameNumber) throws Exception {
        // best guess, AVSEEK_FLAG_FRAME has not been implemented in FFmpeg...
        setTimestamp((long)(1000000*frameNumber/getFrameRate()));
    }

    @Override public void setTimestamp(long timestamp) throws Exception {
        if (pFormatCtx == null || pVideoCodecCtx == null) {
            super.setTimestamp(timestamp);
        } else {
            timestamp = timestamp * AV_TIME_BASE / 1000000;
            /* add the stream start time */
            if (pFormatCtx.start_time() != AV_NOPTS_VALUE) {
                timestamp += pFormatCtx.start_time();
            }
            if (avformat_seek_file(pFormatCtx, -1, Long.MIN_VALUE, timestamp, Long.MAX_VALUE, AVSEEK_FLAG_BACKWARD) < 0) {
                throw new Exception("Could not seek file to timestamp " + timestamp + ".");
            }
            avcodec_flush_buffers(pVideoCodecCtx);
            if (pAudioCodecCtx != null) {
                avcodec_flush_buffers(pAudioCodecCtx);
            }
            while (this.timestamp > timestamp && grab(false) != null) {
                // flush frames if seeking backwards
                last_dts_for_fault_detection = last_pts_for_fault_detection = 0;
            }
            while (this.timestamp < timestamp && grab(false) != null) {
                // decode up to the desired frame
                last_dts_for_fault_detection = last_pts_for_fault_detection = 0;
            }
            frameGrabbed = true;
        }
    }

    @Override public int getLengthInFrames() {
        // best guess...
        return (int)(1000000*getFrameRate()/getLengthInTime());
    }
    @Override public long getLengthInTime() {
        return pFormatCtx.duration() * 1000000 / AV_TIME_BASE;
    }

    public void start() throws Exception {
        pFormatCtx     = new AVFormatContext(null);
        pVideoCodecCtx = null;
        pAudioCodecCtx = null;
        packet         = new AVPacket();
        frameFinished  = new int[1];
        return_image   = null;
        frameGrabbed   = false;
        frame          = new Frame();
        timestamp      = 0;
        frameNumber    = 0;

        // Open video file
        AVInputFormat f = null;
        if (format != null && format.length() > 0) {
            f = av_find_input_format(format);
            if (f == null) {
                throw new Exception("Could not find input format \"" + format + "\".");
            }
        }
        AVFormatParameters fp = null;
        if (frameRate > 0 || bpp > 0 || imageWidth > 0 || imageHeight > 0) {
            fp = new AVFormatParameters();
            fp.time_base(av_d2q(1/frameRate, 1001000));
            fp.sample_rate(bpp);
            fp.channels(imageMode == ImageMode.COLOR ? 3 : 1);
            fp.width(imageWidth);
            fp.height(imageHeight);
        }
        if (av_open_input_file(pFormatCtx, filename, f, 0, fp) != 0) {
            throw new Exception("Could not open file \"" + filename + "\".");
        }

        // Retrieve stream information
        if (av_find_stream_info(pFormatCtx) < 0) {
            throw new Exception("Could not find stream information.");
        }

        // Dump information about file onto standard error
        dump_format(pFormatCtx, 0, filename, 0);

        // Find the first video and stream
        videoStream = audioStream = -1;
        int nb_streams = pFormatCtx.nb_streams();
        for (int i = 0; i < nb_streams; i++) {
            AVStream pStream = pFormatCtx.streams(i);
            // Get a pointer to the codec context for the video or audio stream
            AVCodecContext pCodecCtx = pStream.codec();
            if (videoStream < 0 && pCodecCtx.codec_type() == CODEC_TYPE_VIDEO) {
                videoStream = i;
                pVideoStream = pStream;
                pVideoCodecCtx = pCodecCtx;
            } else if (audioStream < 0 && pCodecCtx.codec_type() == CODEC_TYPE_AUDIO) {
                audioStream = i;
                pAudioStream = pStream;
                pAudioCodecCtx = pCodecCtx;
            }
        }
        if (videoStream == -1) {
            throw new Exception("Did not find a video stream.");
        }

        // Find the decoder for the video stream
        pVideoCodec = avcodec_find_decoder(pVideoCodecCtx.codec_id());
        if (pVideoCodec == null) {
            throw new Exception("Unsupported video format or codec not found: " + pVideoCodecCtx.codec_id() + ".");
        }

        // Open video codec
        if (avcodec_open(pVideoCodecCtx, pVideoCodec) < 0) {
            throw new Exception("Could not open video codec.");
        }

        // Hack to correct wrong frame rates that seem to be generated by some codecs
        if (pVideoCodecCtx.time_base().num() > 1000 && pVideoCodecCtx.time_base().den() == 1) {
            pVideoCodecCtx.time_base().den(1000);
        }

        // Allocate video frame and an AVFrame structure for the RGB image
        pFrame = avcodec_alloc_frame();
        pFrameRGB = avcodec_alloc_frame();
        if (pFrame == null || pFrameRGB == null) {
            throw new Exception("Could not allocate frame.");
        }

        int width  = getImageWidth()  > 0 ? getImageWidth()  : pVideoCodecCtx.width();
        int height = getImageHeight() > 0 ? getImageHeight() : pVideoCodecCtx.height();

        switch (imageMode) {
            case COLOR:
            case GRAY:
                int fmt = pixelFormat;
                if (fmt == PIX_FMT_NONE) {
                    fmt = imageMode == ImageMode.COLOR ? PIX_FMT_BGR24 : PIX_FMT_GRAY8;
                }

                // Determine required buffer size and allocate buffer
                numBytes = avpicture_get_size(fmt, width, height);
                buffer = new BytePointer(av_malloc(numBytes));

                // Assign appropriate parts of buffer to image planes in pFrameRGB
                // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
                // of AVPicture
                avpicture_fill(pFrameRGB, buffer, fmt, width, height);

                // Convert the image into BGR or GRAY format that OpenCV uses
                img_convert_ctx = sws_getContext(
                        pVideoCodecCtx.width(), pVideoCodecCtx.height(), pVideoCodecCtx.pix_fmt(),
                        width, height, fmt, SWS_BILINEAR, null, null, null);
                if (img_convert_ctx == null) {
                    throw new Exception("Cannot initialize the conversion context.");
                }

                return_image = IplImage.createHeader(width, height, IPL_DEPTH_8U, 1);
                break;

            case RAW:
                numBytes = 0;
                buffer = null;
                img_convert_ctx = null;
                return_image = IplImage.createHeader(pVideoCodecCtx.width(), pVideoCodecCtx.height(), IPL_DEPTH_8U, 1);
                break;

            default:
                assert false;
        }

        if (audioStream >= 0) {
            // Find the decoder for the audio stream
            pAudioCodec = avcodec_find_decoder(pAudioCodecCtx.codec_id());
            if (pAudioCodec == null) {
                throw new Exception("Unsupported audio format or codec not found: " + pAudioCodecCtx.codec_id() + ".");
            }

            // Open audio codec
            if (avcodec_open(pAudioCodecCtx, pAudioCodec) < 0) {
                throw new Exception("Could not open audio codec.");
            }

            int bufferSize = AVCODEC_MAX_AUDIO_FRAME_SIZE * 2 * pAudioCodecCtx.channels();
            samplesPointer = new BytePointer(av_malloc(bufferSize));
            samplesBuffer = samplesPointer.capacity(bufferSize).asBuffer();
        }

        faulty_pts = faulty_dts = last_dts_for_fault_detection = last_pts_for_fault_detection = 0;
    }

    public void stop() throws Exception {
        release();
    }

    public void trigger() throws Exception {
        if (pFormatCtx == null || pFormatCtx.isNull()) {
            throw new Exception("Could not trigger: No AVFormatContext. (Has start() been called?)");
        }
        for (int i = 0; i < numBuffers+1; i++) {
            if (av_read_frame(pFormatCtx, packet) < 0) {
                return;
            }
            av_free_packet(packet);
        }
    }

    private void processImage() {
        switch (imageMode) {
            case COLOR:
            case GRAY:
                // Deinterlace Picture
                if (deinterlace) {
                    avpicture_deinterlace(pFrame, pFrame, pVideoCodecCtx.pix_fmt(), pVideoCodecCtx.width(), pVideoCodecCtx.height());
                }

                // Convert the image from its native format to RGB or GRAY
                sws_scale(img_convert_ctx, new PointerPointer(pFrame), pFrame.linesize(), 0,
                        pVideoCodecCtx.height(), new PointerPointer(pFrameRGB), pFrameRGB.linesize());
                return_image.imageData(buffer);
                return_image.widthStep(pFrameRGB.linesize(0));
                break;
            case RAW:
                assert pVideoCodecCtx.width()  == return_image.width() &&
                       pVideoCodecCtx.height() == return_image.height();
                return_image.imageData(pFrame.data(0));
                return_image.widthStep(pFrame.linesize(0));
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
        if (frameGrabbed) {
            frameGrabbed = false;
            if (processImage) {
                processImage();
            }
            frame.image = return_image;
            frame.samples = null;
            return frame;
        }
        if (pFormatCtx == null || pFormatCtx.isNull()) {
            throw new Exception("Could not grab: No AVFormatContext. (Has start() been called?)");
        }
        boolean done = false;
        long pts = 0;
        while (!done) {
            if (av_read_frame(pFormatCtx, packet) < 0) {
                //throw new Exception("Could not read frame");
                return null; // end of file?
            }

            // Is this a packet from the video stream?
            if (packet.stream_index() == videoStream) {
                // Decode video frame
                pVideoCodecCtx.reordered_opaque(packet.pts());
                int len = avcodec_decode_video2(pVideoCodecCtx, pFrame, frameFinished, packet);
                long reordered_opaque = pFrame.reordered_opaque();
                long dts = packet.dts();

                // Did we get a video frame?
                if (frameFinished[0] != 0) {
                    if (dts != AV_NOPTS_VALUE) {
                        faulty_dts += dts <= last_dts_for_fault_detection ? 1 : 0;
                        last_dts_for_fault_detection = dts;
                    }
                    if (reordered_opaque != AV_NOPTS_VALUE) {
                        faulty_pts += reordered_opaque <= last_pts_for_fault_detection ? 1 : 0;
                        last_pts_for_fault_detection = reordered_opaque;
                    }
                    if (processImage && len > 0) {
                        processImage();
                    }
                    done = true;
                    frame.image = return_image;
                    frame.samples = null;
                }

                if (((faulty_pts < faulty_dts) || dts == AV_NOPTS_VALUE) &&
                        reordered_opaque != AV_NOPTS_VALUE) {
                    pts = reordered_opaque;
                } else if (dts != AV_NOPTS_VALUE) {
                    pts = dts;
                } else {
                    pts = 0;
                }
            } else if (doAudio && packet.stream_index() == audioStream) {
                BytePointer p = packet.data();
                // Decode audio frame
                frameFinished[0] = samplesBuffer.clear().capacity();
                while (packet.size() > 0) {
                    int len = avcodec_decode_audio3(pAudioCodecCtx, samplesBuffer.asShortBuffer(), frameFinished, packet);
                    if (len < 0) {
                        packet.data(p);
                        break;
                    }
                    packet.data(packet.data().position(len));
                    packet.size(packet.size() - len);
                }
                if (frameFinished[0] > 0) {
                    samplesBuffer.limit(frameFinished[0]);
                    done = true;
                    frame.image = null;
                    frame.samples = samplesBuffer;
                }
                packet.data(p);
            }

            // Free the packet that was allocated by av_read_frame
            av_free_packet(packet);
        }

        AVRational time_base = pVideoStream.time_base();
        timestamp = 1000000*pts*time_base.num()/time_base.den();
        // best guess, AVCodecContext.frame_number = number of decoded frames...
        frameNumber = (int)(1000000*getFrameRate()/timestamp);
        return frame;
    }
}
