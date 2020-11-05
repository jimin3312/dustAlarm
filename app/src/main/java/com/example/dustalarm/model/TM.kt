package com.example.dustalarm.model

import com.google.gson.annotations.SerializedName

//data class TM (
//    @SerializedName("tmX") val tmX: String,
//    @SerializedName("tmY")val tmY: String
//)

data class TM (
    val msrstnInfoInqireSvrVo: MsrstnInfoInqireSvrVo,
    val list: List<MsrstnInfoInqireSvrVo>,
    val parm: MsrstnInfoInqireSvrVo,
    val totalCount: Long
)

data class MsrstnInfoInqireSvrVo (
    val returnType: String,
    val addr: String,
    val districtNum: String,
    val dmX: String,
    val dmY: String,
    val item: String,
    val mangName: String,
    val map: String,
    val numOfRows: String,
    val oper: String,
    val pageNo: String,
    val photo: String,
    val resultCode: String,
    val resultMsg: String,
    val rnum: Long,
    val serviceKey: String,
    val sggName: String,
    val sidoName: String,
    val stationCode: String,
    val stationName: String,
    val tm: Long,
    val tmX: String,
    val tmY: String,
    val totalCount: String,
    val umdName: String,
    val ver: String,
    val vrml: String,
    val year: String
)