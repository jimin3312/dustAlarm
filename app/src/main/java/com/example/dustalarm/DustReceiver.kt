//package com.example.dustalarm
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.location.Address
//import android.location.Geocoder
//import android.location.Location
//import android.os.Build
//import android.os.Looper
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.dustalarm.view.MainActivity
//import com.google.android.gms.location.*
//import java.util.*
//import kotlin.math.log
//
//class DustReceiver : BroadcastReceiver() {
//
//    var longitude: Double? = null
//    var latitude: Double? = null
//
//    lateinit var pm: Pair<Int, Int>
//    lateinit var locationRequest: LocationRequest
//    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    lateinit var context: Context
//
//    override fun onReceive(context: Context, intent: Intent?) {
//        this.context = context
//        buildLocationRequest()
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
//
//        if (intent!!.action.equals("android.intent.action.Main")) {
//            val pendingIntent =
//                PendingIntent.getBroadcast(context,
//                    0,
//                    Intent(context, DustReceiver::class.java).apply { action = "location" },
//                    PendingIntent.FLAG_UPDATE_CURRENT)
//
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, pendingIntent)
//        }
//        else if(intent.action.equals("dust")){
//            val pendingIntent =
//                PendingIntent.getBroadcast(context,
//                    0,
//                    Intent(context, DustReceiver::class.java).apply { action = "load" },
//                    PendingIntent.FLAG_UPDATE_CURRENT)
//
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, pendingIntent)
//        }
//        else {
//            val location: LocationResult? = LocationResult.extractResult(intent)
//            val loc: Location? = location?.lastLocation
//
//            if(loc != null){
//                latitude = loc.latitude
//                longitude = loc.longitude
//            }
//
//            if(latitude != null && longitude != null){
//                val serviceIntent = Intent(context, NotiIntentService::class.java)
//                serviceIntent.putExtra("latitude", latitude as Double)
//                serviceIntent.putExtra("longitude", longitude as Double)
//                serviceIntent.action =
//                    if(intent.action .equals("location")) "notify" else "load"
//                NotiIntentService().enqueueJob(context, serviceIntent)
//
//            }
//        }
//    }
//
//    private fun buildLocationRequest() {
//        locationRequest = LocationRequest()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 5000
//        locationRequest.fastestInterval = 3000
//    }
//}