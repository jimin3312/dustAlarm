package com.example.dustalarm.model

import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dustalarm.DustAPI
import com.example.dustalarm.R
import com.google.android.gms.location.*
import java.util.*

class Dust(val context: Context) {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    var dustInfo = MutableLiveData<DustDao>()
    var dustImg = MutableLiveData<Int>()
    var dustBackgroundColor = MutableLiveData<Int>()
    fun getInfo(){

        buildLocationRequest()
        buildLocationCallBack()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                Log.d("로그","locationCallback")
                val location = p0!!.locations.get(p0.locations.size - 1)

                val gCoder = Geocoder(context, Locale.getDefault())

                val addr: List<Address> =
                    gCoder.getFromLocation(location.latitude, location.longitude, 1)

                if (addr.size > 0) {
                    val address: Address = addr[0]

                    val newDustDao = DustDao()
                    newDustDao.location = "${address.adminArea} ${address.subLocality} ${address.thoroughfare}"

                    var check10: Int
                    var check25: Int
                    var pm: Pair<Int, Int>

                    object : Thread() {
                        override fun run() {
                            super.run()
                            Log.d("로그","workerThread")
                            pm = DustAPI(address.thoroughfare)
                                .recieveTMLocation()
                                .recieveStationName()
                                .recievePm10Pm25()

                            var pm10State: String
                            var pm25State: String
                            if (pm.first > 150) {
                                pm10State = "매우 나쁨"
                                check10 = 4
                            } else if (pm.first > 80) {
                                pm10State = "나쁨"
                                check10 = 3
                            } else if (pm.first > 80) {
                                pm10State = "보통"
                                check10 = 2
                            } else {
                                pm10State = "좋음"
                                check10 = 1
                            }

                            if (pm.second > 75) {
                                pm25State = "매우 나쁨"
                                check25 = 4
                            } else if (pm.second > 35) {
                                pm25State = "나쁨"
                                check25 = 3
                            } else if (pm.second > 15) {
                                pm25State = "보통"
                                check25 = 2
                            } else {
                                pm25State = "좋음"
                                check25 = 1
                            }
                            var maxValue = Math.max(check10, check25)
                            var resId : Int = R.drawable.normal
                            var color : Int = Color.parseColor("#6b94c2")

                            if (maxValue == 1) {
                                resId = R.drawable.good
                                color = Color.parseColor("#87c1ff")
                            } else if (maxValue == 2) {
                                resId = R.drawable.normal
                                color = Color.parseColor("#6b94c2")
                            } else if (maxValue == 3) {
                                resId = R.drawable.bad
                                color = Color.parseColor("#7e92a8")
                            } else if (maxValue == 4) {
                                resId = R.drawable.very_bad
                                color = Color.parseColor("#87888a")
                            }

                            Log.d("로그","pm10 ${pm.first}")
                            Log.d("로그","pm25 ${pm.second}")
                            Log.d("로그","pm10 state $pm10State")
                            Log.d("로그","pm25 state $pm25State")

                            newDustDao.pm10Value = pm.first.toString()
                            newDustDao.pm25Value = pm.second.toString()
                            newDustDao.pm10State = pm10State
                            newDustDao.pm25State = pm25State

                            dustInfo.postValue(newDustDao)
                            dustBackgroundColor.postValue(color)
                            dustImg.postValue(resId)
                        }
                    }.start()
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }
}