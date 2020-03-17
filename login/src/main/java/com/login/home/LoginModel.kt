package com.login.home

import com.base.common.base.mvp.BaseModelImpl
import com.login.http.LoginHttp

class LoginModel : BaseModelImpl(), LoginModelInterface {
    override suspend fun loginBean() = LoginHttp.loginRequest.loginBean()

    override suspend fun loginResponseBody() = LoginHttp.loginRequest.loginResponseBody()
}
