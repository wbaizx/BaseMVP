package com.camera_opengl.home.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
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
import com.camera_opengl.home.ControlListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CameraControl {
    private static final String TAG = "CameraControl";

    private Activity activity;
    private ControlListener controlListener;

    private CameraManager manager;
    private Integer mSensorOrientation;

    //闪光灯是否支持
    private Boolean supportFlash;

    public static final float RATIO_1_1 = 1f;
    public static final float RATIO_4_3 = 4f / 3;
    public static final float RATIO_16_9 = 16f / 9;
    private float aspectRatio = RATIO_16_9;

    //因为activity使用沉浸式，获取的屏幕高度会不准确，需要加上导航栏和状态栏高度
    //ImmersionBar.getNavigationBarHeight(activity) + ImmersionBar.getStatusBarHeight(activity)
    //期望预览宽，宽高需要对调
    private int expectWidth = AndroidUtil.INSTANCE.getScreenHeight();
    //期望预览高，宽高需要对调
    private int expectHeight = AndroidUtil.INSTANCE.getScreenWidth();
    //最终确定的尺寸
    private Size mfinalSize;

    private HandlerThread mCameraThread;
    private Handler mCameraHandler;

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private Surface previewSurface;
    private Surface captureSurface;

    private SurfaceTexture surfaceTexture;

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

    private final int PREVIEW = 0;
    private final int AFLOCK = 1;
    private final int TAKE_PICTURE = 2;
    private int mState = PREVIEW;

    public CameraControl(Activity activity, ControlListener controlListener) {
        this.activity = activity;
        this.controlListener = controlListener;
        manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
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
            LogUtil.INSTANCE.log(TAG, "camera onOpened");
            mCameraDevice = cameraDevice;
            startPreview();
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

            controlListener.imageAvailable(bytes,
                    cameraOrientation == CameraCharacteristics.LENS_FACING_FRONT, false);

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
    public void setExpectPreviewSize(int width, int height) {
        LogUtil.INSTANCE.log(TAG, "设置期望预览大小 setPreviewSize " + width + "-" + height);
        expectWidth = width;
        expectHeight = height;
    }

    /**
     * 设置宽高比
     *
     * @param aspectRatio
     */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
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

                            chooseSize(map);

                            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                            //检查闪光灯是否支持
                            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                            supportFlash = available == null ? false : available;

                            createSurface();

                            LogUtil.INSTANCE.log(TAG, "real openCamera");
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

    private void chooseSize(StreamConfigurationMap map) {
        LogUtil.INSTANCE.log(TAG, "expectPreviewSize " + expectWidth + " -- " + expectHeight + " -- " +
                ((float) expectWidth / expectHeight));

        Size[] outputSizes = map.getOutputSizes(MediaRecorder.class);
        List<Size> sizeList = new ArrayList<>();
        for (Size size : outputSizes) {
            if (size.getWidth() == size.getHeight() * aspectRatio) {
                sizeList.add(size);
                LogUtil.INSTANCE.log(TAG, "outputSizes " + size.getWidth() + " -- " + size.getHeight() + " -- " +
                        ((float) size.getWidth() / size.getHeight()));
            }
        }

        if (sizeList.isEmpty()) {
            throw new RuntimeException("Did not find the right size");
        }

        mfinalSize = Collections.min(sizeList, new CompareSize(expectWidth, expectHeight));

        LogUtil.INSTANCE.log(TAG, "finalSize " +
                mfinalSize.getWidth() + " -- " + mfinalSize.getHeight() + " -- " +
                ((float) mfinalSize.getWidth() / mfinalSize.getHeight()));

        //确定size后回传出去
        controlListener.confirmCameraSize(mfinalSize);
    }

    private void createSurface() {
        surfaceTexture.setDefaultBufferSize(mfinalSize.getWidth(), mfinalSize.getHeight());
        previewSurface = new Surface(surfaceTexture);

        mImageReader = ImageReader.newInstance(mfinalSize.getWidth(), mfinalSize.getHeight(),
                ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCameraHandler);
        captureSurface = mImageReader.getSurface();
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

    private void startPreview() {
        LogUtil.INSTANCE.log(TAG, "startPreview");
        if (null == mCameraDevice || null == mfinalSize) {
            return;
        }

        try {
            closePreviewSession();

            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, captureSurface),
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
            if (cameraOrientation == CameraCharacteristics.LENS_FACING_FRONT) {
                //前置不支持聚焦
                captureStillPicture();
            } else {
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
    }

    private void captureStillPicture() {
        LogUtil.INSTANCE.log(TAG, "captureStillPicture");
        try {
            if (null == mCameraDevice) {
                return;
            }

            //停止连续取景
            mPreviewSession.stopRepeating();

            mState = TAKE_PICTURE;
            LogUtil.INSTANCE.log(TAG, "mState " + mState + "-> TAKEPICTURE");

            // 这是用来拍摄照片的CaptureRequest.Builder。
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(captureSurface);
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
        if (mState == PREVIEW) {
            closeCamera();
            cameraOrientation = cameraOrientation == CameraCharacteristics.LENS_FACING_BACK ?
                    CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
            openCamera();
        }
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

    public void onDestroy() {
        activity = null;
        controlListener = null;
        LogUtil.INSTANCE.log(TAG, "destroy X");
    }
}
