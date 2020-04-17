package com.camera_opengl.home.gl.record;

import android.util.Size;


public class RecordManager {
    private VideoEncoder videoEncoder = new VideoEncoder();

    public RecordManager(RecordListener recordListener) {
        videoEncoder.setRecordListener(recordListener);
    }

    public void startRecord() {
        if (videoEncoder.getStatus() == VideoEncoder.STATUS_READY) {
            videoEncoder.startRecord();
        }
    }

    public void confirmReallySize(Size reallySize) {
        videoEncoder.confirmReallySize(reallySize);
    }

    public void onPause() {
        stopRecord();
    }

    public void stopRecord(){
        if (videoEncoder.getStatus() == VideoEncoder.STATUS_START) {
            videoEncoder.stopRecord();
        }
    }

    public int getStatus() {
        return videoEncoder.getStatus();
    }
}
