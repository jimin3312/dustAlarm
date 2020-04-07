package com.example.dustalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PushService : Service(){
    val CHANNEL_ID = "maskPush"
    val NOTIFICATION_ID = 123

    lateinit var notificationManager: NotificationManager

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        createNotificationChannel()
//        pushNotification()

        return START_STICKY
    }




}

