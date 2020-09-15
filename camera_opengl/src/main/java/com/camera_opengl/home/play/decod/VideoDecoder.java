package com.camera_opengl.home.play.decod;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.play.PlayListener;
import com.camera_opengl.home.play.extractor.Extractor;
import com.camera_opengl.home.play.extractor.VideoExtractor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class VideoDecoder {
    private static final String TAG = "VideoDecoder";

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_STOP = 2;
    public static final int STATUS_RELEASE = 3;
    private int status = -1;

    private MediaCodec mMediaCodec;
    private SurfaceTexture surfaceTexture;
    private boolean needCoverPicture = false;

    private HandlerThread videoDecoderThread;
    private Handler videoDecoderHandler;

    private MediaFormat format;
    private PlayListener playListener;
    private Extractor videoExtractor = new VideoExtractor();

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    /**
     * 系统时间线
     */
    private long systemTime = 0;

    /**
     * 基准帧时间
     */
    private long frameTime = 0;

    /**
     * 每次播放开始标识，用于重置时间线各变量
     */
    private boolean isFirstFrame = false;

    public int getStatus() {
        return status;
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void init(String path, SurfaceTexture surfaceTexture) {
        videoDecoderThread = new HandlerThread("videoDecoderBackground");
        videoDecoderThread.start();
        videoDecoderHandler = new Handler(videoDecoderThread.getLooper());

        videoDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                videoExtractor.init(path);
                format = videoExtractor.getFormat();

                if (format != null) {
                    MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
                    String name = mediaCodecList.findDecoderForFormat(format);
                    LogUtilKt.log(TAG, "createCodec " + name);
                    try {
                        mMediaCodec = MediaCodec.createByCodecName(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    VideoDecoder.this.surfaceTexture = surfaceTexture;

                    start();
                    status = STATUS_READY;

                    LogUtilKt.log(TAG, "VideoDecoder init X");
                }
            }
        });
    }

    private MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            LogUtilKt.log(TAG, "onInputBufferAvailable");
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
            int size = 0;
            if (inputBuffer != null) {
                inputBuffer.clear();
                size = videoExtractor.readSampleData(inputBuffer);
            }

            if (size == -1) {
                mMediaCodec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mMediaCodec.queueInputBuffer(index, 0, size, videoExtractor.getSampleTime(), 0);
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            try {
                look.lock();
                LogUtilKt.log(TAG, "onOutputBufferAvailable time " + info.presentationTimeUs);
                LogUtilKt.log(TAG, "onOutputBufferAvailable flags " + info.flags);

                checkPlayStatus();

                avSyncTime(info.presentationTimeUs);

                if (status != STATUS_RELEASE) {
                    mMediaCodec.releaseOutputBuffer(index, true);

                    if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        playEnd();
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LogUtilKt.log(TAG, "finally");

                needCoverPicture = false;

                look.unlock();
            }
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            LogUtilKt.log(TAG, "onOutputFormatChanged");
        }
    };

    private void checkPlayStatus() throws InterruptedException {
        if (status != STATUS_START && !needCoverPicture) {
            LogUtilKt.log(TAG, "checkPlayStatus await " + status);
            condition.await();
        }
    }

    /**
     * 时间戳控制，音视频同步
     * --控制方法
     */
    private void avSyncTime(long presentationTimeUs) throws InterruptedException {
        if (status != STATUS_RELEASE && !needCoverPicture) {
            if (isFirstFrame) {
                systemTime = System.nanoTime() / 1000;
                frameTime = presentationTimeUs;
                isFirstFrame = false;
            }

            long ft = presentationTimeUs - frameTime;//帧间隔
            long st = System.nanoTime() / 1000 - systemTime;//系统时间间隔

            LogUtilKt.log(TAG, "avSyncTime await time " + (ft - st) + " -- " + status);
            LogUtilKt.log(TAG, "avSyncTime await ft " + ft + " --st " + st);

            if (ft > st) {
                condition.await(ft - st, TimeUnit.MICROSECONDS);
            }
        }
    }

    /**
     * 这个方法主要用于确保屏幕显示一帧，避免黑屏，和play方法基本相似，但不能修改状态
     */
    public void onResume() {
        look.lock();

        LogUtilKt.log(TAG, "onResume");
        needCoverPicture = true;

        if (status == STATUS_READY) {
            renderingConfiguration();
            condition.signal();
        } else if (status == STATUS_STOP) {
            videoExtractor.reset();
            start();
            status = STATUS_READY;
        }
        look.unlock();
    }

    public void play() {
        look.lock();
        if (status == STATUS_READY) {
            renderingConfiguration();
            condition.signal();
        } else if (status == STATUS_STOP) {
            videoExtractor.reset();
            start();
        }
        status = STATUS_START;

        isFirstFrame = true;

        look.unlock();
    }

    private void start() {
        renderingConfiguration();
        mMediaCodec.setCallback(callback, videoDecoderHandler);
        mMediaCodec.configure(format, new Surface(surfaceTexture), null, 0);
        mMediaCodec.start();
        LogUtilKt.log(TAG, "start");
    }

    private void playEnd() {
        mMediaCodec.flush();
        mMediaCodec.stop();
        status = STATUS_STOP;

        LogUtilKt.log(TAG, "stop");
    }

    /**
     * 回调渲染配置，要确保在surface新建或每次销毁重建后调用
     */
    private void renderingConfiguration() {
        if (format != null) {
            int width = format.getInteger(MediaFormat.KEY_WIDTH);
            int height = format.getInteger(MediaFormat.KEY_HEIGHT);

            surfaceTexture.setDefaultBufferSize(width, height);
            //此方法涉及fbo纹理配置更新，每次surface销毁重建后（比如home退出）都必须调用此方法
            playListener.confirmPlaySize(new Size(width, height));
        }
    }

    public void pause() {
        look.lock();
        if (status == STATUS_START) {
            status = STATUS_READY;
            condition.signal();
        }
        look.unlock();
    }

    public void release() {
        look.lock();

        mMediaCodec.flush();
        mMediaCodec.stop();
        mMediaCodec.release();
        mMediaCodec = null;
        status = STATUS_RELEASE;

        LogUtilKt.log(TAG, "release");

        condition.signal();
        look.unlock();

        videoDecoderThread.quitSafely();
    }
}
