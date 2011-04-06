/*
 * Copyright (C) 2009,2010,2011 Samuel Audet
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

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.beans.PropertyChangeListener;
import com.googlecode.javacpp.Pointer;

import static com.googlecode.javacv.cpp.opencv_core.*;

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
    public ProjectorDevice(String name, CvFileStorage fs) throws Exception {
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

    public CanvasFrame createCanvasFrame() throws Exception {
        if (settings.getScreenNumber() < 0) {
            return null;
        }
        DisplayMode d = new DisplayMode(settings.getImageWidth(), settings.getImageHeight(),
                settings.getBitDepth(), settings.getRefreshRate());
        CanvasFrame c = new CanvasFrame(settings.getName(), settings.getScreenNumber(), d, settings.getResponseGamma());
        c.setLatency(settings.getLatency());

        Dimension size = c.getCanvasSize();
        if (size.width != imageWidth || size.height != imageHeight) {
            rescale(size.width, size.height);
        }

        return c;
    }

    public double getAttenuation(double x, double y, CvMat n, double d) {
        CvMat B  = CvMat.take(4, 3);
        CvMat x2 = CvMat.take(3, 1);
        CvMat x3 = CvMat.take(4, 1);

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

        x3.pool();
        x2.pool();
        B .pool();

        return attenuation;
    }

    public static ProjectorDevice[] read(String filename) throws Exception {
        CvFileStorage fs = CvFileStorage.open(filename, null, CV_STORAGE_READ);
        ProjectorDevice[] devices = read(fs);
        fs.release();
        return devices;
    }
    public static ProjectorDevice[] read(CvFileStorage fs) throws Exception {
        CvFileNode node = cvGetFileNodeByName(fs, null, "Projectors");
        CvSeq seq = node.data_seq();
        int count = seq.total();

        ProjectorDevice[] devices = new ProjectorDevice[count];
        for (int i = 0; i < count; i++) {
            Pointer p = cvGetSeqElem(seq, i);
            if (p == null) continue;
            String name = cvReadString(new CvFileNode(p), null);
            devices[i] = new ProjectorDevice(name, fs);
        }
        return devices;
    }

}
