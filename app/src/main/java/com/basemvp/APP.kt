package com.basemvp

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.util.LogUtil

class APP : Application() {
    companion object {
        private val TAG = "APP-Application"

        lateinit var appContext: APP
        private val allActivities = arrayListOf<Activity>()

        fun registerActivity(activity: Activity) {
            allActivities.add(activity)
        }

        fun unregisterActivity(activity: Activity) {
            allActivities.remove(activity)
        }

        fun exitApp() {
            allActivities.forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
            allActivities.clear()
            LogUtil.log(TAG, allActivities.size.toString())
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        if (isDebug()) {
            LogUtil.log(TAG, "isDebug")
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(appContext)
    }

    fun isDebug() =
        appContext.applicationInfo != null && appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}