package com.camera_opengl.home.gl.renderer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Size;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.gl.GLHelper;

import java.nio.FloatBuffer;

public class ScreenRenderer extends BaseRenderer {
    private static final String TAG = "ScreenRenderer";

    private static final int POSITION_LOCAL = 0;
    private static final int TEXCOORD_LOCAL = 1;
    private static final int POS_MATRIX_LOCAL = 2;

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

    private FloatBuffer vertexBuffer = GLHelper.getFloatBuffer(vertex);
    private FloatBuffer textureCoordBuffer = GLHelper.getFloatBuffer(textureCoord);

    private int program;

    private int textureId;

    //VBO
    private int[] vboArray = new int[2];
    //VAO
    private int[] vaoArray = new int[1];

    private float[] posMatrixc = new float[16];

    private int reallyWidth;
    private int reallyHeight;
    private int viewWidth;
    private int viewHeight;

    @Override
    public void onSurfaceCreated() {
        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated");
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        program = GLHelper.compileAndLink("vshader/screen_v_shader.glsl", "fshader/screen_f_shader.glsl");

        createVBO();
        createVAO();

        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated X");
    }

    @Override
    public int getOutTexture() {
        return 0;
    }

    @Override
    public void setInTexture(int textureId) {
        this.textureId = textureId;
    }

    @Override
    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {
        LogUtil.INSTANCE.log(TAG, "onSurfaceChanged " + viewWidth + "--" + viewHeight);
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
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

    @Override
    public void confirmReallySize(Size cameraSize) {
        //宽高需要对调
        this.reallyWidth = cameraSize.getHeight();
        this.reallyHeight = cameraSize.getWidth();
    }

    @Override
    public void onDrawFrame() {
        GLES30.glViewport(0, 0, viewWidth, viewHeight);
        GLES30.glUseProgram(program);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

        calculationVertexMatrix(posMatrixc);
        GLES30.glUniformMatrix4fv(POS_MATRIX_LOCAL, 1, false, posMatrixc, 0);

        GLES30.glBindVertexArray(vaoArray[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
        GLES30.glDisableVertexAttribArray(POSITION_LOCAL);
        GLES30.glDisableVertexAttribArray(TEXCOORD_LOCAL);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);
    }

    private void calculationVertexMatrix(float[] posMatrixc) {
        Matrix.setIdentityM(posMatrixc, 0);

        float viewScale = (float) viewWidth / (float) viewHeight;
        float cameraScale = (float) reallyWidth / (float) reallyHeight;

        final float aspectRatio = viewScale > cameraScale ?
                viewScale / cameraScale :
                cameraScale / viewScale;

        LogUtil.INSTANCE.log(TAG, "calculationMatrix aspectRatio " + aspectRatio);
        if (viewScale > cameraScale) {
            //视图的宽高比更大，同高下视图更宽，映射出来应该缩放宽度
            //高度已经全屏，只能使用正交投影
            Matrix.orthoM(posMatrixc, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //纹理的宽高比更大，同高下纹理更宽，映射出来应该缩放高度
            //方法一，使用正交矩阵，视图按比例居中
            Matrix.orthoM(posMatrixc, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);

            //方法二，按比例修改顶点(需注意使用VAO VBO的情况)，视图可控制居上
//            float v = 2 - 2 / aspectRatio;
//            //-1.0f, -1.0f, //左下
//            vertexBuffer.put(3, -1f + v);
//            // 1.0f, -1.0f,  //右下
//            vertexBuffer.put(7, -1f + v);

            //方法三，按比例裁剪视图渲染区域(反向)，视图可控制居上
//            float portHeight = viewHeight / aspectRatio;
//            GLES30.glViewport(0, (int) (viewHeight - portHeight), viewWidth, (int) portHeight);
        }
    }

    @Override
    public void onSurfaceDestroy() {
        GLES30.glDeleteProgram(program);
        GLES30.glDeleteBuffers(2, vboArray, 0);
        GLES30.glDeleteVertexArrays(1, vaoArray, 0);

        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy X");
    }

    @Override
    public void onDestroy() {
        vertexBuffer.clear();
        textureCoordBuffer.clear();

        LogUtil.INSTANCE.log(TAG, "onDestroy X");
    }
}
