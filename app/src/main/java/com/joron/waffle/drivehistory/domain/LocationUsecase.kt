package com.joron.waffle.drivehistory.domain

import android.content.Context
import com.joron.waffle.drivehistory.domain.model.LocationItem
import com.joron.waffle.drivehistory.infrastructure.repository.LocationRepository
import com.joron.waffle.drivehistory.infrastructure.repository.PreferenceRepository
import com.joron.waffle.drivehistory.util.LocationHelper

class LocationUsecase {

    fun notifyUpdateLocation(location: LocationItem) {
        LocationRepository.notifyUpdateLocation(location)
    }

    fun addLocationEventListener(key: String, listener: LocationEventListener) {
        LocationRepository.addLocationEventListener(key, listener)
    }

    fun removeLocationEventListener(key: String) {
        LocationRepository.removeLocationEventListener(key)
    }

    fun getLocationPref(context: Context): List<LocationItem> {
        val locations = PreferenceRepository.getLocations(context)
        return LocationHelper.toLocationList(locations)
    }

    fun saveLocationsPref(context: Context, locationList: List<LocationItem>) {
        val locations = LocationHelper.toLocationStr(locationList)
        PreferenceRepository.saveLocations(context, locations)
    }

    fun addLocationsPref(
        context: Context,
        location: LocationItem,
    ) {
        val locationList = getLocationPref(context).toMutableList().apply {
            add(location)
        }
        saveLocationsPref(context, locationList)
    }

    fun clearLocationsPref(context: Context) {
        PreferenceRepository.clearLocations(context)
    }

}