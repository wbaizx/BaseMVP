package com.login.home

import com.base.common.base.mvp.BasePresenterImpl

class LoginPresenter(view: LoginViewInterface?) :
    BasePresenterImpl<LoginViewInterface, LoginModelInterface>(view, LoginModel()),
    LoginPresenterInterface {
    private val TAG = "LoginPresenter"

    override fun loginBean() = launchTaskDialog({ model.loginBean() }, { view?.loginSuccessBean(it) })

    override fun loginResponseBody() = launchTaskDialog({
        //        val async = async {
        model.loginResponseBody()
//        }
//        async.await()
    }, { view?.loginSuccessResponseBody(it) })

}
