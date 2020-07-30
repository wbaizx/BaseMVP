package com.base.common.base.mvp

import com.base.common.base.BaseFragment
import com.base.common.base.mvp.contract.BasePresenter
import com.base.common.base.mvp.contract.BaseView
import com.base.common.util.AndroidUtil
import com.base.common.util.http.CodeException
import com.base.common.util.http.NoNetworkException
import com.google.gson.stream.MalformedJsonException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 对于继承了 BaseViewFragment 但又不需要使用mvp功能
 * 或者只复用其他的 Presenter实例的。泛型直接传 BasePresenter ，presenter实例赋空值就可以
 */
abstract class BaseViewFragment<P : BasePresenter> : BaseFragment(), BaseView {
    private val TAG = "BaseViewFragment"

    protected abstract var presenter: P?

    override fun showLoad() {
        showLoadDialog()
    }

    override fun hideLoad() {
        hideLoadDialog()
    }

    override fun onDestroy() {
        presenter?.detachView()
        presenter = null

        super.onDestroy()
    }

    /**
     * 修改了这里记得修改 BaseViewActivity 对应地方
     */
    override fun runTaskError(e: Exception) {
        when (e) {
            is SocketTimeoutException -> AndroidUtil.showToast("连接超时")
            is UnknownHostException -> AndroidUtil.showToast("网络错误")
            is NoNetworkException -> AndroidUtil.showToast("无网络")
            is MalformedJsonException -> AndroidUtil.showToast("json解析错误")
            is CodeException -> AndroidUtil.showToast("服务器code码错误 + code=${e.message}")
            else -> AndroidUtil.showToast("未知错误")
        }
    }
}