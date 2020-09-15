package com.basemvp.main.coordinator

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.launchARouter
import com.base.common.util.normalNavigation
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_coordinator.*

@Route(path = RouteString.COORDINATOR, name = "折叠滚动布局展示汇总")
class CoordinatorActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_coordinator

    override fun initView() {
        coordinator1.setOnClickListener {
            launchARouter(RouteString.COORDINATOR1).normalNavigation(this)
        }
    }

    override fun initData() {
    }

}
