package com.camera_opengl.home.play.decod;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioDecoder implements Decoder {
    private static final String TAG = "AudioDecoder";
    private MediaCodec mMediaCodec;
    private AudioTrack audioTrack;

    private byte[] bytes;
    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

    public AudioDecoder(MediaFormat audioFormat) {
        int sampleHz = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channel = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) == 1 ?
                AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

        int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleHz, channel, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleHz, channel,
                AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);

        LogUtil.INSTANCE.log(TAG, "audioFormat parameter " + sampleHz + " -- " + channel + " -- " + bufferSizeInBytes);
        bytes = new byte[bufferSizeInBytes];

        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String name = mediaCodecList.findDecoderForFormat(audioFormat);
        LogUtil.INSTANCE.log(TAG, "createCodec " + name);
        try {
            mMediaCodec = MediaCodec.createByCodecName(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaCodec.configure(audioFormat, null, null, 0);
        mMediaCodec.start();
        LogUtil.INSTANCE.log(TAG, "AudioDecoder init X");
    }

    @Override
    public void encoder(MediaExtractor mAudioExtractor) {
        //-1表示一直等待，0表示不等待有可能会丢帧，其他表示等待多少毫秒
        int inputBufferId = mMediaCodec.dequeueInputBuffer(0);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferId);
            int size = 0;
            if (inputBuffer != null) {
                inputBuffer.clear();
                size = mAudioExtractor.readSampleData(inputBuffer, 0);
            }
            mMediaCodec.queueInputBuffer(inputBufferId, 0, size, mAudioExtractor.getSampleTime(), 0);
        }

        while (true) {
            int outputBufferId = mMediaCodec.dequeueOutputBuffer(info, 0);
            if (outputBufferId >= 0) {
                ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferId);

                if (outputBuffer != null) {
                    if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                        LogUtil.INSTANCE.log(TAG, "codec config //sps,pps,csd...");
                    } else {
                        LogUtil.INSTANCE.log(TAG, "audio Rendering " + info.presentationTimeUs + " -- size " + info.size);
                        outputBuffer.get(bytes, 0, info.size);
                        audioTrack.write(bytes, 0, info.size);
                    }
                }

                mMediaCodec.releaseOutputBuffer(outputBufferId, false);

            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                LogUtil.INSTANCE.log(TAG, "onOutputFormatChanged");
            } else {
                break;
            }
        }
    }

    @Override
    public void play() {
        if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.play();
            LogUtil.INSTANCE.log(TAG, "play");
        }
    }

    @Override
    public void pause() {
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            LogUtil.INSTANCE.log(TAG, "play pause");
        }
    }

    @Override
    public void release() {
        mMediaCodec.stop();
        mMediaCodec.release();
        audioTrack.stop();
        audioTrack.release();
        LogUtil.INSTANCE.log(TAG, "release X");
    }
}
