package com.camera_opengl.home.gl.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES30;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.gl.GLHelper;

import java.nio.FloatBuffer;

public class WatermarkRenderer extends BaseRenderer {
    private static final String TAG = "WatermarkRenderer";

    private static final int POSITION_LOCAL = 0;
    private static final int TEXCOORD_LOCAL = 1;

    private FloatBuffer vertexBuffer = GLHelper.getFloatBuffer(new float[]{
            -1.0f, 1.0f, //左上
            -1.0f, -1.0f, //左下
            1.0f, 1.0f,  //右上
            1.0f, -1.0f,  //右下
    });

    /**
     * 在Android平台中，Bitmap绑定的2D纹理，是上下颠倒的
     * 所以这里翻转一下纹理顶点
     */
    private FloatBuffer textureCoordBuffer = GLHelper.getFloatBuffer(new float[]{
            0.0f, 0.0f, //左下
            0.0f, 1.0f, //左上
            1.0f, 0.0f, //右下
            1.0f, 1.0f, //右上
    });

    private int program;

    //VBO
    private int[] vboArray = new int[2];
    //VAO
    private int[] vaoArray = new int[1];

    private int[] watermarkTexture = new int[1];

    private int watermarkWidth;
    private int watermarkHeight;

    @Override
    public void onSurfaceCreated() {
        LogUtilKt.log(TAG, "onSurfaceCreated");
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        program = GLHelper.compileAndLink("watermark/watermark_v_shader.glsl", "watermark/watermark_f_shader.glsl");

        createVBO();
        createVAO();

        GLHelper.createWatermarkTexture(createWatermarkBitmap(), watermarkTexture);

        LogUtilKt.log(TAG, "onSurfaceCreated X");
    }

    private Bitmap createWatermarkBitmap() {
        Paint paint = new Paint();
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.WHITE);

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        watermarkWidth = (int) paint.measureText("这是水印");
        watermarkHeight = fm.descent - fm.ascent;

        Bitmap bitmap = Bitmap.createBitmap(watermarkWidth, watermarkHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText("这是水印", 0, fm.leading - fm.ascent, paint);
        canvas.save();

        return bitmap;
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

        //开启混合
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        GLES30.glViewport(100, 50, watermarkWidth, watermarkHeight);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, watermarkTexture[0]);

        GLES30.glBindVertexArray(vaoArray[0]);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
        GLES30.glDisableVertexAttribArray(POSITION_LOCAL);
        GLES30.glDisableVertexAttribArray(TEXCOORD_LOCAL);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, GLES30.GL_NONE);

        GLES30.glDisable(GLES30.GL_BLEND);
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
