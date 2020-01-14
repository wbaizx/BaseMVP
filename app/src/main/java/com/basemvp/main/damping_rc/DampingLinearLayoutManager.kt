package com.basemvp.main.damping_rc

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.basemvp.util.AndroidUtil
import com.basemvp.util.LogUtil
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DampingLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private val TAG = "DampingLinearLayoutManager"

    private lateinit var parentView: RecyclerView

    private var topView: View? = null
    private var bottomView: View? = null

    private val barHeight = AndroidUtil.dp2px(80f)
    private var isFinalScroll = false
    private var upOffset = 0
    private var downOffset = 0


    private val flingListener = object : RecyclerView.OnFlingListener() {
        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            LogUtil.log(TAG, "onFling $velocityY  5000")
            return true
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    topView = findViewByPosition(findFirstVisibleItemPosition())
                    bottomView = findViewByPosition(findLastVisibleItemPosition())
                }
                RecyclerView.SCROLL_STATE_SETTLING -> {
                }
                RecyclerView.SCROLL_STATE_IDLE -> {
                    if (isFinalScroll) {
                        isFinalScroll = false
                    } else {
                        finalScroll()
                    }
                }
            }
            LogUtil.log(TAG, "onScrollStateChanged $newState")
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun finalScroll() {
        LogUtil.log(TAG, "finalScroll")
        if (upOffset > barHeight / 5 * 3) {
            //需要滚动到下一条数据
            if (bottomView != null) {
                val childPos = parentView.getChildAdapterPosition(bottomView!!)
                LogUtil.log(TAG, "getChildAdapterPosition  up $childPos")
                toTopAlignedScroll(childPos + 1)
            }
        } else if (downOffset > barHeight / 5 * 3) {
            //需要滚动到上一条数据
            val childPos = parentView.getChildAdapterPosition(topView!!)
            LogUtil.log(TAG, "getChildAdapterPosition  down $childPos")
            toTopAlignedScroll(childPos - 1)
        } else {
            if (upOffset > 0) {
                val childPos = parentView.getChildAdapterPosition(topView!!)
                toDownAlignedScroll(childPos)
            } else if (downOffset > 0) {
                val childPos = parentView.getChildAdapterPosition(bottomView!!)
                toTopAlignedScroll(childPos)
            }
        }

        upOffset = 0
        downOffset = 0
    }

    private fun toTopAlignedScroll(pos: Int) {
        isFinalScroll = true
        val top = findViewByPosition(pos)!!.top
        parentView.smoothScrollBy(0, top)
    }

    private fun toDownAlignedScroll(pos: Int) {
        isFinalScroll = true
        val bottom = findViewByPosition(pos)!!.bottom - parentView.height
        parentView.smoothScrollBy(0, bottom)
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)

        view.onFlingListener = flingListener
        view.addOnScrollListener(scrollListener)

        parentView = view
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        var finallyDy = dy
        if (!isFinalScroll) {
            if (dy > 0) {  //向上滑动
                LogUtil.log(TAG, "scrollVerticallyBy up")
                val bv = bottomView!!
                val bottomOffset = bv.bottom - parentView.height
                LogUtil.log(TAG, "----------    $upOffset  ** $bottomOffset")
                if (bottomOffset < 0) {
                    finallyDy = calculationOffset(dy, bottomOffset)
                    //累加向上的偏移量
                    upOffset += finallyDy
                    LogUtil.log(TAG, "scrollVerticallyBy up  $upOffset")
                }

                //消除向下的偏移量
                downOffset = max(downOffset - finallyDy, 0)
            } else { //向下滑动
                LogUtil.log(TAG, "scrollVerticallyBy down")
                val tv = topView!!
                val topOffset = tv.top
                if (topOffset > 0) {
                    finallyDy = -calculationOffset(dy, topOffset)
                    //注意 finallyDy 为负值
                    downOffset -= finallyDy
                    LogUtil.log(TAG, "scrollVerticallyBy down  $downOffset")
                }

                //消除向上的偏移量
                upOffset = max(upOffset + finallyDy, 0)
            }
        }
        return super.scrollVerticallyBy(finallyDy, recycler, state)
    }

    /**
     * 根据偏移量计算偏移距离
     */
    private fun calculationOffset(dy: Int, bottomOffset: Int): Int {
        val heightScale = min(abs(bottomOffset / barHeight), 1f)
        val finallyDy = abs(dy * (1f - heightScale))
        return finallyDy.toInt()
    }
}