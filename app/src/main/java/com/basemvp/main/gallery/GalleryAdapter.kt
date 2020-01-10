package com.basemvp.main.gallery

import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class GalleryAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_gallery_layout_item) {
    init {
        repeat(20) {
            data.add("啦啦")
        }
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.item_text, "$item --  ${helper.adapterPosition - getHeaderLayoutCount()}")
    }

}