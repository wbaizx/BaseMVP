package com.basemvp.main.coordinator

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.R
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import kotlinx.android.synthetic.main.activity_coordinator.*

@Route(path = RouteString.COORDINATOR, name = "折叠滚动布局展示汇总")
class CoordinatorActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_coordinator

    override fun initView() {
        coordinator1.setOnClickListener {
            ARouter.getInstance().build(RouteString.COORDINATOR1).navigation()
        }
    }

    override fun initData() {
    }

}
