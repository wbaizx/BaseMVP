package com.camera_opengl.home.play.extractor;

public interface TimestampListener {
    void syncTime(long time);

    void endSyncTime();
}
