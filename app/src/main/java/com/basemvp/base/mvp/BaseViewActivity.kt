package com.basemvp.base.mvp

import com.basemvp.base.BaseActivity

abstract class BaseViewActivity<P> : BaseActivity() {
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