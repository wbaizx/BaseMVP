package com.basemvp.main.fragment_example

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.launchARouter
import com.base.common.util.normalNavigation
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_fragment_example.*

@Route(path = RouteString.FRAGMENT_EXAMPLE, name = "Fragment展示汇总")
class FragmentExampleActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_fragment_example

    override fun initView() {
        vp2Fragment.setOnClickListener {
            launchARouter(RouteString.VP_FRAGMENT).normalNavigation(this)
        }

        showFragment.setOnClickListener {
            launchARouter(RouteString.SHOW_FRAGMENT).normalNavigation(this)
        }
    }

    override fun initData() {
    }

}
