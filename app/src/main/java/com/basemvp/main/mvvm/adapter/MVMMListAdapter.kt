package com.basemvp.main.mvvm.adapter

import com.base.common.base.adapter.BaseBindingAdapter
import com.basemvp.R
import com.basemvp.databinding.MvvmListItemBinding
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class MVMMListAdapter : BaseBindingAdapter<MVVMListBean, MvvmListItemBinding>(R.layout.mvvm_list_item) {

    init {
        repeat(20) {
            data.add(MVVMListBean(it.toString()))
        }
    }

    override fun bindModelId(binding: MvvmListItemBinding, item: MVVMListBean) {
        binding.bean = item
    }

    override fun convertUI(holder: BaseDataBindingHolder<MvvmListItemBinding>, item: MVVMListBean) {
    }
}