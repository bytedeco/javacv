/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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

import com.googlecode.javacpp.Pointer;
import java.awt.Component;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class JavaCvErrorCallback extends CvErrorCallback {

    public JavaCvErrorCallback() {
        this(false);
    }
    public JavaCvErrorCallback(boolean showDialog) {
        this(showDialog, null);
    }
    public JavaCvErrorCallback(boolean showDialog, Component parent) {
        this(showDialog, parent, 0);
    }
    public JavaCvErrorCallback(boolean showDialog, Component parent, int rc) {
        this.parent = parent;
        this.showDialog = showDialog;
        this.rc = rc;
    }

    private long lastErrorTime = 0;
    private Component parent;
    private boolean showDialog;
    private int rc;

    @Override public int call(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata) {
        final String title = "OpenCV Error";
        final String message = cvErrorStr(status) +
                " (" + err_msg + ")\nin function " +
                func_name + ", " + file_name + "(" + line + ")";
        Logger.getLogger(JavaCvErrorCallback.class.getName()).log(Level.SEVERE,
                title + ": " + message, new Exception("Strack trace"));
        if (showDialog) {
            // Show no more than 1 dialog per second since we cannot stop OpenCV
            // from processing and throwing more errors. Maybe in the future
            // when JavaCPP allows us to throw Exceptions across...
            if (System.currentTimeMillis() - lastErrorTime > 1000) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(parent, message,
                                title, JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            lastErrorTime = System.currentTimeMillis();
        }
        return rc; // 0 = please don't terminate
    }
}
