package com.camera_opengl.home.play;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.play.extractor.AudioExtractorThread;
import com.camera_opengl.home.play.extractor.VideoExtractorThread;

public class PlayManager {
    private static final String TAG = "PlayManager";

    private VideoExtractorThread videoThread;
    private AudioExtractorThread audioThread;

    public void init(String path) {
        videoThread = new VideoExtractorThread(path);
        videoThread.start();

        audioThread = new AudioExtractorThread(path);
        audioThread.start();

        LogUtil.INSTANCE.log(TAG, "init X");
    }

    public void play() {
        audioThread.play();
    }

    public void onPause() {
        pause();
    }

    public void pause() {
        audioThread.pause();
    }

    public void onDestroy() {
        audioThread.release();
    }
}
