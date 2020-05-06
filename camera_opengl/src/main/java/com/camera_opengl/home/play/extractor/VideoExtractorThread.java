package com.camera_opengl.home.play.extractor;

import android.graphics.SurfaceTexture;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Size;
import android.view.Surface;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.PlayListener;
import com.camera_opengl.home.play.decod.VideoDecoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class VideoExtractorThread extends Thread {
    private static final String TAG = "VideoExtractorThread";

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_SNAP = 2;
    public static final int STATUS_RELEASE = 3;
    private int status = STATUS_READY;

    private boolean isConfirmReallySize = false;

    private String path;
    private MediaExtractor mVideoExtractor;
    private PlayListener playListener;
    private SurfaceTexture surfaceTexture;

    private VideoDecoder videoDecoder;

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    /**
     * 当前时间戳
     */
    private long currentTimestamp = 0;
    /**
     * 视频文件上一帧时间戳
     */
    private long previousFrameTimestamp = 0;

    public VideoExtractorThread(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    @Override
    public void run() {
        super.run();
        setName("VideoExtractorThread");

        try {
            mVideoExtractor = new MediaExtractor();
            mVideoExtractor.setDataSource(path);

            int count = mVideoExtractor.getTrackCount();
            MediaFormat videoFormat = null;

            int width = 0;
            int height = 0;

            for (int i = 0; i < count; i++) {
                MediaFormat mediaFormat = mVideoExtractor.getTrackFormat(i);
                String MIME = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (MimeType.H264.equals(MIME) || MimeType.H265.equals(MIME)) {
                    mVideoExtractor.selectTrack(i);
                    videoFormat = mediaFormat;

                    width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                    height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);

                    surfaceTexture.setDefaultBufferSize(width, height);

                    LogUtil.INSTANCE.log(TAG, "init Video " + mediaFormat.getLong(MediaFormat.KEY_DURATION) + " -- "
                            + mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) + " -- " + width + " -- " + height);
                }
            }

            if (videoFormat != null) {
                look.lock();
                while (true) {
                    if (STATUS_START == status) {
                        if (videoDecoder == null) {
                            videoDecoder = new VideoDecoder(videoFormat, new Surface(surfaceTexture));
                        }

                        if (isConfirmReallySize) {
                            isConfirmReallySize = false;
                            playListener.confirmPlaySize(new Size(width, height));
                        }

                        //开始播放，会多次调用，方法内判断
                        videoDecoder.play();

                        if (mVideoExtractor.getSampleTime() == 0) {
                            previousFrameTimestamp = 0;
                            decodeFrame();

                        } else if (mVideoExtractor.getSampleTime() == -1) {
                            LogUtil.INSTANCE.log(TAG, "end of stream");
                            mVideoExtractor.seekTo(0, MediaExtractor.SEEK_TO_NEXT_SYNC);

                        } else {
                            synchronisedTime();

                            decodeFrame();
                        }

                    } else if (STATUS_RELEASE == status) {
                        if (videoDecoder != null) {
                            videoDecoder.release();
                        }
                        break;

                    } else {
                        LogUtil.INSTANCE.log(TAG, "SNAP status " + status + " -- await");
                        if (STATUS_SNAP == status) {
                            //如果是暂停态，通知解码器和播放器暂停，变为准备态
                            if (videoDecoder != null) {
                                videoDecoder.pause();
                            }
                            status = STATUS_READY;
                        }
                        condition.await();
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            look.unlock();
        }

        LogUtil.INSTANCE.log(TAG, "VideoExtractorThread  close");
    }

    /**
     * 这个方法用来控制播放间隔，以及音视频同步
     */
    private void synchronisedTime() throws InterruptedException {
        long timeDifference = getSampleTime() - currentTimestamp;
        long fileTimeDifference = mVideoExtractor.getSampleTime() - previousFrameTimestamp;
        if (timeDifference < fileTimeDifference) {
            LogUtil.INSTANCE.log(TAG, "await " + (fileTimeDifference - timeDifference));
            condition.await(fileTimeDifference - timeDifference, TimeUnit.MICROSECONDS);
        }

        previousFrameTimestamp = mVideoExtractor.getSampleTime();
    }

    /**
     * 解码播放一帧，同时定位下一帧，记录当前时间戳
     */
    private void decodeFrame() {
        currentTimestamp = getSampleTime();
        videoDecoder.encoder(mVideoExtractor);
        mVideoExtractor.advance();
    }

    public void play() {
        look.lock();
        if (status == STATUS_READY) {
            status = STATUS_START;
        }
        isConfirmReallySize = true;
        condition.signal();
        look.unlock();
    }

    public void pause() {
        look.lock();
        status = STATUS_SNAP;
        condition.signal();
        look.unlock();
    }

    public void release() {
        look.lock();
        status = STATUS_RELEASE;
        condition.signal();
        look.unlock();
    }

    //标注时间戳用的
    private long getSampleTime() {
        return System.nanoTime() / 1000;
    }
}
