package com.login.home

import com.base.common.base.mvp.BaseViewFragment
import com.base.common.config.GlideApp
import com.base.common.util.LogUtil
import com.base.common.util.imgUrl
import com.base.common.util.normalInto
import com.login.R
import com.login.home.bean.LoginBean
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.ResponseBody

class LoginFragment : BaseViewFragment<LoginPresenterInterface>(), LoginViewInterface {
    private val TAG = "LoginFragment"

    override var presenter: LoginPresenterInterface? = LoginPresenter(this)

    override fun getContentView() = R.layout.fragment_login

    override fun createView() {
        LogUtil.log(TAG, "createView")
    }

    override fun onFirstVisible() {
        LogUtil.log(TAG, "onFirstVisible")
        GlideApp.with(this).load(imgUrl).normalInto(loginImg)
        loginBtn.setOnClickListener {
//            presenter?.loginBean()
            presenter?.loginResponseBody()
        }
    }

    override fun onVisible() {
        LogUtil.log(TAG, "onVisible")
    }

    override fun onHide() {
        LogUtil.log(TAG, "onHide")
    }

    override fun loginSuccessBean(bean: LoginBean) {
        LogUtil.log(TAG, "loginSuccessBean -- ${bean.data.id}")
        (activity as? LoginActivity)?.loginSuccess()
    }

    override fun loginSuccessResponseBody(responseBody1: ResponseBody, responseBody2: ResponseBody) {
        LogUtil.log(TAG, "loginSuccessResponseBody -- ${responseBody1.string()} -- ${responseBody2.string()}")
        (activity as? LoginActivity)?.loginSuccess()
    }
}