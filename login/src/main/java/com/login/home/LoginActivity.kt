package com.login.home

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.*
import com.base.common.util.http.ObjectBean
import com.base.common.util.http.ParcelableBean
import com.base.common.util.http.SerializableBean
import com.login.R

@Route(path = RouteString.LOGIN, name = "组件化登录模块首页", extras = -1)
class LoginActivity : BaseActivity() {
    private val TAG = "LoginActivity"

    @JvmField
    @Autowired(name = GOTO_MAIN)
    var isGotoMain: Boolean = false

    @JvmField
    @Autowired(name = SERIALIZABLE_BEAN)
    var Sb: SerializableBean? = null

    @JvmField
    @Autowired(name = PARCELABLE_BEAN)
    var pb: ParcelableBean? = null

    @JvmField
    @Autowired(name = OBJECT_BEAN)
    var ob: ObjectBean? = null

    override fun getContentView() = R.layout.activity_login

    override fun initView() {
        LogUtil.log(TAG, "auto wired $isGotoMain")
        LogUtil.log(TAG, "auto wired $Sb")
        LogUtil.log(TAG, "auto wired $pb")
        LogUtil.log(TAG, "auto wired $ob")

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
