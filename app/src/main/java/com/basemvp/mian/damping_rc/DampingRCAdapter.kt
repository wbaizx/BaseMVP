package com.basemvp.mian.damping_rc

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class DampingRCAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.damping_rc_item) {
    init {
        data = arrayListOf("", "", "", "", "", "", "")
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
    }
}