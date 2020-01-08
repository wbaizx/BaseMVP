package com.basemvp.main.item_animation2

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import kotlinx.android.synthetic.main.activity_list_transition_animation.*


@Route(path = RouteString.RECYCLEVIEW_TRANSITION, name = "recyclerView布局切换动画")
class RecyclerViewTransitionActivity : BaseActivity() {
    private var flag = true

    override fun getContentView() = R.layout.activity_list_transition_animation

    override fun initView() {
        val adapter = SceneAdapter()
        val layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator().apply { changeDuration = 2000 }

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)

        switchBtn.setOnClickListener {
            if (flag) {
                adapter.setLine(false)
                layoutManager.orientation = RecyclerView.VERTICAL
                layoutManager.spanCount = 2
            } else {
                adapter.setLine(true)
                layoutManager.orientation = RecyclerView.HORIZONTAL
                layoutManager.spanCount = 1
            }
            flag = !flag
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }

    override fun initData() {
    }
}
