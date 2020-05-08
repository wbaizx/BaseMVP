package com.camera_opengl.home.play.decod;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import com.base.common.util.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecoder implements Decoder {
    private static final String TAG = "VideoDecoder";

    private MediaCodec mMediaCodec;

    public VideoDecoder(MediaFormat videoFormat, Surface surface) {
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String name = mediaCodecList.findDecoderForFormat(videoFormat);
        LogUtil.INSTANCE.log(TAG, "createCodec " + name);
        try {
            mMediaCodec = MediaCodec.createByCodecName(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaCodec.configure(videoFormat, surface, null, 0);
        mMediaCodec.start();
        LogUtil.INSTANCE.log(TAG, "VideoDecoder init X");
    }

    @Override
    public void encoder(MediaExtractor mVideoExtractor) {
        //-1表示一直等待，0表示不等待有可能会丢帧，其他表示等待多少毫秒
        int inputBufferId = mMediaCodec.dequeueInputBuffer(0);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferId);
            int size = 0;
            if (inputBuffer != null) {
                inputBuffer.clear();
                size = mVideoExtractor.readSampleData(inputBuffer, 0);
            }
            mMediaCodec.queueInputBuffer(inputBufferId, 0, size, mVideoExtractor.getSampleTime(), 0);
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        while (true) {
            int outputBufferId = mMediaCodec.dequeueOutputBuffer(info, 0);
            if (outputBufferId >= 0) {
                ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferId);

                if (outputBuffer != null) {
                    if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                        LogUtil.INSTANCE.log(TAG, "codec config //sps,pps,csd...");
                    }
                }

                mMediaCodec.releaseOutputBuffer(outputBufferId, true);

            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                LogUtil.INSTANCE.log(TAG, "onOutputFormatChanged");
            } else {
                break;
            }
        }
    }

    @Override
    public void play() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void release() {
        mMediaCodec.stop();
        mMediaCodec.release();
        LogUtil.INSTANCE.log(TAG, "release X");
    }
}
