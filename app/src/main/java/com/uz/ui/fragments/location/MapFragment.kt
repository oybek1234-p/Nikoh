package com.uz.ui.fragments.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.uz.nikoh.R
import com.uz.nikoh.databinding.FragmentMapBinding
import com.uz.nikoh.location.City
import com.uz.nikoh.location.LocationData
import com.uz.nikoh.location.LocationHelper
import com.uz.nikoh.location.addressWithCity
import com.uz.nikoh.utils.JsonUtils
import com.uz.ui.base.BaseFragment
import com.uz.ui.utils.visibleOrGone
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

class MapFragment : BaseFragment<FragmentMapBinding>() {

    private var supportMap: GoogleMap? = null

    override val layId: Int
        get() = R.layout.fragment_map

    private val viewModel: MapViewModel by viewModels()
    private val locationController: LocationHelper = LocationHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionGiven = false

    companion object {
        private const val LOCATION_RESULT = "location"

        fun putLocation(location: LocationData): Bundle = Bundle().apply {
            putString(LOCATION_RESULT, JsonUtils.json.encodeToString(location))
        }

        private fun getLocation(bundle: Bundle): LocationData? {
            val string = bundle.getString(LOCATION_RESULT) ?: return null
            return JsonUtils.json.decodeFromString(string)
        }

        fun launch(
            fragment: Fragment,
            done: (locationData: LocationData?) -> Unit
        ) {
            fragment.setFragmentResultListener(LOCATION_RESULT) { _, b ->
                val location = getLocation(b)
                done.invoke(location)
            }
            (fragment as BaseFragment<*>).navigate(R.id.mapFragment)
        }
    }

    private var currentLocationGetJob: Job? = null

    @SuppressLint("MissingPermission")
    fun moveToCurrentLocation() {
        val getLocation = {
            currentLocationGetJob?.cancel()
            currentLocationGetJob = lifecycleScope.launch {
                var location: Location? = null
                while (location == null) {
                    if (isActive.not()) {
                        return@launch
                    }
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        location = it
                        if (it != null) {
                            val currentLoc = LatLng(it.latitude, it.longitude)
                            animateToLocation(currentLoc, 18f)
                        }
                    }
                    delay(500)
                }
            }
        }
        if (LocationHelper.isGpsOn(requireActivity())) {
            getLocation.invoke()
        } else {
            LocationHelper.requestGps(requireActivity()) {
                getLocation.invoke()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationController.requestPermission(this) {
            permissionGiven = it
        }
        locationController.registerGpsResult(this) {
            moveToCurrentLocation()
        }
    }

    private fun canContinue() =
        viewModel.addressLoading.value?.not() == true && viewModel.location.value != null

    private var animateToCurrentCityJob: Job? = null

    private fun animateToLocation(latLng: LatLng, zoom: Float = 12f) {
        supportMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun animateToCurrentCity() {
        animateToCurrentCityJob = lifecycleScope.launch {
            val currentCity = LocationHelper.getClientCity() ?: return@launch
            val cityLatLng = City.findByName(currentCity)!!.latLng
            animateToLocation(cityLatLng)
            animateToCurrentCityJob = null
        }
    }

    private fun onMapLoaded() {
        supportMap?.apply {
            setOnCameraMoveListener {
                binding?.continueButton?.isEnabled = false
            }
            setOnCameraMoveStartedListener {
                viewModel.cancelAddressLoad()
                animateToCurrentCityJob?.cancel()
            }
            setOnCameraIdleListener {
                val currentLatLng = cameraPosition.target
                viewModel.loadAddress(locationController, currentLatLng)
            }
            animateToCurrentCity()
        }
    }

    override fun viewCreated(bind: FragmentMapBinding) {
        bind.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            currentButton.setOnClickListener {
                moveToCurrentLocation()
            }
            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.supportMapFragment) as SupportMapFragment
            supportMapFragment.getMapAsync {
                supportMap = it
                onMapLoaded()
            }
            viewModel.apply {
                addressLoading.observe(viewLifecycleOwner) {
                    progressBar.visibleOrGone(it)
                    addressView.visibleOrGone(it.not())
                    continueButton.isEnabled = canContinue()
                }
                location.observe(viewLifecycleOwner) {
                    addressView.text = it?.addressWithCity()
                }
            }
            continueButton.setOnClickListener {
                val location = viewModel.location.value ?: return@setOnClickListener
                setFragmentResult(LOCATION_RESULT, putLocation(location))
                findNavController().popBackStack()
            }
        }
    }
}