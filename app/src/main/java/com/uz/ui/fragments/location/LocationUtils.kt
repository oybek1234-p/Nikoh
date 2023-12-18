package com.uz.ui.fragments.location

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.uz.nikoh.location.LatLngMy


object LocationUtils {

    fun openLatLng(activity: Activity,latLng: LatLngMy) {
        val lat = latLng.lat
        val lng = latLng.long
        val strUri = "http://maps.google.com/maps?q=loc:$lat,$lng (Label which you want)"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

        activity.startActivity(intent)
    }
}