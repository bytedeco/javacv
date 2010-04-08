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
 */

package name.audet.samuel.javacv;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import name.audet.samuel.javacv.jna.cxcore.IplImage;

/**
 *
 * @author Samuel Audet
 */
public abstract class FrameGrabber {

    public static final List<Class<? extends FrameGrabber>> list =
            new LinkedList<Class<? extends FrameGrabber>>();
    static {
        list.add(DC1394FrameGrabber.class);
        list.add(FlyCaptureFrameGrabber.class);
        list.add(OpenCVFrameGrabber.class);
        list.add(FFmpegFrameGrabber.class);
    }
    public static void init() {
        for (Class<? extends FrameGrabber> c : list) {
            try {
                c.getMethod("tryLoad").invoke(null);
            } catch (Exception ex) { }
        }
    }
    public static Class<? extends FrameGrabber> getDefault() {
        Class<? extends FrameGrabber> c = null;
        // select first frame grabber that can load and that may have some cameras..
        for (int i = 0; i < FrameGrabber.list.size(); i++) {
            try {
                c = FrameGrabber.list.get(i);
                c.getMethod("tryLoad").invoke(null);
                boolean mayContainCameras = true;
                try {
                    String[] s = (String[])c.getMethod("getDeviceDescriptions").invoke(null);
                    if (s.length == 0) {
                        mayContainCameras = false;
                    }
                } catch (Throwable t) { }
                if (mayContainCameras) {
                    break;
                }
            } catch (Exception ex) { }
        }
        return c;
    }

    public static class PropertyEditor extends PropertyEditorSupport {
        @Override public String getAsText() {
            Class c = (Class)getValue();
            return c == null ? "null" : c.getSimpleName();
        }
        @Override public void setAsText(String s) {
            if (s == null) {
                setValue(null);
            }
            for (int i = 0; i < list.size(); i++) {
                Class c = list.get(i);
                if (s.equals(c.getSimpleName())) {
                    setValue(c);
                }
            }
        }
        @Override public String[] getTags() {
            String[] s = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                s[i] = list.get(i).getSimpleName();
            }
            return s;
        }
    }


    public static enum ColorMode {
        BGR, GRAY, RAW
    }

    protected int imageWidth = 0, imageHeight = 0;
    protected double frameRate = 0;
    protected boolean triggerMode = false;
    protected int triggerFlushSize = 0;
    protected int bpp = 0;
    protected ColorMode colorMode = ColorMode.BGR;
    protected int timeout = 10000;
    protected int numBuffers = 4;

    public int getImageWidth() {
        return imageWidth;
    }
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public double getFrameRate() {
        return frameRate;
    }
    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public boolean isTriggerMode() {
        return triggerMode;
    }
    public void setTriggerMode(boolean triggerMode) {
        this.triggerMode = triggerMode;
    }

    public int getTriggerFlushSize() {
        return triggerFlushSize;
    }
    public void setTriggerFlushSize(int triggerFlushSize) {
        this.triggerFlushSize = triggerFlushSize;
    }

    public int getBitsPerPixel() {
        return bpp;
    }
    public void setBitsPerPixel(int bitsPerPixel) {
        this.bpp = bitsPerPixel;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }
    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }

    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getNumBuffers() {
        return numBuffers;
    }
    public void setNumBuffers(int numBuffers) {
        this.numBuffers = numBuffers;
    }

    public abstract void start() throws Exception;
    public abstract void stop() throws Exception;
    public abstract void trigger() throws Exception;
    public abstract IplImage grab() throws Exception;
    public abstract void release() throws Exception;

    public static class Array {
        // declared protected to force users to use createArray(), which
        // can be overridden without changing the calling code...
        protected Array(FrameGrabber[] frameGrabbers) {
            setFrameGrabbers(frameGrabbers);
        }

        private IplImage[] grabbedImages = null;
        private long[] latencies = null;
        private long[] bestLatencies = null;
        private long lastNewestTimestamp = 0;
        private long bestInterval = Long.MAX_VALUE;

        protected FrameGrabber[] frameGrabbers = null;
        public FrameGrabber[] getFrameGrabbers() {
            return frameGrabbers;
        }
        public void setFrameGrabbers(FrameGrabber[] frameGrabbers) {
            this.frameGrabbers = frameGrabbers;
            grabbedImages = new IplImage[frameGrabbers.length];
            latencies = new long[frameGrabbers.length];
            bestLatencies = null;
            lastNewestTimestamp = 0;
        }
        public int size() {
            return frameGrabbers.length;
        }

        public void start() throws Exception {
            for (FrameGrabber f : frameGrabbers) {
                f.start();
            }
        }
        public void stop() throws Exception {
            for (FrameGrabber f : frameGrabbers) {
                f.stop();
            }
        }
        // should be overriden to implement a broadcast trigger...
        public void trigger() throws Exception {
            for (FrameGrabber f : frameGrabbers) {
                if (f.isTriggerMode()) {
                    f.trigger();
                }
            }
        }
        // should be overriden to implement a broadcast grab...
        public IplImage[] grab() throws Exception {
            if (frameGrabbers.length == 1) {
                grabbedImages[0] = frameGrabbers[0].grab();
                return grabbedImages;
            }

            // assume we sometimes get perfectly synchronized images,
            // so save the best latencies we find as the perfectly
            // synchronized case, so we know what to aim for in
            // cases of missing/dropped frames ...
            long newestTimestamp = 0;
            for (int i = 0; i < frameGrabbers.length; i++) {
                grabbedImages[i] = frameGrabbers[i].grab();
                newestTimestamp = Math.max(newestTimestamp, grabbedImages[i].getTimestamp());
            }
            for (int i = 0; i < frameGrabbers.length; i++) {
                latencies[i] = newestTimestamp-grabbedImages[i].getTimestamp();
            }
            if (bestLatencies == null) {
                bestLatencies = Arrays.copyOf(latencies, latencies.length);
            } else {
                int sum1 = 0, sum2 = 0;
                for (int i = 0; i < frameGrabbers.length; i++) {
                    sum1 += latencies[i];
                    sum2 += bestLatencies[i];
                }
                if (sum1 < sum2) {
                    bestLatencies = Arrays.copyOf(latencies, latencies.length);
                }
            }

            // we cannot have latencies higher than the time between frames..
            // or something too close to it anyway... 90% is good?
            bestInterval = Math.min(bestInterval, newestTimestamp-lastNewestTimestamp);
            for (int i = 0; i < bestLatencies.length; i++) {
                bestLatencies[i] = Math.min(bestLatencies[i], bestInterval*9/10);
            }

            // try to synchronize by attempting to land within 10% of
            // the bestLatencies looking up to 2 frames ahead ...
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < frameGrabbers.length; i++) {
                    if (frameGrabbers[i].isTriggerMode()) {
                        continue;
                    }
                    int latency = (int)(newestTimestamp - grabbedImages[i].getTimestamp());
                    while (latency-bestLatencies[i] > 0.1*bestLatencies[i]) {
                        grabbedImages[i] = frameGrabbers[i].grab();
                        latency = (int)(newestTimestamp - grabbedImages[i].getTimestamp());
                        if (latency < 0) {
                            // woops, a camera seems to have dropped a frame somewhere...
                            // bump up the newestTimestamp
                            newestTimestamp = grabbedImages[i].getTimestamp();
                            break;
                        }
                    }
                }
            }

//for (int i = 0; i < frameGrabbers.length; i++) {
//    long latency = newestTimestamp - grabbedImages[i].getTimestamp();
//    System.out.print(bestLatencies[i] + " " + latency + "  ");
//}
//System.out.println("  " + bestInterval);

            lastNewestTimestamp = newestTimestamp;

            return grabbedImages;
        }
        public void release() throws Exception {
            for (FrameGrabber f : frameGrabbers) {
                f.release();
            }
        }
    }

    public Array createArray(FrameGrabber[] frameGrabbers) {
        return new Array(frameGrabbers);
    }
}
