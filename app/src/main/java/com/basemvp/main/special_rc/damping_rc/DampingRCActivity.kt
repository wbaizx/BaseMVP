package com.basemvp.main.special_rc.damping_rc

import com.base.common.base.BaseActivity
import com.base.common.util.AndroidUtil
import com.base.common.util.log
import com.basemvp.R
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_damping_rc.*

/**
 * 需要注意全面屏和刘海屏适配
 */
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
            log(TAG, "Offset  $upOffset --- $downOffset")
            footer.translationY = -upOffset
            header.translationY = downOffset
        }

        manager.setPageListener { page ->
            footer.text = "$page footer"
            header.text = "$page header"
            log(TAG, "Page  $page")
        }
    }

    override fun initData() {

    }

}
