package com.basemvp.main

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.APP
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = RouteString.MAIN, name = "功能选择页")
class MainActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        fragmentExample.setOnClickListener {
            ARouter.getInstance().build(RouteString.FRAGMENT_EXAMPLE).navigation()
        }

        coordinator.setOnClickListener {
            ARouter.getInstance().build(RouteString.COORDINATOR).navigation()
        }

        recyclerViewItemAnimation.setOnClickListener {
            ARouter.getInstance().build(RouteString.ITEM_ANIMATION).navigation()
        }



        showDialog.setOnClickListener {
            ARouter.getInstance().build(RouteString.DIALOG).navigation()
        }

        dampingRc.setOnClickListener {
            ARouter.getInstance().build(RouteString.DAMPING_RC).navigation()
        }

        recyclerViewGallery.setOnClickListener {
            ARouter.getInstance().build(RouteString.GALLERY).navigation()
        }

        scrollToRecyclerView.setOnClickListener {
            ARouter.getInstance().build(RouteString.SCROLL_TO_RC).navigation()
        }



        exit.setOnClickListener {
            APP.exitApp()
        }
    }

    override fun initData() {

    }

}
