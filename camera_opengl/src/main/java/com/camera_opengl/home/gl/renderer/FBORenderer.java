package com.camera_opengl.home.gl.renderer;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Size;

import com.base.common.util.ImageUtil;
import com.base.common.util.LogUtil;
import com.camera_opengl.home.gl.GLHelper;
import com.camera_opengl.home.gl.SurfaceTextureListener;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FBORenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "FBORenderer";

    private static final int POSITION_LOCAL = 0;
    private static final int TEXCOORD_LOCAL = 1;
    private static final int TEX_MATRIXC_LOCAL = 2;

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

    private FloatBuffer vertexBuffer = GLHelper.getFloatBuffer(vertex);
    private FloatBuffer textureCoordBuffer = GLHelper.getFloatBuffer(textureCoord);

    private int program;

    private int[] fboTexture = new int[1];
    private int[] fboArray = new int[1];

    //接收相机数据的纹理
    private int[] texture = new int[1];
    private SurfaceTexture surfaceTexture;
    private SurfaceTextureListener surfaceTextureListener;

    //VBO
    private int[] vboArray = new int[2];
    //VAO
    private int[] vaoArray = new int[1];

    private int previewWidth;
    private int previewHeight;

    private ScreenRenderer screenRenderer = new ScreenRenderer();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated");
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GLHelper.createExternalSurface(texture);
        surfaceTexture = new SurfaceTexture(texture[0]);
        //回传surfaceTexture
        surfaceTextureListener.onSurfaceCreated(surfaceTexture);

        program = GLHelper.compileAndLink("vshader/fbo_v_shader.glsl", "fshader/fbo_f_shader.glsl");

        createVBO();
        createVAO();

        GLHelper.createFBOSurface(fboTexture);

        screenRenderer.onSurfaceCreated(gl, config);
        screenRenderer.setFBOtextureId(fboTexture[0]);
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
    }

    private void createFBO() {
        // 创建 FBO
        GLES30.glGenFramebuffers(1, fboArray, 0);
        // 绑定 FBO
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0]);
        // 绑定 FBO 纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTexture[0]);
        // 将纹理连接到 FBO 附着
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D,
                fboTexture[0], 0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, previewWidth, previewHeight, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

        if (GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER) != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("创建fbo失败");
        }
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtil.INSTANCE.log(TAG, "onSurfaceChanged " + width + "--" + height);
        surfaceTextureListener.onSurfaceChanged(width, height);
        screenRenderer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0]);

        GLES30.glViewport(0, 0, previewWidth, previewHeight);
        GLES30.glUseProgram(program);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        surfaceTexture.updateTexImage();

        surfaceTexture.getTransformMatrix(texMatrix);
        GLES30.glUniformMatrix4fv(TEX_MATRIXC_LOCAL, 1, false, texMatrix, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);

        GLES30.glBindVertexArray(vaoArray[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
        GLES30.glDisableVertexAttribArray(POSITION_LOCAL);
        GLES30.glDisableVertexAttribArray(TEXCOORD_LOCAL);

        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_NONE);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);
        screenRenderer.onDrawFrame(gl);
    }

    /**
     * 在Android平台中，Bitmap绑定的2D纹理，是上下颠倒的
     */
    public void takePicture() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0]);
        ByteBuffer buf = ByteBuffer.allocateDirect(previewWidth * previewHeight * GLHelper.BYTES_PER_FLOAT);
        GLES30.glReadPixels(0, 0, previewWidth, previewHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
        if (GLES30.glGetError() != GLES30.GL_NO_ERROR) {
            throw new RuntimeException("获取像素失败");
        }
        Bitmap bmp = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(buf);

        File file = ImageUtil.INSTANCE.savePicture(bmp, "AA" + System.currentTimeMillis() + ".jpg");
        LogUtil.INSTANCE.log(TAG, "savePicture " + file.getAbsolutePath());
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_NONE);
    }

    public void confirmSize(Size cameraSize) {
        LogUtil.INSTANCE.log(TAG, "cameraSize " + cameraSize.getHeight() + "--" + cameraSize.getWidth());
        //宽高需要对调
        this.previewWidth = cameraSize.getHeight();
        this.previewHeight = cameraSize.getWidth();
        screenRenderer.confirmSize(cameraSize);
        createFBO();
    }

    public void setSurfaceTextureListener(SurfaceTextureListener surfaceTextureListener) {
        this.surfaceTextureListener = surfaceTextureListener;
    }

    public void onSurfaceDestroy() {
        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy");
        GLES30.glDeleteProgram(program);
        GLES30.glDeleteTextures(texture.length, texture, 0);
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
        GLES30.glDeleteBuffers(2, vboArray, 0);
        GLES30.glDeleteVertexArrays(1, vaoArray, 0);
        GLES30.glDeleteTextures(1, fboTexture, 0);
        GLES30.glDeleteFramebuffers(1, fboArray, 0);
        screenRenderer.onSurfaceDestroy();
        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy X");
    }

    public void destroy() {
        vertexBuffer.clear();
        textureCoordBuffer.clear();
        surfaceTextureListener = null;
        screenRenderer.destroy();
        LogUtil.INSTANCE.log(TAG, "destroy X");
    }
}