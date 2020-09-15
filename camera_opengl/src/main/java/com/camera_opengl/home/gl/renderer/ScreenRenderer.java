package com.camera_opengl.home.gl.renderer;

import android.opengl.GLES30;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.gl.GLHelper;

import java.nio.FloatBuffer;

public class ScreenRenderer extends BaseRenderer {
    private static final String TAG = "ScreenRenderer";

    private static final int POSITION_LOCAL = 0;
    private static final int TEXCOORD_LOCAL = 1;
    private static final int POS_MATRIX_LOCAL = 2;

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

    //VBO
    private int[] vboArray = new int[2];
    //VAO
    private int[] vaoArray = new int[1];

    private float[] posMatrix = new float[16];

    @Override
    public void onSurfaceCreated() {
        LogUtilKt.log(TAG, "onSurfaceCreated");
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        program = GLHelper.compileAndLink("screen/screen_v_shader.glsl", "screen/screen_f_shader.glsl");

        createVBO();
        createVAO();

        LogUtilKt.log(TAG, "onSurfaceCreated X");
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

        LogUtilKt.log(TAG, "createVBO X");
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

        LogUtilKt.log(TAG, "createVAO X");
    }

    @Override
    public void onDrawFrame() {
        GLES30.glUseProgram(program);
        GLES30.glViewport(0, 0, viewWidth, viewHeight);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, inTextureId);

        calculationVertexMatrix(posMatrix);
        GLES30.glUniformMatrix4fv(POS_MATRIX_LOCAL, 1, false, posMatrix, 0);

        GLES30.glBindVertexArray(vaoArray[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
        GLES30.glDisableVertexAttribArray(POSITION_LOCAL);
        GLES30.glDisableVertexAttribArray(TEXCOORD_LOCAL);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
    }

    @Override
    public void onSurfaceDestroy() {
        GLES30.glDeleteProgram(program);
        GLES30.glDeleteBuffers(2, vboArray, 0);
        GLES30.glDeleteVertexArrays(1, vaoArray, 0);

        LogUtilKt.log(TAG, "onSurfaceDestroy X");
    }

    @Override
    public void onDestroy() {
        vertexBuffer.clear();
        textureCoordBuffer.clear();

        LogUtilKt.log(TAG, "onDestroy X");
    }
}
