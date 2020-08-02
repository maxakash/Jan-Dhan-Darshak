package com.whileloop.jandhandarshak.utils

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.core.content.edit
import es.dmoral.toasty.Toasty


fun Context.infoToast(message: CharSequence) {

    Toasty.info(applicationContext, message, Toast.LENGTH_SHORT, true).show()

}

fun Context.successToast(message: CharSequence) {

    Toasty.success(applicationContext, message, Toast.LENGTH_SHORT, true).show()

}

fun Context.failedToast(message: CharSequence) {


    Toasty.error(applicationContext, message, Toast.LENGTH_SHORT, true).show()

}