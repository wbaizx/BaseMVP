package com.camera_opengl.home.gl.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.camera_opengl.home.ControlListener;

public class EGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "EGLSurfaceView";

    private GLThread glThread;

    public EGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        glThread = new GLThread();
        glThread.start();
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        glThread.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void setControlListener(ControlListener controlListener) {
        glThread.setControlListener(controlListener);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        glThread.surfaceCreated(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        glThread.surfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        glThread.surfaceDestroyed();
    }

    public void confirmCameraSize(Size reallySize) {
        glThread.confirmCameraSize(reallySize);
    }

    public void queueEvent(Runnable event) {
        glThread.queueEvent(event);
    }

    public void takePicture() {
        glThread.takePicture();
    }

    public void onDestroy() {
        glThread.onDestroy();
    }
}