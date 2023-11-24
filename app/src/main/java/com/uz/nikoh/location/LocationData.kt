package com.uz.nikoh.location

data class LocationData(
    var address: String?,
    var latLng: LatLngMy?,
    var city: String? = null
)