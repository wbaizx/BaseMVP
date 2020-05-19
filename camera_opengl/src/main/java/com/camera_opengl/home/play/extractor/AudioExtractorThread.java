package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.decod.AudioDecoder;
import com.camera_opengl.home.play.decod.Decoder;

public class AudioExtractorThread extends ExtractorThread {
    private static final String TAG = "AudioExtractorThread";

    public AudioExtractorThread(String path, AvSyncManager avSyncManager) {
        super(path, avSyncManager);
    }

    @Override
    protected boolean chooseMime(String mime) {
        return MimeType.AAC.equals(mime);
    }

    @Override
    protected Decoder initDecoder(MediaFormat format) {
        return new AudioDecoder(format);
    }

    /**
     * 带look锁
     */
    @Override
    protected void continuousDecode(Decoder decoder, MediaExtractor extractor, boolean isFirstPlay) throws InterruptedException {
        if (isFirstPlay) {
            LogUtil.INSTANCE.log(TAG, "isFirstPlay");
            decoder.play();
        }
        decodeFrame();
    }

    /**
     * 带look锁
     */
    @Override
    protected void decodeComplete() {
        restartPlay();
//        decodeCompletePause();
    }

    @Override
    protected void avSyncTime(AvSyncManager avSyncManager, long previousFrameTimestamp) {
        avSyncManager.syncAudioTime(previousFrameTimestamp);
    }
}
