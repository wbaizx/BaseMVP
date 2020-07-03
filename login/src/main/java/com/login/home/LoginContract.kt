package com.login.home

import com.base.common.base.mvp.contract.BaseModel
import com.base.common.base.mvp.contract.BasePresenter
import com.base.common.base.mvp.contract.BaseView
import com.login.home.bean.LoginBean
import kotlinx.coroutines.Job
import okhttp3.ResponseBody

interface LoginViewInterface : BaseView {
    fun loginSuccessBean(bean: LoginBean)
    fun loginSuccessResponseBody(responseBody1: ResponseBody, responseBody2: ResponseBody)
}

interface LoginPresenterInterface : BasePresenter {
    fun loginBean(): Job
    fun loginResponseBody(): Job
}

interface LoginModelInterface : BaseModel {
    suspend fun loginBean(): LoginBean
    suspend fun loginResponseBody(): ResponseBody
}
