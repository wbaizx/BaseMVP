package com.base.common.base.mvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.base.common.base.BaseFragment

abstract class BaseMVVMFragment<B : ViewDataBinding> : BaseFragment() {
    private val TAG = "BaseMVVMFragment"

    abstract val viewModel: BaseMVVMViewModel
    private lateinit var binding: B

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, getContentView(), container, false)
        binding.lifecycleOwner = this
        bindModelId(binding)

        //如果采用sharedViewModel共用viewModel，那么统一交给activity注册的基本监听接收
        //在fragment中不需要注册基本监听,以免重复接收
        if ((activity as? BaseMVVMActivity<*>)?.viewModel != viewModel) {
            initBaseObserve()
        }

        return binding.root
    }

    /**
     * 绑定viewModel到UI
     */
    abstract fun bindModelId(binding: B)

    private fun initBaseObserve() {
        viewModel.error.observe(this, {
            runError(it)
        })

        viewModel.showLoad.observe(this, {
            if (it) {
                showLoadDialog()
            } else {
                hideLoadDialog()
            }
        })
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }
}