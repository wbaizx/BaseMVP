package com.camera_opengl.home.gl.record.video;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.gl.record.MuxerManager;
import com.camera_opengl.home.gl.record.RecordListener;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoEncoder {
    private static final String TAG = "VideoEncoder";

    public static final String H264 = MediaFormat.MIMETYPE_VIDEO_AVC;
    public static final String H265 = MediaFormat.MIMETYPE_VIDEO_AVC;

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_SNAP = 2;

    private int status = STATUS_READY;

    private RecordListener recordListener;
    private MuxerManager muxerManager;

    private MediaCodec mMediaCodec;

    private MediaFormat mMediaFormat;

    private HandlerThread videoEncoderThread;
    private Handler videoEncoderHandler;

    private Surface surface;

    private MediaCodec.Callback callback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            LogUtil.INSTANCE.log(TAG, "onInputBufferAvailable");
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(index);
            LogUtil.INSTANCE.log(TAG, info.flags + "--" + info.size + "--" + info.presentationTimeUs+ "--" + System.currentTimeMillis());

            if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                LogUtil.INSTANCE.log(TAG, "codec config");
            } else if (MediaCodec.BUFFER_FLAG_KEY_FRAME == info.flags) {
                LogUtil.INSTANCE.log(TAG, "key frame");
            }

            muxerManager.writeVideoSampleData(outputBuffer, info);

            mMediaCodec.releaseOutputBuffer(index, true);

            if (MediaCodec.BUFFER_FLAG_END_OF_STREAM == info.flags) {
                LogUtil.INSTANCE.log(TAG, "end stream");
                release();
            }
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            LogUtil.INSTANCE.log(TAG, "onOutputFormatChanged");
            muxerManager.addVideoTrack(format);
        }
    };

    public VideoEncoder(MuxerManager muxerManager) {
        videoEncoderThread = new HandlerThread("videoEncoderBackground");
        videoEncoderThread.start();
        videoEncoderHandler = new Handler(videoEncoderThread.getLooper());
        this.muxerManager = muxerManager;
    }

    public int getStatus() {
        return status;
    }

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void startRecord(Size reallySize) {
        videoEncoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status == STATUS_READY) {
                    status = STATUS_START;
                    LogUtil.INSTANCE.log(TAG, "startRecord");

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
        });
    }

    public void stopRecord() {
        LogUtil.INSTANCE.log(TAG, "stopRecord  status " + status);
        videoEncoderHandler.post(new Runnable() {
            @Override
            public void run() {
                if (status == STATUS_START) {
                    LogUtil.INSTANCE.log(TAG, "stopRecord");
                    status = STATUS_SNAP;

                    mMediaCodec.signalEndOfInputStream();

                    recordListener.onEncoderSurfaceDestroy();
                }
            }
        });
    }

    private void release() {
        muxerManager.stop();
        mMediaCodec.stop();
        surface.release();
        mMediaCodec.release();
        status = STATUS_READY;
        LogUtil.INSTANCE.log(TAG, "release");
    }

    public void onDestroy() {
        //这里不能移除所有动作，否则可能导致资源无法释放
        videoEncoderThread.quitSafely();
        videoEncoderHandler = null;
        videoEncoderThread = null;
    }
}
