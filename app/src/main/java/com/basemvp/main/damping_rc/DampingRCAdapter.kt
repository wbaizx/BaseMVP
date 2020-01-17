package com.basemvp.main.damping_rc

import com.basemvp.R
import com.basemvp.util.AndroidUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class DampingRCAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.damping_rc_item) {
    init {
        data = arrayListOf("", "", "", "", "", "", "")
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.item_text, "${helper.adapterPosition - getHeaderLayoutCount()}")
        if (helper.adapterPosition - getHeaderLayoutCount() == 2 || helper.adapterPosition - getHeaderLayoutCount() == 3) {
            helper.itemView.layoutParams.height = AndroidUtil.getScreenHeight()
        } else {
            helper.itemView.layoutParams.height = AndroidUtil.dp2px(900f).toInt()
        }
    }
}