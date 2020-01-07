package com.basemvp.main

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.APP
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = RouteString.MAIN, name = "MVP选择页")
class MainActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        dampingRc.setOnClickListener {
            ARouter.getInstance().build(RouteString.DAMPING_RC).navigation()
        }

        vp2Fragment.setOnClickListener {
            ARouter.getInstance().build(RouteString.VP_FRAMENT).navigation()
        }

        showFragment.setOnClickListener {
            ARouter.getInstance().build(RouteString.SHOW_FRAMENT).navigation()
        }

        showDialog.setOnClickListener {
            ARouter.getInstance().build(RouteString.DIALOG).navigation()
        }

        coordinator1.setOnClickListener {
            ARouter.getInstance().build(RouteString.COORDINATOR1).navigation()
        }

        exit.setOnClickListener {
            APP.exitApp()
        }
    }

    override fun initData() {

    }

}
