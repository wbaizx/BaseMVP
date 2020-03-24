package com.camera_opengl.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.base.common.util.AndroidUtil;
import com.base.common.util.LogUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraControl {
    private static final String TAG = "CameraControl";

    private Activity activity;

    private CameraManager manager;
    private Integer mSensorOrientation;
    //最终确定的视频尺寸
    private Size mVideoSize;
    //最终确定的预览尺寸
    private Size mPreviewSize;
    //闪光灯是否支持
    private Boolean supportFlash;
    //期望预览宽
    private int previewWidth;
    //期望预览高
    private int previewHeight;

    private SavePictureThread mSaveThread;

    private HandlerThread mCameraThread;
    private Handler mCameraHandler;

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private AutoFitTextureView autoFitTextureView;
    private Surface previewSurface;

    private ImageReader mImageReader;

    //    CameraCharacteristics.LENS_FACING_BACK
    //    CameraCharacteristics.LENS_FACING_FRONT
    private int cameraOrientation = CameraCharacteristics.LENS_FACING_BACK;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private static final int PREVIEW = 0;
    private static final int AFLOCK = 1;
    private static final int TAKEPICTURE = 2;
    private int mState = PREVIEW;

    public CameraControl(Activity activity, AutoFitTextureView autoFitTextureView) {
        this.activity = activity;
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        this.autoFitTextureView = autoFitTextureView;

        mSaveThread = new SavePictureThread();
        mSaveThread.setSaveListener(new SavePictureThread.SaveListener() {
            @Override
            public void saveSuccess() {
                if (CameraControl.this.activity != null) {
                    CameraControl.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.INSTANCE.showToast("拍照成功");
                        }
                    });
                }
            }
        });
        mSaveThread.start();
    }

    public void startCameraThread() {
        if (mCameraThread == null) {
            LogUtil.INSTANCE.log(TAG, "startCameraThread");
            mCameraThread = new HandlerThread("CameraBackground");
            mCameraThread.start();
            mCameraHandler = new Handler(mCameraThread.getLooper());
        }
    }

    public void stopCameraThread() {
        if (mCameraThread != null) {
            LogUtil.INSTANCE.log(TAG, "stopCameraThread");
            mCameraThread.quitSafely();
            try {
                mCameraThread.join();
                mCameraThread = null;
                mCameraHandler = null;
                LogUtil.INSTANCE.log(TAG, "stopCameraThread X");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            LogUtil.INSTANCE.log(TAG, "onOpened");
            mCameraDevice = cameraDevice;
            startPreview();
            if (null != autoFitTextureView) {
                configureTransform(autoFitTextureView.getWidth(), autoFitTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            LogUtil.INSTANCE.log(TAG, "onDisconnected");
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            LogUtil.INSTANCE.log(TAG, "onError");
            cameraDevice.close();
            mCameraDevice = null;
            activity.finish();
        }
    };

    /**
     * 预览和af锁定时动作回调
     */
    private CameraCaptureSession.CaptureCallback previewCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            if (mState == AFLOCK) {
                Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                LogUtil.INSTANCE.log(TAG, "previewCallback af onCaptureCompleted afState " + afState + " aeState " + aeState);

                if (afState == null) {
                    captureStillPicture();
                } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                        CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                    if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                        //对焦完成
                        captureStillPicture();
                    }
                }
            }
        }
    };

    /**
     * 拍照动作回调
     */
    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            takePictureCompleted();
        }
    };

    /**
     * 拍照数据回调
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            LogUtil.INSTANCE.log(TAG, "onImageAvailable");
            Image image = reader.acquireNextImage();
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);

            mSaveThread.putData(bytes);
            mSaveThread.signal();

            image.close();
            LogUtil.INSTANCE.log(TAG, "onImageAvailable X");
        }
    };

    /**
     * 设置期望预览大小
     *
     * @param width
     * @param height
     */
    public void setPreviewSize(int width, int height) {
        LogUtil.INSTANCE.log(TAG, "设置期望预览大小 setPreviewSize");
        previewWidth = width;
        previewHeight = height;
    }

    public void openCamera() {
        mCameraHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LogUtil.INSTANCE.log(TAG, "openCamera");
                try {
                    for (String cameraId : manager.getCameraIdList()) {
                        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                        Integer integer = characteristics.get(CameraCharacteristics.LENS_FACING);

                        if (integer != null && cameraOrientation == integer) {
                            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                            if (map == null) {
                                throw new RuntimeException("Cannot get available preview/video sizes");
                            }

                            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                            LogUtil.INSTANCE.log(TAG, "chooseVideoSize " + mVideoSize.getWidth() + " -- " + mVideoSize.getHeight());

                            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                                    previewWidth, previewHeight, mVideoSize);
                            LogUtil.INSTANCE.log(TAG, "choosePreviewSize " + mPreviewSize.getWidth() + " -- " + mPreviewSize.getHeight());

                            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                            //检查闪光灯是否支持
                            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                            supportFlash = available == null ? false : available;

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int orientation = activity.getResources().getConfiguration().orientation;
                                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                        autoFitTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                                    } else {
                                        autoFitTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                                    }
                                    configureTransform(previewWidth, previewHeight);
                                }
                            });

                            createSurface();

                            LogUtil.INSTANCE.log(TAG, "begin openCamera");
                            manager.openCamera(cameraId, mStateCallback, mCameraHandler);
                            break;
                        }
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createSurface() {
        SurfaceTexture texture = autoFitTextureView.getSurfaceTexture();
        assert texture != null;
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        previewSurface = new Surface(texture);

        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCameraHandler);
    }


    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        LogUtil.INSTANCE.log(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            LogUtil.INSTANCE.log(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    /**
     * 获取输出数据需要旋转的角度
     */
    public int getOrientationHint() {
        if (mSensorOrientation != null) {
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            switch (mSensorOrientation) {
                case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                    return DEFAULT_ORIENTATIONS.get(rotation);
                case SENSOR_ORIENTATION_INVERSE_DEGREES:
                    return INVERSE_ORIENTATIONS.get(rotation);
            }
        }
        return 0;
    }

    public void configureTransform(int viewWidth, int viewHeight) {
        if (null == autoFitTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        LogUtil.INSTANCE.log(TAG, "configureTransform");
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        autoFitTextureView.setTransform(matrix);
    }

    private void startPreview() {
        LogUtil.INSTANCE.log(TAG, "startPreview");
        if (null == mCameraDevice || !autoFitTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }

        try {
            closePreviewSession();

            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            setRepeatingPreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setRepeatingPreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            //自动聚焦
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //自动曝光
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            //自动控制模式
            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), previewCallback, mCameraHandler);
            LogUtil.INSTANCE.log(TAG, "setRepeatingPreview");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void takePicture() {
        if (mState == PREVIEW) {
            try {
                LogUtil.INSTANCE.log(TAG, "takePicture");

                CaptureRequest.Builder afBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                afBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
                afBuilder.addTarget(previewSurface);

                mState = AFLOCK;
                LogUtil.INSTANCE.log(TAG, "mState " + mState + "-> AFLOCK");
                mPreviewSession.capture(afBuilder.build(), previewCallback, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void captureStillPicture() {
        LogUtil.INSTANCE.log(TAG, "captureStillPicture");
        try {
            if (null == mCameraDevice) {
                return;
            }

            //停止连续取景
            mPreviewSession.stopRepeating();

            mState = TAKEPICTURE;
            LogUtil.INSTANCE.log(TAG, "mState " + mState + "-> TAKEPICTURE");

            // 这是用来拍摄照片的CaptureRequest.Builder。
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.addTarget(previewSurface);

            //自动聚焦
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //自动曝光
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientationHint());

            //捕获图片
            mPreviewSession.capture(captureBuilder.build(), captureCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void takePictureCompleted() {
        try {
            LogUtil.INSTANCE.log(TAG, "takePictureCompleted");
            CaptureRequest.Builder completedBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //取消手动聚焦操作
            completedBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            completedBuilder.addTarget(previewSurface);
            mPreviewSession.capture(completedBuilder.build(), previewCallback, mCameraHandler);

            setRepeatingPreview();

            mState = PREVIEW;
            LogUtil.INSTANCE.log(TAG, "mState " + mState + "-> PREVIEW");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void switchCamera() {
        closeCamera();
        cameraOrientation = cameraOrientation == CameraCharacteristics.LENS_FACING_BACK ?
                CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
        openCamera();
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            LogUtil.INSTANCE.log(TAG, "closePreviewSession");
            mPreviewSession.close();
            mPreviewSession = null;
            LogUtil.INSTANCE.log(TAG, "closePreviewSession X");
        }
    }

    public void closeCamera() {
        mCameraHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.INSTANCE.log(TAG, "closeCamera");

                    if (mImageReader != null) {
                        mImageReader.setOnImageAvailableListener(null, mCameraHandler);
                        mImageReader.close();
                        mImageReader = null;
                    }

                    closePreviewSession();
                    if (null != mCameraDevice) {
                        mCameraDevice.close();
                        mCameraDevice = null;
                    }
                    LogUtil.INSTANCE.log(TAG, "closeCamera X");
                } catch (Exception e) {
                    throw new RuntimeException("Interrupted while trying to lock camera closing.");
                }
            }
        });
    }

    public void destroy() {
        activity = null;
        mSaveThread.interrupt();
        LogUtil.INSTANCE.log(TAG, "destroy X");
    }
}
