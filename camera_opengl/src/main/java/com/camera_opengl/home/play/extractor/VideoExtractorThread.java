package com.camera_opengl.home.play.extractor;

import android.graphics.SurfaceTexture;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Size;
import android.view.Surface;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.PlayListener;
import com.camera_opengl.home.play.decod.Decoder;
import com.camera_opengl.home.play.decod.VideoDecoder;

public class VideoExtractorThread extends ExtractorThread {
    private static final String TAG = "VideoExtractorThread";

    private PlayListener playListener;
    private SurfaceTexture surfaceTexture;

    private int width = 0;
    private int height = 0;
    private long mp4Duration = 0;

    public VideoExtractorThread(String path) {
        super(path);
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    @Override
    protected boolean chooseMime(String mime) {
        return MimeType.H264.equals(mime) || MimeType.H265.equals(mime);
    }

    @Override
    protected Decoder initDecoder(MediaFormat format) {
        width = format.getInteger(MediaFormat.KEY_WIDTH);
        height = format.getInteger(MediaFormat.KEY_HEIGHT);
        mp4Duration = format.getLong(MediaFormat.KEY_DURATION);
        return new VideoDecoder(format, new Surface(surfaceTexture));
    }

    @Override
    protected void continuousDecode(Decoder decoder, MediaExtractor extractor,boolean isFirstPlay) throws InterruptedException {
        if (isFirstPlay) {
            LogUtil.INSTANCE.log(TAG, "isFirstPlay");
            surfaceTexture.setDefaultBufferSize(width, height);
            playListener.confirmPlaySize(new Size(width, height));
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
