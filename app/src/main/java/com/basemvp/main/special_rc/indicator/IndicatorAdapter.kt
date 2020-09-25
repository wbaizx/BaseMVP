package com.basemvp.main.special_rc.indicator

import android.widget.ImageView
import com.base.common.base.adapter.BaseListAdapter
import com.base.common.config.GlideApp
import com.base.common.util.blurInto
import com.base.common.util.imgUrl
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class IndicatorAdapter : BaseListAdapter<String, BaseViewHolder>(R.layout.item_indicator_item) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convertUI(holder: BaseViewHolder, item: String) {
        GlideApp.with(context).load(imgUrl).blurInto(holder.itemView as ImageView)
    }
}