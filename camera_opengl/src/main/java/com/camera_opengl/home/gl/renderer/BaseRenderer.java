package com.camera_opengl.home.gl.renderer;

import android.graphics.SurfaceTexture;
import android.util.Size;

abstract public class BaseRenderer {
    public abstract void onSurfaceCreated();

    public abstract int getOutTexture();

    public abstract void setInTexture(int textureId);

    public abstract void setSurfaceTexture(SurfaceTexture surfaceTexture);

    public abstract void onSurfaceChanged(int viewWidth, int viewHeight);

    public abstract void confirmReallySize(Size cameraSize);

    public abstract void onDrawFrame();

    public abstract void onSurfaceDestroy();

    public abstract void onDestroy();
}
