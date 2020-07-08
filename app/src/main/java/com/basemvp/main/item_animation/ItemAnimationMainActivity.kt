package com.basemvp.main.item_animation

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.normalNavigation
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_item_animation_main.*

@Route(path = RouteString.ITEM_ANIMATION, name = "item动画展示汇总")
class ItemAnimationMainActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_item_animation_main

    override fun initView() {
        itemAnimation1.setOnClickListener {
            ARouter.getInstance().build(RouteString.ITEM_ANIMATION1).normalNavigation()
        }

        itemAnimation2.setOnClickListener {
            ARouter.getInstance().build(RouteString.ITEM_ANIMATION2).normalNavigation()
        }

        itemAnimation3.setOnClickListener {
            ARouter.getInstance().build(RouteString.ITEM_ANIMATION3).normalNavigation()
        }
    }

    override fun initData() {
    }

}
