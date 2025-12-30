package com.joron.waffle.drivehistory.infrastructure.repository

import android.location.Location
import com.joron.waffle.drivehistory.domain.LocationEventListener

object LocationRepository {

    val locationEventMap = mutableMapOf<String, LocationEventListener>()

    fun notifyUpdateLocation(location: Location) {
        for (event in locationEventMap.values) {
            event.onUpdateLocation?.invoke(location)
        }
    }

    fun addLocationEventListener(key: String, listener: LocationEventListener) {
        locationEventMap[key] = listener
    }

    fun removeLocationEventListener(key: String) {
        locationEventMap.remove(key)
    }

}