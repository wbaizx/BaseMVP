package com.camera_opengl.home.gl.renderer;

import android.graphics.Bitmap;
import android.opengl.Matrix;
import android.util.Size;

import com.base.common.util.LogUtilKt;

abstract public class BaseRenderer {
    private static final String TAG = "BaseRenderer";

    /**
     * 实际数据宽高
     */
    protected int reallyWidth;
    protected int reallyHeight;

    /**
     * 控件或容器宽高
     */
    protected int viewWidth;
    protected int viewHeight;

    /**
     * 输入纹理数据id
     */
    protected int inTextureId;

    public abstract void onSurfaceCreated();

    /**
     * 输出纹理id，主要用于fbo输出
     */
    public int getOutTexture() {
        return 0;
    }

    public void setInTexture(int textureId) {
        inTextureId = textureId;
    }

    public void onSurfaceChanged(int viewWidth, int viewHeight) {
        LogUtilKt.log(TAG, "onSurfaceChanged " + viewWidth + "--" + viewHeight);
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * 获取实际数据宽高
     */
    public void confirmReallySize(Size reallySize) {
        LogUtilKt.log(TAG, "confirmReallySize");
        this.reallyHeight = reallySize.getHeight();
        this.reallyWidth  = reallySize.getWidth();
    }

    /**
     * 每帧绘制方法
     */
    public abstract void onDrawFrame();

    /**
     * 每帧绘制方法
     *
     * @param surfaceMatrix 携带相机surface的矩阵信息，主要用于fbo中纹理矩阵配置
     */
    public void onDrawFrame(float[] surfaceMatrix) {
    }

    public abstract void onSurfaceDestroy();

    public abstract void onDestroy();

    /**
     * 读取像素
     * 在Android平台中，Bitmap绑定的2D纹理，是上下颠倒的
     * 华为手机获取为黑图，但在fbo中就没问题（原因不知）
     */
    public Bitmap takePicture() {
        return null;
    }

    /**
     * 用于计算顶点矩阵
     */
    protected void calculationVertexMatrix(float[] posMatrixc) {
        Matrix.setIdentityM(posMatrixc, 0);

        float viewScale = (float) viewWidth / (float) viewHeight;
        float cameraScale = (float) reallyWidth / (float) reallyHeight;

        final float aspectRatio = viewScale > cameraScale ?
                viewScale / cameraScale :
                cameraScale / viewScale;

        LogUtilKt.log(TAG, "calculationMatrix aspectRatio " + aspectRatio);
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
}
