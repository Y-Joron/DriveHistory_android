package com.joron.waffle.drivehistory.domain.viewmodel

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.joron.waffle.drivehistory.domain.LocationUsecase

class MainViewModel : ViewModel(), LifecycleEventObserver {

    val locationUsecase = LocationUsecase()

    fun notifyUpdateLocation(latitude: Double, longitude: Double) {
        locationUsecase.notifyUpdateLocation(latitude, longitude)
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        Log.d(TAG, "onStateChanged event = $event")
        when (event) {
            Lifecycle.Event.ON_CREATE -> {

            }

            Lifecycle.Event.ON_DESTROY -> {

            }

            else -> {

            }
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }

}