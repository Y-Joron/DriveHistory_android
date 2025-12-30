package com.joron.waffle.drivehistory.infrastructure.repository

import com.joron.waffle.drivehistory.domain.LocationEventListener
import com.joron.waffle.drivehistory.domain.model.LocationItem

object LocationRepository {

    val locationEventMap = mutableMapOf<String, LocationEventListener>()

    fun notifyUpdateLocation(location: LocationItem) {
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