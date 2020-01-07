package com.basemvp.main.coordinator1

import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_coordinator1.*

@Route(path = RouteString.COORDINATOR1, name = "折叠滚动布局 coordinator1")
class Coordinator1Activity : BaseActivity() {

    override fun getContentView() = R.layout.activity_coordinator1

    override fun setImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.toolbar).init()
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
        val headerView2: View = LayoutInflater.from(this).inflate(
            R.layout.default_header_layout,
            recyclerView,
            false
        )
        coordinator1Adapter.addHeaderView(headerView)
        coordinator1Adapter.addHeaderView(headerView2)


        appBar.post {
            val layoutParams = appBar.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as AppBarLayout.Behavior
            behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return true
                }
            })
        }
    }

    override fun initData() {
    }
}
