package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Size;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.PlayListener;

import java.io.IOException;

public class VideoExtractorThread extends Thread {
    private static final String TAG = "VideoExtractorThread";

    private String path;
    private MediaExtractor mVideoExtractor;
    private PlayListener playListener;

    public VideoExtractorThread(String path) {
        this.path = path;
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    @Override
    public void run() {
        super.run();
        setName("VideoExtractorThread");

        try {
            mVideoExtractor = new MediaExtractor();
            mVideoExtractor.setDataSource(path);

            int count = mVideoExtractor.getTrackCount();
            MediaFormat videoFormat = null;
            for (int i = 0; i < count; i++) {
                MediaFormat mediaFormat = mVideoExtractor.getTrackFormat(i);
                String MIME = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (MimeType.H264.equals(MIME) || MimeType.H265.equals(MIME)) {
                    mVideoExtractor.selectTrack(i);
                    videoFormat = mediaFormat;
                    playListener.confirmPlaySize(new Size(mediaFormat.getInteger(MediaFormat.KEY_WIDTH),
                            mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)));
                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getLong(MediaFormat.KEY_DURATION));//总时间
                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE));//最大大小
                }
            }

            if (videoFormat != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogUtil.INSTANCE.log(TAG, "VideoExtractorThread  close");
    }

    public void play() {
    }

    public void pause() {
    }

    public void release() {
    }
}
