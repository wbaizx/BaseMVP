package com.basemvp.main.special_rc.indicator

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.basemvp.R

@Route(path = RouteString.PICTURE_INDICATOR, name = "根据图片翻页而变化的的下标指示器", extras = RouteString.isNeedLogin)
class PictureIndicatorActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_picture_indicator

    override fun initView() {
    }

    override fun initData() {
    }
}