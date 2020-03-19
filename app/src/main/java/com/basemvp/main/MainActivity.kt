package com.basemvp.main

import android.Manifest
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.BaseAPP
import com.base.common.base.BaseActivity
import com.base.common.config.*
import com.base.common.util.AndroidUtil
import com.base.common.util.ImageUtil
import com.base.common.util.LogUtil
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@Route(path = RouteString.MAIN, name = "功能选择页")
class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        GlideApp.with(this).load(imgUrl).normalInto(mainImg)

        saveImg.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val bitmap = ImageUtil.createBitmapFromView(mainImg)
                val file = ImageUtil.savePicture(bitmap)
                if (ImageUtil.updateGallery(file)) {
                    withContext(Dispatchers.Main) {
                        AndroidUtil.showToast("保存成功")
                    }
                }
            }
        }

        login.setOnClickListener {
            ARouter.getInstance().build(RouteString.LOGIN).normalNavigation()
        }

        fragmentExample.setOnClickListener {
            ARouter.getInstance().build(RouteString.FRAGMENT_EXAMPLE).normalNavigation()
        }

        coordinator.setOnClickListener {
            ARouter.getInstance().build(RouteString.COORDINATOR).normalNavigation()
        }

        recyclerViewItemAnimation.setOnClickListener {
            ARouter.getInstance().build(RouteString.ITEM_ANIMATION).normalNavigation()
        }

        specialRc.setOnClickListener {
            ARouter.getInstance().build(RouteString.SPECIAL_RC).normalNavigation()
        }

        showDialog.setOnClickListener {
            ARouter.getInstance().build(RouteString.DIALOG).loginNavigation()
        }

        mvpRoom.setOnClickListener {
            ARouter.getInstance().build(RouteString.MVP_ROOM).loginNavigation()
        }

        camera.setOnClickListener {
            ARouter.getInstance().build(RouteString.CAMERA_HOME).loginNavigation()
        }


        exit.setOnClickListener {
            BaseAPP.exitApp()
        }
    }

    override fun initData() {
        getPermissions()
    }


    /**
     * 添加 AfterPermissionGranted 注解，在所有权限申请成功后会再次调用此方法，手动打开除外
     */
    @AfterPermissionGranted(666)
    private fun getPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            LogUtil.log(TAG, "hasPermissions")
        } else {
            EasyPermissions.requestPermissions(
                this, "为了正常使用，需要获取以下权限",
                666, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * 权限拒绝后回调
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        LogUtil.log(TAG, "onPermissionsDenied")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            LogUtil.log(TAG, "Denied and not prompted")
            AppSettingsDialog.Builder(this)
                .setTitle("跳转到手动打开")
                .setRationale("跳转到手动打开")
                .build().show()
        } else {
            finish()
        }
    }

    /**
     * 跳转手动打开权限后回调，不论是否打开权限都会回调，所以这里要再次检查权限
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                finish()
            }
        }
    }
}
