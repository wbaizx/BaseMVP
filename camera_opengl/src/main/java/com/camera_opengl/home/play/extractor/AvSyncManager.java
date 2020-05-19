package com.camera_opengl.home.play.extractor;

public class AvSyncManager {
    private long audioTimestamp;
    private long videoTimestamp;

    public void syncAudioTime(long audioTimestamp) {
        this.audioTimestamp = audioTimestamp;
        sync();
    }

    public void syncVideoTime(long videoTimestamp) {
        this.videoTimestamp = videoTimestamp;
    }

    private void sync() {
        long difference = audioTimestamp - videoTimestamp;
        if (difference > 50) {
            //音频超前，需要视频减慢
        } else if (difference < -50) {
            //音频滞后，需要视频超前
        }
    }
}
