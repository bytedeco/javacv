/*
 * Copyright (C) 2012 Samuel Audet
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

package org.bytedeco.javacv;

/**
 *
 * @author Samuel Audet
 */
public class BufferRing<B extends BufferRing.ReleasableBuffer> {
    public BufferRing(BufferFactory<B> factory, int size) {
        buffers = new Object[size];
        for (int i = 0; i < size; i++) {
            buffers[i] = factory.create();
        }
        position = 0;
    }

    public interface BufferFactory<B extends ReleasableBuffer> {
        B create();
    }

    public interface ReleasableBuffer {
        void release();
    }

    private Object[] buffers;
    private int position;

    public int capacity() {
        return buffers.length;
    }

    public int position() {
        return position;
    }
    public BufferRing position(int position) {
        this.position = ((position % buffers.length) + buffers.length) % buffers.length;
        return this;
    }

    @SuppressWarnings("unchecked")
    public B get() {
        return (B)buffers[position];
    }

    @SuppressWarnings("unchecked")
    public B get(int offset) {
        return (B)buffers[((position + offset) % buffers.length + buffers.length) % buffers.length];
    }

    @SuppressWarnings("unchecked")
    public void release() {
        for (int i = 0; i < buffers.length; i++) {
            ((B)buffers[i]).release();
        }
        buffers = null;
    }
}
