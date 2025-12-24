package com.joron.waffle.drivehistory.infrastructure.service

interface DriveLocationListener {
    fun onUpdatedLocation(latitude: Double, longitude: Double)
}