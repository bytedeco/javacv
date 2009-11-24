/*
 * Copyright (C) 2009 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

package name.audet.samuel.javacv.jna;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Samuel Audet
 */
public class Loader {
    public static String load(String[] paths, String[] libnames) {
        Error firstError = null;
        for (int i = 0; i < libnames.length; i++) {
            for (int j = 0; j < paths.length; j++) {
                NativeLibrary.addSearchPath(libnames[i], paths[j]);
            }
            try {
                Native.register(getNativeClass(getCallingClass()), 
                        NativeLibrary.getInstance(libnames[i]));
                return libnames[i];
            } catch (LinkageError e) {
                if (firstError == null) {
                    firstError = e;
                }
                if (i+1 >= libnames.length) {
                    throw firstError;
                }
            }
        }
        return null;
    }

    public static Class getNativeClass(Class cls) {
        Method[] methods = cls.getDeclaredMethods();
        for (int i=0;i < methods.length;i++) {
            if ((methods[i].getModifiers() & Modifier.NATIVE) != 0) {
                return cls;
            }
        }
        int idx = cls.getName().lastIndexOf("$");
        if (idx != -1) {
            String name = cls.getName().substring(0, idx);
            try {
                return getNativeClass(Class.forName(name, true, cls.getClassLoader()));
            }
            catch(ClassNotFoundException e) {
            }
        }
        throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + cls + ")");
    }

    public static Class getCallingClass() {
        Class[] context = new SecurityManager() {
            public Class[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        if (context.length < 4) {
            throw new IllegalStateException("This method must be called from the static initializer of a class");
        }
        return context[3];
    }
}
