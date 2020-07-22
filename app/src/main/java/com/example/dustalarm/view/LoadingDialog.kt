package com.example.dustalarm.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.dustalarm.R

class LoadingDialog(val context: Context) {

    lateinit var dialog: AlertDialog

    fun startLoading(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val inflater: LayoutInflater = (context as Activity).layoutInflater
        builder.setView(inflater.inflate(R.layout.loading, null))

        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun dismissDialog(){
        dialog.dismiss()
    }
}