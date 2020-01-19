package com.basemvp.main

import android.Manifest
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.APP
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.LogUtil
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@Route(path = RouteString.MAIN, name = "功能选择页")
class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private val TAG = "MainActivity"

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        fragmentExample.setOnClickListener {
            ARouter.getInstance().build(RouteString.FRAGMENT_EXAMPLE).navigation()
        }

        coordinator.setOnClickListener {
            ARouter.getInstance().build(RouteString.COORDINATOR).navigation()
        }

        recyclerViewItemAnimation.setOnClickListener {
            ARouter.getInstance().build(RouteString.ITEM_ANIMATION).navigation()
        }

        specialRc.setOnClickListener {
            ARouter.getInstance().build(RouteString.SPECIAL_RC).navigation()
        }

        showDialog.setOnClickListener {
            ARouter.getInstance().build(RouteString.DIALOG).navigation()
        }



        exit.setOnClickListener {
            APP.exitApp()
        }
    }

    override fun initData() {
        getPermissions()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //添加AfterPermissionGranted 注解，在所有权限申请成功后会再次调用此方法，手动打开除外
    @AfterPermissionGranted(666)
    private fun getPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            LogUtil.log(TAG, "hasPermissions")
        } else {
            EasyPermissions.requestPermissions(
                this, "为什么拒绝",
                666, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        LogUtil.log(TAG, "onPermissionsGranted")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        LogUtil.log(TAG, "onPermissionsDenied")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            LogUtil.log(TAG, "Denied and not prompted")
            AppSettingsDialog.Builder(this)
                .setTitle("跳转到手动打开")
                .setRationale("跳转到手动打开")
                .build().show()
        }
    }

    override fun onRationaleDenied(requestCode: Int) {
        LogUtil.log(TAG, "onRationaleDenied")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        LogUtil.log(TAG, "onRationaleAccepted")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            LogUtil.log(TAG, "manual dialog close")
        }
    }
}
