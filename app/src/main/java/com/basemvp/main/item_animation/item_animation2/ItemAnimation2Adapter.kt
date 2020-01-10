package com.basemvp.main.item_animation.item_animation2

import android.view.ViewGroup
import android.widget.TextView
import com.basemvp.R
import com.basemvp.util.AndroidUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ItemAnimation2Adapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_anim2_layout_item) {
    private var isLine = true

    init {
        repeat(20) {
            data.add("$it")
        }
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
        val view = helper.getView<TextView>(R.id.sceneText)
        view.text = item
        if (isLine) {
            view.layoutParams.width = AndroidUtil.getScreenWidth() - AndroidUtil.dp2px(80f).toInt()
            view.layoutParams.height = AndroidUtil.getScreenHeight() - AndroidUtil.dp2px(160f).toInt()
        } else {
            view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            view.layoutParams.height = AndroidUtil.dp2px(160f).toInt()
        }
    }

    fun setLine(isLine: Boolean) {
        this.isLine = isLine
    }
}