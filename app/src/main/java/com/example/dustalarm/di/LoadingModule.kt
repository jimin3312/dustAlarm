package com.example.dustalarm.di

import android.app.Activity
import com.example.dustalarm.view.LoadingDialog
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val loadingModule = module {
    factory { (activity: Activity) -> LoadingDialog(activity) }
}