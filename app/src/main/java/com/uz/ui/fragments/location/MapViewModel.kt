package com.uz.ui.fragments.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.uz.nikoh.location.LocationData
import com.uz.nikoh.location.LocationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    val addressLoading = MutableLiveData(false)
    val location = MutableLiveData<LocationData?>()

    private var loadAddressJob: Job? = null

    fun cancelAddressLoad() {
        loadAddressJob?.cancel()
    }

    fun loadAddress(locationHelper: LocationHelper, latLng: LatLng) {
        addressLoading.postValue(true)
        cancelAddressLoad()
        loadAddressJob = viewModelScope.launch {
            val newLocation = locationHelper.getAddress(latLng)
            if (isActive.not()) return@launch
            location.postValue(newLocation)
            addressLoading.postValue(false)
        }
    }
}