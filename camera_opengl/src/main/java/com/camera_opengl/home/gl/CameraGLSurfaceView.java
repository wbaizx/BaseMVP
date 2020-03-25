package com.camera_opengl.home.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Size;

public class CameraGLSurfaceView extends GLSurfaceView {
    private CameraRenderer renderer = new CameraRenderer();

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(3);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        renderer.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void confirmSize(Size previewSize, Size videoSize) {
        renderer.confirmSize(previewSize, videoSize);
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

    public void destroy() {
        renderer.destroy();
    }
}
