package com.example.dustalarm.viewmodel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dustalarm.DustAPI
import com.example.dustalarm.DustNotiAlarm
import com.example.dustalarm.model.DTO.Addr
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.DTO.ViewResources
import com.example.dustalarm.model.GeographyInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
//    val dust: Dust by inject()
    val dustAPI : DustAPI by inject()
    val geographyInfo : GeographyInfo by inject()
    val disposable = CompositeDisposable()
//    val dustInfo: MutableLiveData<DustDTO>
//    val viewResources: MutableLiveData<ViewResources>
    lateinit var address : MutableLiveData<String>
//    val isLoadingCompleted: MutableLiveData<Boolean>
    init {
//        load()
//        isLoadingCompleted = dust.isLoadingCompleted
//        dustInfo = dust.dustInfo
//        viewResources = dust.viewResources
////        address = dust.address
//        DustNotiAlarm(application).regist()
        disposable.add(
            geographyInfo.update().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap {
                    address.value = "${it.adminArea ?: ""} ${it.locality ?: ""} " + "${it.subLocality ?: ""} ${it.thoroughfare ?: ""}"
                    dustAPI.receiveTMLocation(umdName= it.thoroughfare)
                }.flatMap {
                    dustAPI.receiveStationName(tmX = it.tmX, tmY = it.tmY)
                }.flatMap {
                    dustAPI.receivePm(stationNmae = it.name)
                }.subscribe({

                },{})
        )

    }

//    fun load(){
//        dust.load()
//    }
//
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}