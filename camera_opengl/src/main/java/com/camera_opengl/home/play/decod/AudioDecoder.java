package com.camera_opengl.home.play.decod;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioDecoder {
    private static final String TAG = "AudioDecoder";
    private MediaCodec mMediaCodec;

    public AudioDecoder(MediaFormat audioFormat) {
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
    }

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

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        while (true) {
            int outputBufferId = mMediaCodec.dequeueOutputBuffer(info, 0);
            if (outputBufferId >= 0) {
                ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferId);

                if (outputBuffer != null) {
                    if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                        LogUtil.INSTANCE.log(TAG, "codec config //sps,pps,csd...");
                    } else {
                        outputBuffer.position(info.offset);
                        LogUtil.INSTANCE.log(TAG, "encoder " + info.size);
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

    public void release() {
        mMediaCodec.stop();
        mMediaCodec.release();
    }
}
