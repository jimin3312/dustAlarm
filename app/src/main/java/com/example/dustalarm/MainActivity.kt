package com.example.dustalarm

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Stream

class MainActivity : AppCompatActivity() {

    companion object{
        val KEY = "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"
        val REQUEST_CODE = 1000
    }
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var service: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//            btn1.setOnClickListener(View.OnClickListener {
//                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
//                {
//                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
//                    return@OnClickListener
//                }
//                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
//            })

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

//        startService(Intent(this, PushService::class.java))

    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                val location = p0!!.locations.get(p0.locations.size - 1)
                Log.d(
                    "locationTAG",
                    "latitude : " + location.latitude.toString() + " longitude : " + location.longitude.toString()
                )

                val gCoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addr: List<Address> =
                    gCoder.getFromLocation(location.latitude, location.longitude, 1)
                val address: Address = addr[0]
                mLocation.setText(address.adminArea + " " + address.countryName + " " + address.thoroughfare + address.featureName + address.locale + address.subLocality)

                var umd: String
                var xy: Pair<String, String>
                var stationName: String
                var pm: Pair<String, String>

                if(address.adminArea.equals("서울특별시"))
                {
                    pm = DustAPI(address.subLocality).recievePm10Pm25()
                }else
                {
                    pm = DustAPI(address.thoroughfare).recieveTMLocation()
                        .recieveStationName()
                        .recievePm10Pm25()
                }

                pm10.setText(pm.first)
                pm25.setText(pm.second)
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

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "Permisson granted", Toast.LENGTH_SHORT)
                            .show()
                    else
                        finish()
                }
            }
        }
    }

}

