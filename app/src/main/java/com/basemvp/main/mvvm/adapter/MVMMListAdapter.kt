package com.basemvp.main.mvvm.adapter

import com.base.common.base.adapter.BaseListAdapter
import com.basemvp.R
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MVMMListAdapter : BaseListAdapter<MVVMBindBean, BaseViewHolder>(R.layout.mvvm_list_item) {

    init {
        repeat(20) {
            data.add(MVVMBindBean(it.toString()))
        }
    }

    override fun convertUI(holder: BaseViewHolder, item: MVVMBindBean) {
        holder.setText(R.id.tex, item.id)
    }
}