package com.example.dustalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dustalarm.model.DTO.Addr
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.DTO.ViewResources
import com.example.dustalarm.model.Dust
import com.example.dustalarm.view.MainActivity
import com.google.android.gms.location.*
import org.koin.android.ext.android.inject
import java.util.*

class NotiIntentService : JobIntentService(){
    val CHANNEL_ID = "maskPush"
    val NOTIFICATION_ID = 123
    lateinit var pm: Pair<Int, Int>
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    fun enqueueJob(context: Context, intent: Intent) {
        enqueueWork(context, NotiIntentService::class.java, 1000, intent)
    }


    override fun onHandleWork(intent: Intent) {
        longitude = intent.getDoubleExtra("longitude", 0.0)
        latitude = intent.getDoubleExtra("latitude", 0.0)

        val gCoder = Geocoder(this@NotiIntentService, Locale.getDefault())
        val addr: List<Address> =
            gCoder.getFromLocation(latitude, longitude, 1)
        val address: Address = addr[0]

        pm = DustAPI(address.thoroughfare).recieveTMLocation()
            .recieveStationName()
            .recievePm10Pm25()

        if(intent.action == "notify"){
            createNotificationChannel()
            pushNotification()
        }else{
            updateData()
        }

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
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }

    }

    private fun updateData(){
        val dust: Dust by inject()
        val gCoder = Geocoder(this, Locale.getDefault())

        val addr: List<Address> =
            gCoder.getFromLocation(latitude, longitude, 1)

        if (addr.size > 0) {
            val address: Address = addr[0]
            dust.address.postValue(Addr("${address.adminArea} ${address.subLocality} ${address.thoroughfare}"))

            val check10: Int
            val check25: Int

            val pm: Pair<Int, Int> =
                DustAPI(address.thoroughfare)
                    .recieveTMLocation()
                    .recieveStationName()
                    .recievePm10Pm25()

            val pm10State: String
            val pm25State: String
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
            val maxValue = Math.max(check10, check25)
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

            dust.dustInfo.postValue(
                DustDTO(
                    pm.first.toString(),
                    pm.second.toString(),
                    pm10State,
                    pm25State
                )
            )
            dust.viewResources.postValue(ViewResources(resId, color))
            dust.isLoadingCompleted.postValue(true)
        }
    }
}