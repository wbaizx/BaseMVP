package com.camera_opengl.home.play;

import android.util.Size;

public interface PlayListener {
    void confirmPlaySize(Size playSize);

    void playEnd();
}
