package com.base.common.util

import android.util.Log
import com.base.common.BaseAPP

fun log(tag: String, any: Any?) {
    if (BaseAPP.isDebug()) {
        Log.e(tag, "thread name=${Thread.currentThread().name} -> ${any.toString()}")
    }
}