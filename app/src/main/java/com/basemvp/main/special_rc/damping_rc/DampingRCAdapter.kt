package com.basemvp.main.special_rc.damping_rc

import com.base.common.base.adapter.BaseListAdapter
import com.base.common.util.AndroidUtil
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 这里直接设置的高度，也可以通过minimumHeight设置最小高度实现全屏
 * 注意全面屏和刘海屏适配
 */
class DampingRCAdapter(private val dampingRCActivity: DampingRCActivity) :
    BaseListAdapter<String, BaseViewHolder>(R.layout.damping_rc_item) {
    init {
        data = arrayListOf("", "", "", "", "", "", "")
    }

    override fun convertUI(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_text, "${holder.adapterPosition - headerLayoutCount}")
        if (holder.adapterPosition - headerLayoutCount == 2 || holder.adapterPosition - headerLayoutCount == 3) {
            holder.itemView.layoutParams.height = AndroidUtil.getScreenRealHeight(dampingRCActivity)

        } else {
            holder.itemView.layoutParams.height = AndroidUtil.dp2px(900f).toInt()
        }
    }
}