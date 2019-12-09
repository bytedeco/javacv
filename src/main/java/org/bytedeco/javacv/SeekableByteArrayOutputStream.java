package org.bytedeco.javacv;

import java.io.ByteArrayOutputStream;

import org.bytedeco.javacv.FFmpegFrameRecorder.SeekableOutputStream;

public class SeekableByteArrayOutputStream extends ByteArrayOutputStream implements SeekableOutputStream {

    long position;

    @Override public void setPosition(long position, int whence) {
        if(position < 0 || position > count || whence != 0)
            throw new IllegalArgumentException();
        this.position = position;
    }

    @Override public synchronized void write(int b) {
        if(position < count) {
            buf[(int) position] = (byte) b; // position < count <= MAX_INT
        } else {
            super.write(b);
        }
        position++;
    }

    @Override public synchronized void write(byte[] b, int off, int len) {
        if(position < count) {
            for (int i = 0 ; i < len ; i++) {
                write(b[off + i]); // should be changed for bigegr arrays
            }
        } else {
            super.write(b, off, len);
            position = count;
        }
    }
}
