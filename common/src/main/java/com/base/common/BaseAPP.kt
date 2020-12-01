package com.base.common

import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.util.log
import java.util.*

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

        /**
         * 线程安全ArrayList
         * CopyOnWriteArrayList 耗
         * 耗内存
         * 只有写加锁，性能好
         * 只能保证数据的最终一致性，不能保证数据的实时一致性
         * 多读少写
         *
         * Collections.synchronizedList
         * 全锁（遍历除外），性能差
         * 多写少读
         */
        private val allActivities = Collections.synchronizedList(arrayListOf<Activity>())

        private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                allActivities.add(activity)
                log(TAG, "onActivityCreated ${activity.javaClass.simpleName} ${allActivities.size}")
            }

            override fun onActivityStarted(activity: Activity) {
                log(TAG, "onActivityStarted ${activity.javaClass.simpleName} ${allActivities.size}")
            }

            override fun onActivityResumed(activity: Activity) {
                log(TAG, "onActivityResumed ${activity.javaClass.simpleName} ${allActivities.size}")
            }

            override fun onActivityPaused(activity: Activity) {
                log(TAG, "onActivityPaused ${activity.javaClass.simpleName} ${allActivities.size}")
            }

            override fun onActivityStopped(activity: Activity) {
                log(TAG, "onActivityStopped ${activity.javaClass.simpleName} ${allActivities.size}")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                log(TAG, "onActivitySaveInstanceState ${activity.javaClass.simpleName} ${allActivities.size}")
            }

            override fun onActivityDestroyed(activity: Activity) {
                //调用exitApp后这里还是会执行
                allActivities.remove(activity)
                log(TAG, "onActivityDestroyed ${activity.javaClass.simpleName} ${allActivities.size}")
            }
        }

        fun exitApp() {
            log(TAG, "exitApp ${allActivities.size}")

            //Collections.synchronizedList转换同步锁原理就是对其本身加锁，但不包含遍历
            //所以遍历需加同步锁，对象就是自身
            synchronized(allActivities) {
                allActivities.forEach {
                    it.finish()
                }
                allActivities.clear()
            }

            log(TAG, "exitApp ${allActivities.size}")
        }

        /**
         * 判断是否是 Debug 模式
         * 也可以使用 BuildConfig.DEBUG 判断（有些情况不准，具体什么情况百度）
         */
        private var isDebug: Boolean? = null

        fun isDebug(): Boolean {
            if (isDebug == null) {
                synchronized(this) {
                    if (isDebug == null) {
                        isDebug =
                            baseAppContext.applicationInfo != null && baseAppContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
                        Log.d("isDebug", "$isDebug")
                    }
                }
            }
            return isDebug!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        baseAppContext = this

        //注册Activity管理
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)

        initARouter()
        initKoin()
    }

    private fun initARouter() {
        if (isDebug()) {
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
}