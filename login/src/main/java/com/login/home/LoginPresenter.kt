package com.login.home

import com.base.common.base.mvp.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class LoginPresenter(view: LoginViewInterface?) :
    BasePresenterImpl<LoginViewInterface, LoginModelInterface>(view, LoginModel()),
    LoginPresenterInterface {
    private val TAG = "LoginPresenter"

    override fun loginBean() = runTaskDialog({ model.loginBean() }, { view?.loginSuccessBean(it) })

    override fun loginResponseBody() = runTaskDialog({
        val async1 = async(Dispatchers.IO) {
            model.loginResponseBody()
        }
        val async2 = async(Dispatchers.IO) {
            model.loginResponseBody()
        }
        async1.await()
        async2.await()
    }, { view?.loginSuccessResponseBody(it) })
}
