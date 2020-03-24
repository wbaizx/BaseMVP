package com.camera_opengl.home;

import com.base.common.util.ImageUtil;
import com.base.common.util.LogUtil;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SavePictureThread extends Thread {
    private static final String TAG = "SavePictureThread";

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    private ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(5);

    private SaveListener listener;

    interface SaveListener {
        void saveSuccess();
    }

    public void setSaveListener(SaveListener listener) {
        this.listener = listener;
    }

    public void signal() {
        look.lock();
        condition.signal();
        look.unlock();
    }

    public void putData(byte[] data) {
        queue.offer(data);
    }

    @Override
    public void run() {
        super.run();

        while (!Thread.currentThread().isInterrupted()) {
            LogUtil.INSTANCE.log(TAG, "run save");
            try {
                look.lock();
                if (!queue.isEmpty()) {
                    LogUtil.INSTANCE.log(TAG, "run poll");
                    byte[] poll = queue.poll();

                    if (poll != null) {
                        File file = ImageUtil.INSTANCE.savePicture(poll, "IMG" + System.currentTimeMillis() + ".jpg");
                        if (ImageUtil.INSTANCE.updateGallery(file)) {
                            LogUtil.INSTANCE.log(TAG, "run saveSuccess");
                            listener.saveSuccess();
                        }
                    }
                } else {
                    LogUtil.INSTANCE.log(TAG, "run await");
                    condition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            } finally {
                look.unlock();
            }
        }

        LogUtil.INSTANCE.log(TAG, "close");
    }
}
