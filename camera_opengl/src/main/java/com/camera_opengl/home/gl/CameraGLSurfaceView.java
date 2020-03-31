package com.camera_opengl.home.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Size;

import com.camera_opengl.home.gl.renderer.FBORenderer;

public class CameraGLSurfaceView extends GLSurfaceView {
    private FBORenderer renderer = new FBORenderer();

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(3);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        renderer.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void confirmSize(Size cameraSize) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.confirmSize(cameraSize);
            }
        });
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
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.destroy();
            }
        });
    }

    public void takePicture() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.takePicture();
            }
        });
    }
}
