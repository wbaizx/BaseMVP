package com.basemvp.main

import android.Manifest
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.BaseAPP
import com.base.common.base.BaseActivity
import com.base.common.base.dialog.DialogFactory
import com.base.common.config.RouteString
import com.base.common.config.RouteString.GOTO_MAIN
import com.base.common.config.RouteString.OBJECT_BEAN
import com.base.common.config.RouteString.PARCELABLE_BEAN
import com.base.common.config.RouteString.SERIALIZABLE_BEAN
import com.base.common.util.*
import com.base.common.util.http.ObjectBean
import com.base.common.util.http.ParcelableBean
import com.base.common.util.http.ParcelableBean2
import com.base.common.util.http.SerializableBean
import com.base.common.util.imageload.LoadImage
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val STORAGE_PERMISSION_CODE = 666

@Route(path = RouteString.MAIN, name = "功能选择页")
class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        LoadImage.load(LoadImage.imgUrl, mainImg, LoadImage.BLUR)

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
            launchARouter(RouteString.LOGIN)
                .withBoolean(GOTO_MAIN, true)
                .withSerializable(SERIALIZABLE_BEAN, SerializableBean("1", "2", arrayListOf("3", "4")))
                .withParcelable(PARCELABLE_BEAN, ParcelableBean("1", "2", arrayListOf("3", "4"), ParcelableBean2("5", "6")))
                .withObject(OBJECT_BEAN, ObjectBean("1", "2", arrayListOf("3", "4")))
                .normalNavigation(this)
        }

        fragmentExample.setOnClickListener {
            launchARouter(RouteString.FRAGMENT_EXAMPLE).normalNavigation(this)
        }

        coordinator.setOnClickListener {
            launchARouter(RouteString.COORDINATOR).normalNavigation(this)
        }

        recyclerViewItemAnimation.setOnClickListener {
            launchARouter(RouteString.ITEM_ANIMATION).normalNavigation(this)
        }

        specialRc.setOnClickListener {
            launchARouter(RouteString.SPECIAL_RC).normalNavigation(this)
        }

        showDialog.setOnClickListener {
            launchARouter(RouteString.DIALOG).loginNavigation(this)
        }

        mvpRoom.setOnClickListener {
            launchARouter(RouteString.MVP_ROOM).loginNavigation(this)
        }

        mvvmRoom.setOnClickListener {
            launchARouter(RouteString.MVVM_ROOM).loginNavigation(this)
        }

        shapeBtn.setOnClickListener {
            launchARouter(RouteString.SHAPE_BTN).loginNavigation(this)
        }

        camera.setOnClickListener {
            launchARouter(RouteString.CAMERA_HOME).loginNavigation(this)
        }

        ndk.setOnClickListener {
            launchARouter(RouteString.NDK_HOME).loginNavigation(this)
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
            log(TAG, "hasPermissions")
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
    override fun deniedPermission(requestCode: Int, perms: MutableList<String>) {
        finish()
    }

    /**
     * 跳转系统打开权限页面返回，或者跳转系统打开权限的指引弹窗被关闭后回调
     * 此时不会再次调用AfterPermissionGranted注解方法，所以这里要再次检查权限
     */
    override fun resultCheckPermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            finish()
        }
    }
}
