package com.base.common.base.mvp

import com.base.common.base.BaseFragment
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI

/**
 * 对于继承了 BaseViewFragment 但又不需要使用mvp功能
 * 或者只复用其他的 Presenter实例的。泛型直接传 BaseMVPPresenterI ，presenter实例赋空值就可以
 */
abstract class BaseMVPFragment<P : BaseMVPPresenterI> : BaseFragment(), BaseMVPViewI {
    private val TAG = "BaseMVPFragment"

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