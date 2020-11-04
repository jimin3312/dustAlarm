package com.example.dustalarm.model

import com.google.gson.annotations.SerializedName

data class Station (
    @SerializedName("stationName")val name : String
)