package com.basemvp.main.mvvm

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mvvmDemoDi = module {
    single { MVVMDemoRepository() }
    viewModel { MVVMDemoViewModel(get()) }
}