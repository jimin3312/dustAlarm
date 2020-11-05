package com.example.dustalarm.model

import android.graphics.Color
import com.example.dustalarm.R
import com.example.dustalarm.model.DTO.DustDTO
import org.koin.core.KoinComponent

class Dust {

    fun getInfo(pm: PM) : DustDTO{


        val pm10State: String
        val pm25State: String
        val pm10 = pm.pm10.toInt()
        val pm25 =pm.pm25.toInt()
        var check10 : Int =0
        var check25 : Int =0

        if (pm10 > 150) {
            pm10State = "매우 나쁨"
            check10 = 4
        } else if (pm10 > 80) {
            pm10State = "나쁨"
            check10 = 3
        } else if (pm10 > 80) {
            pm10State = "보통"
            check10 = 2
        } else {
            pm10State = "좋음"
            check10 = 1
        }

        if (pm25> 75) {
            pm25State = "매우 나쁨"
            check25 = 4
        } else if (pm25> 35) {
            pm25State = "나쁨"
            check25 = 3
        } else if (pm25 > 15) {
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

        var dustDTO = DustDTO(pm.pm10, pm.pm25, pm10State,pm25State,resId,color)
        return dustDTO
    }
}