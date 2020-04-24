package com.camera_opengl.home.gl.record;

import android.util.Size;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.gl.record.audio.AudioEncoder;
import com.camera_opengl.home.gl.record.video.VideoEncoder;

public class RecordManager {
    private static final String TAG = "RecordManager";

    private MuxerManager muxerManager = new MuxerManager();
    private VideoEncoder videoEncoder = new VideoEncoder(muxerManager);
    private AudioEncoder audioEncoder = new AudioEncoder(muxerManager);

    private Size reallySize;

    public RecordManager(RecordListener recordListener) {
        videoEncoder.setRecordListener(recordListener);
    }

    public int getStatus() {
        return videoEncoder.getStatus();
    }

    public void confirmReallySize(Size reallySize) {
        this.reallySize = reallySize;
        LogUtil.INSTANCE.log(TAG, "confirmCameraSize " + reallySize.getWidth() + "  " + reallySize.getHeight());
    }

    public void startRecord() {
        if (videoEncoder.getStatus() == VideoEncoder.STATUS_READY && reallySize != null) {
            boolean initSuccess = muxerManager.init();
            if (initSuccess) {
                videoEncoder.startRecord(reallySize);
            }
        }
    }

    public void stopRecord() {
        videoEncoder.stopRecord();
    }

    public void onPause() {
        stopRecord();
    }

    public void onDestroy() {
        videoEncoder.onDestroy();
    }
}
