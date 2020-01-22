package com.basemvp.base.mvp

import com.basemvp.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
    private val TAG = "BasePresenterImpl"

    inline fun launchUI() {
        launch {

        }
    }

    fun detachView() {
        cancel()
        view = null
        model = null
        LogUtil.log(TAG, "detachView")
    }
}