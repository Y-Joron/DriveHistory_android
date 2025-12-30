package com.joron.waffle.drivehistory.infrastructure.service

import android.location.Location

interface DriveLocationListener {
    fun onUpdatedLocation(location: Location)
}