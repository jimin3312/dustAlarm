package com.example.dustalarm

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
                try {
                    var mURL = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?"
                    mURL += "umdName=" + umdName
                    mURL += "&pageNo=1&numOfRows=10&_returnType=json&ServiceKey=" + MainActivity.KEY

                    mURL = URLDecoder.decode(mURL, "UTF-8")
                    connection = URL(mURL).openConnection() as HttpURLConnection
                    bufferedReader = connection.inputStream.bufferedReader()

                    val line = bufferedReader.readLine()
                    val jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                    tmX = jsonObject.getString("tmX")
                    tmY = jsonObject.getString("tmY")

                    bufferedReader.close()
                    connection.disconnect()
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
                    mURL += "&_returnType=json&ServiceKey=" + MainActivity.KEY

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

    fun recievePm10Pm25(): Pair<String, String>{
        if(stationName == null)
            stationName = umd;

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