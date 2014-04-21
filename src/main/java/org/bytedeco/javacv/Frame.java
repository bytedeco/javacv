package org.bytedeco.javacv;

import java.nio.Buffer;

import static org.bytedeco.javacpp.opencv_core.*;

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
