/*
 * Copyright (C) 2011-2012 Jiri Masa, Samuel Audet
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

import cl.eye.CLCamera;
import java.io.File;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/** Minimal Sony PS3 Eye camera grabber implementation.
 * 
 *  It allows grabbing of frames at higher speed than OpenCVFrameGrabber or VideoInputFrameGrabber.
 *  Underlying implementation of last two grabbers is limited to 30 FPS. PS3 allows grabbing
 *  at maximum speed of 75 FPS in VGA and 187 FPS in QVGA modes.
 *  
 *  This code was developed and tested with CLEyeMulticam.dll, version 1.2.0.1008. The dll library
 *  is part of Code Laboratories CL-Eye Platform SDK and is distributed as part of CLEyeMulticam
 *  Redistributable Dynamic Link Library. For license, download and installation see http://www.codelaboratories.com.
 *    
 *  The grab() method returns an internal instance of IplImage image with fresh camera frame. This returned image
 *  have to be considered "read only" and the caller needs to create it's own copy or clone that image.
 *  Calling of release() method for this image shall be avoided.
 *  Based on used resolution the image is in format either 640x480 or 320x240, IPL_DEPTH_8U, 4 channel (color) or 1 channel (gray).
 *  timestamp is set to actual value of System.nanoTime()/1000 obtained after return from the CL driver.
 *  
 *  Typical use case scenario:
 *     create new instance of PS3MiniGrabber
 *     set camera parameters
 *     start() grabber 
 *     wait at least 2 frames
 *     grab() in loop
 *     stop() grabber
 *     release() internal resources
 *     
 * Note:
 * This code depends on the cl.eye.CLCamera class from Code Laboratories CL-Eye
 * Platform SDK. It is suggested to download SDK and edit the sample file
 * ....\cl\eye\CLCamera.java. A few references to processing.core.PApplet class
 * shall be removed and the file recompiled. The tailored file is not included
 * here namely because of unclear licence.
 * 
 *  @author jmasa, jmasa@cmail.cz
 *
 */
public class PS3EyeFrameGrabber extends FrameGrabber {
    public static String[] getDeviceDescriptions() throws Exception {
        tryLoad();
        String[] descriptions = new String[CLCamera.cameraCount()];
        for (int i = 0; i < descriptions.length; i++) {
            descriptions[i] = CLCamera.cameraUUID(i);
        }
        return descriptions;
    }

    public static PS3EyeFrameGrabber createDefault(File deviceFile)   throws Exception { throw new Exception(PS3EyeFrameGrabber.class + " does not support device files."); }
    public static PS3EyeFrameGrabber createDefault(String devicePath) throws Exception { throw new Exception(PS3EyeFrameGrabber.class + " does not support device paths."); }
    public static PS3EyeFrameGrabber createDefault(int deviceNumber)  throws Exception { return new PS3EyeFrameGrabber(deviceNumber); }

    private static Exception loadingException = null;
    public static void tryLoad() throws Exception {
        if (loadingException != null) {
            throw loadingException;
        } else {
            try {
                CLCamera.IsLibraryLoaded();
            } catch (Throwable t) {
                throw loadingException = new Exception("Failed to load " + PS3EyeFrameGrabber.class, t);
            }
        }
    }

    CLCamera camera;
    int cameraIndex = 0;
    int[]  ps3_frame = null;             // buffer for PS3 camera frame data
    byte[] ipl_frame = null;             // buffer for RGB-3ch, not allocated unless grab_RGB3() is called 

    IplImage image_4ch = null;
    IplImage image_1ch = null;
    FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    String stat;                  // status of PS3 camera handling - mostly for debugging
    String uuid;                  // assigned camera unique key

    // variables for trigger() implementation
    //
    protected enum Triggered {NO_TRIGGER, HAS_FRAME, NO_FRAME};
    protected Triggered triggered = Triggered.NO_TRIGGER;


    /** Default grabber, camera idx = 0, color mode, VGA resolution, 60 FPS frame rate.
     *   
     */
    public PS3EyeFrameGrabber() throws Exception {
        this(0);
    }

    /** Color mode, VGA resolution, 60 FPS frame rate.
     *   @param system wide camera index
     */
    public PS3EyeFrameGrabber(int cameraIndex) throws Exception {
        this(cameraIndex, 640, 480, 60);
    }

    public PS3EyeFrameGrabber(int cameraIndex, int imageWidth, int imageHeight, int framerate) throws Exception {
        this(cameraIndex, 640, 480, 60, null);
    }

    /** Creates grabber, the caller can control basic image and grabbing parameters.
     * 
     * @param cameraIndex - zero based index of used camera (OS system wide)
     * @param imageWidth  - width of image 
     * @param imageHeight - height of image
     * @param framerate   - frame rate - see CLCamera for allowed frame rates based on resolution
     * @param applet      - PApplet object required by CLCamera
     * @throws Exception  - if parameters don't follow CLCamera definition or camera is not created
     */
    public PS3EyeFrameGrabber(int cameraIndex, int imageWidth, int imageHeight, int framerate, Object applet) throws Exception {
        camera = null;

        if (! CLCamera.IsLibraryLoaded()) {
            throw new Exception("CLEye multicam dll not loaded");
        }

        this.camera = new CLCamera();
        this.cameraIndex = cameraIndex;

        stat = "created";
        uuid = CLCamera.cameraUUID(cameraIndex);

        if (((imageWidth == 640) && (imageHeight == 480)) || 
            ((imageWidth == 320) && (imageHeight == 240))) {
            setImageWidth(imageWidth);
            setImageHeight(imageHeight);
        }
        else throw new Exception("Only 640x480 or 320x240 images supported");

        setImageMode(ImageMode.COLOR);
        setFrameRate((double) framerate);  
        setTimeout(1 + 1000/framerate);
        setBitsPerPixel(8);
        setTriggerMode(false);
        setNumBuffers(4);
    }

    /** 
     * @return system wide number of installed/detected Sony PS3 Eye cameras
     */
    public static int getCameraCount() {
        return CLCamera.cameraCount();
    }

    /** Ask the driver for all installed PS3 cameras. Resulting array is sorted in order of camera index.
     *  Its size is defined by CLCamera.cameraCount().
     * 
     * @return array of camera unique uuids or null if there is no PS3 camera
     */
    public static String[] listPS3Cameras() {
        int no = getCameraCount();
        String[] uuids;
        if (no > 0) {
            uuids = new String[no];
            for (--no; no >=0; no--) { uuids[no] = CLCamera.cameraUUID(no); }
            return uuids;
        }
        return null;
    }


    /** Make IplImage form raw int[] frame data
     *  Note: NO array size checks!!
     * 
     * @param frame int[] image frame data 
     * @return internal IplImage set to frame
     */
    public IplImage makeImage(int[] frame) {
        image_4ch.getIntBuffer().put(ps3_frame);
        return image_4ch;
    }


    /** Grab one frame and return it as int[] (in the internal camera format RGBA).
     *  Note: use makeImage() to create RGBA, 4-ch image
     * @return frame as int[] without any processing or null if frame is not available 
     */
    public int[] grab_raw() {
        if (camera.getCameraFrame(ps3_frame, timeout)) {
            return ps3_frame;
        }
        else return null;
    }

    public void trigger() throws Exception {
        for (int i = 0; i < numBuffers+1; i++) {
            grab_raw();
        }

        if ((ps3_frame = grab_raw()) != null) {
            triggered = Triggered.HAS_FRAME;
            timestamp = System.nanoTime()/1000;
        }
        else
            triggered = Triggered.NO_FRAME;
    }


    /** Grab and convert one frame, default timeout is (1 + 1000/framerate) [milliseconds].
     *  Every successful call returns an internal (preallocated) 640x480 or 320x240, IPL_DEPTH_8U, 4-channel image.
     *  The caller shall consider it "read only" and make a copy/clone of it before further processing.
     *  
     *  The call might block for timeout [milliseconds].
     * @return the image or null if there is no new image
     */
     public IplImage grab_RGB4() {

        if (camera.getCameraFrame(ps3_frame, timeout)) {
            timestamp = System.nanoTime()/1000;
            image_4ch.getIntBuffer().put(ps3_frame);
            return image_4ch;
        }
        else return null;
    }

    /** Grab one frame;
     *  the caller have to make a copy of returned image before processing.
     *  
     *  It will throw null pointer exception if not started before grabbing.
     *  @return "read-only" RGB, 4-channel or GRAY/1-channel image, it throws exception if no image is available
     */
    @Override
    public Frame grab() throws Exception {
        IplImage img = null;
        switch (triggered) {
            case NO_TRIGGER:
                img = grab_RGB4();
                break;
            case HAS_FRAME:
                triggered = Triggered.NO_TRIGGER;
                img = makeImage(ps3_frame);
                break;
            case NO_FRAME:     
                triggered = Triggered.NO_TRIGGER;
                return null;
            default:  // just schizophrenia - for future enhancement
                throw new Exception("Int. error - unknown triggering state");
        }
        if ((img != null) && (imageMode == ImageMode.GRAY)) {
                cvCvtColor(img, image_1ch, CV_RGB2GRAY);
                img = image_1ch;
        }
        return converter.convert(img);
    }


    /** Start camera first (before grabbing).
     * 
     * @return success/failure (true/false)
     */
    public void start() throws Exception {
        boolean b;

        if (ps3_frame == null) {
            ps3_frame = new int[ imageWidth * imageHeight ];
            image_4ch = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 4);
            image_1ch = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 1);
        }
   
        b = camera.createCamera(
                 cameraIndex,
                (imageMode == ImageMode.GRAY) ? CLCamera.CLEYE_MONO_PROCESSED : CLCamera.CLEYE_COLOR_PROCESSED,
                (imageWidth == 320 && imageHeight == 240) ? CLCamera.CLEYE_QVGA : CLCamera.CLEYE_VGA,
                (int)frameRate);
        
        if (!b) throw new Exception("Low level createCamera() failed");
        
        b = camera.startCamera();
        if (!b) throw new Exception("Camera start() failed");
        stat = "started";
    }


    /** Stop camera. It can be re-started if needed.
     * 
     * @return success/failure (true/false)
     */
    public void stop() throws Exception {
        boolean b = camera.stopCamera();
        if (b) stat = "stopped";
        else throw new Exception("Camera stop() failed");
    }


    /** Release resources:
     *   - CL driver internal resources binded with camera HW
     *   - internal IplImage
     *  After calling this function, this mini-grabber object instance can not be used anymore.
     */
    public void release() {
        if (camera != null) {
            camera.dispose();
            camera = null;
        }

        if (image_4ch != null) {
            image_4ch.release();
            image_4ch = null;
        }

        if (image_1ch != null) {
            image_1ch.release();
            image_1ch = null;
        }

        if (ipl_frame != null) ipl_frame = null;
        if (ps3_frame != null) ps3_frame = null;

        stat = "released";
    }

    /** Release internal resources, the same as calling release()
     */
    public void dispose() {
        release();
    }

    @Override protected void finalize() throws Throwable {
        super.finalize();
        release();
    }


    /** Return internal CLCamera object, mainly to set camera parameters,
     *  changing camera parameters must be done on stopped camera and before start() is called.
     *  See CL SDK - setCameraParameter(int param, int val) function.
     *  
     * @return internal CLCamera instance
     */
    public CLCamera getCamera() { return camera; }

    public String getUUID() { return uuid; }    

    /**
     * @return status and camera parameters of the grabber
     */
    @Override public String toString() {
        return "UUID="+uuid + "; status=" + stat + "; timeout=" + timeout
            + "; "
            + ((camera != null) ? camera.toString() : "<no camera>")
            ;
    }


    /** Just for testing - loads the CL CLEyeMulticam.dll file, invokes driver
     *  and lists available cameras. 
     *   
     * @param argv - argv is not used
     */
    public static void main(String[] argv) {
        String[] uuids = listPS3Cameras();
        for (int i = 0; i < uuids.length; i++)
            System.out.println(i+": "+uuids[i]);
    }
}
