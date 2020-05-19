package com.camera_opengl.home.play.extractor;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.base.common.util.LogUtil;
import com.camera_opengl.home.play.decod.Decoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ExtractorThread extends Thread {
    private static final String TAG = "ExtractorThread";

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;
    public static final int STATUS_SNAP = 2;
    public static final int STATUS_RELEASE = 3;
    private int status = STATUS_READY;

    private String path;

    private MediaExtractor extractor;
    private Decoder decoder;

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    private boolean isFirstPlay = false;

    private AvSyncManager avSyncManager;

    /**
     * 当前时间戳
     */
    private long currentTimestamp = 0;
    /**
     * 音频文件上一帧时间戳
     */
    protected long previousFrameTimestamp = 0;

    /**
     * 轨道总时长
     */
    private long mp4Duration = 0;

    public ExtractorThread(String path, AvSyncManager avSyncManager) {
        this.path = path;
        this.avSyncManager = avSyncManager;
    }

    public int getStatus() {
        return status;
    }

    public long getMp4Duration() {
        return mp4Duration;
    }

    @Override
    public void run() {
        super.run();
        setName(getClass().getSimpleName());

        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(path);

            int count = extractor.getTrackCount();
            MediaFormat format = null;

            for (int i = 0; i < count; i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String MIME = mediaFormat.getString(MediaFormat.KEY_MIME);

                if (chooseMime(MIME)) {
                    extractor.selectTrack(i);
                    format = mediaFormat;

                    mp4Duration = format.getLong(MediaFormat.KEY_DURATION);
                    LogUtil.INSTANCE.log(TAG, "duration " + mp4Duration);
                }
            }

            if (format != null) {
                look.lock();

                while (true) {
                    if (STATUS_START == status) {
                        if (decoder == null) {
                            decoder = initDecoder(format);
                        }

                        if (extractor.getSampleTime() == -1) {
                            LogUtil.INSTANCE.log(TAG, "end of stream");
                            decodeComplete();
                        } else {
                            continuousDecode(decoder, extractor, isFirstPlay);
                        }

                        isFirstPlay = false;
                    } else if (STATUS_RELEASE == status) {
                        if (decoder != null) {
                            decoder.release();
                        }

                        break;

                    } else {
                        LogUtil.INSTANCE.log(TAG, "SNAP status " + status + " -- await");
                        if (STATUS_SNAP == status) {
                            //如果是暂停态，通知解码器和播放器暂停，变为准备态
                            if (decoder != null) {
                                decoder.pause();
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

        LogUtil.INSTANCE.log(TAG, "close");
    }

    protected abstract boolean chooseMime(String mime);

    //已经加锁
    protected abstract Decoder initDecoder(MediaFormat format);

    //已经加锁
    protected abstract void continuousDecode(Decoder decoder, MediaExtractor extractor, boolean isFirstPlay) throws InterruptedException;

    //已经加锁
    protected abstract void decodeComplete();

    /**
     * 控制播放间隔
     * 解码播放一帧，同时定位下一帧
     * 记录当前时间戳
     */
    protected void decodeFrame() throws InterruptedException {
        long timeDifference = getSampleTime() - currentTimestamp;
        long fileTimeDifference = extractor.getSampleTime() - previousFrameTimestamp;

        LogUtil.INSTANCE.log(TAG, "decodeFrame await " + previousFrameTimestamp + " -- "
                + timeDifference + " -- " + fileTimeDifference + " -- "
                + (fileTimeDifference - timeDifference));

        if (timeDifference < fileTimeDifference) {
            condition.await(fileTimeDifference - timeDifference, TimeUnit.MICROSECONDS);
            LogUtil.INSTANCE.log(TAG, "decodeFrame await  ------------------");
        }

        previousFrameTimestamp = extractor.getSampleTime();
        currentTimestamp = getSampleTime();

        avSyncTime(avSyncManager, previousFrameTimestamp);

        LogUtil.INSTANCE.log(TAG, "decodeFrame - " + previousFrameTimestamp);
        decoder.encoder(extractor);
        extractor.advance();
    }

    /**
     * 播放完毕重新开始
     */
    protected void restartPlay() {
        LogUtil.INSTANCE.log(TAG, "restartPlay");
        extractor.seekTo(0, MediaExtractor.SEEK_TO_NEXT_SYNC);
        currentTimestamp = 0;
        previousFrameTimestamp = 0;
        if (status == STATUS_READY) {
            status = STATUS_START;
            isFirstPlay = true;
        }
    }

    /**
     * 给AvSyncManager同步控制器传递时间戳
     *
     * @param avSyncManager
     * @param previousFrameTimestamp
     */
    protected abstract void avSyncTime(AvSyncManager avSyncManager, long previousFrameTimestamp);

    /**
     * 播放完毕重新开始，带锁
     */
    protected void restartPlayHasLock() {
        look.lock();
        restartPlay();
        condition.signal();
        look.unlock();
    }

    /**
     * 用于被同步方轨道播放完毕后，进入阻塞态
     */
    protected void decodeCompletePause() {
        LogUtil.INSTANCE.log(TAG, "decodeCompletePause await");
        status = STATUS_SNAP;
    }

    public void play() {
        look.lock();
        if (status == STATUS_READY) {
            status = STATUS_START;
            isFirstPlay = true;
        }
        condition.signal();
        look.unlock();
        LogUtil.INSTANCE.log(TAG, "play X");
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
