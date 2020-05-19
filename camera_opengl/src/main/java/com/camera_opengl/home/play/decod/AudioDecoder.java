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

import com.base.common.util.LogUtil;
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
    public static final int STATUS_RELEASE = 2;
    private int status = STATUS_RELEASE;

    private MediaCodec mMediaCodec;

    private HandlerThread audioDecoderThread;
    private Handler audioDecoderHandler;

    private MediaFormat format;
    private Extractor audioExtractor = new AudioExtractor();
    private AudioTrack audioTrack;

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    private byte[] bytes;

    public int getStatus() {
        return status;
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
                    bytes = new byte[bufferSizeInBytes];
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleHz, channel,
                            AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);

                    MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
                    String name = mediaCodecList.findDecoderForFormat(format);
                    LogUtil.INSTANCE.log(TAG, "createCodec " + name);
                    try {
                        mMediaCodec = MediaCodec.createByCodecName(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    status = STATUS_READY;

                    LogUtil.INSTANCE.log(TAG, "AudioDecoder init X");
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
                size = audioExtractor.readSampleData(inputBuffer, 0);
            }

            if (size > 0) {
                mMediaCodec.queueInputBuffer(index, 0, size, audioExtractor.getSampleTime(), 0);
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                LogUtil.INSTANCE.log(TAG, "codec config //sps,pps,csd...");
            }

            LogUtil.INSTANCE.log(TAG, "onOutputBufferAvailable " + info.presentationTimeUs);
            avSyncTime();

            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(index);
            outputBuffer.get(bytes, 0, info.size);
            audioTrack.write(bytes, 0, info.size);
            mMediaCodec.releaseOutputBuffer(index, false);

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
//        try {
//            look.lock();
//            condition.await(30, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            look.unlock();
//        }
    }

    public void play() {
        audioDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status == STATUS_READY) {
                    audioTrack.play();
                    mMediaCodec.setCallback(callback, audioDecoderHandler);
                    mMediaCodec.configure(format, null, null, 0);
                    mMediaCodec.start();
                    status = STATUS_START;
                }
            }
        });
    }

    public void pause() {
        audioDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status == STATUS_START) {
                    mMediaCodec.stop();
                    audioTrack.pause();
                    audioExtractor.goBack();
                    status = STATUS_READY;
                }
            }
        });
    }

    public void release() {
        audioDecoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status != STATUS_RELEASE) {
                    mMediaCodec.release();
                    audioTrack.stop();
                    audioTrack.release();
                    status = STATUS_RELEASE;
                }
            }
        });
        audioDecoderThread.quitSafely();
    }
}