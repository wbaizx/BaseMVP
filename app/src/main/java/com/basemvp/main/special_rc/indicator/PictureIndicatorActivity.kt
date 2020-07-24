package com.basemvp.main.special_rc.indicator

import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_picture_indicator.*

@Route(path = RouteString.PICTURE_INDICATOR, name = "根据图片翻页而变化的的下标指示器", extras = RouteString.isNeedLogin)
class PictureIndicatorActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_picture_indicator

    override fun initView() {
        imgViewPager.offscreenPageLimit = 8
        imgViewPager.adapter = IndicatorAdapter()

        imgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }

    override fun initData() {
    }
}