package com.example.dustalarm

import android.util.Log
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.PM
import com.example.dustalarm.model.Station
import com.example.dustalarm.model.TM
import com.example.dustalarm.view.MainActivity
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.cacheGet
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

interface DustAPI {
    companion object {
        val KEY = "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"
    }

    @GET("MsrstnInfoInqireSvc/getTMStdrCrdnt")
    fun receiveTMLocation(
        @Query("ServiceKey", encoded = true) key : String = KEY,
        @Query("umdName" ) umdName : String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 10,
        @Query("_returnType") type: String = "json"
    ) : Single<TM>

    @GET("MsrstnInfoInqireSvc/getNearbyMsrstnList")
    fun receiveStationName(
        @Query("ServiceKey", encoded = true) key : String = KEY,
        @Query("tmX") tmX : String,
        @Query("tmY") tmY : String,
        @Query("_returnType") type: String = "json"
    ) : Single<Station>

    @GET("ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
    fun receivePm(
        @Query("ServiceKey", encoded = true) key : String = KEY,
        @Query("stationName") stationNmae : String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 10,
        @Query("_returnType") type: String = "json",
        @Query("dataTerm") dataTerm : String = "daily",
        @Query("ver") ver : String = "1.3"

    ) : Single<PM>

}