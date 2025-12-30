package com.joron.waffle.drivehistory.domain.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joron.waffle.drivehistory.domain.LocationUsecase
import com.joron.waffle.drivehistory.domain.TrackUsecase
import com.joron.waffle.drivehistory.domain.model.LocationItem
import com.joron.waffle.drivehistory.domain.model.TrackItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(), LifecycleEventObserver {

    val trackUsecase = TrackUsecase()
    val locationUsecase = LocationUsecase()

    var recordingTrack = TrackItem()

    fun notifyUpdateLocation(location: LocationItem) {
        locationUsecase.notifyUpdateLocation(location)
    }

    fun isRecording(): Boolean {
        return trackUsecase.isRecording()
    }

    fun load(context: Context) {
        val recordingTrackUuid = trackUsecase.getRecordingTrackUuid()
        viewModelScope.launch(Dispatchers.IO) {
            recordingTrack = trackUsecase.queryTrackItem(
                context,
                recordingTrackUuid
            )
        }
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event,
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