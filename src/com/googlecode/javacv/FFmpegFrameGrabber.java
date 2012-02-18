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
 * Based on avcodec_sample.0.5.0.c
 * http://web.me.com/dhoerl/Home/Tech_Blog/Entries/2009/1/22_Revised_avcodec_sample.c_files/avcodec_sample.0.5.0.c
 * by Martin Böhme, Stephen Dranger, and David Hoerl
 * as well as ffplay.c that came with FFmpeg 0.6.1 Copyright (c) 2003 Fabrice Bellard
 */

package com.googlecode.javacv;

import java.io.File;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.PointerPointer;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.avutil.*;
import static com.googlecode.javacv.cpp.avcodec.*;
import static com.googlecode.javacv.cpp.avformat.*;
import static com.googlecode.javacv.cpp.avdevice.*;
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
        stop();
    }
    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    private String          filename;
    private AVFormatContext pFormatCtx;
    private int             videoStream;
    private AVStream        pStream;
    private AVCodecContext  pCodecCtx;
    private AVCodec         pCodec;
    private AVFrame         pFrame, pFrameRGB;
    private int             numBytes;
    private BytePointer     buffer;
    private SwsContext      img_convert_ctx;
    private AVPacket        packet;
    private int[]           frameFinished;
    private IplImage        return_image = null;

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

    @Override public int getPixelFormat() {
        if (imageMode == ImageMode.COLOR || imageMode == ImageMode.GRAY) {
            if (pixelFormat == PIX_FMT_NONE) {
                return imageMode == ImageMode.COLOR ? PIX_FMT_BGR24 : PIX_FMT_GRAY8;
            } else {
                return pixelFormat;
            }
        } else if (pCodecCtx != null) { // RAW
            return pCodecCtx.pix_fmt();
        } else {
            return super.getPixelFormat();
        }
    }

    @Override public double getFrameRate() {
        if (pStream == null) {
            return super.getFrameRate();
        } else {
            AVRational r = pStream.r_frame_rate();
            return (double)r.num()/r.den();
        }
    }

    @Override public int getFrameNumber() {
        return pCodecCtx == null ? super.getFrameNumber() : pCodecCtx.frame_number();
    }
    @Override public void setFrameNumber(int frameNumber) throws Exception {
        if (pCodecCtx == null || pCodecCtx == null) {
            super.setFrameNumber(frameNumber);
        } else {
            if (avformat_seek_file(pFormatCtx, -1, Long.MIN_VALUE, frameNumber, Long.MAX_VALUE, AVSEEK_FLAG_FRAME) < 0) {
                throw new Exception("Could not seek file to frame number " + frameNumber + ".");
            }
            avcodec_flush_buffers(pCodecCtx);
        }
    }

    @Override public void setTimestamp(long timestamp) throws Exception {
        if (pFormatCtx == null || pCodecCtx == null) {
            super.setTimestamp(timestamp);
        } else {
            timestamp = timestamp * AV_TIME_BASE / 1000000;
            /* add the stream start time */
            if (pFormatCtx.start_time() != AV_NOPTS_VALUE) {
                timestamp += pFormatCtx.start_time();
            }
            if (avformat_seek_file(pFormatCtx, -1, Long.MIN_VALUE, timestamp, Long.MAX_VALUE, 0) < 0) {
                throw new Exception("Could not seek file to timestamp " + timestamp + ".");
            }
            avcodec_flush_buffers(pCodecCtx);
        }
    }

    public void start() throws Exception {
        this.pFormatCtx    = new AVFormatContext(null);
        this.packet        = new AVPacket();
        this.frameFinished = new int[1];

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
            fp.time_base(av_d2q(1/frameRate, FFmpegFrameRecorder.DEFAULT_FRAME_RATE_BASE));
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

        // Find the first video stream
        videoStream = -1;
        int nb_streams = pFormatCtx.nb_streams();
        for (int i = 0; i < nb_streams; i++) {
            pStream = pFormatCtx.streams(i);
            // Get a pointer to the codec context for the video stream
            pCodecCtx = pStream.codec();
            if (pCodecCtx.codec_type() == CODEC_TYPE_VIDEO) {
                videoStream = i;
                break;
            }
        }
        if (videoStream == -1) {
            throw new Exception("Did not find a video stream.");
        }

        // Find the decoder for the video stream
        pCodec = avcodec_find_decoder(pCodecCtx.codec_id());
        if (pCodec == null) {
            throw new Exception("Unsupported codec or codec not found: " + pCodecCtx.codec_id() + ".");
        }

        // Open codec
        if (avcodec_open(pCodecCtx, pCodec) < 0) {
            throw new Exception("Could not open codec.");
        }

        // Allocate video frame
        pFrame = avcodec_alloc_frame();

        // Allocate an AVFrame structure
        pFrameRGB = avcodec_alloc_frame();
        if (pFrameRGB == null) {
            throw new Exception("Could not allocate frame.");
        }

        int width  = getImageWidth()  > 0 ? getImageWidth()  : pCodecCtx.width();
        int height = getImageHeight() > 0 ? getImageHeight() : pCodecCtx.height();

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
                        pCodecCtx.width(), pCodecCtx.height(), pCodecCtx.pix_fmt(),
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
                return_image = IplImage.createHeader(pCodecCtx.width(), pCodecCtx.height(), IPL_DEPTH_8U, 1);
                break;

            default:
                assert false;
        }

        faulty_pts = faulty_dts = last_dts_for_fault_detection = last_pts_for_fault_detection = 0;
    }

    public void stop() throws Exception {
        // Free the RGB image
        if (buffer != null) {
            av_free(buffer);
            buffer = null;
        }
        if (pFrameRGB != null) {
            av_free(pFrameRGB);
            pFrameRGB = null;
        }

        // Free the YUV frame
        if (pFrame != null) {
            av_free(pFrame);
            pFrame = null;
        }

        // Close the codec
        if (pCodecCtx != null) {
            avcodec_close(pCodecCtx);
            pCodecCtx = null;
        }

        // Close the video file
        if (pFormatCtx != null && !pFormatCtx.isNull()) {
            av_close_input_file(pFormatCtx);
            pFormatCtx = null;
        }

        return_image = null;
        timestamp   = 0;
        frameNumber = 0;
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

    public IplImage grab() throws Exception {
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
                pCodecCtx.reordered_opaque(packet.pts());
                int len = avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);

                if (frameFinished[0] != 0) {
                    if (packet.dts() != AV_NOPTS_VALUE) {
                        faulty_dts += packet.dts() <= last_dts_for_fault_detection ? 1 : 0;
                        last_dts_for_fault_detection = packet.dts();
                    }
                    if (pFrame.reordered_opaque() != AV_NOPTS_VALUE) {
                        faulty_pts += pFrame.reordered_opaque() <= last_pts_for_fault_detection ? 1 : 0;
                        last_pts_for_fault_detection = pFrame.reordered_opaque();
                    }
                }

                if (((faulty_pts < faulty_dts) || packet.dts() == AV_NOPTS_VALUE) &&
                        pFrame.reordered_opaque() != AV_NOPTS_VALUE) {
                    pts = pFrame.reordered_opaque();
                } else if (packet.dts() != AV_NOPTS_VALUE) {
                    pts = packet.dts();
                } else {
                    pts = 0;
                }

                // Did we get a video frame?
                if (len > 0 && frameFinished[0] != 0) {
                    switch (imageMode) {
                        case COLOR:
                        case GRAY:
                            // Deinterlace Picture
                            if (deinterlace) {
                                avpicture_deinterlace(pFrame, pFrame, pCodecCtx.pix_fmt(), pCodecCtx.width(), pCodecCtx.height());
                            }

                            // Convert the image from its native format to RGB
                            sws_scale(img_convert_ctx, new PointerPointer(pFrame), pFrame.linesize(), 0,
                                    pCodecCtx.height(), new PointerPointer(pFrameRGB), pFrameRGB.linesize());
                            return_image.imageData(buffer);
                            return_image.widthStep(pFrameRGB.linesize(0));
                            break;
                        case RAW:
                            assert pCodecCtx.width()  == return_image.width() &&
                                   pCodecCtx.height() == return_image.height();
                            return_image.imageData(pFrame.data(0));
                            return_image.widthStep(pFrame.linesize(0));
                            break;
                        default:
                            assert false;
                    }
                    return_image.imageSize(return_image.height() * return_image.widthStep());
                    return_image.nChannels(return_image.widthStep() / return_image.width());

                    done = true;
                }
            }

            // Free the packet that was allocated by av_read_frame
            av_free_packet(packet);
        }

        AVRational time_base = pStream.time_base();
        timestamp = 1000000*pts*time_base.num()/time_base.den();
        return return_image;
    }
}
