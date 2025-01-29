package org.bytedeco.javacv.util;

import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.text.TextUtils;

/***
 * easy to mamage and use Camera 
 * @author haogg
 */
public class CameraManager {
	/***Camera.CameraInfo.CAMERA_FACING_BACK */
	static int mCameraId=Camera.CameraInfo.CAMERA_FACING_BACK;
	
	private static Camera mCamera=null;

	private static Parameters mParameters=null;

	private static List<Size> supportedPreviewSizes=null;
	
	private static int mFrameRate;

	private static List<Integer> rates;
	
	@SuppressLint("NewApi") 
	public static Camera getCamera(){
		if (mCameraId != Camera.CameraInfo.CAMERA_FACING_BACK && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			mCamera = Camera.open(mCameraId);
		else
			mCamera = Camera.open();
		
		mParameters = mCamera.getParameters();
		supportedPreviewSizes = mParameters.getSupportedPreviewSizes();
		fixParameters(mParameters);
		mCamera.setParameters(mParameters);
		
		return mCamera;
	}
	
	/***In order to get a better preview:
	 * 1. focusmode
	 * 2. framerate
	 * 3. whitestablization
	 * 4. ....
	 * */
	@SuppressLint("NewApi") 
	private static void fixParameters(Parameters mParameters2) {
		if (mParameters == null)
			return;
		
		//FrameRate
		if (rates==null) {
			rates = mParameters.getSupportedPreviewFrameRates();
			Collections.sort(rates);	
		}
		if (rates != null) {
			mFrameRate=rates.get(rates.size() - 1);
		}
		mParameters.setPreviewFrameRate(mFrameRate);
		
		//PreviewSize
		mParameters.setPreviewSize(640, 480);// 3:2
		
		//？
		mParameters.setPreviewFormat(ImageFormat.NV21);

		// set focus mode
		String mode = checkFocusMode();
		
		// sansung need to handle special
		if (TextUtils.isEmpty(mode)) {
			mParameters.setFocusMode(mode);
		}

		// set camera scene
		//		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && isSupported(mParameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_PORTRAIT))
		//			mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
		
		//whiteBalandce
		if (isKeyInList(mParameters.getSupportedWhiteBalance(), "auto"))
			mParameters.setWhiteBalance("auto");

		//stabilization
		if ("true".equals(mParameters.get("video-stabilization-supported")))
			mParameters.set("video-stabilization", "true");
	}

	@SuppressLint("NewApi") 
	public static Camera switchCamera(int cameraId) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
			mCameraId=0;
		}else{
			if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
				mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
			} else {
				mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			}			
		}
		return getCamera();
	}
	
	/***check the autofocus mode*/
	private static String checkFocusMode() {
		
		if (mParameters != null) {
			
			List<String> focusModes = mParameters.getSupportedFocusModes();
			if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isKeyInList(focusModes, "continuous-picture")) {
				return "continuous-picture";
			} else if (isKeyInList(focusModes, "continuous-video")) {
				return "continuous-video";
			} else if (isKeyInList(focusModes, "auto")) {
				return "auto";
			}
		}
		return null;
	}
	
	/** check whether the key exists in the list */
	private static boolean isKeyInList(List<String> list, String key) {
		return list != null && list.contains(key);
	}

}
