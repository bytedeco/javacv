/*
 * Copyright (C) 2018 Samuel Audet, Johan Vos
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 *
 * Convert Frames into JavaFX images and vice versa
 * @author johan
 */
public class JavaFXFrameConverter extends FrameConverter<Image> {

    @Override
    public Frame convert(Image f) {
        return null;
    }

    @Override
    public Image convert(Frame frame) {
        int iw = frame.imageWidth;
        int ih = frame.imageHeight;
        PixelReader pr = new FramePixelReader(frame);
        WritableImage answer = new WritableImage(pr, iw, ih);
        return answer;
    }

    class FramePixelReader implements PixelReader {

        Frame frame;

        FramePixelReader(Frame f) {
            this.frame = f;
        }

        @Override
        public PixelFormat getPixelFormat() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getArgb(int x, int y) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Color getColor(int x, int y) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T extends Buffer> void getPixels(int x, int y, int w, int h, WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) {
            int fss = frame.imageStride;
            if (buffer instanceof ByteBuffer) {

                ByteBuffer bb = (ByteBuffer) buffer;
                int tot = 0;
                ByteBuffer b = (ByteBuffer) frame.image[0];
                for (int i = y; i < y + h; i++) {
                    for (int j = x; j < x + w; j++) {
                        int base = 3 * j;
                        bb.put(b.get(fss * i + base));
                        bb.put(b.get(fss * i + base + 1));
                        bb.put(b.get(fss * i + base + 2));
                        bb.put((byte) 255);
                        tot = tot + 4;
                    }
                }

            } else throw new UnsupportedOperationException ("We only support bytebuffers at the moment");
        }

        @Override
        public void getPixels(int x, int y, int w, int h, WritablePixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
