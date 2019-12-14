package org.bytedeco.javacv;

public interface Seekable {

    public void seek(long offset, int whence);
}
