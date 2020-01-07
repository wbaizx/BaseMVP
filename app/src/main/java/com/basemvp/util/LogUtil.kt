package com.basemvp.util

import android.util.Log

object LogUtil {
    fun log(tag: String, any: Any?) {
        Log.e(tag, "${Thread.currentThread().name} -- ${any.toString()}")
    }
}