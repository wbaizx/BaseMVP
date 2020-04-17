package com.camera_opengl.home.gl.record;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.base.common.util.LogUtil;

import java.io.IOException;

public class VideoEncoder {
    private static final String TAG = "VideoEncoder";

    public static final String H264 = MediaFormat.MIMETYPE_VIDEO_AVC;
    public static final String H265 = MediaFormat.MIMETYPE_VIDEO_AVC;

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_SNAP = 2;

    private int status = STATUS_READY;

    private RecordListener recordListener;

    private MediaCodec mMediaCodec;

    private MediaFormat mMediaFormat;
    private HandlerThread videoEncoderThread;

    private Handler videoEncoderHandler;
    private Size reallySize;

    private Surface surface;

    private MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
//            LogUtil.INSTANCE.log(TAG, "onInputBufferAvailable");
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
//            LogUtil.INSTANCE.log(TAG, "onOutputBufferAvailable");
            mMediaCodec.releaseOutputBuffer(index, true);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };

    public int getStatus() {
        return status;
    }

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void confirmReallySize(Size reallySize) {
        this.reallySize = reallySize;
        LogUtil.INSTANCE.log(TAG, "confirmCameraSize " + reallySize.getWidth() + "  " + reallySize.getHeight());
    }

    public void startRecord() {
        if (reallySize != null && status == STATUS_READY) {
            status = STATUS_START;
            LogUtil.INSTANCE.log(TAG, "startRecord");

            videoEncoderThread = new HandlerThread("videoEncoderBackground");
            videoEncoderThread.start();
            videoEncoderHandler = new Handler(videoEncoderThread.getLooper());

            try {
                mMediaCodec = MediaCodec.createEncoderByType(H264);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //宽高需要对调
            mMediaFormat = MediaFormat.createVideoFormat(H264, reallySize.getHeight(), reallySize.getWidth());
            mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, reallySize.getHeight() * reallySize.getWidth() * 5);
            mMediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            mMediaCodec.setCallback(callback, videoEncoderHandler);
            mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            surface = mMediaCodec.createInputSurface();
            recordListener.onEncoderSurfaceCreated(surface);
            mMediaCodec.start();
        }
    }

    public void stopRecord() {
        if (reallySize != null && status == STATUS_START) {
            LogUtil.INSTANCE.log(TAG, "stopRecord");
            status = STATUS_READY;

            mMediaCodec.signalEndOfInputStream();

            //录制render还要重写
//            mMediaCodec.stop();
//        surface.release();
//        mMediaCodec.release();

            recordListener.onEncoderSurfaceDestroy();

            videoEncoderThread.quitSafely();
        }
    }
}
