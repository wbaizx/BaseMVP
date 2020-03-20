package com.camera_opengl.home;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.base.common.config.RouteString;
import com.base.common.util.LogUtil;
import com.camera_opengl.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@Route(path = RouteString.CAMERA_HOME, name = "组件化camera首页", extras = RouteString.isNeedLogin)
public class CameraActivity extends BaseActivity {
    private static final String TAG = "CameraActivity";
    private final int CAMERA_PERMISSION_CODE = 666;

    @Override
    protected int getContentView() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {
        getPermissions();
    }

    /**
     * 添加 AfterPermissionGranted 注解，在所有权限申请成功后会再次调用此方法，手动打开除外
     */
    @AfterPermissionGranted(CAMERA_PERMISSION_CODE)
    private void getPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            LogUtil.INSTANCE.log(TAG, "hasPermissions");
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
            }
        }
    }

    @Override
    protected void initData() {

    }
}
