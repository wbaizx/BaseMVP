package com.basemvp.main.mvp

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvp.BaseMVPActivity
import com.base.common.config.RouteString
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_mvp_demo.*

@Route(path = RouteString.MVP_ROOM, name = "展示mvp + room用法")
class MVPDemoActivity : BaseMVPActivity<MVPDemoPresenterInterface>(), MVPDemoViewInterface {

    override var presenter: MVPDemoPresenterInterface? = MVPDemoPresenter(this)

    override fun getContentView() = R.layout.activity_mvp_demo

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
