package com.basemvp

import com.base.common.BaseAPP
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module

class MainApp : BaseAPP() {
    override fun initKoin() {
        /**
         * 所有模块的 总module 添加到这里
         */
        val modules = arrayListOf<Module>().apply {
            addAll(mainDiList)
        }
        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            // 使用默认的 default Level.INFO 在kotlin版本1.4.0会崩溃 NoSuchMethodError
            androidLogger(Level.ERROR)
            // use the Android context given there
            androidContext(this@MainApp)
            // load properties from assets/koin.properties file
            androidFileProperties()
            // module list
            modules(modules)
        }
    }
}