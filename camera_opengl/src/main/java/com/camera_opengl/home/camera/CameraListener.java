package com.camera_opengl.home.camera;

import android.util.Size;

public interface CameraListener {
    void confirmSize(Size previewSize, Size videoSize);
}
