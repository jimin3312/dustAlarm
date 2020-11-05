//package com.example.dustalarm
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.location.Address
//import android.location.Geocoder
//import android.os.Build
//import androidx.core.app.JobIntentService
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.dustalarm.model.DTO.DustDTO
//import com.example.dustalarm.model.Dust
//import com.example.dustalarm.view.MainActivity
//import org.koin.android.ext.android.inject
//import java.util.*
//
//class NotiIntentService : JobIntentService(){
//    val CHANNEL_ID = "maskPush"
//    val NOTIFICATION_ID = 123
//    lateinit var pm: Pair<Int, Int>
//    var longitude: Double = 0.0
//    var latitude: Double = 0.0
//
//    fun enqueueJob(context: Context, intent: Intent) {
//        enqueueWork(context, NotiIntentService::class.java, 1000, intent)
//    }
//
//
//    override fun onHandleWork(intent: Intent) {
//        longitude = intent.getDoubleExtra("longitude", 0.0)
//        latitude = intent.getDoubleExtra("latitude", 0.0)
//
//        val gCoder = Geocoder(this@NotiIntentService, Locale.getDefault())
//        val addr: List<Address> =
//            gCoder.getFromLocation(latitude, longitude, 1)
//        val address: Address = addr[0]
//
//        pm = DustAPI(address.thoroughfare).recieveTMLocation()
//            .recieveStationName()
//            .recievePm10Pm25()
//
//        if(intent.action == "notify"){
//            createNotificationChannel()
//            pushNotification()
//        }else{
//            updateData()
//        }
//
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = this.getString(R.string.channel_name)
//            val descriptionText = this.getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager =
//                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//
//        }
//    }
//
//    private fun pushNotification() {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setContentTitle("미세 먼지")
//            .setContentText("미세 먼지 : " + pm.first + " 초미세 먼지 : " + pm.second)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(this)) {
//            notify(NOTIFICATION_ID, builder.build())
//        }
//
//    }
//
//
//}