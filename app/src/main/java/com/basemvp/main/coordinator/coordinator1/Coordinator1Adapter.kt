package com.basemvp.main.coordinator.coordinator1

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class Coordinator1Adapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_default_layout) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.item_text, "item  ${helper.adapterPosition - getHeaderLayoutCount()}")
    }
}