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

class UMDConvetor {

    val service: ExecutorService
    lateinit var bufferedReader: BufferedReader
    lateinit var connection: HttpURLConnection

    constructor(service: ExecutorService) {
        this.service = service
    }
    fun toTmXTmY(umd: String) : Pair<String, String>
    {
        val umdCall: Callable<Pair<String, String>> =
            Callable {
                var umdName = umd
                try {
                    var mURL = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?"
                    mURL += "umdName=" + umdName
                    mURL += "&pageNo=1&numOfRows=10&_returnType=json&ServiceKey=" + MainActivity.KEY

                    mURL = URLDecoder.decode(mURL, "UTF-8")
                    connection = URL(mURL).openConnection() as HttpURLConnection
                    bufferedReader = connection.inputStream.bufferedReader()

                    val line = bufferedReader.readLine()
                    val jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    bufferedReader.close()
                    connection.disconnect()
                    Pair(jsonObject.getString("tmX"), jsonObject.getString("tmY"))
                } catch (e: Exception) {
                    Pair("error1", e.toString())
                }
            }

        val result: Future<Pair<String, String>> = service.submit(umdCall)
        return result.get()
    }
}