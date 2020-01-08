package com.basemvp.main.item_animation2

import android.view.ViewGroup
import android.widget.TextView
import com.basemvp.R
import com.basemvp.util.AndroidUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class SceneAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.transition_layout_item) {
    private var isLine = true

    init {
        repeat(20) {
            data.add("$it")
        }
    }

    override fun convert(helper: BaseViewHolder, item: String?) {
        val view = helper.getView<TextView>(R.id.sceneText)
        view.setText(item)
        if (isLine) {
            view.layoutParams.width = AndroidUtil.getScreenWidth() - 400
            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            view.layoutParams.width = AndroidUtil.dp2px(160f).toInt()
            view.layoutParams.height = AndroidUtil.dp2px(160f).toInt()
        }
    }

    fun setLine(isLine: Boolean) {
        this.isLine = isLine
    }
}