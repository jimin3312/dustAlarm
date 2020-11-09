package com.example.dustalarm.model
import com.google.gson.annotations.SerializedName

data class PM (
    @SerializedName("list")val list: List<ArpltnInforInqireSVCVo>,
    @SerializedName("totalCount")val totalCount: Long
)

data class ArpltnInforInqireSVCVo (
    @SerializedName("pm10Value") val pm10Value: String,
    @SerializedName("pm25Value") val pm25Value: String
)