package com.basemvp.main.scrollto_rc

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ScrollToRCAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.scorll_to_rc_layout) {

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.item_text, "$item --  ${helper.adapterPosition - getHeaderLayoutCount()}")
    }

}