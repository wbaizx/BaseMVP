package com.basemvp.base.mvp

import com.basemvp.base.BaseActivity
import com.basemvp.util.LogUtil
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
        val activity = view as? BaseActivity
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