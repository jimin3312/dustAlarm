package com.example.dustalarm.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dustalarm.DustAPI
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

//        viewModel.isLoadingCompleted.observe(this, Observer {
//                if(it!!){
//                    loadingDialog.dismissDialog()
//                }
//                else{
//                    loadingDialog.startLoading()
//                }
//        })

    }
}