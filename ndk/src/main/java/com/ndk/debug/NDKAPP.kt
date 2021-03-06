package com.ndk.debug

import com.base.common.BaseAPP
import com.base.common.util.log

/**
 * 对应模块的Application只有在配置ndk组件是否可以独立运行时才生效
 * 记得要在对应AndroidManifest中使用
 */
class NDKAPP : BaseAPP() {

    /**
     * 组件单独运行时，可以在这模拟登陆等一些特殊操作
     */
    override fun onCreate() {
        super.onCreate()
        log("NDKAPP", "$baseAppContext")
    }

    override fun initKoin() {
    }
}