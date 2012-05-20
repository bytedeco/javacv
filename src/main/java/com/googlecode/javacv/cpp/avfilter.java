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
 * This file was derived from avfilter.h include file from
 * FFmpeg 0.6.1, which are covered by the following copyright notice:
 *
 * filter layer
 * copyright (c) 2007 Bobby Bingham
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
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.avutil.*;

/**
 *
 * @author Samuel Audet
 */
@Platform(value="linux", define="__STDC_CONSTANT_MACROS", cinclude="<libavfilter/avfilter.h>",
    includepath=genericIncludepath, linkpath=genericLinkpath, link={"avfilter@.1", "swscale@.0", "avcodec@.52", "avutil@.50"})
public class avfilter {
    static { load(avcodec.class); load(swscale.class); load(); }

    public static final int LIBAVFILTER_VERSION_MAJOR = 1;
    public static final int LIBAVFILTER_VERSION_MINOR = 19;
    public static final int LIBAVFILTER_VERSION_MICRO = 0;

    public static final int    LIBAVFILTER_VERSION_INT = AV_VERSION_INT(LIBAVFILTER_VERSION_MAJOR,
                                                                        LIBAVFILTER_VERSION_MINOR,
                                                                        LIBAVFILTER_VERSION_MICRO);
    public static final String LIBAVFILTER_VERSION     = AV_VERSION(LIBAVFILTER_VERSION_MAJOR,
                                                                    LIBAVFILTER_VERSION_MINOR,
                                                                    LIBAVFILTER_VERSION_MICRO);
    public static final int    LIBAVFILTER_BUILD       = LIBAVFILTER_VERSION_INT;


    public static native int avfilter_version();
    public static native String avfilter_configuration();
    public static native String avfilter_license();


    public static class AVFilterPic extends Pointer {
        static { load(); }
        public AVFilterPic() { allocate(); }
        public AVFilterPic(int size) { allocateArray(size); }
        public AVFilterPic(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterPic position(int position) {
            return (AVFilterPic)super.position(position);
        }

        @Cast("uint8_t*") // [4]
        public native BytePointer data(int i);    public native AVFilterPic data(int i, BytePointer data);
        public native int/*[4]*/ linesize(int i); public native AVFilterPic linesize(int i, int linesize);
        @Cast("PixelFormat")
        public native int format();               public native AVFilterPic format(int format);

        public native int refcount();             public native AVFilterPic refcount(int refcount);

        public native Pointer priv();             public native AVFilterPic priv(Pointer priv);

        public static class Free extends FunctionPointer {
            static { load(); }
            public native void call(AVFilterPic pic);
        }
        public native Free free();                public native AVFilterPic free(Free free);

        public native int w();                    public native AVFilterPic w(int w);
        public native int h();                    public native AVFilterPic h(int h);
    }

    public static class AVFilterPicRef extends Pointer {
        static { load(); }
        public AVFilterPicRef() { allocate(); }
        public AVFilterPicRef(int size) { allocateArray(size); }
        public AVFilterPicRef(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public AVFilterPicRef position(int position) {
            return (AVFilterPicRef)super.position(position);
        }

        public native AVFilterPic pic();          public native AVFilterPicRef pic(AVFilterPic pic);
        @Cast("uint8_t*") // [4]
        public native BytePointer data(int i);    public native AVFilterPicRef data(int i, BytePointer data);
        public native int/*[4]*/ linesize(int i); public native AVFilterPicRef linesize(int i, int linesize);
        public native int w();                    public native AVFilterPicRef w(int w);
        public native int h();                    public native AVFilterPicRef h(int h);

        public native long pts();                 public native AVFilterPicRef pts(long pts);
        public native long pos();                 public native AVFilterPicRef pos(long pos);

        @ByRef
        public native AVRational pixel_aspect();  public native AVFilterPicRef pixel_aspect(AVRational pixel_aspect);

        public native int perms();                public native AVFilterPicRef perms(int perms);
        public static final int
                AV_PERM_READ     = 0x01,
                AV_PERM_WRITE    = 0x02,
                AV_PERM_PRESERVE = 0x04,
                AV_PERM_REUSE    = 0x08,
                AV_PERM_REUSE2   = 0x10;
    }

    public static native AVFilterPicRef avfilter_ref_pic(AVFilterPicRef ref, int pmask);
    public static native void avfilter_unref_pic(AVFilterPicRef ref);

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

        public native int format_count();    public native AVFilterFormats format_count(int format_count);
        @Cast("PixelFormat*")
        public native IntPointer formats();  public native AVFilterFormats formats(IntPointer formats);

        public native int refcount();        public native AVFilterFormats refcount(int refcount);
        @Cast("AVFilterFormats***")
        public native PointerPointer refs(); public native AVFilterFormats refs(PointerPointer refs);
    }

    public static native AVFilterFormats avfilter_make_format_list(@Cast("PixelFormat*") int[] pix_fmts);
    public static native int avfilter_add_colorspace(@ByPtrPtr AVFilterFormats avff, @Cast("PixelFormat") int pix_fmt);
    public static native AVFilterFormats avfilter_all_colorspaces();
    public static native AVFilterFormats avfilter_merge_formats(AVFilterFormats a, AVFilterFormats b);
    public static native void avfilter_formats_ref(AVFilterFormats formats, @ByPtrPtr AVFilterFormats ref);
    public static native void avfilter_formats_unref(@ByPtrPtr AVFilterFormats ref);
    public static native void avfilter_formats_changeref(@ByPtrPtr AVFilterFormats oldref,
            @ByPtrPtr AVFilterFormats newref);

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

        @Cast("const char*")
        public native BytePointer name();   public native AVFilterPad name(BytePointer name);
        @Cast("AVMediaType")
        public native int type();           public native AVFilterPad type(int type);
        public native int min_perms();      public native AVFilterPad min_perms(int min_perms);
        public native int rej_perms();      public native AVFilterPad rej_perms(int rej_perms);

        public static class Start_frame extends FunctionPointer {
            static { load(); }
            public    Start_frame(Pointer p) { super(p); }
            protected Start_frame() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link, AVFilterPicRef picref);
        }
        public native Start_frame start_frame();           public native AVFilterPad start_frame(Start_frame start_frame);

        public static class Get_video_buffer extends FunctionPointer {
            static { load(); }
            public    Get_video_buffer(Pointer p) { super(p); }
            protected Get_video_buffer() { allocate(); }
            protected final native void allocate();
            public native AVFilterPicRef callback(AVFilterLink link, int perms, int w, int h);
        }
        public native Get_video_buffer get_video_buffer(); public native AVFilterPad get_video_buffer(Get_video_buffer get_video_buffer);

        public static class End_frame extends FunctionPointer {
            static { load(); }
            public    End_frame(Pointer p) { super(p); }
            protected End_frame() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link);
        }
        public native End_frame end_frame();               public native AVFilterPad end_frame(End_frame end_frame);

        public static class Draw_slice extends FunctionPointer {
            static { load(); }
            public    Draw_slice(Pointer p) { super(p); }
            protected Draw_slice() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterLink link, int y, int height, int slice_dir);
        }
        public native Draw_slice draw_slice();             public native AVFilterPad draw_slice(Draw_slice draw_slice);

        public static class Poll_frame extends FunctionPointer {
            static { load(); }
            public    Poll_frame(Pointer p) { super(p); }
            protected Poll_frame() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterLink link);
        }
        public native Poll_frame poll_frame();             public native AVFilterPad poll_frame(Poll_frame poll_frame);

        public static class Request_frame extends FunctionPointer {
            static { load(); }
            public    Request_frame(Pointer p) { super(p); }
            protected Request_frame() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterLink link);
        }
        public native Request_frame request_frame();       public native AVFilterPad request_frame(Request_frame request_frame);

        public static class Config_props extends FunctionPointer {
            static { load(); }
            public    Config_props(Pointer p) { super(p); }
            protected Config_props() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterLink link);
        }
        public native Config_props config_props();         public native AVFilterPad config_props(Config_props config_props);
    }

    public static native void avfilter_default_start_frame(AVFilterLink link, AVFilterPicRef picref);
    public static native void avfilter_default_draw_slice(AVFilterLink link, int y, int h, int slice_dir);
    public static native void avfilter_default_end_frame(AVFilterLink link);
    public static native int avfilter_default_config_output_link(AVFilterLink link);
//    public static native int avfilter_default_config_input_link (AVFilterLink link);
    public static native AVFilterPicRef avfilter_default_get_video_buffer(AVFilterLink link, int perms, int w, int h);

    public static native void avfilter_set_common_formats(AVFilterContext ctx, AVFilterFormats formats);
    public static native int avfilter_default_query_formats(AVFilterContext ctx);

    public static native void avfilter_null_start_frame(AVFilterLink link, AVFilterPicRef picref);
    public static native void avfilter_null_draw_slice(AVFilterLink link, int y, int h, int slice_dir);
    public static native void avfilter_null_end_frame(AVFilterLink link);
    public static native AVFilterPicRef avfilter_null_get_video_buffer(AVFilterLink link, int perms, int w, int h);

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

        @Cast("const char*")
        public native BytePointer name();   public native AVFilter name(BytePointer name);
        public native int priv_size();      public native AVFilter priv_size(int priv_size);

        public static class Init extends FunctionPointer {
            static { load(); }
            public    Init(Pointer p) { super(p); }
            protected Init() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterContext ctx, String args, Pointer opaque);
        }
        public native Init init();          public native AVFilter init(Init init);

        public static class Uninit extends FunctionPointer {
            static { load(); }
            public    Uninit(Pointer p) { super(p); }
            protected Uninit() { allocate(); }
            protected final native void allocate();
            public native void call(AVFilterContext ctx);
        }
        public native Uninit uninit();      public native AVFilter uninit(Uninit uninit);

        public static class Query_formats extends FunctionPointer {
            static { load(); }
            public    Query_formats(Pointer p) { super(p); }
            protected Query_formats() { allocate(); }
            protected final native void allocate();
            public native int call(AVFilterContext c);
        }
        public native Query_formats query_formats(); public native AVFilter query_formats(Query_formats query_formats);

        public native @Const AVFilterPad inputs();   public native AVFilter inputs(AVFilterPad inputs);
        public native @Const AVFilterPad outputs();  public native AVFilter outputs(AVFilterPad outputs);

        @Cast("const char*")
        public native BytePointer description();     public native AVFilter description(BytePointer description);
    }

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

        public native @Const AVClass av_class(); public native AVFilterContext av_class(AVClass av_class);

        public native AVFilter filter();         public native AVFilterContext filter(AVFilter filter);

        @Cast("char*")
        public native BytePointer name();        public native AVFilterContext name(BytePointer name);

        public native int input_count();         public native AVFilterContext input_count(int input_count);
        public native AVFilterPad input_pads();  public native AVFilterContext input_pads(AVFilterPad input_pads);
        @Cast("AVFilterLink**")
        public native PointerPointer inputs();   public native AVFilterContext inputs(PointerPointer inputs);

        public native int output_count();        public native AVFilterContext output_count(int output_count);
        public native AVFilterPad output_pads(); public native AVFilterContext output_pads(AVFilterPad output_pads);
        @Cast("AVFilterLink**")
        public native PointerPointer outputs();  public native AVFilterContext outputs(PointerPointer outputs);

        public native Pointer priv();            public native AVFilterContext priv(Pointer priv);
    }

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

        public native AVFilterContext src();        public native AVFilterLink src(AVFilterContext src);
        public native int srcpad();                 public native AVFilterLink srcpad(int srcpad);

        public native AVFilterContext dst();        public native AVFilterLink dst(AVFilterContext dst);
        public native int dstpad();                 public native AVFilterLink dstpad(int dstpad);

        //enum init_state {
        public static final int
                AVLINK_UNINIT    = 0,
                AVLINK_STARTINIT = 1,
                AVLINK_INIT      = 2;

        public native int w();                      public native AVFilterLink w(int w);
        public native int h();                      public native AVFilterLink h(int h);
        @Cast("PixelFormat")
        public native int format();                 public native AVFilterLink format(int format);

        public native AVFilterFormats in_formats(); public native AVFilterLink in_formats(AVFilterFormats in_formats);
        public native AVFilterFormats out_formats();public native AVFilterLink out_formats(AVFilterFormats out_formats);

        public native AVFilterPicRef srcpic();      public native AVFilterLink srcpic(AVFilterPicRef srcpic);

        public native AVFilterPicRef cur_pic();     public native AVFilterLink cur_pic(AVFilterPicRef cur_pic);
        public native AVFilterPicRef outpic();      public native AVFilterLink outpic(AVFilterPicRef outpic);
    }

    public static native int avfilter_link(AVFilterContext src, int srcpad,
            AVFilterContext dst, int dstpad);
    public static native int avfilter_config_links(AVFilterContext filter);

    public static native AVFilterPicRef avfilter_get_video_buffer(AVFilterLink link,
            int perms, int w, int h);

    public static native int avfilter_request_frame(AVFilterLink link);
    public static native int avfilter_poll_frame(AVFilterLink link);
    public static native void avfilter_start_frame(AVFilterLink link, AVFilterPicRef picref);
    public static native void avfilter_end_frame(AVFilterLink link);
    public static native void avfilter_draw_slice(AVFilterLink link, int y, int h, int slice_dir);

    public static native void avfilter_register_all();
    public static native void avfilter_uninit();
    public static native int avfilter_register(AVFilter filter);
    public static native AVFilter avfilter_get_by_name(String name);
    public static native @ByPtrPtr AVFilter av_filter_next(@ByPtrPtr AVFilter filter);
    public static native AVFilterContext avfilter_open(AVFilter filter, String inst_name);
    public static native int avfilter_init_filter(AVFilterContext filter, String args, Pointer opaque);
    public static native void avfilter_destroy(AVFilterContext filter);
    public static native int avfilter_insert_filter(AVFilterLink link, AVFilterContext filt, int in, int out);

    public static native void avfilter_insert_pad(int idx, @Cast("unsigned int*") int[] count, long padidx_off,
            @ByPtrPtr AVFilterPad pads, @Cast("AVFilterLink***") PointerPointer links, AVFilterPad newpad);
    public static native void avfilter_insert_inpad(AVFilterContext f, int index, AVFilterPad p);
    public static native void avfilter_insert_outpad(AVFilterContext f, int index, AVFilterPad p);
}
