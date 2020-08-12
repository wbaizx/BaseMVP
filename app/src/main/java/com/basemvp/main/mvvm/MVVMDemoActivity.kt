package com.basemvp.main.mvvm

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvvm.BaseMVVMActivity
import com.base.common.config.RouteString
import com.base.common.util.LogUtil
import com.basemvp.BR
import com.basemvp.R
import com.basemvp.databinding.ActivityMvvmDemoBinding
import kotlinx.android.synthetic.main.activity_mvvm_demo.*
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = RouteString.MVVM_ROOM, name = "展示mvvm + room用法", extras = RouteString.isNeedLogin)
class MVVMDemoActivity : BaseMVVMActivity<ActivityMvvmDemoBinding>() {
    private val TAG = "MVVMDemoActivity"

    /**
     * viewModel 使用koin注入方式
     */
    override val viewModel by viewModel<MVVMDemoViewModel>()

    override fun getContentView() = R.layout.activity_mvvm_demo

    override fun getBindModelId() = BR.viewModel

    override fun initView() {
        save.setOnClickListener {
            viewModel.saveData()
        }

        query.setOnClickListener {
            viewModel.queryData()
        }
    }

    override fun initData() {
        viewModel.name.observe(this, Observer {
            LogUtil.log(TAG, "name ${this.hashCode()}")
            finish()
        })
    }

    override fun onDestroy() {
        LogUtil.log(TAG, "onDestroy ${this.hashCode()}")
        super.onDestroy()
    }
}