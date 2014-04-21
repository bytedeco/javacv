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

package org.bytedeco.javacv;

import java.beans.PropertyChangeListener;
import java.beans.beancontext.BeanContextSupport;
import java.util.Arrays;

/**
 *
 * @author Samuel Audet
 */
public class BaseSettings extends BeanContextSupport implements Comparable<BaseSettings> {

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcSupport.addPropertyChangeListener(listener);
        for (Object s : toArray()) {
            if (s instanceof BaseChildSettings) {
                ((BaseChildSettings)s).addPropertyChangeListener(listener);
            } else if (s instanceof BaseSettings) {
                ((BaseSettings)s).addPropertyChangeListener(listener);
            }
        }
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcSupport.removePropertyChangeListener(listener);
        for (Object s : toArray()) {
            if (s instanceof BaseChildSettings) {
                ((BaseChildSettings)s).removePropertyChangeListener(listener);
            } else if (s instanceof BaseSettings) {
                ((BaseSettings)s).addPropertyChangeListener(listener);
            }
        }
    }

    public int compareTo(BaseSettings o) {
        return getName().compareTo(o.getName());
    }

    protected String getName() {
        return "";
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
}
