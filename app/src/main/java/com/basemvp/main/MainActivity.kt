package com.basemvp.main

import android.Manifest
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.BaseAPP
import com.base.common.base.BaseActivity
import com.base.common.config.GlideApp
import com.base.common.config.RouteString
import com.base.common.util.*
import com.base.common.util.dialog.DialogFactory
import com.base.common.util.http.ObjectBean
import com.base.common.util.http.ParcelableBean
import com.base.common.util.http.ParcelableBean2
import com.base.common.util.http.SerializableBean
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

private const val STORAGE_PERMISSION_CODE = 666

@Route(path = RouteString.MAIN, name = "功能选择页")
class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        GlideApp.with(this).load(imgUrl).specialInto(mainImg)

        saveImg.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val bitmap = ImageUtil.createBitmapFromView(mainImg)
                val file = ImageUtil.savePicture(bitmap, "test.jpg")
                if (ImageUtil.updateGallery(file, bitmap.width, bitmap.height)) {
                    withContext(Dispatchers.Main) {
                        AndroidUtil.showToast("保存成功")
                    }
                }
                //注意如果是 ImageView 直接返回的 bitmap，用完后不要 recycle
//                bitmap.recycle()
            }
        }

        login.setOnClickListener {
            //测试 ARouter 带参数跳转
            ARouter.getInstance().build(RouteString.LOGIN)
                .withBoolean(GOTO_MAIN, true)
                .withSerializable(SERIALIZABLE_BEAN, SerializableBean("1", "2", arrayListOf("3", "4")))
                .withParcelable(PARCELABLE_BEAN, ParcelableBean("1", "2", arrayListOf("3", "4"), ParcelableBean2("5", "6")))
                .withObject(OBJECT_BEAN, ObjectBean("1", "2", arrayListOf("3", "4")))
                .normalNavigation()
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

        mvvmRoom.setOnClickListener {
            ARouter.getInstance().build(RouteString.MVVM_ROOM).loginNavigation()
        }

        shapeBtn.setOnClickListener {
            ARouter.getInstance().build(RouteString.SHAPE_BTN).loginNavigation()
        }

        camera.setOnClickListener {
            ARouter.getInstance().build(RouteString.CAMERA_HOME).loginNavigation()
        }


        exit.setOnClickListener {
            DialogFactory.createNormalDialog(
                this,
                "警告",
                "确认退出？",
                "确定",
                { BaseAPP.exitApp() },
                "取消"
            )
        }
    }

    override fun initData() {
        getPermissions()
    }


    /**
     * 添加 AfterPermissionGranted 注解，在所有权限申请成功后会再次调用此方法，手动打开除外
     */
    @AfterPermissionGranted(STORAGE_PERMISSION_CODE)
    private fun getPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            LogUtil.log(TAG, "hasPermissions")
        } else {
            EasyPermissions.requestPermissions(
                this, "为了正常使用，需要获取以下权限",
                STORAGE_PERMISSION_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE
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
