package com.example.dustalarm.viewmodel

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageResource")
fun setImageViewResource(view: ImageView, resId : Int) {
    view.setImageResource(resId)
}