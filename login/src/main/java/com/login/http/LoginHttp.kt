package com.login.http

import com.base.common.util.http.BaseHttp

object LoginHttp : BaseHttp() {

    override val BASE_URL: String = "https://easy-mock.com/"

    val api by lazy { getApi(LoginAPI::class.java) }
}