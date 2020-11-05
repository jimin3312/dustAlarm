package com.example.dustalarm.model
import com.google.gson.annotations.SerializedName

data class PM (
    val arpltnInforInqireSVCVo: ArpltnInforInqireSVCVo
)

data class ArpltnInforInqireSVCVo (
    val returnType: String,
    val coGrade: String,
    val coValue: String,
    val dataTerm: String,
    val dataTime: String,
    val khaiGrade: String,
    val khaiValue: String,
    val mangName: String,
    val no2Grade: String,
    val no2Value: String,
    val numOfRows: String,
    val o3Grade: String,
    val o3Value: String,
    val pageNo: String,
    val pm10Grade: String,
    val pm10Grade1H: String,
    val pm10Value: String,
    val pm10Value24: String,
    val pm25Grade: String,
    val pm25Grade1H: String,
    val pm25Value: String,
    val pm25Value24: String,
    val resultCode: String,
    val resultMsg: String,
    val rnum: Long,
    val serviceKey: String,
    val sidoName: String,
    val so2Grade: String,
    val so2Value: String,
    val stationCode: String,
    val stationName: String,
    val totalCount: String,
    val ver: String
)
