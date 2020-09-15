package com.camera_opengl.home.play.decod;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.play.PlayListener;
import com.camera_opengl.home.play.extractor.AudioExtractor;
import com.camera_opengl.home.play.extractor.Extractor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AudioDecoder {
    private static final String TAG = "AudioDecoder";

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_STOP = 2;
    public static final int STATUS_RELEASE = 3;
    private int status = -1;

    private MediaCodec mMediaCodec;

    private HandlerThread audioDecoderThread;
    private Handler audioDecoderHandler;

    private MediaFormat format;
    private PlayListener playListener;
    private Extractor audioExtractor = new AudioExtractor();
    private AudioTrack audioTrack;

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

    public void init(String path) {
        audioDecoderThread = new HandlerThread("AudioDecoderBackground");
        audioDecoderThread.start();
        audioDecoderHandler = new Handler(audioDecoderThread.getLooper());

        audioDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                audioExtractor.init(path);
                format = audioExtractor.getFormat();

                if (format != null) {
                    int sampleHz = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) == 1 ?
                            AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

                    int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleHz, channel, AudioFormat.ENCODING_PCM_16BIT);
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleHz, channel,
                            AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);

                    MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
                    String name = mediaCodecList.findDecoderForFormat(format);
                    LogUtilKt.log(TAG, "createCodec " + name);
                    try {
                        mMediaCodec = MediaCodec.createByCodecName(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    start();
                    status = STATUS_READY;

                    LogUtilKt.log(TAG, "AudioDecoder init X");
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
                size = audioExtractor.readSampleData(inputBuffer);
            }

            if (size == -1) {
                mMediaCodec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mMediaCodec.queueInputBuffer(index, 0, size, audioExtractor.getSampleTime(), 0);
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
                    audioTrack.write(mMediaCodec.getOutputBuffer(index), info.size, AudioTrack.WRITE_NON_BLOCKING);
                    mMediaCodec.releaseOutputBuffer(index, false);
                    if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        playEnd();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LogUtilKt.log(TAG, "finally");

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
        if (status != STATUS_START) {
            LogUtilKt.log(TAG, "checkPlayStatus await " + status);
            condition.await();
        }
    }

    /**
     * 时间戳控制，音视频同步
     * --控制方法
     */
    private void avSyncTime(long presentationTimeUs) throws InterruptedException {
        if (status != STATUS_RELEASE) {
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

    public void play() {
        look.lock();
        if (status == STATUS_READY) {
            condition.signal();
        } else if (status == STATUS_STOP) {
            audioExtractor.reset();
            start();
        }
        audioTrack.play();
        status = STATUS_START;
        isFirstFrame = true;

        look.unlock();
    }

    private void start() {
        mMediaCodec.setCallback(callback, audioDecoderHandler);
        mMediaCodec.configure(format, null, null, 0);
        mMediaCodec.start();
        LogUtilKt.log(TAG, "start");
    }

    private void playEnd() {
        mMediaCodec.flush();
        mMediaCodec.stop();
        audioTrack.pause();
        status = STATUS_STOP;

        playListener.playEnd();
        LogUtilKt.log(TAG, "stop");
    }

    public void pause() {
        look.lock();
        if (status == STATUS_START) {
            audioTrack.pause();

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

        audioTrack.stop();
        audioTrack.release();

        status = STATUS_RELEASE;

        LogUtilKt.log(TAG, "release");

        condition.signal();
        look.unlock();

        audioDecoderThread.quitSafely();
    }
}