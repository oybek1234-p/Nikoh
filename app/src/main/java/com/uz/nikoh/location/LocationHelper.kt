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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {

    private const val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    private val ipAddressRetriever by lazy { IpAddressRetriever() }

    private suspend fun getIpAddressInfo() = ipAddressRetriever.getIpAddress()

    suspend fun getClientCity() = getIpAddressInfo()?.city

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

    private val geocoder by lazy { Geocoder(appContext, Locale.getDefault()) }

    suspend fun getAddress(latLng: LatLng): LocationData? = withContext(Dispatchers.Default) {
        try {
            val addresses = geocoder.getFromLocation(
                latLng.latitude, latLng.longitude, 1
            )
            val address = addresses!![0].getAddressLine(0)
            val city = getClientCity()

            return@withContext LocationData(address, latLng.toMy(), city)
        } catch (e: Exception) {
            ExceptionHandler.handle(e)
        }
        return@withContext null
    }
}