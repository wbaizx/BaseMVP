package com.base.common.config

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.base.common.util.LogUtil
import com.base.common.util.SharedPreferencesUtil

object ARouterInterceptor {
    private val TAG = "ARouterInterceptor"

    /**
     * priority 越低，优先级越高
     */
    @Interceptor(priority = 5, name = "测试用拦截器")
    class TestInterceptor : IInterceptor {
        override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
            LogUtil.log(TAG, "ARouterInterceptor  process")

            //这个 extra 标志是对应 acitvity 配置的 extras 属性，是否需要登录
            //同时判断相应的条件，是否登录，已经测试通过，这里默认已经登录了
            val isLogin = SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.LOGIN, false)
            if (postcard?.extra == RouteString.isNeedLogin && !isLogin) {
                callback?.onInterrupt(RuntimeException(RouteString.isNeedLoginTag))
                LogUtil.log(TAG, "ARouterInterceptor --- ${postcard.tag}")
            } else {
                callback?.onContinue(postcard)
            }
        }

        override fun init(context: Context?) {
            LogUtil.log(TAG, "ARouterInterceptor  init")
        }
    }
}