/*
 * Copyright (C) 2010,2011 Samuel Audet
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
 * This file was derived from avformat.h and avio.h include files from
 * FFmpeg 0.6.1, which are covered by the following copyright notice:
 *
 * copyright (c) 2001 Fabrice Bellard
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.googlecode.javacv.cpp;

import java.nio.ByteBuffer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.LongPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;
import static com.googlecode.javacv.cpp.avcodec.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude="avformat.h",
        includepath=genericIncludepath, linkpath=genericLinkpath, link={"avformat", "avcodec", "avutil"}),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload="avformat-52"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avformat {
    static { load(avcodec.class); load(); }

    public static final int LIBAVFORMAT_VERSION_MAJOR = 52;
    public static final int LIBAVFORMAT_VERSION_MINOR = 64;
    public static final int LIBAVFORMAT_VERSION_MICRO =  2;

    public static final int    LIBAVFORMAT_VERSION_INT = AV_VERSION_INT(LIBAVFORMAT_VERSION_MAJOR,
                                                                        LIBAVFORMAT_VERSION_MINOR,
                                                                        LIBAVFORMAT_VERSION_MICRO);
    public static final String LIBAVFORMAT_VERSION     = AV_VERSION(LIBAVFORMAT_VERSION_MAJOR,
                                                                    LIBAVFORMAT_VERSION_MINOR,
                                                                    LIBAVFORMAT_VERSION_MICRO);
    public static final int    LIBAVFORMAT_BUILD       = LIBAVFORMAT_VERSION_INT;

    public static final String LIBAVFORMAT_IDENT       = "Lavf" + LIBAVFORMAT_VERSION;


    public static native int avformat_version();
    public static native String avformat_configuration();
    public static native String avformat_license();


    //#include "avio.h"
    public static class URLContext extends Pointer {
        static { load(); }
        public URLContext() { allocate(); }
        public URLContext(int size) { allocateArray(size); }
        public URLContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public URLContext position(int position) {
            return (URLContext)super.position(position);
        }

//#if LIBAVFORMAT_VERSION_MAJOR >= 53
//        public native @Const AVClass av_class(); public native URLContext av_class(AVClass av_class);
//#endif
        public native URLProtocol prot();     public native URLContext prot(URLProtocol prot);
        public native int flags();            public native URLContext flags(int flags);
        public native int is_streamed();      public native URLContext is_streamed(int is_streamed);
        public native int max_packet_size();  public native URLContext max_packet_size(int max_packet_size);
        public native Pointer priv_data();    public native URLContext priv_data(Pointer priv_data);
        @Cast("char*")
        public native BytePointer filename(); public native URLContext filename(BytePointer filename);
    }

    public static class URLPollEntry extends Pointer {
        static { load(); }
        public URLPollEntry() { allocate(); }
        public URLPollEntry(int size) { allocateArray(size); }
        public URLPollEntry(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public URLPollEntry position(int position) {
            return (URLPollEntry)super.position(position);
        }

        public native URLContext handle(); public native URLPollEntry handle(URLContext handle);
        public native int events();        public native URLPollEntry events(int events);
        public native int revents();       public native URLPollEntry revents(int revents);
    }

    public static final int
            URL_RDONLY = 0,
            URL_WRONLY = 1,
            URL_RDWR   = 2;

    public static class URLInterruptCB extends FunctionPointer {
        static { load(); }
        public    URLInterruptCB(Pointer p) { super(p); }
        protected URLInterruptCB() { allocate(); }
        protected final native void allocate();
        public native int call();
    }

    public static native int url_open_protocol(@ByPtrPtr URLContext puc, URLProtocol up,
            String url, int flags);
    public static native int url_open(@ByPtrPtr URLContext h, String url, int flags);
    public static native int url_read(URLContext h, @Cast("unsigned char*") byte[]      buf, int size);
    public static native int url_read(URLContext h, @Cast("unsigned char*") ByteBuffer  buf, int size);
    public static native int url_read(URLContext h, @Cast("unsigned char*") BytePointer buf, int size);
    public static native int url_read_complete(URLContext h, @Cast("unsigned char*") byte[]      buf, int size);
    public static native int url_read_complete(URLContext h, @Cast("unsigned char*") ByteBuffer  buf, int size);
    public static native int url_read_complete(URLContext h, @Cast("unsigned char*") BytePointer buf, int size);
    public static native int url_write(URLContext h, @Cast("unsigned char*") byte[]      buf, int size);
    public static native int url_write(URLContext h, @Cast("unsigned char*") ByteBuffer  buf, int size);
    public static native int url_write(URLContext h, @Cast("unsigned char*") BytePointer buf, int size);
    public static native long url_seek(URLContext h, long pos, int whence);
    public static native int url_close(URLContext h);
    public static native int url_exist(String url);

    public static native long url_filesize(URLContext h);
    public static native int url_get_file_handle(URLContext h);
    public static native int url_get_max_packet_size(URLContext h);
    public static native void url_get_filename(URLContext h, @Cast("char*") byte[] buf, int buf_size);
    public static native void url_set_interrupt_cb(URLInterruptCB interrupt_cb);
/* not implemented */
//    public static native int url_poll(URLPollEntry poll_table, int n, int timeout);

    public static native int av_url_read_pause(URLContext h, int pause);
    public static native long av_url_read_seek(URLContext h, int stream_index,
            long timestamp, int flags);

    public static final int
            AVSEEK_SIZE = 0x10000,

            AVSEEK_FORCE = 0x20000;

    public static class URLProtocol extends Pointer {
        static { load(); }
        public URLProtocol() { allocate(); }
        public URLProtocol(int size) { allocateArray(size); }
        public URLProtocol(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public URLProtocol position(int position) {
            return (URLProtocol)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer name();              public native URLProtocol name(BytePointer name);

        public static class Url_open extends FunctionPointer {
            static { load(); }
            public    Url_open(Pointer p) { super(p); }
            protected Url_open() { allocate(); }
            protected final native void allocate();
            public native int call(URLContext h, String url, int flags);
        }
        public native Url_open url_open();             public native URLProtocol url_open(Url_open url_open);

        public static class Url_read extends FunctionPointer {
            static { load(); }
            public    Url_read(Pointer p) { super(p); }
            protected Url_read() { allocate(); }
            protected final native void allocate();
            public native int call(URLContext h, @Cast("unsigned char*") BytePointer buf, int size);
        }
        public native Url_read url_read();             public native URLProtocol url_read(Url_read url_read);

        public static class Url_write extends FunctionPointer {
            static { load(); }
            public    Url_write(Pointer p) { super(p); }
            protected Url_write() { allocate(); }
            protected final native void allocate();
            public native int call(URLContext h, @Cast("URL_WRITE_BUF_TYPE") BytePointer buf, int size);
        }
        public native Url_write url_write();           public native URLProtocol url_write(Url_write url_write);

        public static class Url_seek extends FunctionPointer {
            static { load(); }
            public    Url_seek(Pointer p) { super(p); }
            protected Url_seek() { allocate(); }
            protected final native void allocate();
            public native long call(URLContext h, long pos, int whence);
        }
        public native Url_seek url_seek();             public native URLProtocol url_seek(Url_seek url_seek);

        public static class Url_close extends FunctionPointer {
            static { load(); }
            public    Url_close(Pointer p) { super(p); }
            protected Url_close() { allocate(); }
            protected final native void allocate();
            public native int call(URLContext h);
        }
        public native Url_close url_close();           public native URLProtocol url_close(Url_close url_close);

        public native URLProtocol next();              public native URLProtocol next(URLProtocol next);

        public static class Url_read_pause extends FunctionPointer {
            static { load(); }
            public    Url_read_pause(Pointer p) { super(p); }
            protected Url_read_pause() { allocate(); }
            protected final native void allocate();
            public native int call(URLContext h, int pause);
        }
        public native Url_read_pause url_read_pause(); public native URLProtocol url_read_pause(Url_read_pause url_read_pause);

        public static class Url_read_seek extends FunctionPointer {
            static { load(); }
            public    Url_read_seek(Pointer p) { super(p); }
            protected Url_read_seek() { allocate(); }
            protected final native void allocate();
            public native long call(URLContext h, int stream_index, long timestamp, int flags);
        }
        public native Url_read_seek url_read_seek();   public native URLProtocol url_read_seek(Url_read_seek url_read_seek);

        public static class Url_get_file_handle extends FunctionPointer {
            static { load(); }
            public    Url_get_file_handle(Pointer p) { super(p); }
            protected Url_get_file_handle() { allocate(); }
            protected final native void allocate();
            public native int call(URLContext h);
        }
        public native Url_get_file_handle url_get_file_handle();
        public native URLProtocol url_get_file_handle(Url_get_file_handle url_get_file_handle);
    }

//#if LIBAVFORMAT_VERSION_MAJOR < 53
    public static native URLProtocol first_protocol();
    public static native void first_protocol(URLProtocol first_protocol);
//#endif

    public static native URLInterruptCB url_interrupt_cb();
    public static native void url_interrupt_cb(URLInterruptCB url_interrupt_cb);


    public static native URLProtocol av_protocol_next(URLProtocol p);

//#if LIBAVFORMAT_VERSION_MAJOR < 53
//    @Deprecated
//    public static native int register_protocol(URLProtocol protocol);
//#endif
    public static native int av_register_protocol(URLProtocol protocol);

    public static class ByteIOContext extends Pointer {
        static { load(); }
        public ByteIOContext() { allocate(); }
        public ByteIOContext(int size) { allocateArray(size); }
        public ByteIOContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public ByteIOContext position(int position) {
            return (ByteIOContext)super.position(position);
        }

        @Cast("unsigned char*")
        public native BytePointer buffer();         public native ByteIOContext buffer(BytePointer buffer);
        public native int buffer_size();            public native ByteIOContext buffer_size(int buffer_size);
        @Cast("unsigned char*")
        public native BytePointer buf_ptr();        public native ByteIOContext buf_ptr(BytePointer buf_ptr);
        @Cast("unsigned char*")
        public native BytePointer buf_end();        public native ByteIOContext buf_end(BytePointer buf_end);
        public native Pointer opaque();             public native ByteIOContext opaque(Pointer opaque);
        public static class Read_packet extends FunctionPointer {
            static { load(); }
            public native int call(Pointer opaque, @Cast("uint8_t*") BytePointer buf, int buf_size);
        }
        public native Read_packet read_packet();    public native ByteIOContext read_packet(Read_packet read_packet);
        public static class Write_packet extends FunctionPointer {
            static { load(); }
            public native int call(Pointer opaque, @Cast("uint8_t*") BytePointer buf, int buf_size);
        }
        public native Write_packet write_packet();  public native ByteIOContext write_packet(Write_packet write_packet);
        public static class Seek extends FunctionPointer {
            static { load(); }
            public native long call(Pointer opaque, long offset, int whence);
        }
        public native Seek seek();                  public native ByteIOContext seek(Seek seek);
        public native long pos();                   public native ByteIOContext pos(long pos);
        public native int must_flush();             public native ByteIOContext must_flush(int must_flush);
        public native int eof_reached();            public native ByteIOContext eof_reached(int eof_reached);
        public native int write_flag();             public native ByteIOContext write_flag(int write_flag);
        public native int is_streamed();            public native ByteIOContext is_streamed(int is_streamed);
        public native int max_packet_size();        public native ByteIOContext max_packet_size(int max_packet_size);
        @Cast("unsigned long")
        public native long checksum();              public native ByteIOContext checksum(long checksum);
        @Cast("unsigned char*")
        public native BytePointer checksum_ptr();   public native ByteIOContext checksum_ptr(BytePointer checksum_ptr);
        public static class Update_checksum extends FunctionPointer {
            static { load(); }
            public native @Cast("unsigned long") long call(@Cast("unsigned long") long checksum,
                          @Cast("const uint8_t*") BytePointer buf, @Cast("unsigned int") int size);
        }
        public native Update_checksum update_checksum(); 
        public native ByteIOContext update_checksum(Update_checksum update_checksum);
        
        public native int error();                  public native ByteIOContext error(int error);
        public static class Read_pause extends FunctionPointer {
            static { load(); }
            public native int call(Pointer opaque, int pause);
        }
        public native Read_pause read_pause();      public native ByteIOContext read_pause(Read_pause read_pause);
        public static class Read_seek extends FunctionPointer {
            static { load(); }
            public native long call(Pointer opaque, int stream_index, long timestamp, int flags);
        }
        public native Read_seek read_seek();        public native ByteIOContext read_seek(Read_seek read_seek);
    }

    public static native int init_put_byte(ByteIOContext s, @Cast("unsigned char*") BytePointer buffer,
            int buffer_size,int write_flag, Pointer opaque, ByteIOContext.Read_packet read_packet,
            ByteIOContext.Write_packet write_packet, ByteIOContext.Seek seek);
    public static native ByteIOContext av_alloc_put_byte(@Cast("unsigned char*") BytePointer buffer,
            int buffer_size, int write_flag, Pointer opaque, ByteIOContext.Read_packet read_packet,
            ByteIOContext.Write_packet write_packet, ByteIOContext.Seek seek);

    public static native void put_byte(ByteIOContext s, int b);
    public static native void put_buffer(ByteIOContext s, @Cast("unsigned char*") BytePointer buf, int size);
    public static native void put_le64(ByteIOContext s, long val);
    public static native void put_be64(ByteIOContext s, long val);
    public static native void put_le32(ByteIOContext s, int val);
    public static native void put_be32(ByteIOContext s, int val);
    public static native void put_le24(ByteIOContext s, int val);
    public static native void put_be24(ByteIOContext s, int val);
    public static native void put_le16(ByteIOContext s, int val);
    public static native void put_be16(ByteIOContext s, int val);
    public static native void put_tag(ByteIOContext s, String tag);

    public static native void put_strz(ByteIOContext s, String buf);

    public static native long url_fseek(ByteIOContext s, long offset, int whence);
    public static native void url_fskip(ByteIOContext s, long offset);
    public static native long url_ftell(ByteIOContext s);
    public static native long url_fsize(ByteIOContext s);
    public static native int url_feof(ByteIOContext s);

    public static native int url_ferror(ByteIOContext s);

    public static native int av_url_read_fpause(ByteIOContext h, int pause);
    public static native long av_url_read_fseek(ByteIOContext h, int stream_index,
            long timestamp, int flags);

    public static final int URL_EOF = (-1);
    public static native int url_fgetc(ByteIOContext s);
    public static native int url_fprintf(ByteIOContext s, String fmt);
    public static native String url_fgets(ByteIOContext s, @Cast("char*") byte[]      buf, int buf_size);

    public static native void put_flush_packet(ByteIOContext s);

    public static native int get_buffer(ByteIOContext s, @Cast("unsigned char*") byte[]      buf, int size);
    public static native int get_buffer(ByteIOContext s, @Cast("unsigned char*") ByteBuffer  buf, int size);
    public static native int get_buffer(ByteIOContext s, @Cast("unsigned char*") BytePointer buf, int size);
    public static native int get_partial_buffer(ByteIOContext s, @Cast("unsigned char*") byte[]      buf, int size);
    public static native int get_partial_buffer(ByteIOContext s, @Cast("unsigned char*") ByteBuffer  buf, int size);
    public static native int get_partial_buffer(ByteIOContext s, @Cast("unsigned char*") BytePointer buf, int size);

    public static native int get_byte(ByteIOContext s);
    public static native int get_le24(ByteIOContext s);
    public static native int get_le32(ByteIOContext s);
    public static native long get_le64(ByteIOContext s);
    public static native int get_le16(ByteIOContext s);

    public static native String get_strz(ByteIOContext s, @Cast("char*") byte[] buf, int maxlen);
    public static native int get_be16(ByteIOContext s);
    public static native int get_be24(ByteIOContext s);
    public static native int get_be32(ByteIOContext s);
    public static native long get_be64(ByteIOContext s);

//    public static native long ff_get_v(ByteIOContext bc);

    public static boolean url_is_streamed(ByteIOContext s) {
        return s.is_streamed() != 0;
    }

    public static native int url_fdopen(@ByPtrPtr ByteIOContext s, URLContext h);

    public static native int url_setbufsize(ByteIOContext s, int buf_size);
//#if LIBAVFORMAT_VERSION_MAJOR < 53
    public static native int url_resetbuf(ByteIOContext s, int flags);
//#endif
//    public static native int ff_rewind_with_probe_data(ByteIOContext s, @Cast("unsigned char*") byte[]      buf, int buf_size);
//    public static native int ff_rewind_with_probe_data(ByteIOContext s, @Cast("unsigned char*") ByteBuffer  buf, int buf_size);
//    public static native int ff_rewind_with_probe_data(ByteIOContext s, @Cast("unsigned char*") BytePointer buf, int buf_size);

    public static native int url_fopen(@ByPtrPtr ByteIOContext s, String url, int flags);

    public static native int url_fclose(ByteIOContext s);
    public static native URLContext url_fileno(ByteIOContext s);

    public static native int url_fget_max_packet_size(ByteIOContext s);

    public static native int url_open_buf(@ByPtrPtr ByteIOContext s, @Cast("uint8_t*") byte[]      buf, int buf_size, int flags);
    public static native int url_open_buf(@ByPtrPtr ByteIOContext s, @Cast("uint8_t*") ByteBuffer  buf, int buf_size, int flags);
    public static native int url_open_buf(@ByPtrPtr ByteIOContext s, @Cast("uint8_t*") BytePointer buf, int buf_size, int flags);

    public static native int url_close_buf(ByteIOContext s);

    public static native int url_open_dyn_buf(@ByPtrPtr ByteIOContext s);
    public static native int url_open_dyn_packet_buf(@ByPtrPtr ByteIOContext s, int max_packet_size);
    public static native int url_close_dyn_buf(ByteIOContext s, @Cast("uint8_t**") PointerPointer pbuffer);

//    public static native @Cast("unsigned long") long ff_crc04C11DB7_update(
//            @Cast("unsigned long") long checksum, @Cast("uint8_t*") BytePointer buf, int len);
    public static native @Cast("unsigned long") long get_checksum(ByteIOContext s);
    public static native void init_checksum(ByteIOContext s,
            ByteIOContext.Update_checksum update_checksum, @Cast("unsigned long") long checksum);

//    public static native int udp_set_remote_url(URLContext h, String uri);
//    public static native int udp_get_local_port(URLContext h);
//#if (LIBAVFORMAT_VERSION_MAJOR <= 52)
    public static native int udp_get_file_handle(URLContext h);
//#endif


    public static final int
            AV_METADATA_MATCH_CASE      = 1,
            AV_METADATA_IGNORE_SUFFIX   = 2,
            AV_METADATA_DONT_STRDUP_KEY = 4,
            AV_METADATA_DONT_STRDUP_VAL = 8,
            AV_METADATA_DONT_OVERWRITE  = 16;

    public static class AVMetadataTag extends Pointer {
        static { load(); }
        public AVMetadataTag() { allocate(); }
        public AVMetadataTag(int size) { allocateArray(size); }
        public AVMetadataTag(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVMetadataTag position(int position) {
            return (AVMetadataTag)super.position(position);
        }

        @Cast("char*") public native BytePointer key();   public native AVMetadataTag key(BytePointer key);
        @Cast("char*") public native BytePointer value(); public native AVMetadataTag value(BytePointer value);
    }

    @Opaque public static class AVMetadata extends Pointer {
        static { load(); }
        public AVMetadata() { }
        public AVMetadata(Pointer p) { super(p); }
    }
    @Opaque public static class AVMetadataConv extends Pointer {
        static { load(); }
        public AVMetadataConv() { }
        public AVMetadataConv(Pointer p) { super(p); }
    }


    public static native AVMetadataTag av_metadata_get(AVMetadata m, String key, AVMetadataTag prev, int flags);

//#if LIBAVFORMAT_VERSION_MAJOR == 52
//    @Deprecated
//    public static native int av_metadata_set(@ByPtrPtr AVMetadata pm, String key, String value);
//#endif
    public static native int av_metadata_set2(@ByPtrPtr AVMetadata pm, String key, String value, int flags);

    public static native void av_metadata_conv(AVFormatContext ctx, AVMetadataConv d_conv, AVMetadataConv s_conv);
    public static native void av_metadata_free(@ByPtrPtr AVMetadata m);


    public static native int av_get_packet(ByteIOContext s, AVPacket pkt, int size);


    public static class AVFrac extends Pointer {
        static { load(); }
        public AVFrac() { allocate(); }
        public AVFrac(int size) { allocateArray(size); }
        public AVFrac(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFrac position(int position) {
            return (AVFrac)super.position(position);
        }

        public native long val(); public native AVFrac val(long val);
        public native long num(); public native AVFrac num(long num);
        public native long den(); public native AVFrac den(long den);
    }

    @Opaque public static class AVCodecTag extends Pointer {
        static { load(); }
        public AVCodecTag() { }
        public AVCodecTag(Pointer p) { super(p); }
    }

    public static class AVProbeData extends Pointer {
        static { load(); }
        public AVProbeData() { allocate(); }
        public AVProbeData(int size) { allocateArray(size); }
        public AVProbeData(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVProbeData position(int position) {
            return (AVProbeData)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer filename(); public native AVProbeData filename(BytePointer filename);
        @Cast("unsigned char*")
        public native BytePointer buf();      public native AVProbeData buf(BytePointer buf);
        public native int buf_size();         public native AVProbeData buf_size(int buf_size);
    }

    public static final int
            AVPROBE_SCORE_MAX = 100,
            AVPROBE_PADDING_SIZE = 32;

    public static class AVFormatParameters extends Pointer {
        static { load(); }
        public AVFormatParameters() { allocate(); }
        public AVFormatParameters(int size) { allocateArray(size); }
        public AVFormatParameters(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFormatParameters position(int position) {
            return (AVFormatParameters)super.position(position);
        }

        @ByRef
        public native AVRational time_base();    public native AVFormatParameters time_base(AVRational time_base);
        public native int sample_rate();         public native AVFormatParameters sample_rate(int sample_rate);
        public native int channels();            public native AVFormatParameters channels(int channels);
        public native int width();               public native AVFormatParameters width(int width);
        public native int height();              public native AVFormatParameters height(int height);
        @Cast("PixelFormat")
        public native int pix_fmt();             public native AVFormatParameters pix_fmt(int pix_fmt);
        public native int channel();             public native AVFormatParameters channel(int channel);
        @Cast("const char*")
        public native BytePointer standard();    public native AVFormatParameters standard(BytePointer standard);

        @NoOffset public native int mpeg2ts_raw();         public native AVFormatParameters mpeg2ts_raw(int mpeg2ts_raw);                //:1
        @NoOffset public native int mpeg2ts_compute_pcr(); public native AVFormatParameters mpeg2ts_compute_pcr(int mpeg2ts_compute_pcr);//:1
        @NoOffset public native int initial_pause();       public native AVFormatParameters initial_pause(int initial_pause);            //:1
        @NoOffset public native int prealloced_context();  public native AVFormatParameters prealloced_context(int prealloced_context);  //:1

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        @Cast("CodecID")
        public native int video_codec_id();      public native AVFormatParameters video_codec_id(int video_codec_id);
        @Cast("CodecID")
        public native int audio_codec_id();      public native AVFormatParameters audio_codec_id(int audio_codec_id);
//#endif
    }

    public static final int
            AVFMT_NOFILE        = 0x0001,
            AVFMT_NEEDNUMBER    = 0x0002,
            AVFMT_SHOW_IDS      = 0x0008,
            AVFMT_RAWPICTURE    = 0x0020,
            AVFMT_GLOBALHEADER  = 0x0040,
            AVFMT_NOTIMESTAMPS  = 0x0080,
            AVFMT_GENERIC_INDEX = 0x0100,
            AVFMT_TS_DISCONT    = 0x0200,
            AVFMT_VARIABLE_FPS  = 0x0400,
            AVFMT_NODIMENSIONS  = 0x0800;

    public static class AVOutputFormat extends Pointer {
        static { load(); }
        public AVOutputFormat() { allocate(); }
        public AVOutputFormat(int size) { allocateArray(size); }
        public AVOutputFormat(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVOutputFormat position(int position) {
            return (AVOutputFormat)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer name();             public native AVOutputFormat name(BytePointer name);
        @Cast("const char*")
        public native BytePointer long_name();        public native AVOutputFormat long_name(BytePointer long_name);
        @Cast("const char*")
        public native BytePointer mime_type();        public native AVOutputFormat mime_type(BytePointer mime_type);
        @Cast("const char*")
        public native BytePointer extensions();       public native AVOutputFormat extensions(BytePointer extensions);
        public native int priv_data_size();           public native AVOutputFormat priv_data_size(int priv_data_size);
        @Cast("CodecID")
        public native int audio_codec();              public native AVOutputFormat audio_codec(int audio_codec);
        @Cast("CodecID")
        public native int video_codec();              public native AVOutputFormat video_codec(int video_codec);

        public static class Write_header extends FunctionPointer {
            static { load(); }
            public    Write_header(Pointer p) { super(p); }
            protected Write_header() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c);
        }
        public native Write_header write_header();    public native AVOutputFormat write_header(Write_header write_header);

        public static class Write_packet extends FunctionPointer {
            static { load(); }
            public    Write_packet(Pointer p) { super(p); }
            protected Write_packet() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c, AVPacket pkt);
        }
        public native Write_packet write_packet();    public native AVOutputFormat write_packet(Write_packet write_packet);

        public static class Write_trailer extends FunctionPointer {
            static { load(); }
            public    Write_trailer(Pointer p) { super(p); }
            protected Write_trailer() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c);
        }
        public native Write_trailer write_trailer();  public native AVOutputFormat write_trailer(Write_trailer write_trailer);

        public native int flags();                    public native AVOutputFormat flags(int flags);

//        public static class Set_parameters extends FunctionPointer {
//            static { load(); }
//            public    Set_parameters(Pointer p) { super(p); }
//            protected Set_parameters() { allocate(); }
//            protected final native void allocate();
//            public native int call(AVFormatContext c, AVFormatParameters p);
//        }
//        public native Set_parameters set_parameters(); public native AVOutputFormat set_parameters(Set_parameters set_parameters);

        public static class Interleave_packet extends FunctionPointer {
            static { load(); }
            public    Interleave_packet(Pointer p) { super(p); }
            protected Interleave_packet() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c, AVPacket out, AVPacket in, int flush);
        }
        public native Interleave_packet interleave_packet(); public native AVOutputFormat interleave_packet(Interleave_packet interleave_packet);

        @Cast("const AVCodecTag * const *")
        public native PointerPointer codec_tag();     public native AVOutputFormat codec_tag(PointerPointer codec_tag);

        @Cast("CodecID")
        public native int subtitle_codec();           public native AVOutputFormat subtitle_codec(int subtitle_codec);

        @Const
        public native AVMetadataConv metadata_conv(); public native AVOutputFormat metadata_conv(AVMetadataConv metadata_conv);

        public native AVOutputFormat next();          public native AVOutputFormat next(AVOutputFormat next);
    }

    public static class AVInputFormat extends Pointer {
        static { load(); }
        public AVInputFormat() { allocate(); }
        public AVInputFormat(int size) { allocateArray(size); }
        public AVInputFormat(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVInputFormat position(int position) {
            return (AVInputFormat)super.position(position);
        }

        @Cast("const char*")
        public native BytePointer name();             public native AVInputFormat name(BytePointer name);
        @Cast("const char*")
        public native BytePointer long_name();        public native AVInputFormat long_name(BytePointer long_name);
        public native int priv_data_size();           public native AVInputFormat priv_data_size(int priv_data_size);
        public static class Read_probe extends FunctionPointer {
            static { load(); }
            public    Read_probe(Pointer p) { super(p); }
            protected Read_probe() { allocate(); }
            protected final native void allocate();
            public native int call(AVProbeData d);
        }
        public native Read_probe read_probe();        public native AVInputFormat read_probe(Read_probe read_probe);

        public static class Read_header extends FunctionPointer {
            static { load(); }
            public    Read_header(Pointer p) { super(p); }
            protected Read_header() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c, AVFormatParameters ap);
        }
        public native Read_header read_header();      public native AVInputFormat read_header(Read_header read_header);

        public static class Read_packet extends FunctionPointer {
            static { load(); }
            public    Read_packet(Pointer p) { super(p); }
            protected Read_packet() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c, AVPacket pkt);
        }
        public native Read_packet read_packet();      public native AVInputFormat read_packet(Read_packet read_packet);

        public static class Read_close extends FunctionPointer {
            static { load(); }
            public    Read_close(Pointer p) { super(p); }
            protected Read_close() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c);
        }
        public native Read_close read_close();        public native AVInputFormat read_close(Read_close read_close);

//#if LIBAVFORMAT_VERSION_MAJOR < 53
        public static class Read_seek extends FunctionPointer {
            static { load(); }
            public    Read_seek(Pointer p) { super(p); }
            protected Read_seek() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c, int stream_index, long timestamp, int flags);
        }
        public native Read_seek read_seek();          public native AVInputFormat read_seek(Read_seek read_seek);
//#endif
        public static class Read_timestamp extends FunctionPointer {
            static { load(); }
            public    Read_timestamp(Pointer p) { super(p); }
            protected Read_timestamp() { allocate(); }
            protected final native void allocate();
            public native long call(AVFormatContext s, int stream_index, LongPointer pos, long pos_limit);
        }
        public native Read_timestamp read_timestamp();public native AVInputFormat read_timestamp(Read_timestamp read_timestamp);

        public native int flags();                    public native AVInputFormat flags(int flags);
        @Cast("const char*")
        public native BytePointer extensions();       public native AVInputFormat extensions(BytePointer extensions);
        public native int value();                    public native AVInputFormat value(int value);

        public static class Read_play extends FunctionPointer {
            static { load(); }
            public    Read_play(Pointer p) { super(p); }
            protected Read_play() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c);
        }
        public native Read_play read_play();          public native AVInputFormat read_play(Read_play read_play);

        public static class Read_pause extends FunctionPointer {
            static { load(); }
            public    Read_pause(Pointer p) { super(p); }
            protected Read_pause() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext c);
        }
        public native Read_pause read_pause();        public native AVInputFormat read_pause(Read_pause read_pause);

        @Cast("const AVCodecTag * const *")
        public native PointerPointer codec_tag();     public native AVInputFormat codec_tag(PointerPointer codec_tag);

        public static class Read_seek2 extends FunctionPointer {
            static { load(); }
            public    Read_seek2(Pointer p) { super(p); }
            protected Read_seek2() { allocate(); }
            protected final native void allocate();
            public native int call(AVFormatContext s, int stream_index, long min_ts, long ts, long max_ts, int flags);
        }
        public native Read_seek2 read_seek2();        public native AVInputFormat read_seek2(Read_seek2 read_seek2);

        @Const
        public native AVMetadataConv metadata_conv(); public native AVInputFormat metadata_conv(AVMetadataConv metadata_conv);

        public native AVInputFormat next();           public native AVInputFormat next(AVInputFormat next);
    }

    //enum AVStreamParseType {
    public static final int
            AVSTREAM_PARSE_NONE       = 0,
            AVSTREAM_PARSE_FULL       = 1,
            AVSTREAM_PARSE_HEADERS    = 2,
            AVSTREAM_PARSE_TIMESTAMPS = 3;

    public static class AVIndexEntry extends Pointer {
        static { load(); }
        public AVIndexEntry() { allocate(); }
        public AVIndexEntry(int size) { allocateArray(size); }
        public AVIndexEntry(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVIndexEntry position(int position) {
            return (AVIndexEntry)super.position(position);
        }

        public native long pos();         public native AVIndexEntry pos(long pos);
        public native long timestamp();   public native AVIndexEntry timestamp(long timestamp);
        public static final int AVINDEX_KEYFRAME = 0x0001;

        @NoOffset public native int flags();        public native AVIndexEntry flags(int flags);//:2
        @NoOffset public native int size();         public native AVIndexEntry size(int size);  //:30

        public native int min_distance(); public native AVIndexEntry min_distance(int min_distance);
    }

    public static final int
            AV_DISPOSITION_DEFAULT  = 0x0001,
            AV_DISPOSITION_DUB      = 0x0002,
            AV_DISPOSITION_ORIGINAL = 0x0004,
            AV_DISPOSITION_COMMENT  = 0x0008,
            AV_DISPOSITION_LYRICS   = 0x0010,
            AV_DISPOSITION_KARAOKE  = 0x0020;

    public static class AVStream extends Pointer {
        static { load(); }
        public AVStream() { allocate(); }
        public AVStream(int size) { allocateArray(size); }
        public AVStream(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVStream position(int position) {
            return (AVStream)super.position(position);
        }

        public native int index();                          public native AVStream index(int index);
        public native int id();                             public native AVStream id(int id);
        public native AVCodecContext codec();               public native AVStream codec(AVCodecContext codec);
        @ByRef
        public native AVRational r_frame_rate();            public native AVStream r_frame_rate(AVRational r_frame_rate);
        public native Pointer priv_data();                  public native AVStream priv_data(Pointer priv_data);

        public native long first_dts();                     public native AVStream first_dts(long first_dts);
        @ByRef
        public native AVFrac pts();                         public native AVStream pts(AVFrac pts);

        @ByRef
        public native AVRational time_base();               public native AVStream time_base(AVRational time_base);
        public native int pts_wrap_bits();                  public native AVStream pts_wrap_bits(int pts_wrap_bits);
        public native int stream_copy();                    public native AVStream stream_copy(int stream_copy);
        @Cast("AVDiscard")
        public native int discard();                        public native AVStream discard(int discard);
        public native float quality();                      public native AVStream quality(float quality);
        public native long start_time();                    public native AVStream start_time(long start_time);
        public native long duration();                      public native AVStream duration(long duration);

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        // char language[4]
        public native String language();                    public native AVStream language(String language);
//#endif

        @Cast("AVStreamParseType")
        public native int need_parsing();                   public native AVStream need_parsing(int need_parsing);
        public native AVCodecParserContext parser();        public native AVStream parser(AVCodecParserContext parser);

        public native long cur_dts();                       public native AVStream cur_dts(long cur_dts);
        public native int last_IP_duration();               public native AVStream last_IP_duration(int last_IP_duration);
        public native long last_IP_pts();                   public native AVStream last_IP_pts(long last_IP_pts);
        public native AVIndexEntry index_entries();         public native AVStream index_entries(AVIndexEntry index_entries);
        public native int nb_index_entries();               public native AVStream nb_index_entries(int nb_index_entries);
        public native int index_entries_allocated_size();   public native AVStream index_entries_allocated_size(int index_entries_allocated_size);

        public native long nb_frames();                     public native AVStream nb_frames(long nb_frames);

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        // int64_t unused[4+1];
        public native long unused(int i);                   public native AVStream unused(int i, long unused);

        @Cast("char*")
        public native BytePointer filename();               public native AVStream filename(BytePointer filename);
//#endif

        public native int disposition();                    public native AVStream disposition(int disposition);

        @ByRef
        public native AVProbeData probe_data();             public native AVStream probe_data(AVProbeData probe_data);
        public static final int MAX_REORDER_DELAY = 16;
        //  int64_t pts_buffer[MAX_REORDER_DELAY+1];
        public native long pts_buffer(int i);               public native AVStream pts_buffer(int i, long pts_buffer);

        @ByRef
        public native AVRational sample_aspect_ratio();     public native AVStream sample_aspect_ratio(AVRational sample_aspect_ratio);

        public native AVMetadata metadata();                public native AVStream metadata(AVMetadata metadata);

        @Cast("const uint8_t*")
        public native BytePointer cur_ptr();                public native AVStream cur_ptr(BytePointer cur_ptr);
        public native int cur_len();                        public native AVStream cur_len(int cur_len);
        @ByRef
        public native AVPacket cur_pkt();                   public native AVStream cur_pkt(AVPacket cur_pkt);

        public native long reference_dts();                 public native AVStream reference_dts(long reference_dts);

        public static final int MAX_PROBE_PACKETS = 2500;
        public native int probe_packets();                  public native AVStream probe_packets(int probe_packets);

        public native AVPacketList last_in_packet_buffer(); public native AVStream last_in_packet_buffer(AVPacketList last_in_packet_buffer);

        @ByRef
        public native AVRational avg_frame_rate();          public native AVStream avg_frame_rate(AVRational avg_frame_rate);

        public native int codec_info_nb_frames();           public native AVStream codec_info_nb_frames(int codec_info_nb_frames);
    }

    public static final int AV_PROGRAM_RUNNING = 1;

    public static class AVProgram extends Pointer {
        static { load(); }
        public AVProgram() { allocate(); }
        public AVProgram(int size) { allocateArray(size); }
        public AVProgram(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVProgram position(int position) {
            return (AVProgram)super.position(position);
        }

        public native int            id();                public native AVProgram id(int id);
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        @Cast("char*")
        public native BytePointer    provider_name();     public native AVProgram provider_name(BytePointer provider_name);
        @Cast("char*")
        public native BytePointer    name();              public native AVProgram name(BytePointer name);
//#endif
        public native int            flags();             public native AVProgram flags(int flags);
        @Cast("AVDiscard")
        public native int            discard();           public native AVProgram discard(int discard);
        @Cast("unsigned int*")
        public native IntPointer     stream_index();      public native AVProgram stream_index(IntPointer stream_index);
        public native int            nb_stream_indexes(); public native AVProgram nb_stream_indexes(int nb_stream_indexes);
        public native AVMetadata     metadata();          public native AVProgram metadata(AVMetadata metadata);
    }

    public static final int AVFMTCTX_NOHEADER     = 0x0001;

    public static class AVChapter extends Pointer {
        static { load(); }
        public AVChapter() { allocate(); }
        public AVChapter(int size) { allocateArray(size); }
        public AVChapter(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVChapter position(int position) {
            return (AVChapter)super.position(position);
        }

        public native int id();               public native AVChapter id(int id);
        @ByRef
        public native AVRational time_base(); public native AVChapter time_base(AVRational time_base);
        public native long start();           public native AVChapter start(long start);
        public native long end();             public native AVChapter end(long end);
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        @Cast("char*")
        public native BytePointer title();    public native AVChapter title(BytePointer title);
//#endif
        public native AVMetadata metadata();  public native AVChapter metadata(AVMetadata metadata);
    }

//#if LIBAVFORMAT_VERSION_MAJOR < 53
    public static final int MAX_STREAMS = 20;
//#else
//    public static final int MAX_STREAMS = 100;
//#endif

    public static class AVFormatContext extends Pointer {
        static { load(); }
        public AVFormatContext() { allocate(); }
        public AVFormatContext(int size) { allocateArray(size); }
        public AVFormatContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFormatContext position(int position) {
            return (AVFormatContext)super.position(position);
        }

        public native @Const AVClass av_class();        public native AVFormatContext av_class(AVClass av_class);
        public native AVInputFormat iformat();          public native AVFormatContext iformat(AVInputFormat iformat);
        public native AVOutputFormat oformat();         public native AVFormatContext oformat(AVOutputFormat oformat);
        public native Pointer priv_data();              public native AVFormatContext priv_data(Pointer priv_data);
        public native ByteIOContext pb();               public native AVFormatContext pb(ByteIOContext pb);
        public native int nb_streams();                 public native AVFormatContext nb_streams(int nb_streams);
        // AVStream *streams[MAX_STREAMS]
        public native AVStream streams(int i);          public native AVFormatContext streams(int i, AVStream streams);
        // char filename[1024]
        public native String filename();                public native AVFormatContext filename(String filename);
        public native long timestamp();                 public native AVFormatContext timestamp(long timestamp);
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        // char title[512]
        public native String title();                   public native AVFormatContext title(String title);
        // char author[512]
        public native String author();                  public native AVFormatContext author(String author);
        // char copyright[512]
        public native String copyright();               public native AVFormatContext copyright(String copyright);
        // char comment[512]
        public native String comment();                 public native AVFormatContext comment(String comment);
        // char album[512]
        public native String album();                   public native AVFormatContext album(String album);
        public native int year();                       public native AVFormatContext year(int year);
        public native int track();                      public native AVFormatContext track(int track);
        // char genre[32]
        public native String genre();                   public native AVFormatContext genre(String genre);
//#endif

        public native int ctx_flags();                  public native AVFormatContext ctx_flags(int ctx_flags);
        public native AVPacketList packet_buffer();     public native AVFormatContext packet_buffer(AVPacketList packet_buffer);

        public native long start_time();                public native AVFormatContext start_time(long start_time);
        public native long duration();                  public native AVFormatContext duration(long duration);
        public native long file_size();                 public native AVFormatContext file_size(long file_size);
        public native int bit_rate();                   public native AVFormatContext bit_rate(int bit_rate);

        public native AVStream cur_st();                public native AVFormatContext cur_st(AVStream cur_st);
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        @Deprecated @Cast("const uint8_t*")
        public native BytePointer cur_ptr_deprecated(); public native AVFormatContext cur_ptr_deprecated(BytePointer cur_ptr_deprecated);
        @Deprecated
        public native int cur_len_deprecated();         public native AVFormatContext cur_len_deprecated(int cur_len_deprecated);
        @Deprecated @ByRef
        public native AVPacket cur_pkt_deprecated();    public native AVFormatContext cur_pkt_deprecated(AVPacket cur_pkt_deprecated);
//#endif

        public native long data_offset();         public native AVFormatContext data_offset(long data_offset);
        public native int index_built();          public native AVFormatContext index_built(int index_built);

        public native int mux_rate();             public native AVFormatContext mux_rate(int mux_rate);
        public native int packet_size();          public native AVFormatContext packet_size(int packet_size);
        public native int preload();              public native AVFormatContext preload(int preload);
        public native int max_delay();            public native AVFormatContext max_delay(int max_delay);

        public static final int
                AVFMT_NOOUTPUTLOOP = -1,
                AVFMT_INFINITEOUTPUTLOOP = 0;
        public native int loop_output();          public native AVFormatContext loop_output(int loop_output);

        public native int flags();                public native AVFormatContext flags(int flags);
        public static final int
                AVFMT_FLAG_GENPTS      = 0x0001,
                AVFMT_FLAG_IGNIDX      = 0x0002,
                AVFMT_FLAG_NONBLOCK    = 0x0004,
                AVFMT_FLAG_IGNDTS      = 0x0008,
                AVFMT_FLAG_NOFILLIN    = 0x0010,
                AVFMT_FLAG_NOPARSE     = 0x0020,
                AVFMT_FLAG_RTP_HINT    = 0x0040;

        public native int loop_input();           public native AVFormatContext loop_input(int loop_input);
        public native int probesize();            public native AVFormatContext probesize(int probesize);

        public native int max_analyze_duration(); public native AVFormatContext max_analyze_duration(int max_analyze_duration);

        @Cast("const uint8_t*")
        public native BytePointer key();          public native AVFormatContext key(BytePointer key);
        public native int keylen();               public native AVFormatContext keylen(int keylen);

        public native int nb_programs();          public native AVFormatContext nb_programs(int nb_programs);
        @Cast("AVProgram**")
        public native PointerPointer programs();  public native AVFormatContext programs(PointerPointer programs);

        @Cast("CodecID")
        public native int video_codec_id();       public native AVFormatContext video_codec_id(int video_codec_id);
        @Cast("CodecID")
        public native int audio_codec_id();       public native AVFormatContext audio_codec_id(int audio_codec_id);
        @Cast("CodecID")
        public native int subtitle_codec_id();    public native AVFormatContext subtitle_codec_id(int subtitle_codec_id);

        public native int max_index_size();       public native AVFormatContext max_index_size(int max_index_size);
        public native int max_picture_buffer();   public native AVFormatContext max_picture_buffer(int max_picture_buffer);

        public native int nb_chapters();          public native AVFormatContext nb_chapters(int nb_chapters);
        @Cast("AVChapter**")
        public native PointerPointer chapters();  public native AVFormatContext chapters(PointerPointer chapters);

        public native int debug();                public native AVFormatContext debug(int debug);
        public static final int FF_FDEBUG_TS       = 0x0001;

        public native AVPacketList raw_packet_buffer();     public native AVFormatContext raw_packet_buffer(AVPacketList raw_packet_buffer);
        public native AVPacketList raw_packet_buffer_end(); public native AVFormatContext raw_packet_buffer_end(AVPacketList raw_packet_buffer_end);

        public native AVPacketList packet_buffer_end();     public native AVFormatContext packet_buffer_end(AVPacketList packet_buffer_end);

        public native AVMetadata metadata();                public native AVFormatContext metadata(AVMetadata metadata);

        public static final int RAW_PACKET_BUFFER_SIZE = 2500000;
        public native int raw_packet_buffer_remaining_size();
        public native AVFormatContext raw_packet_buffer_remaining_size(int raw_packet_buffer_remaining_size);

        public native long start_time_realtime();           public native AVFormatContext start_time_realtime(long start_time_realtime);
    }

    public static class AVPacketList extends Pointer {
        static { load(); }
        public AVPacketList() { allocate(); }
        public AVPacketList(int size) { allocateArray(size); }
        public AVPacketList(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVPacketList position(int position) {
            return (AVPacketList)super.position(position);
        }

        @ByRef
        public native AVPacket pkt();      public native AVPacketList pkt(AVPacket pkt);
        public native AVPacketList next(); public native AVPacketList next(AVPacketList next);
    }

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
    public static native AVInputFormat  first_iformat(); public static native void first_iformat(AVInputFormat  first_iformat);
    public static native AVOutputFormat first_oformat(); public static native void first_oformat(AVOutputFormat first_oformat);
//#endif

    public static native AVInputFormat  av_iformat_next(AVInputFormat  f);
    public static native AVOutputFormat av_oformat_next(AVOutputFormat f);

    public static native @Cast("CodecID") int av_guess_image2_codec(String filename);

    public static native void av_register_input_format(AVInputFormat format);
    public static native void av_register_output_format(AVOutputFormat format);
//#if LIBAVFORMAT_VERSION_MAJOR < 53
//    @Deprecated
//    public static native AVOutputFormat guess_stream_format(String short_name,
//            String filename, String mime_type);
//    @Deprecated
//    public static native AVOutputFormat guess_format(String short_name,
//            String filename, String mime_type);
//#endif
    public static native AVOutputFormat av_guess_format(String short_name,
            String filename, String mime_type);

    public static native @Cast("CodecID") int av_guess_codec(AVOutputFormat fmt, String short_name,
            String filename, String mime_type, @Cast("AVMediaType") int type);

    public static native void av_hex_dump(@Cast("FILE*") Pointer f, @Cast("uint8_t*") BytePointer buf, int size);
    public static native void av_hex_dump_log(Pointer avcl, int level, @Cast("uint8_t*") BytePointer buf, int size);
    public static native void av_pkt_dump(@Cast("FILE*") Pointer f, AVPacket pkt, int dump_payload);
    public static native void av_pkt_dump_log(Pointer avcl, int level, AVPacket pkt, int dump_payload);

    public static native void av_register_all();

    public static native @Cast("CodecID") int av_codec_get_id(@ByPtrPtr AVCodecTag tags, int tag);
    public static native int av_codec_get_tag(@ByPtrPtr AVCodecTag tags, @Cast("CodecID") int id);

    public static native AVInputFormat av_find_input_format(String short_name);
    public static native AVInputFormat av_probe_input_format(AVProbeData pd, int is_opened);
//    public static native AVInputFormat av_probe_input_format2(AVProbeData pd, int is_opened, int[] score_max);

    public static native int av_open_input_stream(@ByPtrPtr AVFormatContext ic_ptr,
            ByteIOContext pb, String filename, AVInputFormat fmt, AVFormatParameters ap);
    public static native int av_open_input_file(@ByPtrPtr AVFormatContext ic_ptr, String filename,
            AVInputFormat fmt, int buf_size, AVFormatParameters ap);

//#if LIBAVFORMAT_VERSION_MAJOR < 53
//    @Deprecated
//    public static native AVFormatContext av_alloc_format_context();
//#endif
    public static native AVFormatContext avformat_alloc_context();

    public static native int av_find_stream_info(AVFormatContext ic);
    public static native int av_read_packet(AVFormatContext s, AVPacket pkt);
    public static native int av_read_frame(AVFormatContext s, AVPacket pkt);
    public static native int av_seek_frame(AVFormatContext s, int stream_index, long timestamp, int flags);
    public static native int avformat_seek_file(AVFormatContext s, int stream_index, long min_ts, long ts, long max_ts, int flags);

    public static native int av_read_play(AVFormatContext s);
    public static native int av_read_pause(AVFormatContext s);
    public static native void av_close_input_stream(AVFormatContext s);
    public static native void av_close_input_file(AVFormatContext s);

    public static native AVStream av_new_stream(AVFormatContext s, int id);
    public static native AVProgram av_new_program(AVFormatContext s, int id);

//    public static native AVChapter ff_new_chapter(AVFormatContext s, int id, 
//            @ByVal AVRational time_base, long start, long end, String title);

    public static native void av_set_pts_info(AVStream s, int pts_wrap_bits, int pts_num, int pts_den);

    public static final int
            AVSEEK_FLAG_BACKWARD = 1,
            AVSEEK_FLAG_BYTE     = 2,
            AVSEEK_FLAG_ANY      = 4,
            AVSEEK_FLAG_FRAME    = 8;

    public static native int av_find_default_stream_index(AVFormatContext s);
    public static native int av_index_search_timestamp(AVStream st, long timestamp, int flags);
//    public static native void ff_reduce_index(AVFormatContext s, int stream_index);
    public static native int av_add_index_entry(AVStream st, long pos, long timestamp,
            int size, int distance, int flags);
    public static native int av_seek_frame_binary(AVFormatContext s, int stream_index,
            long target_ts, int flags);
    public static native void av_update_cur_dts(AVFormatContext s, AVStream ref_st, long timestamp);
    public static native long av_gen_search(AVFormatContext s, int stream_index,
            long target_ts, long pos_min, long pos_max, long pos_limit,
            long ts_min, long ts_max, int flags, long[] ts_ret,
            AVInputFormat.Read_timestamp read_timestamp);
    public static native int av_set_parameters(AVFormatContext s, AVFormatParameters ap);

    public static native int av_write_header(AVFormatContext s);
    public static native int av_write_frame(AVFormatContext s, AVPacket pkt);
    public static native int av_interleaved_write_frame(AVFormatContext s, AVPacket pkt);
    public static native int av_interleave_packet_per_dts(AVFormatContext s, AVPacket out,
            AVPacket pkt, int flush);
    public static native int av_write_trailer(AVFormatContext s);

    public static native void dump_format(AVFormatContext ic, int index, String url, int is_output);

//#if LIBAVFORMAT_VERSION_MAJOR < 53
//    @Deprecated
//    public static native int parse_image_size(int[] width_ptr,
//            int[] height_ptr, String str);
//    @Deprecated
//    public static native int parse_frame_rate(int[] frame_rate,
//            int[] frame_rate_base, String arg);
//#endif

    public static native long parse_date(String datestr, int duration);
    public static native long av_gettime();


//    public static final int FFM_PACKET_SIZE = 4096;
//    public static native long ffm_read_write_index(int fd);
//    public static native int ffm_write_write_index(int fd, long pos);
//    public static native void ffm_set_write_index(AVFormatContext s, long pos, long file_size);


    public static native int find_info_tag(@Cast("char*") byte[] arg, int arg_size, String tag1, String info);
    public static native int av_get_frame_filename(@Cast("char*") byte[] buf, int buf_size, String path, int number);
    public static native int av_filename_number_test(String filename);
    public static native int avf_sdp_create(@Cast("AVFormatContext**") PointerPointer ac,
            int n_files, @Cast("char*") byte[] buff, int size);
    public static native int av_match_ext(String filename, String extensions);
}
