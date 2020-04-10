package com.camera_opengl.home.gl.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.util.Size;

import com.base.common.util.LogUtil;
import com.camera_opengl.R;
import com.camera_opengl.home.gl.GLHelper;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class FBORenderer extends BaseRenderer {
    private static final String TAG = "FBORenderer";

    private static final int POSITION_LOCAL = 0;
    private static final int TEXCOORD_LOCAL = 1;
    private static final int TEX_MATRIXC_LOCAL = 2;

    private FloatBuffer vertexBuffer = GLHelper.getFloatBuffer(new float[]{
            -1.0f, 1.0f, //左上
            -1.0f, -1.0f, //左下
            1.0f, 1.0f,  //右上
            1.0f, -1.0f,  //右下
    });
    private FloatBuffer textureCoordBuffer = GLHelper.getFloatBuffer(new float[]{
            0.0f, 1.0f, //左上
            0.0f, 0.0f, //左下
            1.0f, 1.0f, //右上
            1.0f, 0.0f, //右下
    });

    private int program;

    private int[] fboTexture = new int[1];
    private int[] fboArray = new int[1];

    //VBO
    private int[] vboArray = new int[2];
    //VAO
    private int[] vaoArray = new int[1];

    @Override
    public void onSurfaceCreated() {
        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated");

        program = GLHelper.compileAndLink("fbo/fbo_v_shader.glsl", "fbo/fbo_f_shader.glsl");

        createVBO();
        createVAO();

        GLHelper.createFBOTexture(fboTexture);
        createFBO();
//        GLHelper.createLUTFilterTexture(R.drawable.amatorka,new int[1]);
        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated X");
    }

    @Override
    public int getOutTexture() {
        return fboTexture[0];
    }

    @Override
    public void confirmReallySize(Size cameraSize) {
        super.confirmReallySize(cameraSize);
        updateFBO();
    }

    private void createVBO() {
        GLES30.glGenBuffers(2, vboArray, 0);

        //绑定VBO顶点数组
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * GLHelper.BYTES_PER_FLOAT,
                vertexBuffer, GLES30.GL_STATIC_DRAW);

        //绑定VBO纹理数组
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, textureCoordBuffer.capacity() * GLHelper.BYTES_PER_FLOAT,
                textureCoordBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE);

        LogUtil.INSTANCE.log(TAG, "createVBO X");
    }

    private void createVAO() {
        //创建VAO
        GLES30.glGenVertexArrays(1, vaoArray, 0);
        //绑定VAO
        GLES30.glBindVertexArray(vaoArray[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0]);
        GLES30.glEnableVertexAttribArray(POSITION_LOCAL);
        GLES30.glVertexAttribPointer(POSITION_LOCAL, 2, GLES30.GL_FLOAT, false, 0, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[1]);
        GLES30.glEnableVertexAttribArray(TEXCOORD_LOCAL);
        GLES30.glVertexAttribPointer(TEXCOORD_LOCAL, 2, GLES30.GL_FLOAT, false, 0, 0);

        //解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_NONE);
        //解绑VAO
        GLES30.glBindVertexArray(GLES30.GL_NONE);

        LogUtil.INSTANCE.log(TAG, "createVAO X");
    }

    private void createFBO() {
        // 创建 FBO
        GLES30.glGenFramebuffers(1, fboArray, 0);
        if (fboArray[0] == 0) {
            throw new RuntimeException("创建fbo失败");
        }
        LogUtil.INSTANCE.log(TAG, "createFBO X");
    }

    private void updateFBO() {
        // 绑定 FBO
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0]);
        // 绑定 FBO 纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTexture[0]);
        // 将纹理连接到 FBO 附着
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D,
                fboTexture[0], 0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, reallyWidth, reallyHeight, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("fbo挂载失败");
        }

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);

        LogUtil.INSTANCE.log(TAG, "updateFBO X");
    }

    @Override
    public void onDrawFrame(float[] surfaceMatrix) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0]);
        GLES30.glViewport(0, 0, reallyWidth, reallyHeight);

        GLES30.glUseProgram(program);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUniformMatrix4fv(TEX_MATRIXC_LOCAL, 1, false, surfaceMatrix, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, inTextureId);

        GLES30.glBindVertexArray(vaoArray[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
        GLES30.glDisableVertexAttribArray(POSITION_LOCAL);
        GLES30.glDisableVertexAttribArray(TEXCOORD_LOCAL);

        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_NONE);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);
    }

    @Override
    public void onDrawFrame() {

    }

    @Override
    public void onSurfaceDestroy() {
        GLES30.glDeleteProgram(program);
        GLES30.glDeleteBuffers(2, vboArray, 0);
        GLES30.glDeleteVertexArrays(1, vaoArray, 0);
        GLES30.glDeleteTextures(1, fboTexture, 0);
        GLES30.glDeleteFramebuffers(1, fboArray, 0);

        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy X");
    }

    @Override
    public void onDestroy() {
        vertexBuffer.clear();
        textureCoordBuffer.clear();

        LogUtil.INSTANCE.log(TAG, "onDestroy X");
    }

    @Override
    public Bitmap takePicture() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0]);
        ByteBuffer buf = ByteBuffer.allocate(reallyWidth * reallyHeight * GLHelper.BYTES_PER_FLOAT);
        GLES30.glReadPixels(0, 0, reallyWidth, reallyHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
        GLHelper.glGetError("glReadPixels");

        Bitmap btm = Bitmap.createBitmap(reallyWidth, reallyHeight, Bitmap.Config.ARGB_8888);
        btm.copyPixelsFromBuffer(buf);

        buf.clear();
        LogUtil.INSTANCE.log(TAG, "takePicture glReadPixels X");
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);

        return btm;
    }
}
