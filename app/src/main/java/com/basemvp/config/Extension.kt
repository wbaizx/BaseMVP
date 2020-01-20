package com.basemvp.config

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.APP
import com.basemvp.R
import com.basemvp.util.LogUtil
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


/**
 * 使用登录判断模式跳转，acitvity 需要配置 extras 属性
 */
fun Postcard.loginNavigation() {
    navigation(APP.appContext, object : NavCallback() {
        override fun onInterrupt(postcard: Postcard?) {
            //这些标志在activity和拦截器中分别赋值
            if (postcard?.extra == RouteString.isNeedLogin && postcard.tag == RouteString.isNeedLoginTag) {
                LogUtil.log("loginNavigation", "loginNavigation  登录拦截")
                ARouter.getInstance().build(RouteString.MAIN).normalNavigation()
            } else {
                LogUtil.log("loginNavigation", "loginNavigation  onInterrupt")
            }
        }

        override fun onArrival(postcard: Postcard?) {
            LogUtil.log("loginNavigation", "loginNavigation  onArrival")
        }
    })
}

/**
 * 使用普通模式跳转，跳过所有拦截器
 */
fun Postcard.normalNavigation() {
    greenChannel().navigation()
}

fun GlideRequest<Drawable>.normalInto(mainImg: ImageView) {
    thumbnail(0.2f)
        .placeholder(R.mipmap.test_icon)
        .error(R.mipmap.test_icon)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(mainImg)
}