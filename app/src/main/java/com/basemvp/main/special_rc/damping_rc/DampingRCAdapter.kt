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

    override fun convert(helper: BaseViewHolder, item: String?) {
        helper.setText(R.id.item_text, "${helper.adapterPosition - getHeaderLayoutCount()}")
        if (helper.adapterPosition - getHeaderLayoutCount() == 2 || helper.adapterPosition - getHeaderLayoutCount() == 3) {
            if (ImmersionBar.hasNotchScreen(dampingRCActivity)) {
                helper.itemView.layoutParams.height =
                    AndroidUtil.getScreenHeight() + ImmersionBar.getNotchHeight(dampingRCActivity)
            } else {
                helper.itemView.layoutParams.height = AndroidUtil.getScreenHeight()
            }

        } else {
            helper.itemView.layoutParams.height = AndroidUtil.dp2px(900f).toInt()
        }
    }
}