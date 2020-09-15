package com.camera_opengl.home.record;

import android.util.Size;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.record.encoder.AudioEncoder;
import com.camera_opengl.home.record.encoder.VideoEncoder;

public class RecordManager {
    private static final String TAG = "RecordManager";

    private MuxerManager muxerManager = new MuxerManager();
    private VideoEncoder videoEncoder = new VideoEncoder(muxerManager);
    private AudioEncoder audioEncoder = new AudioEncoder(muxerManager);

    private Size reallySize;

    public RecordManager(RecordListener recordListener) {
        videoEncoder.setRecordListener(recordListener);
    }

    public boolean isRecording() {
        return videoEncoder.getStatus() == VideoEncoder.STATUS_START
                && audioEncoder.getStatus() == AudioEncoder.STATUS_START
                && muxerManager.getStatus() == MuxerManager.STATUS_START;
    }

    public boolean isReady() {
        return videoEncoder.getStatus() == VideoEncoder.STATUS_READY
                && audioEncoder.getStatus() == AudioEncoder.STATUS_READY
                && muxerManager.getStatus() == MuxerManager.STATUS_READY;
    }

    public void confirmReallySize(Size reallySize) {
        this.reallySize = reallySize;
        LogUtilKt.log(TAG, "confirmCameraSize " + reallySize.getWidth() + "  " + reallySize.getHeight());
    }

    public void startRecord() {
        if (isReady() && reallySize != null) {
            boolean initSuccess = muxerManager.init();
            if (initSuccess) {
                audioEncoder.startRecord();
                videoEncoder.startRecord(reallySize);
            }
        }
    }

    public void stopRecord() {
        audioEncoder.stopRecord();
        videoEncoder.stopRecord();
    }

    public void onPause() {
        stopRecord();
    }

    public void onDestroy() {
        audioEncoder.onDestroy();
        videoEncoder.onDestroy();
    }
}
