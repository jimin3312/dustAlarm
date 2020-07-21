package com.example.dustalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dustalarm.view.MainActivity
import com.google.android.gms.location.*
import java.util.*

class NotiIntentService : JobIntentService(){
    val CHANNEL_ID = "maskPush"
    val NOTIFICATION_ID = 123
    lateinit var pm: Pair<Int, Int>
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun enqueueJob(context: Context, intent: Intent){
        Log.d("노티 1", " ")
        enqueueWork(context, NotiIntentService::class.java, 1000, intent)
    }

    override fun onHandleWork(intent: Intent) {
        Log.d("노티 2", " ")
        
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val latitude = intent.getDoubleExtra("latitude", 0.0)

        Log.d("위도" , "$longitude")
        Log.d("경도" , "$latitude")

        val gCoder = Geocoder(this@NotiIntentService, Locale.getDefault())
        val addr: List<Address> =
            gCoder.getFromLocation(latitude, longitude, 1)
        val address: Address = addr[0]

        pm = DustAPI(address.thoroughfare).recieveTMLocation()
            .recieveStationName()
            .recievePm10Pm25()

        createNotificationChannel()
        pushNotification()

//        buildLocationCallBack()
//        buildLocationRequest()
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        fusedLocationProviderClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            Looper.myLooper()
//        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.getString(R.string.channel_name)
            val descriptionText = this.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    private fun pushNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("미세 먼지")
            .setContentText("미세 먼지 : " + pm.first + " 초미세 먼지 : " + pm.second)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }

    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                val location = p0!!.locations.get(p0.locations.size - 1)

                val gCoder = Geocoder(this@NotiIntentService, Locale.getDefault())
                val addr: List<Address> =
                    gCoder.getFromLocation(location.latitude, location.longitude, 1)
                val address: Address = addr[0]

                pm = DustAPI(address.thoroughfare).recieveTMLocation()
                    .recieveStationName()
                    .recievePm10Pm25()

                createNotificationChannel()
                pushNotification()

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