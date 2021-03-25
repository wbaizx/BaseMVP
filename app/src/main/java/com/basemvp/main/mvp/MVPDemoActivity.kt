package com.basemvp.main.mvp

import com.base.common.base.mvp.BaseMVPActivity
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_mvp_demo.*

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
