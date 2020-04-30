package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.play.decod.AudioDecoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AudioExtractorThread extends Thread {
    private static final String TAG = "AudioExtractorThread";

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_SNAP = 2;
    public static final int STATUS_RELEASE = 3;
    private int status = STATUS_READY;

    private String path;

    private MediaExtractor mAudioExtractor;
    private AudioDecoder audioDecoder;

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

    public AudioExtractorThread(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void run() {
        super.run();
        setName("AudioExtractorThread");

        try {
            mAudioExtractor = new MediaExtractor();
            mAudioExtractor.setDataSource(path);

            int count = mAudioExtractor.getTrackCount();
            MediaFormat audioFormat = null;
            for (int i = 0; i < count; i++) {
                MediaFormat mediaFormat = mAudioExtractor.getTrackFormat(i);
                String MIME = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (MimeType.AAC.equals(MIME)) {
                    mAudioExtractor.selectTrack(i);
                    audioFormat = mediaFormat;

                    LogUtil.INSTANCE.log(TAG, "init Audio " + mediaFormat.getLong(MediaFormat.KEY_DURATION));//总时间
                }
            }

            if (audioFormat != null) {
                look.lock();
                while (true) {
                    if (STATUS_START == status) {
                        if (audioDecoder == null) {
                            audioDecoder = new AudioDecoder(audioFormat);
                        }

                        //开始播放，会多次调用，方法内判断
                        audioDecoder.play();

                        if (mAudioExtractor.getSampleTime() == 0) {
                            previousFrameTimestamp = 0;
                            decodeFrame();

                        } else if (mAudioExtractor.getSampleTime() == -1) {
                            LogUtil.INSTANCE.log(TAG, "end of stream");
                            mAudioExtractor.seekTo(0, MediaExtractor.SEEK_TO_NEXT_SYNC);

                        } else {
                            synchronisedTime();

                            decodeFrame();
                        }

                    } else if (STATUS_RELEASE == status) {
                        if (audioDecoder != null) {
                            audioDecoder.release();
                        }
                        break;

                    } else {
                        LogUtil.INSTANCE.log(TAG, "SNAP status " + status + " -- await");
                        if (STATUS_SNAP == status) {
                            //如果是暂停态，通知解码器和播放器暂停，变为准备态
                            if (audioDecoder != null) {
                                audioDecoder.pause();
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

        LogUtil.INSTANCE.log(TAG, "AudioExtractorThread  close");
    }

    /**
     * 这个方法用来控制播放间隔，以及音视频同步
     */
    private void synchronisedTime() throws InterruptedException {
        long timeDifference = getSampleTime() - currentTimestamp;
        long fileTimeDifference = mAudioExtractor.getSampleTime() - previousFrameTimestamp;
        if (timeDifference < fileTimeDifference) {
            LogUtil.INSTANCE.log(TAG, "await " + (fileTimeDifference - timeDifference));
            condition.await(fileTimeDifference - timeDifference, TimeUnit.MICROSECONDS);
        }

        previousFrameTimestamp = mAudioExtractor.getSampleTime();
    }

    /**
     * 解码播放一帧，同时定位下一帧，记录当前时间戳
     */
    private void decodeFrame() {
        currentTimestamp = getSampleTime();
        audioDecoder.encoder(mAudioExtractor);
        mAudioExtractor.advance();
    }

    public void play() {
        look.lock();
        if (status == STATUS_READY) {
            status = STATUS_START;
        }
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
