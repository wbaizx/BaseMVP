package com.basemvp.main.coordinator.coordinator1

import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.LogUtil
import com.basemvp.R
import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_coordinator1.*
import kotlin.math.abs

/**
 * 注意!
 * 折叠布局下方的滚动子View如果是嵌套recyclerView，会影响折叠布局滚动顺序逻辑
 * 后面研究下解决这个问题
 */
@Route(path = RouteString.COORDINATOR1, name = "折叠滚动布局第一种 coordinator1")
class Coordinator1Activity : BaseActivity() {
    private val TAG = "Coordinator1Activity"

    override fun getContentView() = R.layout.activity_coordinator1

    override fun setImmersionBar() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .init()
    }

    override fun initView() {
        val coordinator1Adapter = Coordinator1Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(Coordinator1Decoration(coordinator1Adapter))
        recyclerView.adapter = coordinator1Adapter

        val headerView: View = LayoutInflater.from(this).inflate(
            R.layout.default_header_layout,
            recyclerView,
            false
        )
        coordinator1Adapter.addHeaderView(headerView)

        appBar.post {
            val layoutParams = appBar.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as AppBarLayout.Behavior
            behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return true
                }
            })
        }

        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, offset ->
            val off = abs(offset.toFloat())
            toolbar.alpha = off / appBar.totalScrollRange
            testArea.y = appBar.totalScrollRange.toFloat() + toolbar.paddingTop
            testArea.translationX = -off
            LogUtil.log(TAG, "addOnOffsetChangedListener $off  -  ${appBar.totalScrollRange}")
        })

        toolbarImg.setOnClickListener {
            LogUtil.log(TAG, "toolbarImg click")
        }

        testArea.setOnClickListener {
            LogUtil.log(TAG, "testArea click")
        }
    }

    override fun initData() {
    }
}
