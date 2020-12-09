package com.login.home

import com.base.common.base.mvp.BaseMVPPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class LoginPresenter(view: LoginViewInterface?) : BaseMVPPresenter<LoginViewInterface, LoginModelInterface>(view, LoginModel()),
    LoginPresenterInterface {
    private val TAG = "LoginPresenter"

    override fun loginBean() = runTask(bgAction = { model.loginBean() }, uiAction = { view?.loginSuccessBean(it) })

    override fun loginResponseBody() = runTask(bgAction = {
        val async1 = async(Dispatchers.IO) {
            model.loginResponseBody()
        }
        val async2 = async(Dispatchers.IO) {
            model.loginResponseBody()
        }

        //2元组 Pair
        Pair(async1.await(), async2.await())
        //3元组 Triple
    }, uiAction = { (one, two) -> view?.loginSuccessResponseBody(one, two) })
}
