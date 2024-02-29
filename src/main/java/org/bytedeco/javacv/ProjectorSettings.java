/*
 * Copyright (C) 2009-2010 Samuel Audet
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
