package com.example.dustalarm.model

import com.google.gson.annotations.SerializedName

data class TM (
    @SerializedName("tmX") val tmX: String,
    @SerializedName("tmY")val tmY: String
)
