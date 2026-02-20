package org.bytedeco.javacv.view;

import java.io.IOException;
import java.util.logging.Logger;

import org.bytedeco.javacv.constant.Constant;
import org.bytedeco.javacv.util.CameraManager;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//---------------------------------------------
// camera thread, gets and encodes video data
//---------------------------------------------
public class CameraView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {

	private String LOG_TAG="SurfaceView";
	
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isPreviewOn = false;
    /***during preview, we can do somthing like image processing..,etc*/
    private PreviewCallback mPreviewCallbackDelegate;

    private CameraView(Context context, Camera camera,PreviewCallback _previewCallbackDelegate) {
        super(context);
        Log.w("camera","camera view");
        mCamera = camera;
        mHolder = getHolder();
        mPreviewCallbackDelegate=_previewCallbackDelegate;
        mHolder.addCallback(CameraView.this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera.setPreviewCallback(CameraView.this);
    }
    
    private CameraView(Context context, Camera camera) {
    	super(context);
    	Log.w("camera","camera view");
    	mCamera = camera;
    	mHolder = getHolder();
    	mPreviewCallbackDelegate=null;
    	mHolder.addCallback(CameraView.this);
    	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	mCamera.setPreviewCallback(CameraView.this);
    }
    
    public CameraView(Context context, PreviewCallback _previewCallbackDelegate) {
    	super(context);
    	Log.w("camera","camera view");
    	mCamera = CameraManager.getCamera();
    	Log.w("CameraView", "camera:"+String.valueOf(mCamera==null));
    	mHolder = getHolder();
    	mPreviewCallbackDelegate=_previewCallbackDelegate;
    	mHolder.addCallback(CameraView.this);
    	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	mCamera.setPreviewCallback(CameraView.this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOG_TAG,"Setting Constant.imageWidth: " + Constant.imageWidth + " Constant.imageHeight: " + Constant.imageHeight + " frameRate: " + Constant.frameRate);
        Camera.Parameters camParams = mCamera.getParameters();
        camParams.setPreviewSize(Constant.imageWidth, Constant.imageHeight);

        Log.v(LOG_TAG,"Preview Framerate: " + camParams.getPreviewFrameRate());

        camParams.setPreviewFrameRate(Constant.frameRate);
        mCamera.setParameters(camParams);
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            mHolder.addCallback(null);
            mCamera.setPreviewCallback(null);
        } catch (RuntimeException e) {
            // The camera has probably just been released, ignore.
        }
    }

    public void startPreview() {
        if (!isPreviewOn && mCamera != null) {
            isPreviewOn = true;
            mCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (isPreviewOn && mCamera != null) {
            isPreviewOn = false;
        }
        
        if(mCamera != null) {
        	mCamera.stopPreview();
        	mCamera.setPreviewCallback(null);
        	mCamera.release();
        	mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
    	if(mPreviewCallbackDelegate!=null){
    		mPreviewCallbackDelegate.onPreviewFrame(data, camera);
    	}
    }
    
    //logic is not complete,
    public void switchCamera(int cameraFrontOrBack){
    	stopPreview();
    	mCamera=CameraManager.switchCamera(cameraFrontOrBack);
		startPreview();
    }
}