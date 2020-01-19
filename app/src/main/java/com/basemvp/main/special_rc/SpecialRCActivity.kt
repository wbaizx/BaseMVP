package com.basemvp.main.special_rc

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import kotlinx.android.synthetic.main.activity_special_rc.*

@Route(path = RouteString.SPECIAL_RC, name = "各种特殊效果的recycleView效果展示汇总")
class SpecialRCActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_special_rc

    override fun initView() {
        dampingRc.setOnClickListener {
            ARouter.getInstance().build(RouteString.DAMPING_RC).navigation()
        }

        recyclerViewGallery.setOnClickListener {
            ARouter.getInstance().build(RouteString.GALLERY).navigation()
        }

        scrollToRecyclerView.setOnClickListener {
            ARouter.getInstance().build(RouteString.SCROLL_TO_RC).navigation()
        }
    }

    override fun initData() {
    }

}
