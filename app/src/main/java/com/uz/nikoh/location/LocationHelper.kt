package com.uz.nikoh.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.uz.base.exception.ExceptionHandler
import com.uz.nikoh.appContext
import com.uz.nikoh.location.ipaddress.IpAddressRetriever
import com.uz.ui.BaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {

    private const val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    private val ipAddressRetriever by lazy { IpAddressRetriever() }

    private suspend fun getIpAddressInfo() = ipAddressRetriever.getIpAddress()

    suspend fun getClientCity() = getIpAddressInfo()?.city

    private val searchLocation get() = BaseConfig.searchLocation
    var searchLocationLive = MutableLiveData(searchLocation)

    fun getSearchLocationCity(): City {
        val location = searchLocation
        if (location.isEmpty()) return City.UZBEKISTAN
        return City.findByName(location)!!
    }

    fun setSearchLocation(newLocation: String) {
        BaseConfig.searchLocation = newLocation
        BaseConfig.save()
        searchLocationLive.postValue(newLocation)
    }

    suspend fun findSearchLocationByNetwork(): String {
        val city = getClientCity()?.uppercase()
        val validCity = City.entries.firstOrNull { it.name == city } != null

        if (validCity.not() || city.isNullOrEmpty()) return City.UZBEKISTAN.name
        return city
    }

    /**
     * Call in onCreate of the fragment
     */
    fun requestPermission(fragment: Fragment, result: (success: Boolean) -> Unit) {
        val locationPermissionRequest = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { allowed ->
            result.invoke(allowed)
        }
        locationPermissionRequest.launch(fineLocationPermission)
    }

    private val locationRequest =
        LocationRequest.Builder(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).build()

    fun isGpsOn(activity: Activity): Boolean {
        val manager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private var gpsRequestResultListener: ActivityResultLauncher<IntentSenderRequest>? = null

    fun registerGpsResult(fragment: Fragment, result: (gpsOn: Boolean) -> Unit) {
        val launcher =
            fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { r ->
                val success = r.resultCode == Activity.RESULT_OK
                result.invoke(success)
            }
        gpsRequestResultListener = launcher
    }

    fun requestGps(activity: Activity, done: (success: Boolean) -> Unit) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            done.invoke(true)
        }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    gpsRequestResultListener?.launch(
                        IntentSenderRequest.Builder(it.resolution).build(), null
                    )
                } catch (e: Exception) {
                    ExceptionHandler.handle(e)
                }
            }
        }
    }

    private val geocoder by lazy { Geocoder(appContext, Locale("uz")) }

    suspend fun getAddress(latLng: LatLng, times: Int = 0): LocationData =
        withContext(Dispatchers.Default) {
            try {
                val list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 2)
                val address = list!![0]
                val secondAddress = list[1]
                var tempCity = getClientCity()
                if (tempCity != null) {
                    if (City.findByName(tempCity.uppercase()) == null) {
                        tempCity = address.locality ?: secondAddress.locality ?: ""
                    }
                }
                val city = tempCity!!

                val subLocal = address.subLocality ?: secondAddress?.subLocality ?: ""
                var text = "$subLocal, $city"

                if (city.isEmpty() || subLocal.isEmpty()) {
                    text = address.getAddressLine(0)
                }
                val subAddress = text.substringBefore(",").trim()
                val extraCity = text.substringAfter(",").trim().ifEmpty { getClientCity() }

                val addressModel = LocationData()
                addressModel.address = subAddress
                addressModel.city = city.ifEmpty { extraCity }
                addressModel.latLng = latLng.toMy()
                return@withContext addressModel
            } catch (e: Throwable) {
                if (times == 3) {
                    //
                } else {
                    delay(100)
                    getAddress(latLng, times + 1)
                }
            }
            return@withContext LocationData(null, latLng = latLng.toMy(), null)
        }
}