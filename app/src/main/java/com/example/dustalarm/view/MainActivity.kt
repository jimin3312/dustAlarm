package com.example.dustalarm.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dustalarm.R
import com.example.dustalarm.databinding.ActivityMainBinding
import com.example.dustalarm.model.DustDao
import com.example.dustalarm.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE = 1000
    }
    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
//        binding.viewModel?.getUsers()?.observe(this, Observer<DustDao> {
//            mLocation.text = it.location
//        })
        Log.d("TEST", "" +viewModel?.dust.getDustInfo().value?.pm10State)
//        DustNotiAlarm(this).regist()
    }
}