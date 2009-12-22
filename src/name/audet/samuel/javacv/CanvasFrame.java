/*
 * Copyright (C) 2009 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.audet.samuel.javacv;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import static name.audet.samuel.javacv.jna.cxcore.*;

/**
 *
 * @author Samuel Audet
 * 
 * Make sure OpenGL is enabled to get low latency, something like
 *      export _JAVA_OPTIONS=-Dsun.java2d.opengl=True
 *
 */
public class CanvasFrame extends JFrame {
    public static String[] getScreenDescriptions() {
        GraphicsDevice[] screens = getScreenDevices();
        String[] descriptions = new String[screens.length];
        for (int i = 0; i < screens.length; i++) {
            descriptions[i] = screens[i].getIDstring();
        }
        return descriptions;
    }

    public static DisplayMode getDisplayMode(int screenNumber) {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= 0 && screenNumber < screens.length) {
            return screens[screenNumber].getDisplayMode();
        } else {
            return null;
        }
    }
    public static GraphicsDevice getScreenDevice(int screenNumber) throws Exception {
        GraphicsDevice[] screens = getScreenDevices();
        if (screenNumber >= screens.length) {
            throw new Exception("CanvasFrame Error: Screen number " + screenNumber + " not found. " +
                                "There are only " + screens.length + " screens.");
        }
        return screens[screenNumber];//.getDefaultConfiguration();
    }
    public static GraphicsDevice[] getScreenDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    public CanvasFrame(boolean fullScreen) {
        this(fullScreen, null, null, null);
    }
    public CanvasFrame(boolean fullScreen,  String title) {
        this(fullScreen, title, null, null);
    }
    public CanvasFrame(boolean fullScreen, int screenNumber) throws Exception {
        this(fullScreen, null, getScreenDevice(screenNumber).getDefaultConfiguration(), null);
    }
    public CanvasFrame(boolean fullScreen, GraphicsConfiguration gc) {
        this(fullScreen, null, gc, null);
    }
    public CanvasFrame(boolean fullScreen, int screenNumber, DisplayMode displayMode) throws Exception {
        this(fullScreen, null, getScreenDevice(screenNumber).getDefaultConfiguration(), displayMode);
    }
    public CanvasFrame(boolean fullScreen, GraphicsConfiguration gc, DisplayMode displayMode) {
        this(fullScreen, null, gc, displayMode);
    }
    public CanvasFrame(boolean fullScreen,  String title,
            int screenNumber, DisplayMode displayMode) throws Exception {
        this(fullScreen, title, getScreenDevice(screenNumber).getDefaultConfiguration(), displayMode);
    }
    public CanvasFrame(boolean fullScreen, String title,
            GraphicsConfiguration gc, DisplayMode displayMode) {
        super(title, gc);
        init(fullScreen, displayMode);
    }

    @Override public void dispose() {
        bufferStrategy.dispose();
        super.dispose();
    }

    private void init(boolean fullScreen, DisplayMode displayMode) {
        GraphicsDevice gd = getGraphicsConfiguration().getDevice();
        int w = displayMode == null ? 0 : displayMode.getWidth();
        int h = displayMode == null ? 0 : displayMode.getHeight();
        int b = displayMode == null ? 0 : displayMode.getBitDepth();
        int r = displayMode == null ? 0 : displayMode.getRefreshRate();
        DisplayMode d = gd.getDisplayMode();
        displayMode = new DisplayMode(w > 0 ? w : d.getWidth(), h > 0 ? h : d.getHeight(),
                                      b > 0 ? b : d.getBitDepth(), r > 0 ? r : d.getRefreshRate());
        if (!displayMode.equals(d)) {
            gd.setDisplayMode(displayMode);
        }
        if (fullScreen) {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.NONE);
            setResizable(false);
            gd.setFullScreenWindow(this);
        }

        // must be called after the fullscreen stuff, but before
        // getting our BufferStrategy
        setVisible(true);

        canvas = new Canvas();
        if (fullScreen) {
            canvas.setSize(getSize());
        }
        getContentPane().add(canvas);
        canvas.setVisible(true);
        canvas.createBufferStrategy(2);
        canvas.setIgnoreRepaint(true);
        bufferStrategy = canvas.getBufferStrategy();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    synchronized (CanvasFrame.this) {
                        keyEvent = e;
                        CanvasFrame.this.notify();
                    }
                }
                return false;
            }
        });
    }

    public DisplayMode getDisplayMode() {
        return getGraphicsConfiguration().getDevice().getDisplayMode();
    }

    // used for example as debugging console...
    public static CanvasFrame global = null;

    // maximum is 60 ms on Metacity and Windows XP, and 90 ms on Compiz Fusion,
    // but set the default to twice as much for safety...
    public static final long DEFAULT_LATENCY = 120;
    private long latency = DEFAULT_LATENCY;

    private KeyEvent keyEvent = null;

    private Canvas canvas = null;
    private BufferStrategy bufferStrategy = null;

    public long getLatency() {
        // if there exists some way to estimate the latency in real time,
        // add it here
        return latency;
    }
    public void setLatency(long latency) {
        this.latency = latency;
    }
    public void waitLatency() {
        try {
            Thread.sleep(getLatency());
        } catch (InterruptedException ex) { }
    }

    public KeyEvent waitKey() {
        return waitKey(0);
    }
    public synchronized KeyEvent waitKey(int delay) {
        try {
            keyEvent = null;
            wait(delay);
        } catch (InterruptedException ex) { }
        KeyEvent e = keyEvent;
        keyEvent = null;
        return e;
    }

    public Canvas getCanvas() {
        return canvas;
    }
    public BufferStrategy getBufferStrategy() {
        return bufferStrategy;
    }

    public Graphics2D acquireGraphics() {
        return (Graphics2D)bufferStrategy.getDrawGraphics();
    }
    public void releaseGraphics(Graphics2D g) {
        g.dispose();
        bufferStrategy.show();
    }

    public Dimension getCanvasSize() {
        return canvas.getSize();
    }
    public void setCanvasSize(int width, int height) {
        // there is apparently a bug in Java code for Linux, and what happens goes like this:
        // 1. Canvas gets resized, checks the visible area (has not changed) and updates
        // BufferStrategy with the same size. 2. pack() resizes the frame and changes
        // the visible area 3. We call Canvas.setSize() with different dimensions, to make
        // it check the visible area and reallocate the BufferStrategy almost correctly
        // 4. We resize the Canvas to the desired size... pff..
        setExtendedState(NORMAL); // force unmaximization.. 
        canvas.setSize(width, height);
        pack();
        canvas.setSize(width+1, height+1);
        canvas.setSize(width, height);
    }

    public void showImage(Image image, double scale) {
        if (image == null)
            return;
        final int w = (int)Math.round(image.getWidth(null)*scale);
        final int h = (int)Math.round(image.getHeight(null)*scale);

        if (canvas.getWidth() != w || canvas.getHeight() != h) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        setCanvasSize(w, h);
                    }
                });
            } catch (Exception ex) { }
        }
        Graphics2D g = acquireGraphics();
        g.drawImage(image, 0, 0, w, h, null);
        releaseGraphics(g);
    }
    public void showImage(Image image) {
        showImage(image, 1.0);
    }
    public void showImage(IplImage image) {
        showImage(image.getBufferedImage());
    }
    public void showImage(IplImage image, double scale) {
        showImage(image.getBufferedImage(), scale);
    }

    public void showColor(Color color) {
        Graphics2D g = acquireGraphics();
        g.setColor(color);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        releaseGraphics(g);
    }
    public void showColor(CvScalar color) {
        showColor(new Color((int)color.getRed(), (int)color.getGreen(), (int)color.getBlue()));
    }

    // this should not be called from the event dispatch thread, but if it is,
    // it should still work... it should simply be slower as it will timeout
    // waiting for the moved event
    public static void tile(final CanvasFrame[] frames) {

        class MovedListener extends ComponentAdapter {
            boolean moved = false;
            @Override public void componentMoved(ComponentEvent e) {
                moved = true;
                Component c = e.getComponent();
                synchronized (c) {
                    c.notify();
                }
            }
        }
        final MovedListener movedListener = new MovedListener();

        // layout the canvas frames for the cameras in tiles
        int canvasCols = (int)Math.round(Math.sqrt(frames.length));
        if (canvasCols*canvasCols < frames.length) {
            // if we don't get a square, favor horizontal layouts
            // since screens are usually wider than cameras...
            // and we also have title bars, tasks bar, menus, etc that
            // takes up vertical space
            canvasCols++;
        }
        int canvasX = 0, canvasY = 0;
        int canvasMaxY = 0;
        for (int i = 0; i < frames.length; i++) {
            final int n = i;
            final int x = canvasX;
            final int y = canvasY;
            try {
                movedListener.moved = false;
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        frames[n].addComponentListener(movedListener);
                        frames[n].setLocation(x, y);
                    }
                });
                int count = 0;
                while (!movedListener.moved && count < 5) {
                    // wait until the window manager actually places our window...
                    // wait a maximum of 500 ms since this does not work if
                    // we are on the event dispatch thread. also some window
                    // managers like Windows do not always send us the event...
                    synchronized (frames[n]) {
                        frames[n].wait(100);
                    }
                    count++;
                }
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        frames[n].removeComponentListener(movedListener);
                    }
                });
            } catch (Exception ex) { }
            canvasX = frames[i].getX()+frames[i].getWidth();
            canvasMaxY = Math.max(canvasMaxY, frames[i].getY()+frames[i].getHeight());
            if ((i+1)%canvasCols == 0) {
                canvasX = 0;
                canvasY = canvasMaxY;
            }
        }
    }

}
