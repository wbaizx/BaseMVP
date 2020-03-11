package com.base.common.base.mvp

import com.base.common.base.BaseActivity
import com.base.common.base.BaseFragment
import com.base.common.util.LogUtil
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * CoroutineScope
 * GlobalScope
 * MainScope
 * Dispatchers
 *
 * runBlocking()
 */
abstract class BasePresenterImpl<V, M>(protected var view: V?, protected var model: M?) :
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    protected inline fun <V> launchTask(
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

    protected inline fun <V> launchTaskDialog(
        crossinline bgAction: suspend () -> V,
        noinline uiAction: ((V) -> Unit)? = null
    ): Job {
        val activity = getBaseActivity()

        val job = launch {
            try {
                activity?.showLoadDialog()
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

    protected fun getBaseActivity(): BaseActivity? {
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
        LogUtil.log("BasePresenterImpl", e.toString())
        val activity = getBaseActivity()
        when (e) {
            is SocketTimeoutException -> activity?.showToast("连接超时")
            is UnknownHostException -> activity?.showToast("网络错误")
            else -> activity?.showToast("未知错误")
        }
    }

    fun detachView() {
        cancel()
        view = null
        model = null
        LogUtil.log("BasePresenterImpl", "detachView")
    }
}