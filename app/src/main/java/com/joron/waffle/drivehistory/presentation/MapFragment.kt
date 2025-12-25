package com.joron.waffle.drivehistory.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.joron.waffle.drivehistory.R
import com.joron.waffle.drivehistory.databinding.MapFragmentBinding
import com.joron.waffle.drivehistory.domain.viewmodel.MapViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: MapFragmentBinding
    private var mapViewModel = MapViewModel()
    private var gMap: GoogleMap? = null
    private var polyline: Polyline? = null
    private val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity()
        binding = MapFragmentBinding.inflate(layoutInflater)
        binding.mapViewModel = mapViewModel
        binding.lifecycleOwner = this
        lifecycle.addObserver(mapViewModel)
        val trackUuid = args.trackUuid
        binding.buttonRecord.setOnClickListener {
            val recording = mapViewModel.recording.value ?: return@setOnClickListener
            if (recording) {
                mapViewModel.stopRecording()
                (activity as? MainActivity)?.stopLocationService()
            } else {
                mapViewModel.startRecording()
                (activity as? MainActivity)?.startLocationService(trackUuid)
            }
        }

        mapViewModel.load(activity, trackUuid)
        mapViewModel.recording.observe(activity, Observer {
            observeRecording(it)
        })
        mapViewModel.locationList.observe(activity, Observer {
            observeLocationList(it)
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.isZoomControlsEnabled = true
            if (isLocationInfoPermissionGranted()) {
                isMyLocationEnabled = true
            }

            //日本エリアにマップを移動する
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(35.670292, 139.773006), 4.2f))
        }
    }

    private fun observeRecording(recording: Boolean) {
        binding.buttonRecord.setImageResource(
            if (recording)
                R.drawable.ico_pause
            else
                R.drawable.round_fiber_manual_record_24
        )
    }

    private fun observeLocationList(locationList: List<LatLng>) {
        mapViewModel.trackItem = mapViewModel.trackItem.copy(locationList = locationList)
        val lineOptions = PolylineOptions()
            .addAll(locationList)
            .width(8f)
            .color(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red,
                ),
            )
        polyline = gMap?.addPolyline(lineOptions)
    }

    /**
     * 位置情報が許可されているかどうか
     * 位置情報が許可されていれば、現在値情報の取得をONにする
     */
    private fun isLocationInfoPermissionGranted(): Boolean {
        // 既に許可している
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationBackgroundPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            isLocationInfoPermissionGranted()
        }
    }

    companion object {
        const val TAG = "MapFragment"
    }
}