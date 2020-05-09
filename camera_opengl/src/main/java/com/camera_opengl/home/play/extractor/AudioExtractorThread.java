package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.decod.AudioDecoder;
import com.camera_opengl.home.play.decod.Decoder;

public class AudioExtractorThread extends ExtractorThread {
    private static final String TAG = "AudioExtractorThread";

    private long videoSampleTime = 0;

    public AudioExtractorThread(String path) {
        super(path);
    }

    @Override
    protected boolean chooseMime(String mime) {
        return MimeType.AAC.equals(mime);
    }

    @Override
    protected Decoder initDecoder(MediaFormat format) {
        return new AudioDecoder(format);
    }

    @Override
    protected void continuousDecode(Decoder decoder, MediaExtractor extractor, boolean isFirstPlay) throws InterruptedException {
        if (isFirstPlay) {
            LogUtil.INSTANCE.log(TAG, "isFirstPlay");
            decoder.play();
        }
        decodeOnTime(videoSampleTime);
        decodeFrame();
    }

    @Override
    protected void decodeComplete() {
        decodeCompletePause();
    }

    public void syncTime(long time) {
        LogUtil.INSTANCE.log(TAG, "syncTime " + time);
        videoSampleTime = time;
    }

    /**
     * 同步方播放完毕回调
     */
    public void endSyncTime() {
        videoSampleTime = 0;
        //注意保证如果音频先播放完毕后进入阻塞态，也能被唤醒
        restartPlayHasLock();
    }
}
