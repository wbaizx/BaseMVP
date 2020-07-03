package com.basemvp.main.mvp_room

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvp.BaseViewActivity
import com.base.common.config.RouteString
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_mvp_room.*

@Route(path = RouteString.MVP_ROOM, name = "展示mvp + room用法", extras = RouteString.isNeedLogin)
class MvpRoomActivity : BaseViewActivity<MvpRoomPresenterInterface>(), MvpRoomViewInterface {

    override var presenter: MvpRoomPresenterInterface? = MvpRoomPresenter(this)

    override fun getContentView() = R.layout.activity_mvp_room

    override fun initView() {
        save.setOnClickListener {
            presenter?.saveData()
        }

        query.setOnClickListener {
            presenter?.queryData()
        }
    }

    override fun initData() {
    }
}
