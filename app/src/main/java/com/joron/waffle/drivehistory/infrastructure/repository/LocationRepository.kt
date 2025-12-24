package com.joron.waffle.drivehistory.infrastructure.repository

import com.joron.waffle.drivehistory.domain.LocationEventListener

object LocationRepository {

    val locationEventMap = mutableMapOf<String, LocationEventListener>()

    fun notifyUpdateLocation(latitude: Double, longitude: Double) {
        for (event in locationEventMap.values) {
            event.onUpdateLocation?.invoke(latitude, longitude)
        }
    }

    fun addLocationEventListener(key: String, listener: LocationEventListener) {
        locationEventMap[key] = listener
    }

    fun removeLocationEventListener(key: String) {
        locationEventMap.remove(key)
    }

}