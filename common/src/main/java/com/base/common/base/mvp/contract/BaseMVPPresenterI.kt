package com.base.common.base.mvp.contract

/**
 * 对应 Presenter 基类和 Contract 接口类 都需要继承
 * 具体实现是在 对应 Presenter 基类中
 */
interface BaseMVPPresenterI {
    fun detachView()
}