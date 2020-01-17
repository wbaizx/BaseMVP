package com.basemvp.main.damping_rc

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.basemvp.util.LogUtil

class DampingRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
    private val TAG = "DampingRecyclerView"

    //用于控制屏蔽列表点击和拖动事件
    private var blockClicks = false

    /**
     * 返回true表示自己消费，后续事件还会传来，所以返回false交给父控件
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        LogUtil.log(TAG, "dispatchTouchEvent $blockClicks")
        if (blockClicks) {
            return false
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setBlockClicks(blockClicks: Boolean) {
        this.blockClicks = blockClicks
    }
}