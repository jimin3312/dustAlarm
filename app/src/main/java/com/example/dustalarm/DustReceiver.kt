package com.example.dustalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dustalarm.model.GeographyInfo
import com.example.dustalarm.model.MsrstnInfoInqireSvrVo
import com.example.dustalarm.model.PM
import com.example.dustalarm.view.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class DustReceiver : BroadcastReceiver(), KoinComponent {
    val CHANNEL_ID = "maskPush"
    val NOTIFICATION_ID = 123
    val dustAPI: DustAPI by inject()
    val geographyInfo: GeographyInfo by inject()
    val disposable = CompositeDisposable()

    lateinit var adminArea: String
    lateinit var thoroughfare: String
    lateinit var pm: PM
    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent?) {
        this.context = context
        if (intent!!.action == "dust.alarm") {
            disposable.add(
                geographyInfo.update()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        Log.d("에러", it.message)
                    }
                    .flatMap {
                        adminArea = it.adminArea
                        thoroughfare = it.thoroughfare
                        Log.d("주소", "$adminArea $thoroughfare")
                        dustAPI.receiveTMLocation(umdName = it.thoroughfare)
                            .subscribeOn(Schedulers.io())
                    }
                    .flatMap {
                        var tmp: MsrstnInfoInqireSvrVo? = null
                        it.list.forEach {
                            if (it.umdName.equals(thoroughfare) && it.sidoName.equals(adminArea)) {
                                tmp = it
                            }
                        }
                        dustAPI.receiveStationName(tmX = tmp!!.tmX, tmY = tmp!!.tmY)
                            .subscribeOn(Schedulers.io())
                    }
                    .flatMap {
                        dustAPI.receivePm(stationNmae = it.list[0].stationName)
                            .subscribeOn(Schedulers.io())
                    }
                    .subscribe({
                        pm = it
                        createNotificationChannel()
                        pushNotification()
                    }, {})
            )
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun pushNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("미세 먼지")
            .setContentText("미세 먼지 : " + pm.list[0].pm10Value + " 초미세 먼지 : " + pm.list[0].pm25Value)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }

    }
}