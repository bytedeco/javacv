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
package org.bytedeco.javacv;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.librealsense.global.RealSense;
import org.bytedeco.librealsense2.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.bytedeco.librealsense2.global.realsense2.*;
import static org.bytedeco.opencv.global.opencv_core.*;

public class RealSense2FrameGrabber extends FrameGrabber {
    private rs2_error error = new rs2_error();
    private rs2_context context;
    private rs2_device device;
    private rs2_pipeline pipeline;
    private rs2_config config;
    private rs2_pipeline_profile pipelineProfile;

    private rs2_frame frameset;

    private int deviceNumber;
    private ArrayList<RealSenseStream> streams = new ArrayList<>();

    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    public RealSense2FrameGrabber() throws Exception {
        this(0);
    }

    public RealSense2FrameGrabber(int deviceNumber) throws FrameGrabber.Exception {
        this.deviceNumber = deviceNumber;

        // create context
        this.context = createContext();
    }

    public List<RealSense2DeviceInfo> getDeviceInfos() throws Exception {
        List<RealSense2DeviceInfo> devices = new ArrayList<>();

        rs2_device_list deviceList = createDeviceList();
        int count = rs2_get_device_count(deviceList, error);
        checkError(error);

        for (int i = 0; i < count; i++) {
            rs2_device device = createDevice(deviceList, i);
            devices.add(new RealSense2DeviceInfo(
                    getDeviceInfo(device, RS2_CAMERA_INFO_NAME),
                    getDeviceInfo(device, RS2_CAMERA_INFO_SERIAL_NUMBER),
                    getDeviceInfo(device, RS2_CAMERA_INFO_FIRMWARE_VERSION),
                    toBoolean(getDeviceInfo(device, RS2_CAMERA_INFO_ADVANCED_MODE)),
                    toBoolean(getDeviceInfo(device, RS2_CAMERA_INFO_CAMERA_LOCKED))
            ));
            rs2_delete_device(device);
        }

        rs2_delete_device_list(deviceList);
        return devices;
    }

    public void clearStreams() {
        streams.clear();
    }

    public List<RealSenseStream> getStreams() {
        return this.streams;
    }

    public void addStream(RealSenseStream stream) {
        streams.add(stream);
    }

    public void addColorStream(int width, int height, int frameRate) {
        addStream(new RealSenseStream(
                RS2_STREAM_COLOR,
                0,
                new Size(width, height),
                frameRate,
                RS2_FORMAT_BGR8
        ));
    }

    public void addDepthStream(int width, int height, int frameRate) {
        addStream(new RealSenseStream(
                RS2_STREAM_DEPTH,
                0,
                new Size(width, height),
                frameRate,
                RS2_FORMAT_Z16
        ));
    }

    public void addIRStream(int width, int height, int frameRate) {
        addStream(new RealSenseStream(
                RS2_STREAM_INFRARED,
                1,
                new Size(width, height),
                frameRate,
                RS2_FORMAT_Y8
        ));
    }

    @Override
    public void start() throws FrameGrabber.Exception {
        // check if device is available
        if (getDeviceCount() <= 0) {
            throw new FrameGrabber.Exception("No realsense2 device is connected.");
        }

        // create device
        rs2_device_list devices = createDeviceList();
        this.device = createDevice(devices, this.deviceNumber);

        // create pipeline
        this.pipeline = createPipeline();
        this.config = createConfig();

        // check if streams is not empty
        if (streams.isEmpty())
            throw new FrameGrabber.Exception("No stream has been added to be enabled.");

        // enable streams
        for (RealSenseStream stream : streams) {
            rs2_config_enable_stream(config,
                    stream.type,
                    stream.index,
                    stream.size.width(),
                    stream.size.height(),
                    stream.format,
                    stream.frameRate,
                    error);
            checkError(error);
        }

        // todo: set options (emitter)

        // set image width & height to largest stream
        RealSenseStream largestStream = streams.stream().max(Comparator.comparing(RealSenseStream::getArea)).get();
        this.imageWidth = largestStream.size.width();
        this.imageHeight = largestStream.size.height();

        // start pipeline
        pipelineProfile = rs2_pipeline_start_with_config(pipeline, config, error);
        checkError(error);
    }

    @Override
    public void stop() throws FrameGrabber.Exception {
        rs2_pipeline_stop(this.pipeline, error);
        checkError(error);

        rs2_release_frame(this.frameset);
        rs2_delete_pipeline_profile(this.pipelineProfile);
        rs2_delete_config(this.config);
        rs2_delete_pipeline(this.pipeline);
        rs2_delete_device(this.device);
        this.device = null;
    }

    @Override
    public void trigger() throws FrameGrabber.Exception {
        // release previous frame
        rs2_release_frame(this.frameset);

        // read frames
        this.frameset = rs2_pipeline_wait_for_frames(pipeline, RS2_DEFAULT_TIMEOUT, error);
        checkError(error);
    }

    @Override
    public Frame grab() throws FrameGrabber.Exception {
        RealSenseStream stream = streams.get(0);

        switch (stream.type) {
            case RS2_STREAM_DEPTH:
                return grabDepth();

            case RS2_STREAM_INFRARED:
                return grabIR();

            default:
                return grabColor();
        }
    }

    public float getDistance(int x, int y) throws Exception {
        rs2_frame frame = findFrameByStreamType(this.frameset, RS2_STREAM_DEPTH, 0);
        if (frame == null)
            return -1f;

        float distance = rs2_depth_frame_get_distance(frame, x, y, error);
        checkError(error);
        rs2_release_frame(frame);
        return distance;
    }

    public Frame grabColor() throws Exception {
        return grabCVFrame(RS2_STREAM_COLOR, 0, IPL_DEPTH_8U, 3);
    }

    public Frame grabDepth() throws Exception {
        return grabCVFrame(RS2_STREAM_DEPTH, 0, IPL_DEPTH_16U, 1);
    }

    public Frame grabIR() throws Exception {
        return grabIR(0);
    }

    public Frame grabIR(int streamIndex) throws Exception {
        return grabCVFrame(RS2_STREAM_INFRARED, streamIndex, IPL_DEPTH_8U, 1);
    }

    private Frame grabCVFrame(int streamType, int streamIndex, int iplDepth, int iplChannels) throws Exception {
        Frame colorFrame;

        // get depth frame if available
        rs2_frame frame = findFrameByStreamType(this.frameset, streamType, streamIndex);
        if (frame == null)
            return null;

        // get frame data
        Pointer frameData = getFrameData(frame);
        Size size = getFrameSize(frame);

        // create cv frame
        IplImage image = IplImage.createHeader(size.width(), size.height(), iplDepth, iplChannels);
        cvSetData(image, frameData, size.width() * iplChannels * iplDepth / 8);
        colorFrame = converter.convert(image);

        // cleanup
        rs2_release_frame(frame);

        return colorFrame;
    }

    private rs2_frame findFrameByStreamType(rs2_frame frameset, int streamType, int index) throws FrameGrabber.Exception {
        rs2_frame result = null;

        // read frames
        int frameCount = rs2_embedded_frames_count(frameset, error);
        checkError(error);

        int i = 0;
        int searchIndex = 0;
        while (i < frameCount) {
            rs2_frame frame = rs2_extract_frame(frameset, i, error);
            checkError(error);

            // get stream profile data
            rs2_stream_profile streamProfile = getStreamProfile(frame);
            StreamProfileData streamProfileData = getStreamProfileData(streamProfile);

            // compare stream type
            if (streamType == streamProfileData.nativeStreamIndex.get() && searchIndex == index) {
                result = frame;
                break;
            }

            rs2_delete_stream_profile(streamProfile);
            rs2_release_frame(frame);
            i++;
        }

        return result;
    }

    @Override
    public void release() throws FrameGrabber.Exception {
        rs2_delete_device(this.device);
        rs2_delete_context(this.context);
    }

    private rs2_context createContext() throws FrameGrabber.Exception {
        rs2_context context = rs2_create_context(RS2_API_VERSION, error);
        checkError(error);
        return context;
    }

    private rs2_device_list createDeviceList() throws FrameGrabber.Exception {
        rs2_device_list deviceList = rs2_query_devices(context, error);
        checkError(error);
        return deviceList;
    }

    private rs2_device createDevice(rs2_device_list deviceList, int index) throws FrameGrabber.Exception {
        rs2_device device = rs2_create_device(deviceList, index, error);
        checkError(error);
        return device;
    }

    private rs2_pipeline createPipeline() throws FrameGrabber.Exception {
        rs2_pipeline pipeline = rs2_create_pipeline(context, error);
        checkError(error);
        return pipeline;
    }

    private rs2_config createConfig() throws FrameGrabber.Exception {
        rs2_config config = rs2_create_config(error);
        checkError(error);
        return config;
    }

    private double getFrameTimeStamp(rs2_frame frame) throws FrameGrabber.Exception {
        double timestamp = rs2_get_frame_timestamp(frame, error);
        checkError(error);
        return timestamp;
    }

    private int getDeviceCount() throws FrameGrabber.Exception {
        rs2_device_list deviceList = createDeviceList();
        int count = rs2_get_device_count(deviceList, error);

        checkError(error);
        rs2_delete_device_list(deviceList);
        return count;
    }

    private String getDeviceInfo(rs2_device device, int info) throws FrameGrabber.Exception {
        // check if info is supported
        rs2_error error = new rs2_error();
        boolean isSupported = toBoolean(rs2_supports_device_info(device, info, error));
        checkError(error);

        if (!isSupported)
            return null;

        // read device info
        String infoText = rs2_get_device_info(device, info, error).getString();
        checkError(error);

        return infoText;
    }

    private Pointer getFrameData(rs2_frame frame) throws Exception {
        Pointer frameData = rs2_get_frame_data(frame, error);
        checkError(error);
        return frameData;
    }

    private Size getFrameSize(rs2_frame frame) throws Exception {
        int width = rs2_get_frame_width(frame, error);
        checkError(error);
        int height = rs2_get_frame_height(frame, error);
        checkError(error);
        return new Size(width, height);
    }

    private rs2_stream_profile getStreamProfile(rs2_frame frame) throws Exception {
        rs2_stream_profile streamProfile = rs2_get_frame_stream_profile(frame, error);
        checkError(error);
        return streamProfile;
    }

    private StreamProfileData getStreamProfileData(rs2_stream_profile streamProfile) throws Exception {
        StreamProfileData profileData = new StreamProfileData();

        // check if stream profile matches search type
        rs2_get_stream_profile_data(streamProfile,
                profileData.nativeStreamIndex,
                profileData.nativeFormatIndex,
                profileData.index,
                profileData.uniqueId,
                profileData.frameRate,
                error);
        checkError(error);

        return profileData;
    }

    private static void checkError(rs2_error e) throws FrameGrabber.Exception {
        if (!e.isNull()) {
            throw new FrameGrabber.Exception(String.format("rs_error was raised when calling %s(%s):\n%s\n",
                    rs2_get_failed_function(e),
                    rs2_get_failed_args(e),
                    rs2_get_error_message(e)));
        }
    }

    private static boolean toBoolean(int value) {
        return value >= 1;
    }

    private static boolean toBoolean(String value) {
        if (value == null)
            return false;

        return value.equals("YES");
    }

    static class StreamProfileData {
        IntPointer nativeStreamIndex = new IntPointer(1);
        IntPointer nativeFormatIndex = new IntPointer(1);
        IntPointer index = new IntPointer(1);
        IntPointer uniqueId = new IntPointer(1);
        IntPointer frameRate = new IntPointer(1);
    }

    public static class RealSenseStream {
        private int type;
        private int index;
        private Size size;
        private int frameRate;
        private int format;

        public RealSenseStream(int type, int index, Size size, int frameRate, int format) {
            this.type = type;
            this.index = index;
            this.size = size;
            this.frameRate = frameRate;
            this.format = format;
        }

        public int getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }

        public Size getSize() {
            return size;
        }

        public int getFrameRate() {
            return frameRate;
        }

        public int getFormat() {
            return format;
        }

        protected int getArea() {
            return size.area();
        }
    }

    public static class RealSense2DeviceInfo {
        private String name;
        private String serialNumber;
        private String firmware;
        private boolean inAdvancedMode;
        private boolean locked;

        RealSense2DeviceInfo(String name, String serialNumber, String firmware, boolean inAdvancedMode, boolean locked) {
            this.name = name;
            this.serialNumber = serialNumber;
            this.firmware = firmware;
            this.inAdvancedMode = inAdvancedMode;
            this.locked = locked;
        }

        public String getName() {
            return name;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public String getFirmware() {
            return firmware;
        }

        public boolean isInAdvancedMode() {
            return inAdvancedMode;
        }

        public boolean isLocked() {
            return locked;
        }
    }
}
