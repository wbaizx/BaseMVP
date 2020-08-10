package com.basemvp.main.mvvm

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvvm.BaseMVVMActivity
import com.base.common.config.RouteString
import com.basemvp.R

@Route(path = RouteString.MVVM_ROOM, name = "展示mvvm + room用法", extras = RouteString.isNeedLogin)
class MVVMDemoActivity : BaseMVVMActivity() {
    //    val aa: MutableLiveData<String>
    //        viewModelScope.launch {  }
//        ViewModelProvider(this).get()

    override fun getContentView() = R.layout.activity_mvvm_demo

    override fun initView() {
    }

    override fun initData() {
    }
}