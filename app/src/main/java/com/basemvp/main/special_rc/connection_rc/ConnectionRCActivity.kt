package com.basemvp.main.special_rc.connection_rc

import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.BaseActivity
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_connection_rc.*

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
