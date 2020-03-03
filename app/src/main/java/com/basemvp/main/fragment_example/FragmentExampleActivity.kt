package com.basemvp.main.fragment_example

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.basemvp.R
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import kotlinx.android.synthetic.main.activity_fragment_example.*

@Route(path = RouteString.FRAGMENT_EXAMPLE, name = "Fragment展示汇总")
class FragmentExampleActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_fragment_example

    override fun initView() {
        vp2Fragment.setOnClickListener {
            ARouter.getInstance().build(RouteString.VP_FRAGMENT).navigation()
        }

        showFragment.setOnClickListener {
            ARouter.getInstance().build(RouteString.SHOW_FRAGMENT).navigation()
        }
    }

    override fun initData() {
    }

}
