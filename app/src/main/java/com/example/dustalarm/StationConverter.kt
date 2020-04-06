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

class StationConverter{

    val service: ExecutorService
    lateinit var bufferedReader: BufferedReader
    lateinit var connection: HttpURLConnection

    constructor(service: ExecutorService) {
        this.service = service
    }

    fun toPm10Pm25(stationName : String) : Pair<String, String>
    {
        val stationCall: Callable<Pair<String, String>> =
            Callable<Pair<String, String>> {
                var pm10 = ""
                var pm25 = ""
                try {
                    var mURL =
                        "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?"
                    mURL += "stationName=" + stationName
                    mURL += "&dataTerm=daily&pageNo=1&numOfRows=10&ver=1.3&_returnType=json&ServiceKey=" + MainActivity.KEY
                    mURL = URLDecoder.decode(mURL, "UTF-8")
                    connection = URL(mURL).openConnection() as HttpURLConnection
                    bufferedReader = connection.inputStream.bufferedReader()

                    var line = bufferedReader.readLine()
                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    pm10 = jsonObject.getString("pm10Value")
                    pm25 = jsonObject.getString("pm25Value")
                    bufferedReader.close()
                    connection.disconnect()
                    Pair(pm10, pm25)
                } catch (e: Exception) {
                    Pair(pm10, pm25)
                }
            }
        val future: Future<Pair<String, String>> = service.submit(stationCall)

        return future.get()
    }
}