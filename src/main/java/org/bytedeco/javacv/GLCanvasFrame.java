/*
 * Copyright (C) 2012 Samuel Audet
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

import com.jogamp.opencl.CLImage2d;
import com.jogamp.opencl.gl.CLGLImage2d;
import com.jogamp.opengl.util.Gamma;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

/**
 *
 * @author Samuel Audet
 */
public class GLCanvasFrame extends CanvasFrame {
    public GLCanvasFrame(String title) {
        this(title, 0.0);
    }
    public GLCanvasFrame(String title, double gamma) {
        super(title, gamma);
        init(false, null, null);
    }

    public GLCanvasFrame(String title, GraphicsConfiguration gc,
            GLCapabilitiesImmutable caps, GLContext shareWith) {
        this(title, gc, caps, shareWith, 0.0);
    }
    public GLCanvasFrame(String title, GraphicsConfiguration gc,
            GLCapabilitiesImmutable caps, GLContext shareWith, double gamma) {
        super(title, gc, gamma);
        init(false, caps, shareWith);
    }

    public GLCanvasFrame(String title, int screenNumber, DisplayMode displayMode) throws Exception {
        this(title, screenNumber, displayMode, 0.0);
    }
    public GLCanvasFrame(String title, int screenNumber, DisplayMode displayMode, double gamma) throws Exception {
        super(title, screenNumber, displayMode, gamma);
        init(true, null, null);
    }

    public GLCanvasFrame(String title, int screenNumber, DisplayMode displayMode,
            GLCapabilitiesImmutable caps, GLContext shareWith) throws Exception {
        this(title, screenNumber, displayMode, caps, shareWith, 0.0);
    }
    public GLCanvasFrame(String title, int screenNumber, DisplayMode displayMode,
            GLCapabilitiesImmutable caps, GLContext shareWith, double gamma) throws Exception {
        super(title, screenNumber, displayMode, gamma);
        init(true, caps, shareWith);
    }

    private void init(final boolean fullScreen,
            final GLCapabilitiesImmutable caps, final GLContext shareWith) {
        Runnable r = new Runnable() { public void run() {
            String wasErase = System.setProperty("sun.awt.noerasebackground", "true");

            canvas = new GLCanvas(caps, shareWith);
            ((GLCanvas)canvas).addGLEventListener(eventListener);
            if (fullScreen) {
                canvas.setSize(getSize());
                needInitialResize = false;
            } else {
                canvas.setSize(1, 1); // or we do not get a GLContext
                needInitialResize = true;
            }
            getContentPane().add(canvas);
            canvas.setVisible(true);

            if (wasErase != null) {
                System.setProperty("sun.awt.noerasebackground", wasErase);
            } else {
                System.clearProperty("sun.awt.noerasebackground");
            }
        }};

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (java.lang.Exception ex) { }
        }
    }

    @Override protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) { }

    private int[] params = new int[2];
    private Color color = null;
    private int width, height, format, type;
    private Buffer buffer = null;
    private int frameBuffer = 0, renderBuffer = 0;

    private GLEventListener eventListener = new GLEventListener() {
        public void init(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            gl.setSwapInterval(1); // Sync to VBlank

            if (inverseGamma != 1.0) {
                // Yeah baby, gamma correction in hardware!
                Gamma.setDisplayGamma(gl, (float)inverseGamma, 0, 1);
            }
            gl.glGenFramebuffers(1, params, 0);
            frameBuffer = params[0];
        }
        public void dispose(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            params[0] = frameBuffer;
            gl.glDeleteFramebuffers(1, params, 0);
            if (inverseGamma != 1.0) {
                Gamma.resetDisplayGamma(gl);
            }
        }
        public void display(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            if (color != null) {
                gl.glClearColor(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1f);
                gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
            } else if (buffer != null) {
                if (isResizable() && needInitialResize) {
                    int w = (int)Math.round(width *initialScale);
                    int h = (int)Math.round(height*initialScale);
                    setCanvasSize(w, h);
                }
                gl.glWindowPos2i(0, canvas.getHeight());
                gl.glPixelZoom((float)canvas.getWidth()/width, -(float)canvas.getHeight()/height);
                // XXX: Tell OpenGL about the alignment of buffer via glPixelStore(), somehow...
                gl.glDrawPixels(width, height, format, type, buffer);
            } else if (renderBuffer > 0) {
                gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, renderBuffer);
                gl.glGetRenderbufferParameteriv(GL2.GL_RENDERBUFFER,
                        GL2.GL_RENDERBUFFER_WIDTH, params, 0);
                gl.glGetRenderbufferParameteriv(GL2.GL_RENDERBUFFER,
                        GL2.GL_RENDERBUFFER_HEIGHT, params, 1);
                if (isResizable() && needInitialResize) {
                    int w = (int)Math.round(params[0]*initialScale);
                    int h = (int)Math.round(params[1]*initialScale);
                    setCanvasSize(w, h);
                }
                gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, frameBuffer);
                gl.glFramebufferRenderbuffer(GL2.GL_READ_FRAMEBUFFER,
                        GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, renderBuffer);
                // Often GL_RENDERBUFFER_WIDTH == 0 and GL_RENDERBUFFER_HEIGHT == 1,
                // while glCheckFramebufferStatus() returns
                // GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT here,
                // but, for a given object, it either never happens or
                // always happens ... NVIDIA driver bug?
//System.out.println(params[0] + " " + params[1] +
//        " glCheckFramebufferStatus = " + gl.glCheckFramebufferStatus(GL2.GL_READ_FRAMEBUFFER));
                assert gl.glCheckFramebufferStatus(GL2.GL_READ_FRAMEBUFFER) == GL2.GL_FRAMEBUFFER_COMPLETE;
                gl.glBlitFramebuffer(0, 0,  params[0], params[1],
                        0, canvas.getHeight(),  canvas.getWidth(), 0,
                        GL2.GL_COLOR_BUFFER_BIT, GL2.GL_LINEAR);
            }
        }
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }
    };

    public GLCanvas getGLCanvas() {
        return (GLCanvas)canvas;
    }

    @Override public void showColor(Color color) {
        this.color = color;
        this.buffer = null;
        getGLCanvas().display();
    }

    @Override public void showImage(Frame frame) {
        showImage(frame, false);
    }
    @Override public void showImage(Frame frame, boolean flipChannels) {
        if (flipChannels) {
            throw new RuntimeException("GLCanvasFrame does not support channel flipping.");
        }
        if (frame == null) {
            return;
        }
        this.color  = null;
        this.width  = frame.imageWidth;
        this.height = frame.imageHeight;
        this.buffer = frame.image[0];
        switch (frame.imageDepth) {
            case Frame.DEPTH_BYTE:   this.type = GL2.GL_BYTE;           break;
            case Frame.DEPTH_UBYTE:  this.type = GL2.GL_UNSIGNED_BYTE;  break;
            case Frame.DEPTH_SHORT:  this.type = GL2.GL_SHORT;          break;
            case Frame.DEPTH_USHORT: this.type = GL2.GL_UNSIGNED_SHORT; break;
            case Frame.DEPTH_INT:    this.type = GL2.GL_INT;            break;
            case Frame.DEPTH_FLOAT:  this.type = GL2.GL_FLOAT;          break;
            case Frame.DEPTH_DOUBLE: this.type = GL2.GL_DOUBLE;         break;
            default: assert false;
        }
        switch (frame.imageChannels) {
            case 1: this.format = GL2.GL_LUMINANCE; break;
            case 2: this.format = GL2.GL_RG;        break;
            case 3: this.format = GL2.GL_RGB;       break;
            case 4: this.format = GL2.GL_RGBA;      break;
            default: assert false;
        }
        getGLCanvas().display();
    }
    @Override public void showImage(Image image) {
        if (!(image instanceof BufferedImage)) {
            throw new RuntimeException("GLCanvasFrame does not support " + image + ", BufferedImage required.");
        }
        showImage((BufferedImage)image);
    }
    public void showImage(BufferedImage image) {
        if (image == null) {
            return;
        }
        this.color = null;
        this.width  = image.getWidth();
        this.height = image.getHeight();

        DataBuffer buffer = image.getRaster().getDataBuffer();
        if (buffer instanceof DataBufferByte) {
            this.buffer = ByteBuffer.wrap(((DataBufferByte)buffer).getData());
            this.type = GL2.GL_UNSIGNED_BYTE;
        } else if (buffer instanceof DataBufferDouble) {
            this.buffer = DoubleBuffer.wrap(((DataBufferDouble)buffer).getData());
            this.type = GL2.GL_DOUBLE;
        } else if (buffer instanceof DataBufferFloat) {
            this.buffer = FloatBuffer.wrap(((DataBufferFloat)buffer).getData());
            this.type = GL2.GL_FLOAT;
        } else if (buffer instanceof DataBufferInt) {
            this.buffer = IntBuffer.wrap(((DataBufferInt)buffer).getData());
            this.type = GL2.GL_INT;
        } else if (buffer instanceof DataBufferShort) {
            this.buffer = ShortBuffer.wrap(((DataBufferShort)buffer).getData());
            this.type = GL2.GL_SHORT;
        } else if (buffer instanceof DataBufferUShort) {
            this.buffer = ShortBuffer.wrap(((DataBufferUShort)buffer).getData());
            this.type = GL2.GL_UNSIGNED_SHORT;
        } else {
            assert false;
        }
        switch (image.getSampleModel().getNumBands()) {
            case 1: this.format = GL2.GL_LUMINANCE; break;
            case 2: this.format = GL2.GL_RG;        break;
            case 3: this.format = GL2.GL_RGB;       break;
            case 4: this.format = GL2.GL_RGBA;      break;
            default: assert false;
        }
        getGLCanvas().display();
    }
    public void showImage(int renderBuffer) {
        if (renderBuffer <= 0) {
            return;
        }
        this.color = null;
        this.buffer = null;
        this.renderBuffer = renderBuffer;
        getGLCanvas().display();
    }

    private static GLCanvasFrame canvasFrame;
    public static void main(String[] args) throws java.lang.Exception {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    canvasFrame = new GLCanvasFrame("Some Title"/*, context.getGLContext()*/);
                    canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    //canvasFrame.setCanvasSize(640, 480);
                    canvasFrame.showColor(Color.BLUE);
                } catch (java.lang.Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        final JavaCVCL context = new JavaCVCL(canvasFrame.getGLCanvas().getContext());
        final IplImage image = cvLoadImageBGRA("/usr/share/opencv/samples/c/lena.jpg"/*args[0]*/);
        //final IplImage image = cvLoadImage("/usr/share/opencv/samples/c/lena.jpg"/*args[0]*/, 0);
        //final IplImage image = IplImage.create(640, 480, IPL_DEPTH_32F, 4);
        final CLGLImage2d imageCLGL = context.createCLGLImageFrom(image);
        //final CLImage2d imageCL = context.createCLImageFrom(image);
        context.acquireGLObject(imageCLGL);
        context.writeImage(imageCLGL, image, true);
        context.releaseGLObject(imageCLGL);
        //System.out.println(imageCLGL.getFormat());
        //System.exit(0);

        canvasFrame.setCanvasScale(0.5);
        for (int i = 0; i < 1000; i++) {
            canvasFrame.showImage(imageCLGL.getGLObjectID());
            Thread.sleep(10);
            canvasFrame.showColor(Color.RED);
            Thread.sleep(10);
        }
        canvasFrame.waitKey();
        context.release();
        System.exit(0);
    }
}
