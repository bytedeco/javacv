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

package org.bytedeco.javacv;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Samuel Audet
 */
public class ProjectorSettings extends BaseSettings {

    public ProjectorSettings() {
        this(false);
    }
    public ProjectorSettings(boolean calibrated) {
        this.calibrated = calibrated;
    }

    boolean calibrated = false;

    public int getQuantity() {
        return size();
    }
    public void setQuantity(int quantity) {
        Object[] a = toArray();
        int i = a.length;
        while (i > quantity) {
            remove(a[i-1]);
            i--;
        }
        while (i < quantity) {
            ProjectorDevice.Settings c = calibrated ? new ProjectorDevice.CalibratedSettings() :
                                                      new ProjectorDevice.CalibrationSettings();
            c.setName("Projector " + String.format("%2d", i));
            c.setScreenNumber(c.getScreenNumber()+i);
            add(c);
            for (PropertyChangeListener l : pcSupport.getPropertyChangeListeners()) {
                ((BaseChildSettings)c).addPropertyChangeListener(l);
            }
            i++;
        }
        pcSupport.firePropertyChange("quantity", a.length, quantity);
    }

    @Override public ProjectorDevice.Settings[] toArray() {
        return (ProjectorDevice.Settings[])toArray(new ProjectorDevice.Settings[size()]);
    }
}
