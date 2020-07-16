package com.basemvp.main.item_animation.item_animation2

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.basemvp.R
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
        recyclerView.itemAnimator = MyDefaultItemAnimator().apply {
            changeDuration = 600
            addDuration = 600
        }


        change.setOnClickListener {
            if (isLine) {
                adapter.setLine(false)
                layoutManager.orientation = GridLayoutManager.VERTICAL
                layoutManager.spanCount = 2
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            } else {
                adapter.setLine(true)
                layoutManager.orientation = GridLayoutManager.HORIZONTAL
                layoutManager.spanCount = 1
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }
            isLine = !isLine

        }
    }

    override fun initData() {
    }
}
