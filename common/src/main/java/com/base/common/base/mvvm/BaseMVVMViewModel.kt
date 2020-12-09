package com.base.common.base.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.base.common.util.AndroidUtil
import com.base.common.util.log
import kotlinx.coroutines.*

/**
 * 这里高阶函数函数运行必须使用 action() 方式，使用 action.invoke() 会报错
 */
abstract class BaseMVVMViewModel : ViewModel() {
    private val TAG = "BaseMVVMViewModel"

    val showLoad: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val error: MutableLiveData<Exception> by lazy { MutableLiveData<Exception>() }

    //crossinline action: suspend CoroutineScope.() -> Unit, 带接收者参数形式 T.()->Unit
    inline fun runTask(
        isShowDialog: Boolean = true,
        crossinline action: suspend () -> Unit,
        noinline error: ((Exception) -> Unit)? = null
    ) = viewModelScope.launch {
        try {
            if (isShowDialog) {
                showLoad.postValue(true)

                //测试用
                delay(1000)
            }
            withContext(Dispatchers.IO) { action() }
        } catch (e: Exception) {
            if (error != null) {
                error(e)
            } else {
                runTaskError(e)
            }
        } finally {
            if (isShowDialog) {
                log("BaseMVVMViewModel", "runTaskDialog finally")
                showLoad.postValue(false)
            }
        }
    }

    /**
     * 此方法必须是public权限，否则无法内联
     *
     * 未手动捕获异常时，这里统一捕获，交给BaseView基类处理
     */
    fun runTaskError(e: Exception) {
        log("BaseMVVMViewModel", "runTaskError $e")

        //CancellationException 协程取消异常，由于detachView主动取消了协程，此时view为空，无法在基类中捕获
        if (e is CancellationException) {
            AndroidUtil.showToast("协程被取消")
        } else {
            error.postValue(e)
        }
    }

    override fun onCleared() {
        log(TAG, "onCleared")
        super.onCleared()
    }
}