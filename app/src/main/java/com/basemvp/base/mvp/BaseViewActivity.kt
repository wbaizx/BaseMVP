package com.basemvp.base.mvp

import com.basemvp.base.BaseActivity

abstract class BaseViewActivity<P> : BaseActivity() {
    protected var presenter: P? = null

    override fun configure() {
        presenter = initBasePresenter()
    }

    abstract fun initBasePresenter(): P?

    override fun onDestroy() {
        if (presenter is BasePresenterImpl<*, *>) {
            val presenterImpl = presenter as BasePresenterImpl<*, *>
            presenterImpl.detachView()
        }
        presenter = null

        super.onDestroy()
    }
}