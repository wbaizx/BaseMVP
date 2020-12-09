package com.base.common.base.mvp

import com.base.common.base.mvp.contract.BaseMVPModelI
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI
import com.base.common.util.AndroidUtil
import com.base.common.util.log
import kotlinx.coroutines.*

/**
 * CoroutineScope
 * GlobalScope  用于启动全局协程
 * Dispatchers  运行线程切换
 *
 * by CoroutineScope(Dispatchers.Main) 用这种方式async会有bug，所以用 by MainScope()方式
 *
 * 参数 view 和 model 需要被内联，只能是 public 权限
 *
 * 对于presenter复用，直接在需要的地方创建需要的 presenter实例，同时V层继承对应的 Contract接口（注意presenter实例的回收 detachView）
 * 对于presenter共用（多个V层同时使用同一个 presenter实例，一般为fragment和activity共用），暂无方法
 */
abstract class BaseMVPPresenter<V : BaseMVPViewI, M : BaseMVPModelI>(var view: V?, var model: M) : BaseMVPPresenterI,
    CoroutineScope by MainScope() {

    /**
     * bgAction  执行方法，运行在IO线程
     * uiAction  执行方法，运行在主线程
     * error     手动捕获当次异常，运行在主线程
     */
    protected inline fun <T> runTask(
        isShowDialog: Boolean = true,
        crossinline bgAction: suspend () -> T,
        noinline uiAction: ((T) -> Unit)? = null,
        noinline error: ((Exception) -> Unit)? = null
    ) = launch {
        try {
            if (isShowDialog) {
                view?.showLoad()

                //测试用
                delay(1000)
            }
            val v = withContext(Dispatchers.IO) { bgAction() }
            uiAction?.invoke(v)
        } catch (e: Exception) {
            if (error != null) {
                error(e)
            } else {
                runTaskError(e)
            }
        } finally {
            if (isShowDialog) {
                view?.hideLoad()
            }
        }
    }

    /**
     * 此方法必须是public权限，否则无法内联
     *
     * 未手动捕获异常时，这里统一捕获，交给BaseView基类处理
     */
    fun runTaskError(e: Exception) {
        log("BaseMVPPresenter", "runTaskError $e")

        //CancellationException 协程取消异常，由于detachView主动取消了协程，此时view为空，无法在基类中捕获
        if (e is CancellationException) {
            AndroidUtil.showToast("协程被取消")
        } else {
            view?.runTaskError(e)
        }
    }

    override fun detachView() {
        cancel()
        view = null
        log("BaseMVPPresenter", "detachView")
    }
}