package com.example.dustalarm

import android.app.Application
import com.example.dustalarm.di.dataModule
import com.example.dustalarm.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DustApp : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DustApp)
            modules(dataModule, viewModelModule)
        }
    }
}