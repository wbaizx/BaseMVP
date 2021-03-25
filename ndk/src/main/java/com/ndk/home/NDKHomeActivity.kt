package com.ndk.home

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.ndk.R
import kotlinx.android.synthetic.main.activity_ndk_home.*

@Route(path = "/ndk/ndk_home", name = "NDK模块首页")
class NDKHomeActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_ndk_home

    override fun initView() {
        text.text = NDKHelper.stringFromJNI()
    }

    override fun initData() {
        JavaTest().start()
    }
}