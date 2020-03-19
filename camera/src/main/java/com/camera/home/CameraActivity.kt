package com.camera.home

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.camera.R
import kotlinx.android.synthetic.main.activity_camera.*

@Route(path = RouteString.CAMERA_HOME, name = "组件化camera首页", extras = RouteString.isNeedLogin)
class CameraActivity : BaseActivity() {
    private val TAG = "CameraActivity"

    override fun getContentView() = R.layout.activity_camera

    override fun initView() {
    }

    override fun initData() {
    }
}
