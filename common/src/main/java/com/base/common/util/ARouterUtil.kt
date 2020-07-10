package com.base.common.util

import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.BaseAPP
import com.base.common.config.RouteString

private const val TAG = "ARouterUtil"

const val GOTO_MAIN = "is_goto_main"
const val SERIALIZABLE_BEAN = "serializable_bean"
const val PARCELABLE_BEAN = "parcelable_bean"
const val OBJECT_BEAN = "object_bean"

/**
 * 使用登录判断模式跳转
 */
fun Postcard.loginNavigation(arrival: (() -> Unit)? = null) {
    navigation(BaseAPP.baseAppContext, object : NavCallback() {
        override fun onInterrupt(postcard: Postcard?) {
            //这个标志在拦截器中赋值
            if (postcard?.tag == RouteString.isNeedLoginTag) {
                LogUtil.log(TAG, "loginNavigation  登录拦截")
                ARouter.getInstance().build(RouteString.LOGIN).normalNavigation()
            } else {
                LogUtil.log(TAG, "loginNavigation  onInterrupt")
            }
        }

        override fun onArrival(postcard: Postcard?) {
            LogUtil.log(TAG, "loginNavigation  onArrival")
            arrival?.invoke()
        }
    })
}

/**
 * 使用普通模式跳转，跳过所有拦截器
 */
fun Postcard.normalNavigation(arrival: (() -> Unit)? = null) {
    greenChannel().navigation(BaseAPP.baseAppContext, object : NavCallback() {
        override fun onArrival(postcard: Postcard?) {
            LogUtil.log(TAG, "normalNavigation  onArrival")
            arrival?.invoke()
        }
    })
}