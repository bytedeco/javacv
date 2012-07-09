/*
 * Copyright (C) 2010,2011,2012 Samuel Audet
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
 * This file was derived from avfilter.h include file from
 * FFmpeg 0.11.1, which are covered by the following copyright notice:
 *
 * filter layer
 * Copyright (c) 2007 Bobby Bingham
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
import com.googlecode.javacpp.LongPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avcodec.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(define="__STDC_CONSTANT_MACROS", cinclude={"<libavfilter/avfilter.h>", "<libavfilter/buffersink.h>",
        "<libavfilter/buffersrc.h>", "<libavfilter/avcodec.h>", "<libavfilter/avfiltergraph.h>"},
        includepath=genericIncludepath, linkpath=genericLinkpath, link={"avfilter@.2", "swscale@.2", "swresample@.0",
        "postproc@.52", "avformat@.54", "avcodec@.54", "avutil@.51"}),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, preload="avfilter-2"),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class avfilter {
    static { load(avformat.class); load(postproc.class); load(swresample.class); load(swscale.class); load(); }

    // #include "version.h"
    /**
     * @file
     * Libavfilter version macros
     */

    public static final int LIBAVFILTER_VERSION_MAJOR =  2;
    public static final int LIBAVFILTER_VERSION_MINOR = 77;
    public static final int LIBAVFILTER_VERSION_MICRO = 100;

    public static final int    LIBAVFILTER_VERSION_INT = AV_VERSION_INT(LIBAVFILTER_VERSION_MAJOR,
                                                                        LIBAVFILTER_VERSION_MINOR,
                                                                        LIBAVFILTER_VERSION_MICRO);
    public static final String LIBAVFILTER_VERSION     = AV_VERSION(LIBAVFILTER_VERSION_MAJOR,
                                                                    LIBAVFILTER_VERSION_MINOR,
                                                                    LIBAVFILTER_VERSION_MICRO);
    public static final int    LIBAVFILTER_BUILD       = LIBAVFILTER_VERSION_INT;


    /**
     * Return the LIBAVFILTER_VERSION_INT constant.
     */
    public static native @Cast("unsigned") int avfilter_version();

    /**
     * Return the libavfilter build-time configuration.
     */
    public static native String avfilter_configuration();

    /**
     * Return the libavfilter license.
     */
    public static native String avfilter_license();


    /**
     * A reference-counted buffer data type used by the filter system. Filters
     * should not store pointers to this structure directly, but instead use the
     * AVFilterBufferRef structure below.
     */
    public static class AVFilterBuffer extends Pointer {
        static { load(); }
        public AVFilterBuffer() { allocate(); }
        public AVFilterBuffer(int size) { allocateArray(size); }
        public AVFilterBuffer(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterBuffer position(int position) {
            return (AVFilterBuffer)super.position(position);
        }

        @Cast("uint8_t*") // uint8_t *data[8];        ///< buffer data for each plane/channel
        public native BytePointer data(int i);        public native AVFilterBuffer data(int i, BytePointer data);
        // int linesize[8];                           ///< number of bytes per line
        public native int linesize(int i);            public native AVFilterBuffer linesize(int i, int linesize);
        @Cast("unsigned")                             ///< number of references to this buffer
        public native int refcount();                 public native AVFilterBuffer refcount(int refcount);

        /** private data to be used by a custom free function */
        public native Pointer priv();                 public native AVFilterBuffer priv(Pointer priv);
        /**
         * A pointer to the function to deallocate this buffer if the default
         * function is not sufficient. This could, for example, add the memory
         * back into a memory pool to be reused later without the overhead of
         * reallocating it from scratch.
         */
        public static class Free extends FunctionPointer {
            static { load(); }
            public native void call(AVFilterBuffer buf);
        }
        public native Free free();                    public native AVFilterBuffer free(Free free);
                                                      ///< media format
        public native int format();                   public native AVFilterBuffer format(int format);
                                                      ///< width and height of the allocated buffer
        public native int w();                        public native AVFilterBuffer w(int w);
        public native int h();                        public native AVFilterBuffer h(int h);

        /**
         * pointers to the data planes/channels.
         *
         * For video, this should simply point to data[].
         *
         * For planar audio, each channel has a separate data pointer, and
         * linesize[0] contains the size of each channel buffer.
         * For packed audio, there is just one data pointer, and linesize[0]
         * contains the total size of the buffer for all channels.
         *
         * Note: Both data and extended_data will always be set, but for planar
         * audio with more channels that can fit in data, extended_data must be used
         * in order to access all channels.
         */
        @Cast("uint8_t**")
        public native PointerPointer extended_data(); public native AVFilterBuffer extended_data(PointerPointer extended_data);
    }

    public static final int
            AV_PERM_READ          = 0x01,  ///< can read from the buffer
            AV_PERM_WRITE         = 0x02,  ///< can write to the buffer
            AV_PERM_PRESERVE      = 0x04,  ///< nobody else can overwrite the buffer
            AV_PERM_REUSE         = 0x08,  ///< can output the buffer multiple times, with the same contents each time
            AV_PERM_REUSE2        = 0x10,  ///< can output the buffer multiple times, modified each time
            AV_PERM_NEG_LINESIZES = 0x20,  ///< the buffer requested can have negative linesizes
            AV_PERM_ALIGN         = 0x40;  ///< the buffer must be aligned

    /**
     * Audio specific properties in a reference to an AVFilterBuffer. Since
     * AVFilterBufferRef is common to different media formats, audio specific
     * per reference properties must be separated out.
     */
    public static class AVFilterBufferRefAudioProps extends Pointer {
        static { load(); }
        public AVFilterBufferRefAudioProps() { allocate(); }
        public AVFilterBufferRefAudioProps(int size) { allocateArray(size); }
        public AVFilterBufferRefAudioProps(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterBufferRefAudioProps position(int position) {
            return (AVFilterBufferRefAudioProps)super.position(position);
        }

        @Cast("uint64_t")                    ///< channel layout of audio buffer
        public native long channel_layout(); public native AVFilterBufferRefAudioProps channel_layout(long channel_layout);
                                             ///< number of audio samples per channel
        public native int nb_samples();      public native AVFilterBufferRefAudioProps nb_samples(int nb_samples);
                                             ///< audio buffer sample rate
        public native int sample_rate();     public native AVFilterBufferRefAudioProps sample_rate(int sample_rate);
    }

    /**
     * Video specific properties in a reference to an AVFilterBuffer. Since
     * AVFilterBufferRef is common to different media formats, video specific
     * per reference properties must be separated out.
     */
    public static class AVFilterBufferRefVideoProps extends Pointer {
        static { load(); }
        public AVFilterBufferRefVideoProps() { allocate(); }
        public AVFilterBufferRefVideoProps(int size) { allocateArray(size); }
        public AVFilterBufferRefVideoProps(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterBufferRefVideoProps position(int position) {
            return (AVFilterBufferRefVideoProps)super.position(position);
        }
                                                        ///< image width
        public native int w();                          public native AVFilterBufferRefVideoProps w(int w);
                                                        ///< image height
        public native int h();                          public native AVFilterBufferRefVideoProps h(int h);
        @ByRef                                          ///< sample aspect ratio
        public native AVRational sample_aspect_ratio(); public native AVFilterBufferRefVideoProps sample_aspect_ratio(AVRational sample_aspect_ratio);
                                                        ///< is frame interlaced
        public native int interlaced();                 public native AVFilterBufferRefVideoProps interlaced(int interlaced);
                                                        ///< field order
        public native int top_field_first();            public native AVFilterBufferRefVideoProps top_field_first(int top_field_first);
        @Cast("enum AVPictureType")                     ///< picture type of the frame
        public native int pict_type();                  public native AVFilterBufferRefVideoProps pict_type(int pict_type);
                                                        ///< 1 -> keyframe, 0-> not
        public native int key_frame();                  public native AVFilterBufferRefVideoProps key_frame(int key_frame);
    }

    /**
     * A reference to an AVFilterBuffer. Since filters can manipulate the origin of
     * a buffer to, for example, crop image without any memcpy, the buffer origin
     * and dimensions are per-reference properties. Linesize is also useful for
     * image flipping, frame to field filters, etc, and so is also per-reference.
     *
     * TODO: add anything necessary for frame reordering
     */
    public static class AVFilterBufferRef extends Pointer {
        static { load(); }
        public AVFilterBufferRef() { allocate(); }
        public AVFilterBufferRef(int size) { allocateArray(size); }
        public AVFilterBufferRef(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterBufferRef position(int position) {
            return (AVFilterBufferRef)super.position(position);
        }
                                                           ///< the buffer that this is a reference to
        public native AVFilterBuffer buf();                public native AVFilterBufferRef buf(AVFilterBuffer buf);
        @Cast("uint8_t*") // uint8_t *data[8];             ///< picture/audio data for each plane
        public native BytePointer data(int i);             public native AVFilterBufferRef data(int i, BytePointer data);
        // int linesize[8];                                ///< number of bytes per line
        public native int linesize(int i);                 public native AVFilterBufferRef linesize(int i, int linesize);
                                                           ///< media format
        public native int format();                        public native AVFilterBufferRef format(int format);

        /**
         * presentation timestamp. The time unit may change during
         * filtering, as it is specified in the link and the filter code
         * may need to rescale the PTS accordingly.
         */
        public native long pts();                          public native AVFilterBufferRef pts(long pts);
                                                           ///< byte position in stream, -1 if unknown
        public native long pos();                          public native AVFilterBufferRef pos(long pos);
                                                           ///< permissions, see the AV_PERM_* flags
        public native int perms();                         public native AVFilterBufferRef perms(int perms);

        @Cast("AVMediaType")                               ///< media type of buffer data
        public native int type();                          public native AVFilterBufferRef type(int type);
                                                           ///< video buffer specific properties
        public native AVFilterBufferRefVideoProps video(); public native AVFilterBufferRef video(AVFilterBufferRefVideoProps video);
                                                           ///< audio buffer specific properties
        public native AVFilterBufferRefAudioProps audio(); public native AVFilterBufferRef audio(AVFilterBufferRefAudioProps audio);

        /**
         * pointers to the data planes/channels.
         *
         * For video, this should simply point to data[].
         *
         * For planar audio, each channel has a separate data pointer, and
         * linesize[0] contains the size of each channel buffer.
         * For packed audio, there is just one data pointer, and linesize[0]
         * contains the total size of the buffer for all channels.
         *
         * Note: Both data and extended_data will always be set, but for planar
         * audio with more channels that can fit in data, extended_data must be used
         * in order to access all channels.
         */
        @Cast("uint8_t**")
        public native PointerPointer extended_data();      public native AVFilterBufferRef extended_data(PointerPointer extended_data);
    }

    /**
     * Copy properties of src to dst, without copying the actual data
     */
    public static native void avfilter_copy_buffer_ref_props(AVFilterBufferRef dst, AVFilterBufferRef src);

    /**
     * Add a new reference to a buffer.
     *
     * @param ref   an existing reference to the buffer
     * @param pmask a bitmask containing the allowable permissions in the new
     *              reference
     * @return      a new reference to the buffer with the same properties as the
     *              old, excluding any permissions denied by pmask
     */
    public static native AVFilterBufferRef avfilter_ref_buffer(AVFilterBufferRef ref, int pmask);

    /**
     * Remove a reference to a buffer. If this is the last reference to the
     * buffer, the buffer itself is also automatically freed.
     *
     * @param ref reference to the buffer, may be NULL
     */
    public static native void avfilter_unref_buffer(AVFilterBufferRef ref);

    /**
     * Remove a reference to a buffer and set the pointer to NULL.
     * If this is the last reference to the buffer, the buffer itself
     * is also automatically freed.
     *
     * @param ref pointer to the buffer reference
     */
    public static native void avfilter_unref_bufferp(@ByPtrPtr AVFilterBufferRef ref);

    /**
     * A list of supported formats for one end of a filter link. This is used
     * during the format negotiation process to try to pick the best format to
     * use to minimize the number of necessary conversions. Each filter gives a
     * list of the formats supported by each input and output pad. The list
     * given for each pad need not be distinct - they may be references to the
     * same list of formats, as is often the case when a filter supports multiple
     * formats, but will always output the same format as it is given in input.
     *
     * In this way, a list of possible input formats and a list of possible
     * output formats are associated with each link. When a set of formats is
     * negotiated over a link, the input and output lists are merged to form a
     * new list containing only the common elements of each list. In the case
     * that there were no common elements, a format conversion is necessary.
     * Otherwise, the lists are merged, and all other links which reference
     * either of the format lists involved in the merge are also affected.
     *
     * For example, consider the filter chain:
     * filter (a) --> (b) filter (b) --> (c) filter
     *
     * where the letters in parenthesis indicate a list of formats supported on
     * the input or output of the link. Suppose the lists are as follows:
     * (a) = {A, B}
     * (b) = {A, B, C}
     * (c) = {B, C}
     *
     * First, the first link's lists are merged, yielding:
     * filter (a) --> (a) filter (a) --> (c) filter
     *
     * Notice that format list (b) now refers to the same list as filter list (a).
     * Next, the lists for the second link are merged, yielding:
     * filter (a) --> (a) filter (a) --> (a) filter
     *
     * where (a) = {B}.
     *
     * Unfortunately, when the format lists at the two ends of a link are merged,
     * we must ensure that all links which reference either pre-merge format list
     * get updated as well. Therefore, we have the format list structure store a
     * pointer to each of the pointers to itself.
     */
    public static class AVFilterFormats extends Pointer {
        static { load(); }
        public AVFilterFormats() { allocate(); }
        public AVFilterFormats(int size) { allocateArray(size); }
        public AVFilterFormats(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterFormats position(int position) {
            return (AVFilterFormats)super.position(position);
        }
        @Cast("unsigned")                           ///< number of formats
        public native int format_count();           public native AVFilterFormats format_count(int format_count);
                                                    ///< list of media formats
        public native IntPointer formats();         public native AVFilterFormats formats(IntPointer formats);
        @MemberGetter public native int formats(int i);
        @Cast("unsigned")                           ///< number of references to this list
        public native int refcount();               public native AVFilterFormats refcount(int refcount);
        @Cast("AVFilterFormats***")                 ///< references to this list
        public native PointerPointer refs();        public native AVFilterFormats refs(PointerPointer refs);
    }

    /**
     * Create a list of supported formats. This is intended for use in
     * AVFilter->query_formats().
     *
     * @param fmts list of media formats, terminated by -1. If NULL an
     *        empty list is created.
     * @return the format list, with no existing references
     */
    public static native AVFilterFormats avfilter_make_format_list(int[] fmts);

    /**
     * Add fmt to the list of media formats contained in *avff.
     * If *avff is NULL the function allocates the filter formats struct
     * and puts its pointer in *avff.
     *
     * @return a non negative value in case of success, or a negative
     * value corresponding to an AVERROR code in case of error
     */
    public static native int avfilter_add_format(@ByPtrPtr AVFilterFormats avff, long fmt);

    /**
     * Return a list of all formats supported by FFmpeg for the given media type.
     */
    public static native AVFilterFormats avfilter_make_all_formats(@Cast("AVMediaType") int type);

    /**
     * A list of all channel layouts supported by libavfilter.
     */
    @MemberGetter public static native long avfilter_all_channel_layouts(int i);

    /**
     * Return a format list which contains the intersection of the formats of
     * a and b. Also, all the references of a, all the references of b, and
     * a and b themselves will be deallocated.
     *
     * If a and b do not share any common formats, neither is modified, and NULL
     * is returned.
     */
    public static native AVFilterFormats avfilter_merge_formats(AVFilterFormats a, AVFilterFormats b);

    /**
     * Add *ref as a new reference to formats.
     * That is the pointers will point like in the ASCII art below:
     *   ________
     *  |formats |<--------.
     *  |  ____  |     ____|___________________
     *  | |refs| |    |  __|_
     *  | |* * | |    | |  | |  AVFilterLink
     *  | |* *--------->|*ref|
     *  | |____| |    | |____|
     *  |________|    |________________________
     */
    public static native void avfilter_formats_ref(AVFilterFormats formats, @ByPtrPtr AVFilterFormats ref);

    /**
     * If *ref is non-NULL, remove *ref as a reference to the format list
     * it currently points to, deallocates that list if this was the last
     * reference, and sets *ref to NULL.
     *
     *         Before                                 After
     *   ________                               ________         NULL
     *  |formats |<--------.                   |formats |         ^
     *  |  ____  |     ____|________________   |  ____  |     ____|________________
     *  | |refs| |    |  __|_                  | |refs| |    |  __|_
     *  | |* * | |    | |  | |  AVFilterLink   | |* * | |    | |  | |  AVFilterLink
     *  | |* *--------->|*ref|                 | |*   | |    | |*ref|
     *  | |____| |    | |____|                 | |____| |    | |____|
     *  |________|    |_____________________   |________|    |_____________________
     */
    public static native void avfilter_formats_unref(@ByPtrPtr AVFilterFormats ref);

    /**
     *
     *         Before                                 After
     *   ________                         ________
     *  |formats |<---------.            |formats |<---------.
     *  |  ____  |       ___|___         |  ____  |       ___|___
     *  | |refs| |      |   |   |        | |refs| |      |   |   |   NULL
     *  | |* *--------->|*oldref|        | |* *--------->|*newref|     ^
     *  | |* * | |      |_______|        | |* * | |      |_______|  ___|___
     *  | |____| |                       | |____| |                |   |   |
     *  |________|                       |________|                |*oldref|
     *                                                             |_______|
     */
    public static native void avfilter_formats_changeref(@ByPtrPtr AVFilterFormats oldref, @ByPtrPtr AVFilterFormats newref);

    /**
     * A filter pad used for either input or output.
     *
     * See doc/filter_design.txt for details on how to implement the methods.
     */
    public static class AVFilterPad extends Pointer {
        static { load(); }
        public AVFilterPad() { allocate(); }
        public AVFilterPad(int size) { allocateArray(size); }
        public AVFilterPad(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterPad position(int position) {
            return (AVFilterPad)super.position(position);
        }

        /**
         * Pad name. The name is unique among inputs and among outputs, but an
         * input may have the same name as an output. This may be NULL if this
         * pad has no need to ever be referenced by name.
         */
        @Cast("const char*")
        public native BytePointer name();                  public native AVFilterPad name(BytePointer name);

        /**
         * AVFilterPad type.
         */
        @Cast("AVMediaType")
        public native int type();                          public native AVFilterPad type(int type);

        /**
         * Minimum required permissions on incoming buffers. Any buffer with
         * insufficient permissions will be automatically copied by the filter
         * system to a new buffer which provides the needed access permissions.
         *
         * Input pads only.
         */
        public native int min_perms();                     public native AVFilterPad min_perms(int min_perms);

        /**
         * Permissions which are not accepted on incoming buffers. Any buffer
         * which has any of these permissions set will be automatically copied
         * by the filter system to a new buffer which does not have those
         * permissions. This can be used to easily disallow buffers with
         * AV_PERM_REUSE.
         *
         * Input pads only.
         */
        public native int rej_perms();                     public native AVFilterPad rej_perms(int rej_perms);

        /**
         * Callback called before passing the first slice of a new frame. If
         * NULL, the filter layer will default to storing a reference to the
         * picture inside the link structure.
         *
         * Input video pads only.
         */
        public static class Start_frame extends FunctionPointer {
            static { load(); }
            public    Start_frame(Pointer p) { super(p); }
            protected Start_frame() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link, AVFilterBufferRef picref);
        }
        public native Start_frame start_frame();           public native AVFilterPad start_frame(Start_frame start_frame);

        /**
         * Callback function to get a video buffer. If NULL, the filter system will
         * use avfilter_default_get_video_buffer().
         *
         * Input video pads only.
         */
        public static class Get_video_buffer extends FunctionPointer {
            static { load(); }
            public    Get_video_buffer(Pointer p) { super(p); }
            protected Get_video_buffer() { allocate(); }
            protected final native void allocate();
            public native AVFilterBufferRef callback(AVFilterLink link, int perms, int w, int h);
        }
        public native Get_video_buffer get_video_buffer(); public native AVFilterPad get_video_buffer(Get_video_buffer get_video_buffer);

        /**
         * Callback function to get an audio buffer. If NULL, the filter system will
         * use avfilter_default_get_audio_buffer().
         *
         * Input audio pads only.
         */
        public static class Get_audio_buffer extends FunctionPointer {
            static { load(); }
            public    Get_audio_buffer(Pointer p) { super(p); }
            protected Get_audio_buffer() { allocate(); }
            protected final native void allocate();
            public native AVFilterBufferRef callback(AVFilterLink link, int perms, int nb_samples);
        }
        public native Get_audio_buffer get_audio_buffer(); public native AVFilterPad get_audio_buffer(Get_audio_buffer get_audio_buffer);

        /**
         * Callback called after the slices of a frame are completely sent. If
         * NULL, the filter layer will default to releasing the reference stored
         * in the link structure during start_frame().
         *
         * Input video pads only.
         */
        public static class End_frame extends FunctionPointer {
            static { load(); }
            public    End_frame(Pointer p) { super(p); }
            protected End_frame() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link);
        }
        public native End_frame end_frame();               public native AVFilterPad end_frame(End_frame end_frame);

        /**
         * Slice drawing callback. This is where a filter receives video data
         * and should do its processing.
         *
         * Input video pads only.
         */
        public static class Draw_slice extends FunctionPointer {
            static { load(); }
            public    Draw_slice(Pointer p) { super(p); }
            protected Draw_slice() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link, int y, int height, int slice_dir);
        }
        public native Draw_slice draw_slice();             public native AVFilterPad draw_slice(Draw_slice draw_slice);

        /**
         * Samples filtering callback. This is where a filter receives audio data
         * and should do its processing.
         *
         * Input audio pads only.
         */
        public static class Filter_samples extends FunctionPointer {
            static { load(); }
            public    Filter_samples(Pointer p) { super(p); }
            protected Filter_samples() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link, AVFilterBufferRef samplesref);
        }
        public native Filter_samples filter_samples();     public native AVFilterPad filter_samples(Filter_samples filter_samples);

        /**
         * Frame poll callback. This returns the number of immediately available
         * samples. It should return a positive value if the next request_frame()
         * is guaranteed to return one frame (with no delay).
         *
         * Defaults to just calling the source poll_frame() method.
         *
         * Output pads only.
         */
        public static class Poll_frame extends FunctionPointer {
            static { load(); }
            public    Poll_frame(Pointer p) { super(p); }
            protected Poll_frame() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterLink link);
        }
        public native Poll_frame poll_frame();             public native AVFilterPad poll_frame(Poll_frame poll_frame);

        /**
         * Frame request callback. A call to this should result in at least one
         * frame being output over the given link. This should return zero on
         * success, and another value on error.
         * See avfilter_request_frame() for the error codes with a specific
         * meaning.
         *
         * Output pads only.
         */
        public static class Request_frame extends FunctionPointer {
            static { load(); }
            public    Request_frame(Pointer p) { super(p); }
            protected Request_frame() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterLink link);
        }
        public native Request_frame request_frame();       public native AVFilterPad request_frame(Request_frame request_frame);

        /**
         * Link configuration callback.
         *
         * For output pads, this should set the following link properties:
         * video: width, height, sample_aspect_ratio, time_base
         * audio: sample_rate.
         *
         * This should NOT set properties such as format, channel_layout, etc which
         * are negotiated between filters by the filter system using the
         * query_formats() callback before this function is called.
         *
         * For input pads, this should check the properties of the link, and update
         * the filter's internal state as necessary.
         *
         * For both input and output pads, this should return zero on success,
         * and another value on error.
         */
        public static class Config_props extends FunctionPointer {
            static { load(); }
            public    Config_props(Pointer p) { super(p); }
            protected Config_props() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterLink link);
        }
        public native Config_props config_props();         public native AVFilterPad config_props(Config_props config_props);
    }

    /**
     * Helpers for query_formats() which set all links to the same list of
     * formats/layouts. If there are no links hooked to this filter, the list
     * of formats is freed.
     */
    public static native void avfilter_set_common_formats(AVFilterContext ctx, AVFilterFormats formats);
    public static native void avfilter_set_common_pixel_formats(AVFilterContext ctx, AVFilterFormats formats);
    public static native void avfilter_set_common_sample_formats(AVFilterContext ctx, AVFilterFormats formats);
    public static native void avfilter_set_common_channel_layouts(AVFilterContext ctx, AVFilterFormats formats);

    /**
     * Filter definition. This defines the pads a filter contains, and all the
     * callback functions used to interact with the filter.
     */
    public static class AVFilter extends Pointer {
        static { load(); }
        public AVFilter() { allocate(); }
        public AVFilter(int size) { allocateArray(size); }
        public AVFilter(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilter position(int position) {
            return (AVFilter)super.position(position);
        }

        @Cast("const char*")                             ///< filter name
        public native BytePointer name();                public native AVFilter name(BytePointer name);
                                                         ///< size of private data to allocate for the filter
        public native int priv_size();                   public native AVFilter priv_size(int priv_size);

        /**
         * Filter initialization function. Args contains the user-supplied
         * parameters. FIXME: maybe an AVOption-based system would be better?
         * opaque is data provided by the code requesting creation of the filter,
         * and is used to pass data to the filter.
         */
        public static class Init extends FunctionPointer {
            static { load(); }
            public    Init(Pointer p) { super(p); }
            protected Init() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterContext ctx, String args, Pointer opaque);
        }
        public native Init init();                       public native AVFilter init(Init init);

        /**
         * Filter uninitialization function. Should deallocate any memory held
         * by the filter, release any buffer references, etc. This does not need
         * to deallocate the AVFilterContext->priv memory itself.
         */
        public static class Uninit extends FunctionPointer {
            static { load(); }
            public    Uninit(Pointer p) { super(p); }
            protected Uninit() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterContext ctx);
        }
        public native Uninit uninit();                   public native AVFilter uninit(Uninit uninit);

        /**
         * Queries formats/layouts supported by the filter and its pads, and sets
         * the in_formats/in_chlayouts for links connected to its output pads,
         * and out_formats/out_chlayouts for links connected to its input pads.
         *
         * @return zero on success, a negative value corresponding to an
         * AVERROR code otherwise
         */
        public static class Query_formats extends FunctionPointer {
            static { load(); }
            public    Query_formats(Pointer p) { super(p); }
            protected Query_formats() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterContext c);
        }
        public native Query_formats query_formats();     public native AVFilter query_formats(Query_formats query_formats);

        @Const                                           ///< NULL terminated list of inputs. NULL if none
        public native AVFilterPad inputs();              public native AVFilter inputs(AVFilterPad inputs);
        @Const                                           ///< NULL terminated list of outputs. NULL if none
        public native AVFilterPad outputs();             public native AVFilter outputs(AVFilterPad outputs);

        /**
         * A description for the filter. You should use the
         * NULL_IF_CONFIG_SMALL() macro to define it.
         */
        @Cast("const char*")
        public native BytePointer description();         public native AVFilter description(BytePointer description);

        /**
         * Make the filter instance process a command.
         *
         * @param cmd    the command to process, for handling simplicity all commands must be alphanumeric only
         * @param arg    the argument for the command
         * @param res    a buffer with size res_size where the filter(s) can return a response. This must not change when the command is not supported.
         * @param flags  if AVFILTER_CMD_FLAG_FAST is set and the command would be
         *               time consuming then a filter should treat it like an unsupported command
         *
         * @returns >=0 on success otherwise an error code.
         *          AVERROR(ENOSYS) on unsupported commands
         */
        public static class Process_command extends FunctionPointer {
            static { load(); }
            public    Process_command(Pointer p) { super(p); }
            protected Process_command() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterContext c, String cmd, String arg, @Cast("char*") BytePointer res, int res_len, int flags);
        }
        public native Process_command process_command(); public native AVFilter process_command(Process_command process_command);
    }

    /** An instance of a filter */
    public static class AVFilterContext extends Pointer {
        static { load(); }
        public AVFilterContext() { allocate(); }
        public AVFilterContext(int size) { allocateArray(size); }
        public AVFilterContext(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterContext position(int position) {
            return (AVFilterContext)super.position(position);
        }

        @Const                                         ///< needed for av_log()
        public native  AVClass av_class();             public native AVFilterContext av_class(AVClass av_class);

                                                       ///< the AVFilter of which this is an instance
        public native AVFilter filter();               public native AVFilterContext filter(AVFilter filter);

        @Cast("char*")                                 ///< name of this filter instance
        public native BytePointer name();              public native AVFilterContext name(BytePointer name);


        @Cast("unsigned")                              ///< number of input pads
        public native int input_count();               public native AVFilterContext input_count(int input_count);
                                                       ///< array of input pads
        public native AVFilterPad input_pads();        public native AVFilterContext input_pads(AVFilterPad input_pads);
        @Cast("AVFilterLink**")                        ///< array of pointers to input links
        public native PointerPointer inputs();         public native AVFilterContext inputs(PointerPointer inputs);


        @Cast("unsigned")                              ///< number of output pads
        public native int output_count();              public native AVFilterContext output_count(int output_count);
                                                       ///< array of output pads
        public native AVFilterPad output_pads();       public native AVFilterContext output_pads(AVFilterPad output_pads);
        @Cast("AVFilterLink**")                        ///< array of pointers to output links
        public native PointerPointer outputs();        public native AVFilterContext outputs(PointerPointer outputs);

                                                       ///< private data for use by the filter
        public native Pointer priv();                  public native AVFilterContext priv(Pointer priv);

        public native AVFilterCommand command_queue(); public native AVFilterContext command_queue(AVFilterCommand command_queue);
    }

    @Opaque public static class AVFilterCommand extends Pointer {
        public AVFilterCommand() { }
        public AVFilterCommand(Pointer p) { super(p); }
    }
    @Opaque public static class AVFilterPool extends Pointer {
        public AVFilterPool() { }
        public AVFilterPool(Pointer p) { super(p); }
    }
    @Opaque public static class AVFilterChannelLayouts extends Pointer {
        public AVFilterChannelLayouts() { }
        public AVFilterChannelLayouts(Pointer p) { super(p); }
    }

    /**
     * A link between two filters. This contains pointers to the source and
     * destination filters between which this link exists, and the indexes of
     * the pads involved. In addition, this link also contains the parameters
     * which have been negotiated and agreed upon between the filter, such as
     * image dimensions, format, etc.
     */
    public static class AVFilterLink extends Pointer {
        static { load(); }
        public AVFilterLink() { allocate(); }
        public AVFilterLink(int size) { allocateArray(size); }
        public AVFilterLink(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterLink position(int position) {
            return (AVFilterLink)super.position(position);
        }
                                                         ///< source filter
        public native AVFilterContext src();             public native AVFilterLink src(AVFilterContext src);
                                                         ///< output pad on the source filter
        public native AVFilterPad srcpad();              public native AVFilterLink srcpad(AVFilterPad srcpad);

                                                         ///< dest filter
        public native AVFilterContext dst();             public native AVFilterLink dst(AVFilterContext dst);
                                                         ///< input pad on the dest filter
        public native AVFilterPad dstpad();              public native AVFilterLink dstpad(AVFilterPad dstpad);

        /** stage of the initialization of the link properties (dimensions, etc) */
        public static final int // enum {
                AVLINK_UNINIT = 0,    ///< not started
                AVLINK_STARTINIT = 1, ///< started, but incomplete
                AVLINK_INIT = 2;      ///< complete
        @MemberGetter public native int init_state();

        @Cast("AVMediaType")                             ///< filter media type
        public native int type();                        public native AVFilterLink type(int type);

        /* These parameters apply only to video */
                                                         ///< agreed upon image width
        public native int w();                           public native AVFilterLink w(int w);
                                                         ///< agreed upon image height
        public native int h();                           public native AVFilterLink h(int h);
        @ByRef                                           ///< agreed upon sample aspect ratio
        public native AVRational sample_aspect_ratio();  public native AVFilterLink sample_aspect_ratio(AVRational sample_aspect_ratio);
        /* These parameters apply only to audio */
        @Cast("uint64_t")                                ///< channel layout of current buffer (see libavutil/audioconvert.h)
        public native long channel_layout();             public native AVFilterLink channel_layout(long channel_layout);
                                                         ///< samples per second
        public native long sample_rate();                public native AVFilterLink sample_rate(long sample_rate);
                                                         ///< agreed upon media format
        public native int format();                      public native AVFilterLink format(int format);

        /**
         * Lists of formats and channel layouts supported by the input and output
         * filters respectively. These lists are used for negotiating the format
         * to actually be used, which will be loaded into the format and
         * channel_layout members, above, when chosen.
         *
         */
        public native AVFilterFormats in_formats();      public native AVFilterLink in_formats(AVFilterFormats in_formats);
        public native AVFilterFormats out_formats();     public native AVFilterLink out_formats(AVFilterFormats out_formats);

        /**
         * The buffer reference currently being sent across the link by the source
         * filter. This is used internally by the filter system to allow
         * automatic copying of buffers which do not have sufficient permissions
         * for the destination. This should not be accessed directly by the
         * filters.
         */
        public native AVFilterBufferRef src_buf();       public native AVFilterLink src_buf(AVFilterBufferRef src_buf);

        public native AVFilterBufferRef cur_buf();       public native AVFilterLink cur_buf(AVFilterBufferRef cur_buf);
        public native AVFilterBufferRef out_buf();       public native AVFilterLink out_buf(AVFilterBufferRef out_buf);

        /**
         * Define the time base used by the PTS of the frames/samples
         * which will pass through this link.
         * During the configuration stage, each filter is supposed to
         * change only the output timebase, while the timebase of the
         * input link is assumed to be an unchangeable property.
         */
        @ByRef                                           ///< agreed upon sample aspect ratio
        public native AVRational time_base();            public native AVFilterLink time_base(AVRational time_base);

        /*****************************************************************
         * All fields below this line are not part of the public API. They
         * may not be used outside of libavfilter and can be changed and
         * removed at will.
         * New public fields should be added right above.
         *****************************************************************
         */
        /**
         * Lists of channel layouts and sample rates used for automatic
         * negotiation.
         */
        public native AVFilterFormats in_samplerates();  public native AVFilterLink in_samplerates(AVFilterFormats in_samplerates);
        public native AVFilterFormats out_samplerates(); public native AVFilterLink out_samplerates(AVFilterFormats out_samplerates);

        public native AVFilterChannelLayouts in_channel_layouts();  
        public native AVFilterLink in_channel_layouts(AVFilterChannelLayouts in_channel_layouts);
        public native AVFilterChannelLayouts out_channel_layouts(); 
        public native AVFilterLink out_channel_layouts(AVFilterChannelLayouts out_channel_layouts);

        public native AVFilterPool pool();               public native AVFilterLink pool(AVFilterPool pool);

        /**
         * Graph the filter belongs to.
         */
        public native AVFilterGraph graph();             public native AVFilterLink graph(AVFilterGraph graph);

        /**
         * Current timestamp of the link, as defined by the most recent
         * frame(s), in AV_TIME_BASE units.
         */
        public native long current_pts();                public native AVFilterLink current_pts(long current_pts);

        /**
         * Index in the age array.
         */
        public native int age_index();                   public native AVFilterLink age_index(int age_index);
    }

    /**
     * Link two filters together.
     *
     * @param src    the source filter
     * @param srcpad index of the output pad on the source filter
     * @param dst    the destination filter
     * @param dstpad index of the input pad on the destination filter
     * @return       zero on success
     */
    public static native int avfilter_link(AVFilterContext src, @Cast("unsigned") int srcpad,
            AVFilterContext dst, @Cast("unsigned") int dstpad);

    /**
     * Free the link in *link, and set its pointer to NULL.
     */
    public static native void avfilter_link_free(@ByPtrPtr AVFilterLink link);

    /**
     * Negotiate the media format, dimensions, etc of all inputs to a filter.
     *
     * @param filter the filter to negotiate the properties for its inputs
     * @return       zero on successful negotiation
     */
    public static native int avfilter_config_links(AVFilterContext filter);

    /**
     * Request a picture buffer with a specific set of permissions.
     *
     * @param link  the output link to the filter from which the buffer will
     *              be requested
     * @param perms the required access permissions
     * @param w     the minimum width of the buffer to allocate
     * @param h     the minimum height of the buffer to allocate
     * @return      A reference to the buffer. This must be unreferenced with
     *              avfilter_unref_buffer when you are finished with it.
     */
    public static native AVFilterBufferRef avfilter_get_video_buffer(AVFilterLink link, int perms, int w, int h);

    /**
     * Create a buffer reference wrapped around an already allocated image
     * buffer.
     *
     * @param data pointers to the planes of the image to reference
     * @param linesize linesizes for the planes of the image to reference
     * @param perms the required access permissions
     * @param w the width of the image specified by the data and linesize arrays
     * @param h the height of the image specified by the data and linesize arrays
     * @param format the pixel format of the image specified by the data and linesize arrays
     */
    public static native AVFilterBufferRef avfilter_get_video_buffer_ref_from_arrays(@Cast("uint8_t**") PointerPointer data,
            int[/*4*/] linesize, int perms, int w, int h, @Cast("PixelFormat") int format);

    /**
     * Create an audio buffer reference wrapped around an already
     * allocated samples buffer.
     *
     * @param data           pointers to the samples plane buffers
     * @param linesize       linesize for the samples plane buffers
     * @param perms          the required access permissions
     * @param nb_samples     number of samples per channel
     * @param sample_fmt     the format of each sample in the buffer to allocate
     * @param channel_layout the channel layout of the buffer
     */
    public static native AVFilterBufferRef avfilter_get_audio_buffer_ref_from_arrays(@Cast("uint8_t**") PointerPointer data,
            int linesize, int perms, int nb_samples, @Cast("AVSampleFormat") int sample_fmt, @Cast("uint64_t") long channel_layout);

    /**
     * Request an input frame from the filter at the other end of the link.
     *
     * @param link the input link
     * @return     zero on success or a negative error code; in particular:
     *             AVERROR_EOF means that the end of frames have been reached;
     *             AVERROR(EAGAIN) means that no frame could be immediately
     *             produced.
     */
    public static native int avfilter_request_frame(AVFilterLink link);

    /**
     * Poll a frame from the filter chain.
     *
     * @param  link the input link
     * @return the number of immediately available frames, a negative
     * number in case of error
     */
    public static native int avfilter_poll_frame(AVFilterLink link);

    /**
     * Notify the next filter of the start of a frame.
     *
     * @param link   the output link the frame will be sent over
     * @param picref A reference to the frame about to be sent. The data for this
     *               frame need only be valid once draw_slice() is called for that
     *               portion. The receiving filter will free this reference when
     *               it no longer needs it.
     */
    public static native void avfilter_start_frame(AVFilterLink link, AVFilterBufferRef picref);

    /**
     * Notify the next filter that the current frame has finished.
     *
     * @param link the output link the frame was sent over
     */
    public static native void avfilter_end_frame(AVFilterLink link);

    /**
     * Send a slice to the next filter.
     *
     * Slices have to be provided in sequential order, either in
     * top-bottom or bottom-top order. If slices are provided in
     * non-sequential order the behavior of the function is undefined.
     *
     * @param link the output link over which the frame is being sent
     * @param y    offset in pixels from the top of the image for this slice
     * @param h    height of this slice in pixels
     * @param slice_dir the assumed direction for sending slices,
     *             from the top slice to the bottom slice if the value is 1,
     *             from the bottom slice to the top slice if the value is -1,
     *             for other values the behavior of the function is undefined.
     */
    public static native void avfilter_draw_slice(AVFilterLink link, int y, int h, int slice_dir);

    public static final int
            AVFILTER_CMD_FLAG_ONE  = 1, ///< Stop once a filter understood the command (for target=all for example), fast filters are favored automatically
            AVFILTER_CMD_FLAG_FAST = 2; ///< Only execute command when its fast (like a video out that supports contrast adjustment in hw)

    /**
     * Make the filter instance process a command.
     * It is recommended to use avfilter_graph_send_command().
     */
    public static native int avfilter_process_command(AVFilterContext filter, String cmd, String arg,
            @Cast("char*") byte[] res, int res_len, int flags);

    /** Initialize the filter system. Register all builtin filters. */
    public static native void avfilter_register_all();

    /** Uninitialize the filter system. Unregister all filters. */
    public static native void avfilter_uninit();

    /**
     * Register a filter. This is only needed if you plan to use
     * avfilter_get_by_name later to lookup the AVFilter structure by name. A
     * filter can still by instantiated with avfilter_open even if it is not
     * registered.
     *
     * @param filter the filter to register
     * @return 0 if the registration was successful, a negative value
     * otherwise
     */
    public static native int avfilter_register(AVFilter filter);

    /**
     * Get a filter definition matching the given name.
     *
     * @param name the filter name to find
     * @return     the filter definition, if any matching one is registered.
     *             NULL if none found.
     */
    public static native AVFilter avfilter_get_by_name(String name);

    /**
     * If filter is NULL, returns a pointer to the first registered filter pointer,
     * if filter is non-NULL, returns the next pointer after filter.
     * If the returned pointer points to NULL, the last registered filter
     * was already reached.
     */
    public static native @ByPtrPtr AVFilter av_filter_next(@ByPtrPtr AVFilter filter);

    /**
     * Create a filter instance.
     *
     * @param filter_ctx put here a pointer to the created filter context
     * on success, NULL on failure
     * @param filter    the filter to create an instance of
     * @param inst_name Name to give to the new instance. Can be NULL for none.
     * @return >= 0 in case of success, a negative error code otherwise
     */
    public static native int avfilter_open(@ByPtrPtr AVFilterContext filter_ctx, AVFilter filter, String inst_name);

    /**
     * Initialize a filter.
     *
     * @param filter the filter to initialize
     * @param args   A string of parameters to use when initializing the filter.
     *               The format and meaning of this string varies by filter.
     * @param opaque Any extra non-string data needed by the filter. The meaning
     *               of this parameter varies by filter.
     * @return       zero on success
     */
    public static native int avfilter_init_filter(AVFilterContext filter, String args, Pointer opaque);

    /**
     * Free a filter context.
     *
     * @param filter the filter to free
     */
    public static native void avfilter_free(AVFilterContext filter);

    /**
     * Insert a filter in the middle of an existing link.
     *
     * @param link the link into which the filter should be inserted
     * @param filt the filter to be inserted
     * @param filt_srcpad_idx the input pad on the filter to connect
     * @param filt_dstpad_idx the output pad on the filter to connect
     * @return     zero on success
     */
    public static native int avfilter_insert_filter(AVFilterLink link, AVFilterContext filt,
            @Cast("unsigned") int filt_srcpad_idx, @Cast("unsigned") int filt_dstpad_idx);

    /**
     * Insert a new pad.
     *
     * @param idx Insertion point. Pad is inserted at the end if this point
     *            is beyond the end of the list of pads.
     * @param count Pointer to the number of pads in the list
     * @param padidx_off Offset within an AVFilterLink structure to the element
     *                   to increment when inserting a new pad causes link
     *                   numbering to change
     * @param pads Pointer to the pointer to the beginning of the list of pads
     * @param links Pointer to the pointer to the beginning of the list of links
     * @param newpad The new pad to add. A copy is made when adding.
     */
    public static native void avfilter_insert_pad(@Cast("unsigned") int idx, @Cast("unsigned*") int[] count,
            @Cast("size_t") long padidx_off, @Cast("AVFilterPad**") PointerPointer pads,
            @Cast("AVFilterLink***") PointerPointer links, AVFilterPad newpad);

    /** Insert a new input pad for the filter. */
    public static native void avfilter_insert_inpad(AVFilterContext f, @Cast("unsigned") int index, AVFilterPad p);

    /** Insert a new output pad for the filter. */
    public static native  void avfilter_insert_outpad(AVFilterContext f, @Cast("unsigned") int index, AVFilterPad p);


    // #include "buffersink.h"
    /**
     * @file
     * memory buffer sink API for audio and video
     */

    /**
     * Struct to use for initializing a buffersink context.
     */
    public static class AVBufferSinkParams extends Pointer {
        static { load(); }
        public AVBufferSinkParams() { allocate(); }
        public AVBufferSinkParams(int size) { allocateArray(size); }
        public AVBufferSinkParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVBufferSinkParams position(int position) {
            return (AVBufferSinkParams)super.position(position);
        }

        @Cast("const PixelFormat*")            ///< list of allowed pixel formats, terminated by PIX_FMT_NONE
        public native IntPointer pixel_fmts(); public native AVBufferSinkParams pixel_fmts(IntPointer pixel_fmts);
    }

    /**
     * Create an AVBufferSinkParams structure.
     *
     * Must be freed with av_free().
     */
    public static native AVBufferSinkParams av_buffersink_params_alloc();

    /**
     * Struct to use for initializing an abuffersink context.
     */
    public static class AVABufferSinkParams extends Pointer {
        static { load(); }
        public AVABufferSinkParams() { allocate(); }
        public AVABufferSinkParams(int size) { allocateArray(size); }
        public AVABufferSinkParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVABufferSinkParams position(int position) {
            return (AVABufferSinkParams)super.position(position);
        }

        @Cast("const AVSampleFormat*")               ///< list of allowed sample formats, terminated by AV_SAMPLE_FMT_NONE
        public native IntPointer sample_fmts();      public native AVABufferSinkParams sample_fmts(IntPointer sample_fmts);
        @Const                                       ///< list of allowed channel layouts, terminated by -1
        public native LongPointer channel_layouts(); public native AVABufferSinkParams channel_layouts(LongPointer channel_layouts);
    }

    /**
     * Create an AVABufferSinkParams structure.
     *
     * Must be freed with av_free().
     */
    public static native AVABufferSinkParams av_abuffersink_params_alloc();

    /**
     * Tell av_buffersink_get_buffer_ref() to read video/samples buffer
     * reference, but not remove it from the buffer. This is useful if you
     * need only to read a video/samples buffer, without to fetch it.
     */
    public static final int AV_BUFFERSINK_FLAG_PEEK = 1;

    /**
     * Tell av_buffersink_get_buffer_ref() not to request a frame from its input.
     * If a frame is already buffered, it is read (and removed from the buffer),
     * but if no frame is present, return AVERROR(EAGAIN).
     */
    public static final int AV_BUFFERSINK_FLAG_NO_REQUEST = 2;

    /**
     * Get an audio/video buffer data from buffer_sink and put it in bufref.
     *
     * This function works with both audio and video buffer sinks.
     *
     * @param buffer_sink pointer to a buffersink or abuffersink context
     * @param flags a combination of AV_BUFFERSINK_FLAG_* flags
     * @return >= 0 in case of success, a negative AVERROR code in case of
     * failure
     */
    public static native int av_buffersink_get_buffer_ref(AVFilterContext buffer_sink,
            @ByPtrPtr AVFilterBufferRef bufref, int flags);


    /**
     * Get the number of immediately available frames.
     */
    public static native int av_buffersink_poll_frame(AVFilterContext ctx);

    /**
     * Get a buffer with filtered data from sink and put it in buf.
     *
     * @param sink pointer to a context of a buffersink or abuffersink AVFilter.
     * @param buf pointer to the buffer will be written here if buf is non-NULL. buf
     *            must be freed by the caller using avfilter_unref_buffer().
     *            Buf may also be NULL to query whether a buffer is ready to be
     *            output.
     *
     * @return >= 0 in case of success, a negative AVERROR code in case of
     *         failure.
     */
    public static native int av_buffersink_read(AVFilterContext sink,
            @ByPtrPtr AVFilterBufferRef buf);

    /**
     * Same as av_buffersink_read, but with the ability to specify the number of
     * samples read. This function is less efficient than av_buffersink_read(),
     * because it copies the data around.
     *
     * @param sink pointer to a context of the abuffersink AVFilter.
     * @param buf pointer to the buffer will be written here if buf is non-NULL. buf
     *            must be freed by the caller using avfilter_unref_buffer(). buf
     *            will contain exactly nb_samples audio samples, except at the end
     *            of stream, when it can contain less than nb_samples.
     *            Buf may also be NULL to query whether a buffer is ready to be
     *            output.
     *
     * @warning do not mix this function with av_buffersink_read(). Use only one or
     * the other with a single sink, not both.
     */
    public static native int av_buffersink_read_samples(AVFilterContext ctx,
            @ByPtrPtr AVFilterBufferRef buf, int nb_samples);


    // #include "buffersrc.h"
    /**
     * @file
     * Memory buffer source API.
     */

    public static final int
        /**
         * Do not check for format changes.
         */
        AV_BUFFERSRC_FLAG_NO_CHECK_FORMAT = 1,

        /**
         * Do not copy buffer data.
         */
        AV_BUFFERSRC_FLAG_NO_COPY = 2;

    /**
     * Add buffer data in picref to buffer_src.
     *
     * @param buffer_src  pointer to a buffer source context
     * @param picref      a buffer reference, or NULL to mark EOF
     * @param flags       a combination of AV_BUFFERSRC_FLAG_*
     * @return            >= 0 in case of success, a negative AVERROR code
     *                    in case of failure
     */
    public static native int av_buffersrc_add_ref(AVFilterContext buffer_src, AVFilterBufferRef picref, int flags);

    /**
     * Get the number of failed requests.
     *
     * A failed request is when the request_frame method is called while no
     * frame is present in the buffer.
     * The number is reset when a frame is added.
     */
    public static native @Cast("unsigned") int av_buffersrc_get_nb_failed_requests(AVFilterContext buffer_src);

    /**
     * Add a buffer to the filtergraph s.
     *
     * @param buf buffer containing frame data to be passed down the filtergraph.
     * This function will take ownership of buf, the user must not free it.
     * A NULL buf signals EOF -- i.e. no more frames will be sent to this filter.
     */
    public static native int av_buffersrc_buffer(AVFilterContext s, AVFilterBufferRef buf);

    /**
     * Add a frame to the buffer source.
     *
     * @param s an instance of the buffersrc filter.
     * @param frame frame to be added.
     *
     * @warning frame data will be memcpy()ed, which may be a big performance
     *          hit. Use av_buffersrc_buffer() to avoid copying the data.
     */
    public static native int av_buffersrc_write_frame(AVFilterContext s, AVFrame frame);


    // #include "avcodec.h"
    /**
     * @file
     * libavcodec/libavfilter gluing utilities
     *
     * This should be included in an application ONLY if the installed
     * libavfilter has been compiled with libavcodec support, otherwise
     * symbols defined below will not be available.
     */


    /**
     * Copy the frame properties of src to dst, without copying the actual
     * image data.
     *
     * @return 0 on success, a negative number on error.
     */
    public static native int avfilter_copy_frame_props(AVFilterBufferRef dst, AVFrame src);

    /**
     * Copy the frame properties and data pointers of src to dst, without copying
     * the actual data.
     *
     * @return 0 on success, a negative number on error.
     */
    public static native int avfilter_copy_buf_props(AVFrame dst, AVFilterBufferRef src);

    /**
     * Create and return a picref reference from the data and properties
     * contained in frame.
     *
     * @param perms permissions to assign to the new buffer reference
     */
    public static native AVFilterBufferRef avfilter_get_video_buffer_ref_from_frame(AVFrame frame, int perms);


    /**
     * Create and return a picref reference from the data and properties
     * contained in frame.
     *
     * @param perms permissions to assign to the new buffer reference
     */
    public static native AVFilterBufferRef avfilter_get_audio_buffer_ref_from_frame(AVFrame frame, int perms);

    /**
     * Fill an AVFrame with the information stored in samplesref.
     *
     * @param frame an already allocated AVFrame
     * @param samplesref an audio buffer reference
     * @return 0 in case of success, a negative AVERROR code in case of
     * failure
     */
    public static native int avfilter_fill_frame_from_audio_buffer_ref(AVFrame frame, AVFilterBufferRef samplesref);

    /**
     * Fill an AVFrame with the information stored in picref.
     *
     * @param frame an already allocated AVFrame
     * @param picref a video buffer reference
     * @return 0 in case of success, a negative AVERROR code in case of
     * failure
     */
    public static native int avfilter_fill_frame_from_video_buffer_ref(AVFrame frame, AVFilterBufferRef picref);

    /**
     * Fill an AVFrame with information stored in ref.
     *
     * @param frame an already allocated AVFrame
     * @param ref a video or audio buffer reference
     * @return 0 in case of success, a negative AVERROR code in case of
     * failure
     */
    public static native int avfilter_fill_frame_from_buffer_ref(AVFrame frame, AVFilterBufferRef ref);

    /**
     * Add frame data to buffer_src.
     *
     * @param buffer_src  pointer to a buffer source context
     * @param frame       a frame, or NULL to mark EOF
     * @param flags       a combination of AV_BUFFERSRC_FLAG_*
     * @return            >= 0 in case of success, a negative AVERROR code
     *                    in case of failure
     */
    public static native int av_buffersrc_add_frame(AVFilterContext buffer_src, AVFrame frame, int flags);

    /**
     * Add frame data to buffer_src.
     *
     * @param buffer_src pointer to a buffer source context
     * @param flags a combination of AV_VSRC_BUF_FLAG_* flags
     * @return >= 0 in case of success, a negative AVERROR code in case of
     * failure
     */
    public static native int av_vsrc_buffer_add_frame(AVFilterContext buffer_src, AVFrame frame, int flags);


    // #include "filtergraph.h"
    public static class AVFilterGraph extends Pointer {
        static { load(); }
        public AVFilterGraph() { allocate(); }
        public AVFilterGraph(int size) { allocateArray(size); }
        public AVFilterGraph(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterGraph position(int position) {
            return (AVFilterGraph)super.position(position);
        }

        @Cast("unsigned")
        public native int filter_count();           public native AVFilterGraph filter_count(int filter_count);
        @Cast("AVFilterContext**")
        public native PointerPointer filters();     public native AVFilterGraph filters(PointerPointer filters);
        @MemberGetter public native AVFilterContext filters(int i);
        @Cast("char*")                              ///< sws options to use for the auto-inserted scale filters
        public native BytePointer scale_sws_opts(); public native AVFilterGraph scale_sws_opts(BytePointer scale_sws_opts);
    }

    /**
     * Allocate a filter graph.
     */
    public static native AVFilterGraph avfilter_graph_alloc();

    /**
     * Get a filter instance with name name from graph.
     *
     * @return the pointer to the found filter instance or NULL if it
     * cannot be found.
     */
    public static native AVFilterContext avfilter_graph_get_filter(AVFilterGraph graph, @Cast("char*") String name);

    /**
     * Add an existing filter instance to a filter graph.
     *
     * @param graphctx  the filter graph
     * @param filter the filter to be added
     */
    public static native int avfilter_graph_add_filter(AVFilterGraph graphctx, AVFilterContext filter);

    /**
     * Create and add a filter instance into an existing graph.
     * The filter instance is created from the filter filt and inited
     * with the parameters args and opaque.
     *
     * In case of success put in *filt_ctx the pointer to the created
     * filter instance, otherwise set *filt_ctx to NULL.
     *
     * @param name the instance name to give to the created filter instance
     * @param graph_ctx the filter graph
     * @return a negative AVERROR error code in case of failure, a non
     * negative value otherwise
     */
    public static native int avfilter_graph_create_filter(@ByPtrPtr AVFilterContext filt_ctx, AVFilter filt,
            String name, String args, Pointer opaque, AVFilterGraph graph_ctx);

    /**
     * Enable or disable automatic format conversion inside the graph.
     *
     * Note that format conversion can still happen inside explicitly inserted
     * scale and aconvert filters.
     *
     * @param flags  any of the AVFILTER_AUTO_CONVERT_* constants
     */
    public static native void avfilter_graph_set_auto_convert(AVFilterGraph graph, @Cast("unsigned") int flags);

    public static final int
        AVFILTER_AUTO_CONVERT_ALL  =  0, /**< all automatic conversions enabled */
        AVFILTER_AUTO_CONVERT_NONE = -1; /**< all automatic conversions disabled */

    /**
     * Check validity and configure all the links and formats in the graph.
     *
     * @param graphctx the filter graph
     * @param log_ctx context used for logging
     * @return 0 in case of success, a negative AVERROR code otherwise
     */
    public static native int avfilter_graph_config(AVFilterGraph graphctx, Pointer log_ctx);

    /**
     * Free a graph, destroy its links, and set *graph to NULL.
     * If *graph is NULL, do nothing.
     */
    public static native void avfilter_graph_free(@ByPtrPtr AVFilterGraph graph);

    /**
     * A linked-list of the inputs/outputs of the filter chain.
     *
     * This is mainly useful for avfilter_graph_parse() / avfilter_graph_parse2(),
     * where it is used to communicate open (unlinked) inputs and outputs from and
     * to the caller.
     * This struct specifies, per each not connected pad contained in the graph, the
     * filter context and the pad index required for establishing a link.
     */
    public static class AVFilterInOut extends Pointer {
        static { load(); }
        public AVFilterInOut() { allocate(); }
        public AVFilterInOut(int size) { allocateArray(size); }
        public AVFilterInOut(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterInOut position(int position) {
            return (AVFilterInOut)super.position(position);
        }

        /** unique name for this input/output in the list */
        @Cast("char*")
        public native BytePointer name();           public native AVFilterInOut name(BytePointer name);

        /** filter context associated to this input/output */
        public native AVFilterContext filter_ctx(); public native AVFilterInOut filter_ctx(AVFilterContext filter_ctx);

        /** index of the filt_ctx pad to use for linking */
        public native int pad_idx();                public native AVFilterInOut pad_idx(int pad_idx);

        /** next input/input in the list, NULL if this is the last */
        public native AVFilterInOut next();         public native AVFilterInOut next(AVFilterInOut next);
    }

    /**
     * Allocate a single AVFilterInOut entry.
     * Must be freed with avfilter_inout_free().
     * @return allocated AVFilterInOut on success, NULL on failure.
     */
    public static native AVFilterInOut avfilter_inout_alloc();

    /**
     * Free the supplied list of AVFilterInOut and set *inout to NULL.
     * If *inout is NULL, do nothing.
     */
    public static native void avfilter_inout_free(@ByPtrPtr AVFilterInOut inout);

    /**
     * Add a graph described by a string to a graph.
     *
     * @param graph   the filter graph where to link the parsed graph context
     * @param filters string to be parsed
     * @param inputs  pointer to a linked list to the inputs of the graph, may be NULL.
     *                If non-NULL, *inputs is updated to contain the list of open inputs
     *                after the parsing, should be freed with avfilter_inout_free().
     * @param outputs pointer to a linked list to the outputs of the graph, may be NULL.
     *                If non-NULL, *outputs is updated to contain the list of open outputs
     *                after the parsing, should be freed with avfilter_inout_free().
     * @return non negative on success, a negative AVERROR code on error
     */
    public static native int avfilter_graph_parse(AVFilterGraph graph, String filters,
            @ByPtrPtr AVFilterInOut inputs, @ByPtrPtr AVFilterInOut outputs, Pointer log_ctx);

    /**
     * Add a graph described by a string to a graph.
     *
     * @param[in]  graph   the filter graph where to link the parsed graph context
     * @param[in]  filters string to be parsed
     * @param[out] inputs  a linked list of all free (unlinked) inputs of the
     *                     parsed graph will be returned here. It is to be freed
     *                     by the caller using avfilter_inout_free().
     * @param[out] outputs a linked list of all free (unlinked) outputs of the
     *                     parsed graph will be returned here. It is to be freed by the
     *                     caller using avfilter_inout_free().
     * @return zero on success, a negative AVERROR code on error
     *
     * @note the difference between avfilter_graph_parse2() and
     * avfilter_graph_parse() is that in avfilter_graph_parse(), the caller provides
     * the lists of inputs and outputs, which therefore must be known before calling
     * the function. On the other hand, avfilter_graph_parse2() \em returns the
     * inputs and outputs that are left unlinked after parsing the graph and the
     * caller then deals with them. Another difference is that in
     * avfilter_graph_parse(), the inputs parameter describes inputs of the
     * <em>already existing</em> part of the graph; i.e. from the point of view of
     * the newly created part, they are outputs. Similarly the outputs parameter
     * describes outputs of the already existing filters, which are provided as
     * inputs to the parsed filters.
     * avfilter_graph_parse2() takes the opposite approach -- it makes no reference
     * whatsoever to already existing parts of the graph and the inputs parameter
     * will on return contain inputs of the newly parsed part of the graph.
     * Analogously the outputs parameter will contain outputs of the newly created
     * filters.
     */
    public static native int avfilter_graph_parse2(AVFilterGraph graph, String filters,
            @ByPtrPtr AVFilterInOut inputs, @ByPtrPtr AVFilterInOut outputs);


    /**
     * Send a command to one or more filter instances.
     *
     * @param graph  the filter graph
     * @param target the filter(s) to which the command should be sent
     *               "all" sends to all filters
     *               otherwise it can be a filter or filter instance name
     *               which will send the command to all matching filters.
     * @param cmd    the command to sent, for handling simplicity all commands must be alphanumeric only
     * @param arg    the argument for the command
     * @param res    a buffer with size res_size where the filter(s) can return a response.
     *
     * @returns >=0 on success otherwise an error code.
     *              AVERROR(ENOSYS) on unsupported commands
     */
    public static native int avfilter_graph_send_command(AVFilterGraph graph, String target,
            String cmd, String arg, @Cast("char*") byte[] res, int res_len, int flags);

    /**
     * Queue a command for one or more filter instances.
     *
     * @param graph  the filter graph
     * @param target the filter(s) to which the command should be sent
     *               "all" sends to all filters
     *               otherwise it can be a filter or filter instance name
     *               which will send the command to all matching filters.
     * @param cmd    the command to sent, for handling simplicity all commands must be alphanummeric only
     * @param arg    the argument for the command
     * @param ts     time at which the command should be sent to the filter
     *
     * @note As this executes commands after this function returns, no return code
     *       from the filter is provided, also AVFILTER_CMD_FLAG_ONE is not supported.
     */
    public static native int avfilter_graph_queue_command(AVFilterGraph graph, String target,
            String cmd, String arg, int flags, double ts);


    /**
     * Dump a graph into a human-readable string representation.
     *
     * @param graph    the graph to dump
     * @param options  formatting options; currently ignored
     * @return  a string, or NULL in case of memory allocation failure;
     *          the string must be freed using av_free
     */
    public static native @Cast("char*") BytePointer avfilter_graph_dump(AVFilterGraph graph, String options);

    /**
     * Request a frame on the oldest sink link.
     *
     * If the request returns AVERROR_EOF, try the next.
     *
     * Note that this function is not meant to be the sole scheduling mechanism
     * of a filtergraph, only a convenience function to help drain a filtergraph
     * in a balanced way under normal circumstances.
     *
     * @return  the return value of avfilter_request_frame,
     *          or AVERROR_EOF of all links returned AVERROR_EOF.
     */
    public static native int avfilter_graph_request_oldest(AVFilterGraph graph);

}
