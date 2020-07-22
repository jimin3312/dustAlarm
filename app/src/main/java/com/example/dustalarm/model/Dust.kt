package com.example.dustalarm.model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.dustalarm.DustReceiver
import com.example.dustalarm.model.DTO.Addr
import com.example.dustalarm.model.DTO.DustDTO
import com.example.dustalarm.model.DTO.ViewResources

class Dust(val context: Context) {
    val isLoadingCompleted =  MutableLiveData<Boolean>()
    val dustInfo = MutableLiveData<DustDTO>()
    val viewResources = MutableLiveData<ViewResources>()
    val address = MutableLiveData<Addr>()

    fun load(){
        dustInfo.value = DustDTO()
        viewResources.value = ViewResources()
        address.value = Addr()
        isLoadingCompleted.value = false

        val intent = Intent(context, DustReceiver::class.java)
        intent.action = "dust"
        context.sendBroadcast(intent)
    }
}