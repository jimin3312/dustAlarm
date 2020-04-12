package com.example.dustalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class DustNotiAlarm {
    val context: Context
    val alarmManager: AlarmManager

    constructor(context: Context) {
        this.context = context
        this.alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun register() {
        var intent = Intent(context, DustReceiver::class.java)
        intent.action = "android.intent.action.Main"
        var alarmUP  = (PendingIntent.getBroadcast(context, 0, intent,  PendingIntent.FLAG_NO_CREATE))
        if(alarmUP == null)
        {
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                intent, 0
            )
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
            }
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }

        //val sender: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

//        val registerdSender: PendingIntent? = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE)

//
//        if(alarmUP == null){
//            val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, registerdSender)
//            Log.d("TEST","alarm")
//        }
        //알람 예약

        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, sender)

    }
}