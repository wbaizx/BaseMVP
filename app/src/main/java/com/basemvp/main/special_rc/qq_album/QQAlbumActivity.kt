package com.basemvp.main.special_rc.qq_album

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.base.common.base.BaseActivity
import com.base.common.config.RouteString
import kotlinx.android.synthetic.main.activity_qqalbum.*

/**
 * 使用 多布局 + setSpanSizeLookup 方式
 * 也可以使用嵌套布局方式
 * 或者分割线方式
 */
@Route(path = RouteString.QQ_ALBUM, name = "仿QQ空间相册样式")
class QQAlbumActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_qqalbum

    override fun initView() {
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = QQAlbumAdapter()
    }

    override fun initData() {
    }
}
