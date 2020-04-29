package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;

import java.io.IOException;

public class VideoExtractorThread extends Thread {
    private static final String TAG = "VideoExtractorThread";

    private String path;
    private MediaExtractor mVideoExtractor;

    public VideoExtractorThread(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        super.run();
        setName("VideoExtractorThread");

        try {
            mVideoExtractor = new MediaExtractor();
            mVideoExtractor.setDataSource(path);

            int count1 = mVideoExtractor.getTrackCount();
            for (int i = 0; i < count1; i++) {
                MediaFormat mediaFormat = mVideoExtractor.getTrackFormat(i);
                String MIME = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (MimeType.H264.equals(MIME) || MimeType.H265.equals(MIME)) {
                    mVideoExtractor.selectTrack(i);
                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getLong(MediaFormat.KEY_DURATION));//总时间
                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getInteger(MediaFormat.KEY_WIDTH));//视频宽
                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getInteger(MediaFormat.KEY_HEIGHT));//视频高
                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE));//最大大小
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogUtil.INSTANCE.log(TAG, "AudioExtractorThread  close");
    }
}
