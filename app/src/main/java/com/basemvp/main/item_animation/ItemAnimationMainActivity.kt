package com.basemvp.main.item_animation

import com.base.common.base.BaseActivity
import com.base.common.util.launchActivity
import com.basemvp.R
import com.basemvp.main.item_animation.item_animation1.ItemAnimation1Activity
import com.basemvp.main.item_animation.item_animation2.ItemAnimation2Activity
import com.basemvp.main.item_animation.item_animation3.ItemAnimation3Activity
import kotlinx.android.synthetic.main.activity_item_animation_main.*

class ItemAnimationMainActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_item_animation_main

    override fun initView() {
        itemAnimation1.setOnClickListener {
            launchActivity(this, ItemAnimation1Activity::class.java)
        }

        itemAnimation2.setOnClickListener {
            launchActivity(this, ItemAnimation2Activity::class.java)
        }

        itemAnimation3.setOnClickListener {
            launchActivity(this, ItemAnimation3Activity::class.java)
        }
    }

    override fun initData() {
    }

}
