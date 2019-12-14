/*
 * Copyright (C) 2019 Sven Vorlauf
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

import java.io.ByteArrayOutputStream;

public class SeekableByteArrayOutputStream extends ByteArrayOutputStream implements Seekable {

    long position;

    @Override public void seek(long position, int whence) {
        if (position < 0 || position > count || whence != 0)
            throw new IllegalArgumentException();
        this.position = position;
    }

    @Override public synchronized void write(int b) {
        if (position < count) {
            buf[(int) position] = (byte) b; // position < count <= MAX_INT
        } else {
            super.write(b);
        }
        position++;
    }

    @Override public synchronized void write(byte[] b, int off, int len) {
        if (position < count) {
            for (int i = 0 ; i < len ; i++) {
                write(b[off + i]); // should be changed for bigegr arrays
            }
        } else {
            super.write(b, off, len);
            position = count;
        }
    }
}
