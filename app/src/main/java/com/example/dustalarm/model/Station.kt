package com.example.dustalarm.model

import com.google.gson.annotations.SerializedName

// result generated from /json


data class Station (
    @SerializedName("list")val list: List<MsrstnInfoInqireSvrVo2>,
    @SerializedName("totalCount")val totalCount: Long
)

data class MsrstnInfoInqireSvrVo2 (
    @SerializedName("stationName") val stationName : String,
    @SerializedName("addr") val addr : String ,
    @SerializedName("tm") val tm : String=""
)