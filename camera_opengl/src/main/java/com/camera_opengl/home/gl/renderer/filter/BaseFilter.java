package com.camera_opengl.home.gl.renderer.filter;

import android.opengl.GLES30;

import com.base.common.util.LogUtil;

public abstract class BaseFilter {
    private static final String TAG = "BaseFilter";

    protected int program;
    private FilterType type;

    public BaseFilter(FilterType type) {
        this.type = type;
    }

    public abstract void init();

    public void use() {
        GLES30.glUseProgram(program);
    }

    public abstract void useFilter();

    public void release() {
        LogUtil.INSTANCE.log(TAG, "release");
        onSurfaceDestroy();
        onDestroy();
    }

    public void onSurfaceDestroy() {
        LogUtil.INSTANCE.log(TAG, "onSurfaceDestroy");
        GLES30.glDeleteProgram(program);
    }

    public void onDestroy() {
        LogUtil.INSTANCE.log(TAG, "onDestroy");
    }

    public String getId() {
        return type.getId();
    }

    public int getLut() {
        return type.getLut();
    }
}
