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

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

/**
 *
 * @author Samuel Audet
 */
public class size_t extends IntegerType {
    public size_t() { 
        this(0);
    }
    public size_t(long value) { 
        super(Native.SIZE_T_SIZE, value);
    }
}