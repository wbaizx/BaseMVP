package com.basemvp.main.fragment_example.show_fragment

import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import kotlinx.android.synthetic.main.activity_control_fragment.*

@Route(path = RouteString.SHOW_FRAGMENT, name = "使用add,show + Fragment + Lifecycle 测试懒加载base类的生命周期")
class ControlFragmentActivity : BaseActivity() {
    private lateinit var fragmentControl: FragmentControl

    override fun getContentView() = R.layout.activity_control_fragment

    override fun initView() {
        fragmentControl = FragmentControl(supportFragmentManager, R.id.frameLayout)

        simpleTabLayout.setListener {
            fragmentControl.show(it)
        }

        reset.setOnClickListener {
            fragmentControl.reset()
            fragmentControl.show(0)
            simpleTabLayout.setPosition(0)
        }
    }

    override fun initData() {
        simpleTabLayout.setData(arrayListOf("第1个", "第2个", "第3个", "第4个", "第5个", "第6个", "第7个"))

        fragmentControl.show(0)
    }
}
