package com.basemvp.main.item_animation.item_animation2

import android.widget.TextView
import com.base.common.util.AndroidUtil
import com.basemvp.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ItemAnimation2Adapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_anim2_layout_item) {
    private var isLine = true

    init {
        repeat(20) {
            data.add("$it")
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        val view = holder.getView<TextView>(R.id.sceneText)
        view.text = item
        if (isLine) {
            view.layoutParams.height = AndroidUtil.getScreenShowHeight() - AndroidUtil.dp2px(200f).toInt()
        } else {
            view.layoutParams.height = AndroidUtil.dp2px(160f).toInt()
        }
    }

    fun setLine(isLine: Boolean) {
        this.isLine = isLine
    }
}