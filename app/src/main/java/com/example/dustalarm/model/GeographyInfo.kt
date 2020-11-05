package com.example.dustalarm.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.*
import io.reactivex.subjects.PublishSubject
import java.util.*

class GeographyInfo(val context: Context) {
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    val single: PublishSubject<Address> = PublishSubject.create()
    val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        buildLocationRequest()
        buildLocationCallback()

    }
    fun update() : PublishSubject<Address> {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

//        fusedLocationProviderClient.remove
        return single
        //return obaservable
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations[p0.locations.size - 1]
                val gCoder = Geocoder(context, Locale.getDefault())
                val addr: List<Address> =
                    gCoder.getFromLocation(location.latitude, location.longitude, 1)

                single.onNext(addr[0])
                single.onComplete()
            }
        }
    }
}