package com.basemvp.main.mvvm

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.adapter.BaseViewPagerAdapter
import com.base.common.base.mvvm.BaseMVVMActivity
import com.base.common.config.RouteString
import com.base.common.extension.setOnAvoidRepeatedClickListener
import com.base.common.util.log
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

    override fun bindModelId(binding: ActivityMvvmDemoBinding) {
        binding.viewModel = viewModel
    }

    override fun initView() {
        log(TAG, "viewModel ${viewModel.hashCode()}")

        save.setOnClickListener {
            viewModel.saveData()
        }

        query.setOnAvoidRepeatedClickListener {
            viewModel.queryData()
        }

        viewPager2.adapter = BaseViewPagerAdapter(
            this, arrayListOf(
                MVVMDemoFragment(),
                MVVMDemoFragment()
            )
        )
    }

    override fun initData() {
        viewModel.name.observe(this, {
            log(TAG, "name ${this.hashCode()}")
            finish()
        })
    }

    override fun onDestroy() {
        log(TAG, "onDestroy ${this.hashCode()}")
        super.onDestroy()
    }
}