package com.basemvp

import com.base.common.BaseAPP
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class MainApp : BaseAPP() {
    override fun initKoin() {
        val modules = arrayListOf<Module>().apply {
            addAll(mainDiList)
        }
        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()
            // use the Android context given there
            androidContext(this@MainApp)
            // load properties from assets/koin.properties file
            androidFileProperties()
            // module list
            modules(modules)
        }
    }
}