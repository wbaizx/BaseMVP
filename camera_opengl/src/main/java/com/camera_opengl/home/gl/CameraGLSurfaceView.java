package com.camera_opengl.home.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CameraGLSurfaceView extends GLSurfaceView {
    private CameraRenderer renderer = new CameraRenderer();

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(3);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onPause() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.onSurfaceDestroy();
            }
        });
        super.onPause();
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        renderer.setSurfaceTextureListener(surfaceTextureListener);
    }
}
