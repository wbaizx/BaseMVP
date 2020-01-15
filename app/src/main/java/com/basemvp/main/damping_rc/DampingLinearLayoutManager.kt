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

    var barHeight = AndroidUtil.dp2px(80f)

    //最终滚动标志，在最终滚动中，不记录偏移量
    private var isFinalScroll = false
    private var upOffset = 0f
    private var downOffset = 0f

    private var listener: ((Float, Float) -> Unit)? = null


    private val flingListener = object : RecyclerView.OnFlingListener() {
        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            LogUtil.log(TAG, "onFling $velocityY  --  ${parentView.minFlingVelocity}")
            //允许惯性滚动
            //scrollVerticallyBy方法中计算出来的阻尼滚动距离如果与目标距离不一致，惯性滚动就会停止
            return false
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    topView = findViewByPosition(findFirstVisibleItemPosition())
                    bottomView = findViewByPosition(findLastVisibleItemPosition())
                    LogUtil.log(TAG, "newState ${findFirstVisibleItemPosition()}--${findLastVisibleItemPosition()}")

                    //手指触发一次新的滑动，需要重置最终滚动标准
                    isFinalScroll = false

                    //重置偏移量
                    upOffset = 0f
                    downOffset = 0f
                    listener?.invoke(upOffset, downOffset)
                }
                RecyclerView.SCROLL_STATE_SETTLING -> {
                }
                RecyclerView.SCROLL_STATE_IDLE -> {
                    LogUtil.log(TAG, "newState $isFinalScroll")
                    if (isFinalScroll) {
                        isFinalScroll = false
                    } else {
                        finalScroll()
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun finalScroll() {
        LogUtil.log(TAG, "finalScroll")
        if (upOffset > barHeight / 5 * 3) {
            //需要滚动到下一条数据
            upOffset = 0f
            downOffset = 0f
            isFinalScroll = true
            val childPos = parentView.getChildAdapterPosition(bottomView!!)
            LogUtil.log(TAG, "getChildAdapterPosition  up $childPos")
            toTopAlignedScroll(childPos + 1)
        } else if (downOffset > barHeight / 5 * 3) {
            //需要滚动到上一条数据
            upOffset = 0f
            downOffset = 0f
            isFinalScroll = true
            val childPos = parentView.getChildAdapterPosition(topView!!)
            LogUtil.log(TAG, "getChildAdapterPosition  down $childPos")
            toTopAlignedScroll(childPos - 1)
        } else {
            LogUtil.log(TAG, "Align")
            val nowTopViewPos = findFirstVisibleItemPosition()
            val nowBottomViewPos = findLastVisibleItemPosition()
            if (nowTopViewPos != nowBottomViewPos) {
                LogUtil.log(TAG, "no equal Pos")
                val nowBottomView = findViewByPosition(nowBottomViewPos)

                //根据两个item交点位置，做最后滚动
                if (nowBottomView!!.top < parentView.height / 2) {
                    toTopAlignedScroll(nowBottomViewPos)
                } else {
                    toDownAlignedScroll(nowTopViewPos)
                }
            }
        }

        //回调偏移量
        listener?.invoke(upOffset, downOffset)
    }

    private fun toTopAlignedScroll(pos: Int) {
        LogUtil.log(TAG, "TopAligned")
        val top = findViewByPosition(pos)!!.top
        parentView.smoothScrollBy(0, top)
    }

    private fun toDownAlignedScroll(pos: Int) {
        LogUtil.log(TAG, "DownAligned")
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
        LogUtil.log(TAG, "scrollVerticallyBy start $finallyDy")
        if (!isFinalScroll) {
            if (dy > 0) {  //向上滑动
                val bottomOffset = bottomView!!.bottom - parentView.height
                LogUtil.log(TAG, "scrollVerticallyBy up  $bottomOffset")
                if (bottomOffset - dy <= 0) {
                    if (bottomOffset <= 0) {
                        //已经超出
                        finallyDy = calculationOffset(dy, bottomOffset)
                        //累加向上的偏移量
                        upOffset += finallyDy
                        LogUtil.log(TAG, "scrollVerticallyBy already  $upOffset")
                    } else {
                        //滑动后将超出
                        finallyDy = calculationOffset(dy, dy - bottomOffset) + bottomOffset
                        //累加向上的偏移量
                        upOffset += finallyDy - bottomOffset
                        LogUtil.log(TAG, "scrollVerticallyBy will $upOffset")
                    }
                }

                //消除向下的偏移量
                downOffset = max(downOffset - finallyDy, 0f)
            } else { //向下滑动
                val topOffset = topView!!.top
                LogUtil.log(TAG, "scrollVerticallyBy down  $topOffset")
                //注意dy为负值
                if (topOffset - dy >= 0) {
                    if (topOffset >= 0) {
                        finallyDy = -calculationOffset(dy, topOffset)
                        //注意 finallyDy 为负值
                        downOffset -= finallyDy
                        LogUtil.log(TAG, "scrollVerticallyBy already  $downOffset")
                    } else {
                        finallyDy = -calculationOffset(dy, dy - topOffset) + topOffset
                        //累加向上的偏移量
                        downOffset -= finallyDy - topOffset
                        LogUtil.log(TAG, "scrollVerticallyBy will $downOffset")
                    }
                }

                //消除向上的偏移量
                upOffset = max(upOffset + finallyDy, 0f)
            }
            //回调偏移量
            listener?.invoke(upOffset, downOffset)
        }
        return super.scrollVerticallyBy(finallyDy, recycler, state)
    }

    /**
     * 根据偏移量计算偏移距离
     */
    private fun calculationOffset(dy: Int, bottomOffset: Int): Int {
        val heightScale = min(abs(bottomOffset / barHeight), 1f)
        val finallyDy = abs(dy * (1f - heightScale))
        LogUtil.log(TAG, "calculationOffset  $finallyDy")
        return finallyDy.toInt()
    }

    fun setOffsetListener(listener: (Float, Float) -> Unit) {
        this.listener = listener
    }
}