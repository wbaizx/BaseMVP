package com.camera_opengl.home.gl.record;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;

import com.base.common.util.FileUtil;
import com.base.common.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

public class MuxerManager {
    private static final String TAG = "MuxerManager";

    private final String path = FileUtil.INSTANCE.getDiskFilePath("VIDEO") + File.separator;
    private String thisPath;

    private static final int STATUS_STOP = 0;
    private static final int STATUS_START = 1;
    private static final int STATUS_INIT = 2;
    private int status = STATUS_STOP;

    private ReentrantLock look = new ReentrantLock();
    private MediaMuxer muxer;

    private int audioTrackIndex = -1;
    private int videoTrackIndex = -1;

    public boolean init() {
        boolean initSuccess = false;
        look.lock();
        if (status == STATUS_STOP) {
            try {
                thisPath = path + System.currentTimeMillis() + ".mp4";
                muxer = new MediaMuxer(thisPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                status = STATUS_INIT;
                initSuccess = true;
                LogUtil.INSTANCE.log(TAG, "init");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                look.unlock();
            }
        } else if (status == STATUS_INIT) {
            initSuccess = true;
            look.unlock();
        } else {
            look.unlock();
        }
        return initSuccess;
    }

    public void addVideoTrack(MediaFormat videoFormat) {
        look.lock();
        if (status == STATUS_INIT) {
            LogUtil.INSTANCE.log(TAG, "addVideoTrack");
            videoTrackIndex = muxer.addTrack(videoFormat);
            if (audioTrackIndex != -1) {
                start();
            }
        }
        look.unlock();
    }

    public void addAudioTrack(MediaFormat audioFormat) {
        look.lock();
        if (status == STATUS_INIT) {
            audioTrackIndex = muxer.addTrack(audioFormat);
            if (videoTrackIndex != -1) {
                start();
            }
        }
        look.unlock();
    }

    private void start() {
        muxer.start();
        status = STATUS_START;
        LogUtil.INSTANCE.log(TAG, "start");
    }

    public void writeVideoSampleData(ByteBuffer buffer, MediaCodec.BufferInfo info) {
        look.lock();
        if (status == STATUS_START) {
            muxer.writeSampleData(videoTrackIndex, buffer, info);
            LogUtil.INSTANCE.log(TAG, "writeVideoSampleData");
        }
        look.unlock();
    }

    public void writeAudioSampleData(ByteBuffer buffer, MediaCodec.BufferInfo info) {
        look.lock();
        if (status == STATUS_START) {
            muxer.writeSampleData(audioTrackIndex, buffer, info);
            LogUtil.INSTANCE.log(TAG, "writeAudioSampleData");
        }
        look.unlock();
    }

    public void stop() {
        look.lock();
        if (status == STATUS_START) {
            try {
                muxer.stop();
                muxer.release();
                LogUtil.INSTANCE.log(TAG, "stop");
            } catch (Exception e) {
                if (!TextUtils.isEmpty(thisPath)) {
                    File file = new File(thisPath);
                    file.delete();
                }
            } finally {
                thisPath = null;
                status = STATUS_STOP;
                audioTrackIndex = -1;
                videoTrackIndex = -1;
                look.unlock();
            }
        } else {
            look.unlock();
        }
    }
}
