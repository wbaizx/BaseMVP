package com.base.common.util

import android.util.Log

object LogUtil {
    fun log(tag: String, any: Any?) {
        Log.e(tag, "name=${Thread.currentThread().name} -> ${any.toString()}")
    }
}