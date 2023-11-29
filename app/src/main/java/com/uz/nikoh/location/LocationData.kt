package com.uz.nikoh.location

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    var address: String?,
    var latLng: LatLngMy?,
    var city: String? = null
) {
    constructor() : this(null, null, null)
}

fun LocationData.addressWithCity() = "$address $city"