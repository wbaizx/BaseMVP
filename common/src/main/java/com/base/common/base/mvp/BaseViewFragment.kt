package com.base.common.base.mvp

import com.base.common.base.BaseFragment

abstract class BaseViewFragment<P> : BaseFragment() {
    protected var presenter: P? = null

    override fun configure() {
        presenter = initBasePresenter()
    }

    abstract fun initBasePresenter(): P?

    override fun onDestroy() {
        (presenter as? BasePresenterImpl<*, *>)?.detachView()
        presenter = null
        super.onDestroy()
    }
}