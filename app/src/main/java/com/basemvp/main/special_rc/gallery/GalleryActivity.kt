package com.basemvp.main.special_rc.gallery

import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.BaseActivity
import com.base.common.util.log
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : BaseActivity() {
    private val TAG = "GalleryActivity"

    override fun getContentView() = R.layout.activity_gallery

    override fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = GalleryAdapter()

        GalleryHelper {
            log(TAG, "$it")
        }.attachToRecyclerView(recyclerView)
    }

    override fun initData() {
    }

}
