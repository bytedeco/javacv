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

package com.googlecode.javacv;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.util.ListResourceBundle;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Samuel Audet
 */
public abstract class BaseSettings implements Comparable<BaseSettings> {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public int compareTo(BaseSettings o) {
        return getName().compareTo(o.getName());
    }

    protected String getName() {
        return "";
    }

    public static class PropertyVetoExceptionThatNetBeansLikes extends PropertyVetoException implements Callable {
        public PropertyVetoExceptionThatNetBeansLikes(String mess, PropertyChangeEvent evt)  {
            super(mess, evt);
        }
        public Object call() throws Exception {
            LogRecord lg = new LogRecord(Level.ALL, getMessage());
            lg.setResourceBundle(new ListResourceBundle() {
                protected Object[][] getContents() {
                    return new Object[][] { {getMessage(), getMessage()} };
                }
            });
            return new LogRecord[] { lg };
        }
    }

}
