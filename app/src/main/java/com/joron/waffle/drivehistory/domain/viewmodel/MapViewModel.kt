package com.joron.waffle.drivehistory.domain.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.joron.waffle.drivehistory.domain.LocationEventListener
import com.joron.waffle.drivehistory.domain.LocationUsecase
import com.joron.waffle.drivehistory.domain.TrackUsecase
import com.joron.waffle.drivehistory.domain.model.TrackItem
import com.joron.waffle.drivehistory.domain.type.TrackStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel(), LifecycleEventObserver {

    val trackUsecase = TrackUsecase()
    val locationUsecase = LocationUsecase()

    var recording = MutableLiveData(false)
    var locationList = MutableLiveData(emptyList<LatLng>())

    var trackItem = TrackItem()
    val locationEventListener = LocationEventListener(
        onUpdateLocation = { lat, lng ->
            onUpdateLocation(lat, lng)
        }
    )

    fun load(context: Context, trackUuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tItem = trackUsecase.queryTrackItem(context, trackUuid)
            if (tItem.status == TrackStatus.PLAY) {
                // 記録中の場合はPreferenceからの情報を追加する
                val locationsPref = locationUsecase.getLocationPref(context)
                tItem.locationList += locationsPref
            }
            withContext(Dispatchers.Main) {
                recording.value = tItem.status == TrackStatus.PLAY
                trackItem = tItem
                locationList.value = tItem.locationList
            }
        }
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event,
    ) {
        Log.d(TAG, "onStateChanged event = $event")
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                locationUsecase.addLocationEventListener(TAG, locationEventListener)
            }

            Lifecycle.Event.ON_DESTROY -> {
                locationUsecase.removeLocationEventListener(TAG)
            }

            else -> {}
        }
    }

    private fun onUpdateLocation(latitude: Double, longitude: Double) {
        val tmpRecording = recording.value ?: return
        if (!tmpRecording) {
            return
        }
        Log.d(TAG, "onUpdateLocation latitude = $latitude, longitude = $longitude")
        val loList = (locationList.value ?: return).toMutableList()
        loList += LatLng(latitude, longitude)
        locationList.value = loList
    }

    fun startRecording(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            trackUsecase.updateTrackStatus(
                context,
                trackItem.trackUuid,
                TrackStatus.PLAY,
            )
            withContext(Dispatchers.Main) {
                recording.value = true
            }
        }
    }

    fun stopRecording(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val trackListRecording = trackUsecase.queryTrackListByStatus(
                context,
                TrackStatus.PLAY
            )
            val locationsPref = locationUsecase.getLocationPref(context)
            Log.d(TAG, "trackListRecording = $trackListRecording")
            for (track in trackListRecording) {
                Log.d(TAG, "track = $track, locationsPref = $locationsPref")
                trackUsecase.updateTrackLocation(
                    context,
                    track.trackUuid,
                    track.locationList + locationsPref
                )
            }

            // DBとPreferenceを停止状態にする
            trackUsecase.initTrackStatus(context)
            locationUsecase.clearLocationsPref(context)
            withContext(Dispatchers.Main) {
                recording.value = false
            }
        }
    }

    companion object {
        const val TAG = "MapViewModel"
    }

}