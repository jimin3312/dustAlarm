package com.example.dustalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

const val KEY =  "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        httpConnect("서울","구로구")
//        Log.d("TEST",tm.first+' '+tm.second)
//        var pm = getDustInformation("경기","종로구")
//        pm10.post(Runnable {
//            run {
//                pm10.setText(pm.first)
//            }
//        })
//        pm25.post(Runnable {
//            run {
//                pm25.setText(pm.first)
//            }
//        })
    }
   fun httpConnect(sidoName: String, throughfare: String)
   {
       Thread {
           try {

               var cnt = 1
               var totalCnt = 1
               lateinit var bufferedReader:BufferedReader
               lateinit var connection : HttpURLConnection
               var stringBuilder = StringBuilder()
               do {
                   var serviceKey = URLDecoder.decode("zckvmg1OH8svbljDs5lezq2jnqX2i0IDDhKSbDjualbVgwMHx8hcwIjB5rI50FB0dQ01W9pLivT%2BYm%2Fy25SZQg%3D%3D","UTF-8")
                   var mURL ="http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?"
                   mURL += "sidoName=" + sidoName
                   mURL += "&pageNo=" + cnt
                   mURL += "&numOfRows=20&ver=1.3&_returnType=json&ServiceKey=" + KEY
                   connection = URL(mURL).openConnection() as HttpURLConnection
                   bufferedReader = connection.inputStream.bufferedReader()

                   var line = bufferedReader.readLine()
                   stringBuilder.append(line)
                   while(line!=null)
                   {
                       line = bufferedReader.readLine()
                       stringBuilder.append(line)
                   }
                   totalCnt = JSONObject(stringBuilder.toString()).getInt("totalCount")
                   var jsonArray = JSONObject(stringBuilder.toString()).getJSONArray("list")

                   for(i in 0.. jsonArray.length()-1)
                   {
                        Log.d("TEST", jsonArray.get(i).toString())
                   }
                   cnt++
               } while(cnt<=totalCnt)

               bufferedReader.close()
               connection.disconnect()
           } catch (e : Exception) {
               Log.d("TESTexception",e.toString())
           }

       }.start()
   }

}

