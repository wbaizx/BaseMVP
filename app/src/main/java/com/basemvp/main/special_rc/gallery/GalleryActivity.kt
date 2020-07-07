package com.basemvp.main.special_rc.gallery

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import com.base.common.util.LogUtil
import kotlinx.android.synthetic.main.activity_gallery.*

@Route(path = RouteString.GALLERY, name = "recyclerView 仿Gallery效果，也可以使用PagerSnapHelper+滚动监听改变view大小实现")
class GalleryActivity : BaseActivity() {
    private val TAG = "GalleryActivity"

    override fun getContentView() = R.layout.activity_gallery

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = GalleryAdapter()

        GalleryHelper{
            LogUtil.log(TAG,"$it")
        }.attachToRecyclerView(recyclerView)
    }

    override fun initData() {
    }

}
