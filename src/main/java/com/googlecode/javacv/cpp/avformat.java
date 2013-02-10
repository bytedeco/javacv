/*
 * Copyright (C) 2010,2011,2012,2013 Samuel Audet
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
 * FFmpeg 1.1, which are covered by the following copyright notice:
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

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.MemberGetter;
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
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude="<libavformat/avformat.h>",
        includepath=genericIncludepath, linkpath=genericLinkpath, link={"avformat@.54", "avcodec@.54", "avutil@.52"}),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload="avformat-54"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avformat {
    static { load(avcodec.class); load(); }

    /**
     * @file
     * @ingroup libavf
     * Main libavformat public API header
     */

    // #include "version.h"
    /**
     * @file
     * @ingroup libavf
     * Libavformat version macros
     */

    public static final int LIBAVFORMAT_VERSION_MAJOR = 54;
    public static final int LIBAVFORMAT_VERSION_MINOR = 59;
    public static final int LIBAVFORMAT_VERSION_MICRO = 106;

    public static final int    LIBAVFORMAT_VERSION_INT = AV_VERSION_INT(LIBAVFORMAT_VERSION_MAJOR,
                                                                        LIBAVFORMAT_VERSION_MINOR,
                                                                        LIBAVFORMAT_VERSION_MICRO);
    public static final String LIBAVFORMAT_VERSION     = AV_VERSION(LIBAVFORMAT_VERSION_MAJOR,
                                                                    LIBAVFORMAT_VERSION_MINOR,
                                                                    LIBAVFORMAT_VERSION_MICRO);
    public static final int    LIBAVFORMAT_BUILD       = LIBAVFORMAT_VERSION_INT;

    public static final String LIBAVFORMAT_IDENT       = "Lavf" + LIBAVFORMAT_VERSION;


    /**
     * @defgroup libavf I/O and Muxing/Demuxing Library
     * @{
     *
     * Libavformat (lavf) is a library for dealing with various media container
     * formats. Its main two purposes are demuxing - i.e. splitting a media file
     * into component streams, and the reverse process of muxing - writing supplied
     * data in a specified container format. It also has an @ref lavf_io
     * "I/O module" which supports a number of protocols for accessing the data (e.g.
     * file, tcp, http and others). Before using lavf, you need to call
     * av_register_all() to register all compiled muxers, demuxers and protocols.
     * Unless you are absolutely sure you won't use libavformat's network
     * capabilities, you should also call avformat_network_init().
     *
     * A supported input format is described by an AVInputFormat struct, conversely
     * an output format is described by AVOutputFormat. You can iterate over all
     * registered input/output formats using the av_iformat_next() /
     * av_oformat_next() functions. The protocols layer is not part of the public
     * API, so you can only get the names of supported protocols with the
     * avio_enum_protocols() function.
     *
     * Main lavf structure used for both muxing and demuxing is AVFormatContext,
     * which exports all information about the file being read or written. As with
     * most Libavformat structures, its size is not part of public ABI, so it cannot be
     * allocated on stack or directly with av_malloc(). To create an
     * AVFormatContext, use avformat_alloc_context() (some functions, like
     * avformat_open_input() might do that for you).
     *
     * Most importantly an AVFormatContext contains:
     * @li the @ref AVFormatContext.iformat "input" or @ref AVFormatContext.oformat
     * "output" format. It is either autodetected or set by user for input;
     * always set by user for output.
     * @li an @ref AVFormatContext.streams "array" of AVStreams, which describe all
     * elementary streams stored in the file. AVStreams are typically referred to
     * using their index in this array.
     * @li an @ref AVFormatContext.pb "I/O context". It is either opened by lavf or
     * set by user for input, always set by user for output (unless you are dealing
     * with an AVFMT_NOFILE format).
     *
     * @section lavf_options Passing options to (de)muxers
     * Lavf allows to configure muxers and demuxers using the @ref avoptions
     * mechanism. Generic (format-independent) libavformat options are provided by
     * AVFormatContext, they can be examined from a user program by calling
     * av_opt_next() / av_opt_find() on an allocated AVFormatContext (or its AVClass
     * from avformat_get_class()). Private (format-specific) options are provided by
     * AVFormatContext.priv_data if and only if AVInputFormat.priv_class /
     * AVOutputFormat.priv_class of the corresponding format struct is non-NULL.
     * Further options may be provided by the @ref AVFormatContext.pb "I/O context",
     * if its AVClass is non-NULL, and the protocols layer. See the discussion on
     * nesting in @ref avoptions documentation to learn how to access those.
     *
     * @defgroup lavf_decoding Demuxing
     * @{
     * Demuxers read a media file and split it into chunks of data (@em packets). A
     * @ref AVPacket "packet" contains one or more encoded frames which belongs to a
     * single elementary stream. In the lavf API this process is represented by the
     * avformat_open_input() function for opening a file, av_read_frame() for
     * reading a single packet and finally avformat_close_input(), which does the
     * cleanup.
     *
     * @section lavf_decoding_open Opening a media file
     * The minimum information required to open a file is its URL or filename, which
     * is passed to avformat_open_input(), as in the following code:
     * @code
     * const char    *url = "in.mp3";
     * AVFormatContext *s = NULL;
     * int ret = avformat_open_input(&s, url, NULL, NULL);
     * if (ret < 0)
     *     abort();
     * @endcode
     * The above code attempts to allocate an AVFormatContext, open the
     * specified file (autodetecting the format) and read the header, exporting the
     * information stored there into s. Some formats do not have a header or do not
     * store enough information there, so it is recommended that you call the
     * avformat_find_stream_info() function which tries to read and decode a few
     * frames to find missing information.
     *
     * In some cases you might want to preallocate an AVFormatContext yourself with
     * avformat_alloc_context() and do some tweaking on it before passing it to
     * avformat_open_input(). One such case is when you want to use custom functions
     * for reading input data instead of lavf internal I/O layer.
     * To do that, create your own AVIOContext with avio_alloc_context(), passing
     * your reading callbacks to it. Then set the @em pb field of your
     * AVFormatContext to newly created AVIOContext.
     *
     * Since the format of the opened file is in general not known until after
     * avformat_open_input() has returned, it is not possible to set demuxer private
     * options on a preallocated context. Instead, the options should be passed to
     * avformat_open_input() wrapped in an AVDictionary:
     * @code
     * AVDictionary *options = NULL;
     * av_dict_set(&options, "video_size", "640x480", 0);
     * av_dict_set(&options, "pixel_format", "rgb24", 0);
     *
     * if (avformat_open_input(&s, url, NULL, &options) < 0)
     *     abort();
     * av_dict_free(&options);
     * @endcode
     * This code passes the private options 'video_size' and 'pixel_format' to the
     * demuxer. They would be necessary for e.g. the rawvideo demuxer, since it
     * cannot know how to interpret raw video data otherwise. If the format turns
     * out to be something different than raw video, those options will not be
     * recognized by the demuxer and therefore will not be applied. Such unrecognized
     * options are then returned in the options dictionary (recognized options are
     * consumed). The calling program can handle such unrecognized options as it
     * wishes, e.g.
     * @code
     * AVDictionaryEntry *e;
     * if (e = av_dict_get(options, "", NULL, AV_DICT_IGNORE_SUFFIX)) {
     *     fprintf(stderr, "Option %s not recognized by the demuxer.\n", e->key);
     *     abort();
     * }
     * @endcode
     *
     * After you have finished reading the file, you must close it with
     * avformat_close_input(). It will free everything associated with the file.
     *
     * @section lavf_decoding_read Reading from an opened file
     * Reading data from an opened AVFormatContext is done by repeatedly calling
     * av_read_frame() on it. Each call, if successful, will return an AVPacket
     * containing encoded data for one AVStream, identified by
     * AVPacket.stream_index. This packet may be passed straight into the libavcodec
     * decoding functions avcodec_decode_video2(), avcodec_decode_audio4() or
     * avcodec_decode_subtitle2() if the caller wishes to decode the data.
     *
     * AVPacket.pts, AVPacket.dts and AVPacket.duration timing information will be
     * set if known. They may also be unset (i.e. AV_NOPTS_VALUE for
     * pts/dts, 0 for duration) if the stream does not provide them. The timing
     * information will be in AVStream.time_base units, i.e. it has to be
     * multiplied by the timebase to convert them to seconds.
     *
     * If AVPacket.destruct is set on the returned packet, then the packet is
     * allocated dynamically and the user may keep it indefinitely.
     * Otherwise, if AVPacket.destruct is NULL, the packet data is backed by a
     * static storage somewhere inside the demuxer and the packet is only valid
     * until the next av_read_frame() call or closing the file. If the caller
     * requires a longer lifetime, av_dup_packet() will make an av_malloc()ed copy
     * of it.
     * In both cases, the packet must be freed with av_free_packet() when it is no
     * longer needed.
     *
     * @section lavf_decoding_seek Seeking
     * @}
     *
     * @defgroup lavf_encoding Muxing
     * @{
     * @}
     *
     * @defgroup lavf_io I/O Read/Write
     * @{
     * @}
     *
     * @defgroup lavf_codec Demuxers
     * @{
     * @defgroup lavf_codec_native Native Demuxers
     * @{
     * @}
     * @defgroup lavf_codec_wrappers External library wrappers
     * @{
     * @}
     * @}
     * @defgroup lavf_protos I/O Protocols
     * @{
     * @}
     * @defgroup lavf_internal Internal
     * @{
     * @}
     * @}
     *
     */


    // #include "avio.h"
    /**
     * @file
     * @ingroup lavf_io
     * Buffered I/O operations
     */

    public static final int AVIO_SEEKABLE_NORMAL = 0x0001; /**< Seeking works like for a local file */

    /**
     * Callback for checking whether to abort blocking functions.
     * AVERROR_EXIT is returned in this case by the interrupted
     * function. During blocking operations, callback is called with
     * opaque as parameter. If the callback returns 1, the
     * blocking operation will be aborted.
     *
     * No members can be added to this struct without a major bump, if
     * new elements have been added after this struct in AVFormatContext
     * or AVIOContext.
     */
    public static class AVIOInterruptCB extends Pointer {
        static { load(); }
        public AVIOInterruptCB() { allocate(); }
        public AVIOInterruptCB(int size) { allocateArray(size); }
        public AVIOInterruptCB(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVIOInterruptCB position(int position) {
            return (AVIOInterruptCB)super.position(position);
        }

        public static class Callback extends FunctionPointer {
            static { load(); }
            public    Callback(Pointer p) { super(p); }
            protected Callback() { allocate(); }
            private native void allocate();
            public native int call(Pointer p);
        }
        public native Callback callback(); public native AVIOInterruptCB callback(Callback callback);
        public native Pointer opaque();    public native AVIOInterruptCB opaque(Pointer opaque);
    }

    /**
     * Bytestream IO Context.
     * New fields can be added to the end with minor version bumps.
     * Removal, reordering and changes to existing fields require a major
     * version bump.
     * sizeof(AVIOContext) must not be used outside libav*.
     *
     * @note None of the function pointers in AVIOContext should be called
     *       directly, they should only be set by the client application
     *       when implementing custom I/O. Normally these are set to the
     *       function pointers specified in avio_alloc_context()
     */
    public static class AVIOContext extends Pointer {
        static { load(); }
        public AVIOContext() { allocate(); }
        public AVIOContext(int size) { allocateArray(size); }
        public AVIOContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVIOContext position(int position) {
            return (AVIOContext)super.position(position);
        }

        /**
         * A class for private options.
         *
         * If this AVIOContext is created by avio_open2(), av_class is set and
         * passes the options down to protocols.
         *
         * If this AVIOContext is manually allocated, then av_class may be set by
         * the caller.
         *
         * warning -- this field can be NULL, be sure to not pass this AVIOContext
         * to any av_opt_* functions in that case.
         */
        @Const
        public native AVClass av_class();    public native AVIOContext av_class(AVClass av_class);
        @Cast("unsigned char*") /**< Start of the buffer. */
        public native BytePointer buffer();  public native AVIOContext buffer(BytePointer buffer);
                                /**< Maximum buffer size */
        public native int buffer_size();     public native AVIOContext buffer_size(int buffer_size); 
        @Cast("unsigned char*") /**< Current position in the buffer */
        public native BytePointer buf_ptr(); public native AVIOContext buf_ptr(BytePointer buf_ptr);  
        @Cast("unsigned char*") /**< End of the data, may be less than
                                     buffer+buffer_size if the read function returned
                                     less data than requested, e.g. for streams where
                                     no more data has been received yet. */
        public native BytePointer buf_end(); public native AVIOContext buf_end(BytePointer buf_end);  
                                /**< A private pointer, passed to the read/write/seek/... functions. */
        public native Pointer opaque();      public native AVIOContext opaque(Pointer opaque); 

        public static class Read_packet extends FunctionPointer {
            static { load(); }
            public    Read_packet(Pointer p) { super(p); }
            protected Read_packet() { allocate(); }
            private native void allocate();
            public native int call(Pointer opaque, @Cast("uint8_t*") BytePointer buf, int buf_size);
        }
        public native Read_packet read_packet(); public native AVIOContext read_packet(Read_packet read_packet);

        public static class Write_packet extends FunctionPointer {
            static { load(); }
            public    Write_packet(Pointer p) { super(p); }
            protected Write_packet() { allocate(); }
            private native void allocate();
            public native int call(Pointer opaque, @Cast("uint8_t*") BytePointer buf, int buf_size);
        }
        public native Write_packet write_packet(); public native AVIOContext write_packet(Write_packet write_packet);

        public static class Seek extends FunctionPointer {
            static { load(); }
            public    Seek(Pointer p) { super(p); }
            protected Seek() { allocate(); }
            private native void allocate();
            public native long call(Pointer opaque, long offset, int whence);
        }
        public native Seek seek(); public native AVIOContext seek(Seek seek);

        public native long pos();            public native AVIOContext pos(long pos);                /**< position in the file of the current buffer */
        public native int must_flush();      public native AVIOContext must_flush(int must_flush);   /**< true if the next seek should flush */
        public native int eof_reached();     public native AVIOContext eof_reached(int eof_reached); /**< true if eof reached */
        public native int write_flag();      public native AVIOContext write_flag(int write_flag);   /**< true if open for writing */
        public native int max_packet_size(); public native AVIOContext max_packet_size(int max_packet_size);
        @Cast("unsigned long")
        public native long checksum();       public native AVIOContext checksum(long checksum);
        @Cast("unsigned char*")
        public native BytePointer checksum_ptr(); public native AVIOContext checksum_ptr(BytePointer checksum_ptr);

        public static class Update_checksum extends FunctionPointer {
            static { load(); }
            public    Update_checksum(Pointer p) { super(p); }
            protected Update_checksum() { allocate(); }
            private native void allocate();
            public native @Cast("unsigned long") long call(@Cast("unsigned long") long checksum,
                          @Cast("const uint8_t*") BytePointer buf, @Cast("unsigned") int size);
        }
        public native Update_checksum update_checksum(); 
        public native AVIOContext update_checksum(Update_checksum update_checksum);

        public native int error(); public native AVIOContext error(int error); /**< contains the error code or 0 if no error happened */
        /**
         * Pause or resume playback for network streaming protocols - e.g. MMS.
         */
        public static class Read_pause extends FunctionPointer {
            static { load(); }
            public    Read_pause(Pointer p) { super(p); }
            protected Read_pause() { allocate(); }
            private native void allocate();
            public native int call(Pointer opaque, int pause);
        }
        public native Read_pause read_pause(); public native AVIOContext read_pause(Read_pause read_pause);
        /**
         * Seek to a given timestamp in stream with the specified stream_index.
         * Needed for some network streaming protocols which don't support seeking
         * to byte position.
         */
        public static class Read_seek extends FunctionPointer {
            static { load(); }
            public    Read_seek(Pointer p) { super(p); }
            protected Read_seek() { allocate(); }
            private native void allocate();
            public native long call(Pointer opaque, int stream_index, long timestamp, int flags);
        }
        public native Read_seek read_seek(); public native AVIOContext read_seek(Read_seek read_seek);
        /**
         * A combination of AVIO_SEEKABLE_ flags or 0 when the stream is not seekable.
         */
        public native int seekable(); public native AVIOContext seekable(int seekable);

        /**
         * max filesize, used to limit allocations
         * This field is internal to libavformat and access from outside is not allowed.
         */
        public native long maxsize(); public native AVIOContext maxsize(long maxsize);

         /**
          * avio_read and avio_write should if possible be satisfied directly
          * instead of going through a buffer, and avio_seek will always
          * call the underlying seek function directly.
          */
        public native int direct();   public native AVIOContext direct(int direct);

        /**
         * Bytes read statistic
         * This field is internal to libavformat and access from outside is not allowed.
         */
        public native long bytes_read(); public native AVIOContext bytes_read(long bytes_read);

        /**
         * seek statistic
         * This field is internal to libavformat and access from outside is not allowed.
         */
        public native int seek_count();  public native AVIOContext seek_count(int seek_count);
    }

    /* unbuffered I/O */

    /**
     * Return AVIO_FLAG_* access flags corresponding to the access permissions
     * of the resource in url, or a negative value corresponding to an
     * AVERROR code in case of failure. The returned access flags are
     * masked by the value in flags.
     *
     * @note This function is intrinsically unsafe, in the sense that the
     * checked resource may change its existence or permission status from
     * one call to another. Thus you should not trust the returned value,
     * unless you are sure that no other processes are accessing the
     * checked resource.
     */
    public static native int avio_check(String url, int flags);

    /**
     * Allocate and initialize an AVIOContext for buffered I/O. It must be later
     * freed with av_free().
     *
     * @param buffer Memory block for input/output operations via AVIOContext.
     *        The buffer must be allocated with av_malloc() and friends.
     * @param buffer_size The buffer size is very important for performance.
     *        For protocols with fixed blocksize it should be set to this blocksize.
     *        For others a typical size is a cache page, e.g. 4kb.
     * @param write_flag Set to 1 if the buffer should be writable, 0 otherwise.
     * @param opaque An opaque pointer to user-specific data.
     * @param read_packet  A function for refilling the buffer, may be NULL.
     * @param write_packet A function for writing the buffer contents, may be NULL.
     *        The function may not change the input buffers content.
     * @param seek A function for seeking to specified byte position, may be NULL.
     *
     * @return Allocated AVIOContext or NULL on failure.
     */
    public static native AVIOContext avio_alloc_context(@Cast("unsigned char*") BytePointer buffer,
            int buffer_size, int write_flag, Pointer opaque, AVIOContext.Read_packet read_packet,
            AVIOContext.Write_packet write_packet, AVIOContext.Seek seek);

    public static native void avio_w8(AVIOContext s, int b);
    public static native void avio_write(AVIOContext s, @Cast("unsigned char*") BytePointer buf, int size);
    public static native void avio_wl64(AVIOContext s, @Cast("uint64_t") long val);
    public static native void avio_wb64(AVIOContext s, @Cast("uint64_t") long val);
    public static native void avio_wl32(AVIOContext s, @Cast("unsigned") int val);
    public static native void avio_wb32(AVIOContext s, @Cast("unsigned") int val);
    public static native void avio_wl24(AVIOContext s, @Cast("unsigned") int val);
    public static native void avio_wb24(AVIOContext s, @Cast("unsigned") int val);
    public static native void avio_wl16(AVIOContext s, @Cast("unsigned") int val);
    public static native void avio_wb16(AVIOContext s, @Cast("unsigned") int val);

    /**
     * Write a NULL-terminated string.
     * @return number of bytes written.
     */
    public static native int avio_put_str(AVIOContext s, String str);

    /**
     * Convert an UTF-8 string to UTF-16LE and write it.
     * @return number of bytes written.
     */
    public static native int avio_put_str16le(AVIOContext s, String str);

    /**
     * Passing this as the "whence" parameter to a seek function causes it to
     * return the filesize without seeking anywhere. Supporting this is optional.
     * If it is not supported then the seek function will return <0.
     */
    public static final int AVSEEK_SIZE = 0x10000;

    /**
     * Oring this flag as into the "whence" parameter to a seek function causes it to
     * seek by any means (like reopening and linear reading) or other normally unreasonble
     * means that can be extreemly slow.
     * This may be ignored by the seek code.
     */
    public static final int AVSEEK_FORCE = 0x20000;

    /**
     * fseek() equivalent for AVIOContext.
     * @return new position or AVERROR.
     */
    public static native long avio_seek(AVIOContext s, long offset, int whence);

    /**
     * Skip given number of bytes forward
     * @return new position or AVERROR.
     */
    public static native long avio_skip(AVIOContext s, long offset);

    /**
     * ftell() equivalent for AVIOContext.
     * @return position or AVERROR.
     */
    public static native long avio_tell(AVIOContext s);

    /**
     * Get the filesize.
     * @return filesize or AVERROR
     */
    public static native long avio_size(AVIOContext s);

    /**
     * feof() equivalent for AVIOContext.
     * @return non zero if and only if end of file
     */
    public static native int url_feof(AVIOContext s);

    /** @warning currently size is limited */
    public static native int avio_printf(AVIOContext s, String fmt);

    /**
     * Force flushing of buffered data to the output s.
     *
     * Force the buffered data to be immediately written to the output,
     * without to wait to fill the internal buffer.
     */
    public static native void avio_flush(AVIOContext s);

    /**
     * Read size bytes from AVIOContext into buf.
     * @return number of bytes read or AVERROR
     */
    public static native int avio_read(AVIOContext s, @Cast("unsigned char*") BytePointer buf, int size);

    /**
     * @name Functions for reading from AVIOContext
     * @{
     *
     * @note return 0 if EOF, so you cannot use it if EOF handling is
     *       necessary
     */
    public static native int                    avio_r8  (AVIOContext s);
    public static native @Cast("unsigned") int  avio_rl16(AVIOContext s);
    public static native @Cast("unsigned") int  avio_rl24(AVIOContext s);
    public static native @Cast("unsigned") int  avio_rl32(AVIOContext s);
    public static native @Cast("uint64_t") long avio_rl64(AVIOContext s);
    public static native @Cast("unsigned") int  avio_rb16(AVIOContext s);
    public static native @Cast("unsigned") int  avio_rb24(AVIOContext s);
    public static native @Cast("unsigned") int  avio_rb32(AVIOContext s);
    public static native @Cast("uint64_t") long avio_rb64(AVIOContext s);
    /**
     * @}
     */

    /**
     * Read a string from pb into buf. The reading will terminate when either
     * a NULL character was encountered, maxlen bytes have been read, or nothing
     * more can be read from pb. The result is guaranteed to be NULL-terminated, it
     * will be truncated if buf is too small.
     * Note that the string is not interpreted or validated in any way, it
     * might get truncated in the middle of a sequence for multi-byte encodings.
     *
     * @return number of bytes read (is always <= maxlen).
     * If reading ends on EOF or error, the return value will be one more than
     * bytes actually read.
     */
    public static native int avio_get_str(AVIOContext pb, int maxlen, @Cast("char*") BytePointer buf, int buflen);

    /**
     * Read a UTF-16 string from pb and convert it to UTF-8.
     * The reading will terminate when either a null or invalid character was
     * encountered or maxlen bytes have been read.
     * @return number of bytes read (is always <= maxlen)
     */
    public static native int avio_get_str16le(AVIOContext pb, int maxlen, @Cast("char*") BytePointer buf, int buflen);
    public static native int avio_get_str16be(AVIOContext pb, int maxlen, @Cast("char*") BytePointer buf, int buflen);


    /**
     * @name URL open modes
     * The flags argument to avio_open must be one of the following
     * constants, optionally ORed with other flags.
     * @{
     */
    public static final int
            AVIO_FLAG_READ  = 1,                                      /**< read-only */
            AVIO_FLAG_WRITE = 2,                                      /**< write-only */
            AVIO_FLAG_READ_WRITE = (AVIO_FLAG_READ|AVIO_FLAG_WRITE);  /**< read-write pseudo flag */
    /**
     * @}
     */

    /**
     * Use non-blocking mode.
     * If this flag is set, operations on the context will return
     * AVERROR(EAGAIN) if they can not be performed immediately.
     * If this flag is not set, operations on the context will never return
     * AVERROR(EAGAIN).
     * Note that this flag does not affect the opening/connecting of the
     * context. Connecting a protocol will always block if necessary (e.g. on
     * network protocols) but never hang (e.g. on busy devices).
     * Warning: non-blocking protocols is work-in-progress; this flag may be
     * silently ignored.
     */
    public static final int AVIO_FLAG_NONBLOCK = 8;

    /**
     * Use direct mode.
     * avio_read and avio_write should if possible be satisfied directly
     * instead of going through a buffer, and avio_seek will always
     * call the underlying seek function directly.
     */
    public static final int AVIO_FLAG_DIRECT = 0x8000;

    /**
     * Create and initialize a AVIOContext for accessing the
     * resource indicated by url.
     * @note When the resource indicated by url has been opened in
     * read+write mode, the AVIOContext can be used only for writing.
     *
     * @param s Used to return the pointer to the created AVIOContext.
     * In case of failure the pointed to value is set to NULL.
     * @param flags flags which control how the resource indicated by url
     * is to be opened
     * @return 0 in case of success, a negative value corresponding to an
     * AVERROR code in case of failure
     */
    public static native int avio_open(@ByPtrPtr AVIOContext s, String url, int flags);

    /**
     * Create and initialize a AVIOContext for accessing the
     * resource indicated by url.
     * @note When the resource indicated by url has been opened in
     * read+write mode, the AVIOContext can be used only for writing.
     *
     * @param s Used to return the pointer to the created AVIOContext.
     * In case of failure the pointed to value is set to NULL.
     * @param flags flags which control how the resource indicated by url
     * is to be opened
     * @param int_cb an interrupt callback to be used at the protocols level
     * @param options  A dictionary filled with protocol-private options. On return
     * this parameter will be destroyed and replaced with a dict containing options
     * that were not found. May be NULL.
     * @return 0 in case of success, a negative value corresponding to an
     * AVERROR code in case of failure
     */
    public static native int avio_open2(@ByPtrPtr AVIOContext s, String url, int flags,
            AVIOInterruptCB int_cb, @ByPtrPtr AVDictionary options);

    /**
     * Close the resource accessed by the AVIOContext s and free it.
     * This function can only be used if s was opened by avio_open().
     *
     * The internal buffer is automatically flushed before closing the
     * resource.
     *
     * @return 0 on success, an AVERROR < 0 on error.
     * @see avio_closep
     */
    public static native int avio_close(AVIOContext s);

    /**
     * Close the resource accessed by the AVIOContext *s, free it
     * and set the pointer pointing to it to NULL.
     * This function can only be used if s was opened by avio_open().
     *
     * The internal buffer is automatically flushed before closing the
     * resource.
     *
     * @return 0 on success, an AVERROR < 0 on error.
     * @see avio_close
     */
    public static native int avio_closep(@ByPtrPtr AVIOContext s);


    /**
     * Open a write only memory stream.
     *
     * @param s new IO context
     * @return zero if no error.
     */
    public static native int avio_open_dyn_buf(@ByPtrPtr AVIOContext s);

    /**
     * Return the written size and a pointer to the buffer. The buffer
     * must be freed with av_free().
     * Padding of FF_INPUT_BUFFER_PADDING_SIZE is added to the buffer.
     *
     * @param s IO context
     * @param pbuffer pointer to a byte buffer
     * @return the length of the byte buffer
     */
    public static native int avio_close_dyn_buf(AVIOContext s, @Cast("uint8_t**") PointerPointer pbuffer);

    /**
     * Iterate through names of available protocols.
     *
     * @param opaque A private pointer representing current protocol.
     *        It must be a pointer to NULL on first iteration and will
     *        be updated by successive calls to avio_enum_protocols.
     * @param output If set to 1, iterate over output protocols,
     *               otherwise over input protocols.
     *
     * @return A static string containing the name of current protocol or NULL
     */
    public static native String avio_enum_protocols(PointerPointer opaque, int output);

    /**
     * Pause and resume playing - only meaningful if using a network streaming
     * protocol (e.g. MMS).
     * @param pause 1 for pause, 0 for resume
     */
    public static native int avio_pause(AVIOContext h, int pause);

    /**
     * Seek to a given timestamp relative to some component stream.
     * Only meaningful if using a network streaming protocol (e.g. MMS.).
     * @param stream_index The stream index that the timestamp is relative to.
     *        If stream_index is (-1) the timestamp should be in AV_TIME_BASE
     *        units from the beginning of the presentation.
     *        If a stream_index >= 0 is used and the protocol does not support
     *        seeking based on component streams, the call will fail.
     * @param timestamp timestamp in AVStream.time_base units
     *        or if there is no stream specified then in AV_TIME_BASE units.
     * @param flags Optional combination of AVSEEK_FLAG_BACKWARD, AVSEEK_FLAG_BYTE
     *        and AVSEEK_FLAG_ANY. The protocol may silently ignore
     *        AVSEEK_FLAG_BACKWARD and AVSEEK_FLAG_ANY, but AVSEEK_FLAG_BYTE will
     *        fail if used and not supported.
     * @return >= 0 on success
     * @see AVInputFormat::read_seek
     */
    public static native long avio_seek_time(AVIOContext h, int stream_index, long timestamp, int flags);


    /**
     * @defgroup metadata_api Public Metadata API
     * @{
     * @ingroup libavf
     * The metadata API allows libavformat to export metadata tags to a client
     * application when demuxing. Conversely it allows a client application to
     * set metadata when muxing.
     *
     * Metadata is exported or set as pairs of key/value strings in the 'metadata'
     * fields of the AVFormatContext, AVStream, AVChapter and AVProgram structs
     * using the @ref lavu_dict "AVDictionary" API. Like all strings in FFmpeg,
     * metadata is assumed to be UTF-8 encoded Unicode. Note that metadata
     * exported by demuxers isn't checked to be valid UTF-8 in most cases.
     *
     * Important concepts to keep in mind:
     * -  Keys are unique; there can never be 2 tags with the same key. This is
     *    also meant semantically, i.e., a demuxer should not knowingly produce
     *    several keys that are literally different but semantically identical.
     *    E.g., key=Author5, key=Author6. In this example, all authors must be
     *    placed in the same tag.
     * -  Metadata is flat, not hierarchical; there are no subtags. If you
     *    want to store, e.g., the email address of the child of producer Alice
     *    and actor Bob, that could have key=alice_and_bobs_childs_email_address.
     * -  Several modifiers can be applied to the tag name. This is done by
     *    appending a dash character ('-') and the modifier name in the order
     *    they appear in the list below -- e.g. foo-eng-sort, not foo-sort-eng.
     *    -  language -- a tag whose value is localized for a particular language
     *       is appended with the ISO 639-2/B 3-letter language code.
     *       For example: Author-ger=Michael, Author-eng=Mike
     *       The original/default language is in the unqualified "Author" tag.
     *       A demuxer should set a default if it sets any translated tag.
     *    -  sorting  -- a modified version of a tag that should be used for
     *       sorting will have '-sort' appended. E.g. artist="The Beatles",
     *       artist-sort="Beatles, The".
     *
     * -  Demuxers attempt to export metadata in a generic format, however tags
     *    with no generic equivalents are left as they are stored in the container.
     *    Follows a list of generic tag names:
     *
     @verbatim
     album        -- name of the set this work belongs to
     album_artist -- main creator of the set/album, if different from artist.
                     e.g. "Various Artists" for compilation albums.
     artist       -- main creator of the work
     comment      -- any additional description of the file.
     composer     -- who composed the work, if different from artist.
     copyright    -- name of copyright holder.
     creation_time-- date when the file was created, preferably in ISO 8601.
     date         -- date when the work was created, preferably in ISO 8601.
     disc         -- number of a subset, e.g. disc in a multi-disc collection.
     encoder      -- name/settings of the software/hardware that produced the file.
     encoded_by   -- person/group who created the file.
     filename     -- original name of the file.
     genre        -- <self-evident>.
     language     -- main language in which the work is performed, preferably
                     in ISO 639-2 format. Multiple languages can be specified by
                     separating them with commas.
     performer    -- artist who performed the work, if different from artist.
                     E.g for "Also sprach Zarathustra", artist would be "Richard
                     Strauss" and performer "London Philharmonic Orchestra".
     publisher    -- name of the label/publisher.
     service_name     -- name of the service in broadcasting (channel name).
     service_provider -- name of the service provider in broadcasting.
     title        -- name of the work.
     track        -- number of this work in the set, can be in form current/total.
     variant_bitrate -- the total bitrate of the bitrate variant that the current stream is part of
     @endverbatim
     *
     * Look in the examples section for an application example how to use the Metadata API.
     *
     * @}
     */

    /* packet functions */


    /**
     * Allocate and read the payload of a packet and initialize its
     * fields with default values.
     *
     * @param pkt packet
     * @param size desired payload size
     * @return >0 (read size) if OK, AVERROR_xxx otherwise
     */
    public static native int av_get_packet(AVIOContext s, AVPacket pkt, int size);


    /**
     * Read data and append it to the current content of the AVPacket.
     * If pkt->size is 0 this is identical to av_get_packet.
     * Note that this uses av_grow_packet and thus involves a realloc
     * which is inefficient. Thus this function should only be used
     * when there is no reasonable way to know (an upper bound of)
     * the final size.
     *
     * @param pkt packet
     * @param size amount of data to read
     * @return >0 (read size) if OK, AVERROR_xxx otherwise, previous data
     *         will not be lost even if an error occurs.
     */
    public static native int av_append_packet(AVIOContext s, AVPacket pkt, int size);

    /*************************************************/
    /* fractional numbers for exact pts handling */

    /**
     * The exact value of the fractional number is: 'val + num / den'.
     * num is assumed to be 0 <= num < den.
     */
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

    /*************************************************/
    /* input/output formats */

    @Opaque public static class AVCodecTag extends Pointer {
        static { load(); }
        public AVCodecTag() { }
        public AVCodecTag(Pointer p) { super(p); }
    }

    /**
     * This structure contains the data a format has to probe a file.
     */
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
        @Cast("unsigned char*")                         /**< Buffer must have AVPROBE_PADDING_SIZE of extra allocated bytes filled with zero. */
        public native BytePointer buf();      public native AVProbeData buf(BytePointer buf);
        public native int buf_size();         public native AVProbeData buf_size(int buf_size); /**< Size of buf except extra allocated bytes */
    }

    public static final int
            AVPROBE_SCORE_MAX = 100,         ///< maximum score, half of that is used for file-extension-based detection
            AVPROBE_SCORE_RETRY = AVPROBE_SCORE_MAX/4,
            AVPROBE_PADDING_SIZE = 32;       ///< extra allocated bytes at the end of the probe buffer

    /// Demuxer will use avio_open, no opened file should be provided by the caller.
    public static final int
            AVFMT_NOFILE        = 0x0001,
            AVFMT_NEEDNUMBER    = 0x0002, /**< Needs '%d' in filename. */
            AVFMT_SHOW_IDS      = 0x0008, /**< Show format stream IDs numbers. */
            AVFMT_RAWPICTURE    = 0x0020, /**< Format wants AVPicture structure for
                                               raw picture data. */
            AVFMT_GLOBALHEADER  = 0x0040, /**< Format wants global header. */
            AVFMT_NOTIMESTAMPS  = 0x0080, /**< Format does not need / have any timestamps. */
            AVFMT_GENERIC_INDEX = 0x0100, /**< Use generic index building code. */
            AVFMT_TS_DISCONT    = 0x0200, /**< Format allows timestamp discontinuities. Note, muxers always require valid (monotone) timestamps */
            AVFMT_VARIABLE_FPS  = 0x0400, /**< Format allows variable fps. */
            AVFMT_NODIMENSIONS  = 0x0800, /**< Format does not need width/height */
            AVFMT_NOSTREAMS     = 0x1000, /**< Format does not require any streams */
            AVFMT_NOBINSEARCH   = 0x2000, /**< Format does not allow to fallback to binary search via read_timestamp */
            AVFMT_NOGENSEARCH   = 0x4000, /**< Format does not allow to fallback to generic search */
            AVFMT_NO_BYTE_SEEK  = 0x8000, /**< Format does not allow seeking by bytes */
            AVFMT_ALLOW_FLUSH  = 0x10000, /**< Format allows flushing. If not set, the muxer will not receive a NULL packet in the write_packet function. */
//#if LIBAVFORMAT_VERSION_MAJOR <= 54
            AVFMT_TS_NONSTRICT = 0x8020000, //we try to be compatible to the ABIs of ffmpeg and major forks
//#else
//            AVFMT_TS_NONSTRICT = 0x20000 /**< Format does not require strictly increasing timestamps, but they must still be monotonic */
//#endif
            AVFMT_SEEK_TO_PTS  =  0x4000000; /**< Seeking is based on PTS */

    /**
     * @addtogroup lavf_encoding
     * @{
     */
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
        public native BytePointer name();         public native AVOutputFormat name(BytePointer name);
        /**
         * Descriptive name for the format, meant to be more human-readable
         * than name. You should use the NULL_IF_CONFIG_SMALL() macro
         * to define it.
         */
        @Cast("const char*")
        public native BytePointer long_name();    public native AVOutputFormat long_name(BytePointer long_name);
        @Cast("const char*")
        public native BytePointer mime_type();    public native AVOutputFormat mime_type(BytePointer mime_type);
        @Cast("const char*") /**< comma-separated filename extensions */
        public native BytePointer extensions();   public native AVOutputFormat extensions(BytePointer extensions);
        /* output support */
        @Cast("AVCodecID")     /**< default audio codec */
        public native int audio_codec();          public native AVOutputFormat audio_codec(int audio_codec);
        @Cast("AVCodecID")     /**< default video codec */
        public native int video_codec();          public native AVOutputFormat video_codec(int video_codec);
        @Cast("AVCodecID")     /**< default subtitle codec */
        public native int subtitle_codec();       public native AVOutputFormat subtitle_codec(int subtitle_codec);
        /**
         * can use flags: AVFMT_NOFILE, AVFMT_NEEDNUMBER, AVFMT_RAWPICTURE,
         * AVFMT_GLOBALHEADER, AVFMT_NOTIMESTAMPS, AVFMT_VARIABLE_FPS,
         * AVFMT_NODIMENSIONS, AVFMT_NOSTREAMS, AVFMT_ALLOW_FLUSH,
         * AVFMT_TS_NONSTRICT
         */
        public native int flags();                public native AVOutputFormat flags(int flags);

        /**
         * List of supported codec_id-codec_tag pairs, ordered by "better
         * choice first". The arrays are all terminated by AV_CODEC_ID_NONE.
         */
        @Cast("const AVCodecTag * const *")
        public native PointerPointer codec_tag(); public native AVOutputFormat codec_tag(PointerPointer codec_tag);


        @Const ///< AVClass for the private context
        public native AVClass priv_class();       public native AVOutputFormat priv_class(AVClass av_class);
    }
    /**
     * @}
     */

    /**
     * @addtogroup lavf_decoding
     * @{
     */
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

        /**
         * A comma separated list of short names for the format. New names
         * may be appended with a minor bump.
         */
        @Cast("const char*")
        public native BytePointer name();             public native AVInputFormat name(BytePointer name);

        /**
         * Descriptive name for the format, meant to be more human-readable
         * than name. You should use the NULL_IF_CONFIG_SMALL() macro
         * to define it.
         */
        @Cast("const char*")
        public native BytePointer long_name();        public native AVInputFormat long_name(BytePointer long_name);

        /**
         * Can use flags: AVFMT_NOFILE, AVFMT_NEEDNUMBER, AVFMT_SHOW_IDS,
         * AVFMT_GENERIC_INDEX, AVFMT_TS_DISCONT, AVFMT_NOBINSEARCH,
         * AVFMT_NOGENSEARCH, AVFMT_NO_BYTE_SEEK, AVFMT_SEEK_TO_PTS.
         */
        public native int flags();                    public native AVInputFormat flags(int flags);

        /**
         * If extensions are defined, then no probe is done. You should
         * usually not use extension format guessing because it is not
         * reliable enough
         */
        @Cast("const char*")
        public native BytePointer extensions();       public native AVInputFormat extensions(BytePointer extensions);

        @Cast("const AVCodecTag * const *")
        public native PointerPointer codec_tag();     public native AVInputFormat codec_tag(PointerPointer codec_tag);

        @Const ///< AVClass for the private context
        public native AVClass priv_class();           public native AVInputFormat priv_class(AVClass av_class);
    }
    /**
     * @}
     */

    public static final int // enum AVStreamParseType {
            AVSTREAM_PARSE_NONE       = 0,
            AVSTREAM_PARSE_FULL       = 1, /**< full parsing and repack */
            AVSTREAM_PARSE_HEADERS    = 2, /**< Only parse headers, do not repack. */
            AVSTREAM_PARSE_TIMESTAMPS = 3, /**< full parsing and interpolation of timestamps for frames not starting on a packet boundary */
            AVSTREAM_PARSE_FULL_ONCE  = 4, /**< full parsing and repack of the first frame only, only implemented for H.264 currently */
            AVSTREAM_PARSE_FULL_RAW   = MKTAG(0,'R','A','W'); /**< full parsing and repack with timestamp and position generation by parser for raw
                                                                   this assumes that each packet in the file contains no demuxer level headers and
                                                                   just codec level data, otherwise position generation would fail */

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

        public native long pos();            public native AVIndexEntry pos(long pos);
        /**<
          * Timestamp in AVStream.time_base units, preferably the time from which on correctly decoded frames are available
          * when seeking to this entry. That means preferable PTS on keyframe based formats.
          * But demuxers can choose to store a different timestamp, if it is more convenient for the implementation or nothing better
          * is known
          */
        public native long timestamp();      public native AVIndexEntry timestamp(long timestamp);

        public static final int AVINDEX_KEYFRAME = 0x0001;

        @NoOffset public native int flags(); public native AVIndexEntry flags(int flags);//:2
        //Yeah, trying to keep the size of this small to reduce memory requirements (it is 24 vs. 32 bytes due to possible 8-byte alignment).
        @NoOffset public native int size();  public native AVIndexEntry size(int size);  //:30
        /**< Minimum distance between this and the previous keyframe, used to avoid unneeded searching. */
        public native int min_distance();    public native AVIndexEntry min_distance(int min_distance);
    }

    public static final int
            AV_DISPOSITION_DEFAULT  = 0x0001,
            AV_DISPOSITION_DUB      = 0x0002,
            AV_DISPOSITION_ORIGINAL = 0x0004,
            AV_DISPOSITION_COMMENT  = 0x0008,
            AV_DISPOSITION_LYRICS   = 0x0010,
            AV_DISPOSITION_KARAOKE  = 0x0020;

    /**
     * Track should be used during playback by default.
     * Useful for subtitle track that should be displayed
     * even when user did not explicitly ask for subtitles.
     */
    public static final int
            AV_DISPOSITION_FORCED           = 0x0040,
            AV_DISPOSITION_HEARING_IMPAIRED = 0x0080,  /**< stream for hearing impaired audiences */
            AV_DISPOSITION_VISUAL_IMPAIRED  = 0x0100,  /**< stream for visual impaired audiences */
            AV_DISPOSITION_CLEAN_EFFECTS    = 0x0200;  /**< stream without voice */
    /**
     * The stream is stored in the file as an attached picture/"cover art" (e.g.
     * APIC frame in ID3v2). The single packet associated with it will be returned
     * among the first few packets read from the file unless seeking takes place.
     * It can also be accessed at any time in AVStream.attached_pic.
     */
    public static final int AV_DISPOSITION_ATTACHED_PIC = 0x0400;

    /**
     * Options for behavior on timestamp wrap detection.
     */
    public static final int
            AV_PTS_WRAP_IGNORE     = 0,   ///< ignore the wrap
            AV_PTS_WRAP_ADD_OFFSET = 1,   ///< add the format specific offset on wrap detection
            AV_PTS_WRAP_SUB_OFFSET = -1;  ///< subtract the format specific offset on wrap detection

    /**
     * Stream structure.
     * New fields can be added to the end with minor version bumps.
     * Removal, reordering and changes to existing fields require a major
     * version bump.
     * sizeof(AVStream) must not be used outside libav*.
     */
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

        /**< stream index in AVFormatContext */
        public native int index();                      public native AVStream index(int index);
        /**
         * Format-specific stream ID.
         * decoding: set by libavformat
         * encoding: set by the user, replaced by libavformat if left unset
         */
        public native int id();                         public native AVStream id(int id);
        /**
         * Codec context associated with this stream. Allocated and freed by
         * libavformat.
         *
         * - decoding: The demuxer exports codec information stored in the headers
         *             here.
         * - encoding: The user sets codec information, the muxer writes it to the
         *             output. Mandatory fields as specified in AVCodecContext
         *             documentation must be set even if this AVCodecContext is
         *             not actually used for encoding.
         */
        public native AVCodecContext codec();           public native AVStream codec(AVCodecContext codec);
        /**
         * Real base framerate of the stream.
         * This is the lowest framerate with which all timestamps can be
         * represented accurately (it is the least common multiple of all
         * framerates in the stream). Note, this value is just a guess!
         * For example, if the time base is 1/90000 and all frames have either
         * approximately 3600 or 1800 timer ticks, then r_frame_rate will be 50/1.
         */
        @ByRef
        public native AVRational r_frame_rate();        public native AVStream r_frame_rate(AVRational r_frame_rate);
        public native Pointer priv_data();              public native AVStream priv_data(Pointer priv_data);

        /**
         * encoding: pts generation when outputting stream
         */
        @ByRef
        public native AVFrac pts();                     public native AVStream pts(AVFrac pts);

        /**
         * This is the fundamental unit of time (in seconds) in terms
         * of which frame timestamps are represented.
         *
         * decoding: set by libavformat
         * encoding: set by libavformat in avformat_write_header. The muxer may use the
         * user-provided value of @ref AVCodecContext.time_base "codec->time_base"
         * as a hint.
         */
        @ByRef
        public native AVRational time_base();           public native AVStream time_base(AVRational time_base);

        /**
         * Decoding: pts of the first frame of the stream in presentation order, in stream time base.
         * Only set this if you are absolutely 100% sure that the value you set
         * it to really is the pts of the first frame.
         * This may be undefined (AV_NOPTS_VALUE).
         * @note The ASF header does NOT contain a correct start_time the ASF
         * demuxer must NOT set this.
         */
        public native long start_time();                public native AVStream start_time(long start_time);

        /**
         * Decoding: duration of the stream, in stream time base.
         * If a source file does not specify a duration, but does specify
         * a bitrate, this value will be estimated from bitrate and file size.
         */
        public native long duration();                  public native AVStream duration(long duration);

        ///< number of frames in this stream if known or 0
        public native long nb_frames();                 public native AVStream nb_frames(long nb_frames);

        /**< AV_DISPOSITION_* bit field */
        public native int disposition();                public native AVStream disposition(int disposition);

        @Cast("AVDiscard") ///< Selects which packets can be discarded at will and do not need to be demuxed.
        public native int discard();                    public native AVStream discard(int discard); 

        /**
         * sample aspect ratio (0 if unknown)
         * - encoding: Set by user.
         * - decoding: Set by libavformat.
         */
        @ByRef
        public native AVRational sample_aspect_ratio(); public native AVStream sample_aspect_ratio(AVRational sample_aspect_ratio);

        public native AVDictionary metadata();          public native AVStream metadata(AVDictionary metadata);

        /**
         * Average framerate
         */
        @ByRef
        public native AVRational avg_frame_rate();      public native AVStream avg_frame_rate(AVRational avg_frame_rate);

        /**
         * For streams with AV_DISPOSITION_ATTACHED_PIC disposition, this packet
         * will contain the attached picture.
         *
         * decoding: set by libavformat, must not be modified by the caller.
         * encoding: unused
         */
        @ByRef
        public native AVPacket attached_pic();          public native AVStream attached_pic(AVPacket attached_pic);
    }

    public static final int AV_PROGRAM_RUNNING = 1;

    /**
     * New fields can be added to the end with minor version bumps.
     * Removal, reordering and changes to existing fields require a major
     * version bump.
     * sizeof(AVProgram) must not be used outside libav*.
     */
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

        public native int          id();                public native AVProgram id(int id);
        public native int          flags();             public native AVProgram flags(int flags);
        @Cast("AVDiscard") ///< selects which program to discard and which to feed to the caller
        public native int          discard();           public native AVProgram discard(int discard);
        @Cast("unsigned int*")
        public native IntPointer   stream_index();      public native AVProgram stream_index(IntPointer stream_index);
        public native int          nb_stream_indexes(); public native AVProgram nb_stream_indexes(int nb_stream_indexes);
        public native AVDictionary metadata();          public native AVProgram metadata(AVDictionary metadata);

        public native int program_num();                public native AVProgram program_num(int program_num);
        public native int pmt_pid();                    public native AVProgram pmt_pid(int pmt_pid);
        public native int pcr_pid();                    public native AVProgram pcr_pid(int pcr_pid);
    }

    public static final int AVFMTCTX_NOHEADER = 0x0001; /**< signal that no header is present (streams are added dynamically) */

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

                                               ///< unique ID to identify the chapter
        public native int id();                public native AVChapter id(int id);
        @ByRef                                 ///< time base in which the start/end timestamps are specified
        public native AVRational time_base();  public native AVChapter time_base(AVRational time_base); 
                                               ///< chapter start/end time in time_base units
        public native long start();            public native AVChapter start(long start);
        public native long end();              public native AVChapter end(long end);
        public native AVDictionary metadata(); public native AVChapter metadata(AVDictionary metadata);
    }


    /**
     * The duration of a video can be estimated through various ways, and this enum can be used
     * to know how the duration was estimated.
     */
    public static final int // enum AVDurationEstimationMethod {
            AVFMT_DURATION_FROM_PTS      = 0, ///< Duration accurately estimated from PTSes
            AVFMT_DURATION_FROM_STREAM   = 1, ///< Duration estimated from a stream with a known duration
            AVFMT_DURATION_FROM_BITRATE  = 2; ///< Duration estimated from bitrate (less accurate)

    /**
     * Format I/O context.
     * New fields can be added to the end with minor version bumps.
     * Removal, reordering and changes to existing fields require a major
     * version bump.
     * sizeof(AVFormatContext) must not be used outside libav*, use
     * avformat_alloc_context() to create an AVFormatContext.
     */
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

        /**
         * A class for logging and AVOptions. Set by avformat_alloc_context().
         * Exports (de)muxer private options if they exist.
         */
        public native @Const AVClass av_class();            public native AVFormatContext av_class(AVClass av_class);

        /**
         * Can only be iformat or oformat, not both at the same time.
         *
         * decoding: set by avformat_open_input().
         * encoding: set by the user.
         */
        public native AVInputFormat iformat();              public native AVFormatContext iformat(AVInputFormat iformat);
        public native AVOutputFormat oformat();             public native AVFormatContext oformat(AVOutputFormat oformat);

        /**
         * Format private data. This is an AVOptions-enabled struct
         * if and only if iformat/oformat.priv_class is not NULL.
         */
        public native Pointer priv_data();                  public native AVFormatContext priv_data(Pointer priv_data);

        /**
         * I/O context.
         *
         * decoding: either set by the user before avformat_open_input() (then
         * the user must close it manually) or set by avformat_open_input().
         * encoding: set by the user.
         *
         * Do NOT set this field if AVFMT_NOFILE flag is set in
         * iformat/oformat.flags. In such a case, the (de)muxer will handle
         * I/O in some other way and this field will be NULL.
         */
        public native AVIOContext pb();                     public native AVFormatContext pb(AVIOContext pb);

        /* stream info */                                   /**< Format-specific flags, see AVFMTCTX_xx */
        public native int ctx_flags();                      public native AVFormatContext ctx_flags(int ctx_flags);

        /**
         * A list of all streams in the file. New streams are created with
         * avformat_new_stream().
         *
         * decoding: streams are created by libavformat in avformat_open_input().
         * If AVFMTCTX_NOHEADER is set in ctx_flags, then new streams may also
         * appear in av_read_frame().
         * encoding: streams are created by the user before avformat_write_header().
         */
        @Cast("unsigned")
        public native int nb_streams();                     public native AVFormatContext nb_streams(int nb_streams);
        @Cast("AVStream**")
        public native PointerPointer streams();             public native AVFormatContext streams(PointerPointer streams);
        @MemberGetter public native AVStream streams(int i);

        // char filename[1024]; /**< input or output filename */
        public native String filename();                    public native AVFormatContext filename(String filename);

        /**
         * Decoding: position of the first frame of the component, in
         * AV_TIME_BASE fractional seconds. NEVER set this value directly:
         * It is deduced from the AVStream values.
         */
        public native long start_time();                    public native AVFormatContext start_time(long start_time);

        /**
         * Decoding: duration of the stream, in AV_TIME_BASE fractional
         * seconds. Only set this value if you know none of the individual stream
         * durations and also do not set any of them. This is deduced from the
         * AVStream values if not set.
         */
        public native long duration();                      public native AVFormatContext duration(long duration);

        /**
         * Decoding: total stream bitrate in bit/s, 0 if not
         * available. Never set it directly if the file_size and the
         * duration are known as FFmpeg can compute it automatically.
         */
        public native int bit_rate();                       public native AVFormatContext bit_rate(int bit_rate);

        @Cast("unsigned")
        public native int packet_size();                    public native AVFormatContext packet_size(int packet_size);
        public native int max_delay();                      public native AVFormatContext max_delay(int max_delay);

        public native int flags();                          public native AVFormatContext flags(int flags);
        public static final int
                AVFMT_FLAG_GENPTS      = 0x0001, ///< Generate missing pts even if it requires parsing future frames.
                AVFMT_FLAG_IGNIDX      = 0x0002, ///< Ignore index.
                AVFMT_FLAG_NONBLOCK    = 0x0004, ///< Do not block when reading packets from input.
                AVFMT_FLAG_IGNDTS      = 0x0008, ///< Ignore DTS on frames that contain both DTS & PTS
                AVFMT_FLAG_NOFILLIN    = 0x0010, ///< Do not infer any values from other values, just return what is stored in the container
                AVFMT_FLAG_NOPARSE     = 0x0020, ///< Do not use AVParsers, you also must set AVFMT_FLAG_NOFILLIN as the fillin code works on frames and no parsing -> no frames. Also seeking to frames can not work if parsing to find frame boundaries has been disabled
                AVFMT_FLAG_NOBUFFER    = 0x0040, ///< Do not buffer frames when possible
                AVFMT_FLAG_CUSTOM_IO   = 0x0080, ///< The caller has supplied a custom AVIOContext, don't avio_close() it.
                AVFMT_FLAG_DISCARD_CORRUPT = 0x0100, ///< Discard frames marked corrupted
                AVFMT_FLAG_MP4A_LATM   = 0x8000, ///< Enable RTP MP4A-LATM payload
                AVFMT_FLAG_SORT_DTS   = 0x10000, ///< try to interleave outputted packets by dts (using this flag can slow demuxing down)
                AVFMT_FLAG_PRIV_OPT   = 0x20000, ///< Enable use of private options by delaying codec open (this could be made default once all code is converted)
                AVFMT_FLAG_KEEP_SIDE_DATA = 0x40000; ///< Don't merge side data but keep it separate.

        /**
         * decoding: size of data to probe; encoding: unused.
         */
        @Cast("unsigned")
        public native int probesize();                      public native AVFormatContext probesize(int probesize);

        /**
         * decoding: maximum time (in AV_TIME_BASE units) during which the input should
         * be analyzed in avformat_find_stream_info().
         */
        public native int max_analyze_duration();           public native AVFormatContext max_analyze_duration(int max_analyze_duration);

        @Cast("const uint8_t*")
        public native BytePointer key();                    public native AVFormatContext key(BytePointer key);
        public native int keylen();                         public native AVFormatContext keylen(int keylen);

        @Cast("unsigned")
        public native int nb_programs();                    public native AVFormatContext nb_programs(int nb_programs);
        @Cast("AVProgram**")
        public native PointerPointer programs();            public native AVFormatContext programs(PointerPointer programs);
        @MemberGetter public native AVProgram programs(int i);

        /**
         * Forced video codec_id.
         * Demuxing: Set by user.
         */
        @Cast("AVCodecID")
        public native int video_codec_id();                 public native AVFormatContext video_codec_id(int video_codec_id);

        /**
         * Forced audio codec_id.
         * Demuxing: Set by user.
         */
        @Cast("AVCodecID")
        public native int audio_codec_id();                 public native AVFormatContext audio_codec_id(int audio_codec_id);

        /**
         * Forced subtitle codec_id.
         * Demuxing: Set by user.
         */
        @Cast("AVCodecID")
        public native int subtitle_codec_id();              public native AVFormatContext subtitle_codec_id(int subtitle_codec_id);

        /**
         * Maximum amount of memory in bytes to use for the index of each stream.
         * If the index exceeds this size, entries will be discarded as
         * needed to maintain a smaller size. This can lead to slower or less
         * accurate seeking (depends on demuxer).
         * Demuxers for which a full in-memory index is mandatory will ignore
         * this.
         * muxing  : unused
         * demuxing: set by user
         */
        @Cast("unsigned")
        public native int max_index_size();                 public native AVFormatContext max_index_size(int max_index_size);

        /**
         * Maximum amount of memory in bytes to use for buffering frames
         * obtained from realtime capture devices.
         */
        @Cast("unsigned")
        public native int max_picture_buffer();             public native AVFormatContext max_picture_buffer(int max_picture_buffer);

        @Cast("unsigned")
        public native int nb_chapters();                    public native AVFormatContext nb_chapters(int nb_chapters);
        @Cast("AVChapter**")
        public native PointerPointer chapters();            public native AVFormatContext chapters(PointerPointer chapters);
        @MemberGetter public native AVChapter chapters(int i);

        public native AVDictionary metadata();              public native AVFormatContext metadata(AVDictionary metadata);

        /**
         * Start time of the stream in real world time, in microseconds
         * since the unix epoch (00:00 1st January 1970). That is, pts=0
         * in the stream was captured at this real world time.
         * - encoding: Set by user.
         * - decoding: Unused.
         */
        public native long start_time_realtime();           public native AVFormatContext start_time_realtime(long start_time_realtime);

        /**
         * decoding: number of frames used to probe fps
         */
        public native int fps_probe_size();                 public native AVFormatContext fps_probe_size(int fps_probe_size);

        /**
         * Error recognition; higher values will detect more errors but may
         * misdetect some more or less valid parts as errors.
         * - encoding: unused
         * - decoding: Set by user.
         */
        public native int error_recognition();              public native AVFormatContext error_recognition(int error_recognition);

        /**
         * Custom interrupt callbacks for the I/O layer.
         *
         * decoding: set by the user before avformat_open_input().
         * encoding: set by the user before avformat_write_header()
         * (mainly useful for AVFMT_NOFILE formats). The callback
         * should also be passed to avio_open2() if it's used to
         * open the file.
         */
        @ByRef
        public native AVIOInterruptCB interrupt_callback(); public native AVFormatContext interrupt_callback(AVIOInterruptCB interrupt_callback);

        /**
         * Flags to enable debugging.
         */
        public native int debug();                          public native AVFormatContext debug(int debug);
        public static final int FF_FDEBUG_TS = 0x0001;

        /**
         * Transport stream id.
         * This will be moved into demuxer private options. Thus no API/ABI compatibility
         */
        public native int ts_id();                          public native AVFormatContext ts_id(int ts_id);

        /**
         * Audio preload in microseconds.
         * Note, not all formats support this and unpredictable things may happen if it is used when not supported.
         * - encoding: Set by user via AVOptions (NO direct access)
         * - decoding: unused
         */
        public native int audio_preload();                  public native AVFormatContext audio_preload(int audio_preload);

        /**
         * Max chunk time in microseconds.
         * Note, not all formats support this and unpredictable things may happen if it is used when not supported.
         * - encoding: Set by user via AVOptions (NO direct access)
         * - decoding: unused
         */
        public native int max_chunk_duration();             public native AVFormatContext max_chunk_duration(int max_chunk_duration);

        /**
         * Max chunk size in bytes
         * Note, not all formats support this and unpredictable things may happen if it is used when not supported.
         * - encoding: Set by user via AVOptions (NO direct access)
         * - decoding: unused
         */
        public native int max_chunk_size();                 public native AVFormatContext max_chunk_size(int max_chunk_size);

        /**
         * forces the use of wallclock timestamps as pts/dts of packets
         * This has undefined results in the presence of B frames.
         * - encoding: unused
         * - decoding: Set by user via AVOptions (NO direct access)
         */
        public native int use_wallclock_as_timestamps();    public native AVFormatContext use_wallclock_as_timestamps(int use_wallclock_as_timestamps);

        /**
         * Avoids negative timestamps during muxing
         *  0 -> allow negative timestamps
         *  1 -> avoid negative timestamps
         * -1 -> choose automatically (default)
         * Note, this is only works when interleave_packet_per_dts is in use
         * - encoding: Set by user via AVOptions (NO direct access)
         * - decoding: unused
         */
        public native int avoid_negative_ts();              public native AVFormatContext avoid_negative_ts(int avoid_negative_ts);

        /**
         * avio flags, used to force AVIO_FLAG_DIRECT.
         * - encoding: unused
         * - decoding: Set by user via AVOptions (NO direct access)
         */
        public native int avio_flags();                     public native AVFormatContext avio_flags(int avio_flags);

        /**
         * The duration field can be estimated through various ways, and this field can be used
         * to know how the duration was estimated.
         * - encoding: unused
         * - decoding: Read by user via AVOptions (NO direct access)
         */
        @Cast("AVDurationEstimationMethod")
        public native int duration_estimation_method();     public native AVFormatContext duration_estimation_method(int duration_estimation_method);
        /**
         * Skip initial bytes when opening stream
         * - encoding: unused
         * - decoding: Set by user via AVOptions (NO direct access)
         */
        @Cast("unsigned")
        public native int skip_initial_bytes();             public native AVFormatContext skip_initial_bytes(int skip_initial_bytes);

        /**
         * Correct single timestamp overflows
         * - encoding: unused
         * - decoding: Set by user via AVOPtions (NO direct access)
         */
        @Cast("unsigned")
        public native int correct_ts_overflow();            public native AVFormatContext correct_ts_overflow(int correct_ts_overflow);
    }

    /**
     * Returns the method used to set ctx->duration.
     *
     * @return AVFMT_DURATION_FROM_PTS, AVFMT_DURATION_FROM_STREAM, or AVFMT_DURATION_FROM_BITRATE.
     */
    public static native @Cast("AVDurationEstimationMethod") int av_fmt_ctx_get_duration_estimation_method(AVFormatContext ctx);

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

        public native @ByRef AVPacket pkt(); public native AVPacketList pkt(AVPacket pkt);
        public native AVPacketList next();   public native AVPacketList next(AVPacketList next);
    }


    /**
     * @defgroup lavf_core Core functions
     * @ingroup libavf
     *
     * Functions for querying libavformat capabilities, allocating core structures,
     * etc.
     * @{
     */

    /**
     * Return the LIBAVFORMAT_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int avformat_version();

    /**
     * Return the libavformat build-time configuration.
     */
    public static native String avformat_configuration();

    /**
     * Return the libavformat license.
     */
    public static native String avformat_license();

    /**
     * Initialize libavformat and register all the muxers, demuxers and
     * protocols. If you do not call this function, then you can select
     * exactly which formats you want to support.
     *
     * @see av_register_input_format()
     * @see av_register_output_format()
     */
    public static native void av_register_all();

    public static native void av_register_input_format(AVInputFormat format);
    public static native void av_register_output_format(AVOutputFormat format);

    /**
     * Do global initialization of network components. This is optional,
     * but recommended, since it avoids the overhead of implicitly
     * doing the setup for each session.
     *
     * Calling this function will become mandatory if using network
     * protocols at some major version bump.
     */
    public static native int avformat_network_init();

    /**
     * Undo the initialization done by avformat_network_init.
     */
    public static native int avformat_network_deinit();

    /**
     * If f is NULL, returns the first registered input format,
     * if f is non-NULL, returns the next registered input format after f
     * or NULL if f is the last one.
     */
    public static native AVInputFormat av_iformat_next(AVInputFormat f);

    /**
     * If f is NULL, returns the first registered output format,
     * if f is non-NULL, returns the next registered output format after f
     * or NULL if f is the last one.
     */
    public static native AVOutputFormat av_oformat_next(AVOutputFormat f);

    /**
     * Allocate an AVFormatContext.
     * avformat_free_context() can be used to free the context and everything
     * allocated by the framework within it.
     */
    public static native AVFormatContext avformat_alloc_context();

    /**
     * Free an AVFormatContext and all its streams.
     * @param s context to free
     */
    public static native void avformat_free_context(AVFormatContext s);

    /**
     * Get the AVClass for AVFormatContext. It can be used in combination with
     * AV_OPT_SEARCH_FAKE_OBJ for examining options.
     *
     * @see av_opt_find().
     */
    public static native @Const AVClass avformat_get_class();

    /**
     * Add a new stream to a media file.
     *
     * When demuxing, it is called by the demuxer in read_header(). If the
     * flag AVFMTCTX_NOHEADER is set in s.ctx_flags, then it may also
     * be called in read_packet().
     *
     * When muxing, should be called by the user before avformat_write_header().
     *
     * @param c If non-NULL, the AVCodecContext corresponding to the new stream
     * will be initialized to use this codec. This is needed for e.g. codec-specific
     * defaults to be set, so codec should be provided if it is known.
     *
     * @return newly created stream or NULL on error.
     */
    public static native AVStream avformat_new_stream(AVFormatContext s, AVCodec c);

    public static native AVProgram av_new_program(AVFormatContext s, int id);

    /**
     * @}
     */


    /**
     * Allocate an AVFormatContext for an output format.
     * avformat_free_context() can be used to free the context and
     * everything allocated by the framework within it.
     *
     * @param *ctx is set to the created format context, or to NULL in
     * case of failure
     * @param oformat format to use for allocating the context, if NULL
     * format_name and filename are used instead
     * @param format_name the name of output format to use for allocating the
     * context, if NULL filename is used instead
     * @param filename the name of the filename to use for allocating the
     * context, may be NULL
     * @return >= 0 in case of success, a negative AVERROR code in case of
     * failure
     */
    public static native int avformat_alloc_output_context2(@ByPtrPtr AVFormatContext ctx,
            AVOutputFormat oformat, String format_name, String filename);

    /**
     * @addtogroup lavf_decoding
     * @{
     */

    /**
     * Find AVInputFormat based on the short name of the input format.
     */
    public static native AVInputFormat av_find_input_format(String short_name);

    /**
     * Guess the file format.
     *
     * @param is_opened Whether the file is already opened; determines whether
     *                  demuxers with or without AVFMT_NOFILE are probed.
     */
    public static native AVInputFormat av_probe_input_format(AVProbeData pd, int is_opened);

    /**
     * Guess the file format.
     *
     * @param is_opened Whether the file is already opened; determines whether
     *                  demuxers with or without AVFMT_NOFILE are probed.
     * @param score_max A probe score larger that this is required to accept a
     *                  detection, the variable is set to the actual detection
     *                  score afterwards.
     *                  If the score is <= AVPROBE_SCORE_MAX / 4 it is recommended
     *                  to retry with a larger probe buffer.
     */
    public static native AVInputFormat av_probe_input_format2(AVProbeData pd, int is_opened, int[] score_max);

    /**
     * Guess the file format.
     *
     * @param is_opened Whether the file is already opened; determines whether
     *                  demuxers with or without AVFMT_NOFILE are probed.
     * @param score_ret The score of the best detection.
     */
    public static native AVInputFormat av_probe_input_format3(AVProbeData pd, int is_opened, int[] score_ret);

    /**
     * Probe a bytestream to determine the input format. Each time a probe returns
     * with a score that is too low, the probe buffer size is increased and another
     * attempt is made. When the maximum probe size is reached, the input format
     * with the highest score is returned.
     *
     * @param pb the bytestream to probe
     * @param fmt the input format is put here
     * @param filename the filename of the stream
     * @param logctx the log context
     * @param offset the offset within the bytestream to probe from
     * @param max_probe_size the maximum probe buffer size (zero for default)
     * @return 0 in case of success, a negative value corresponding to an
     * AVERROR code otherwise
     */
    public static native int av_probe_input_buffer(AVIOContext pb, @ByPtrPtr AVInputFormat fmt, String filename,
            Pointer logctx, @Cast("unsigned") int offset, @Cast("unsigned") int max_probe_size);

    /**
     * Open an input stream and read the header. The codecs are not opened.
     * The stream must be closed with av_close_input_file().
     *
     * @param ps Pointer to user-supplied AVFormatContext (allocated by avformat_alloc_context).
     *           May be a pointer to NULL, in which case an AVFormatContext is allocated by this
     *           function and written into ps.
     *           Note that a user-supplied AVFormatContext will be freed on failure.
     * @param filename Name of the stream to open.
     * @param fmt If non-NULL, this parameter forces a specific input format.
     *            Otherwise the format is autodetected.
     * @param options  A dictionary filled with AVFormatContext and demuxer-private options.
     *                 On return this parameter will be destroyed and replaced with a dict containing
     *                 options that were not found. May be NULL.
     *
     * @return 0 on success, a negative AVERROR on failure.
     *
     * @note If you want to use custom IO, preallocate the format context and set its pb field.
     */
    public static native int avformat_open_input(@ByPtrPtr AVFormatContext ps, String filename, AVInputFormat fmt, @ByPtrPtr AVDictionary options);


    /**
     * Read packets of a media file to get stream information. This
     * is useful for file formats with no headers such as MPEG. This
     * function also computes the real framerate in case of MPEG-2 repeat
     * frame mode.
     * The logical file position is not changed by this function;
     * examined packets may be buffered for later processing.
     *
     * @param ic media file handle
     * @param options  If non-NULL, an ic.nb_streams long array of pointers to
     *                 dictionaries, where i-th member contains options for
     *                 codec corresponding to i-th stream.
     *                 On return each dictionary will be filled with options that were not found.
     * @return >=0 if OK, AVERROR_xxx on error
     *
     * @note this function isn't guaranteed to open all the codecs, so
     *       options being non-empty at return is a perfectly normal behavior.
     *
     * @todo Let the user decide somehow what information is needed so that
     *       we do not waste time getting stuff the user does not need.
     */
    public static native int avformat_find_stream_info(AVFormatContext ic, @ByPtrPtr AVDictionary options);

    /**
     * Find the programs which belong to a given stream.
     *
     * @param ic    media file handle
     * @param last  the last found program, the search will start after this
     *              program, or from the beginning if it is NULL
     * @param s     stream index
     * @return the next program which belongs to s, NULL if no program is found or
     *         the last program is not among the programs of ic.
     */
    public static native AVProgram av_find_program_from_stream(AVFormatContext ic, AVProgram last, int s);

    /**
     * Find the "best" stream in the file.
     * The best stream is determined according to various heuristics as the most
     * likely to be what the user expects.
     * If the decoder parameter is non-NULL, av_find_best_stream will find the
     * default decoder for the stream's codec; streams for which no decoder can
     * be found are ignored.
     *
     * @param ic                media file handle
     * @param type              stream type: video, audio, subtitles, etc.
     * @param wanted_stream_nb  user-requested stream number,
     *                          or -1 for automatic selection
     * @param related_stream    try to find a stream related (eg. in the same
     *                          program) to this one, or -1 if none
     * @param decoder_ret       if non-NULL, returns the decoder for the
     *                          selected stream
     * @param flags             flags; none are currently defined
     * @return  the non-negative stream number in case of success,
     *          AVERROR_STREAM_NOT_FOUND if no stream with the requested type
     *          could be found,
     *          AVERROR_DECODER_NOT_FOUND if streams were found but no decoder
     * @note  If av_find_best_stream returns successfully and decoder_ret is not
     *        NULL, then *decoder_ret is guaranteed to be set to a valid AVCodec.
     */
    public static native int av_find_best_stream(AVFormatContext ic, @Cast("AVMediaType") int type,
            int wanted_stream_nb, int related_stream, @ByPtrPtr AVCodec decoder_ret, int flags);

    /**
     * Return the next frame of a stream.
     * This function returns what is stored in the file, and does not validate
     * that what is there are valid frames for the decoder. It will split what is
     * stored in the file into frames and return one for each call. It will not
     * omit invalid data between valid frames so as to give the decoder the maximum
     * information possible for decoding.
     *
     * If pkt->destruct is NULL, then the packet is valid until the next
     * av_read_frame() or until av_close_input_file(). Otherwise the packet is valid
     * indefinitely. In both cases the packet must be freed with
     * av_free_packet when it is no longer needed. For video, the packet contains
     * exactly one frame. For audio, it contains an integer number of frames if each
     * frame has a known fixed size (e.g. PCM or ADPCM data). If the audio frames
     * have a variable size (e.g. MPEG audio), then it contains one frame.
     *
     * pkt->pts, pkt->dts and pkt->duration are always set to correct
     * values in AVStream.time_base units (and guessed if the format cannot
     * provide them). pkt->pts can be AV_NOPTS_VALUE if the video format
     * has B-frames, so it is better to rely on pkt->dts if you do not
     * decompress the payload.
     *
     * @return 0 if OK, < 0 on error or end of file
     */
    public static native int av_read_frame(AVFormatContext s, AVPacket pkt);

    /**
     * Seek to the keyframe at timestamp.
     * 'timestamp' in 'stream_index'.
     * @param stream_index If stream_index is (-1), a default
     * stream is selected, and timestamp is automatically converted
     * from AV_TIME_BASE units to the stream specific time_base.
     * @param timestamp Timestamp in AVStream.time_base units
     *        or, if no stream is specified, in AV_TIME_BASE units.
     * @param flags flags which select direction and seeking mode
     * @return >= 0 on success
     */
    public static native int av_seek_frame(AVFormatContext s, int stream_index, long timestamp, int flags);

    /**
     * Seek to timestamp ts.
     * Seeking will be done so that the point from which all active streams
     * can be presented successfully will be closest to ts and within min/max_ts.
     * Active streams are all streams that have AVStream.discard < AVDISCARD_ALL.
     *
     * If flags contain AVSEEK_FLAG_BYTE, then all timestamps are in bytes and
     * are the file position (this may not be supported by all demuxers).
     * If flags contain AVSEEK_FLAG_FRAME, then all timestamps are in frames
     * in the stream with stream_index (this may not be supported by all demuxers).
     * Otherwise all timestamps are in units of the stream selected by stream_index
     * or if stream_index is -1, in AV_TIME_BASE units.
     * If flags contain AVSEEK_FLAG_ANY, then non-keyframes are treated as
     * keyframes (this may not be supported by all demuxers).
     *
     * @param stream_index index of the stream which is used as time base reference
     * @param min_ts smallest acceptable timestamp
     * @param ts target timestamp
     * @param max_ts largest acceptable timestamp
     * @param flags flags
     * @return >=0 on success, error code otherwise
     *
     * @note This is part of the new seek API which is still under construction.
     *       Thus do not use this yet. It may change at any time, do not expect
     *       ABI compatibility yet!
     */
    public static native int avformat_seek_file(AVFormatContext s, int stream_index, long min_ts, long ts, long max_ts, int flags);

    /**
     * Start playing a network-based stream (e.g. RTSP stream) at the
     * current position.
     */
    public static native int av_read_play(AVFormatContext s);

    /**
     * Pause a network-based stream (e.g. RTSP stream).
     *
     * Use av_read_play() to resume it.
     */
    public static native int av_read_pause(AVFormatContext s);

    /**
     * Close an opened input AVFormatContext. Free it and all its contents
     * and set *s to NULL.
     */
    public static native void avformat_close_input(@ByPtrPtr AVFormatContext s);
    /**
     * @}
     */

    public static final int
            AVSEEK_FLAG_BACKWARD = 1, ///< seek backward
            AVSEEK_FLAG_BYTE     = 2, ///< seeking based on position in bytes
            AVSEEK_FLAG_ANY      = 4, ///< seek to any frame, even non-keyframes
            AVSEEK_FLAG_FRAME    = 8; ///< seeking based on frame number

    /**
     * @addtogroup lavf_encoding
     * @{
     */
    /**
     * Allocate the stream private data and write the stream header to
     * an output media file.
     *
     * @param s Media file handle, must be allocated with avformat_alloc_context().
     *          Its oformat field must be set to the desired output format;
     *          Its pb field must be set to an already openened AVIOContext.
     * @param options  An AVDictionary filled with AVFormatContext and muxer-private options.
     *                 On return this parameter will be destroyed and replaced with a dict containing
     *                 options that were not found. May be NULL.
     *
     * @return 0 on success, negative AVERROR on failure.
     *
     * @see av_opt_find, av_dict_set, avio_open, av_oformat_next.
     */
    public static native int avformat_write_header(AVFormatContext s, @ByPtrPtr AVDictionary options);

    /**
     * Write a packet to an output media file.
     *
     * The packet shall contain one audio or video frame.
     * The packet must be correctly interleaved according to the container
     * specification, if not then av_interleaved_write_frame must be used.
     *
     * @param s media file handle
     * @param pkt The packet, which contains the stream_index, buf/buf_size,
     *            dts/pts, ...
     *            This can be NULL (at any time, not just at the end), in
     *            order to immediately flush data buffered within the muxer,
     *            for muxers that buffer up data internally before writing it
     *            to the output.
     * @return < 0 on error, = 0 if OK, 1 if flushed and there is no more data to flush
     */
    public static native int av_write_frame(AVFormatContext s, AVPacket pkt);

    /**
     * Write a packet to an output media file ensuring correct interleaving.
     *
     * The packet must contain one audio or video frame.
     * If the packets are already correctly interleaved, the application should
     * call av_write_frame() instead as it is slightly faster. It is also important
     * to keep in mind that completely non-interleaved input will need huge amounts
     * of memory to interleave with this, so it is preferable to interleave at the
     * demuxer level.
     *
     * @param s media file handle
     * @param pkt The packet containing the data to be written. Libavformat takes
     * ownership of the data and will free it when it sees fit using the packet's
     * @ref AVPacket.destruct "destruct" field. The caller must not access the data
     * after this function returns, as it may already be freed.
     * This can be NULL (at any time, not just at the end), to flush the
     * interleaving queues.
     * Packet's @ref AVPacket.stream_index "stream_index" field must be set to the
     * index of the corresponding stream in @ref AVFormatContext.streams
     * "s.streams".
     * It is very strongly recommended that timing information (@ref AVPacket.pts
     * "pts", @ref AVPacket.dts "dts" @ref AVPacket.duration "duration") is set to
     * correct values.
     *
     * @return 0 on success, a negative AVERROR on error.
     */
    public static native int av_interleaved_write_frame(AVFormatContext s, AVPacket pkt);

    /**
     * Write the stream trailer to an output media file and free the
     * file private data.
     *
     * May only be called after a successful call to avformat_write_header.
     *
     * @param s media file handle
     * @return 0 if OK, AVERROR_xxx on error
     */
    public static native int av_write_trailer(AVFormatContext s);

    /**
     * Return the output format in the list of registered output formats
     * which best matches the provided parameters, or return NULL if
     * there is no match.
     *
     * @param short_name if non-NULL checks if short_name matches with the
     * names of the registered formats
     * @param filename if non-NULL checks if filename terminates with the
     * extensions of the registered formats
     * @param mime_type if non-NULL checks if mime_type matches with the
     * MIME type of the registered formats
     */
    public static native AVOutputFormat av_guess_format(String short_name, String filename, String mime_type);

    /**
     * Guess the codec ID based upon muxer and filename.
     */
    public static native @Cast("AVCodecID") int av_guess_codec(AVOutputFormat fmt, String short_name,
            String filename, String mime_type, @Cast("AVMediaType") int type);

    /**
     * Get timing information for the data currently output.
     * The exact meaning of "currently output" depends on the format.
     * It is mostly relevant for devices that have an internal buffer and/or
     * work in real time.
     * @param s          media file handle
     * @param stream     stream in the media file
     * @param[out] dts   DTS of the last packet output for the stream, in stream
     *                   time_base units
     * @param[out] wall  absolute time when that packet whas output,
     *                   in microsecond
     * @return  0 if OK, AVERROR(ENOSYS) if the format does not support it
     * Note: some formats or devices may not allow to measure dts and wall
     * atomically.
     */
    public static native int av_get_output_timestamp(AVFormatContext s, int stream, long[] dts, long[] wall);


    /**
     * @}
     */


    /**
     * @defgroup lavf_misc Utility functions
     * @ingroup libavf
     * @{
     *
     * Miscellaneous utility functions related to both muxing and demuxing (or neither).
     */

    /**
     * Send a nice hexadecimal dump of a buffer to the specified file stream.
     *
     * @param f The file stream pointer where the dump should be sent to.
     * @param buf buffer
     * @param size buffer size
     *
     * @see av_hex_dump_log, av_pkt_dump2, av_pkt_dump_log2
     */
    public static native void av_hex_dump(@Cast("FILE*") Pointer f,  @Cast("uint8_t*") BytePointer buf, int size);

    /**
     * Send a nice hexadecimal dump of a buffer to the log.
     *
     * @param avcl A pointer to an arbitrary struct of which the first field is a
     * pointer to an AVClass struct.
     * @param level The importance level of the message, lower values signifying
     * higher importance.
     * @param buf buffer
     * @param size buffer size
     *
     * @see av_hex_dump, av_pkt_dump2, av_pkt_dump_log2
     */
    public static native void av_hex_dump_log(Pointer avcl, int level, @Cast("uint8_t*") BytePointer buf, int size);

    /**
     * Send a nice dump of a packet to the specified file stream.
     *
     * @param f The file stream pointer where the dump should be sent to.
     * @param pkt packet to dump
     * @param dump_payload True if the payload must be displayed, too.
     * @param st AVStream that the packet belongs to
     */
    public static native void av_pkt_dump2(@Cast("FILE*") Pointer f, AVPacket pkt, int dump_payload, AVStream st);


    /**
     * Send a nice dump of a packet to the log.
     *
     * @param avcl A pointer to an arbitrary struct of which the first field is a
     * pointer to an AVClass struct.
     * @param level The importance level of the message, lower values signifying
     * higher importance.
     * @param pkt packet to dump
     * @param dump_payload True if the payload must be displayed, too.
     * @param st AVStream that the packet belongs to
     */
    public static native void av_pkt_dump_log2(Pointer avcl, int level, AVPacket pkt, int dump_payload, AVStream st);

    /**
     * Get the AVCodecID for the given codec tag tag.
     * If no codec id is found returns AV_CODEC_ID_NONE.
     *
     * @param tags list of supported codec_id-codec_tag pairs, as stored
     * in AVInputFormat.codec_tag and AVOutputFormat.codec_tag
     */
    public static native @Cast("AVCodecID") int av_codec_get_id(@ByPtrPtr AVCodecTag tags, @Cast("unsigned") int tag);

    /**
     * Get the codec tag for the given codec id id.
     * If no codec tag is found returns 0.
     *
     * @param tags list of supported codec_id-codec_tag pairs, as stored
     * in AVInputFormat.codec_tag and AVOutputFormat.codec_tag
     */
    public static native @Cast("unsigned") int av_codec_get_tag(@ByPtrPtr AVCodecTag tags, @Cast("AVCodecID") int id);

    public static native int av_find_default_stream_index(AVFormatContext s);

    /**
     * Get the index for a specific timestamp.
     * @param flags if AVSEEK_FLAG_BACKWARD then the returned index will correspond
     *                 to the timestamp which is <= the requested one, if backward
     *                 is 0, then it will be >=
     *              if AVSEEK_FLAG_ANY seek to any frame, only keyframes otherwise
     * @return < 0 if no such timestamp could be found
     */
    public static native int av_index_search_timestamp(AVStream st, long timestamp, int flags);

    /**
     * Add an index entry into a sorted list. Update the entry if the list
     * already contains it.
     *
     * @param timestamp timestamp in the time base of the given stream
     */
    public static native int av_add_index_entry(AVStream st, long pos, long timestamp,
            int size, int distance, int flags);


    /**
     * Split a URL string into components.
     *
     * The pointers to buffers for storing individual components may be null,
     * in order to ignore that component. Buffers for components not found are
     * set to empty strings. If the port is not found, it is set to a negative
     * value.
     *
     * @param proto the buffer for the protocol
     * @param proto_size the size of the proto buffer
     * @param authorization the buffer for the authorization
     * @param authorization_size the size of the authorization buffer
     * @param hostname the buffer for the host name
     * @param hostname_size the size of the hostname buffer
     * @param port_ptr a pointer to store the port number in
     * @param path the buffer for the path
     * @param path_size the size of the path buffer
     * @param url the URL to split
     */
    public static native void av_url_split(@Cast("char*") byte[] proto, int proto_size,
            @Cast("char*") byte[] authorization, int authorization_size, @Cast("char*") byte[] hostname,
            int hostname_size, int[] port_ptr, @Cast("char*") byte[] path, int path_size, String url);

    public static native void av_dump_format(AVFormatContext ic, int index, String url, int is_output);

    /**
     * Return in 'buf' the path with '%d' replaced by a number.
     *
     * Also handles the '%0nd' format where 'n' is the total number
     * of digits and '%%'.
     *
     * @param buf destination buffer
     * @param buf_size destination buffer size
     * @param path numbered sequence string
     * @param number frame number
     * @return 0 if OK, -1 on format error
     */
    public static native int av_get_frame_filename(@Cast("char*") BytePointer buf, int buf_size, String path, int number);

    /**
     * Check whether filename actually is a numbered sequence generator.
     *
     * @param filename possible numbered sequence string
     * @return 1 if a valid numbered sequence string, 0 otherwise
     */
    public static native int av_filename_number_test(String filename);

    /**
     * Generate an SDP for an RTP session.
     *
     * Note, this overwrites the id values of AVStreams in the muxer contexts
     * for getting unique dynamic payload types.
     *
     * @param ac array of AVFormatContexts describing the RTP streams. If the
     *           array is composed by only one context, such context can contain
     *           multiple AVStreams (one AVStream per RTP stream). Otherwise,
     *           all the contexts in the array (an AVCodecContext per RTP stream)
     *           must contain only one AVStream.
     * @param n_files number of AVCodecContexts contained in ac
     * @param buf buffer where the SDP will be stored (must be allocated by
     *            the caller)
     * @param size the size of the buffer
     * @return 0 if OK, AVERROR_xxx on error
     */
    public static native int av_sdp_create(@Cast("AVFormatContext**") PointerPointer ac, int n_files, @Cast("char*") BytePointer buf, int size);

    /**
     * Return a positive value if the given filename has one of the given
     * extensions, 0 otherwise.
     *
     * @param extensions a comma-separated list of filename extensions
     */
    public static native int av_match_ext(String filename, String extensions);

    /**
     * Test if the given container can store a codec.
     *
     * @param std_compliance standards compliance level, one of FF_COMPLIANCE_*
     *
     * @return 1 if codec with ID codec_id can be stored in ofmt, 0 if it cannot.
     *         A negative number if this information is not available.
     */
    public static native int avformat_query_codec(AVOutputFormat ofmt, @Cast("AVCodecID") int codec_id, int std_compliance);

    /**
     * @defgroup riff_fourcc RIFF FourCCs
     * @{
     * Get the tables mapping RIFF FourCCs to libavcodec AVCodecIDs. The tables are
     * meant to be passed to av_codec_get_id()/av_codec_get_tag() as in the
     * following code:
     * @code
     * uint32_t tag = MKTAG('H', '2', '6', '4');
     * const struct AVCodecTag *table[] = { avformat_get_riff_video_tags(), 0 };
     * enum AVCodecID id = av_codec_get_id(table, tag);
     * @endcode
     */
    /**
     * @return the table mapping RIFF FourCCs for video to libavcodec AVCodecID.
     */
    public static native @Const AVCodecTag avformat_get_riff_video_tags();
    /**
     * @return the table mapping RIFF FourCCs for audio to AVCodecID.
     */
    public static native @Const AVCodecTag avformat_get_riff_audio_tags();

    /**
     * @}
     */

    /**
     * Guess the sample aspect ratio of a frame, based on both the stream and the
     * frame aspect ratio.
     *
     * Since the frame aspect ratio is set by the codec but the stream aspect ratio
     * is set by the demuxer, these two may not be equal. This function tries to
     * return the value that you should use if you would like to display the frame.
     *
     * Basic logic is to use the stream aspect ratio if it is set to something sane
     * otherwise use the frame aspect ratio. This way a container setting, which is
     * usually easy to modify can override the coded value in the frames.
     *
     * @param format the format context which the stream is part of
     * @param stream the stream which the frame is part of
     * @param frame the frame with the aspect ratio to be determined
     * @return the guessed (valid) sample_aspect_ratio, 0/1 if no idea
     */
    public static native @ByVal AVRational av_guess_sample_aspect_ratio(AVFormatContext format, AVStream stream, AVFrame frame);

    /**
     * Check if the stream st contained in s is matched by the stream specifier
     * spec.
     *
     * See the "stream specifiers" chapter in the documentation for the syntax
     * of spec.
     *
     * @return  >0 if st is matched by spec;
     *          0  if st is not matched by spec;
     *          AVERROR code if spec is invalid
     *
     * @note  A stream specifier can match several streams in the format.
     */
    public static native int avformat_match_stream_specifier(AVFormatContext s, AVStream st, String spec);

    public static native void avformat_queue_attached_pictures(AVFormatContext s);

    /**
     * @}
     */
}
