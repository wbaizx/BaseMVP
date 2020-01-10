package com.basemvp.main.fragment_example

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import kotlinx.android.synthetic.main.activity_fragment_example.*

@Route(path = RouteString.FRAMENT_EXAMPLE, name = "Fragment展示汇总")
class FragmentExampleActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_fragment_example

    override fun initView() {
        vp2Fragment.setOnClickListener {
            ARouter.getInstance().build(RouteString.VP_FRAMENT).navigation()
        }

        showFragment.setOnClickListener {
            ARouter.getInstance().build(RouteString.SHOW_FRAMENT).navigation()
        }
    }

    override fun initData() {
    }

}
