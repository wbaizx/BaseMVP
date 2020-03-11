package com.login.home

import com.login.home.bean.LoginBean
import kotlinx.coroutines.Job


interface LoginViewInterface {
    fun loginSuccess(bean: LoginBean?)
}

interface LoginPresenterInterface {
    fun login(): Job
}

interface LoginModelInterface {
    suspend fun login(): LoginBean
}
