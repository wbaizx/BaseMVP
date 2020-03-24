package com.camera_opengl.home.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.base.common.util.LogUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "CameraRenderer";

    private float[] vertex = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f,  1.0f,
    };

    private float[] textureCoord = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    private FloatBuffer vertexBuffer = GLHelper.getFloatBuffer(vertex);
    private FloatBuffer textureCoordBuffer = GLHelper.getFloatBuffer(textureCoord);

    private int program;

    //接收相机数据的纹理
    private int[] texture = new int[1];
    private SurfaceTexture surfaceTexture;
    private SurfaceTextureListener surfaceTextureListener;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GLHelper.createExternalSurface(texture);
        surfaceTexture = new SurfaceTexture(texture[0]);

        program = GLHelper.compileAndLink("vshader/camera_v_shader.glsl", "fshader/camera_f_shader.glsl");

        surfaceTextureListener.onSurfaceCreated(surfaceTexture);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        surfaceTextureListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtil.INSTANCE.log(TAG, "onDrawFrame");

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(program);
        surfaceTexture.updateTexImage();

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, textureCoordBuffer);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);

        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        this.surfaceTextureListener = surfaceTextureListener;
    }
}
