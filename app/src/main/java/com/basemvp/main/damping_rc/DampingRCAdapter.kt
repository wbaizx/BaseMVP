package com.basemvp.main.damping_rc

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class DampingRCAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.damping_rc_item) {
    init {
        data = arrayListOf("", "", "", "", "", "", "")
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.item_text, "${helper.adapterPosition - getHeaderLayoutCount()}")
    }
}