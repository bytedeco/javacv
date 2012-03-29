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
 */

package com.googlecode.javacv;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.googlecode.javacv.cpp.opencv_core.*;

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
        list.add(OpenKinectFrameGrabber.class);
        list.add(PS3EyeFrameGrabber.class);
        list.add(VideoInputFrameGrabber.class);
        list.add(OpenCVFrameGrabber.class);
        list.add(FFmpegFrameGrabber.class);
    }
    public static void init() {
        for (Class<? extends FrameGrabber> c : list) {
            try {
                c.getMethod("tryLoad").invoke(null);
            } catch (Throwable t) { }
        }
    }
    public static Class<? extends FrameGrabber> getDefault() {
        // select first frame grabber that can load and that may have some cameras..
        for (Class<? extends FrameGrabber> c : FrameGrabber.list) {
            try {
                c.getMethod("tryLoad").invoke(null);
                boolean mayContainCameras = false;
                try {
                    String[] s = (String[])c.getMethod("getDeviceDescriptions").invoke(null);
                    if (s.length > 0) {
                        mayContainCameras = true;
                    }
                } catch (Throwable t) { 
                    if (t.getCause() instanceof UnsupportedOperationException) {
                        mayContainCameras = true;
                    }
                }
                if (mayContainCameras) {
                    return c;
                }
            } catch (Throwable t) { }
        }
        return null;
    }
    public static Class<? extends FrameGrabber> get(String className) throws Exception {
        className = FrameGrabber.class.getPackage().getName() + "." + className;
        try {
            return Class.forName(className).asSubclass(FrameGrabber.class);
        } catch (ClassNotFoundException e) {
            String className2 = className + "FrameGrabber";
            try {
                return Class.forName(className2).asSubclass(FrameGrabber.class);
            } catch (ClassNotFoundException ex) {
                throw new Exception("Could not get FrameGrabber class for " + className + " or " + className2, e);
            }
        }
    }

    public static FrameGrabber create(Class<? extends FrameGrabber> c, Class p, Object o) throws Exception {
        Throwable cause = null;
        try {
            return c.getConstructor(p).newInstance(o);
        } catch (InstantiationException ex) {
            cause = ex;
        } catch (IllegalAccessException ex) {
            cause = ex;
        } catch (IllegalArgumentException ex) {
            cause = ex;
        } catch (NoSuchMethodException ex) {
            cause = ex;
        } catch (InvocationTargetException ex) {
            cause = ex;
        }
        throw new Exception("Could not create new " + c.getSimpleName() + "(" + o + ")", cause);
    }

    public static FrameGrabber createDefault(File deviceFile) throws Exception {
        return create(getDefault(), File.class, deviceFile);
    }
    public static FrameGrabber createDefault(String devicePath) throws Exception {
        return create(getDefault(), String.class, devicePath);
    }
    public static FrameGrabber createDefault(int deviceNumber) throws Exception {
        try {
            return create(getDefault(), int.class, deviceNumber);
        } catch (Exception ex) {
            return create(getDefault(), Integer.class, deviceNumber);
        }
    }

    public static FrameGrabber create(String className, File deviceFile) throws Exception {
        return create(get(className), File.class, deviceFile);
    }
    public static FrameGrabber create(String className, String devicePath) throws Exception {
        return create(get(className), String.class, devicePath);
    }
    public static FrameGrabber create(String className, int deviceNumber) throws Exception {
        try {
            return create(get(className), int.class, deviceNumber);
        } catch (Exception ex) {
            return create(get(className), Integer.class, deviceNumber);
        }
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


    public static enum ImageMode {
        COLOR, GRAY, RAW
    }

    public static final long
            SENSOR_PATTERN_RGGB = 0,
            SENSOR_PATTERN_GBRG = (1L << 32),
            SENSOR_PATTERN_GRBG = 1,
            SENSOR_PATTERN_BGGR = (1L << 32) | 1;

    protected String format = null;
    protected int imageWidth = 0, imageHeight = 0;
    protected ImageMode imageMode = ImageMode.COLOR;
    protected long sensorPattern = -1L;
    protected int pixelFormat = -1;
    protected double frameRate = 0;
    protected boolean triggerMode = false;
    protected int bpp = 0;
    protected int timeout = 10000;
    protected int numBuffers = 4;
    protected double gamma = 0.0;
    protected boolean deinterlace = false;
    protected int frameNumber = 0;
    protected long timestamp = 0;

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

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

    public ImageMode getImageMode() {
        return imageMode;
    }
    public void setImageMode(ImageMode imageMode) {
        this.imageMode = imageMode;
    }

    public long getSensorPattern() {
        return sensorPattern;
    }
    public void setSensorPattern(long sensorPattern) {
        this.sensorPattern = sensorPattern;
    }

    public int getPixelFormat() {
        return pixelFormat;
    }
    public void setPixelFormat(int pixelFormat) {
        this.pixelFormat = pixelFormat;
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

    public int getBitsPerPixel() {
        return bpp;
    }
    public void setBitsPerPixel(int bitsPerPixel) {
        this.bpp = bitsPerPixel;
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

    public double getGamma() {
        return gamma;
    }
    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public boolean isDeinterlace() {
        return deinterlace;
    }
    public void setDeinterlace(boolean deinterlace) {
        this.deinterlace = deinterlace;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
    public void setFrameNumber(int frameNumber) throws Exception {
        this.frameNumber = frameNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) throws Exception {
        this.timestamp = timestamp;
    }

    public int getLengthInFrames() {
        return 0;
    }
    public long getLengthInTime() {
        return 0;
    }

    public static class Exception extends java.lang.Exception {
        public Exception(String message) { super(message); }
        public Exception(String message, Throwable cause) { super(message, cause); }
    }

    public abstract void start() throws Exception;
    public abstract void stop() throws Exception;
    public abstract void trigger() throws Exception;
    public abstract IplImage grab() throws Exception;
    public abstract void release() throws Exception;

    public void restart() throws Exception {
        stop();
        start();
    }
    public void flush() throws Exception {
        for (int i = 0; i < numBuffers+1; i++) {
            grab();
        }
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<Void> future = null;
    private IplImage delayedImage = null;
    private long delayedTime = 0;
    public void delayedGrab(final long delayTime) {
        delayedImage = null;
        delayedTime = 0;
        final long start = System.nanoTime()/1000;
        if (future != null && !future.isDone()) {
            return;
        }
        future = executor.submit(new Callable<Void>() { public Void call() throws Exception {
            do {
                delayedImage = grab();
                delayedTime = System.nanoTime()/1000 - start;
            } while (delayedTime < delayTime);
            return null;
        }});
    }
    public long getDelayedTime() throws InterruptedException, ExecutionException {
        if (future == null) {
            return 0;
        }
        future.get();
        return delayedTime;
    }
    public IplImage getDelayedImage() throws InterruptedException, ExecutionException {
        if (future == null) {
            return null;
        }
        future.get();
        return delayedImage;
    }

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
                if (grabbedImages[i] != null) {
                    newestTimestamp = Math.max(newestTimestamp, frameGrabbers[i].getTimestamp());
                }
            }
            for (int i = 0; i < frameGrabbers.length; i++) {
                if (grabbedImages[i] != null) {
                    latencies[i] = newestTimestamp-frameGrabbers[i].getTimestamp();
                }
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
                    if (frameGrabbers[i].isTriggerMode() || grabbedImages[i] == null) {
                        continue;
                    }
                    int latency = (int)(newestTimestamp - frameGrabbers[i].getTimestamp());
                    while (latency-bestLatencies[i] > 0.1*bestLatencies[i]) {
                        grabbedImages[i] = frameGrabbers[i].grab();
                        if (grabbedImages[i] == null) {
                            break;
                        }
                        latency = (int)(newestTimestamp - frameGrabbers[i].getTimestamp());
                        if (latency < 0) {
                            // woops, a camera seems to have dropped a frame somewhere...
                            // bump up the newestTimestamp
                            newestTimestamp = frameGrabbers[i].getTimestamp();
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
