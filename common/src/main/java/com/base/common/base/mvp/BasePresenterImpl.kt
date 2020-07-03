package com.base.common.base.mvp

import com.base.common.base.mvp.contract.BaseModel
import com.base.common.base.mvp.contract.BasePresenter
import com.base.common.base.mvp.contract.BaseView
import com.base.common.util.AndroidUtil
import com.base.common.util.LogUtil
import kotlinx.coroutines.*

/**
 * CoroutineScope
 * GlobalScope  用于启动全局协程
 * Dispatchers  运行线程切换
 *
 * by CoroutineScope(Dispatchers.Main) 用这种方式async会有bug，所以用 by MainScope()方式
 *
 * 参数 view 和 model 需要被内联，只能是 public 权限
 */
abstract class BasePresenterImpl<V : BaseView, M : BaseModel>(var view: V?, var model: M) : BasePresenter,
    CoroutineScope by MainScope() {

    /**
     * bgAction  执行方法，运行在IO线程
     * uiAction  执行方法，运行在主线程
     * error     手动捕获当次异常，运行在主线程
     */
    protected inline fun <T> runTask(
        crossinline bgAction: suspend () -> T,
        noinline uiAction: ((T) -> Unit)? = null,
        noinline error: ((Exception) -> Unit)? = null
    ): Job {
        val job = launch {
            try {
                val v = withContext(Dispatchers.IO) { bgAction() }
                uiAction?.invoke(v)
            } catch (e: Exception) {
                if (error != null) {
                    error(e)
                } else {
                    runTaskError(e)
                }
            } finally {
            }
        }
        return job
    }

    /**
     * bgAction  执行方法，运行在IO线程
     * uiAction  执行方法，运行在主线程
     * error     手动捕获当次异常，运行在主线程
     */
    protected inline fun <T> runTaskDialog(
        crossinline bgAction: suspend () -> T,
        noinline uiAction: ((T) -> Unit)? = null,
        noinline error: ((Exception) -> Unit)? = null
    ): Job {
        val job = launch {
            view?.showLoad()
            try {
                delay(1000)
                val v = withContext(Dispatchers.IO) { bgAction() }
                uiAction?.invoke(v)
            } catch (e: Exception) {
                if (error != null) {
                    error(e)
                } else {
                    runTaskError(e)
                }
            } finally {
                view?.hideLoad()
            }
        }
        return job
    }

    /**
     * 此方法必须是public权限，否则无法内联
     *
     * 未手动捕获异常时，这里统一捕获，交给BaseView基类处理
     */
    fun runTaskError(e: Exception) {
        LogUtil.log("BasePresenterImpl", "runTaskError $e")

        //CancellationException 协程取消异常，由于detachView主动取消了协程，此时view为空，在基类中无法捕获
        if (e is CancellationException) {
            AndroidUtil.showToast("协程被取消")
        } else {
            view?.runTaskError(e)
        }
    }

    override fun detachView() {
        cancel()
        view = null
        LogUtil.log("BasePresenterImpl", "detachView")
    }
}