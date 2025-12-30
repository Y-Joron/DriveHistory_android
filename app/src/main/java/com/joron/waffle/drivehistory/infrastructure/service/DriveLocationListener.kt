package com.joron.waffle.drivehistory.infrastructure.service

import com.joron.waffle.drivehistory.domain.model.LocationItem

interface DriveLocationListener {
    fun onUpdatedLocation(location: LocationItem)
}