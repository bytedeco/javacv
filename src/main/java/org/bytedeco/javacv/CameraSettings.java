/*
 * Copyright (C) 2009-2011 Samuel Audet
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

import java.beans.PropertyVetoException;

/**
 *
 * @author Samuel Audet
 */
public class CameraSettings extends BaseSettings {

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

    @Override public CameraDevice.Settings[] toArray() {
        return (CameraDevice.Settings[])toArray(new CameraDevice.Settings[size()]);
    }
}
