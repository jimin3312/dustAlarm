package com.example.dustalarm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dustalarm.DustAPI
import com.example.dustalarm.model.*
import com.example.dustalarm.model.DTO.DustDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainActivityViewModel : ViewModel(), KoinComponent {
    val dust: Dust by inject()
    val dustAPI: DustAPI by inject()
    val geographyInfo: GeographyInfo by inject()
    val disposable = CompositeDisposable()
    val dustInfo: MutableLiveData<DustDTO> = MutableLiveData()

    lateinit var adminArea: String
    lateinit var thoroughfare: String
    val address: MutableLiveData<String> = MutableLiveData()
    val isLoadingCompleted: MutableLiveData<Boolean> = MutableLiveData()

    fun loadDust() {
        disposable.add(
            geographyInfo.update()
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoadingCompleted.postValue(false)
                }
                .doOnError {
                    Log.d("에러", it.message)
                }
                .flatMap {
                    address.value =
                        "${it.adminArea ?: ""} ${it.subLocality ?: ""}" + "${it.locality ?: ""} ${it.thoroughfare ?: ""}"
                    adminArea = it.adminArea
                    thoroughfare = it.thoroughfare
                    dustAPI.receiveTMLocation(umdName = it.thoroughfare)
                        .subscribeOn(Schedulers.io())
                }
                .flatMap {
                    var tmp: MsrstnInfoInqireSvrVo? = null
                    it.list.forEach {
                        if (it.umdName.equals(thoroughfare) && it.sidoName.equals(adminArea)) {
                            tmp = it
                        }
                    }
                    dustAPI.receiveStationName(tmX = tmp!!.tmX, tmY = tmp!!.tmY)
                        .subscribeOn(Schedulers.io())
                }
                .flatMap {
                    dustAPI.receivePm(stationNmae = it.list[0].stationName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribe({
                    dustInfo.value = dust.getInfo(it.list[0])
                    isLoadingCompleted.value = true
                }, {})

        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}