package com.basemvp.main.special_rc.connection_rc

import com.base.common.base.adapter.BaseListAdapter
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ConnectionAdapter : BaseListAdapter<String, BaseViewHolder>(R.layout.item_default_layout) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convertUI(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_text, "$item  ${holder.adapterPosition - headerLayoutCount}")
    }
}