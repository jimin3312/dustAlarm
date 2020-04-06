package com.example.dustalarm

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class LocationConvertor {

    val service: ExecutorService
    lateinit var bufferedReader: BufferedReader
    lateinit var connection: HttpURLConnection
    constructor(service: ExecutorService) {
        this.service = service
    }

    fun toStationName(location: Pair<String, String>) : String
    {
        val getLocationCall: Callable<String> =
            Callable<String> {
                try {
                    var mURL =
                        "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?"
                    mURL += "tmX=" + location.first
                    mURL += "&tmY=" + location.second
                    mURL += "&_returnType=json&ServiceKey=" + MainActivity.KEY

                    mURL = URLDecoder.decode(mURL, "UTF-8")
                    connection = URL(mURL).openConnection() as HttpURLConnection
                    bufferedReader = connection.inputStream.bufferedReader()

                    var line = bufferedReader.readLine()
                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    bufferedReader.close()
                    connection.disconnect()
                    jsonObject.getString("stationName")
                } catch (e: Exception) {
                    "error2"+e.toString()
                }
            }

        val result: Future<String> = service.submit(getLocationCall)
        return result.get()
    }
}