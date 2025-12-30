package com.joron.waffle.drivehistory.domain

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.joron.waffle.drivehistory.infrastructure.repository.LocationRepository
import com.joron.waffle.drivehistory.infrastructure.repository.PreferenceRepository
import com.joron.waffle.drivehistory.util.LocationHelper

class LocationUsecase {

    fun notifyUpdateLocation(location: Location) {
        LocationRepository.notifyUpdateLocation(location)
    }

    fun addLocationEventListener(key: String, listener: LocationEventListener) {
        LocationRepository.addLocationEventListener(key, listener)
    }

    fun removeLocationEventListener(key: String) {
        LocationRepository.removeLocationEventListener(key)
    }

    fun getLocationPref(context: Context): List<LatLng> {
        val locations = PreferenceRepository.getLocations(context)
        return LocationHelper.toLocationList(locations)
    }

    fun saveLocationsPref(context: Context, locationList: List<LatLng>) {
        val locations = LocationHelper.toLocationStr(locationList)
        PreferenceRepository.saveLocations(context, locations)
    }

    fun addLocationsPref(
        context: Context,
        latitude: Double,
        longitude: Double,
    ) {
        val locationList = getLocationPref(context).toMutableList().apply {
            add(LatLng(latitude, longitude))
        }
        saveLocationsPref(context, locationList)
    }

    fun clearLocationsPref(context: Context) {
        PreferenceRepository.clearLocations(context)
    }

}