package com.camera_opengl.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

import com.base.common.util.AndroidUtil;
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

    private ArrayBlockingQueue<Picture> queue = new ArrayBlockingQueue<>(5);

    public void signal() {
        look.lock();
        condition.signal();
        look.unlock();
    }

    public void putData(byte[] data, boolean horizontalMirror, boolean verticalMirror) {
        LogUtil.INSTANCE.log(TAG, "putData bytes");
        Picture picture = new Picture(horizontalMirror, verticalMirror);
        picture.data = data;
        queue.offer(picture);
        signal();
    }

    public void putData(Bitmap btm, boolean horizontalMirror, boolean verticalMirror) {
        LogUtil.INSTANCE.log(TAG, "putData bitmap");
        Picture picture = new Picture(horizontalMirror, verticalMirror);
        picture.btm = btm;
        queue.offer(picture);
        signal();
    }

    @Override
    public void run() {
        super.run();

        Handler mCameraHandler = new Handler(Looper.getMainLooper());

        while (!Thread.currentThread().isInterrupted()) {
            try {
                look.lock();
                if (!queue.isEmpty()) {
                    Picture picture = queue.poll();
                    LogUtil.INSTANCE.log(TAG, "run save");

                    if (picture != null) {
                        File file;
                        Bitmap btm = null;
                        Bitmap saveBmp = null;

                        if (picture.data != null) {
                            if (picture.horizontalMirror || picture.verticalMirror) {
                                btm = BitmapFactory.decodeByteArray(picture.data, 0, picture.data.length)
                                        .copy(Bitmap.Config.ARGB_8888, true);
                                saveBmp = flipBitmap(btm, picture.horizontalMirror, picture.verticalMirror);
                                file = ImageUtil.INSTANCE.savePicture(saveBmp, "IMG_" + System.currentTimeMillis() + ".jpg");
                            } else {
                                file = ImageUtil.INSTANCE.savePicture(picture.data, "IMG_" + System.currentTimeMillis() + ".jpg");
                            }
                        } else if (picture.btm != null) {
                            btm = picture.btm;
                            saveBmp = flipBitmap(btm, picture.horizontalMirror, picture.verticalMirror);
                            file = ImageUtil.INSTANCE.savePicture(saveBmp, "IMG_" + System.currentTimeMillis() + ".jpg");
                        } else {
                            throw new RuntimeException("data or btm must not null");
                        }

                        if (btm != null) {
                            btm.recycle();
                        }
                        if (saveBmp != null) {
                            saveBmp.recycle();
                        }

                        if (ImageUtil.INSTANCE.updateGallery(file)) {
                            mCameraHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtil.INSTANCE.showToast("拍照成功");
                                }
                            });
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

        queue.clear();
        LogUtil.INSTANCE.log(TAG, "close");
    }

    /**
     * 翻转 bitmap
     *
     * @param btm
     * @param horizontalMirror
     * @param vertical
     * @return
     */
    private Bitmap flipBitmap(Bitmap btm, boolean horizontalMirror, boolean vertical) {
        Matrix m = new Matrix();
        if (horizontalMirror) {
            m.postScale(-1, 1);   //镜像水平翻转
        }
        if (vertical) {
            m.postScale(1, -1);   //镜像垂直翻转
        }
        Canvas cv = new Canvas(btm);
        Bitmap saveBmp = Bitmap.createBitmap(btm, 0, 0, btm.getWidth(), btm.getHeight(), m, true);
        Rect rect = new Rect(0, 0, btm.getWidth(), btm.getHeight());
        cv.drawBitmap(saveBmp, rect, rect, null);
        return saveBmp;
    }

    private static class Picture {
        private byte[] data;
        private Bitmap btm;
        private boolean horizontalMirror;
        private boolean verticalMirror;

        public Picture(boolean horizontalMirror, boolean verticalMirror) {
            this.verticalMirror = verticalMirror;
            this.horizontalMirror = horizontalMirror;
        }

    }
}