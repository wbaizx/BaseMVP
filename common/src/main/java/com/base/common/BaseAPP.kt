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
 *
 * 初始化三方 sdk 还可以可以使用 App Startup 方案
 */
abstract class BaseAPP : Application() {
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

        initARouter()

        initKoin()
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
     * 初始化koin注入框架，在主module或者需要单独运行的module的application中配置
     * 需要添加所有运行需要的module的di配置文件
     * 参考MainApp
     */
    abstract fun initKoin()

    /**
     * 判断是否是 Debug 模式
     * 也可以使用 BuildConfig.DEBUG 判断（有些情况不准，具体什么情况百度）
     */
    private fun isDebug() =
        baseAppContext.applicationInfo != null && baseAppContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}