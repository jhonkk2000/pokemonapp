package com.jhonkk.mylocations

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.jhonkk.common.model.MyLocation
import com.jhonkk.mylocations.adapter.LocationPosAdapater
import com.jhonkk.mylocations.databinding.FragmentMyLocationsBinding
import com.jhonkk.mylocations.service.LocationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyLocationsFragment: Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMyLocationsBinding? = null
    private val binding: FragmentMyLocationsBinding
        get() = _binding!!

    private var allAreGranted = false
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.values.forEach {
            allAreGranted = allAreGranted && it
        }
        bindTextPermission()
    }

    private val viewModel: MyLocationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentMyLocationsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch {
            viewModel.locationUiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state ->
                    Log.d("dataTestintThis", "${state.list.size}")
                    adapter?.submitList(state.list)
                    updateMap(state.list)
                }
        }

        initMap()
        initRecycler()
        checkPermissions()
        viewModel.startListenerUpdates()
        binding.btnVerify.setOnClickListener { checkPermissions() }
        return binding.root
    }

    private val listMarkers: MutableList<Marker> = mutableListOf()
    private fun updateMap(list: List<MyLocation>) {
        listMarkers.forEach { it.remove() }
        listMarkers.clear()
        googleMap?.let { map ->
            val bounds = LatLngBounds.Builder()
            var lastLng: LatLng? = null
            list.forEach {
                val latLng = LatLng(it.latitude?: 0.0, it.longitude?: 0.0)
                val marker = map.addMarker(MarkerOptions().position(latLng))
                marker?.let { m -> listMarkers.add(m) }
                lastLng = latLng
                bounds.include(latLng)
            }
            if (list.size > 1) {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120))
            } else {
                lastLng?.let { map.animateCamera(CameraUpdateFactory.newLatLng(it))  }
            }
        }
    }

    private var adapter: LocationPosAdapater? = null
    private fun initRecycler() {
        adapter = LocationPosAdapater()
        binding.rvPoints.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.rvPoints.adapter = adapter
    }

    private fun bindTextPermission() {
        if (allAreGranted) {
            binding.tvPermissions.text = "Permisos aceptados"
        } else {
            binding.tvPermissions.text = "Faltan permisos"
        }
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
            || (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        ) {
            launchPermissions()
        } else {
            allAreGranted = true
            startService()
            bindTextPermission()
        }

    }

    private fun startService() {
        if (LocationService.STARTED) return
        Intent(requireContext(), LocationService::class.java).apply {
            action = LocationService.ACTION_START_IN_BACKGROUND
            requireActivity().startService(this)
        }
    }

    private fun launchPermissions() {
        resultLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resultLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ))
        }
    }

    private var mapFragment: SupportMapFragment? = null
    private fun initMap() {
        val prev = childFragmentManager.findFragmentByTag("gmap_location")
        if (prev == null) {
            val gmapOptions: GoogleMapOptions = GoogleMapOptions().liteMode(false)
            mapFragment = SupportMapFragment.newInstance(gmapOptions)
            childFragmentManager
                .beginTransaction()
                .add(R.id.map_container, mapFragment!!, "gmap_location")
                .commit()
        }
        if (mapFragment == null) {
            mapFragment = childFragmentManager
                .findFragmentByTag("gmap_location") as SupportMapFragment?
        }
        mapFragment?.getMapAsync(this)
    }

    private var googleMap: GoogleMap? = null
    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        googleMap?.let { map ->
            map.setOnMapLoadedCallback { updateMap(viewModel.locationUiState.value.list) }
        }
    }

    private val REQUEST_CHECK_SETTINGS = 101
    private var locationRequest: LocationRequest? = null
    private var result: Task<LocationSettingsResponse>? = null
    private val isLocationEnable: Boolean
        get() {
            if (!isGpsEnable) {
                locationRequest = LocationRequest.create()
                locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                locationRequest?.interval = 10000
                locationRequest?.fastestInterval = 5000
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest!!)
                builder.setAlwaysShow(true)
                val client = LocationServices.getSettingsClient(requireActivity())
                result = client.checkLocationSettings(builder.build())
                result?.addOnFailureListener(requireActivity()) { e ->
                    if (e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(
                                requireActivity(),
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            sendEx.printStackTrace()
                        }
                    }
                }
                return false
            }
            return true
        }

    private val isGpsEnable: Boolean
        get() {
            val locationMode: Int = try {
                Settings.Secure.getInt(requireActivity().contentResolver, Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        }

    override fun onResume() {
        super.onResume()
        isLocationEnable
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}