package com.joron.waffle.drivehistory.domain

data class LocationEventListener(
    val onUpdateLocation: ((latitude: Double, longitude: Double) -> Unit)?
)