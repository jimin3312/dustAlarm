package com.example.dustalarm

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
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
                val address: Address = addr[0]
                mLocation.setText(address.adminArea + " "+ address.subLocality+ " " + address.thoroughfare)

                var pm: Pair<String, String>
                object : Thread(){
                    override fun run() {
                        super.run()
                        pm = DustAPI(address.thoroughfare).recieveTMLocation()
                            .recieveStationName()
                            .recievePm10Pm25()
                        pm10.post{
                            pm10.setText(pm.first)
                        }
                        pm25.post{
                            pm25.setText(pm.second)
                        }
                    }
                }.start()

                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
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
