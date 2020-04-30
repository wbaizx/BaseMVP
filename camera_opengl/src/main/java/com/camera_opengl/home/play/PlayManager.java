package com.camera_opengl.home.play;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.play.extractor.AudioExtractorThread;
import com.camera_opengl.home.play.extractor.VideoExtractorThread;

public class PlayManager {
    private static final String TAG = "PlayManager";

    private VideoExtractorThread videoThread;
    private AudioExtractorThread audioThread;

    private Surface surface;
    private PlayListener playListener;

    public PlayManager(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void init(String path) {
        videoThread = new VideoExtractorThread(path);
        videoThread.setPlayListener(playListener);
        videoThread.start();

        audioThread = new AudioExtractorThread(path);
        audioThread.start();

        LogUtil.INSTANCE.log(TAG, "init X");
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        surface = new Surface(surfaceTexture);
    }

    public void play() {
        videoThread.play();
        audioThread.play();
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
