package com.basemvp.main.fragment_example.vp_fragment

import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.LogUtil
import kotlinx.android.synthetic.main.activity_vpfragment.*

@Route(path = RouteString.VP_FRAMENT, name = "viewPager2 + Fragment + Lifecycle 测试懒加载base类的生命周期")
class VP2FragmentActivity : BaseActivity() {
    private val TAG = "VP2FragmentActivity"

    override fun getContentView() = R.layout.activity_vpfragment

    override fun initView() {
        //根据需求设置缓存数量
        viewPager2.offscreenPageLimit = 1
        viewPager2.adapter = ViewPager2Adapter(this)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                LogUtil.log(TAG, "$position -- $positionOffset -- $positionOffsetPixels")
                if (positionOffset == 0f) {
                    simpleTabLayout.setPosition(position)
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                LogUtil.log(TAG, "$position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        simpleTabLayout.setListener {
            viewPager2.setCurrentItem(it, false)
        }
    }

    override fun initData() {
        simpleTabLayout.setData(arrayListOf("第1个", "第2个", "第3个", "第4个", "第5个", "第6个", "第7个"))
    }
}
