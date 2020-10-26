package com.basemvp.main.special_rc.circle_rc

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_circle_rc.*

@Route(path = RouteString.CIRCLE_RC, name = "重叠头像list")
class CircleRCActivity : BaseActivity() {
    private val TAG = "CircleRCActivity"

    override fun getContentView() = R.layout.activity_circle_rc

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CircleAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(CircleDecoration(adapter))
    }

    override fun initData() {
    }
}
