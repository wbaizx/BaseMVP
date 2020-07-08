package com.basemvp.main.special_rc.circle_rc

import com.base.common.config.GlideApp
import com.base.common.util.imgUrl
import com.base.common.util.normalInto
import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class CircleAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.circle_layout) {
    init {
        repeat(15) {
            data.add("啦啦")
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        GlideApp.with(context).load(imgUrl).normalInto(holder.getView<CircleImageView>(R.id.circleImg))
    }
}