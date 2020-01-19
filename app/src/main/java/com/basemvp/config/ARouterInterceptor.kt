package com.basemvp.config

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.basemvp.util.LogUtil
import com.basemvp.util.SharedPreferencesUtil

/**
 * 我们经常需要在目标页面中配置一些属性，比方说"是否需要登陆"之类的
 * 在要跳转到的acitvity可以通过 Route 注解中的 extras 属性，这个属性是一个 int值32位，可以配置32个开关
 * 通过字节操作可以标识32个开关，通过开关标记目标页面的一些属性，在拦截器中可以拿到这个标记进行业务逻辑判断
 * 例如 @Route(path = "/test/activity", extras = 1)
 */
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