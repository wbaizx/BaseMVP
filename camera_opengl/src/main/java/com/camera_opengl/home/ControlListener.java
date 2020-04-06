package com.camera_opengl.home;

import android.graphics.Bitmap;
import android.util.Size;

public interface ControlListener {
    void confirmCameraSize(Size cameraSize);

    void imageAvailable(byte[] bytes, boolean horizontalMirror, boolean verticalMirror);

    void imageAvailable(Bitmap btm, boolean horizontalMirror, boolean verticalMirror);
}
