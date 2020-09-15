package com.camera_opengl.home.gl.renderer.filter;

import android.opengl.GLES30;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.gl.GLHelper;

public class LutFilter extends BaseFilter {
    private static final String TAG = "LutFilter";

    private static final int FILTER_LOCAL = 3;
    //滤镜texture
    private int[] filterTexture = new int[1];

    public LutFilter(FilterType type) {
        super(type);
    }

    @Override
    public void init() {
        GLHelper.createLUTFilterTexture(getLut(), filterTexture);
        program = GLHelper.compileAndLink("fbo/fbo_v_shader.glsl", "fbo/fbo_f_lut.glsl");
        LogUtilKt.log(TAG, "init");
    }

    @Override
    public void useFilter() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        //激活启用1纹理单元，绑定滤镜纹理数据，
        //调用glUniform1i传递1（个人理解glUniform1i仅是告诉层级关系的，所以上面原始数据可以不调，默认为0）
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, filterTexture[0]);
        GLES30.glUniform1i(FILTER_LOCAL, 1);
        GLHelper.glGetError("useFilter");
    }

    @Override
    public void onSurfaceDestroy() {
        LogUtilKt.log(TAG, "onSurfaceDestroy filterTexture");
        GLES30.glDeleteTextures(filterTexture.length, filterTexture, 0);
        super.onSurfaceDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
