package com.base.common.extension

import android.view.View
import com.base.common.util.log
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 * view防止重复点击的扩展方法
 */
inline fun View.setOnAvoidRepeatedClick(crossinline event: (View) -> Unit) {
    var lastTime = 0L
    setOnClickListener {
        if (System.currentTimeMillis() - lastTime > 800L) {
            lastTime = System.currentTimeMillis()
            event(it)
        } else {
            log("avoidRepeated", "performClick false")
        }
    }
}

/**
 * BaseQuickAdapter防止重复点击的扩展方法
 */
inline fun BaseQuickAdapter<*, *>.setOnItemAvoidRepeatedClick(crossinline event: (BaseQuickAdapter<*, *>, View, Int) -> Unit) {
    var lastTime = 0L
    setOnItemClickListener { adapter, view, position ->
        if (System.currentTimeMillis() - lastTime > 800L) {
            lastTime = System.currentTimeMillis()
            event(adapter, view, position)
        } else {
            log("avoidRepeated", "performClick false")
        }
    }
}

/**
 * BaseQuickAdapter内子View防止重复点击的扩展方法
 */
inline fun BaseQuickAdapter<*, *>.setOnItemChildAvoidRepeatedClick(crossinline event: (BaseQuickAdapter<*, *>, View, Int) -> Unit) {
    var lastTime = 0L
    setOnItemChildClickListener { adapter, view, position ->
        if (System.currentTimeMillis() - lastTime > 800L) {
            lastTime = System.currentTimeMillis()
            event(adapter, view, position)
        } else {
            log("avoidRepeated", "performClick false")
        }
    }
}