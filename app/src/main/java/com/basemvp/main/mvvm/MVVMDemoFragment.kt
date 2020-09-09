package com.basemvp.main.mvvm

import com.base.common.base.mvvm.BaseMVVMFragment
import com.base.common.util.LogUtil
import com.basemvp.R
import com.basemvp.databinding.FragmentMvvmDemoFBinding
import kotlinx.android.synthetic.main.activity_mvvm_demo.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MVVMDemoFragment : BaseMVVMFragment<FragmentMvvmDemoFBinding>() {
    private val TAG = "MVVMDemoFragment"

    /**
     * viewModel 使用koin注入方式
     */
    override val viewModel by sharedViewModel<MVVMDemoViewModel>()

    override fun getContentView() = R.layout.fragment_mvvm_demo_f

    override fun bindModelId(binding: FragmentMvvmDemoFBinding) {
        binding.viewModel = viewModel
    }

    override fun createView() {
        LogUtil.log(TAG, "viewModel ${viewModel.hashCode()}")
        viewModel.name.observe(this, {
            LogUtil.log(TAG, "name ${this.hashCode()}")
        })
    }

    override fun onFirstVisible() {
        save.setOnClickListener {
            viewModel.saveData()
        }

        query.setOnClickListener {
            viewModel.queryData()
        }
    }

    override fun onVisible() {
    }

    override fun onHide() {
    }
}