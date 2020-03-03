package com.basemvp.main.special_rc.scrollto_rc

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.LogUtil
import kotlinx.android.synthetic.main.activity_scroll_to_rc.*

@Route(path = RouteString.SCROLL_TO_RC, name = "recyclerView滚动到指定位置")
class ScrollToRCActivity : BaseActivity() {
    private val TAG = "ScrollToRCActivity"

    private val manager = LinearLayoutManager(this)
    private val adapter = ScrollToRCAdapter()
    private val decoration = ScrollToRCDecoration(adapter)

    override fun getContentView() = R.layout.activity_scroll_to_rc

    override fun initView() {
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(decoration)

        tabLayoutBind()
    }

    override fun initData() {
        val data = arrayListOf<String>()
        repeat(20) {
            data.add("第 $it 个")
        }
        adapter.setNewData(data)
        simpleTabLayout.setData(data)
    }

    private fun tabLayoutBind() {
        var needOverseeScroll = true
        var move = false
        var toPos = -1

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        move = false
                        needOverseeScroll = true
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (!move) {
                            needOverseeScroll = true
                        }
                    }
                }
                LogUtil.log(TAG, "onScrollStateChanged $newState")
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (needOverseeScroll) {
                    simpleTabLayout.setPosition(manager.findFirstVisibleItemPosition())
                } else {
                    if (move && manager.findLastVisibleItemPosition() == toPos) {
                        recyclerView.stopScroll()

                        move = false
                        //要置顶的项已经在屏幕上
                        val top = manager.findViewByPosition(toPos)!!.top - decoration.titleHeight
                        recyclerView.smoothScrollBy(0, top)
                    }
                }
            }
        })

        simpleTabLayout.setListener { pos ->
            needOverseeScroll = false

            val first = manager.findFirstVisibleItemPosition()
            val last = manager.findLastVisibleItemPosition()
            if (pos < first) {
                //当要置顶的项在当前显示的第一个项的前面时
                recyclerView.smoothScrollToPosition(pos)
            } else if (pos > last) {
                //这两变量是用在RecyclerView滚动监听里面的
                move = true
                toPos = pos
                //当要置顶的项在当前显示的最后一项的后面时
                recyclerView.smoothScrollToPosition(pos)
            } else {
                //当要置顶的项已经在屏幕上显示时
                val top = manager.findViewByPosition(pos)!!.top - decoration.titleHeight
                recyclerView.smoothScrollBy(0, top)
            }
        }
    }
}
