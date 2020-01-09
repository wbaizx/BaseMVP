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

        //如果自定义item进场动画可继承 AnimationAdapter（由于构造方法泛型有问题，如果要继承目前只能用java）
        //或者直接在自己的adapter中添加，首次加载通过变量控制
        recyclerView.adapter = TestAnimationAdapter(adapter).apply {
            setDuration(2000)
            setFirstOnly(false)
        }

        //用于item操作动画，用于add,remove,change等操作
        //如果需要修改change动画，可以继承BaseItemAnimator 或者 DefaultItemAnimator
        //仿照 DefaultItemAnimator 重写 animateChange 或者 animateChangeImpl
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 2000
            changeDuration = 2000
        }
    }

    override fun initData() {
    }
}
