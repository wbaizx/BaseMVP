package com.basemvp.main.special_rc.damping_rc

import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.AndroidUtil
import com.basemvp.util.LogUtil
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_damping_rc.*

/**
 * 需要注意全面屏和刘海屏适配
 */
@Route(path = RouteString.DAMPING_RC, name = "带阻尼和标题栏，类似竖直viewpager的RecyclerView item必须至少占全屏")
class DampingRCActivity : BaseActivity() {
    private val TAG = "DampingRCActivity"

    private val manager = DampingLinearLayoutManager(this)

    override fun getContentView() = R.layout.activity_damping_rc

    override fun setImmersionBar() {
        ImmersionBar.with(this)
//            .statusBarColor(R.color.color_black)
//            .fitsSystemWindows(true)
            .init()
    }

    override fun initView() {
        recyclerView.layoutManager = manager
        recyclerView.adapter = DampingRCAdapter(this)

        manager.barHeight = AndroidUtil.dp2px(80f)

        manager.setOffsetListener { upOffset, downOffset ->
            //upOffset 控制脚部偏移
            //downOffset 控制头部偏移
            LogUtil.log(TAG, "Offset  $upOffset --- $downOffset")
            footer.translationY = -upOffset
            header.translationY = downOffset
        }

        manager.setPageListener { page ->
            footer.text = "$page footer"
            header.text = "$page header"
            LogUtil.log(TAG, "Page  $page")
        }
    }

    override fun initData() {

    }

}
