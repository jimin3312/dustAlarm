package com.example.dustalarm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.dustalarm.view.MainActivity

class PermissionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_layout)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                MainActivity.REQUEST_CODE
            )
        }
        else
            startActivity(Intent(this, MainActivity::class.java))

//        val permissionAccessCoarseLocationApproved = ActivityCompat
//            .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED
//
//        if (permissionAccessCoarseLocationApproved) {
//            val backgroundLocationPermissionApproved = ActivityCompat
//                .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
//                    PackageManager.PERMISSION_GRANTED
//
//            if (backgroundLocationPermissionApproved) {
//                // App can access location both in the foreground and in the background.
//                // Start your service that doesn't have a foreground service type
//                // defined.
//            } else {
//                // App can only access location in the foreground. Display a dialog
//                // warning the user that your app must have all-the-time access to
//                // location in order to function properly. Then, request background
//                // location.
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                    MainActivity.REQUEST_CODE
//                )
//            }
//        } else if(){
//            // App doesn't have access to the device's location at all. Make full request
//            // for permission.
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                MainActivity.REQUEST_CODE
//            )
//        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    Log.d("권한", "$grantResults")
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else
                        finish()
                }
            }
        }
    }
}