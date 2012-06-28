package com.googlecode.javacv;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.nio.ByteBuffer;

/**
 *
 * @author Samuel Audet
 */
public class Frame {
    public IplImage image;     // for video frame
    public ByteBuffer samples; // for audio frame
}
