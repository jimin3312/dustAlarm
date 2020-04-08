package com.example.dustalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class DustNotiAlarm {
    val context: Context
    val alarmManager: AlarmManager

    constructor(context: Context) {
        this.context = context
        this.alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun regist() {
        val intent: Intent = Intent(context, DustReceiver::class.java)
        intent.action = "android.intent.action.Main"

        val registerdSender: PendingIntent? = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
//            set(Calendar.MINUTE, 3)
        }

        if(registerdSender == null){
            val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, sender)
        }
    }
}