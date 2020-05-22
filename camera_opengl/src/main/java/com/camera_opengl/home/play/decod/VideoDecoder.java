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

import com.base.common.util.LogUtil;
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
    public static final int STATUS_RELEASE = 2;
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
                    LogUtil.INSTANCE.log(TAG, "createCodec " + name);
                    try {
                        mMediaCodec = MediaCodec.createByCodecName(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    VideoDecoder.this.surfaceTexture = surfaceTexture;
                    renderingConfiguration();

                    mMediaCodec.setCallback(callback, videoDecoderHandler);
                    mMediaCodec.configure(format, new Surface(surfaceTexture), null, 0);
                    mMediaCodec.start();

                    status = STATUS_READY;

                    LogUtil.INSTANCE.log(TAG, "VideoDecoder init X");
                }
            }
        });
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

    private MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            LogUtil.INSTANCE.log(TAG, "onInputBufferAvailable");
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
            int size = 0;
            if (inputBuffer != null) {
                inputBuffer.clear();
                size = videoExtractor.readSampleData(inputBuffer);
            }

            if (size > 0) {
                mMediaCodec.queueInputBuffer(index, 0, size, videoExtractor.getSampleTime(), 0);
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            try {
                look.lock();
                LogUtil.INSTANCE.log(TAG, "onOutputBufferAvailable " + info.presentationTimeUs);

                avSyncTime();

                //releaseOutputBuffer 在 checkPlayStatus 之前调用，保证release之前释放所有Buffer
                //但这样会在播放开始之前先渲染一帧，正好用来做封面
                mMediaCodec.releaseOutputBuffer(index, true);

                checkPlayStatus();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                look.unlock();
            }
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            LogUtil.INSTANCE.log(TAG, "onOutputFormatChanged");
        }
    };

    private void checkPlayStatus() throws InterruptedException {
        if (status != STATUS_START && status != STATUS_RELEASE) {
            LogUtil.INSTANCE.log(TAG, "await " + status);
            condition.await();
        }
    }

    /**
     * 音视频同步控制方法
     */
    private void avSyncTime() throws InterruptedException {
        condition.await(30, TimeUnit.MILLISECONDS);
    }

    public void play() {
        look.lock();
        if (status == STATUS_READY) {
            status = STATUS_START;
            renderingConfiguration();
            condition.signal();
        }
        look.unlock();
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
        if (status != STATUS_RELEASE) {
            status = STATUS_RELEASE;
            mMediaCodec.flush();
            mMediaCodec.stop();
            mMediaCodec.release();
            LogUtil.INSTANCE.log(TAG, "release");
            condition.signal();
        }
        look.unlock();
        videoDecoderThread.quitSafely();
    }
}
