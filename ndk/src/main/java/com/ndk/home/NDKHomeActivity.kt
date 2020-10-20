package com.ndk.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.config.RouteString
import com.ndk.R

@Route(path = RouteString.NDK_HOME, name = "NDK模块首页", extras = RouteString.isNeedLogin)
class NDKHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ndk_home)
    }
}