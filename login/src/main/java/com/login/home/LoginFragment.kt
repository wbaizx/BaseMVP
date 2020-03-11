package com.login.home

import com.base.common.base.mvp.BaseViewFragment
import com.base.common.config.GlideApp
import com.base.common.config.imgUrl
import com.base.common.config.normalInto
import com.base.common.util.LogUtil
import com.login.R
import com.login.home.bean.LoginBean
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseViewFragment<LoginPresenterInterface>(), LoginViewInterface {
    private val TAG = "LoginFragment"

    override fun initBasePresenter() = LoginPresenter(this)

    override fun getContentView() = R.layout.fragment_login

    override fun createView() {
        LogUtil.log(TAG, "createView")
    }

    override fun onFristVisible() {
        LogUtil.log(TAG, "onFristVisible")
        GlideApp.with(this).load(imgUrl).normalInto(loginImg)
        loginBtn.setOnClickListener {
            presenter?.login()
        }
    }

    override fun onVisible() {
        LogUtil.log(TAG, "onVisible")
    }

    override fun onHide() {
        LogUtil.log(TAG, "onHide")
    }

    override fun loginSuccess(bean: LoginBean?) {
        LogUtil.log(TAG, "loginSuccess -- ${bean?.data?.id}")
        (activity as? LoginActivity)?.loginSuccess()
    }
}