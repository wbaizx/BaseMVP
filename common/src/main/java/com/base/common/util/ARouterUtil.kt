package com.base.common.util

import android.app.Activity
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.config.RouteString

private const val TAG = "ARouterUtil"

/**
 * 默认页面跳转code
 */
const val REQUEST_CODE = 1314

/**
 * 创建路由
 */
fun launchARouter(path: String): Postcard = ARouter.getInstance().build(path)

/**
 * 使用登录判断模式跳转
 */
fun Postcard.loginNavigation(activity: Activity, request: Int = REQUEST_CODE, arrival: (() -> Unit)? = null) {
    val isLogin = SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.LOGIN, false)

    if (isLogin) {
        navigation(activity, request, object : NavCallback() {
            override fun onInterrupt(postcard: Postcard?) {
            }

            override fun onArrival(postcard: Postcard?) {
                log(TAG, "loginNavigation  onArrival")
                arrival?.invoke()
            }
        })

    } else {
        log(TAG, "loginNavigation  Intercept")
        launchARouter(RouteString.LOGIN).normalNavigation(activity)
    }
}

/**
 * 使用普通模式跳转，跳过所有拦截器
 */
fun Postcard.normalNavigation(activity: Activity, request: Int = REQUEST_CODE, arrival: (() -> Unit)? = null) {
    greenChannel().navigation(activity, request, object : NavCallback() {
        override fun onInterrupt(postcard: Postcard?) {
        }

        override fun onArrival(postcard: Postcard?) {
            log(TAG, "normalNavigation  onArrival")
            arrival?.invoke()
        }
    })
}