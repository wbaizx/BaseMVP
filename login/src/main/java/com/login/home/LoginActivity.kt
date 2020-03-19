package com.login.home

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.base.BaseActivity
import com.base.common.config.GOTO_MAIN
import com.base.common.config.RouteString
import com.base.common.config.normalNavigation
import com.base.common.util.LogUtil
import com.base.common.util.SharedPreferencesUtil
import com.login.R

@Route(path = RouteString.LOGIN, name = "组件化登录模块首页", extras = -1)
class LoginActivity : BaseActivity() {
    private val TAG = "LoginActivity"

    @JvmField
    @Autowired(name = GOTO_MAIN)
    var isGotoMain: Boolean = false

    override fun getContentView() = R.layout.activity_login

    override fun initView() {
        ARouter.getInstance().inject(this)
        isGotoMain = intent.getBooleanExtra(GOTO_MAIN, false)
        LogUtil.log(TAG, "isGotoMain $isGotoMain")

        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayout, loginFragment)
            .setMaxLifecycle(loginFragment, Lifecycle.State.RESUMED)
            .commit()
    }

    override fun initData() {
    }

    fun loginSuccess() {
        SharedPreferencesUtil.putData(SharedPreferencesUtil.LOGIN, true)
        if (isGotoMain) {
            ARouter.getInstance().build(RouteString.MAIN).normalNavigation { finish() }
        } else {
            finish()
        }
    }
}
