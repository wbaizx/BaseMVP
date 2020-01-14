package com.basemvp.main.damping_rc

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.basemvp.util.LogUtil

class DampingLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private val TAG = "DampingLinearLayoutManager"

    private val flingListener = object : RecyclerView.OnFlingListener() {
        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            return true
        }
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        LogUtil.log(TAG, "onAttachedToWindow")
        view?.onFlingListener = flingListener
        super.onAttachedToWindow(view)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val firstVisiblePosition = findFirstVisibleItemPosition()
        val positionView = findViewByPosition(firstVisiblePosition)
        LogUtil.log(TAG, "$firstVisiblePosition -- ${positionView?.top} -- ${positionView?.bottom}")
        return super.scrollVerticallyBy(dy, recycler, state)
    }
}