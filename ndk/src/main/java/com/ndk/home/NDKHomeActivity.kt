package com.ndk.home

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.ndk.R
import kotlinx.android.synthetic.main.activity_ndk_home.*

@Route(path = RouteString.NDK_HOME, name = "NDK模块首页", extras = RouteString.isNeedLogin)
class NDKHomeActivity : BaseActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override fun getContentView() = R.layout.activity_ndk_home

    override fun initView() {
        text.text = stringFromJNI()
    }

    override fun initData() {
    }

    external fun stringFromJNI(): String
}