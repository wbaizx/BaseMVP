package com.base.common.extension

import android.view.View
import com.base.common.util.LogUtil

/**
 * view防止重复点击的扩展方法
 */
inline fun View.setOnAvoidRepeatedClickListener(crossinline event: (View) -> Unit) {
    var lastTime = 0L
    setOnClickListener {
        if (System.currentTimeMillis() - lastTime > 800L) {
            lastTime = System.currentTimeMillis()
            event(it)
        } else {
            LogUtil.log("avoidRepeated", "performClick false")
        }
    }
}