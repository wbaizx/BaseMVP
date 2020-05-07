package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.decod.AudioDecoder;
import com.camera_opengl.home.play.decod.Decoder;

public class AudioExtractorThread extends ExtractorThread {
    private static final String TAG = "AudioExtractorThread";

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

        if (extractor.getSampleTime() == 0) {
            previousFrameTimestamp = 0;
            decodeFrame();

        } else if (extractor.getSampleTime() == -1) {
            LogUtil.INSTANCE.log(TAG, "end of stream");
            extractor.seekTo(0, MediaExtractor.SEEK_TO_NEXT_SYNC);

        } else {
            synchronisedTime();
            decodeFrame();
        }
    }
}
