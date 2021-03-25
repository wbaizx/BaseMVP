package com.basemvp.main.fragment_example

import com.base.common.base.BaseActivity
import com.base.common.util.launchActivity
import com.basemvp.R
import com.basemvp.main.fragment_example.show_fragment.ControlFragmentActivity
import com.basemvp.main.fragment_example.vp_fragment.VP2FragmentActivity
import kotlinx.android.synthetic.main.activity_fragment_example.*

class FragmentExampleActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_fragment_example

    override fun initView() {
        vp2Fragment.setOnClickListener {
            launchActivity(this, VP2FragmentActivity::class.java)
        }

        showFragment.setOnClickListener {
            launchActivity(this, ControlFragmentActivity::class.java)
        }
    }

    override fun initData() {
    }

}
