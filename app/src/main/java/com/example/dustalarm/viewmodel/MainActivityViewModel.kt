package com.example.dustalarm.viewmodel

import android.location.Address
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dustalarm.DustAPI
import com.example.dustalarm.model.*
import com.example.dustalarm.model.DTO.Addr
import com.example.dustalarm.model.DTO.DustDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.URLEncoder

class MainActivityViewModel : ViewModel(), KoinComponent {
    val dust: Dust by inject()
    val dustAPI: DustAPI by inject()
    val geographyInfo: GeographyInfo by inject()
    val disposable = CompositeDisposable()
    val dustInfo: MutableLiveData<DustDTO> = MutableLiveData()

    val address: MutableLiveData<String> = MutableLiveData()
    val isLoadingCompleted: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var addressValue: Addr

    fun loadDust() {
        disposable.add(
            geographyInfo.update()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoadingCompleted.value = false
                }
                .doOnError {
                    Log.d("에러", it.message)
                }
                .flatMap {
                    address.value =
                        "${it.adminArea ?: ""} ${it.subLocality ?: ""}" + "${it.locality ?: ""} ${it.thoroughfare ?: ""}"
                    addressValue = Addr(it.adminArea, it.locality, it.subLocality, it.thoroughfare)
                    dustAPI.receiveTMLocation(umdName = it.thoroughfare)
                        .subscribeOn(Schedulers.io())
                }
                .flatMap {
                    var tmp: MsrstnInfoInqireSvrVo? = null
                    val addr = addressValue
                    it.list.forEach {
                        if (it.umdName.equals(addr.thoroughfare) && it.sidoName.equals(addr.adminArea)) {
                            tmp = it
                        }
                    }
                    Log.d("TM", tmp!!.tmX)
                    dustAPI.receiveStationName(tmX = tmp!!.tmX, tmY = tmp!!.tmY)
                        .subscribeOn(Schedulers.io())
                }
                .flatMap {
                    Log.d("Station", it.list[0].stationName)
                    dustAPI.receivePm(stationNmae = it.list[0].stationName)
                        .subscribeOn(Schedulers.io())
                }
                .subscribe({
                    dustInfo.postValue(dust.getInfo(it.list[0]))
                    isLoadingCompleted.postValue(true)
                }, {})

        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}