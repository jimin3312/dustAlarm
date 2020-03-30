package com.example.dustalarm

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


const val KEY = "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else
        {
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

            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
                }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

    }

    private fun buildLocationCallBack() {
        locationCallback = object :LocationCallback(){

            override fun onLocationResult(p0: LocationResult?) {
                var location =  p0!!.locations.get(p0!!.locations.size-1)
                Log.d("locationTAG", "latitude : " + location.latitude.toString() + " longitude : " + location.longitude.toString())

                val gCoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addr: List<Address> = gCoder.getFromLocation(location.latitude, location.longitude, 1)
                val a: Address = addr[0]
                myLocation.setText(a.adminArea.toString() + " " + a.locality + " " + a.thoroughfare)
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
        when(requestCode)
        {
            REQUEST_CODE->{
                if(grantResults.size > 0)
                {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "Permisson granted", Toast.LENGTH_SHORT)
                            .show()
                    else
                        Toast.makeText(this, "Permisson denied", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }
    }
}
