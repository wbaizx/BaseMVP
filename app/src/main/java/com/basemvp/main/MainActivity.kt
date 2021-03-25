package com.basemvp.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Debug
import androidx.lifecycle.Observer
import androidx.work.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.BaseAPP
import com.base.common.base.BaseActivity
import com.base.common.base.dialog.DialogFactory
import com.base.common.util.*
import com.base.common.util.http.ObjectBean
import com.base.common.util.http.ParcelableBean
import com.base.common.util.http.ParcelableBean2
import com.base.common.util.http.SerializableBean
import com.base.common.util.imageload.LoadImage
import com.basemvp.R
import com.basemvp.main.coordinator.CoordinatorActivity
import com.basemvp.main.fragment_example.FragmentExampleActivity
import com.basemvp.main.item_animation.ItemAnimationMainActivity
import com.basemvp.main.mvp.MVPDemoActivity
import com.basemvp.main.mvvm.MVVMDemoActivity
import com.basemvp.main.shape_btn.ShowShapeBtnActivity
import com.basemvp.main.show_dialog.ShowDialogActivity
import com.basemvp.main.special_rc.SpecialRCActivity
import com.basemvp.main.workmanager.MainWork
import com.camera_opengl.home.CameraActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

private const val STORAGE_PERMISSION_CODE = 666

@Route(path = "/main/main_home", name = "功能选择页")
class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"

    override fun getContentView() = R.layout.activity_main

    override fun setImmersionBar() {
    }

    override fun initView() {
        //adb pull /sdcard/Android/data/com.basemvp/files/basemvp.trace  可用Profiler查看分析文件
        Debug.startMethodTracing("basemvp")
        LoadImage.loadBlur(LoadImage.imgUrl, mainImg)
        Debug.stopMethodTracing()

        saveImg.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val bitmap = ImageUtil.createBitmapFromView(mainImg)
                val file = ImageUtil.savePicture(bitmap, "test.jpg")
                if (ImageUtil.updateGallery(file, bitmap.width, bitmap.height)) {
                    withContext(Dispatchers.Main) {
                        AndroidUtil.showToast(this@MainActivity, "保存成功")
                    }
                }
                //注意如果是 ImageView 直接返回的 bitmap，用完后不要 recycle
//                bitmap.recycle()
            }
        }

        login.setOnClickListener {
            //测试 ARouter 带参数跳转
            launchARouter("/login/login_home")
                .withBoolean("is_goto_main", true)
                .withSerializable("serializable_bean", SerializableBean("1", "2", arrayListOf("3", "4")))
                .withParcelable("parcelable_bean", ParcelableBean("1", "2", arrayListOf("3", "4"), ParcelableBean2("5", "6")))
                .withObject("object_bean", ObjectBean("1", "2", arrayListOf("3", "4")))
                .normalNavigation(this)
        }

        fragmentExample.setOnClickListener {
            launchActivity(this, FragmentExampleActivity::class.java)
        }

        coordinator.setOnClickListener {
            launchActivity(this, CoordinatorActivity::class.java)
        }

        recyclerViewItemAnimation.setOnClickListener {
            launchActivity(this, ItemAnimationMainActivity::class.java)
        }

        specialRc.setOnClickListener {
            launchActivity(this, SpecialRCActivity::class.java)
        }

        showDialog.setOnClickListener {
            launchActivity(this, ShowDialogActivity::class.java)
        }

        mvpRoom.setOnClickListener {
            launchActivity(this, MVPDemoActivity::class.java)
        }

        mvvmRoom.setOnClickListener {
            launchActivity(this, MVVMDemoActivity::class.java)
        }

        shapeBtn.setOnClickListener {
            launchActivity(this, ShowShapeBtnActivity::class.java)
        }

        camera.setOnClickListener {
            launchARouter("/camera/camera_home").loginNavigation(this)
        }

        ndk.setOnClickListener {
            launchARouter("/ndk/ndk_home").loginNavigation(this)
        }

        exit.setOnClickListener {
            DialogFactory.createNormalDialog(
                this,
                "警告",
                "确认退出？",
                "确定",
                { BaseAPP.exitApp() },
                "取消"
            ).showDialog()
        }

        startWork()
    }

    /**
     * 后台执行任务，保证一定能执行，即使清理后台也会在下次启动时续上上次的任务
     */
    @SuppressLint("IdleBatteryChargingConstraints")
    private fun startWork() {
        log("MainWork", "startWork")
        // 创建约束条件
        val constraints = Constraints.Builder()
//            .setRequiresBatteryNotLow(true)                 // 电量不低
//            .setRequiredNetworkType(NetworkType.CONNECTED)  // 连接了网络
//            .setRequiresCharging(true)                      // 充电中
//            .setRequiresStorageNotLow(true)                 // 储存空间不低
//            .setRequiresDeviceIdle(true)                    // 设备空闲中
            .build()

        val mainWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<MainWork>()
            .setConstraints(constraints)    // 约束条件
            //重试任务时的策略
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(mainWorkRequest.id).observe(this, Observer {
            log("MainWork", "${it.state}")
            log("MainWork", "${it.progress.getInt("int", 0)}")
        })
        WorkManager.getInstance(this).enqueue(mainWorkRequest)
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

//    override fun onBackPressed() {
//        moveTaskToBack(true)
//    }
}
