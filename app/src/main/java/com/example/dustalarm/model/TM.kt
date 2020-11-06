package com.example.dustalarm.model

import com.google.gson.annotations.SerializedName

// result generated from /json

data class TM (
    @SerializedName("list")val list: List<MsrstnInfoInqireSvrVo>,
    @SerializedName("totalCount")val totalCount: Long
)

data class MsrstnInfoInqireSvrVo (
    @SerializedName("tmX") val tmX : String,
    @SerializedName("tmY") val tmY : String,
    @SerializedName("umdName") val umdName : String,
    @SerializedName("sggName") val sggName: String,
    @SerializedName("sidoName") val sidoName: String
)