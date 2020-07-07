package com.basemvp.main.special_rc.connection_rc

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_connection_rc.*

@Route(path = RouteString.CONNECTION_RC, name = "连接器分割线", extras = RouteString.isNeedLogin)
class ConnectionRCActivity : BaseActivity() {
    private val TAG = "ConnectionRCActivity"

    override fun getContentView() = R.layout.activity_connection_rc

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ConnectionAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ConnectionDecoration(adapter))

    }

    override fun initData() {
    }
}
