package com.example.dustalarm

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        val KEY = "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"
        val REQUEST_CODE = 1000
    }
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildLocationRequest()
        buildLocationCallBack()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

        DustNotiAlarm(this).regist()
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                val location = p0!!.locations.get(p0.locations.size - 1)

                val gCoder = Geocoder(this@MainActivity, Locale.getDefault())

                val addr: List<Address> =
                    gCoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addr.size > 0) {
                    val address: Address = addr[0]
                    mLocation.setText(address.adminArea + " " + address.subLocality + " " + address.thoroughfare)
                    var check10:Int
                    var check25:Int
                    var pm: Pair<Int, Int>
                    object : Thread() {
                        override fun run() {
                            super.run()
                            pm = DustAPI("구로구").recieveTMLocation()
                                .recieveStationName()
                                .recievePm10Pm25()
                            runOnUiThread(Runnable {
                                run()
                                {

                                    var pm10State:String
                                    var pm25State:String
                                    if(pm.first > 150)
                                    {
                                        pm10State=  "매우 나쁨"
                                        check10 = 4
                                    } else if(pm.first > 80)
                                    {
                                        pm10State=  "나쁨"
                                        check10 = 3
                                    } else if(pm.first > 80) {
                                        pm10State=  "보통"
                                        check10 = 2
                                    }
                                    else  {
                                        pm10State= "좋음"
                                        check10 =1
                                    }

                                    if(pm.second > 75)
                                    {
                                        pm25State=  "매우 나쁨"
                                        check25 = 4
                                    } else if(pm.second > 35)
                                    {
                                        pm25State=  "나쁨"
                                        check25 = 3
                                    } else if(pm.second > 15) {
                                        pm25State=  "보통"
                                        check25 = 2
                                    }
                                    else  {
                                        pm25State=  "좋음"
                                        check25 = 1
                                    }
                                    var maxValue = max(check10,check25)
                                    if(maxValue == 1)
                                    {
                                        dustStateFace.setImageResource(R.drawable.good)
                                        main_constraintLayout.setBackgroundColor(Color.parseColor("#87c1ff"))
                                    }else if(maxValue==2)
                                    {
                                        dustStateFace.setImageResource(R.drawable.normal2)
                                        main_constraintLayout.setBackgroundColor(Color.parseColor("#6b94c2"))
                                    }else if(maxValue==3)
                                    {
                                        dustStateFace.setImageResource(R.drawable.bad)
                                        main_constraintLayout.setBackgroundColor(Color.parseColor("#7e92a8"))
                                    }else if(maxValue==4)
                                    {
                                        dustStateFace.setImageResource(R.drawable.very_bad)
                                        main_constraintLayout.setBackgroundColor(Color.parseColor("#87888a"))
                                    }
                                    pm10.text = pm.first.toString()
                                    pm10_state.text = pm10State
                                    pm25.text = pm.second.toString()
                                    pm25_state.text = pm25State
                                }
                            })
//                        pm10.post{
//                            pm10.setText(pm.first)
//                        }
//                        pm25.post{
//                            pm25.setText(pm.second)
//                        }
                        }
                    }.start()
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }
    private fun max(a:Int ,b:Int):Int
    {
        if(a>b)
            return a
        else
            return b
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }
}
