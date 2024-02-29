/*
 * Copyright (C) 2009-2012 Samuel Audet
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

import java.awt.Component;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
public class JavaCvErrorCallback extends CvErrorCallback {

    static JavaCvErrorCallback instance;

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
        instance = this;
        this.parent = parent;
        this.showDialog = showDialog;
        this.rc = rc;
    }

    private long lastErrorTime = 0;
    private Component parent;
    private boolean showDialog;
    private int rc;

    @Override public int call(int status, BytePointer func_name, BytePointer err_msg,
            BytePointer file_name, int line, Pointer userdata) {
        final String title = "OpenCV Error";
        final String message = cvErrorStr(status) +
                " (" + err_msg.getString() + ")\nin function " +
                func_name.getString() + ", " + file_name.getString() + "(" + line + ")";
        Logger.getLogger(JavaCvErrorCallback.class.getName()).log(Level.SEVERE,
                title + ": " + message, new java.lang.Exception("Strack trace"));
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
