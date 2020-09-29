package com.basemvp.main.coordinator.coordinator2

import androidx.constraintlayout.motion.widget.MotionLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.base.adapter.BaseListAdapter
import com.base.common.config.RouteString
import com.base.common.util.log
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.activity_coordinator2.*

/**
 * 目前为止，绝大部分动画通过 View.animate() 或者 MotionLayout都能完成了
 */
@Route(path = RouteString.COORDINATOR2, name = "折叠滚动布局第二种 coordinator2 使用MotionLayout")
class Coordinator2Activity : BaseActivity() {
    private val TAG = "Coordinator2Activity"

    override fun getContentView() = R.layout.activity_coordinator2

    override fun initView() {
        recyclerView.adapter = object : BaseListAdapter<String, BaseViewHolder>(R.layout.item_default_layout) {
            override fun convertUI(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.item_text, "$item  ${holder.adapterPosition - headerLayoutCount}")
            }
        }.apply { setNewInstance(arrayListOf("啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦", "啦啦")) }

        MotionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                log(TAG, "onTransitionStarted")
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                log(TAG, "onTransitionChange  $p3")
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                log(TAG, "onTransitionCompleted")
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                log(TAG, "onTransitionTrigger")
            }
        })
    }

    override fun initData() {
    }
}