package com.base.common.base.mvp

import com.base.common.base.BaseActivity
import com.base.common.base.BaseFragment
import com.base.common.util.AndroidUtil
import com.base.common.util.LogUtil
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * CoroutineScope
 * GlobalScope  用于启动全局协程
 * MainScope
 * Dispatchers  运行线程切换
 *
 * runBlocking()
 *
 * by CoroutineScope(Dispatchers.Main)
 */
abstract class BasePresenterImpl<V, M>(protected var view: V?, protected var model: M) : CoroutineScope by MainScope() {

    protected inline fun <V> runTask(
        crossinline bgAction: suspend () -> V,
        noinline uiAction: ((V) -> Unit)? = null
    ): Job {
        val job = launch {
            try {
                val v = withContext(Dispatchers.IO) { bgAction() }
                uiAction?.invoke(v)
            } catch (e: Exception) {
                errorMessage(e)
            } finally {
            }
        }
        return job
    }

    protected inline fun <V> runTaskDialog(
        crossinline bgAction: suspend () -> V,
        noinline uiAction: ((V) -> Unit)? = null
    ): Job {
        val job = launch {
            val activity = getBaseActivity()
            activity?.showLoadDialog()
            try {
                delay(1200)
                val v = withContext(Dispatchers.IO) { bgAction() }
                uiAction?.invoke(v)
            } catch (e: Exception) {
                errorMessage(e)
            } finally {
                activity?.hideLoadDialog()
            }
        }
        return job
    }

    /**
     * 这个方法必须 public ,否则无法内联会崩溃
     */
    fun getBaseActivity(): BaseActivity? {
        return if (view is BaseFragment) {
            (view as BaseFragment).activity as? BaseActivity
        } else {
            view as? BaseActivity
        }
    }

    /**
     * 这个方法必须 public ,否则无法内联会崩溃
     */
    fun errorMessage(e: Exception) {
        LogUtil.log("BasePresenterImpl", "errorMessage $e")
        when (e) {
            is SocketTimeoutException -> AndroidUtil.showToast("连接超时")
            is UnknownHostException -> AndroidUtil.showToast("网络错误")
            is MalformedJsonException -> AndroidUtil.showToast("json解析错误")
            is CancellationException -> LogUtil.log("BasePresenterImpl", "errorMessage 协程被取消")
            else -> AndroidUtil.showToast("未知错误")
        }
    }

    fun detachView() {
        cancel()
        view = null
        LogUtil.log("BasePresenterImpl", "detachView")
    }
}