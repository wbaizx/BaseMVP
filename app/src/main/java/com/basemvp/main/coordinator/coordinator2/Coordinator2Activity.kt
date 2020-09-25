package com.basemvp.main.coordinator.coordinator2

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.base.adapter.BaseListAdapter
import com.base.common.config.RouteString
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.activity_coordinator2.*

@Route(path = RouteString.COORDINATOR2, name = "折叠滚动布局第二种 coordinator2 使用MotionLayout")
class Coordinator2Activity : BaseActivity() {
    override fun getContentView() = R.layout.activity_coordinator2

    override fun initView() {
        recyclerView.adapter = object : BaseListAdapter<String, BaseViewHolder>(R.layout.item_default_layout) {
            override fun convertUI(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.item_text, "$item  ${holder.adapterPosition - headerLayoutCount}")
            }
        }.apply { setNewInstance(arrayListOf("啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦")) }
    }

    override fun initData() {
    }
}