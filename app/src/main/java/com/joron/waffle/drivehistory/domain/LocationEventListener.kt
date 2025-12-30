package com.joron.waffle.drivehistory.domain

import android.location.Location

data class LocationEventListener(
    val onUpdateLocation: ((location: Location) -> Unit)?
)