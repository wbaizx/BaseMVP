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
import com.base.common.util.LogUtilKt;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SavePictureThread extends Thread {
    private static final String TAG = "SavePictureThread";

    private ReentrantLock look = new ReentrantLock();
    private Condition condition = look.newCondition();

    private ArrayBlockingQueue<Picture> queue = new ArrayBlockingQueue<>(5);

    public void putData(byte[] data, boolean horizontalMirror, boolean verticalMirror) {
        look.lock();

        LogUtilKt.log(TAG, "putData bytes");
        Picture picture = new Picture(horizontalMirror, verticalMirror);
        picture.data = data;
        queue.offer(picture);

        condition.signal();
        look.unlock();
    }

    public void putData(Bitmap btm, boolean horizontalMirror, boolean verticalMirror) {
        look.lock();

        LogUtilKt.log(TAG, "putData bitmap");
        Picture picture = new Picture(horizontalMirror, verticalMirror);
        picture.btm = btm;
        queue.offer(picture);

        condition.signal();
        look.unlock();
    }

    @Override
    public void run() {
        super.run();

        Handler mMainHandler = new Handler(Looper.getMainLooper());

        while (!Thread.currentThread().isInterrupted()) {
            try {
                look.lock();
                if (!queue.isEmpty()) {
                    Picture picture = queue.poll();
                    LogUtilKt.log(TAG, "run save");

                    if (picture != null) {
                        File file;
                        Bitmap btm = null;
                        Bitmap saveBmp = null;

                        if (picture.data != null) {
                            if (picture.horizontalMirror || picture.verticalMirror) {
                                btm = BitmapFactory.decodeByteArray(picture.data, 0, picture.data.length)
                                        .copy(Bitmap.Config.ARGB_8888, true);
                                saveBmp = flipBitmap(btm, picture.horizontalMirror, picture.verticalMirror);
                            } else {
                                saveBmp = BitmapFactory.decodeByteArray(picture.data, 0, picture.data.length);
                            }
                        } else if (picture.btm != null) {
                            btm = picture.btm;
                            saveBmp = flipBitmap(btm, picture.horizontalMirror, picture.verticalMirror);
                        } else {
                            throw new RuntimeException("data or btm must not null");
                        }

                        file = ImageUtil.INSTANCE.savePicture(saveBmp, "IMG_" + System.currentTimeMillis() + ".jpg");

                        if (ImageUtil.INSTANCE.updateGallery(file, saveBmp.getWidth(), saveBmp.getHeight())) {
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtil.INSTANCE.showToast(null, "拍照成功");
                                }
                            });
                        }

                        if (btm != null) {
                            btm.recycle();
                        }

                        saveBmp.recycle();
                    }
                } else {
                    LogUtilKt.log(TAG, "run await");
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
        LogUtilKt.log(TAG, "SavePictureThread close");
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
