package com.basemvp.main.special_rc

import com.base.common.base.BaseActivity
import com.base.common.util.launchActivity
import com.basemvp.R
import com.basemvp.main.special_rc.circle_rc.CircleRCActivity
import com.basemvp.main.special_rc.connection_rc.ConnectionRCActivity
import com.basemvp.main.special_rc.damping_rc.DampingRCActivity
import com.basemvp.main.special_rc.gallery.GalleryActivity
import com.basemvp.main.special_rc.indicator.PictureIndicatorActivity
import com.basemvp.main.special_rc.qq_album.QQAlbumActivity
import com.basemvp.main.special_rc.scrollto_rc.ScrollToRCActivity
import kotlinx.android.synthetic.main.activity_special_rc.*

class SpecialRCActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_special_rc

    override fun initView() {
        dampingRc.setOnClickListener {
            launchActivity(this, DampingRCActivity::class.java)
        }

        recyclerViewGallery.setOnClickListener {
            launchActivity(this, GalleryActivity::class.java)
        }

        scrollToRecyclerView.setOnClickListener {
            launchActivity(this, ScrollToRCActivity::class.java)
        }

        qqAlbum.setOnClickListener {
            launchActivity(this, QQAlbumActivity::class.java)
        }

        connectionRecyclerView.setOnClickListener {
            launchActivity(this, ConnectionRCActivity::class.java)
        }

        overlappingRecyclerView.setOnClickListener {
            launchActivity(this, CircleRCActivity::class.java)
        }

        pictureIndicator.setOnClickListener {
            launchActivity(this, PictureIndicatorActivity::class.java)
        }
    }

    override fun initData() {
    }

}
