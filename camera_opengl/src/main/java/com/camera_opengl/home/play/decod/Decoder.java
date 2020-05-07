package com.camera_opengl.home.play.decod;

import android.media.MediaExtractor;

public interface Decoder {
    void encoder(MediaExtractor extractor);

    void play();

    void pause();

    void release();
}
