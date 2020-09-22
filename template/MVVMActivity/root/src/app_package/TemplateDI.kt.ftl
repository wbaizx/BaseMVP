package ${escapeKotlinIdentifiers(packageName)}

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ${mvvmCommonName}Di = module {
    //默认整个app全局单例
    single { ${mvvmCommonName}Repository() }
    //默认单个activity生命周期内单例
    viewModel { ${mvvmCommonName}ViewModel(get()) }
}