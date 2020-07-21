package com.example.dustalarm.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.dustalarm.DustAPI
import com.example.dustalarm.DustReceiver
import com.example.dustalarm.R
import com.example.dustalarm.model.DTO.Addr
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.DTO.ViewResources
import com.google.android.gms.location.*
import java.util.*

class Dust(val context: Context) {
    val dustInfo = MutableLiveData<DustDTO>()
    val viewResources = MutableLiveData<ViewResources>()
    val address = MutableLiveData<Addr>()

    fun load(){
        dustInfo.value = DustDTO()
        viewResources.value = ViewResources()
        address.value = Addr()

        val intent = Intent(context, DustReceiver::class.java)
        intent.action = "dust"
        context.sendBroadcast(intent)
    }
}