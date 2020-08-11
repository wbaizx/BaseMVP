package com.base.common.base.mvvm

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.base.common.base.BaseActivity

abstract class BaseMVVMActivity<B : ViewDataBinding> : BaseActivity() {
    private val TAG = "BaseMVVMActivity"

    abstract val viewModel: BaseMVVMViewModel
    private lateinit var binding: B

    override fun bindView() {
        binding = DataBindingUtil.setContentView(this, getContentView())
        binding.lifecycleOwner = this
        binding.setVariable(getBindModelId(), viewModel)

        initBaseObserve()
    }

    private fun initBaseObserve() {
        viewModel.error.observe(this, Observer {
            runError(it)
        })

        viewModel.showLoad.observe(this, Observer {
            if (it) {
                showLoadDialog()
            } else {
                hideLoadDialog()
            }
        })
    }

    abstract fun getBindModelId(): Int

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }
}