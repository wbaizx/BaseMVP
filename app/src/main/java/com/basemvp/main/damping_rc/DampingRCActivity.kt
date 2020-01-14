package com.basemvp.main.damping_rc

import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.LogUtil
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_damping_rc.*


@Route(path = RouteString.DAMPING_RC, name = "带阻尼和标题栏，类似竖直viewpager的RecyclerView")
class DampingRCActivity : BaseActivity() {
    private val TAG = "DampingRCActivity"

    private val manager = DampingLinearLayoutManager(this)

    override fun getContentView() = R.layout.activity_damping_rc

    override fun setImmersionBar() {
        ImmersionBar.with(this)
            .statusBarColor(R.color.color_D1D)
            .fitsSystemWindows(true)
            .init()
    }

    override fun initView() {
        recyclerView.layoutManager = manager
        recyclerView.adapter = DampingRCAdapter()

//        val pagerSnapHelper = PagerSnapHelper()
//        val pagerSnapHelper2 = LinearSnapHelper()
//        pagerSnapHelper.attachToRecyclerView(list)
//        pagerSnapHelper2.attachToRecyclerView(list)
    }

    override fun initData() {
    }

}
