package com.camera_opengl.home.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Size;

import com.base.common.util.LogUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "CameraRenderer";

    private static final int POSITION_LOCAL = 0;
    private static final int TEXCOORD_LOCAL = 1;
    private static final int TEX_MATRIXC_LOCAL = 2;
    private static final int POS_MATRIX_LOCAL = 3;

    private float[] vertex = {
            -1.0f, 1.0f, //左上
            -1.0f, -1.0f, //左下
            1.0f, 1.0f,  //右上
            1.0f, -1.0f,  //右下
    };

    private float[] textureCoord = {
            0.0f, 1.0f, //左上
            0.0f, 0.0f, //左下
            1.0f, 1.0f, //右上
            1.0f, 0.0f, //右下
    };

    private float[] texMatrix = new float[16];
    private float[] posMatrixc = new float[16];

    private FloatBuffer vertexBuffer = GLHelper.getFloatBuffer(vertex);
    private FloatBuffer textureCoordBuffer = GLHelper.getFloatBuffer(textureCoord);

    private int program;

    //接收相机数据的纹理
    private int[] texture = new int[1];
    private SurfaceTexture surfaceTexture;
    private SurfaceTextureListener surfaceTextureListener;

    private int viewWidth;
    private int viewHeight;
    private int previewWidth;
    private int previewHeight;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated");
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GLHelper.createExternalSurface(texture);
        surfaceTexture = new SurfaceTexture(texture[0]);

        program = GLHelper.compileAndLink("vshader/camera_v_shader.glsl", "fshader/camera_f_shader.glsl");

        surfaceTextureListener.onSurfaceCreated(surfaceTexture);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtil.INSTANCE.log(TAG, "onSurfaceChanged " + width + "--" + height);
        this.viewWidth = width;
        this.viewHeight = height;
        GLES30.glViewport(0, 0, width, height);
        surfaceTextureListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(program);
        surfaceTexture.updateTexImage();

        GLES30.glEnableVertexAttribArray(POSITION_LOCAL);
        GLES30.glEnableVertexAttribArray(TEXCOORD_LOCAL);

        GLES30.glVertexAttribPointer(POSITION_LOCAL, 2, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glVertexAttribPointer(TEXCOORD_LOCAL, 2, GLES30.GL_FLOAT, false, 0, textureCoordBuffer);


        surfaceTexture.getTransformMatrix(texMatrix);
        GLES30.glUniformMatrix4fv(TEX_MATRIXC_LOCAL, 1, false, texMatrix, 0);

        calculationVertexMatrix();
        Matrix.setIdentityM(posMatrixc, 0);
        GLES30.glUniformMatrix4fv(POS_MATRIX_LOCAL, 1, false, posMatrixc, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisableVertexAttribArray(POSITION_LOCAL);
        GLES30.glDisableVertexAttribArray(TEXCOORD_LOCAL);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    private void calculationVertexMatrix() {
//        float[] orthoMatrix = new float[16];
//        Matrix.setIdentityM(orthoMatrix, 0);
//
//        float viewScale = (float) viewWidth / (float) viewHeight;
//        LogUtil.INSTANCE.log(TAG, "calculationMatrix viewScale " + viewScale);
//        float previewScale = (float) previewWidth / (float) previewHeight;
//        LogUtil.INSTANCE.log(TAG, "calculationMatrix previewScale " + previewScale);
//
//        final float aspectRatio = viewScale > previewScale ?
//                previewScale / viewScale :
//                viewScale / previewScale;
//
//        LogUtil.INSTANCE.log(TAG, "calculationMatrix aspectRatio " + aspectRatio);
//        if (viewScale > previewScale) {
//            Matrix.orthoM(orthoMatrix, 0, -1f, 1f, -0.5f, 0.5f, -1f, 1f);
//        } else {
//            Matrix.orthoM(orthoMatrix, 0, -0.5f, 0.5f, -1f, 1f, -1f, 1f);
//            LogUtil.INSTANCE.log(TAG, "calculationMatrix aspectRatio -------");
//        }

//        surfaceTexture.getTransformMatrix(matrix);
//        Matrix.setIdentityM(matrix, 0);
//        Matrix.multiplyMM(matrix, 0, orthoMatrix, 0, matrix, 0);
//        Matrix.multiplyMM(orthoMatrix, 0,aa , 0, matrix, 0);
//        Matrix.rotateM(matrix,0,90f,0f,0f,1f);
//        Matrix.rotateM(matrix,0,90f,0f,0f,1f);
    }

    public void confirmSize(Size previewSize, Size videoSize) {
        LogUtil.INSTANCE.log(TAG, "confirmSize  previewSize " + previewSize.getHeight() + "--" + previewSize.getWidth());
        //宽高需要对调
        this.previewWidth = previewSize.getHeight();
        this.previewHeight = previewSize.getWidth();
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        this.surfaceTextureListener = surfaceTextureListener;
    }

    public void onSurfaceDestroy() {
        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy");
        GLES30.glDeleteProgram(program);
        GLES30.glDeleteTextures(texture.length, texture, 0);
        surfaceTexture.release();
        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy X");
    }

    public void destroy() {
        LogUtil.INSTANCE.log(TAG, "destroy");
        surfaceTextureListener = null;
    }
}
