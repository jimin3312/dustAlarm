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

    fun register() {
        val intent: Intent = Intent(context, DustReceiver::class.java)

        val sender: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MINUTE, 55)
        }
        //알람 예약
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, 1000 * 60, sender)
    }
}