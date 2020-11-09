package com.example.dustalarm
//
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DustReceiver : BroadcastReceiver() {
    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent?) {
        this.context = context

        if (intent!!.action == "dust.alarm") {
            val serviceIntent = Intent(context, NotiIntentService::class.java)
            NotiIntentService().enqueueJob(context, serviceIntent)
        }

    }
}