package com.camera_opengl.home.record;

import android.view.Surface;

public interface RecordListener {
    void onEncoderSurfaceCreated(Surface surface);

    void onEncoderSurfaceDestroy();
}
