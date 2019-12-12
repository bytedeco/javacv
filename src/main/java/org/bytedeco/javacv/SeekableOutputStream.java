package org.bytedeco.javacv;

public interface SeekableOutputStream {

    public void seek(long offset, int whence);
}