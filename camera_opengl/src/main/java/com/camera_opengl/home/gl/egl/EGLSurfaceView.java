package com.camera_opengl.home.gl.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.camera_opengl.home.camera.CameraControlListener;
import com.camera_opengl.home.gl.renderer.filter.FilterType;

public class EGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "EGLSurfaceView";

    private GLThread glThread;

    public EGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        glThread = new GLThread();
        glThread.start();
    }

    public void setGlSurfaceListener(GLSurfaceListener glSurfaceListener) {
        glThread.setGlSurfaceListener(glSurfaceListener);
    }

    public void setCameraControlListener(CameraControlListener cameraControlListener) {
        glThread.setCameraControlListener(cameraControlListener);
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

    /**
     * 此方法涉及fbo纹理配置更新，每次surface销毁重建后（比如home退出）都必须调用此方法
     */
    public void confirmReallySize(Size reallySize) {
        glThread.confirmReallySize(reallySize);
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

    public void switchFilterType(FilterType type) {
        glThread.switchFilterType(type);
    }

    public void onEncoderSurfaceCreated(Surface surface) {
        glThread.onEncoderSurfaceCreated(surface);
    }

    public void onEncoderSurfaceDestroy() {
        glThread.onEncoderSurfaceDestroy();
    }
}
