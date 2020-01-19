package com.basemvp.main.special_rc.gallery

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.LogUtil
import kotlinx.android.synthetic.main.activity_gallery.*

@Route(path = RouteString.GALLERY, name = "recyclerView 仿Gallery效果")
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
