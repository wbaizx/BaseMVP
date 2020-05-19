package com.camera_opengl.home.play;

import android.graphics.SurfaceTexture;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.play.extractor.AudioExtractorThread;
import com.camera_opengl.home.play.extractor.AvSyncManager;
import com.camera_opengl.home.play.extractor.ExtractorThread;
import com.camera_opengl.home.play.extractor.VideoExtractorThread;

public class PlayManager {
    private static final String TAG = "PlayManager";

    private VideoExtractorThread videoThread;
    private AudioExtractorThread audioThread;
    private AvSyncManager avSyncManager = new AvSyncManager();

    private SurfaceTexture surfaceTexture;
    private PlayListener playListener;

    public PlayManager(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void init(String path) {
        videoThread = new VideoExtractorThread(path, avSyncManager);
        videoThread.setPlayListener(playListener);
        videoThread.setSurfaceTexture(surfaceTexture);
        videoThread.start();

        audioThread = new AudioExtractorThread(path, avSyncManager);
        audioThread.start();

        LogUtil.INSTANCE.log(TAG, "init X");
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    public void play() {
        if (isReady()) {
            videoThread.play();
            audioThread.play();
        }
    }

    public boolean isReady() {
        return videoThread.getStatus() == ExtractorThread.STATUS_READY && audioThread.getStatus() == ExtractorThread.STATUS_READY;
    }

    public void onPause() {
        pause();
    }

    public void pause() {
        videoThread.pause();
        audioThread.pause();
    }

    public void onDestroy() {
        audioThread.release();
        videoThread.release();
    }
}
