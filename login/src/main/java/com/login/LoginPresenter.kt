package com.login

import com.base.common.base.mvp.BasePresenterImpl

class LoginPresenter(view: LoginViewInterface?) :
    BasePresenterImpl<LoginViewInterface, LoginModelInterface>(view, LoginModel()), LoginPresenterInterface {
    private val TAG = "LoginPresenter"

}
