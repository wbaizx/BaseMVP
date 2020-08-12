package com.base.common.base.mvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.base.common.base.BaseFragment
import com.base.common.util.LogUtil

abstract class BaseMVVMFragment<B : ViewDataBinding> : BaseFragment() {
    private val TAG = "BaseMVVMFragment"

    abstract val viewModel: BaseMVVMViewModel
    private lateinit var binding: B

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, getContentView(), container, false)
        binding.lifecycleOwner = this
        binding.setVariable(getBindModelId(), viewModel)

        //如果采用sharedViewModel共用viewModel,在fragment中不需要注册基本监听,以免重复接收
        //统一交给activity注册的基本监听接收
        if ((activity as? BaseMVVMActivity<*>)?.viewModel != viewModel) {
            initBaseObserve()
        }

        return binding.root
    }

    private fun initBaseObserve() {
        viewModel.error.observe(this, Observer {
            runError(it)
        })

        viewModel.showLoad.observe(this, Observer {
            LogUtil.log(TAG, "showLoad $it  ${this.hashCode()}  ${this.javaClass.simpleName}")
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