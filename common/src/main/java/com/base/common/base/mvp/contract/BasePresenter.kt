package com.base.common.base.mvp.contract

/**
 * 对应 Presenter 基类以及 Contract 接口类 需要继承
 * 具体实现是在 对应 Presenter 基类中
 */
interface BasePresenter {
    fun detachView()
}