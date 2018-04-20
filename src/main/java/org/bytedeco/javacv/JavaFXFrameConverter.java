package org.bytedeco.javacv;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

/**
 *
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

            }
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
