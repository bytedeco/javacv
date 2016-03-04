/*
 * Copyright (C) 2013 Greg Perry
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

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

public class IPCameraFrameGrabber extends FrameGrabber {

    /*
     * excellent reference - http://www.jpegcameras.com/ foscam url
     * http://host/videostream.cgi?user=username&pwd=password
     * http://192.168.0.59:60/videostream.cgi?user=admin&pwd=password android ip
     * cam http://192.168.0.57:8080/videofeed
     */

    private static Exception loadingException = null;

    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.javacpp.opencv_highgui.class);
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + IPCameraFrameGrabber.class, t);
            }
        }
    }

    private final FrameConverter converter = new OpenCVFrameConverter.ToIplImage();
    private final URL url;
    private final int connectionTimeout;
    private final int readTimeout;
    private DataInputStream input;
    private byte[] pixelBuffer = new byte[1024];
    private IplImage decoded = null;

    /**
     * @param url          The URL to create the camera connection with.
     * @param startTimeout How long should this wait on the connection while trying to {@link #start()} before
     *                     timing out.
     *                     If this value is less than zero it will be ignored.
     *                     {@link URLConnection#setConnectTimeout(int)}
     * @param grabTimeout  How long should grab wait while reading the connection before timing out.
     *                     If this value is less than zero it will be ignored.
     *                     {@link URLConnection#setReadTimeout(int)}
     * @param timeUnit     The time unit to use for the connection and read timeout.
     *                     If this value is null then the start timeout and grab timeout will be ignored.
     */
    public IPCameraFrameGrabber(URL url, int startTimeout, int grabTimeout, TimeUnit timeUnit) {
        super(); // Always good practice to do this
        if (url == null) {
            throw new IllegalArgumentException("URL can not be null");
        }
        this.url = url;
        if (timeUnit == null) {
            this.connectionTimeout = toIntExact(TimeUnit.MILLISECONDS.convert(startTimeout, timeUnit));
            this.readTimeout = toIntExact(TimeUnit.MILLISECONDS.convert(grabTimeout, timeUnit));
        } else {
            this.connectionTimeout = -1;
            this.readTimeout = -1;
        }
    }

    public IPCameraFrameGrabber(String urlstr, int connectionTimeout, int readTimeout, TimeUnit timeUnit) throws MalformedURLException {
        this(new URL(urlstr), connectionTimeout, readTimeout, timeUnit);
    }

    /**
     * @param urlstr A string to be used to create the URL.
     * @throws MalformedURLException if the urlstr is a malformed URL
     * @deprecated By not setting the connection timeout and the read timeout if your network ever crashes
     * then {@link #start()} or {@link #grab()} can hang for upwards of 45 to 60 seconds before failing.
     * You should always explicitly set the connectionTimeout and readTimeout so that your application can
     * respond appropriately to a loss or failure to connect.
     */
    @Deprecated
    public IPCameraFrameGrabber(String urlstr) throws MalformedURLException {
        this(new URL(urlstr), -1, -1, null);
    }

    @Override
    public void start() throws Exception {
        try {
            /*
             * We don't need to keep a reference to the connection
             * after it is opened in the parent class.
             * It never uses it outside of start.
             */
            final URLConnection connection = url.openConnection();
            // If the class was initialized with timeout values then configure those
            if (connectionTimeout >= 0) {
                connection.setConnectTimeout(connectionTimeout);
            }
            if (readTimeout >= 0) {
                connection.setReadTimeout(readTimeout);
            }
            input = new DataInputStream(connection.getInputStream());
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws Exception {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                throw new Exception(e.getMessage(), e);
            } finally {
                // Close may have failed but there's really nothing we can do about it at this point
                input = null;
                // Don't set the url to null, it may be needed to restart this object
                releaseDecoded();
            }
        }
    }

    @Override
    public void trigger() throws Exception {
    }

    @Override
    public Frame grab() throws Exception {
        try {
            final byte[] b = readImage();
            final CvMat mat = cvMat(1, b.length, CV_8UC1, new BytePointer(b));
            releaseDecoded();
            return converter.convert(decoded = cvDecodeImage(mat));
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public BufferedImage grabBufferedImage() throws IOException {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(readImage()));
        return bi;
    }

    /**
     * Ensures that if the decoded image is not null that it gets released and set to null.
     * If the image was not set to null then trying to release a null pointer will cause  a
     * segfault.
     */
    private void releaseDecoded() {
        if (decoded != null) {
            cvReleaseImage(decoded);
            decoded = null;
        }
    }

    private byte[] readImage() throws IOException {
        final StringBuffer sb = new StringBuffer();
        int c;
        // read http subheader
        while ((c = input.read()) != -1) {
            if (c > 0) {
                sb.append((char) c);
                if (c == 13) {
                    sb.append((char) input.read());// '10'+
                    c = input.read();
                    sb.append((char) c);
                    if (c == 13) {
                        sb.append((char) input.read());// '10'
                        break; // done with subheader
                    }

                }
            }
        }
        // find embedded jpeg in stream
        /*
         * Some cameras return headers 'content-length' using different casing
         * Eg. Axis cameras return 'Content-Length:' while TrendNet cameras return 'content-length:'
         */
        final String subheader = sb.toString().toLowerCase();
        //log.debug(subheader);

        // Yay! - server was nice and sent content length
        int c0 = subheader.indexOf("content-length: ");
        final int c1 = subheader.indexOf('\r', c0);

        if (c0 < 0) {
            //log.info("no content length returning null");
            throw new EOFException("The camera stream ended unexpectedly");
        }

        c0 += 16;
        final int contentLength = Integer.parseInt(subheader.substring(c0, c1).trim());
        //log.debug("Content-Length: " + contentLength);

        // adaptive size - careful - don't want a 2G jpeg
        ensureBufferCapacity(contentLength);

        input.readFully(pixelBuffer, 0, contentLength);
        input.read();// \r
        input.read();// \n
        input.read();// \r
        input.read();// \n

        return pixelBuffer;
    }

    @Override
    public void release() throws Exception {
    }

    /**
     * Grow the pixel buffer if necessary.  Using this method instead of allocating a new buffer every time a frame
     * is grabbed improves performance by reducing the frequency of garbage collections.  In a simple test, the
     * original version of IPCameraFrameGrabber that allocated a 4096 element byte array for every read
     * caused about 200MB of allocations within 13 seconds.  In this version, almost no additional heap space
     * is typically allocated per frame.
     */
    private void ensureBufferCapacity(int desiredCapacity) {
        int capacity = pixelBuffer.length;

        while (capacity < desiredCapacity) {
            capacity *= 2;
        }

        if (capacity > pixelBuffer.length) {
            pixelBuffer = new byte[capacity];
        }
    }

    /**
     * Returns the value of the {@code long} argument;
     * throwing an exception if the value overflows an {@code int}.
     *
     * @param value the long value
     * @return the argument as an int
     * @throws ArithmeticException if the {@code argument} overflows an int
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#toIntExact-long-">Java 8 Implementation</a>
     */
    private static int toIntExact(long value) {
        if ((int) value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) value;
    }

}
