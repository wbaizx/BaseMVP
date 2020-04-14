package com.camera_opengl.home;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.util.LogUtil;
import com.camera_opengl.R;
import com.camera_opengl.home.camera.CameraControl;
import com.camera_opengl.home.gl.egl.EGLSurfaceView;
import com.camera_opengl.home.gl.egl.SurfaceTextureListener;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//RouteString.CAMERA_HOME
//RouteString.isNeedLogin
@Route(path = "/camera/camera_home", name = "组件化camera首页", extras = 1)
public class CameraActivity extends BaseActivity implements ControlListener, SurfaceTextureListener {
    private static final String TAG = "CameraActivity";
    private final int CAMERA_PERMISSION_CODE = 666;

    private boolean hasPermissions = false;
    private boolean isResume = false;
    private boolean isSurfaceCreated = false;
    private CameraControl cameraControl;

    private EGLSurfaceView eglSurfaceView;

    private ReentrantLock look = new ReentrantLock();

    private SavePictureThread mSaveThread;

    //滤镜type
    private int filterType = -1;

    @Override
    protected int getContentView() {
        return R.layout.activity_camera;
    }

    @Override
    protected void setImmersionBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
    }

    @Override
    protected void initView() {
        getPermissions();

        mSaveThread = new SavePictureThread();
        mSaveThread.start();

        cameraControl = new CameraControl(this, this);

        eglSurfaceView = findViewById(R.id.eglSurfaceView);
        eglSurfaceView.setSurfaceTextureListener(this);
        eglSurfaceView.setControlListener(this);

        findViewById(R.id.switchCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraControl.switchCamera();
            }
        });

        findViewById(R.id.takePicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cameraControl.takePicture();
                eglSurfaceView.takePicture();
            }
        });

        Button switchFilter = findViewById(R.id.switchFilter);
        switchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterType++;
                //filterType的判断和着色器中的逻辑判断要一致
                if (filterType == 0) {
                    switchFilter.setText("LUT滤镜");
                } else if (filterType == 1) {
                    switchFilter.setText("灰度滤镜");
                } else if (filterType == 2) {
                    switchFilter.setText("亮度滤镜");
                } else {
                    //没有滤镜
                    filterType = -1;
                    switchFilter.setText("原色");
                }

                eglSurfaceView.setFilterType(filterType);
            }
        });
    }

    /**
     * 添加 AfterPermissionGranted 注解，在所有权限申请成功后会再次调用此方法，手动打开除外
     */
    @AfterPermissionGranted(CAMERA_PERMISSION_CODE)
    private void getPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            LogUtil.INSTANCE.log(TAG, "hasPermissions");
            begin();
        } else {
            EasyPermissions.requestPermissions(
                    this, "为了正常使用，需要获取以下权限",
                    CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NotNull List<String> perms) {
        LogUtil.INSTANCE.log(TAG, "onPermissionsDenied");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            LogUtil.INSTANCE.log(TAG, "Denied and not prompted");
            new AppSettingsDialog.Builder(this)
                    .setTitle("跳转到手动打开")
                    .setRationale("跳转到手动打开")
                    .build().show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                finish();
            } else {
                begin();
            }
        }
    }

    private void begin() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        if (am != null && am.getDeviceConfigurationInfo().reqGlEsVersion >= 0x30000) {
            look.lock();

            LogUtil.INSTANCE.log(TAG, "begin");
            hasPermissions = true;
            openCamera();

            look.unlock();
        } else {
            finish();
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        look.lock();

        LogUtil.INSTANCE.log(TAG, "onResume");
        isResume = true;
        openCamera();

        look.unlock();
    }

    @Override
    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
        cameraControl.setSurfaceTexture(surfaceTexture);

        look.lock();

        LogUtil.INSTANCE.log(TAG, "onSurfaceCreated");
        isSurfaceCreated = true;
        openCamera();

        look.unlock();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    private void openCamera() {
        LogUtil.INSTANCE.log(TAG, "try openCamera " + hasPermissions + "-" + isResume + "-" + isSurfaceCreated);
        if (hasPermissions && isResume && isSurfaceCreated) {
            LogUtil.INSTANCE.log(TAG, "openCamera");
            cameraControl.startCameraThread();
            cameraControl.openCamera();
        }
    }

    @Override
    public void confirmCameraSize(Size cameraSize) {
        eglSurfaceView.confirmCameraSize(cameraSize);
    }

    @Override
    public void imageAvailable(byte[] bytes, boolean horizontalMirror, boolean verticalMirror) {
        mSaveThread.putData(bytes, horizontalMirror, verticalMirror);
    }

    @Override
    public void imageAvailable(Bitmap btm, boolean horizontalMirror, boolean verticalMirror) {
        mSaveThread.putData(btm, horizontalMirror, verticalMirror);
    }

    @Override
    protected void onPause() {
        super.onPause();

        look.lock();
        LogUtil.INSTANCE.log(TAG, "onPause");
        if (hasPermissions && isResume && isSurfaceCreated) {
            cameraControl.closeCamera();
            cameraControl.stopCameraThread();
        }

        isResume = false;
        look.unlock();
    }

    @Override
    protected void onDestroy() {
        LogUtil.INSTANCE.log(TAG, "onDestroy");
        cameraControl.onDestroy();
        eglSurfaceView.onDestroy();
        mSaveThread.interrupt();
        super.onDestroy();
    }
}
