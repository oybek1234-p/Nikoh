package com.uz.nikoh.location

import com.google.android.gms.maps.model.LatLng
import com.uz.nikoh.R

enum class City(val resIdName: Int, val latLng: LatLng) {
    UZBEKISTAN(R.string.uzbekiston, LatLng(41.345570, 69.284599)),
    TASHKENT(R.string.tashkent, LatLng(41.345570, 69.284599)),
    SAMARQAND(R.string.samarqand, LatLng(39.652451, 66.970139));

    companion object {
        fun findByName(name: String?): City? {
            try {
                return if (name.isNullOrEmpty()) null else City.valueOf(name.uppercase())
            } catch (e: Exception) {
                //
            }
            return null
        }
    }
}
