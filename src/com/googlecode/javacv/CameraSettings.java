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

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.beancontext.BeanContextSupport;
import java.util.Arrays;

/**
 *
 * @author Samuel Audet
 */
public class CameraSettings extends BeanContextSupport {

    public CameraSettings() {
        this(false);
    }
    public CameraSettings(boolean calibrated) {
        this.calibrated = calibrated;
    }

    boolean calibrated = false;
    double monitorWindowsScale = 1.0;
    Class<? extends FrameGrabber> frameGrabber = null;

    public int getQuantity() {
        return size();
    }
    public void setQuantity(int quantity) throws PropertyVetoException {
        quantity = Math.max(1, quantity);
        Object[] a = toArray();
        int i = a.length;
        while (i > quantity) {
            remove(a[i-1]);
            i--;
        }
        while (i < quantity) {
            CameraDevice.Settings c = calibrated ? new CameraDevice.CalibratedSettings() :
                                                   new CameraDevice.CalibrationSettings();
            c.setName("Camera " + String.format("%2d", i));
            c.setDeviceNumber(i);
            c.setFrameGrabber(frameGrabber);
            add(c);
            i++;
        }
        pcSupport.firePropertyChange("quantity", a.length, quantity);
    }

    public double getMonitorWindowsScale() {
        return monitorWindowsScale;
    }
    public void setMonitorWindowsScale(double monitorWindowsScale) {
        this.monitorWindowsScale = monitorWindowsScale;
    }

    public Class<? extends FrameGrabber> getFrameGrabber() {
        return frameGrabber;
    }
    public void setFrameGrabber(Class<? extends FrameGrabber> frameGrabber) {
        pcSupport.firePropertyChange("frameGrabber", this.frameGrabber, this.frameGrabber = frameGrabber);
    }

    @Override public Object[] toArray() {
        Object[] a = super.toArray();
        Arrays.sort(a);
        return a;
    }
    @Override public Object[] toArray(Object[] a) {
        a = super.toArray(a);
        Arrays.sort(a);
        return a;
    }
    public CameraDevice.Settings[] toTypedArray() {
        return (CameraDevice.Settings[])toArray(new CameraDevice.Settings[0]);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcSupport.addPropertyChangeListener(listener);
        for (Object s : toArray()) {
            ((BaseSettings)s).addPropertyChangeListener(listener);
        }
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcSupport.removePropertyChangeListener(listener);
        for (Object s : toArray()) {
            ((BaseSettings)s).removePropertyChangeListener(listener);
        }
    }
}
