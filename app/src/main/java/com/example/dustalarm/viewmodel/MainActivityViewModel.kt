package com.example.dustalarm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dustalarm.DustAPI
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.GeographyInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainActivityViewModel : ViewModel(), KoinComponent {
    val dust: Dust by inject()
    val dustAPI : DustAPI by inject()
    val geographyInfo : GeographyInfo by inject()
    val disposable = CompositeDisposable()

    val dustInfo: MutableLiveData<DustDTO> = MutableLiveData()
    val address : MutableLiveData<String> = MutableLiveData()
    val isLoadingCompleted: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposable.add(
            geographyInfo.update().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe{
                    isLoadingCompleted.value = false
                }
                .flatMap {
                    address.value = "${it.adminArea ?: ""} ${it.locality ?: ""} " + "${it.subLocality ?: ""} ${it.thoroughfare ?: ""}"
                    dustAPI.receiveTMLocation(umdName= "혜화동")
                }.flatMap {
                    dustAPI.receiveStationName(tmX = it.tmX, tmY = it.tmY)
                }.flatMap {
                    dustAPI.receivePm(stationNmae = it.name)
                }.subscribe({
                    dustInfo.value = dust.getInfo(it)
                    isLoadingCompleted.value=true
                },{})
        )

    }
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}