package com.base.common.base.mvp

import com.base.common.base.BaseActivity
import com.base.common.base.mvp.contract.BasePresenter
import com.base.common.base.mvp.contract.BaseView

/**
 * 对于继承了 BaseViewActivity，但又不需要使用mvp功能
 * 或者只复用其他的 Presenter实例的。泛型直接传 BasePresenter ，presenter实例赋空值就可以
 */
abstract class BaseViewActivity<P : BasePresenter> : BaseActivity(), BaseView {
    private val TAG = "BaseViewActivity"

    protected abstract var presenter: P?

    override fun showLoad() {
        showLoadDialog()
    }

    override fun hideLoad() {
        hideLoadDialog()
    }

    override fun runTaskError(e: Exception) {
        runError(e)
    }

    override fun onDestroy() {
        presenter?.detachView()
        presenter = null

        super.onDestroy()
    }
}