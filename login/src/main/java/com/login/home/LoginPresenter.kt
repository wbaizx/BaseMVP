package com.login.home

import com.base.common.base.mvp.BasePresenterImpl

class LoginPresenter(view: LoginViewInterface?) :
    BasePresenterImpl<LoginViewInterface, LoginModelInterface>(view, LoginModel()),
    LoginPresenterInterface {
    private val TAG = "LoginPresenter"

    override fun login() = launchTaskDialog({ model?.login() }, { view?.loginSuccess(it) })

}
