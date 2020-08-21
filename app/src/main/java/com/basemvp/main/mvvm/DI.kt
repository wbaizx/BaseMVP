package com.basemvp.main.mvvm

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mvvmDemoDi = module {
    //默认整个app全局单例
    single { MVVMDemoRepository() }
    //默认单个activity生命周期内单例
    viewModel { MVVMDemoViewModel(get()) }
}