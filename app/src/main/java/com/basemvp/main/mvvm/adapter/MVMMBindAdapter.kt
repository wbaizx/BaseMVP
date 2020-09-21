package com.basemvp.main.mvvm.adapter

import com.base.common.base.adapter.BaseBindingAdapter
import com.basemvp.R
import com.basemvp.databinding.MvvmBindItemBinding
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class MVMMBindAdapter : BaseBindingAdapter<MVVMBindBean, MvvmBindItemBinding>(R.layout.mvvm_bind_item) {

    init {
        repeat(20) {
            data.add(MVVMBindBean(it.toString()))
        }
    }

    override fun bindModelId(binding: MvvmBindItemBinding, item: MVVMBindBean) {
        binding.bean = item
    }

    override fun convertUI(holder: BaseDataBindingHolder<MvvmBindItemBinding>, item: MVVMBindBean) {
    }
}