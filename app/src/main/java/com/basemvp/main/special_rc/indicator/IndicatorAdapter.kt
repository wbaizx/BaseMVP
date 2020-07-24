package com.basemvp.main.special_rc.indicator

import android.widget.ImageView
import com.base.common.config.GlideApp
import com.base.common.util.imgUrl
import com.base.common.util.specialInto
import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class IndicatorAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_indicator_item) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        GlideApp.with(context).load(imgUrl).specialInto(holder.itemView as ImageView)
    }
}