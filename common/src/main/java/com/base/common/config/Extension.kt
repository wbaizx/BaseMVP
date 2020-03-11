package com.base.common.config

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.BaseAPP
import com.base.common.R
import com.base.common.util.LogUtil
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

const val GOTO_MAIN = "is_goto_main"
/**
 * 使用登录判断模式跳转，acitvity 需要配置 extras 属性
 */
fun Postcard.loginNavigation(arrival: (() -> Unit)? = null) {
    navigation(BaseAPP.baseAppContext, object : NavCallback() {
        override fun onInterrupt(postcard: Postcard?) {
            //这些标志在activity和拦截器中分别赋值
            if (postcard?.extra == RouteString.isNeedLogin && postcard.tag == RouteString.isNeedLoginTag) {
                LogUtil.log("loginNavigation", "loginNavigation  登录拦截")
                ARouter.getInstance().build(RouteString.LOGIN).normalNavigation()
            } else {
                LogUtil.log("loginNavigation", "loginNavigation  onInterrupt")
            }
        }

        override fun onArrival(postcard: Postcard?) {
            LogUtil.log("loginNavigation", "loginNavigation  onArrival")
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
            LogUtil.log("normalNavigation", "normalNavigation  onArrival")
            arrival?.invoke()
        }
    })
}

const val imgUrl = "http://img1.imgtn.bdimg.com/it/u=1004510913,4114177496&fm=26&gp=0.jpg"
//const val imgUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583902813407&di=e5c444a2a80d5561d59d2866e3d2b8b8&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F68%2F61%2F300000839764127060614318218_950.jpg"
fun GlideRequest<Drawable>.normalInto(mainImg: ImageView) {
    thumbnail(0.2f)
        .placeholder(R.mipmap.test_icon)
        .error(R.mipmap.test_icon)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(mainImg)
}