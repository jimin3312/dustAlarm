package com.example.dustalarm.di

import com.example.dustalarm.DustAPI
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.GeographyInfo
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {
    single { Dust() }
    factory { GeographyInfo(androidApplication()) }
}