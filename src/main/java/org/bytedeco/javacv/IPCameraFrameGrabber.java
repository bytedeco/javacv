/*
 * Copyright (C) 2013 Greg Perry
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bytedeco.javacv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

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

    private URL url;
    private URLConnection connection;
    private InputStream input;
    private Map<String, List<String>> headerfields;
    private String boundryKey;
    private IplImage decoded = null;
    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    public IPCameraFrameGrabber(String urlstr) throws MalformedURLException {
        url = new URL(urlstr);
    }

    @Override
    public void start() {

        try {
            connection = url.openConnection();
            headerfields = connection.getHeaderFields();
            if (headerfields.containsKey("Content-Type")) {
                List<String> ct = headerfields.get("Content-Type");
                for (int i = 0; i < ct.size(); ++i) {
                    String key = ct.get(i);
                    int j = key.indexOf("boundary=");
                    if (j != -1) {
                        boundryKey = key.substring(j + 9); // FIXME << fragile
                    }
                }
            }
            input = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            input.close();
            input = null;
            connection = null;
            url = null;
            if (decoded != null){
                cvReleaseImage(decoded);
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    @Override
    public void trigger() throws Exception {
    }

    @Override
    public Frame grab() throws Exception {
        try {
            byte[] b = readImage();
            CvMat mat = cvMat(1, b.length, CV_8UC1, new BytePointer(b));
            if (decoded != null){
                cvReleaseImage(decoded);
            }
            return converter.convert(decoded = cvDecodeImage(mat));
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public BufferedImage grabBufferedImage() throws IOException {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(readImage()));
        return bi;
    }

    byte[] readImage() throws IOException {
        byte[] buffer = new byte[4096];// MTU or JPG Frame Size?
        int n = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        StringBuffer sb = new StringBuffer();
        int total = 0;
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
        String subheader = sb.toString();
        //log.debug(subheader);
        int contentLength = -1;
        // if (boundryKey == null)
        // {
        // Yay! - server was nice and sent content length
        int c0 = subheader.indexOf("Content-Length: ");
        int c1 = subheader.indexOf('\r', c0);

        if (c0 < 0) {
            //log.info("no content length returning null");
            return null;
        }

        c0 += 16;
        contentLength = Integer.parseInt(subheader.substring(c0, c1).trim());
        //log.debug("Content-Length: " + contentLength);

        // adaptive size - careful - don't want a 2G jpeg
        if (contentLength > buffer.length) {
            buffer = new byte[contentLength];
        }

        n = -1;
        total = 0;
        while ((n = input.read(buffer, 0, contentLength - total)) != -1) {
            total += n;
            baos.write(buffer, 0, n);

            if (total == contentLength) {
                break;
            }
        }

        baos.flush();

        input.read();// \r
        input.read();// \n
        input.read();// \r
        input.read();// \n

        return baos.toByteArray();
    }

    @Override
    public void release() throws Exception {
    }

}
