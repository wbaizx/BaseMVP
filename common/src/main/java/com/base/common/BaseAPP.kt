package com.base.common

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.util.LogUtil

open class BaseAPP : Application() {
    companion object {
        private val TAG = "BaseAPP-Application"

        lateinit var baseAppContext: BaseAPP
        private val allActivities = arrayListOf<Activity>()

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
        if (isDebug()) {
            LogUtil.log(TAG, "isDebug")
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(baseAppContext)
    }

    fun isDebug() =
        baseAppContext.applicationInfo != null && baseAppContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}