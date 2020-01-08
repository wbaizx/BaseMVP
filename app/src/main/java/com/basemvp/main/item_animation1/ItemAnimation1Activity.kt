package com.basemvp.main.item_animation1

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.activity_item_animation1.*

@Route(path = RouteString.ITEM_ANIMATION1, name = "recyclerView item动画1")
class ItemAnimation1Activity : BaseActivity() {
    override fun getContentView() = R.layout.activity_item_animation1

    override fun initView() {
        val adapter = ItemAnimation1Adapter()

        change.setOnClickListener {
            adapter.data.set(5, "测试")
            adapter.notifyItemChanged(5)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        //用于item进场动画，
        //setFirstOnly 设置是否只有首次加载
//        recyclerView.adapter = ScaleInAnimationAdapter(adapter).apply {
//            setDuration(2000)
//            setFirstOnly(false)}

        recyclerView.adapter = TestAnimationAdapter(adapter).apply {
            setDuration(2000)
            setFirstOnly(false)
        }

        //用于item操作动画，用于add,remove等操作
        //如果需要修改change动画，可以继承BaseItemAnimator 仿照 DefaultItemAnimator 重写 animateChange 或者 animateChangeImpl
        recyclerView.itemAnimator = ScaleInAnimator()
    }

    override fun initData() {
    }
}
