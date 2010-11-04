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

import com.sun.jna.Pointer;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JOptionPane;

import static com.googlecode.javacv.jna.cxcore.*;

/**
 *
 * @author Samuel Audet
 *
 *  VERY IMPORTANT:
 *  need to keep a reference somewhere so it doesn't get garbage collected...
 *
 */
public class JavaCvErrorCallback implements CvErrorCallback {

    public JavaCvErrorCallback(boolean showDialog, Component parent, int rc) {
        this.parent = parent;
        this.showDialog = showDialog;
        this.rc = rc;
    }
    public JavaCvErrorCallback(boolean showDialog, Component parent) {
        this(showDialog, parent, 0);
    }
    public JavaCvErrorCallback(boolean showDialog) {
        this(showDialog, null);
    }
    public JavaCvErrorCallback() {
        this(false);
    }

    private long lastErrorTime = 0;
    private Component parent;
    private boolean showDialog;
    private int rc;

    public int callback(int status, String func_name, String err_msg,
            String file_name, int line, Pointer userdata) {
        final String title = "OpenCV Error";
        final String message = cvErrorStr(status) +
                " (" + err_msg + ")\nin function " +
                func_name + ", " + file_name + "(" + line + ")";
        System.err.println(title + ": " + message);
        Thread.dumpStack();
        if (showDialog) {
            // no more than 1 dialog per second since we cannot stop OpenCV
            // from processing and throwing more errors. Maybe in the future
            // when JNA allows us to throw Exceptions across...
            if (System.currentTimeMillis()-lastErrorTime > 1000) {
                if (EventQueue.isDispatchThread()) {
                    JOptionPane.showMessageDialog(parent, message,
                            title, JOptionPane.ERROR_MESSAGE);
                } else try {
                    EventQueue.invokeAndWait(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(parent, message,
                                    title, JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) { }
            }
            lastErrorTime = System.currentTimeMillis();
        }
        return rc; // 0 = please don't terminate
    }

    // VERY IMPORTANT:
    // need to keep a reference here so it doesn't get garbage collected...
    static private JavaCvErrorCallback notGarbage = null;
    public CvErrorCallback redirectError() {
        notGarbage = this;
        return cvRedirectError(this, null, null);
    }
}
