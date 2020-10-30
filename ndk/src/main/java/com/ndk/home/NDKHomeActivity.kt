package com.ndk.home

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.log
import com.ndk.R
import kotlinx.android.synthetic.main.activity_ndk_home.*

@Route(path = RouteString.NDK_HOME, name = "NDK模块首页")
class NDKHomeActivity : BaseActivity() {
    private val TAG = "NDKHomeActivity"

    override fun getContentView() = R.layout.activity_ndk_home

    override fun initView() {
        text.text = NDKHelper.stringFromJNI()
    }

    override fun initData() {
        val time = System.currentTimeMillis()
        NDKHelper.test()
        log(TAG, "${System.currentTimeMillis() - time}")
    }
}