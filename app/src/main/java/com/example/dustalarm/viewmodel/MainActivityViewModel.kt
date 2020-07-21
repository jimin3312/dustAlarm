package com.example.dustalarm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dustalarm.DustNotiAlarm
import com.example.dustalarm.model.DTO.Addr
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.DTO.ViewResources
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainActivityViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    val dust: Dust by inject()
    val dustInfo: MutableLiveData<DustDTO>
    val viewResources: MutableLiveData<ViewResources>
    val address : MutableLiveData<Addr>

    init {
        load()
        dustInfo = dust.dustInfo
        viewResources = dust.viewResources
        address = dust.address
        DustNotiAlarm(application).regist()
    }

    fun load(){
        dust.load()
    }
}