package com.basemvp.util

import android.util.TypedValue
import com.basemvp.APP

object AndroidUtil {
    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        return APP.appContext.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(): Int {
        return APP.appContext.resources.displayMetrics.heightPixels
    }

    fun sp2px(f: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, f, APP.appContext.resources.displayMetrics)

    fun dp2px(f: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, APP.appContext.resources.displayMetrics)
}