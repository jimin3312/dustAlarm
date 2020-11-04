package com.example.dustalarm.model
import com.google.gson.annotations.SerializedName

data class PM(
    @SerializedName("pm10Value") val pm10: String,
    @SerializedName("pm25Value") val pm25: String)