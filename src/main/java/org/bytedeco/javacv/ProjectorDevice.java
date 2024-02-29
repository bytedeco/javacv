/*
 * Copyright (C) 2009-2012 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bytedeco.javacv;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import org.bytedeco.javacpp.Pointer;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectorDevice extends ProjectiveDevice {
    public ProjectorDevice(String name) {
        super(name);
    }
    public ProjectorDevice(String name, String filename) throws Exception {
        super(name, filename);
        settings.setImageWidth(imageWidth);
        settings.setImageHeight(imageHeight);
    }
    public ProjectorDevice(String name, FileStorage fs) throws Exception {
        super(name, fs);
        settings.setImageWidth(imageWidth);
        settings.setImageHeight(imageHeight);
    }
    public ProjectorDevice(Settings settings) throws Exception {
        super((ProjectiveDevice.Settings)settings);
    }

    public interface Settings {
        String getName();
        void setName(String name);
        double getResponseGamma();
        void setResponseGamma(double gamma);

        int getScreenNumber();
        void setScreenNumber(int screenNumber);
        long getLatency();
        void setLatency(long latency);
        String getDescription();

        int getImageWidth();
        void setImageWidth(int imageWidth);
        int getImageHeight();
        void setImageHeight(int imageHeight);
        int getBitDepth();
        void setBitDepth(int bitDepth);
        int getRefreshRate();
        void setRefreshRate(int refreshRate);

        boolean isUseOpenGL();
        void setUseOpenGL(boolean useOpenGL);

        void addPropertyChangeListener(PropertyChangeListener listener);
        void removePropertyChangeListener(PropertyChangeListener listener);
    }

    public static class SettingsImplementation extends ProjectiveDevice.Settings implements Settings {
        public SettingsImplementation() { name = "Projector  0"; setScreenNumber(screenNumber); }
        public SettingsImplementation(ProjectiveDevice.Settings settings) {
            super(settings);
            if (settings instanceof SettingsImplementation) {
                SettingsImplementation s = (SettingsImplementation)settings;
                this.screenNumber = s.screenNumber;
                this.latency      = s.latency;
                this.imageWidth   = s.imageWidth;
                this.imageHeight  = s.imageHeight;
                this.bitDepth     = s.bitDepth;
                this.refreshRate  = s.refreshRate;
                this.useOpenGL    = s.useOpenGL;
            }
        }

        int screenNumber = CanvasFrame.getScreenDevices().length > 1 ? 1 : 0;
        long latency = CanvasFrame.DEFAULT_LATENCY;

        public int getScreenNumber() {
            return screenNumber;
        }
        public void setScreenNumber(int screenNumber) {
            DisplayMode d = CanvasFrame.getDisplayMode(screenNumber);
            String oldDescription = getDescription();
            firePropertyChange("screenNumber",  this.screenNumber,  this.screenNumber  = screenNumber);
            firePropertyChange("description", oldDescription, getDescription());
            firePropertyChange("imageWidth",    this.imageWidth,    this.imageWidth    = d == null ? 0 : d.getWidth());
            firePropertyChange("imageHeight",   this.imageHeight,   this.imageHeight   = d == null ? 0 : d.getHeight());
            firePropertyChange("bitDepth",      this.bitDepth,      this.bitDepth      = d == null ? 0 : d.getBitDepth());
            firePropertyChange("refreshRate",   this.refreshRate,   this.refreshRate   = d == null ? 0 : d.getRefreshRate());
            firePropertyChange("responseGamma", this.responseGamma, this.responseGamma = CanvasFrame.getGamma(screenNumber));
        }

        public long getLatency() {
            return latency;
        }
        public void setLatency(long latency) {
            this.latency = latency;
        }

        public String getDescription() {
            String[] descriptions = null;
            descriptions = CanvasFrame.getScreenDescriptions();

            if (descriptions != null && screenNumber >= 0 && screenNumber < descriptions.length) {
                return descriptions[screenNumber];
            } else {
                return "";
            }
        }

        int imageWidth = 0, imageHeight = 0, bitDepth = 0, refreshRate = 0;

        public int getImageWidth() {
            return imageWidth;
        }
        public void setImageWidth(int imageWidth) {
            firePropertyChange("imageWidth", this.imageWidth, this.imageWidth = imageWidth);
        }

        public int getImageHeight() {
            return imageHeight;
        }
        public void setImageHeight(int imageHeight) {
            firePropertyChange("imageHeight", this.imageHeight, this.imageHeight = imageHeight);
        }

        public int getBitDepth() {
            return bitDepth;
        }
        public void setBitDepth(int bitDepth) {
            this.bitDepth = bitDepth;
        }

        public int getRefreshRate() {
            return refreshRate;
        }
        public void setRefreshRate(int refreshRate) {
            this.refreshRate = refreshRate;
        }

        private boolean useOpenGL = false;

        public boolean isUseOpenGL() {
            return useOpenGL;
        }
        public void setUseOpenGL(boolean useOpenGL) {
            this.useOpenGL = useOpenGL;
        }
    }

    // pouah.. hurray for Scala!
    public static class CalibrationSettings extends ProjectiveDevice.CalibrationSettings implements Settings {
        public CalibrationSettings() { }
        public CalibrationSettings(ProjectiveDevice.CalibrationSettings settings) {
            super(settings);
            if (settings instanceof CalibrationSettings) {
                CalibrationSettings s = (CalibrationSettings)settings;
                si = new SettingsImplementation(s.si);
                this.brightnessBackground = s.brightnessBackground;
                this.brightnessForeground = s.brightnessForeground;
            }
        }
        SettingsImplementation si = new SettingsImplementation() {
            @Override public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
                CalibrationSettings.this.firePropertyChange(propertyName, oldValue, newValue);
            }
        };

        @Override public String getName() { return si.getName(); }
        @Override public void setName(String name) { si.setName(name); }
        @Override public double getResponseGamma() { return si.getResponseGamma(); }
        @Override public void setResponseGamma(double responseGamma) { si.setResponseGamma(responseGamma); }
//        @Override public double getNominalDistance() { return si.getNominalDistance(); }
//        @Override public void setNominalDistance(double nominalDistance) { si.setNominalDistance(nominalDistance); }

        public int getScreenNumber() { return si.getScreenNumber(); }
        public void setScreenNumber(int screenNumber) { si.setScreenNumber(screenNumber); }
        public long getLatency() { return si.getLatency(); }
        public void setLatency(long latency) { si.setLatency(latency); }
        public String getDescription() { return si.getDescription(); }

        public int getImageWidth() { return si.getImageWidth(); }
        public void setImageWidth(int imageWidth) { si.setImageWidth(imageWidth); }
        public int getImageHeight() { return si.getImageHeight(); }
        public void setImageHeight(int imageHeight) { si.setImageHeight(imageHeight); }
        public int getBitDepth() { return si.getBitDepth(); }
        public void setBitDepth(int bitDepth) { si.setBitDepth(bitDepth); }
        public int getRefreshRate() { return si.getRefreshRate(); }
        public void setRefreshRate(int refreshRate) { si.setRefreshRate(refreshRate); }

        public boolean isUseOpenGL() { return si.isUseOpenGL(); }
        public void setUseOpenGL(boolean useOpenGL) { si.setUseOpenGL(useOpenGL); }

        double brightnessBackground = 0.0, brightnessForeground = 1.0;

        public double getBrightnessBackground() {
            return brightnessBackground;
        }
        public void setBrightnessBackground(double brightnessBackground) {
            firePropertyChange("brightnessBackground", this.brightnessBackground,
                    this.brightnessBackground = brightnessBackground);
        }

        public double getBrightnessForeground() {
            return brightnessForeground;
        }
        public void setBrightnessForeground(double brightnessForeground) {
            firePropertyChange("brightnessForeground", this.brightnessForeground,
                    this.brightnessForeground = brightnessForeground);
        }
    }

    public static class CalibratedSettings extends ProjectiveDevice.CalibratedSettings implements Settings {
        public CalibratedSettings() { }
        public CalibratedSettings(ProjectiveDevice.CalibratedSettings settings) {
            super(settings);
            if (settings instanceof CalibratedSettings) {
                si = new SettingsImplementation(((CalibratedSettings)settings).si);
            }
        }
        SettingsImplementation si = new SettingsImplementation() {
            @Override public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
                CalibratedSettings.this.firePropertyChange(propertyName, oldValue, newValue);
            }
        };

        @Override public String getName() { return si.getName(); }
        @Override public void setName(String name) { si.setName(name); }
        @Override public double getResponseGamma() { return si.getResponseGamma(); }
        @Override public void setResponseGamma(double responseGamma) { si.setResponseGamma(responseGamma); }
//        @Override public double getNominalDistance() { return si.getNominalDistance(); }
//        @Override public void setNominalDistance(double nominalDistance) { si.setNominalDistance(nominalDistance); }

        public int getScreenNumber() { return si.getScreenNumber(); }
        public void setScreenNumber(int screenNumber) { si.setScreenNumber(screenNumber); }
        public long getLatency() { return si.getLatency(); }
        public void setLatency(long latency) { si.setLatency(latency); }
        public String getDescription() { return si.getDescription(); }

        public int getImageWidth() { return si.getImageWidth(); }
        public void setImageWidth(int imageWidth) { si.setImageWidth(imageWidth); }
        public int getImageHeight() { return si.getImageHeight(); }
        public void setImageHeight(int imageHeight) { si.setImageHeight(imageHeight); }
        public int getBitDepth() { return si.getBitDepth(); }
        public void setBitDepth(int bitDepth) { si.setBitDepth(bitDepth); }
        public int getRefreshRate() { return si.getRefreshRate(); }
        public void setRefreshRate(int refreshRate) { si.setRefreshRate(refreshRate); }

        public boolean isUseOpenGL() { return si.isUseOpenGL(); }
        public void setUseOpenGL(boolean useOpenGL) { si.setUseOpenGL(useOpenGL); }
    }

    private Settings settings;
    @Override public ProjectiveDevice.Settings getSettings() {
        return (ProjectiveDevice.Settings)settings;
    }
    public void setSettings(Settings settings) {
        setSettings((ProjectiveDevice.Settings)settings);
    }
    @Override public void setSettings(ProjectiveDevice.Settings settings) {
        super.setSettings(settings);
        if (settings instanceof ProjectiveDevice.CalibrationSettings) {
            this.settings = new CalibrationSettings((ProjectiveDevice.CalibrationSettings)settings);
        } else if (settings instanceof ProjectiveDevice.CalibratedSettings) {
            this.settings = new CalibratedSettings((ProjectiveDevice.CalibratedSettings)settings);
        } else {
            this.settings = new SettingsImplementation((ProjectiveDevice.Settings)settings);
        }
        if (this.settings.getName() == null || this.settings.getName().length() == 0) {
            this.settings.setName("Projector " + String.format("%2d", this.settings.getScreenNumber()));
        }
    }

    public CanvasFrame createCanvasFrame() throws CanvasFrame.Exception {
        if (settings.getScreenNumber() < 0) {
            return null;
        }
        DisplayMode d = new DisplayMode(settings.getImageWidth(), settings.getImageHeight(),
                settings.getBitDepth(), settings.getRefreshRate());
        CanvasFrame c = null;
        Throwable cause = null;
        try {
            c = Class.forName(CanvasFrame.class.getPackage().getName() + (settings.isUseOpenGL() ? ".GLCanvasFrame" : ".CanvasFrame")).
                    asSubclass(CanvasFrame.class).getConstructor(String.class, int.class, DisplayMode.class, double.class).
                    newInstance(settings.getName(), settings.getScreenNumber(), d, settings.getResponseGamma());
        } catch (ClassNotFoundException ex) {
            cause = ex;
        } catch (InstantiationException ex) {
            cause = ex;
        } catch (IllegalAccessException ex) {
            cause = ex;
        } catch (IllegalArgumentException ex) {
            cause = ex;
        } catch (NoSuchMethodException ex) {
            cause = ex;
        } catch (InvocationTargetException ex) {
            cause = ex.getCause();
        }
        if (cause != null) {
            if (cause instanceof CanvasFrame.Exception) {
                throw (CanvasFrame.Exception)cause;
            } else {
                throw new CanvasFrame.Exception("Failed to create CanvasFrame", cause);
            }
        }
        c.setLatency(settings.getLatency());

        Dimension size = c.getCanvasSize();
        if (size.width != imageWidth || size.height != imageHeight) {
            rescale(size.width, size.height);
        }

        return c;
    }

    private static ThreadLocal<CvMat>
            B4x3  = CvMat.createThreadLocal(4, 3),
            x23x1 = CvMat.createThreadLocal(3, 1),
            x34x1 = CvMat.createThreadLocal(4, 1);
    public double getAttenuation(double x, double y, CvMat n, double d) {
        CvMat B  = B4x3.get();
        CvMat x2 = x23x1.get();
        CvMat x3 = x34x1.get();

        getBackProjectionMatrix(n, d, B);
        x2.put(x, y, 1);
        cvMatMul(B, x2, x3);

        // find the direction and the distance of that middle point to the
        // projector and use it to compute the expected overall attenuation
        //      cos(theta) * nominal_distance^2 / distance^2
        // we assume a perfectly Lambertian surface ... ugh ...
        // at a sufficient distance from the projector... ugh...
        cvGEMM(R, T, -1, null, 0, x2, CV_GEMM_A_T);
        x3.rows(3);
        cvAddWeighted(x3, 1/x3.get(3), x2, -1, 0, x2);
        double distance2 = cvDotProduct(x2, x2);
        double distance = Math.sqrt(distance2);
        double cosangle = -Math.signum(d)*cvDotProduct(x2, n)/
                (distance * Math.sqrt(cvDotProduct(n, n)));
        double attenuation = cosangle/distance2;
//        System.out.println(distance + " " + cosangle + " " + attenuation);
        x3.rows(4);

        return attenuation;
    }

    public static ProjectorDevice[] read(String filename) throws Exception {
        FileStorage fs = new FileStorage(filename, FileStorage.READ);
        ProjectorDevice[] devices = read(fs);
        fs.release();
        return devices;
    }
    public static ProjectorDevice[] read(FileStorage fs) throws Exception {
        FileNode node = fs.get("Projectors");
        FileNodeIterator seq = node.begin();
        int count = (int)seq.remaining();

        ProjectorDevice[] devices = new ProjectorDevice[count];
        for (int i = 0; i < count; i++, seq.increment()) {
            FileNode n = seq.multiply();
            if (n.empty()) continue;
            String name = n.asBytePointer().getString();
            devices[i] = new ProjectorDevice(name, fs);
        }
        return devices;
    }
}
