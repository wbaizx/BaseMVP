package com.basemvp.main.item_animation.item_animation2

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import kotlinx.android.synthetic.main.activity_item_animation2.*


@Route(path = RouteString.ITEM_ANIMATION2, name = "recyclerView布局切换item动画")
class ItemAnimation2Activity : BaseActivity() {
    private var isLine = true

    override fun getContentView() = R.layout.activity_item_animation2

    override fun initView() {
        val adapter = ItemAnimation2Adapter()
        val layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator().apply { changeDuration = 2000 }

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)

        switchBtn.setOnClickListener {
            if (isLine) {
                adapter.setLine(false)
                layoutManager.orientation = GridLayoutManager.VERTICAL
                layoutManager.spanCount = 2
            } else {
                adapter.setLine(true)
                layoutManager.orientation = GridLayoutManager.HORIZONTAL
                layoutManager.spanCount = 1
            }
            isLine = !isLine
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }

    override fun initData() {
    }
}
