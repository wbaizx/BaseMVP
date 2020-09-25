package com.basemvp.main.item_animation.item_animation3

import com.base.common.base.adapter.BaseListAdapter
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ItemAnimation3VAdapter : BaseListAdapter<String, BaseViewHolder>(R.layout.item_anim3v_layout_item) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convertUI(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_text, "$item --  ${holder.adapterPosition - headerLayoutCount}")
    }

}