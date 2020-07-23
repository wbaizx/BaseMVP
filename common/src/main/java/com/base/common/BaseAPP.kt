package com.base.common

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.util.LogUtil

/**
 * 基类 Application
 *
 * 记录一个翻翻工具 蚂蚁  https://pp.lanshuapi.com/
 */
open class BaseAPP : Application() {
    companion object {
        private val TAG = "BaseAPP-Application"

        lateinit var baseAppContext: BaseAPP
        val allActivities = arrayListOf<Activity>()

        fun registerActivity(activity: Activity) {
            allActivities.add(activity)
        }

        fun unregisterActivity(activity: Activity) {
            allActivities.remove(activity)
        }

        fun exitApp() {
            allActivities.forEach {
                it.finish()
            }
            allActivities.clear()
            LogUtil.log(TAG, allActivities.size.toString())
        }
    }

    override fun onCreate() {
        super.onCreate()
        baseAppContext = this

        //初始化三方 sdk 可以使用 App Startup
        initARouter()
    }

    private fun initARouter() {
        if (isDebug()) {
            LogUtil.log(TAG, "isDebug")
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(baseAppContext)
    }

    /**
     * 判断是否是 Debug 模式
     * 也可以使用 BuildConfig.DEBUG 判断（有些情况不准，具体什么情况百度）
     */
    private fun isDebug() =
        baseAppContext.applicationInfo != null && baseAppContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}