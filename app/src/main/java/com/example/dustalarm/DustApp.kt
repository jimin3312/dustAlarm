package com.example.dustalarm

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.dustalarm.di.dataModule
import com.example.dustalarm.di.loadingModule
import com.example.dustalarm.di.networkModule
import com.example.dustalarm.di.viewModelModule
import com.example.dustalarm.view.MainActivity
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DustApp : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DustApp)
            modules(dataModule, loadingModule, viewModelModule, networkModule)
        }
    }
}