package com.basemvp.main.special_rc

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.launchARouter
import com.base.common.util.loginNavigation
import com.base.common.util.normalNavigation
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_special_rc.*

@Route(path = RouteString.SPECIAL_RC, name = "各种特殊效果的recycleView效果展示汇总")
class SpecialRCActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_special_rc

    override fun initView() {
        dampingRc.setOnClickListener {
            launchARouter(RouteString.DAMPING_RC).normalNavigation(this)
        }

        recyclerViewGallery.setOnClickListener {
            launchARouter(RouteString.GALLERY).normalNavigation(this)
        }

        scrollToRecyclerView.setOnClickListener {
            launchARouter(RouteString.SCROLL_TO_RC).normalNavigation(this)
        }

        qqAlbum.setOnClickListener {
            launchARouter(RouteString.QQ_ALBUM).normalNavigation(this)
        }

        connectionRecyclerView.setOnClickListener {
            launchARouter(RouteString.CONNECTION_RC).loginNavigation(this)
        }

        overlappingRecyclerView.setOnClickListener {
            launchARouter(RouteString.CIRCLE_RC).loginNavigation(this)
        }

        pictureIndicator.setOnClickListener {
            launchARouter(RouteString.PICTURE_INDICATOR).loginNavigation(this)
        }
    }

    override fun initData() {
    }

}
