/*
 * Copyright (C) 2019-2022 Florian Bruggisser, Samuel Audet
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
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.librealsense2.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bytedeco.librealsense2.global.realsense2.*;
import static org.bytedeco.opencv.global.opencv_core.*;

public class RealSense2FrameGrabber extends FrameGrabber {

    private static FrameGrabber.Exception loadingException = null;

    private static final int defaultFrameRate = 30;
    private static final int defaultWidth = 640;
    private static final int defaultHeight = 480;

    public static void tryLoad() throws FrameGrabber.Exception {
        if (loadingException != null) {
            loadingException.printStackTrace();
            throw loadingException;
        } else {
            try {
                Loader.load(org.bytedeco.librealsense2.presets.realsense2.class);
                System.out.println("RealSense2 devices found: " + getDeviceDescriptions().length);
            } catch (Throwable t) {
                throw loadingException = new FrameGrabber.Exception("Failed to load " + RealSense2FrameGrabber.class, t);
            }
        }
    }

    private rs2_error error = new rs2_error();
    private rs2_context context;
    private rs2_device device;
    private rs2_pipeline pipeline;
    private rs2_config config;
    private rs2_pipeline_profile pipelineProfile;

    private rs2_frame frameset;

    private int deviceNumber;
    private List<RealSenseStream> streams = new ArrayList<>();

    private FrameConverter converter = new OpenCVFrameConverter.ToIplImage();

    public RealSense2FrameGrabber() throws Exception {
        this(0);
    }

    public RealSense2FrameGrabber(int deviceNumber) throws Exception {
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

    public static String[] getDeviceDescriptions() throws Exception {
        RealSense2FrameGrabber rs2 = new RealSense2FrameGrabber();
        List<RealSense2DeviceInfo> infos = rs2.getDeviceInfos();
        rs2.release();

        String[] deviceDescriptions = new String[infos.size()];
        for (int i = 0; i < deviceDescriptions.length; i++) {
            RealSense2DeviceInfo info = infos.get(i);
            deviceDescriptions[i] = info.toString();
        }

        return deviceDescriptions;
    }

    public void disableAllStreams() {
        streams.clear();
    }

    public List<RealSenseStream> getEnabledStreams() {
        return this.streams;
    }

    public void enableStream(RealSenseStream stream) {
        streams.add(stream);
    }

    public void enableColorStream(int width, int height, int frameRate) {
        enableStream(new RealSenseStream(
                RS2_STREAM_COLOR,
                0,
                new Size(width, height),
                frameRate,
                RS2_FORMAT_BGR8
        ));
    }

    public void enableDepthStream(int width, int height, int frameRate) {
        enableStream(new RealSenseStream(
                RS2_STREAM_DEPTH,
                0,
                new Size(width, height),
                frameRate,
                RS2_FORMAT_Z16
        ));
    }

    public void enableIRStream(int width, int height, int frameRate, int index) {
        enableStream(new RealSenseStream(
                RS2_STREAM_INFRARED,
                index,
                new Size(width, height),
                frameRate,
                RS2_FORMAT_Y8
        ));
    }

    public void enableIRStream(int width, int height, int frameRate) {
        enableIRStream(width, height, frameRate, 1);
    }

    public void open() throws Exception {
        // check if device is available
        if (getDeviceCount() <= 0) {
            throw new Exception("No realsense2 device is connected.");
        }

        // create device
        rs2_device_list devices = createDeviceList();
        this.device = createDevice(devices, this.deviceNumber);
        rs2_delete_device_list(devices);
    }

    @Override
    public void start() throws Exception {
        if (this.device == null) {
            open();
        }

        // create pipeline
        this.pipeline = createPipeline();
        this.config = createConfig();

        // check if streams is not empty
        if (streams.isEmpty()) {
            enableAllVideoStreams();
            Collections.sort(streams);
        }

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

        // set image width & height to the largest stream
        RealSenseStream largestStream = getLargestStreamByArea();
        this.imageWidth = largestStream.size.width();
        this.imageHeight = largestStream.size.height();

        // start pipeline
        pipelineProfile = rs2_pipeline_start_with_config(pipeline, config, error);
        checkError(error);
    }

    @Override
    public void stop() throws Exception {
        rs2_pipeline_stop(this.pipeline, error);
        checkError(error);

        rs2_release_frame(this.frameset);
        rs2_delete_pipeline_profile(this.pipelineProfile);
        rs2_delete_config(this.config);
        rs2_delete_pipeline(this.pipeline);
        rs2_delete_device(this.device);
        this.device = null;
    }

    private void readNextFrameSet() throws Exception {
        // release previous frame
        rs2_release_frame(this.frameset);

        // read frames
        this.frameset = rs2_pipeline_wait_for_frames(pipeline, RS2_DEFAULT_TIMEOUT, error);
        checkError(error);
    }

    @Override
    public void trigger() throws Exception {
        // set trigger load flag
        if (!triggerMode)
            triggerMode = true;

        // read frames
        readNextFrameSet();
    }

    @Override
    public Frame grab() throws Exception {
        int videoStreamId = Math.max(0, videoStream);
        RealSenseStream stream = streams.get(videoStreamId);

        switch (stream.type) {
            case RS2_STREAM_DEPTH:
                return grabDepth();

            case RS2_STREAM_INFRARED:
                return grabIR();

            default:
                return grabColor();
        }
    }

    public Frame grab(int streamType, int streamIndex, int iplDepth, int channels) throws Exception {
        if (!triggerMode)
            readNextFrameSet();

        return grabCVFrame(streamType, streamIndex, iplDepth, channels);
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
        if (!triggerMode)
            readNextFrameSet();

        return grabCVFrame(RS2_STREAM_COLOR, 0, IPL_DEPTH_8U, 3);
    }

    public Frame grabDepth() throws Exception {
        if (!triggerMode)
            readNextFrameSet();

        return grabCVFrame(RS2_STREAM_DEPTH, 0, IPL_DEPTH_16U, 1);
    }

    public Frame grabIR() throws Exception {
        return grabIR(0);
    }

    public Frame grabIR(int streamIndex) throws Exception {
        if (!triggerMode)
            readNextFrameSet();

        return grabCVFrame(RS2_STREAM_INFRARED, streamIndex, IPL_DEPTH_8U, 1);
    }

    private RealSenseStream getLargestStreamByArea() {
        RealSenseStream largest = streams.get(0);
        for (RealSenseStream rs : streams) {
            if (rs.size.area() > largest.size.area()) {
                largest = rs;
            }
        }
        return largest;
    }

    private Frame grabCVFrame(int streamType, int streamIndex, int iplDepth, int iplChannels) throws Exception {
        Frame outputFrame;

        // get frame of type if available
        rs2_frame frame = findFrameByStreamType(this.frameset, streamType, streamIndex);
        if (frame == null)
            return null;

        // get frame data
        Pointer frameData = getFrameData(frame);
        Size size = getFrameSize(frame);

        // create cv frame
        IplImage image = IplImage.createHeader(size.width(), size.height(), iplDepth, iplChannels);
        cvSetData(image, frameData, size.width() * iplChannels * iplDepth / 8);
        outputFrame = converter.convert(image);

        // add timestamp
        double timestamp = getFrameTimeStamp(frame);
        outputFrame.timestamp = Math.round(timestamp);

        // cleanup
        rs2_release_frame(frame);

        return outputFrame;
    }

    private rs2_frame findFrameByStreamType(rs2_frame frameset, int streamType, int index) throws Exception {
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
            if (streamType == streamProfileData.nativeStreamIndex.get()) {
                if (searchIndex == index) {
                    result = frame;
                    break;
                }
                searchIndex++;
            }

            rs2_release_frame(frame);
            i++;
        }

        return result;
    }

    @Override
    public void release() {
        rs2_delete_device(this.device);
        rs2_delete_context(this.context);
    }

    public void setSensorOption(Rs2SensorType sensorType, int optionIndex, boolean value) throws Exception {
        setSensorOption(sensorType, optionIndex, value ? 1f : 0f);
    }

    public void setSensorOption(Rs2SensorType sensorType, int optionIndex, float value) throws Exception {
        rs2_sensor[] sensors = getSensors(device);

        for (rs2_sensor sensor : sensors) {
            checkError(error);

            // check if name matches
            String name = getSensorInfo(sensor, RS2_CAMERA_INFO_NAME);
            if (sensorType.getName().equals(name)) {
                rs2_options options = new rs2_options(sensor);
                setRs2Option(options, optionIndex, value);
            }

            // cleanup
            rs2_delete_sensor(sensor);
        }
    }

    private rs2_context createContext() throws Exception {
        rs2_context context = rs2_create_context(RS2_API_VERSION, error);
        checkError(error);
        return context;
    }

    private rs2_device_list createDeviceList() throws Exception {
        rs2_device_list deviceList = rs2_query_devices(context, error);
        checkError(error);
        return deviceList;
    }

    private rs2_device createDevice(rs2_device_list deviceList, int index) throws Exception {
        rs2_device device = rs2_create_device(deviceList, index, error);
        checkError(error);
        return device;
    }

    private rs2_pipeline createPipeline() throws Exception {
        rs2_pipeline pipeline = rs2_create_pipeline(context, error);
        checkError(error);
        return pipeline;
    }

    private rs2_config createConfig() throws Exception {
        rs2_config config = rs2_create_config(error);
        checkError(error);
        return config;
    }

    private double getFrameTimeStamp(rs2_frame frame) throws Exception {
        double timestamp = rs2_get_frame_timestamp(frame, error);
        checkError(error);
        return timestamp;
    }

    private int getDeviceCount() throws Exception {
        rs2_device_list deviceList = createDeviceList();
        int count = rs2_get_device_count(deviceList, error);

        checkError(error);
        rs2_delete_device_list(deviceList);
        return count;
    }

    private String getDeviceInfo(rs2_device device, int info) throws Exception {
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

    private String getSensorInfo(rs2_sensor sensor, int info) throws Exception {
        // check if info is supported
        rs2_error error = new rs2_error();
        boolean isSupported = toBoolean(rs2_supports_sensor_info(sensor, info, error));
        checkError(error);

        if (!isSupported)
            return null;

        // read sensor info
        String infoText = rs2_get_sensor_info(sensor, info, error).getString();
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

        if (isStreamProfile(streamProfile, RS2_EXTENSION_VIDEO_PROFILE)) {
            VideoStreamProfileData videoStreamProfileData = new VideoStreamProfileData();

            rs2_get_video_stream_resolution(streamProfile,
                    videoStreamProfileData.width,
                    videoStreamProfileData.height,
                    error);
            checkError(error);

            profileData = videoStreamProfileData;
        }

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

    private boolean isSensorExtendableTo(rs2_sensor sensor, int extension) throws Exception {
        boolean isExtandable = toBoolean(rs2_is_sensor_extendable_to(sensor, extension, error));
        checkError(error);
        return isExtandable;
    }

    private boolean isStreamProfile(rs2_stream_profile profile, int type) throws Exception {
        boolean isOfType = toBoolean(rs2_stream_profile_is(profile, type, error));
        checkError(error);
        return isOfType;
    }

    private boolean matchesVideoStreamProfile(rs2_stream_profile profile,
                                              int streamType,
                                              int streamFormat,
                                              int frameRate,
                                              int width,
                                              int height) throws Exception {
        VideoStreamProfileData data = (VideoStreamProfileData) getStreamProfileData(profile);

        return streamType == data.nativeStreamIndex.get()
                && streamFormat == data.nativeFormatIndex.get()
                && frameRate == data.frameRate.get()
                && width == data.width.get()
                && height == data.height.get();
    }

    private void setRs2Option(rs2_options options, int optionIndex, float value) throws Exception {
        boolean isSupported = toBoolean(rs2_supports_option(options, optionIndex, error));
        checkError(error);

        if (!isSupported) {
            throw new Exception("Option " + optionIndex + " is not supported!");
        }

        rs2_set_option(options, optionIndex, value, error);
        checkError(error);
    }

    private rs2_sensor[] getSensors(rs2_device device) throws Exception {
        rs2_sensor_list sensorList = rs2_query_sensors(device, error);
        checkError(error);

        int sensorCount = rs2_get_sensors_count(sensorList, error);
        checkError(error);

        rs2_sensor[] sensors = new rs2_sensor[sensorCount];

        for (int i = 0; i < sensorCount; i++) {
            rs2_sensor sensor = rs2_create_sensor(sensorList, i, error);
            checkError(error);

            sensors[i] = sensor;
        }

        rs2_delete_sensor_list(sensorList);

        return sensors;
    }

    private rs2_stream_profile[] getStreamProfiles(rs2_sensor sensor) throws Exception {
        rs2_stream_profile_list streamList = rs2_get_stream_profiles(sensor, error);
        checkError(error);

        int streamProfileCount = rs2_get_stream_profiles_count(streamList, error);
        checkError(error);

        rs2_stream_profile[] profiles = new rs2_stream_profile[streamProfileCount];

        for (int i = 0; i < streamProfileCount; i++) {
            rs2_stream_profile profile = rs2_get_stream_profile(streamList, i, error);
            checkError(error);

            profiles[i] = profile;
        }

        rs2_delete_stream_profiles_list(streamList);
        return profiles;
    }

    private void enableAllVideoStreams() throws Exception {
        for (rs2_sensor sensor : getSensors(device)) {
            if (!isSensorExtendableTo(sensor, RS2_EXTENSION_VIDEO)) {
                rs2_delete_sensor(sensor);
                continue;
            }

            for (rs2_stream_profile profile : getStreamProfiles(sensor)) {
                int rsFrameRate = frameRate > 0 ? (int) frameRate : defaultFrameRate;
                int rsWidth = imageWidth > 0 ? imageWidth : defaultWidth;
                int rsHeight = imageWidth > 0 ? imageWidth : defaultHeight;

                if (matchesVideoStreamProfile(profile, RS2_STREAM_DEPTH, RS2_FORMAT_Z16, rsFrameRate, rsWidth, rsHeight)) {
                    enableDepthStream(imageWidth, imageHeight, rsFrameRate);
                } else if (matchesVideoStreamProfile(profile, RS2_STREAM_COLOR, RS2_FORMAT_RGB8, rsFrameRate, rsWidth, rsHeight)) {
                    enableColorStream(imageWidth, imageHeight, rsFrameRate);
                } else if (matchesVideoStreamProfile(profile, RS2_STREAM_INFRARED, RS2_FORMAT_Y8, rsFrameRate, rsWidth, rsHeight)) {
                    enableIRStream(imageWidth, imageHeight, rsFrameRate);
                }
            }

            rs2_delete_sensor(sensor);
        }
    }

    private static void checkError(rs2_error e) throws Exception {
        if (!e.isNull()) {
            throw new Exception(String.format("rs_error was raised when calling %s(%s):\n%s\n",
                    rs2_get_failed_function(e).getString(),
                    rs2_get_failed_args(e).getString(),
                    rs2_get_error_message(e).getString()));
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

    static class VideoStreamProfileData extends StreamProfileData {
        IntPointer width = new IntPointer(1);
        IntPointer height = new IntPointer(1);
    }

    public static class RealSenseStream implements Comparable<RealSenseStream> {
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

        @Override
        public int compareTo(RealSenseStream o) {
            return Integer.compare(getType(), o.getType());
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

        @Override
        public String toString() {
            return String.format("%s", name);
        }
    }

    public enum Rs2SensorType {
        StereoModule("Stereo Module"),
        RGBCamera("RGB Camera");

        private String name;

        Rs2SensorType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
