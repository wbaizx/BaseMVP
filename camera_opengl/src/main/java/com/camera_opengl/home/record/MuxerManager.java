package com.camera_opengl.home.record;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.base.common.util.AndroidUtil;
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

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_INIT = 2;
    public static final int STATUS_SNAP = 3;
    private int status = STATUS_READY;

    private MediaMuxer mediaMuxer;

    private ReentrantLock look = new ReentrantLock();

    private int audioTrackIndex = -1;
    private int videoTrackIndex = -1;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public int getStatus() {
        return status;
    }

    public boolean init() {
        boolean initSuccess = false;
        if (status == STATUS_READY) {
            try {
                thisPath = path + System.currentTimeMillis() + ".mp4";
                mediaMuxer = new MediaMuxer(thisPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                status = STATUS_INIT;
                initSuccess = true;
                LogUtil.INSTANCE.log(TAG, "init");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (status == STATUS_INIT) {
            initSuccess = true;
        }
        return initSuccess;
    }

    public void addVideoTrack(MediaFormat videoFormat) {
        look.lock();
        if (status == STATUS_INIT) {
            LogUtil.INSTANCE.log(TAG, "addVideoTrack");
            videoTrackIndex = mediaMuxer.addTrack(videoFormat);
            if (audioTrackIndex != -1) {
                start();
            }
        }
        look.unlock();
    }

    public void addAudioTrack(MediaFormat audioFormat) {
        look.lock();
        if (status == STATUS_INIT) {
            LogUtil.INSTANCE.log(TAG, "addAudioTrack");
            audioTrackIndex = mediaMuxer.addTrack(audioFormat);
            if (videoTrackIndex != -1) {
                start();
            }
        }
        look.unlock();
    }

    private void start() {
        mediaMuxer.start();
        status = STATUS_START;
        LogUtil.INSTANCE.log(TAG, "start");
    }

    public void writeVideoSampleData(ByteBuffer buffer, MediaCodec.BufferInfo info) {
        LogUtil.INSTANCE.log(TAG, "writeVideo");
        if (status == STATUS_START) {
            LogUtil.INSTANCE.log(TAG, "writeVideoSampleData " + info.presentationTimeUs);
            mediaMuxer.writeSampleData(videoTrackIndex, buffer, info);
        }
    }

    public void writeAudioSampleData(ByteBuffer buffer, MediaCodec.BufferInfo info) {
        LogUtil.INSTANCE.log(TAG, "writeAudio");
        if (status == STATUS_START) {
            LogUtil.INSTANCE.log(TAG, "writeAudioSampleData " + info.presentationTimeUs);
            mediaMuxer.writeSampleData(audioTrackIndex, buffer, info);
        }
    }

    public void stop() {
        if (status == STATUS_START) {
            status = STATUS_SNAP;
            try {
                mediaMuxer.stop();
                mediaMuxer.release();
                LogUtil.INSTANCE.log(TAG, "stop");

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtil.INSTANCE.showToast("录制成功 " + thisPath);
                    }
                });
            } catch (Exception e) {
                if (!TextUtils.isEmpty(thisPath)) {
                    File file = new File(thisPath);
                    file.delete();
                }
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtil.INSTANCE.showToast("录制失败");
                    }
                });
            } finally {
                status = STATUS_READY;
                audioTrackIndex = -1;
                videoTrackIndex = -1;
            }
        }
    }
}
