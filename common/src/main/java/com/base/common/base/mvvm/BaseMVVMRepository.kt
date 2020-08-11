package com.base.common.base.mvvm

import com.base.common.util.AndroidUtil
import com.base.common.util.http.BaseBean
import com.base.common.util.http.CodeException
import com.base.common.util.http.NoNetworkException
import okhttp3.ResponseBody

abstract class BaseMVVMRepository {
    /**
     * 在这里进行网络请求统一处理，可以判断网络，统一bean格式，code判断等
     * 根据需求将T替换成对应基类，判断code
     * 根据需求抛出不同的异常
     */
    inline fun <T> requestNetwork(call: () -> BaseBean<T>): T {
        if (AndroidUtil.isNetworkAvailable()) {
            val bean = call.invoke()
            if (bean.code == 200) {
                return call.invoke().data
            } else {
                throw CodeException("${bean.code}")
            }
        } else {
            throw NoNetworkException("No network")
        }
    }

    /**
     * 返回值为okhttp原始ResponseBody的网络请求，主要用于下载
     */
    inline fun <T : ResponseBody> requestNetworkBase(call: () -> T): T {
        if (AndroidUtil.isNetworkAvailable()) {
            return call.invoke()
        } else {
            throw NoNetworkException("No network")
        }
    }
}