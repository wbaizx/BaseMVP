package com.base.common.base.mvp

import com.base.common.base.BaseActivity
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI

/**
 * 对于继承了 BaseMVPActivity，但又不需要使用mvp功能
 * 或者只复用其他的 Presenter实例的。泛型直接传 BaseMVPPresenterI ，presenter实例赋空值就可以
 */
abstract class BaseMVPActivity<P : BaseMVPPresenterI> : BaseActivity(), BaseMVPViewI {
    private val TAG = "BaseMVPActivity"

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