package com.googlecode.javacv;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.nio.Buffer;

/**
 *
 * @author Samuel Audet
 */
public class Frame {
    public boolean keyFrame;
    public IplImage image;   // for video frame
    public int sampleRate, audioChannels;
    public Buffer[] samples; // for audio frame
    public Object opaque;
}
