package com.basemvp.main.special_rc.damping_rc

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.common.util.AndroidUtil
import com.base.common.util.LogUtil
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * item必须至少占全屏!!!!
 * 注意全面屏和刘海屏适配!!!
 */
class DampingLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private val TAG = "DampingLinearLayoutManager"

    private lateinit var parentView: DampingRecyclerView

    private var topView: View? = null
    private var bottomView: View? = null

    var barHeight = AndroidUtil.dp2px(80f)

    //最终滚动标志，在最终滚动中，不记录偏移量
    private var isFinalScroll = false
    private var upOffset = 0f
    private var downOffset = 0f

    /**
     * 是否允许item之间的滑动，这个标识可以用于禁用或放开item之间的滚动权限
     * 一般用于比较耗时的网络请求，需要再滚动完毕禁用，数据请求完毕放开
     * 需注意如果列表因数据动态改变高度，在放开权限时可以适当延迟，或者改变RecyclerView动画时间
     */
    private var itemSliding = true

    private var offsetListener: ((Float, Float) -> Unit)? = null
    private var pageListener: ((Int) -> Unit)? = null

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
            LogUtil.log(TAG, "scrollChanged newState $newState")
            when (newState) {
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    topView = findViewByPosition(findFirstVisibleItemPosition())
                    bottomView = findViewByPosition(findLastVisibleItemPosition())
                    LogUtil.log(TAG, "scrollChanged pos ${findFirstVisibleItemPosition()}--${findLastVisibleItemPosition()}")

                    //手指触发一次新的滑动，需要重置最终滚动标志
                    isFinalScroll = false

                    //重置偏移量
                    upOffset = 0f
                    downOffset = 0f
                    offsetListener?.invoke(upOffset, downOffset)
                }
                RecyclerView.SCROLL_STATE_SETTLING -> {
                    //只有在惯性滑动中，并且屏幕中同时存在大于1个item时才需要屏手指拖动事件
                    if (findFirstVisibleItemPosition() != findLastVisibleItemPosition()) {
                        parentView.setBlockClicks(true)
                    }
                }
                RecyclerView.SCROLL_STATE_IDLE -> {
                    //当前页码监听回调
                    val nowTopViewPos = findFirstVisibleItemPosition()
                    val nowBottomViewPos = findLastVisibleItemPosition()
                    if (nowTopViewPos == nowBottomViewPos) {
                        //某个item已经完全占据屏幕
                        pageListener?.invoke(nowTopViewPos)
                        parentView.setBlockClicks(false)
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
            isFinalScroll = true
            upOffset = 0f
            downOffset = 0f
            val childPos = findLastVisibleItemPosition()
            LogUtil.log(TAG, "getChildAdapterPosition  up $childPos")
            //之前采用的getChildAdapterPosition + 1 获取位置，但是有时为空
            //现在采用获取屏幕上对应显示的view的位置，因为偏移量出来时，要滚动到的那个view其实已经在屏幕上了
            toTopAlignedScroll(childPos)
        } else if (downOffset > barHeight / 5 * 3) {
            //需要滚动到上一条数据
            isFinalScroll = true
            upOffset = 0f
            downOffset = 0f
            val childPos = findFirstVisibleItemPosition()
            LogUtil.log(TAG, "getChildAdapterPosition  down $childPos")
            //之前采用的getChildAdapterPosition - 1 获取位置，但是有时为空
            //现在采用获取屏幕上对应显示的view的位置，因为偏移量出来时，要滚动到的那个view其实已经在屏幕上了
            toTopAlignedScroll(childPos)
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
        offsetListener?.invoke(upOffset, downOffset)
    }

    private fun toTopAlignedScroll(pos: Int) {
        LogUtil.log(TAG, "TopAligned $pos")
        val top = findViewByPosition(pos)!!.top
        parentView.smoothScrollBy(0, top)
    }

    private fun toDownAlignedScroll(pos: Int) {
        LogUtil.log(TAG, "DownAligned $pos")
        val bottom = findViewByPosition(pos)!!.bottom - parentView.height
        parentView.smoothScrollBy(0, bottom)
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)

        view.onFlingListener = flingListener
        view.addOnScrollListener(scrollListener)

        parentView = view as DampingRecyclerView
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        var finallyDy = dy
        LogUtil.log(TAG, "scrollVerticallyBy start $finallyDy")
        if (!isFinalScroll) {
            if (dy > 0) {//向上滑动
                if (bottomView != null) {//滚动状态可能不走1，导致view未赋值
                    val bottomOffset = bottomView!!.bottom - parentView.height
                    LogUtil.log(TAG, "scrollVerticallyBy up  $bottomOffset")
                    if (bottomOffset - dy < 0) {//此次滚动将会超出，也可能已经超出
                        if (itemSliding) {
                            //判断屏幕中是否已经只剩下最后一项，如果是则不需要再计算距离和偏移量
                            if (findFirstVisibleItemPosition() != parentView.adapter!!.itemCount - 1) {
                                if (bottomOffset <= 0) {
                                    //已经超出
                                    finallyDy = calculationOffset(dy, bottomOffset, upOffset)
                                    //累加向上的偏移量
                                    upOffset += finallyDy
                                    LogUtil.log(TAG, "scrollVerticallyBy already  $upOffset")
                                } else {
                                    //滑动后将超出
                                    if (findLastVisibleItemPosition() != parentView.adapter!!.itemCount - 1) {
                                        finallyDy = calculationOffset(dy, dy - bottomOffset, upOffset) + bottomOffset
                                        //累加向上的偏移量
                                        upOffset += finallyDy - bottomOffset
                                        LogUtil.log(TAG, "scrollVerticallyBy will $upOffset")
                                    } else {
                                        //屏幕最后一项已经是底部，此时不允许超出
                                        finallyDy = dy - bottomOffset
                                        LogUtil.log(TAG, "scrollVerticallyBy will stop $upOffset")
                                    }
                                }
                            }
                        } else {
                            finallyDy = 0
                        }
                    }

                    //消除向下的偏移量
                    downOffset = max(downOffset - finallyDy, 0f)
                }
            } else {//向下滑动
                if (topView != null) {//滚动状态可能不走1，导致view未赋值
                    val topOffset = topView!!.top
                    LogUtil.log(TAG, "scrollVerticallyBy down  $topOffset")
                    //注意dy为负值
                    if (topOffset - dy > 0) {//此次滚动将会超出，也可能已经超出
                        if (itemSliding) {
                            //判断屏幕中是否已经只剩下第一项，如果是则不需要再计算距离和偏移量
                            if (findLastVisibleItemPosition() != 0) {
                                if (topOffset >= 0) {
                                    //已经超出
                                    finallyDy = -calculationOffset(dy, topOffset, downOffset)
                                    //注意 finallyDy 为负值
                                    downOffset -= finallyDy
                                    LogUtil.log(TAG, "scrollVerticallyBy already  $downOffset")
                                } else {
                                    //滑动后将超出
                                    //此时topOffset为负值
                                    if (findFirstVisibleItemPosition() != 0) {
                                        finallyDy = -calculationOffset(dy, dy - topOffset, downOffset) + topOffset
                                        //累加向下的偏移量
                                        downOffset -= finallyDy - topOffset
                                        LogUtil.log(TAG, "scrollVerticallyBy will $downOffset")
                                    } else {
                                        //屏幕第一项已经是顶部，此时不允许超出
                                        finallyDy = dy - topOffset
                                        LogUtil.log(TAG, "scrollVerticallyBy will stop $downOffset")
                                    }
                                }
                            }
                        } else {
                            finallyDy = 0
                        }
                    }

                    //消除向上的偏移量
                    upOffset = max(upOffset + finallyDy, 0f)
                }
            }
            //回调偏移量
            offsetListener?.invoke(upOffset, downOffset)
        }
        return super.scrollVerticallyBy(finallyDy, recycler, state)
    }

    /**
     * 根据偏移量计算偏移距离
     */
    private fun calculationOffset(dy: Int, bottomOffset: Int, offset: Float): Int {
        val heightScale = min(abs(bottomOffset / barHeight), 1f)
        val distance = abs(dy * (1f - heightScale))
        //根据已有偏移量做最大值限制
        val finallyDy = min(distance, barHeight - offset)
        LogUtil.log(TAG, "calculationOffset  $finallyDy")
        return finallyDy.toInt()
    }

    fun setOffsetListener(listener: (Float, Float) -> Unit) {
        this.offsetListener = listener
    }

    fun setPageListener(listener: (Int) -> Unit) {
        this.pageListener = listener
    }

    fun setItemSliding(itemSliding: Boolean) {
        this.itemSliding = itemSliding
    }
}