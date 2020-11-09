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

    val address : MutableLiveData<String> = MutableLiveData()
    val isLoadingCompleted: MutableLiveData<Boolean> = MutableLiveData()
    var addressValue : Addr = Addr()

    init {
        disposable.add(
            geographyInfo.update()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoadingCompleted.value = false
                }
                .doOnError{
                    Log.d("에러", it.message)
                }
                .subscribe({
                    addressValue = Addr(it.adminArea?:"",it.locality?:"",it.subLocality?:"",it.thoroughfare?:"")
                    address.value = "${it.adminArea ?: ""} ${it.subLocality ?: ""} " + "${it.locality ?: ""} ${it.thoroughfare ?: ""}"
                },{})

//                .flatMap{
//                    Log.d("준다했잖아요12",it.locality?:"")
//                    Log.d("준다했잖아요13",it.subLocality?:"")
//                    Log.d("준다했잖아요1","${it.adminArea ?: ""} ${it.locality ?: ""}${it.subLocality ?: ""} ${it.thoroughfare ?: ""}")
//                    addressValue = Addr(it.adminArea?:"",it.locality?:"",it.subLocality?:"",it.thoroughfare?:"")
//                    address.value = "${it.adminArea ?: ""} ${it.subLocality ?: ""} " + "${it.locality ?: ""} ${it.thoroughfare ?: ""}"
//                    dustAPI.receiveTMLocation(umdName = it.thoroughfare).subscribeOn(Schedulers.io())
//                }
//                    .flatMap {
//                        var tmp: MsrstnInfoInqireSvrVo? = null
//                        val addr = addressValue
//                        it.list.forEach{
//                            if(it.umdName.equals( addr!!.thoroughfare) && it.sidoName.equals( addr.adminArea)) {
//                                tmp = it
//                            }
//                        }
//                        Log.d("준다했잖아요2", tmp.toString())
//                        Log.d("준다했잖아요2", "${tmp!!.tmX} ${tmp!!.tmY}")
//                        dustAPI.receiveStationName(tmX = tmp!!.tmX, tmY = tmp!!.tmY).subscribeOn(Schedulers.io())
//                    }
//                    .flatMap {
//                        Log.d("준다했잖아요3", it.toString())
//                        Log.d("준다했잖3", it.list[0].stationName)
//                        dustAPI.receivePm(stationNmae = it.list[0].stationName).subscribeOn(Schedulers.io())
//                    }
//                    .subscribe({
//                        Log.d("준다했잖아요4","${it.toString()}")
//                        Log.d("준다했잖아요4", "${it.list[0].pm10Value} ${it.list[0].pm25Value}")
//                        dustInfo.value = dust.getInfo(it.list[0])
//                        isLoadingCompleted.value = true
//                }, {})
        )

    }
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

//class MainActivity : AppCompatActivity() {
//    var count = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //val myObserver = MyObserver(this, lifecycle)
//        setContentView(R.layout.activity_main)
//        val datas = arrayListOf<String>("data1","data2","data3","data4","data5")
//        executeFlatMapForCompare()
//        executeConcatMapForCompare()
//    }
//
//    //flatMap은 interleaving이 허용되어
//    //데이터의 순서가 보장되지 않는다.
//    //1초마다 5번실행하는 flatMap
//    //flatMap을 통해서 Obervable.interval이 다시 실행되고
//    //1초마다 5번 실행된다. 그리고 상위 take의 count와 하위 take의 count를 출력한다.
//    //subsribe를 통해서 로그를 찍는다.
//    //Observable의 take가 실행된다.
//    //flatMap의 take가 실행된다.
//    //하지만 병렬적인 느낌으로 flatMap.take(5)로 5번 실행되기도 전에 Observable.interval.take(5)가 실행된다.
//    //결국 5초 정도로 순서가 허용되지 않고 데이터를 출력해버린다.
//    private fun executeFlatMapForCompare() = Observable
//        .interval(0, 1, TimeUnit.SECONDS)
//        .take(5)
//        .flatMap { index -> Observable.interval(0,1,TimeUnit.SECONDS).take(5).map { m-> "flat_index:$index $m"}}
//        .subscribe { updateData -> Log.d("flat#", updateData) }
//
//    //concatMap은 interleaving이 불허용되어
//    //데이터의 순서가 보장된다.
//    //1초마다 5번실행하는 flatMap
//    //flatMap을 통해서 Obervable.interval이 다시 실행되고
//    //1초마다 5번 실행된다. 그리고 상위 take의 count와 하위 take의 count를 출력한다.
//    //subsribe를 통해서 로그를 찍는다.
//    //Observable의 interval이 실행된다.
//    //concatMap의 Observable의 interval이 5번 실행된다.
//    //상위의 Observable은 concatMap이 끝나지 않았으므로
//    //계속해서 concatMap의 take가 끝날 때까지 출력한다.
//    //상위의 Observable 5번과 concatMap의 5번이 실행된다.
//    //5*5..
//    private fun executeConcatMapForCompare() = Observable
//        .interval(0, 1, TimeUnit.SECONDS)
//        .take(5)
//        .concatMap { index -> Observable.interval(0, 1, TimeUnit.SECONDS).take(5).map { m -> "concat_index:$index $m"} }
//        .subscribe { index -> Log.d("concat#", index) }
//}
