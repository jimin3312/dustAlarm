package com.example.dustalarm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dustalarm.DustAPI
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.GeographyInfo
import com.example.dustalarm.model.TM
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
    val address: MutableLiveData<String> = MutableLiveData()
    val isLoadingCompleted: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposable.add(
            geographyInfo.update()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    isLoadingCompleted.value = false
                }
                .doOnComplete {
                    isLoadingCompleted.value = true
                    Log.d("완료", "끝났어요")
                }
                .doOnError {
                    isLoadingCompleted.value = true
                    Log.d("에러1", "에러가 왔자나요")
                }
                .concatMapSingle {
                    address.postValue("${it.adminArea ?: ""} ${it.locality ?: ""} " + "${it.subLocality ?: ""} ${it.thoroughfare ?: ""}")
                    dustAPI.receiveTMLocation(umdName = it.thoroughfare).subscribeOn(Schedulers.io())
                }
                .doOnError {
                    isLoadingCompleted.value = true
                    Log.d("에러2", "${it.message}")
                }
                .concatMapSingle {
                    Log.d("준다했잖아요2", "${it.list[0].tmX} ${it.list[0].tmX}")
                    dustAPI.receiveStationName(tmX = it.list[0].tmX, tmY = it.list[0].tmX).subscribeOn(Schedulers.io())
                }
                .doOnError {
                    isLoadingCompleted.value = true
                    Log.d("에러3", "${it.message}")
                }
                .concatMapSingle {
                    Log.d("준다했잖아요3", it.list[0].stationName)
                    dustAPI.receivePm(stationNmae = it.list[0].stationName).subscribeOn(Schedulers.io())
                }
                .doOnError {
                    isLoadingCompleted.value = true
                    Log.d("에러4", "${it.message}")
                }
                .subscribe({
//                    Log.d("준다했잖아요4", "${it.pm10} ${it.pm25}")
                    dustInfo.postValue(dust.getInfo(it.arpltnInforInqireSVCVo))
                }, {})
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}