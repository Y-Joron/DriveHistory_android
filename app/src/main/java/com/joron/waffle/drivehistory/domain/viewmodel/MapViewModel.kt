package com.joron.waffle.drivehistory.domain.viewmodel

import android.content.Context
import android.location.Location
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel(), LifecycleEventObserver {

    val trackUsecase = TrackUsecase()
    val locationUsecase = LocationUsecase()

    var recording = MutableLiveData(false)
    var locationList = MutableLiveData(emptyList<LatLng>())
    var accuracy = MutableLiveData(0f)
    var speed = MutableLiveData(0f)
    var speed2 = MutableLiveData(0f)
    var altitude = MutableLiveData(0.0)

    var trackItem = TrackItem()
    val locationEventListener = LocationEventListener(
        onUpdateLocation = { onUpdateLocation(it) }
    )

    fun load(context: Context, trackUuid: String) {
        Log.d(TAG, "enter load")
        viewModelScope.launch(Dispatchers.IO) {
            val tItem = trackUsecase.queryTrackItem(context, trackUuid)
            val recordingUuid = trackUsecase.getRecordingTrackUuid()
            Log.d(
                TAG,
                "load currentTrackUuid = ${tItem.trackUuid}" +
                        " recordingUuid = $recordingUuid"
            )
            if (tItem.trackUuid == recordingUuid) {
                // 記録中の場合はPreferenceからの情報を追加する
                val locationsPref = locationUsecase.getLocationPref(context)
                tItem.locationList += locationsPref
            }
            withContext(Dispatchers.Main) {
                recording.value = tItem.trackUuid == recordingUuid
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

    private fun onUpdateLocation(location: Location) {
        val tmpRecording = recording.value ?: return
        if (!tmpRecording) {
            return
        }
        Log.d(
            TAG,
            "onUpdateLocation latitude = ${location.latitude}, longitude = ${location.longitude}"
        )
        val loList = (locationList.value ?: return).toMutableList()
        loList += LatLng(location.latitude, location.longitude)
        locationList.value = loList
        accuracy.value = location.accuracy
        speed.value = location.speed
        speed2.value = location.speed * 3.6f
        altitude.value = location.altitude
    }

    fun startRecording() {
        recording.value = true
    }

    fun stopRecording() {
        recording.value = false
    }

    companion object {
        const val TAG = "MapViewModel"
    }

}