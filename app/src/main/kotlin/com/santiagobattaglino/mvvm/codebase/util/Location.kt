package com.santiagobattaglino.mvvm.codebase.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import java.util.*

fun getAddress(lat: Double, lon: Double, context: Context?): Address? {
    val geocoder = Geocoder(context, Locale.getDefault())
    var address: Address? = null
    try {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNotEmpty()) {
            address = addresses[0]
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return address
}

fun isLocationEnabled(context: Context?): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.isLocationEnabled
    } else {
        val mode = Settings.Secure.getInt(
            context?.contentResolver, Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        mode != Settings.Secure.LOCATION_MODE_OFF
    }
}