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
 * Based on avcodec_sample.0.5.0.c
 * http://web.me.com/dhoerl/Home/Tech_Blog/Entries/2009/1/22_Revised_avcodec_sample.c_files/avcodec_sample.0.5.0.c
 * by Martin Böhme, Stephen Dranger, and David Hoerl
 */

package com.googlecode.javacv;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.io.File;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.avcodec.*;
import static com.googlecode.javacv.jna.avdevice.*;
import static com.googlecode.javacv.jna.avformat.*;
import static com.googlecode.javacv.jna.avutil.*;
import static com.googlecode.javacv.jna.swscale.*;

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
                String avutil   = com.googlecode.javacv.jna.avutil.libname;
                String avcodec  = com.googlecode.javacv.jna.avcodec.libname;
                String avformat = com.googlecode.javacv.jna.avformat.libname;
                String avdevice = com.googlecode.javacv.jna.avdevice.libname;
                String swscale  = com.googlecode.javacv.jna.swscale.libname;
            } catch (Throwable t) {
                if (t instanceof Exception) {
                    throw loadingException = (Exception)t;
                } else {
                    throw loadingException = new Exception(t);
                }
            }
        }
    }

    public FFmpegFrameGrabber(File file) {
        this(file.getAbsolutePath(), null);
    }
    public FFmpegFrameGrabber(String filename) {
        this(filename, null);
    }
    public FFmpegFrameGrabber(String filename, String inputFormatName) {
        // Register all formats and codecs
        avcodec_init();
        avcodec_register_all();
        avdevice_register_all();
        av_register_all();

        this.filename = filename;
        this.inputFormatName = inputFormatName;
    }
    public void release() throws Exception {
        stop();
    }
    @Override protected void finalize() {
        try {
            release();
        } catch (Exception ex) { }
    }

    private String filename = null, inputFormatName = null;
    private AVFormatContext pFormatCtx;
    private int             videoStream;
    private AVStream        pStream;
    private AVCodecContext  pCodecCtx;
    private AVCodec         pCodec;
    private AVFrame         pFrame;
    private AVFrame         pFrameRGB;
    private SwsContext      img_convert_ctx;
    private AVPacket        packet = new AVPacket();
    private IntByReference  frameFinished = new IntByReference();
    private int             numBytes;
    private Pointer         buffer;
    private IplImage return_image = null;

    @Override public double getGamma() {
        // default to a gamma of 2.2 for cheap Webcams, DV cameras, etc.
        if (gamma == 0.0) {
            return 2.2;
        } else {
            return gamma;
        }
    }

    public void start() throws Exception {
        // Open video file
        AVFormatContext.PointerByReference p = new AVFormatContext.PointerByReference();
        AVInputFormat f = null;
        if (inputFormatName != null) {
            f = av_find_input_format(inputFormatName);
            if (f == null) {
                throw new Exception("Could not find input format.");
            }
        }
        AVFormatParameters fp = null;
        if (frameRate > 0 || bpp > 0 || imageWidth > 0 || imageHeight > 0) {
            fp = new AVFormatParameters();
            fp.time_base = av_d2q(1/frameRate, FFmpegFrameRecorder.DEFAULT_FRAME_RATE_BASE);
            fp.sample_rate = bpp;
            fp.channels = colorMode == ColorMode.BGR ? 3 : 1;
            fp.width = imageWidth;
            fp.height = imageHeight;
        }
        if (av_open_input_file(p, filename, f, 0, fp) != 0) {
            throw new Exception("Could not open file.");
        }
        pFormatCtx = p.getStructure();
        pFormatCtx.setAutoSynch(false);

        // Retrieve stream information
        if (av_find_stream_info(pFormatCtx) < 0) {
           throw new Exception("Could not find stream information.");
        }

        // Dump information about file onto standard error
        dump_format(pFormatCtx, 0, filename, 0);

        // Find the first video stream
        videoStream = -1;
        for (int i = 0; i < pFormatCtx.nb_streams; i++) {
            pStream = pFormatCtx.streams[i];
            pStream.setAutoSynch(false);
            // Get a pointer to the codec context for the video stream
            pCodecCtx = pStream.codec;
            pCodecCtx.setAutoSynch(false);
            pCodecCtx.readField("codec_type");
            if (pCodecCtx.codec_type == CODEC_TYPE_VIDEO) {
                videoStream = i;
                break;
            }
        }
        if (videoStream == -1) {
            throw new Exception("Did not find a video stream.");
        }

        // Find the decoder for the video stream
        pCodecCtx.readField("codec_id");
        pCodec = avcodec_find_decoder(pCodecCtx.codec_id);
        if (pCodec == null) {
            throw new Exception("Unsupported codec or codec not found: " + pCodecCtx.codec_id + ".");
        }
        pCodec.setAutoSynch(false);
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

        pCodecCtx.readField("width");
        pCodecCtx.readField("height");
        pCodecCtx.readField("pix_fmt");
        int width  = getImageWidth()  > 0 ? getImageWidth()  : pCodecCtx.width;
        int height = getImageHeight() > 0 ? getImageHeight() : pCodecCtx.height;

        switch (colorMode) {
            case BGR:
                // Determine required buffer size and allocate buffer
                numBytes = avpicture_get_size(PIX_FMT_BGR24, width, height);
                buffer = av_malloc(numBytes);

                // Assign appropriate parts of buffer to image planes in pFrameRGB
                // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
                // of AVPicture
                avpicture_fill(pFrameRGB, buffer, PIX_FMT_BGR24, width, height);

                // Convert the image into YUV format that SDL uses
                img_convert_ctx = sws_getContext(pCodecCtx.width, pCodecCtx.height, pCodecCtx.pix_fmt,
                        width, height, PIX_FMT_BGR24, SWS_BILINEAR, Pointer.NULL, Pointer.NULL, Pointer.NULL);
                if (img_convert_ctx == null) {
                    throw new Exception("Cannot initialize the conversion context.");
                }

                return_image = IplImage.createHeader(width, height, IPL_DEPTH_8U, 3);
                break;

            case GRAY:
                numBytes = avpicture_get_size(PIX_FMT_GRAY8, width, height);
                buffer = av_malloc(numBytes);
                avpicture_fill(pFrameRGB, buffer, PIX_FMT_GRAY8, width, height);
                img_convert_ctx = sws_getContext(pCodecCtx.width, pCodecCtx.height, pCodecCtx.pix_fmt,
                        width, height, PIX_FMT_GRAY8, SWS_BILINEAR, Pointer.NULL, Pointer.NULL, Pointer.NULL);
                if (img_convert_ctx == null) {
                    throw new Exception("Cannot initialize the conversion context.");
                }
                return_image = IplImage.createHeader(width, height, IPL_DEPTH_8U, 1);
                break;

            case RAW:
                numBytes = 0;
                buffer = null;
                img_convert_ctx = null;
                return_image = IplImage.createHeader(pCodecCtx.width, pCodecCtx.height, IPL_DEPTH_8U, 1);
                break;

            default:
                assert(false);
        }
    }

    public void stop() throws Exception {
        // Free the RGB image
        if (buffer != null) {
            av_free(buffer);
            buffer = null;
        }
        if (pFrameRGB != null) {
            av_free(pFrameRGB.getPointer());
            pFrameRGB = null;
        }

        // Free the YUV frame
        if (pFrame != null) {
            av_free(pFrame.getPointer());
            pFrame = null;
        }

        // Close the codec
        if (pCodecCtx != null) {
            avcodec_close(pCodecCtx);
            pCodecCtx = null;
        }

        // Close the video file
        if (pFormatCtx != null) {
            av_close_input_file(pFormatCtx);
            pFormatCtx = null;
        }

        if (return_image != null) {
            return_image.release();
            return_image = null;
        }
    }

    public void trigger() throws Exception {
        if (pFormatCtx == null) {
            throw new Exception("Could not trigger: No AVFormatContext (not started?)");
        }
        for (int i = 0; i < triggerFlushSize; i++) {
            if (av_read_frame(pFormatCtx, packet) < 0) {
                return;
            }
            av_free_packet(packet);
        }
    }

    public IplImage grab() throws Exception {
        if (pFormatCtx == null) {
            throw new Exception("Could not grab: No AVFormatContext (not started?)");
        }
        boolean done = false;
        long pts = 0;
        while (!done) {
            if (av_read_frame(pFormatCtx, packet) < 0) {
                //throw new Exception("Could not read frame");
                return null; // end of file?
            }

            // Is this a packet from the video stream?
            if (packet.stream_index == videoStream) {
                // Decode video frame
                int len = avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);

                if (packet.dts == AV_NOPTS_VALUE && pFrame.opaque != null &&
                        pFrame.opaque.getLong(0) != AV_NOPTS_VALUE) {
                    pts = pFrame.opaque.getLong(0);
                } else if (packet.dts != AV_NOPTS_VALUE) {
                    pts = packet.dts;
                } else {
                    pts = 0;
                }
                pts = 1000*pts*pStream.time_base.num/pStream.time_base.den;

                // Did we get a video frame?
                if (len > 0 && frameFinished.getValue() != 0) {
                    switch (colorMode) {
                        case BGR:
                        case GRAY:
                            // Convert the image from its native format to RGB
                            Pointer framePtr = pFrame.getPointer();
                            Pointer frameRGBPtr = pFrameRGB.getPointer();
                            sws_scale(img_convert_ctx, framePtr, framePtr.share(Pointer.SIZE*4), 0,
                                    pCodecCtx.height, frameRGBPtr, frameRGBPtr.share(Pointer.SIZE*4));
                            return_image.imageData = pFrameRGB.data0;
                            return_image.widthStep = pFrameRGB.linesize0;
                            break;
                        case RAW:
                            assert (pCodecCtx.width  == return_image.width &&
                                    pCodecCtx.height == return_image.height);
                            return_image.imageData = pFrame.data0;
                            return_image.widthStep = pFrame.linesize0;
                            break;
                        default:
                            assert (false);
                    }
                    return_image.imageSize = return_image.height * return_image.widthStep;

                    done = true;
                }
            }

            // Free the packet that was allocated by av_read_frame
            av_free_packet(packet);
        }

        return_image.setTimestamp(pts);
        return return_image;
    }
}
