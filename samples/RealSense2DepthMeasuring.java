/*
 * Copyright (C) 2019 Florian Bruggisser
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

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.RealSense2FrameGrabber;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RealSense2DepthMeasuring {
    public static void main(String[] args) throws FrameGrabber.Exception {
        RealSense2FrameGrabber rs2 = new RealSense2FrameGrabber();

        // list all cameras
        for(RealSense2FrameGrabber.RealSense2DeviceInfo info : rs2.getDeviceInfos()) {
            System.out.printf("Device: %s %s %s Locked: %b\n",
                    info.getName(),
                    info.getFirmware(),
                    info.getSerialNumber(),
                    info.isLocked());
        }

        // enable the depth stream of the realsense camera
        rs2.enableDepthStream(640, 480, 30);

        // here are more examples of streams:
        /*
        rs2.addColorStream(640, 480, 30); // color stream
        rs2.addIRStream(640, 480, 90); // ir stream
        rs2.addStream(new RealSense2FrameGrabber.RealSenseStream(
                RS2_STREAM_INFRARED,
                2,
                new Size(640, 480),
                30,
                RS2_FORMAT_Y8
        )); // second ir stream
        */

        // start realsense camera
        rs2.start();

        // start frame to view the stream
        CanvasFrame canvasFrame = new CanvasFrame("RealSense");
        canvasFrame.setCanvasSize(rs2.getImageWidth(), rs2.getImageHeight());

        // add mouse listener to see the depth at the clicked point
        canvasFrame.getCanvas().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    System.out.println("Depth: " + rs2.getDistance(e.getX(), e.getY()));
                } catch (FrameGrabber.Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // run canvas
        while (canvasFrame.isVisible()) {
            // trigger camera to capture images
            rs2.trigger();

            // display images -> grab will return the first stream added
            // use rs2.grabDepth(), rs2.grabColor() and rs2.grabIR() for the other streams
            Frame frame = rs2.grab();

            // display frame
            canvasFrame.showImage(frame);
        }

        // close realsense camera
        rs2.stop();
        rs2.release();
        canvasFrame.dispose();

    }
}
