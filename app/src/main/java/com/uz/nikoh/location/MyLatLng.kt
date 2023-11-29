package com.uz.nikoh.location

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class LatLngMy(
    var lat: Double,
    var long: Double
) {
    constructor() : this(0.0, 0.0)
}

fun LatLng.toMy() = LatLngMy(latitude, longitude)