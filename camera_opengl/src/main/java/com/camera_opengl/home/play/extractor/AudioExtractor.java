package com.camera_opengl.home.play.extractor;

import com.camera_opengl.home.MimeType;

public class AudioExtractor extends Extractor {
    @Override
    protected boolean chooseMime(String mime) {
        return MimeType.AAC.equals(mime);
    }
}
