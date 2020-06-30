package com.login.home

import com.login.home.bean.LoginBean
import kotlinx.coroutines.Job
import okhttp3.ResponseBody


interface LoginViewInterface {
    fun loginSuccessBean(bean: LoginBean)
    fun loginSuccessResponseBody(responseBody1: ResponseBody, responseBody2: ResponseBody)
}

interface LoginPresenterInterface {
    fun loginBean(): Job
    fun loginResponseBody(): Job
}

interface LoginModelInterface {
    suspend fun loginBean(): LoginBean
    suspend fun loginResponseBody(): ResponseBody
}
