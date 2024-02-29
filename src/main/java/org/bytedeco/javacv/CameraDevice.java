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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bytedeco.javacpp.Pointer;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class CameraDevice extends ProjectiveDevice {
    public CameraDevice(String name) {
        super(name);
    }
    public CameraDevice(String name, String filename) throws Exception {
        super(name, filename);
        settings.setImageWidth(imageWidth);
        settings.setImageHeight(imageHeight);
    }
    public CameraDevice(String name, FileStorage fs) throws Exception {
        super(name, fs);
        settings.setImageWidth(imageWidth);
        settings.setImageHeight(imageHeight);
    }
    public CameraDevice(Settings settings) throws Exception {
        super((ProjectiveDevice.Settings)settings);
    }

    public interface Settings {
        String getName();
        void setName(String name);
        double getResponseGamma();
        void setResponseGamma(double gamma);

        Integer getDeviceNumber();
        void setDeviceNumber(Integer deviceNumber) throws PropertyVetoException;
        File getDeviceFile();
        void setDeviceFile(File deviceFile) throws PropertyVetoException;
        String getDeviceFilename();
        void setDeviceFilename(String deviceFilename) throws PropertyVetoException;
        String getDevicePath();
        void setDevicePath(String devicePath) throws PropertyVetoException;
        Class<? extends FrameGrabber> getFrameGrabber();
        void setFrameGrabber(Class<? extends FrameGrabber> frameGrabber);
        String getDescription();

        String getFormat();
        void setFormat(String format);
        int getImageWidth();
        void setImageWidth(int imageWidth);
        int getImageHeight();
        void setImageHeight(int imageHeight);
        double getFrameRate();
        void setFrameRate(double frameRate);
        boolean isTriggerMode();
        void setTriggerMode(boolean triggerMode);
        int getBitsPerPixel();
        void setBitsPerPixel(int bitsPerPixel);
        FrameGrabber.ImageMode getImageMode();
        void setImageMode(FrameGrabber.ImageMode imageMode);
        int getTimeout();
        void setTimeout(int timeout);
        int getNumBuffers();
        void setNumBuffers(int numBuffers);
        boolean isDeinterlace();
        void setDeinterlace(boolean deinterlace);

        void addPropertyChangeListener(PropertyChangeListener listener);
        void removePropertyChangeListener(PropertyChangeListener listener);
    }

    public static class SettingsImplementation extends ProjectiveDevice.Settings implements Settings {
        public SettingsImplementation() { name = "Camera  0"; }
        public SettingsImplementation(ProjectiveDevice.Settings settings) {
            super(settings);
            if (settings instanceof SettingsImplementation) {
                SettingsImplementation s = (SettingsImplementation)settings;
                this.deviceNumber = s.deviceNumber;
                this.deviceFile   = s.deviceFile;
                this.devicePath   = s.devicePath;
                this.frameGrabber = s.frameGrabber;
                this.format       = s.format;
                this.imageWidth   = s.imageWidth;
                this.imageHeight  = s.imageHeight;
                this.frameRate    = s.frameRate;
                this.triggerMode  = s.triggerMode;
                this.bpp          = s.bpp;
                this.imageMode    = s.imageMode;
                this.timeout      = s.timeout;
                this.numBuffers   = s.numBuffers;
                this.deinterlace  = s.deinterlace;
            }
        }

        Integer deviceNumber = null;
        File deviceFile = null;
        String devicePath = null;
        Class<? extends FrameGrabber> frameGrabber = null;

        public Integer getDeviceNumber() {
            return deviceNumber;
        }
        public void setDeviceNumber(Integer deviceNumber) throws PropertyVetoException {
            if (deviceNumber != null) {
                try {
                    if (frameGrabber != null) {
                        try {
                            frameGrabber.getConstructor(int.class);
                        } catch (NoSuchMethodException e) {
                            frameGrabber.getConstructor(Integer.class);
                        }
                    }
                    setDevicePath(null);
                    setDeviceFile(null);
                } catch (NoSuchMethodException e) {
                    throw new PropertyVetoExceptionThatNetBeansLikes(frameGrabber.getSimpleName() + " does not accept a deviceNumber.",
                            new PropertyChangeEvent(this, "deviceNumber", this.deviceNumber, this.deviceNumber = null));
                }
            }
            String oldDescription = getDescription();
            firePropertyChange("deviceNumber", this.deviceNumber, this.deviceNumber = deviceNumber);
            firePropertyChange("description", oldDescription, getDescription());
        }

        public File getDeviceFile() {
            return deviceFile;
        }
        public void setDeviceFile(File deviceFile) throws PropertyVetoException {
            if (deviceFile != null) {
                try {
                    if (frameGrabber != null) {
                        frameGrabber.getConstructor(File.class);
                    }
                    setDeviceNumber(null);
                    setDevicePath(null);
                } catch (NoSuchMethodException e) {
                    deviceFile = null;
                    throw new PropertyVetoExceptionThatNetBeansLikes(frameGrabber.getSimpleName() + " does not accept a deviceFile.",
                            new PropertyChangeEvent(this, "deviceFile", this.deviceFile, this.deviceFile = null));
                }
            }
            String oldDescription = getDescription();
            firePropertyChange("deviceFile", this.deviceFile, this.deviceFile = deviceFile);
            firePropertyChange("description", oldDescription, getDescription());
        }
        public String getDeviceFilename() {
            return getDeviceFile() == null ? "" : getDeviceFile().getPath();
        }
        public void setDeviceFilename(String deviceFilename) throws PropertyVetoException {
            setDeviceFile(deviceFilename == null || deviceFilename.length() == 0 ?
                null : new File(deviceFilename));
        }

        public String getDevicePath() {
            return devicePath;
        }
        public void setDevicePath(String devicePath) throws PropertyVetoException {
            if (devicePath != null) {
                try {
                    if (frameGrabber != null) {
                        frameGrabber.getConstructor(String.class);
                    }
                    setDeviceNumber(null);
                    setDeviceFile(null);
                } catch (NoSuchMethodException e) {
                    devicePath = "";
                    throw new PropertyVetoExceptionThatNetBeansLikes(frameGrabber.getSimpleName() + " does not accept a devicePath.",
                            new PropertyChangeEvent(this, "devicePath", this.devicePath, this.devicePath = null));
                }
            }
            String oldDescription = getDescription();
            firePropertyChange("devicePath", this.devicePath, this.devicePath = devicePath);
            firePropertyChange("description", oldDescription, getDescription());
        }

        public Class<? extends FrameGrabber> getFrameGrabber() {
            return frameGrabber;
        }
        public void setFrameGrabber(Class<? extends FrameGrabber> frameGrabber) {
            String oldDescription = getDescription();
            firePropertyChange("frameGrabber", this.frameGrabber, this.frameGrabber = frameGrabber);
            firePropertyChange("description", oldDescription, getDescription());

            if (frameGrabber == null) {
                firePropertyChange("deviceNumber", this.deviceNumber, this.deviceNumber = null);
                firePropertyChange("deviceFile", this.deviceFile, this.deviceFile = null);
                firePropertyChange("devicePath", this.devicePath, this.devicePath = null);
                return;
            }

            boolean hasDeviceNumber = false;
            try {
                frameGrabber.getConstructor(int.class);
                hasDeviceNumber = true;
            } catch (NoSuchMethodException e) {
                try {
                    frameGrabber.getConstructor(Integer.class);
                    hasDeviceNumber = true;
                } catch (NoSuchMethodException e2) {
                    firePropertyChange("deviceNumber", this.deviceNumber, this.deviceNumber = null);
                }
            }
            try {
                frameGrabber.getConstructor(File.class);
            } catch (NoSuchMethodException e) {
                firePropertyChange("deviceFile", this.deviceFile, this.deviceFile = null);
            }
            try {
                frameGrabber.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                firePropertyChange("devicePath", this.devicePath, this.devicePath = null);
            }

            if (hasDeviceNumber && deviceNumber == null && deviceFile == null && devicePath == null) {
                try {
                    setDeviceNumber(0);
                } catch (PropertyVetoException e) { }
            }
        }

        public String getDescription() {
            String[] descriptions = null;
            try {
                Method m = frameGrabber.getMethod("getDeviceDescriptions");
                descriptions = (String[])m.invoke(null);
            } catch (java.lang.Exception ex) { }

            if (descriptions != null && deviceNumber != null && deviceNumber < descriptions.length) {
                return descriptions[deviceNumber];
            } else {
                return "";
            }
        }

        String format = "";
        int imageWidth = 0, imageHeight = 0;
        double frameRate = 0;
        boolean triggerMode = false;
        int bpp = 0;
        FrameGrabber.ImageMode imageMode = FrameGrabber.ImageMode.COLOR;
        int timeout = 10000;
        int numBuffers = 4;
        boolean deinterlace = false;

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

        public FrameGrabber.ImageMode getImageMode() {
            return imageMode;
        }
        public void setImageMode(FrameGrabber.ImageMode imageMode) {
            this.imageMode = imageMode;
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

        public boolean isDeinterlace() {
            return deinterlace;
        }
        public void setDeinterlace(boolean deinterlace) {
            this.deinterlace = deinterlace;
        }
    }

    // pouah.. hurray for Scala!
    public static class CalibrationSettings extends ProjectiveDevice.CalibrationSettings implements Settings {
        public CalibrationSettings() { }
        public CalibrationSettings(ProjectiveDevice.CalibrationSettings settings) {
            super(settings);
            if (settings instanceof CalibrationSettings) {
                si = new SettingsImplementation(((CalibrationSettings)settings).si);
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

        public Integer getDeviceNumber() { return si.getDeviceNumber(); }
        public void setDeviceNumber(Integer deviceNumber) throws PropertyVetoException { si.setDeviceNumber(deviceNumber); }
        public File getDeviceFile() { return si.getDeviceFile(); }
        public void setDeviceFile(File deviceFile) throws PropertyVetoException { si.setDeviceFile(deviceFile); }
        public String getDeviceFilename() { return si.getDeviceFilename(); }
        public void setDeviceFilename(String deviceFilename) throws PropertyVetoException { si.setDeviceFilename(deviceFilename); }
        public String getDevicePath() { return si.getDevicePath(); }
        public void setDevicePath(String devicePath) throws PropertyVetoException { si.setDevicePath(devicePath); }
        public Class<? extends FrameGrabber> getFrameGrabber() { return si.getFrameGrabber(); }
        public void setFrameGrabber(Class<? extends FrameGrabber> frameGrabber) { si.setFrameGrabber(frameGrabber); }
        public String getDescription() { return si.getDescription(); }

        public String getFormat() { return si.getFormat(); }
        public void setFormat(String format) { si.setFormat(format); }
        public int getImageWidth() { return si.getImageWidth(); }
        public void setImageWidth(int imageWidth) { si.setImageWidth(imageWidth); }
        public int getImageHeight() { return si.getImageHeight(); }
        public void setImageHeight(int imageHeight) { si.setImageHeight(imageHeight); }
        public double getFrameRate() { return si.getFrameRate(); }
        public void setFrameRate(double frameRate) { si.setFrameRate(frameRate); }
        public boolean isTriggerMode() { return si.isTriggerMode(); }
        public void setTriggerMode(boolean triggerMode) { si.setTriggerMode(triggerMode); }
        public int getBitsPerPixel() { return si.getBitsPerPixel(); }
        public void setBitsPerPixel(int bitsPerPixel) { si.setBitsPerPixel(bitsPerPixel); }
        public FrameGrabber.ImageMode getImageMode() { return si.getImageMode(); }
        public void setImageMode(FrameGrabber.ImageMode imageMode) { si.setImageMode(imageMode); }
        public int getTimeout() { return si.getTimeout(); }
        public void setTimeout(int timeout) { si.setTimeout(timeout); }
        public int getNumBuffers() { return si.getNumBuffers(); }
        public void setNumBuffers(int numBuffers) { si.setNumBuffers(numBuffers); }
        public boolean isDeinterlace() { return si.isDeinterlace(); }
        public void setDeinterlace(boolean deinterlace) { si.setDeinterlace(deinterlace); }
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

        public Integer getDeviceNumber() { return si.getDeviceNumber(); }
        public void setDeviceNumber(Integer deviceNumber) throws PropertyVetoException { si.setDeviceNumber(deviceNumber); }
        public File getDeviceFile() { return si.getDeviceFile(); }
        public void setDeviceFile(File deviceFile) throws PropertyVetoException { si.setDeviceFile(deviceFile); }
        public String getDeviceFilename() { return si.getDeviceFilename(); }
        public void setDeviceFilename(String deviceFilename) throws PropertyVetoException { si.setDeviceFilename(deviceFilename); }
        public String getDevicePath() { return si.getDevicePath(); }
        public void setDevicePath(String devicePath) throws PropertyVetoException { si.setDevicePath(devicePath); }
        public Class<? extends FrameGrabber> getFrameGrabber() { return si.getFrameGrabber(); }
        public void setFrameGrabber(Class<? extends FrameGrabber> frameGrabber) { si.setFrameGrabber(frameGrabber); }
        public String getDescription() { return si.getDescription(); }

        public String getFormat() { return si.getFormat(); }
        public void setFormat(String format) { si.setFormat(format); }
        public int getImageWidth() { return si.getImageWidth(); }
        public void setImageWidth(int imageWidth) { si.setImageWidth(imageWidth); }
        public int getImageHeight() { return si.getImageHeight(); }
        public void setImageHeight(int imageHeight) { si.setImageHeight(imageHeight); }
        public double getFrameRate() { return si.getFrameRate(); }
        public void setFrameRate(double frameRate) { si.setFrameRate(frameRate); }
        public boolean isTriggerMode() { return si.isTriggerMode(); }
        public void setTriggerMode(boolean triggerMode) { si.setTriggerMode(triggerMode); }
        public int getBitsPerPixel() { return si.getBitsPerPixel(); }
        public void setBitsPerPixel(int bitsPerPixel) { si.setBitsPerPixel(bitsPerPixel); }
        public FrameGrabber.ImageMode getImageMode() { return si.getImageMode(); }
        public void setImageMode(FrameGrabber.ImageMode imageMode) { si.setImageMode(imageMode); }
        public int getTimeout() { return si.getTimeout(); }
        public void setTimeout(int timeout) { si.setTimeout(timeout); }
        public int getNumBuffers() { return si.getNumBuffers(); }
        public void setNumBuffers(int numBuffers) { si.setNumBuffers(numBuffers); }
        public boolean isDeinterlace() { return si.isDeinterlace(); }
        public void setDeinterlace(boolean deinterlace) { si.setDeinterlace(deinterlace); }
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
            this.settings.setName("Camera " + String.format("%2d", this.settings.getDeviceNumber()));
        }
    }

    public FrameGrabber createFrameGrabber() throws FrameGrabber.Exception {
        try {
            settings.getFrameGrabber().getMethod("tryLoad").invoke(null);
            FrameGrabber f;
            if (settings.getDeviceFile() != null) {
                f = settings.getFrameGrabber().getConstructor(File.class).newInstance(settings.getDeviceFile());
            } else if (settings.getDevicePath() != null && settings.getDevicePath().length() > 0) {
                f = settings.getFrameGrabber().getConstructor(String.class).newInstance(settings.getDevicePath());
            } else {
                int number = settings.getDeviceNumber() == null ? 0 : settings.getDeviceNumber();
                try {
                    f = settings.getFrameGrabber().getConstructor(int.class).newInstance(number);
                } catch (NoSuchMethodException e) {
                    f = settings.getFrameGrabber().getConstructor(Integer.class).newInstance(number);
                }
            }
            f.setFormat(settings.getFormat());
            f.setImageWidth(settings.getImageWidth());
            f.setImageHeight(settings.getImageHeight());
            f.setFrameRate(settings.getFrameRate());
            f.setTriggerMode(settings.isTriggerMode());
            f.setBitsPerPixel(settings.getBitsPerPixel());
            f.setImageMode(settings.getImageMode());
            f.setTimeout(settings.getTimeout());
            f.setNumBuffers(settings.getNumBuffers());
            f.setGamma(settings.getResponseGamma());
            f.setDeinterlace(settings.isDeinterlace());
            return f;
        } catch (Throwable t) {
            if (t instanceof InvocationTargetException) {
                t = ((InvocationTargetException)t).getCause();
            }
            if (t instanceof FrameGrabber.Exception) {
                throw (FrameGrabber.Exception)t;
            } else {
                throw new FrameGrabber.Exception("Failed to create " + settings.getFrameGrabber(), t);
            }
        }
    }

    public static CameraDevice[] read(String filename) throws Exception {
        FileStorage fs = new FileStorage(filename, FileStorage.READ);
        CameraDevice[] devices = read(fs);
        fs.release();
        return devices;
    }
    public static CameraDevice[] read(FileStorage fs) throws Exception {
        FileNode node = fs.get("Cameras");
        FileNodeIterator seq = node.begin();
        int count = (int)seq.remaining();

        CameraDevice[] devices = new CameraDevice[count];
        for (int i = 0; i < count; i++, seq.increment()) {
            FileNode n = seq.multiply();
            if (n.empty()) continue;
            String name = n.asBytePointer().getString();
            devices[i] = new CameraDevice(name, fs);
        }
        return devices;
    }

}
