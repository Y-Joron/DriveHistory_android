package com.joron.waffle.drivehistory.domain.model

import android.location.Location
import com.joron.waffle.drivehistory.util.LocationHelper

data class LocationItem(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val altitude: Double = 0.0,
) {
    val accurate: Boolean
        get() = accuracy < LocationHelper.BOUND_OF_ACCURACY

    companion object {
        fun fromLocation(location: Location): LocationItem {
            return LocationItem(
                location.latitude,
                location.longitude,
                location.accuracy,
                location.speed,
                location.altitude,
            )
        }
    }
}
