package com.camera_opengl.home;

import android.Manifest;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.config.RouteString;
import com.base.common.util.LogUtil;
import com.camera_opengl.R;
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
    private AutoFitTextureView autoFitTextureView;
    private CameraControl cameraControl;

    private Button switchCamera;
    private Button takePicture;

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
        switchCamera = findViewById(R.id.switchCamera);
        takePicture = findViewById(R.id.takePicture);

        autoFitTextureView = findViewById(R.id.autoFitTextureView);
        autoFitTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        cameraControl = new CameraControl(this, autoFitTextureView);
        getPermissions();

        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraControl.switchCamera();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraControl.takePicture();
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

    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.INSTANCE.log(TAG, "onSurfaceTextureAvailable");
            if (hasPermissions) {
                cameraControl.setPreviewSize(width, height);
                cameraControl.openCamera();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtil.INSTANCE.log(TAG, "onSurfaceTextureSizeChanged");
            cameraControl.configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.INSTANCE.log(TAG, "onSurfaceTextureDestroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void begin() {
        hasPermissions = true;
        LogUtil.INSTANCE.log(TAG, "begin");
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.INSTANCE.log(TAG, "onResume");
        if (hasPermissions) {
            cameraControl.startCameraThread();
            if (autoFitTextureView.isAvailable()) {
                cameraControl.setPreviewSize(autoFitTextureView.getWidth(), autoFitTextureView.getHeight());
                cameraControl.openCamera();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
