package com.example.auraenglish.practice.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

// Toast từ string resource
fun Context.showToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageRes, duration).show()
}

// Toast từ chuỗi
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
