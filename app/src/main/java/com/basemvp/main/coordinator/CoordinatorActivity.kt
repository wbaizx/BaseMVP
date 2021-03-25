package com.basemvp.main.coordinator

import com.base.common.base.BaseActivity
import com.base.common.util.launchActivity
import com.basemvp.R
import com.basemvp.main.coordinator.coordinator1.Coordinator1Activity
import com.basemvp.main.coordinator.coordinator2.Coordinator2Activity
import kotlinx.android.synthetic.main.activity_coordinator.*

class CoordinatorActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_coordinator

    override fun initView() {
        coordinator1.setOnClickListener {
            launchActivity(this, Coordinator1Activity::class.java)
        }

        coordinator2.setOnClickListener {
            launchActivity(this, Coordinator2Activity::class.java)
        }
    }

    override fun initData() {
    }

}
