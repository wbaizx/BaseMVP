package com.camera_opengl.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.base.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CameraControl {
    private static final String TAG = "CameraControl";

    private Activity activity;
    private ReentrantLock look = new ReentrantLock();
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

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private AutoFitTextureView autoFitTextureView;

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

    public CameraControl(Activity activity, AutoFitTextureView autoFitTextureView) {
        this.activity = activity;
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        this.autoFitTextureView = autoFitTextureView;
    }

    public void startBackgroundThread() {
        if (mBackgroundThread == null) {
            LogUtil.INSTANCE.log(TAG, "startBackgroundThread");
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    public void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            LogUtil.INSTANCE.log(TAG, "stopBackgroundThread");
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
                LogUtil.INSTANCE.log(TAG, "stopBackgroundThread X");
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

    private CameraCaptureSession.CaptureCallback previewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
//            LogUtil.INSTANCE.log(TAG, "onCaptureStarted");
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            LogUtil.INSTANCE.log(TAG, "onCaptureFailed");
            closeCamera();
            activity.finish();
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
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LogUtil.INSTANCE.log(TAG, "openCamera lock");
                look.lock();
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

                            LogUtil.INSTANCE.log(TAG, "begin openCamera");
                            manager.openCamera(cameraId, mStateCallback, mBackgroundHandler);
                            break;
                        }
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                } finally {
                    look.unlock();
                }
            }
        });
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
            SurfaceTexture texture = autoFitTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), previewCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
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
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.INSTANCE.log(TAG, "closeCamera lock");
                try {
                    look.lock();
                    LogUtil.INSTANCE.log(TAG, "closeCamera");
                    closePreviewSession();
                    if (null != mCameraDevice) {
                        mCameraDevice.close();
                        mCameraDevice = null;
                    }
                    LogUtil.INSTANCE.log(TAG, "closeCamera X");
                } catch (Exception e) {
                    throw new RuntimeException("Interrupted while trying to lock camera closing.");
                } finally {
                    look.unlock();
                }
            }
        });
    }

    public void destroy() {
        activity = null;
        LogUtil.INSTANCE.log(TAG, "destroy X");
    }
}
