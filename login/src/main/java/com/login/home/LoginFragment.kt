package com.login.home

import com.base.common.base.mvp.BaseMVPFragment
import com.base.common.util.imageload.LoadImage
import com.base.common.util.log
import com.login.R
import com.login.home.bean.LoginBean
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.ResponseBody

class LoginFragment : BaseMVPFragment<LoginPresenterInterface>(), LoginViewInterface {
    private val TAG = "LoginFragment"

    override var presenter: LoginPresenterInterface? = LoginPresenter(this)

    override fun getContentView() = R.layout.fragment_login

    override fun createView() {
        log(TAG, "createView")
    }

    override fun onFirstVisible() {
        log(TAG, "onFirstVisible")

        LoadImage.load(LoadImage.imgUrl, loginImg)

        loginBtn.setOnClickListener {
//            presenter?.loginBean()
            presenter?.loginResponseBody()
        }
    }

    override fun onVisible() {
        log(TAG, "onVisible")
    }

    override fun onHide() {
        log(TAG, "onHide")
    }

    override fun loginSuccessBean(bean: LoginBean) {
        log(TAG, "loginSuccessBean -- ${bean.data.id}")
        (activity as? LoginActivity)?.loginSuccess()
    }

    override fun loginSuccessResponseBody(responseBody1: ResponseBody, responseBody2: ResponseBody) {
        log(TAG, "loginSuccessResponseBody -- ${responseBody1.string()} -- ${responseBody2.string()}")
        (activity as? LoginActivity)?.loginSuccess()
    }
}