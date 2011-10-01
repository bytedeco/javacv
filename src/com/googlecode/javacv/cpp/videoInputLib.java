/*
 * Copyright (C) 2011 Samuel Audet
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
 *
 *
 * This file was derived from videoInput.h of videoInput 0.1995,
 * which is covered by the following copyright notice:
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * //////////////////////////////////////////////////////////
 * //Written by Theodore Watson - theo.watson@gmail.com    //
 * //Do whatever you want with this code but if you find   //
 * //a bug or make an improvement I would love to know!    //
 * //                                                      //
 * //Warning This code is experimental                     //
 * //use at your own risk :)                               //
 * //////////////////////////////////////////////////////////
 * /////////////////////////////////////////////////////////
 *                        Shoutouts
 *
 * Thanks to:
 *
 *            Dillip Kumar Kara for crossbar code.
 *            Zachary Lieberman for getting me into this stuff
 *            and for being so generous with time and code.
 *            The guys at Potion Design for helping me with VC++
 *            Josh Fisher for being a serious C++ nerd :)
 *            Golan Levin for helping me debug the strangest
 *            and slowest bug in the world!
 *
 *            And all the people using this library who send in
 *            bugs, suggestions and improvements who keep me working on
 *            the next version - yeah thanks a lot ;)
 *
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.CharPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;

import static com.googlecode.javacpp.Loader.*;

/**
 *
 * @author Samuel Audet
 */
@Platform(value="windows", include="<videoInput.cpp>", link={"ole32", "oleaut32", "amstrmid", "strmiids", "uuid"},
    includepath={"../videoInput0.1995/videoInputSrcAndDemos/libs/videoInput/",
                 "../videoInput0.1995/videoInputSrcAndDemos/libs/DShow/Include/"})
public class videoInputLib {
    static { load(); }

    @Cast("bool")
    public static native boolean verbose(); public static native void verbose(boolean verbose);

    public static final double
            VI_VERSION = 0.1995;
    public static final int
            VI_MAX_CAMERAS = 20,
            VI_NUM_TYPES   = 18,
            VI_NUM_FORMATS = 18,

            VI_COMPOSITE = 0,
            VI_S_VIDEO   = 1,
            VI_TUNER     = 2,
            VI_USB       = 3,
            VI_1394      = 4,

            VI_NTSC_M   = 0,
            VI_PAL_B    = 1,
            VI_PAL_D    = 2,
            VI_PAL_G    = 3,
            VI_PAL_H    = 4,
            VI_PAL_I    = 5,
            VI_PAL_M    = 6,
            VI_PAL_N    = 7,
            VI_PAL_NC   = 8,
            VI_SECAM_B  = 9,
            VI_SECAM_D  = 10,
            VI_SECAM_G  = 11,
            VI_SECAM_H  = 12,
            VI_SECAM_K  = 13,
            VI_SECAM_K1 = 14,
            VI_SECAM_L  = 15,
            VI_NTSC_M_J = 16,
            VI_NTSC_433 = 17;


    @Opaque public static class GUID                  extends Pointer { }
    @Opaque public static class ICaptureGraphBuilder2 extends Pointer { }
    @Opaque public static class IGraphBuilder         extends Pointer { }
    @Opaque public static class IBaseFilter           extends Pointer { }
    @Opaque public static class IAMCrossbar           extends Pointer { }
    @Opaque public static class IMediaControl         extends Pointer { }
    @Opaque public static class ISampleGrabber        extends Pointer { }
    @Opaque public static class IMediaEventEx         extends Pointer { }
    @Opaque public static class IAMStreamConfig       extends Pointer { }
    @Opaque public static class _AMMediaType          extends Pointer { }
    @Opaque public static class SampleGrabberCallback extends Pointer { }

    public static native int comInitCount(); public static native void comInitCount(int comInitCount);


    public static class videoDevice extends Pointer {
        static { load(); }
        public videoDevice() { allocate(); }
        public videoDevice(Pointer p) { super(p); }
        private native void allocate();

        public native void setSize(int w, int h);
        public native void NukeDownstream(IBaseFilter pBF);
	public native void destroyGraph();

        public native int videoSize(); public native videoDevice videoSize(int videoSize);
        public native int width();     public native videoDevice width(int width);
        public native int height();    public native videoDevice height(int height);
        public native int tryWidth();  public native videoDevice tryWidth(int tryWidth);
        public native int tryHeight(); public native videoDevice tryHeight(int tryHeight);

        public native ICaptureGraphBuilder2 pCaptureGraph(); public native videoDevice pCaptureGraph(ICaptureGraphBuilder2 pCaptureGraph);
        public native IGraphBuilder pGraph();                public native videoDevice pGraph(IGraphBuilder pGraph);
        public native IMediaControl pControl();              public native videoDevice pControl(IMediaControl pControl);
        public native IBaseFilter pVideoInputFilter();       public native videoDevice pVideoInputFilter(IBaseFilter pVideoInputFilter);
        public native IBaseFilter pGrabberF();               public native videoDevice pGrabberF(IBaseFilter pGrabberF);
        public native IBaseFilter pDestFilter();             public native videoDevice pDestFilter(IBaseFilter pDestFilter);
        public native IAMStreamConfig streamConf();          public native videoDevice streamConf(IAMStreamConfig streamConf);
        public native ISampleGrabber  pGrabber();            public native videoDevice pGrabber(ISampleGrabber pGrabber);
        public native _AMMediaType pAmMediaType();           public native videoDevice pAmMediaType(_AMMediaType pAmMediaType);

        public native IMediaEventEx pMediaEvent();           public native videoDevice pMediaEvent(IMediaEventEx pMediaEvent);

        public native @ByRef GUID videoType();               public native videoDevice videoType(GUID videoType);
        public native @Cast("long") int formatType();        public native videoDevice formatType(int formatType);

        public native SampleGrabberCallback sgCallback();    public native videoDevice sgCallback(SampleGrabberCallback sgCallback);

        public native @Cast("bool") boolean tryDiffSize();    public native videoDevice tryDiffSize(boolean tryDiffSize);
        public native @Cast("bool") boolean useCrossbar();    public native videoDevice useCrossbar(boolean useCrossbar);
        public native @Cast("bool") boolean readyToCapture(); public native videoDevice readyToCapture(boolean readyToCapture);
        public native @Cast("bool") boolean sizeSet();        public native videoDevice sizeSet(boolean sizeSet);
        public native @Cast("bool") boolean setupStarted();   public native videoDevice setupStarted(boolean setupStarted);
        public native @Cast("bool") boolean specificFormat(); public native videoDevice specificFormat(boolean specificFormat);
        public native @Cast("bool") boolean autoReconnect();  public native videoDevice autoReconnect(boolean autoReconnect);

        public native int nFramesForReconnect(); public native videoDevice nFramesForReconnect(int nFramesForReconnect);
        @Cast("unsigned long")
        public native int nFramesRunning();      public native videoDevice nFramesRunning(int nFramesRunning);
        public native int connection();          public native videoDevice connection(int connection);
        public native int storeConn();           public native videoDevice storeConn(int storeConn);
        public native int myID();                public native videoDevice myID(int myID);
        @Cast("long")
        public native int requestedFrameTime();  public native videoDevice requestedFrameTime(int requestedFrameTime);

        @MemberGetter @Cast("char*")  public native BytePointer nDeviceName();
        @MemberGetter @Cast("WCHAR*") public native CharPointer wDeviceName();

        @Cast("unsigned char*") public native BytePointer pixels();  public native videoDevice pixels(BytePointer pixels);
        @Cast("char*")          public native BytePointer pBuffer(); public native videoDevice pBuffer(BytePointer pBuffer);
    }


    public static class videoInput extends Pointer {
        static { load(); }
        public videoInput() { allocate(); }
        public videoInput(Pointer p) { super(p); }
        private native void allocate();

        public static native void setVerbose(@Cast("bool") boolean _verbose);
        public static int listDevices() { return listDevices(false); }
        public static native int listDevices(@Cast("bool") boolean silent);
        public static native String getDeviceName(int deviceID);

        public native void setUseCallback(@Cast("bool") boolean useCallback);
        public native void setIdealFramerate(int deviceID, int idealFramerate);
        public native void setAutoReconnectOnFreeze(int deviceNumber, @Cast("bool") boolean doReconnect, int numMissedFramesBeforeReconnect);

        public native boolean setupDevice(int deviceID);
        public native boolean setupDevice(int deviceID, int w, int h);

        public native boolean setupDevice(int deviceID, int connection);
        public native boolean setupDevice(int deviceID, int w, int h, int connection);

        public native boolean setFormat(int deviceNumber, int format);
        public native boolean isFrameNew(int deviceID);
        public native boolean isDeviceSetup(int deviceID);

        public BytePointer getPixels(int deviceID) { return getPixels(deviceID, true, false); }
        public native @Cast("unsigned char*") BytePointer getPixels(int deviceID,
                @Cast("bool") boolean flipRedAndBlue, @Cast("bool") boolean flipImage);

        public boolean getPixels(int id, BytePointer pixels) { return getPixels(id, pixels, true, false); }
        public native boolean getPixels(int id, @Cast("unsigned char*") BytePointer pixels,
                @Cast("bool") boolean flipRedAndBlue, @Cast("bool") boolean flipImage);

        public native void showSettingsWindow(int deviceID);

        public native boolean setVideoSettingFilter(int deviceID, @Cast("long") int Property,
                @Cast("long") int lValue, @Cast("long") int Flags/*=0*/, @Cast("bool") boolean useDefaultValue/*=false*/);
        public native boolean setVideoSettingFilterPct(int deviceID, @Cast("long") int Property,
                float pctValue, @Cast("long") int Flags/*=0*/);
        public native boolean getVideoSettingFilter(int deviceID, @Cast("long") int Property,
                @ByRef @Cast("long*") int[] min, @ByRef @Cast("long*") int[] max,
                @ByRef @Cast("long*") int[] SteppingDelta, @ByRef @Cast("long*") int[] currentValue,
                @ByRef @Cast("long*") int[] flags, @ByRef @Cast("long*") int[] defaultValue);

        public native boolean setVideoSettingCamera(int deviceID, @Cast("long") int Property,
                @Cast("long") int lValue, @Cast("long") int Flags/*=0*/, @Cast("bool") boolean useDefaultValue/*=false*/);
        public native boolean setVideoSettingCameraPct(int deviceID, @Cast("long") int Property,
                float pctValue, @Cast("long") int Flags/*=0*/);
        public native boolean getVideoSettingCamera(int deviceID, @Cast("long") int Property,
                @ByRef @Cast("long*") int[] min, @ByRef @Cast("long*") int[] max,
                @ByRef @Cast("long*") int[] SteppingDelta, @ByRef @Cast("long*") int[] currentValue,
                @ByRef @Cast("long*") int[] flags, @ByRef @Cast("long*") int[] defaultValue);

//        public native boolean setVideoSettingCam(int deviceID, @Cast("long") int Property,
//                @Cast("long") int lValue, @Cast("long") int Flags/*=0*/, @Cast("bool") boolean useDefaultValue/*=false*/);

        public native int  getWidth(int deviceID);
        public native int  getHeight(int deviceID);
        public native int  getSize(int deviceID);

        public native void stopDevice(int deviceID);
        public native boolean restartDevice(int deviceID);

        public native int  devicesFound();                           public native videoInput devicesFound(int devicesFound);

        public native @Cast("long") int propBrightness();            public native videoInput propBrightness(int propBrightness);
        public native @Cast("long") int propContrast();              public native videoInput propContrast(int propContrast);
        public native @Cast("long") int propHue();                   public native videoInput propHue(int propHue);
        public native @Cast("long") int propSaturation();            public native videoInput propSaturation(int propSaturation);
        public native @Cast("long") int propSharpness();             public native videoInput propSharpness(int propSharpness);
        public native @Cast("long") int propGamma();                 public native videoInput propGamma(int propGamma);
        public native @Cast("long") int propColorEnable();           public native videoInput propColorEnable(int propColorEnable);
        public native @Cast("long") int propWhiteBalance();          public native videoInput propWhiteBalance(int propWhiteBalance);
        public native @Cast("long") int propBacklightCompensation(); public native videoInput propBacklightCompensation(int propBacklightCompensation);
        public native @Cast("long") int propGain();                  public native videoInput propGain(int propGain);

        public native @Cast("long") int propPan();                   public native videoInput propPan(int propPan);
        public native @Cast("long") int propTilt();                  public native videoInput propTilt(int propTilt);
        public native @Cast("long") int propRoll();                  public native videoInput propRoll(int propRoll);
        public native @Cast("long") int propZoom();                  public native videoInput propZoom(int propZoom);
        public native @Cast("long") int propExposure();              public native videoInput propExposure(int propExposure);
        public native @Cast("long") int propIris();                  public native videoInput propIris(int propIris);
        public native @Cast("long") int propFocus();                 public native videoInput propFocus(int propFocus);
    }
}
