package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Extractor {
    private static final String TAG = "VideoExtractor";

    private MediaExtractor extractor;
    private MediaFormat format;

    /**
     * 当前解封帧的时间戳
     */
    private long currentTimestamp = 0;

    /**
     * 轨道总时长
     */
    private long mp4Duration = 0;

    public void init(String path) {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int count = extractor.getTrackCount();

        for (int i = 0; i < count; i++) {
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String MIME = mediaFormat.getString(MediaFormat.KEY_MIME);

            if (chooseMime(MIME)) {
                extractor.selectTrack(i);
                format = mediaFormat;

                mp4Duration = format.getLong(MediaFormat.KEY_DURATION);
            }
        }
    }

    protected abstract boolean chooseMime(String mime);

    /**
     * 取数据
     */
    public int readSampleData(ByteBuffer inputBuffer) {
        int size = extractor.readSampleData(inputBuffer, 0);
        nextFrame();
        return size;
    }

    /**
     * 定位下一帧
     */
    public void nextFrame() {
        extractor.advance();
        currentTimestamp = extractor.getSampleTime();
    }

    /**
     * 获取当前解封帧时间戳
     */
    public long getSampleTime() {
        return extractor.getSampleTime();
    }

    /**
     * 用于暂停后
     */
    public void goBack() {
        extractor.seekTo(currentTimestamp, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
    }

    public MediaFormat getFormat() {
        return format;
    }

    /**
     * 获取当前轨道总时长
     */
    public long getMp4Duration() {
        return mp4Duration;
    }
}
