package com.example.dustalarm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dustalarm.model.Dust
import com.example.dustalarm.model.DustDao

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val dust = Dust(application)
    val dustInfo: MutableLiveData<DustDao> by lazy {
        dust.getInfo()
    }

}