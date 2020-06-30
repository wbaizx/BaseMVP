package com.base.common.base.mvp

import com.base.common.util.AndroidUtil
import com.base.common.util.http.NoNetworkException

abstract class BaseModelImpl {
    /**
     * 在这里进行网络请求统一处理，可以判断网络，统一bean格式，code判断等
     * 根据需求将T替换成对应基类，判断code
     * 根据需求抛出不同的异常
     */
    inline fun <T> requestNetwork(call: () -> T): T {
        if (AndroidUtil.isNetworkAvailable()) {
            return call.invoke()
        } else {
            throw NoNetworkException("No network")
        }
    }
}