package com.basemvp.main.shape_btn

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.LogUtil
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_show_shapebtn.*

@Route(path = RouteString.SHAPE_BTN, name = "展示shapeBtn", extras = RouteString.isNeedLogin)
class ShowShapeBtnActivity : BaseActivity() {
    private val TAG = "ShowShapeBtnActivity"

    override fun getContentView() = R.layout.activity_show_shapebtn

    override fun initView() {
        shapeButton.setOnClickListener {
            LogUtil.log(TAG, "shapeButton OnClick")
        }
//        shapeDrawableButton.isEnabled = false
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.log(TAG, "onDestroy")
    }
}