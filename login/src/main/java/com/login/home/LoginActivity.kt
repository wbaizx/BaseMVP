package com.login.home

import androidx.lifecycle.Lifecycle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.base.common.base.BaseActivity
import com.base.common.util.SharedPreferencesUtil
import com.base.common.util.http.ObjectBean
import com.base.common.util.http.ParcelableBean
import com.base.common.util.http.SerializableBean
import com.base.common.util.launchARouter
import com.base.common.util.log
import com.base.common.util.normalNavigation
import com.login.R

@Route(path = "/login/login_home", name = "组件化登录模块首页")
class LoginActivity : BaseActivity() {
    private val TAG = "LoginActivity"

    @JvmField
    @Autowired(name = "is_goto_main")
    var isGotoMain: Boolean = false

    @JvmField
    @Autowired(name = "serializable_bean")
    var Sb: SerializableBean? = null

    @JvmField
    @Autowired(name = "parcelable_bean")
    var pb: ParcelableBean? = null

    @JvmField
    @Autowired(name = "object_bean")
    var ob: ObjectBean? = null

    override fun getContentView() = R.layout.activity_login

    override fun initView() {
        log(TAG, "auto wired $isGotoMain")
        log(TAG, "auto wired $Sb")
        log(TAG, "auto wired $pb")
        log(TAG, "auto wired $ob")

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
            launchARouter("/main/main_home").normalNavigation(this, navCallback = object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        } else {
            finish()
        }
    }
}
