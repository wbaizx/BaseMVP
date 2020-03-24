package com.camera_opengl.home;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.config.RouteString;
import com.base.common.util.LogUtil;
import com.camera_opengl.R;
import com.camera_opengl.home.gl.CameraGLSurfaceView;
import com.camera_opengl.home.gl.SurfaceTextureListener;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@Route(path = RouteString.CAMERA_HOME, name = "组件化camera首页", extras = RouteString.isNeedLogin)
public class CameraActivity extends BaseActivity {
    private static final String TAG = "CameraActivity";
    private final int CAMERA_PERMISSION_CODE = 666;

    private boolean hasPermissions = false;
    private boolean isResume = false;
    private boolean isSurfaceCreated = false;
    private CameraGLSurfaceView glSurfaceView;
    private CameraControl cameraControl;

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
        glSurfaceView = findViewById(R.id.glSurfaceView);

        glSurfaceView.setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
                LogUtil.INSTANCE.log(TAG, "onSurfaceCreated");
                cameraControl.setSurfaceTexture(surfaceTexture);
                isSurfaceCreated = true;
                openCamera();
            }

            @Override
            public void onSurfaceChanged(int width, int height) {

            }
        });

        cameraControl = new CameraControl(this);

        getPermissions();

        findViewById(R.id.switchCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraControl.switchCamera();
            }
        });

        findViewById(R.id.takePicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraControl.takePicture();
            }
        });
    }

    private void openCamera() {
        synchronized (this) {
            if (hasPermissions && isResume && isSurfaceCreated) {
                cameraControl.startCameraThread();
                cameraControl.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        glSurfaceView.requestRender();
                    }
                });
                cameraControl.openCamera();
            }
        }
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
            hasPermissions = true;
            LogUtil.INSTANCE.log(TAG, "begin");
            openCamera();
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
        isResume = true;
        LogUtil.INSTANCE.log(TAG, "onResume");
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
        isSurfaceCreated = false;
        LogUtil.INSTANCE.log(TAG, "onPause");
        if (hasPermissions) {
            cameraControl.closeCamera();
            cameraControl.stopCameraThread();
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.INSTANCE.log(TAG, "onDestroy");
        cameraControl.destroy();
        super.onDestroy();
    }
}
