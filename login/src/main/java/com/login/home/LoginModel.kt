package com.login.home

import com.base.common.base.mvp.BaseMVPModel
import com.login.http.LoginHttp

class LoginModel : BaseMVPModel(), LoginModelInterface {
    override suspend fun loginBean() = requestNetwork { LoginHttp.loginRequest.loginBean() }

    override suspend fun loginResponseBody() = requestNetworkBase { LoginHttp.loginRequest.loginResponseBody() }
}
