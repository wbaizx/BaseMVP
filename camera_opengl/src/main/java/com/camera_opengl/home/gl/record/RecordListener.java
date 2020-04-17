package com.camera_opengl.home.gl.record;

import android.view.Surface;

public interface RecordListener {
    void onEncoderSurfaceCreated(Surface surface);

    void onEncoderSurfaceDestroy();
}
