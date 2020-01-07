package com.basemvp.base.mvp

import com.basemvp.base.BaseActivity

abstract class BaseViewActivity<P : BasePresenterInterface> : BaseActivity() {
    protected var presenter: P? = null

    override fun configure() {
        presenter = initBasePresenter()
    }

    abstract fun initBasePresenter(): P

    override fun onDestroy() {
        presenter?.detachView()
        presenter = null
        super.onDestroy()
    }
}