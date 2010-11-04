/*
 * Copyright (C) 2010 Samuel Audet
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

package com.googlecode.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;

import static com.googlecode.javacv.jna.avutil.*;
import static com.googlecode.javacv.jna.avcodec.*;

/**
 *
 * @author Samuel Audet
 */
public class avformat {
    public static final String[] paths = avcodec.paths;
    public static final String[] libnames = { "avformat", "avformat-52" };
    public static final String libname = Loader.load(paths, libnames);
    public static final NativeLibrary nativeLibrary = NativeLibrary.getInstance(libname);

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
    public static class URLContext extends Structure {
        public URLContext() { }
        public URLContext(Pointer m) { super(m); read(); }

//#if LIBAVFORMAT_VERSION_MAJOR >= 53
        public AVClass.ByReference av_class;
//#endif
        public URLProtocol.ByReference prot;
        public int flags;
        public int is_streamed;
        public int max_packet_size;
        public Pointer priv_data;
        public String filename;

        public static class ByReference extends URLContext implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(URLContext p) {
                setStructure(p);
            }
            public URLContext getStructure() {
                return new URLContext(getValue());
            }
            public void getStructure(URLContext p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(URLContext p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class URLPollEntry extends Structure {
        public URLContext.ByReference handle;
        public int events;
        public int revents;
    }

    public static final int
            URL_RDONLY = 0,
            URL_WRONLY = 1,
            URL_RDWR   = 2;

    public interface URLInterruptCB extends Callback {
        int callback();
    }

    public static native int url_open_protocol(URLContext.PointerByReference puc, URLProtocol up,
            String url, int flags);
    public static native int url_open(URLContext.PointerByReference h, String url, int flags);
    public static native int url_read(URLContext h, byte[] buf, int size);
    public static native int url_read(URLContext h, ByteBuffer buf, int size);
    public static native int url_read(URLContext h, Pointer buf, int size);
    public static native int url_read_complete(URLContext h, byte[] buf, int size);
    public static native int url_read_complete(URLContext h, ByteBuffer buf, int size);
    public static native int url_read_complete(URLContext h, Pointer buf, int size);
    public static native int url_write(URLContext h, byte[] buf, int size);
    public static native int url_write(URLContext h, ByteBuffer buf, int size);
    public static native int url_write(URLContext h, Pointer buf, int size);
    public static native long url_seek(URLContext h, long pos, int whence);
    public static native int url_close(URLContext h);
    public static native int url_exist(String url);

    public static native long url_filesize(URLContext h);
    public static native int url_get_file_handle(URLContext h);
    public static native int url_get_max_packet_size(URLContext h);
    public static native void url_get_filename(URLContext h, byte[] buf, int buf_size);
    public static native void url_get_filename(URLContext h, ByteBuffer buf, int buf_size);
    public static native void url_get_filename(URLContext h, Pointer buf, int buf_size);
    public static native void url_set_interrupt_cb(URLInterruptCB interrupt_cb);
/* not implemented */
//    public static native int url_poll(URLPollEntry poll_table, int n, int timeout);

    public static native int av_url_read_pause(URLContext h, int pause);
    public static native long av_url_read_seek(URLContext h, int stream_index,
            long timestamp, int flags);

    public static final int 
            AVSEEK_SIZE = 0x10000,

            AVSEEK_FORCE = 0x20000;

    public static class URLProtocol extends Structure {
        public URLProtocol() { }
        public URLProtocol(Pointer m) { super(m); read(); }

        public String name;

        public interface Url_open extends Callback {
            int callback(URLContext h, String url, int flags);
        }
        public Url_open url_open;

        public interface Url_read extends Callback {
            int callback(URLContext h, Pointer buf, int size);
        }
        public Url_read url_read;

        public interface Url_write extends Callback {
            int callback(URLContext h, Pointer buf, int size);
        }
        public Url_write url_write;

        public interface Url_seek extends Callback {
            long callback(URLContext h, long pos, int whence);
        }
        public Url_seek url_seek;

        public interface Url_close extends Callback {
            int callback(URLContext h);
        }
        public Url_close url_close;

        public URLProtocol.ByReference next;

        public interface Url_read_pause extends Callback {
            int callback(URLContext h, int pause);
        }
        public Url_read_pause url_read_pause;

        public interface Url_read_seek extends Callback {
            long callback(URLContext h, int stream_index, long timestamp, int flags);
        }
        public Url_read_seek url_read_seek;

        public interface Url_get_file_handle extends Callback {
            int callback(URLContext h);
        }
        public Url_get_file_handle url_get_file_handle;

        public static class ByReference extends URLProtocol implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

//#if LIBAVFORMAT_VERSION_MAJOR < 53
    public static final URLProtocol first_protocol = new URLProtocol(nativeLibrary.
            getGlobalVariableAddress("first_protocol").getPointer(0));
//#endif

    public static final Function /* URLInterruptCB */ url_interrupt_cb = Function.getFunction(nativeLibrary.
            getGlobalVariableAddress("url_interrupt_cb").getPointer(0));


    public static native URLProtocol av_protocol_next(URLProtocol p);

    public static native int av_register_protocol(URLProtocol protocol);

    public static class ByteIOContext extends Structure {
        public ByteIOContext() { }
        public ByteIOContext(Pointer m) { super(m); read(); }

        public Pointer buffer;
        public int buffer_size;
        public Pointer buf_ptr, buf_end;
        public Pointer opaque;
        public interface Read_packet extends Callback {
            int callback(Pointer opaque, Pointer buf, int buf_size);
        }
        public Read_packet read_packet;
        public interface Write_packet extends Callback {
            int callback(Pointer opaque, Pointer buf, int buf_size);
        }
        public Write_packet write_packet;
        public interface Seek extends Callback {
            long callback(Pointer opaque, long offset, int whence);
        }
        public Seek seek;
        public long pos; 
        public int must_flush;
        public int eof_reached;
        public int write_flag;
        public int is_streamed;
        public int max_packet_size;
        public NativeLong checksum;
        public Pointer checksum_ptr;
        public interface Update_checksum extends Callback {
            NativeLong callback(NativeLong checksum, Pointer buf, int size);
        }
        public Update_checksum update_checksum;
        public int error;
        public interface Read_pause extends Callback {
            int callback(Pointer opaque, int pause);
        }
        public Read_pause read_pause;
        public interface Read_seek extends Callback {
            long callback(Pointer opaque, int stream_index, long timestamp, int flags);
        }
        public Read_seek read_seek;

        public static class ByReference extends ByteIOContext implements Structure.ByReference { 
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(ByteIOContext p) {
                setStructure(p);
            }
            public ByteIOContext.ByReference getStructure() {
                return new ByteIOContext.ByReference(getValue());
            }
            public void getStructure(ByteIOContext p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(ByteIOContext p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native int init_put_byte(ByteIOContext s, Pointer buffer, int buffer_size, 
            int write_flag, Pointer opaque, ByteIOContext.Read_packet read_packet,
            ByteIOContext.Write_packet write_packet, ByteIOContext.Seek seek);
    public static native ByteIOContext av_alloc_put_byte(Pointer buffer, int buffer_size, 
            int write_flag, Pointer opaque, ByteIOContext.Read_packet read_packet,
            ByteIOContext.Write_packet write_packet, ByteIOContext.Seek seek);

    public static native void put_byte(ByteIOContext s, int b);
    public static native void put_buffer(ByteIOContext s, Pointer buf, int size);
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
    public interface ComplexCalls extends Library {
        final ComplexCalls I = (ComplexCalls)Native.loadLibrary(libname, ComplexCalls.class);
        int url_fprintf(ByteIOContext s, String fmt, Object ... p);
    }
    public static int url_fprintf(ByteIOContext s, String fmt, Object ... p) {
        return ComplexCalls.I.url_fprintf(s, fmt, p);
    }
    public static native String url_fgets(ByteIOContext s, byte[] buf, int buf_size);
    public static native String url_fgets(ByteIOContext s, ByteBuffer buf, int buf_size);
    public static native String url_fgets(ByteIOContext s, Pointer buf, int buf_size);

    public static native void put_flush_packet(ByteIOContext s);

    public static native int get_buffer(ByteIOContext s, byte[] buf, int size);
    public static native int get_buffer(ByteIOContext s, ByteBuffer buf, int size);
    public static native int get_buffer(ByteIOContext s, Pointer buf, int size);
    public static native int get_partial_buffer(ByteIOContext s, byte[] buf, int size);
    public static native int get_partial_buffer(ByteIOContext s, ByteBuffer buf, int size);
    public static native int get_partial_buffer(ByteIOContext s, Pointer buf, int size);

    public static native int get_byte(ByteIOContext s);
    public static native int get_le24(ByteIOContext s);
    public static native int get_le32(ByteIOContext s);
    public static native long get_le64(ByteIOContext s);
    public static native int get_le16(ByteIOContext s);

    public static native String get_strz(ByteIOContext s, byte[] buf, int maxlen);
    public static native String get_strz(ByteIOContext s, ByteBuffer buf, int maxlen);
    public static native String get_strz(ByteIOContext s, Pointer buf, int maxlen);
    public static native int get_be16(ByteIOContext s);
    public static native int get_be24(ByteIOContext s);
    public static native int get_be32(ByteIOContext s);
    public static native long get_be64(ByteIOContext s);

    public static native long ff_get_v(ByteIOContext bc);

    public static boolean url_is_streamed(ByteIOContext s) {
        return s.is_streamed != 0;
    }

    public static native int url_fdopen(ByteIOContext.PointerByReference s, URLContext h);

    public static native int url_setbufsize(ByteIOContext s, int buf_size);
//#if LIBAVFORMAT_VERSION_MAJOR < 53
    public static native int url_resetbuf(ByteIOContext s, int flags);
//#endif

    public static native int url_fopen(ByteIOContext.PointerByReference s, String url, int flags);

    public static native int url_fclose(ByteIOContext s);
    public static native URLContext url_fileno(ByteIOContext s);

    public static native int url_fget_max_packet_size(ByteIOContext s);

    public static native int url_open_buf(ByteIOContext.PointerByReference s, byte[] buf, int buf_size, int flags);
    public static native int url_open_buf(ByteIOContext.PointerByReference s, ByteBuffer buf, int buf_size, int flags);
    public static native int url_open_buf(ByteIOContext.PointerByReference s, Pointer buf, int buf_size, int flags);

    public static native int url_close_buf(ByteIOContext s);

    public static native int url_open_dyn_buf(ByteIOContext.PointerByReference s);
    public static native int url_open_dyn_packet_buf(ByteIOContext.PointerByReference s, int max_packet_size);
    public static native int url_close_dyn_buf(ByteIOContext s, PointerByReference pbuffer);

    public static native NativeLong ff_crc04C11DB7_update(NativeLong checksum, Pointer buf, int len);
    public static native NativeLong get_checksum(ByteIOContext s);
    public static native void init_checksum(ByteIOContext s, 
            ByteIOContext.Update_checksum update_checksum, NativeLong checksum);

    public static native int udp_set_remote_url(URLContext h, String uri);
    public static native int udp_get_local_port(URLContext h);
//#if (LIBAVFORMAT_VERSION_MAJOR <= 52)
    public static native int udp_get_file_handle(URLContext h);
//#endif


    public static final int 
            AV_METADATA_MATCH_CASE      = 1,
            AV_METADATA_IGNORE_SUFFIX   = 2,
            AV_METADATA_DONT_STRDUP_KEY = 4,
            AV_METADATA_DONT_STRDUP_VAL = 8,
            AV_METADATA_DONT_OVERWRITE  = 16;

    public static class AVMetadataTag extends Structure {
        public String key;
        public String value;
    }

    public static class AVMetadata extends PointerType {
        public AVMetadata() { }
        public AVMetadata(Pointer p) { super(p); }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVMetadata p) {
                setStructure(p);
            }
            public AVMetadata getStructure() {
                return new AVMetadata(getValue());
            }
            public void getStructure(AVMetadata p) {
                p.setPointer(getValue());
            }
            public void setStructure(AVMetadata p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }
    public static class AVMetadataConv extends PointerType { }


    public static native AVMetadataTag av_metadata_get(AVMetadata m, String key, AVMetadataTag prev, int flags);

    public static native int av_metadata_set2(AVMetadata.PointerByReference pm, String key, String value, int flags);

    public static native void av_metadata_conv(AVFormatContext ctx, AVMetadataConv d_conv, AVMetadataConv s_conv);
    public static native void av_metadata_free(AVMetadata.PointerByReference m);


    public static native int av_get_packet(ByteIOContext s, AVPacket pkt, int size);


    public static class AVFrac extends Structure {
        public long val, num, den;
    }

    public static class AVCodecTag extends PointerType {
        public AVCodecTag() { }
        public AVCodecTag(Pointer p) { super(p); }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVCodecTag p) {
                setStructure(p);
            }
            public AVCodecTag getStructure() {
                return new AVCodecTag(getValue());
            }
            public void getStructure(AVCodecTag p) {
                p.setPointer(getValue());
            }
            public void setStructure(AVCodecTag p) {
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class AVProbeData extends Structure {
        public String filename;
        public Pointer buf;
        public int buf_size;
    }

    public static final int 
            AVPROBE_SCORE_MAX = 100,
            AVPROBE_PADDING_SIZE = 32;

    public static class AVFormatParameters extends Structure {
        public AVRational time_base;
        public int sample_rate;
        public int channels;
        public int width;
        public int height;
        public int /* enum PixelFormat */ pix_fmt;
        public int channel;
        public String standard;

        public byte bitfield;
        //public int mpeg2ts_raw:1;
        //public int mpeg2ts_compute_pcr:1;
        //public int initial_pause:1;
        //public int prealloced_context:1;

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        public int /* enum CodecID */ video_codec_id;
        public int /* enum CodecID */ audio_codec_id;
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

    public static class AVOutputFormat extends Structure {
        public AVOutputFormat() { }
        public AVOutputFormat(Pointer m) { super(m); read(); }

        public String name;
        public String long_name;
        public String mime_type;
        public String extensions;
        public int priv_data_size;
        public int /* enum CodecID */ audio_codec;
        public int /* enum CodecID */ video_codec;

        public interface Write_header extends Callback {
            int callback(AVFormatContext c);
        }
        public Write_header write_header;

        public interface Write_packet extends Callback {
            int callback(AVFormatContext c, AVPacket pkt);
        }
        public Write_packet write_packet;

        public interface Write_trailer extends Callback {
            int callback(AVFormatContext c);
        }
        public Write_trailer write_trailer;

        public int flags;

        public interface Set_parameters extends Callback {
            int callback(AVFormatContext c, AVFormatParameters p);
        }
        public Set_parameters set_parameters;

        public interface Interleave_packet extends Callback {
            int callback(AVFormatContext c, AVPacket out, AVPacket in, int flush);
        }
        public Interleave_packet interleave_packet;

        public AVCodecTag.PointerByReference codec_tag;

        public int /* enum CodecID */ subtitle_codec;

        public AVMetadataConv metadata_conv;

        public AVOutputFormat.ByReference next;

        public static class ByReference extends AVOutputFormat implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVInputFormat extends Structure {
        public AVInputFormat() { }
        public AVInputFormat(Pointer m) { super(m); read(); }

        public String name;
        public String long_name;
        public int priv_data_size;
        public interface Read_probe extends Callback {
            int callback(AVProbeData d);
        }
        public Read_probe read_probe;

        public interface Read_header extends Callback {
            int callback(AVFormatContext c, AVFormatParameters ap);
        }
        public Read_header read_header;

        public interface Read_packet extends Callback {
            int callback(AVFormatContext c, AVPacket pkt);
        }
        public Read_packet read_packet;

        public interface Read_close extends Callback {
            int callback(AVFormatContext c);
        }
        public Read_close read_close;

//#if LIBAVFORMAT_VERSION_MAJOR < 53
        public interface Read_seek extends Callback {
            int callback(AVFormatContext c, int stream_index, long timestamp, int flags);
        }
        public Read_seek read_seek;
//#endif
        public interface Read_timestamp extends Callback {
            long callback(AVFormatContext s, int stream_index, LongByReference pos, long pos_limit);
        }
        public Read_timestamp read_timestamp;

        public int flags;
        public String extensions;
        public int value;

        public interface Read_play extends Callback {
            int callback(AVFormatContext c);
        }
        public Read_play read_play;

        public interface Read_pause extends Callback {
            int callback(AVFormatContext c);
        }
        public Read_pause read_pause;

        public AVCodecTag.PointerByReference codec_tag;

        public interface Read_seek2 extends Callback {
            int callback(AVFormatContext s, int stream_index, long min_ts, long ts, long max_ts, int flags);
        }
        public Read_seek2 read_seek2;

        public AVMetadataConv metadata_conv;

        public AVInputFormat.ByReference next;

        public static class ByReference extends AVInputFormat implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    //enum AVStreamParseType {
    public static final int
            AVSTREAM_PARSE_NONE       = 0,
            AVSTREAM_PARSE_FULL       = 1,
            AVSTREAM_PARSE_HEADERS    = 2,
            AVSTREAM_PARSE_TIMESTAMPS = 3;

    public static class AVIndexEntry extends Structure {
        public AVIndexEntry() { }
        public AVIndexEntry(Pointer m) { super(m); }

        public long pos;
        public long timestamp;
        public static final int AVINDEX_KEYFRAME = 0x0001;

        public int bitfield;
        //public int flags:2;
        //public int size:30;

        public int min_distance;

        public static class ByReference extends AVIndexEntry implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static final int 
            AV_DISPOSITION_DEFAULT  = 0x0001,
            AV_DISPOSITION_DUB      = 0x0002,
            AV_DISPOSITION_ORIGINAL = 0x0004,
            AV_DISPOSITION_COMMENT  = 0x0008,
            AV_DISPOSITION_LYRICS   = 0x0010,
            AV_DISPOSITION_KARAOKE  = 0x0020;

    public static class AVStream extends Structure {
        public AVStream() { }
        public AVStream(Pointer m) { useMemory(m); read(); }

        public int index;
        public int id;
        public AVCodecContext.ByReference codec;
        public AVRational r_frame_rate;
        public Pointer priv_data;

        public long first_dts;
        public AVFrac pts;

        public AVRational time_base;
        public int pts_wrap_bits;
        public int stream_copy;
        public int /* enum AVDiscard */ discard;
        public float quality;
        public long start_time;
        public long duration;

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        public byte language0, language1, language2, language3;
//#endif

        public int /* enum AVStreamParseType */ need_parsing;
        public AVCodecParserContext.ByReference parser;

        public long cur_dts;
        public int last_IP_duration;
        public long last_IP_pts;
        public AVIndexEntry.ByReference index_entries;
        public int nb_index_entries;
        public int index_entries_allocated_size;

        public long nb_frames;

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        public long unused0, unused1, unused2, unused3, unused4;

        public String filename;
//#endif

        public int disposition;

        public AVProbeData probe_data;
        public static final int MAX_REORDER_DELAY = 16;
        public long[] pts_buffer = new long[MAX_REORDER_DELAY+1];

        public AVRational sample_aspect_ratio;

        public AVMetadata metadata;

        public Pointer cur_ptr;
        public int cur_len;
        public AVPacket cur_pkt;

        public long reference_dts;

        public static final int MAX_PROBE_PACKETS = 2500;
        public int probe_packets;

        public AVPacketList.ByReference last_in_packet_buffer;

        public AVRational avg_frame_rate;

        public int codec_info_nb_frames;

        public static class ByReference extends AVStream implements Structure.ByReference { 
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static final int AV_PROGRAM_RUNNING = 1;

    public static class AVProgram extends Structure {
        public AVProgram() { }
        public AVProgram(Pointer m) { super(m); read(); }

        public int            id;
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        public String         provider_name;
        public String         name;
//#endif
        public int            flags;
        public int /* enum AVDiscard */ discard;
        public IntByReference stream_index;
        public int            nb_stream_indexes;
        public AVMetadata     metadata;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVProgram p) {
                setStructure(p);
            }
            public AVProgram getStructure() {
                return new AVProgram(getValue());
            }
            public void getStructure(AVProgram p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVProgram p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static final int AVFMTCTX_NOHEADER     = 0x0001;

    public static class AVChapter extends Structure {
        public AVChapter() { }
        public AVChapter(Pointer m) { super(m); read(); }

        public int id;
        public AVRational time_base;
        public long start, end;
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        public String title;
//#endif
        public AVMetadata metadata;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVChapter p) {
                setStructure(p);
            }
            public AVChapter getStructure() {
                return new AVChapter(getValue());
            }
            public void getStructure(AVChapter p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVChapter p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

//#if LIBAVFORMAT_VERSION_MAJOR < 53
    public static final int MAX_STREAMS = 20;
//#else
//    public static final int MAX_STREAMS = 100;
//#endif

    public static class AVFormatContext extends Structure {
        public AVFormatContext() { }
        public AVFormatContext(Pointer m) { useMemory(m); read(); }

        public AVClass.ByReference av_class;
        public AVInputFormat.ByReference iformat;
        public AVOutputFormat.ByReference oformat;
        public Pointer priv_data;
        public ByteIOContext.ByReference pb;
        public int nb_streams;
        public AVStream.ByReference[] streams = new AVStream.ByReference[MAX_STREAMS];
        public byte[] filename = new byte[1024];
        public long timestamp;
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        public byte[] title = new byte[512];
        public byte[] author = new byte[512];
        public byte[] copyright = new byte[512];
        public byte[] comment = new byte[512];
        public byte[] album = new byte[512];
        public int year;
        public int track;
        public byte[] genre = new byte[32];
//#endif

        public int ctx_flags;
        public AVPacketList.ByReference packet_buffer;

        public long start_time;
        public long duration;
        public long file_size;
        public int bit_rate;

        public AVStream.ByReference cur_st;
//#if LIBAVFORMAT_VERSION_INT < (53<<16)
        @Deprecated public Pointer cur_ptr_deprecated;
        @Deprecated public int cur_len_deprecated;
        @Deprecated public AVPacket cur_pkt_deprecated;
//#endif

        public long data_offset;
        public int index_built;

        public int mux_rate;
        public int packet_size;
        public int preload;
        public int max_delay;

        public static final int
                AVFMT_NOOUTPUTLOOP = -1,
                AVFMT_INFINITEOUTPUTLOOP = 0;
        public int loop_output;

        public int flags;
        public static final int
                AVFMT_FLAG_GENPTS      = 0x0001,
                AVFMT_FLAG_IGNIDX      = 0x0002,
                AVFMT_FLAG_NONBLOCK    = 0x0004,
                AVFMT_FLAG_IGNDTS      = 0x0008,
                AVFMT_FLAG_NOFILLIN    = 0x0010,
                AVFMT_FLAG_NOPARSE     = 0x0020,
                AVFMT_FLAG_RTP_HINT    = 0x0040;

        public int loop_input;
        public int probesize;

        public int max_analyze_duration;

        public Pointer key;
        public int keylen;

        public int nb_programs;
        public AVProgram.PointerByReference programs;

        public int /* enum CodecID */ video_codec_id;
        public int /* enum CodecID */ audio_codec_id;
        public int /* enum CodecID */ subtitle_codec_id;

        public int max_index_size;
        public int max_picture_buffer;

        public int nb_chapters;
        public AVChapter.PointerByReference chapters;

        public int debug;
        public static final int FF_FDEBUG_TS       = 0x0001;

        public AVPacketList.ByReference raw_packet_buffer;
        public AVPacketList.ByReference raw_packet_buffer_end;

        public AVPacketList.ByReference packet_buffer_end;

        public AVMetadata metadata;

        public static final int RAW_PACKET_BUFFER_SIZE = 2500000;
        public int raw_packet_buffer_remaining_size;

        public long start_time_realtime;

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVFormatContext p) {
                setStructure(p);
            }
            public AVFormatContext getStructure() {
                return new AVFormatContext(getValue());
            }
            public void getStructure(AVFormatContext p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVFormatContext p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class AVPacketList extends Structure {
        public AVPacketList() { }
        public AVPacketList(Pointer m) { super(m); }

        public AVPacket pkt;
        public AVPacketList.ByReference next;

        public static class ByReference extends AVPacketList implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

//#if LIBAVFORMAT_VERSION_INT < (53<<16)
    public static final AVInputFormat first_iformat = new AVInputFormat(nativeLibrary.
            getGlobalVariableAddress("first_iformat").getPointer(0));
    public static final AVOutputFormat first_oformat = new AVOutputFormat(nativeLibrary.
            getGlobalVariableAddress("first_oformat").getPointer(0));
//#endif

    public static native AVInputFormat  av_iformat_next(AVInputFormat  f);
    public static native AVOutputFormat av_oformat_next(AVOutputFormat f);

    public static native int /* enum CodecID */ av_guess_image2_codec(String filename);

    public static native void av_register_input_format(AVInputFormat format);
    public static native void av_register_output_format(AVOutputFormat format);
    public static native AVOutputFormat.ByReference av_guess_format(String short_name,
            String filename, String mime_type);

    public static native int /* enum CodecID */ av_guess_codec(AVOutputFormat fmt, String short_name,
            String filename, String mime_type, int /* enum AVMediaType */ type);

    public static class FILE extends PointerType { }
    public static native void av_hex_dump(FILE f, Pointer buf, int size);
    public static native void av_hex_dump_log(Pointer avcl, int level, Pointer buf, int size);
    public static native void av_pkt_dump(FILE f, AVPacket pkt, int dump_payload);
    public static native void av_pkt_dump_log(Pointer avcl, int level, AVPacket pkt, int dump_payload);

    public static native void av_register_all();

    public static native int /* enum CodecID */ av_codec_get_id(AVCodecTag.PointerByReference tags, int tag);
    public static native int av_codec_get_tag(AVCodecTag.PointerByReference tags, int /* enum CodecID */ id);

    public static native AVInputFormat av_find_input_format(String short_name);
    public static native AVInputFormat av_probe_input_format(AVProbeData pd, int is_opened);

    public static native int av_open_input_stream(AVFormatContext.PointerByReference ic_ptr,
            ByteIOContext pb, String filename, AVInputFormat fmt, AVFormatParameters ap);
    public static native int av_open_input_file(AVFormatContext.PointerByReference ic_ptr, String filename,
            AVInputFormat fmt, int buf_size, AVFormatParameters ap);

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

    public static native AVChapter ff_new_chapter(AVFormatContext s, int id, AVRational.ByValue time_base,
            long start, long end, String title);

    public static native void av_set_pts_info(AVStream s, int pts_wrap_bits, int pts_num, int pts_den);

    public static final int
            AVSEEK_FLAG_BACKWARD = 1,
            AVSEEK_FLAG_BYTE     = 2,
            AVSEEK_FLAG_ANY      = 4,
            AVSEEK_FLAG_FRAME    = 8;

    public static native int av_find_default_stream_index(AVFormatContext s);
    public static native int av_index_search_timestamp(AVStream st, long timestamp, int flags);
    public static native void ff_reduce_index(AVFormatContext s, int stream_index);
    public static native int av_add_index_entry(AVStream st, long pos, long timestamp,
            int size, int distance, int flags);
    public static native int av_seek_frame_binary(AVFormatContext s, int stream_index,
            long target_ts, int flags);
    public static native void av_update_cur_dts(AVFormatContext s, AVStream ref_st, long timestamp);
    public static native long av_gen_search(AVFormatContext s, int stream_index,
            long target_ts, long pos_min, long pos_max, long pos_limit,
            long ts_min, long ts_max, int flags, LongByReference ts_ret,
            AVInputFormat.Read_timestamp read_timestamp);
    public static native int av_set_parameters(AVFormatContext s, AVFormatParameters ap);

    public static native int av_write_header(AVFormatContext s);
    public static native int av_write_frame(AVFormatContext s, AVPacket pkt);
    public static native int av_interleaved_write_frame(AVFormatContext s, AVPacket pkt);
    public static native int av_interleave_packet_per_dts(AVFormatContext s, AVPacket out,
            AVPacket pkt, int flush);
    public static native int av_write_trailer(AVFormatContext s);

    public static native void dump_format(AVFormatContext ic, int index, String url, int is_output);

    public static native long parse_date(String datestr, int duration);
    public static native long av_gettime();

    public static class FFserver extends avformat  {
        public static final String libname = Loader.load(paths, libnames);

        public static final int FFM_PACKET_SIZE = 4096;
        public static native long ffm_read_write_index(int fd);
        public static native int ffm_write_write_index(int fd, long pos);
        public static native void ffm_set_write_index(AVFormatContext s, long pos, long file_size);
    }

    public static native int find_info_tag(byte[] arg, int arg_size, String tag1, String info);
    public static native int av_get_frame_filename(Pointer buf, int buf_size, String path, int number);
    public static native int av_filename_number_test(String filename);
    public static native int avf_sdp_create(AVFormatContext.PointerByReference ac, int n_files, Pointer buff, int size);
    public static native int av_match_ext(String filename, String extensions);

    public static class NewFunctions extends avformat {
        public static final String libname = Loader.load(paths, libnames);

        public static native int ff_rewind_with_probe_data(ByteIOContext s, byte[] buf, int buf_size);
        public static native int ff_rewind_with_probe_data(ByteIOContext s, ByteBuffer buf, int buf_size);
        public static native int ff_rewind_with_probe_data(ByteIOContext s, Pointer buf, int buf_size);

        public static native AVInputFormat av_probe_input_format2(AVProbeData pd, int is_opened, IntByReference score_max);
    }

    public static class DeprecatedFunctions extends avformat {
        public static final String libname = Loader.load(paths, libnames);

        @Deprecated public static native int register_protocol(URLProtocol protocol);

        @Deprecated public static native int av_metadata_set(AVMetadata.PointerByReference pm, String key, String value);

        @Deprecated public static native AVOutputFormat.ByReference guess_stream_format(String short_name,
                String filename, String mime_type);

        @Deprecated public static native AVOutputFormat.ByReference guess_format(String short_name,
                String filename, String mime_type);

        @Deprecated public static native AVFormatContext av_alloc_format_context();

        @Deprecated public static native int parse_image_size(IntByReference width_ptr,
                IntByReference height_ptr, String str);

        @Deprecated public static native int parse_frame_rate(IntByReference frame_rate,
                IntByReference frame_rate_base, String arg);
    }
}
