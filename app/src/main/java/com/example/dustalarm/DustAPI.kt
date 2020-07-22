package com.example.dustalarm

import android.util.Log
import com.example.dustalarm.model.Dust
import com.example.dustalarm.view.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.cacheGet
import org.json.JSONObject
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class DustAPI {
    companion object {
        val KEY = "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"
        val REQUEST_CODE = 1000
    }

    var service: ExecutorService
    lateinit var bufferedReader: BufferedReader
    lateinit var connection: HttpURLConnection
    lateinit var tmLocation: Pair<String, String>
    var stationName: String?
    var umd: String

    constructor(umd: String) {
        service = Executors.newSingleThreadExecutor()
        this.umd = umd
        this.stationName = null
    }

    fun recieveTMLocation(): DustAPI {
        val umdCall: Callable<Pair<String, String>> =
            Callable {
                var tmX = ""
                var tmY = ""
                var umdName = umd
                val okHttpClient = OkHttpClient()
                try {
                    var mURL = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?"
                    mURL += "umdName=" + umdName
                    mURL += "&pageNo=1&numOfRows=10&_returnType=json&ServiceKey=$KEY"
                    mURL = URLDecoder.decode(mURL, "UTF-8")

                    val request = Request.Builder()
                        .url(mURL)
                        .build()

                    OkHttpClient.Builder().apply {

                    }


//                    Request.Builder().apply {
//                        cache(get())
//                    }

                    val response = okHttpClient.newCall(request).execute()
                    val jsonObject = JSONObject(response.body!!.string()).getJSONArray("list").getJSONObject(0)

//                    connection = URL(mURL).openConnection() as HttpURLConnection
//                    bufferedReader = connection.inputStream.bufferedReader()
//
//                    val line = bufferedReader.readLine()
//                    val jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    tmX = jsonObject.getString("tmX")
                    tmY = jsonObject.getString("tmY")
//                    bufferedReader.close()
//                    connection.disconnect()
                    Pair(tmX, tmY)
                } catch (e: Exception) {
                    Pair(tmX, tmY)
                }
            }

        val result: Future<Pair<String, String>> = service.submit(umdCall)
        this.tmLocation = result.get()
        return this
    }

    fun recieveStationName(): DustAPI{
        val getLocationCall: Callable<String> =
            Callable<String> {
                try {
                    var mURL =
                        "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?"
                    mURL += "tmX=" + tmLocation.first
                    mURL += "&tmY=" + tmLocation.second
                    mURL += "&_returnType=json&ServiceKey=" + KEY

                    mURL = URLDecoder.decode(mURL, "UTF-8")
                    connection = URL(mURL).openConnection() as HttpURLConnection
                    bufferedReader = connection.inputStream.bufferedReader()

                    var line = bufferedReader.readLine()
                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    bufferedReader.close()
                    connection.disconnect()
                    stationName = jsonObject.getString("stationName")
                    stationName
                } catch (e: Exception) {
                    stationName
                }
            }

        val result: Future<String> = service.submit(getLocationCall)
        stationName = result.get()
        return this
    }

    fun recievePm10Pm25(): Pair<Int , Int>{
        if(stationName == null)
            stationName = umd;

        val stationCall: Callable<Pair<Int, Int>> =
            Callable<Pair<Int, Int>> {
                try {
                    var mURL =
                        "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?"
                    mURL += "stationName=" + stationName
                    mURL += "&dataTerm=daily&pageNo=1&numOfRows=10&ver=1.3&_returnType=json&ServiceKey=" + KEY
                    mURL = URLDecoder.decode(mURL, "UTF-8")
                    connection = URL(mURL).openConnection() as HttpURLConnection
                    bufferedReader = connection.inputStream.bufferedReader()

                    var line = bufferedReader.readLine()
                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    var pm10 = jsonObject.getString("pm10Value")
                    var pm25 = jsonObject.getString("pm25Value")
                    Log.d("로그", "API")
                    Log.d("로그", pm10)
                    Log.d("로그", pm25)
                    bufferedReader.close()
                    connection.disconnect()
                    Pair(pm10.toInt(), pm25.toInt())
                } catch (e: Exception) {
                    Pair(0, 0)
                }
            }
        val future: Future<Pair<Int, Int>> = service.submit(stationCall)
        return future.get()
    }
}