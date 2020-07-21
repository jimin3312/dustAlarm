package com.example.dustalarm.di

import com.example.dustalarm.DustAPI
import com.example.dustalarm.model.Dust
import org.koin.dsl.module

val dataModule = module {
    single { Dust(get()) }
}