package com.basemvp.main.special_rc.circle_rc

import com.base.common.base.adapter.BaseListAdapter
import com.base.common.util.imageload.LoadImage
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 使用 Glide + CircleImageView + into(view) 可能导致第一次只加载显示 placeholder
 * 参考 GlideEngine 注释
 */
class CircleAdapter : BaseListAdapter<String, BaseViewHolder>(R.layout.circle_layout) {
    init {
        repeat(15) {
            data.add("啦啦")
        }
    }

    override fun convertUI(holder: BaseViewHolder, item: String) {
        LoadImage.load(LoadImage.imgUrl, holder.getView(R.id.circleImg))
    }
}