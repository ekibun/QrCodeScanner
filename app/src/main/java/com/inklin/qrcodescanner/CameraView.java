package com.inklin.qrcodescanner;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.inklin.qrcodescanner.utils.CameraUtils;

import java.util.List;

/**
 * Created by acaoa on 2017/8/17.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraView";

    SurfaceHolder holder;
    public Camera camera;

    public CameraView(Context context){
        this(context, null);
    }
    public CameraView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        loadCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        loadCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private float distZoom = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(camera == null)
            return false;
        if (event.getPointerCount() == 1) {
            focus(event.getX(), event.getY());//CameraUtils.handleFocusMetering(event.getX(), event.getY(), camera, getWidth(), getHeight());
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    Camera.Parameters params = camera.getParameters();
                    distZoom = getFingerSpacing(event) - params.getZoom() * 10;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    if (newDist > distZoom) {
                        CameraUtils.handleZoom((int) (newDist - distZoom) / 10, camera);
                    }
                    break;
            }
        }
        return true;
    }

    public void focus(float x, float y){
        if(getWidth() == 0 || getHeight() == 0)
            return;
        if(x == -1)
            x = getWidth()/2;
        if(y == -1)
            y = getHeight()/2;
        CameraUtils.handleFocusMetering(x, y, camera, getWidth(), getHeight());
    }

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void init(){
        holder = getHolder();
        holder.setKeepScreenOn(true);//屏幕常亮
        holder.addCallback(this);
    }

    public Camera getCameraInstance() {
        if (camera == null)
            try {
                camera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return camera;
    }

    public void releaseCamera(){
        if (camera != null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void loadCamera(){
        init();
        getCameraInstance();
        try{
            camera.setParameters(camera.getParameters());
            camera.setPreviewDisplay(holder);
            CameraUtils.setParameters(camera, getWidth(), getHeight());
            camera.startPreview();
        }catch (Exception e){
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public boolean setFlashLight(boolean open) {
        return CameraUtils.setFlashLight(open, camera);
    }

}
