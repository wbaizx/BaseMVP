package com.login.home

import com.base.common.base.mvp.BaseModelImpl
import com.login.http.LoginHttp

class LoginModel : BaseModelImpl(), LoginModelInterface {
    override suspend fun login() = LoginHttp.loginRequest.login()
}
