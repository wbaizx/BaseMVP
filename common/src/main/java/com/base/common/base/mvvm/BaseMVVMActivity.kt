package com.base.common.base.mvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.base.common.base.BaseActivity

abstract class BaseMVVMActivity<B : ViewDataBinding> : BaseActivity() {
    private val TAG = "BaseMVVMActivity"

    abstract val viewModel: BaseMVVMViewModel
    private lateinit var binding: B

    override fun bindView(inflater: LayoutInflater, container: ViewGroup): View {
//        binding = DataBindingUtil.setContentView(this, getContentView())

        //因为基类采用add方式添加的content布局，所以这里通过这种方式绑定DataBinding
        binding = DataBindingUtil.inflate(inflater, getContentView(), container, false)
        binding.lifecycleOwner = this
        bindModelId(binding)

        initBaseObserve()

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