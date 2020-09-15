package com.camera_opengl.home.record.encoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;

import com.base.common.util.LogUtilKt;
import com.camera_opengl.home.MimeType;
import com.camera_opengl.home.record.MuxerManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AudioEncoder extends Thread {
    private static final String TAG = "AudioEncoder";

    public static final int STATUS_READY = 0;
    public static final int STATUS_START = 1;

    private int status = STATUS_READY;

    private MuxerManager muxerManager;
    private AudioRecord audioRecord;
    private MediaCodec mMediaCodec;

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    private int bufferSizeInBytes = 0;
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 44100;
    //音频码率
    private final static int BIT_RATE = 128000;

    public AudioEncoder(MuxerManager muxerManager) {
        this.muxerManager = muxerManager;
    }

    public int getStatus() {
        return status;
    }

    public void startRecord() {
        startBackground();

        if (status == STATUS_READY) {
            look.lock();
            status = STATUS_START;
            condition.signal();
            look.unlock();
        }
    }

    /**
     * 启动音频编码工作线程
     */
    private void startBackground() {
        if (!isAlive() && !isInterrupted()) {
            start();
            LogUtilKt.log(TAG, "threadState isAlive " + isAlive());
            LogUtilKt.log(TAG, "threadState isInterrupted " + isInterrupted());
            LogUtilKt.log(TAG, "threadState state " + getState().name());
        }
    }

    public void stopRecord() {
        status = STATUS_READY;
    }

    public void onDestroy() {
        interrupt();
    }

    @Override
    public void run() {
        super.run();
        setName("AudioEncoderBackground");

        //音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
                AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        LogUtilKt.log(TAG, "bufferSizeInBytes " + bufferSizeInBytes);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes);

        look.lock();
        try {
            byte[] bytes = new byte[bufferSizeInBytes];
            int recordLength = 0;

            while (!Thread.currentThread().isInterrupted()) {
                if (status == STATUS_READY && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop();
                    mMediaCodec.stop();
                    mMediaCodec.release();
                    LogUtilKt.log(TAG, "audioRecord stop");
                }

                if (status == STATUS_START && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
                    createCodec();
                    audioRecord.startRecording();
                    LogUtilKt.log(TAG, "audioRecord startRecording");
                }

                if (status == STATUS_START && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    recordLength = audioRecord.read(bytes, 0, bufferSizeInBytes);

                    LogUtilKt.log(TAG, "recordLength " + recordLength);
                    if (AudioRecord.ERROR_DEAD_OBJECT != recordLength
                            && AudioRecord.ERROR_INVALID_OPERATION != recordLength
                            && AudioRecord.ERROR_BAD_VALUE != recordLength
                            && AudioRecord.ERROR != recordLength
                            && 0 != recordLength
                    ) {
                        encoder(bytes, recordLength);
                    }
                    continue;
                }

                LogUtilKt.log(TAG, "await");
                condition.await();
            }
        } catch (InterruptedException e) {
            LogUtilKt.log(TAG, "InterruptedException");
        } finally {
            look.unlock();
            audioRecord.release();
        }

        LogUtilKt.log(TAG, "AudioEncoder  close");
    }

    private void createCodec() {
        MediaFormat mediaFormat = MediaFormat.createAudioFormat(MimeType.AAC, AUDIO_SAMPLE_RATE, 2);

        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        String name = mediaCodecList.findEncoderForFormat(mediaFormat);
        LogUtilKt.log(TAG, "createCodec " + name);
        try {
            mMediaCodec = MediaCodec.createByCodecName(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSizeInBytes);

        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
    }

    private void encoder(byte[] bytes, int recordLength) {
        //-1表示一直等待，0表示不等待有可能会丢帧，其他表示等待多少毫秒
        int inputBufferId = mMediaCodec.dequeueInputBuffer(0);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferId);
            if (inputBuffer != null) {
                inputBuffer.clear();
                inputBuffer.put(bytes, 0, recordLength);//添加数据
                inputBuffer.limit(recordLength);
            }
            mMediaCodec.queueInputBuffer(inputBufferId, 0, recordLength, getSampleTime(), 0);
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        while (true) {
            int outputBufferId = mMediaCodec.dequeueOutputBuffer(info, 0);
            if (outputBufferId >= 0) {
                ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferId);

                if (outputBuffer != null) {
                    if (MediaCodec.BUFFER_FLAG_CODEC_CONFIG == info.flags) {
                        LogUtilKt.log(TAG, "codec config //sps,pps,csd...");
                    } else {
                        byte[] adtsBytes = new byte[info.size + 7];
                        addADTStoPacket(adtsBytes, adtsBytes.length);
                        outputBuffer.get(adtsBytes, 7, info.size);

                        outputBuffer.position(info.offset);
                        muxerManager.writeAudioSampleData(outputBuffer, info);
                    }
                }

                mMediaCodec.releaseOutputBuffer(outputBufferId, false);

            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                LogUtilKt.log(TAG, "onOutputFormatChanged");
                muxerManager.addAudioTrack(mMediaCodec.getOutputFormat());
            } else {
                break;
            }
        }
    }

    /**
     * MP4,flv,rtmp都不需要adts头
     * hls,rtp,ts和单独aac文件需要adts头
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //音频声道数为两个
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    //标注时间戳用的
    private long getSampleTime() {
        return System.nanoTime() / 1000;
    }
}
