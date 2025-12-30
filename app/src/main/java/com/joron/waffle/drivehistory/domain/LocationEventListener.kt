package com.joron.waffle.drivehistory.domain

import com.joron.waffle.drivehistory.domain.model.LocationItem

data class LocationEventListener(
    val onUpdateLocation: ((location: LocationItem) -> Unit)?
)