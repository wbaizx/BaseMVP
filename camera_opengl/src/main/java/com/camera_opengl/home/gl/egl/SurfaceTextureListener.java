package com.camera_opengl.home.gl.egl;

import android.graphics.SurfaceTexture;

public interface SurfaceTextureListener {
    void onSurfaceCreated(SurfaceTexture surfaceTexture);

    void onSurfaceChanged(int width, int height);
}
