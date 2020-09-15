package com.basemvp.main.item_animation

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.launchARouter
import com.base.common.util.normalNavigation
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_item_animation_main.*

@Route(path = RouteString.ITEM_ANIMATION, name = "item动画展示汇总")
class ItemAnimationMainActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_item_animation_main

    override fun initView() {
        itemAnimation1.setOnClickListener {
            launchARouter(RouteString.ITEM_ANIMATION1).normalNavigation(this)
        }

        itemAnimation2.setOnClickListener {
            launchARouter(RouteString.ITEM_ANIMATION2).normalNavigation(this)
        }

        itemAnimation3.setOnClickListener {
            launchARouter(RouteString.ITEM_ANIMATION3).normalNavigation(this)
        }
    }

    override fun initData() {
    }

}
