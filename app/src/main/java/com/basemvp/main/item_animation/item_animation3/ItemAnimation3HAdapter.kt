package com.basemvp.main.item_animation.item_animation3

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ItemAnimation3HAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_anim3h_layout_item) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.item_text, "$item --  ${helper.adapterPosition - headerLayoutCount}")
    }

}