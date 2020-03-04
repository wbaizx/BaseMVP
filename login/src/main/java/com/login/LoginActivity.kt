package com.login

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvp.BaseViewActivity
import com.base.common.config.RouteString
import com.base.common.util.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_login.*

@Route(path = RouteString.LOGIN, name = "组件化登录模块首页", extras = -1)
class LoginActivity : BaseViewActivity<LoginPresenterInterface>(), LoginViewInterface {
    private val TAG = "LoginActivity"

    override fun initBasePresenter() = LoginPresenter(this)

    override fun getContentView() = R.layout.activity_login

    override fun initView() {
        loginBtn.setOnClickListener {
            SharedPreferencesUtil.putData(SharedPreferencesUtil.LOGIN, true)
            finish()
        }
    }

    override fun initData() {
    }
}
