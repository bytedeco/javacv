/*
 * Copyright (C) 2025 shan-luan
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

import com.badlogic.gdx.graphics.Pixmap;

import java.nio.ByteBuffer;

/**
 * A utility class for converting between {@link Pixmap} and {@link Frame}.
 * The alpha channel is not converted and memory cannot be shared.
 *
 * @author shan-luan
 */
public class LibgdxFrameConverter extends FrameConverter<Pixmap> {
    /**
     * Converts a {@link Pixmap} to a {@link Frame}.
     *
     * @param pixmap the Pixmap to convert,RGBA8888 format
     * @return the converted Frame
     */
    @Override
    public Frame convert(Pixmap pixmap) {
        if (pixmap == null) return null;

        Frame frame = new Frame(pixmap.getWidth(), pixmap.getHeight(), Frame.DEPTH_UBYTE, 3, pixmap.getWidth() * 3);

        ByteBuffer pixmapBuffer = pixmap.getPixels().duplicate();
        ByteBuffer frameBuffer = (ByteBuffer) frame.image[0];

        int numPixels = pixmap.getWidth() * pixmap.getHeight();
        for (int i = 0; i < numPixels; i++) {
            byte r = pixmapBuffer.get();
            byte g = pixmapBuffer.get();
            byte b = pixmapBuffer.get();
            pixmapBuffer.position(pixmapBuffer.position() + 1);

            frameBuffer.put(b);
            frameBuffer.put(g);
            frameBuffer.put(r);
        }

        frameBuffer.flip();
        return frame;
    }

    /**
     * Converts a {@link Frame} to a {@link Pixmap}.
     *
     * @param frame the Frame to convert
     * @return the converted Pixmap, RGBA8888 format
     */
    @Override
    public Pixmap convert(Frame frame) {
        if (frame == null || frame.image[0] == null) return null;
        Pixmap pixmap = new Pixmap(frame.imageWidth, frame.imageHeight, Pixmap.Format.RGBA8888);
        ByteBuffer frameBuffer = ((ByteBuffer) frame.image[0]).duplicate();
        ByteBuffer pixmapBuffer = pixmap.getPixels();
        pixmapBuffer.position(0);
        frameBuffer.rewind();

        int numPixels = frame.imageWidth * frame.imageHeight;
        for (int i = 0; i < numPixels; i++) {
            byte b = frameBuffer.get();
            byte g = frameBuffer.get();
            byte r = frameBuffer.get();

            pixmapBuffer.put(r);
            pixmapBuffer.put(g);
            pixmapBuffer.put(b);
            pixmapBuffer.put((byte) -1);// alpha always set to 255
        }

        pixmapBuffer.flip();
        return pixmap;
    }

    /**
     * Converts a {@link Frame} to a {@link Pixmap}.
     * Available only when the format of the Frame is {@link org.bytedeco.ffmpeg.global.avutil
     * #AV_PIX_FMT_RGBA}.
     * Faster than {@link #convert(Frame)}
     *
     * @param frame the Frame to convert
     * @return the converted Pixmap, RGBA8888 format
     */
    public Pixmap fastConvert(Frame frame) {
        if (frame == null || frame.image[0] == null) return null;

        Pixmap pixmap = new Pixmap(frame.imageWidth, frame.imageHeight, Pixmap.Format.RGBA8888);
        ByteBuffer frameBuffer = ((ByteBuffer) frame.image[0]).duplicate();
        ByteBuffer pixmapBuffer = pixmap.getPixels();
        pixmapBuffer.position(0);
        frameBuffer.rewind();
        pixmapBuffer.put(frameBuffer);
        pixmapBuffer.flip();
        return pixmap;
    }
}
