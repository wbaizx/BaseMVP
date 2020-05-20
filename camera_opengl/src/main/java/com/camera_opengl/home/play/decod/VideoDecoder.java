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
    private int status = STATUS_RELEASE;

    private MediaCodec mMediaCodec;
    private SurfaceTexture surfaceTexture;
    private Surface surface;

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
                    surface = new Surface(surfaceTexture);

                    status = STATUS_READY;

                    LogUtil.INSTANCE.log(TAG, "VideoDecoder init X");
                }
            }
        });
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
            if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                LogUtil.INSTANCE.log(TAG, "codec config //sps,pps,csd...");
            }

            LogUtil.INSTANCE.log(TAG, "onOutputBufferAvailable " + info.presentationTimeUs);
            avSyncTime();

            mMediaCodec.releaseOutputBuffer(index, true);

            if (MediaCodec.BUFFER_FLAG_END_OF_STREAM == info.flags) {
                LogUtil.INSTANCE.log(TAG, "end stream");
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

    /**
     * 音视频同步控制方法
     */
    private void avSyncTime() {
        try {
            look.lock();
            condition.await(30, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            look.unlock();
        }
    }

    public void play() {
        videoDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status == STATUS_READY) {
                    int width = format.getInteger(MediaFormat.KEY_WIDTH);
                    int height = format.getInteger(MediaFormat.KEY_HEIGHT);
                    surfaceTexture.setDefaultBufferSize(width, height);
                    //此方法设计fbo纹理配置更新，每次surface销毁重建后（比如home退出）都必须调用此方法
                    playListener.confirmPlaySize(new Size(width, height));

                    mMediaCodec.setCallback(callback, videoDecoderHandler);
                    mMediaCodec.configure(format, surface, null, 0);
                    mMediaCodec.start();
                    status = STATUS_START;
                }
            }
        });
    }

    public void pause() {
        videoDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status == STATUS_START) {
                    mMediaCodec.stop();
                    videoExtractor.goBack();
                    status = STATUS_READY;
                }
            }
        });
    }

    public void release() {
        videoDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status != STATUS_RELEASE) {
                    mMediaCodec.release();
                    status = STATUS_RELEASE;
                }
            }
        });
        videoDecoderThread.quitSafely();
    }
}
