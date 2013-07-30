package com.example.junction;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Parameters parameters;
    private Size pSize;

    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        parameters = camera.getParameters();
        pSize = camera.getParameters().getPreviewSize();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
        	if (mCamera == null) {
        		mCamera = Camera.open();
        	}
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
        	
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        // start preview with new settings
        try {
        	if (mCamera == null) {
        		mCamera = Camera.open();
        	}
            mCamera.setPreviewDisplay(surfaceHolder);
            parameters.setPreviewSize(pSize.width, pSize.height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
        	
        }
    }
}