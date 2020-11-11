package com.example.dustalarm.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dustalarm.DustAPI
import com.example.dustalarm.DustNotiAlarm
import com.example.dustalarm.R
import com.example.dustalarm.databinding.ActivityMainBinding
import com.example.dustalarm.viewmodel.MainActivityViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE = 1000
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainActivityViewModel by viewModel()
    private val loadingDialog: LoadingDialog by inject { parametersOf(this@MainActivity)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        getPermission()

        viewModel.isLoadingCompleted.observe(this, Observer {
                if(it!!){
                    loadingDialog.dismissDialog()
                }
                else{
                    loadingDialog.startLoading()
                }
        })
    }

    private fun getPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_CODE
            )
        }
        else
            getDust()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        getDust()
                    else
                        finish()
                }
            }
        }
    }

    private fun getDust() {
        viewModel.loadDust()
        DustNotiAlarm(applicationContext).regist()
    }
}