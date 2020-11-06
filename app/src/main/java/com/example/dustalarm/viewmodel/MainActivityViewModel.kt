package com.example.dustalarm.viewmodel

import android.location.Address
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

    val address : MutableLiveData<String> = MutableLiveData()
    val isLoadingCompleted: MutableLiveData<Boolean> = MutableLiveData()


    var addressValue : Addr = Addr()
    init {
        address.value = ""

        disposable.add(
            geographyInfo.update()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    isLoadingCompleted.value = false
                }
                .concatMapSingle {
                    Log.d("준다했잖아요12",it.locality?:"")
                    Log.d("준다했잖아요13",it.subLocality?:"")
                    Log.d("준다했잖아요1","${it.adminArea ?: ""} ${it.locality ?: ""} " + "${it.subLocality ?: ""} ${it.thoroughfare ?: ""}")
                    addressValue = Addr(it.adminArea?:"",it.locality?:"",it.subLocality?:"",it.thoroughfare?:"")
                    address.postValue("${it.adminArea ?: ""} ${it.subLocality ?: ""} " + "${it.locality ?: ""} ${it.thoroughfare ?: ""}")
                    dustAPI.receiveTMLocation(umdName = it.thoroughfare).subscribeOn(Schedulers.io())
                }
                .doOnError {
                    isLoadingCompleted.postValue(true)
                    Log.d("에러1", "${it.message}")
                }
                .concatMapSingle {

                    var tmp: MsrstnInfoInqireSvrVo? = null
                    val addr = addressValue
                    it.list.forEach{
                        if(it.umdName.equals( addr!!.thoroughfare) && it.sidoName.equals( addr.adminArea)) {
                            var locality=""
                            if(!addr.locality.equals("")){
                                locality = addr.subLocality+" "+addr.locality
                            } else {
                               locality= addr.subLocality
                            }
                            if(it.sggName == locality)
                                tmp = it
                        }
                    }
                    Log.d("준다했잖아요2",tmp.toString())
                    Log.d("준다했잖아요2", "${tmp!!.tmX} ${tmp!!.tmY}")
                    dustAPI.receiveStationName(tmX = tmp!!.tmX, tmY = tmp!!.tmY).subscribeOn(Schedulers.io())

                }
                .doOnError {
                    isLoadingCompleted.postValue(true)
                    Log.d("에러2", "${it.message}")
                }
                .concatMapSingle {
                    Log.d("준다했잖아요3", it.toString())
                    Log.d("준다했잖3", it.list[0].stationName)
                    dustAPI.receivePm(stationNmae = it.list[0].stationName).subscribeOn(Schedulers.io())
                }
                .doOnComplete {
                    isLoadingCompleted.postValue(true)
                    Log.d("완료", "끝났어요")
                }
                .doOnError {
                    isLoadingCompleted.postValue(true)
                    Log.d("에러3", "${it.message}")
                }
                .subscribe({
                    Log.d("준다했잖아요4","${it.toString()}")
                    Log.d("준다했잖아요4", "${it.list[0].pm10Value} ${it.list[0].pm25Value}")
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