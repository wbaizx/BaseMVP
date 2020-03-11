package com.login.http

import com.base.common.util.LogUtil
import com.base.common.util.http.BaseHttp

object LoginHttp {
    val loginRequest: LoginAPI by lazy {
        LogUtil.log("LoginHttp", "loginRequest")
        BaseHttp.retrofit.create(LoginAPI::class.java)
    }
}