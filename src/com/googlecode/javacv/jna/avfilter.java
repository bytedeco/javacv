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
 * This file was derived from avdevice.h include file from
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

package com.googlecode.javacv.jna;

import com.sun.jna.Callback;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import static com.googlecode.javacv.jna.avutil.*;

/**
 *
 * @author Samuel Audet
 */
public class avfilter {
    public static final String[] paths = avutil.paths;
    public static final String[] libnames = { "avfilter", "avfilter-1" };
    public static final String libname = Loader.load(paths, libnames);
    public static final NativeLibrary nativeLibrary = NativeLibrary.getInstance(libname);

    public static int LIBAVFILTER_VERSION_MAJOR = 1;
    public static int LIBAVFILTER_VERSION_MINOR = 19;
    public static int LIBAVFILTER_VERSION_MICRO = 0;

    public static int    LIBAVFILTER_VERSION_INT = AV_VERSION_INT(LIBAVFILTER_VERSION_MAJOR,
                                                                  LIBAVFILTER_VERSION_MINOR,
                                                                  LIBAVFILTER_VERSION_MICRO);
    public static String LIBAVFILTER_VERSION     = AV_VERSION(LIBAVFILTER_VERSION_MAJOR,
                                                              LIBAVFILTER_VERSION_MINOR,
                                                              LIBAVFILTER_VERSION_MICRO);
    public static int    LIBAVFILTER_BUILD       = LIBAVFILTER_VERSION_INT;


    public static native int avfilter_version();
    public static native String avfilter_configuration();
    public static native String avfilter_license();


    public static class AVFilterPic extends Structure {
        public AVFilterPic() { }
        public AVFilterPic(Pointer m) { super(m); read(); }

        public Pointer data0, data1, data2, data3;
        public int linesize0, linesize1, linesize2, linesize3;
        public int /* enum PixelFormat */ format;

        public int refcount;

        public Pointer priv;

        public interface Free extends Callback {
            void callback(AVFilterPic pic);
        }
        public Free free;

        public int w, h;

        public static class ByReference extends AVFilterPic implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVFilterPicRef extends Structure {
        public AVFilterPicRef() { }
        public AVFilterPicRef(Pointer m) { super(m); read(); }

        public AVFilterPic.ByReference pic;
        public Pointer data0, data1, data2, data3;
        public int linesize0, linesize1, linesize2, linesize3;
        public int w;
        public int h;

        public long pts;
        public long pos;

        public AVRational pixel_aspect;

        public int perms;
        public static final int
                AV_PERM_READ     = 0x01,
                AV_PERM_WRITE    = 0x02,
                AV_PERM_PRESERVE = 0x04,
                AV_PERM_REUSE    = 0x08,
                AV_PERM_REUSE2   = 0x10;

        public static class ByReference extends AVFilterPicRef implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static native AVFilterPicRef avfilter_ref_pic(AVFilterPicRef ref, int pmask);
    public static native void avfilter_unref_pic(AVFilterPicRef ref);

    public static class AVFilterFormats extends Structure {
        public AVFilterFormats() { }
        public AVFilterFormats(Pointer m) { super(m); read(); }

        public int format_count;
        public int /* enum PixelFormat* */ formats;

        public int refcount;
        public Pointer /* AVFilterFormats*** */ refs;

        public static class ByReference extends AVFilterFormats implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVFilterFormats p) {
                setStructure(p);
            }
            public AVFilterFormats getStructure() {
                return new AVFilterFormats(getValue());
            }
            public void getStructure(AVFilterFormats p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVFilterFormats p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native AVFilterFormats avfilter_make_format_list(IntByReference /* enum PixelFormat* */ pix_fmts);
    public static native int avfilter_add_colorspace(AVFilterFormats.PointerByReference avff, int /* enum PixelFormat */ pix_fmt);
    public static native AVFilterFormats avfilter_all_colorspaces();
    public static native AVFilterFormats avfilter_merge_formats(AVFilterFormats a, AVFilterFormats b);
    public static native void avfilter_formats_ref(AVFilterFormats formats, AVFilterFormats.PointerByReference ref);
    public static native void avfilter_formats_unref(AVFilterFormats.PointerByReference ref);
    public static native void avfilter_formats_changeref(AVFilterFormats.PointerByReference oldref,
            AVFilterFormats.PointerByReference newref);

    public static class AVFilterPad extends Structure {
        public AVFilterPad() { }
        public AVFilterPad(Pointer m) { super(m); read(); }

        public String name;
        public int /* enum AVMediaType */ type;
        public int min_perms;
        public int rej_perms;

        public interface Start_frame extends Callback {
            void callback(AVFilterLink link, AVFilterPicRef picref);
        }
        public Start_frame start_frame;

        public interface Get_video_buffer extends Callback {
            AVFilterPicRef callback(AVFilterLink link, int perms, int w, int h);
        }
        public Get_video_buffer get_video_buffer;

        public interface End_frame extends Callback {
            void callback(AVFilterLink link);
        }
        public End_frame end_frame;

        public interface Draw_slice extends Callback {
            void callback(AVFilterLink link, int y, int height, int slice_dir);
        }
        public Draw_slice draw_slice;

        public interface Poll_frame extends Callback {
            int callback(AVFilterLink link);
        }
        public Poll_frame poll_frame;

        public interface Request_frame extends Callback {
            int callback(AVFilterLink link);
        }
        public Request_frame request_frame;

        public interface Config_props extends Callback {
            int callback(AVFilterLink link);
        }
        public Config_props config_props;

        public static class ByReference extends AVFilterPad implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVFilterPad p) {
                setStructure(p);
            }
            public AVFilterPad.ByReference getStructure() {
                return new AVFilterPad.ByReference(getValue());
            }
            public void getStructure(AVFilterPad p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVFilterPad p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static native void avfilter_default_start_frame(AVFilterLink link, AVFilterPicRef picref);
    public static native void avfilter_default_draw_slice(AVFilterLink link, int y, int h, int slice_dir);
    public static native void avfilter_default_end_frame(AVFilterLink link);
    public static native int avfilter_default_config_output_link(AVFilterLink link);
    public static native int avfilter_default_config_input_link (AVFilterLink link);
    public static native AVFilterPicRef avfilter_default_get_video_buffer(AVFilterLink link, int perms, int w, int h);

    public static native void avfilter_set_common_formats(AVFilterContext ctx, AVFilterFormats formats);
    public static native int avfilter_default_query_formats(AVFilterContext ctx);

    public static native void avfilter_null_start_frame(AVFilterLink link, AVFilterPicRef picref);
    public static native void avfilter_null_draw_slice(AVFilterLink link, int y, int h, int slice_dir);
    public static native void avfilter_null_end_frame(AVFilterLink link);
    public static native AVFilterPicRef avfilter_null_get_video_buffer(AVFilterLink link, int perms, int w, int h);

    public static class AVFilter extends Structure {
        public AVFilter() { }
        public AVFilter(Pointer m) { super(m); read(); }

        public String name;
        public int priv_size;

        public interface Init extends Callback {
            int callback(AVFilterContext ctx, String args, Pointer opaque);
        }
        public Init init;

        public interface Uninit extends Callback {
            void callback(AVFilterContext ctx);
        }
        public Uninit uninit;

        public interface Query_formats extends Callback {
            int callback(AVFilterContext c);
        }
        public Query_formats query_formats;

        public AVFilterPad.ByReference inputs;
        public AVFilterPad.ByReference outputs;

        public String description;

        public static class ByReference extends AVFilter implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVFilter p) {
                setStructure(p);
            }
            public AVFilter getStructure() {
                return new AVFilter(getValue());
            }
            public void getStructure(AVFilter p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVFilter p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
    }

    public static class AVFilterContext extends Structure {
        public AVFilterContext() { }
        public AVFilterContext(Pointer m) { super(m); read(); }

        public AVClass.ByReference av_class;

        public AVFilter.ByReference filter;

        public String name;

        public int input_count;
        public AVFilterPad.ByReference     input_pads;
        public AVFilterLink.PointerByReference inputs;

        public int output_count;
        public AVFilterPad.ByReference     output_pads;
        public AVFilterLink.PointerByReference outputs;

        public Pointer priv;

        public static class ByReference extends AVFilterContext implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }
    }

    public static class AVFilterLink extends Structure {
        public AVFilterLink() { }
        public AVFilterLink(Pointer m) { super(m); read(); }

        public AVFilterContext.ByReference src;
        public int srcpad;

        public AVFilterContext.ByReference dst;
        public int dstpad;

        //enum init_state {
        public static final int
                AVLINK_UNINIT    = 0,
                AVLINK_STARTINIT = 1,
                AVLINK_INIT      = 2;

        public int w;
        public int h;
        public int /* enum PixelFormat */ format;

        public AVFilterFormats.ByReference in_formats;
        public AVFilterFormats.ByReference out_formats;

        public AVFilterPicRef.ByReference srcpic;

        public AVFilterPicRef.ByReference cur_pic;
        public AVFilterPicRef.ByReference outpic;

        public static class ByReference extends AVFilterLink implements Structure.ByReference {
            public ByReference() { }
            public ByReference(Pointer m) { super(m); }
        }

        public static class PointerByReference extends com.sun.jna.ptr.PointerByReference {
            public PointerByReference() { }
            public PointerByReference(AVFilterLink p) {
                setStructure(p);
            }
            public AVFilterLink getStructure() {
                return new AVFilterLink(getValue());
            }
            public void getStructure(AVFilterLink p) {
                p.useMemory(getValue());
                p.read();
            }
            public void setStructure(AVFilterLink p) {
                p.write();
                setValue(p.getPointer());
            }
        }
        public PointerByReference pointerByReference() {
            return new PointerByReference(this);
        }
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
    public static native AVFilter.PointerByReference av_filter_next(AVFilter.PointerByReference filter);
    public static native AVFilterContext avfilter_open(AVFilter filter, String inst_name);
    public static native int avfilter_init_filter(AVFilterContext filter, String args, Pointer opaque);
    public static native void avfilter_destroy(AVFilterContext filter);
    public static native int avfilter_insert_filter(AVFilterLink link, AVFilterContext filt, int in, int out);

    public static native void avfilter_insert_pad(int idx, IntByReference count, size_t padidx_off,
            AVFilterPad.PointerByReference pads, PointerByReference /* AVFilterLink *** */ links, AVFilterPad newpad);
//    public static void avfilter_insert_inpad(AVFilterContext f, int index, AVFilterPad p) {
//        IntByReference count = new IntByReference(f.input_count);
//        AVFilterPad.PointerByReference pads = f.input_pads.pointerByReference();
//        PointerByReference links = new PointerByReference(f.inputs.getPointer());
//        avfilter_insert_pad(index, count, offsetof(AVFilterLink, dstpad), pads, links, p);
//        f.inputs.setPointer(links.getValue());
//        f.input_pads = pads.getStructure();
//        f.input_count = count.getValue();
//    }
//
//    public static void avfilter_insert_outpad(AVFilterContext f, int index, AVFilterPad p) {
//        IntByReference count = new IntByReference(f.output_count);
//        AVFilterPad.PointerByReference pads = f.output_pads.pointerByReference();
//        PointerByReference links = new PointerByReference(f.outputs.getPointer());
//        avfilter_insert_pad(index, count, offsetof(AVFilterLink, srcpad), pads, links, p);
//        f.outputs.setPointer(links.getValue());
//        f.output_pads = pads.getStructure();
//        f.output_count = count.getValue();
//    }
}