package com.example.dustalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class DustNotiAlarm(val context: Context) {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun regist() {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(context, DustReceiver::class.java).apply {
            action = "dust.alarm"
        }
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1000 * 60, sender)

    }
}