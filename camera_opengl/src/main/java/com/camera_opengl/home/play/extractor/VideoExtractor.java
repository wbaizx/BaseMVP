package com.camera_opengl.home.play.extractor;

import com.camera_opengl.home.MimeType;

public class VideoExtractor extends Extractor {
    @Override
    protected boolean chooseMime(String mime) {
        return MimeType.H264.equals(mime) || MimeType.H265.equals(mime);
    }
}
