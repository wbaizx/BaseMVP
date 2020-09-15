package com.camera_opengl.home.gl.renderer.filter;

import android.opengl.GLES30;

import com.base.common.util.LogUtilKt;

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
        LogUtilKt.log(TAG, "release");
        onSurfaceDestroy();
        onDestroy();
    }

    public void onSurfaceDestroy() {
        LogUtilKt.log(TAG, "onSurfaceDestroy");
        GLES30.glDeleteProgram(program);
    }

    public void onDestroy() {
        LogUtilKt.log(TAG, "onDestroy");
    }

    public String getId() {
        return type.getId();
    }

    public int getLut() {
        return type.getLut();
    }
}
