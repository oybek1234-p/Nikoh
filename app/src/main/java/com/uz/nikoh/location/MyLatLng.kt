package com.uz.nikoh.location

data class LatLngMy(
    var lat: Double,
    var long: Double
) {
    constructor() : this(0.0, 0.0)
}