package com.uz.nikoh.location.ipaddress

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IpAddressInfo(
    @SerialName("status") val status: String,
    @SerialName("country") val country: String,
    @SerialName("countryCode") val countryCode: String,
    @SerialName("region") val region: String,
    @SerialName("regionName") val regionName: String,
    @SerialName("city") val city: String,
    @SerialName("zip") val zip: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("timezone") val timezone: String,
    @SerialName("isp") val isp: String,
    @SerialName("org") val org: String,
    @SerialName("query") val query: String
) {
    fun latLng() = LatLng(lat, lon)
}