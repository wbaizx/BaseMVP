package com.basemvp.main.special_rc.damping_rc

import com.basemvp.R
import com.base.common.util.AndroidUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gyf.immersionbar.ImmersionBar

/**
 * 这里直接设置的高度，也可以通过minimumHeight设置最小高度实现全屏
 * 注意全面屏和刘海屏适配
 */
class DampingRCAdapter(private val dampingRCActivity: DampingRCActivity) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.damping_rc_item) {
    init {
        data = arrayListOf("", "", "", "", "", "", "")
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_text, "${holder.adapterPosition - headerLayoutCount}")
        if (holder.adapterPosition - headerLayoutCount == 2 || holder.adapterPosition - headerLayoutCount == 3) {
            if (ImmersionBar.hasNotchScreen(dampingRCActivity)) {
                holder.itemView.layoutParams.height =
                    AndroidUtil.getScreenHeight() + ImmersionBar.getNotchHeight(dampingRCActivity)
            } else {
                holder.itemView.layoutParams.height = AndroidUtil.getScreenHeight()
            }

        } else {
            holder.itemView.layoutParams.height = AndroidUtil.dp2px(900f).toInt()
        }
    }
}