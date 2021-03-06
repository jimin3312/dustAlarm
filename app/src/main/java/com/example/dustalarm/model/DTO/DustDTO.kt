package com.example.dustalarm.model.DTO

import android.graphics.Color
import com.example.dustalarm.R

data class DustDTO(
    var pm10Value: String = "",
    var pm25Value: String = "",
    var pm10State: String = "",
    var pm25State: String = "",
    val resId: Int = R.drawable.good,
    val backgroundColor: Int = Color.parseColor("#87c1ff")
)