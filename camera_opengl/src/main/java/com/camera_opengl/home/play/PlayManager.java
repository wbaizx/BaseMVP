package com.camera_opengl.home.play;

import android.graphics.SurfaceTexture;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.play.decod.AudioDecoder;
import com.camera_opengl.home.play.decod.VideoDecoder;

public class PlayManager {
    private static final String TAG = "PlayManager";

    private VideoDecoder videoDecoder = new VideoDecoder();
    private AudioDecoder audioDecoder = new AudioDecoder();

    private SurfaceTexture surfaceTexture;
    private PlayListener playListener;

    public PlayManager(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    public void init(String path) {
        videoDecoder.setPlayListener(playListener);
        videoDecoder.init(path, surfaceTexture);

        audioDecoder.init(path);

        LogUtil.INSTANCE.log(TAG, "init X");
    }

    public void play() {
        if (isReady()) {
            videoDecoder.play();
            audioDecoder.play();
        }
    }

    public boolean isReady() {
        return videoDecoder.getStatus() == VideoDecoder.STATUS_READY && audioDecoder.getStatus() == AudioDecoder.STATUS_READY;
    }

    public void onPause() {
        pause();
    }

    public void pause() {
        videoDecoder.pause();
        audioDecoder.pause();
    }

    public void onDestroy() {
        videoDecoder.release();
        audioDecoder.release();
    }
}
