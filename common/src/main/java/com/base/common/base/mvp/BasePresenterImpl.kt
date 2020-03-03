package com.base.common.base.mvp

import com.base.common.base.BaseActivity
import com.base.common.base.BaseFragment
import com.base.common.util.LogUtil
import kotlinx.coroutines.*

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
            } finally {
            }
        }
        return job
    }

    protected inline fun <V> launchTaskDialog(
        crossinline bgAction: suspend () -> V,
        noinline uiAction: ((V) -> Unit)? = null
    ): Job {
        val activity = if (view is BaseFragment) {
            (view as BaseFragment).activity as? BaseActivity
        } else {
            view as? BaseActivity
        }

        val job = launch {
            try {
                activity?.showLoadDialog()
                delay(2000)
                val v = withContext(Dispatchers.IO) { bgAction() }
                uiAction?.invoke(v)
            } catch (e: Exception) {
            } finally {
                activity?.hideLoadDialog()
            }
        }
        return job
    }

    fun detachView() {
        cancel()
        view = null
        model = null
        LogUtil.log("BasePresenterImpl", "detachView")
    }
}