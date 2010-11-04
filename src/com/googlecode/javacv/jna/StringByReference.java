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
 *
 *
 * This file contains code copied from Native.java of JNA 3.2.3, which is
 * Copyright (c) 2007, 2008, 2009 Timothy Wall, All Rights Reserved
 */

package com.googlecode.javacv.jna;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.ByReference;

/**
 *
 * @author Samuel Audet
 */
public class StringByReference extends ByReference {
    public StringByReference() {
        this(null);
    }
    public StringByReference(String value) {
        super(1);
        setValue(value);
    }
    public void setValue(String value) {
        if (value == null) {
            setPointer(null);
        } else {
            setPointer(new Memory(Native.toByteArray(value).length));
            getPointer().setString(0, value);
        }
    }
    public String getValue() {
        if (getPointer() == null) {
            return null;
        } else {
            return getPointer().getString(0);
        }
    }
}