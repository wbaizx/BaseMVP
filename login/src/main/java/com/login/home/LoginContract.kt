package com.login.home

import com.base.common.base.mvp.contract.BaseMVPModelI
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI
import com.login.home.bean.LoginBean
import kotlinx.coroutines.Job
import okhttp3.ResponseBody

interface LoginViewInterface : BaseMVPViewI {
    fun loginSuccessBean(bean: LoginBean)
    fun loginSuccessResponseBody(responseBody1: ResponseBody, responseBody2: ResponseBody)
}

interface LoginPresenterInterface : BaseMVPPresenterI {
    fun loginBean(): Job
    fun loginResponseBody(): Job
}

interface LoginModelInterface : BaseMVPModelI {
    suspend fun loginBean(): LoginBean
    suspend fun loginResponseBody(): ResponseBody
}
