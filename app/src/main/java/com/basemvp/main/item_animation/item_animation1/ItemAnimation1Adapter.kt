package com.basemvp.main.item_animation.item_animation1

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ItemAnimation1Adapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_default_layout) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_text, "$item --  ${holder.adapterPosition - headerLayoutCount}")
    }

}