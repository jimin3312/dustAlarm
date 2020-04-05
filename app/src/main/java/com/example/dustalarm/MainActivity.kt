package com.example.dustalarm

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

const val KEY = "3ahny5aBOqdFAnQlYaGDLnLLd3QyV3ORoXp6Aml886Qdp%2FbPCb4rqir5r8IjeWJHnT4HykKItVW2Mv2SIFhvdg%3D%3D"
var tmXValue = ""
var tmYValue = ""
var stationNameValue = ""

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var service: ExecutorService

    val REQUEST_CODE = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = Executors.newSingleThreadExecutor()

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//            btn1.setOnClickListener(View.OnClickListener {
//                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
//                {
//                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
//                    return@OnClickListener
//                }
//                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
//            })

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations.get(p0!!.locations.size - 1)
                Log.d(
                    "locationTAG",
                    "latitude : " + location.latitude.toString() + " longitude : " + location.longitude.toString()
                )

                val gCoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addr: List<Address> =
                    gCoder.getFromLocation(location.latitude, location.longitude, 1)
                val a: Address = addr[0]
                mLocation.setText(a.adminArea + " " + a.locality + " " + a.thoroughfare)

                val getTmXTmy: Callable<Pair<String, String>> =
                    Callable<Pair<String, String>> {
                        var tmX = ""
                        var tmY = ""
                        var umdName = "혜화동"
                        try {
                            lateinit var bufferedReader: BufferedReader
                            lateinit var connection: HttpURLConnection
                            //var serviceKey = URLDecoder.decode("zckvmg1OH8svbljDs5lezq2jnqX2i0IDDhKSbDjualbVgwMHx8hcwIjB5rI50FB0dQ01W9pLivT%2BYm%2Fy25SZQg%3D%3D","UTF-8")
                            var mURL =
                                "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?"
                            mURL += "umdName=" + umdName
                            mURL += "&pageNo=1&numOfRows=10&_returnType=json&ServiceKey=" + KEY

                            mURL = URLDecoder.decode(mURL, "UTF-8")
                            connection = URL(mURL).openConnection() as HttpURLConnection
                            bufferedReader = connection.inputStream.bufferedReader()

                            var line = bufferedReader.readLine()
                            var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                            var tmX = jsonObject.getString("tmX")
                            var tmY = jsonObject.getString("tmY")

                            bufferedReader.close()
                            connection.disconnect()
                            Pair(tmX, tmY)
                        } catch (e: Exception) {
                            Pair(tmX, tmY)
                        }
                    }
                val getStationName: Callable<String> =
                    Callable<String> {
                        var stationName = ""
                        try {
                            lateinit var bufferedReader: BufferedReader
                            lateinit var connection: HttpURLConnection
                            var mURL =
                                "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?"
                            mURL += "tmX=" + tmXValue
                            mURL += "&tmY=" + tmYValue
                            mURL += "&_returnType=json&ServiceKey=" + KEY

                            mURL = URLDecoder.decode(mURL, "UTF-8")
                            connection = URL(mURL).openConnection() as HttpURLConnection
                            bufferedReader = connection.inputStream.bufferedReader()

                            var line = bufferedReader.readLine()
                            var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
                            stationName = jsonObject.getString("stationName")
                            bufferedReader.close()
                            connection.disconnect()
                            stationName
                        } catch (e: Exception) {
                            stationName
                        }
                    }
                val getPm10Pm25: Callable<Pair<String, String>> =
                    Callable<Pair<String, String>> {
                        var pm10 = ""
                        var pm25 = ""
                        try {
                            lateinit var bufferedReader: BufferedReader
                            lateinit var connection: HttpURLConnection
                            var mURL =
                                "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?"
                            mURL += "stationName=" + stationNameValue
                            mURL += "&dataTerm=daily&pageNo=1&numOfRows=10&ver=1.3&_returnType=json&ServiceKey=" + KEY
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
                try {
                    val future: Future<Pair<String, String>> = service.submit(getTmXTmy)
                    val tm: Pair<String, String> = future.get()
                    tmXValue = tm.first
                    tmYValue = tm.second
                    val future2: Future<String> = service.submit(getStationName)
                    val station: String = future2.get()
                    stationNameValue = station
                    val future3: Future<Pair<String, String>> = service.submit(getPm10Pm25)
                    val pm10pm25: Pair<String, String> = future3.get()
                    pm10.setText(pm10pm25.first)
                    pm25.setText(pm10pm25.second)
                } catch (e: Exception) {

                }

                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "Permisson granted", Toast.LENGTH_SHORT)
                            .show()
                    else
                        Toast.makeText(this, "Permisson denied", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }

//        val service: ExecutorService = Executors.newSingleThreadExecutor()
//        val getTmXTmy: Callable<Pair<String, String>> =
//            Callable<Pair<String, String>> {
//                var tmX = ""
//                var tmY = ""
//                var umdName = "혜화동"
//                try {
//                    lateinit var bufferedReader: BufferedReader
//                    lateinit var connection: HttpURLConnection
//                    //var serviceKey = URLDecoder.decode("zckvmg1OH8svbljDs5lezq2jnqX2i0IDDhKSbDjualbVgwMHx8hcwIjB5rI50FB0dQ01W9pLivT%2BYm%2Fy25SZQg%3D%3D","UTF-8")
//                    var mURL =
//                        "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?"
//                    mURL += "umdName=" + umdName
//                    mURL += "&pageNo=1&numOfRows=10&_returnType=json&ServiceKey=" + KEY
//
//                    mURL = URLDecoder.decode(mURL, "UTF-8")
//                    connection = URL(mURL).openConnection() as HttpURLConnection
//                    bufferedReader = connection.inputStream.bufferedReader()
//
//                    var line = bufferedReader.readLine()
//                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
//                    var tmX = jsonObject.getString("tmX")
//                    var tmY = jsonObject.getString("tmY")
//
//                    bufferedReader.close()
//                    connection.disconnect()
//                    Pair(tmX, tmY)
//                } catch (e: Exception) {
//                    Pair(tmX, tmY)
//                }
//            }
//        val getStationName: Callable<String> =
//            Callable<String> {
//                var stationName = ""
//                try {
//                    lateinit var bufferedReader: BufferedReader
//                    lateinit var connection: HttpURLConnection
//                    var mURL =
//                        "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList?"
//                    mURL += "tmX=" + tmXValue
//                    mURL += "&tmY=" + tmYValue
//                    mURL += "&_returnType=json&ServiceKey=" + KEY
//
//                    mURL = URLDecoder.decode(mURL, "UTF-8")
//                    connection = URL(mURL).openConnection() as HttpURLConnection
//                    bufferedReader = connection.inputStream.bufferedReader()
//
//                    var line = bufferedReader.readLine()
//                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
//                    stationName = jsonObject.getString("stationName")
//                    bufferedReader.close()
//                    connection.disconnect()
//                    stationName
//                } catch (e: Exception) {
//                    stationName
//                }
//            }
//        val getPm10Pm25: Callable<Pair<String, String>> =
//            Callable<Pair<String, String>> {
//                var pm10 = ""
//                var pm25 = ""
//                try {
//                    lateinit var bufferedReader: BufferedReader
//                    lateinit var connection: HttpURLConnection
//                    var mURL =
//                        "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?"
//                    mURL += "stationName=" + stationNameValue
//                    mURL += "&dataTerm=daily&pageNo=1&numOfRows=10&ver=1.3&_returnType=json&ServiceKey=" + KEY
//                    mURL = URLDecoder.decode(mURL, "UTF-8")
//                    connection = URL(mURL).openConnection() as HttpURLConnection
//                    bufferedReader = connection.inputStream.bufferedReader()
//
//                    var line = bufferedReader.readLine()
//                    var jsonObject = JSONObject(line).getJSONArray("list").getJSONObject(0)
//                    pm10 = jsonObject.getString("pm10Value")
//                    pm25 = jsonObject.getString("pm25Value")
//
//                    bufferedReader.close()
//                    connection.disconnect()
//                    Pair(pm10, pm25)
//                } catch (e: Exception) {
//                    Pair(pm10, pm25)
//                }
//            }
//        try {
//            val future: Future<Pair<String, String>> = service.submit(getTmXTmy)
//            val tm: Pair<String, String> = future.get()
//            tmXValue = tm.first
//            tmYValue = tm.second
//            val future2: Future<String> = service.submit(getStationName)
//            val station: String = future2.get()
//            stationNameValue = station
//            val future3: Future<Pair<String, String>> = service.submit(getPm10Pm25)
//            val pm10pm25: Pair<String, String> = future3.get()
//            pm10.setText(pm10pm25.first)
//            pm25.setText(pm10pm25.second)
//        } catch (e: Exception) {
//
//        }
    }

}

