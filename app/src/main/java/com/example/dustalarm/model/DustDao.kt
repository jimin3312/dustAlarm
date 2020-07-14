package com.example.dustalarm.model
data class DustDao(var pm10Value: String = "",
                   var pm25Value: String = "",
                   var pm10State: String = "",
                   var pm25State: String = "",
                   var location: String = "")