package com.basemvp.main.damping_rc

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.LogUtil
import kotlinx.android.synthetic.main.activity_damping_rc.*


@Route(path = RouteString.DAMPING_RC, name = "带阻尼和标题栏，类似竖直viewpager的RecyclerView")
class DampingRCActivity : BaseActivity() {
    private val TAG = "DampingRCActivity"

    private var first = 0
    private var last = 0
    private var move = true

    private val manager = LinearLayoutManager(this)

    override fun getContentView() = R.layout.activity_damping_rc

    override fun initView() {
        list.layoutManager = manager
        list.adapter = DampingRCAdapter()

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(list)

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        move = true
                        first = manager.findFirstVisibleItemPosition()
                        last = manager.findLastVisibleItemPosition()
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                    }
                }
                LogUtil.log(TAG, "onScrollStateChanged $newState")
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                LogUtil.log(TAG, "onScrolled $dy")
                if (move) {
                    if (dy < 0) {//下拉
//                        header.translationY += -dy
                    } else {
                        val lastL = manager.findLastVisibleItemPosition()
                        if (lastL != last) {
                            if ((-footer.translationY + dy) >= footer.height) {
                                footer.translationY = 0f
                                move = false
                            } else {
                                footer.translationY -= dy
                            }
                        }

                    }
                }


            }
        })
    }

    override fun initData() {
    }

}
